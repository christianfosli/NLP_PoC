import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.model.vocabulary.OWL;
import org.eclipse.rdf4j.model.vocabulary.RDFS;
import org.eclipse.rdf4j.model.vocabulary.SHACL;
import org.eclipse.rdf4j.model.vocabulary.XSD;
import vocabulary.Vocabulary;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Utils {

    public static final String NS_UNIT = "http://qudt.org/vocab/unit/";

    public static final String LOA_JSON_OBJ = "identified_vessel_length_overall";
    public static final String BD_JSON_OBJ = "identified_build_dates";
    public static final String EL_JSON_OBJ = "identified_electrical_installations";
    public static final String PASS_JSON_OBJ = "identified_PASSENGER";
    public static final String GT_JSON_OBJ = "identified_GROSS_TONNAGE";
    public static final String FP_JSON_OBJ = "identified_FLASHPOINT";
    public static final String VT_JSON_OBJ = "identified_VESSEL_TYPE";
    public static final String TA_JSON_OBJ = "identified_TRADE_AREA";
    public static final String CARGO_JSON_OBJ = "identified_CARGO";
    public static final String RA_JSON_OBJ = "identified_RADIO_AREA";
    public static final String CON_JSON_OBJ = "identified_CONVERSION";
    public static final String PRO_JSON_OBJ = "identified_PROTECTED";
    public static final String LO_JSON_OBJ = "identified_LOAD_INSTALLATION";
    public static final String PROP_JOSN_OBJ = "identified_PROPULSION_POWER";

    public static String readFromFile(String filename) throws IOException {
        StringBuilder sb = new StringBuilder();
        Files.lines(Paths.get(filename)).forEach(s -> sb.append(s).append("\n"));
        return sb.toString();
    }

    public static Model initModel() {
        Model model = new LinkedHashModel();
        model.setNamespace(OWL.NS);
        model.setNamespace(RDFS.NS);
        model.setNamespace(XSD.NS);
        model.setNamespace("sh", SHACL.NAMESPACE);
        model.setNamespace("unit", NS_UNIT);
        model.setNamespace("sdir", Vocabulary.NS);
        model.setNamespace("scope", Vocabulary.NS_SCOPE);
        model.setNamespace("tradearea", Vocabulary.NS_TRADE);
        model.setNamespace("vesseltype", Vocabulary.NS_VESSEL);
        return model;
    }

    public static String removeDecimal(String number) {
        if (number.contains(".")) {
            return number.split("\\.")[0];
        }
        return number;
    }

    public static void csvToXlxs(String filename, String output) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(filename));

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();

        String line = bufferedReader.readLine();

        for (int i = 0; line != null; i++) {
            Row row = sheet.createRow(i);
            String[] items = line.split(",");

            for (int j = 0, column = 0; j < items.length; j++) {
                String item = items[j];
                Cell cell = row.createCell(column++);
                cell.setCellValue(item);
            }
            line = bufferedReader.readLine();
        }

        FileOutputStream out = new FileOutputStream(output);
        workbook.write(out);
        bufferedReader.close();
        out.close();
    }
}