import java.io.IOException;
import java.text.ParseException;

public class Main {

    public static void main(String[] args) {
        try {
            //Preprocessing.buildGraph();
            //Scope.machinePower();
            //Scope.builtDate();
            //Scope.vesselLength();
            Scope.writeAllToFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
