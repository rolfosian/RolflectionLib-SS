package rolflectionlib.inheritor.test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.objectweb.asm.Opcodes;

import rolflectionlib.inheritor.Inherit;
import rolflectionlib.inheritor.MethodData;
import rolflectionlib.inheritor.MethodHook;

import rolflectionlib.util.ClassRefs;
import rolflectionlib.util.RolfLectionUtil;

// i need a better testing mechanism than this mess
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

                @Override
                public Object runInterface(Object... methodParams) {
                    throw new UnsupportedOperationException("Unimplemented method 'runInterface'");
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

                @Override
                public Object runInterface(Object... methodParams) {
                    throw new UnsupportedOperationException("Unimplemented method 'runInterface'");
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

                @Override
                public Object runInterface(Object... methodParams) {
                    throw new UnsupportedOperationException("Unimplemented method 'runInterface'");
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

                @Override
                public Object runInterface(Object... methodParams) {
                    throw new UnsupportedOperationException("Unimplemented method 'runInterface'");
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

                @Override
                public Object runInterface(Object... methodParams) {
                    throw new UnsupportedOperationException("Unimplemented method 'runInterface'");
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

                @Override
                public Object runInterface(Object... methodParams) {
                    throw new UnsupportedOperationException("Unimplemented method 'runInterface'");
                }
                
            };

            MethodHook interfaceStringGetTestHook = new MethodHook() {

                @Override
                public Object[] runBefore(Object... methodParams) {
                    return null;
                }

                @Override
                public Object runAfter(Object returnValue) {
                    return null;
                }

                @Override
                public Object runInterface(Object... methodParams) {
                    return "BLARGH";
                }
                
            };

            MethodHook interfaceVoidTestHook = new MethodHook() {

                @Override
                public Object[] runBefore(Object... methodParams) {
                    return null;
                }

                @Override
                public Object runAfter(Object returnValue) {
                    return null;
                }

                @Override
                public Object runInterface(Object... methodParams) {
                    Inherit.print(methodParams[0], "AND VOID");
                    return null;
                }
                
            };

            MethodHook interfaceObjTestHook = new MethodHook() {

                @Override
                public Object[] runBefore(Object... methodParams) {
                    return null;
                }

                @Override
                public Object runAfter(Object returnValue) {
                    return null;
                }

                @Override
                public Object runInterface(Object... methodParams) {
                    return new Object() {
                        @Override
                        public String toString() {
                            return "777 " + methodParams[0].toString();
                        }
                    };
                }
                
            };

            // this order must be the same as the order of the hooks passed to the constructor
            MethodData[] methodData = new MethodData[] {
                new MethodData(RolfLectionUtil.getMethod("add", BaseTestClass.class)),
                new MethodData(RolfLectionUtil.getMethod("increment", BaseTestClass.class)),
                new MethodData(RolfLectionUtil.getMethod("getValue", BaseTestClass.class)),
                new MethodData(RolfLectionUtil.getMethod("get", BaseTestClass.class)),
                new MethodData(RolfLectionUtil.getMethod("put", BaseTestClass.class)),
                new MethodData(RolfLectionUtil.getMethod("getString", BaseTestClass.class)),

                new MethodData(RolfLectionUtil.getMethod("interfaceStringGetTest", BaseTestInterface.class)),
                new MethodData(RolfLectionUtil.getMethod("interfaceVoidTest", BaseTestInterface.class)),
                new MethodData(RolfLectionUtil.getMethod("interfaceObjTest", BaseTestInterface.class))

            };

            // generate subclass
            Class<?> subClass = Inherit.extendClass(
                    BaseTestClass.class,
                    new Class<?>[] {BaseTestInterface.class},
                    BaseTestClass.class.getConstructor(new Class<?>[] {int.class, String.class, Map.class}),
                    "BaseClassInheritor",
                    methodData
            );

            // instantiate subclass with hooks in correct order
            Object[] ctorParams = new Object[] {
                0, "STRING", new HashMap<>(), // super params
                
                addHook,
                incHook,
                getValueHook,
                getHook,
                putHook,
                getStringHook,

                interfaceStringGetTestHook,
                interfaceVoidTestHook,
                interfaceObjTestHook
            };

            Object ctor = subClass.getConstructors()[0];

            BaseTestInterface instance = (BaseTestInterface) RolfLectionUtil.instantiateClass(ctor, ctorParams);
            
            // log
            RolfLectionUtil.logMethods(instance);
            RolfLectionUtil.logFields(instance);

            int addResult = (int) RolfLectionUtil.getMethodAndInvokeDirectly("add", instance, 3, 6);
            Inherit.print("Final add result:", addResult);

            RolfLectionUtil.getMethodAndInvokeDirectly("increment", instance);
            int finalValue = (int) RolfLectionUtil.getPrivateVariable("value", instance);
            Inherit.print("Final value after increment:", finalValue);

            int hookedGetValue = (int) RolfLectionUtil.getMethodAndInvokeDirectly("getValue", instance);
            Inherit.print("Hooked getValue:", hookedGetValue);

            RolfLectionUtil.getMethodAndInvokeDirectly("put", instance, "key", "value");

            Inherit.print(RolfLectionUtil.getMethodAndInvokeDirectly("get", instance));

            Inherit.print(RolfLectionUtil.getMethodAndInvokeDirectly("getString", instance));

            Inherit.print(instance.interfaceStringGetTest());
            instance.interfaceVoidTest(555, 666);
            Inherit.print(instance.interfaceObjTest("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"));

            Class<?> listenerClass = Inherit.implementInterface(
                "RolfListener", 
                new Class<?>[]{ClassRefs.actionListenerInterface, ClassRefs.dialogDismissedInterface},
                new MethodData[] {
                    new MethodData(ClassRefs.buttonListenerActionPerformedMethod),
                    new MethodData(ClassRefs.dialogDismissedInterfaceMethod)
                }
            );
            
            ctorParams = new Object[] {
                new MethodHook() {
                    @Override
                    public Object[] runBefore(Object... methodParams) { return null;}
                    @Override
                    public Object runAfter(Object returnValue) { return null; }
                    @Override
                    public Object runInterface(Object... params) { return null; }
                },
                new MethodHook() {
                    @Override
                    public Object[] runBefore(Object... methodParams) { return null;}
                    @Override
                    public Object runAfter(Object returnValue) { return null; }
                    @Override
                    public Object runInterface(Object... params) { return null; }
                }
            };

            ctor = listenerClass.getConstructors()[0];



        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}