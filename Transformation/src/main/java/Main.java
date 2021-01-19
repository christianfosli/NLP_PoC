import java.io.*;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) throws IOException {

        Utils.createOTTRLibrary();

        Utils.csvToXlxs("src/main/resources/input.csv", "../OTTR/test.xlsx");

        Scope.builtDateScope(Utils.SCOPE_FILE_BUILT_DATE);
        Scope.vesselLengthScope(Utils.SCOPE_FILE_VESSEL_LENGTH_OVERALL);
        Scope.machinePowerScope(Utils.SCOPE_FILE_MACHINE_POWER);

        Scope.writeScopeModelToFile();
        Scope.writeRequirementModelToFile();

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
                " --mode expand --library ../OTTR/o-tpl-lib.ttl --libraryFormat stottr --fetchMissing " +
                "--inputFormat tabottr src/main/resources/ottr/tabexample.xlsx " +
                "-o src/main/resources/output/sdir";

        Process exec = Runtime.getRuntime().exec(command);
        InputStream inputStream = exec.getInputStream();
        return new BufferedReader(new InputStreamReader(inputStream))
                .lines().collect(Collectors.joining("\n"));
    }
}
