package main.module;

import core.Portal;
import core.driver.MDriver;
import core.module.RegisteredCourse;
import core.utils.App;
import core.utils.Globals;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.StringJoiner;
import java.util.regex.Pattern;

public class Register {

    public static void main(String[] args) {
//        This is a registration attempt
        final Scanner scanner = new Scanner(System.in);
        System.out.print("Enter email: ");
        final String email = scanner.nextLine();
        System.out.print("Enter password: ");
        final String password = scanner.nextLine();
        final String courseCode = "mth101"; // to be provided
        final String courseName = "Calculus 1"; // to be provided
        final String key = String.join(" ",
                normalizeCode(courseCode), courseName.toLowerCase());
        final RemoteWebDriver driver = MDriver.forgeNew(false);
        if (driver == null) {
            System.err.println("[ERROR] Unable to build the driver");
            return;
        }
        final int loginAttempt = MDriver.attemptLogin(driver, email, password);
        if (loginAttempt == MDriver.ATTEMPT_SUCCEEDED) {
            String name = driver.findElement(By.className("media-heading")).getText();
            System.out.println("Login successfully: "+name);
            final WebDriverWait loadWaiter = new WebDriverWait(driver, Duration.ofSeconds(Portal.MAXIMUM_WAIT_TIME));
            driver.navigate().to(Portal.CONTENTS_PAGE);
            loadWaiter.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.tagName("table")));
            final List<WebElement> tabs = loadWaiter.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(".nav-tabs > li")));
            final WebElement runningElement = Portal.getTabElement("Running Courses", tabs);
            if (runningElement == null) {
                System.err.println("[ERROR] Connection lost");
                return;
            }
            runningElement.click();
//            Todo maximize the drop-down list here
//            driver.findElementByCssSelector("#semester-table_filter > label > input").sendKeys(key);
            driver.findElement(By.cssSelector("#semester-table_filter > label > input")).sendKeys(key);
//            WebElement processElement = driver.findElementById("semester-table_processing");
            WebElement processElement = driver.findElement(By.id("semester-table_processing"));
            loadWaiter.until(ExpectedConditions.invisibilityOf(processElement));
//            final WebElement semesterTable = driver.findElementById("semester-table");
            final WebElement semesterTable = driver.findElement(By.id("semester-table"));
            final WebElement body = semesterTable.findElement(By.tagName("tbody"));
            final List<WebElement> rows = body.findElements(By.tagName("tr"));

            if (rows.size() == 1 && rows.get(0).findElements(By.tagName("td")).size() == 1) {
                System.out.printf("No matching results found for '%s' '%s'",
                        courseCode, courseName);
                System.out.println("Search using a valid Course Code, and Name.");
                return;
            }

            final ArrayList<RunningCourse> foundRunningModules = new ArrayList<>();
            for (WebElement row : rows) {
                final List<WebElement> data = row.findElements(By.tagName("td"));
                final String[] nameExt = data.get(2).getText().split("[ ]");
                final int numbRegistered = Integer.parseInt(nameExt[nameExt.length - 1]);
                final StringJoiner nameJoiner = new StringJoiner(" ");
                for (int i = 0; i < nameExt.length - 2; i++) {
                    nameJoiner.add(nameExt[i]);
                }
                final String[] dayTime = data.get(7).getText().split("[ ]");
                final String day;
                final String time;
                if (dayTime.length == 2) {
                    day = dayTime[0];
                    time = dayTime[1].split("[-]")[0];
                } else if (dayTime.length == 1){
                    // Summer semester?
                    time = dayTime[0].split("[-]")[0];
                } else {
                    day = time = "";
                }
                final String action = data.get(data.size() - 1).getText();
                final boolean registered = Globals.hasText(action) && !action.equalsIgnoreCase("Register");
                foundRunningModules.add(new RunningCourse(data.get(1).getText(), nameJoiner.toString(), numbRegistered,
                        Integer.parseInt(data.get(3).getText()), data.get(4).getText(), data.get(5).getText(),
                        data.get(6).getText(), dayTime[0], dayTime[1].split("[-]")[0], registered));
            }

            final int rIndex;
            if (foundRunningModules.size() == 1) {
                final RunningCourse module = foundRunningModules.get(0);
                System.out.println("[INFO] Found the following course:");
                System.out.printf("\tCode<%s> Name<%s> Lecturer<%s> Venue<%s> Room<%s> Time<%s> " +
                        "Class-Size<%d> Number-Registered<%d> Status<%s>%n", module.getCode(), module.getName(),
                        module.getLecturer(), module.getCampus(), module.getRoom(), module.getTime(), module.classSize,
                        module.numberRegistered, module.getStatus());
                if (module.isRegistered) {
                    System.out.println("You've already registered this course.");
                    return;
                } else if (module.isClassFull()) {
                    System.err.println("[INFO] This class is already full.");
                    return;
                } else {
                    System.out.print("Do you want to register this course? [Yes/No]: ");
                    final String choice = scanner.nextLine().strip();
                    if (choice.equalsIgnoreCase("N") || choice.equalsIgnoreCase("No")) {
                        System.out.println("[INFO] Operation cancelled");
                        return;
                    } else {
                        rIndex = 0;
                    }
                }
            } else {
                System.out.println("[INFO] Found the following courses:");
                int var = 1;
                for (RunningCourse module : foundRunningModules) {
                    System.out.printf("%d. Code<%s> Name<%s> Lecturer<%s> Venue<%s> Room<%s> Time<%s> " +
                                    "Class-Size<%d> Number-Registered<%d> Status<%s>%n", var++, module.getCode(), module.getName(),
                            module.getLecturer(), module.getCampus(), module.getRoom(), module.getTime(), module.classSize,
                            module.numberRegistered, module.getStatus());
                }
                System.out.print("Enter the number you want to register for: ");
                try {
                    final int i = Integer.parseInt(scanner.nextLine().strip());
                    if (i < 1 || i > foundRunningModules.size()) {
                        System.err.println("[ERROR] The number cannot be less than 1, or greater than the list size");
                        return;
                    }
                    rIndex = i - 1;
                    final RunningCourse module = foundRunningModules.get(rIndex);
                    if (module.isRegistered) {
                        System.out.println("You've already registered that course.");
                        return;
                    } else if (module.isClassFull()) {
                        System.err.println("[ERROR] This class is already full.");
                        return;
                    }
                } catch (NumberFormatException e) {
                    System.err.println("[ERROR] That is not an integer");
                    return;
                }
            }
            scanner.close();

