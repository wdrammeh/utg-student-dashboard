package core.module;

import core.Activity;
import core.Board;
import core.Portal;
import core.alert.Notification;
import core.driver.MDriver;
import core.serial.Serializer;
import core.user.Student;
import core.utils.App;
import core.utils.Globals;
import core.utils.Internet;
import core.utils.MComponent;
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
import java.util.Objects;

public class SemesterActivity implements Activity {
    private static KTable activeTable;
    private static KTableModel activeModel;
    private static FirefoxDriver activeDriver;
    public static KLabel semesterBigLabel;
    public static KLabel noticeLabel;
    private static KMenuItem matchItem;
    private static KButton optionsButton;
    private static JPopupMenu modulePopupMenu;
    private static KLabel hintLabel;
    public static final ArrayList<RegisteredCourse> STARTUP_REGISTRATIONS = new ArrayList<>();
    private static final String REGISTERED_HINT = "Right-click a row (or a course) on the table for more actions.";
    private static final String UNREGISTERED_HINT = "Courses you register this semester will be shown here.";
    private static final ArrayList<RegisteredCourse> ACTIVE_COURSES = new ArrayList<>() {
        @Override
        public boolean add(RegisteredCourse course) {
            activeModel.addRow(new String[]{course.getCode(), course.getName(), course.getLecturer(),
                    course.getSchedule(), course.getStatus()});
            hintLabel.setText(REGISTERED_HINT);
            return super.add(course);
        }

        @Override
        public boolean remove(Object o) {
            activeModel.removeRow(activeModel.getRowOf(((RegisteredCourse) o).getCode()));
            hintLabel.setText(activeModel.getRowCount() > 0 ? REGISTERED_HINT : UNREGISTERED_HINT);
            return super.remove(o);
        }

        @Override
        public RegisteredCourse set(int index, RegisteredCourse course) {
            final int targetRow = activeModel.getRowOf(course.getCode());
            activeModel.setValueAt(course.getCode(), targetRow, 0);
            activeModel.setValueAt(course.getName(), targetRow, 1);
            activeModel.setValueAt(course.getLecturer(), targetRow, 2);
            activeModel.setValueAt(course.getSchedule(), targetRow, 3);
            activeModel.setValueAt(course.getStatus(), targetRow, 4);
            return super.set(index, course);
        }
    };


    public SemesterActivity() {
        final KPanel runningActivity = new KPanel(new BorderLayout());
        if (Student.isGuest()) {
            runningActivity.add(MComponent.createUnavailableActivity("Semester"), BorderLayout.CENTER);
        } else {
            semesterBigLabel = new KLabel(Student.getSemester(), KFontFactory.BODY_HEAD_FONT);
            semesterBigLabel.setPreferredSize(new Dimension(925, 35));
            semesterBigLabel.setHorizontalAlignment(SwingConstants.CENTER);
            semesterBigLabel.underline(Color.GRAY, true);

            noticeLabel = new KLabel(Portal.getRegistrationNotice(), KFontFactory.createPlainFont(16), Color.RED);
            noticeLabel.setToolTipText("Registration Notice");

            matchItem = new KMenuItem("Match Portal", e-> startMatching(true));

            final KMenuItem updateItem = new KMenuItem("Update Registration Notice",
                    e-> App.reportInfo("Tip",
                            "To update the Registration Notice, go to "+ Globals.reference("Notifications", "Portal Alerts", "Update Alerts") +"."));

            final KMenuItem visitItem = new KMenuItem("Visit Portal");
            visitItem.addActionListener(e-> new Thread(()-> Portal.openPortal(visitItem)).start());

            final KMenuItem addItem = new KMenuItem("Add Running Course", additionAction());

            final JPopupMenu popupMenu = new JPopupMenu();
            popupMenu.add(matchItem);
            popupMenu.add(updateItem);
            popupMenu.add(addItem);
            popupMenu.add(visitItem);

            optionsButton = KButton.createIconifiedButton("options.png",25,25);
            optionsButton.setCursor(MComponent.HAND_CURSOR);
            optionsButton.setToolTipText("More options");
            optionsButton.addActionListener(e-> popupMenu.show(optionsButton,
                    optionsButton.getX() + (int)(.75 * optionsButton.getPreferredSize().width),
                    optionsButton.getY() - optionsButton.getPreferredSize().height/2));

            final KPanel upperPanel = new KPanel(new BorderLayout());
            upperPanel.add(optionsButton, BorderLayout.WEST);
            upperPanel.add(new KPanel(semesterBigLabel), BorderLayout.CENTER);
            upperPanel.add(Box.createRigidArea(new Dimension(975, 10)), BorderLayout.SOUTH);

            runningActivity.add(upperPanel, BorderLayout.NORTH);
            runningActivity.add(semesterContent(), BorderLayout.CENTER);
            runningActivity.add(new KPanel(new FlowLayout(FlowLayout.LEFT), noticeLabel), BorderLayout.SOUTH);
            uploadModules();

            Board.POST_PROCESSES.add(()-> noticeLabel.setText(Portal.getRegistrationNotice()));
        }
        Board.addCard(runningActivity, "This Semester");
    }

