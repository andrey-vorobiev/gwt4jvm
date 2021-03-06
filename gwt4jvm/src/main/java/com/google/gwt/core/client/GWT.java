/*
 * Copyright 2011 Mind Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at:
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * This file incorporates work covered by the following copyright and permission
 * notice:  
 * 
 *     Copyright 2008 Google Inc.
 * 
 *     Licensed under the Apache License, Version 2.0 (the "License"); you may
 *     not use this file except in compliance with the License. You may obtain a
 *     copy of the License at:
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *     WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *     License for the specific language governing permissions and limitations
 *     under the License.
*/
package com.google.gwt.core.client;

import com.mind.gwt.jclient.context.Context;

/**
 * Supports core functionality that in some cases requires direct support from
 * the compiler and runtime systems such as runtime type information and
 * deferred binding.
 */
public final class GWT
{
    /**
     * This interface is used to catch exceptions at the "top level" just before
     * they escape to the browser. This is used in places where the browser calls
     * into user code such as event callbacks, timers, and RPC.
     *
     * In Development Mode, the default handler prints a stack trace to the log
     * window. In Production Mode, the default handler is null and thus exceptions
     * are allowed to escape, which provides an opportunity to use a JavaScript
     * debugger.
    */
    public interface UncaughtExceptionHandler
    {
        void onUncaughtException(Throwable exception);
    }

    /**
     * This constant is used by {@link #getPermutationStrongName} when running in Development Mode.
    */
    private static final String HOSTED_MODE_PERMUTATION_STRONG_NAME = "HostedMode";

    /**
     * Instantiates a class via deferred binding.
     *
     * <p>
     * The argument to {@link #create(Class)}&#160;<i>must</i> be a class literal
     * because the Production Mode compiler must be able to statically determine
     * the requested type at compile-time. This can be tricky because using a
     * {@link Class} variable may appear to work correctly in Development Mode.
     * </p>
     *
     * @param c a class literal specifying the base class to be instantiated
     * @return the new instance, which must be typecast to the requested class.
    */
    @SuppressWarnings("unchecked")
    public static <T> T create(final Class<?> c)
    {
        try
        {
            return (T) Context.getCurrentContext().getClient().getDeferredBindingFactory().create(c);
        }
        catch (ClassNotFoundException exception)
        {
            throw new UnsupportedOperationException(c + ": class not found", exception);
        }
        catch (Exception exception)
        {
            throw new UnsupportedOperationException(c + ": class can't be instantenated", exception);
        }
    }

    /**
     * Gets the URL prefix of the module which should be prepended to URLs that
     * are intended to be module-relative, such as RPC entry points and files in
     * the module's public path.
     *
     * @return if non-empty, the base URL is guaranteed to end with a slash
    */
    public static String getModuleBaseURL()
    {
        return Context.getCurrentContext().getClient().getModuleBaseURL();
    }

    /**
     * Returns the permutation's strong name. This can be used to distinguish
     * between different permutations of the same module. In Development Mode,
     * this method will return {@value #HOSTED_MODE_PERMUTATION_STRONG_NAME}.
    */
    public static String getPermutationStrongName()
    {
        return HOSTED_MODE_PERMUTATION_STRONG_NAME;
    }

    /**
     * Returns <code>true</code> when running inside the normal GWT environment,
     * either in Development Mode or Production Mode. Returns <code>false</code>
     * if this code is running in a plain JVM. This might happen when running
     * shared code on the server, or during the bootstrap sequence of a
     * GWTTestCase test.
    */
    public static boolean isClient()
    {
        return false;
    }

    /**
     * Determines whether or not the running program is script or bytecode. It always return <code>false</code> - bytcode.
    */
    public static boolean isScript()
    {
        return false;
    }

}