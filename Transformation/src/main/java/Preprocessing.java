import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.vocabulary.OWL;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.RDFS;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Preprocessing {

    static Model model = new LinkedHashModel();
    static ValueFactory valueFactory = SimpleValueFactory.getInstance();

    public static List<String> ignoreWordList() throws IOException, ParseException {
        String ingoreWords = Utils.readFromFile("src/main/resources/ingore.txt");

        String[] tmp = ingoreWords.split("\n");

        return new ArrayList<>(Arrays.asList(tmp));
    }

    public static void buildGraph() throws IOException, ParseException {

        String input = Utils.readFromFile("src\\main\\resources\\noun.txt");

        model = new LinkedHashModel();
        model.setNamespace(OWL.NS);
        model.setNamespace(RDFS.NS);
        IRI NS = valueFactory.createIRI("https://www.sdir.no/SDIR_Simulator#");
        model.setNamespace("sdir", NS.toString());

        String[] words = input.split("\n");

        List<String> w = new ArrayList();

        for (String s : words) {

            if(s.contains("/")) {
                String[] split = s.split("/");
                String w1 = split[0].substring(0, 1).toUpperCase() + split[0].substring(1);
                String w2 = split[1].substring(0, 1).toUpperCase() + split[1].substring(1);

                if (!ignoreWordList().contains(w1.toLowerCase()) && !ignoreWordList().contains(w2.toLowerCase())) {
                    w.add(w1);
                    w.add(w2);
                }
            }

            if(s.length() > 2 && !s.contains("/")) {
                String tmp = s.substring(0, 1).toUpperCase() + s.substring(1);
                if (!ignoreWordList().contains(tmp.toLowerCase())) {
                    w.add(tmp);
                }
            }
        }

        w.forEach(s -> {
            model.add(valueFactory.createIRI(NS + s), RDF.TYPE, OWL.CLASS);
            model.add(valueFactory.createIRI(NS + s), RDFS.LABEL,valueFactory.createLiteral(s, "en"));
        });
        writeModelToFile(model);
    }

    public static void writeModelToFile(Model model) throws FileNotFoundException {
        FileOutputStream out = new FileOutputStream("src/main/resources/output/owl.ttl");
        Rio.write(model, out, RDFFormat.TURTLE);
    }


}
