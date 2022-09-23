package com.antwerkz.codegen.maven.gizmo;

import com.antwerkz.codegen.maven.Generator;
import org.apache.maven.project.MavenProject;

public class Gizmo implements Generator {
    private MavenProject project;

    public Gizmo(MavenProject project) {
        this.project = project;
    }

    @Override
    public void generate() {

    }
}
