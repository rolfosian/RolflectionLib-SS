package rolflectionlib.inheritor;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import rolflectionlib.util.RolfLectionUtil;

public class MethodData implements Opcodes {
    public final Object method;
    public final String methodName;

    public final Class<?> returnType;
    public final Class<?>[] paramTypes;
    
    public final int access;
    public final String descriptor;

    public final String declaringClassInternalName;
    public final boolean isInterfaceMethod;

    public MethodData(Object method) {
        this.method = method;

        this.methodName = RolfLectionUtil.getMethodName(method);
        this.returnType = RolfLectionUtil.getReturnType(method);
        this.paramTypes = RolfLectionUtil.getMethodParamTypes(method);
        this.descriptor = Type.getMethodDescriptor(method);

        Class<?> declaringClass = RolfLectionUtil.getMethodDeclaringClass(method);
        this.declaringClassInternalName = Type.getInternalName(declaringClass);
        this.isInterfaceMethod = declaringClass.isInterface();

        String visibility = RolfLectionUtil.getVisibility(RolfLectionUtil.getMethodModifiers(method));
        switch(visibility) {
            case "public":
                this.access = ACC_PUBLIC;
                return;
                
            case "protected":
                this.access = ACC_PROTECTED;
                return;
            
            default:
                throw new IllegalArgumentException("invalid method visibility for method " + methodName + ": " + visibility);
        }
    }
}