/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.maven.mojo.datagen;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import kernel.Main;
import net.codjo.maven.common.artifact.ArtifactDescriptor;
import net.codjo.maven.common.artifact.ArtifactGetter;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.manager.WagonManager;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.metadata.RepositoryMetadataManager;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.wagon.ResourceDoesNotExistException;
import org.apache.maven.wagon.TransferFailedException;
import org.codehaus.plexus.util.FileUtils;
/**
 * Goal pour la génération datagen.
 *
 * @goal generate
 * @phase generate-sources
 */
public class GenerateMojo extends AbstractDatagenMojo {

    /**
     * Database type (sybase, oracle, mysql).
     *
     * @parameter expression="${databaseType}" default-value="sybase"
     * @noinspection UNUSED_SYMBOL, UnusedDeclaration
     */
    private String databaseType;
    /**
     * Type de generator a activer (e.g. : "JAVA, SQL, CONFIGURATION" ).
     *
     * @parameter
     * @noinspection UNUSED_SYMBOL, UnusedDeclaration
     */
    private String activatedGeneratorTypes = Main.ALL_GENERATOR;
    /**
     * Derived Artifacts List.
     *
     * @parameter default-value="${project.basedir}/src/datagen/DatagenDef.xml"
     * @noinspection UNUSED_SYMBOL, UnusedDeclaration
     */
    private String datagenFile;
    /**
     * Derived Artifacts List.
     *
     * @parameter default-value="${project.basedir}/src/datagen/datagen.properties"
     * @noinspection UNUSED_SYMBOL, UnusedDeclaration
     */
    private String datagenProperties;
    /**
     * Liste des includes.
     *
     * @parameter
     * @noinspection UNUSED_SYMBOL, UnusedDeclaration
     */
    private ArtifactDescriptor[] includes;
    /**
     * @parameter expression="${component.org.apache.maven.artifact.factory.ArtifactFactory}"
     * @required
     * @readonly
     */
    protected ArtifactFactory artifactFactory;
    /**
     * @parameter expression="${component.org.apache.maven.artifact.manager.WagonManager}"
     * @required
     * @readonly
     * @noinspection UNUSED_SYMBOL, UnusedDeclaration
     */
    private WagonManager wagonManager;
    /**
     * @parameter expression="${localRepository}"
     * @required
     * @readonly
     */
    protected ArtifactRepository localRepository;
    /**
     * @parameter expression="${component.org.apache.maven.artifact.repository.metadata.RepositoryMetadataManager}"
     * @required
     * @readonly
     * @noinspection UNUSED_SYMBOL, UnusedDeclaration
     */
    private RepositoryMetadataManager repositoryMetadataManager;


