package core.task.self;

import core.alert.Notification;
import core.task.handler.EventHandler;
import core.user.Student;
import core.utils.App;
import core.utils.Globals;
import core.utils.MComponent;
import core.utils.MDate;
import proto.KButton;
import proto.KFontFactory;
import proto.KLabel;
import proto.KPanel;

import javax.swing.*;
import java.awt.*;
import java.util.Calendar;
import java.util.Date;

/**
 * This type deal with dates similar to AssignmentSelf
 */
public class EventSelf {
    private String title;
    private String dateDue;
    private Timer timer;
    private boolean isPending;
    public boolean eveIsAlerted;
    public boolean timeupIsAlerted;
    private transient KLabel stateIndicator;
    private transient KButton canceller;
    private transient KPanel eventLayer;


    // where deadline is in date only, and no time, as in the AssignmentSelf type above
    public EventSelf(String name, String deadline){
        this(name, deadline, true);
        initializeTimer(Globals.DAY);
        setUpUI();
    }

    public EventSelf(String name, String deadline, boolean status){
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
            } else if (MDate.isSameDay(MDate.parse(dateDue+" 0:0:0"), new Date())) {
                endState();
                setUpUI();
                MComponent.ready(eventLayer);
                EventHandler.renewCount(-1);
            }
        });
        timer.start();
    }

    public void setUpUI(){
        canceller = KButton.createIconifiedButton("terminate.png", 20, 20);
        if (isPending) {
            canceller.addActionListener(e -> {
                if (App.showYesNoCancelDialog("Confirm",
                        "Do you really wish to cancel '"+title+"'?")) {
                    EventHandler.deleteEvent(this);
                    isPending = false;
                    timer.stop();
                }
            });
        } else {
            canceller.addActionListener(e -> {
                if (App.showYesNoCancelDialog("Confirm",
                        "Do you really wish to remove '"+title+"'?")) {
                    EventHandler.deleteEvent(this);
                }
            });
        }
        stateIndicator = new KLabel((isPending? "Due on" : "Past since") + dateDue, KFontFactory.createBoldFont(16));

        canceller.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        stateIndicator.setOpaque(false);

        eventLayer = new KPanel(new BorderLayout(), new Dimension(1_000,30)); // this is 30, unlike the others
        eventLayer.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true));
        eventLayer.add(new KPanel(new KLabel(getTitle(), KFontFactory.createPlainFont(17),
                        Color.BLUE)), BorderLayout.WEST);
        eventLayer.add(new KPanel(stateIndicator), BorderLayout.CENTER);
        eventLayer.add(canceller, BorderLayout.EAST);
    }

    private void signalEveNotice(){
        if (!eveIsAlerted) {
            final String info = "Dear "+ Student.getLastName()+"," +
                    "<p>"+title+" is just one day away from now.</p>";
            Notification.create("Event Reminder",Student.getLastName()+", "+getTitle()+" is at your door-step!", info);
            eveIsAlerted = true;
        }
    }

    private void signalTimeupNotice(){
        if (!timeupIsAlerted) {
            final String info = "Dear "+Student.getLastName()+"," +
                    "<p>time is up for the event "+title+".</p>";
            Notification.create("Event Time-up",Student.getLastName()+", "+getTitle()+" is due now!", info);
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

    public String getDateDue() {
        return dateDue;
    }

    public void endState(){
        stateIndicator.setText("Past : "+dateDue);
        stateIndicator.setFont(KFontFactory.createPlainFont(16));
        isPending = false;
        if (timer != null) {
            timer.stop();
        }
        signalTimeupNotice();
    }

    public void wakeAlive(){
        final Calendar eveCalendar = Calendar.getInstance();
        eveCalendar.setTime(MDate.parse(this.dateDue+" 0:0:0"));
        eveCalendar.add(Calendar.DATE, -1);
        if (MDate.isSameDay(eveCalendar.getTime(), new Date())) {
            signalEveNotice();
        }
        final int residue = Globals.DAY - MDate.getTimeValue(new Date());
        initializeTimer(residue);
    }

    public String export(){
        return Globals.joinLines(new Object[]{title, dateDue, isPending, eveIsAlerted, timeupIsAlerted});
    }

}
