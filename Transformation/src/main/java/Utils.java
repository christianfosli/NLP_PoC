import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Utils {
    public static ValueFactory vf = SimpleValueFactory.getInstance();
    public static IRI NS = vf.createIRI("https://www.sdir.no/SDIR_Simulator#");

    public static String readFromFile(String filename) throws IOException {
        StringBuilder sb = new StringBuilder();
        Files.lines(Paths.get(filename)).forEach(s -> sb.append(s).append("\n"));
        return sb.toString();
    }
}