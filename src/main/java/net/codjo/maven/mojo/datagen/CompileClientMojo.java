package net.codjo.maven.mojo.datagen;
import java.io.File;
/**
 * Goal pour la génération datagen.
 *
 * @goal compile-client
 * @phase compile
 * @requiresDependencyResolution
 */
public class CompileClientMojo extends AbstractCompilerMojo {

    protected File getClassesDirectory() {
        return new File(PathUtil.toUnixLikePath(buildDirectory.getAbsolutePath()), "/client-classes");
    }


    protected File getSourcesDirectory() {
        return path.getClientSource(project);
    }


    protected File getResourceDirectory() {
        return path.getClientResource(project);
    }


    protected String getClassifier() {
        return "client";
    }
}
