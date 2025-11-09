package rolflectionlib.inheritor.enginetex;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

import java.util.*;

import org.apache.log4j.Logger;
import org.lwjgl.opengl.GL11;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.ShipAPI;

import rolflectionlib.util.ClassRefs;
import rolflectionlib.util.RolfLectionUtil;
import rolflectionlib.util.TexReflection;

public class EngineTex {
    private static final Logger logger = Global.getLogger(EngineTex.class);
    public static void print(Object... args) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            sb.append(args[i] instanceof String ? (String) args[i] : String.valueOf(args[i]));
            if (i < args.length - 1) sb.append(' ');
        }
        logger.info(sb.toString());
    }

    public static final Class<?> ourTexClassInheritor;
    public static final Object engineTexClassCtorOne;
    public static final Object engineTexClassCtorThree;
    public static final Object engineTexClassCtorTwo;
    public static final Object engineTexClassCtorFour;

    private static Class<?> engineControllerClass;
    private static MethodHandle getEngineControllerHandle;
    private static MethodHandle getEngineGlowHandle;
    private static MethodHandle engineControllerGetEnginesHandle;
    
    private static Class<?> engineClass;

    private static Class<?> engineGlowClass;
    private static Object engineGlowTexField;
    private static Object engineGlowTexFieldSmall;

    static {
        try {

            MethodHandles.Lookup lookup = MethodHandles.lookup();

            for (Object method : ClassRefs.shipClass.getDeclaredMethods()) {
                String methodName = (String) RolfLectionUtil.getMethodNameHandle.invoke(method);

                if (methodName.equals("getEngineController")) {
                    engineControllerClass = (Class<?>) RolfLectionUtil.getReturnTypeHandle.invoke(method);
                    if (engineControllerClass.isInterface()) continue;
                    
                    MethodType methodType = MethodType.methodType(engineControllerClass);
                    getEngineControllerHandle = lookup.findVirtual(ClassRefs.shipClass, "getEngineController", methodType);
                    break;
                }
            }

            for (Object method : engineControllerClass.getDeclaredMethods()) {
                String methodName = (String) RolfLectionUtil.getMethodNameHandle.invoke(method);

                if (methodName.equals("getEngineGlow")) {
                    engineGlowClass = (Class<?>) RolfLectionUtil.getReturnTypeHandle.invoke(method);

                    MethodType methodType = MethodType.methodType(engineGlowClass);
                    getEngineGlowHandle = lookup.findVirtual(engineControllerClass, "getEngineGlow", methodType);

                } else if (methodName.equals("getEngines")) {
                    engineControllerGetEnginesHandle = lookup.findVirtual(engineControllerClass, "getEngines", MethodType.methodType(List.class));

                    String genericReturnType = String.valueOf(RolfLectionUtil.getGenericReturnTypeHandle.invoke(method));
                    int idx1 = genericReturnType.indexOf("<");
                    int idx2 = genericReturnType.indexOf(">");

                    String engineClassName = genericReturnType.substring(idx1 + 1, idx2).trim();
                    engineClass = Class.forName(engineClassName, false, Global.class.getClassLoader());
                }
            }

            for (Object field : engineGlowClass.getDeclaredFields()) {
                Class<?> fieldType = (Class<?>) RolfLectionUtil.getFieldTypeHandle.invoke(field);
                if (fieldType.equals(TexReflection.texClass)) {
                    if (engineGlowTexField == null) {
                        engineGlowTexField = field;
                        RolfLectionUtil.setFieldAccessibleHandle.invoke(engineGlowTexField, true);

                    } else if (engineGlowTexFieldSmall == null) {
                        engineGlowTexFieldSmall = field;
                        RolfLectionUtil.setFieldAccessibleHandle.invoke(engineGlowTexFieldSmall, true);

                    }
                }
            }

            String texBindMethodName = (String) RolfLectionUtil.getMethodNameHandle.invoke(TexReflection.getTexBindMethod());
            ourTexClassInheritor = EngineTexASM.buildEngineTexClass(EngineTex.class.getClassLoader(), TexReflection.texClass, engineClass, texBindMethodName);

            engineTexClassCtorOne = ourTexClassInheritor.getDeclaredConstructor(new Class<?>[]{int.class, int.class, int[].class, List.class});
            engineTexClassCtorThree = ourTexClassInheritor.getDeclaredConstructor(new Class<?>[]{int.class, int.class, int[].class, List.class, EngineTexDelegate.class});

            engineTexClassCtorTwo = ourTexClassInheritor.getDeclaredConstructor(new Class<?>[]{int.class, int.class, String[].class, List.class});
            engineTexClassCtorFour = ourTexClassInheritor.getDeclaredConstructor(new Class<?>[]{int.class, int.class, String[].class, List.class, EngineTexDelegate.class});

        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static EngineTexInterface setEngineTextures(ShipAPI shipApi, EngineTexDelegate delegate) {
        try {

            Object engineController = getEngineControllerHandle.invoke(shipApi);
            Object engineGlow = getEngineGlowHandle.invoke(engineController);
    
            List<Object> engines = (List<Object>) engineControllerGetEnginesHandle.invoke(engineController);
            delegate.setEngines(engines);
            
            EngineTexInterface ourTexInheritor = instantiateEngineTex(delegate.getTexIds(0), engines, delegate);
            delegate.setTexWrapper(ourTexInheritor);
    
            RolfLectionUtil.setFieldHandle.invoke(engineGlowTexField, engineGlow, ourTexInheritor);
            RolfLectionUtil.setFieldHandle.invoke(engineGlowTexFieldSmall, engineGlow, ourTexInheritor);
            
            return ourTexInheritor;

        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private static EngineTexInterface instantiateEngineTex(int[] intTexIds, List<Object> engines) {
        return (EngineTexInterface) RolfLectionUtil.instantiateClass(engineTexClassCtorOne, GL11.GL_TEXTURE_2D, 0, intTexIds, engines);
    }

    public static EngineTexInterface setEngineTextures(ShipAPI shipApi, int[] textures) {
        try {

            Object engineController = getEngineControllerHandle.invoke(shipApi);
            Object engineGlow = getEngineGlowHandle.invoke(engineController);
    
            List<Object> engines = (List<Object>) engineControllerGetEnginesHandle.invoke(engineController);
            
            EngineTexInterface ourTexInheritor = instantiateEngineTex(textures, engines);
    
            RolfLectionUtil.setFieldHandle.invoke(engineGlowTexField, engineGlow, ourTexInheritor);
            RolfLectionUtil.setFieldHandle.invoke(engineGlowTexFieldSmall, engineGlow, ourTexInheritor);
            
            return ourTexInheritor;

        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private static EngineTexInterface instantiateEngineTex(int[] intTexIds, List<Object> engines, EngineTexDelegate delegate) {
        return (EngineTexInterface) RolfLectionUtil.instantiateClass(engineTexClassCtorThree, GL11.GL_TEXTURE_2D, 0, intTexIds, engines, delegate);
    }

    public static EngineTexInterface setEngineTextures(ShipAPI shipApi, int[] textures, EngineTexDelegate delegate) {
        try {

            Object engineController = getEngineControllerHandle.invoke(shipApi);
            Object engineGlow = getEngineGlowHandle.invoke(engineController);
    
            List<Object> engines = (List<Object>) engineControllerGetEnginesHandle.invoke(engineController);
            delegate.setEngines(engines);
            
            EngineTexInterface ourTexInheritor = instantiateEngineTex(textures, engines, delegate);
            delegate.setTexWrapper(ourTexInheritor);
    
            RolfLectionUtil.setFieldHandle.invoke(engineGlowTexField, engineGlow, ourTexInheritor);
            RolfLectionUtil.setFieldHandle.invoke(engineGlowTexFieldSmall, engineGlow, ourTexInheritor);

            return ourTexInheritor;

        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private static EngineTexInterface instantiateEngineTex(String[] stringTexIds, List<Object> engines) {
        return (EngineTexInterface) RolfLectionUtil.instantiateClass(engineTexClassCtorTwo, GL11.GL_TEXTURE_2D, 0, stringTexIds, engines);
    }

    public static EngineTexInterface setEngineTextures(ShipAPI shipApi, String[] textures) {
        try {

            Object engineController = getEngineControllerHandle.invoke(shipApi);
            Object engineGlow = getEngineGlowHandle.invoke(engineController);
    
            List<Object> engines = (List<Object>) engineControllerGetEnginesHandle.invoke(engineController);
            
            EngineTexInterface ourTexInheritor = instantiateEngineTex(textures, engines);
    
            RolfLectionUtil.setFieldHandle.invoke(engineGlowTexField, engineGlow, ourTexInheritor);
            RolfLectionUtil.setFieldHandle.invoke(engineGlowTexFieldSmall, engineGlow, ourTexInheritor);

            return ourTexInheritor;

        } catch (Throwable e) {
            throw new RuntimeException(e);
        }

    }

    private static EngineTexInterface instantiateEngineTex(String[] stringTexIds, List<Object> engines, EngineTexDelegate delegate) {
        return (EngineTexInterface) RolfLectionUtil.instantiateClass(engineTexClassCtorFour, GL11.GL_TEXTURE_2D, 0, stringTexIds, engines, delegate);
    }

    public static EngineTexInterface setEngineTextures(ShipAPI shipApi, String[] textures, EngineTexDelegate delegate) {
        try {

            Object engineController = getEngineControllerHandle.invoke(shipApi);
            Object engineGlow = getEngineGlowHandle.invoke(engineController);
    
            List<Object> engines = (List<Object>) engineControllerGetEnginesHandle.invoke(engineController);
            delegate.setEngines(engines);

            EngineTexInterface ourTexInheritor = instantiateEngineTex(textures, engines, delegate);
            delegate.setTexWrapper(ourTexInheritor);
    
            RolfLectionUtil.setFieldHandle.invoke(engineGlowTexField, engineGlow, ourTexInheritor);
            RolfLectionUtil.setFieldHandle.invoke(engineGlowTexFieldSmall, engineGlow, ourTexInheritor);

            return ourTexInheritor;

        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }



    public static void init() {}
}
