package rdftransformer.api.transformer.action;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import rdftransformer.api.transformer.scope.*;
import rdftransformer.api.transformer.utils.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JSONHandler {

    public static void JSONReceiver(String s) throws IOException {
        JsonObject jsonObject = new JsonParser().parse(s).getAsJsonObject();

        if (s.contains(Utils.PROP_JOSN_OBJ)) {
            GraphGenerator.propulsionPowerScope(jsonObject.get(Utils.PROP_JOSN_OBJ).getAsJsonArray());
        }
        if (s.contains(Utils.LO_JSON_OBJ)) {
            GraphGenerator.loadingScope(jsonObject.get(Utils.LO_JSON_OBJ).getAsJsonArray());
        }
        if (s.contains(Utils.PRO_JSON_OBJ)) {
            GraphGenerator.protectedScope(jsonObject.get(Utils.PRO_JSON_OBJ).getAsJsonArray());
        }
        if (s.contains(Utils.CON_JSON_OBJ)) {
            GraphGenerator.convertedScope(jsonObject.get(Utils.CON_JSON_OBJ).getAsJsonArray());
        }
        if (s.contains(Utils.RA_JSON_OBJ)) {
            GraphGenerator.radioAreaScope(jsonObject.get(Utils.RA_JSON_OBJ).getAsJsonArray());
        }
        if (s.contains(Utils.CARGO_JSON_OBJ)) {
            GraphGenerator.cargoScope(jsonObject.get(Utils.CARGO_JSON_OBJ).getAsJsonArray());
        }
        if (s.contains(Utils.TA_JSON_OBJ)) {
            GraphGenerator.tradeAreaScope(jsonObject.get(Utils.TA_JSON_OBJ).getAsJsonArray());
        }
        if (s.contains(Utils.VT_JSON_OBJ)) {
            GraphGenerator.vesselTypeScope(jsonObject.get(Utils.VT_JSON_OBJ).getAsJsonArray());
        }
        if (s.contains(Utils.FP_JSON_OBJ)) {
            GraphGenerator.flashpointScope(jsonObject.get(Utils.FP_JSON_OBJ).getAsJsonArray());
        }
        if (s.contains(Utils.GT_JSON_OBJ)) {
            GraphGenerator.grossTonnageScope(jsonObject.get(Utils.GT_JSON_OBJ).getAsJsonArray());
        }
        if (s.contains(Utils.PASS_JSON_OBJ)) {
            GraphGenerator.passengerScope(jsonObject.get(Utils.PASS_JSON_OBJ).getAsJsonArray());
        }
        if (s.contains(Utils.EL_JSON_OBJ)) {
            GraphGenerator.electricalInstallationScope(jsonObject.get(Utils.EL_JSON_OBJ).getAsJsonArray());
        }
        if (s.contains(Utils.BD_JSON_OBJ)) {
            GraphGenerator.builtDateScope(jsonObject.get(Utils.BD_JSON_OBJ).getAsJsonArray());
        }
        if (s.contains(Utils.LOA_JSON_OBJ)) {
            GraphGenerator.vesselLengthScope(jsonObject.get(Utils.LOA_JSON_OBJ).getAsJsonArray());
        }
    }

    public static List<PropulsionPower> propulsionPower(JsonArray array) {
        List<PropulsionPower> proList = new ArrayList<>();

        for (int i = 0; i < array.size(); i++) {

            JsonElement element = array.get(i);
            JsonObject object = element.getAsJsonObject();

            Requirement requirement = createRequirement(object);
            PropulsionPower pp = new PropulsionPower(
                    requirement,
                    object.get("propulsion_power_context").toString().replace("\"", ""),
                    object.get("propulsion_power_value_1").toString().replace("\"", ""),
                    object.get("measurement_text").toString().replace("\"", "")
            );

            proList.add(pp);
        }
        return proList;
    }

    public static List<Loading> loading(JsonArray array) {

        List<Loading> loList = new ArrayList<>();

        for (int i = 0; i < array.size(); i++) {

            JsonElement element = array.get(i);
            JsonObject object = element.getAsJsonObject();

            Requirement requirement = createRequirement(object);
            Loading l = new Loading(
                    requirement,
                    object.get("load_installation_text").toString().replace("\"", "")
            );

            loList.add(l);
        }
        return loList;
    }

    public static List<Protected> _protected(JsonArray array) {

        List<Protected> proList = new ArrayList<>();

        for (int i = 0; i < array.size(); i++) {

            JsonElement element = array.get(i);
            JsonObject object = element.getAsJsonObject();

            Requirement requirement = createRequirement(object);
            Protected p = new Protected(
                    requirement,
                    object.get("protected").toString().replace("\"", "")
            );

            proList.add(p);
        }
        return proList;
    }

    public static List<Converted> converted(JsonArray array) {

        List<Converted> cList = new ArrayList<>();

        for (int i = 0; i < array.size(); i++) {

            JsonElement element = array.get(i);
            JsonObject object = element.getAsJsonObject();

            Requirement requirement = createRequirement(object);
            Converted c = new Converted(
                    requirement,
                    object.get("conversion_text").toString().replace("\"", "")
            );

            cList.add(c);
        }
        return cList;
    }

    public static List<RadioArea> radioArea(JsonArray array) {

        List<RadioArea> raList = new ArrayList<>();

        for (int i = 0; i < array.size(); i++) {

            JsonElement element = array.get(i);
            JsonObject object = element.getAsJsonObject();

            Requirement requirement = createRequirement(object);
            RadioArea ra = new RadioArea(
                    requirement,
                    object.get("radio_area_type_text").toString().replace("\"", "")
            );

            raList.add(ra);
        }
        return raList;
    }

    public static List<Cargo> cargo(JsonArray array) {

        List<Cargo> cargoList = new ArrayList<>();

        for (int i = 0; i < array.size(); i++) {

            JsonElement element = array.get(i);
            JsonObject object = element.getAsJsonObject();

            Requirement requirement = createRequirement(object);
            Cargo c = new Cargo(
                    requirement,
                    object.get("cargo_text").toString().replace("\"", "")
            );

            cargoList.add(c);
        }
        return cargoList;
    }

    public static List<TradeArea> tradeArea(JsonArray array) {

        List<TradeArea> taList = new ArrayList<>();

        for (int i = 0; i < array.size(); i++) {

            JsonElement element = array.get(i);
            JsonObject object = element.getAsJsonObject();

            Requirement requirement = createRequirement(object);
            TradeArea ta = new TradeArea(
                    requirement,
                    object.get("trade_area_text").toString().replace("\"", "")
            );

            taList.add(ta);
        }
        return taList;
    }

    public static List<VesselType> vesselType(JsonArray array) {

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

    public static List<Flashpoint> flashpoint(JsonArray array) {

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

    public static List<GrossTonnage> grossTonnage(JsonArray array) {

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

    public static List<Passengers> passengers(JsonArray array)  {

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

    public static List<ElectricalInstallation> electricalInstallations(JsonArray array) {

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

    public static List<BuiltDate> builtDate(JsonArray array) {

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

    public static List<LOA> vesselLengthOverall(JsonArray array)  {

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
                    object.get("sub_part_number").toString().replace("\"", ""),
                    object.get("chapter_title").toString().replace("\"", ""),
                    object.get("section_title").toString().replace("\"", "")
            );
        } else {
            return new Requirement(
                    regulation_id,
                    object.get("regulation_id").toString().replace("\"", ""),
                    object.get("chapter_number").toString().replace("\"", ""),
                    object.get("section_number").toString().replace("\"", ""),
                    object.get("part_number").toString().replace("\"", ""),
                    object.get("chapter_title").toString().replace("\"", ""),
                    object.get("section_title").toString().replace("\"", "")
            );
        }
    }
}
