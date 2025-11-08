package rolflectionlib.util;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.MethodHandle;

import java.util.*;

import org.apache.log4j.Logger;

public class RolfLectionUtil {
    public static final Logger logger = Logger.getLogger(RolfLectionUtil.class);
    public static void print(Object... args) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            sb.append(args[i] instanceof String ? (String) args[i] : String.valueOf(args[i]));
            if (i < args.length - 1) sb.append(' ');
        }
        logger.info(sb.toString());
    }

    // Code taken and modified from Grand Colonies and Ashes of the Domain
    public static final MethodHandles.Lookup lookup = MethodHandles.lookup();

    public static final Class<?> fieldClass;
    public static final Class<?> fieldArrayClass;
    public static final Class<?> methodClass;
    public static final Class<?> typeClass;
    public static final Class<?> typeArrayClass;
    public static final Class<?> constructorClass;

    public static final MethodHandle getFieldTypeHandle;
    public static final MethodHandle setFieldHandle;
    public static final MethodHandle getFieldHandle;
    public static final MethodHandle getFieldNameHandle;
    public static final MethodHandle setFieldAccessibleHandle;
    public static final MethodHandle getFieldModifiersHandle;

    public static final MethodHandle getMethodNameHandle;
    public static final MethodHandle getMethodDeclaringClassHandle;
    public static final MethodHandle invokeMethodHandle;
    public static final MethodHandle setMethodAccessible;
    public static final MethodHandle getModifiersHandle;
    public static final MethodHandle getParameterTypesHandle;
    public static final MethodHandle getReturnTypeHandle;
    public static final MethodHandle getGenericReturnTypeHandle;
    
    public static final MethodHandle getGenericTypeHandle;
    public static final MethodHandle getGenericParameterTypesHandle;
    
    public static final MethodHandle setConstructorAccessibleHandle;
    public static final MethodHandle getConstructorParameterTypesHandle;
    public static final MethodHandle constructorNewInstanceHandle;
    public static final MethodHandle getConstructorDeclaringClassHandle;
    public static final MethodHandle getConstructorGenericParameterTypesHandle;
    public static final MethodHandle getConstructorNameHandle;

    static {
        try {
            fieldClass = Class.forName("java.lang.reflect.Field", false, Class.class.getClassLoader());
            fieldArrayClass = Class.forName("[Ljava.lang.reflect.Field;", false, Class.class.getClassLoader());
            methodClass = Class.forName("java.lang.reflect.Method", false, Class.class.getClassLoader());
            typeClass = Class.forName("java.lang.reflect.Type", false, Class.class.getClassLoader());
            typeArrayClass = Class.forName("[Ljava.lang.reflect.Type;", false, Class.class.getClassLoader());
            constructorClass = Class.forName("java.lang.reflect.Constructor", false, Class.class.getClassLoader());

            setFieldHandle = lookup.findVirtual(fieldClass, "set", MethodType.methodType(void.class, Object.class, Object.class));
            getFieldHandle = lookup.findVirtual(fieldClass, "get", MethodType.methodType(Object.class, Object.class));
            getFieldNameHandle = lookup.findVirtual(fieldClass, "getName", MethodType.methodType(String.class));
            getFieldTypeHandle = lookup.findVirtual(fieldClass, "getType", MethodType.methodType(Class.class));
            getFieldModifiersHandle = lookup.findVirtual(fieldClass, "getModifiers", MethodType.methodType(int.class));
            setFieldAccessibleHandle = lookup.findVirtual(fieldClass, "setAccessible", MethodType.methodType(void.class, boolean.class));

            getMethodNameHandle = lookup.findVirtual(methodClass, "getName", MethodType.methodType(String.class));
            getMethodDeclaringClassHandle = lookup.findVirtual(methodClass, "getDeclaringClass", MethodType.methodType(Class.class));
            invokeMethodHandle = lookup.findVirtual(methodClass, "invoke", MethodType.methodType(Object.class, Object.class, Object[].class));
            setMethodAccessible = lookup.findVirtual(methodClass, "setAccessible", MethodType.methodType(void.class, boolean.class));
            getModifiersHandle = lookup.findVirtual(methodClass, "getModifiers", MethodType.methodType(int.class));
            getParameterTypesHandle = lookup.findVirtual(methodClass, "getParameterTypes", MethodType.methodType(Class[].class));
            getReturnTypeHandle = lookup.findVirtual(methodClass, "getReturnType", MethodType.methodType(Class.class));
            getGenericReturnTypeHandle = lookup.findVirtual(methodClass, "getGenericReturnType", MethodType.methodType(typeClass));

            getGenericTypeHandle = lookup.findVirtual(fieldClass, "getGenericType", MethodType.methodType(typeClass));
            getGenericParameterTypesHandle = lookup.findVirtual(methodClass, "getGenericParameterTypes", MethodType.methodType(typeArrayClass));

            setConstructorAccessibleHandle = lookup.findVirtual(constructorClass, "setAccessible", MethodType.methodType(void.class, boolean.class));
            getConstructorParameterTypesHandle = lookup.findVirtual(constructorClass, "getParameterTypes", MethodType.methodType(Class[].class));
            constructorNewInstanceHandle = lookup.findVirtual(constructorClass, "newInstance", MethodType.methodType(Object.class, Object[].class));
            getConstructorDeclaringClassHandle = lookup.findVirtual(constructorClass, "getDeclaringClass", MethodType.methodType(Class.class));
            getConstructorGenericParameterTypesHandle = lookup.findVirtual(constructorClass, "getGenericParameterTypes", MethodType.methodType(typeArrayClass));
            getConstructorNameHandle = lookup.findVirtual(constructorClass, "getName", MethodType.methodType(String.class));


        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Class<?> getMethodDeclaringClass(Object method) {
        try {
            return (Class<?>) getMethodDeclaringClassHandle.invoke(method);
        } catch (Throwable e) {
            throw new RuntimeException();
        }
    }

    public static boolean hasFieldOfType(Class<?> cls, Class<?> fieldType) {
        try {
            for (Object field : cls.getDeclaredFields()) {
                if (((Class<?>)getFieldTypeHandle.invoke(field)).equals(fieldType)) {
                    return true;
                }
            }
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    public static int getFieldModifiers(Object field) throws Throwable {
        return (int)getFieldModifiersHandle.invoke(field);
    }

    public static void transplant(Object original, Object template) {
        try {
            Class<?> currentClass = original.getClass();
            while ((currentClass = currentClass.getSuperclass()) != null) {

                for (Object field : currentClass.getDeclaredFields()) {
                    if (isFinal(getFieldModifiers(field))) continue;

                    String fieldName = (String) getFieldNameHandle.invoke(field);
                    Object variable = getPrivateVariableFromSuperClass(fieldName, original);
                    if (variable == original) {
                        setpublicVariableFromSuperclass(fieldName, template, template);
                    } else {
                        setpublicVariableFromSuperclass(fieldName, template, variable);
                    }
                }
            }

            boolean isExactClass = original.getClass().equals(template.getClass());
            for (Object field : original.getClass().getDeclaredFields()) {
                if (isFinal(getFieldModifiers(field))) continue;

                Object variable = getPrivateVariable(field, original);
                if (isExactClass) {
                    if (variable == original) {
                        setpublicVariable(field, template, template);
                    } else {
                        setpublicVariable(field, template, variable);
                    }
                } else {
                    if (variable == original) {
                        setpublicVariableByName((String)getFieldNameHandle.invoke(field), template, template);
                    } else {
                        setpublicVariableByName((String)getFieldNameHandle.invoke(field), template, variable);
                    }
                }
            }
            return;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Object> getAllVariables(Object instanceToGetFrom) {
        List<Object> lst = new ArrayList<>();
        Class<?> currentClass = instanceToGetFrom.getClass();
        while (currentClass != null) {
            for (Object field : currentClass.getDeclaredFields()) {
                lst.add(getPrivateVariable(field, instanceToGetFrom));
            }
            currentClass = currentClass.getSuperclass();
        }
        return lst;
    }

    // public static List<Object> getAllFields(Object instance) {
    //     return getAllFields(instance.getClass());
    // }

    public static List<Object> getAllFields(Class<?> cls) {
        List<Object> lst = new ArrayList<>();
        Class<?> currentClass = cls;
        while (currentClass != null) {
            for (Object field : currentClass.getDeclaredFields()) {
                lst.add(field);
            }
            currentClass = currentClass.getSuperclass();
        }
        return lst;
    }

    public static void setpublicVariableByName(String fieldName, Object instanceToModify, Object newValue) throws Throwable {
        Object field = instanceToModify.getClass().getDeclaredField(fieldName);
        setFieldAccessibleHandle.invoke(field, true);
        setFieldHandle.invoke(field, instanceToModify, newValue);
    }

    public static String getMethodName(Object method) {
        try {
            return (String) getMethodNameHandle.invoke(method);
        } catch (Throwable ignored) {
            return null;
        }
    }

    public static Class<?> getReturnType(Object method) {
        try {
            return (Class<?>) getReturnTypeHandle.invoke(method);
        } catch (Throwable e) {
            print(e);
            return null;
        }
    }

    public static String getFieldName(Object field) {
        try {
            return (String) getFieldNameHandle.invoke(field);
        } catch (Throwable e) {
            print(e);
            return null;
        }
    }

    public static Object getFieldByName(String name, Class<?> cls) {
        try {
            for (Object field : cls.getDeclaredFields()) {
                if (((String)getFieldNameHandle.invoke(field)).equals(name)) {
                    return field;
                }
            }
        } catch (Throwable e) {
            print(e);
        }
        return null;
    }

    public static Object getPrivateVariable(Object field, Object instanceToGetFrom) {
        try {
            setFieldAccessibleHandle.invoke(field, true);
            return getFieldHandle.invoke(field, instanceToGetFrom);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static Object getPrivateVariable(String fieldName, Object instanceToGetFrom) {
        try {
            Class<?> instances = instanceToGetFrom.getClass();
            while (instances != null) {
                for (Object obj : instances.getDeclaredFields()) {
                    setFieldAccessibleHandle.invoke(obj, true);
                    String name = (String) getFieldNameHandle.invoke(obj);
                    if (name.equals(fieldName)) {
                        return getFieldHandle.invoke(obj, instanceToGetFrom);
                    }
                }
                for (Object obj : instances.getFields()) {
                    setFieldAccessibleHandle.invoke(obj, true);
                    String name = (String) getFieldNameHandle.invoke(obj);
                    if (name.equals(fieldName)) {
                        return getFieldHandle.invoke(obj, instanceToGetFrom);
                    }
                }
                instances = instances.getSuperclass();
            }
            return null;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static Object getPrivateVariableFromSuperClass(String fieldName, Object instanceToGetFrom) {
        try {
            Class<?> instances = instanceToGetFrom.getClass();
            while (instances != null) {
                for (Object obj : instances.getDeclaredFields()) {
                    setFieldAccessibleHandle.invoke(obj, true);
                    String name = (String) getFieldNameHandle.invoke(obj);
                    if (name.equals(fieldName)) {
                        return getFieldHandle.invoke(obj, instanceToGetFrom);
                    }
                }
                for (Object obj : instances.getFields()) {
                    setFieldAccessibleHandle.invoke(obj, true);
                    String name = (String) getFieldNameHandle.invoke(obj);
                    if (name.equals(fieldName)) {
                        return getFieldHandle.invoke(obj, instanceToGetFrom);
                    }
                }
                instances = instances.getSuperclass();
            }
            return null;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static void setpublicVariable(Object field, Object instanceToModify, Object newValue) {
        try {
            setFieldAccessibleHandle.invoke(field, true);
            setFieldHandle.invoke(field, instanceToModify, newValue);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static void setpublicVariableFromSuperclass(String fieldName, Object instanceToModify, Object newValue) {
        try {
            Class<?> instances = instanceToModify.getClass();
            while (instances != null) {
                for (Object obj : instances.getDeclaredFields()) {
                    setFieldAccessibleHandle.invoke(obj, true);
                    String name = (String) getFieldNameHandle.invoke(obj);
                    if (name.equals(fieldName)) {
                        setFieldHandle.invoke(obj, instanceToModify, newValue);
                        return;
                    }
                }
                for (Object obj : instances.getFields()) {
                    setFieldAccessibleHandle.invoke(obj, true);
                    String name = (String) getFieldNameHandle.invoke(obj);
                    if (name.equals(fieldName)) {
                        setFieldHandle.invoke(obj, instanceToModify, newValue);
                        return;
                    }
                }
                instances = instances.getSuperclass();
            }
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean hasMethodOfName(String name, Object instance) {
        try {
            for (Object method : instance.getClass().getMethods()) {
                if (getMethodNameHandle.invoke(method).equals(name)) {
                    return true;
                }
            }
            return false;
        } catch (Throwable e) {
            print(e);
            return false;
        }
    }

    public static Class<?>[] getMethodParamTypes(Object method) {
        try {
            return (Class<?>[]) getParameterTypesHandle.invoke(method);
        } catch (Throwable e) {
            print(e);
            throw new RuntimeException(e);
        }
    }

    public static void logClasses(Object instance) {
        print("---------------------------------");
        print("CLASSES FOR:", instance.getClass());
        print("---------------------------------");
        try {
            Class<?>[] classes = instance.getClass().getDeclaredClasses();
            for (Class<?> cls : classes) {
                logger.info("Class: " + cls.getCanonicalName());
    
                Object[] constructors = cls.getDeclaredConstructors();
                for (Object constructor : constructors) {
                    StringBuilder paramString = new StringBuilder();
                    Class<?>[] paramTypes = (Class<?>[]) getConstructorParameterTypesHandle.invoke(constructor);
                    for (int i = 0; i < paramTypes.length; i++) {
                        if (i > 0) paramString.append(", ");
                        paramString.append(paramTypes[i].getCanonicalName());
                    }
                    logger.info("  Constructor: " + cls.getSimpleName() + "(" + paramString.toString() + ")");
                }
            }
        } catch (Throwable e) {
            print(e);
            logger.info("Error logging classes: ", e);
        }
    }

    public static void logClasses(Class<?> masterClass) {
        print("---------------------------------");
        print("CLASSES FOR:", masterClass);
        print("---------------------------------");
        try {
            Class<?>[] classes = masterClass.getDeclaredClasses();
            for (Class<?> cls : classes) {
                logger.info("Class: " + cls.getCanonicalName());
    
                Object[] constructors = cls.getDeclaredConstructors();
                for (Object constructor : constructors) {
                    StringBuilder paramString = new StringBuilder();
                    Class<?>[] paramTypes = (Class<?>[]) getConstructorParameterTypesHandle.invoke(constructor);

                    for (int i = 0; i < paramTypes.length; i++) {
                        if (i > 0) paramString.append(", ");
                        paramString.append(paramTypes[i].getCanonicalName());
                    }
                    logger.info("  Constructor: " + cls.getSimpleName() + "(" + paramString.toString() + ")");
                }
            }
        } catch (Throwable e) {
            print(e);
            logger.info("Error logging classes: ", e);
        }
    }

    public static void logMethod(Object method) throws Throwable {
        String methodName = (String) getMethodNameHandle.invoke(method);
        Object genericReturnType = getGenericReturnTypeHandle.invoke(method);
        Object[] paramTypes = (Object[]) getGenericParameterTypesHandle.invoke(method);
        int modifiers = (int) getModifiersHandle.invoke(method);
        String static_ = isStatic(modifiers) ? " static " : " ";
        String final_ = isFinal(modifiers) ? "final " : "";

        StringBuilder paramString = new StringBuilder();
        for (Object paramType : paramTypes) {
            if (paramString.length() > 0) paramString.append(", ");
            paramString.append(String.valueOf(paramType));
        }
        logger.info(getVisibility(modifiers) + static_ + final_ + String.valueOf(genericReturnType) + " " + methodName + "(" + paramString.toString() + ")");
    }

    public static void logConstructor(Object constructor, Class<?> cls) throws Throwable {
        Object[] paramTypes = (Object[]) getConstructorGenericParameterTypesHandle.invoke(constructor);

        StringBuilder paramString = new StringBuilder();
        for (Object paramType : paramTypes) {
            if (paramString.length() > 0) paramString.append(", ");
            paramString.append(String.valueOf(paramType));
        }
        logger.info("public " + cls.getSimpleName() + "(" + paramString.toString() + ")");
    }

    public static void logConstructors(Object instance) {
        logConstructors(instance.getClass());
    }

    public static void logConstructors(Class<?> cls) {
        try {
            Object[] ctors = cls.getDeclaredConstructors();
            for (Object ctor : ctors) {
                logConstructor(ctor, cls);
            }
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static void logMethods(Object instance) {
        logMethods(instance.getClass());
    }

    public static void logMethods(Class<?> cls) {
        print("---------------------------------");
        print("METHODS FOR:", cls);
        print("---------------------------------");
        try {
            logConstructors(cls);
            List<Class<?>> classHierarchy = new ArrayList<>();
            Class<?> currentClass = cls;
            while (currentClass != null) {
                classHierarchy.add(0, currentClass);
                currentClass = currentClass.getSuperclass();
            }
            for (Class<?> hierarchyClass : classHierarchy) {
                for (Object method : hierarchyClass.getDeclaredMethods()) {
                    logMethod(method);
                }
            }
        } catch (Throwable e) {
            print(e);
            logger.info("Error logging methods: ", e);
        }
    }

    public static HashMap<String, Object> getMethods(Object instance) {
        HashMap<String, Object> methods = new HashMap<>();
        try {
            for (Object method : instance.getClass().getDeclaredMethods()) {
                String methodName = (String) getMethodNameHandle.invoke(method);
                methods.put(methodName, method);
            }
        } catch (Throwable ignored) {}
        return methods;
    }

    public static Object getMethod(String methodName, Object instance, int paramCount) {
        for (Object method : instance.getClass().getMethods()) {
            try {
                if (((String)getMethodNameHandle.invoke(method)).equals(methodName) && 
                    ((Object[])getParameterTypesHandle.invoke(method)).length == paramCount) {
                    return method;
                }
            } catch (Throwable e) {
                print(e);
            }
        }
        return null;
    }

    public static Object getMethod(String methodName, Class<?> cls, int paramCount) {
        for (Object method : cls.getMethods()) {
            try {
                if (((String)getMethodNameHandle.invoke(method)).equals(methodName) && 
                    ((Object[])getParameterTypesHandle.invoke(method)).length == paramCount) {
                    return method;
                }
            } catch (Throwable e) {
                print(e);
            }
        }
        return null;
    }

    public static Object getMethodDeclared(String methodName, Class<?> cls, int paramCount) {
        for (Object method : cls.getDeclaredMethods()) {
            try {
                if (((String)getMethodNameHandle.invoke(method)).equals(methodName) && 
                    ((Object[])getParameterTypesHandle.invoke(method)).length == paramCount) {
                    return method;
                }
            } catch (Throwable e) {
                print(e);
            }
        }
        return null;
    }

    public static Object getMethodByParamTypes(Object instance, Class<?>[] parameterTypes) {
        for (Object method : instance.getClass().getDeclaredMethods()) {
            try {
                Class<?>[] targetParameterTypes = (Class<?>[]) getParameterTypesHandle.invoke(method);
                if (targetParameterTypes.length != parameterTypes.length)
                    continue;

                boolean match = true;
                for (int i = 0; i < targetParameterTypes.length; i++) {
                    if (!targetParameterTypes[i].equals(parameterTypes[i])) {
                        match = false;
                        break;
                    }
                }

                if (match) return method;
            } catch (Throwable e) {
                print(e);
            }
        }
        return null;
    }

    public static List<Object> getMethodsByParamTypes(Object instance, Class<?>[] parameterTypes) {
        List<Object> methods = new ArrayList<>();

        for (Object method : instance.getClass().getDeclaredMethods()) {
            try {
                Class<?>[] targetParameterTypes = (Class<?>[]) getParameterTypesHandle.invoke(method);
                if (targetParameterTypes.length != parameterTypes.length)
                    continue;

                boolean match = true;
                for (int i = 0; i < targetParameterTypes.length; i++) {
                    if (!targetParameterTypes[i].equals(parameterTypes[i])) {
                        match = false;
                        break;
                    }
                }

                if (match) methods.add(method);
            } catch (Throwable e) {
                print(e);
            }
        }
        return methods;
    }

    public static Object getMethodExplicit(String methodName, Object instance, Class<?>[] parameterTypes) {
        for (Object method : instance.getClass().getMethods()) {
            try {
                if (((String) getMethodNameHandle.invoke(method)).equals(methodName)) {
                    Class<?>[] targetParameterTypes = (Class<?>[]) getParameterTypesHandle.invoke(method);
                    if (targetParameterTypes.length != parameterTypes.length)
                        continue;
    
                    boolean match = true;
                    for (int i = 0; i < targetParameterTypes.length; i++) {
                        if (!targetParameterTypes[i].getCanonicalName().equals(parameterTypes[i].getCanonicalName())) {
                            match = false;
                            break;
                        }
                    }
    
                    if (match) return method;
                }
            } catch (Throwable e) {
                print(e);
            }
        }
        return null;
    }

    public static Object getMethodExplicit(String methodName, Class<?> cls, Class<?>[] parameterTypes) {
        for (Object method : cls.getMethods()) {
            try {
                if (((String) getMethodNameHandle.invoke(method)).equals(methodName)) {
                    Class<?>[] targetParameterTypes = (Class<?>[]) getParameterTypesHandle.invoke(method);
                    if (targetParameterTypes.length != parameterTypes.length)
                        continue;
    
                    boolean match = true;
                    for (int i = 0; i < targetParameterTypes.length; i++) {
                        if (!targetParameterTypes[i].getCanonicalName().equals(parameterTypes[i].getCanonicalName())) {
                            match = false;
                            break;
                        }
                    }
    
                    if (match) return method;
                }
            } catch (Throwable e) {
                print(e);
            }
        }
        return null;
    }

    public static List<Object> getMethodsByReturnType(Class<?> cls, Class<?> returnType, int numArgs) {
        List<Object> methods = new ArrayList<>();
        for (Object method : cls.getDeclaredMethods()) {
            try {
                Class<?>[] targetParamTypes = (Class<?>[]) getParameterTypesHandle.invoke(method);
                if (numArgs != targetParamTypes.length) continue;

                Class<?> targetReturnType = (Class<?>) getReturnTypeHandle.invoke(method);
                if (targetReturnType.equals(returnType)) methods.add(method);

            } catch (Throwable e) {
                print(e);
            }
        }
        return methods;
    }

    public static List<Object> getMethodsByReturnType(Class<?> cls, Class<?> returnType) {
        List<Object> methods = new ArrayList<>();
        for (Object method : cls.getDeclaredMethods()) {
            try {
                Class<?> targetReturnType = (Class<?>) getReturnTypeHandle.invoke(method);
                if (targetReturnType.equals(returnType)) methods.add(method);
            } catch (Throwable e) {
                print(e);
            }
        }
        return methods;
    }

    public static Object invokeMethod(String methodName, Object instance, Object... arguments) {
        try {
            Object method = instance.getClass().getMethod(methodName);
            return invokeMethodHandle.invoke(method, instance, arguments);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static Object getMethodFromSuperClassAndInvokeDirectly(String methodName, Object instance, Object... arguments) {
        Object method = getMethodFromSuperclass(methodName, instance);
        if (method == null) return null;
        return invokepublicMethodDirectly(method, instance, arguments);
    }

    public static Object getMethodExplicitFromSuperClassAndInvokeDirectly(String methodName, Object instance, Class<?>[] targetParamTypes, Object... arguments) {
        Object method = getMethodExplicitFromSuperclass(methodName, targetParamTypes, instance);
        if (method == null) return null;
        return invokepublicMethodDirectly(method, instance, arguments);
    }

    public static Object getMethodAndInvokeDirectly(String methodName, Object instance, int argumentsNum, Object... arguments) {
        Object method = getMethod(methodName, instance, argumentsNum);
        if (method == null) return null;
        return invokeMethodDirectly(method, instance, arguments);
    }

    public static Object getMethodDeclaredAndInvokeDirectly(String methodName, Object instance, int argumentsNum, Object... arguments) {
        Object method = getMethodDeclared(methodName, instance.getClass(), argumentsNum);
        if (method == null) return null;
        return invokepublicMethodDirectly(method, instance, arguments);
    }

    public static Object getMethodExplicitAndInvokeDirectly(String methodName, Object instance, Class<?>[] parameterTypes, Object... arguments) {
        Object method = getMethodExplicit(methodName, instance, parameterTypes);
        if (method == null) return null;
        return invokeMethodDirectly(method, instance, arguments);
    }

    public static void setMethodAccessible(Object method, boolean accessible) {
        try {
            setMethodAccessible.invoke(method, true);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static Object invokeMethodDirectly(Object method, Object instance, Object... arguments) {
        try {
            return invokeMethodHandle.invoke(method, instance, arguments);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static Object invokepublicMethodDirectly(Object method, Object instance, Object... arguments) {
        try {
            setMethodAccessible.invoke(method, true);
            return invokeMethodHandle.invoke(method, instance, arguments);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static Object invokeNonpublicMethodDirectly(Object method, Object instance, Object... arguments) {
        try {
            if (isPublic((int)getModifiersHandle.invoke(method))) return null;
            return invokeMethodHandle.invoke(method, instance, arguments);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static Object invokeMethodDirectly(Object method, Object instance) {
        try {
            return invokeMethodHandle.invoke(method, instance);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static Object instantiateClass(String canonicalName, Class<?>[] paramTypes, Object... params) {
        try {
            Class<?> clazz = Class.forName(canonicalName, false, Class.class.getClassLoader());
            Object ctor = clazz.getDeclaredConstructor(paramTypes);
            setConstructorAccessibleHandle.invoke(ctor, true);
            return constructorNewInstanceHandle.invoke(ctor, params);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static Object instantiateClass(Object ctor, Object... args) {
        try {
            return RolfLectionUtil.constructorNewInstanceHandle.invoke(ctor, args);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static Class<?>[] getConstructorParamTypes(Object ctor) {
        try {
            return (Class<?>[]) getConstructorParameterTypesHandle.invoke(ctor);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Class<?>[]> getConstructorParamTypes(Class<?> cls) {
        Object[] ctors = cls.getDeclaredConstructors();
        List<Class<?>[]> lst = new ArrayList<>();

        try {
            for (Object ctor : ctors) {
                Class<?>[] ctorParams = (Class<?>[]) getConstructorParameterTypesHandle.invoke(ctor);
                lst.add(ctorParams);
            }
            return lst;

        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static Class<?>[] getConstructorParamTypesSingleConstructor(Class<?> cls) {
        Object ctor = cls.getDeclaredConstructors()[0];
        try {
            return (Class<?>[]) getConstructorParameterTypesHandle.invoke(ctor);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static Object instantiateClass(Class<?> clazz, Class<?>[] paramTypes, Object... params) {
        try {
            Object ctor = clazz.getDeclaredConstructor(paramTypes);
            setConstructorAccessibleHandle.invoke(ctor, true);
            return constructorNewInstanceHandle.invoke(ctor, params);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static Class<?> getClazz(String canonicalName) {
        try {
            return Class.forName(canonicalName);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static void logEnumConstantNames(Class<?> clazz) {
        try {
            if (!clazz.isEnum()) throw new IllegalArgumentException("Not an enum");
    
            Object[] constants = clazz.getEnumConstants();

            for (Object constant : constants) {
                print(constant);
            }
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static Object getEnumConstantByName(String canonicalName, String constantName) {
        try {
            Class<?> clazz = Class.forName(canonicalName);
            if (!clazz.isEnum()) throw new IllegalArgumentException("Not an enum");
            return Enum.valueOf((Class<? extends Enum>) clazz, constantName);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean doInstantiationParamsMatch(Class<?> cls, Class<?>[] targetParams) {
        Object[] ctors = cls.getDeclaredConstructors();
        for (Object ctor : ctors) {
            try {
                Class<?>[] ctorParams = (Class<?>[]) getConstructorParameterTypesHandle.invoke(ctor);
                if (ctorParams.length != targetParams.length) continue;

                boolean match = true;
                for (int i = 0; i < ctorParams.length; i++) {
                    if (!ctorParams[i].equals(targetParams[i])) {
                        match = false;
                        break;
                    }
                }
                if (match) return true;

            } catch (Throwable e) {
                print(e);
            }
        }
        return false;
    }

    public static Object getMethodFromSuperclass(String methodName, Object instance) {
        try {
            Class<?> currentClass = instance.getClass();

            while (currentClass != null) {
                for (Object method : currentClass.getDeclaredMethods()) {
                    if (getMethodNameHandle.invoke(method).equals(methodName)) {
                        return method;
                    }
                }
                currentClass = currentClass.getSuperclass();
            }
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public static Object getMethodExplicitFromSuperclass(String methodName, Class<?>[] targetParamTypes, Object instance) {
        try {
            Class<?> currentClass = instance.getClass();

            while (currentClass != null) {
                outer:
                for (Object method : currentClass.getDeclaredMethods()) {
                    if (getMethodNameHandle.invoke(method).equals(methodName)) {
                        Class<?>[] paramTypes = getMethodParamTypes(method);

                        if (paramTypes.length != targetParamTypes.length) continue;

                        for (int i = 0; i < paramTypes.length; i++) {
                            if (!paramTypes[i].equals(targetParamTypes[i])) continue outer;
                        }

                        return method;
                    }
                }
                currentClass = currentClass.getSuperclass();
            }
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public static boolean areParameterTypesMatching(Class<?>[] methodParamTypes, Class<?>[] targetParamTypes) {
        if (methodParamTypes.length != targetParamTypes.length) {
            return false;
        }

        for (int i = 0; i < methodParamTypes.length; i++) {
            if (!methodParamTypes[i].isAssignableFrom(targetParamTypes[i])) {
                return false;
            }
        }

        return true;
    }

    public static void logField(String fieldName, Class<?> fieldType, Object field, int i) throws Throwable {
        if (List.class.isAssignableFrom(fieldType) || Map.class.isAssignableFrom(fieldType)
        || Set.class.isAssignableFrom(fieldType) || Collection.class.isAssignableFrom(fieldType)) {
            print(getGenericTypeHandle.invoke(field), fieldName, i);
        } else {
            print(fieldType.getCanonicalName(), fieldName, i);
        }
    }

    public static void logField(String fieldName, Class<?> fieldType, Object field, int i, Object instance) throws Throwable {
        setFieldAccessibleHandle.invoke(field, true);

        if (List.class.isAssignableFrom(fieldType) || Map.class.isAssignableFrom(fieldType)
        || Set.class.isAssignableFrom(fieldType) || Collection.class.isAssignableFrom(fieldType)) {
            print(getGenericTypeHandle.invoke(field), fieldName, i, getFieldHandle.invoke(field, instance));
        } else {
            print(fieldType.getCanonicalName(), fieldName, i, getFieldHandle.invoke(field, instance));
        }
    }

    public static boolean isNativeJavaClass(Class<?> clazz) {
        if (clazz == null || clazz.getPackage() == null) return false;
        String pkg = clazz.getPackage().getName();
        return "java.lang".equals(pkg) || "java.util".equals(pkg);
    }

    public static Object getFieldAtIndex(Object instance, int index) {
        try {
            int i = 0;
            Class<?> currentClass = instance.getClass();
            while (currentClass != null) {
                for (Object field : currentClass.getDeclaredFields()) {
                    if (i == index) {
                        setFieldAccessibleHandle.invoke(field, true);
                        return getFieldHandle.invoke(field, instance);
                    }
                    i++;
                }
                currentClass = currentClass.getSuperclass();
            }
            return null;
        } catch (Throwable e) {
            print(e);
            return null;
        }
    }

    public static void setFieldAtIndex(Object instance, int index, Object value) {
        try {
            int i = 0;
            Class<?> currentClass = instance.getClass();
            while (currentClass != null) {
                for (Object field : currentClass.getDeclaredFields()) {
                    if (i == index) {
                        setFieldAccessibleHandle.invoke(field, true);
                        setFieldHandle.invoke(field, instance, value);
                        return;
                    }
                    i++;
                }
                currentClass = currentClass.getSuperclass();
            }
        } catch (Throwable e) {
            print(e);
        }
    }

    public static Class<?> getFieldTypeAtIndex(Object instance, int index) {
        try {
            int i = 0;
            Class<?> currentClass = instance.getClass();
            while (currentClass != null) {
                for (Object field : currentClass.getDeclaredFields()) {
                    if (i == index) {
                        return (Class<?>) getFieldTypeHandle.invoke(field);
                    }
                    i++;
                }
                currentClass = currentClass.getSuperclass();
            }
            return null;
        } catch (Throwable e) {
            print(e);
            return null;
        }
    }

    public static Class<?> getFieldType(Object field) {
        try {
            return (Class<?>) getFieldTypeHandle.invoke(field);
        } catch (Throwable e) {
            print(e);
            return null;
        }
    }

    public static void logFields(Object instance) {
        if (instance == null) return;
        try {
            print("---------------------------------");
            print("FIELDS FOR:", instance.getClass());
            print("---------------------------------");
            int i = 0;
            Class<?> currentClass = instance.getClass();
            while (currentClass != null) {
                for (Object field : currentClass.getDeclaredFields()) {
                    String fieldName = (String) getFieldNameHandle.invoke(field);
                    Class<?> fieldType = (Class<?>) getFieldTypeHandle.invoke(field);
                    
                    // if (fieldType.isPrimitive() || (fieldType.isArray() && fieldType.getComponentType().isPrimitive())) {
                    //     print(fieldType.getCanonicalName() + " " + fieldName + " " + i);
                    //     i++;
                    //     continue;
                    // } else {
                        logField(fieldName, fieldType, field, i, instance);
                    // }
                    try {
                        logConstructors(fieldType);
                    } catch (Exception e) {
                        print(e);
                    }
                    i++;
                }
                currentClass = currentClass.getSuperclass();
            }
        } catch (Throwable e) {
            logger.info("Error logging fields: ", e);
        }
    }

    public static void logFields(Class<?> cls) {
        try {
            print("---------------------------------");
            print("FIELDS FOR:", cls);
            print("---------------------------------");
            int i = 0;
            Class<?> currentClass = cls;
            while (currentClass != null) {
                for (Object field : currentClass.getDeclaredFields()) {
                    String fieldName = (String) getFieldNameHandle.invoke(field);
                    Class<?> fieldType = (Class<?>) getFieldTypeHandle.invoke(field);
                    
                    if (fieldType.isPrimitive() || (fieldType.isArray() && fieldType.getComponentType().isPrimitive())) {
                        print(fieldType.getCanonicalName() + " " + fieldName + " " + i);
                        i++;
                        continue;
                    } else {
                        logField(fieldName, fieldType, field, i);
                    }
                    try {
                        logConstructors(fieldType);
                    } catch (Exception e) {
                        print(e);
                    }
                    i++;
                }
                currentClass = currentClass.getSuperclass();
            }
        } catch (Throwable e) {
            logger.info("Error logging fields: ", e);
        }
    }

    public static void logFieldsOfFieldIndex(Object instance, int index) {
        try {
            int i = 0;
            Class<?> currentClass = instance.getClass();
            while (currentClass != null) {
                for (Object field : currentClass.getDeclaredFields()) {
                    if (i == index) {
                        logger.info("---------------------------------------------");
                        Class<?> fieldType = (Class<?>) getFieldTypeHandle.invoke(field);
                        if (fieldType.isPrimitive()) return;
                        int j = 0;
                        for (Object childField : fieldType.getDeclaredFields()) {
                            String childFieldName = (String) getFieldNameHandle.invoke(childField);
                            Class<?> childFieldType = (Class<?>) getFieldTypeHandle.invoke(childField);

                            logField(childFieldName, childFieldType, childField, j);
                            j++;
                        }
            
                        try {
                            logConstructors(fieldType);
                        } catch (Exception e) {
                            print(e);
                        }
                        return;
                    }
                    i++;
                }
                currentClass = currentClass.getSuperclass();
            }
        } catch (Throwable e) {
            logger.info("Error logging fields: ", e);
        }
    }

    public static int getMethodModifiers(Object method) {
        try {
            return (int) getModifiersHandle.invoke(method);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isPublic(int modifiers) {
        return (modifiers & 1) != 0;
    }

    public static boolean isStatic(int modifiers) {
        return (modifiers & 8) != 0;
    }

    public static boolean isFinal(int modifiers) {
        return (modifiers & 16) != 0;
    }

    public static boolean isPrivate(int modifiers) {
        return (modifiers & 2) != 0;
    }

    public static boolean isProtected(int modifiers) {
        return (modifiers & 4) != 0;
    }

    public static String getVisibility(int modifiers) {
        if (isPublic(modifiers)) return "public";
        if (isPrivate(modifiers)) return "private";
        if (isProtected(modifiers)) return "protected";
        return "package-private";
    }

    public static String getMethodPrefix(int modifiers) {
        String prefix = getVisibility(modifiers);
        
        if (isStatic(modifiers)) prefix += " static ";
        return prefix;
    }

    public static void init() {}
}