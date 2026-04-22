package API;

import io.restassured.RestAssured;
import io.restassured.response.Response;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.testng.Assert;
import org.testng.annotations.*;

import com.aventstack.extentreports.ExtentTest;

import java.io.FileInputStream;
import java.io.FileOutputStream;

public class Crawl_Status_Excel {

    Workbook workbook;
    Sheet sheet;

    String filePath = "src/test/resources/response.xlsx";

    int rowCounter = 1; // start after header

    // ✅ DataProvider
    @DataProvider(name = "excelData")
    public Object[][] getData() throws Exception {
        return ExcelUtils.getTestData(filePath, "Sheet1");
    }

    // ✅ BEFORE CLASS → Reset sheet
    @BeforeClass
    public void setupExcel() throws Exception {

        FileInputStream fis = new FileInputStream(filePath);
        workbook = new XSSFWorkbook(fis);

        // Delete old sheet
        Sheet existingSheet = workbook.getSheet("Final Response");
        if (existingSheet != null) {
            int index = workbook.getSheetIndex(existingSheet);
            workbook.removeSheetAt(index);
        }

        // Create new sheet
        sheet = workbook.createSheet("Final Response");

        // Header
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Job ID");
        header.createCell(1).setCellValue("Status");
        header.createCell(2).setCellValue("Status Code");
        header.createCell(3).setCellValue("Response");

        fis.close();
    }

    // ✅ TEST METHOD
    @Test(dataProvider = "excelData")
    public void getStatus(Object[] rowData) {

        String jobId = (String) rowData[0];

        ExtentTest test = ExtentListener.getTest();
        test.info("Starting test for Job ID: " + jobId);

        try {
            Response response = RestAssured
                    .given()
                        .baseUri("https://webcrawler.qa3.fintelsandbox.com")
                        .header("x-api-key", "12345-ABCDE-67890")
                        .header("Content-Type", "application/json")
                        .pathParam("jobId", jobId)
                    .when()
                        .get("/crawl/status/{jobId}");

            int statusCode = response.getStatusCode();
            String jobStatus = response.jsonPath().getString("status");

            System.out.println("======================================");
            System.out.println("Job ID: " + jobId);
            System.out.println("Status Code: " + statusCode);
            System.out.println("Response: " + response.asPrettyString());
            System.out.println("======================================");

            test.info("<pre>" + response.asPrettyString() + "</pre>");

            // ✅ ASSERTION (THIS FIXES YOUR ISSUE)
            Assert.assertEquals(statusCode, 200, "Status code mismatch!");

            // ✅ Extent logging
            test.pass("Status Code is valid: " + statusCode);
            test.pass("Job Status: " + jobStatus);

            // ✅ Write to Excel
            Row row = sheet.createRow(rowCounter++);
            row.createCell(0).setCellValue(jobId);
            row.createCell(1).setCellValue(jobStatus);
            row.createCell(2).setCellValue(statusCode);
            row.createCell(3).setCellValue(response.asPrettyString());

        } catch (AssertionError ae) {
            test.fail("Assertion Failed: " + ae.getMessage());
            throw ae; // ✅ Important: fail test in TestNG
        } catch (Exception e) {
            test.fail("Exception: " + e.getMessage());
            e.printStackTrace();
            Assert.fail("Test failed due to exception");
        }
    }

    // ✅ AFTER CLASS → Save Excel
    @AfterClass
    public void saveExcel() throws Exception {

        FileOutputStream fos = new FileOutputStream(filePath);
        workbook.write(fos);

        fos.close();
        workbook.close();

        System.out.println("✅ Final Response sheet rewritten successfully!");
    }
}