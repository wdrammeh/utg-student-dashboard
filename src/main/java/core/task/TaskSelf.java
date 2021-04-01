package core.task;

import core.Board;
import core.alert.Notification;
import core.user.Student;
import core.utils.*;
import proto.*;

import javax.swing.Timer;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.util.*;

/**
 * It specifies the task itself - provides description, dates, etc.
 * A TaskSelf must support serialization, but not necessarily by itself.
 */
public class TaskSelf {


    public static class TodoSelf {
        private String description;
        private String startDate;
        private int specifiedDuration;//In days !<5, !>30
        private int totalTimeConsumed;//This has only 2 variations: 1. An ongoing task is marked done 2. A task completes normally
        private boolean isActive;
        private String dateExpectedToComplete;//Shown for running tasks only - set with the constructor
        private String dateCompleted;//Shown for completed #s only - set with ending, whether voluntary or time-due
        private Timer timer;
        private boolean eveIsAlerted, doneIsAlerted;
        private transient KLabel togoLabel;
        private transient TaskExhibition.TodoExhibition exhibition;
        private transient KPanel layerPanel;

        public TodoSelf(String desc, int duration){
            this(desc, duration, MDate.now(), true);
            initializeTimer(Globals.DAY);
            setUpUI();
        }

        private TodoSelf(String desc, int duration, String start, boolean isActive){
            description = desc;
            specifiedDuration = duration;
            startDate = start;
            dateExpectedToComplete = MDate.daysAfter(MDate.parse(startDate), duration);
            this.isActive = isActive;
        }

        private void initializeTimer(int firstDelay){
            timer = new Timer(Globals.DAY,null);
            timer.setInitialDelay(firstDelay);
            timer.addActionListener(e -> {
                togoLabel.setText(Globals.checkPlurality(getDaysLeft(), "days")+" to go");
                if (getDaysLeft() == 1) {//Fire eve-day notification if was not fired already
                    togoLabel.setText("Less than a day to go");
                    signalEveNotice();
                } else if (getDaysLeft() <= 0) {
                    if (exhibition != null && exhibition.isShowing()) {
                        exhibition.dispose();
                    }
                    TaskActivity.TodoHandler.transferTask(this, null, true);
                    signalDoneNotice();
                }
            });
            timer.start();
        }

        private void setUpUI(){
            final KPanel namePanel = new KPanel(new BorderLayout());
            namePanel.add(new KLabel(description, KFontFactory.createPlainFont(17), Color.BLUE), BorderLayout.SOUTH);

            final KButton moreOptions = KButton.createIconifiedButton("options.png", 20, 20);
            moreOptions.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            moreOptions.setToolTipText("About this Task");
            moreOptions.addActionListener(e-> exhibition = new TaskExhibition.TodoExhibition(this));

            togoLabel = new KLabel(Globals.checkPlurality(getDaysLeft(), "days") + " to complete",
                    KFontFactory.createPlainFont(16));
            togoLabel.setOpaque(false);
            if (isActive) {
                togoLabel.setForeground(Color.RED);
            } else {
                togoLabel.setText("Completed "+dateCompleted);
                togoLabel.setForeground(Color.BLUE);
            }

            final KPanel quantaPanel = new KPanel(new FlowLayout(FlowLayout.RIGHT));
            quantaPanel.addAll(new KLabel(specifiedDuration+" days task", KFontFactory.createPlainFont(16)),
                    Box.createRigidArea(new Dimension(10, 10)), togoLabel,
                    Box.createRigidArea(new Dimension(15, 10)), moreOptions);

            if (layerPanel == null) {
                layerPanel = new KPanel(1_000, 35);
            } else {
                MComponent.empty(layerPanel);
            }
            layerPanel.setLayout(new BoxLayout(layerPanel, BoxLayout.X_AXIS));
            layerPanel.addAll(namePanel, quantaPanel);
            MComponent.ready(layerPanel);
        }

        private void signalEveNotice(){
            if (!eveIsAlerted) {
                final String text = "Dear "+ Student.getLastName()+", the Task you created on "+getStartDate()+", "+
                        getDescription()+", is to be completed in less than a day.";
                Notification.create("Task Reminder","Task "+getDescription()+" is due tomorrow", text);
                eveIsAlerted = true;
            }
        }

        private void signalDoneNotice(){
            if (!doneIsAlerted) {
                final String text = "Dear "+Student.getLastName()+", the Task you created on "+getStartDate()+", "+
                        getDescription()+", is now due. This Task is now considered done as per the given date limit.";
                Notification.create("Task Completed","Task "+getDescription()+" is now completed", text);
                doneIsAlerted = true;
            }
        }

        public KLabel getTogoLabel(){
            return togoLabel;
        }

        public String getDescription() {
            return description;
        }

        public String getStartDate() {
            return startDate;
        }

        public int getDaysTaken() {
            return (int) MDate.actualDayDifference(MDate.parse(startDate), new Date());
        }

        public int getTotalTimeConsumed(){
            return totalTimeConsumed;
        }

        public void setTotalTimeConsumed(int totalTimeConsumed){
            this.totalTimeConsumed = totalTimeConsumed;
        }

        public int getSpecifiedDuration() {
            return specifiedDuration;
        }

        public int getDaysLeft() {
            return specifiedDuration - getDaysTaken();
        }

        public boolean isActive() {
            return isActive;
        }

        public void setActive(boolean active) {
            isActive = active;
            if (!active) {
                timer.stop();
            }
        }

        public String getDateCompleted() {
            return dateCompleted;
        }

        public void setDateCompleted(String dateCompleted) {
            this.dateCompleted = dateCompleted;
        }

