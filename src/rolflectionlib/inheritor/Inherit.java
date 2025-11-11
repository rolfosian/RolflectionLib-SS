package rolflectionlib.inheritor;

import org.apache.log4j.Logger;
import org.objectweb.asm.*;

import rolflectionlib.util.RolfLectionUtil;

import com.fs.starfarer.api.Global;

public class Inherit implements Opcodes {
    private static Logger logger = Global.getLogger(Inherit.class);
    public static void print(Object... args) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            sb.append(args[i] instanceof String ? (String) args[i] : String.valueOf(args[i]));
            if (i < args.length - 1) sb.append(' ');
        }
        logger.info(sb.toString());
    }

    private static final String methodHookInternalName = Type.getInternalName(MethodHook.class);
    private static final String methodHookDescriptor = Type.getDescriptor(MethodHook.class);
    private static final String preHookMethodDescriptor = Type.getMethodDescriptor(RolfLectionUtil.getMethod("runBefore", MethodHook.class, 1));
    private static final String postHookMethodDescriptor = Type.getMethodDescriptor(RolfLectionUtil.getMethod("runAfter", MethodHook.class, 1));
    
    public static Class<?> inheritClass(
            Class<?> superClass,
            Object superClassConstructor,
            String superClassConstructorSignature,
            String inheritorName,
            MethodData[] methodDatas
        ) {

        try {
            final String origCtorDesc = Type.getConstructorDescriptor(superClassConstructor);

            String internalName = inheritorName;
            String superName = Type.getInternalName(superClass);

            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);

            // public inheritorName extends superClass
            cw.visit(V17, ACC_PUBLIC, internalName, null, superName, null);

            // define fields for method hooks
            for (int i = 0; i < methodDatas.length; i++) {
                String fieldName = "rolfLectionHook" + i;

                cw.visitField(ACC_PRIVATE | ACC_FINAL, fieldName, methodHookDescriptor, null, null).visitEnd();
            }

            // define ctor
            Class<?>[] ctorParamTypes = RolfLectionUtil.getConstructorParamTypes(superClassConstructor);
            Type[] types = new Type[ctorParamTypes.length];

            String ctorDescriptor = origCtorDesc;
            int bracketIndex = ctorDescriptor.lastIndexOf(")");
            ctorDescriptor = ctorDescriptor.substring(0, bracketIndex);

            for (int i = 0; i < methodDatas.length; i++) ctorDescriptor += methodHookDescriptor;
            ctorDescriptor += ")V";

            String ctorSignature = null;
            if (superClassConstructorSignature != null) {
                ctorSignature = superClassConstructorSignature;
                bracketIndex = superClassConstructorSignature.lastIndexOf(")");
                ctorSignature = superClassConstructorSignature.substring(0, bracketIndex);
    
                for (int i = 0; i < methodDatas.length; i++) ctorSignature += methodHookInternalName;
                ctorSignature += ")V";
            }


            for (int i = 0; i < ctorParamTypes.length; i++) types[i] = Type.getType(ctorParamTypes[i]);
            MethodVisitor ctor = cw.visitMethod(
                ACC_PUBLIC,
                "<init>",
                ctorDescriptor,
                ctorSignature,
                null
            );
            ctor.visitCode();
            // super(args)
            ctor.visitVarInsn(ALOAD, 0);

            int slot = 1;
            for (Class<?> paramType : ctorParamTypes) {
                load(ctor, paramType, slot);
                slot += (paramType == long.class || paramType == double.class) ? 2 : 1;
            }

            ctor.visitMethodInsn(INVOKESPECIAL, superName, "<init>", origCtorDesc, false);

            // set hook fields
            int argIndex = paramSlotSize(ctorParamTypes);
            for (int i = 0; i < methodDatas.length; i++) {
                ctor.visitVarInsn(ALOAD, 0);
                ctor.visitVarInsn(ALOAD, argIndex);
                ctor.visitFieldInsn(PUTFIELD, internalName, "rolfLectionHook" + i, methodHookDescriptor);
                argIndex += 1;
            }

            ctor.visitInsn(RETURN);
            ctor.visitMaxs(0, 0);
            ctor.visitEnd();

            // hook methods
            for (int i = 0; i < methodDatas.length; i++) {
                MethodData methodData = methodDatas[i];

                MethodVisitor mv = cw.visitMethod(
                    methodData.access,
                    methodData.methodName,
                    methodData.descriptor,
                    null,
                    null
                );
                mv.visitCode();

                mv.visitVarInsn(ALOAD, 0);

                Class<?> returnType = methodData.returnType;
                Class<?>[] paramTypes = methodData.paramTypes;

                boolean isPrimitiveReturnType = returnType.isPrimitive();

                int currSlot = 1;
                if (paramTypes.length > 0) {
                    mv.visitLdcInsn(paramTypes.length);
    
                    mv.visitTypeInsn(ANEWARRAY, "java/lang/Object");
                    int arrayIdx = paramSlotSize(paramTypes) + 1;
                    mv.visitVarInsn(ASTORE, arrayIdx);
                    currSlot = incrementSlot(currSlot, Object[].class);
    
                    for (int j = 0; j < paramTypes.length; j++) {
                        mv.visitVarInsn(ALOAD, arrayIdx); // load array reference
                        mv.visitLdcInsn(j);                 // array index
    
                        load(mv, paramTypes[j], paramSlot(paramTypes, j)); // store in array
    
                        if (paramTypes[j].isPrimitive()) box(mv, Type.getType(paramTypes[j]));
                        mv.visitInsn(AASTORE);
                    }
    
                    // invoke pre-hook with our new array as parameter
                    mv.visitVarInsn(ALOAD, 0);
                    mv.visitFieldInsn(GETFIELD, internalName, "rolfLectionHook" + i, methodHookDescriptor); 
                    mv.visitVarInsn(ALOAD, arrayIdx);
                    mv.visitMethodInsn(INVOKEINTERFACE, methodHookInternalName, "runBefore", preHookMethodDescriptor, true);
    
                    // store returned Object[] in same arrayIdx
                    mv.visitVarInsn(ASTORE, arrayIdx);
    
                    // unpack array back into parameters
                    for (int j = 0; j < paramTypes.length; j++) {
                        mv.visitVarInsn(ALOAD, arrayIdx);
                        mv.visitLdcInsn(j);
                        mv.visitInsn(AALOAD);

                        if (paramTypes[j].isPrimitive()) {
                            unbox(mv, Type.getType(paramTypes[j]));
                        } else {
                            mv.visitTypeInsn(CHECKCAST, Type.getInternalName(paramTypes[j]));
                        }

                        store(mv, paramTypes[j], paramSlot(paramTypes, j));
                    }
    
                    // --- invoke super/original method ---
                    mv.visitVarInsn(ALOAD, 0);
                    for (int j = 0; j < paramTypes.length; j++) {
                        load(mv, paramTypes[j], paramSlot(paramTypes, j));
                    }

                    if (returnType == void.class) {
                        // call runAfter with null and discard its return
                        mv.visitVarInsn(ALOAD, 0);
                        mv.visitFieldInsn(GETFIELD, internalName, "rolfLectionHook" + i, methodHookDescriptor);
                        mv.visitInsn(ACONST_NULL);
                        mv.visitMethodInsn(INVOKEINTERFACE, methodHookInternalName, "runAfter", postHookMethodDescriptor, true);
                        mv.visitInsn(POP);
                        mv.visitInsn(RETURN);
                        
                    } else {
                        mv.visitMethodInsn(INVOKESPECIAL, superName, methodData.methodName, methodData.descriptor, methodData.isInterfaceMethod);
                        if (isPrimitiveReturnType) {
                            box(mv, Type.getType(returnType));
                        }

                        mv.visitVarInsn(ALOAD, 0);
                        mv.visitFieldInsn(GETFIELD, internalName, "rolfLectionHook" + i, methodHookDescriptor);
                        mv.visitInsn(SWAP);
                        mv.visitMethodInsn(INVOKEINTERFACE, methodHookInternalName, "runAfter", postHookMethodDescriptor, true);
    
                        if (isPrimitiveReturnType) unbox(mv, Type.getType(returnType));
                        else mv.visitTypeInsn(CHECKCAST, Type.getInternalName(returnType));
                        mv.visitInsn(getReturnOpcode(returnType));
                    }

                } else {
                    // invoke prehook with null
                    mv.visitFieldInsn(GETFIELD, internalName, "rolfLectionHook" + i, methodHookDescriptor); 
                    mv.visitInsn(ACONST_NULL);
                    mv.visitMethodInsn(INVOKEINTERFACE, methodHookInternalName, "runBefore", preHookMethodDescriptor, true);

                    // invoke super method
                    mv.visitVarInsn(ALOAD, 0);
                    mv.visitMethodInsn(INVOKESPECIAL, superName, methodData.methodName, methodData.descriptor, methodData.isInterfaceMethod);
                    
                    if (returnType == void.class) {
                        // invoke posthook with null
                        mv.visitVarInsn(ALOAD, 0);
                        mv.visitFieldInsn(GETFIELD, internalName, "rolfLectionHook" + i, methodHookDescriptor);
                        mv.visitInsn(ACONST_NULL);
                        mv.visitMethodInsn(INVOKEINTERFACE, methodHookInternalName, "runAfter", postHookMethodDescriptor, true);

                        mv.visitInsn(POP);
                        mv.visitInsn(RETURN);
                        
                    } else {
                        if (isPrimitiveReturnType) {
                            box(mv, Type.getType(returnType));
                        }

                        mv.visitVarInsn(ALOAD, 0);
                        mv.visitFieldInsn(GETFIELD, internalName, "rolfLectionHook" + i, methodHookDescriptor);
                        mv.visitInsn(SWAP);
                        mv.visitMethodInsn(INVOKEINTERFACE, methodHookInternalName, "runAfter", postHookMethodDescriptor, true);
    
                        if (isPrimitiveReturnType) unbox(mv, Type.getType(returnType));
                        else mv.visitTypeInsn(CHECKCAST, Type.getInternalName(returnType));
                        mv.visitInsn(getReturnOpcode(returnType));
                    }
                }
                mv.visitMaxs(0, 0);
                mv.visitEnd();
            }
            cw.visitEnd();

            String classBinaryName = internalName.replace('/', '.');
            return (Class<?>) RolfLectionUtil.getMethodDeclaredAndInvokeDirectly("define", new ClassLoader(Inherit.class.getClassLoader()) {
                Class<?> define(byte[] b) {
                    return defineClass(classBinaryName, b, 0, b.length);
                }
            },
            cw.toByteArray());

        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    protected static void popStack(MethodVisitor mv, Class<?> type) {
        if (type == long.class || type == double.class) mv.visitInsn(POP2);
        else mv.visitInsn(POP);
    }

    protected static int incrementSlot(int currSlot, Class<?> type) {
        return currSlot + ((type == long.class || type == double.class) ? 2 : 1);
    }

    protected static int paramSlot(Class<?>[] params, int index) {
        int slot = 1; // slot 0 = this
        for (int i = 0; i < index; i++) {
            Class<?> t = params[i];
            slot += (t == long.class || t == double.class) ? 2 : 1;
        }
        return slot;
    }

    protected static int paramSlotSize(Class<?>[] params) {
        int slots = 1;
        for (Class<?> t : params) slots += (t == long.class || t == double.class) ? 2 : 1;
        return slots;
    }

    protected static int getMethodOpCode(Object method) {
        int modifiers = RolfLectionUtil.getMethodModifiers(method);
        if (RolfLectionUtil.isPrivate(modifiers)) return INVOKESPECIAL;

        return RolfLectionUtil.isStatic(modifiers) ? INVOKESTATIC : INVOKEVIRTUAL;
    }

    protected static void load(MethodVisitor mv, Class<?> type, int idx) {
        mv.visitVarInsn(getLoadOpCode(type), idx);
    }

    protected static void emitReturn(MethodVisitor mv, Class<?> returnType) {
        if (returnType == void.class) mv.visitInsn(RETURN);
        else if (returnType == long.class) mv.visitInsn(LRETURN);
        else if (returnType == float.class) mv.visitInsn(FRETURN);
        else if (returnType == double.class) mv.visitInsn(DRETURN);
        else if (returnType.isPrimitive()) mv.visitInsn(IRETURN);
        else mv.visitInsn(ARETURN);
    }

    protected static int getLoadOpCode(Class<?> type) {
        if (type == int.class || type == boolean.class || type == short.class || type == byte.class || type == char.class) return ILOAD;
        if (type == float.class) return FLOAD;
        if (type == double.class) return DLOAD;
        if (type == long.class) return LLOAD;
        return ALOAD;
    }

    protected static void box(MethodVisitor mv, Type type) {
        switch (type.getSort()) {
            case Type.BOOLEAN:
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;", false);
                break;
            case Type.BYTE:
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;", false);
                break;
            case Type.CHAR:
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/Character", "valueOf", "(C)Ljava/lang/Character;", false);
                break;
            case Type.SHORT:
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/Short", "valueOf", "(S)Ljava/lang/Short;", false);
                break;
            case Type.INT:
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
                break;
            case Type.FLOAT:
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;", false);
                break;
            case Type.LONG:
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;", false);
                break;
            case Type.DOUBLE:
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;", false);
                break;
            default:
                break;
        }
    }

    protected static void unbox(MethodVisitor mv, Type type) {
        String owner, method, desc;
    
        switch (type.getSort()) {
            case Type.BOOLEAN:
                owner = "java/lang/Boolean";
                method = "booleanValue";
                desc = "()Z";
                break;
            case Type.BYTE:
                owner = "java/lang/Byte";
                method = "byteValue";
                desc = "()B";
                break;
            case Type.CHAR:
                owner = "java/lang/Character";
                method = "charValue";
                desc = "()C";
                break;
            case Type.SHORT:
                owner = "java/lang/Short";
                method = "shortValue";
                desc = "()S";
                break;
            case Type.INT:
                owner = "java/lang/Integer";
                method = "intValue";
                desc = "()I";
                break;
            case Type.FLOAT:
                owner = "java/lang/Float";
                method = "floatValue";
                desc = "()F";
                break;
            case Type.LONG:
                owner = "java/lang/Long";
                method = "longValue";
                desc = "()J";
                break;
            case Type.DOUBLE:
                owner = "java/lang/Double";
                method = "doubleValue";
                desc = "()D";
                break;
            default:
                return;
        }
    
        mv.visitTypeInsn(CHECKCAST, owner);
        mv.visitMethodInsn(INVOKEVIRTUAL, owner, method, desc, false);
    }

    protected static void store(MethodVisitor mv, Class<?> type, int idx) {
        mv.visitVarInsn(getStoreOpCode(type), idx);
    }
    
    protected static int getStoreOpCode(Class<?> type) {
        if (type == int.class || type == boolean.class || type == byte.class || type == char.class || type == short.class)
            return ISTORE;
        if (type == float.class) return FSTORE;
        if (type == double.class) return DSTORE;
        if (type == long.class) return LSTORE;
        return ASTORE;
    }

    protected static int getReturnOpcode(Class<?> type) {
        if (type == void.class) return RETURN;
        if (type == int.class || type == boolean.class || type == byte.class
            || type == char.class || type == short.class) return IRETURN;
        if (type == long.class) return LRETURN;
        if (type == float.class) return FRETURN;
        if (type == double.class) return DRETURN;
        return ARETURN;
    }
}
