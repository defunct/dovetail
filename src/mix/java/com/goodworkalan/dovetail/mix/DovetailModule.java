package com.goodworkalan.dovetail.mix;

import com.goodworkalan.go.go.Artifact;
import com.goodworkalan.mix.BasicJavaModule;

public class DovetailModule extends BasicJavaModule {
    public DovetailModule() {
        super(new Artifact("com.goodworkalan", "dovetail", "0.7"));
        addTestDependency(new Artifact("org.testng", "testng", "5.10"));
    }
}
