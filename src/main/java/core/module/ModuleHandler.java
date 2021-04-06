package core.module;

import core.Board;
import core.Portal;
import core.driver.MDriver;
import core.serial.Serializer;
import core.user.Student;
import core.utils.App;
import core.utils.Globals;
import core.utils.Internet;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import proto.*;
import utg.Dashboard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles all module related activities.
 * Even the {@link Memory} has its root from here.
 * @see #modulesMonitor
 * Todo: keep track of all venues, rooms, lecturer-names the user gives and recommend them on appropriate dialogs.
 */
public class ModuleHandler {
    private static String semesterName;
    private static FirefoxDriver modulesDriver;
    /**
     * This list has complete dominance over all the add, remove, and edit events
     * of modules from wherever, withing Dashboard.
     * All the models delegate to it. They only add or delete or substitute after it does.
     * All of the tables, models, the transcript counts on this.
     */
    private static ArrayList<Course> modulesMonitor;
    private ModuleYear yearOne;
    private ModuleYear yearTwo;
    private ModuleYear yearThree;
    private ModuleYear yearFour;
    public static final ArrayList<KTableModel> ALL_MODELS = new ArrayList<>();
    public static final ArrayList<Course> STARTUP_COURSES = new ArrayList<>();
    private static final String[] COLUMNS = new String[] { "CODE", "NAME", "LECTURER", "GRADE" };
    public static final String EDIT = "Edit";
    public static final String CONFIRM = "Verify";
    public static final String DETAILS = "Details";
    public static final String DELETE = "Remove";
    public static final String ADD = "Add";


    public ModuleHandler() {
        yearOne = new ModuleYear(Student.firstAcademicYear());
        yearTwo = new ModuleYear(Student.secondAcademicYear());
        yearThree = new ModuleYear(Student.thirdAcademicYear());
        yearFour = new ModuleYear(Student.fourthAcademicYear());

        modulesMonitor = new ArrayList<Course>() {
            @Override
            public boolean add(Course course) {
                if (course.isMisc()) {
                    MiscModule.add(course);
                } else if (course.isSummerSemester()) {
                    SummerModule.add(course);
                } else if (course.getYear().equals(Student.firstAcademicYear())) {
                    yearOne.add(course);
                } else if (course.getYear().equals(Student.secondAcademicYear())) {
                    yearTwo.add(course);
                } else if (course.getYear().equals(Student.thirdAcademicYear())) {
                    yearThree.add(course);
                } else if (course.getYear().equals(Student.fourthAcademicYear())) {
                    yearFour.add(course);
                }
                Memory.add(course);
                return super.add(course);
            }

            @Override
            public boolean remove(Object o) {
                final Course course = (Course) o;
                if (!App.showYesNoCancelDialog("Remove", "Are you sure you did not do \""+course.getName()+"\",\n" +
                        "and that you wish to remove it from your collection?")) {
                    return false;
                }

                if (course.isVerified()) {
                    final int vInt = App.verifyUser("Enter your Mat. Number to proceed with this changes:");
                    if (vInt == App.VERIFICATION_FALSE) {
                        App.reportMatError();
                        return false;
                    } else if (vInt != App.VERIFICATION_TRUE) {
                        return false;
                    }
                }

                if (course.isMisc()) {
                    MiscModule.remove(course);
                } else if (course.isSummerSemester()) {
                    SummerModule.remove(course);
                } else if (course.isFirstYear()) {
                    yearOne.remove(course);
                } else if (course.isSecondYear()) {
                    yearTwo.remove(course);
                } else if (course.isThirdYear()) {
                    yearThree.remove(course);
                } else if (course.isFourthYear()) {
                    yearFour.remove(course);
                }
                Memory.remove(course);
                return super.remove(course);
            }
        };

        uploadModules();
    }

    public Component yearOnePresent(){
        return yearOne.getPresent();
    }

    public Component yearTwoPresent(){
        return yearTwo.getPresent();
    }

    public Component yearThreePresent(){
        return yearThree.getPresent();
    }

    public Component yearFourPresent(){
        return yearFour.getPresent();
    }

    private static void uploadModules() {
        if (Dashboard.isFirst()) {
            for (Course c : STARTUP_COURSES) {
                modulesMonitor.add(c);
            }
        } else {
            deserializeData();
        }
    }

