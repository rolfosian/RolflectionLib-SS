package rolflectionlib.inheritor.test;

import java.util.Arrays;

import org.objectweb.asm.Opcodes;

import rolflectionlib.inheritor.Inherit;
import rolflectionlib.util.RolfLectionUtil;
import rolflectionlib.inheritor.Inherit.MethodData;
import rolflectionlib.inheritor.Inherit.MethodHook;;

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
                    // hookArgs[0] = (int) hookArgs[0] * 2; // modify return
                    return returnValue;
                }
            };

            // hook for increment
            MethodHook incHook = new MethodHook() {
                @Override
                public Object[] runBefore(Object... hookArgs) {
                    Inherit.print("increment pre-hook called");
                    return hookArgs;
                }

                @Override
                public Object runAfter(Object returnValue) {
                    Inherit.print("increment post-hook called");
                    return returnValue;
                }
            };

            MethodHook getValueHook = new MethodHook() {
                @Override
                public Object[] runBefore(Object... methodParams) {
                    Inherit.print("getValue pre-hook called");
                    return methodParams;
                }

                @Override
                public Object runAfter(Object returnValue) {
                    Inherit.print("getValue post-hook called");
                    return 42069;
                }
            };

            MethodData[] methodData = new MethodData[] {
                new Inherit.MethodData(
                    ACC_PUBLIC,
                    RolfLectionUtil.getMethod("add", BaseTestClass.class, 2),
                    null
                ),
                new Inherit.MethodData(
                    ACC_PUBLIC,
                    RolfLectionUtil.getMethod("increment", BaseTestClass.class, 0),
                    null
                ),
                new MethodData(
                    ACC_PUBLIC,
                    RolfLectionUtil.getMethod("getValue", BaseTestClass.class, 0),
                    null
                )
            };

            Object[] ctorParams = new Object[] {
                0, addHook, incHook, getValueHook
            };

            // generate subclass
            Class<?> subClass = Inherit.inheritClass(
                    BaseTestClass.class,
                    BaseTestClass.class.getConstructor(int.class),
                    null,
                    "BaseClassInheritor",
                    methodData
            );

            // instantiate subclass with hooks
            Object ctor = subClass.getConstructors()[0];
            Object instance = RolfLectionUtil.instantiateClass(ctor, ctorParams);

            RolfLectionUtil.logMethods(instance);
            RolfLectionUtil.logFields(instance);

            // invoke add
            int addResult = (int) RolfLectionUtil.getMethodAndInvokeDirectly("add", instance, 2, 3, 6);
            Inherit.print("Final add result:", addResult);

            // invoke increment
            RolfLectionUtil.getMethodAndInvokeDirectly("increment", instance, 0);
            int finalValue = (int) RolfLectionUtil.getPrivateVariable("value", instance);
            Inherit.print("Final value after increment:", finalValue);

            // invoke getValue
            int hookedGetValue = (int) RolfLectionUtil.getMethodAndInvokeDirectly("getValue", instance, 0);
            Inherit.print("Hooked getValue:", hookedGetValue);

        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}