package rolflectionlib.inheritor.enginetex;

import org.objectweb.asm.*;

import rolflectionlib.util.RolfLectionUtil;

public class EngineTexASM implements Opcodes {

    public static Class<?> buildEngineTexClass(
            ClassLoader parent,
            Class<?> texSuperClass,
            Class<?> engineClass,
            String texBindMethodName
    ) throws Exception {

        String internalName = "com/fs/graphics/EngineTex";
        String superName = Type.getInternalName(texSuperClass);
        String engineDesc = Type.getDescriptor(engineClass);
        String engineInternalName = Type.getInternalName(engineClass);
        String interfaceName = Type.getInternalName(EngineTexInterface.class);

        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);

        // package com.fs.graphics;
        // import rolflectionlib.inheritor.enginetex.EngineTexInterface;

        // public Class EngineTex extends texSuperClass implements EngineTexInterface;
        cw.visit(V17, ACC_PUBLIC, internalName, null, superName, new String[]{interfaceName});

        // fields
        cw.visitField(ACC_PRIVATE, "increments", "I", null, null).visitEnd();
        cw.visitField(ACC_PRIVATE, "texIds", "[I", null, null).visitEnd();
        cw.visitField(ACC_PRIVATE | ACC_FINAL, "engines", "Ljava/util/List;", "Ljava/util/List<" + engineDesc + ">;", null).visitEnd();

        cw.visitField(ACC_PRIVATE, "lastUsedFrame", "I", null, null).visitEnd();
        cw.visitField(ACC_PRIVATE, "incrementer", "I", null, null).visitEnd();
        cw.visitField(ACC_PRIVATE, "engineTexDelegate", "Lrolflectionlib/inheritor/enginetex/EngineTexDelegate;", null, null).visitEnd();

        // Constructor: public EngineTex(int arg0, int arg1, int[] texIds, List<engineClass> engines)
        MethodVisitor mv2 = cw.visitMethod(
                ACC_PUBLIC,
                "<init>",
                "(II[ILjava/util/List;)V",
                "(II[ILjava/util/List<" + engineDesc + ">;)V",
                null
        );
        mv2.visitCode();
        // super(arg0, arg1)
        mv2.visitVarInsn(ALOAD, 0);
        mv2.visitVarInsn(ILOAD, 1);
        mv2.visitVarInsn(ILOAD, 2);
        mv2.visitMethodInsn(INVOKESPECIAL, superName, "<init>", "(II)V", false);

        // this.increments = texIds.length
        mv2.visitVarInsn(ALOAD, 0);
        mv2.visitVarInsn(ALOAD, 3); // Load texIds array
        mv2.visitInsn(ARRAYLENGTH); // Get array length
        mv2.visitFieldInsn(PUTFIELD, internalName, "increments", "I");

        // this.texIds = texIds (direct assignment)
        mv2.visitVarInsn(ALOAD, 0);
        mv2.visitVarInsn(ALOAD, 3); // Load texIds array parameter
        mv2.visitFieldInsn(PUTFIELD, internalName, "texIds", "[I");

        // this.incrementer = 0;
        mv2.visitVarInsn(ALOAD, 0);
        mv2.visitInsn(ICONST_0);
        mv2.visitFieldInsn(PUTFIELD, internalName, "incrementer", "I");

        // this.lastUsedFrame = -1
        mv2.visitVarInsn(ALOAD, 0);
        mv2.visitInsn(ICONST_M1); // Push -1
        mv2.visitFieldInsn(PUTFIELD, internalName, "lastUsedFrame", "I");

        // this.engines = engines
        mv2.visitVarInsn(ALOAD, 0);
        mv2.visitVarInsn(ALOAD, 4);
        mv2.visitFieldInsn(PUTFIELD, internalName, "engines", "Ljava/util/List;");
        mv2.visitInsn(RETURN);
        mv2.visitMaxs(0, 0);
        mv2.visitEnd();

