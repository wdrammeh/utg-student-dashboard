package core.first;

import core.Portal;
import core.driver.MDriver;
import core.module.Course;
import core.module.ModuleHandler;
import core.module.RegisteredCourse;
import core.module.RunningCourseActivity;
import core.utils.App;
import core.utils.Globals;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringJoiner;

/**
 * Todo: to save time, setting-up of the driver should begin (separately) since {@link utg.Dashboard}.
 *  And this type is to wait on that process, if not completed already.
 *
 * Also, Dashboard is to support multiple driver in a future release.
 * @see MDriver
 */
public class PrePortal {
    private static String email, password, temporaryName;
    private static FirefoxDriver driver; // may be delivered to Portal after task
    private static WebDriverWait loadWaiter;
    private static boolean isTerminated;
    public static final ArrayList<String> USER_DATA = new ArrayList<>();
    public static final ActionListener CANCEL_LISTENER = e-> {
        if (App.showYesNoCancelDialog(Login.getRoot(), "Confirm", "Do you really want to cancel the process?")) {
            isTerminated = true;
            Login.getInstance().dispose();
            if (driver != null) {
                driver.quit();
                driver = null;
            }
        }
    };


    public static void launchVerification(String email, String password){
        PrePortal.email = email;
        PrePortal.password = password;
        isTerminated = false;
        Login.appendToStatus("Setting up the web driver....... Please wait");
        fixingDriver();
        if (driver == null) {
            if (!isTerminated) {
                App.reportMissingDriver(Login.getRoot());
                Login.setInputState(true);
            }
            return;
        }
        Login.replaceLastUpdate("Setting up the driver....... Successful");
        Login.appendToStatus("Now contacting utg.gm.......");
        loadWaiter = new WebDriverWait(driver, Portal.MAXIMUM_WAIT_TIME);
//        make sure we are at the login page
        if (MDriver.isOnPortal(driver)) {
            final int logoutAttempt = MDriver.attemptLogout(driver);
            if (logoutAttempt != MDriver.ATTEMPT_SUCCEEDED) {
                if (!isTerminated) {
                    Login.replaceLastUpdate("Now contacting utg....... Failed");
                    App.reportConnectionLost(Login.getRoot());
                    Login.setInputState(true);
                }
                return;
            }
        }
//        then proceed
        final int loginAttempt = MDriver.attemptLogin(driver, email, password);
        if (isTerminated) {
            return;
        }
        if (loginAttempt == MDriver.ATTEMPT_SUCCEEDED) {
            Login.replaceLastUpdate("Now contacting utg....... Ok");
            temporaryName = driver.findElement(By.className("media-heading")).getText();
            Login.appendToStatus("Login successfully : " + temporaryName);
            launchReading();
        } else if (loginAttempt == MDriver.ATTEMPT_FAILED) {
            Login.replaceLastUpdate("Now contacting utg....... Done");
            Login.appendToStatus("Verification failed : No such student");
            App.reportError(Login.getRoot(),"Invalid Credentials","The information you provided,\n" +
                    "Email: "+email+"\nPassword: "+password+"\n" +
                    "do not match any student. Please try again.");
            Login.setInputState(true);
        } else {
            Login.appendToStatus("Connection lost");
            App.reportConnectionLost(Login.getRoot());
            Login.setInputState(true);
        }
    }

    private static void fixingDriver(){
        if (driver == null) {
            driver = MDriver.forgeNew(true);
        }
    }

