package core.task;

import core.Board;
import core.module.RunningCourseActivity;
import core.utils.App;
import core.utils.Globals;
import core.utils.MComponent;
import core.utils.MDate;
import proto.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Extends KDialog to provide addition frames for tasks.
 * This class is supported by series of inner classes for each of the addition types.
 */
public class TaskCreator {
    public static final int TASKS_DESCRIPTION_LIMIT = 50;//Note, this should be increased. it's only small because of irregular component arrangement and fixed layouts


    private static Component createSpace(int w, int h){
        return Box.createRigidArea(new Dimension(w, h));
    }


    public static class TodoCreator extends KDialog {
        private KTextField descriptionField;
        private JComboBox<Object> durationBox;
        private KButton createButton;//The button which returns 'true' signaling that user provides all required inputs for a task to be joined

        public TodoCreator(){
            super("Create Task");
            setModalityType(TodoCreator.DEFAULT_MODALITY_TYPE);
            setResizable(true);

            final Dimension platesDimension = new Dimension(475, 35);
            final Dimension fieldsDimension = new Dimension(310, 30);
            final Font labelsFont = KFontFactory.createBoldFont(16);

            descriptionField = KTextField.rangeControlField(TASKS_DESCRIPTION_LIMIT);
            descriptionField.setPreferredSize(fieldsDimension);
            final KPanel namePlate = new KPanel(new BorderLayout(), platesDimension);
            namePlate.add(new KPanel(new KLabel("Task Description:", labelsFont)), BorderLayout.WEST);
            namePlate.add(new KPanel(descriptionField), BorderLayout.CENTER);

            durationBox = new JComboBox<>(new Object[] {"Five Days", "One Week", "Two Weeks", "Three Weeks", "One Month"});
            durationBox.setFont(KFontFactory.createPlainFont(15));
            durationBox.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            final KPanel durationPlate = new KPanel(new BorderLayout(), platesDimension);
            durationPlate.add(new KPanel(new KLabel("To be completed in:", labelsFont)), BorderLayout.WEST);
            durationPlate.add(new KPanel(durationBox), BorderLayout.CENTER);

            final KButton quitButton = new KButton("Cancel");
            quitButton.addActionListener(e -> dispose());
            createButton = new KButton("Create");
            createButton.setFocusable(true);
            createButton.addActionListener(TaskActivity.TodoHandler.additionWaiter());

            rootPane.setDefaultButton(createButton);
            final KPanel contentPlate = new KPanel();
            contentPlate.setLayout(new BoxLayout(contentPlate,BoxLayout.Y_AXIS));
            contentPlate.addAll(namePlate,durationPlate, MComponent.contentBottomGap(),
                    new KPanel(new FlowLayout(FlowLayout.RIGHT), quitButton, createButton));
            setContentPane(contentPlate);
            pack();
            setMinimumSize(getPreferredSize());
            setLocationRelativeTo(Board.getRoot());
        }

        public KTextField getDescriptionField(){
            return descriptionField;
        }

        public String getDuration(){
            return String.valueOf(durationBox.getSelectedItem());
        }
    }


    public static class ProjectCreator extends KDialog{
        private KTextField nameField;
        private JComboBox<Object> durationBox;
        private JComboBox<String> typeBox;
        private KButton createButton;

