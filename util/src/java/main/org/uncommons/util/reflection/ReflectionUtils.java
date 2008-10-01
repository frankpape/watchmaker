// ============================================================================
//   Copyright 2006, 2007 Daniel W. Dyer
//
//   Licensed under the Apache License, Version 2.0 (the "License");
//   you may not use this file except in compliance with the License.
//   You may obtain a copy of the License at
//
//       http://www.apache.org/licenses/LICENSE-2.0
//
//   Unless required by applicable law or agreed to in writing, software
//   distributed under the License is distributed on an "AS IS" BASIS,
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//   See the License for the specific language governing permissions and
//   limitations under the License.
// ============================================================================
package org.uncommons.util.reflection;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Helper methods to simplify code that uses reflection.  These methods handle the
 * checked exceptions and throw only unchecked exceptions.  They are useful for dealing
 * with reflection when we know that there is no chance of a checked exception.  We can
 * use this class and avoid all of the boiler-plate exception handling.
 * @author Daniel Dyer
 */
public class ReflectionUtils
{
    private ReflectionUtils()
    {
        // Prevents instantiation.
    }


    /**
     * Invokes the specified method without throwing any checked exceptions.
     * This is only valid for methods that are not declared to throw any checked
     * exceptions.  Any unchecked exceptions thrown by the specified method will be
     * re-thrown (in their original form, not wrapped in an InvocationTargetException
     * as would be the case for a normal reflective invocation).
     * @param method The method to invoke.  Both the method and its class must have
     * been declared public, otherwise they will be inaccessible.
     * @param target The object on which to invoke the method.
     * @param arguments The method arguments.
     * @param <T> The return type of the method.  The compiler can usually infer the
     * correct type.
     * @return The result of invoking the method, or null if the method is void.
     */
    @SuppressWarnings("unchecked")
    public static <T> T invokeUnchecked(Method method, Object target, Object... arguments)
    {
        try
        {
            return (T) method.invoke(target, arguments);
        }
        catch (IllegalAccessException ex)
        {
            // This cannot happen if the method is public.
            String message = "Method " + method.getName() + " is not publicly accessible.";
            throw new IllegalArgumentException(message, ex);
        }
        catch (InvocationTargetException ex)
        {
            // If the method is not declared to throw any checked exceptions,
            // the worst that can happen is a RuntimeException or an Error (we can,
            // and should, re-throw both).
            if (ex.getCause() instanceof Error)
            {
                throw (Error) ex.getCause();
            }
            else
            {
                throw (RuntimeException) ex.getCause();
            }
        }
    }


    /**
     * Looks up a method that is explicitly identified.  This method should only
     * be used for methods that definitely exist.  It does not throw the checked
     * NoSuchMethodException.  If the method does not exist, it will instead fail
     * with an unchecked IllegalArgumentException.
     * @param aClass The class in which the method exists.
     * @param name The name of the method.
     * @param paramTypes The types of the method's parameters.
     * @return The identified method.
     */
    public static Method findKnownMethod(Class<?> aClass,
                                         String name,
                                         Class<?>... paramTypes)
    {
        try
        {
            return aClass.getMethod(name, paramTypes);
        }
        catch (NoSuchMethodException ex)
        {
            // This cannot happen if the method is correctly identified.
            String message = "Method " + name + " does not exist in class " + aClass.getName();
            throw new IllegalArgumentException(message, ex);
        }
    }
}
