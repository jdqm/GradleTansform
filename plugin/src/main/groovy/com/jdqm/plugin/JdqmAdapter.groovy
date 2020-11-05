package com.jdqm.plugin

import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Type
import org.objectweb.asm.commons.AdviceAdapter


class JdqmAdapter extends AdviceAdapter {
    private int mStartTimeId = -1

    private String mClassName
    private String mMethodName

    JdqmAdapter(int api, MethodVisitor mv, int access, String name, String desc, String fileName) {
        super(api, mv, access, name, desc)
        mMethodName = name
        mClassName = fileName
    }

    @Override
    protected void onMethodEnter() {
        super.onMethodEnter()
        mStartTimeId = newLocal(Type.LONG_TYPE)
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J", false)
        mv.visitIntInsn(LSTORE, mStartTimeId)
    }

    @Override
    protected void onMethodExit(int opcode) {
        super.onMethodExit(opcode)
        int durationId = newLocal(Type.LONG_TYPE)
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J", false)
        mv.visitVarInsn(LLOAD, mStartTimeId)
        mv.visitInsn(LSUB)
        mv.visitVarInsn(LSTORE, durationId)
        mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;")
        mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
        mv.visitInsn(DUP)
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false)
        mv.visitLdcInsn("The cost time of " + mClassName + "#" + mMethodName + "() is ")
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false)
        mv.visitVarInsn(LLOAD, durationId)
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(J)Ljava/lang/StringBuilder;", false)
        mv.visitLdcInsn(" ms")
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false)
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false)
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false)
    }
}