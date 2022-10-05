package com.antwerkz.codegen.javapoet;

import com.antwerkz.codegen.maven.Generator;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeSpec.Builder;
import jdk.jfr.Name;
import org.apache.maven.project.MavenProject;
import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.source.JavaClassSource;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static com.squareup.javapoet.AnnotationSpec.builder;
import static com.squareup.javapoet.MethodSpec.methodBuilder;
import static javax.lang.model.element.Modifier.PUBLIC;

public class Javapoet implements Generator {
    private Builder builder;

    private File generated;

    private MavenProject project;

    public Javapoet(MavenProject project) {
        System.out.println("Generating using Javapoet");
        this.project = project;
    }

    @Override
    public void generate() {
        generated = new File(project.getBasedir() + "/target/generated-sources/codegen/");
        project.addCompileSourceRoot(generated.getAbsolutePath());

        emitHelloWorld(generated);

    }

    private void emitHelloWorld(File outputDirectory) {
        builder = TypeSpec.classBuilder("HelloWorld")
                          .addModifiers(PUBLIC)
                          .addAnnotation(jdk.jfr.Enabled.class)
                          .addAnnotation(builder(ClassName.get(SuppressWarnings.class))
                                             .addMember("value", "{\"all of it\"}")
                                             .build());

        builder.addJavadoc(CodeBlock.of("@since forever"));

        builder.addMethod(methodBuilder("greet")
                              .addModifiers(PUBLIC)
                              .returns(void.class)
                              .addCode("System.out.println(greeting());")
                              .addAnnotation(builder(Name.class)
                                                 .addMember("value", "\"Inigo Montoya\"")
                                                 .build())
                              .build());

        builder.addMethod(methodBuilder("greeting")
                              .addModifiers(PUBLIC)
                              .returns(String.class)
                              .addCode("return \"Hello world!\";")
                              .build());
        buildFile(outputDirectory, builder.build());
    }

    void buildFile(File outputDirectory, TypeSpec typeSpec) {
        String packageName = "com.antwerkz.generated";
        var builder = JavaFile
                          .builder(packageName, typeSpec);

        try {
            builder
                .indent("\t")
//                .skipJavaLangImports(true)
                .build()
                .writeTo(outputDirectory);

            format(outputDirectory, typeSpec, packageName);

        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private void format(File outputDirectory, TypeSpec typeSpec, String packageName) throws IOException {
        var sourceFile = new File(outputDirectory, packageName.replace('.', '/')
                                                   + "/" + typeSpec.name + ".java");
        var parsed = Roaster.parse(JavaClassSource.class, sourceFile);
        try (var writer = new FileWriter(sourceFile)) {
            writer.write(parsed.toString());
        }
    }
}