        public String getDateExpectedToComplete() {
            return dateExpectedToComplete;
        }

        public KPanel getLayer(){
            return layerPanel;
        }

        private void wakeAlive(){
            setUpUI();
            if (getDaysLeft() == 1) {
                togoLabel.setText("Less than a day to complete");
                signalEveNotice();
            }
            int residue = MDate.getTimeValue(MDate.parse(dateExpectedToComplete)) - MDate.getTimeValue(new Date());
            if (residue < 0) { // reverse it then...
                residue = Globals.DAY - Math.abs(residue);
            }
            initializeTimer(residue);
        }

        private void wakeDead(){
            dateCompleted = dateExpectedToComplete;
            isActive = false;
            signalDoneNotice();
        }

        private String export(){
            return Globals.joinLines(description,
                    startDate,
                    specifiedDuration,
                    totalTimeConsumed,
                    isActive,
                    dateCompleted,
                    eveIsAlerted,
                    doneIsAlerted);
        }
    }


    public static class ProjectSelf {
        private String projectName;
        private String type;
        private String startDate;
        private int specifiedDuration;
        private int totalTimeConsumed;
        private boolean isLive;
        private Timer timer;
        private String dateExpectedToComplete;
        private String dateCompleted;
        private boolean eveIsAlerted, completionIsAlerted;
        private transient TaskExhibition.ProjectExhibition exhibition;
        private transient KButton terminationButton, completionButton, moreOptions;
        private transient JProgressBar projectProgression;
        private transient KLabel progressLabelPercentage;
        private transient KPanel projectLayer;

        public ProjectSelf(String name, String type, int duration){
            this(name, type, MDate.now(), duration, true);
            initializeTimer(Globals.DAY);
            initializeUI();
        }

        private ProjectSelf(String name, String type, String startTime, int duration, boolean live){
            projectName = name;
            this.type = type;
            startDate = startTime;
            specifiedDuration = duration;
            dateExpectedToComplete = MDate.daysAfter(new Date(), duration);
            isLive = live;
        }

        private void initializeTimer(int firstDelay){
            timer = new Timer(Globals.DAY,null);
            timer.setInitialDelay(firstDelay);
            timer.addActionListener(e -> {
                projectProgression.setValue(this.getDaysTaken());
                if (getDaysLeft() == 1) {
                    signalEveNotice();
                } else if (getDaysLeft() <= 0) {
                    if (exhibition != null && exhibition.isShowing()) {
                        exhibition.dispose();
                    }
                    TaskActivity.ProjectsHandler.performIComplete(this,true);
                    signalCompletionNotice();
                }
            });
            timer.start();
        }

        private void initializeUI(){
            final KPanel namePanel = new KPanel(new BorderLayout());
            namePanel.add(new KLabel(projectName, KFontFactory.createPlainFont(17), Color.BLUE),
                    BorderLayout.CENTER);

            final Dimension optionsDim = new Dimension(30, 30);//the small-buttons actually

            progressLabelPercentage = new KLabel("", KFontFactory.createPlainFont(18), Color.BLUE);
            progressLabelPercentage.setOpaque(false);

            projectProgression = new JProgressBar(0, specifiedDuration){
                @Override
                public void setValue(int n) {
                    super.setValue(n);
                    progressLabelPercentage.setText(projectProgression.getString());
                }
            };
            projectProgression.setValue(getDaysTaken());
            projectProgression.setPreferredSize(new Dimension(150, 20));
            projectProgression.setForeground(Color.BLUE);

            terminationButton = KButton.createIconifiedButton("terminate.png", 20, 20);
            terminationButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            terminationButton.setPreferredSize(optionsDim);
            terminationButton.setToolTipText("Remove this Project");
            terminationButton.addActionListener(TaskActivity.ProjectsHandler.removalListener(this));

            completionButton = KButton.createIconifiedButton("mark.png", 20, 20);
            completionButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            completionButton.setPreferredSize(optionsDim);
            completionButton.setToolTipText("Mark as Complete");
            completionButton.addActionListener(e-> TaskActivity.ProjectsHandler.performIComplete(this, false));

            moreOptions = KButton.createIconifiedButton("options.png", 20, 20);
            moreOptions.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            moreOptions.setToolTipText("About this Project");
            moreOptions.addActionListener(e -> exhibition = new TaskExhibition.ProjectExhibition(this));

            final KPanel quanterLayer = new KPanel(new FlowLayout(FlowLayout.RIGHT));
            quanterLayer.addAll(new KLabel(getType()+" Project", KFontFactory.createPlainFont(16)),
                    Box.createHorizontalStrut(15), projectProgression, progressLabelPercentage, terminationButton,
                    completionButton, Box.createHorizontalStrut(15), moreOptions);

            projectLayer = new KPanel(1_000, 35);
            projectLayer.setLayout(new BoxLayout(projectLayer, BoxLayout.X_AXIS));
            projectLayer.addAll(namePanel, quanterLayer);
        }

