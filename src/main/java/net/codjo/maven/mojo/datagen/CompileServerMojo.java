package net.codjo.maven.mojo.datagen;
import java.io.File;
/**
 * Goal pour la g�n�ration datagen.
 *
 * @goal compile-server
 * @phase compile
 * @requiresDependencyResolution
 */
public class CompileServerMojo extends AbstractCompilerMojo {

    protected File getClassesDirectory() {
        return new File(PathUtil.toUnixLikePath(buildDirectory.getAbsolutePath()), "/server-classes");
    }


    protected File getSourcesDirectory() {
        return path.getServerSource(project);
    }


    protected File getResourceDirectory() {
        return path.getServerResource(project);
    }


    protected String getClassifier() {
        return "server";
    }
}
