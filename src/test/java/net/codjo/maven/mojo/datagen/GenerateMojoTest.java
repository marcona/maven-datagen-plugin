/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.maven.mojo.datagen;
import java.io.File;
import java.util.List;
import net.codjo.test.common.XmlUtil;
import net.codjo.test.common.fixture.DirectoryFixture;
import net.codjo.util.file.FileUtil;
import org.apache.maven.plugin.Mojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
/**
 * Classe de test de {@link GenerateMojo}.
 */
public class GenerateMojoTest extends AbstractMojoTestCase {

    public void test_generateWithoutInclude() throws Exception {
        MockUtil util = MockUtil.setupEnvironment("withoutInclude/withoutInclude-pom.xml");
        Mojo mojo = lookupMojo("generate", util.getPomFile());

        mojo.execute();

        assertExist("./target/withoutInclude/server/resources/conf/role.xml");
        assertExist("./target/withoutInclude/server/resources/conf/castor-config.xml");

        String expected = "<?xml version='1.0' encoding='ISO-8859-1'?>"
                          + "<database name='DirectUseDB' engine='sybase'>"
                          + "    <driver class-name='com.sybase.jdbc2.jdbc.SybDataSource' url='jdbc:sybase:Tds:notUSED'/>"
                          + "    <mapping href='Mapping.xml'/>"
                          + "</database>";

        String actual =
              FileUtil.loadContent(toFile("./target/withoutInclude/server/resources/conf/castor-config.xml"));
        XmlUtil.assertEquals(expected, actual);

        List compileSourceRoots = util.getProject().getCompileSourceRoots();
        assertEquals(2, compileSourceRoots.size());
        assertPath("target/withoutInclude/server/java", compileSourceRoots.get(0));
        assertPath("target/withoutInclude/client/java", compileSourceRoots.get(1));
    }


    public void test_generateWithoutInclude_oracle() throws Exception {
        MockUtil util = MockUtil.setupEnvironment("withoutInclude/withoutInclude-oracle-pom.xml");
        Mojo mojo = lookupMojo("generate", util.getPomFile());

        mojo.execute();

        String expected = "<?xml version='1.0' encoding='ISO-8859-1'?>"
                          + "<database name='DirectUseDB' engine='oracle'>"
                          + "    <driver class-name='oracle.jdbc.driver.OracleDriver' url='jdbc:oracle:thin:notUSED'/>"
                          + "    <mapping href='Mapping.xml'/>"
                          + "</database>";

        String actual =
              FileUtil.loadContent(toFile("./target/withoutInclude/server/resources/conf/castor-config.xml"));
        XmlUtil.assertEquals(expected, actual);
    }


    public void test_generateWithoutInclude_mysql() throws Exception {
        MockUtil util = MockUtil.setupEnvironment("withoutInclude/withoutInclude-mysql-pom.xml");
        Mojo mojo = lookupMojo("generate", util.getPomFile());

        mojo.execute();

        String expected = "<?xml version='1.0' encoding='ISO-8859-1'?>"
                          + "<database name='DirectUseDB' engine='mysql'>"
                          + "    <driver class-name='com.mysql.jdbc.Driver' url='jdbc:mysql://notUSED'/>"
                          + "    <mapping href='Mapping.xml'/>"
                          + "</database>";

        String actual =
              FileUtil.loadContent(toFile("./target/withoutInclude/server/resources/conf/castor-config.xml"));
        XmlUtil.assertEquals(expected, actual);
    }


    public void test_generateWithDatagenInclude()
          throws Exception {
        MockUtil util = MockUtil.setupEnvironment("withDatagenInclude/withInclude-pom.xml");

        Mojo mojo = lookupMojo("generate", util.getPomFile());

        util.getProject().setFile(util.getPomFile());

        mojo.execute();

        assertExist("./target/withDatagenInclude/resources/conf/role.xml");
        assertExist("./target/withDatagenInclude/table/MY_REF.tab");
    }


    public void test_generateWithoutInclude_noConfiguration() throws Exception {
        new DirectoryFixture("./target/withDatagenInclude").doTearDown();

        MockUtil util = MockUtil.setupEnvironment("withDatagenInclude/withInclude-noConfiguration-pom.xml");

        Mojo mojo = lookupMojo("generate", util.getPomFile());

        util.getProject().setFile(util.getPomFile());

        mojo.execute();

        assertNotExist("./target/withDatagenInclude/resources/conf/role.xml");
        assertExist("./target/withDatagenInclude/table/MY_REF.tab");

//        List compileSourceRoots = util.getProject().getCompileSourceRoots();
//        assertEquals(0, compileSourceRoots.size());
    }


    public void test_generateWithInclude() throws Exception {
        MockUtil util = MockUtil.setupEnvironment("withInclude/withInclude-pom.xml");

        Mojo mojo = lookupMojo("generate", util.getPomFile());

        try {
            mojo.execute();
        }
        catch (MojoExecutionException e) {
            ;
        }

        assertExist("./target/withInclude/resources/conf/role.xml");
    }


    public void test_generateWithIncludeButNoVersion()
          throws Exception {
        MockUtil util = MockUtil.setupEnvironment("WithIncludeButNoVersion/WithIncludeButNoVersion-pom.xml");

        Mojo mojo = lookupMojo("generate", util.getPomFile());

        try {
            mojo.execute();
        }
        catch (MojoExecutionException e) {
            ;
        }

        assertExist("./target/localRepository/codjo-mad/codjo-mad-datagen/1.0/codjo-mad-datagen-1.0.xml");
    }


    public void test_generateWithoutDatagenFile() throws Exception {
        MockUtil util = MockUtil.setupEnvironment("noDatagenFile/noDatagenFile-pom.xml");

        Mojo mojo = lookupMojo("generate", util.getPomFile());

        mojo.execute();
    }


    public void test_generateWithBadVMArgs() throws Exception {
        MockUtil util = MockUtil.setupEnvironment("withVMArgs/withVMArgs-pom.xml");

        Mojo mojo = lookupMojo("generate", util.getPomFile());

        try {
            mojo.execute();
            fail();
        }
        catch (MojoExecutionException e) {
            // InvalidArgument
        }
    }


    protected void setUp() throws Exception {
        super.setUp();
        new File("./target/final.xml").delete();
    }


    private void assertExist(String pathname) {
        assertTrue(pathname + " existe", toFile(pathname).exists());
    }


    private void assertNotExist(String pathname) {
        assertFalse(pathname + " n'existe pas", toFile(pathname).exists());
    }


    private File toFile(String pathname) {
        return new File(pathname);
    }


    private void assertPath(String expected, Object actual) {
        assertEquals(toFile(expected).getPath(), actual);
    }


    protected Mojo lookupMojo(String goal, String pomFile) throws Exception {
        try {
            return lookupMojo(goal, getPomFile(pomFile));
        }
        catch (Exception e) {
            fail("lookup en echec : " + e.getLocalizedMessage());
        }
        return null;
    }


    protected File getPomFile(String path) {
        return getTestFile("target/test-classes/mojos/" + path);
    }
}