    @Override
    public void answerActivity() {
        Board.showCard("This Semester");
    }

    private static void uploadModules(){
        if (Dashboard.isFirst()) {
            for (RegisteredCourse c : STARTUP_REGISTRATIONS) {
                ACTIVE_COURSES.add(c);
            }
        } else {
            deserializeModules();
        }
    }

    private static synchronized void fixRunningDriver(){
        if (activeDriver == null) {
            activeDriver = MDriver.forgeNew(true);
        }
    }

    /**
     * Checks-out this course for the currently running semester using its code.
     * If it's found, it shall be replaced; otherwise, unsuccessful.
     * Todo: if not found, an attempt will be made to register it.
     */
    private static void startCheckout(RegisteredCourse targetCourse) {
        if (targetCourse.getStatus().equals(Course.VERIFYING)) {
            App.silenceInfo(String.format("Already verifying '%s'.", targetCourse.getAbsoluteName()));
            return;
        }

        final String targetCode = targetCourse.getCode();
        final String initialStatus = String.valueOf(activeModel.getValueAt(activeModel.getRowOf(targetCode),
                activeModel.getColumnCount() - 1)); // any of 'confirmed', or 'unknown'
        targetCourse.setStatus(Course.VERIFYING);
        activeModel.setValueAt(Course.VERIFYING, activeModel.getRowOf(targetCode),
                activeModel.getColumnCount() - 1);
        fixRunningDriver();
        if (activeDriver == null) {
            App.reportMissingDriver();
            final int targetRow = activeModel.getRowOf(targetCode);
            if (targetRow >= 0) {
                activeModel.setValueAt(initialStatus, targetRow, activeModel.getColumnCount() - 1);
                targetCourse.setStatus(initialStatus);
            }
            return;
        }
        if (!Internet.isInternetAvailable()) {
            App.reportNoInternet();
            final int targetRow = activeModel.getRowOf(targetCode);
            if (targetRow >= 0) {
                activeModel.setValueAt(initialStatus, targetRow, activeModel.getColumnCount() - 1);
                targetCourse.setStatus(initialStatus);
            }
            return;
        }

        synchronized (activeDriver){
            final WebDriverWait loadWaiter = new WebDriverWait(activeDriver, Portal.MAXIMUM_WAIT_TIME);
            final int loginAttempt = MDriver.attemptLogin(activeDriver);
            if (loginAttempt == MDriver.ATTEMPT_SUCCEEDED) {
                if (Portal.isEvaluationNeeded(activeDriver)) {
                    Portal.reportEvaluationNeeded();
                    final int targetRow = activeModel.getRowOf(targetCode);
                    if (targetRow >= 0) {
                        activeModel.setValueAt(initialStatus, targetRow, activeModel.getColumnCount() - 1);
                        targetCourse.setStatus(initialStatus);
                    }
                    return;
                }
            } else if (loginAttempt == MDriver.CONNECTION_LOST) {
                App.reportConnectionLost();
                final int targetRow = activeModel.getRowOf(targetCode);
                if (targetRow >= 0) {
                    activeModel.setValueAt(initialStatus, targetRow, activeModel.getColumnCount() - 1);
                    targetCourse.setStatus(initialStatus);
                }
                return;
            } else if (loginAttempt == MDriver.ATTEMPT_FAILED) {
                App.reportLoginAttemptFailed();
                final int targetRow = activeModel.getRowOf(targetCode);
                if (targetRow >= 0) {
                    activeModel.setValueAt(initialStatus, targetRow, activeModel.getColumnCount() - 1);
                    targetCourse.setStatus(initialStatus);
                }
                return;
            }

            final List<WebElement> tabs;
            try {
                activeDriver.navigate().to(Portal.CONTENTS_PAGE);
                Portal.onPortal(activeDriver);
                tabs = loadWaiter.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                        By.cssSelector(".nav-tabs > li")));
            } catch (Exception e) {
                App.reportConnectionLost();
                final int targetRow = activeModel.getRowOf(targetCode);
                if (targetRow >= 0) {
                    activeModel.setValueAt(initialStatus, targetRow, activeModel.getColumnCount() - 1);
                    targetCourse.setStatus(initialStatus);
                }
                return;
            }

