package core;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Handles driver-related operations, including setting and donating them.
 * In a future release, Dashboard will be flexible with its driver specification,
 * which is currently restricted to only mozilla.
 */
public class MDriver {
    private static boolean isSetup;
    public static final int CONNECTION_LOST = 0;
    public static final int ATTEMPT_FAILED = 1;
    public static final int ATTEMPT_SUCCEEDED = 2;


    /**
     * Returns a fresh firefox driver as appropriate for the currently running OS.
     * First, an attempt will be made to setup the driver, if it's not already.
     * This method may return null, if anything goes wrong in setting up the driver;
     * and it is self-silent.
     * @see #setup()
     */
    public static synchronized FirefoxDriver forgeNew(boolean headless) {
        setup();
        try {
            return new FirefoxDriver(new FirefoxOptions().setHeadless(headless));
        } catch (Exception e) {
            App.silenceException(e);
            return null;
        }
    }

    /**
     * Sets up the firefox driver, if it's not already set.
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
            } catch (Exception ignored) {
                isSetup = false;
            }
        }
    }

    /**
     * Attempts to log this driver in to the Portal using the given email and password.
     * A successful attempt leaves the driver at the {@link Portal#HOME_PAGE}.
     */
    public static int attemptLogin(FirefoxDriver driver, String email, String password) {
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
            new WebDriverWait(driver, Portal.MINIMUM_WAIT_TIME).until(
                    ExpectedConditions.presenceOfElementLocated(By.cssSelector(".alert-danger")));
            return ATTEMPT_FAILED;
        } catch (TimeoutException ignored) {
        }

        try {
            new WebDriverWait(driver, Portal.MAXIMUM_WAIT_TIME).until(
                    ExpectedConditions.presenceOfElementLocated(By.className("media-heading")));
            return ATTEMPT_SUCCEEDED;
        } catch (TimeoutException e) {
            return CONNECTION_LOST;
        }
    }

    /**
     * Attempts a login to the Portal using the current user's credentials.
     * @see #attemptLogin(FirefoxDriver, String, String)
     */
    public static int attemptLogin(FirefoxDriver driver) {
        return attemptLogin(driver, Student.getPortalMail(), Student.getPortalPassword());
    }

    /**
     * Attempts to log this driver out of the Portal.
     * By the time this call returns, the driver will be at the {@link Portal#LOGIN_PAGE}
     */
    public static int attemptLogout(FirefoxDriver driver) {
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
    public static boolean isOnPortal(FirefoxDriver driver) {
        final String url = driver.getCurrentUrl();
        return url.equals(Portal.HOME_PAGE) || url.equals(Portal.CONTENTS_PAGE) || url.equals(Portal.PROFILE_PAGE);
    }

}
