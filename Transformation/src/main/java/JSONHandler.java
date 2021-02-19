import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import scope.BuiltDate;
import scope.ElectricalInstallation;
import scope.LOA;
import scope.Requirement;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JSONHandler {

    public static List<ElectricalInstallation> electricalInstallations() throws IOException {
        String content = Utils.readFromFile("src/main/resources/input/electricalinstallation.json");

        JsonObject jsonObject = new JsonParser().parse(content).getAsJsonObject();
        JsonElement jsonElement = jsonObject.get(Utils.EL_JSON_OBJ);
        JsonArray array = jsonElement.getAsJsonArray();

        List<ElectricalInstallation> elList = new ArrayList<>();

        for (int i = 0; i < array.size(); i++) {

            JsonElement element = array.get(i);
            JsonObject object = element.getAsJsonObject();

            Requirement requirement = createRequirement(object);
            ElectricalInstallation el = new ElectricalInstallation(
                    requirement,
                    object.get("voltage_prefix").toString().replace("\"", ""),
                    object.get("voltage_value").toString().replace("\"", ""),
                    object.get("measurement_text").toString().replace("\"", "")
            );

            elList.add(el);
        }
        return elList;
    }

    public static List<BuiltDate> builtDate() throws IOException {
        String content = Utils.readFromFile("src/main/resources/input/builtdate.json");

        JsonObject jsonObject = new JsonParser().parse(content).getAsJsonObject();
        JsonElement jsonElement = jsonObject.get(Utils.BD_JSON_OBJ);
        JsonArray array = jsonElement.getAsJsonArray();

        List<BuiltDate> bdList = new ArrayList<>();

        for (int i = 0; i < array.size(); i++) {

            JsonElement element = array.get(i);
            JsonObject object = element.getAsJsonObject();

            Requirement requirement = createRequirement(object);

            if (object.get("date_separator") != null) {
                BuiltDate builtDate = new BuiltDate(
                        requirement,
                        object.get("date_context").toString().replace("\"", ""),
                        object.get("date_separator").toString().replace("\"", ""),
                        object.get("date_value_1").toString().replace("\"", ""),
                        object.get("date_value_2").toString().replace("\"", "")
                );

                bdList.add(builtDate);
            } if (object.get("date_context") != null && object.get("date_separator") == null) {
                BuiltDate builtDate = new BuiltDate(
                        requirement,
                        object.get("date_context").toString().replace("\"", ""),
                        object.get("date_value_1").toString().replace("\"", "")
                );

                bdList.add(builtDate);
            }
        }
        return bdList;
    }

    public static List<LOA> vesselLengthOverall() throws IOException {
        String content = Utils.readFromFile("src/main/resources/input/loa.json");

        JsonObject jsonObject = new JsonParser().parse(content).getAsJsonObject();
        JsonElement jsonElement = jsonObject.get(Utils.LOA_JSON_OBJ);
        JsonArray array = jsonElement.getAsJsonArray();

        List<LOA> loaList = new ArrayList<>();

        for (int i = 0; i < array.size(); i++) {

            JsonElement element = array.get(i);
            JsonObject object = element.getAsJsonObject();

            Requirement requirement = createRequirement(object);

            if (object.get("length_separator") != null) {
                LOA loa = new LOA(
                        requirement,
                        object.get("length_separator").toString().replace("\"", ""),
                        object.get("length_value_1").toString().replace("\"", ""),
                        object.get("length_value_2").toString().replace("\"", ""),
                        object.get("measurement").toString().replace("\"", "")
                        );

                loaList.add(loa);
            }
            if (object.get("length_prefix") != null) {
                LOA loa = new LOA(
                        requirement,
                        object.get("length_prefix").toString().replace("\"", ""),
                        object.get("length_value").toString().replace("\"", ""),
                        object.get("measurement").toString().replace("\"", "")
                );
                loaList.add(loa);
            }
        }

        return loaList;
    }

    public static Requirement createRequirement(JsonObject object) {
        return new Requirement(
                object.get("regulation_id").toString().replace("\"", ""),
                object.get("chapter_number").toString().replace("\"", ""),
                object.get("section_number").toString().replace("\"", ""),
                object.get("part_number").toString().replace("\"", "")
        );
    }
}
