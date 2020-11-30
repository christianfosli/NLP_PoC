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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Scope {

    static ValueFactory valueFactory = SimpleValueFactory.getInstance();
    public static IRI NS = valueFactory.createIRI("https://www.sdir.no/SDIR_Simulator#");

    public static Model initModel() {
        Model model = new LinkedHashModel();
        model.setNamespace(OWL.NS);
        model.setNamespace(RDFS.NS);
        model.setNamespace(XSD.NS);
        model.setNamespace("sh", SHACL.NS);
        model.setNamespace("unit", "http://qudt.org/vocab/unit/");
        model.setNamespace("sdir", NS.toString());
        return model;
    }

    public static void writeAllToFile() throws IOException, ParseException {
        Model graph = machinePower();
        graph.addAll(builtDate());

        writeModelToFile(graph);
    }

    public static Model machinePower() throws IOException {
        String text = Utils.readFromFile("src/main/resources/machinepower.txt");

        Model model = initModel();

        String[] tmp = text.split("\n");

        List<String> tmpList = new ArrayList<>(Arrays.asList(tmp));

        IRI subject;

        for (String s : tmpList) {
            String[] info = s.split(",");
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
        return model;
    }

    public static Model builtDate() throws IOException, ParseException {
        String text = Utils.readFromFile("src/main/resources/builtdate.txt");

        Model model = initModel();

        String[] tmp = text.split("\n");

        List<String> tmpList = new ArrayList<>(Arrays.asList(tmp));
        IRI subject;

        for (String s : tmpList) {
            String[] info = s.split(",");
            IRI paragraph = valueFactory.createIRI(NS + "unknown");;

            for (String str : info) {
                if (str.contains("Section")) {

                    String[] tmpSec = str.split(" ");
                    String parNum = tmpSec[1].replace("'", "");
                    paragraph = valueFactory.createIRI(NS + "FOR1404P" + parNum);
                    model.add(paragraph, RDF.TYPE, valueFactory.createIRI(NS + "Requirement"));
                    model.add(paragraph, RDF.TYPE, valueFactory.createIRI(SHACL.NS + "NodeShape"));
                    model.add(paragraph, valueFactory.createIRI(NS + "regulationReference"), valueFactory.createLiteral("https://lovdata.no/forskrift/2013-11-22-1404/§" + parNum, XSD.ANYURI));
                }
                if (str.contains("before")) {
                    String[] dateTmp = s.split(",");
                    String dateStr = dateTmp[dateTmp.length-1];

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.ENGLISH);
                    LocalDate date = LocalDate.parse(dateStr, formatter);

                    subject = valueFactory.createIRI(NS + "PS_BuiltDate_b" + date.toString().replace("-", ""));

                    model.add(paragraph, valueFactory.createIRI(SHACL.NS + "property"), subject);

                    model.add(subject, RDF.TYPE, valueFactory.createIRI(SHACL.NS + "NodeShape"));
                    model.add(subject, RDF.TYPE, valueFactory.createIRI(NS + "Scope"));

                    model.add(subject, valueFactory.createIRI(SHACL.NS + "description"), valueFactory.createLiteral(
                            "Scope of built date before " + date.toString() + ".", "en"));
                    model.add(subject, valueFactory.createIRI(SHACL.NS + "description"), valueFactory.createLiteral(
                            "Virkeområde byggeår før " + date.toString() + ".", "no"));

                    model.add(subject, valueFactory.createIRI(SHACL.NS + "maxExclusive"), valueFactory.createLiteral(date.toString()));

                    model.add(subject, valueFactory.createIRI(SHACL.NS + "datatype"), XSD.DATE);
                    model.add(subject, valueFactory.createIRI(SHACL.NS + "minCount"), valueFactory.createLiteral(1));
                    model.add(subject, valueFactory.createIRI(SHACL.NS + "maxCount"), valueFactory.createLiteral(1));

                }
                if (str.contains("after")) {
                    String[] dateTmp = s.split(",");
                    String dateStr = dateTmp[dateTmp.length-1];

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.ENGLISH);
                    LocalDate date = LocalDate.parse(dateStr, formatter);

                    subject = valueFactory.createIRI(NS + "PS_BuiltDate_a" + date.toString().replace("-", ""));

                    model.add(paragraph, valueFactory.createIRI(SHACL.NS + "property"), subject);

                    model.add(subject, RDF.TYPE, valueFactory.createIRI(SHACL.NS + "NodeShape"));
                    model.add(subject, RDF.TYPE, valueFactory.createIRI(NS + "Scope"));

                    model.add(subject, valueFactory.createIRI(SHACL.NS + "description"), valueFactory.createLiteral(
                            "Scope of built date after " + date.toString() + ".", "en"));
                    model.add(subject, valueFactory.createIRI(SHACL.NS + "description"), valueFactory.createLiteral(
                            "Virkeområde byggeår etter " + date.toString() + ".", "no"));

                    model.add(subject, valueFactory.createIRI(SHACL.NS + "minInclusive"), valueFactory.createLiteral(date.toString()));

                    model.add(subject, valueFactory.createIRI(SHACL.NS + "datatype"), XSD.DATE);
                    model.add(subject, valueFactory.createIRI(SHACL.NS + "minCount"), valueFactory.createLiteral(1));
                    model.add(subject, valueFactory.createIRI(SHACL.NS + "maxCount"), valueFactory.createLiteral(1));

                }
            }
        }
        return model;
    }

    public static void writeModelToFile(Model model) throws FileNotFoundException {
        FileOutputStream out = new FileOutputStream("src/main/resources/output/scope.ttl");
        Rio.write(model, out, RDFFormat.TURTLE);
    }
}