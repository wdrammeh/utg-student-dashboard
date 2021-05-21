package core.task.creator;

import core.Board;
import core.module.SemesterActivity;
import core.task.handler.AssignmentHandler;
import core.utils.App;
import core.utils.Globals;
import core.utils.MComponent;
import core.utils.MDate;
import proto.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

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

        final Font labelsFont = KFontFactory.createBoldFont(16);
        final Font hintsFont = KFontFactory.createPlainFont(16);

        final KPanel importPanel = new KPanel();
        final String[] activeNames = SemesterActivity.names();
        if (activeNames.length >= 1) {
            final KComboBox<String> importBox = new KComboBox<>(activeNames);
            importBox.setFont(KFontFactory.createPlainFont(15));
            importBox.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            importBox.setFocusable(false);
            importBox.setToolTipText("Import");
            importBox.addActionListener(e-> {
                nameField.setText(String.valueOf(importBox.getSelectedItem()));
                dField.requestFocusInWindow();
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
        groupChoice.setFont(KFontFactory.createPlainFont(15));
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
        modes.setFont(KFontFactory.createPlainFont(15));
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
        questionPanel.add(new KPanel(new KLabel("Write the question(s) below",KFontFactory.createPlainFont(14))),BorderLayout.NORTH);
        questionArea = new KTextArea();
        final KScrollPane scrollPane = questionArea.outerScrollPane(new Dimension(475,150));
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.BLUE,2,true));
        questionPanel.add(scrollPane,BorderLayout.CENTER);

        final KButton cancelButton = new KButton("Cancel");
        cancelButton.addActionListener(e -> this.dispose());
        final KButton addButton = new KButton("Add");
        addButton.addActionListener(AssignmentHandler.additionListener());

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

    public String getProvidedDeadLine() {
        if (Globals.hasNoText(dField.getText()) || Globals.hasNoText(mField.getText()) || Globals.hasNoText(yField.getText())) {
            return "";
        }
        final String sep = MDate.VAL_SEP;
        return dField.getText()+sep+mField.getText()+sep+yField.getText();
    }
}