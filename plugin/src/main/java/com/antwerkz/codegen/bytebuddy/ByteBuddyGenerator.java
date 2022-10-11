package com.antwerkz.codegen.bytebuddy;

import com.antwerkz.codegen.maven.Generator;
import jdk.jfr.Enabled;
import jdk.jfr.Name;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.implementation.FixedValue;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.annotation.Annotation;

import static java.lang.reflect.Modifier.PUBLIC;
import static net.bytebuddy.implementation.MethodCall.invoke;
import static net.bytebuddy.matcher.ElementMatchers.named;

public class ByteBuddyGenerator implements Generator {

    private MavenProject project;

    public ByteBuddyGenerator(MavenProject project) {
        System.out.println("Generating using ByteBuddy");
        this.project = project;
    }

    private static boolean matches(MethodDescription target) {
        return target.getName().equals("greeting");
    }

    @Override
    public void generate() {
        try {
            var helloworld = new ByteBuddy()
                                 .subclass(Object.class)
                                 .name("com.antwerkz.generated.HelloWorld")
                                 .annotateType(new FunctionalInterfaceAnnotation(), new EnabledAnnotation())

                                 .defineMethod("greeting", String.class, PUBLIC)
                                 .intercept(FixedValue.value("Hello world!"))

                                 .defineMethod("greet", void.class, PUBLIC)
                                 .intercept(invoke(PrintStream.class.getDeclaredMethod("println", Object.class))
                                                .onField(System.class.getDeclaredField("out"))
                                                .withMethodCall(invoke(named("greeting")))
                                           )
                                 .annotateMethod(new NameAnnotation());

            write(helloworld.make().getBytes());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void write(byte[] bytes) {
        File file = new File(project.getBasedir(), "target/classes/com/antwerkz/generated/HelloWorld.class");
        System.out.println("Writing to " + file.getAbsolutePath());
        file.getParentFile().mkdirs();
        try (FileOutputStream stream = new FileOutputStream(file)) {
            stream.write(bytes);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private static class EnabledAnnotation implements Enabled {
        @Override
        public Class<? extends Annotation> annotationType() {
            return Enabled.class;
        }

        @Override
        public boolean value() {
            return false;
        }
    }

    private static class NameAnnotation implements Name {
        @Override
        public Class<? extends Annotation> annotationType() {
            return Name.class;
        }

        @Override
        public String value() {
            return "Inigo Montoya";
        }
    }

    private static class FunctionalInterfaceAnnotation implements FunctionalInterface {
        @Override
        public Class<? extends Annotation> annotationType() {
            return FunctionalInterface.class;
        }
    }
}
