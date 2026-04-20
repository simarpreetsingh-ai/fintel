package API;



	import java.io.File;
	import java.io.IOException;
	import org.apache.commons.io.FileUtils;

	public class ScreenshotUtil {

	    public static String captureScreenshot(String testName) throws IOException {

	        // 👉 For API testing (dummy screenshot OR replace with Selenium if available)
	        String path = "screenshots/" + testName + "_" + System.currentTimeMillis() + ".png";

	        File src = new File("src/test/resources/sample.png"); // placeholder image
	        File dest = new File(path);

	        FileUtils.copyFile(src, dest);

	        return path;
	    }
	}


