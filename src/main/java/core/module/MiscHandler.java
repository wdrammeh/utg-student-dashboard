package core.module;

import core.user.Student;
import core.utils.App;
import core.utils.FontFactory;
import proto.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Handles operations relating to Miscellaneous ("Misc.") courses.
 * 
 * Deals with miscellaneous courses.
 * A module is considered misc. if its academic-year falls out
 * of the student's bachelors level specification.
 * @see Course#isMinor()
 */
public class MiscHandler {
    private KMenuItem detailsItem;
    private KMenuItem editItem;
    private KMenuItem removeItem;
    private KMenuItem confirmItem;
    private KMenuItem newItem;
    private KPopupMenu popupMenu;
    private static KTable miscTable;
    public static KTableModel miscModel;


    public MiscHandler(){
        setupTable();
        configurePopUp();
    }

    /**
     * Adds the given course to the miscModel.
     * Triggered by the monitor to signal that addition
     * has been directed to the miscellaneous-table.
     */
    public static void add(Course course) {
        miscModel.addRow(new String[] {course.getCode(), course.getName(), course.getLecturer(),
                course.getGrade(), course.getYear()});
    }

    /**
     * Removes the gives course from the miscModel.
     * Triggered by the monitor to signal that removal
     * has directed to miscellaneous-table.
     */
    public static void remove(Course summerCourse){
        miscModel.removeRow(miscModel.getRow(summerCourse.getCode()));
    }

