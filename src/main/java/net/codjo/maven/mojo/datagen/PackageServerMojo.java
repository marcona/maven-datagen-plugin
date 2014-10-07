package net.codjo.maven.mojo.datagen;
import java.io.File;
/**
 * Goal pour cr�er le jar utilis� par le serveur.
 *
 * @goal package-server
 * @phase package
 */
public class PackageServerMojo extends AbstractPackagerMojo {

    protected String getClassifier() {
        return "server";
    }


    protected File getClassesDirectory() {
        return new File(PathUtil.toUnixLikePath(buildDirectory.getAbsolutePath()), "/server-classes");
    }
}