            final WebElement registeredTab = Portal.getTabElement("All Registered Courses", tabs);
            if (registeredTab == null) {
                App.reportConnectionLost();
                final int targetRow = activeModel.getRowOf(targetCode);
                if (targetRow >= 0) {
                    activeModel.setValueAt(initialStatus, targetRow, activeModel.getColumnCount() - 1);
                    targetCourse.setStatus(initialStatus);
                }
                return;
            } else {
                registeredTab.click();
            }
            final WebElement registrationTable = activeDriver.findElementByCssSelector(".table-warning");
            final WebElement tableBody = registrationTable.findElement(By.tagName("tbody"));
            final List<WebElement> captions = tableBody.findElements(By.cssSelector("b, strong"));
            final boolean registered = captions.get(captions.size() - 1).getText().equals(Student.getSemester());
            if (!registered) {
                App.reportWarning("Checkout Failed",
                        "The attempt to checkout '"+targetCourse.getName()+"' was unsuccessful.\n" +
                        "It seems like you no registration for this semester yet.");
                final int targetRow = activeModel.getRowOf(targetCode);
                if (targetRow >= 0) {
                    activeModel.setValueAt(initialStatus, targetRow, activeModel.getColumnCount() - 1);
                    targetCourse.setStatus(initialStatus);
                }
                return;
            }

            final List<WebElement> allRows = tableBody.findElements(By.tagName("tr"));
//            Let the scrapping begin!
//            'match' refers the row-index of the table's data falling under the required caption
            int match = allRows.size() - 1;
            boolean found = false;
//            iteration works upward until the caption is found
            while (!allRows.get(match).getText().equals(Student.getSemester())) {
                final List<WebElement> instantData = allRows.get(match).findElements(By.tagName("td"));
                if (instantData.get(0).getText().equals(targetCode)) {
                    final RegisteredCourse foundCourse = new RegisteredCourse(instantData.get(0).getText(),
                            instantData.get(1).getText(), instantData.get(2).getText(), instantData.get(3).getText(),
                            instantData.get(4).getText(), targetCourse.getDay(), targetCourse.getTime(), true);
                    final int targetIndex = getIndexOf(targetCourse);
                    if (targetIndex >= 0) { // still present?
                        ACTIVE_COURSES.set(targetIndex, foundCourse);
                    } else { // deleted?
                        ACTIVE_COURSES.add(foundCourse);
                    }
                    App.reportInfo("Checkout Successful",
                            "Registered course '"+targetCourse.getName()+"' checked-out successful.\n" +
                            "It is found on your Portal as a registered course for this semester.");
                    found = true;
                    break;
                }
                match--;
            }

