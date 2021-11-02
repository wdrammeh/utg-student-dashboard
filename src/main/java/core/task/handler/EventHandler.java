package core.task.handler;

import core.utils.Serializer;
import core.task.creator.EventCreator;
import core.task.self.EventSelf;
import core.utils.App;
import core.utils.Globals;
import core.utils.MComponent;
import core.utils.MDate;
import proto.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

import static core.task.TaskActivity.*;

public class EventHandler {
    private static int upcomingCount;
    private static KPanel eventsReside;
    private static KButton portalButton;
    public static final ArrayList<EventSelf> EVENTS = new ArrayList<>();


    public static void initHandle(KButton portalButton){
        EventHandler.portalButton = portalButton;
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
        eventsReside.setLayout(new FlowLayout(CONTENTS_POSITION, 10, 10));
        deserialize();
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
        portalButton.setText(upcomingCount);
    }

    public static void newIncoming(EventSelf event){
        EVENTS.add(event);
        eventsReside.add(event.getEventLayer());
        MComponent.ready(eventsReside);
        renewCount(1);
    }

    public static void receiveFromSerials(EventSelf eventSelf) {
        eventsReside.add(eventSelf.getEventLayer());
        if (eventSelf.isPending()) {
            renewCount(1);
        }
        EVENTS.add(eventSelf);
    }

    public static JComponent getComponent(){
        final KMenuItem testItem = new KMenuItem("Upcoming Test", e-> {
            new EventCreator(EventCreator.TEST).setVisible(true);
        });

        final KMenuItem examItem = new KMenuItem("Upcoming Exam", e-> {
            new EventCreator(EventCreator.EXAM).setVisible(true);
        });

        final KMenuItem otherItem = new KMenuItem("Other", e-> {
            new EventCreator(EventCreator.OTHER).setVisible(true);
        });

        final KPopupMenu jPopup = new KPopupMenu();
        jPopup.add(testItem);
        jPopup.add(examItem);
        jPopup.add(otherItem);

        final KButton popUpButton = new KButton("New Event");
        popUpButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        popUpButton.setFont(TASK_BUTTONS_FONT);
        popUpButton.addActionListener(e-> jPopup.show(popUpButton,
                popUpButton.getX() + popUpButton.getPreferredSize().width,
                popUpButton.getY() - popUpButton.getPreferredSize().height/2));

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

    private static void deserialize(){
        final Object eventsObj = Serializer.fromDisk(Serializer.inPath("tasks", "events.ser"));
        if (eventsObj == null) {
            App.silenceException("Failed to read Events.");
        } else {
            final String[] events = (String[]) eventsObj;
            for (String data : events){
                final String[] lines = Globals.splitLines(data);
                final EventSelf eventSelf = new EventSelf(lines[0],
                        MDate.formatDay(MDate.fromSerial(lines[1])), Boolean.parseBoolean(lines[2]));
                eventSelf.eveIsAlerted = Boolean.parseBoolean(lines[3]);
                eventSelf.timeupIsAlerted = Boolean.parseBoolean(lines[4]);
                eventSelf.setUpUI(); // Todo consider recall
                if (eventSelf.isPending()) {
                    if (MDate.isDeadlinePast(MDate.parseDay(eventSelf.getDateDue()))) {
                        eventSelf.endState();
                    } else {
                        eventSelf.wakeAlive();
                    }
                }
                eventSelf.setUpUI();
                receiveFromSerials(eventSelf);
            }
        }
    }

}
