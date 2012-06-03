/*
 * Copyright 2012 Mind Ltd.
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
*/
package com.google.gwt.user.client.rpc.impl;

import java.lang.reflect.Proxy;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.google.gwt.user.client.rpc.SerializationException;

class TypeHandlerFactory
{
    private static final ConcurrentMap<Class<?>, TypeHandler> typeHandlers = new ConcurrentHashMap<Class<?>, TypeHandler>();

    public static TypeHandler getTypeHandler(Class<?> c) throws SerializationException
    {
        try
        {
            if (!typeHandlers.containsKey(c))
            {
                typeHandlers.putIfAbsent(c, createTypeHandler(c));
            }
            return typeHandlers.get(c);
        }
        catch (Exception exception)
        {
            throw new SerializationException(exception);
        }
    }

    /**
     * Creates and returns thread-safe <tt>TypeHandler</tt> for the specified GWT-RPC serializable class. Standard
     * (generated by GWT) field serializers are only used if the serializable class has {@link CustomFieldSerializer},
     * otherwise an instance of {@link ReflectiveTypeHandler} is returned instead.
     *
     * <p>Standard field serializers (which don't rely on custom field serializers) aren't used not only because they
     * can contain JSNI methods. Even if they don't, they still might delegate to "super field serializer" which is
     * dedicated to (de)serialize fields of class that the serializable class is inherited from. Since the delegation
     * is performed through a static call of <tt>serialize/deserialize</tt> method which can't be intercepted (easily)
     * and which in its turn can call JSNI setters/getters, we limit the use of standard fields serializers.
    */
    private static TypeHandler createTypeHandler(Class<?> c) throws SerializationException
    {
        try
        {
            Class<TypeHandler> typeHandlerClass = getGeneratedTypeHandlerClass(c);
            if (c.isEnum() || isDelegateToCustomFieldSerializer(typeHandlerClass))
            {
                return typeHandlerClass.getDeclaredConstructor().newInstance();
            }
            else
            {
                return (TypeHandler) Proxy.newProxyInstance(TypeHandler.class.getClassLoader(), new Class[] {TypeHandler.class}, new ReflectiveTypeHandler(c));
            }
        }
        catch (Exception exception)
        {
            throw new SerializationException(exception);
        }
    }

    @SuppressWarnings("unchecked")
    private static Class<TypeHandler> getGeneratedTypeHandlerClass(Class<?> c) throws ClassNotFoundException
    {
        String generatedTypeHandlerClassName = c.getName().replace('$', '_') + "_FieldSerializer";
        if (generatedTypeHandlerClassName.startsWith("java"))
        {
            generatedTypeHandlerClassName = "com.google.gwt.user.client.rpc.core." + generatedTypeHandlerClassName;
        }
        return (Class<TypeHandler>) Class.forName(generatedTypeHandlerClassName);
    }

    private static boolean isDelegateToCustomFieldSerializer(Class<TypeHandler> typeHandlerClass)
    {
        try
        {
            Class.forName(typeHandlerClass.getName().replace('_', '$').replaceFirst("\\$FieldSerializer$", "_CustomFieldSerializer"));
            return true;
        }
        catch (ClassNotFoundException exception)
        {
            return false;
        }
    }

    private TypeHandlerFactory() {}

}