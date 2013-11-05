package net.codjo.maven.mojo.datagen;
import org.apache.maven.plugin.MojoExecutionException;
/**
 *
 */
public class CompileClientMojoTest extends AbstractCompileMojoTestCase {

    protected void setUp() throws Exception {
        super.setUp();
        setupEnvironment("compileClient/compileClient-pom.xml", "compile-client");
        classFile = getGeneratedClassFile("client-classes", "com/MyGui.class");
        resourceFile = getGeneratedClassFile("client-classes", "conf/role.xml");
        classHasBeenCompiledFile = getGeneratedClassFile("", "client-classes-has-been-compiled");
    }


    public void test_compile_noJava() throws Exception {
        setupEnvironment("compileClient/compileClient-nojava-pom.xml", "compile-client");

        mojo.execute();

        assertTrue(resourceFile.exists());
        assertTrue(classHasBeenCompiledFile.exists());
    }


    public void test_compile_badJavaFile() throws Exception {
        setupEnvironment("compileClient/compileClient-badjava-pom.xml", "compile-client");

        try {
            mojo.execute();
            fail();
        }
        catch (MojoExecutionException e) {
            assertFalse(classHasBeenCompiledFile.exists());
            assertEquals("Erreur de compilation des fichiers client générés par datagen !", e.getMessage());
        }
    }
}
