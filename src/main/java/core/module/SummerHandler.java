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
 * Handles operations relating to summer courses, and semesters.
 */
public class SummerHandler {
    private KMenuItem detailsItem, editItem, removeItem, confirmItem, newItem;
    private JPopupMenu popupMenu;
    private static KTable summerTable;
    public static KTableModel summerModel;


    public SummerHandler(){
        setupTable();
        configurePopup();
    }

    public static void add(Course summerCourse){
        summerModel.addRow(new String[] {summerCourse.getCode(), summerCourse.getName(), summerCourse.getLecturer(),
                summerCourse.getGrade(), summerCourse.getYear()});
    }

    public static void remove(Course summerCourse){
        summerModel.removeRow(summerModel.getRow(summerCourse.getCode()));
    }

    private void setupTable(){
        summerModel = new KTableModel();
        summerModel.setColumnIdentifiers(new String[] {"CODE", "NAME", "LECTURER", "GRADE", "YEAR"});
        ModuleHandler.ALL_MODELS.add(summerModel);

        summerTable = new KTable(summerModel);
        summerTable.setRowHeight(30);
        summerTable.setHeaderHeight(30);
        summerTable.setFont(FontFactory.createPlainFont(15));
        summerTable.getTableHeader().setFont(FontFactory.createBoldFont(16));
        summerTable.centerAlignAllColumns();
        summerTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    detailsItem.setEnabled(true);
                    editItem.setEnabled(true);
                    removeItem.setEnabled(true);
                    confirmItem.setEnabled(true);
                    summerTable.getSelectionModel().setSelectionInterval(0, summerTable.rowAtPoint(e.getPoint()));
                    popupMenu.show(summerTable, e.getX(), e.getY());
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                mousePressed(e);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() >= 2) {
                    final int selectedRow = summerTable.getSelectedRow();
                    if (selectedRow >= 0) {
                        final String code = String.valueOf(summerTable.getValueAt(selectedRow, 0));
                        final Course c = ModuleHandler.getModuleByCode(code);
                        if (c != null) {
                            c.exhibit();
                        }
                    }
                    e.consume();
                }
            }
        });
    }

    private void configurePopup(){
        detailsItem = new KMenuItem(ModuleHandler.DETAILS);
        detailsItem.addActionListener(e-> {
            final String code = String.valueOf(summerModel.getValueAt(summerTable.getSelectedRow(), 0));
            final Course c = ModuleHandler.getModuleByCode(code);
            if (c != null) {
                c.exhibit();
            }
        });

        editItem = new KMenuItem(ModuleHandler.EDIT);
        editItem.addActionListener(e-> {
            final String code = String.valueOf(summerModel.getValueAt(summerTable.getSelectedRow(), 0));
            final Course course = ModuleHandler.getModuleByCode(code);
            if (course != null) {
                final SummerModuleEditor editor = new SummerModuleEditor(course);
                SwingUtilities.invokeLater(()-> editor.setVisible(true));
            }
        });

        removeItem = new KMenuItem(ModuleHandler.DELETE);
        removeItem.addActionListener(e-> {
            final String code = String.valueOf(summerModel.getValueAt(summerTable.getSelectedRow(),0));
            final Course course = ModuleHandler.getModuleByCode(code);
            if (course != null) {
                ModuleHandler.getMonitor().remove(course);
            }
        });

        confirmItem = new KMenuItem(ModuleHandler.CONFIRM);
        confirmItem.addActionListener(e-> {
            final String code = String.valueOf(summerModel.getValueAt(summerTable.getSelectedRow(),0));
            final Course course = ModuleHandler.getModuleByCode(code);
            if (course != null) {
                new Thread(()-> ModuleHandler.launchVerification(course)).start();
            }
        });

        newItem = new KMenuItem(ModuleHandler.ADD);
        newItem.addActionListener(e-> {
            final SummerModuleAdder adder = new SummerModuleAdder();
            SwingUtilities.invokeLater(()-> adder.setVisible(true));
        });

        popupMenu = new JPopupMenu();
        popupMenu.add(detailsItem);
        popupMenu.add(editItem);
        popupMenu.add(confirmItem);
        popupMenu.add(removeItem);
        popupMenu.add(newItem);
    }

    public KPanel getPresent() {
        final KScrollPane summerScrollPane = summerTable.sizeMatchingScrollPane();
        summerScrollPane.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    summerTable.clearSelection();
                    detailsItem.setEnabled(false);
                    editItem.setEnabled(false);
                    removeItem.setEnabled(false);
                    confirmItem.setEnabled(false);
                    popupMenu.show(summerScrollPane, e.getX(), e.getY());
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                mousePressed(e);
            }
        });

        final KPanel present = new KPanel();
        present.setLayout(new BoxLayout(present, BoxLayout.Y_AXIS));
        present.addAll(summerScrollPane, Box.createVerticalStrut(15));
        return present;
    }


    public static class SummerModuleAdder extends ModuleHandler.ModuleAdder {
        KComboBox<String> availableYearsBox;

        private SummerModuleAdder(){
            super("", Student.SUMMER_SEMESTER);
            setTitle("New Summer Course");
            availableYearsBox = new KComboBox<>(new String[] {Student.firstAcademicYear(),
                    Student.secondAcademicYear(), Student.thirdAcademicYear(), Student.fourthAcademicYear()});
            availableYearsBox.setFont(FontFactory.createPlainFont(15));
            yearPanel.removeLast();
            yearPanel.add(new KPanel(availableYearsBox), BorderLayout.CENTER);

            actionButton.removeActionListener(actionButton.getActionListeners()[0]);
            actionButton.addActionListener(additionListener());
        }

        private ActionListener additionListener() {
            return e-> {
                if (codeField.isBlank()) {
                    App.reportError(getRootPane(), "No Code","Please provide the code of the course.");
                    codeField.requestFocusInWindow();
                } else if (nameField.isBlank()) {
                    App.reportError(getRootPane(),"No Name","Please provide the name of the course.");
                    nameField.requestFocusInWindow();
                } else if (scoreField.isBlank()) {
                    App.reportError(getRootPane(),"No Score","Please enter the score you get from this course.");
                    scoreField.requestFocusInWindow();
                } else {
                    final double score;
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

                    final Course course = new Course(availableYearsBox.getSelectionText(), semesterField.getText(),
                            codeField.getText().toUpperCase(), nameField.getText(), lecturerField.getText(),
                            campusBox.getSelectionText(), roomField.getText(),
                            dayBox.getSelectionText(), timeBox.getSelectionText(),
                            score, Integer.parseInt(String.valueOf(creditBox.getSelectedItem())),
                            requirementBox.getSelectionText(), false);
                    ModuleHandler.getMonitor().add(course);
                    dispose();
                }
            };
        }
    }


    public static class SummerModuleEditor extends SummerModuleAdder {
        private final Course target;

        private SummerModuleEditor(Course summerCourse){
            super();
            this.setTitle(summerCourse.getName());
            this.target = summerCourse;

            availableYearsBox.setSelectedItem(target.getYear());
            availableYearsBox.setEnabled(!target.isConfirmed());

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

            if (summerCourse.isConfirmed()) {
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
                if (codeField.isBlank()) {
                    App.reportError(this.getRootPane(),"No Code","Please provide the code of the course.");
                    codeField.requestFocusInWindow();
                } else if (nameField.isBlank()) {
                    App.reportError(this.getRootPane(),"No Name","Please provide the name of the course.");
                    nameField.requestFocusInWindow();
                } else if (scoreField.isBlank()) {
                    App.reportError(this.getRootPane(),"No Score","Please enter the score you get from this course.");
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

                    for (int row = 0; row < summerModel.getRowCount(); row++) {
                        if (row == summerModel.getSelectedRow()) {
                            continue;
                        }
                        final String tempCode = String.valueOf(summerModel.getValueAt(row, 0));
                        if (tempCode.equalsIgnoreCase(codeField.getText())) {
                            ModuleHandler.reportCodeDuplication(codeField.getText());
                            codeField.requestFocusInWindow();
                            return;
                        }
                    }

                    if (ModuleHandler.existsExcept(summerModel, codeField.getText())) {
                        ModuleHandler.reportCodeDuplication(codeField.getText());
                        codeField.requestFocusInWindow();
                        return;
                    }

                    final Course course = new Course(availableYearsBox.getSelectionText(), semesterField.getText(),
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