        public void setUpDoneUI(){
            final KPanel namePanel = new KPanel(new BorderLayout());
            namePanel.add(new KLabel(this.projectName, KFontFactory.createBoldFont(16), Color.BLUE),
                    BorderLayout.CENTER);

            projectProgression = new JProgressBar(0, specifiedDuration);
            projectProgression.setValue(specifiedDuration);
            projectProgression.setPreferredSize(new Dimension(150, 20));
            projectProgression.setForeground(Color.BLUE);

            progressLabelPercentage = new KLabel(projectProgression.getString(),
                    KFontFactory.createPlainFont(18), Color.BLUE);
            progressLabelPercentage.setOpaque(false);

            terminationButton = KButton.createIconifiedButton("trash.png", 20, 20);
            terminationButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            terminationButton.setPreferredSize(new Dimension(30, 30));
            terminationButton.setToolTipText("Remove this Project");
            terminationButton.addActionListener(TaskActivity.ProjectsHandler.removalListener(this));

            moreOptions = KButton.createIconifiedButton("options.png", 20, 20);
            moreOptions.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            moreOptions.setToolTipText("About this Project");
            moreOptions.addActionListener(e -> exhibition = new TaskExhibition.ProjectExhibition(this));

            final KPanel quantaLayer = new KPanel(new FlowLayout(FlowLayout.RIGHT));
            quantaLayer.addAll(new KLabel(getType()+" Project", KFontFactory.createPlainFont(16)),
                    Box.createHorizontalStrut(15), projectProgression, progressLabelPercentage, terminationButton,
                    Box.createRigidArea(new Dimension(10, 10)), moreOptions);

            if (projectLayer == null) {
                projectLayer = new KPanel(1_000, 35);
                projectLayer.setLayout(new BoxLayout(projectLayer, BoxLayout.X_AXIS));
            } else {
                MComponent.empty(projectLayer);
            }
            projectLayer.addAll(namePanel, quantaLayer);
            MComponent.ready(projectLayer);
        }

        private void signalEveNotice(){
            if (!eveIsAlerted) {
                final String text = "Dear "+Student.getLastName()+", the "+getSpecifiedDuration()+" days "+
                        getType()+" Project you created, "+getProjectName()+", since "+getStartDate()+" is to be completed by tomorrow.";
                Notification.create("Project Reminder","Specified duration for the "+getType()+" Project "+
                        getProjectName()+" is running out",text);
                eveIsAlerted = true;
            }
        }

        private void signalCompletionNotice(){
            if (!completionIsAlerted) {
                final String text = "Dear "+Student.getLastName()+", the specified period of the "+
                        getType()+" Project you created, "+getProjectName()+", since "+getStartDate()+" is now attained.";
                Notification.create("Project Completed","Specified duration for the "+getType()+" Project "+
                        getProjectName()+" is reached",text);
                completionIsAlerted = true;
            }
        }

        public String getProjectName() {
            return projectName;
        }

        public String getType() {
            return type;
        }

        public String getStartDate() {
            return startDate;
        }

        public int getSpecifiedDuration() {
            return specifiedDuration;
        }

        public boolean isLive() {
            return isLive;
        }

        public void setLive(boolean live) {
            isLive = live;
            if (!live) {
                timer.stop();
            }
        }

        public String getDateCompleted() {
            return dateCompleted;
        }

        public void setDateCompleted(String dateCompleted) {
            this.dateCompleted = dateCompleted;
        }

        public int getDaysLeft() {
            return specifiedDuration - getDaysTaken();
        }

        public int getDaysTaken() {
            return (int) MDate.actualDayDifference(Objects.requireNonNull(MDate.parse(startDate)), new Date());
        }

        public int getTotalTimeConsumed() {
            return totalTimeConsumed;
        }

        public void setTotalTimeConsumed(int totalTimeConsumed) {
            this.totalTimeConsumed = totalTimeConsumed;
        }

        public String getDateExpectedToComplete() {
            return dateExpectedToComplete;
        }

        public KPanel getLayer(){
            return projectLayer;
        }

        private void wakeLive(){
            if (getDaysLeft() == 1) {
                signalEveNotice();
            }
            int residue = MDate.getTimeValue(MDate.parse(dateExpectedToComplete)) - MDate.getTimeValue(new Date());
            if (residue < 0) {
                residue = Globals.DAY - Math.abs(residue);
            }
            initializeTimer(residue);
        }

        private void wakeDead(){
            dateCompleted = dateExpectedToComplete;
            isLive = false;
            signalCompletionNotice();
        }

        public String export() {
            return Globals.joinLines(projectName,
                    type,
                    startDate,
                    specifiedDuration,
                    totalTimeConsumed,
                    isLive,
                    dateCompleted,
                    eveIsAlerted,
                    completionIsAlerted);
        }
    }


    /**
     * Please note that the assignments-task type does not consider the time differences
     * among date values as other types do.
     * In fact, it assumes all time values to be the beginning of the day.
     * Computation with this is easier.
     */
    public static class AssignmentSelf {
        private String courseName;
        private String question;
        private boolean isGroup;
        private boolean isOn;
        private String modeOfSubmission;
        private String startDate;
        private String deadLine;
        private String dateSubmitted;
        private Timer timer; // the only purpose of its timer is to compare the dates after every day... comparison returns 0 implies deadline is met
        private int memberCount;
        private ArrayList<String> members = new ArrayList<>();
        private boolean eveIsAlerted, submissionIsAlerted;
        private transient KLabel deadlineIndicator;
        private transient KLabel groupLabel;
        private transient DeadLineEditor deadlineEditor;
        private transient TaskExhibition.AssignmentExhibition assignmentExhibitor;
        private transient KPanel assignmentPanel;

        // where dueDate shall have no time
        public AssignmentSelf(String subject, String dueDate, String question, boolean groupWork,
                              String submissionMode){
            this(subject, dueDate, question, groupWork, submissionMode, MDate.formatDateOnly(new Date()),
                    true);
            setUpUI();
            initializeTimer(Globals.DAY);
        }

        private AssignmentSelf(String subject, String dueDate, String question, boolean isGroup,
                                       String submissionMode, String startTime, boolean on){
            startDate = startTime;
            courseName = subject;
            deadLine = dueDate;
            this.question = question;
            this.isGroup = isGroup;
            modeOfSubmission = submissionMode;
            isOn = on;
        }

