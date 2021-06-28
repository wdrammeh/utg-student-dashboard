package core.module;

import core.Board;
import core.utils.App;
import core.utils.Globals;
import core.utils.MComponent;
import proto.*;

import javax.swing.*;
import java.awt.*;

/**
 * A model for registered (active) courses.
 * Todo: add requirement field
 */
public class RegisteredCourse {
    private String code;
    private String name;
    private String lecturer;
    private String campus;
    private String room;
    private String day;
    private String time;
    private boolean isConfirmed;
    private String status;
    public static final String REGISTERED = "Registered";


    public RegisteredCourse(String code, String name, String lecturer, String campus, String room,
                            String day, String time, boolean onPortal){
        this.code = code.toUpperCase();
        this.name = name;
        this.lecturer = lecturer;
        this.campus = campus;
        this.room = room;
        this.day = day;
        this. time = time;
        this.isConfirmed = onPortal;
        this.status = onPortal ? REGISTERED : Globals.UNKNOWN;
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

    public String getCampus(){
        return campus;
    }

    public void setCampus(String newVenue){
        this.campus = newVenue;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;

//        if this course was edited while being verified, then it was replaced in the list, then this is just an unintended pointer
        final RegisteredCourse any = SemesterActivity.getByCode(this.code);
        if (any != null) {
            any.status = status;
        }
    }

    public String getAbsoluteName(){
        return String.join(" ", "("+code+")", name);
    }

    public String getSchedule(){
        return Course.scheduleOf(day, time);
    }

    public String getVenue(){
        return Course.venueOf(campus, room);
    }

    /**
     * @see Course#exportContent()
     */
    public String exportContent(){
        return Globals.joinLines(new Object[]{code, name, lecturer, campus, room, day, time,
                isConfirmed});
    }

    /**
     * @see Course#create(String)
     */
    public static RegisteredCourse create(String data){
        final String[] lines = Globals.splitLines(data);
        boolean validity = false;
        try {
            validity = Boolean.parseBoolean(lines[7]);
        } catch (Exception e) {
            App.silenceException(String.format("Failed to read validity of registered course '%s'.", lines[3]));
        }
        return new RegisteredCourse(lines[0], lines[1], lines[2], lines[3], lines[4], lines[5], lines[6], validity);
    }

    /**
     * @see Course#exhibit(Component)
     */
    public void exhibit(Component base) {
        final KDialog exhibitor = new KDialog(this.name);
        exhibitor.setResizable(true);
        exhibitor.setModalityType(KDialog.DEFAULT_MODALITY_TYPE);

        final Font hintFont = KFontFactory.createBoldFont(15);
        final Font valueFont = KFontFactory.createPlainFont(15);

        final KPanel codePanel = new KPanel(new BorderLayout());
        codePanel.add(new KPanel(new KLabel("Code:", hintFont)), BorderLayout.WEST);
        codePanel.add(new KPanel(new KLabel(this.code, valueFont)), BorderLayout.CENTER);

        final KPanel namePanel = new KPanel(new BorderLayout());
        namePanel.add(new KPanel(new KLabel("Name:", hintFont)), BorderLayout.WEST);
        namePanel.add(new KPanel(new KLabel(this.name, valueFont)), BorderLayout.CENTER);

        final KPanel lectPanel = new KPanel(new BorderLayout());
        lectPanel.add(new KPanel(new KLabel("Lecturer:", hintFont)), BorderLayout.WEST);
        lectPanel.add(new KPanel(new KLabel(this.lecturer, valueFont)), BorderLayout.CENTER);

        final KPanel venuePanel = new KPanel(new BorderLayout());
        venuePanel.add(new KPanel(new KLabel("Venue:", hintFont)), BorderLayout.WEST);
        venuePanel.add(new KPanel(new KLabel(this.getVenue(), valueFont)), BorderLayout.CENTER);

        final KPanel schedulePanel = new KPanel(new BorderLayout());
        schedulePanel.add(new KPanel(new KLabel("Schedule:", hintFont)), BorderLayout.WEST);
        schedulePanel.add(new KPanel(new KLabel(this.getSchedule(), valueFont)), BorderLayout.CENTER);

        final KLabel statusLabel = new KLabel(this.status, valueFont, this.status.equals(Course.CONFIRMED) ? Color.BLUE :
                this.status.equals(Globals.UNKNOWN) ? Color.RED : Color.GRAY);
        final KPanel statusPanel = new KPanel(new BorderLayout());
        statusPanel.add(new KPanel(new KLabel("Status:", hintFont)), BorderLayout.WEST);
        statusPanel.add(new KPanel(statusLabel), BorderLayout.CENTER);

        final KButton closeButton = new KButton("Close");
        closeButton.addActionListener(e-> exhibitor.dispose());

        final KPanel contentPanel = new KPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.addAll(codePanel, namePanel, lectPanel, venuePanel, schedulePanel, statusPanel,
                MComponent.contentBottomGap(), new KPanel(closeButton));

        exhibitor.getRootPane().setDefaultButton(closeButton);
        exhibitor.setContentPane(contentPanel);
        exhibitor.pack();
        exhibitor.setLocationRelativeTo(base);
        SwingUtilities.invokeLater(()-> exhibitor.setVisible(true));
    }

    public void exhibit() {
        exhibit(Board.getRoot());
    }

}
