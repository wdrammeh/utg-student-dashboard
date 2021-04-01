package core;

import core.driver.MDriver;
import core.module.RunningCourseActivity;
import core.user.Student;
import core.utils.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.awt.*;
import java.util.Date;
import java.util.List;

/**
 * The Dashboard's abstract Portal representative.
 * The functionality of this class come to life after the student is verified.
 * Never forget to use it on a different thread.
 */
public class Portal {
    public static final String LOGIN_PAGE = "https://utg.gm/login";
    public static final String LOGOUT_PAGE = "https://utg.gm/logout";
    // do not call the contentsPage or profilePage on a driver that has not yet entered...
    public static final String CONTENTS_PAGE = "https://utg.gm/course-registrations";
    public static final String PROFILE_PAGE = "https://utg.gm/profile";
    public static final int MAXIMUM_WAIT_TIME = 30;//intended in seconds
    public static final int MINIMUM_WAIT_TIME = 5;
    /**
     * Note that the HOME_PAGE seems to be pointing
     * to the LOGIN_PAGE when there's no session.
     */
    public static final String HOME_PAGE = "https://utg.gm/home";
    public static final String ADMISSION_PAGE = "https://utg.gm";
    private static String admissionNotice;
    private static String registrationNotice;
    private static Date lastAdmissionNoticeUpdate, lastRegistrationNoticeUpdate;
    private static boolean autoSync = false;
    private static Date lastLogin;
    private static FirefoxDriver portalDriver;


    public static void openPortal(Component clickable){
        clickable.setEnabled(false);
        if (Student.isTrial()) {
            try {
                Internet.visit(LOGIN_PAGE);
            } catch (Exception e) {
                App.reportError(e);
            }
        } else {
            if (Internet.isInternetAvailable()) {
                final int vInt = App.verifyUser("To access your portal, kindly enter your Matriculation Number below:");
                if (vInt == App.VERIFICATION_TRUE) {
                    launchPortal();
                } else if (vInt == App.VERIFICATION_FALSE) {
                    App.reportMatError();
                }
            } else {
                App.reportNoInternet();
            }
        }
        clickable.setEnabled(true);
    }

    /**
     * Meant to launch the portal for the student, and that's all.
     * To remedy a situation of multiple clicks, it accepts a nullable button, which if possible,
     * will enable it after completing the pending charges.
     */
    private static void launchPortal(){
        if (portalDriver == null) {
            portalDriver = MDriver.forgeNew(false);
            if (portalDriver == null) {
                App.reportMissingDriver();
                return;
            }
        }

        final int loginAttempt = MDriver.attemptLogin(portalDriver);
        if (loginAttempt == MDriver.ATTEMPT_SUCCEEDED) {
            portalDriver.navigate().to(Portal.CONTENTS_PAGE);
        }
    }

    /**
     * Returns true if the two notices were found and renewed successfully.
     */
    public static boolean startRenewingNotices(FirefoxDriver noticeDriver, boolean userRequested){
        if (isEvaluationNeeded(noticeDriver)) {
            if (userRequested) {
                reportEvaluationNeeded();
            }
            return false;
        }

        try {
            noticeDriver.navigate().to(HOME_PAGE);
            new WebDriverWait(noticeDriver, 50).until(
                    ExpectedConditions.presenceOfElementLocated(By.className("media-heading")));
            final WebElement admissionElement = new WebDriverWait(noticeDriver, 59).until(
                    ExpectedConditions.presenceOfElementLocated(By.className("gritter-title")));
            setAdmissionNotice(admissionElement.getText());
        } catch (Exception e) {
            if (userRequested) {
                App.reportConnectionLost();
            }
            return false;
        }

        try {
            noticeDriver.navigate().to(CONTENTS_PAGE);
            WebElement registrationElement = new WebDriverWait(noticeDriver,59).until(
                    ExpectedConditions.presenceOfElementLocated(By.className("gritter-title")));
            Portal.setRegistrationNotice(registrationElement.getText());
//            Board.POST_PROCESSES.add(()-> RunningCourseActivity.noticeLabel.setText(getRegistrationNotice()));
            return true;
        } catch (Exception e) {
            if (userRequested) {
                App.reportConnectionLost();
            }
            return false;
        }
    }

    /**
     * Returns the (current) admission notice.
     * It is more appropriate to show the user this with its
     * reading date as it was set with it.
     * @see #setAdmissionNotice(String)
     */
    public static String getAdmissionNotice(){
        return admissionNotice;
    }

    /**
     * Sets the admission notice to the given admissionNotice;
     * and its reading date will be set to this point in time.
     */
    public static void setAdmissionNotice(String admissionNotice){
        Portal.admissionNotice = admissionNotice;
        lastAdmissionNoticeUpdate = new Date();
    }

    /**
     * Returns the last time the admissionNotice was updated;
     * or "Never" if it has never been.
     * This is specified by the standard formatter.
     * @see MDate
     */
    public static String getLastAdmissionNoticeUpdate(){
        return lastAdmissionNoticeUpdate == null ? "Never" : MDate.format(lastAdmissionNoticeUpdate);
    }

