package com.antwerkz.codegen.maven;

import com.antwerkz.codegen.roaster.Roaster;
import org.apache.maven.project.MavenProject;

public enum GeneratorType {
    roaster {
        @Override
        public Generator create(MavenProject project) {
            return new Roaster(project);
        }
    };

    public abstract Generator create(MavenProject project);
}
