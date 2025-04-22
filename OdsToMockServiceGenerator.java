import java.io.*;
import java.nio.file.*;
import java.util.zip.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import java.io.StringReader;

public class OdsToMockServiceGenerator {
    public static void main(String[] args) {
        try {
            // Step 1: Extract content.xml from ODS
            String xmlContent = unzipOds("ServiceMocked.ods");

            // Step 2: Parse the content to get rows of folderName, schema, response
            String[][] rows = parseOdsXml(xmlContent);

            for (String[] row : rows) {
                if (row.length < 3) continue;
                String folder = row[0];
                String schema = row[1];
                String response = row[2];

                // Step 3: Create folder and write mock response files
                createMockFolderStructure(folder, schema, response);
            }

            System.out.println("All folders and mock files created successfully.");

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static String unzipOds(String filename) throws IOException {
        try (ZipFile zf = new ZipFile(filename)) {
            ZipEntry entry = zf.getEntry("content.xml");
            try (InputStream is = zf.getInputStream(entry)) {
                return new String(is.readAllBytes());
            }
        }
    }

    private static String[][] parseOdsXml(String xml) throws Exception {
        Document doc = DocumentBuilderFactory.newInstance()
            .newDocumentBuilder()
            .parse(new InputSource(new StringReader(xml)));

        NodeList rows = doc.getElementsByTagName("table:table-row");
        int rowCount = rows.getLength();
        String[][] data = new String[rowCount][];

        for (int i = 0; i < rowCount; i++) {
            Node rowNode = rows.item(i);
            NodeList cells = ((Element) rowNode).getElementsByTagName("table:table-cell");
            String[] rowValues = new String[cells.getLength()];
            for (int j = 0; j < cells.getLength(); j++) {
                rowValues[j] = getCellText(cells.item(j));
            }
            data[i] = rowValues;
        }
        return data;
    }

    private static String getCellText(Node cell) {
        return cell != null ? cell.getTextContent().trim() : "";
    }

    private static void createMockFolderStructure(String folder, String schema, String response) throws IOException {
        Path base = Paths.get(folder);
        Path mappings = base.resolve("mappings");
        Path files = base.resolve("__files");
        Files.createDirectories(mappings);
        Files.createDirectories(files);

        // Create badResponse.json
        String badResponse = """
            {
              \"priority\": 2,
              \"request\": {
                \"method\": \"POST\",
                \"urlPath\": \"/%s\"
              },
              \"response\": {
                \"status\": 400,
                \"jsonBody\": {
                  \"status\": 400,
                  \"error\": \"Bad Request\"
                }
              }
            }
            """.formatted(folder);

        Files.writeString(mappings.resolve("badResponse.json"), badResponse);

        // Create successResponse.json using schema and response
        String successResponse = createSuccessResponseJson(folder, schema, response);
        Files.writeString(mappings.resolve("successResponse.json"), successResponse);
    }

    private static String createSuccessResponseJson(String folder, String schema, String response) {
        return String.format(
            "{\n" +
            "  \"priority\": 1,\n" +
            "  \"request\": {\n" +
            "    \"method\": \"POST\",\n" +
            "    \"urlPath\": \"/%s\",\n" +
            "    \"bodyPatterns\": [\n" +
            "      {\"matchesJsonSchema\": %s}\n" +
            "    ]\n" +
            "  },\n" +
            "  \"response\": {\n" +
            "    \"status\": 200,\n" +
            "    \"jsonBody\": %s\n" +
            "  }\n" +
            "}\n",
            folder, schema, response);
    }
}
