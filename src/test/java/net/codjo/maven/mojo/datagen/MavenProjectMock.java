/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.maven.mojo.datagen;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.DefaultArtifactRepository;
import org.apache.maven.artifact.repository.layout.DefaultRepositoryLayout;
import org.apache.maven.artifact.versioning.VersionRange;
import org.apache.maven.model.Model;
import org.apache.maven.project.MavenProject;
/**
 *
 */
public class MavenProjectMock extends MavenProject {
    public MavenProjectMock() {
        super(new Model());
        ArtifactRepository remote =
              new DefaultArtifactRepository("for-test",
                                            MockUtil.toUrl("src/test/data/remoteRepository"),
                                            new DefaultRepositoryLayout());
        setRemoteArtifactRepositories(Collections.singletonList(remote));
        setFile(new File("./pom.xml"));
        MockUtil.singleton.setProject(this);
        setArtifact(new ArtifactMock());

        setPluginArtifacts(new HashSet());
        setReportArtifacts(new HashSet());
        setExtensionArtifacts(new HashSet());
        setPluginArtifactRepositories(new ArrayList());

        getBuild().setDirectory(MockUtil.singleton.getTargetDir().getAbsolutePath());
        getBuild().setOutputDirectory("target/target-test");
    }


    private static class ArtifactMock extends DefaultArtifact {
        ArtifactMock() {
            super("group", "art", VersionRange.createFromVersion("1.0"), "runtime", "jar", "main", null);
        }
    }
}
