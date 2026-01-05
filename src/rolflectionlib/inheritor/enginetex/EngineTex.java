package rolflectionlib.inheritor.enginetex;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.VarHandle;
import java.util.*;

import org.apache.log4j.Logger;
import org.lwjgl.opengl.GL11;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.ShipAPI;

import rolflectionlib.util.ClassRefs;
import rolflectionlib.util.RolFileUtil;
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

    private static String[] getTexFieldNames(Class<?> engineGlowClass) {
        ClassReader cr = new ClassReader(RolFileUtil.getClassBytes(engineGlowClass));
        final String[] names = {null, null, null};
        final String texClassDesc = Type.getDescriptor(TexReflection.texClass);

        cr.accept(new ClassVisitor(Opcodes.ASM9) {
            private int putfields = 0;

            @Override
            public MethodVisitor visitMethod(int access, String name, String desc, String sig, String[] ex) {
                if (!name.equals("<init>")) return null;

                return new MethodVisitor(Opcodes.ASM9) {
                    @Override
                    public void visitFieldInsn(int opcode, String owner, String fld, String fldDesc) {
                        if (opcode == Opcodes.PUTFIELD && fldDesc.equals(texClassDesc)) {
                            names[putfields++] = fld;
                        }
                    }
                };
            }
        }, 0);

        return names;
    }

    public static final Class<?> ourTexClassInheritor;
    public static final MethodHandle engineTexClassCtorOne;
    public static final MethodHandle engineTexClassCtorThree;
    public static final MethodHandle engineTexClassCtorTwo;
    public static final MethodHandle engineTexClassCtorFour;

    private static Class<?> engineControllerClass;
    private static MethodHandle getEngineControllerHandle;
    private static MethodHandle getEngineGlowHandle;
    private static MethodHandle engineControllerGetEnginesHandle;
    
    private static Class<?> engineClass;

    private static Class<?> engineGlowClass;
    private static final VarHandle engineGlowTexVarHandle;
    private static final VarHandle engineGlowSmoothTexVarHandle;
    private static final VarHandle engineGlowFlameTexVarHandle;
    private static final VarHandle engineGlowContrailMapVarHandle;

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

            String[] texFieldNames = getTexFieldNames(engineGlowClass);
            MethodHandles.Lookup privateLookup = MethodHandles.privateLookupIn(engineGlowClass, lookup);

            engineGlowTexVarHandle = privateLookup.findVarHandle(
                engineGlowClass, 
                texFieldNames[0], 
                TexReflection.texClass
            );
            engineGlowSmoothTexVarHandle = privateLookup.findVarHandle(
                engineGlowClass, 
                texFieldNames[1], 
                TexReflection.texClass
            );
            engineGlowFlameTexVarHandle = privateLookup.findVarHandle(
                engineGlowClass, 
                texFieldNames[2], 
                TexReflection.texClass
            );

            String contrailMapFieldName = null;
            for (Object field : engineGlowClass.getDeclaredFields()) {
                Class<?> fieldType = (Class<?>) RolfLectionUtil.getFieldTypeHandle.invoke(field);
                
                if (fieldType.equals(Map.class)) {
                    String genericType = String.valueOf(RolfLectionUtil.getGenericTypeHandle.invoke(field));

                    if (genericType.contains("ContrailEmitter")) {
                        contrailMapFieldName = RolfLectionUtil.getFieldName(field);
                    }
                }
            }

            engineGlowContrailMapVarHandle = privateLookup.findVarHandle(
                engineGlowClass, 
                contrailMapFieldName, 
                Map.class
            );

            String texBindMethodName = (String) RolfLectionUtil.getMethodNameHandle.invoke(TexReflection.getTexBindMethod());
            ourTexClassInheritor = EngineTexASM.buildEngineTexClass(EngineTex.class.getClassLoader(), TexReflection.texClass, engineClass, texBindMethodName);

            engineTexClassCtorOne = lookup.findConstructor(ourTexClassInheritor, MethodType.methodType(void.class, int.class, int.class, int[].class, List.class));
            engineTexClassCtorThree = lookup.findConstructor(ourTexClassInheritor, MethodType.methodType(void.class, int.class, int.class, int[].class, List.class, EngineTexDelegate.class));

            engineTexClassCtorTwo = lookup.findConstructor(ourTexClassInheritor, MethodType.methodType(void.class, int.class, int.class, String[].class, List.class));
            engineTexClassCtorFour = lookup.findConstructor(ourTexClassInheritor, MethodType.methodType(void.class, int.class, int.class, String[].class, List.class, EngineTexDelegate.class));

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
            delegate.setContrailMap((Map<Object, Object>) engineGlowContrailMapVarHandle.get(engineGlow));
            
            EngineTexInterface ourTexInheritor = instantiateEngineTex(delegate.getTexIds(0), engines, delegate);
            delegate.setTexWrapper(ourTexInheritor);

            engineGlowTexVarHandle.set(engineGlow, ourTexInheritor);
            engineGlowSmoothTexVarHandle.set(engineGlow, ourTexInheritor);
            
            return ourTexInheritor;

        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private static EngineTexInterface instantiateEngineTex(int[] intTexIds, List<Object> engines) throws Throwable {
        return (EngineTexInterface) engineTexClassCtorOne.invoke(GL11.GL_TEXTURE_2D, 0, intTexIds, engines);
    }

    public static EngineTexInterface setEngineTextures(ShipAPI shipApi, int[] textures) {
        try {
            Object engineController = getEngineControllerHandle.invoke(shipApi);
            Object engineGlow = getEngineGlowHandle.invoke(engineController);
            
            List<Object> engines = (List<Object>) engineControllerGetEnginesHandle.invoke(engineController);
            
            EngineTexInterface ourTexInheritor = instantiateEngineTex(textures, engines);
            
            engineGlowTexVarHandle.set(engineGlow, ourTexInheritor);
            engineGlowSmoothTexVarHandle.set(engineGlow, ourTexInheritor);
            
            return ourTexInheritor;

        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private static EngineTexInterface instantiateEngineTex(int[] intTexIds, List<Object> engines, EngineTexDelegate delegate) throws Throwable {
        return (EngineTexInterface) engineTexClassCtorThree.invoke(GL11.GL_TEXTURE_2D, 0, intTexIds, engines, delegate);

    }

    public static EngineTexInterface setEngineTextures(ShipAPI shipApi, int[] textures, EngineTexDelegate delegate) {
        try {

            Object engineController = getEngineControllerHandle.invoke(shipApi);
            Object engineGlow = getEngineGlowHandle.invoke(engineController);
    
            List<Object> engines = (List<Object>) engineControllerGetEnginesHandle.invoke(engineController);
            delegate.setEngines(engines);
            delegate.setContrailMap((Map<Object, Object>) engineGlowContrailMapVarHandle.get(engineGlow));
            
            EngineTexInterface ourTexInheritor = instantiateEngineTex(textures, engines, delegate);
            delegate.setTexWrapper(ourTexInheritor);
    
            engineGlowTexVarHandle.set(engineGlow, ourTexInheritor);
            engineGlowSmoothTexVarHandle.set(engineGlow, ourTexInheritor);

            return ourTexInheritor;

        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private static EngineTexInterface instantiateEngineTex(String[] stringTexIds, List<Object> engines) throws Throwable {
        return (EngineTexInterface) engineTexClassCtorTwo.invoke(GL11.GL_TEXTURE_2D, 0, stringTexIds, engines);
    }

    public static EngineTexInterface setEngineTextures(ShipAPI shipApi, String[] textures) {
        try {

            Object engineController = getEngineControllerHandle.invoke(shipApi);
            Object engineGlow = getEngineGlowHandle.invoke(engineController);
    
            List<Object> engines = (List<Object>) engineControllerGetEnginesHandle.invoke(engineController);
            
            EngineTexInterface ourTexInheritor = instantiateEngineTex(textures, engines);
    
            engineGlowTexVarHandle.set(engineGlow, ourTexInheritor);
            engineGlowSmoothTexVarHandle.set(engineGlow, ourTexInheritor);

            return ourTexInheritor;

        } catch (Throwable e) {
            throw new RuntimeException(e);
        }

    }

    private static EngineTexInterface instantiateEngineTex(String[] stringTexIds, List<Object> engines, EngineTexDelegate delegate) throws Throwable {
        return (EngineTexInterface) engineTexClassCtorFour.invoke(GL11.GL_TEXTURE_2D, 0, stringTexIds, engines, delegate);
    }

    public static EngineTexInterface setEngineTextures(ShipAPI shipApi, String[] textures, EngineTexDelegate delegate) {
        try {

            Object engineController = getEngineControllerHandle.invoke(shipApi);
            Object engineGlow = getEngineGlowHandle.invoke(engineController);
    
            List<Object> engines = (List<Object>) engineControllerGetEnginesHandle.invoke(engineController);
            delegate.setEngines(engines);
            delegate.setContrailMap((Map<Object, Object>) engineGlowContrailMapVarHandle.get(engineGlow));

            EngineTexInterface ourTexInheritor = instantiateEngineTex(textures, engines, delegate);
            delegate.setTexWrapper(ourTexInheritor);
    
            engineGlowTexVarHandle.set(engineGlow, ourTexInheritor);
            engineGlowSmoothTexVarHandle.set(engineGlow, ourTexInheritor);

            return ourTexInheritor;

        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static void init() {}
}