//            Now, let's register this...
            final List<WebElement> rowElements = rows.get(rIndex).findElements(By.tagName("td"));
            WebElement registerElement = rowElements.get(rowElements.size() - 1);
            if (registerElement.getText().strip().equalsIgnoreCase("Register")) {
                registerElement.findElement(By.className("reg")).click();
                WebElement okButton = loadWaiter.until(ExpectedConditions.presenceOfElementLocated(By.xpath("/html/body/div[1]/div/div/div[2]/button[2]")));
                okButton.click();

                WebElement respDialog = loadWaiter.until(ExpectedConditions.presenceOfElementLocated(By.xpath("/html/body/div[1]/div/div/div[1]")));
//                WebElement respondMsg = driver.findElementByCssSelector(".bootbox-body > h3:nth-child(1)");
//                WebElement respondMsg = driver.findElementByCssSelector(".bootbox-body > h3:nth-child(1)");
                WebElement respondMsg = driver.findElement(By.cssSelector(".bootbox-body > h3:nth-child(1)"));
                final String respondText = respondMsg.getText().strip();
                System.out.println("[INFO] "+respondText);
//                WebElement okBtn = driver.findElementByCssSelector(".btn-primary");
                WebElement okBtn = driver.findElement(By.cssSelector(".btn-primary"));
                okBtn.click();

                if (respondText.startsWith("Course Registered Successfully...")) {
                    // add this course to Dashboard
                }
            } else {
                System.err.println("[ERROR] Registration failed.");
                System.err.println("It's either no registration is available, or you're not cleared.");
            }
        } else if (loginAttempt == MDriver.ATTEMPT_FAILED) {
            System.out.println("Verification failed. No such student:");
            System.out.println("Email: "+email);
            System.out.println("Password: "+password);
        } else {
            System.err.println("Connection lost");
        }

//        driver.quit();
    }

    private static String normalizeCode(String code){
        String text = code.strip().toLowerCase();
        final String regex = "[a-z]{3,4}[ ]*[0-9]{3}";
        if (Pattern.matches(regex, text)) {
            final int length = text.length();
            final int midIndex = length - 3;
            return String.join(" ",
                    text.substring(0, midIndex), text.substring(midIndex, length));
        } else {
            System.err.printf("[WARNING] unrecognized code format: '%s'%n", code);
            return text;
        }
    }


    public static class RunningCourse extends RegisteredCourse {
        private int classSize;
        private int numberRegistered;
        private boolean isRegistered;

        public RunningCourse(String code, String name, int classSize, int numbRegistered, String lecturer,
                             String campus, String room, String day, String time, boolean registered) {
            super(code, name, lecturer, campus, room, day, time, true);
            this.classSize = classSize;
            this.numberRegistered = numbRegistered;
            this.isRegistered = registered;
        }

        public boolean isClassFull(){
            return numberRegistered < classSize;
        }

        public String getStatus(){
            if (isRegistered) {
                return "Registered";
            } else if (classSize == numberRegistered) {
                return "Class full";
            } else {
                return "Not registered";
            }
        }
    }

    public static ChromeDriver forgeNew(boolean headless) {
        try {
            WebDriverManager.chromedriver().setup();
            final ChromeOptions options = new ChromeOptions();
            options.setHeadless(headless);
            final ChromeDriver driver = new ChromeDriver(options);
            return driver;
        } catch (Exception e) {
            App.silenceException(e);
            return null;
        }
    }

}
