package com.goodworkalan.dovetail;

import com.goodworkalan.cafe.ProjectModule;
import com.goodworkalan.cafe.builder.Builder;
import com.goodworkalan.cafe.outline.JavaProject;

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
                .produces("com.github.bigeasy.dovetail/dovetail/0.7.0.2")
                .depends()
                    .production("com.github.bigeasy.danger/danger/0.+3")
                    .development("org.testng/testng-jdk15/5.10")
                    .end()
                .end()
            .end();
    }
}
