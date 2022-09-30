package com.antwerkz.codegen.maven.gizmo;

import com.antwerkz.codegen.maven.Generator;
import io.quarkus.gizmo.ClassCreator;
import io.quarkus.gizmo.ClassOutput;
import io.quarkus.gizmo.FieldDescriptor;
import io.quarkus.gizmo.MethodCreator;
import io.quarkus.gizmo.MethodDescriptor;
import io.quarkus.gizmo.ResultHandle;
import jdk.jfr.Enabled;
import jdk.jfr.Name;
import org.apache.maven.project.MavenProject;
import org.jboss.jandex.AnnotationInstance;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Modifier;

import static io.quarkus.gizmo.MethodDescriptor.ofMethod;

public class Gizmo implements Generator {
    private MavenProject project;

    public Gizmo(MavenProject project) {
        this.project = project;
    }

    @Override
    public void generate() {
        try (ClassCreator classCreator = ClassCreator.builder()
                                                     .className("com.antwerkz.generated.HelloWorld")
                                                     .classOutput(new DemoClassOutput())
                                                     .build()) {
            classCreator.addAnnotation(Enabled.class);
            classCreator.addAnnotation(AnnotationInstance.builder(SuppressWarnings.class)
                                           .value("all of it")
                                           .build());

            greeting(classCreator);

            greet(classCreator);
        }
    }

    private void greeting(ClassCreator classCreator) {
        try (MethodCreator methodCreator = classCreator.getMethodCreator("greeting", String.class)) {
            methodCreator.setModifiers(Modifier.PUBLIC);
            methodCreator.returnValue(methodCreator.load("Hello world!"));

        }
    }

    private void greet(ClassCreator classCreator) {
        try (MethodCreator methodCreator = classCreator.getMethodCreator("greet", void.class)) {
            AnnotationInstance annotation = AnnotationInstance.builder(Name.class)
                                                              .value("Inigo Montoya")
                                                              .build();
            methodCreator.setModifiers(Modifier.PUBLIC);
            methodCreator.addAnnotation(annotation);

            // call greeting()
            MethodDescriptor greeting = ofMethod("com.antwerkz.generated.HelloWorld", "greeting", String.class);
            System.out.println("greeting = " + greeting);
            var greetingCall = methodCreator.invokeVirtualMethod(greeting, methodCreator.getThis());


            // print it
            FieldDescriptor out = FieldDescriptor.of(System.class, "out", PrintStream.class);
            System.out.println("out = " + out);

            var field = methodCreator.readStaticField(out);
            System.out.println("field = " + field);
            MethodDescriptor println = ofMethod(PrintStream.class, "println", void.class, String.class);

            System.out.println("println = " + println);

            methodCreator.invokeVirtualMethod(println, field, greetingCall);

            methodCreator.returnValue(methodCreator.loadNull());
        }
    }

    private static class DemoClassOutput implements ClassOutput {
        @Override
        public void write(String s, byte[] bytes) {
            File file = new File("target/classes", s + ".class");
            file.getParentFile().mkdirs();
            try (FileOutputStream stream = new FileOutputStream(file)) {
                stream.write(bytes);
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }
    }
}
