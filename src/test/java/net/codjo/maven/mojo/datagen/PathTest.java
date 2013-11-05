/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.maven.mojo.datagen;
import java.io.File;
import junit.framework.TestCase;
import org.apache.maven.model.Model;
import org.apache.maven.project.MavenProject;
/**
 * Classe de test de {@link Path}.
 */
public class PathTest extends TestCase {
    public void test_defaultValue() throws Exception {
        Path path = new Path();

        assertEquals(new File("@basedir@/target/src/sql/"), path.getSql());
        assertEquals(new File("@basedir@/target/src/client/"), path.getClient());
        assertEquals(new File("@basedir@/target/src/server/"), path.getServer());
    }


    public void test_constructor() throws Exception {
        Path path = new Path("sql", "client", "server");

        assertEquals(new File("sql"), path.getSql());
        assertEquals(new File("client"), path.getClient());
        assertEquals(new File("server"), path.getServer());
    }


    public void test_getters() throws Exception {
        MavenProject project = new MavenProject(new Model());
        project.setFile(new File("c:/basedir/pom.xml"));

        Path path = new Path("sql", "@basedir@/client", "server");

        assertEquals(new File("sql/table"), path.getSqlTable(project));
        assertEquals(new File("sql/view"), path.getSqlView(project));
        assertEquals(new File("sql/index"), path.getSqlIndex(project));
        assertEquals(new File("sql/constraint"), path.getSqlConstraint(project));
        assertEquals(new File("sql/trigger"), path.getSqlTrigger(project));

        assertEquals(new File("c:/basedir/client/resources"), path.getClientResource(project));

        assertEquals(new File("server/java"), path.getServerSource(project));
        assertEquals(new File("server/resources"), path.getServerResource(project));
    }
}
