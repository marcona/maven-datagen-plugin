package net.codjo.maven.mojo.datagen;
import java.io.File;
import org.apache.maven.plugin.Mojo;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.apache.tools.ant.util.FileUtils;
/**
 *
 */
public abstract class DatagenTestCase extends AbstractMojoTestCase {
    private MockUtil mockUtil;
    protected Mojo mojo;


    protected void setupEnvironment(String pomFilePath, String mojoName) throws Exception {
        mockUtil = MockUtil.setupEnvironment(pomFilePath);
        initMojo(mojoName);
    }


    protected void tearDown() throws Exception {
        if (getBuildDirectory().exists()) {
            FileUtils.delete(getBuildDirectory());
        }
        super.tearDown();
    }


    private void initMojo(String goal) throws Exception {
        mojo = lookupMojo(goal, mockUtil.getPomFile());

        mockUtil.getProject().setGroupId("my-group-id");
        mockUtil.getProject().setArtifactId("artifactId");
        mockUtil.getProject().setVersion("1.0");
        mockUtil.getProject().getBuild().setFinalName("artifactId-1.0");
    }


    protected File getBuildDirectory() {
        String pathname = mockUtil.getProject().getBuild().getDirectory();
        return new File(PathUtil.toUnixLikePath(pathname));
    }


    protected File getGeneratedClassFile(String classesDirectory, String className) {
        return new File(getBuildDirectory(), classesDirectory + File.separator + className);
    }


    protected File getPomFile() {
        return mockUtil.getPomFile();
    }


    protected MavenProjectMock getProject() {
        return mockUtil.getProject();
    }
}
