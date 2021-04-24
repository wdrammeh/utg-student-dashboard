import core.Portal;
import core.driver.MDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Temporary {

    public static void main(String[] args) {
        final FirefoxDriver driver = MDriver.forgeNew(false);
        MDriver.attemptLogin(driver, "lb21612105@utg.edu.gm", "21612105");
        driver.navigate().to(Portal.CONTENTS_PAGE);
        final WebDriverWait waiter = MDriver.newDefaultWait(driver);
        final WebElement registrationAlert = waiter.until(ExpectedConditions.presenceOfElementLocated(By.className("gritter-title")));
        System.out.println(registrationAlert.getText());
    }

}
