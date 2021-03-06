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
*/
package com.google.gwt.user.client.rpc.impl;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;

/**
 * <tt>ReflectiveTypeHandler</tt> is an implementation of {@link TypeHandler} based on <tt>Java Reflection</tt> which
 * can substitute any of standard (generated by GWT) field serializers except the ones that pin one's faith on {@link
 * CustomFieldSerializer CustomFieldSerializers}.
 * 
 * <p>The implementation is thread-safe: its instances can be safely shared across multiple threads.
*/
class ReflectiveTypeHandler implements InvocationHandler, TypeHandler
{
    private final LinkedList<Field> fields = new LinkedList<Field>();

    private final Class<?> serializableClass;
    private final Constructor<?> constructor;

    public ReflectiveTypeHandler(Class<?> serializableClass) throws SecurityException, NoSuchMethodException, NoSuchFieldException
    {
        for (Field field : getPotentiallySerializableFields(serializableClass))
        {
            field.setAccessible(true);
            int fieldModifiers = field.getModifiers();
            if (!Modifier.isStatic(fieldModifiers) && !Modifier.isTransient(fieldModifiers))
            {
                fields.add(field);
            }
        }
        Collections.sort(fields, new Comparator<Field>()
        {
            @Override
            public int compare(Field field1, Field field2)
            {
                return field1.getName().compareTo(field2.getName());
            }
        });

        this.serializableClass = serializableClass;
        constructor = serializableClass.getDeclaredConstructor();
        constructor.setAccessible(true);
    }

    private Field[] getPotentiallySerializableFields(Class<?> serializableClass) throws SecurityException, NoSuchFieldException
    {
        return serializableClass == Throwable.class ? new Field[]{serializableClass.getDeclaredField("detailMessage")} : serializableClass.getDeclaredFields();
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
    {
        String methodName = method.getName();
        if (methodName.equals("create"))
        {
            return create((SerializationStreamReader) args[0]);
        }
        if (methodName.equals("deserial"))
        {
            deserial((SerializationStreamReader) args[0], args[1]);
            return null;
        }
        if (methodName.equals("serial"))
        {
            serial((SerializationStreamWriter) args[0], args[1]);
            return null;
        }
        throw new RuntimeException("Unsupported method: " + method);
    }

    @Override
    public Object create(SerializationStreamReader reader) throws SerializationException
    {
        try
        {
            return constructor.newInstance();
        }
        catch (Exception exception)
        {
            throw new SerializationException(exception);
        }
    }

    @Override
    public void deserial(SerializationStreamReader reader, Object object) throws SerializationException
    {
        try
        {
            for (Field field : fields)
            {
                if (field.getType() == boolean.class)
                {
                    field.setBoolean(object, reader.readBoolean());
                }
                else if (field.getType() == byte.class)
                {
                    field.setByte(object, reader.readByte());
                }
                else if (field.getType() == char.class)
                {
                    field.setChar(object, reader.readChar());
                }
                else if (field.getType() == short.class)
                {
                    field.setShort(object, reader.readShort());
                }
                else if (field.getType() == int.class)
                {
                    field.setInt(object, reader.readInt());
                }
                else if (field.getType() == long.class)
                {
                    field.setLong(object, reader.readLong());
                }
                else if (field.getType() == float.class)
                {
                    field.setFloat(object, reader.readFloat());
                }
                else if (field.getType() == double.class)
                {
                    field.setDouble(object, reader.readDouble());
                }
                else if (field.getType() == String.class)
                {
                    field.set(object, reader.readString());
                }
                else
                {
                    field.set(object, reader.readObject());
                }
            }
        }
        catch (Exception exception)
        {
            throw new SerializationException(exception);
        }
        if (serializableClass.getSuperclass() != Object.class)
        {
            TypeHandlerFactory.getTypeHandler(serializableClass.getSuperclass()).deserial(reader, object);
        }
    }

    @Override
    public void serial(SerializationStreamWriter writer, Object object) throws SerializationException
    {
        try
        {
            for (Field field : fields)
            {
                if (field.getType() == boolean.class)
                {
                    writer.writeBoolean(field.getBoolean(object));
                }
                else if (field.getType() == byte.class)
                {
                    writer.writeByte(field.getByte(object));
                }
                else if (field.getType() == char.class)
                {
                    writer.writeChar(field.getChar(object));
                }
                else if (field.getType() == short.class)
                {
                    writer.writeShort(field.getShort(object));
                }
                else if (field.getType() == int.class)
                {
                    writer.writeInt(field.getInt(object));
                }
                else if (field.getType() == long.class)
                {
                    writer.writeLong(field.getLong(object));
                }
                else if (field.getType() == float.class)
                {
                    writer.writeFloat(field.getFloat(object));
                }
                else if (field.getType() == double.class)
                {
                    writer.writeDouble(field.getDouble(object));
                }
                else if (field.getType() == String.class)
                {
                    writer.writeString((String) field.get(object));
                }
                else
                {
                    writer.writeObject(field.get(object));
                }
            }
        }
        catch (Exception exception)
        {
            throw new SerializationException(exception);
        }
        if (serializableClass.getSuperclass() != Object.class)
        {
            TypeHandlerFactory.getTypeHandler(serializableClass.getSuperclass()).serial(writer, object);
        }
    }

}