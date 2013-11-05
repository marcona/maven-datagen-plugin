/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.maven.mojo.datagen;
/**
 * Classe de test {@link PackageClientMojo}.
 */
public class PackageClientMojoTest extends AbstractPackagerMojoTestCase {

    protected void setUp() throws Exception {
        super.setUp();
        targetClassesDir = "client-classes";
        setupEnvironment("packagingClient/packagingClient-pom.xml", "package-client");
        classFile = getGeneratedClassFile(targetClassesDir, "MyGlob.class");
        resourceFile = getGeneratedClassFile(targetClassesDir, "conf/preference.xml");
    }
}
