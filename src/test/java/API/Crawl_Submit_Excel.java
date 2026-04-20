package API;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.testng.annotations.Test;

import java.io.FileInputStream;
import java.io.FileOutputStream;

import static io.restassured.RestAssured.given;

public class Crawl_Submit_Excel {

    @Test
    public void submitCrawlJob() throws Exception {

        // 🔹 Base URI
        RestAssured.baseURI = "https://webcrawler.qa3.fintelsandbox.com";

        // 🔹 Payload Excel Path
        String payloadFilePath = "src/test/resources/payload.xlsx";

        FileInputStream payloadFis = new FileInputStream(payloadFilePath);
        Workbook payloadWorkbook = new XSSFWorkbook(payloadFis);
        Sheet payloadSheet = payloadWorkbook.getSheet("Sheet1");

        // 🔹 Response Excel Path
        String responseFilePath = "src/test/resources/response.xlsx";
        FileInputStream responseFis = new FileInputStream(responseFilePath);
        Workbook responseWorkbook = new XSSFWorkbook(responseFis);
        Sheet responseSheet = responseWorkbook.getSheet("Sheet1");

        int responseLastRow = responseSheet.getLastRowNum();

        // 🔹 Loop through all rows (skip header)
        for (int i = 1; i <= payloadSheet.getLastRowNum(); i++) {

            Row payloadRow = payloadSheet.getRow(i);

            try {
                // 🔹 Read data from Excel
                String merchantId = payloadRow.getCell(0).getStringCellValue();
                String domain = payloadRow.getCell(1).getStringCellValue();
                int depth = (int) payloadRow.getCell(2).getNumericCellValue();
                boolean enabledLinkDiscovery = payloadRow.getCell(3).getBooleanCellValue();
                boolean freshScrape = payloadRow.getCell(4).getBooleanCellValue();
                int frequency = (int) payloadRow.getCell(5).getNumericCellValue();
                boolean isDfs = payloadRow.getCell(6).getBooleanCellValue();

                // 🔹 Create Payload
                String payload = "{\n" +
                        "  \"merchantId\": \"" + merchantId + "\",\n" +
                        "  \"domain\": [\"" + domain + "\"],\n" +
                        "  \"depth\": " + depth + ",\n" +
                        "  \"enabledLinkDiscovery\": " + enabledLinkDiscovery + ",\n" +
                        "  \"freshScrape\": " + freshScrape + ",\n" +
                        "  \"frequency\": " + frequency + ",\n" +
                        "  \"isDfs\": " + isDfs + "\n" +
                        "}";

                // 🔹 API Call
                Response response =
                        given()
                                .header("x-api-key", "12345-ABCDE-67890")
                                .header("Content-Type", "application/json")
                                .body(payload)
                        .when()
                                .post("/crawl/submit");

                int statusCode = response.getStatusCode();
                String responseBody = response.asPrettyString();
                String jobId = response.jsonPath().getString("jobId");

                System.out.println("Row " + i + " Status Code: " + statusCode);
                System.out.println("Row " + i + " Job ID: " + jobId);

                if (statusCode != 201) {
                   // System.out.println("Row " + i + " FAILED but continuing...");
                }

                // 🔹 Write to Response Excel
                Row responseRow = responseSheet.createRow(++responseLastRow);
                responseRow.createCell(0).setCellValue(jobId != null ? jobId : "N/A");
                responseRow.createCell(1).setCellValue(statusCode);
                responseRow.createCell(2).setCellValue(responseBody);

            } catch (Exception e) {
                System.out.println("Row " + i + " ERROR: " + e.getMessage());

                // 🔹 Log error in Excel
                Row responseRow = responseSheet.createRow(++responseLastRow);
                responseRow.createCell(0).setCellValue("ERROR");
                responseRow.createCell(1).setCellValue("Exception");
                responseRow.createCell(2).setCellValue(e.getMessage());
            }
        }

        // 🔹 Close payload workbook
        payloadFis.close();
        payloadWorkbook.close();

        // 🔹 Write response workbook
        responseFis.close();
        FileOutputStream fos = new FileOutputStream(responseFilePath);
        responseWorkbook.write(fos);
        fos.close();
        responseWorkbook.close();

        System.out.println("All payloads processed successfully!");
    }
}