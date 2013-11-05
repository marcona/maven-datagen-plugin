/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.maven.mojo.datagen;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProjectHelper;
import org.apache.tools.ant.DirectoryScanner;
import org.codehaus.plexus.compiler.CompilerConfiguration;
import org.codehaus.plexus.compiler.CompilerException;
import org.codehaus.plexus.compiler.manager.CompilerManager;
import org.codehaus.plexus.compiler.manager.NoSuchCompilerException;
import org.codehaus.plexus.util.FileUtils;
/**
 */
public abstract class AbstractCompilerMojo extends AbstractDatagenMojo {
    private static final String JAVA_VERSION = "1.5";
    /**
     * Maven ProjectHelper
     *
     * @component
     * @readonly
     * @noinspection UNUSED_SYMBOL,UnusedDeclaration
     */
    private MavenProjectHelper projectHelper;
    /**
     * Plexus compiler manager.
     *
     * @component
     * @noinspection UNUSED_SYMBOL,UnusedDeclaration
     */
    private CompilerManager compilerManager;
    /**
     * Set this to 'true' to bypass the source compilation.
     *
     * @parameter expression="${maven.datagen.skip.compile}"
     */
    private boolean skipCompile = false;
    /**
     * The -source argument for the Java compiler
     *
     * @parameter
     * @noinspection UNUSED_SYMBOL,UnusedDeclaration
     */
    private String source = JAVA_VERSION;
    /**
     * The -target argument for the Java compiler
     *
     * @parameter
     * @noinspection UNUSED_SYMBOL,UnusedDeclaration
     */
    private String target = JAVA_VERSION;
    /**
     * Version of the compiler to use, ex. "1.3", "1.5"
     *
     * @parameter
     * @noinspection UNUSED_SYMBOL,UnusedDeclaration
     */
    private String compilerVersion = JAVA_VERSION;
    /**
     * Optimize compiled code using the compiler's optimization methods
     *
     * @parameter expression="${maven.compiler.optimize}" default-value="true"
     * @noinspection UNUSED_SYMBOL,UnusedDeclaration
     */
    private boolean optimize = true;


    protected abstract File getClassesDirectory();


    protected abstract File getSourcesDirectory();


    protected abstract File getResourceDirectory();


    protected abstract String getClassifier();


    public void execute() throws MojoExecutionException, MojoFailureException {
        deleteClassHasBeenCompiledFile();

        if (skipCompile) {
            getLog().info("\t -> skip de la compilation de '" + getClassifier() + "'.");
            return;
        }

        boolean shouldProcessResource = shouldProcessResources();
        if (shouldProcessResource) {
            processResources();
        }

        boolean shouldProcessComile = shouldCompile();
        if (shouldProcessComile) {
            compileDatagenFiles();
        }
        else {
            getLog().info("\t -> compilation du code '" + getClassifier() + "' non necessaire.");
        }

        if (shouldProcessComile || shouldProcessResource) {
            createClassHasBeenCompiledFile();
        }
    }


    private void processResources() throws MojoExecutionException {
        try {
            FileUtils.copyDirectoryStructure(getResourceDirectory(), getClassesDirectory());
        }
        catch (IOException e) {
            throw new MojoExecutionException("Impossible de copier les resources " + getClassifier(), e);
        }
    }


    private void compileDatagenFiles() throws MojoExecutionException {
        CompilerConfiguration configuration = new CompilerConfiguration();
        configuration.addSourceLocation(getSourcesDirectory().getAbsolutePath());
        configuration.setSourceVersion(source);
        configuration.setTargetVersion(target);
        configuration.setOutputLocation(getClassesDirectory().getAbsolutePath());
        configuration.setCompilerVersion(compilerVersion);
        configuration.setDebug(false);
        configuration.setOptimize(optimize);

        for (Iterator it = project.getArtifacts().iterator(); it.hasNext();) {
            Artifact artifact = (Artifact)it.next();
            if (!"pom".equals(artifact.getType())) {
                configuration.addClasspathEntry(artifact.getFile().getPath());
            }
        }

        try {
            org.codehaus.plexus.compiler.Compiler compiler = compilerManager.getCompiler("javac");
            List messages = compiler.compile(configuration);
            if (messages.size() > 0) {
                for (Iterator it = messages.iterator(); it.hasNext();) {
                    getLog().error("Erreur de compilation dans le fichier : ");
                    getLog().error("> " + it.next().toString());
                }
                throw new MojoExecutionException("Erreur de compilation des fichiers "
                                                 + getClassifier() + " générés par datagen !");
            }
        }
        catch (MojoExecutionException e) {
            deleteClassHasBeenCompiledFile();
            throw e;
        }
        catch (CompilerException e) {
            deleteClassHasBeenCompiledFile();
            throw new MojoExecutionException("Compilation du code " + getClassifier() + " en erreur : "
                                             + e.getMessage(), e);
        }
        catch (NoSuchCompilerException e) {
            deleteClassHasBeenCompiledFile();
            throw new MojoExecutionException("Compilation du code " + getClassifier() + " en erreur : "
                                             + e.getMessage(), e);
        }
    }


    private boolean shouldProcessResources() {
        DirectoryScanner directoryScanner = new DirectoryScanner();
        directoryScanner.setBasedir(getResourceDirectory());
        directoryScanner.setCaseSensitive(false);
        directoryScanner.setExcludes(new String[]{"**/.svn/**"});
        directoryScanner.scan();
        String[] files = directoryScanner.getIncludedFiles();
        if (files.length == 0) {
            return false;
        }

        File generatedResourceFile = new File(getClassesDirectory(), files[0]);
        File resourceFile = new File(getResourceDirectory(), files[0]);

        return !isUpToDate(resourceFile, generatedResourceFile);
    }


    private boolean shouldCompile() {
        String generatedSrcPath = getSourcesDirectory().getAbsolutePath();

        File generatedJavaFile = getOneGeneratedFile(new File(generatedSrcPath), "java");
        //noinspection SimplifiableIfStatement
        if (generatedJavaFile == null) {
            return false;
        }

        return !isUpToDate(generatedJavaFile, toClassFile(generatedJavaFile));
    }


    private boolean isUpToDate(File sourceFile, File generatedFile) {
        return (generatedFile.exists() && sourceFile.lastModified() <= generatedFile.lastModified());
    }


    private void createClassHasBeenCompiledFile() {
        File hasBeenCompiledFile = getClassHasBeenCompiledFile();
        try {
            hasBeenCompiledFile.createNewFile();
        }
        catch (IOException e) {
            getLog().warn("Impossible de creer le fichier " + hasBeenCompiledFile.getAbsolutePath());
        }
    }


    private void deleteClassHasBeenCompiledFile() {
        getClassHasBeenCompiledFile().delete();
    }


    protected File getClassHasBeenCompiledFile() {
        return PathUtil.classHasBeenCompiledFile(buildDirectory, getClassesDirectory());
    }


    private File toClassFile(File generatedJavaFile) {
        String relativeSourceFilePath = PathUtil.relativePath(getSourcesDirectory(), generatedJavaFile);

        return new File(getClassesDirectory(), relativeSourceFilePath.replaceAll("\\.java", "\\.class"));
    }


    public void setSkipCompile(boolean skipCompile) {
        this.skipCompile = skipCompile;
    }
}
