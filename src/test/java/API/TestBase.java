package API;


import org.testng.annotations.AfterSuite;

	public class TestBase {

	    @AfterSuite
	    public void afterSuite() {
	        SendEmailReport.sendReport();
	    }
	}


