package core.module;

import core.Board;
import core.utils.App;
import core.utils.Globals;
import core.utils.MComponent;
import proto.*;

import javax.swing.*;
import java.awt.*;
import java.io.Serializable;

/**
 * A model for the registered courses.
 * Todo: Rename this to RegisteredCourse
 */
public class RunningCourse implements Serializable {
    private String code;
    private String name;
    private String lecturer;
    private String venue;
    private String room;
    private String day;
    private String time;
    private boolean isConfirmed;


    public RunningCourse(String code, String name, String lecturer, String venue, String room, String day, String time,
                         boolean onPortal){
        this.code = code.toUpperCase();
        this.name = name;
        this.lecturer = lecturer;
        this.venue = venue;
        this.room = room;
        this.day = Globals.hasNoText(day) || day.equals(Course.UNKNOWN) ? "" : day;
        this. time = Globals.hasNoText(time) || time.equals(Course.UNKNOWN) ? "" : time;
        this.isConfirmed = onPortal;
    }

    public String getCode(){
        return code;
    }

    public void setCode(String newCode){
        this.code = newCode;
    }

    public String getName(){
        return name;
    }

    public void setName(String newName){
        this.name = newName;
    }

    public String getLecturer(){
        return lecturer;
    }

    public void setLecturer(String newLecturer){
        this.lecturer = newLecturer;
    }

    public String getVenue(){
        return venue;
    }

    public void setVenue(String newVenue){
        this.venue = newVenue;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getDay(){
        return day;
    }

    public void setDay(String newDay){
        this.day = newDay;
    }

    public String getTime(){
        return time;
    }

    public void setTime(String newTime){
        this.time = newTime;
    }

    public boolean isConfirmed(){
        return isConfirmed;
    }

    public void setConfirmed(boolean onPortal){
        this.isConfirmed = onPortal;
    }

    public String getAbsoluteName(){
        return String.join(" ", code, name);
    }

    public String getSchedule(){
        if (Globals.hasText(day) && Globals.hasText(time)) {
            return String.join(" ", day, time);
        } else if (Globals.hasText(day) && Globals.hasNoText(time)) {
            return String.join(" - ", day, "Unknown time");
        } else if (Globals.hasNoText(day) && Globals.hasText(time)) {
            return String.join(" - ", time, "Unknown day");
        } else {
            return "";
        }
    }

    /**
     * @see Course#exportContent()
     */
    public String exportContent(){
        return Globals.joinLines(code,
                name,
                lecturer,
                venue,
                room,
                day,
                time,
                isConfirmed);
    }

    /**
     * @see Course#create(String)
     */
    public static RunningCourse create(String data){
        final String[] lines = Globals.splitLines(data);
        boolean validity = false;
        try {
            validity = Boolean.parseBoolean(lines[7]);
        } catch (Exception e) {
            App.silenceException("Error reading validity of registered course "+lines[3]);
        }
        return new RunningCourse(lines[0], lines[1], lines[2], lines[3], lines[4], lines[5], lines[6], validity);
    }

    /**
     * @see Course#exhibit(Course)
     */
    public static void exhibit(RunningCourse course, Component base) {
        if (course == null) {
            return;
        }

        final KDialog dialog = new KDialog(course.name);
        dialog.setResizable(true);
        dialog.setModalityType(KDialog.DEFAULT_MODALITY_TYPE);

        final Font hintFont = KFontFactory.createBoldFont(15);
        final Font valueFont = KFontFactory.createPlainFont(15);

        final KPanel codePanel = new KPanel(new BorderLayout());
        codePanel.add(new KPanel(new KLabel("Code:", hintFont)), BorderLayout.WEST);
        codePanel.add(new KPanel(new KLabel(course.code, valueFont)), BorderLayout.CENTER);

        final KPanel namePanel = new KPanel(new BorderLayout());
        namePanel.add(new KPanel(new KLabel("Name:", hintFont)), BorderLayout.WEST);
        namePanel.add(new KPanel(new KLabel(course.name, valueFont)), BorderLayout.CENTER);

        final KPanel lectPanel = new KPanel(new BorderLayout());
        lectPanel.add(new KPanel(new KLabel("Lecturer:", hintFont)), BorderLayout.WEST);
        lectPanel.add(new KPanel(new KLabel(course.lecturer, valueFont)), BorderLayout.CENTER);

        final KPanel schedulePanel = new KPanel(new BorderLayout());
        schedulePanel.add(new KPanel(new KLabel("Schedule:", hintFont)), BorderLayout.WEST);
        schedulePanel.add(new KPanel(new KLabel(course.getSchedule(), valueFont)), BorderLayout.CENTER);

        final KPanel venuePanel = new KPanel(new BorderLayout());
        venuePanel.add(new KPanel(new KLabel("Venue:", hintFont)), BorderLayout.WEST);
        venuePanel.add(new KPanel(new KLabel(course.venue, valueFont)), BorderLayout.CENTER);

        final KPanel roomPanel = new KPanel(new BorderLayout());
        roomPanel.add(new KPanel(new KLabel("Room:", hintFont)), BorderLayout.WEST);
        roomPanel.add(new KPanel(new KLabel(course.room, valueFont)), BorderLayout.CENTER);

        final KPanel statusPanel = new KPanel(new BorderLayout());
        statusPanel.add(new KPanel(new KLabel("Status:", hintFont)), BorderLayout.WEST);
        final KLabel vLabel = course.isConfirmed ? new KLabel("Confirmed", valueFont, Color.BLUE) :
                new KLabel("Unknown", valueFont, Color.RED);
        statusPanel.add(new KPanel(vLabel), BorderLayout.CENTER);

        final KButton closeButton = new KButton("Close");
        closeButton.addActionListener(e-> dialog.dispose());

        final KPanel contentPanel = new KPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.addAll(codePanel, namePanel, lectPanel, schedulePanel, venuePanel, roomPanel, statusPanel,
                MComponent.contentBottomGap(), new KPanel(closeButton));

        dialog.getRootPane().setDefaultButton(closeButton);
        dialog.setContentPane(contentPanel);
        dialog.pack();
        dialog.setLocationRelativeTo(base == null ? Board.getRoot() : base);
        SwingUtilities.invokeLater(()-> dialog.setVisible(true));
    }

    public static void exhibit(RunningCourse runningCourse) {
        exhibit(runningCourse, null);
    }

}
