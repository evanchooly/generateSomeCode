package com.antwerkz.codegen.roaster;

import com.antwerkz.codegen.maven.Generator;
import org.apache.maven.project.MavenProject;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.forge.roaster.model.source.JavaDocSource;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;

import static java.lang.String.format;
import static org.jboss.forge.roaster.Roaster.create;

public class Roaster implements Generator {
    private JavaClassSource builder;

    private File generated;

    private MavenProject project;

    public Roaster(MavenProject project) {
        this.project = project;
    }

    @Override
    public void generate() {
        generated = new File(project.getBasedir() + "/target/generated-sources/codegen/");

        project.addCompileSourceRoot(generated.getAbsolutePath());

        emitHelloWorld(new File(generated, "com/antwerkz/generated/HelloWorld.java"));
    }

    private void emitHelloWorld(File outputFile) {
        builder = create(JavaClassSource.class)
                      .setName("HelloWorld")
                      .setPackage("com.antwerkz.generated");

        builder.addImport(Objects.class);
        builder.addImport(SuppressWarnings.class);

        builder.addAnnotation("jdk.jfr.Enabled");
        builder.addAnnotation("SuppressWarnings")  // can also use String name
            .setStringArrayValue("value", new String[]{"all of it"});


        JavaDocSource<JavaClassSource> javaDoc = builder.getJavaDoc();
        javaDoc.addTagValue("@since", "forever");

        builder.addMethod()
               .setPublic()
               .setName("greet")
               .setReturnType(void.class)
               .setBody("""
                   System.out.println(greeting());
                   """)
            .addAnnotation("jdk.jfr.Name")
            .setStringValue("value", "Inigo Montoya");

        builder.addMethod()
               .setPublic()
               .setName("greeting")
               .setReturnType(String.class)
               .setBody("""
                   return "Hello world!";
                   """);

        output(outputFile);
    }

    private void output(File outputFile) {
        if (!outputFile.getParentFile().mkdirs() && !outputFile.getParentFile().exists()) {
            throw new RuntimeException(format("Could not create directory: %s", outputFile.getParentFile()));
        }
        try (var writer = new FileWriter(outputFile)) {
            writer.write(builder.toString());
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}