    /**
     * Once verification succeeds, reading follows.
     * Error in reading a specific detail is ignored,
     * and only comes to the user's notice on enlisting.
     * @see #enlistDetail(String, String)
     */
    private static void launchReading(){
        String firstName = "", lastName = "", matNumber = "", program = "", major = "",
                school = "", division = "", nationality = "", MOA = "", YOA = "",
                address = "", mStatus = "", DOB = "", tel = "", ongoingSemester, level, status;

//        checking for busyness of the portal, i.e is Course Evaluation required?
        if (Portal.isEvaluationNeeded(driver)) {
            if (!isTerminated) {
                Login.appendToStatus("Busy portal: Course Evaluation needed");
                Portal.reportEvaluationNeeded(Login.getRoot());
                Login.setInputState(true);
            }
            return;
        }
//        extract the admission notice herein the home-page
        try {
            final WebElement admissionAlert = loadWaiter.until(ExpectedConditions.presenceOfElementLocated(By.className("gritter-title")));
            Portal.setAdmissionNotice(admissionAlert.getText());
        } catch (Exception e) {
            App.silenceException("Failed to set 'Admission Notice'");
        }

        Login.appendToStatus("Now processing details.......");
        Login.appendToStatus("Operation may take longer based on your internet signal or temporary server issues");
//        going to the contents page
        try {
            driver.navigate().to(Portal.CONTENTS_PAGE);
        } catch (Exception e1) {
            if (!isTerminated) {
                App.reportConnectionLost(Login.getRoot());
                Login.setInputState(true);
            }
            return;
        }
        try { // extracting the Registration Notice...
            final WebElement registrationAlert = loadWaiter.until(ExpectedConditions.presenceOfElementLocated(By.className("gritter-title")));
            Portal.setRegistrationNotice(registrationAlert.getText());
        } catch (Exception e) {
            App.silenceException("Failed to set 'Registration Notice'");
        }

        final String[] fullName = temporaryName.split(" ");
        firstName = fullName[fullName.length - 1];
        final StringJoiner nameJoiner = new StringJoiner(" ");
        for (int i = 0; i < fullName.length - 1; i++) {
            nameJoiner.add(fullName[i]);
        }
        lastName = nameJoiner.toString();

        try {
            program = driver.findElementByXPath("/html/body/section/div[2]/div/div[1]/div/div[2]/div[2]/div[1]/div/h4").getText();
            major = program.contains("Unknown") ? "Unknown" : program.split(" ")[4];
        } catch (Exception e) {
            App.silenceException(e);
        }
        final List<WebElement> iGroup = driver.findElementsByClassName("info-group");
        level = iGroup.get(2).getText().split("\n")[1];
        status = iGroup.get(3).getText().split("\n")[1];
        try {
            school = iGroup.get(1).getText().split("\n")[1];
            school = school.replace("School of ", "");//if it's there
        } catch (Exception ignored) {
        }
        try {
            division = iGroup.get(0).getText().split("\n")[1];
            division = division.replace("Division of ", "").replace("Department of", "");
        } catch (Exception ignored) {
        }
        final String[] findingSemester = iGroup.get(6).getText().split("\n")[0].split(" ");
        ongoingSemester = String.join(" ", findingSemester[0], findingSemester[1], findingSemester[2]);

//        going to the profile page
        if (isTerminated) {
            return;
        }

        try {
            driver.navigate().to(Portal.PROFILE_PAGE);
        } catch (Exception e1) {
            if (!isTerminated) {
                App.reportConnectionLost(Login.getRoot());
                Login.setInputState(true);
            }
            return;
        }
        try {
            matNumber = driver.findElementByCssSelector("initialModules, strong").getText().split(" ")[1];
        } catch (Exception ignored) {
        }
        final List<WebElement> detail = driver.findElementsByClassName("info-group");
        try {
            address = detail.get(0).getText().split("\n")[1];
        } catch (Exception ignored) {
        }
        try {
            tel = detail.get(2).getText().split("\n")[1];
        } catch (Exception ignored) {
        }
        try {
            mStatus = detail.get(3).getText().split("\n")[1];
        } catch (Exception ignored) {
        }
        try {
            final String[] DOBParts = detail.get(4).getText().split("\n")[1].split(" ");
            DOB = DOBParts[0]+" "+DOBParts[1]+" "+DOBParts[2];
        } catch (Exception ignored) {
        }
        try {
            nationality = detail.get(5).getText().split("\n")[1];
        } catch (Exception ignored) {
        }
        final String[] admissionDate = detail.get(6).getText().split("\n");
        try {
            YOA = admissionDate[1].split("-")[0];
        } catch (Exception ignored) {
        }
        try {
            MOA = admissionDate[1].split("-")[1];
        } catch (Exception ignored) {
        }
//        adding in predefined order... CGPA will be added with transcript later on
        enlistDetail("First Name", firstName);
        enlistDetail("Last Name", lastName);
        enlistDetail("Program", program);
        enlistDetail("Mat#", matNumber); // silenced
        enlistDetail("Major", major);
        enlistDetail("School", school);
        enlistDetail("Division", division);
        enlistDetail("Nationality", nationality);
        enlistDetail("Month of Admission", MOA);
        enlistDetail("Year of Admission", YOA);
        enlistDetail("Address", address);
        enlistDetail("Marital status", mStatus);
        enlistDetail("Birth Day", DOB);
        enlistDetail("Telephone", tel);
        enlistDetail("Email", email);
        enlistDetail("psswd", password); // silenced
        enlistDetail("Current Semester", ongoingSemester);
        enlistDetail("Level", level);
        enlistDetail("Status", status);

        Login.appendToStatus("#####");
        Login.appendToStatus("Collecting up all your courses....... This may take a while");
//        back to the contents to generate modules
        if (isTerminated) {
            return;
        }

        try {
            driver.navigate().to(Portal.CONTENTS_PAGE);
            loadWaiter.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.tagName("table")));
        } catch (Exception e) {
            if (!isTerminated) {
                App.reportConnectionLost(Login.getRoot());
                Login.setInputState(true);
            }
            return;
        }

        if (isTerminated) {
            return;
        }

        final List<WebElement> tabs = loadWaiter.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(".nav-tabs > li")));
