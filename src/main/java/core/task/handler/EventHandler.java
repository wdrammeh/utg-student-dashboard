package core.task.handler;

import core.task.creator.EventCreator;
import core.task.self.EventSelf;
import core.utils.App;
import core.utils.Globals;
import core.utils.MComponent;
import core.utils.MDate;
import proto.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Date;

import static core.task.TaskActivity.*;
import static core.task.creator.TodoCreator.DESCRIPTION_LIMIT;

public class EventHandler {
    public static final ArrayList<EventSelf> EVENTS = new ArrayList<>();
    private static int upcomingCount;
    private static KPanel eventsReside;
    private static EventCreator testCreator, examCreator, othersCreator;
    private static KButton bigButton;


    public EventHandler(KButton bigButton){
        EventHandler.bigButton = bigButton;
        eventsReside = new KPanel(){
            @Override
            public Component add(Component comp) {
                eventsReside.setPreferredSize(new Dimension(eventsReside.getPreferredSize().width,
                        eventsReside.getPreferredSize().height+35));
                return super.add(comp);
            }

            @Override
            public void remove(Component comp) {
                super.remove(comp);
                eventsReside.setPreferredSize(new Dimension(eventsReside.getPreferredSize().width,
                        eventsReside.getPreferredSize().height-35));
            }
        };
        eventsReside.setLayout(new FlowLayout(CONTENTS_POSITION));
    }

    public static ActionListener newListener(){
        return e -> {
            final EventCreator requiredCreator = getShowingCreator();
            if (requiredCreator == null) {
                return;
            }
            String tName = requiredCreator.getDescriptionField().getText();
            if (Globals.hasNoText(tName)) {
                App.reportError(requiredCreator.getRootPane(), "No Name",
                        "Please specify a name for the event.");
                requiredCreator.getDescriptionField().requestFocusInWindow();
            } else if (tName.length() > DESCRIPTION_LIMIT) {
                App.reportError(requiredCreator.getRootPane(), "Error",
                        "Sorry, the event's name should be at most "+
                                DESCRIPTION_LIMIT+" characters.");
                requiredCreator.getDescriptionField().requestFocusInWindow();
            } else if (Globals.hasNoText(requiredCreator.getProvidedDate())) {
                App.reportError(requiredCreator.getRootPane(), "Error",
                        "Please provide all the fields for the date of the "+
                                (requiredCreator.type()));
            } else {
                final Date date = MDate.parse(requiredCreator.getProvidedDate() + " 0:0:0");
                if (date == null) {
                    return;
                }
                if (date.before(new Date())) {
                    App.reportError(requiredCreator.getRootPane(),"Past Deadline",
                            "Please consider the deadline. It's already past.");
                    return;
                }
                if (requiredCreator.getTitle().contains("Test")) {
                    tName = tName + " Test";
                } else if (requiredCreator.getTitle().contains("Exam")) {
                    tName = tName + " Examination";
                }
                final String dateString = MDate.formatDateOnly(date);
                if (App.showYesNoCancelDialog(requiredCreator.getRootPane(),"Confirm",
                        "Do you wish to add the following event?\n-\n" +
                                "Title:  "+tName+"\n" +
                                "Date:  "+dateString)) {
                    final EventSelf incomingEvent = new EventSelf(tName, dateString);
                    EVENTS.add(incomingEvent);
                    eventsReside.add(incomingEvent.getEventLayer());
                    MComponent.ready(eventsReside);
                    requiredCreator.dispose();
                    renewCount(1);
                }
            }
        };
    }

    private static EventCreator getShowingCreator(){
        if (testCreator != null && testCreator.isShowing()) {
            return testCreator;
        } else if (examCreator != null && examCreator.isShowing()) {
            return examCreator;
        } else if (othersCreator != null && othersCreator.isShowing()) {
            return othersCreator;
        } else {
            return null;
        }
    }

    public static void deleteEvent(EventSelf event){
        EVENTS.remove(event);
        eventsReside.remove(event.getEventLayer());
        MComponent.ready(eventsReside);
        if (event.isPending()) {
            EventHandler.renewCount(-1);
        }
    }

    public static void renewCount(int value){
        upcomingCount += value;
        bigButton.setText(upcomingCount);
    }

    public static void receiveFromSerials(EventSelf eventSelf) {
        eventsReside.add(eventSelf.getEventLayer());
        if (eventSelf.isPending()) {
            renewCount(1);
        }
        EVENTS.add(eventSelf);
    }

    public JComponent getComponent(){
        final KMenuItem testItem = new KMenuItem("Upcoming Test", e-> {
            testCreator = new EventCreator(EventCreator.TEST);
            testCreator.setVisible(true);
        });

        final KMenuItem examItem = new KMenuItem("Upcoming Exam", e-> {
            examCreator = new EventCreator(EventCreator.EXAM);
            examCreator.setVisible(true);
        });

        final KMenuItem otherItem = new KMenuItem("Other", e-> {
            othersCreator = new EventCreator(EventCreator.OTHER);
            othersCreator.setVisible(true);
        });

        final JPopupMenu jPopup = new JPopupMenu();
        jPopup.add(testItem);
        jPopup.add(examItem);
        jPopup.add(otherItem);

        final KButton popUpButton = new KButton("New Event");
        popUpButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        popUpButton.setFont(TASK_BUTTONS_FONT);
        popUpButton.addActionListener(e-> jPopup.show(popUpButton, popUpButton.getX(), popUpButton.getY() +
                (popUpButton.getPreferredSize().height)));

        final KPanel labelPanelPlus = new KPanel(new BorderLayout());
        labelPanelPlus.add(popUpButton, BorderLayout.WEST);
        labelPanelPlus.add(new KPanel(new KLabel("Events", TASK_HEADERS_FONT)), BorderLayout.CENTER);

        final KPanel eventsComponent = new KPanel(new BorderLayout());
        eventsComponent.add(labelPanelPlus, BorderLayout.NORTH);
        eventsComponent.add(new KScrollPane(eventsReside), BorderLayout.CENTER);
        return eventsComponent;
    }

    public static int getUpcomingCount(){
        return upcomingCount;
    }

}
