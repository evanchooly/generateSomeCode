package com.antwerkz.codegen.bytebuddy;

import com.antwerkz.codegen.maven.Generator;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.scaffold.InstrumentedType;
import net.bytebuddy.implementation.DefaultMethodCall;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.Implementation.Compound;
import net.bytebuddy.implementation.MethodCall;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.List;

import static java.lang.reflect.Modifier.PUBLIC;

public class ByteBuddyGenerator implements Generator {

    private MavenProject project;

    public ByteBuddyGenerator(MavenProject project) {
        this.project = project;
    }

    @Override
    public void generate() {
        var helloworld = new ByteBuddy()
                             .subclass(Object.class)
                             .name("com.antwerkz.generated.HelloWorld")
            /*.make()*/;
        helloworld = helloworld.defineMethod("greeting", String.class, PUBLIC)
                      .intercept(FixedValue.value("Hello world!"));

        helloworld = helloworld.defineMethod("greet", void.class, PUBLIC)
                               .intercept(new Compound(new MethodCall()));

        write(helloworld.make().getBytes());
    }

    private void write(byte[] bytes) {
        File file = new File("target/classes", "com/antwerkz/generated/HelloWorld.class");
        file.getParentFile().mkdirs();
        try (FileOutputStream stream = new FileOutputStream(file)) {
            stream.write(bytes);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
