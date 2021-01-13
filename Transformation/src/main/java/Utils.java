import org.apache.commons.io.IOUtils;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFParser;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.rio.helpers.StatementCollector;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;

public class Utils {
    public static ValueFactory vf = SimpleValueFactory.getInstance();
    public static IRI NS = vf.createIRI("https://www.sdir.no/SDIR_Simulator#");

    public static String readFromFile(String filename) throws IOException {
        StringBuilder sb = new StringBuilder();
        Files.lines(Paths.get(filename)).forEach(s -> sb.append(s).append("\n"));
        return sb.toString();
    }

    public static void createOTTRLibrary() throws IOException {

        String sdir = IOUtils.toString(
                Utils.class.getResourceAsStream("ottr/o-sdir.ttl"),
                StandardCharsets.UTF_8
        );

        String sh = IOUtils.toString(
                Utils.class.getResourceAsStream("ottr/shaclcore.ttl"),
                StandardCharsets.UTF_8
        );

        Path file = Paths.get("../OTTR/o-tpl-lib.ttl");
        Files.write(file, Collections.singleton(sh + sdir), StandardCharsets.UTF_8);

    }
}