        private void initializeTimer(int firstDelay){
            timer = new Timer(Globals.DAY,null);
            timer.setInitialDelay(firstDelay);
            timer.addActionListener(e -> {
                if (getTimeRemaining() == 1) {
                    signalEveNotice();
                } else if (getTimeRemaining() <= 0) {
                    if (assignmentExhibitor != null && assignmentExhibitor.isShowing()) {
                        assignmentExhibitor.dispose();
                    }
                    if (deadlineEditor != null && deadlineEditor.isShowing()) {
                        deadlineEditor.dispose();
                    }
                    TaskActivity.AssignmentsHandler.transferAssignment(this, null, true);
                    signalSubmissionNotice();
                }
            });
            timer.start();
        }

        private void setUpUI(){
        	final KPanel namePanel = new KPanel(new BorderLayout());
        	namePanel.add(new KLabel(this.getCourseName(), KFontFactory.createPlainFont(17),
                    Color.BLUE), BorderLayout.SOUTH);

        	deadlineIndicator = new KLabel();
            if (isOn) {
                deadlineIndicator.setText("Deadline: "+deadLine);
                deadlineIndicator.setStyle(KFontFactory.createItalicFont(17), Color.RED);
                deadlineIndicator.underline(false);
                deadlineIndicator.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                deadlineIndicator.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        deadlineEditor = new DeadLineEditor(AssignmentSelf.this);
                    }
                });
            } else {
                deadlineIndicator.setText("Submitted: "+dateSubmitted);
                deadlineIndicator.setStyle(KFontFactory.createPlainFont(16), Color.BLUE);
                deadlineIndicator.setCursor(null);
                for (MouseListener l : deadlineIndicator.getMouseListeners()) {
                    deadlineIndicator.removeMouseListener(l);
                }
            }
            deadlineIndicator.setOpaque(false);

            if (isGroup()) {
                groupLabel = KLabel.createIcon("group.png",20,20);
                groupLabel.setText(memberCount <= 1 ? "1 Member" : memberCount+" Members");
                groupLabel.setFont(KFontFactory.createPlainFont(17));
                groupLabel.setToolTipText("View Participants");
                groupLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                groupLabel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        groupLabel.setForeground(Color.BLUE);
                    }

                    @Override
                    public void mouseClicked(MouseEvent e) {
                        new MemberExhibitor(AssignmentSelf.this).setVisible(true);
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        groupLabel.setForeground(null);
                    }
                });
            } else {
                groupLabel = KLabel.createIcon("personal.png", 20, 20);
                groupLabel.setText("Personal");
            }
            groupLabel.setFont(KFontFactory.createPlainFont(16));

            final KButton showButton = KButton.createIconifiedButton("options.png", 20, 20);
            showButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            showButton.setToolTipText("About this Assignment");
            showButton.addActionListener(e -> assignmentExhibitor = new TaskExhibition.AssignmentExhibition(AssignmentSelf.this));

        	final KPanel quantaPanel = new KPanel(new FlowLayout(FlowLayout.RIGHT));
        	quantaPanel.addAll(deadlineIndicator, Box.createRigidArea(new Dimension(10, 10)), groupLabel,
                    Box.createRigidArea(new Dimension(10, 15)), showButton);

        	assignmentPanel = new KPanel(1_000, 35);
        	assignmentPanel.setLayout(new BoxLayout(assignmentPanel, BoxLayout.X_AXIS));
        	assignmentPanel.addAll(namePanel, quantaPanel);
        }

        private void signalEveNotice(){
            if (!eveIsAlerted) {
                final String text = "Dear, "+Student.getLastName()+", the "+courseName+
                        (isGroup ? " Group Assignment" : " Assignment")+" is to be submitted in 24 hours. Submission Mode is "+modeOfSubmission+". " +
                        "If you have already submitted this assignment, mark it as 'submitted' to prevent further-notifications.";
                Notification.create("Assignment Reminder",courseName+" Assignment is due tomorrow!", text);
                eveIsAlerted = true;
            }
        }

        private void signalSubmissionNotice(){
            if (!submissionIsAlerted) {
                final String text = "Dear, "+Student.getLastName()+", the submission date of the "+
                        courseName+(isGroup ? " Group Assignment" : " Assignment")+" is past. Submission Mode was "+modeOfSubmission+".";
                Notification.create("Assignment Completed",courseName+" Assignment has reached submission date.", text);
                submissionIsAlerted = true;
            }
        }

        public KLabel getDeadlineIndicator(){
            return deadlineIndicator;
        }

        public boolean isGroup() {
            return isGroup;
        }

        public boolean isOn() {
            return isOn;
        }

        public void setOn(boolean on) {
            isOn = on;
            if (!on) {
                timer.stop();
            }
        }

        public String getCourseName() {
            return courseName;
        }

        public String getQuestion() {
            return question;
        }

        public void setQuestion(String question) {
            this.question = question;
        }

        public String getDeadLine() {
            return deadLine;
        }

        public void setDeadLine(String deadLine) {
            this.deadLine = deadLine;
            deadlineIndicator.setText("Deadline: "+deadLine);
        }

        public String getStartDate() {
        	return startDate;
        }

        public String getSubmissionDate() {
            return dateSubmitted;
        }

        public void setSubmissionDate(String submissionDate) {
            this.dateSubmitted = submissionDate;
        }

        public String getModeOfSubmission() {
            return modeOfSubmission;
        }

        public void setModeOfSubmission(String modeOfSubmission) {
            this.modeOfSubmission = modeOfSubmission;
        }

        public int getTimeRemaining(){
            return (int) MDate.actualDayDifference(MDate.parse(MDate.formatDateOnly(new Date()) + " 0:0:0"),
                    MDate.parse(deadLine + " 0:0:0"));
        }

        private void effectMembersCount(int effectValue){
            memberCount += effectValue;
            groupLabel.setText(Globals.checkPlurality(memberCount, "Members"));
        }

        public KPanel getLayer(){
            return assignmentPanel;
        }

        private void wakeAlive(){
            if (getTimeRemaining() == 1) {
                signalEveNotice();
            }
            final int residue = Globals.DAY - MDate.getTimeValue(new Date());
            initializeTimer(residue);
        }

        private void wakeDead(){
            isOn = false;
            setSubmissionDate(deadLine);
            signalSubmissionNotice();
        }

        public String export() {
            return Globals.joinLines(courseName,
                    question,
                    isGroup,
                    isOn,
                    modeOfSubmission,
                    startDate,
                    deadLine,
                    dateSubmitted,
                    memberCount,
                    eveIsAlerted,
                    submissionIsAlerted);
        }

        private class MemberExhibitor extends KDialog {
            int pX, pY;
            private KPanel membersPanel;
            private KButton memberAdder;

            private MemberExhibitor(AssignmentSelf assignmentSelf){
                setUndecorated(true);
                setSize(500, 500);
                setModalityType(KDialog.DEFAULT_MODALITY_TYPE);
                final KPanel upperBar = new KPanel(new FlowLayout(FlowLayout.CENTER));
                upperBar.add(new KLabel(assignmentSelf.getCourseName()+" Assignment",
                        KFontFactory.createPlainFont(15), Color.BLUE));
                upperBar.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        super.mousePressed(e);
                        pX = e.getX();
                        pY = e.getY();
                        upperBar.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                    }

                    @Override
                    public void mouseDragged(MouseEvent e) {
                        super.mouseDragged(e);
                        MemberExhibitor.this.setLocation(MemberExhibitor.this.getLocation().x + e.getX() - pX,
                                MemberExhibitor.this.getLocation().y + e.getY() - pY);
                    }

                    @Override
                    public void mouseReleased(MouseEvent e) {
                        super.mouseReleased(e);
                        upperBar.setCursor(null);
                    }
                });
                upperBar.addMouseMotionListener(new MouseMotionAdapter() {
                    @Override
                    public void mouseDragged(MouseEvent e) {
                        super.mouseDragged(e);
                        MemberExhibitor.this.setLocation(MemberExhibitor.this.getLocation().x + e.getX() - pX,
                                MemberExhibitor.this.getLocation().y + e.getY() - pY);
                    }
                });

                membersPanel = new KPanel(){
                    @Override
                    public Component add(Component comp) {
                        assignmentSelf.effectMembersCount(1);
                        membersPanel.setPreferredSize(new Dimension(membersPanel.getPreferredSize().width,
                                membersPanel.getPreferredSize().height+35));
                        return super.add(comp);
                    }

                    @Override
                    public void remove(Component comp) {
                        super.remove(comp);
                        membersPanel.setPreferredSize(new Dimension(membersPanel.getPreferredSize().width,
                                membersPanel.getPreferredSize().height-35));
                        assignmentSelf.effectMembersCount(-1);
                    }
                };
                membersPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
                membersPanel.setBackground(Color.WHITE);
                final KScrollPane midScroll = new KScrollPane(membersPanel);

                final KButton closeButton = new KButton("Close");
                closeButton.addActionListener(e-> dispose());

                memberAdder = new KButton("Add Member");
                memberAdder.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                memberAdder.addActionListener(e -> {
                    final String newMemberName = App.requestInput(this.getRootPane(),
                            "New Member","Enter member's name below:\n \n");
                    if (newMemberName != null && !Globals.hasNoText(newMemberName)) {
                        if (newMemberName.length() > 30) {
                            App.reportError("Error", "Sorry, a member's name cannot exceed 30 characters.");
                        } else {
                            appendNewMember(newMemberName, false);
                            assignmentSelf.members.add(newMemberName);
                        }
                    }
                });
                memberAdder.setEnabled(assignmentSelf.isOn);

                final KPanel buttonsPanel = new KPanel(new BorderLayout());
                buttonsPanel.add(new KPanel(memberAdder), BorderLayout.WEST);
                buttonsPanel.add(new KPanel(closeButton), BorderLayout.EAST);

                rootPane.setDefaultButton(closeButton);
                final KPanel contentPanel = new KPanel(new BorderLayout());
                contentPanel.setBorder(BorderFactory.createLineBorder(null, 1, false));
                contentPanel.add(upperBar, BorderLayout.NORTH);
                contentPanel.add(midScroll, BorderLayout.CENTER);
                contentPanel.add(buttonsPanel, BorderLayout.SOUTH);
                setContentPane(contentPanel);
                setLocationRelativeTo(Board.getRoot());

                effectMembersCount(-assignmentSelf.memberCount);//This is useful for those triggered from serialization
                if (assignmentSelf.members.isEmpty()) {
                    appendNewMember(Student.getFullNamePostOrder()+" (me)", true);
                    assignmentSelf.members.add(Student.getFullNamePostOrder());
                } else {
                    appendNewMember(Student.getFullNamePostOrder()+" (me)", true);
                    for (int i = 1; i < assignmentSelf.members.size(); i++) {
                        appendNewMember(assignmentSelf.members.get(i), false);
                    }
                }
            }

            private void appendNewMember(String name, boolean myself){
                final KLabel nameLabel = new KLabel(name,KFontFactory.createPlainFont(18));

                final KButton removeButton = KButton.createIconifiedButton("terminate.png", 17, 17);

                final KPanel namePanel = new KPanel(new BorderLayout(),new Dimension(480,30));
                namePanel.add(new KPanel(nameLabel),BorderLayout.WEST);
                namePanel.add(removeButton, BorderLayout.EAST);
                namePanel.setBackground(Color.WHITE);//except head and toe, the dialog is to be white
                namePanel.getComponent(0).setBackground(Color.WHITE);

                membersPanel.add(namePanel);
                MComponent.ready(membersPanel);

                removeButton.setToolTipText("Remove "+name.split(" ")[0]);
                removeButton.addActionListener(e-> {
                    if (App.showYesNoCancelDialog(rootPane, "Confirm",
                            "Are you sure you want to remove "+name+" as a participant for this assignment?")) {
                        membersPanel.remove(namePanel);
                        MComponent.ready(membersPanel);
                        AssignmentSelf.this.members.remove(name);
                    }
                });
                removeButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                removeButton.setEnabled(!myself && AssignmentSelf.this.isOn);
            }
        }

        private static class DeadLineEditor extends KDialog {

            private DeadLineEditor(AssignmentSelf assignmentSelf){
                super("Edit Deadline");
                setResizable(true);
                setModalityType(KDialog.DEFAULT_MODALITY_TYPE);

                final Font valsFont = KFontFactory.createPlainFont(16);
                final Date assignmentDeadline = MDate.parse(assignmentSelf.deadLine+" 0:0:0");

                final KTextField dField = KTextField.dayField();
                dField.setText(MDate.getPropertyFrom(assignmentDeadline, Calendar.DATE));
                final KTextField mField = KTextField.monthField();
                mField.setText(MDate.getPropertyFrom(assignmentDeadline, Calendar.MONTH));
                final KTextField yField = KTextField.yearField();
                yField.setText(MDate.getPropertyFrom(assignmentDeadline, Calendar.YEAR));
                final KPanel datesPanel = new KPanel(new FlowLayout(FlowLayout.CENTER));
                datesPanel.addAll(new KLabel("D", valsFont), dField,
                        Box.createRigidArea(new Dimension(20, 30)), new KLabel("M", valsFont),
                        mField, Box.createRigidArea(new Dimension(20, 30)),
                        new KLabel("Y", valsFont), yField);
                final KPanel deadLinePanel = new KPanel(new BorderLayout(), new Dimension(465, 35));
                deadLinePanel.add(new KPanel(new KLabel("New Deadline", KFontFactory.createBoldFont(15))),
                        BorderLayout.WEST);
                deadLinePanel.add(datesPanel,BorderLayout.EAST);

                final KButton setButton = new KButton("Set");
                setButton.addActionListener(e1 -> {
                    if (Globals.hasNoText(dField.getText())) {
                        App.reportError(rootPane,"Error", "Please specify the day");
                        dField.requestFocusInWindow();
                    } else if (Globals.hasNoText(mField.getText())) {
                        App.reportError(rootPane,"Error", "Please specify the month");
                        mField.requestFocusInWindow();
                    } else if (Globals.hasNoText(yField.getText())) {
                        App.reportError(rootPane,"Error", "Please specify the year");
                        yField.requestFocusInWindow();
                    } else {
                        final Date newDeadline = MDate.parse(
                                dField.getText()+"/"+mField.getText()+"/"+yField.getText()+" 0:0:0");
                        if (newDeadline == null) {
                            return;
                        }
                        if (newDeadline.before(new Date())) {
                            App.reportError(rootPane, "Invalid Deadline",
                                    "That deadline is already past. Enter another dealine.");
                        } else {
                            assignmentSelf.setDeadLine(MDate.formatDateOnly(newDeadline));
                            dispose();
                        }
                    }
                });
                this.getRootPane().setDefaultButton(setButton);
                final KButton cancelButton = new KButton("Cancel");
                cancelButton.addActionListener(e2-> dispose());
                final KPanel bottomPlate = new KPanel(new FlowLayout(FlowLayout.RIGHT));
                bottomPlate.addAll(cancelButton,setButton);

                final KPanel allPlate = new KPanel();
                allPlate.setLayout(new BoxLayout(allPlate, BoxLayout.Y_AXIS));
                allPlate.addAll(deadLinePanel,Box.createVerticalStrut(25),bottomPlate);
                setContentPane(allPlate);
                pack();
                setMinimumSize(getPreferredSize());
                setLocationRelativeTo(Board.getRoot());
                SwingUtilities.invokeLater(()-> this.setVisible(true));
            }
        }
    }


    /**
     * This type deal with dates similar to AssignmentSelf
     */
    public static class EventSelf {
        private String title;
        private String dateDue;
        private Timer timer;
        private boolean isPending;
        private boolean eveIsAlerted, timeupIsAlerted;
        private transient KLabel stateIndicator;
        private transient KButton canceller;
        private transient KPanel eventLayer;

        // where deadline is in date only, and no time, as in the AssignmentSelf type above
        public EventSelf(String name, String deadline){
            this(name, deadline, true);
            initializeTimer(Globals.DAY);
            setUpUI();
        }

        private EventSelf(String name, String deadline, boolean status){
            title = name;
            dateDue = deadline;
            isPending = status;
        }

        private void initializeTimer(int iDelay){
            timer = new Timer(Globals.DAY,null);
            timer.setInitialDelay(iDelay);
            timer.addActionListener(e-> {
                final Calendar eveCalendar = Calendar.getInstance();
                eveCalendar.setTime(MDate.parse(this.dateDue+" 0:0:0"));
                eveCalendar.add(Calendar.DATE, -1);
                if (MDate.isSameDay(eveCalendar.getTime(), new Date())) {
                    signalEveNotice();
                } else if(MDate.isSameDay(MDate.parse(dateDue+" 0:0:0"), new Date())) {
                    endState();
                    setUpUI();
                    MComponent.ready(eventLayer);
                    TaskActivity.EventsHandler.renewCount(-1);
                }
            });
            timer.start();
        }

        private void setUpUI(){
            if (isPending) {
                canceller = KButton.createIconifiedButton("terminate.png", 20, 20);
                canceller.setToolTipText("Terminate this Event");
                canceller.addActionListener(e -> {
                    if (App.showYesNoCancelDialog("Confirm",
                            "Do you really wish to cancel this " + (isTest() ? "Test?" : isExam() ? "Exam?" : "Event?"))) {
                        TaskActivity.EventsHandler.deleteEvent(this);
                        isPending = false;
                        timer.stop();
                    }
                });
            } else {
                canceller = KButton.createIconifiedButton("trash.png", 20, 20);
                canceller.setToolTipText("Remove this Event");
                canceller.addActionListener(e -> {
                    if (App.showYesNoCancelDialog("Confirm Removal","Do you wish to remove this "+
                            (isTest() ? "Test?" : isExam() ? "Exam?" : "Event?"))) {
                        TaskActivity.EventsHandler.deleteEvent(this);
                    }
                });
            }
            canceller.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            stateIndicator = new KLabel("Pending : "+dateDue, KFontFactory.createBoldFont(16));
            stateIndicator.setOpaque(false);

            eventLayer = new KPanel(1_000,30);//this is 30
            eventLayer.setLayout(new BorderLayout());
            eventLayer.add(new KPanel(new KLabel(getTitle(), KFontFactory.createPlainFont(17), Color.BLUE)),
                    BorderLayout.WEST);
            eventLayer.add(new KPanel(stateIndicator), BorderLayout.CENTER);
            eventLayer.add(canceller, BorderLayout.EAST);
        }

        private void signalEveNotice(){
            if (!eveIsAlerted) {
                final String eveText = "Dear "+Student.getLastName()+", "+title+" is just one day away from now.";
                Notification.create("Event Reminder",Student.getLastName()+", "+getTitle()+" is at your door-step!", eveText);
                eveIsAlerted = true;
            }
        }

        private void signalTimeupNotice(){
            if (!timeupIsAlerted) {
                final String timeupText = "Dear "+Student.getLastName()+", time is up for the event "+title+".";
                Notification.create("Event Time-Up",Student.getLastName()+", "+getTitle()+" is due now!", timeupText);
                timeupIsAlerted = true;
            }
        }

        public String getTitle(){
            return title;
        }

        public boolean isTest(){
            return this.title.contains("Test");
        }

        public boolean isExam(){
            return this.title.contains("Exam");
        }

        public boolean isPending(){
            return isPending;
        }

        private KButton getCanceller(){
            return canceller;
        }

        public KPanel getEventLayer(){
            return eventLayer;
        }

        public void endState(){
            stateIndicator.setText("Past : "+dateDue);
            stateIndicator.setFont(KFontFactory.createPlainFont(16));
            isPending = false;
            timer.stop();
            signalTimeupNotice();
        }

        private void wakeAlive(){
            final Calendar eveCalendar = Calendar.getInstance();
            eveCalendar.setTime(MDate.parse(this.dateDue+" 0:0:0"));
            eveCalendar.add(Calendar.DATE, -1);
            if (MDate.isSameDay(eveCalendar.getTime(), new Date())) {
                signalEveNotice();
            }
            final int residue = Globals.DAY - MDate.getTimeValue(new Date());
            initializeTimer(residue);
        }

        private String export(){
            return Globals.joinLines(title,
                    dateDue,
                    isPending,
                    eveIsAlerted,
                    timeupIsAlerted);
        }
    }


    public static void serialize(){
        final ArrayList<TodoSelf> todoList = TaskActivity.TodoHandler.TODOS;
        final String[] todos = new String[todoList.size()];
        for(int i = 0; i < todoList.size(); i++){
            todos[i] = todoList.get(i).export();
        }
        Serializer.toDisk(todos, Serializer.inPath("tasks", "todos.ser"));

        final ArrayList<ProjectSelf> projectList = TaskActivity.ProjectsHandler.PROJECTS;
        final String[] projects = new String[projectList.size()];
        for(int i = 0; i < projectList.size(); i++){
            projects[i] = projectList.get(i).export();
        }
        Serializer.toDisk(projects, Serializer.inPath("tasks", "projects.ser"));

        final ArrayList<AssignmentSelf> assignmentList = TaskActivity.AssignmentsHandler.ASSIGNMENTS;
        final String[] assignments = new String[assignmentList.size()];
        final ArrayList<Integer> groupIndexes = new ArrayList<>();
        for(int i = 0; i < assignmentList.size(); i++){
            final AssignmentSelf assignment = assignmentList.get(i);
            assignments[i] = assignment.export();
            if (assignment.isGroup) {
                groupIndexes.add(i);
            }
        }
        Serializer.toDisk(assignments, Serializer.inPath("tasks", "assignments.ser"));
        final String[] groupsMembers = new String[groupIndexes.size()];
        int j = 0;
        for (int index : groupIndexes){
            final AssignmentSelf assignment = assignmentList.get(index);
            groupsMembers[j] = Globals.joinLines(assignment.members);
            j++;
        }
        Serializer.toDisk(groupsMembers, Serializer.inPath("tasks", "groups.members.ser"));

        final ArrayList<EventSelf> eventsList = TaskActivity.EventsHandler.EVENTS;
        final String[] events = new String[eventsList.size()];
        for(int i = 0; i < eventsList.size(); i++){
            events[i] = eventsList.get(i).export();
        }
        Serializer.toDisk(events, Serializer.inPath("tasks", "events.ser"));
    }


    public static void deSerializeAll(){
        final Object todoObj = Serializer.fromDisk("todos.ser");
        if (todoObj == null) {
            App.silenceException("Failed to read TODO Tasks.");
        } else {
            final String[] todos = (String[]) todoObj;
            for (String data : todos) {
                final String[] lines = Globals.splitLines(data);
                final TodoSelf todoSelf = new TodoSelf(lines[0], Integer.parseInt(lines[2]),
                        lines[1], Boolean.parseBoolean(lines[4]));
                todoSelf.setTotalTimeConsumed(Integer.parseInt(lines[3]));
                todoSelf.setDateCompleted(lines[5]);
                todoSelf.eveIsAlerted = Boolean.parseBoolean(lines[6]);
                todoSelf.doneIsAlerted = Boolean.parseBoolean(lines[7]);
                if (todoSelf.isActive) { // This only means it slept alive - we're to check then if it's to wake alive or not
                    if (new Date().before(MDate.parse(todoSelf.dateExpectedToComplete))) {
                        todoSelf.wakeAlive();
                    } else {
                        todoSelf.wakeDead();
                    }
                }
                todoSelf.setUpUI();
                TaskActivity.TodoHandler.receiveFromSerials(todoSelf);
            }
        }

        final Object projectObj = Serializer.fromDisk(Serializer.inPath("tasks", "projects.ser"));
        if (projectObj == null) {
            App.silenceException("Failed to read Projects.");
        } else {
            final String[] projects = (String[]) projectObj;
            for (String data : projects) {
                final String[] lines = Globals.splitLines(data);
                final ProjectSelf projectSelf = new ProjectSelf(lines[0], lines[1], lines[2],
                        Integer.parseInt(lines[3]), Boolean.parseBoolean(lines[5]));
                projectSelf.setTotalTimeConsumed(Integer.parseInt(lines[4]));
                projectSelf.setDateCompleted(lines[6]);
                projectSelf.eveIsAlerted = Boolean.parseBoolean(lines[7]);
                projectSelf.completionIsAlerted = Boolean.parseBoolean(lines[8]);
                if (projectSelf.isLive) {
                    if (new Date().before(MDate.parse(projectSelf.dateExpectedToComplete))) {
                        projectSelf.wakeLive();
                        projectSelf.initializeUI();
                    } else {
                        projectSelf.wakeDead();
                        projectSelf.setUpDoneUI();
                    }
                } else {
                    projectSelf.setUpDoneUI();
                }
                TaskActivity.ProjectsHandler.receiveFromSerials(projectSelf);
            }
        }

        final Object assignObj = Serializer.fromDisk(Serializer.inPath("tasks", "assignments.ser"));
        final Object groupsMembersObj = Serializer.fromDisk(Serializer.inPath("tasks", "groups.members.ser"));
        if (assignObj == null || groupsMembersObj == null) {
            App.silenceException("Failed to read assignments.");
        } else {
            final String[] assignments = (String[]) assignObj;
            final String[] groupsMembers = (String[]) groupsMembersObj;
            for (int i = 0, j = 0; i < assignments.length; i++) {
                final String[] lines = Globals.splitLines(assignments[i]);
                final AssignmentSelf assignmentSelf = new AssignmentSelf(lines[0], lines[6], lines[1],
                        Boolean.parseBoolean(lines[2]), lines[4], lines[5], Boolean.parseBoolean(lines[3]));
                assignmentSelf.setSubmissionDate(lines[7]);
                assignmentSelf.memberCount = Integer.parseInt(lines[8]);
                assignmentSelf.eveIsAlerted = Boolean.parseBoolean(lines[9]);
                assignmentSelf.submissionIsAlerted = Boolean.parseBoolean(lines[10]);
                assignmentSelf.setUpUI(); // Todo consider recall
                if (assignmentSelf.isGroup) {
                    final String[] memberLines = Globals.splitLines(groupsMembers[j]);
                    assignmentSelf.members.addAll(Arrays.asList(memberLines));
                    j++;
                }
                if (assignmentSelf.isOn) {
                    if (MDate.parse(MDate.formatDateOnly(new Date())+" 0:0:0").
                            before(MDate.parse(assignmentSelf.deadLine+" 0:0:0"))) {
                        assignmentSelf.wakeAlive();
                    } else {
                        assignmentSelf.wakeDead();
                    }
                }
                assignmentSelf.setUpUI();
                TaskActivity.AssignmentsHandler.receiveFromSerials(assignmentSelf);
            }
        }

        final Object eventsObj = Serializer.fromDisk(Serializer.inPath("tasks", "events.ser"));
        if (eventsObj == null) {
            App.silenceException("Failed to read Events.");
        } else {
            final String[] events = (String[]) eventsObj;
            for (String data : events){
                final String[] lines = Globals.splitLines(data);
                final EventSelf eventSelf = new EventSelf(lines[0], lines[1], Boolean.parseBoolean(lines[2]));
                eventSelf.eveIsAlerted = Boolean.parseBoolean(lines[3]);
                eventSelf.timeupIsAlerted = Boolean.parseBoolean(lines[4]);
                eventSelf.setUpUI(); // Todo consider recall
                if (eventSelf.isPending) {
                    if (MDate.parse(MDate.formatDateOnly(new Date()) + " 0:0:0").
                            before(MDate.parse(eventSelf.dateDue + " 0:0:0"))) {
                        eventSelf.wakeAlive();
                    } else {
                        eventSelf.endState();
                    }
                }
                eventSelf.setUpUI();
                TaskActivity.EventsHandler.receiveFromSerials(eventSelf);
            }
        }
    }

}
