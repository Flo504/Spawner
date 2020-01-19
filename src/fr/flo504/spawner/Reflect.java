package fr.flo504.spawner;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

public class Reflect {

    public static Class<?> getClass(String name){
        Objects.requireNonNull(name, "The class name can not be null");
        try {
            return Class.forName(name);
        } catch (ClassNotFoundException e) {
            try {
                throw e;
            } catch (ClassNotFoundException ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }

    public static Constructor<?> getConstructor(Class<?> clazz, Class<?>... parameterTypes){
        Objects.requireNonNull(clazz, "The class can not be null");
        try {
            return clazz.getDeclaredConstructor(parameterTypes);
        } catch (NoSuchMethodException e) {
            try {
                throw e;
            } catch (NoSuchMethodException ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }

    public static Method getMethod(Class<?> clazz, String methodName, Class<?>... parameterTypes){
        Objects.requireNonNull(clazz, "The class can not be null");
        Objects.requireNonNull(methodName, "The method name can not be null");
        try{
            return clazz.getDeclaredMethod(methodName, parameterTypes);
        } catch (NoSuchMethodException e) {
            try {
                throw e;
            } catch (NoSuchMethodException ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }

    public static Field getField(Class<?> clazz, String fieldName){
        Objects.requireNonNull(clazz, "The class can not be null");
        Objects.requireNonNull(fieldName, "The field name can not be null");
        try {
            return clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            try {
                throw e;
            } catch (NoSuchFieldException ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }

    public static Object newInstance(Constructor<?> constructor, Object... parameters){
        Objects.requireNonNull(constructor, "The constructor can not be null");
        try {
            return constructor.newInstance(parameters);
        } catch (InstantiationException e) {
            try {
                throw e;
            } catch (InstantiationException ex) {
                ex.printStackTrace();
            }
        } catch (IllegalAccessException e) {
            try {
                throw e;
            } catch (IllegalAccessException ex) {
                ex.printStackTrace();
            }
        } catch (InvocationTargetException e) {
            try {
                throw e;
            } catch (InvocationTargetException ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }

    public static Object invoke(Method method, Object instance, Object... parameters){
        Objects.requireNonNull(method, "The method can not be null");
        try {
            return method.invoke(instance, parameters);
        } catch (IllegalAccessException e) {
            try {
                throw e;
            } catch (IllegalAccessException ex) {
                ex.printStackTrace();
            }
        } catch (InvocationTargetException e) {
            try {
                throw e;
            } catch (InvocationTargetException ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }

    public static Object invokeStatic(Method method, Object... parameters){
        return invoke(method, null, parameters);
    }

    public static Object get(Field field, Object instance){
        Objects.requireNonNull(field, "The field can not be null");
        try {
            return field.get(instance);
        } catch (IllegalAccessException e) {
            try {
                throw e;
            } catch (IllegalAccessException ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }

    public static Object getStatic(Field field){
        return get(field, null);
    }

    public static void set(Field field, Object instance, Object value){
        Objects.requireNonNull(field, "The field can not be null");
        try {
            field.set(instance, value);
        } catch (IllegalAccessException e) {
            try {
                throw e;
            } catch (IllegalAccessException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void setStatic(Field field, Object value){
        set(field, null, value);
    }

    public static boolean classExist(String className){
        Objects.requireNonNull(className, "The class name can not be null");
        try{
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e){}
        return false;
    }

    public static Class<?> getClassOrNull(String className){
        Objects.requireNonNull(className, "The class name can not be null");
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {}
        return null;
    }

}