        // Constructor: public EngineTex(int arg0, int arg1, String[] spriteIds, List<engineClass> engines)
        MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC,
                "<init>",
                "(II[Ljava/lang/String;Ljava/util/List;)V",
                "(II[Ljava/lang/String;Ljava/util/List<" + engineDesc + ">;)V",
                null
        );
        mv.visitCode();
        // super(arg0, arg1)
        mv.visitVarInsn(ALOAD, 0);
        mv.visitVarInsn(ILOAD, 1);
        mv.visitVarInsn(ILOAD, 2);
        mv.visitMethodInsn(INVOKESPECIAL, superName, "<init>", "(II)V", false);

        // this.increments = spriteIds.length
        mv.visitVarInsn(ALOAD, 0);
        mv.visitVarInsn(ALOAD, 3); // Load spriteIds array
        mv.visitInsn(ARRAYLENGTH); // Get array length
        mv.visitFieldInsn(PUTFIELD, internalName, "increments", "I");

        // this.texIds = new int[spriteIds.length]
        mv.visitVarInsn(ALOAD, 0);
        mv.visitVarInsn(ALOAD, 3); // Load spriteIds array
        mv.visitInsn(ARRAYLENGTH); // Get array length
        mv.visitIntInsn(NEWARRAY, T_INT); // Create new int array
        mv.visitFieldInsn(PUTFIELD, internalName, "texIds", "[I");

        // this.incrementer = 0;
        mv.visitVarInsn(ALOAD, 0);
        mv.visitInsn(ICONST_0);
        mv.visitFieldInsn(PUTFIELD, internalName, "incrementer", "I");

        // this.lastUsedFrame = -1
        mv.visitVarInsn(ALOAD, 0);
        mv.visitInsn(ICONST_M1); // Push -1
        mv.visitFieldInsn(PUTFIELD, internalName, "lastUsedFrame", "I");

        // for (int i = 0; i < spriteIds.length; i++) {
        //     java.lang.Object texWrapper = (java.lang.Object) rolflectionlib.util.TexReflection.texObjectMap.get(spriteIds[i]);
        //     this.texIds[i] = rolflectionlib.util.TexReflection.getTexId(texWrapper);
        // }
        Label loopStart = new Label();
        Label loopEnd = new Label();
        mv.visitInsn(ICONST_0); // i = 0
        mv.visitVarInsn(ISTORE, 5); // Store i in local variable 5
        mv.visitLabel(loopStart);
        mv.visitVarInsn(ILOAD, 5); // Load i
        mv.visitVarInsn(ALOAD, 3); // Load spriteIds array
        mv.visitInsn(ARRAYLENGTH); // Get spriteIds.length
        mv.visitJumpInsn(IF_ICMPGE, loopEnd); // if (i >= spriteIds.length) goto loopEnd
        
        // texIds[i] = TexReflection.getTexId(TexReflection.texObjectMap.get(spriteIds[i]))
        mv.visitVarInsn(ALOAD, 0); // Load this
        mv.visitFieldInsn(GETFIELD, internalName, "texIds", "[I"); // Load texIds array
        mv.visitVarInsn(ILOAD, 5); // Load i
        mv.visitFieldInsn(GETSTATIC, "rolflectionlib/util/TexReflection", "texObjectMap", "Ljava/util/Map;");
        mv.visitVarInsn(ALOAD, 3); // Load spriteIds array
        mv.visitVarInsn(ILOAD, 5); // Load i
        mv.visitInsn(AALOAD); // Load spriteIds[i]
        mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "get", "(Ljava/lang/Object;)Ljava/lang/Object;", true);
        mv.visitMethodInsn(INVOKESTATIC, "rolflectionlib/util/TexReflection", "getTexId", "(Ljava/lang/Object;)I", false);
        mv.visitInsn(IASTORE); // Store result in texIds[i]
        
        mv.visitIincInsn(5, 1); // i++
        mv.visitJumpInsn(GOTO, loopStart);
        mv.visitLabel(loopEnd);

        // this.engines = engines
        mv.visitVarInsn(ALOAD, 0);
        mv.visitVarInsn(ALOAD, 4);
        mv.visitFieldInsn(PUTFIELD, internalName, "engines", "Ljava/util/List;");
        mv.visitInsn(RETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();

        // Constructor: public EngineTex(int arg0, int arg1, int[] texIds, List<engineClass> engines, EngineTexDelegate delegate)
        MethodVisitor mv3 = cw.visitMethod(
                ACC_PUBLIC,
                "<init>",
                "(II[ILjava/util/List;Lrolflectionlib/inheritor/enginetex/EngineTexDelegate;)V",
                "(II[ILjava/util/List<" + engineDesc + ">;Lrolflectionlib/inheritor/enginetex/EngineTexDelegate;)V",
                null
        );
        mv3.visitCode();
        // super(arg0, arg1)
        mv3.visitVarInsn(ALOAD, 0);
        mv3.visitVarInsn(ILOAD, 1);
        mv3.visitVarInsn(ILOAD, 2);
        mv3.visitMethodInsn(INVOKESPECIAL, superName, "<init>", "(II)V", false);

        // this.increments = texIds.length
        mv3.visitVarInsn(ALOAD, 0);
        mv3.visitVarInsn(ALOAD, 3); // Load texIds array
        mv3.visitInsn(ARRAYLENGTH); // Get array length
        mv3.visitFieldInsn(PUTFIELD, internalName, "increments", "I");

        // this.texIds = texIds (direct assignment)
        mv3.visitVarInsn(ALOAD, 0);
        mv3.visitVarInsn(ALOAD, 3); // Load texIds array parameter
        mv3.visitFieldInsn(PUTFIELD, internalName, "texIds", "[I");

        // this.incrementer = 0;
        mv3.visitVarInsn(ALOAD, 0);
        mv3.visitInsn(ICONST_0);
        mv3.visitFieldInsn(PUTFIELD, internalName, "incrementer", "I");

        // this.lastUsedFrame = -1
        mv3.visitVarInsn(ALOAD, 0);
        mv3.visitInsn(ICONST_M1); // Push -1
        mv3.visitFieldInsn(PUTFIELD, internalName, "lastUsedFrame", "I");

        // this.engines = engines
        mv3.visitVarInsn(ALOAD, 0);
        mv3.visitVarInsn(ALOAD, 4);
        mv3.visitFieldInsn(PUTFIELD, internalName, "engines", "Ljava/util/List;");

        // this.engineTexDelegate = delegate
        mv3.visitVarInsn(ALOAD, 0);
        mv3.visitVarInsn(ALOAD, 5); // Load delegate parameter
        mv3.visitFieldInsn(PUTFIELD, internalName, "engineTexDelegate", "Lrolflectionlib/inheritor/enginetex/EngineTexDelegate;");
        mv3.visitInsn(RETURN);
        mv3.visitMaxs(0, 0);
        mv3.visitEnd();

        // Constructor: public EngineTex(int arg0, int arg1, String[] spriteIds, List<engineClass> engines, EngineTexDelegate delegate)
        MethodVisitor mv4 = cw.visitMethod(
                ACC_PUBLIC,
                "<init>",
                "(II[Ljava/lang/String;Ljava/util/List;Lrolflectionlib/inheritor/enginetex/EngineTexDelegate;)V",
                "(II[Ljava/lang/String;Ljava/util/List<" + engineDesc + ">;Lrolflectionlib/inheritor/enginetex/EngineTexDelegate;)V",
                null
        );
        mv4.visitCode();
        // super(arg0, arg1)
        mv4.visitVarInsn(ALOAD, 0);
        mv4.visitVarInsn(ILOAD, 1);
        mv4.visitVarInsn(ILOAD, 2);
        mv4.visitMethodInsn(INVOKESPECIAL, superName, "<init>", "(II)V", false);

        // this.increments = spriteIds.length
        mv4.visitVarInsn(ALOAD, 0);
        mv4.visitVarInsn(ALOAD, 3); // Load spriteIds array
        mv4.visitInsn(ARRAYLENGTH); // Get array length
        mv4.visitFieldInsn(PUTFIELD, internalName, "increments", "I");

        // this.texIds = new int[spriteIds.length]
        mv4.visitVarInsn(ALOAD, 0);
        mv4.visitVarInsn(ALOAD, 3); // Load spriteIds array
        mv4.visitInsn(ARRAYLENGTH); // Get array length
        mv4.visitIntInsn(NEWARRAY, T_INT); // Create new int array
        mv4.visitFieldInsn(PUTFIELD, internalName, "texIds", "[I");

        // this.incrementer = 0;
        mv4.visitVarInsn(ALOAD, 0);
        mv4.visitInsn(ICONST_0);
        mv4.visitFieldInsn(PUTFIELD, internalName, "incrementer", "I");

        // this.lastUsedFrame = -1
        mv4.visitVarInsn(ALOAD, 0);
        mv4.visitInsn(ICONST_M1); // Push -1
        mv4.visitFieldInsn(PUTFIELD, internalName, "lastUsedFrame", "I");

        // for (int i = 0; i < spriteIds.length; i++) {
        //     java.lang.Object texWrapper = (java.lang.Object) rolflectionlib.util.TexReflection.texObjectMap.get(spriteIds[i]);
        //     this.texIds[i] = rolflectionlib.util.TexReflection.getTexId(texWrapper);
        // }
        Label loopStart2 = new Label();
        Label loopEnd2 = new Label();
        mv4.visitInsn(ICONST_0); // i = 0
        mv4.visitVarInsn(ISTORE, 6); // Store i in local variable 6
        mv4.visitLabel(loopStart2);
        mv4.visitVarInsn(ILOAD, 6); // Load i
        mv4.visitVarInsn(ALOAD, 3); // Load spriteIds array
        mv4.visitInsn(ARRAYLENGTH); // Get spriteIds.length
        mv4.visitJumpInsn(IF_ICMPGE, loopEnd2); // if (i >= spriteIds.length) goto loopEnd2
        
        // texIds[i] = TexReflection.getTexId(TexReflection.texObjectMap.get(spriteIds[i]))
        mv4.visitVarInsn(ALOAD, 0); // Load this
        mv4.visitFieldInsn(GETFIELD, internalName, "texIds", "[I"); // Load texIds array
        mv4.visitVarInsn(ILOAD, 6); // Load i
        mv4.visitFieldInsn(GETSTATIC, "rolflectionlib/util/TexReflection", "texObjectMap", "Ljava/util/Map;");
        mv4.visitVarInsn(ALOAD, 3); // Load spriteIds array
        mv4.visitVarInsn(ILOAD, 6); // Load i
        mv4.visitInsn(AALOAD); // Load spriteIds[i]
        mv4.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "get", "(Ljava/lang/Object;)Ljava/lang/Object;", true);
        mv4.visitMethodInsn(INVOKESTATIC, "rolflectionlib/util/TexReflection", "getTexId", "(Ljava/lang/Object;)I", false);
        mv4.visitInsn(IASTORE); // Store result in texIds[i]
        
        mv4.visitIincInsn(6, 1); // i++
        mv4.visitJumpInsn(GOTO, loopStart2);
        mv4.visitLabel(loopEnd2);

        // this.engines = engines
        mv4.visitVarInsn(ALOAD, 0);
        mv4.visitVarInsn(ALOAD, 4);
        mv4.visitFieldInsn(PUTFIELD, internalName, "engines", "Ljava/util/List;");

        // this.engineTexDelegate = delegate
        mv4.visitVarInsn(ALOAD, 0);
        mv4.visitVarInsn(ALOAD, 5); // Load delegate parameter
        mv4.visitFieldInsn(PUTFIELD, internalName, "engineTexDelegate", "Lrolflectionlib/inheritor/enginetex/EngineTexDelegate;");
        mv4.visitInsn(RETURN);
        mv4.visitMaxs(0, 0);
        mv4.visitEnd();

        // Override texBindMethodName
        // public void texBindMethodName() {
        //     if (rolflectionlib.plugins.RolfLectionLibPlugin.frame != this.lastUsedFrame) {
        //         this.incrementer = 0;
        //         this.lastUsedFrame = rolflectionlib.plugins.RolfLectionLibPlugin.frame;
        //     }
        //
        //     while (!this.engines.get(incrementer).isActive()) {
        //         this.incrementer = (this.incrementer + this.increments) % increments;
        //     }
        //
        //     this.engineTexDelegate.onTexBind(incrementer);
        //
        //     rolflectionlib.util.TexReflection.setTexId(this, this.texIds[this.incrementer]);
        //     super.texBindMethodName();
        //     this.incrementer = (this.incrementer + 1) % this.increments;
        // }
        MethodVisitor bind = cw.visitMethod(ACC_PUBLIC, texBindMethodName, "()V", null, null);
        bind.visitCode();
        // boolean isRolloverEngine = false
        bind.visitInsn(ICONST_0); // Push false
        bind.visitVarInsn(ISTORE, 1); // Store in local variable 1 (isRolloverEngine)
        
        // if (RolfLectionLibPlugin.frame != this.lastUsedFrame) {
        Label ifEnd = new Label();
        bind.visitFieldInsn(GETSTATIC, "rolflectionlib/plugins/RolfLectionLibCombatPlugin", "frame", "I");
        bind.visitVarInsn(ALOAD, 0);
        bind.visitFieldInsn(GETFIELD, internalName, "lastUsedFrame", "I");
        bind.visitJumpInsn(IF_ICMPEQ, ifEnd); // if (frame == lastUsedFrame) goto ifEnd
        
        //     this.incrementer = 0;
        bind.visitVarInsn(ALOAD, 0);
        bind.visitInsn(ICONST_0);
        bind.visitFieldInsn(PUTFIELD, internalName, "incrementer", "I");

        // isRolloverEngine = true
        bind.visitInsn(ICONST_1); // Push true
        bind.visitVarInsn(ISTORE, 1); // Store in local variable 1 (isRolloverEngine)
        
        //     this.lastUsedFrame = RolfLectionLibPlugin.frame;
        bind.visitVarInsn(ALOAD, 0);
        bind.visitFieldInsn(GETSTATIC, "rolflectionlib/plugins/RolfLectionLibCombatPlugin", "frame", "I");
        bind.visitFieldInsn(PUTFIELD, internalName, "lastUsedFrame", "I");
        
        bind.visitLabel(ifEnd);

        // while (!this.engines.get(this.incrementer).isActive()) {
        //     this.incrementer = (this.incrementer + 1) % increments;
        // }
        Label whileStart = new Label();
        Label whileEnd = new Label();
        bind.visitLabel(whileStart);
        
        // Load this.engines
        bind.visitVarInsn(ALOAD, 0);
        bind.visitFieldInsn(GETFIELD, internalName, "engines", "Ljava/util/List;");
        
        // Load this.incrementer
        bind.visitVarInsn(ALOAD, 0);
        bind.visitFieldInsn(GETFIELD, internalName, "incrementer", "I");
        
        // Call engines.get(incrementer)
        bind.visitMethodInsn(INVOKEINTERFACE, "java/util/List", "get", "(I)Ljava/lang/Object;", true);
        
        // Cast to engine class and call isActive()
        bind.visitTypeInsn(CHECKCAST, engineInternalName);
        bind.visitMethodInsn(INVOKEVIRTUAL, engineInternalName, "isActive", "()Z", false);
        
        // If isActive() returns true, exit loop
        bind.visitJumpInsn(IFNE, whileEnd);
        
        // If not active, increment: this.incrementer = (this.incrementer + 1) % increments
        bind.visitVarInsn(ALOAD, 0); // Load this
        bind.visitVarInsn(ALOAD, 0); // Load this
        bind.visitFieldInsn(GETFIELD, internalName, "incrementer", "I"); // Load incrementer
        bind.visitInsn(ICONST_1); // Push 1
        bind.visitInsn(IADD); // incrementer + 1
        bind.visitVarInsn(ALOAD, 0); // Load this
        bind.visitFieldInsn(GETFIELD, internalName, "increments", "I"); // Load increments
        bind.visitInsn(IREM); // (incrementer + 1) % increments
        bind.visitFieldInsn(PUTFIELD, internalName, "incrementer", "I"); // Store result
        
        // Loop back
        bind.visitJumpInsn(GOTO, whileStart);
        bind.visitLabel(whileEnd);

        // if (this.engineTexDelegate != null) {
        //     this.engineTexDelegate.onTexBind(this.incrementer);
        // }
        Label delegateNull = new Label();
        bind.visitVarInsn(ALOAD, 0); // Load this
        bind.visitFieldInsn(GETFIELD, internalName, "engineTexDelegate", "Lrolflectionlib/inheritor/enginetex/EngineTexDelegate;"); // Load engineTexDelegate
        bind.visitJumpInsn(IFNULL, delegateNull); // if (engineTexDelegate == null) goto delegateNull
        
        // Call onTexBind(incrementer, isRolloverEngine)
        bind.visitVarInsn(ALOAD, 0); // Load this
        bind.visitFieldInsn(GETFIELD, internalName, "engineTexDelegate", "Lrolflectionlib/inheritor/enginetex/EngineTexDelegate;"); // Load engineTexDelegate
        bind.visitVarInsn(ALOAD, 0); // Load this
        bind.visitFieldInsn(GETFIELD, internalName, "incrementer", "I"); // Load incrementer
        bind.visitVarInsn(ILOAD, 1); // Load isRolloverEngine
        bind.visitMethodInsn(INVOKEVIRTUAL, "rolflectionlib/inheritor/enginetex/EngineTexDelegate", "onTexBind", "(IZ)V", false);
        
        bind.visitLabel(delegateNull);
        
        // TexReflection.setTexId(this, this.texIds[this.incrementer]);
        bind.visitVarInsn(ALOAD, 0); // Load this
        bind.visitVarInsn(ALOAD, 0); // Load this
        bind.visitFieldInsn(GETFIELD, internalName, "texIds", "[I"); // Load texIds array
        bind.visitVarInsn(ALOAD, 0); // Load this
        bind.visitFieldInsn(GETFIELD, internalName, "incrementer", "I"); // Load incrementer
        bind.visitInsn(IALOAD); // Load texIds[incrementer]
        bind.visitMethodInsn(INVOKESTATIC, "rolflectionlib/util/TexReflection", "setTexId", "(Ljava/lang/Object;I)V", false);
        
        // super.texBindMethodName();
        bind.visitVarInsn(ALOAD, 0);
        bind.visitMethodInsn(INVOKESPECIAL, superName, texBindMethodName, "()V", false);
        
        // this.incrementer = (this.incrementer + 1) % this.increments;
        bind.visitVarInsn(ALOAD, 0); // Load this
        bind.visitVarInsn(ALOAD, 0); // Load this
        bind.visitFieldInsn(GETFIELD, internalName, "incrementer", "I"); // Load incrementer
        bind.visitInsn(ICONST_1); // Push 1
        bind.visitInsn(IADD); // incrementer + 1
        bind.visitVarInsn(ALOAD, 0); // Load this
        bind.visitFieldInsn(GETFIELD, internalName, "increments", "I"); // Load increments
        bind.visitInsn(IREM); // (incrementer + 1) % increments
        bind.visitFieldInsn(PUTFIELD, internalName, "incrementer", "I");
        
        bind.visitInsn(RETURN);
        bind.visitMaxs(0, 0);
        bind.visitEnd();

        // Implement EngineTexInterface.setTexIds(int[] texIds)
        // public void setTexIds(int[] texIds) {
        //     this.texIds = texIds;
        //     this.increments = texIds.length;
        // }
        MethodVisitor setTexIds = cw.visitMethod(ACC_PUBLIC, "setTexIds", "([I)V", null, null);
        setTexIds.visitCode();
        
        // this.texIds = texIds
        setTexIds.visitVarInsn(ALOAD, 0); // Load this
        setTexIds.visitVarInsn(ALOAD, 1); // Load texIds parameter
        setTexIds.visitFieldInsn(PUTFIELD, internalName, "texIds", "[I");
        
        // this.increments = texIds.length
        setTexIds.visitVarInsn(ALOAD, 0); // Load this
        setTexIds.visitVarInsn(ALOAD, 1); // Load texIds parameter
        setTexIds.visitInsn(ARRAYLENGTH); // Get array length
        setTexIds.visitFieldInsn(PUTFIELD, internalName, "increments", "I");
        
        setTexIds.visitInsn(RETURN);
        setTexIds.visitMaxs(0, 0);
        setTexIds.visitEnd();

        // implement EngineTexInterface.getTexIds()
        // public int[] getTexIds() {
        //     return this.texIds;
        // }
        MethodVisitor getTexIds = cw.visitMethod(ACC_PUBLIC, "getTexIds", "()[I", null, null);
        getTexIds.visitCode();
        
        // return this.texIds
        getTexIds.visitVarInsn(ALOAD, 0); // Load this
        getTexIds.visitFieldInsn(GETFIELD, internalName, "texIds", "[I"); // Load texIds field
        getTexIds.visitInsn(ARETURN); // Return the array
        getTexIds.visitMaxs(0, 0);
        getTexIds.visitEnd();

        // implement EngineTexInterface.setDelegate(EngineTexDelegate delegate)
        // public void setDelegate(EngineTexDelegate delegate) {
        //     this.engineTexDelegate = delegate;
        // }
        MethodVisitor setDelegate = cw.visitMethod(ACC_PUBLIC, "setDelegate", "(Lrolflectionlib/inheritor/enginetex/EngineTexDelegate;)V", null, null);
        setDelegate.visitCode();
        
        // this.engineTexDelegate = delegate
        setDelegate.visitVarInsn(ALOAD, 0); // Load this
        setDelegate.visitVarInsn(ALOAD, 1); // Load delegate parameter
        setDelegate.visitFieldInsn(PUTFIELD, internalName, "engineTexDelegate", "Lrolflectionlib/inheritor/enginetex/EngineTexDelegate;");
        
        setDelegate.visitInsn(RETURN);
        setDelegate.visitMaxs(0, 0);
        setDelegate.visitEnd();

        // implement EngineTexInterface.getDelegate()
        // public EngineTexDelegate getDelegate() {
        //     return this.engineTexDelegate;
        // }
        MethodVisitor getDelegate = cw.visitMethod(ACC_PUBLIC, "getDelegate", "()Lrolflectionlib/inheritor/enginetex/EngineTexDelegate;", null, null);
        getDelegate.visitCode();
        
        // return this.engineTexDelegate
        getDelegate.visitVarInsn(ALOAD, 0); // Load this
        getDelegate.visitFieldInsn(GETFIELD, internalName, "engineTexDelegate", "Lrolflectionlib/inheritor/enginetex/EngineTexDelegate;"); // Load engineTexDelegate field
        getDelegate.visitInsn(ARETURN); // Return the delegate
        getDelegate.visitMaxs(0, 0);
        getDelegate.visitEnd();

        cw.visitEnd();
        byte[] bytes = cw.toByteArray();

        return (Class<?>) RolfLectionUtil.getMethodDeclaredAndInvokeDirectly("define", new ClassLoader(parent) {
            Class<?> define(byte[] b) {
                return defineClass("com.fs.graphics.EngineTex", b, 0, b.length);
            }
        },
        1,
        bytes);
    }
}