//        Firstly, code, name, year, semester, and credit hours at transcript tab
//        Addition to startupCourses is only here; all the following loops only updates the details. this eradicates the possibility of adding running courses at tab-4
//        tabs.get(7).click();  // depreciated!
        Portal.getTabElement("Transcript", tabs).click();
        final WebElement transcriptTable = driver.findElementByCssSelector(".table-bordered");
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
                ModuleHandler.STARTUP_COURSES.add(new Course(vYear, vSemester, data.get(1).getText(), data.get(2).getText(),
                        "", "", "", "", 0.0, Integer.parseInt(data.get(3).getText()),"",true));
            }
        }
        final WebElement surrounds = driver.findElementsByCssSelector(".pull-right").get(3);
        final String cgpa = surrounds.findElements(By.tagName("th")).get(1).getText();
        enlistDetail("cgpa", cgpa);

//        Secondly, add scores at grades tab
//        tabs.get(6).click();  // depreciated!
        Portal.getTabElement("Grades", tabs).click();
        final WebElement gradesTable = driver.findElementsByCssSelector(".table-warning").get(1);
        final WebElement tBody = gradesTable.findElement(By.tagName("tbody"));
        final List<WebElement> rows = tBody.findElements(By.tagName("tr"));
        for(WebElement t : rows){
            final List<WebElement> data = t.findElements(By.tagName("td"));
            for (Course c : ModuleHandler.STARTUP_COURSES) {
                if (c.getCode().equals(data.get(0).getText())) {
                    c.setScore(Double.parseDouble(data.get(6).getText()));
                }
            }
        }

//        Finally, available lecturer names at all-registered tab
//        tabs.get(4).click();  // depreciated!
        Portal.getTabElement("All Registered Courses", tabs).click();
        final WebElement allRegisteredTable = driver.findElementByCssSelector(".table-warning");
        final WebElement tableBody = allRegisteredTable.findElement(By.tagName("tbody"));
        final List<WebElement> allRows = tableBody.findElements(By.tagName("tr"));
        int t = 0;
        while (t < allRows.size()) {
            final List<WebElement> instantRow = allRows.get(t).findElements(By.tagName("td"));
            for (Course c : ModuleHandler.STARTUP_COURSES) {
                if (c.getCode().equals(instantRow.get(0).getText())) {
                    c.setLecturer(instantRow.get(2).getText(), false);
                }
            }
            t++;
        }

//        Available running courses? add
        final List<WebElement> captions = tableBody.findElements(By.cssSelector("b, strong"));
        final boolean running = captions.get(captions.size() - 1).getText().equalsIgnoreCase(ongoingSemester);
        int runningCount = 0;
        if (running) {
            int match = allRows.size() - 1;
            while (!allRows.get(match).getText().equalsIgnoreCase(ongoingSemester)){
                final List<WebElement> data = allRows.get(match).findElements(By.tagName("td"));
                RunningCourseActivity.STARTUP_REGISTRATIONS.add(new RegisteredCourse(data.get(0).getText(),
                        data.get(1).getText(), data.get(2).getText(), data.get(3).getText(), data.get(4).getText(),
                        "", "", true));
                match--;
                runningCount++;
            }
        }

        final int courseCount = rows.size();
        final int semesterCount = semCaptions.size();
        Login.appendToStatus("Successfully found "+ Globals.checkPlurality(courseCount, "courses")+" in "+ Globals.checkPlurality(semesterCount, "semesters"));
        if (runningCount == 0) {
            Login.appendToStatus("No registration detected this semester");
        } else if (runningCount == 1) {
            Login.appendToStatus("Plus 1 registration this semester");
        } else {
            Login.appendToStatus("Plus "+runningCount+" registered courses this semester");
        }

        new Thread(()-> driver.quit()).start();
        Portal.setLastLogin(new Date());
        Login.appendToStatus("#####");
        Login.notifyCompletion();
    }

    private static void enlistDetail(String key, String value){
        if (Globals.hasNoText(value) || value.contains("Unknown")) {
            Login.appendToStatus("**Warning: " + key + " not found");
        } else if(!(key.equals("Mat#") || key.equals("psswd") || key.equals("cgpa"))) {
            Login.appendToStatus(key+": "+value);
        }

        USER_DATA.add(value);
    }

}
