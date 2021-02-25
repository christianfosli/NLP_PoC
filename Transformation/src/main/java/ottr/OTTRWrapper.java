package ottr;

import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;
import xyz.ottr.lutra.cli.CLI;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class OTTRWrapper {

    public static Model runLutra() throws IOException {

        createOTTRLibrary();

        String command = "--mode expand " +
                "--library ../OTTR/o-tpl-lib.ttl --libraryFormat stottr --fetchMissing " +
                "--inputFormat stottr src/main/resources/ottr/instances.ttl " +
                "--stdout";

        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outStream, true, "UTF-8");
        CLI cli = new CLI(out, System.out);
        cli.run(command.split(" "));

        String result = outStream.toString();

        InputStream inputStream = new ByteArrayInputStream(result.getBytes(StandardCharsets.UTF_8));
        return Rio.parse(inputStream, inputStream.toString(), RDFFormat.TURTLE);
    }

    public static void createOTTRLibrary() throws IOException {

        String sdir = readFromFile("src/main/resources/ottr/o-sdir.ttl");
        String sh = readFromFile("src/main/resources/ottr/shaclcore.ttl");

        Path file = Paths.get("../OTTR/o-tpl-lib.ttl");
        Files.write(file, Collections.singleton(sh + sdir), StandardCharsets.UTF_8);
    }

    public static void createOTTRInstances(List<String> instances) throws IOException {

        Set<String> set = new HashSet<>(instances);
        instances.clear();
        instances.addAll(set);

        Files.write(Paths.get("src/main/resources/ottr/builtdate.ttl"), set);
    }

    public static String readFromFile(String filename) throws IOException {
        StringBuilder sb = new StringBuilder();
        Files.lines(Paths.get(filename)).forEach(s -> sb.append(s).append("\n"));
        return sb.toString();
    }
}
