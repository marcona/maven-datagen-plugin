package net.codjo.maven.mojo.datagen;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.DependencyManagement;
/**
 *
 */
public class MavenProjectWithDependencyMock extends MavenProjectMock {
    public MavenProjectWithDependencyMock() {
        getModel().setDependencyManagement(new DependencyManagement());

        Dependency dependency = new Dependency();
        dependency.setGroupId("agf-mad");
        dependency.setArtifactId("agf-mad-datagen");
        dependency.setVersion("1.0");

        getDependencyManagement().addDependency(dependency);
    }
}
