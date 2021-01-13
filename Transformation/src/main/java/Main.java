import java.io.*;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) throws IOException {

        Utils.createOTTRLibrary();

        /*
        String err = runLutra();
        if (err != null) {
            System.err.println(err);
        }*/
    }

    /** runLutra
     *
     * Running lutra through system command.
     *
     * @return String with error message if it fails to execute jar-file
     * @throws IOException
     */
    public static String runLutra() throws IOException {

        Main.class.getResource("lutra.jar");

        String command = "java -jar src/main/resources/lutra.jar" +
                " --mode expand --library src/main/resources/ottr/shaclcore.ttl --libraryFormat stottr --fetchMissing " +
                "--inputFormat stottr src/main/resources/ottr/instances.ttl " +
                "-o src/main/resources/output/sdir";

        Process exec = Runtime.getRuntime().exec(command);
        InputStream inputStream = exec.getInputStream();
        return new BufferedReader(new InputStreamReader(inputStream))
                .lines().collect(Collectors.joining("\n"));
    }
}