            if (!found) {
                App.reportError("Checkout Unsuccessful",
                        "The attempt to checkout '"+targetCourse.getName()+"' was unsuccessful.\n" +
                                "It seems like you haven't registered that course for this semester.");
                activeModel.setValueAt(initialStatus, activeModel.getRowOf(targetCode),
                        activeModel.getColumnCount() - 1);
                targetCourse.setConfirmed(false);
                targetCourse.setStatus(Globals.UNKNOWN);
            }
        }

    }

    /**
     * Attempts to bring all the registered courses (if there is any) for the current semester.
     * Sync will only be allowed to commence if one is not already on the way.
     * The difference is that: the user does not need to be prompted when they
     * did not request it.
     * This embeds itself in a thread.
     */
    public static void startMatching(boolean userRequested){
        if (!(userRequested || matchItem.isEnabled())) {
            return;
        }

        if (!userRequested || App.showYesNoCancelDialog("Match Table",
                "Do you want to match this table with your Portal?\n" +
                        "Dashboard will contact the Portal and bring all the courses\n" +
                "(if there is any) you have registered this semester.")) {
            new Thread(()-> {
                matchItem.setEnabled(false);
                fixRunningDriver();
                if (activeDriver == null) {
                    if (userRequested) {
                        App.reportMissingDriver();
                    }
                    matchItem.setEnabled(true);
                    return;
                }

                if (!Internet.isInternetAvailable()) {
                    if (userRequested) {
                        App.reportNoInternet();
                    }
                    matchItem.setEnabled(true);
                    return;
                }

                synchronized (activeDriver){
                    final WebDriverWait loadWaiter = new WebDriverWait(activeDriver, Portal.MAXIMUM_WAIT_TIME);
                    final int loginAttempt = MDriver.attemptLogin(activeDriver);
                    if (loginAttempt == MDriver.ATTEMPT_SUCCEEDED) {
                        if (Portal.isEvaluationNeeded(activeDriver)) {
                            if (userRequested) {
                                Portal.reportEvaluationNeeded();
                            }
                            matchItem.setEnabled(true);
                            return;
                        }
                    } else if (loginAttempt == MDriver.CONNECTION_LOST) {
                        if (userRequested) {
                            App.reportConnectionLost();
                        }
                        matchItem.setEnabled(true);
                        return;
                    } else if (loginAttempt == MDriver.ATTEMPT_FAILED) {
                        if (userRequested) {
                            App.reportLoginAttemptFailed();
                        }
                        matchItem.setEnabled(true);
                        return;
                    }

                    final List<WebElement> tabs;
                    try {
                        activeDriver.navigate().to(Portal.CONTENTS_PAGE);
                        Portal.onPortal(activeDriver);
                        tabs = loadWaiter.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                                By.cssSelector(".nav-tabs > li")));
                    } catch (Exception e) {
                        if (userRequested) {
                            App.reportConnectionLost();
                        }
                        matchItem.setEnabled(true);
                        return;
                    }

                    final WebElement registeredTab = Portal.getTabElement("All Registered Courses", tabs);
                    if (registeredTab == null) {
                        if (userRequested) {
                            App.reportConnectionLost();
                        }
                        matchItem.setEnabled(true);
                        return;
                    } else {
                        registeredTab.click();
                    }
                    final WebElement registrationTable = activeDriver.findElementByCssSelector(".table-warning");
                    final WebElement tableBody = registrationTable.findElement(By.tagName("tbody"));
                    final List<WebElement> allRows = tableBody.findElements(By.tagName("tr"));
                    final List<WebElement> captions = tableBody.findElements(By.cssSelector("b, strong"));
                    final boolean isRegistered = captions.get(captions.size() - 1).getText().equals(Student.getSemester());
                    if (!isRegistered) {
                        if (userRequested) {
                            App.reportWarning("Match Failed",
                                    "Dashboard could not locate any registered course on your portal.\n" +
                                    "Please, register your courses first then try this action.");
                        }
                        matchItem.setEnabled(true);
                        return;
                    }

                    int match = allRows.size() -1;
                    int count = 0;
                    final List<String> foundCodes = new ArrayList<>();
                    final StringBuilder matchBuilder = new StringBuilder("Match completed successfully.\n");
                    while (!allRows.get(match).getText().equalsIgnoreCase(Student.getSemester())) {
                        final List<WebElement> data = allRows.get(match).findElements(By.tagName("td"));
                        final RegisteredCourse incoming = new RegisteredCourse(data.get(0).getText(), data.get(1).getText(),
                                data.get(2).getText(), data.get(3).getText(),data.get(4).getText(), "","", true);
                        final RegisteredCourse present = getByCode(incoming.getCode());
                        if (present == null) {//does not exist at all
                            ACTIVE_COURSES.add(incoming);
                            matchBuilder.append(incoming.getAbsoluteName()).append(" was found registered.\n");
                        } else if (!present.isConfirmed()) {//existed? override, but merge the schedule
                            incoming.setDay(present.getDay());
                            incoming.setTime(present.getTime());
                            ACTIVE_COURSES.set(getIndexOf(present), incoming);
                            matchBuilder.append(incoming.getName()).append(" was found registered - it's now merged, and confirmed set.\n");
                        }
                        foundCodes.add(incoming.getCode());
                        match--;
                        count++;
                    }
                    matchBuilder.append("-\n");

                    for (String existingCode : codes()) {
                        if (!foundCodes.contains(existingCode)) {
                            final RegisteredCourse existingCourse = Objects.requireNonNull(getByCode(existingCode));
                            if (existingCourse.isConfirmed()) {
                                ACTIVE_COURSES.remove(existingCourse);
                                matchBuilder.append(existingCourse.getAbsoluteName());
                                matchBuilder.append(" is not found registered for this semester, hence it was removed.\n");
                            } else {
                                matchBuilder.append(existingCourse.getAbsoluteName());
                                matchBuilder.append(" was not found registered - you can remove it.");
                            }
                        }
                    }

                    if (count == 0) {
                        App.reportWarning("Match Unsuccessful",
                                "Dashboard could not locate any registered course on your portal.\n" +
                                "Did you register for this semester yet?");
                    } else {
                        App.reportInfo("Matching Successful", matchBuilder.toString());
                    }

                    matchItem.setEnabled(true);
                }
            }).start();
        }
    }

    private static String generateNotificationWarning(String moduleName) {
        return "<p>You've added <b>"+moduleName+"</b> to your list of registered courses " +
                "without <i>verifying</i> it. Please check it out now, so Dashboard could know " +
                "it's on your Portal.</p>";
    }

    public static String[] names() {
        final String[] names = new String[ACTIVE_COURSES.size()];
        for (int i = 0; i < ACTIVE_COURSES.size(); i++) {
            names[i] = ACTIVE_COURSES.get(i).getName();
        }
        return names;
    }

    public static String[] codes() {
        final String[] codes = new String[ACTIVE_COURSES.size()];
        for (int i = 0; i < ACTIVE_COURSES.size(); i++) {
            codes[i] = ACTIVE_COURSES.get(i).getCode();
        }
        return codes;
    }

    public static RegisteredCourse getByCode(String code) {
        for (RegisteredCourse course : ACTIVE_COURSES) {
            if (course.getCode().equals(code)) {
                return course;
            }
        }
        App.silenceException("No registered course with code '"+code+"'.");
        return null;
    }

    /**
     * This and co- refers to the index in the list.
     * For index in the table. use the model.getRowOf(String)
     */
    private static int getIndexOf(RegisteredCourse course){
        for (int i = 0; i < ACTIVE_COURSES.size(); i++) {
            if (ACTIVE_COURSES.get(i).getCode().equals(course.getCode())) {
                return i;
            }
        }
        return -1;
    }

    private Container semesterContent() {
        final KMenuItem editItem = new KMenuItem("Edit");
        editItem.addActionListener(e-> {
            final String code = String.valueOf(activeModel.getValueAt(activeTable.getSelectedRow(), 0));
            final RegisteredCourse runningCourse = getByCode(code);
            if (runningCourse != null) {
                SwingUtilities.invokeLater(()-> new RegisteredCourseEditor(runningCourse).setVisible(true));
            }
        });

        final KMenuItem detailsItem = new KMenuItem("Show Details");
        detailsItem.addActionListener(e-> {
            final String code = String.valueOf(activeModel.getValueAt(activeTable.getSelectedRow(), 0));
            final RegisteredCourse course = getByCode(code);
            if (course != null) {
                course.exhibit();
            }
        });

        final KMenuItem checkItem = new KMenuItem("Checkout");
        checkItem.addActionListener(e-> new Thread(()-> {
            final String targetCode = String.valueOf(activeModel.getValueAt(activeTable.getSelectedRow(),0));
            final RegisteredCourse targetCourse = getByCode(targetCode);
            if (targetCourse != null) {
                startCheckout(targetCourse);
            }

        }).start());

        final KMenuItem removeItem = new KMenuItem("Remove");
        removeItem.addActionListener(e-> {
            final String code = String.valueOf(activeModel.getValueAt(activeTable.getSelectedRow(), 0));
            final RegisteredCourse course = getByCode(code);
            if (course != null && App.showYesNoCancelDialog("Confirm",
                    "Do you want to remove '"+course.getAbsoluteName()+"'?")){
                if (course.isConfirmed()) {
                    final int vInt = App.verifyUser("Enter your Mat. number to effect this changes:");
                    if (vInt == App.VERIFICATION_FALSE) {
                        App.reportMatError();
                        return;
                    } else if (vInt != App.VERIFICATION_TRUE) {
                        return;
                    }
                }
                ACTIVE_COURSES.remove(course);
            }
        });

        modulePopupMenu = new JPopupMenu();
        modulePopupMenu.add(detailsItem);
        modulePopupMenu.add(editItem);
        modulePopupMenu.add(checkItem);
        modulePopupMenu.add(removeItem);

        activeModel = new KTableModel();
        activeModel.setColumnIdentifiers(new String[] {"CODE", "NAME", "LECTURER", "SCHEDULE","STATUS"});
        activeTable = new KTable(activeModel);
        activeTable.setRowHeight(30);
        activeTable.setFont(KFontFactory.createBoldFont(15));
        activeTable.getTableHeader().setFont(KFontFactory.createBoldFont(16));
        activeTable.getTableHeader().setPreferredSize(new Dimension(activeTable.getPreferredSize().width,35));
        activeTable.getTableHeader().setForeground(Color.BLUE);
        activeTable.setFont(KFontFactory.createPlainFont(17));
        activeTable.getColumnModel().getColumn(0).setPreferredWidth(75);
        activeTable.getColumnModel().getColumn(1).setPreferredWidth(275);
        activeTable.getColumnModel().getColumn(2).setPreferredWidth(250);
        activeTable.getColumnModel().getColumn(3).setPreferredWidth(175);
        activeTable.getColumnModel().getColumn(4).setPreferredWidth(75);
        activeTable.centerAlignAllColumns();
        activeTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() >= 2) {
                    final int selectedRow = activeTable.getSelectedRow();
                    if (selectedRow >= 0) {
                        final String code = String.valueOf(activeTable.getValueAt(selectedRow, 0));
                        final RegisteredCourse course = getByCode(code);
                        if (course != null) {
                            course.exhibit();
                        }
                        e.consume();
                    }
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    activeTable.getSelectionModel().setSelectionInterval(0, activeTable.rowAtPoint(e.getPoint()));
                    SwingUtilities.invokeLater(()-> modulePopupMenu.show(activeTable, e.getX(), e.getY()));
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                mousePressed(e);
            }
        });

        final KButton addButton = KButton.createIconifiedButton("plus.png", 15, 15);
        addButton.redress();
        addButton.setText("Add");
        addButton.setFont(KFontFactory.createPlainFont(16));
        addButton.addActionListener(additionAction());

        hintLabel = new KLabel(activeModel.getRowCount() > 0 ? REGISTERED_HINT : UNREGISTERED_HINT,
                KFontFactory.createPlainFont(15), Color.GRAY);

        final KPanel bottomPanel = new KPanel(new BorderLayout());
        bottomPanel.add(new KPanel(new FlowLayout(FlowLayout.LEFT), hintLabel), BorderLayout.CENTER);
        bottomPanel.add(new KPanel(addButton), BorderLayout.EAST);

        final KPanel substancePanel = new KPanel();
        substancePanel.setLayout(new BoxLayout(substancePanel, BoxLayout.Y_AXIS));
        substancePanel.addAll(new KScrollPane(activeTable), bottomPanel, Box.createVerticalStrut(50));
        return substancePanel;
    }

    private static ActionListener additionAction(){
        return e-> {
            if (activeTable.getRowCount() >= 6) {
                App.reportError("Error","You can only register up to six (6) courses per semester.");
            } else {
                SwingUtilities.invokeLater(()-> new RegisteredCourseAdder().setVisible(true));
            }
        };
    }


    public static class RegisteredCourseAdder extends KDialog {
        KTextField codeField, nameField, lecturerField, roomField;
        KComboBox<String> daysBox, hoursBox, campusBox;
        KButton doneButton;
        KPanel checkPanel;
        KPanel contentPanel;

        public RegisteredCourseAdder(){
            super("New Registered Course");
            setResizable(true);
            setModalityType(KDialog.DEFAULT_MODALITY_TYPE);

            codeField = KTextField.rangeControlField(10);
            codeField.setPreferredSize(new Dimension(150, 30));
            final KPanel codeLayer = new KPanel(new BorderLayout());
            codeLayer.add(new KPanel(newHintLabel("*Course Code:")), BorderLayout.WEST);
            codeLayer.add(new KPanel(codeField), BorderLayout.CENTER);

            nameField = new KTextField(new Dimension(325,30));
            final KPanel nameLayer = new KPanel(new BorderLayout());
            nameLayer.add(new KPanel(newHintLabel("*Name:")), BorderLayout.WEST);
            nameLayer.add(new KPanel(nameField), BorderLayout.CENTER);

            lecturerField = new KTextField(new Dimension(325,30));
            final KPanel lecturerLayer = new KPanel(new BorderLayout());
            lecturerLayer.add(new KPanel(newHintLabel("*Lecturer:")), BorderLayout.WEST);
            lecturerLayer.add(new KPanel(lecturerField), BorderLayout.CENTER);

            campusBox = new KComboBox<>(Course.campuses(), -1);
            campusBox.addMask(Globals.UNKNOWN, "");
            final KPanel campusLayer = new KPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
            campusLayer.add(new KPanel(newHintLabel("Campus:")), BorderLayout.WEST);
            campusLayer.add(new KPanel(campusBox), BorderLayout.CENTER);

            roomField = new KTextField(new Dimension(275, 30));
            final KPanel roomLayer = new KPanel(new BorderLayout());
            roomLayer.add(new KPanel(newHintLabel("Lecture Room:")), BorderLayout.WEST);
            roomLayer.add(new KPanel(roomField), BorderLayout.CENTER);

            daysBox = new KComboBox<>(Course.weekDays(), -1);
            daysBox.addMask(Globals.UNKNOWN, "");
            hoursBox = new KComboBox<>(Course.periods(), -1);
            hoursBox.addMask(Globals.UNKNOWN, "");
            final KPanel scheduleLayer = new KPanel();
            scheduleLayer.addAll(newHintLabel("Day:"), daysBox,
                    Box.createRigidArea(new Dimension(50, 30)),
                    newHintLabel("Time:"), hoursBox);

            final KCheckBox instantCheck = new KCheckBox("Checkout now", true);
            instantCheck.setFont(KFontFactory.createPlainFont(15));
            instantCheck.setCursor(MComponent.HAND_CURSOR);
            checkPanel = new KPanel(instantCheck);
            ((FlowLayout) checkPanel.getLayout()).setVgap(10);

            final KButton cancelButton = new KButton("Cancel");
            cancelButton.addActionListener(e-> dispose());

            doneButton = new KButton("Done");
            doneButton.addActionListener(e-> {
                if (codeField.isBlank()) {
                    App.reportError(getRootPane(),"No Code", "Please provide the code of the course.");
                    codeField.requestFocusInWindow();
                } else if (nameField.isBlank()) {
                    App.reportError(getRootPane(),"No Name", "Please provide the name of the course.");
                    nameField.requestFocusInWindow();
                } else if (lecturerField.isBlank()) {
                    App.reportError(getRootPane(),"No Lecturer","Please provide the name of the lecturer.");
                    lecturerField.requestFocusInWindow();
                } else {
                    final String givenCode = codeField.getText().toUpperCase();
                    if (activeModel.getRowOf(givenCode) >= 0) {
                        App.reportError("Duplicate Code",
                                "Cannot add code '"+givenCode+"'. It's already assigned to a course in the list.");
                        return;
                    }

                    final RegisteredCourse addedCourse = new RegisteredCourse(codeField.getText(), nameField.getText(),
                            lecturerField.getText(), campusBox.getSelectionText(), roomField.getText(),
                            daysBox.getSelectionText(), hoursBox.getSelectionText(), false);
                    ACTIVE_COURSES.add(addedCourse);
                    dispose();
                    if (instantCheck.isSelected()) {
                        new Thread(()-> startCheckout(addedCourse)).start();
                    } else {
                        Notification.create("Local Registration", nameField.getText()+
                                " is locally added and may not be on your portal.",
                                generateNotificationWarning(nameField.getText()));
                    }
                }
            });

            getRootPane().setDefaultButton(doneButton);
            contentPanel = new KPanel();
            contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
            contentPanel.addAll(codeLayer, nameLayer, lecturerLayer, campusLayer, roomLayer, scheduleLayer, checkPanel,
                    MComponent.contentBottomGap(), new KPanel(new FlowLayout(FlowLayout.RIGHT), cancelButton, doneButton));
            setContentPane(contentPanel);
            pack();
            setMinimumSize(getPreferredSize());
            setLocationRelativeTo(Board.getRoot());
        }

        private static KLabel newHintLabel(String string) {
            return new KLabel(string, KFontFactory.createBoldFont(16));
        }
    }


    public static class RegisteredCourseEditor extends RegisteredCourseAdder {

        public RegisteredCourseEditor(RegisteredCourse original){
            super();
            setTitle(original.getName());
            codeField.setText(original.getCode());
            nameField.setText(original.getName());
            lecturerField.setText(original.getLecturer());
            campusBox.setSelectedItem(original.getCampus()); // remember, selection is not affected if it has no such item in its model
            roomField.setText(original.getRoom());
            daysBox.setSelectedItem(original.getDay());
            hoursBox.setSelectedItem(original.getTime());

            if (original.isConfirmed()) {
                codeField.setEditable(false);
                nameField.setEditable(false);
                lecturerField.setEditable(false);
            }
            contentPanel.remove(checkPanel);
            doneButton.removeActionListener(doneButton.getActionListeners()[0]);
            doneButton.addActionListener(e-> {
                if (codeField.isBlank()) {
                    App.reportError(getRootPane(),"No Code", "Please provide the code of the course.");
                    codeField.requestFocusInWindow();
                } else if (nameField.isBlank()) {
                    App.reportError(getRootPane(),"No Name", "Please provide the name of the course.");
                    nameField.requestFocusInWindow();
                } else if (lecturerField.isBlank()) {
                    App.reportError(getRootPane(),"No Lecturer","Please provide the name of the lecturer.");
                    lecturerField.requestFocusInWindow();
                } else {
                    final RegisteredCourse refracted = new RegisteredCourse(codeField.getText(), nameField.getText(),
                            lecturerField.getText(), campusBox.getSelectionText(), roomField.getText(),
                            daysBox.getSelectionText(), hoursBox.getSelectionText(), original.isConfirmed());
                    for (int i = 0; i < activeModel.getRowCount(); i++) {
                        if (i == activeTable.getSelectedRow()) {
                            continue;
                        }
                        final String refCode = refracted.getCode();
                        if ((refCode.equalsIgnoreCase(String.valueOf(activeModel.getValueAt(i, 0))))) {
                            App.reportError("Duplicate Code",
                                    "Cannot add code '"+refCode+"'. It's already assigned to a course in the list.");
                            return;
                        }
                    }
                    refracted.setStatus(original.getStatus());
                    ACTIVE_COURSES.set(getIndexOf(original), refracted);
                    dispose();
                }
            });
        }
    }


    public static void serialize(){
        final String[] data = new String[ACTIVE_COURSES.size()];
        for (int i = 0; i < data.length; i++) {
            data[i] = ACTIVE_COURSES.get(i).exportContent();
        }
        Serializer.toDisk(data, Serializer.inPath("modules", "registered.ser"));
    }

    public static void deserializeModules(){
        final Object obj = Serializer.fromDisk(Serializer.inPath("modules", "registered.ser"));
        if (obj == null) {
            App.silenceException("Failed to read Active Courses.");
        } else {
            final String[] data = (String[]) obj;
            for (String entry : data) {
                ACTIVE_COURSES.add(RegisteredCourse.create(entry));
            }
        }
    }

}
