import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.security.AnyTypePermission;
import core.App;
import core.Course;
import core.MDriver;
import core.Portal;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;
import java.util.List;

public class TranscriptTable {
    private static FirefoxDriver modulesDriver;


    public static void main(String[] args) {
        modulesDriver = MDriver.forgeNew(true);

        final WebDriverWait loadWaiter = new WebDriverWait(modulesDriver, 30);
        final int loginAttempt = MDriver.attemptLogin(modulesDriver, "md21712494@utg.edu.gm", "Student@21712494");
        if (loginAttempt == MDriver.ATTEMPT_SUCCEEDED) {
            if (Portal.isEvaluationNeeded(modulesDriver)) {
                Portal.reportEvaluationNeeded();
                return;
            }
        } else if (loginAttempt == MDriver.ATTEMPT_FAILED) {
            App.reportLoginAttemptFailed();
            return;
        } else if (loginAttempt == MDriver.CONNECTION_LOST) {
            App.reportConnectionLost();
            return;
        }

        final List<WebElement> tabs;
        try {
            modulesDriver.navigate().to(Portal.CONTENTS_PAGE);
            tabs = loadWaiter.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(".nav-tabs > li")));
        } catch (Exception e) {
            App.reportConnectionLost();
            return;
        }

        Portal.getTabElement("Transcript", tabs).click();
        final ArrayList<Course> foundCourses = new ArrayList<>();
        final WebElement transcriptTable = modulesDriver.findElementByCssSelector(".table-bordered");
        final WebElement transBody = transcriptTable.findElement(By.tagName("tbody"));
        final List<WebElement> transRows = transBody.findElements(By.tagName("tr"));
        final List<WebElement> semCaptions = transBody.findElements(By.className("warning"));
        String vYear = null;
        String vSemester = null;
        for (WebElement transRow : transRows) {
            if (transRow.getText().contains("Semester")) {
                vYear = transRow.getText().split(" ")[0];
                vSemester = transRow.getText().split(" ")[1]+" Semester";
            } else {
                final List<WebElement> data = transRow.findElements(By.tagName("td"));
                foundCourses.add(new Course(vYear, vSemester, data.get(1).getText(), data.get(2).getText(),
                        "", "", "", "", 0.0, Integer.parseInt(data.get(3).getText()),
                        "", true));
            }
        }

        XStream xStream = new XStream(new DomDriver());
        xStream.addPermission(AnyTypePermission.ANY);
        System.out.println(xStream.toXML(foundCourses.toArray()));
    }

}
