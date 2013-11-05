package net.codjo.maven.mojo.datagen.xsd;
import net.codjo.maven.common.mock.AgfMojoTestCase;
import net.codjo.test.common.fixture.DirectoryFixture;
import java.io.File;
import org.apache.maven.model.Plugin;

public class GenerateTokioXsdsMojoTest extends AgfMojoTestCase {
    private DirectoryFixture directoryFixture = DirectoryFixture.newTemporaryDirectoryFixture("xsds");
    private GenerateTokioXsdsMojo mojo;


    protected void setUp() throws Exception {
        super.setUp();
        directoryFixture.doSetUp();

        setupEnvironment("/mojos/generate-tokio-xsds/pom.xml");
        mojo = (GenerateTokioXsdsMojo)lookupMojo("generate-tokio-xsds");
        mojo.setOutputDirectory(directoryFixture);

        getProject().setArtifactId("test-project");

        Plugin plugin = new Plugin();
        plugin.setGroupId("net.codjo.maven.mojo");
        plugin.setArtifactId("maven-datagen-plugin");
        getProject().getPluginArtifactMap().put("net.codjo.maven.mojo:maven-datagen-plugin", plugin);
    }


    protected void tearDown() throws Exception {
        directoryFixture.doTearDown();
        super.tearDown();
    }


    public void test_doNothingWhenNoDatagenPlugin() throws Exception {
        getProject().getPluginArtifactMap().clear();

        mojo.execute();

        assertNoXsd();
    }


    public void test_doNothingWhenNoDatagenFile() throws Exception {
        mojo.setDatagenFinalFile(new File("dummy.file"));

        mojo.execute();

        assertNoXsd();
    }


    private void assertNoXsd() {
        assertFalse(new File(mojo.getOutputDirectory(), "myProject-cases.xsd").exists());
        assertFalse(new File(mojo.getOutputDirectory(), "myProject-scenarii.xsd").exists());
        assertFalse(new File(mojo.getOutputDirectory(), "myProject-story.xsd").exists());
        assertFalse(new File(mojo.getOutputDirectory(), "myProject-entities.xsd").exists());
    }
}
