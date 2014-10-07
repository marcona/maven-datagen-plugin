/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.maven.mojo.datagen;
import java.io.File;
/**
 * Goal pour cr�er le jar utilis� par le client.
 *
 * @goal package-client
 * @phase package
 */
public class PackageClientMojo extends AbstractPackagerMojo {

    protected String getClassifier() {
        return "client";
    }


    protected File getClassesDirectory() {
        return new File(PathUtil.toUnixLikePath(buildDirectory.getAbsolutePath()), "/client-classes");
    }
}
