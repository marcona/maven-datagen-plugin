package net.codjo.maven.mojo.datagen;
import java.io.File;
/**
 * Goal pour créer le jar utilisé par le serveur.
 *
 * @goal package-server
 * @phase package
 */
public class PackageServerMojo extends AbstractPackagerMojo {

    protected String getClassifier() {
        return "server";
    }


    protected File getClassesDirectory() {
        return new File(buildDirectory, "/server-classes");
    }
}
