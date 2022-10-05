package com.antwerkz.codegen.asm;

import com.antwerkz.codegen.maven.Generator;
import org.apache.maven.project.MavenProject;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.RecordComponentVisitor;

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
        FieldVisitor fieldVisitor;
        RecordComponentVisitor recordComponentVisitor;
        MethodVisitor methodVisitor;
        AnnotationVisitor annotationVisitor;

        classWriter.visit(V17, ACC_PUBLIC | ACC_SUPER, "com/antwerkz/generated/HelloWorld", null, "java/lang/Object", null);

        classWriter.visitSource("HelloWorld.java", null);

        annotationVisitor = classWriter.visitAnnotation("Ljdk/jfr/Enabled;", true);
        annotationVisitor.visitEnd();

        methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
        methodVisitor.visitCode();
        Label label0 = new Label();
        methodVisitor.visitLabel(label0);
        methodVisitor.visitLineNumber(12, label0);
        methodVisitor.visitVarInsn(ALOAD, 0);
        methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
        methodVisitor.visitInsn(RETURN);
        Label label1 = new Label();
        methodVisitor.visitLabel(label1);
        methodVisitor.visitLocalVariable("this", "Lcom/antwerkz/generated/HelloWorld;", null, label0, label1, 0);
        methodVisitor.visitMaxs(1, 1);
        methodVisitor.visitEnd();

        methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "greet", "()V", null, null);

        annotationVisitor = methodVisitor.visitAnnotation("Ljdk/jfr/Name;", true);
        annotationVisitor.visit("value", "Inigo Montoya");
        annotationVisitor.visitEnd();

        methodVisitor.visitCode();
        label0 = new Label();
        methodVisitor.visitLabel(label0);
        methodVisitor.visitLineNumber(16, label0);
        methodVisitor.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        methodVisitor.visitVarInsn(ALOAD, 0);
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/antwerkz/generated/HelloWorld", "greeting", "()Ljava/lang/String;", false);
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
        label1 = new Label();
        methodVisitor.visitLabel(label1);
        methodVisitor.visitLineNumber(17, label1);
        methodVisitor.visitInsn(RETURN);
        Label label2 = new Label();
        methodVisitor.visitLabel(label2);
        methodVisitor.visitLocalVariable("this", "Lcom/antwerkz/generated/HelloWorld;", null, label0, label2, 0);
        methodVisitor.visitMaxs(2, 1);
        methodVisitor.visitEnd();

        methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "greeting", "()Ljava/lang/String;", null, null);
        methodVisitor.visitCode();
        label0 = new Label();
        methodVisitor.visitLabel(label0);
        methodVisitor.visitLineNumber(20, label0);
        methodVisitor.visitLdcInsn("Hello world!");
        methodVisitor.visitInsn(ARETURN);
        label1 = new Label();
        methodVisitor.visitLabel(label1);
        methodVisitor.visitLocalVariable("this", "Lcom/antwerkz/generated/HelloWorld;", null, label0, label1, 0);
        methodVisitor.visitMaxs(1, 1);
        methodVisitor.visitEnd();

        classWriter.visitEnd();

        File file = new File(project.getBasedir(), "target/classes/com/antwerkz/generated/HelloWorld.class");
        file.getParentFile().mkdirs();
        try (var stream = new FileOutputStream(file)) {
            stream.write(classWriter.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
