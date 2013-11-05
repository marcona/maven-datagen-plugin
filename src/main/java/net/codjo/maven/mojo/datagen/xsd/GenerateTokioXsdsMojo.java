package net.codjo.maven.mojo.datagen.xsd;
import net.codjo.maven.mojo.datagen.AbstractDatagenMojo;
import net.codjo.maven.mojo.datagen.JavaExecutor;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
/**
 * @goal generate-tokio-xsds
 */
public class GenerateTokioXsdsMojo extends AbstractDatagenMojo {
    private static final String DATAGEN_PLUGIN_KEY = "net.codjo.maven.mojo:maven-datagen-plugin";

    /**
     * @parameter expression="${datagenFinalFile}" default-value="${project.build.directory}/final.xml"
     * @required
     * @noinspection UNUSED_SYMBOL
     */
    private File datagenFinalFile;

    /**
     * @parameter expression="${outputDirectory}" default-value="C:/dev/platform/cache/xsd"
     * @required
     * @noinspection UNUSED_SYMBOL
     */
    private File outputDirectory;


    public File getDatagenFinalFile() {
        return datagenFinalFile;
    }


    public void setDatagenFinalFile(File datagenFinalFile) {
        this.datagenFinalFile = datagenFinalFile;
    }


    public File getOutputDirectory() {
        return outputDirectory;
    }


    public void setOutputDirectory(File outputDirectory) {
        this.outputDirectory = outputDirectory;
    }


    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            if (!isDatagenPluginConfigured()) {
                return;
            }

            if (!datagenFinalFile.exists()) {
                getLog().info(
                      "Aucune generation car aucun fichier datagen : " + datagenFinalFile.getAbsolutePath());
                return;
            }

            URL[] urls = ((URLClassLoader)getClass().getClassLoader()).getURLs();
            File[] dependencies = new File[urls.length];
            for (int i = 0; i < urls.length; i++) {
                dependencies[i] = new File(urls[i].getFile());
            }

            String arguments = toDatagenArgument(new String[]{project.getArtifactId(),
                                                              datagenFinalFile.getPath(),
                                                              outputDirectory.getPath()});

            manageLegacyMode();
            manageLegacyPrefix();

            JavaExecutor executor = new JavaExecutor();
            executor.setJvmArg(vmArguments);
            executor.execute(xsd.GenerateTokioXsdsMain.class.getCanonicalName(), dependencies, arguments);
        }
        catch (Exception e) {
            throw new MojoExecutionException("Generation en erreur : " + e.getMessage(), e);
        }
    }


    private boolean isDatagenPluginConfigured() {
        return project.getPluginArtifactMap().containsKey(DATAGEN_PLUGIN_KEY);
    }
}