    /**
     * Checks if the given code exists in the entire monitor.
     * The edition dialogs should not use this, since they present an exception
     * by skipping the focused row of the respective table- model.
     */
    public static boolean exists(String code) {
        for (Course course : modulesMonitor) {
            if (course.getCode().equalsIgnoreCase(code)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the given code exists in any other model except the given.
     */
    public static boolean existsExcept(KTableModel targetModel, String code){
        for (KTableModel model : ALL_MODELS) {
            if (model != targetModel && model.getRowOf(code) >= 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * This call is complete.
     * It makes sure the list replaces the old with recent, and inflict
     * the changes on the appropriate table, thereafter.
     * This also caters for the case where the old might not exist for whatever reason,
     * and halt, in that case, the subsequent attempt for visual changes.
     *
     * Do not call set() on the monitor! Call this.
     */
    public static void substitute(Course old, Course recent){
        if (exists(old.getCode())) {
            // typically for editing. or if it's from a sync / verification,
            // the details should be merged prior to this call
            modulesMonitor.set(old.getListIndex(), recent);
        } else {
            //may be an issue, after verification - as the user might have removed it during the process
            modulesMonitor.add(recent);
            return;
        }

        for (KTableModel defaultTableModel : ALL_MODELS) {
            final int targetRow = defaultTableModel.getRowOf(old.getCode());
            if (targetRow >= 0) {
                defaultTableModel.setValueAt(recent.getCode(), targetRow, 0);
                defaultTableModel.setValueAt(recent.getName(), targetRow, 1);
                defaultTableModel.setValueAt(recent.getLecturer(), targetRow, 2);
                defaultTableModel.setValueAt(recent.getGrade(), targetRow, 3);
                if (defaultTableModel == SummerModule.summerModel || defaultTableModel == MiscModule.miscModel) {
                    defaultTableModel.setValueAt(recent.getYear(), targetRow, 4);
                }
                break;
            }
        }

        Memory.replace(old, recent);
    }

    public static void reportCodeDuplication(String dCode){
        App.reportError("Duplicate Error",
                "Sorry, there's already a course with the code '"+dCode.toUpperCase()+"' in the list.");
    }

    /**
     * Returns the first course, in the list, with the given ccode.
     * This method is useful. Especially, since indexing of the monitor and the
     * many models do not coincide, call this - never want to retrieve a course from the list
     * by using get(int) as such an index might be matching a different course
     * according to the monitor's index.
     * This function compares only the code, and it's case-insensitive.
     * Null value shall implies no such course in the entire list.
     */
    public static Course getModuleByCode(String code){
        for (Course course : modulesMonitor) {
            if (course.getCode().equalsIgnoreCase(code)) {
                return course;
            }
        }
        return null;
    }

    private static synchronized void fixModulesDriver(){
        if (modulesDriver == null) {
            modulesDriver = MDriver.forgeNew(true);
        }
    }

    /**
     * Attempts to verify this course using its code only.
     * Call this on a different thread.
     */
    public static void launchVerification(Course target) {
        if (!App.showYesNoCancelDialog("Verify "+target.getCode(),
                "Do you want verify \""+target.getName()+"\".\n" +
                        "Dashboard will find out if it is on your Portal.\n" +
                        "Refer to Dashboard Tips for more info about this action.")) {
            return;
        }
        fixModulesDriver();
        if (modulesDriver == null) {
            App.reportMissingDriver();
            return;
        }

        if (!Internet.isInternetAvailable()) {
            App.reportNoInternet();
            return;
        }

        synchronized (modulesDriver){
            final WebDriverWait loadWaiter = new WebDriverWait(modulesDriver, Portal.MAXIMUM_WAIT_TIME);
            final int loginAttempt = MDriver.attemptLogin(modulesDriver);
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

            try {
                modulesDriver.navigate().to(Portal.CONTENTS_PAGE);
                Portal.onPortal(modulesDriver);
            } catch (Exception e) {
                App.reportConnectionLost();
                return;
            }

            Course foundOne = null;
            final List<WebElement> tabs = loadWaiter.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(".nav-tabs > li")));
            //Firstly, the code, name, and score at grades tab
//            tabs.get(6).click();
            Portal.getTabElement("Grades", tabs).click();
            final WebElement gradesTable = modulesDriver.findElementsByCssSelector(".table-warning").get(1);
            final WebElement tBody = gradesTable.findElement(By.tagName("tbody"));
            final List<WebElement> rows = tBody.findElements(By.tagName("tr"));
            for(WebElement row : rows){
                final List<WebElement> instantRow = row.findElements(By.tagName("td"));
                if (instantRow.get(0).getText().equalsIgnoreCase(target.getCode())) {
                    foundOne = new Course("","", instantRow.get(0).getText(), instantRow.get(1).getText(),
                            "","","","", Double.parseDouble(instantRow.get(6).getText()),
                            0,"",true);
                    break;
                }
            }

            if (foundOne == null) {
                App.reportWarning("Checkout Unsuccessful",
                        "The process to checkout for "+target.getAbsoluteName()+" was unsuccessful.\n" +
                        "Dashboard could not locate any trace of it on your portal.\n" +
                                "If you've done this course, then contact the lecturer, or the department.");
                return;
            }

            //Secondly, the code, year and semester at transcript tab
//            tabs.get(7).click();
            Portal.getTabElement("Transcript", tabs).click();
            final WebElement transcriptTable = modulesDriver.findElementByCssSelector(".table-bordered");
            final WebElement transBody = transcriptTable.findElement(By.tagName("tbody"));
            final List<WebElement> transRows = transBody.findElements(By.tagName("tr"));
            String vYear = null;
            String vSemester = null;
            for (WebElement transRow : transRows) {
                if (transRow.getText().contains("Semester")) {
                    vYear = transRow.getText().split(" ")[0];
                    vSemester = transRow.getText().split(" ")[1] + " Semester";
                } else {
                    if (transRow.getText().contains(foundOne.getCode())) {
                        foundOne.setYear(vYear);
                        foundOne.setSemester(vSemester);
                        break;
                    }
                }
            }

            //Finally, the lecturer name at registration tab - if there
//            tabs.get(4).click();
            Portal.getTabElement("All Registered Courses", tabs).click();
            final WebElement allCourseTable = modulesDriver.findElementByCssSelector(".table-warning");
            final WebElement tableBody = allCourseTable.findElement(By.tagName("tbody"));
            final List<WebElement> allRows = tableBody.findElements(By.tagName("tr"));

            int i = 0;
            while (i < allRows.size()) {
                final List<WebElement> instantRow = allRows.get(i).findElements(By.tagName("td"));
                if (foundOne.getCode().equals(instantRow.get(0).getText())) {
                    foundOne.setLecturer(instantRow.get(2).getText(), false);
                    break;
                }
                i++;
            }

            final Course existed = getModuleByCode(target.getCode());
            if (existed == null) {//removed?
                modulesMonitor.add(foundOne);
            } else {//merge and replace
                Course.merge(foundOne, existed);
                substitute(existed, foundOne);
            }

            App.reportInfo("Checkout Successful",
                    "The process to checkout for "+target.getAbsoluteName()+" is completed successfully.\n" +
                    "It has been found on your Portal. It would now be included your Analysis and Transcript.");
        }
    }

    /**
     * Called to perform a thorough sync.
     * This action has a lot of consequences!
     * This executes itself on a thread.
     */
    public static void launchThoroughSync(boolean userRequested, KButton triggerButton){
        if (userRequested && !App.showYesNoCancelDialog("Synchronize Modules",
                "Do you want to synchronize your courses with the Portal?\n" +
                        "Dashboard will perform a complete 're-indexing' of your courses.\n" +
                        "Refer to the Tips for more info about this action.")) {
            return;
        }

        new Thread(()-> {
            triggerButton.setEnabled(false);

            fixModulesDriver();
            if (modulesDriver == null) {
                if (userRequested) {
                    App.reportMissingDriver();
                    triggerButton.setEnabled(true);
                }
                return;
            }

            if (!Internet.isInternetAvailable()) {
                if (userRequested) {
                    App.reportNoInternet();
                    triggerButton.setEnabled(true);
                }
                return;
            }

            synchronized (modulesDriver){
                final WebDriverWait loadWaiter = new WebDriverWait(modulesDriver, 30);
                final int loginAttempt = MDriver.attemptLogin(modulesDriver);
                if (loginAttempt == MDriver.ATTEMPT_SUCCEEDED) {
                    if (Portal.isEvaluationNeeded(modulesDriver)) {
                        if (userRequested) {
                            Portal.reportEvaluationNeeded();
                            triggerButton.setEnabled(true);
                        }
                        return;
                    }
                } else if (loginAttempt == MDriver.ATTEMPT_FAILED) {
                    if (userRequested) {
                        App.reportLoginAttemptFailed();
                        triggerButton.setEnabled(true);
                    }
                    return;
                } else if (loginAttempt == MDriver.CONNECTION_LOST) {
                    if (userRequested) {
                        App.reportConnectionLost();
                        triggerButton.setEnabled(true);
                    }
                    return;
                }

                final List<WebElement> tabs;
                try {
                    modulesDriver.navigate().to(Portal.CONTENTS_PAGE);
                    Portal.onPortal(modulesDriver);
                    tabs = loadWaiter.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(".nav-tabs > li")));
                } catch (Exception e) {
                    if (userRequested) {
                        App.reportConnectionLost();
                        triggerButton.setEnabled(true);
                    }
                    return;
                }

//                Firstly, code, name, year, semester, and credit hour at transcript tab
//                Addition to startupCourses is only here; all the following loops only updates the details.
//                this eradicates the possibility of adding running courses at tab-4
                final ArrayList<Course> foundCourses = new ArrayList<>();
                tabs.get(7).click();
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
                final WebElement surrounds = modulesDriver.findElementsByCssSelector(".pull-right").get(3);
                final String CGPA = surrounds.findElements(By.tagName("th")).get(1).getText();
                Student.setCGPA(Double.parseDouble(CGPA));

                //Secondly, add scores at grades tab
                tabs.get(6).click();
                final WebElement gradesTable = modulesDriver.findElementsByCssSelector(".table-warning").get(1);
                final WebElement tBody = gradesTable.findElement(By.tagName("tbody"));
                final List<WebElement> rows = tBody.findElements(By.tagName("tr"));
                for(WebElement t : rows){
                    final List<WebElement> data = t.findElements(By.tagName("td"));
                    for (Course c : foundCourses) {
                        if (c.getCode().equals(data.get(0).getText())) {
                            c.setScore(Double.parseDouble(data.get(6).getText()));
                        }
                    }
                }

                //Finally, available lecturer names at all-registered tab
                tabs.get(4).click();
                final WebElement allRegisteredTable = modulesDriver.findElementByCssSelector(".table-warning");
                final WebElement tableBody = allRegisteredTable.findElement(By.tagName("tbody"));
                final List<WebElement> allRows = tableBody.findElements(By.tagName("tr"));
                int l = 0;
                while (l < allRows.size()) {
                    final List<WebElement> instantRow = allRows.get(l).findElements(By.tagName("td"));
                    for (Course c : foundCourses) {
                        if (c.getCode().equals(instantRow.get(0).getText())) {
                            c.setLecturer(instantRow.get(2).getText(), false);
                        }
                    }
                    l++;
                }

                for (Course found : foundCourses) {
                    final Course existed = getModuleByCode(found.getCode());
                    if (existed == null) {//does not exist?
                        modulesMonitor.add(found);
                    } else {//merge and replace
                        Course.merge(found, existed);
                        substitute(existed, found);
                    }
                }

                final int foundCount = foundCourses.size();
                final int semesterCount = semCaptions.size();
                App.reportInfo("Sync Successful", "Synchronization of the modules completed successfully:\n" +
                        String.format("%s were found in %s.", Globals.checkPlurality(foundCount, "courses"),
                                Globals.checkPlurality(semesterCount, "semesters.")));
                triggerButton.setEnabled(true);
            }
        }).start();
    }

    /**
     * This method should:
     * 1. Affect only the obligatory program courses
     * 2. Spare courses which were requirement-set by the user
     * except for major-obligatory.
     *
     * The algorithm is in two-phase form:
     * the first stage involves revoking all previous requirements
     * of the code followed by resetting, if necessary, new requirements as per the code.
     */
    public static void effectMajorCodeChanges(String from, String to) {
        new Thread(()-> {
            revokeMajors(from);
            if (Globals.hasText(to)) {
                resetMajors(to);
            }//else, a mere reset is intended
        }).start();
    }

    /**
     * Called to relief all major-courses from the given from.
     */
    private static void revokeMajors(String from) {
        for (Course course : modulesMonitor) {
            try {
                final String courseCode = course.getCode().substring(0, 3);
                if (courseCode.equalsIgnoreCase(from) && course.isMajorObligatory()) {
                    course.setRequirement(Course.NONE);
                }
            } catch (StringIndexOutOfBoundsException ignored) {
            }
        }
    }

    private static void resetMajors(String to) {
        for (Course course : modulesMonitor) {
            try {
                final String courseCode = course.getCode().substring(0, 3);
                if (courseCode.equalsIgnoreCase(to) && course.isUnclassified()) {
                    course.setRequirement(Course.MAJOR_OBLIGATORY);
                }
            } catch (StringIndexOutOfBoundsException ignored) {
            }
        }
    }

    public static void effectMinorCodeChanges(String from, String to) {
        new Thread(() -> {
            revokeMinors(from);
            if (Globals.hasText(to)) {
                resetMinors(to);
            }
        }).start();
    }

    private static void revokeMinors(String from){
        for (Course course : modulesMonitor) {
            try {
                final String courseCode = course.getCode().substring(0, 3);
                if (courseCode.equalsIgnoreCase(from) && course.isMinorObligatory()) {
                    course.setRequirement(Course.NONE);
                }
            } catch (StringIndexOutOfBoundsException ignored) {
            }
        }
    }

    private static void resetMinors(String to){
        for (Course course : modulesMonitor) {
            try {
                final String courseCode = course.getCode().substring(0, 3);
                if (courseCode.equalsIgnoreCase(to) && course.isUnclassified()) {
                    course.setRequirement(Course.MINOR_OBLIGATORY);
                }
            } catch (StringIndexOutOfBoundsException ignored) {
            }
        }
    }

    public static void reportScoreInvalid(String score, Container root){
        App.reportError(root,"Invalid Score",
                "\""+score+"\" is not a valid score. Please enter a correct value.");
    }

    public static void reportScoreOutOfRange(Container root){
        App.reportError(root,"Invalid Score", "Score cannot be less than 0 or more than 100.");
    }

    public static ArrayList<Course> getMonitor(){
        return modulesMonitor;
    }

    /**
     * Deals with pretty much, everything of the an academic-year,
     * and presents the entire frame work on a panel.
     */
    public static class ModuleYear {
        private String yearName;
        private KTable table1, table2;
        private KTable focusTable;
        private KTableModel model1, model2;
        private KTableModel focusModel;
        private KMenuItem detailsItem, editItem, removeItem, confirmItem, newItem;
        private JPopupMenu popupMenu;

        public ModuleYear(String yearName) {
            this.yearName = yearName;
            setupTable1();
            setupTable2();
            ALL_MODELS.add(model1);
            ALL_MODELS.add(model2);

            detailsItem = new KMenuItem(DETAILS);
            detailsItem.addActionListener(e-> {
                final String code = String.valueOf(focusModel.getValueAt(focusTable.getSelectedRow(), 0));
                Course.exhibit(getModuleByCode(code));
            });

            editItem = new KMenuItem(EDIT);
            editItem.addActionListener(e-> {
                final String code = String.valueOf(focusModel.getValueAt(focusTable.getSelectedRow(), 0));
                final Course course = getModuleByCode(code);
                if (course != null) {
                    new ModuleEditor(course, focusModel).setVisible(true);
                }
            });

            removeItem = new KMenuItem(DELETE);
            removeItem.addActionListener(e-> {
                final String code = String.valueOf(focusModel.getValueAt(focusTable.getSelectedRow(), 0));
                final Course course = getModuleByCode(code);
                if (course != null) {
                    modulesMonitor.remove(course);
                }
            });

            confirmItem = new KMenuItem(CONFIRM);
            confirmItem.addActionListener(e-> {
                final String code = String.valueOf(focusModel.getValueAt(focusTable.getSelectedRow(), 0));
                final Course course = getModuleByCode(code);
                if (course != null) {
                    new Thread(()-> launchVerification(course)).start();
                }
            });

            newItem = new KMenuItem(ADD);
            newItem.addActionListener(e-> {
                final ModuleAdder adder = new ModuleAdder(yearName, semesterName);
                SwingUtilities.invokeLater(()-> adder.setVisible(true));
            });

            popupMenu = new JPopupMenu();
            popupMenu.add(confirmItem);
            popupMenu.add(editItem);
            popupMenu.add(detailsItem);
            popupMenu.add(removeItem);
            popupMenu.add(newItem);
        }

        private void setupTable1() {
            model1 = new KTableModel();
            model1.setColumnIdentifiers(COLUMNS);

            table1 = getSemesterTable(model1);
            table1.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if (e.isPopupTrigger()) {
                        semesterName = Student.FIRST_SEMESTER;
                        focusTable = table1;
                        focusModel = model1;
                        detailsItem.setEnabled(true);
                        editItem.setEnabled(true);
                        removeItem.setEnabled(true);
                        confirmItem.setEnabled(true);
                        newItem.setEnabled(table1.getRowCount() < 6);
                        table1.getSelectionModel().setSelectionInterval(0, table1.rowAtPoint(e.getPoint()));
                        SwingUtilities.invokeLater(()-> popupMenu.show(table1, e.getX(), e.getY()));
                    }
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    mousePressed(e);
                }
            });
        }

