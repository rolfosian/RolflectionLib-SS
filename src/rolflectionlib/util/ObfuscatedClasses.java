package rolflectionlib.util;

import java.util.*;
import java.util.jar.JarFile;
import java.util.jar.JarEntry;

import com.fs.starfarer.api.Global;

public class ObfuscatedClasses {
    protected static final Class<?>[] obfClazzes;
    protected static final Class<?>[] obfInterfaces;
    protected static final Class<?>[] obfEnums;

    static {
        List<Class<?>> obfClasses = getAllObfClasses("starfarer_obf.jar");

        List<Class<?>> enumz = new ArrayList<>();
        List<Class<?>> interfeces = new ArrayList<>();
        List<Class<?>> clses = new ArrayList<>();

        for (Class<?> cls : obfClasses) {
            if (cls.isEnum()) enumz.add(cls);
            else if (cls.isInterface()) interfeces.add(cls);
            else clses.add(cls);
        }

        Class<?>[] clsArr = new Class<?>[0];
        obfClazzes = clses.toArray(clsArr);
        obfInterfaces = interfeces.toArray(clsArr);
        obfEnums = enumz.toArray(clsArr);
    }

    public static Class<?>[] getClasses() {
        return obfClazzes;
    }

    public static Class<?>[] getInterfaces() {
        return obfInterfaces;
    }

    public static Class<?>[] getEnums() {

        return obfEnums;
    }

    public static List<Class<?>> getAllObfClasses(String jarPath) {
        try {
            JarFile jarFile = new JarFile(jarPath);
            Enumeration<JarEntry> entries = jarFile.entries();
            List<Class<?>> obfClasses = new ArrayList<>();
    
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                if (entry.isDirectory()) continue;
    
                String name = entry.getName();
                if (name.endsWith(".class")) {
                    String className = name.replace("/", ".").substring(0, name.length() - ".class".length());
                    obfClasses.add(Class.forName(className, false, Global.class.getClassLoader()));
                }
            }
    
            jarFile.close();
            return obfClasses;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void init() {}

}
