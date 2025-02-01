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
        // This is a course registration attempt

        // # Enter email and password
        final Scanner scanner = new Scanner(System.in);
        System.out.print("Enter email: ");
        final String email = scanner.nextLine();
        System.out.print("Enter password: ");
        final String password = scanner.nextLine();

        // # Init web driver
        final RemoteWebDriver driver = MDriver.forgeNew(false);
        if (driver == null) {
            System.err.println("[ERROR] Unable to build the driver.");
            scanner.close();
            return;
        }

        // # Attemp log in
        final int loginAttempt = MDriver.attemptLogin(driver, email, password);
        if (loginAttempt == MDriver.ATTEMPT_FAILED) {
            System.err.println("[ERROR] Invalid credentials. Please try again.");
            scanner.close();
            return;
        }

        if (loginAttempt != MDriver.ATTEMPT_SUCCEEDED) {
            System.err.println("[ERROR] Connection lost.");
            scanner.close();
            return;
        }

        String name = driver.findElement(By.className("media-heading")).getText();
        System.out.println("[INFO]: Login successfully '"+name+"'.");

        final WebDriverWait loadWaiter = new WebDriverWait(driver, Duration.ofSeconds(Portal.MAXIMUM_WAIT_TIME));
        driver.navigate().to(Portal.CONTENTS_PAGE);
        loadWaiter.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.tagName("table")));
        final List<WebElement> tabs = loadWaiter.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(".nav-tabs > li")));
        final WebElement runningElement = Portal.getTabElement("Running Courses", tabs);
        if (runningElement == null) {
            System.err.println("[ERROR] Connection lost.");
            scanner.close();
            return;
        }
        runningElement.click();

        // Todo maximize the drop-down list here

        // # Enter query
        // final String courseCode = "mth101";
        // final String courseName = "Calculus 1";
        // final String key = String.join(" ",
        //         normalizeCode(courseCode), courseName.toLowerCase());
        System.out.print("Enter course code/name: ");
        final String key = scanner.nextLine();

        driver.findElement(By.cssSelector("#semester-table_filter > label > input")).sendKeys(key);
        WebElement processElement = driver.findElement(By.id("semester-table_processing"));
        loadWaiter.until(ExpectedConditions.invisibilityOf(processElement));
        final WebElement semesterTable = driver.findElement(By.id("semester-table"));
        final WebElement body = semesterTable.findElement(By.tagName("tbody"));
        final List<WebElement> rows = body.findElements(By.tagName("tr"));
        if (rows.size() == 1 && rows.get(0).findElements(By.tagName("td")).size() == 1) {
            System.out.printf("No matching results found for '%s'\n", key);
            System.out.println("Search using a valid Course Code or Name.");
            scanner.close();
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
            final String action = data.get(data.size() - 1).getText();
            final boolean registered = Globals.hasText(action) && !action.equalsIgnoreCase("Register");
            foundRunningModules.add(new RunningCourse(data.get(1).getText(), nameJoiner.toString(),
                    Integer.parseInt(data.get(3).getText()), numbRegistered, data.get(4).getText(), data.get(5).getText(),
                    data.get(6).getText(), data.get(7).getText(), registered));
        }

        final int rIndex;
        if (foundRunningModules.size() == 1) {
            final RunningCourse module = foundRunningModules.get(0);
            System.out.println("[INFO] Found the following course:");
            System.out.printf("\tCode<%s> Name<%s> Lecturer<%s> Venue<%s> Room<%s> Time<%s> " +
                    "Class-Size<%d> Number-Registered<%d> Status<%s>%n", module.getCode(), module.getName(),
                    module.getLecturer(), module.getCampus(), module.getRoom(), module.dayTime, module.classSize,
                    module.numberRegistered, module.getStatus());
            if (module.isRegistered) {
                System.out.println("You've already registered this course.");
                scanner.close();
                return;
            } else if (module.isClassFull()) {
                System.err.println("[INFO] This class is already full.");
                scanner.close();
                return;
            } else {
                System.out.print("Do you want to register this course? [Yes/No]: ");
                final String choice = scanner.nextLine().strip();
                if (choice.equalsIgnoreCase("N") || choice.equalsIgnoreCase("No")) {
                    System.out.println("[INFO] Operation cancelled.");
                    scanner.close();
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
                        module.getLecturer(), module.getCampus(), module.getRoom(), module.dayTime, module.classSize,
                        module.numberRegistered, module.getStatus());
            }
            System.out.print("Enter the number you want to register for: ");
            try {
                final int i = Integer.parseInt(scanner.nextLine().strip());
                if (i < 1 || i > foundRunningModules.size()) {
                    System.err.println("[ERROR] Invalid list number.");
                    scanner.close();
                    return;
                }
                rIndex = i - 1;
                final RunningCourse module = foundRunningModules.get(rIndex);
                if (module.isRegistered) {
                    System.out.println("You've already registered that course.");
                    scanner.close();
                    return;
                } else if (module.isClassFull()) {
                    System.err.println("[ERROR] This class is already full.");
                    scanner.close();
                    return;
                }
            } catch (NumberFormatException e) {
                System.err.println("[ERROR] Is that a number?");
                return;
            }
        }
        scanner.close();

        // Now, let's register this...
        final List<WebElement> rowElements = rows.get(rIndex).findElements(By.tagName("td"));
        WebElement registerElement = rowElements.get(rowElements.size() - 1);
        if (registerElement.getText().strip().equalsIgnoreCase("Register")) {
            registerElement.findElement(By.className("reg")).click();
            WebElement okButton = loadWaiter.until(ExpectedConditions.presenceOfElementLocated(By.xpath("/html/body/div[1]/div/div/div[2]/button[2]")));
            okButton.click();

            WebElement respDialog = loadWaiter.until(ExpectedConditions.presenceOfElementLocated(By.xpath("/html/body/div[1]/div/div/div[1]")));
            WebElement respondMsg = driver.findElement(By.cssSelector(".bootbox-body > h3:nth-child(1)"));
            final String respondText = respondMsg.getText().strip();
            System.out.println("[INFO] "+respondText);
            // WebElement okBtn = driver.findElementByCssSelector(".btn-primary");
            WebElement okBtn = driver.findElement(By.cssSelector(".btn-primary"));
            okBtn.click();

            if (respondText.startsWith("Course Registered Successfully...")) {
                // Add this course to Dashboard
            }
        } else {
            System.err.println("[ERROR] Registration failed.");
            System.err.println("It's either no registration is available, or you're not cleared.");
        }

    //    driver.quit();
    }

    private static String normalizeCode(String code) {
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
        private String dayTime;

        public RunningCourse(String code, String name, int classSize, int numbRegistered, String lecturer,
                             String campus, String room, String dayTime, boolean registered) {
            super(code, name, lecturer, campus, room, "", "", true);
            this.classSize = classSize;
            this.numberRegistered = numbRegistered;
            this.isRegistered = registered;
            this.dayTime = dayTime;
        }

        public boolean isClassFull() {
            return numberRegistered == classSize;
        }

        public String getStatus() {
            if (isRegistered) {
                return "Registered";
            } else if (classSize == numberRegistered) {
                return "Class full";
            } else {
                return "Not registered";
            }
        }
    }

    // public static ChromeDriver forgeNew(boolean headless) {
    //     try {
    //         WebDriverManager.chromedriver().setup();
    //         final ChromeOptions options = new ChromeOptions();
    //         options.setHeadless(headless);
    //         final ChromeDriver driver = new ChromeDriver(options);
    //         return driver;
    //     } catch (Exception e) {
    //         App.silenceException(e);
    //         return null;
    //     }
    // }

}