        private void setupTable2() {
            model2 = new KTableModel();
            model2.setColumnIdentifiers(COLUMNS);

            table2 = getSemesterTable(model2);
            table2.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if (e.isPopupTrigger()) {
                        semesterName = Student.SECOND_SEMESTER;
                        focusTable = table2;
                        focusModel = model2;
                        detailsItem.setEnabled(true);
                        editItem.setEnabled(true);
                        removeItem.setEnabled(true);
                        confirmItem.setEnabled(true);
                        newItem.setEnabled(table2.getRowCount() < 6);
                        table2.getSelectionModel().setSelectionInterval(0, table2.rowAtPoint(e.getPoint()));
                        SwingUtilities.invokeLater(()-> popupMenu.show(table2, e.getX(), e.getY()));
                    }
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    mousePressed(e);
                }
            });
        }

        /**
         * Adds the given course to this academic year.
         * The given course must not be a miscellaneous course,
         * because it will be added to the model of either the first
         * semester of the second depending on its semester.
         * @see #remove(Course)
         */
        private void add(Course course){
            final String semester = course.getSemester();
            if (semester.equals(Student.FIRST_SEMESTER)) {
                model1.addRow(new String[] {course.getCode(), course.getName(), course.getLecturer(), course.getGrade()});
            } else if (semester.equals(Student.SECOND_SEMESTER)) {
                model2.addRow(new String[]{course.getCode(), course.getName(), course.getLecturer(), course.getGrade()});
            } else {
                App.silenceException(String.format("Error: %s [%s] cannot be added to the academic year %s.",
                        course.getName(), course.getAbsoluteSemesterName(), yearName));
            }
        }

        /**
         * Removes the given course from this academic year.
         * This course should be pre-existing in one of the models
         * of this year, otherwise nothing is done.
         */
        private void remove(Course course){
            if (course.getSemester().equals(Student.FIRST_SEMESTER)) {
                final int i = model1.getRowOf(course.getCode());
                if (i >= 0) {
                    model1.removeRow(i);
                }
            } else if (course.getSemester().equals(Student.SECOND_SEMESTER)) {
                final int i = model2.getRowOf(course.getCode());
                if (i >= 0) {
                    model2.removeRow(i);
                }
            }
        }

        private KTable getSemesterTable(KTableModel model){
            final KTable table = new KTable(model);
            table.setFont(KFontFactory.createPlainFont(15));
            table.setRowHeight(30);
            table.setHeaderHeight(30);
            table.centerAlignAllColumns();
            table.getTableHeader().setFont(KFontFactory.createBoldFont(16));
            table.getColumnModel().getColumn(0).setPreferredWidth(70);
            table.getColumnModel().getColumn(1).setPreferredWidth(280);
            table.getColumnModel().getColumn(2).setPreferredWidth(250);
            table.getColumnModel().getColumn(3).setPreferredWidth(50);
            table.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() >= 2) {
                        final int selectedRow = table.getSelectedRow();
                        if (selectedRow >= 0) {
                            final String code = String.valueOf(table.getValueAt(selectedRow, 0));
                            Course.exhibit(getModuleByCode(code));
                            e.consume();
                        }
                    }
                }
            });
            return table;
        }

        /**
         * The entire present of this academic year on a Panel.
         */
        private KPanel getPresent() {
            final KScrollPane scrollPane1 = table1.sizeMatchingScrollPane();
            scrollPane1.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if (e.isPopupTrigger()) {
                        semesterName = Student.FIRST_SEMESTER;
                        focusModel = model1;
                        focusTable = table1;
                        focusTable.getSelectionModel().clearSelection();
                        detailsItem.setEnabled(false);
                        editItem.setEnabled(false);
                        removeItem.setEnabled(false);
                        confirmItem.setEnabled(false);
                        newItem.setEnabled(table1.getRowCount() < 6);
                        SwingUtilities.invokeLater(()-> popupMenu.show(scrollPane1, e.getX(), e.getY()));
                    }
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    mousePressed(e);
                }
            });

            final KScrollPane scrollPane2 = table2.sizeMatchingScrollPane();
            scrollPane2.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if (e.isPopupTrigger()) {
                        semesterName = Student.SECOND_SEMESTER;
                        focusModel = model2;
                        focusTable = table2;
                        focusTable.getSelectionModel().clearSelection();
                        detailsItem.setEnabled(false);
                        editItem.setEnabled(false);
                        removeItem.setEnabled(false);
                        confirmItem.setEnabled(false);
                        newItem.setEnabled(table2.getRowCount() < 6);
                        SwingUtilities.invokeLater(()-> popupMenu.show(scrollPane2, e.getX(), e.getY()));
                    }
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    mousePressed(e);
                }
            });

            final KPanel present = new KPanel();
            present.setLayout(new BoxLayout(present, BoxLayout.Y_AXIS));
            present.addAll(new KPanel(semesterHead(Student.FIRST_SEMESTER)), scrollPane1, Box.createVerticalStrut(15),
                    new KPanel(semesterHead(Student.SECOND_SEMESTER)), scrollPane2, Box.createVerticalStrut(15));
            return present;
        }

        private KLabel semesterHead(String semester){
            return new KLabel(yearName+" "+semester, KFontFactory.createPlainFont(17), Color.BLUE);
        }
    }


    /**
     * Provides the dialog for locally addition of courses.
     */
    public static class ModuleAdder extends KDialog {
        KTextField yearField, semesterField, codeField, nameField, lecturerField, venueField, scoreField;
        JComboBox<String> dayBox, timeBox, requirementBox, creditBox;
        KPanel yearPanel, semesterPanel;
        String yearName, semesterName;
        KButton actionButton;

        /**
         * Constructs a course addition dialog.
         * The yearName and semesterName are provided-set,
         * fields of them are never editable.
         * Todo: there should be distinction between the venue and the room.
         * Todo: 'Checkout Now' option to be added.
         */
        public ModuleAdder(String yearName, String semesterName){
            super("New Course");
            setResizable(true);
            setModalityType(KDialog.DEFAULT_MODALITY_TYPE);
            this.yearName = yearName;
            this.semesterName = semesterName;

            final Font hintFont = KFontFactory.createBoldFont(16);

            if (this instanceof MiscModule.MiscModuleAdder) {
                yearField = KTextField.rangeControlField(9);
            } else {
                yearField = new KTextField();
            }
            yearField.setPreferredSize(new Dimension(125, 30));
            yearField.setText(yearName);
            yearField.setEditable(yearName == null);
            yearPanel = new KPanel(new BorderLayout());
            yearPanel.add(new KPanel(new KLabel("Year:", hintFont)),  BorderLayout.WEST);
            yearPanel.add(new KPanel(yearField),BorderLayout.CENTER);

            semesterField = new KTextField(new Dimension(200,30));
            semesterField.setText(semesterName);
            semesterField.setEditable(semesterName == null);
            semesterPanel = new KPanel(new BorderLayout());
            semesterPanel.add(new KPanel(new KLabel("Semester:", hintFont)), BorderLayout.WEST);
            semesterPanel.add(new KPanel(semesterField), BorderLayout.CENTER);

            codeField = KTextField.rangeControlField(10);
            codeField.setPreferredSize(new Dimension(125,30));
            final KPanel codePanel = new KPanel(new BorderLayout());
            codePanel.add(new KPanel(new KLabel("Code:", hintFont)), BorderLayout.WEST);
            codePanel.add(new KPanel(codeField), BorderLayout.CENTER);

            nameField = new KTextField(new Dimension(300,30));
            final KPanel namePanel = new KPanel(new BorderLayout());
            namePanel.add(new KPanel(new KLabel("Name:", hintFont)), BorderLayout.WEST);
            namePanel.add(new KPanel(nameField), BorderLayout.CENTER);

            lecturerField = new KTextField(new Dimension(300,30));
            final KPanel lecturerPanel = new KPanel(new BorderLayout());
            lecturerPanel.add(new KPanel(new KLabel("Lecturer:", hintFont)), BorderLayout.WEST);
            lecturerPanel.add(new KPanel(lecturerField), BorderLayout.CENTER);

            dayBox = new JComboBox<>(Course.getWeekDays());
            dayBox.setFont(KFontFactory.createPlainFont(15));
            timeBox = new JComboBox<>(Course.getCoursePeriods());
            timeBox.setFont(KFontFactory.createPlainFont(15));
            final KPanel schedulePanel = new KPanel(new FlowLayout(FlowLayout.CENTER));//this a litte sort of an exception
            schedulePanel.addAll(new KLabel("Day:", hintFont), dayBox);
            schedulePanel.addAll(Box.createRigidArea(new Dimension(25, 30)),
                    new KLabel("Time:", hintFont), timeBox);

            venueField = new KTextField(new Dimension(300,30));
            final KPanel venuePanel = new KPanel(new BorderLayout());
            venuePanel.add(new KPanel(new KLabel("Venue:", hintFont)), BorderLayout.WEST);
            venuePanel.add(new KPanel(venueField), BorderLayout.CENTER);

            requirementBox = new JComboBox<>(Course.getRequirements());
            requirementBox.setFont(KFontFactory.createPlainFont(15));
            requirementBox.setSelectedItem(Course.NONE);
            final KPanel requirementPanel = new KPanel(new BorderLayout());
            requirementPanel.add(new KPanel(new KLabel("Requirement:", hintFont)), BorderLayout.WEST);
            requirementPanel.add(new KPanel(requirementBox), BorderLayout.CENTER);

            creditBox = new JComboBox<>(Course.creditHours());
            creditBox.setFont(KFontFactory.createPlainFont(15));
            final KPanel creditPanel = new KPanel(new BorderLayout());
            creditPanel.add(new KPanel(new KLabel("Credit Hours:", hintFont)), BorderLayout.WEST);
            creditPanel.add(new KPanel(creditBox), BorderLayout.CENTER);

            scoreField = KTextField.rangeControlField(7);
            scoreField.setPreferredSize(new Dimension(125,30));
            final KPanel scorePanel = new KPanel(new BorderLayout());
            scorePanel.add(new KPanel(new KLabel("Score:", hintFont)), BorderLayout.WEST);
            scorePanel.add(new KPanel(scoreField), BorderLayout.CENTER);

            final KButton cancelButton = new KButton("Cancel");
            cancelButton.addActionListener(e-> dispose());

            actionButton = new KButton("Add");
            actionButton.addActionListener(additionListener());
            final KPanel buttonsPanel = new KPanel(new FlowLayout(FlowLayout.RIGHT));
            buttonsPanel.addAll(cancelButton, actionButton);

            getRootPane().setDefaultButton(actionButton);
            final KPanel contentPanel = new KPanel();
            contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
            contentPanel.addAll(Box.createVerticalStrut(10), yearPanel, semesterPanel, codePanel, namePanel,
                    lecturerPanel, schedulePanel, venuePanel, requirementPanel, creditPanel, scorePanel,
                    Box.createVerticalStrut(30), buttonsPanel);
            setContentPane(contentPanel);
            pack();
            setMinimumSize(getPreferredSize());
            setLocationRelativeTo(Board.getRoot());
        }

        private ActionListener additionListener(){
            return e-> {
                if (codeField.isBlank()) {
                    App.reportError(getRootPane(),"No Code", "Please enter the code of the course.");
                    codeField.requestFocusInWindow();
                } else if (nameField.isBlank()) {
                    App.reportError(getRootPane(),"No Name","Please enter the name of the course.");
                    nameField.requestFocusInWindow();
                } else if (scoreField.isBlank()) {
                    App.reportError(getRootPane(),"No Score","Please enter the score you get from the course.");
                    scoreField.requestFocusInWindow();
                } else {
                    double score;
                    try {
                        score = Double.parseDouble(scoreField.getText());
                    } catch (NumberFormatException formatError){
                        reportScoreInvalid(scoreField.getText(), getRootPane());
                        scoreField.requestFocusInWindow();
                        return;
                    }
                    if (score < 0 || score > 100) {
                        reportScoreOutOfRange(getRootPane());
                        scoreField.requestFocusInWindow();
                        return;
                    }

                    if (exists(codeField.getText())) {
                        reportCodeDuplication(codeField.getText());
                        codeField.requestFocusInWindow();
                        return;
                    }

                    final Course incomingCourse = new Course(yearName, semesterName, codeField.getText().toUpperCase(),
                            nameField.getText(), lecturerField.getText(), venueField.getText(),
                            String.valueOf(dayBox.getSelectedItem()),
                            String.valueOf(timeBox.getSelectedItem()), score,
                            Integer.parseInt(String.valueOf(creditBox.getSelectedItem())),
                            String.valueOf(requirementBox.getSelectedItem()), false);
                    modulesMonitor.add(incomingCourse);
                    dispose();
                }
            };
        }

    }


    /**
     * Extends the Adding-dialog to make it an editing-one.
     */
    private static class ModuleEditor extends ModuleAdder {
        private KTableModel onModel;
        private Course target;

        /**
         * Constructs a course edition dialogue.
         * Extending the adder-dialogue, this alters the components as appropriate;
         * it may add new ones, or remove inherited ones; set some editable or not.
         * @param course The course on which edition is to be.
         * @param onModel The model to perform the removal.
         * @see ModuleAdder
         */
        private ModuleEditor(Course course, KTableModel onModel) {
            super(course.getYear(), course.getSemester());
            setTitle(course.getName());
            this.target = course;
            this.onModel = onModel;

            codeField.setText(course.getCode());
            nameField.setText(course.getName());
            lecturerField.setText(course.getLecturer());
            lecturerField.setEditable(course.isLecturerNameEditable());
            dayBox.setSelectedItem(course.getDay());
            timeBox.setSelectedItem(course.getTime());
            venueField.setText(course.getVenue());
            requirementBox.setSelectedItem(course.getRequirement());
            creditBox.setSelectedItem(String.valueOf(course.getCreditHours()));
            scoreField.setText(String.valueOf(course.getScore()));

            if (course.isVerified()) {
                codeField.setEditable(false);
                nameField.setEditable(false);
                scoreField.setEditable(false);
            }

            actionButton.removeActionListener(actionButton.getActionListeners()[0]);
            actionButton.addActionListener(editionListener());
            actionButton.setText("Done");
        }

        private ActionListener editionListener(){
            return e-> {
                if (codeField.isBlank()) {
                    App.reportError(getRootPane(),"No Code", "Please enter the code of the course.");
                    codeField.requestFocusInWindow();
                } else if (nameField.isBlank()) {
                    App.reportError(getRootPane(),"No Name","Please enter the name of the course.");
                    nameField.requestFocusInWindow();
                } else if (scoreField.isBlank()) {
                    App.reportError(getRootPane(),"No Score","Please enter the score you get from the course.");
                    scoreField.requestFocusInWindow();
                } else {
                    double score;
                    try {
                        score = Double.parseDouble(scoreField.getText());
                    } catch (NumberFormatException formatError){
                        reportScoreInvalid(scoreField.getText(), getRootPane());
                        scoreField.requestFocusInWindow();
                        return;
                    }
                    if (score < 0 || score > 100) {
                        reportScoreOutOfRange(getRootPane());
                        scoreField.requestFocusInWindow();
                        return;
                    }

                    //check for exclusive existence in this table first
                    for (int row = 0; row < onModel.getRowCount(); row++) {
                        if (row == onModel.getSelectedRow()) {
                            continue;
                        }
                        final String tempCode = String.valueOf(onModel.getValueAt(row, 0));
                        if (tempCode.equalsIgnoreCase(codeField.getText())) {
                            reportCodeDuplication(codeField.getText());
                            codeField.requestFocusInWindow();
                            return;
                        }
                    }
                    //check for general existence in other tables
                    if (existsExcept(onModel, codeField.getText())) {
                        reportCodeDuplication(codeField.getText());
                        codeField.requestFocusInWindow();
                        return;
                    }

                    final Course course = new Course(yearField.getText(), semesterField.getText(),
                            codeField.getText().toUpperCase(), nameField.getText(), lecturerField.getText(),
                            venueField.getText(), String.valueOf(dayBox.getSelectedItem()),
                            String.valueOf(timeBox.getSelectedItem()), score,
                            Integer.parseInt(String.valueOf(creditBox.getSelectedItem())),
                            String.valueOf(requirementBox.getSelectedItem()), target.isVerified());
                    substitute(target, course);
                    dispose();
                }
            };
        }

    }


    public static void serialize(){
        final String[] data = new String[modulesMonitor.size()];
        for (int i = 0; i < data.length; i++) {
            data[i] = modulesMonitor.get(i).exportContent();
        }
        Serializer.toDisk(data, Serializer.inPath("modules", "courses.ser"));
    }

    public static void deserializeData() {
        final Object obj = Serializer.fromDisk(Serializer.inPath("modules", "courses.ser"));
        if (obj == null) {
            App.silenceException("Failed to read Modules.");
        } else {
            final String[] data = (String[]) obj;
            for (String entry : data) {
                modulesMonitor.add(Course.create(entry));
            }
        }
    }

}
