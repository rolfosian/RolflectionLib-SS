package rolflectionlib.util;

import org.apache.log4j.Logger;
import org.lwjgl.opengl.GL11;

import com.fs.graphics.Sprite;
import com.fs.graphics.TextureLoader;

import java.util.*;

public class TexReflection {
    public static Class<?>[] obfCommonClasses = ObfuscatedClasses.getAllObfClasses("fs.common_obf.jar").toArray(new Class<?>[0]);

    public static Object spriteTextureField;
    public static Object spriteTextureIdField;

    public static Object texClassCtor;
    public static Object texObjectIdField;
    public static Object texObjectGLBindField;
    public static Object texObjectBindMethod;
    public static Class<?> texClass;

    public static Map<String, Object> texObjectMap;

    public static Class<?> shipClass;

    static {
        try {
            for (Object field : Sprite.class.getDeclaredFields()) {
                switch((String)RolfLectionUtil.getFieldNameHandle.invoke(field)) {
                    case "texture":
                        spriteTextureField = field;
                        RolfLectionUtil.setFieldAccessibleHandle.invoke(field, true);

                        for (Object ctor : ((Class<?>)RolfLectionUtil.getFieldTypeHandle.invoke(field)).getConstructors()) {
                            if (((Class[])RolfLectionUtil.getConstructorParameterTypesHandle.invoke(ctor)).length == 2) {
                                texClassCtor = ctor;
                                break;
                            }
                        }
                        break;
                    
                    case "textureId":
                        spriteTextureIdField = field;
                        RolfLectionUtil.setFieldAccessibleHandle.invoke(field, true);
                        break;
                    
                    default:
                        break;
                }
            }

            Object textObj = instantiateTexObj(69, 420);
            texClass = textObj.getClass();
            for (Object field : textObj.getClass().getDeclaredFields()) {
                if (RolfLectionUtil.getFieldTypeHandle.invoke(field).equals(int.class)) {
                    RolfLectionUtil.setFieldAccessibleHandle.invoke(field, true);
                    int value = (int) RolfLectionUtil.getFieldHandle.invoke(field, textObj);

                    if (value == 420) {
                        texObjectIdField = field;
                    } else if (value == 69) {
                        texObjectGLBindField = field;
                    }
                }
            }

            for (Class<?> cls : obfCommonClasses) {
                Object[] fields = cls.getDeclaredFields();
                if (!(fields.length == 4)) continue;
    
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
                    break;
                }
            }

        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static void setTexId(Object texWrapper, int id) {
        try {
            RolfLectionUtil.setFieldHandle.invoke(texObjectIdField, texWrapper, id);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static int getTexId(Object texWrapper) {
        try {
            return (int) RolfLectionUtil.getFieldHandle.invoke(texObjectIdField, texWrapper);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static int[] getTexIds(Object[] texWrappers) {
        try {
            int[] result = new int[texWrappers.length];
            for (int i = 0; i < texWrappers.length; i++) {
                result[i] = (int) RolfLectionUtil.getFieldHandle.invoke(texObjectIdField, texWrappers[i]);
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
            return (int) RolfLectionUtil.getFieldHandle.invoke(texObjectIdField, texObjectMap.get(texPath));
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static int[] getTexIds(String[] texPaths) {
        try {
            int[] result = new int[texPaths.length];

            for (int i = 0; i < texPaths.length; i++) {
                result[i] = (int) RolfLectionUtil.getFieldHandle.invoke(texObjectIdField, texObjectMap.get(texPaths[i]));
            }

            return result;

        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static Object instantiateTexObj(int glBindType, int texId) {
        try {
            return RolfLectionUtil.constructorNewInstanceHandle.invoke(texClassCtor, glBindType, texId);
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
                    if (getBoundTexture() == 42069) {
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

    public static int getBoundTexture() {
        return GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
    }

    public static void init() {}
}
