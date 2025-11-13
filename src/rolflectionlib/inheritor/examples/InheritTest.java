package rolflectionlib.inheritor.examples;

import java.util.HashMap;
import java.util.Map;

import rolflectionlib.inheritor.ClassMethodHook;
import rolflectionlib.inheritor.Inherit;
import rolflectionlib.inheritor.InterfaceMethodHook;
import rolflectionlib.inheritor.MethodData;
import rolflectionlib.inheritor.MethodHook;
import rolflectionlib.inheritor.test.BaseTestClass;
import rolflectionlib.inheritor.test.BaseTestInterface;

import rolflectionlib.util.RolfLectionUtil;

public class InheritTest {
    private static final Object testCtor;

    static {
        try {
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
                BaseTestClass.class, // super class
                new Class<?>[] {BaseTestInterface.class}, // interfaces to implement
                BaseTestClass.class.getConstructor(new Class<?>[] {int.class, String.class, Map.class}), // super class constructor to use
                "rolflectionlib/inheritor/examples/BaseClassInheritor", // internal name of resulting subclass
                methodData // MethodData array
            );

            testCtor = subClass.getConstructors()[0];

        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private final BaseTestInterface instance;
    private final InheritTest self = this; // this isnt strictly needed but i put it for readability

    public InheritTest(int returnValue, String arg1, Map<String, Object> arg2) {
        // Important note: hooks must be in the same order as MethodData was passed when generating the subclass

        MethodHook addHook = new ClassMethodHook() {
            @Override
            public Object[] runBefore(Object... methodParams) {
                return self.addBefore(methodParams);
            }

            @Override
            public Object runAfter(Object returnValue) {
                return self.addAfter(returnValue);
            }
        };

        MethodHook incrementHook = new ClassMethodHook() {
            @Override
            public Object[] runBefore(Object... methodParams) {
                return self.incrementBefore(methodParams);
            }

            @Override
            public Object runAfter(Object returnValue) {
                return self.incrementAfter(returnValue);
            }
        };

        MethodHook getValueHook = new ClassMethodHook() {
            @Override
            public Object[] runBefore(Object... methodParams) {
                return self.getValueBefore(methodParams);
            }

            @Override
            public Object runAfter(Object returnValue) {
                return self.getValueAfter(returnValue);
            }
        };

        MethodHook getHook = new ClassMethodHook() {
            @Override
            public Object[] runBefore(Object... methodParams) {
                return self.getBefore(methodParams);
            }

            @Override
            public Object runAfter(Object returnValue) {
                return self.getAfter(returnValue);
            }            
        };

        MethodHook putHook = new ClassMethodHook() {
            @Override
            public Object[] runBefore(Object... methodParams) {
                return self.putBefore(methodParams);
            }

            @Override
            public Object runAfter(Object returnValue) {
                return self.putAfter(returnValue);
            }
        };

        MethodHook getStringHook = new ClassMethodHook() {

            @Override
            public Object[] runBefore(Object... methodParams) {
                return self.getStringBefore(methodParams);
            }

            @Override
            public Object runAfter(Object returnValue) {
                return self.getStringAfter(returnValue);
            }
        };

        MethodHook interfaceStringGetTestHook = new InterfaceMethodHook() {
            @Override
            public Object runInterface(Object... methodParams) {
                return self.interfaceStringGetTest(methodParams);
            }
        };

        MethodHook interfaceVoidTestHook = new InterfaceMethodHook() {
            @Override
            public Object runInterface(Object... methodParams) {
                return self.interfaceVoidTest(methodParams);
            }
        };

        MethodHook interfaceObjTestHook = new InterfaceMethodHook() {
            @Override
            public Object runInterface(Object... methodParams) {
                return self.interfaceObjTest(methodParams);
            }
        };
        // instantiate subclass with hooks in correct order
        Object[] ctorParams = new Object[] {
            returnValue, arg1, arg2, // super params
            
            addHook,
            incrementHook,
            getValueHook,
            getHook,
            putHook,
            getStringHook,

            interfaceStringGetTestHook,
            interfaceVoidTestHook,
            interfaceObjTestHook
        };

        instance = (BaseTestInterface) RolfLectionUtil.instantiateClass(testCtor, ctorParams);
        // log
        RolfLectionUtil.logMethods(instance);
        RolfLectionUtil.logFields(instance);

    }

    public BaseTestInterface getInstance() {
        return this.instance;
    }

    public Object[] addBefore(Object... methodParams) {
        return methodParams;
    }

    public Object addAfter(Object returnValue) {
        Inherit.print("addAfter");
        return returnValue;
    }

    public Object[] incrementBefore(Object... methodParams) {
        return methodParams;
    }

    public Object incrementAfter(Object returnValue) {
        return null;
    }

    public Object[] getValueBefore(Object... methodParams) {
        return methodParams;
    }

    public Object getValueAfter(Object returnValue) {
        return returnValue;
    }

    public Object[] putBefore(Object... methodParams) {
        return methodParams;
    }

    public Object putAfter(Object returnValue) {
        return returnValue;
    }

    public Object[] getBefore(Object... methodParams) {
        return methodParams;
    }

    public Object getAfter(Object returnValue) {
        return returnValue;
    }

    public Object[] getStringBefore(Object... methodParams) {
        return methodParams;
    }

    public Object getStringAfter(Object returnValue) {
        return returnValue;
    }

    public Object interfaceStringGetTest(Object... methodParams) {
        return methodParams;
    }

    public Object interfaceVoidTest(Object... methodParams) {
        return methodParams;
    }

    public Object interfaceObjTest(Object... methodParams) {
        return methodParams;
    }

    public static void test() {
        BaseTestInterface test = new InheritTest(5, "STRING", new HashMap<>()).getInstance();

        Inherit.print(RolfLectionUtil.getMethodAndInvokeDirectly("add", test, 5, 8));
    }
}