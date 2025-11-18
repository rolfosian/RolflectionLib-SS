package rolflectionlib.inheritor;

import org.objectweb.asm.Type;

/**
 * <code>MethodDataPair</code> dataclass for implementing calling a super method directly without reflection via a custom interface.
 * Note: the constructor parameter <code>customInterface</code> must also be passed in the {@link Inherit#extendClass(Class, Class[], Object, String, MethodData[], MethodDataPair[])} interface array parameter
 * @param customInterface Your custom interface implementing the desired target method invoker
 * @param customInterfaceMethod The custom interface method that is to invoke the target method
 * @param methodToInvoke The super method to invoke
 */
public class MethodDataPair {
    public final String customInterfaceDescriptor;
    public final MethodData customInterfaceMethod;
    public final MethodData targetMethodToInvoke;

    public MethodDataPair(Class<?> customInterface, MethodData customInterfaceMethodData, MethodData targetMethodToInvoke) {
        if (!signatureCheck(customInterfaceMethodData, targetMethodToInvoke))
            throw new IllegalArgumentException("Method signatures must match");

        this.customInterfaceDescriptor = Type.getDescriptor(customInterface);
        this.customInterfaceMethod = customInterfaceMethodData;
        this.targetMethodToInvoke = targetMethodToInvoke;
    }

    public static boolean signatureCheck(MethodData one, MethodData two) {
        if (one.returnType != two.returnType || one.paramTypes.length != two.paramTypes.length) return false;
        for (int i = 0; i < one.paramTypes.length; i++) if (one.paramTypes[i] != two.paramTypes[i]) return false;
        return true;
    }
}
