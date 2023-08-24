package core.driver;

import core.Portal;
import core.user.Student;
import core.utils.App;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxDriverLogLevel;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * Handles driver-related operations, including setting and donating them.
 * In a future release, Dashboard will be flexible with its driver specification,
 * which is currently restricted to mozilla firefox.
 */
public class MDriver {
    private static boolean isSetup;
    public static final int CONNECTION_LOST = 0;
    public static final int ATTEMPT_FAILED = 1;
    public static final int ATTEMPT_SUCCEEDED = 2;
    public static final List<FirefoxDriver> DRIVERS = new ArrayList<>();


    /**
     * Returns a fresh firefox driver as appropriate for the currently running OS.
     * First, an attempt will be made to set up the driver, if it's not already.
     * This method may return null, if anything goes wrong in setting up the driver;
     * and it is self-silent.
     * @see #setup()
     */
    public static synchronized FirefoxDriver forgeNew(boolean headless) {
        FirefoxDriver driver = null;
        setup();
        try {
            final FirefoxOptions options = new FirefoxOptions();
            options.setHeadless(headless);
//            options.setLogLevel(FirefoxDriverLogLevel.fromLevel(Level.OFF));
            options.setLogLevel(FirefoxDriverLogLevel.fromLevel(Level.ALL));
            driver = new FirefoxDriver(options);
            DRIVERS.add(driver);
        } catch (Exception e) {
            App.silenceException(e);
        }
        return driver;
    }

    /**
     * Sets up the firefox driver.
     * Always call this:
     * 1) at startup - to save time during runtime;
     * 2) on a different thread.
     * The convention is that, once Dashboard is done building,
     * classes may not independently call this.
     * This method is self-silent.
     * @see #forgeNew(boolean)
     */
    public static void setup(){
        if (!isSetup) {
            try {
                WebDriverManager.firefoxdriver().setup();
                isSetup = true;
            } catch (Exception e) {
                App.silenceException(e);
                isSetup = false;
            }
        }
    }

    /**
     * Attempts to log this driver in to the Portal using the given email and password.
     * A successful attempt leaves the driver at the {@link Portal#HOME_PAGE}.
     */
    public static int attemptLogin(RemoteWebDriver driver, String email, String password) {
        if (isOnPortal(driver)) {
            final int logoutAttempt = attemptLogout(driver);
            if (logoutAttempt == CONNECTION_LOST) {
                return CONNECTION_LOST;
            }
        } else {
            try {
                driver.navigate().to(Portal.LOGIN_PAGE);
                driver.findElement(By.name("email")).sendKeys(email);
                driver.findElement(By.name("password")).sendKeys(password);
                driver.findElement(By.className("form-group")).submit();
            } catch (Exception lost) {
                return CONNECTION_LOST;
            }
        }

        try {
            new WebDriverWait(driver, Duration.ofSeconds(Portal.MINIMUM_WAIT_TIME)).until(
                    ExpectedConditions.presenceOfElementLocated(By.cssSelector(".alert-danger")));
            return ATTEMPT_FAILED;
        } catch (TimeoutException ignored) {
        }

        try {
            new WebDriverWait(driver, Duration.ofSeconds(Portal.MAXIMUM_WAIT_TIME)).until(
                    ExpectedConditions.presenceOfElementLocated(By.className("media-heading")));
            return ATTEMPT_SUCCEEDED;
        } catch (TimeoutException e) {
            return CONNECTION_LOST;
        }
    }

    /**
     * Attempts a login to the Portal using the current user's credentials.
     * @see #attemptLogin(RemoteWebDriver, String, String)
     */
    public static int attemptLogin(FirefoxDriver driver) {
        return attemptLogin(driver, Student.getPortalMail(), Student.getPortalPassword());
    }

    /**
     * Attempts to log this driver out of the Portal.
     * By the time this call returns, the driver will be at the {@link Portal#LOGIN_PAGE}
     */
    public static int attemptLogout(RemoteWebDriver driver) {
        if (isOnPortal(driver)) {
            try {
                driver.navigate().to(Portal.LOGOUT_PAGE);
                return ATTEMPT_SUCCEEDED;
            } catch (Exception failed) {
                return ATTEMPT_FAILED;
            }
        } else {
            try {
                driver.navigate().to(Portal.LOGIN_PAGE);
                return ATTEMPT_SUCCEEDED;
            } catch (Exception lost) {
                return CONNECTION_LOST;
            }
        }
    }

    /**
     * Returns true if the given driver is on the Portal.
     * A driver is considered in the Portal if its url matches any of
     * {@link Portal#HOME_PAGE}, {@link Portal#CONTENTS_PAGE}, or {@link Portal#PROFILE_PAGE}
     */
    public static boolean isOnPortal(RemoteWebDriver driver) {
        final String url = driver.getCurrentUrl();
        return url.equals(Portal.HOME_PAGE) || url.equals(Portal.CONTENTS_PAGE) || url.equals(Portal.PROFILE_PAGE);
    }

    public static WebDriverWait newDefaultWait(FirefoxDriver driver){
        return new WebDriverWait(driver, Duration.ofSeconds(Portal.MAXIMUM_WAIT_TIME));
    }

    /**
     * This is necessary because drivers, instead of closing,
     * disconnect themselves from the program as it exits.
     * Note, also, that most classes do not explicitly quit drivers
     * within runtime, as they reuse them over and over.
     */
    public static void stopAll() {
        for (FirefoxDriver driver : DRIVERS) {
            try {
                driver.quit();
            } catch (Exception ignored) {
            }
        }
    }

}
