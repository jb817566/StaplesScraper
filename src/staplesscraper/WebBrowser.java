package staplesscraper;

import java.io.File;
import java.net.URL;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.internal.ProfilesIni;

public class WebBrowser {

    private FirefoxProfile fx = null;
    private WebDriver wd = null;

    public WebBrowser() {
        fx = new FirefoxProfile();
        fx.setPreference("browser.helperApp.neverAsk.saveToDisk", "application/pdf");
    }

    public WebDriver getBrowser() {
        ProfilesIni allProfiles = new ProfilesIni();
        System.setProperty("webdriver.chrome.driver", getDriverPath() + "chromedriver.exe");
        try {
//            wd = new PhantomJSDriver(caps);
            wd = new ChromeDriver();
            return wd;
        } catch (Exception e) {
            System.out.println("Getting page failed");
            e.printStackTrace();
            return null;
        }
    }
    public String getDriverPath() {
      URL url = StaplesScraper.class.getProtectionDomain().getCodeSource().getLocation();
      return (new File(url.getFile().toString().substring(1)).getParent() + File.separator);
      
    }
    
}
