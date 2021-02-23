import org.antlr.v4.misc.Graph;
import ottr.OTTRWrapper;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {

        GraphGenerator.vesselLengthScope();
        GraphGenerator.builtDateScope();
        GraphGenerator.electricalInstallationScope();
        GraphGenerator.passengerScope();
        GraphGenerator.writeScopeModelToFile();
        GraphGenerator.writeRequirementModelToFile();


        OTTRWrapper.runLutra();
        /*
        Utils.createOTTRLibrary();

        Utils.csvToXlxs("src/main/resources/input.csv", "../OTTR/test.xlsx");

        GraphGenerator.builtDateScope(Utils.SCOPE_FILE_BUILT_DATE);
        GraphGenerator.vesselLengthScope(Utils.SCOPE_FILE_VESSEL_LENGTH_OVERALL);
        GraphGenerator.machinePowerScope(Utils.SCOPE_FILE_MACHINE_POWER);

        GraphGenerator.writeScopeModelToFile();
        GraphGenerator.writeRequirementModelToFile();
        */

        /*
        String err = runLutra();
        if (err != null) {
            System.err.println(err);
        }*/
    }
}
