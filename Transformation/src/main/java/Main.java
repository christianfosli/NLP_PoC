import java.io.IOException;
import java.text.ParseException;

public class Main {

    public static void main(String[] args) {
        try {
            //Preprocessing.buildGraph();
            Scope.machinePower();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
