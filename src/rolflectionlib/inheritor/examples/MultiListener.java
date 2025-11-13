package rolflectionlib.inheritor.examples;

import org.apache.log4j.Logger;

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
public class MultiListener {
    private static final Logger logger = Global.getLogger(MultiListener.class);
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
                dialogDismissed(params);
                return null; // return null as "dialogDismissed" is a void method
            }
        };

        MethodHook actionPerformed = new InterfaceMethodHook() {
            @Override
            public Object runInterface(Object... params) {
                actionPerformed(params);
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

    public Object getInstance() {
        return this.instance;
    }

    public void dialogDismissed(Object... args) {
        Object dialog = args[0];
        int yesOrNo = (int) args[1];

        logger.info(String.valueOf(dialog) + " dismissed with value: " + String.valueOf(yesOrNo));
    }

    public void actionPerformed(Object... args) {
        Object uiElement = args[1];
        Object inputEvent = args[0];

        logger.info("actionPerformed called with UI element " + String.valueOf(uiElement) + " and input event " + String.valueOf(inputEvent));
    }

    /**
     * Init function to run to load the class and run the static block to assemble the inheritor class in onApplicationload
     */
    public static final void init() {}
}
