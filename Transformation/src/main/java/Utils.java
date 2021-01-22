import org.apache.commons.io.IOUtils;
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

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;

public class Utils {

    public static final String ROOT_DIR = "../NLP Python Jupyter Notebooks/";
    public static final String SCOPE_FILE_BUILT_DATE = ROOT_DIR + "Get scope - Built date/V2 - output - Get scope - Built date.csv";
    public static final String SCOPE_FILE_MACHINE_POWER = ROOT_DIR + "Get scope - Machine power/V2 - output - Get scope - Machine power.csv";
    public static final String SCOPE_FILE_VESSEL_LENGTH_OVERALL = ROOT_DIR +"Get scope - Vessel length overall/V2 - output - Get scope - Vessel length overall.csv";
    public static final String FILE_SECTION_HEADER = ROOT_DIR + "Get chapter and section name from chapter text/output - Get chapter and section name from chapter text.csv";

    public static final String NS_UNIT = "http://qudt.org/vocab/unit/";

    public static String readFromFile(String filename) throws IOException {
        StringBuilder sb = new StringBuilder();
        Files.lines(Paths.get(filename)).forEach(s -> sb.append(s).append("\n"));
        return sb.toString();
    }

    public static String[] fileContentSplitByComma(String filename) throws IOException {
        String content = readFromFile(filename);
        String[] splitLine = content.split("\n");

        StringBuilder tmp = new StringBuilder();

        for (int i = 0; i < splitLine.length; i++) {
            if (i == splitLine.length-1) {
                tmp.append(splitLine[i]);
            } else {
                tmp.append(splitLine[i]).append(",");
            }
        }
        return tmp.toString().split(",");
    }

    public static String[] fileContentSplitByLine(String filename) throws IOException {
        String content = readFromFile(filename);
        return content.split("\n");
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

    public static void createOTTRLibrary() throws IOException {

        String sdir = IOUtils.toString(
                Utils.class.getResourceAsStream("ottr/o-sdir.ttl"),
                StandardCharsets.UTF_8
        );

        String sh = IOUtils.toString(
                Utils.class.getResourceAsStream("ottr/shaclcore.ttl"),
                StandardCharsets.UTF_8
        );

        Path file = Paths.get("../OTTR/o-tpl-lib.ttl");
        Files.write(file, Collections.singleton(sh + sdir), StandardCharsets.UTF_8);

    }
}