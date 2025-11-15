package rolflectionlib.inheritor;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import rolflectionlib.util.RolfLectionUtil;

public class MethodData {
    public final Object method;
    public final String methodName;

    public final Class<?> returnType;
    public final Class<?>[] paramTypes;
    
    public final int access;
    public final String descriptor;

    public final String declaringClassInternalName;
    public final boolean isInterfaceMethod;
    public final boolean callSuper;

    /**
     * MethodData object to define class methods to hook
     * @param method java.lang.reflect.Method object - The method to hook <p>
     * <code>SuperClassMethodHook</code> corresponds with regular class methods. Sandwiches a super call between the two hooks given. Both parameters and return values may be altered at each stage here.</p><p>
     * <code>InterfaceMethodHook</code> corresponds with interface methods being implemented.</p><p>
     * @param callSuper Whether or not to call the super method. Irrelevant for interface methods. <p><code>OverrideMethodHook</code> corresponds to the given method when set to false.
     */
    public MethodData(Object method, boolean callSuper) {
        this.method = method;

        this.methodName = RolfLectionUtil.getMethodName(method);
        this.returnType = RolfLectionUtil.getReturnType(method);
        this.paramTypes = RolfLectionUtil.getMethodParamTypes(method);
        this.descriptor = Type.getMethodDescriptor(method);

        Class<?> declaringClass = RolfLectionUtil.getMethodDeclaringClass(method);
        this.declaringClassInternalName = Type.getInternalName(declaringClass);
        this.isInterfaceMethod = declaringClass.isInterface();

        if (this.isInterfaceMethod) this.callSuper = true;
        else this.callSuper = callSuper;

        String visibility = RolfLectionUtil.getVisibility(RolfLectionUtil.getMethodModifiers(method));
        switch(visibility) {
            case "public":
                this.access = Opcodes.ACC_PUBLIC;
                return;
                
            case "protected":
                this.access = Opcodes.ACC_PROTECTED;
                return;
            
            default:
                throw new IllegalArgumentException("invalid method visibility for method " + methodName + ": " + visibility);
        }
    }
}