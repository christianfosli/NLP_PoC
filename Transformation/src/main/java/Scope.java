import org.apache.jena.shacl.vocabulary.SHACL;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.vocabulary.OWL;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.RDFS;
import org.eclipse.rdf4j.model.vocabulary.XSD;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Scope {

    static ValueFactory valueFactory = SimpleValueFactory.getInstance();
    public static IRI NS = valueFactory.createIRI("https://www.sdir.no/SDIR_Simulator#");

    public static void machinePower() throws IOException {
        String text = Utils.readFromFile("src/main/resources/machinepower.txt");

        Model model = new LinkedHashModel();
        model.setNamespace(OWL.NS);
        model.setNamespace(RDFS.NS);
        model.setNamespace(XSD.NS);
        model.setNamespace("sh", SHACL.NS);
        model.setNamespace("unit", "http://qudt.org/vocab/unit/");
        model.setNamespace("sdir", NS.toString());

        String[] tmp = text.split("\n");

        List<String> tmpList = new ArrayList<>(Arrays.asList(tmp));

        IRI subject = valueFactory.createIRI(NS + "unknown");

        for (String s : tmpList) {
            String[] info = s.split(",");

            System.out.println(s);
            IRI paragraph = valueFactory.createIRI(NS + "unknown");

            for (String str : info) {
                if (str.contains("Section")) {

                    String[] tmpSec = str.split(" ");
                    String parNum = tmpSec[1].replace("'", "");
                    paragraph = valueFactory.createIRI(NS + "FOR1404P" + parNum);
                    model.add(paragraph, RDF.TYPE, valueFactory.createIRI(NS + "Requirement"));
                    model.add(paragraph, RDF.TYPE, valueFactory.createIRI(SHACL.NS + "NodeShape"));
                    model.add(paragraph, valueFactory.createIRI(NS + "regulationReference"), valueFactory.createLiteral("https://lovdata.no/forskrift/2013-11-22-1404/§"+parNum, XSD.ANYURI));



                }
                if (str.contains("more than")) {
                    subject = valueFactory.createIRI(NS + "PS_MP_min50");

                    model.add(paragraph, valueFactory.createIRI(SHACL.NS + "property"), subject);

                    model.add(subject, RDF.TYPE, valueFactory.createIRI(SHACL.NS + "NodeShape"));
                    model.add(subject, RDF.TYPE, valueFactory.createIRI(NS + "Scope"));

                    model.add(subject, valueFactory.createIRI(SHACL.NS + "description"), valueFactory.createLiteral(
                            "Scope of machine power more than 50V.", "en"));
                    model.add(subject, valueFactory.createIRI(SHACL.NS + "description"), valueFactory.createLiteral(
                            "Virkeområde maskinkraft mer enn 50V.", "no"));

                    String value = info[2].replace("'", "");
                    model.add(subject, valueFactory.createIRI(SHACL.NS + "minInclusive"), valueFactory.createLiteral(Integer.parseInt(value.trim())));

                    model.add(subject, valueFactory.createIRI(SHACL.NS + "datatype"), valueFactory.createIRI("http://qudt.org/vocab/unit/V"));
                    model.add(subject, valueFactory.createIRI(SHACL.NS + "minCount"), valueFactory.createLiteral(1));
                    model.add(subject, valueFactory.createIRI(SHACL.NS + "maxCount"), valueFactory.createLiteral(1));
                }
                if (str.contains("up to")) {
                    subject = valueFactory.createIRI(NS + "PS_MP_max50");

                    model.add(paragraph, valueFactory.createIRI(SHACL.NS + "property"), subject);

                    model.add(subject, RDF.TYPE, valueFactory.createIRI(SHACL.NS + "NodeShape"));
                    model.add(subject, RDF.TYPE, valueFactory.createIRI(NS + "Scope"));

                    model.add(subject, valueFactory.createIRI(SHACL.NS + "description"), valueFactory.createLiteral(
                            "Scope of machine power up to 50V.", "en"));
                    model.add(subject, valueFactory.createIRI(SHACL.NS + "description"), valueFactory.createLiteral(
                            "Virkeområde maskinkraft opp til 50V.", "no"));

                    String value = info[2].replace("'", "");
                    model.add(subject, valueFactory.createIRI(SHACL.NS + "maxExclusive"), valueFactory.createLiteral(Integer.parseInt(value.trim())));

                    model.add(subject, valueFactory.createIRI(SHACL.NS + "datatype"), valueFactory.createIRI("http://qudt.org/vocab/unit/V"));
                    model.add(subject, valueFactory.createIRI(SHACL.NS + "minCount"), valueFactory.createLiteral(1));
                    model.add(subject, valueFactory.createIRI(SHACL.NS + "maxCount"), valueFactory.createLiteral(1));
                }
            }
        }
        writeModelToFile(model);

    }
    public static void writeModelToFile(Model model) throws FileNotFoundException {
        FileOutputStream out = new FileOutputStream("src/main/resources/output/scope.ttl");
        Rio.write(model, out, RDFFormat.TURTLE);
    }
}
