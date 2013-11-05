package net.codjo.maven.mojo.datagen;
/**
 *
 */
public class CompileServerMojoTest extends AbstractCompileMojoTestCase {

    protected void setUp() throws Exception {
        super.setUp();
        setupEnvironment("compileServer/compileServer-pom.xml", "compile-server");

        classFile = getGeneratedClassFile("server-classes", "MyHandler.class");
        resourceFile = getGeneratedClassFile("server-classes", "conf/role.xml");
        classHasBeenCompiledFile = getGeneratedClassFile("", "server-classes-has-been-compiled");
    }
}
