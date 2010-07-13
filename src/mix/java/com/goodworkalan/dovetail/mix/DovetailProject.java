package com.goodworkalan.dovetail.mix;

import com.goodworkalan.mix.ProjectModule;
import com.goodworkalan.mix.builder.Builder;
import com.goodworkalan.mix.cookbook.JavaProject;

/**
 * Builds the project definition for Dovetail.
 *
 * @author Alan Gutierrez
 */
public class DovetailProject implements ProjectModule {
    /**
     * Build the project definition for Dovetail.
     *
     * @param builder
     *          The project builder.
     */
    public void build(Builder builder) {
        builder
            .cookbook(JavaProject.class)
                .produces("com.github.bigeasy.dovetail/dovetail/0.7.0.1")
                .depends()
                    .production("com.github.bigeasy.danger/danger/0.+3")
                    .development("org.testng/testng-jdk15/5.10")
                    .end()
                .end()
            .end();
    }
}
