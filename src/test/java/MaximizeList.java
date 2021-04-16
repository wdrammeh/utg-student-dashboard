import core.Portal;
import core.driver.MDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

public class MaximizeList {

    public static void main(String[] args) {
        final FirefoxDriver driver = MDriver.forgeNew(false);
//        MDriver.attemptLogin(driver, "lb21612105@utg.edu.gm", "21612105");
        MDriver.attemptLogin(driver, "md21712494@utg.edu.gm", "Student@21712494");
        final WebDriverWait waiter = MDriver.newDefaultWait(driver);
        final WebElement admissionAlert = waiter.until(ExpectedConditions.presenceOfElementLocated(By.className("gritter-title")));
        System.out.println(admissionAlert.getText());
        driver.navigate().to(Portal.CONTENTS_PAGE);
        final WebElement registrationAlert = waiter.until(ExpectedConditions.presenceOfElementLocated(By.className("gritter-title")));
        System.out.println(registrationAlert.getText());

//        we want to maximize the course-list drop-down
//        noticeably appears at RUNNING COURSE, PROGRAM COURSES Tab
//        changes to one is independent of the other
        final WebElement dropDown = driver.findElementByCssSelector("#semester-table_length > label:nth-child(1) > select:nth-child(1)");
        dropDown.click();
        final List<WebElement> options = dropDown.findElements(By.tagName("option"));
        for (WebElement e : options) {
            System.out.println(e.getText());
        }
        options.get(options.size() - 1).click();
    }

}
