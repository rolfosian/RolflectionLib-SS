package rolflectionlib.inheritor.examples;

import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.characters.FullName.Gender;
import com.fs.starfarer.rpg.Person;

import rolflectionlib.inheritor.Inherit;
import rolflectionlib.inheritor.MethodData;

import rolflectionlib.inheritor.OverrideMethodHook;
import rolflectionlib.inheritor.SuperClassMethodHook;

import rolflectionlib.util.RolfLectionUtil;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.characters.FullName;

public class Rolfos {
    private static final FullName ROFLOS = new FullName("Rolfos", "", Gender.MALE);
    private static final Object ctor;

    static {
        try {
            // generate subclass
            Class<?> subClass = Inherit.extendClass(
                Person.class, // super class
                null, // interfaces to implement
                Person.class.getConstructor(), // super class constructor to use
                "rolflectionlib/inheritor/examples/Roflos", // internal name of resulting subclass
                new MethodData[] { // MethodData array - this order must be the same as the order of the hooks passed to the constructor
                    new MethodData(RolfLectionUtil.getMethod("getName", Person.class), false),
                    new MethodData(RolfLectionUtil.getMethod("wantsToContactPlayer", Person.class), true)
                }
            );

            ctor = subClass.getConstructors()[0];

        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private final PersonAPI roflos;

    public Rolfos() {

        OverrideMethodHook getNameHook = new OverrideMethodHook() {
            @Override
            public Object runFullOverride(Object... params) {
                return ROFLOS;
            }
        };

        SuperClassMethodHook wantsToContactPlayerHook = new SuperClassMethodHook() {
            @Override
            public Object[] runBefore(Object... methodParams) {
                return null;
            }

            @Override
            public Object runAfter(Object returnValue) {
                return true;
            }
        };

        this.roflos = (PersonAPI) RolfLectionUtil.instantiateClass(ctor, getNameHook, wantsToContactPlayerHook);
    }

    public PersonAPI getRoflos() {
        return this.roflos;
    }

    public static void test() {
        PersonAPI roflos = new Rolfos().getRoflos();

        Inherit.print("Hooked name:", roflos.getName().getFullName());
        Inherit.print("Hooked wantsToContactPlayer:", roflos.wantsToContactPlayer());

        FullName actualFullName = (FullName) RolfLectionUtil.getPrivateVariable("name", roflos);
        Inherit.print("Unhooked name:", actualFullName.getFullName());
        
        int wantsToContactReasonsCount = (int) RolfLectionUtil.getPrivateVariable("wantsToContactReasonsCount", roflos);
        Inherit.print("Unhooked wantsToContactPlayer:", wantsToContactReasonsCount > 0);
    }
}
