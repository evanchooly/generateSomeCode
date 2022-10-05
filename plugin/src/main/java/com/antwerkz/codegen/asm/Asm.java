package com.antwerkz.codegen.asm;

import com.antwerkz.codegen.maven.Generator;
import org.apache.maven.project.MavenProject;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static org.objectweb.asm.ClassWriter.COMPUTE_FRAMES;
import static org.objectweb.asm.ClassWriter.COMPUTE_MAXS;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_SUPER;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ARETURN;
import static org.objectweb.asm.Opcodes.GETSTATIC;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.RETURN;
import static org.objectweb.asm.Opcodes.V17;

public class Asm implements Generator {
    private MavenProject project;

    public Asm(MavenProject project) {
        this.project = project;
    }

    @Override
    public void generate() {
        ClassWriter classWriter = new ClassWriter(COMPUTE_MAXS | COMPUTE_FRAMES);

        classWriter.visit(V17, ACC_PUBLIC | ACC_SUPER, "com/antwerkz/generated/HelloWorld", null, "java/lang/Object", null);

        classWriter
            .visitAnnotation("Ljdk/jfr/Enabled;", true)
            .visitEnd();

        constructor(classWriter);

        greet(classWriter);

        greeting(classWriter);

        classWriter.visitEnd();

        dumpClassFile(classWriter);
    }

    private void dumpClassFile(ClassWriter classWriter) {
        File file = new File(project.getBasedir(), "target/classes/com/antwerkz/generated/HelloWorld.class");
        file.getParentFile().mkdirs();
        try (var stream = new FileOutputStream(file)) {
            stream.write(classWriter.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private void constructor(ClassWriter classWriter) {
        MethodVisitor mv = classWriter.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
        mv.visitCode();

        Label label0 = new Label();
        mv.visitLabel(label0);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
        mv.visitInsn(RETURN);

        Label label1 = new Label();
        mv.visitLabel(label1);
        mv.visitLocalVariable("this", "Lcom/antwerkz/generated/HelloWorld;", null, label0, label1, 0);
        mv.visitMaxs(1, 1);
        mv.visitEnd();
    }

    private void greet(ClassWriter classWriter) {
        MethodVisitor mv = classWriter.visitMethod(ACC_PUBLIC, "greet", "()V", null, null);

        AnnotationVisitor av = mv.visitAnnotation("Ljdk/jfr/Name;", true);
        av.visit("value", "Inigo Montoya");
        av.visitEnd();

        mv.visitCode();

        Label label0 = new Label();
        mv.visitLabel(label0);
        mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKEVIRTUAL, "com/antwerkz/generated/HelloWorld", "greeting", "()Ljava/lang/String;", false);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);

        Label label1 = new Label();
        mv.visitLabel(label1);
        mv.visitInsn(RETURN);

        Label label2 = new Label();
        mv.visitLabel(label2);

        mv.visitLocalVariable("this", "Lcom/antwerkz/generated/HelloWorld;", null, label0, label2, 0);
        mv.visitMaxs(2, 1);
        mv.visitEnd();
    }

    private void greeting(ClassWriter classWriter) {
        MethodVisitor mv = classWriter.visitMethod(ACC_PUBLIC, "greeting", "()Ljava/lang/String;", null, null);
        mv.visitCode();

        Label label0 = new Label();
        mv.visitLabel(label0);
        mv.visitLdcInsn("Hello world!");
        mv.visitInsn(ARETURN);

        Label label1 = new Label();
        mv.visitLabel(label1);
        mv.visitLocalVariable("this", "Lcom/antwerkz/generated/HelloWorld;", null, label0, label1, 0);

        mv.visitMaxs(1, 1);
        mv.visitEnd();
    }
}
