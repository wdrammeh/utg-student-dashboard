package core.task.self;

import core.alert.Notification;
import core.task.exhibition.ProjectExhibition;
import core.task.handler.ProjectHandler;
import core.utils.Globals;
import core.utils.MComponent;
import core.utils.MDate;
import core.utils.FontFactory;
import proto.KButton;
import proto.KLabel;
import proto.KPanel;

import javax.swing.*;
import java.awt.*;
import java.util.Date;
import java.util.Objects;

public class ProjectSelf {
    private String projectName;
    private String type;
    private String startDate;
    private int specifiedDuration;
    private int totalTimeConsumed;
    private boolean isLive;
    private Timer timer;
    private String dateExpectedToComplete;
    private String dateCompleted;
    public boolean eveIsAlerted;
    public boolean completionIsAlerted;
    private transient ProjectExhibition exhibition;
    private transient KButton terminationButton, completionButton, moreOptions;
    private transient JProgressBar projectProgression;
    private transient KLabel progressLabelPercentage;
    private transient KPanel projectLayer;


    public ProjectSelf(String name, String type, int duration){
        this(name, type, MDate.formatNow(), duration, true);
        initializeTimer(Globals.DAY);
        initializeUI();
    }

    public ProjectSelf(String name, String type, String startTime, int duration, boolean live){
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
                ProjectHandler.performIComplete(this,true);
                signalCompletionNotice();
            }
        });
        timer.start();
    }

    public void initializeUI(){
        final Dimension optionsDim = new Dimension(30, 30);//the small-buttons actually

        progressLabelPercentage = new KLabel("", FontFactory.createPlainFont(18), Color.BLUE);
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
        terminationButton.setToolTipText("Terminate");
        terminationButton.addActionListener(ProjectHandler.removalListener(this));

        completionButton = KButton.createIconifiedButton("mark.png", 20, 20);
        completionButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        completionButton.setPreferredSize(optionsDim);
        completionButton.setToolTipText("Mark as Completed");
        completionButton.addActionListener(e-> ProjectHandler.performIComplete(this, false));

        moreOptions = KButton.createIconifiedButton("options.png", 15, 15);
        moreOptions.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        moreOptions.setToolTipText("About");
        moreOptions.addActionListener(e -> exhibition = new ProjectExhibition(this));

        final KPanel quantaLayer = new KPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        quantaLayer.addAll(new KLabel("( "+getType()+" Project )", FontFactory.createPlainFont(16)),
                projectProgression, progressLabelPercentage, terminationButton,
                completionButton, moreOptions);

        projectLayer = new KPanel(1_000, 35);
        projectLayer.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true));
        projectLayer.setLayout(new BorderLayout());
        projectLayer.add(new KPanel(new KLabel(projectName, FontFactory.createPlainFont(17),
                Color.BLUE)), BorderLayout.WEST);
        projectLayer.add(quantaLayer, BorderLayout.EAST);
    }

    public void setUpDoneUI(){
        projectProgression = new JProgressBar(0, specifiedDuration);
        projectProgression.setValue(specifiedDuration);
        projectProgression.setPreferredSize(new Dimension(150, 20));
        projectProgression.setForeground(Color.BLUE);

        progressLabelPercentage = new KLabel(projectProgression.getString(),
                FontFactory.createPlainFont(18), Color.BLUE);
        progressLabelPercentage.setOpaque(false);

        terminationButton = KButton.createIconifiedButton("terminate.png", 20, 20);
        terminationButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        terminationButton.setToolTipText("Remove");
        terminationButton.addActionListener(ProjectHandler.removalListener(this));

        moreOptions = KButton.createIconifiedButton("options.png", 15, 15);
        moreOptions.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        moreOptions.setToolTipText("About");
        moreOptions.addActionListener(e -> exhibition = new ProjectExhibition(this));

        final KPanel quantaLayer = new KPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        quantaLayer.addAll(new KLabel("( "+getType()+" Project )", FontFactory.createPlainFont(16)),
                projectProgression, progressLabelPercentage, terminationButton, moreOptions);

        if (projectLayer == null) {
            projectLayer = new KPanel(1_000, 35);
            projectLayer.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true));
            projectLayer.setLayout(new BorderLayout());
        } else {
            MComponent.clear(projectLayer);
        }
        projectLayer.add(new KPanel(new KLabel(this.projectName, FontFactory.createPlainFont(17),
                Color.BLUE)), BorderLayout.WEST);
        projectLayer.add(quantaLayer, BorderLayout.EAST);
        MComponent.ready(projectLayer);
    }

    private void signalEveNotice(){
        if (!eveIsAlerted) {
            final String info = "<p>Project "+getProjectName()+", created since "+getStartDate()+" is to be completed by tomorrow.</p>";
            Notification.create("Project Reminder","Specified duration for the "+getType()+" Project "+
                    getProjectName()+" is running out.", info);
            eveIsAlerted = true;
        }
    }

    private void signalCompletionNotice(){
        if (!completionIsAlerted) {
            final String text = "<p>The specified period of the "+ getType()+" Project <b>"+getProjectName()+"</b> is now attained.</p>";
            Notification.create("Project Completed","Specified duration for the "+getType()+" Project "+
                    getProjectName()+" is reached.", text);
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
        return (int) MDate.getDifference(Objects.requireNonNull(MDate.parseDayTime(startDate)), new Date());
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

    public void wakeLive(){
        if (getDaysLeft() == 1) {
            signalEveNotice();
        }
        int residue = MDate.getTimeValue(MDate.parseDayTime(dateExpectedToComplete)) - MDate.getTimeValue(new Date());
        if (residue < 0) {
            residue = Globals.DAY - Math.abs(residue);
        }
        initializeTimer(residue);
    }

    public void wakeDead(){
        dateCompleted = dateExpectedToComplete;
        isLive = false;
        signalCompletionNotice();
    }

    public String export() {
        return Globals.joinLines(new Object[]{projectName, type, MDate.toSerial(MDate.parseDayTime(startDate)),
                specifiedDuration, totalTimeConsumed, isLive, MDate.toSerial(MDate.parseDayTime(dateCompleted)),
                eveIsAlerted, completionIsAlerted});
    }

}
