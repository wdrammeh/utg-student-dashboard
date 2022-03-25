package core.module;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.BoxLayout;
import javax.swing.SwingUtilities;

import core.utils.FontFactory;
import core.utils.Globals;
import core.utils.MComponent;
import proto.KButton;
import proto.KDialog;
import proto.KLabel;
import proto.KPanel;

/**
 * RegisteredCourse in general refer to modules the student
 * has registered for any given semester. In other words,
 * the courses student is doing in a semester.
 *
 * <ul>
 *    <li> Does not use <code> creditHours </code> </li>
 *    <li> Schedule and venue may be readable,
 *    but it's recommended for user to be setting them as required </li>
 *    <li> Lecturer names are generally never editable
 * </ul>
 *
 */
public class RegisteredCourse extends Module {

    public RegisteredCourse(String code, String name, String lecturer, String campus,
                            String room, String day, String time, boolean confirmed) {
        // Todo: Refer to missing/indeterminate params - year, semester, requirement
        super(code, name, "", "", lecturer, campus, room, day, time, "", -1, confirmed);
    }

//    @Override
//    public void merge(Module old) {
//        this.day = old.day;
//        this.time = old.time;
//        this.requirement = old.requirement;
//        if (this.isLecturerEditable) {
//            this.lecturer = old.lecturer;
//        }
//    }

    @Override
    public String export() {
        return Globals.joinLines(new Object[]{code, name, lecturer, campus, room, day, time, isConfirmed});
    }

    public static RegisteredCourse create(String data) {
        final String[] lines = Globals.splitLines(data);
        return new RegisteredCourse(lines[0], lines[1], lines[2], lines[3], lines[4], lines[5],
            lines[6], Boolean.parseBoolean(lines[7]));
    }

    @Override
    public void exhibit(Component base) {
        final KDialog dialog = new KDialog(name);
        dialog.setResizable(true);
        dialog.setModalityType(KDialog.DEFAULT_MODALITY_TYPE);

        final Font hintFont = FontFactory.createBoldFont(15);
        final Font valueFont = FontFactory.createPlainFont(15);

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

        final KLabel statusLabel = new KLabel(this.status, valueFont, this.status.equals(Module.CONFIRMED) ? Color.BLUE :
                this.status.equals(Globals.UNKNOWN) ? Color.RED : Color.GRAY);
        final KPanel statusPanel = new KPanel(new BorderLayout());
        statusPanel.add(new KPanel(new KLabel("Status:", hintFont)), BorderLayout.WEST);
        statusPanel.add(new KPanel(statusLabel), BorderLayout.CENTER);

        final KButton closeButton = new KButton("Close");
        closeButton.addActionListener(e-> dialog.dispose());

        final KPanel contentPanel = new KPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.addAll(codePanel, namePanel, lectPanel, venuePanel, schedulePanel, statusPanel,
                MComponent.contentBottomGap(), new KPanel(closeButton));

        dialog.getRootPane().setDefaultButton(closeButton);
        dialog.setContentPane(contentPanel);
        dialog.pack();
        dialog.setLocationRelativeTo(base);
        SwingUtilities.invokeLater(()-> dialog.setVisible(true));
    }

}
