package core.task.creator;

import core.Board;
import core.module.SemesterActivity;
import core.task.handler.AssignmentHandler;
import core.task.self.AssignmentSelf;
import core.utils.*;
import proto.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import static core.task.creator.TodoCreator.DESCRIPTION_LIMIT;

public class AssignmentCreator extends KDialog {
    private KTextField nameField;
    private JRadioButton groupChoice;
    private KTextArea questionArea;
    private JComboBox<String> modes;
    private String meanValue;
    private KTextField dField, mField, yField;

    public AssignmentCreator(){
        super("New Assignment");
        setModalityType(TodoCreator.DEFAULT_MODALITY_TYPE);
        setResizable(true);

        final Font labelsFont = FontFactory.createBoldFont(16);
        final Font hintsFont = FontFactory.createPlainFont(16);

        final KPanel importPanel = new KPanel();
        final String[] activeNames = SemesterActivity.getDisplayNames();
        if (activeNames.length >= 1) {
            final KComboBox<String> importBox = new KComboBox<>(activeNames);
            importBox.setFont(FontFactory.createPlainFont(15));
            importBox.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            importBox.setFocusable(false);
            importBox.setToolTipText("Import");
            importBox.addActionListener(e-> {
                if (importBox.getSelectedIndex() != 0) {
                    nameField.setText(String.valueOf(importBox.getSelectedItem()));
                }
            });
            importPanel.add(importBox);
        }

        nameField = KTextField.rangeControlField(DESCRIPTION_LIMIT);
        nameField.setPreferredSize(new Dimension(300,30));
        final KPanel namePanel = new KPanel(new BorderLayout());
        namePanel.add(new KPanel(new KLabel("Course Name:",labelsFont)),BorderLayout.WEST);
        namePanel.add(new KPanel(nameField),BorderLayout.CENTER);

        dField = KTextField.dayField();
        mField = KTextField.monthField();
        yField = KTextField.yearField();

        final KPanel deadLinePanel = new KPanel(new FlowLayout(FlowLayout.CENTER));
        deadLinePanel.addAll(new KLabel("Deadline:",labelsFont), MComponent.createRigidArea(50,30),new KLabel("Day",
                        hintsFont),dField, MComponent.createRigidArea(20,30),
                new KLabel("Month",hintsFont),mField, MComponent.createRigidArea(20,30),new KLabel("Year",hintsFont),yField);

        groupChoice = new JRadioButton("Group Work");
        groupChoice.setFont(FontFactory.createPlainFont(15));
        groupChoice.setFocusable(false);
        groupChoice.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        final JRadioButton personalChoice = new JRadioButton("Individual",true);
        personalChoice.setFont(groupChoice.getFont());
        personalChoice.setFocusable(false);
        personalChoice.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        final ButtonGroup radioChoices = new ButtonGroup();
        radioChoices.add(groupChoice);
        radioChoices.add(personalChoice);
        final KPanel choicesPlate = new KPanel(new FlowLayout(FlowLayout.CENTER));//because it's boring - the ButtonGroup itself cannot be joined as a component
        choicesPlate.addAll(groupChoice,personalChoice);
        final KPanel groupPanel = new KPanel(new BorderLayout());
        groupPanel.add(new KPanel(new KLabel("Assignment Type:",labelsFont)),BorderLayout.WEST);
        groupPanel.add(new KPanel(choicesPlate),BorderLayout.CENTER);

        modes = new JComboBox<>(new String[]{"To submit a hard copy","To submit a soft copy", "Through an email address", "Through a web site", "Other"});
        modes.setFont(FontFactory.createPlainFont(15));
        modes.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        modes.setFocusable(false);
        modes.addActionListener(e -> {
            final String selectedOption = String.valueOf(modes.getSelectedItem());
            if (selectedOption.contains("mail")) {
                try {
                    meanValue = keepAskingEmailAddress();
                } catch (NullPointerException ex){
                    modes.setSelectedIndex(0);
                }
            } else if (selectedOption.contains("web")) {
                try {
                    meanValue = keepAskingWebAddress();
                } catch (NullPointerException ex){
                    modes.setSelectedIndex(0);
                }
            }
        });
        final KPanel modePanel = new KPanel(new BorderLayout());
        modePanel.add(new KPanel(new KLabel("Submission Mode:",labelsFont)),BorderLayout.WEST);
        modePanel.add(new KPanel(modes),BorderLayout.CENTER);

        final KPanel questionPanel = new KPanel(new BorderLayout());
        questionPanel.add(new KPanel(new KLabel("Write the question(s) below", FontFactory.createPlainFont(14))),BorderLayout.NORTH);
        questionArea = new KTextArea();
        final KScrollPane scrollPane = questionArea.outerScrollPane(new Dimension(475,150));
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.BLUE,2,true));
        questionPanel.add(scrollPane,BorderLayout.CENTER);

        final KButton cancelButton = new KButton("Cancel");
        cancelButton.addActionListener(e -> this.dispose());
        final KButton addButton = new KButton("Add");
        addButton.addActionListener(listener());

        final KPanel contentPlate = new KPanel();
        contentPlate.setLayout(new BoxLayout(contentPlate,BoxLayout.Y_AXIS));
        contentPlate.addAll(importPanel, namePanel,deadLinePanel,groupPanel,modePanel,groupPanel, MComponent.createRigidArea(500,25),questionPanel,
                MComponent.contentBottomGap(), new KPanel(new FlowLayout(FlowLayout.RIGHT), cancelButton, addButton));
        setContentPane(contentPlate);
        rootPane.setDefaultButton(addButton);
        pack();
        setMinimumSize(getPreferredSize());
        setLocationRelativeTo(Board.getRoot());

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                cancelButton.doClick();
            }
        });
    }

    private String keepAskingEmailAddress() throws NullPointerException {
        final String mailAddress = App.requestInput(this.getRootPane(), "Email Address","This assignment will be sent to the email: ");
        if (mailAddress == null) {
            throw new NullPointerException();
        } else if (Globals.hasNoText(mailAddress)) {
            App.reportError(this.getRootPane(), "No Email", "Please provide an appropriate email address.");
            return keepAskingEmailAddress();
        } else {
            return mailAddress;
        }
    }

    private String keepAskingWebAddress() throws NullPointerException {
        final String webAddress = App.requestInput(this.getRootPane(),"Web Address","This assignment will be posted on the site: ");
        if (webAddress == null) {
            throw new NullPointerException();
        } else if (Globals.hasNoText(webAddress)) {
            App.reportError(this.getRootPane(), "No Web Site", "Please provide an appropriate web site name.");
            return keepAskingWebAddress();
        } else {
            return webAddress;
        }
    }

    public KTextField getNameField(){
        return nameField;
    }

    public boolean isGroup(){
        return groupChoice.isSelected();
    }

    public String getQuestion(){
        return questionArea.getText();
    }

    public String getSelectedMode(){
        return String.valueOf(modes.getSelectedItem());
    }

    public String getMeanValue(){
        return meanValue;
    }

    public Date getProvidedDeadLine() {
        if (Globals.hasNoText(dField.getText()) || Globals.hasNoText(mField.getText()) || Globals.hasNoText(yField.getText())) {
            return null;
        }
        return MDate.date(dField.getTextAsInt(), mField.getTextAsInt(), yField.getTextAsInt(), true);
    }

    public ActionListener listener(){
        return e -> {
            final String name = nameField.getText();
            if (Globals.hasNoText(name)) {
                App.reportError(getRootPane(), "No Name", "Please provide the name of the course.");
                nameField.requestFocusInWindow();
            } else if (name.length() > DESCRIPTION_LIMIT) {
                App.reportError(getRootPane(), "Error", "Sorry, the subject name cannot exceed "+
                                DESCRIPTION_LIMIT +" characters.");
                nameField.requestFocusInWindow();
            } else if (getProvidedDeadLine() == null) {
                App.reportError(getRootPane(), "Deadline Error",
                        "Please fill out all the fields for the deadline. You can change them later.");
            } else {
                final String type = isGroup() ? "Group Assignment" : "Individual Assignment";
                final String question = getQuestion();
                final Date givenDate = getProvidedDeadLine();
                if (givenDate.before(new Date())) {
                    App.reportError(getRootPane(), "Past Deadline",
                            "That deadline is already past. Enter a valid deadline.");
                    return;
                }
                final String deadline = MDate.formatDay(givenDate);
                final String preMean = String.valueOf(getSelectedMode());
                String mean;
                if (preMean.contains("hard")) {
                    mean = "A Hard Copy";
                } else if (preMean.contains("soft")) {
                    mean = "A Soft Copy";
                } else if (preMean.contains("email")) {
                    mean = "An Email Address - " + getMeanValue();
                } else if (preMean.contains("web")) {
                    mean = "A Webpage - " + getMeanValue();
                } else {
                    mean = "Other Means";
                }

                AssignmentHandler.newIncoming(new AssignmentSelf(name, deadline, question, isGroup(), mean));
                dispose();
            }
        };
    }

}
