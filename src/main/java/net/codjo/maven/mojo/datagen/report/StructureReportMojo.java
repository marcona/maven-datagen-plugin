/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.maven.mojo.datagen.report;
import net.codjo.maven.common.report.XslGenerator;
import java.io.File;
import java.io.StringWriter;
import java.util.Locale;
import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.siterenderer.Renderer;
import org.apache.maven.project.MavenProject;
import org.apache.maven.reporting.AbstractMavenReport;
import org.apache.maven.reporting.MavenReportException;
import org.apache.xalan.processor.TransformerFactoryImpl;
/**
 * Generation d'un rapport portant sur la structure BD.
 *
 * @goal structure-report
 */
public class StructureReportMojo extends AbstractMavenReport {

    /**
     * Repertoire contenant les resources.
     *
     * @parameter expression="${project.basedir}/src/main/resources"
     * @required
     * @readonly
     * @noinspection UNUSED_SYMBOL
     */
    private String resourcesDirectory;
    /**
     * Fichier resultant de la generation (cf. goal generate).
     *
     * @parameter expression="${structureDirectory}" default-value="${project.build.directory}/final.xml"
     * @required
     * @readonly
     * @noinspection UNUSED_SYMBOL
     */
    private String datagenFinalFile;
    /**
     * Repertoire de destination du rapport.
     *
     * @parameter expression="${project.reporting.outputDirectory}"
     * @required
     * @readonly
     * @noinspection UNUSED_SYMBOL
     */
    private String outputDirectory;
    /**
     * @parameter expression="${component.org.apache.maven.doxia.siterenderer.Renderer}"
     * @required
     * @readonly
     * @noinspection UNUSED_SYMBOL
     */
    private Renderer siteRenderer;
    /**
     * @parameter expression="${project}"
     * @required
     * @readonly
     * @noinspection UNUSED_SYMBOL
     */
    private MavenProject project;
    private XslGenerator xslGenerator = new XslGenerator();


    public boolean canGenerateReport() {
        return new File(datagenFinalFile).exists();
    }


    protected Renderer getSiteRenderer() {
        return siteRenderer;
    }


    protected String getOutputDirectory() {
        return outputDirectory;
    }


    protected MavenProject getProject() {
        return project;
    }


    public String getDescription(Locale locale) {
        return "Documentation sur la structure datagen du projet";
    }


    public String getName(Locale locale) {
        return "Structure Base de données";
    }


    public String getOutputName() {
        return "structure-report";
    }


    public XslGenerator getXslGenerator() {
        return xslGenerator;
    }


    public void setXslGenerator(XslGenerator xslGenerator) {
        this.xslGenerator = xslGenerator;
    }


    protected void executeReport(Locale locale) throws MavenReportException {
        File structureFile = new File(datagenFinalFile);
        getLog().info("Generation de la documentation de la structure BD " + structureFile.getName());

        // System.setProperty("javax.xml.transform.TransformerFactory",
        //                    "org.apache.xalan.processor.TransformerFactoryImpl");
        xslGenerator.setTransformerFactory(new TransformerFactoryImpl());

        xslGenerator.setXslResourceName("/doc/doc_structure.xsl", resourcesDirectory);

        StringWriter output = new StringWriter();
        xslGenerator.generate(structureFile, output);
        String result = output.toString();
        generateReportIndex(getSink(), result.substring(result.indexOf("<body>") + "<body>".length(),
                                                        result.indexOf("</body>")));
    }


    private void generateReportIndex(Sink sink, String content) {
        sink.head();
        sink.text("Structure SQL");
        sink.head_();
        sink.body();

        sink.sectionTitle1();
        sink.text("Dictionnaire des tables");
        sink.sectionTitle1_();

        sink.rawText(content);

        sink.body_();
        sink.flush();
        sink.close();
    }
}
