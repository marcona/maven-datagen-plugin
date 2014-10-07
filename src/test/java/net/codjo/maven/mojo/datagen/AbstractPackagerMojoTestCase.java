package net.codjo.maven.mojo.datagen;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.jar.JarFile;
import org.apache.maven.artifact.Artifact;
/**
 *
 */
public abstract class AbstractPackagerMojoTestCase extends DatagenTestCase {
    protected String targetClassesDir;
    protected File classFile;
    protected File resourceFile;


    public void test_packaging() throws Exception {
        createFile(classFile);
        createFile(resourceFile);
        createFile(getClassHasBeenCompiledFile());

        mojo.execute();

        assertPackagingDone(classFile, resourceFile);
    }


    public void test_noPackageBecauseNoNewStuff() throws Exception {
        createFile(classFile);
        getClassHasBeenCompiledFile().delete();

        mojo.execute();

        assertFalse(getJarFile().exists());
    }


    public void test_nothingToPackage() throws Exception {
        mojo.execute();

        assertFalse(getJarFile().exists());
    }


    public void test_getClassHasBeenCompiledFile() throws Exception {
        AbstractPackagerMojo jarMojo = ((AbstractPackagerMojo)mojo);

        AbstractCompilerMojo compileMojo = (AbstractCompilerMojo)
              lookupMojo("compile-" + jarMojo.getClassifier(), getPomFile());

        assertEquals(compileMojo.getClassHasBeenCompiledFile(),
                     jarMojo.getClassHasBeenCompiledFile());
    }


    private void createFile(File file) throws IOException {
        file.getParentFile().mkdirs();
        file.createNewFile();
    }


    private void assertPackagingDone(File file1, File file2) throws IOException {
        assertPackagingDone(new File[]{file1, file2});
    }


    private File getClassHasBeenCompiledFile() {
        return ((AbstractPackagerMojo)mojo).getClassHasBeenCompiledFile();
    }


    private void assertPackagingDone(File[] files) throws IOException {
        File serverJarFile = getJarFile();
        assertTrue(serverJarFile.exists());

        JarFile jarFile = new JarFile(serverJarFile);
        try {
            File targetDir = getGeneratedClassFile(targetClassesDir, "na").getParentFile();
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                String relativePath = PathUtil.toUnixLikePath(PathUtil.relativePath(targetDir, file));
                assertNotNull(relativePath + " présent", jarFile.getJarEntry(relativePath));
            }
        }
        finally {
            jarFile.close();
        }

        assertAttachedArtifact(serverJarFile, getProject());
    }


    private void assertAttachedArtifact(File clientJarFile, MavenProjectMock project) {
        List attachedArtifacts = project.getAttachedArtifacts();
        assertEquals(1, attachedArtifacts.size());
        assertEquals(clientJarFile, ((Artifact)attachedArtifacts.get(0)).getFile().getAbsoluteFile());
    }


    private File getJarFile() {
        String classifier = ((AbstractPackagerMojo)mojo).getClassifier();
        return new File(getBuildDirectory(), "artifactId-1.0-" + classifier + ".jar");
    }
}