    /**
     * Returns the (current) registration notice.
     * It is more appropriate to show the user this with its
     * reading date as it was set with it.
     * @see #setRegistrationNotice(String)
     */
    public static String getRegistrationNotice(){
        return registrationNotice;
    }

    /**
     * Sets the registration notice to the given registrationNotice;
     * and its reading date will be set to this point in time.
     * This has a runtime component modification consequence?
     * @see RunningCourseActivity#effectNoticeUpdate()
     */
    public static void setRegistrationNotice(String registrationNotice){
        Portal.registrationNotice = registrationNotice;
        lastRegistrationNoticeUpdate = new Date();
//        RunningCourseActivity.effectNoticeUpdate();
    }

    /**
     * Returns the last time the registrationNotice was updated;
     * or "Never" if it has never been.
     * This is specified by the standard formatter.
     * @see MDate
     */
    public static String getLastRegistrationNoticeUpdate(){
        return lastRegistrationNoticeUpdate == null ? "Never" : MDate.format(lastRegistrationNoticeUpdate);
    }

    public static WebElement getTabElement(String elementText, List<WebElement> tabs){
        for (WebElement tab : tabs) {
            if (tab.getText().equalsIgnoreCase(elementText)) {
                return tab;
            }
        }
        return null;
    }

    /**
     * Auto-sync shall mean that courses will be verified as they are locally added to the tables,
     * notices will be renewed in the background, news updates, etc.
     *
     * The default is daily, but there should be options for the user in next compilations.
     */
    public static void  setAutoSync(boolean sync){
        autoSync = sync;
    }

    public static boolean isAutoSynced(){
        return autoSync;
    }

    public static Date getLastLogin() {
        return lastLogin;
    }

    private static void setLastLogin(Date lastLogin) {
        Portal.lastLogin = lastLogin;
    }

    /**
     * This call is intended, purposely, for pre-scrapping actions.
     * These include setting of semesters, levels, and other dynamic details.
     * Make sure the driver is at the {@link #CONTENTS_PAGE} before this call.
     */
    public static void onPortal(FirefoxDriver driver){
        if (driver.getCurrentUrl().equals(CONTENTS_PAGE)) {
            final List<WebElement> iGroup = driver.findElementsByClassName("info-group");
            Student.setLevel(iGroup.get(2).getText().split("\n")[1]);
            Student.setStatus(iGroup.get(3).getText().split("\n")[1]);

            final String[] findingSemester = iGroup.get(6).getText().split("\n")[0].split(" ");
            final String ongoingSemester = findingSemester[0]+" "+findingSemester[1]+" "+findingSemester[2];
            Student.setSemester(ongoingSemester);
        }
        setLastLogin(new Date());
    }

    /**
     * Returns true if the Portal is currently requesting course evaluations.
     * This check is important prior to scrapping, because scrapping of most
     * data are not possible if an evaluation is needed.
     */
    public static boolean isEvaluationNeeded(FirefoxDriver driver){
        try {
            driver.findElementByCssSelector("div.gritter-item-wrapper:nth-child(4)");
            return true;
        } catch (Exception e){
            return false;
        }
    }

    /**
     * Reports that the Portal is requesting course evaluations.
     * This report is as a consequence of the
     * This report is made on the given parent component [or Board.getRoot() if null].
     */
    public static void reportEvaluationNeeded(Component parent){
        final Component actualParent = parent == null ? Board.getRoot() : parent;
        App.reportWarning(actualParent, "Course Evaluation",
                "Your portal is currently requesting \"Course Evaluations\".\n" +
                        "Please visit your portal to perform the evaluation first.");
    }

    /**
     * Makes the evaluation report on the Dashboard's instance.
     * @see #reportEvaluationNeeded(Component)
     */
    public static void reportEvaluationNeeded(){
        reportEvaluationNeeded(null);
    }

    public static void serialize(){
        final String data = Globals.joinLines(registrationNotice,
                MDate.format(lastRegistrationNoticeUpdate),
                admissionNotice,
                MDate.format(lastAdmissionNoticeUpdate),
                autoSync,
                MDate.format(lastLogin));
        Serializer.toDisk(data, Serializer.inPath("portal.ser"));
    }

    public static void deSerialize(){
        final Object obj = Serializer.fromDisk(Serializer.inPath("portal.ser"));
        if (obj == null) {
            App.silenceException("Failed to read Portal Data.");
        } else {
            final String[] data = Globals.splitLines((String) obj);
            registrationNotice = data[0];
            lastRegistrationNoticeUpdate = MDate.parse(data[1]);
            admissionNotice = data[2];
            lastAdmissionNoticeUpdate = MDate.parse(data[3]);
            autoSync = Boolean.parseBoolean(data[4]);
            lastLogin = MDate.parse(data[5]);
        }
    }

}
