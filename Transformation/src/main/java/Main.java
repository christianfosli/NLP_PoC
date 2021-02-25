import org.antlr.v4.misc.Graph;
import org.eclipse.rdf4j.model.Model;
import ottr.OTTRWrapper;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {

        GraphGenerator.vesselLengthScope();
        GraphGenerator.builtDateScope();
        GraphGenerator.electricalInstallationScope();
        GraphGenerator.passengerScope();
        GraphGenerator.grossTonnageScope();
        GraphGenerator.flashpointScope();

        GraphGenerator.writeScopeModelToFile();
        GraphGenerator.writeRequirementModelToFile();

        OTTRWrapper.createOTTRInstances(GraphGenerator.ottrInstances);
        Model model = OTTRWrapper.runLutra();
        model.forEach(System.out::println);
    }
}
