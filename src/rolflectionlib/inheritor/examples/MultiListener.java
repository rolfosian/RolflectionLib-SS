package rolflectionlib.inheritor.examples;

import com.fs.starfarer.api.Global;

import rolflectionlib.inheritor.Inherit;
import rolflectionlib.inheritor.InterfaceMethodHook;
import rolflectionlib.inheritor.MethodData;
import rolflectionlib.inheritor.MethodHook;

import rolflectionlib.util.ClassRefs;
import rolflectionlib.util.RolfLectionUtil;

/**
 * Combines the dialogDismissed and actionPerformed listener interfaces into one subclass.
 */
public abstract class MultiListener {
    private static final Object exampleSubClassConstructor;

    private final Object instance;

    static {
        Class<?> exampleSubClass = Inherit.implementInterface(
            "rolflectionlib/inheritor/example/MultiListener", // our inheritor internal name (Cannot be the same name as an existing class that has been loaded by the default Classloader)
            new Class<?>[] {ClassRefs.dialogDismissedInterface, ClassRefs.actionListenerInterface}, // The interfaces we are implementing

            new MethodData[] {
                new MethodData(ClassRefs.dialogDismissedInterfaceMethod), // dialogDismissedListener "dialogDismissed" method object
                new MethodData(ClassRefs.buttonListenerActionPerformedMethod) // actionListenerInterface "actionPerformed" method object
            },

            Global.getSettings().getModManager().getModSpec("rolflection_lib").getPath() 
            + "/src/rolflectionlib/inheritor/examples/MultiListenerDump.class"
        );

        exampleSubClassConstructor = exampleSubClass.getConstructors()[0];
    }

    public MultiListener() {
        MethodHook dialogDismissed = new InterfaceMethodHook() {
            @Override
            public Object runInterface(Object... params) {
                dialogDismissed((Object)params[0], (int)params[1]);
                return null; // return null as "dialogDismissed" is a void method
            }
        };

        MethodHook actionPerformed = new InterfaceMethodHook() {
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