    private void setupTable(){
        miscModel = new KTableModel();
        miscModel.setColumnIdentifiers(new String[] {"CODE", "NAME", "LECTURER", "GRADE", "YEAR"});
        ModuleHandler.ALL_MODELS.add(miscModel);

        miscTable = new KTable(miscModel);
        miscTable.setRowHeight(30);
        miscTable.setHeaderHeight(30);
        miscTable.getTableHeader().setFont(FontFactory.createBoldFont(16));
        miscTable.setFont(FontFactory.createPlainFont(15));
        miscTable.centerAlignAllColumns();
        miscTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    detailsItem.setEnabled(true);
                    editItem.setEnabled(true);
                    removeItem.setEnabled(true);
                    confirmItem.setEnabled(true);
                    miscTable.getSelectionModel().setSelectionInterval(0, miscTable.rowAtPoint(e.getPoint()));
                    SwingUtilities.invokeLater(()-> popupMenu.show(miscTable, e.getX(), e.getY()));
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                mousePressed(e);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() >= 2) {
                    final int selectedRow = miscTable.getSelectedRow();
                    if (selectedRow >= 0) {
                        final String code = String.valueOf(miscTable.getValueAt(selectedRow, 0));
                        final Course course = ModuleHandler.getModuleByCode(code);
                        if (course != null) {
                            course.exhibit();
                        }
                    }
                    e.consume();
                }
            }
        });
    }

    private void configurePopUp(){
        detailsItem = new KMenuItem(ModuleHandler.DETAILS);
        detailsItem.addActionListener(e-> SwingUtilities.invokeLater(()-> {
            final Course course = ModuleHandler.getModuleByCode(String.valueOf(miscModel.getValueAt(
                    miscTable.getSelectedRow(), 0)));
            if (course != null) {
                course.exhibit();
            }
        }));

        editItem = new KMenuItem(ModuleHandler.EDIT);
        editItem.addActionListener(e-> {
            final String code = String.valueOf(miscModel.getValueAt(miscTable.getSelectedRow(), 0));
            final Course course = ModuleHandler.getModuleByCode(code);
            if (course != null) {
                final  MiscModuleEditor editor = new MiscModuleEditor(course);
                SwingUtilities.invokeLater(()-> editor.setVisible(true));
            }
        });

        removeItem = new KMenuItem(ModuleHandler.DELETE);
        removeItem.addActionListener(e-> {
            final String code = String.valueOf(miscModel.getValueAt(miscTable.getSelectedRow(), 0));
            final Course course = ModuleHandler.getModuleByCode(code);
            if (course != null) {
                ModuleHandler.getMonitor().remove(course);
            }
        });

        confirmItem = new KMenuItem(ModuleHandler.CONFIRM);
        confirmItem.addActionListener(e-> {
            final String code = String.valueOf(miscModel.getValueAt(miscTable.getSelectedRow(), 0));
            final Course course = ModuleHandler.getModuleByCode(code);
            if (course != null) {
                new Thread(()-> ModuleHandler.launchVerification(course)).start();
            }
        });

        newItem = new KMenuItem(ModuleHandler.ADD);
        newItem.addActionListener(e-> {
            final MiscModuleAdder adder = new MiscModuleAdder();
            SwingUtilities.invokeLater(()-> adder.setVisible(true));
        });

        popupMenu = new KPopupMenu();
        popupMenu.add(detailsItem);
        popupMenu.add(editItem);
        popupMenu.add(confirmItem);
        popupMenu.add(removeItem);
        popupMenu.add(newItem);
    }

    public Component getPresent(){
        final KScrollPane miscScrollPane = miscTable.sizeMatchingScrollPane();
        miscScrollPane.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    miscTable.clearSelection();
                    detailsItem.setEnabled(false);
                    editItem.setEnabled(false);
                    removeItem.setEnabled(false);
                    confirmItem.setEnabled(false);
                    popupMenu.show(miscScrollPane, e.getX(), e.getY());
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                mousePressed(e);
            }
        });

        final KPanel present = new KPanel();
        present.setLayout(new BoxLayout(present, BoxLayout.Y_AXIS));
        present.addAll(miscScrollPane, Box.createVerticalStrut(15));
        return present;
    }


    public static class MiscModuleAdder extends ModuleHandler.ModuleAdder {
        KComboBox<String> semestersBox;

        private MiscModuleAdder(){
            super("", "");
            setTitle("New Miscellaneous Course");

            semestersBox = new KComboBox<>(new String[] {Student.FIRST_SEMESTER, Student.SECOND_SEMESTER,
                    Student.SUMMER_SEMESTER});
            semesterPanel.removeLast();
            semesterPanel.add(new KPanel(semestersBox), BorderLayout.CENTER);

            actionButton.removeActionListener(actionButton.getActionListeners()[0]);
            actionButton.addActionListener(additionListener());
        }

        private ActionListener additionListener() {
            return e-> {
                final String givenYear;
                if (Student.isValidAcademicYear(yearField.getText())) {
                    givenYear = yearField.getText();
                } else {
                    App.reportError("Error", "Sorry, "+yearField.getText()+" is not a valid academic year.\n" +
                                    "Dashboard reads years in the format: yyyy/yyyy. E.g 2016/2017");
                    yearField.requestFocusInWindow();
                    return;
                }

                if (givenYear.equals(Student.firstAcademicYear())) {
                    App.reportError("Error",
                            "Sorry, the year you provided ["+givenYear+"] corresponds your first academic year.\n" +
                                    "By convention, Dashboard restricts the addition of specific resources to the miscellaneous table.\n" +
                                    "If you've done this module in your first year, consider adding it in the appropriate place.");
                    yearField.requestFocusInWindow();
                    return;
                } else if (givenYear.equals(Student.secondAcademicYear())) {
                    App.reportError("Error",
                            "Sorry, the year you provided ["+givenYear+"] corresponds your second academic year.\n" +
                                    "By convention, Dashboard restricts the addition of specific resources to the miscellaneous table.\n" +
                                    "If you've done this module in your second year, consider adding it in the appropriate place.");
                    yearField.requestFocusInWindow();
                    return;
                } else if (givenYear.equals(Student.thirdAcademicYear())) {
                    App.reportError("Error",
                            "Sorry, the year you provided ["+givenYear+"] corresponds your third academic year.\n" +
                                    "By convention, Dashboard restricts the addition of specific resources to the miscellaneous table.\n" +
                                    "If you've done this module in your third year, consider adding it in the appropriate place.");
                    yearField.requestFocusInWindow();
                    return;
                } else if (givenYear.equals(Student.fourthAcademicYear())) {
                    App.reportError("Error",
                            "Sorry, the year you provided ["+givenYear+"] corresponds your fourth academic year.\n" +
                                    "By convention, Dashboard restricts the addition of specific resources to the miscellaneous table.\n" +
                                    "If you've done this module in your fourth year, consider adding it in the appropriate place.");
                    yearField.requestFocusInWindow();
                    return;
                }

                if (codeField.isBlank()) {
                    App.reportError(getRootPane(),"No Code", "Please provide the code of the course.");
                    codeField.requestFocusInWindow();
                } else if (nameField.isBlank()) {
                    App.reportError(getRootPane(),"No Name","Please provide the name of the course.");
                    nameField.requestFocusInWindow();
                } else if (scoreField.isBlank()) {
                    App.reportError(getRootPane(),"No Score","Please enter the score you get from this course.");
                    scoreField.requestFocusInWindow();
                } else {
                    double score;
                    try {
                        score = Double.parseDouble(scoreField.getText());
                    } catch (NumberFormatException formatError){
                        ModuleHandler.reportScoreInvalid(scoreField.getText(), getRootPane());
                        scoreField.requestFocusInWindow();
                        return;
                    }

                    if (score < 0 || score > 100) {
                        ModuleHandler.reportScoreOutOfRange(getRootPane());
                        scoreField.requestFocusInWindow();
                        return;
                    }

                    if (ModuleHandler.exists(codeField.getText())) {
                        ModuleHandler.reportCodeDuplication(codeField.getText());
                        codeField.requestFocusInWindow();
                        return;
                    }

                    final Course course = new Course(givenYear, semestersBox.getSelectionText(),
                            codeField.getText().toUpperCase(), nameField.getText(), lecturerField.getText(),
                            campusBox.getSelectionText(), roomField.getText(), dayBox.getSelectionText(),
                            timeBox.getSelectionText(), score, Integer.parseInt(String.valueOf(creditBox.getSelectedItem())),
                            requirementBox.getSelectionText(), false);
                    ModuleHandler.getMonitor().add(course);
                    dispose();
                }
            };
        }
    }


    public static class MiscModuleEditor extends MiscModuleAdder {
        private Course target;

        private MiscModuleEditor(Course miscCourse){
            super();
            setTitle(miscCourse.getName()+" - Miscellaneous");
            target = miscCourse;

            yearField.setText(target.getYear());
            codeField.setText(target.getCode());
            nameField.setText(target.getName());
            lecturerField.setText(target.getLecturer());
            lecturerField.setEditable(target.isLecturerEditable());
            dayBox.setSelectedItem(target.getDay());
            timeBox.setSelectedItem(target.getTime());
            campusBox.setSelectedItem(target.getCampus());
            roomField.setText(target.getRoom());
            requirementBox.setSelectedItem(target.getRequirement());
            creditBox.setSelectedItem(target.getCreditHours());
            scoreField.setText(Double.toString(target.getScore()));

            if (miscCourse.isConfirmed()) {
                yearField.setEditable(false);
                codeField.setEditable(false);
                nameField.setEditable(false);
                scoreField.setEditable(false);
            }

            actionButton.removeActionListener(actionButton.getActionListeners()[0]);
            actionButton.addActionListener(editionListener());
            actionButton.setText("Done");
        }

        private ActionListener editionListener() {
            return e-> {
                final String givenYear;
                if (Student.isValidAcademicYear(yearField.getText())) {
                    givenYear = yearField.getText();
                } else {
                    App.reportError("Invalid Format",
                            "Sorry, "+yearField.getText()+" is not a valid academic year.\n" +
                                    "Dashboard reads years in the format: yyyy/yyyy. E.g 2016/2017");
                    yearField.requestFocusInWindow();
                    return;
                }
                if (givenYear.equals(Student.firstAcademicYear())) {
                    App.reportError("Error",
                            "Sorry, the year you provided ["+givenYear+"] corresponds your first academic year.\n" +
                                    "By convention, Dashboard restricts the addition of specific resources to the miscellaneous table.\n" +
                                    "If you've done this module in your first year, consider adding it in the appropriate place.");
                    yearField.requestFocusInWindow();
                    return;
                } else if (givenYear.equals(Student.secondAcademicYear())) {
                    App.reportError("Error",
                            "Sorry, the year you provided ["+givenYear+"] corresponds your second academic year.\n" +
                                    "By convention, Dashboard restricts the addition of specific resources to the miscellaneous table.\n" +
                                    "If you've done this module in your second year, consider adding it in the appropriate place.");
                    yearField.requestFocusInWindow();
                    return;
                } else if (givenYear.equals(Student.thirdAcademicYear())) {
                    App.reportError("Error",
                            "Sorry, the year you provided ["+givenYear+"] corresponds your third academic year.\n" +
                                    "By convention, Dashboard restricts the addition of specific resources to the miscellaneous table.\n" +
                                    "If you've done this module in your third year, consider adding it in the appropriate place.");
                    yearField.requestFocusInWindow();
                    return;
                } else if (givenYear.equals(Student.fourthAcademicYear())) {
                    App.reportError("Error",
                            "Sorry, the year you provided ["+givenYear+"] corresponds your fourth academic year.\n" +
                            "By convention, Dashboard restricts the addition of specific resources to the miscellaneous table.\n" +
                                    "If you've done this module in your fourth year, consider adding it in the appropriate place.");
                    yearField.requestFocusInWindow();
                    return;
                }

                if (codeField.isBlank()) {
                    App.reportError(getRootPane(),"No Code", "Please provide the code of the course.");
                    codeField.requestFocusInWindow();
                } else if (nameField.isBlank()) {
                    App.reportError(getRootPane(),"No Name","Please provide the name of the course.");
                    nameField.requestFocusInWindow();
                } else if (scoreField.isBlank()) {
                    App.reportError(getRootPane(),"No Score","Please enter the score you get from this course.");
                    scoreField.requestFocusInWindow();
                } else {
                    double score;
                    try {
                        score = Double.parseDouble(scoreField.getText());
                    } catch (NumberFormatException formatError){
                        ModuleHandler.reportScoreInvalid(scoreField.getText(), this.getRootPane());
                        scoreField.requestFocusInWindow();
                        return;
                    }
                    if (score < 0 || score > 100) {
                        ModuleHandler.reportScoreOutOfRange(this.getRootPane());
                        scoreField.requestFocusInWindow();
                        return;
                    }

                    for (int row = 0; row < miscModel.getRowCount(); row++) {
                        if (row == miscModel.getSelectedRow()) {
                            continue;
                        }
                        final String tempCode = String.valueOf(miscModel.getValueAt(row, 0));
                        if (tempCode.equalsIgnoreCase(codeField.getText())) {
                            ModuleHandler.reportCodeDuplication(codeField.getText());
                            codeField.requestFocusInWindow();
                            return;
                        }
                    }

                    if (ModuleHandler.existsExcept(miscModel, codeField.getText())) {
                        ModuleHandler.reportCodeDuplication(codeField.getText());
                        codeField.requestFocusInWindow();
                        return;
                    }

                    final Course course = new Course(yearField.getText(), semestersBox.getSelectionText(),
                            codeField.getText().toUpperCase(), nameField.getText(), lecturerField.getText(),
                            campusBox.getSelectionText(), roomField.getText(), dayBox.getSelectionText(),
                            timeBox.getSelectionText(), score, Integer.parseInt(String.valueOf(creditBox.getSelectedItem())),
                            requirementBox.getSelectionText(), target.isConfirmed());
                    course.setStatus(target.getStatus());
                    course.setLecturerEditable(target.isLecturerEditable());
                    ModuleHandler.substitute(target, course);
                    dispose();
                }
            };
        }
    }

}
