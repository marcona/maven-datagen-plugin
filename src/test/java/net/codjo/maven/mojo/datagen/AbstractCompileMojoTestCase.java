package net.codjo.maven.mojo.datagen;
import net.codjo.maven.common.test.FileUtil;
import java.io.File;
import java.io.IOException;
/**
 *
 */
public abstract class AbstractCompileMojoTestCase extends DatagenTestCase {
    protected File classFile;
    protected File resourceFile;
    protected File classHasBeenCompiledFile;


    public void test_execute_firstCompile() throws Exception {
        assertFalse(classFile.exists());

        mojo.execute();

        assertCompileDone();
        assertTrue(classHasBeenCompiledFile.exists());
    }


    public void test_execute_recompile() throws Exception {
        createFile(classFile, "n/a");
        classFile.setLastModified(0);
        assertTrue(classFile.lastModified() == 0);

        mojo.execute();

        assertCompileDone();
        assertTrue(classFile.lastModified() != 0);
        assertTrue(classHasBeenCompiledFile.exists());
    }


    public void test_execute_noRecompile() throws Exception {
        createFile(classFile, "previous class file content");
        createFile(resourceFile, "previous resource file content");
        classHasBeenCompiledFile.createNewFile();

        mojo.execute();

        assertEquals("previous class file content", FileUtil.loadContent(classFile));
        assertFalse(classHasBeenCompiledFile.exists());
    }


    public void test_execute_skipCompile() throws Exception {
        assertFalse(classFile.exists());
        classHasBeenCompiledFile.createNewFile();

        ((AbstractCompilerMojo)mojo).setSkipCompile(true);

        mojo.execute();

        assertFalse(classFile.exists());
        assertFalse(classHasBeenCompiledFile.exists());
    }


    private void assertCompileDone() throws IOException {
        assertTrue(classFile.exists());
        assertTrue(resourceFile.exists());
    }


    private void createFile(File file, String content) throws IOException {
        file.getParentFile().mkdirs();
        FileUtil.saveContent(file, content);
        assertTrue(file.exists());
    }
}
