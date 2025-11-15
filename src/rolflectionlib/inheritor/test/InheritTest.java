package rolflectionlib.inheritor.test;

import java.util.*;

import rolflectionlib.inheritor.Inherit;
import rolflectionlib.inheritor.SuperClassMethodHook;
import rolflectionlib.inheritor.InterfaceMethodHook;
import rolflectionlib.inheritor.MethodData;
import rolflectionlib.inheritor.MethodHook;
import rolflectionlib.inheritor.OverrideMethodHook;

import rolflectionlib.util.RolfLectionUtil;

public class InheritTest {
    private static final Object testCtor;
    private static final Class<?> testClass;

    static {
        try {
            // this order must be the same as the order of the hooks passed to the constructor
            MethodData[] methodData = new MethodData[] {
                new MethodData(RolfLectionUtil.getMethod("manyParams", BaseTestClass.class), true),

                new MethodData(RolfLectionUtil.getMethod("oneParamReturnValue1", BaseTestClass.class), true),
                new MethodData(RolfLectionUtil.getMethod("oneParamReturnValue2", BaseTestClass.class), false),

                new MethodData(RolfLectionUtil.getMethod("oneParamVoid1", BaseTestClass.class), true),
                new MethodData(RolfLectionUtil.getMethod("oneParamVoid2", BaseTestClass.class), false),

                new MethodData(RolfLectionUtil.getMethod("twoParamReturnValue1", BaseTestClass.class), true),
                new MethodData(RolfLectionUtil.getMethod("twoParamReturnValue2", BaseTestClass.class), false),

                new MethodData(RolfLectionUtil.getMethod("twoParamVoid1", BaseTestClass.class), true),
                new MethodData(RolfLectionUtil.getMethod("twoParamVoid2", BaseTestClass.class), false),

                new MethodData(RolfLectionUtil.getMethod("noParamReturnValue1", BaseTestClass.class), true),
                new MethodData(RolfLectionUtil.getMethod("noParamReturnValue2", BaseTestClass.class), false),

                new MethodData(RolfLectionUtil.getMethod("noParamVoid1", BaseTestClass.class), true),
                new MethodData(RolfLectionUtil.getMethod("noParamVoid2", BaseTestClass.class), false),

                new MethodData(RolfLectionUtil.getMethod("interfaceOneParamReturnValue", BaseTestInterface.class), true),
                new MethodData(RolfLectionUtil.getMethod("interfaceOneParamVoid", BaseTestInterface.class), true),
                new MethodData(RolfLectionUtil.getMethod("interfaceTwoParamReturnValue", BaseTestInterface.class), true),
                new MethodData(RolfLectionUtil.getMethod("interfaceTwoParamVoid", BaseTestInterface.class), true),
                new MethodData(RolfLectionUtil.getMethod("interfaceNoParamReturnValue", BaseTestInterface.class), true),
                new MethodData(RolfLectionUtil.getMethod("interfaceNoParamVoid", BaseTestInterface.class), true)
            };

            // generate subclass
            testClass = Inherit.extendClass(
                BaseTestClass.class, // super class
                new Class<?>[] {BaseTestInterface.class}, // interfaces to implement
                BaseTestClass.class.getConstructor(new Class<?>[] {int.class, String.class, Map.class}), // super class constructor to use
                "rolflectionlib/inheritor/test/BaseClassInheritor", // internal name of resulting subclass
                methodData // MethodData array - this order must be the same as the order of the hooks passed to the constructor
            );

            testCtor = testClass.getConstructors()[0];

        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private final BaseTestInterface instance;

    public InheritTest(int returnValue, String arg1, Map<String, Object> arg2) {
        // Important note: hooks must be in the same order as MethodData was passed when generating the subclass
        MethodHook manyParamsHook = new SuperClassMethodHook() {

            @Override
            public Object[] runBefore(Object... methodParams) {
                methodParams[0] = "has no games"; // correct original misinfo param to the truth
                return methodParams;
            }

            @Override
            public Object runAfter(Object returnValue) {
                return "ps3 " + (String) returnValue;
            }
            
        };

        MethodHook oneParamReturnValue1 = new SuperClassMethodHook() {

            @Override
            public Object[] runBefore(Object... methodParams) {
                return methodParams;
            }

            @Override
            public Object runAfter(Object returnValue) {
                return returnValue;
            }
        };

        MethodHook oneParamReturnValue2 = new OverrideMethodHook() {

            @Override
            public Object runFullOverride(Object... params) {
                return params[0];
            }
            
        };

        MethodHook oneParamVoid1 = new SuperClassMethodHook() {
            @Override
            public Object[] runBefore(Object... methodParams) {
                return methodParams;
            }

            @Override
            public Object runAfter(Object returnValue) {
                return returnValue;
            }
        };

        MethodHook oneParamVoid2 = new OverrideMethodHook() {
            @Override
            public Object runFullOverride(Object... params) {
                return null;
            }
        };

        MethodHook twoParamReturnValue1 = new SuperClassMethodHook() {
            @Override
            public Object[] runBefore(Object... methodParams) {
                return methodParams;
            }

            @Override
            public Object runAfter(Object returnValue) {
                return returnValue;
            }
        };

        MethodHook twoParamReturnValue2 = new OverrideMethodHook() {
            @Override
            public Object runFullOverride(Object... params) {
                return params[0];
            }
        };

        MethodHook twoParamVoid1 = new SuperClassMethodHook() {
            @Override
            public Object[] runBefore(Object... methodParams) {
                return methodParams;
            }

            @Override
            public Object runAfter(Object returnValue) {
                return returnValue;
            }
        };

        MethodHook twoParamVoid2 = new OverrideMethodHook() {

            @Override
            public Object runFullOverride(Object... params) {
                return params;
            }
            
        };

        MethodHook noParamReturnValue1 = new SuperClassMethodHook() {
            @Override
            public Object[] runBefore(Object... methodParams) {
                return methodParams;
            }

            @Override
            public Object runAfter(Object returnValue) {
                return returnValue;
            }
        };

        MethodHook noParamReturnValue2 = new OverrideMethodHook() {
            @Override
            public Object runFullOverride(Object... params) {
                return "NULL POINTER EXCEPTION";
            }
        };

        MethodHook noParamVoid1 = new SuperClassMethodHook() {
            @Override
            public Object[] runBefore(Object... methodParams) {
                return methodParams;
            }
            
            @Override
            public Object runAfter(Object returnValue) {
                return returnValue;
            }
        };

        MethodHook noParamVoid2 = new OverrideMethodHook() {
            @Override
            public Object runFullOverride(Object... params) {
                return params;
            }
        };

        MethodHook interfaceOneParamReturnValue = new InterfaceMethodHook() {
            @Override
            public Object runInterface(Object... methodParams) {
                return methodParams[0];
            }
        };

        MethodHook interfaceOneParamVoid = new InterfaceMethodHook() {
            @Override
            public Object runInterface(Object... methodParams) {
                return null;
            }
        };

        MethodHook interfaceTwoParamReturnValue = new InterfaceMethodHook() {
            @Override
            public Object runInterface(Object... methodParams) {
                return methodParams[0];
            }
        };

        MethodHook interfaceTwoParamVoid = new InterfaceMethodHook() {
            @Override
            public Object runInterface(Object... methodParams) {
                return null;
            }
        };

        MethodHook interfaceNoParamReturnValue = new InterfaceMethodHook() {
            @Override
            public Object runInterface(Object... methodParams) {
                return "TWO PLUS TWO IS FOUR";
            }
        };

        MethodHook interfaceNoParamVoid = new InterfaceMethodHook() {
            @Override
            public Object runInterface(Object... methodParams) {
                Inherit.print("NO RETURN TWO PLUS TWO IS FOUR");
                return null;
            }
        };

        // instantiate subclass with hooks in correct order
        Object[] ctorParams = new Object[] {
            returnValue, arg1, arg2, // super params
            
            manyParamsHook,

            oneParamReturnValue1,
            oneParamReturnValue2,

            oneParamVoid1,
            oneParamVoid2,

            twoParamReturnValue1,
            twoParamReturnValue2,

            twoParamVoid1,
            twoParamVoid2,

            noParamReturnValue1,
            noParamReturnValue2,

            noParamVoid1,
            noParamVoid2,

            interfaceOneParamReturnValue,
            interfaceOneParamVoid,
            interfaceTwoParamReturnValue,
            interfaceTwoParamVoid,
            interfaceNoParamReturnValue,
            interfaceNoParamVoid
        };

        instance = (BaseTestInterface) RolfLectionUtil.instantiateClass(testCtor, ctorParams);
        // log
        RolfLectionUtil.logMethods(instance);
        RolfLectionUtil.logFields(instance);
    }

    public BaseTestInterface getInstance() {
        return this.instance;
    }

    public static void test() {
        BaseTestInterface test = new InheritTest(5, "STRING", new HashMap<>()).getInstance();
        
        Inherit.print(RolfLectionUtil.getMethodAndInvokeDirectly(
            "manyParams", 
            test,

            "has games", // misinfo param
            "foo",
            "bar",
            new ArrayList<>(),
            new HashMap<>(),
            5,
            6L,
            (short) 4,
            90D
        ));

        Inherit.print(RolfLectionUtil.getMethodAndInvokeDirectly("oneParamReturnValue1", test, "first"));
        Inherit.print(RolfLectionUtil.getMethodAndInvokeDirectly("oneParamReturnValue2", test, "second"));
    
        Inherit.print("OK: oneParamVoid1");
        RolfLectionUtil.getMethodAndInvokeDirectly("oneParamVoid1", test, Arrays.asList(1));
    
        Inherit.print("OK: oneParamVoid2");
        RolfLectionUtil.getMethodAndInvokeDirectly("oneParamVoid2", test, Arrays.asList(2));
    
        Inherit.print(RolfLectionUtil.getMethodAndInvokeDirectly("twoParamReturnValue1", test, 10, 20));
        Inherit.print(RolfLectionUtil.getMethodAndInvokeDirectly("twoParamReturnValue2", test, 11, 22));
    
        Inherit.print("OK: twoParamVoid1");
        RolfLectionUtil.getMethodAndInvokeDirectly("twoParamVoid1", test, "k1", "v1");
    
        Inherit.print("OK: twoParamVoid2");
        RolfLectionUtil.getMethodAndInvokeDirectly("twoParamVoid2", test, "k2", "v2");
    
        Inherit.print(RolfLectionUtil.getMethodAndInvokeDirectly("noParamReturnValue1", test));
        Inherit.print(RolfLectionUtil.getMethodAndInvokeDirectly("noParamReturnValue2", test));
    
        Inherit.print("OK: noParamVoid1");
        RolfLectionUtil.getMethodAndInvokeDirectly("noParamVoid1", test);
    
        Inherit.print("OK: noParamVoid2");
        RolfLectionUtil.getMethodAndInvokeDirectly("noParamVoid2", test);
    
        Inherit.print(RolfLectionUtil.getMethodAndInvokeDirectly("interfaceOneParamReturnValue", test, "abc"));
        Inherit.print("OK: interfaceOneParamVoid");
        RolfLectionUtil.getMethodAndInvokeDirectly("interfaceOneParamVoid", test, 2);
    
        Inherit.print(RolfLectionUtil.getMethodAndInvokeDirectly("interfaceTwoParamReturnValue", test, 2, 2));
        Inherit.print("OK: interfaceTwoParamVoid");
        RolfLectionUtil.getMethodAndInvokeDirectly("interfaceTwoParamVoid", test, "K", new Object());
    
        Inherit.print(RolfLectionUtil.getMethodAndInvokeDirectly("interfaceNoParamReturnValue", test));
        RolfLectionUtil.getMethodAndInvokeDirectly("interfaceNoParamVoid", test);
    }
}