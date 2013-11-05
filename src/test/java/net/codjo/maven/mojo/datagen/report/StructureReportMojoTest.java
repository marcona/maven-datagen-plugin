package net.codjo.maven.mojo.datagen.report;
import net.codjo.maven.common.mock.AgfMojoTestCase;
import net.codjo.maven.common.mock.RendererMock;
import net.codjo.maven.common.report.XslGeneratorMock;
import net.codjo.maven.common.test.LogString;
/**
 *
 */
public class StructureReportMojoTest extends AgfMojoTestCase {
    private LogString log = new LogString();
    private LogString report = new LogString();


    public void test_execute_ideaFailure() throws Exception {
        setupEnvironment("/mojos/structureReport/pom-structureReport.xml");

        StructureReportMojo mojo = (StructureReportMojo)lookupMojo("structure-report");
        mojo.setXslGenerator(new XslGeneratorMock(log, "<body>generated content</body>"));

        ((RendererMock)mojo.getSiteRenderer()).setLog(report);

        mojo.execute();

        log.assertContent("setXslResourceName(/doc/doc_structure.xsl, ./target/test-classes/mojos)"
                          + ", generate(.\\target\\test-classes\\mojos\\target\\final.xml, StringWriter)");

        report.assertContent("Structure SQL, Dictionnaire des tables, generated content");
    }
}
