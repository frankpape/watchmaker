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

import java.awt.Component;
import java.awt.Container;
import java.util.HashMap;
import java.util.Map;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JPanel;
import org.testng.annotations.Test;

/**
 * Test for the {@link GeneticProgrammingApplet} example application.
 * @author Frank Pape
 */
public class GeneticProgrammingAppletTest
{
    @Test
    public void testPrepareGUI()
    {
        Map<double[], Double> testData = new HashMap<double[], Double>();
        testData.put(new double[]{26, 35}, 165.0d);
        testData.put(new double[]{8, 24}, 64.0d);
        testData.put(new double[]{20, 1}, 101.0d);
        testData.put(new double[]{33, 11}, 176.0d);
        testData.put(new double[]{37, 16}, 201.0d);

        Container container = new Container();
        GeneticProgrammingApplet applet = new GeneticProgrammingApplet(testData);
        applet.prepareGUI(container);

        Component[] comps = container.getComponents();
        assert comps.length == 2 : "Wrong number of components: " + comps.length;
        assert comps[0] instanceof JPanel : "Wrong type for controls component: " + comps[0].getClass().getName();
        assert comps[1] instanceof JPanel : "Wrong type for monitor component" + comps[1].getClass().getName();
        
        JPanel controls = (JPanel) comps[0];
        assert controls.getComponentCount() == 1 : "Wrong number of components in controls: "
                                                   + controls.getComponentCount();
        assert controls.getComponents()[0] instanceof Box : "Wrong type for controls subcomponent: "
                                                            + controls.getComponents()[0].getClass().getName();
    }

    @Test
    public void testStartEvolution()
    {
        Map<double[], Double> testData = new HashMap<double[], Double>();
        testData.put(new double[]{26, 35}, 165.0d);
        testData.put(new double[]{8, 24}, 64.0d);
        testData.put(new double[]{20, 1}, 101.0d);
        testData.put(new double[]{33, 11}, 176.0d);
        testData.put(new double[]{37, 16}, 201.0d);

        Container container = new Container();
        GeneticProgrammingApplet applet = new GeneticProgrammingApplet(testData);
        applet.prepareGUI(container);
        Component[] comps = container.getComponents();

        JButton startButton = null;
        for (Component c : ((Box) ((JPanel) comps[0]).getComponents()[0]).getComponents())
        {
            if (c instanceof JButton && ((JButton) c).getText().equals("Start"))
            {
                startButton = (JButton) c;
                break;
            }
        }
        assert startButton != null : "Start button not found";
        
        assert startButton.getActionListeners().length == 1 : "Wrong action listener count: "
                                                              + startButton.getActionListeners().length;
        
        // Make sure that we can at least start the evolution.
        startButton.getActionListeners()[0].actionPerformed(null);
    }
}