        public ProjectCreator(){
            super("Create Project");
            setModalityType(TodoCreator.DEFAULT_MODALITY_TYPE);
            setResizable(true);

            final Font labelsFont = KFontFactory.createBoldFont(16);
            final Font boxFont = KFontFactory.createPlainFont(15);
            final Dimension panelsDimension = new Dimension(465,35);

            nameField = KTextField.rangeControlField(TASKS_DESCRIPTION_LIMIT);
            nameField.setPreferredSize(new Dimension(310, 30));
            final KPanel namePanelPlus = new KPanel(new BorderLayout(), panelsDimension);
            namePanelPlus.add(new KPanel(new KLabel("Project Name:", labelsFont)), BorderLayout.WEST);
            namePanelPlus.add(new KPanel(nameField), BorderLayout.CENTER);

            typeBox = new JComboBox<>(new String[]{"Java", "Python", "C/C++", "C#", "Database", "Web", "Other"});
            typeBox.setFont(boxFont);
            typeBox.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            final KPanel typePanelPlus = new KPanel(new BorderLayout(),panelsDimension);
            typePanelPlus.add(new KPanel(new KLabel("Project Type:", labelsFont)), BorderLayout.WEST);
            typePanelPlus.add(new KPanel(typeBox), BorderLayout.CENTER);

            durationBox = new JComboBox<>(new Object[] {"Five Days", "One Week", "Two Weeks", "Three Weeks", "One Month", "Two Months", "Three Months", "Six Months"});
            durationBox.setFont(boxFont);
            durationBox.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            final KPanel durationPanelPlus = new KPanel(new BorderLayout(),panelsDimension);
            durationPanelPlus.add(new KPanel(new KLabel("Specified Duration:",labelsFont)),BorderLayout.WEST);
            durationPanelPlus.add(new KPanel(durationBox),BorderLayout.CENTER);

            final KButton cancelButton = new KButton("Cancel");
            cancelButton.addActionListener(e-> dispose());
            createButton = new KButton("Create");
            createButton.addActionListener(TaskActivity.ProjectsHandler.additionWaiter());
            rootPane.setDefaultButton(createButton);

            final KPanel contentPlate = new KPanel();
            contentPlate.setLayout(new BoxLayout(contentPlate,BoxLayout.Y_AXIS));
            contentPlate.addAll(namePanelPlus,typePanelPlus,durationPanelPlus, createSpace(400, 25),
                    new KPanel(new FlowLayout(FlowLayout.RIGHT), cancelButton, createButton));
            setContentPane(contentPlate);
            pack();
            setMinimumSize(this.getPreferredSize());
            setLocationRelativeTo(Board.getRoot());
        }

        public KTextField getNameField(){
            return nameField;
        }

        public String getTheType(){
            return String.valueOf(typeBox.getSelectedItem());
        }

        public String getTheDuration(){
            return String.valueOf(durationBox.getSelectedItem());
        }
    }


    public static class AssignmentCreator extends KDialog{
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
            final String[] activeNames = RunningCourseActivity.names();
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

            nameField = KTextField.rangeControlField(TASKS_DESCRIPTION_LIMIT);
            nameField.setPreferredSize(new Dimension(300,30));
            final KPanel namePanel = new KPanel(new BorderLayout());
            namePanel.add(new KPanel(new KLabel("Course Name:",labelsFont)),BorderLayout.WEST);
            namePanel.add(new KPanel(nameField),BorderLayout.CENTER);

            dField = KTextField.dayField();
            mField = KTextField.monthField();
            yField = KTextField.yearField();

            final KPanel deadLinePanel = new KPanel(new FlowLayout(FlowLayout.CENTER));
            deadLinePanel.addAll(new KLabel("Deadline:",labelsFont), createSpace(50,30),new KLabel("Day",
                            hintsFont),dField, createSpace(20,30),
                    new KLabel("Month",hintsFont),mField, createSpace(20,30),new KLabel("Year",hintsFont),yField);

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
            addButton.addActionListener(TaskActivity.AssignmentsHandler.additionListener());

