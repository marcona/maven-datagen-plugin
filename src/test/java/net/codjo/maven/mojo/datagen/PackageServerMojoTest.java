/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.maven.mojo.datagen;
/**
 * Classe de test {@link PackageServerMojo}.
 */
public class PackageServerMojoTest extends AbstractPackagerMojoTestCase {

    protected void setUp() throws Exception {
        super.setUp();
        targetClassesDir = "server-classes";
        setupEnvironment("packagingServer/packagingServer-pom.xml", "package-server");
        classFile = getGeneratedClassFile(targetClassesDir, "MyHandler.class");
        resourceFile = getGeneratedClassFile(targetClassesDir, "conf/role.xml");
    }
}
