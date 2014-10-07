/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.maven.mojo.datagen;
import java.io.File;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Zip;
/**
 * Goal pour créer le jar utilisé par le sql.
 *
 * @goal package-sql
 * @phase package
 */
public class PackageSqlMojo extends AbstractDatagenMojo {
    /**
     * Maven ProjectHelper
     *
     * @component
     * @readonly
     * @noinspection UNUSED_SYMBOL, UnusedDeclaration
     */
    private MavenProjectHelper projectHelper;
    static final String[] SQL_EXTENSIONS = new String[]{"sql", "tab", "txt"};


    public void execute() throws MojoExecutionException, MojoFailureException {
        if (skipDatagen) {
            getLog().info("##### packaging DATAGEN - SQL : skippé");
            return;
        }

        File zipFile = new File(PathUtil.toUnixLikePath(buildDirectory.getAbsolutePath()), buildFilename());
        File sourceDir = path.getSqlDirectory(project);
        File generatedSqlFile = getOneGeneratedFile(sourceDir, SQL_EXTENSIONS);
        if (zipFile.exists()
            && generatedSqlFile.lastModified() <= zipFile.lastModified()) {
            getLog().info("\t -> creation du zip 'sql' non necessaire.");
            return;
        }

        Zip zipTask = new Zip();
        zipTask.setBasedir(sourceDir);
        zipTask.setDestFile(zipFile);
        initAntStuff(project, zipTask);

        zipTask.execute();

        projectHelper.attachArtifact(project, "zip", getClassifier(), zipFile);
    }


    private String buildFilename() {
        StringBuffer filename = new StringBuffer(project.getBuild().getFinalName());
        filename.append('-').append(getClassifier()).append(".zip");
        return filename.toString();
    }


    private String getClassifier() {
        return "sql";
    }


    static void initAntStuff(MavenProject project, Task task) {
        Project ant = new Project();
        ant.setProperty("project.version", project.getVersion());
        ant.setProperty("project.name", project.getName());
        ant.setProperty("project.groupId", project.getGroupId());
        ant.setProperty("project.artifactId", project.getArtifactId());

        ant.setBaseDir(project.getBasedir());
        task.setProject(ant);
        ant.init();
    }
}
