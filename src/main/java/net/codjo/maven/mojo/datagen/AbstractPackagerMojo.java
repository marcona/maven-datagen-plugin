package net.codjo.maven.mojo.datagen;
import java.io.File;
import org.apache.maven.archiver.MavenArchiveConfiguration;
import org.apache.maven.archiver.MavenArchiver;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProjectHelper;
import org.codehaus.plexus.archiver.jar.JarArchiver;
/**
 *
 */
public abstract class AbstractPackagerMojo extends AbstractDatagenMojo {
    private static final String[] DEFAULT_EXCLUDES = new String[]{"**/package.html", "**/.svn/**"};
    private static final String[] DEFAULT_INCLUDES = new String[]{"**/**"};

    /**
     * @component
     * @readonly
     * @noinspection UNUSED_SYMBOL, UnusedDeclaration
     */
    private MavenProjectHelper projectHelper;

    /**
     * @parameter expression="${component.org.codehaus.plexus.archiver.Archiver#jar}"
     * @required
     * @noinspection UNUSED_SYMBOL, UnusedDeclaration
     */
    private JarArchiver jarArchiver;
    private MavenArchiveConfiguration archive = new MavenArchiveConfiguration();


    protected abstract String getClassifier();


    protected abstract File getClassesDirectory();


    public void execute() throws MojoExecutionException, MojoFailureException {
        if (skipDatagen) {
            return;
        }

        if (!getClassesDirectory().exists()) {
            getLog().info("\t -> pas de classe '" + getClassifier() + "'");
            return;
        }

        if (!getClassHasBeenCompiledFile().exists()) {
            getLog().info("\t -> creation du jar '" + getClassifier() + "' non necessaire.");
            return;
        }

        createArchive(getJarFile());

        projectHelper.attachArtifact(project, "jar", getClassifier(), getJarFile());
    }


    public void createArchive(File jarFile) throws MojoExecutionException {
        MavenArchiver archiver = new MavenArchiver();
        archiver.setArchiver(jarArchiver);
        archiver.setOutputFile(jarFile);

        try {
            archiver.getArchiver().addDirectory(getClassesDirectory(), DEFAULT_INCLUDES, DEFAULT_EXCLUDES);

            archiver.createArchive(project, archive);
        }
        catch (Exception e) {
            throw new MojoExecutionException("Error assembling JAR", e);
        }
    }


    private File getJarFile() {
        return getJarFile(buildDirectory, project.getBuild().getFinalName(), getClassifier());
    }


    protected static File getJarFile(File basedir, String finalName, String classifier) {
        return new File(PathUtil.toUnixLikePath(basedir.getAbsolutePath()),
                        PathUtil.toUnixLikePath(finalName + "-" + classifier + ".jar"));
    }


    protected File getClassHasBeenCompiledFile() {
        return PathUtil.classHasBeenCompiledFile(buildDirectory, getClassesDirectory());
    }
}
