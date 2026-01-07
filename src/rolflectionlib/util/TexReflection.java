package rolflectionlib.util;

import org.apache.log4j.Logger;
import org.lwjgl.opengl.GL11;

import com.fs.graphics.Sprite;
import com.fs.graphics.TextureLoader;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.MethodType;
import java.lang.invoke.VarHandle;
import java.util.*;

public class TexReflection {
    public static Class<?>[] obfCommonClasses = ObfuscatedClasses.getAllObfClasses("fs.common_obf.jar").toArray(new Class<?>[0]);

    public static VarHandle spriteTextureVarHandle;
    public static VarHandle spriteTextureIdVarHandle;

    public static MethodHandle texClassCtorHandle;
    public static VarHandle texObjectIdVarHandle;
    public static VarHandle texObjectGLBindVarHandle;
    public static Object texObjectBindMethod;
    public static Class<?> texClass;

    public static Map<String, Object> texObjectMap;
    public static Map<String, Integer> texIdMap = new HashMap<>();

    static {
        try {
            Lookup lookup = MethodHandles.lookup();
            Lookup privateLookup = MethodHandles.privateLookupIn(Sprite.class, lookup);

            outer:
            for (Object field : Sprite.class.getDeclaredFields()) {
                String fieldName = RolfLectionUtil.getFieldName(field);

                if (fieldName.equals("texture")) {
                    texClass = RolfLectionUtil.getFieldType(field);

                    for (Object ctor : texClass.getConstructors()) {
                        Class<?>[] paramTypes = (Class[]) RolfLectionUtil.getConstructorParameterTypesHandle.invoke(ctor);
                        if (paramTypes.length == 2) {
                            texClassCtorHandle = lookup.findConstructor(texClass, MethodType.methodType(void.class, paramTypes[0], paramTypes[1]));
                            break outer;
                        }
                    }
                }
            }

            spriteTextureVarHandle = privateLookup.findVarHandle(
                Sprite.class,
                "texture",
                texClass
            );
            spriteTextureIdVarHandle = privateLookup.findVarHandle(
                Sprite.class,
                "textureId",
                String.class
            );

            privateLookup = MethodHandles.privateLookupIn(texClass, lookup);

            Object textObj = instantiateTexObj(69, 420);
            for (Object field : textObj.getClass().getDeclaredFields()) {
                if (RolfLectionUtil.getFieldTypeHandle.invoke(field).equals(int.class)) {
                    RolfLectionUtil.setFieldAccessibleHandle.invoke(field, true);
                    int value = (int) RolfLectionUtil.getFieldHandle.invoke(field, textObj);

                    if (value == 420) {
                        texObjectIdVarHandle = privateLookup.findVarHandle(
                            texClass,
                            RolfLectionUtil.getFieldName(field),
                            int.class
                        );
                        // texObjectIdField = field;


                    } else if (value == 69) {
                        texObjectGLBindVarHandle = privateLookup.findVarHandle(
                            texClass,
                            RolfLectionUtil.getFieldName(field),
                            int.class
                        );
                        // texObjectGLBindField = field;
                    }
                }
            }

            for (Class<?> cls : obfCommonClasses) {
                Object[] fields = cls.getDeclaredFields();
                if (fields.length != 4) continue;
    
                boolean booleanMatch = false;
                boolean mapMatch = false;
                boolean loggerMatch = false;
                boolean textureLoaderMatch = false;
    
                Object mapField = null;
                for (Object field : fields) {

                    Class<?> fieldType = (Class<?>) RolfLectionUtil.getFieldTypeHandle.invoke(field);
                    if (fieldType.equals(boolean.class)) {
                        booleanMatch = true;

                    } else if (fieldType.equals(Map.class)) {
                        mapMatch = true;
                        mapField = field;

                    } else if (fieldType.equals(Logger.class)) {
                        loggerMatch = true;

                    } else if (fieldType.equals(TextureLoader.class)) {
                        textureLoaderMatch = true;
                    }
                }
    
                if (booleanMatch && mapMatch && loggerMatch && textureLoaderMatch) {
                    RolfLectionUtil.setFieldAccessibleHandle.invoke(mapField, true);
                    texObjectMap = (Map<String, Object>) RolfLectionUtil.getFieldHandle.invoke(mapField, null);

                    for (Map.Entry<String, Object> entry : texObjectMap.entrySet()) texIdMap.put(entry.getKey(), getTexId(entry.getValue()));

                    break;
                }
            }

        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static void setTexId(Object texWrapper, int id) {
        try {
            texObjectIdVarHandle.set(texWrapper, id);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static int getTexId(Object texWrapper) {
        try {
            return (int) texObjectIdVarHandle.get(texWrapper);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static int[] getTexIds(Object[] texWrappers) {
        try {
            int[] result = new int[texWrappers.length];
            for (int i = 0; i < texWrappers.length; i++) {
                result[i] = (int) texObjectIdVarHandle.get(texWrappers[i]);
            }

            return result;

        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static int[][] getTexOrder(String[][] order) {
        int len = order.length;
        int[][] result = new int[len][];

        for (int i = 0; i < len; i++) {
            result[i] = getTexIds(order[i]);
        }
        return result;
    }

    public static int getTexId(String texPath) {
        try {
            return (int) texObjectIdVarHandle.get(texObjectMap.get(texPath));
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static int[] getTexIds(String[] texPaths) {
        try {
            int[] result = new int[texPaths.length];

            for (int i = 0; i < texPaths.length; i++) {
                result[i] = texIdMap.get(texPaths[i]);
            }

            return result;

        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static Object instantiateTexObj(int glBindType, int texId) {
        try {
            return texClassCtorHandle.invoke(glBindType, texId);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static Object getTexBindMethod() {
        if (texObjectBindMethod != null) return texObjectBindMethod;
        Object texWrapper = instantiateTexObj(GL11.GL_TEXTURE_2D, 42069);

        try {

            for (Object method : texWrapper.getClass().getDeclaredMethods()) {
                Class<?> returnType = (Class<?>) RolfLectionUtil.getReturnTypeHandle.invoke(method);
                Class<?>[] paramTypes = (Class<?>[]) RolfLectionUtil.getParameterTypesHandle.invoke(method);

                if ((returnType.equals(void.class)) && paramTypes.length == 0 && RolfLectionUtil.isPublic((int)RolfLectionUtil.getModifiersHandle.invoke(method))) {
                    RolfLectionUtil.invokeMethodHandle.invoke(method, texWrapper);

                    if (GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D) == 42069) {
                        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
                        
                        texObjectBindMethod = method;
                        return method;
                    }
                }
            }
            return null;

        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static void init() {}
}
