package com.jdqm.plugin

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

class JdqmVisitor extends ClassVisitor {
    private String mFileName

    JdqmVisitor(ClassVisitor cv, String fileName) {
        super(Opcodes.ASM5, cv)
        mFileName = fileName
    }

    @Override
    MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor methodVisitor = super.visitMethod(access, name, desc, signature, exceptions)
        if (name == "onCreate") {
            return new JdqmAdapter(Opcodes.ASM5, methodVisitor, access, name, desc, mFileName)
        }
        return methodVisitor
    }
}