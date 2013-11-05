package net.codjo.maven.mojo.datagen;
import java.io.File;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Java;
import org.apache.tools.ant.types.Path;
/**
 *
 */
public class JavaExecutor {
    protected Long timeout;
    private boolean spawnProcess = false;
    private boolean failOnError = true;
    private File workingDir = new File(".");
    private String jvmArg;


    public void execute(String mainClass, File[] classpathFiles, String arguments)
          throws MojoExecutionException {
        Java java = new Java();
        initAntStuff(java);
        java.setTaskName(mainClass.substring(mainClass.lastIndexOf(".") + 1, mainClass.length()));

        java.setClassname(mainClass);
        java.createArg().setLine(arguments);
        if (jvmArg != null) {
            java.createJvmarg().setLine(jvmArg);
        }
        java.setJvm(System.getProperty("java.home") + File.separator + "bin" + File.separator + "java");
        java.setDir(workingDir);
        java.setFork(true);
        java.setTimeout(timeout);
        java.setSpawn(spawnProcess);
        if (!spawnProcess) {
            java.setFailonerror(failOnError);
        }

        Path classpath = java.createClasspath();
        for (int i = 0; i < classpathFiles.length; i++) {
            File classpathFile = classpathFiles[i];
            Path path = classpath.createPath();
            path.setPath(classpathFile.getPath());
        }

        try {
            java.execute();
        }
        catch (BuildException buildException) {
            throw new MojoExecutionException("Erreur lors de l'exécution de la tâche Ant", buildException);
        }
    }


    public void setTimeout(long timeout) {
        this.timeout = new Long(timeout);
    }


    public void setWorkingDir(File workingDir) {
        this.workingDir = workingDir;
    }


    public void setFailOnError(boolean failOnError) {
        this.failOnError = failOnError;
    }


    public void setSpawnProcess(boolean spawnProcess) {
        this.spawnProcess = spawnProcess;
    }


    public void setJvmArg(String jvmArg) {
        this.jvmArg = jvmArg;
    }


    public static void initAntStuff(Task task) {
        Project ant = new Project();
        task.setProject(ant);

        DefaultLogger listener = new DefaultLogger();
        listener.setOutputPrintStream(System.out);
        listener.setErrorPrintStream(System.err);
        listener.setMessageOutputLevel(Project.MSG_INFO);
        ant.addBuildListener(listener);
        ant.init();
    }
}
