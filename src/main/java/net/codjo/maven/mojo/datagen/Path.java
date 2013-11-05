package net.codjo.maven.mojo.datagen;
import java.io.File;
import org.apache.maven.project.MavenProject;
/**
 *
 */
public class Path {
    private File sql = new File("@basedir@/target/src/sql/");
    private File client = new File("@basedir@/target/src/client/");
    private File server = new File("@basedir@/target/src/server/");


    public Path() {
    }


    public Path(String sql, String client, String server) {
        this.sql = new File(sql);
        this.client = new File(client);
        this.server = new File(server);
    }


    public File getSql() {
        return sql;
    }


    public File getClient() {
        return client;
    }


    public File getServer() {
        return server;
    }


    public File getSqlDirectory(MavenProject project) {
        return replaceBasedir(sql, project);
    }


    public File getSqlTable(MavenProject project) {
        return replaceBasedir(new File(sql, "table"), project);
    }


    public File getSqlView(MavenProject project) {
        return replaceBasedir(new File(sql, "view"), project);
    }


    public File getSqlIndex(MavenProject project) {
        return replaceBasedir(new File(sql, "index"), project);
    }


    public File getSqlConstraint(MavenProject project) {
        return replaceBasedir(new File(sql, "constraint"), project);
    }


    public File getSqlTrigger(MavenProject project) {
        return replaceBasedir(new File(sql, "trigger"), project);
    }


    public File getClientResource(MavenProject project) {
        return replaceBasedir(new File(client, "resources"), project);
    }


    public File getServerResource(MavenProject project) {
        return replaceBasedir(new File(server, "resources"), project);
    }


    public File getServerSource(MavenProject project) {
        return replaceBasedir(new File(server, "java"), project);
    }

    public File getClientSource(MavenProject project) {
        return replaceBasedir(new File(client, "java"), project);
    }


    private File replaceBasedir(File file, MavenProject project) {
        String result =
              file.getPath().replaceAll("@basedir@",
                                        project.getBasedir().getAbsolutePath().replace('\\', '/'));
        return new File(result);
    }
}
