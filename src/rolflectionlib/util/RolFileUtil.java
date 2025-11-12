package rolflectionlib.util;

import org.apache.log4j.Logger;

public class RolFileUtil {
    public static final Logger logger = Logger.getLogger(RolFileUtil.class);
    public static void print(Object... args) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            sb.append(args[i] instanceof String ? (String) args[i] : String.valueOf(args[i]));
            if (i < args.length - 1) sb.append(' ');
        }
        logger.info(sb.toString());
    }

    private static Object faosCtor;
    private static final Object faosWriteMethod;

    private static final Object fileCtor;
    private static final Object getParentFileMethod;
    private static final Object fileExistsMethod;
    private static final Object mkdirsMethod;
    private static final Object faosFlushMethod;
    private static final Object faosCloseMethod;

    static {
        try {
            Class<?> faosClass = Class.forName("java.io.FileOutputStream", false, Class.class.getClassLoader());
            Class<?> fileClass = Class.forName("java.io.File", false, Class.class.getClassLoader());

            fileCtor = fileClass.getConstructor(String.class);
            getParentFileMethod = RolfLectionUtil.getMethod("getParentFile", fileClass, 0);
            fileExistsMethod = RolfLectionUtil.getMethod("exists", fileClass, 0);
            mkdirsMethod = RolfLectionUtil.getMethod("mkdirs", fileClass, 0);

            Object[] faosCtors = faosClass.getDeclaredConstructors();
            for (Object ctor : faosCtors) {
                Class<?>[] paramTypes = (Class<?>[]) RolfLectionUtil.getConstructorParameterTypesHandle.invoke(ctor);
                if ((paramTypes.length == 1 && paramTypes[0].equals(fileClass))) {
                    faosCtor = ctor;
                    break;
                }
            }
            faosWriteMethod = RolfLectionUtil.getMethodExplicit("write", faosClass, new Class<?>[]{byte[].class});
            faosFlushMethod = RolfLectionUtil.getMethod("flush", faosClass, 0);
            faosCloseMethod = RolfLectionUtil.getMethod("close", faosClass, 0);

        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static void writeToFile(String path, byte[] data) {
        Object file = RolfLectionUtil.instantiateClass(fileCtor, path);
        Object parent = RolfLectionUtil.invokeMethodDirectly(getParentFileMethod, file);

        if (parent != null && !((boolean) RolfLectionUtil.invokeMethodDirectly(fileExistsMethod, parent))) {
            RolfLectionUtil.invokeMethodDirectly(mkdirsMethod, parent);
        }

        Object faos = RolfLectionUtil.instantiateClass(faosCtor, file);
        RolfLectionUtil.invokeMethodDirectly(faosWriteMethod, faos, data);
        RolfLectionUtil.invokeMethodDirectly(faosFlushMethod, faos);
        RolfLectionUtil.invokeMethodDirectly(faosCloseMethod, faos);
    }
}
