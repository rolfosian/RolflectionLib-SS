package rolflectionlib.inheritor.test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.objectweb.asm.Opcodes;

import rolflectionlib.inheritor.Inherit;
import rolflectionlib.inheritor.MethodData;
import rolflectionlib.inheritor.MethodHook;
import rolflectionlib.util.RolfLectionUtil;

public class InheritTest implements Opcodes {

    public static void main() {
        try {
            // hook for add
            MethodHook addHook = new MethodHook() {
                @Override
                public Object[] runBefore(Object... hookArgs) {
                    Inherit.print("add pre-hook called, original arg:", Arrays.asList(hookArgs));
                    hookArgs[0] = (int) hookArgs[0] + 5; // modify argument
                    return hookArgs;
                }

                @Override
                public Object runAfter(Object returnValue) {
                    Inherit.print("add post-hook called, original return value:", returnValue);
                    returnValue = (int) returnValue * 2; // modify return
                    return returnValue;
                }
            };

            // hook for increment
            MethodHook incHook = new MethodHook() {
                @Override
                public Object[] runBefore(Object... hookArgs) {
                    Inherit.print("increment pre-hook called");
                    return null;
                }

                @Override
                public Object runAfter(Object returnValue) {
                    Inherit.print("increment post-hook called");
                    return null;
                }
            };

            MethodHook getValueHook = new MethodHook() {
                @Override
                public Object[] runBefore(Object... methodParams) {
                    Inherit.print("getValue pre-hook called");
                    return null;
                }

                @Override
                public Object runAfter(Object returnValue) {
                    Inherit.print("getValue post-hook called");
                    return 42069;
                }
            };

            MethodHook getHook = new MethodHook() {

                @Override
                public Object[] runBefore(Object... methodParams) {
                    return methodParams;
                }

                @Override
                public Object runAfter(Object returnValue) {
                    return returnValue;
                }
                
            };

            MethodHook putHook = new MethodHook() {

                @Override
                public Object[] runBefore(Object... methodParams) {
                    methodParams[0] = "STRING";
                    return methodParams;
                }

                @Override
                public Object runAfter(Object returnValue) {
                    return returnValue;
                }
                
            };

            MethodHook getStringHook = new MethodHook() {

                @Override
                public Object[] runBefore(Object... methodParams) {
                    return methodParams;
                }

                @Override
                public Object runAfter(Object returnValue) {
                    return "HO HO HO";
                }
                
            };

            MethodData[] methodData = new MethodData[] {
                new MethodData(RolfLectionUtil.getMethod("add", BaseTestClass.class)),
                new MethodData(RolfLectionUtil.getMethod("increment", BaseTestClass.class)),
                new MethodData(RolfLectionUtil.getMethod("getValue", BaseTestClass.class)),
                new MethodData(RolfLectionUtil.getMethod("get", BaseTestClass.class)),
                new MethodData(RolfLectionUtil.getMethod("put", BaseTestClass.class)),
                new MethodData(RolfLectionUtil.getMethod("getString", BaseTestClass.class))
            };

            // generate subclass
            Class<?> subClass = Inherit.inheritClass(
                    BaseTestClass.class,
                    BaseTestClass.class.getConstructor(new Class<?>[] {int.class, String.class, Map.class}),
                    null,
                    "BaseClassInheritor",
                    methodData
            );

            // instantiate subclass with hooks
            Object ctor = subClass.getConstructors()[0];

            Object[] ctorParams = new Object[] {
                0, "STRING", new HashMap<>(), addHook, incHook, getValueHook, getHook, putHook, getStringHook
            };

            Object instance = RolfLectionUtil.instantiateClass(ctor, ctorParams);
            
            // log
            RolfLectionUtil.logMethods(instance);
            RolfLectionUtil.logFields(instance);

            // invoke add
            int addResult = (int) RolfLectionUtil.getMethodAndInvokeDirectly("add", instance, 3, 6);
            Inherit.print("Final add result:", addResult);

            // invoke increment
            RolfLectionUtil.getMethodAndInvokeDirectly("increment", instance);
            int finalValue = (int) RolfLectionUtil.getPrivateVariable("value", instance);
            Inherit.print("Final value after increment:", finalValue);

            // invoke getValue
            int hookedGetValue = (int) RolfLectionUtil.getMethodAndInvokeDirectly("getValue", instance);
            Inherit.print("Hooked getValue:", hookedGetValue);

            RolfLectionUtil.getMethodAndInvokeDirectly("put", instance, "key", "value");

            Inherit.print(RolfLectionUtil.getMethodAndInvokeDirectly("get", instance));

            Inherit.print(RolfLectionUtil.getMethodAndInvokeDirectly("getString", instance));

        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}