    public void execute() throws MojoExecutionException, MojoFailureException {
        if (skipDatagen) {
            getLog().info("##### Generation DATAGEN : skippée");
            return;
        }

        try {
            getLog().info("##### Generation DATAGEN (" + databaseType + ")");
            if (!new File(PathUtil.toUnixLikePath(datagenFile)).exists()) {
                getLog().info("Aucune generation car aucun fichier datagen : " + datagenFile);
                return;
            }

            String datagenFileContents = loadContentsFromDatagenFile();

            File normalizedBuildDirectory = new File(PathUtil.toUnixLikePath(buildDirectory.getAbsolutePath()));
            if (!normalizedBuildDirectory.exists()) {
                normalizedBuildDirectory.mkdir();
            }

            if (includes != null) {
                datagenFileContents = addIncludeArtifact(datagenFileContents);
            }

            datagenFileContents = addConfigurationNode(datagenFileContents);
            datagenFileContents = replaceBasedir(datagenFileContents);

            String generatedDatagenFile =
                  normalizedBuildDirectory.getAbsolutePath() + "/DatagenDef.xml";
            writeFile(generatedDatagenFile, datagenFileContents);

            String finalFilename = normalizedBuildDirectory + File.separator + "final.xml";

            URL[] urls = ((URLClassLoader)getClass().getClassLoader()).getURLs();
            File[] dependencies = new File[urls.length];
            for (int i = 0; i < urls.length; i++) {
                dependencies[i] = new File(urls[i].getFile());
            }

            String arguments = toDatagenArgument(new String[]{generatedDatagenFile,
                                                              finalFilename,
                                                              datagenProperties,
                                                              databaseType,
                                                              activatedGeneratorTypes});

            manageLegacyMode();
            manageLegacyPrefix();

            JavaExecutor executor = new JavaExecutor();
            executor.setJvmArg(vmArguments);
            executor.execute(kernel.Main.class.getCanonicalName(), dependencies, arguments);

            project.addCompileSourceRoot(path.getServerSource(project).getPath());
            project.addResource(createResource(path.getServerResource(project)));

            project.addCompileSourceRoot(path.getClientSource(project).getPath());
            project.addResource(createResource(path.getClientResource(project)));
        }
        catch (MojoExecutionException e) {
            throw e;
        }
        catch (Exception e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }


    private String addConfigurationNode(String datagenFileContents) throws MojoExecutionException {
        Pattern pattern = Pattern.compile(".*(<data[^>]*).*",
                                          Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(datagenFileContents);
        if (!matcher.matches()) {
            throw new MojoExecutionException(
                  "Fichier datagen incorrecte (impossible de trouver la balise <data>");
        }

        int inDataNodeIndex = matcher.end(1) + 1;

        return datagenFileContents.substring(0, inDataNodeIndex)
               + "<configuration>"
               + " <project>"
               + "    <name>" + project.getName() + "</name>"
               + " </project>"
               + " <path>"
               + "    <sql>" + path.getSqlTable(project) + "</sql>"
               + "    <sql-view>" + path.getSqlView(project) + "</sql-view>"
               + "    <sql-index>" + path.getSqlIndex(project) + "</sql-index>"
               + "    <sql-constraint>" + path.getSqlConstraint(project) + "</sql-constraint>"
               + "    <trigger>" + path.getSqlTrigger(project) + "</trigger>"
               + "    <requetor>" + path.getClientResource(project) + "\\conf</requetor>"
               + "    <bean>" + path.getServerSource(project) + "</bean>"
               + "    <cmdHandler>" + path.getServerSource(project) + "</cmdHandler>"
               + "    <handlers>" + path.getServerSource(project) + "</handlers>"
               + "    <castor>" + path.getServerResource(project) + "\\conf</castor>"
               + "    <structure>" + path.getServerResource(project) + "\\conf</structure>"
               + "    <referential>" + path.getClientResource(project) + "\\conf</referential>"
               + "    <globs>" + path.getClientSource(project) + "</globs>"
               + " </path>"
               + "</configuration>"
               + datagenFileContents.substring(inDataNodeIndex, datagenFileContents.length());
    }


    private String replaceBasedir(String datagenFileContents) {
        datagenFileContents =
              datagenFileContents.replaceAll("@basedir@",
                                             project.getBasedir().getAbsolutePath());
        return PathUtil.toUnixLikePath(datagenFileContents);
    }


    private void writeFile(String generatedDatagenFile, String datagenFileContents) {
        try {
            FileUtils.fileWrite(generatedDatagenFile, datagenFileContents);
        }
        catch (IOException e) {
            getLog().warn(e);
        }
    }


    private String loadContentsFromDatagenFile() throws IOException {
        return FileUtils.fileRead(new File(PathUtil.toUnixLikePath(datagenFile)));
    }


    private String addIncludeArtifact(String datagenFileContents)
          throws ResourceDoesNotExistException, TransferFailedException, ArtifactNotFoundException,
                 ArtifactResolutionException {
        StringBuffer buffer = new StringBuffer(datagenFileContents);

        ArtifactGetter artifactGetter =
              new ArtifactGetter(artifactFactory,
                                 localRepository,
                                 project.getRemoteArtifactRepositories(),
                                 wagonManager,
                                 repositoryMetadataManager);

        for (int i = 0; i < includes.length; i++) {
            ArtifactDescriptor include = includes[i];
            include.resolveType("xml");
            include.resolveIncludeVersion(project.getDependencyManagement());

            Artifact artifact = artifactGetter.getArtifact(include);

            appendArtifactPathToDatagenContents(artifact, buffer);
        }
        return buffer.toString();
    }


    private void appendArtifactPathToDatagenContents(Artifact artifact,
                                                     StringBuffer datagenFileContents) {
        int index = datagenFileContents.indexOf("</data>");
        String toAppend = "<include name=\"" + artifact.getFile().getPath() + "\"/>\n";
        datagenFileContents.insert(index, toAppend);
    }


    private static Resource createResource(File path) {
        Resource serverResource = new Resource();
        serverResource.setDirectory(path.getPath());
        return serverResource;
    }
}
