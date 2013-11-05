package net.codjo.maven.mojo.datagen;
import java.io.File;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.project.MavenProject;
/**
 *
 */
public abstract class AbstractDatagenMojo extends AbstractMojo {
    /**
     * @parameter
     */
    protected Path path = new Path();
    /**
     * @parameter expression="${project}"
     * @required
     * @readonly
     * @noinspection UNUSED_SYMBOL
     */
    protected MavenProject project;
    /**
     * Directory containing the classes.
     *
     * @parameter default-value="${project.build.directory}"
     */
    protected File buildDirectory = new File("target");

    /**
     * @parameter expression="${maven.datagen.skip}" default-value="false"
     * @noinspection UNUSED_SYMBOL,UnusedDeclaration
     */
    protected boolean skipDatagen;

    /**
     * @parameter expression="${net.codjo.datagen.legacyMode}" default-value="false"
     */
    protected boolean legacyMode;

    /**
     * @parameter expression="${net.codjo.datagen.legacyPrefix}"
     */
    protected String legacyPrefix = null;

    /**
     * Paramètres JVM pour la génération
     *
     * @parameter expression="${vmArguments}"
     */
    protected String vmArguments = null;


    static File getOneGeneratedFile(File srcDirectory, String extension) {
        String[] extensions = new String[1];
        extensions[0] = extension;
        return getOneGeneratedFile(srcDirectory, extensions);
    }


    static File getOneGeneratedFile(File srcDirectory, String[] extensions) {
        if (srcDirectory == null) {
            return null;
        }
        for (int i = 0; i < extensions.length; i++) {
            if (srcDirectory.isFile() && srcDirectory.getPath().endsWith("." + extensions[i])) {
                return srcDirectory;
            }
        }
        if (srcDirectory.isDirectory()) {
            File[] files = srcDirectory.listFiles();
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                File generatedJavaFile = getOneGeneratedFile(file, extensions);
                if (generatedJavaFile != null) {
                    return generatedJavaFile;
                }
            }
        }
        return null;
    }


    protected String toDatagenArgument(String[] args) {
        StringBuffer arguments = new StringBuffer();
        for (int i = 0; i < args.length; i++) {
            String string = args[i];
            if (string != null) {
                string = string.replaceAll(" ", "");
            }
            arguments.append(string).append(" ");
        }
        return arguments.toString();
    }


    protected void manageLegacyMode() {
        if (legacyMode) {
            vmArguments = vmArguments + " -Dnet.codjo.datagen.legacyMode=true";
        }
    }


    protected void manageLegacyPrefix() {
        if (legacyPrefix != null) {
            vmArguments = vmArguments + " -Dnet.codjo.datagen.legacyPrefix=" + legacyPrefix;
        }
    }
}
