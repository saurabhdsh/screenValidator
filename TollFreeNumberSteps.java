package stepDefinitions;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.cucumber.java.en.*;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import org.openqa.selenium.By;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TollFreeNumberSteps {

    private AppiumDriver<MobileElement> driver;
    private ExtentReports extentReports;
    private ExtentTest extentTest;
    private String platform;

    // Regex for toll-free number format
    private static final String TOLL_FREE_REGEX = "1-\\d{3}-\\d{3}-\\d{4}";
    private Pattern tollFreePattern;

    public TollFreeNumberSteps() {
        // Initialize the regex pattern
        tollFreePattern = Pattern.compile(TOLL_FREE_REGEX);

        // Initialize ExtentReports
        ExtentHtmlReporter htmlReporter = new ExtentHtmlReporter("extent-report.html");
        extentReports = new ExtentReports();
        extentReports.attachReporter(htmlReporter);
        extentTest = extentReports.createTest("Toll-Free Number Detection Test");
    }

    @Given("^the mobile app is open on \"(iOS|Android)\"$")
    public void openMobileApp(String platformName) throws Exception {
        platform = platformName;
        // Initialize Appium driver based on platform
        AppiumSetup appiumSetup = new AppiumSetup();
        driver = appiumSetup.setUp(platformName);
        extentTest.info("Mobile app is open on " + platformName);
    }

    @When("^I search for toll-free numbers in the app$")
    public void searchTollFreeNumbers() {
        List<MobileElement> allTextElements;

        // Use platform-specific locator strategy
        if (platform.equalsIgnoreCase("Android")) {
            allTextElements = driver.findElements(By.className("android.widget.TextView"));
        } else {
            allTextElements = driver.findElements(By.className("XCUIElementTypeStaticText"));
        }

        for (MobileElement element : allTextElements) {
            String elementText = element.getText();
            Matcher matcher = tollFreePattern.matcher(elementText);

            if (matcher.find()) {
                String screenName = driver.getTitle(); // Get screen name or title (iOS) or equivalent for Android
                String tollFreeNumber = matcher.group();
                extentTest.info("Found toll-free number: " + tollFreeNumber + " on screen: " + screenName);
            }
        }
    }

    @Then("^I should log the screen names containing toll-free numbers in the report$")
    public void logScreenNames() {
        extentTest.pass("Logged all screens with toll-free numbers");
        extentReports.flush();
    }
}
