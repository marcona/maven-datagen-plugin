package net.codjo.maven.mojo.datagen;
import java.io.File;
/**
 *
 */
public class PathUtil {
    private PathUtil() {
    }


    public static String relativePath(File rootDirectory, File file) {
        String sourceFile = file.getAbsolutePath();
        String sourceDir = rootDirectory.getAbsolutePath();

        return sourceFile.substring(sourceDir.length() + 1, sourceFile.length());
    }


    static File classHasBeenCompiledFile(File buildDirectory, File classesDirectory) {
        return new File(toUnixLikePath(buildDirectory.getAbsolutePath()),
                        classesDirectory.getName() + "-has-been-compiled");
    }


    public static String toUnixLikePath(String path) {
        if (path != null) {
            return path.replace('\\', '/');
        }
        return path;
    }
}
