package rolflectionlib.inheritor.examples;

import com.fs.starfarer.api.Global;

import rolflectionlib.inheritor.Inherit;
import rolflectionlib.inheritor.InterfaceMethodHook;
import rolflectionlib.inheritor.MethodData;

import rolflectionlib.util.ClassRefs;
import rolflectionlib.util.RolfLectionUtil;

/**
 * Combines the obfuscated dialogDismissed and actionPerformed listener interfaces into one subclass.
 */
public abstract class MultiListener {
    private static final Object exampleSubClassConstructor;

    private final Object instance;

    static {
        Class<?> exampleSubClass = Inherit.implementInterface(
            "rolflectionlib/inheritor/examples/MultiListener", // our inheritor internal name (Cannot be the same name as an existing class that has been loaded by the default Classloader)
            new Class<?>[] {ClassRefs.dialogDismissedInterface, ClassRefs.actionListenerInterface}, // The interfaces we are implementing

            new MethodData[] {
                new MethodData(ClassRefs.dialogDismissedInterfaceMethod, false), // dialogDismissedInterface "dialogDismissed" method object
                new MethodData(ClassRefs.buttonListenerActionPerformedMethod, false) // actionPerformedInterface "actionPerformed" method object
            }
        );

        exampleSubClassConstructor = exampleSubClass.getConstructors()[0];
    }

    public MultiListener() {
        InterfaceMethodHook dialogDismissed = new InterfaceMethodHook() {
            @Override
            public Object runInterface(Object... params) {
                dialogDismissed((Object)params[0], (int)params[1]);
                return null; // return null as "dialogDismissed" is a void method
            }
        };

        InterfaceMethodHook actionPerformed = new InterfaceMethodHook() {
            @Override
            public Object runInterface(Object... params) {
                actionPerformed(params[0], params[1]);
                return null; // return null as "actionPerformed" is a void method
            }
        };

        // note that hook order passed here must be same as the order of the MethodData array given to generate the subclass
        this.instance = RolfLectionUtil.instantiateClass(
            exampleSubClassConstructor,
            dialogDismissed, 
            actionPerformed
        );
    }

    public final Object getInstance() {
        return this.instance;
    }

    public abstract void dialogDismissed(Object dialog, int yesOrNo);

    public abstract void actionPerformed(Object inputEvent, Object uiElement);

    // public void dialogDismissed(Object dialog, int yesOrNo) {
    //     logger.info(String.valueOf(dialog) + " dismissed with value: " + String.valueOf(yesOrNo));
    // }

    // public void actionPerformed(Object inputEvent, Object uiElement) {
    //     logger.info("actionPerformed called with UI element " + String.valueOf(uiElement) + " and input event " + String.valueOf(inputEvent));
    // }

    /**
     * Init function to run to load the class and run the static block to assemble the inheritor class in onApplicationload
     */
    public static final void init() {}
}

// Assembled at runtime class output decompiled:
// // Source code is decompiled from a .class file using FernFlower decompiler (from Intellij IDEA).
// package rolflectionlib.inheritor.examples;

// import com.fs.starfarer.ui.U;
// import com.fs.starfarer.ui.oo0O;
// import rolflectionlib.inheritor.MethodHook;

// public class MultiListener implements oo0O.o, U {
//    private final MethodHook rolfLectionHook0;
//    private final MethodHook rolfLectionHook1;

//    public MultiListener(MethodHook var1, MethodHook var2) {
//       this.rolfLectionHook0 = var1;
//       this.rolfLectionHook1 = var2;
//    }

//    public void dialogDismissed(oo0O var1, int var2) {
//       Object[] var4 = new Object[]{var1, var2};
//       this.rolfLectionHook0.runInterface(var4);
//    }

//    public void actionPerformed(Object var1, Object var2) {
//       Object[] var4 = new Object[]{var1, var2};
//       this.rolfLectionHook1.runInterface(var4);
//    }
// }
