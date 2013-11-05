package net.codjo.maven.mojo.datagen;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipFile;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.Mojo;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.apache.tools.ant.taskdefs.Zip;
import org.apache.tools.ant.util.FileUtils;
/**
 *
 */
public class PackageSqlMojoTest extends AbstractMojoTestCase {
    public void test_execute_packagingSqlAtFirstGeneration() throws Exception {
        MockUtil util =
              MockUtil.setupEnvironment("packagingSql/packagingSql-pom.xml");

        Mojo mojo = lookupMojo("package-sql", util.getPomFile());

        util.getProject().setGroupId("my-group-id");
        util.getProject().setArtifactId("artifactId");
        util.getProject().setVersion("1.0");
        util.getProject().getBuild().setFinalName("artifactId-1.0");

        File sqlZipFile = new File(util.getTargetDir(), "artifactId-1.0-sql.zip");
        assertFalse(sqlZipFile.exists());

        mojo.execute();

        assertTrue(sqlZipFile.exists());
        assertNotNull(new ZipFile(sqlZipFile).getEntry("index/index.sql"));
        assertNotNull(new ZipFile(sqlZipFile).getEntry("table/table.tab"));

        assertAttachedArtifact(sqlZipFile, util.getProject());
    }


    public void test_execute_zipNotDoneIfNoSQLGeneration() throws Exception {
        MockUtil util =
              MockUtil.setupEnvironment("packagingSql/packagingSql-pom.xml");

        Mojo mojo = lookupMojo("package-sql", util.getPomFile());

        util.getProject().setGroupId("my-group-id");
        util.getProject().setArtifactId("artifactId");
        util.getProject().setVersion("1.0");
        util.getProject().getBuild().setFinalName("artifactId-1.0");

        File sqlZipFile = createGeneratedZipFile(util);
        assertTrue(sqlZipFile.exists());

        File generatedSqlDir = util.getPomFile().getParentFile();
        File generatedSqlFile = AbstractDatagenMojo.getOneGeneratedFile(generatedSqlDir, "sql");
        FileUtils.getFileUtils().setFileLastModified(generatedSqlFile, sqlZipFile.lastModified() - 1);

        mojo.execute();

        assertTrue(sqlZipFile.exists());
        assertNoAttachedArtifact(util.getProject());
    }


    public void test_execute_zipDoneAfterNewerJavaGeneration() throws Exception {
        MockUtil util =
              MockUtil.setupEnvironment("packagingSql/packagingSql-pom.xml");

        Mojo mojo = lookupMojo("package-sql", util.getPomFile());

        util.getProject().setGroupId("my-group-id");
        util.getProject().setArtifactId("artifactId");
        util.getProject().setVersion("1.0");
        util.getProject().getBuild().setFinalName("artifactId-1.0");

        File sqlZipFile = createGeneratedZipFile(util);
        assertTrue(sqlZipFile.exists());

        File generatedSqlDir = util.getPomFile().getParentFile();
        File generatedSqlFile = AbstractDatagenMojo
              .getOneGeneratedFile(generatedSqlDir, PackageSqlMojo.SQL_EXTENSIONS);
        FileUtils.getFileUtils().setFileLastModified(sqlZipFile, generatedSqlFile.lastModified() - 1);

        mojo.execute();

        assertTrue(sqlZipFile.exists());
        assertAttachedArtifact(sqlZipFile, util.getProject());
    }


    private File createGeneratedZipFile(MockUtil util) throws IOException {
        File file = new File(util.getTargetDir(), "artifactId-1.0-sql.zip");

        Zip zipTask = new Zip();
        zipTask.setBasedir(util.getTargetDir().getParentFile());
        zipTask.setDestFile(file);
        PackageSqlMojo.initAntStuff(util.getProject(), zipTask);
        zipTask.execute();

        return file;
    }


    private void assertAttachedArtifact(File file, MavenProjectMock project) {
        List attachedArtifacts = project.getAttachedArtifacts();
        assertEquals(1, attachedArtifacts.size());
        assertEquals(file, ((Artifact)attachedArtifacts.get(0)).getFile().getAbsoluteFile());
    }


    private void assertNoAttachedArtifact(MavenProjectMock project) {
        List attachedArtifacts = project.getAttachedArtifacts();
        assertEquals(0, attachedArtifacts.size());
    }
}
