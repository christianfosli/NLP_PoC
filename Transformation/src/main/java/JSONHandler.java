import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import scope.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JSONHandler {

    public static List<VesselType> vesselType() throws IOException {
        String content = Utils.readFromFile("src/main/resources/input/vesseltype.json");

        JsonObject jsonObject = new JsonParser().parse(content).getAsJsonObject();
        JsonElement jsonElement = jsonObject.get(Utils.VT_JSON_OBJ);
        JsonArray array = jsonElement.getAsJsonArray();

        List<VesselType> vtList = new ArrayList<>();

        for (int i = 0; i < array.size(); i++) {

            JsonElement element = array.get(i);
            JsonObject object = element.getAsJsonObject();

            Requirement requirement = createRequirement(object);
            VesselType vt = new VesselType(
                    requirement,
                    object.get("vessel_type_text").toString().replace("\"", "")
            );

            vtList.add(vt);
        }
        return vtList;
    }

    public static List<Flashpoint> flashpoint() throws IOException {
        String content = Utils.readFromFile("src/main/resources/input/flashpoint.json");

        JsonObject jsonObject = new JsonParser().parse(content).getAsJsonObject();
        JsonElement jsonElement = jsonObject.get(Utils.FP_JSON_OBJ);
        JsonArray array = jsonElement.getAsJsonArray();

        List<Flashpoint> fpList = new ArrayList<>();

        for (int i = 0; i < array.size(); i++) {

            JsonElement element = array.get(i);
            JsonObject object = element.getAsJsonObject();

            Requirement requirement = createRequirement(object);
            Flashpoint fp = new Flashpoint(
                    requirement,
                    object.get("flashpoint_value_1_suffix").toString().replace("\"", ""),
                    object.get("flashpoint_value_1").toString().replace("\"", ""),
                    object.get("flashpoint_value_1_measurement").toString().replace("\"", "")
            );

            fpList.add(fp);
        }
        return fpList;
    }

    public static List<GrossTonnage> grossTonnage() throws IOException {
        String content = Utils.readFromFile("src/main/resources/input/grosstonnage.json");

        JsonObject jsonObject = new JsonParser().parse(content).getAsJsonObject();
        JsonElement jsonElement = jsonObject.get(Utils.GT_JSON_OBJ);
        JsonArray array = jsonElement.getAsJsonArray();

        List<GrossTonnage> gtList = new ArrayList<>();

        for (int i = 0; i < array.size(); i++) {

            JsonElement element = array.get(i);
            JsonObject object = element.getAsJsonObject();

            Requirement requirement = createRequirement(object);
            GrossTonnage p = new GrossTonnage(
                    requirement,
                    object.get("gross_tonnage_context").toString().replace("\"", ""),
                    object.get("gross_tonnage_value_1").toString().replace("\"", "")
            );

            gtList.add(p);
        }
        return gtList;
    }

    public static List<Passengers> passengers() throws IOException {
        String content = Utils.readFromFile("src/main/resources/input/passenger.json");

        JsonObject jsonObject = new JsonParser().parse(content).getAsJsonObject();
        JsonElement jsonElement = jsonObject.get(Utils.PASS_JSON_OBJ);
        JsonArray array = jsonElement.getAsJsonArray();

        List<Passengers> passList = new ArrayList<>();

        for (int i = 0; i < array.size(); i++) {

            JsonElement element = array.get(i);
            JsonObject object = element.getAsJsonObject();

            Requirement requirement = createRequirement(object);
            Passengers p = new Passengers(
                    requirement,
                    object.get("passenger_context").toString().replace("\"", ""),
                    object.get("passenger_value_1").toString().replace("\"", "")
            );

            passList.add(p);
        }
        return passList;
    }

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

        String regulation_id =
                object.get("regulation_year").toString().replace("\"", "") + "-" +
                        object.get("regulation_month").toString().replace("\"", "") + "-" +
                        object.get("regulation_day").toString().replace("\"", "") + "-" +
                        object.get("regulation_id").toString().replace("\"", "");

        if (object.get("sub_part_number") != null) {
            return new Requirement(
                    regulation_id,
                    object.get("regulation_id").toString().replace("\"", ""),
                    object.get("chapter_number").toString().replace("\"", ""),
                    object.get("section_number").toString().replace("\"", ""),
                    object.get("part_number").toString().replace("\"", ""),
                    object.get("sub_part_number").toString().replace("\"", "")
            );
        } else {
            return new Requirement(
                    regulation_id,
                    object.get("regulation_id").toString().replace("\"", ""),
                    object.get("chapter_number").toString().replace("\"", ""),
                    object.get("section_number").toString().replace("\"", ""),
                    object.get("part_number").toString().replace("\"", "")
            );
        }
    }
}
