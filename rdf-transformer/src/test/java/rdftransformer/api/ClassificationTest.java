package rdftransformer.api;

import org.junit.Test;
import rdftransformer.api.transformer.action.Classification;
import rdftransformer.api.transformer.utils.Utils;
import static org.junit.Assert.*;


import java.io.IOException;

public class ClassificationTest {

    @Test
    public void classificationTest() throws IOException {
        String s = Classification.classification(Utils.readFromFile("src/test/resources/classification/input.json"));

        assertEquals(
                cleanString(Utils.readFromFile("src/test/resources/classification/expected")),
                cleanString(s)
        );
    }

    public static String cleanString(String s) {
        return s.trim().replace("\n", "").replace("\r", "");
    }

}
