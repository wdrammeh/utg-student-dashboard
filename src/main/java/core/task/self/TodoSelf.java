package core.task.self;

import core.alert.Notification;
import core.task.exhibition.TodoExhibition;
import core.task.handler.TodoHandler;
import core.utils.FontFactory;
import core.utils.Globals;
import core.utils.MComponent;
import core.utils.MDate;
import proto.KButton;
import proto.KLabel;
import proto.KPanel;

import javax.swing.*;
import java.awt.*;
import java.util.Date;

public class TodoSelf {
    private String description;
    private String startDate;
    private int specifiedDuration;//In days !<5, !>30
    private int totalTimeConsumed;//This has only 2 variations: 1. An ongoing task is marked done 2. A task completes normally
    private boolean isActive;
    private String dateExpectedToComplete;//Shown for running tasks only - set with the constructor
    private String dateCompleted;//Shown for completed #s only - set with ending, whether voluntary or time-due
    private Timer timer;
    public boolean eveIsAlerted;
    public boolean doneIsAlerted;
    private transient KLabel togoLabel;
    private transient TodoExhibition exhibition;
    private transient KPanel layerPanel;


    public TodoSelf(String desc, int duration){
        this(desc, duration, MDate.formatNow(), true);
        initializeTimer(Globals.DAY);
        setUpUI();
    }

    public TodoSelf(String desc, int duration, String start, boolean isActive){
        description = desc;
        specifiedDuration = duration;
        startDate = start;
        dateExpectedToComplete = MDate.daysAfter(MDate.parseDayTime(startDate), duration);
        this.isActive = isActive;
    }

    private void initializeTimer(int firstDelay){
        timer = new Timer(Globals.DAY,null);
        timer.setInitialDelay(firstDelay);
        timer.addActionListener(e -> {
            togoLabel.setText(Globals.checkPlurality(getDaysLeft(), "days")+" to go");
            if (getDaysLeft() == 1) { // Fire eve-day notification if was not fired already
                togoLabel.setText("Less than a day to go");
                signalEveNotice();
            } else if (getDaysLeft() <= 0) {
                if (exhibition != null && exhibition.isShowing()) {
                    exhibition.dispose();
                }
                TodoHandler.transferTask(this, null, true);
                signalDoneNotice();
            }
        });
        timer.start();
    }

    public void setUpUI(){
        final KButton moreOptions = KButton.createIconifiedButton("options.png", 15, 15);
        moreOptions.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        moreOptions.addActionListener(e-> exhibition = new TodoExhibition(this));

        togoLabel = new KLabel(Globals.checkPlurality(getDaysLeft(), "days") + " to complete",
                FontFactory.createPlainFont(16));
        togoLabel.setOpaque(false);
        if (isActive) {
            togoLabel.setForeground(Color.RED);
        } else {
            togoLabel.setText("Completed "+dateCompleted);
            togoLabel.setForeground(Color.BLUE);
        }

        final KPanel quantaPanel = new KPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        quantaPanel.addAll(new KLabel(specifiedDuration+" days task",
                        FontFactory.createPlainFont(16)), togoLabel, moreOptions);

        if (layerPanel == null) {
            layerPanel = new KPanel(1_000, 35);
        } else {
            MComponent.clear(layerPanel);
        }
        layerPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true));
        layerPanel.setLayout(new BorderLayout());
        layerPanel.add(new KPanel(new KLabel(description, FontFactory.createPlainFont(17),
                        Color.BLUE)), BorderLayout.WEST);
        layerPanel.add(quantaPanel, BorderLayout.EAST);
        MComponent.ready(layerPanel);
    }

    private void signalEveNotice(){
        if (!eveIsAlerted) {
            final String info = "<p>Task <b>"+getDescription()+"</b> is to be completed in less than a day.</p>";
            Notification.create("Task Reminder","Task "+getDescription()+" is due tomorrow.", info);
            eveIsAlerted = true;
        }
    }

    private void signalDoneNotice(){
        if (!doneIsAlerted) {
            final String info = "<p>Task <b>"+getDescription()+"</b> is now due. This Task is now considered done as per the given date limit.</p>";
            Notification.create("Task Completed","Task "+getDescription()+" is now completed.", info);
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
        return (int) MDate.getDifference(MDate.parseDayTime(startDate), new Date());
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

    public void wakeAlive(){
        setUpUI();
        if (getDaysLeft() == 1) {
            togoLabel.setText("Less than a day to complete");
            signalEveNotice();
        }
        int residue = MDate.getTimeValue(MDate.parseDayTime(dateExpectedToComplete)) - MDate.getTimeValue(new Date());
        if (residue < 0) { // reverse it then...
            residue = Globals.DAY - Math.abs(residue);
        }
        initializeTimer(residue);
    }

    public void wakeDead(){
        dateCompleted = dateExpectedToComplete;
        totalTimeConsumed = specifiedDuration;
        isActive = false;
        signalDoneNotice();
    }

    public String export(){
        return Globals.joinLines(new Object[]{description, MDate.toSerial(MDate.parseDayTime(startDate)),
                specifiedDuration, totalTimeConsumed, isActive, MDate.toSerial(MDate.parseDayTime(dateCompleted)),
                eveIsAlerted, doneIsAlerted});
    }

}
