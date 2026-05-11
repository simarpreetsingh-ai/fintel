package API;


import org.testng.annotations.AfterSuite;

	public class BaseTest {

	    @AfterSuite
	    public void afterSuite() {
	        SendEmailReport.sendReport();
	    }
	}


