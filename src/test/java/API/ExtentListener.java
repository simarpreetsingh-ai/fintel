package API;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.aventstack.extentreports.*;
import com.aventstack.extentreports.MediaEntityBuilder;

public class ExtentListener implements ITestListener {

    private static ExtentReports extent = ExtentManager.getInstance();
    private static ThreadLocal<ExtentTest> test = new ThreadLocal<>();

    @Override
    public void onTestStart(ITestResult result) {
        ExtentTest extentTest = extent.createTest(result.getMethod().getMethodName());
        test.set(extentTest);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        test.get().pass("✅ Test Passed");
    }

    @Override
    public void onTestFailure(ITestResult result) {

        ExtentTest extentTest = test.get();

        extentTest.fail("❌ Test Failed: " + result.getThrowable());

        // ✅ Attach Screenshot (if available)
        try {
            String screenshotPath = ScreenshotUtil.captureScreenshot(
                    result.getMethod().getMethodName()
            );

            extentTest.fail("Screenshot:",
                    MediaEntityBuilder.createScreenCaptureFromPath(screenshotPath).build());

        } catch (Exception e) {
            extentTest.fail("Screenshot capture failed: " + e.getMessage());
        }

        // ✅ Attach Video (Optional)
        try {
            String videoPath = "Videos" + result.getMethod().getMethodName() + ".mp4";

            extentTest.info(
                "<b>Execution Video:</b><br>" +
                "<video width='400' controls>" +
                "<source src='" + videoPath + "' type='video/mp4'>" +
                "Your browser does not support video" +
                "</video>"
            );

        } catch (Exception e) {
            extentTest.info("Video attachment failed: " + e.getMessage());
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        test.get().skip("⚠️ Test Skipped");
    }

    @Override
    public void onFinish(ITestContext context) {
        extent.flush();
    }

    public static ExtentTest getTest() {
        return test.get();
    }
}