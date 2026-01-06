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
import com.fs.starfarer.combat.entities.Ship;

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
    public static final MethodHandle engineTexClassCtorTwo;

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

            for (Object method : Ship.class.getDeclaredMethods()) {
                String methodName = (String) RolfLectionUtil.getMethodNameHandle.invoke(method);

                if (methodName.equals("getEngineController")) {
                    engineControllerClass = (Class<?>) RolfLectionUtil.getReturnTypeHandle.invoke(method);
                    if (engineControllerClass.isInterface()) continue;
                    
                    MethodType methodType = MethodType.methodType(engineControllerClass);
                    getEngineControllerHandle = lookup.findVirtual(Ship.class, "getEngineController", methodType);
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
            engineTexClassCtorTwo = lookup.findConstructor(ourTexClassInheritor, MethodType.methodType(void.class, int.class, int.class, String[].class, List.class));

        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Applies one or more {@link EngineTexDelegate} instances to a ship's engines.
     * Each delegate determines which texture type(s) it overrides; processing stops early if a delegate handles all.
     *
     * @param shipApi ship whose engine textures are modified
     * @param delegates delegates describing texture sources and target types
     */
    public static void setEngineTextures(ShipAPI shipApi, EngineTexDelegate... delegates) {
        try {
            Object engineController = shipApi.getEngineController();
            Object engineGlow = getEngineGlowHandle.invoke(engineController);
            List<Object> engines = (List<Object>) engineControllerGetEnginesHandle.invoke(engineController);

            for (EngineTexDelegate delegate : delegates) {
                delegate.setEngines(engines);
                delegate.setContrailMap((Map<Object, Object>) engineGlowContrailMapVarHandle.get(engineGlow));

                EngineTexInterface ourTexInheritor = instantiateEngineTex(delegate.getTexIds(0), engines);
                ourTexInheritor.setDelegate(delegate);
                delegate.setTexWrapper(ourTexInheritor);

                switch(delegate.getType()) {
                    case GLOW:
                        engineGlowTexVarHandle.set(engineGlow, ourTexInheritor);
                        continue;
                    case SMOOTH_GLOW:
                        engineGlowSmoothTexVarHandle.set(engineGlow, ourTexInheritor);
                        continue;
                    case GLOW_AND_SMOOTH_GLOW:
                        engineGlowTexVarHandle.set(engineGlow, ourTexInheritor);
                        engineGlowSmoothTexVarHandle.set(engineGlow, ourTexInheritor);
                        continue;
                    case FLAME:
                        engineGlowFlameTexVarHandle.set(engineGlow, ourTexInheritor);
                        continue;
                    case ALL:
                        engineGlowTexVarHandle.set(engineGlow, ourTexInheritor);
                        engineGlowSmoothTexVarHandle.set(engineGlow, ourTexInheritor);
                        engineGlowFlameTexVarHandle.set(engineGlow, ourTexInheritor);
                        return;
                }
            }
            return;

        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Sets all engine glow textures (flame, glow, smooth glow) to delegate's singular texture order. This means that all three will use the same texture.
     * @param shipApi Ship
     * @param delegate EngineTexDelegate
     */
    public static void setEngineTextures(ShipAPI shipApi, EngineTexDelegate delegate) {
        try {
            Object engineController = getEngineControllerHandle.invoke(shipApi);
            Object engineGlow = getEngineGlowHandle.invoke(engineController);
    
            List<Object> engines = (List<Object>) engineControllerGetEnginesHandle.invoke(engineController);

            delegate.setEngines(engines);
            delegate.setContrailMap((Map<Object, Object>) engineGlowContrailMapVarHandle.get(engineGlow));

            EngineTexInterface ourTexInheritor = instantiateEngineTex(delegate.getTexIds(0), engines);
            ourTexInheritor.setDelegate(delegate);
            delegate.setTexWrapper(ourTexInheritor);

            engineGlowTexVarHandle.set(engineGlow, ourTexInheritor);
            engineGlowSmoothTexVarHandle.set(engineGlow, ourTexInheritor);
            engineGlowFlameTexVarHandle.set(engineGlow, ourTexInheritor);
            
            return;

        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Sets engine textures individually using integer texture ids.
     * Any {@code null} array leaves that texture type untouched and the returned entry will be {@code null}.
     *
     * @param shipApi Ship whose engines are updated
     * @param glowTextures texture ids for standard glow frames, or {@code null} to keep the current glow texture
     * @param smoothGlowTextures texture ids for smooth glow frames, or {@code null} to keep the current smooth glow texture
     * @param flameTextures texture ids for flame animation frames, or {@code null} to keep the current flame texture
     * @return array of wrappers for glow, smooth glow, and flame textures (entries are {@code null} for any texture left untouched)
     */
    public static EngineTexInterface[] setEngineTextures(ShipAPI shipApi, int[] glowTextures, int[] smoothGlowTextures, int[] flameTextures) {
        try {
            Object engineController = getEngineControllerHandle.invoke(shipApi);
            Object engineGlow = getEngineGlowHandle.invoke(engineController);
            
            List<Object> engines = (List<Object>) engineControllerGetEnginesHandle.invoke(engineController);
            
            EngineTexInterface glowTexture = null;
            EngineTexInterface smoothGlowTexture = null;
            EngineTexInterface flameTexture = null;

            if (glowTextures != null) {
                glowTexture = instantiateEngineTex(glowTextures, engines);
                engineGlowTexVarHandle.set(engineGlow, glowTexture);
            }
            if (smoothGlowTextures != null) {
                smoothGlowTexture = instantiateEngineTex(smoothGlowTextures, engines);
                engineGlowSmoothTexVarHandle.set(engineGlow, smoothGlowTexture);
            }
            if (flameTextures != null) {
                flameTexture = instantiateEngineTex(flameTextures, engines);
                engineGlowFlameTexVarHandle.set(engineGlow, flameTexture);
            }
            
            return new EngineTexInterface[] {glowTexture, smoothGlowTexture, flameTexture};

        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private static EngineTexInterface instantiateEngineTex(int[] intTexIds, List<Object> engines) throws Throwable {
        return (EngineTexInterface) engineTexClassCtorOne.invoke(GL11.GL_TEXTURE_2D, 0, intTexIds, engines);
    }

    /**
     * Sets engine textures individually using string texture ids.
     * Any {@code null} array leaves that texture type untouched and the returned entry will be {@code null}.
     *
     * @param shipApi Ship whose engines are updated
     * @param glowTextures texture ids for standard glow frames, or {@code null} to keep the current glow texture
     * @param smoothGlowTextures texture ids for smooth glow frames, or {@code null} to keep the current smooth glow texture
     * @param flameTextures texture ids for flame animation frames, or {@code null} to keep the current flame texture
     * @return array of wrappers for glow, smooth glow, and flame textures (entries are {@code null} for any texture left untouched)
     */
    public static EngineTexInterface[] setEngineTextures(ShipAPI shipApi, String[] glowTextures, String[] smoothGlowTextures, String[] flameTextures) {
        try {

            Object engineController = getEngineControllerHandle.invoke(shipApi);
            Object engineGlow = getEngineGlowHandle.invoke(engineController);
    
            List<Object> engines = (List<Object>) engineControllerGetEnginesHandle.invoke(engineController);
            
            EngineTexInterface glowTexture = null;
            EngineTexInterface smoothGlowTexture = null;
            EngineTexInterface flameTexture = null;

            if (glowTextures != null) {
                glowTexture = instantiateEngineTex(glowTextures, engines);
                engineGlowTexVarHandle.set(engineGlow, glowTexture);
            }
            if (smoothGlowTextures != null) {
                smoothGlowTexture = instantiateEngineTex(smoothGlowTextures, engines);
                engineGlowSmoothTexVarHandle.set(engineGlow, smoothGlowTexture);
            }
            if (flameTextures != null) {
                flameTexture = instantiateEngineTex(flameTextures, engines);
                engineGlowFlameTexVarHandle.set(engineGlow, flameTexture);
            }
            
            return new EngineTexInterface[] {glowTexture, smoothGlowTexture, flameTexture};

        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private static EngineTexInterface instantiateEngineTex(String[] stringTexIds, List<Object> engines) throws Throwable {
        return (EngineTexInterface) engineTexClassCtorTwo.invoke(GL11.GL_TEXTURE_2D, 0, stringTexIds, engines);
    }

    public static void init() {}
}