            final KPanel contentPlate = new KPanel();
            contentPlate.setLayout(new BoxLayout(contentPlate,BoxLayout.Y_AXIS));
            contentPlate.addAll(importPanel, namePanel,deadLinePanel,groupPanel,modePanel,groupPanel, createSpace(500,25),questionPanel,
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

    public static class EventCreator extends KDialog{
        public static final int TEST = 0;
        public static final int EXAM = 1;
        public static final int OTHER = 2;
        private String eventType;
        private KTextField descriptionField;
        private KTextField dayField, monthField, yearField;

        public EventCreator(int eType){
            setModalityType(TodoCreator.DEFAULT_MODALITY_TYPE);
            setResizable(true);
            final KPanel contentPanel = new KPanel();
            contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

            final KLabel typeLabel = new KLabel();
            typeLabel.setFont(KFontFactory.createBoldFont(16));
            final KLabel dateLabel = new KLabel();
            dateLabel.setFont(KFontFactory.createBoldFont(16));

            final KPanel importPanel = new KPanel();
            final String[] activeNames = RunningCourseActivity.names();
            if (activeNames.length >= 1) {
                final KComboBox<String> importBox = new KComboBox<>(activeNames);
                importBox.setFont(KFontFactory.createPlainFont(15));
                importBox.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                importBox.setFocusable(false);
                importBox.setToolTipText("Import");
                importBox.addActionListener(e -> {
                    descriptionField.setText(String.valueOf(importBox.getSelectedItem()));
                    dayField.requestFocusInWindow();
                });
                importPanel.add(importBox);
            }

            if (eType == TEST) {
                setTitle("Upcoming Test");
                typeLabel.setText("Course name:");
                dateLabel.setText("Test Date:");
                eventType = "Test";
                contentPanel.add(importPanel);
            } else if (eType == EXAM) {
                setTitle("Upcoming Exam");
                typeLabel.setText("Course name:");
                dateLabel.setText("Exam Date:");
                eventType = "Exam";
                contentPanel.add(importPanel);
            } else if (eType == OTHER) {
                setTitle("Upcoming Event");
                typeLabel.setText("Event title:");
                dateLabel.setText("Date:");
                eventType = "Event";
            }

            final Font labelsFont = KFontFactory.createPlainFont(16);
            final Dimension typicalPanelsDimension = new Dimension(465,35);

            descriptionField = KTextField.rangeControlField(TASKS_DESCRIPTION_LIMIT);
            descriptionField.setPreferredSize(new Dimension(310,30));
            final KPanel titleLayer = new KPanel(new BorderLayout(),typicalPanelsDimension);
            titleLayer.add(new KPanel(typeLabel), BorderLayout.WEST);
            titleLayer.add(new KPanel(descriptionField), BorderLayout.CENTER);

            dayField = KTextField.dayField();
            monthField = KTextField.monthField();
            yearField = KTextField.yearField();

            final KPanel dateFieldsPanel = new KPanel();
            dateFieldsPanel.addAll(new KLabel("Day",labelsFont),dayField, createSpace(10, 20),
                    new KLabel("Month",labelsFont),monthField, createSpace(10, 20),new KLabel("Year",labelsFont),yearField);
            final KPanel datesLayer = new KPanel(new BorderLayout(),typicalPanelsDimension);
            datesLayer.add(new KPanel(dateLabel),BorderLayout.WEST);
            datesLayer.add(dateFieldsPanel,BorderLayout.CENTER);

            final KButton cancelButton = new KButton("Cancel");
            cancelButton.addActionListener(e -> this.dispose());

            final KButton addButton = new KButton("Add");
            addButton.addActionListener(TaskActivity.EventsHandler.newListener());//No fear - if value was not one of the specified 3, compiler won't reach this line

            rootPane.setDefaultButton(addButton);
            contentPanel.addAll(titleLayer, datesLayer, MComponent.contentBottomGap(),
                    new KPanel(new FlowLayout(FlowLayout.RIGHT), cancelButton, addButton));
            setContentPane(contentPanel);
            pack();
            setMinimumSize(getPreferredSize());
            setLocationRelativeTo(Board.getRoot());
        }

        public KTextField getDescriptionField(){
            return descriptionField;
        }

        public String getProvidedDate() {
            if(Globals.hasNoText(dayField.getText()) || Globals.hasNoText(monthField.getText()) || Globals.hasNoText(yearField.getText())) {
                return "";
            }
            final String sep = MDate.VAL_SEP;
            return dayField.getText()+sep+monthField.getText()+sep+yearField.getText();
        }

        public String type(){
            return eventType;
        }
    }

}
