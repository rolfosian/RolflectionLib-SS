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

    private static final Object baosCtor;
    private static final Object baosWriteMethod;
    private static final Object baosFlushMethod;
    private static final Object baosToByteArrayMethod;

    private static final Object inputStreamAvailableMethod;
    private static final Object inputStreamReadMethod;

    private static final Object getResourceAsStreamMethod;

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


            Class<?> baosClass = Class.forName("java.io.ByteArrayOutputStream", false, Class.class.getClassLoader());

            baosCtor = RolfLectionUtil.getConstructor(baosClass, new Class<?>[0]);
            baosWriteMethod = RolfLectionUtil.getMethodExplicit("write", baosClass, new Class<?>[]{byte[].class, int.class, int.class});
            baosFlushMethod = RolfLectionUtil.getMethod("flush", baosClass);
            baosToByteArrayMethod = RolfLectionUtil.getMethod("toByteArray", baosClass);
            
            getResourceAsStreamMethod = RolfLectionUtil.getMethod("getResourceAsStream", ClassLoader.class);
            Class<?> inputStreamClass = RolfLectionUtil.getReturnType(getResourceAsStreamMethod);
            inputStreamAvailableMethod = RolfLectionUtil.getMethod("available", inputStreamClass);
            inputStreamReadMethod = RolfLectionUtil.getMethod("read", inputStreamClass);

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

    public static int computeBufferSize(Object inputStream) {
        try {
            int expectedLength = (int) RolfLectionUtil.invokeMethodDirectly(inputStreamAvailableMethod, inputStream);

            if (expectedLength < 256) {
              return 4096;
            }
            return Math.min(expectedLength, 1024 * 1024);

        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] readStream(Object inputStream) {
        try {
            int bufferSize = computeBufferSize(inputStream);
            Object outputStream = RolfLectionUtil.instantiateClass(baosCtor);

            byte[] data = new byte[bufferSize];
            int bytesRead;
            int readCount = 0;

            while ((bytesRead = (int) RolfLectionUtil.invokeMethodDirectly(inputStreamReadMethod, inputStream, data, 0, bufferSize)) != -1) {
                RolfLectionUtil.invokeMethodDirectly(baosWriteMethod, outputStream, data, 0, bytesRead);
                readCount++;
            }
            
            RolfLectionUtil.invokeMethodDirectly(baosFlushMethod, outputStream);
            if (readCount == 1) {
                return data;
            }
            return (byte[]) RolfLectionUtil.invokeMethodDirectly(baosToByteArrayMethod, outputStream);

        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] getClassBytes(Class<?> cls) {
        return readStream(RolfLectionUtil.invokeMethodDirectly(
                getResourceAsStreamMethod,
                cls.getClassLoader(),
                cls.getCanonicalName().replace(".", "/") + ".class"
            )
        );
    }
}
