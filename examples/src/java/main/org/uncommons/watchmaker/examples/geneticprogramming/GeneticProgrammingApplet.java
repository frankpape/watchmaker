//=============================================================================
// Copyright 2006-2010 Daniel W. Dyer
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//=============================================================================
package org.uncommons.watchmaker.examples.geneticprogramming;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import org.uncommons.swing.SwingBackgroundTask;
import org.uncommons.watchmaker.examples.AbstractExampleApplet;
import org.uncommons.watchmaker.examples.EvolutionLogger;
import org.uncommons.watchmaker.framework.EvolutionObserver;
import org.uncommons.watchmaker.framework.TerminationCondition;
import org.uncommons.watchmaker.framework.interactive.Renderer;
import org.uncommons.watchmaker.framework.termination.TargetFitness;
import org.uncommons.watchmaker.swing.AbortControl;
import org.uncommons.watchmaker.swing.evolutionmonitor.EvolutionMonitor;

/**
 * Simple tree-based genetic programming application based on the first example
 * in Chapter 11 of Toby Segaran's Programming Collective Intelligence.
 *
 * <p>This class presents a graphical interface to the evolution run by
 * {@link GeneticProgrammingExample}.
 * @author Frank Pape
 */
public class GeneticProgrammingApplet extends AbstractExampleApplet
{
    // This data describes the problem.  For each pair of inputs, the generated program
    // should return the associated output.  The goal of this application is to generalise
    // the examples into an equation.
    private static final Map<double[], Double> TEST_DATA = new HashMap<double[], Double>();
    static
    {
        // a^2 + 3a + 2b + 5
        TEST_DATA.put(new double[]{26, 35}, 829.0d);
        TEST_DATA.put(new double[]{8, 24}, 141.0d);
        TEST_DATA.put(new double[]{20, 1}, 467.0d);
        TEST_DATA.put(new double[]{33, 11}, 1215.0d);
        TEST_DATA.put(new double[]{37, 16}, 1517.0d);
    }

    private Map<double[], Double> data;
    private EvolutionMonitor<Node> monitor;
    private JButton startButton;
    private AbortControl abort;
    private JSpinner populationSpinner;
    private JSpinner elitismSpinner;

    public GeneticProgrammingApplet(Map<double[], Double> data)
    {
        this.data = data;
    }

    @Override
    protected void prepareGUI(Container container)
    {
        JPanel controls = new JPanel(new BorderLayout());
        controls.add(createParametersPanel(), BorderLayout.NORTH);
        container.add(controls, BorderLayout.NORTH);

        Renderer<Node, JComponent> renderer = new SwingGPTreeRenderer();
        monitor = new EvolutionMonitor<Node>(renderer, false);
        container.add(monitor.getGUIComponent(), BorderLayout.CENTER);
    }

    private JComponent createParametersPanel()
    {
        Box parameters = Box.createHorizontalBox();
        parameters.add(Box.createHorizontalStrut(10));
        final JLabel populationLabel = new JLabel("Population Size: ");
        parameters.add(populationLabel);
        parameters.add(Box.createHorizontalStrut(10));
        populationSpinner = new JSpinner(new SpinnerNumberModel(10, 2, 1000, 1));
        populationSpinner.setMaximumSize(populationSpinner.getMinimumSize());
        parameters.add(populationSpinner);
        parameters.add(Box.createHorizontalStrut(10));
        final JLabel elitismLabel = new JLabel("Elitism: ");
        parameters.add(elitismLabel);
        parameters.add(Box.createHorizontalStrut(10));
        elitismSpinner = new JSpinner(new SpinnerNumberModel(2, 1, 1000, 1));
        elitismSpinner.setMaximumSize(elitismSpinner.getMinimumSize());
        parameters.add(elitismSpinner);
        parameters.add(Box.createHorizontalStrut(10));

        parameters.add(new JLabel("Selection Pressure: "));
        parameters.add(Box.createHorizontalStrut(10));

        startButton = new JButton("Start");
        abort = new AbortControl();        
        startButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                abort.getControl().setEnabled(true);
                populationLabel.setEnabled(false);
                populationSpinner.setEnabled(false);
                elitismLabel.setEnabled(false);
                elitismSpinner.setEnabled(false);
                startButton.setEnabled(false);
                new EvolutionTask((Integer) populationSpinner.getValue(),
                                  (Integer) elitismSpinner.getValue()).execute();
            }
        });
        abort.getControl().setEnabled(false);
        parameters.add(startButton);
        parameters.add(abort.getControl());
        parameters.add(Box.createHorizontalStrut(10));

        parameters.setBorder(BorderFactory.createTitledBorder("Parameters"));
        return parameters;
    }

    public static void main(String[] args)
    {
        GeneticProgrammingApplet gui = new GeneticProgrammingApplet(TEST_DATA);
        gui.displayInFrame("Watchmaker Framework - Genetic Programming Example");
    }

    /**
     * The task that actually performs the evolution.
     */
    private class EvolutionTask extends SwingBackgroundTask<Node>
    {
        private final int populationSize;
        private final int eliteCount;


        EvolutionTask(int populationSize, int eliteCount)
        {
            this.populationSize = populationSize;
            this.eliteCount = eliteCount;
        }


        @Override
        protected Node performTask() throws Exception
        {
            @SuppressWarnings("unchecked")
            EvolutionObserver<Node>[] observers = (EvolutionObserver<Node>[]) new EvolutionObserver[]
            {
                new EvolutionLogger<Node>(),
                monitor
            };
            TerminationCondition[] conditions = new TerminationCondition[]
            {
                new TargetFitness(0d, false),
                abort.getTerminationCondition()
            };
            return GeneticProgrammingExample.evolveProgram(data, populationSize, eliteCount, observers, conditions);
        }


        @Override
        protected void postProcessing(Node result)
        {
            abort.reset();
            abort.getControl().setEnabled(false);
            populationSpinner.setEnabled(true);
            elitismSpinner.setEnabled(true);
            startButton.setEnabled(true);

            System.out.println(result.print());
        }


        @Override
        protected void onError(Throwable throwable)
        {
            super.onError(throwable);
            postProcessing(null);
        }
    }
}
