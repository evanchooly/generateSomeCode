package com.antwerkz.codegen.maven;

import com.antwerkz.codegen.javapoet.Javapoet;
import com.antwerkz.codegen.maven.gizmo.Gizmo;
import com.antwerkz.codegen.roaster.Roaster;
import org.apache.maven.project.MavenProject;

public enum GeneratorType {
    roaster {
        @Override
        public Generator create(MavenProject project) {
            return new Roaster(project);
        }
    },
    javapoet {
        @Override
        public Generator create(MavenProject project) {
            return new Javapoet(project);
        }
    },
    gizmo {
        @Override
        public Generator create(MavenProject project) {
            return new Gizmo(project);
        }
    };

    public abstract Generator create(MavenProject project);
}
