package core.module;

import core.Board;
import core.first.PrePortal;
import core.user.Student;
import core.utils.App;
import core.utils.FontFactory;
import core.utils.Globals;
import core.utils.MComponent;
import proto.KButton;
import proto.KDialog;
import proto.KLabel;
import proto.KPanel;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * The Course type models courses the student has already done
 * and therefore obtained a {@code score}.
 * 
 * <p> A course may be verified in two ways:
 * <ol>
 *  <li> Those that are initially provided to the {@link ModuleMemory} from {@link PrePortal}
 *       are automatically "verified" set </li>
 *  <li> Those that are put into the tables by the user can be verified
 *       ({@code checked-out}) in the Portal through {@link ModuleHandler} </li>
 * </ol>
 * </p>
 * 
 * <p> It should be noted that only the "verified-courses" are analyzed,
 * and only them are printed on the transcript. This limits forgery.
 * </p>
 * 
 * @see ModuleHandler
 * @see ModuleMemory
 */
public class Course extends Module {
    private double score;


    /**
     * 
     */
    public Course(String year, String semester, String code, String name, String lecturer, String campus, String room,
                  String day, String time, double score, int creditHours, String requirement, boolean verified) {
        super(code, name, year, semester, lecturer, campus, room, day, time, requirement, creditHours, verified);
        this.score = score;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public String getGrade() {
        return grade(score);
    }

    public String getGradeComment() {
        return gradeComment(score);
    }

    public double getQualityPoint() {
        return gradePoints(getGrade());
    }

    /**
     * Merges this course with the outgoing, old course.
     * This ensures that the user's given details,
     * prior to editing or verification are not lost.
     * By the time this method returns, it's safe to substitute old with this.
     */
    @Override
    public void merge(Module old) {
        this.day = old.day;
        this.time = old.time;
        this.campus = old.campus;
        this.room = old.room;
        this.requirement = old.requirement;
        if (this.isLecturerEditable) {
            this.lecturer = old.lecturer;
        }
    }

    /**
     * Exports the contents of this course to a line-separated value text.
     * During build, a course will be reconstructed with these lines.
     * 
     * E.g:
     * 2016/2017
     * First Semester
     * MTH002
     * Calculus 1
     * Amadou Keita
     * ...
     *
     * @see #create(String)
     */
    @Override
    public String export(){
        return Globals.joinLines(new Object[]{year, semester, code, name, lecturer, campus, room, day,
                time, score, creditHours, requirement, isConfirmed, isLecturerEditable});
    }

    /**
     * Creates a course whose export() was these data.
     * Exceptions throwable by this operation must be handled with
     * great care across implementations.
     * 
     * @see #exportContent()
     */
    public static Course create(String data) {
        final String[] lines = Globals.splitLines(data);
        final Course course = new Course(lines[0], lines[1], lines[2], lines[3], lines[4],
                lines[5], lines[6], lines[7], lines[8], Double.parseDouble(lines[9]),
                Integer.parseInt(lines[10]), lines[11], Boolean.parseBoolean(lines[12]));
        course.isLecturerEditable = Boolean.parseBoolean(lines[13]);
        return course;
    }

    @Override
    public void exhibit(Component base){
        final KDialog exhibitor = new KDialog(this.name);
        if (this.isMisc()) {
            exhibitor.setTitle(exhibitor.getTitle()+" - Miscellaneous");
        }
        exhibitor.setResizable(true);
        exhibitor.setModalityType(KDialog.DEFAULT_MODALITY_TYPE);

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

        final KPanel yearPanel = new KPanel(new BorderLayout());
        yearPanel.add(new KPanel(new KLabel("Academic Year:", hintFont)), BorderLayout.WEST);
        yearPanel.add(new KPanel(new KLabel(this.year, valueFont)), BorderLayout.CENTER);

        final KPanel semesterPanel = new KPanel(new BorderLayout());
        semesterPanel.add(new KPanel(new KLabel("Semester:", hintFont)), BorderLayout.WEST);
        semesterPanel.add(new KPanel(new KLabel(this.semester,valueFont)), BorderLayout.CENTER);

        final KPanel typePanel = new KPanel(new BorderLayout());
        typePanel.add(new KPanel(new KLabel("Requirement:", hintFont)), BorderLayout.WEST);
        typePanel.add(new KPanel(new KLabel(this.requirement, valueFont)), BorderLayout.CENTER);

        final KPanel schedulePanel = new KPanel(new BorderLayout());
        schedulePanel.add(new KPanel(new KLabel("Schedule:", hintFont)), BorderLayout.WEST);
        schedulePanel.add(new KPanel(new KLabel(this.getSchedule(), valueFont)), BorderLayout.CENTER);

        final KPanel venuePanel = new KPanel(new BorderLayout());
        venuePanel.add(new KPanel(new KLabel("Venue:", hintFont)), BorderLayout.WEST);
        venuePanel.add(new KPanel(new KLabel(this.getVenue(), valueFont)), BorderLayout.CENTER);

        final KPanel creditPanel = new KPanel(new BorderLayout());
        creditPanel.add(new KPanel(new KLabel("Credit Hours:", hintFont)), BorderLayout.WEST);
        creditPanel.add(new KPanel(new KLabel(Integer.toString(this.creditHours), valueFont)), BorderLayout.CENTER);

        final KPanel scorePanel = new KPanel(new BorderLayout());
        scorePanel.add(new KPanel(new KLabel("Final Score:", hintFont)), BorderLayout.WEST);
        scorePanel.add(new KPanel(new KLabel(Double.toString(this.score), valueFont)), BorderLayout.CENTER);

        final KPanel gradePanel = new KPanel(new BorderLayout());
        gradePanel.add(new KPanel(new KLabel("Grade:", hintFont)), BorderLayout.WEST);
        gradePanel.add(new KPanel(new KLabel(this.getGrade()+"  ("+this.getGradeComment()+")", valueFont)), BorderLayout.CENTER);

        final KPanel gradeValuePanel = new KPanel(new BorderLayout());
        gradeValuePanel.add(new KPanel(new KLabel("Grade Value:", hintFont)), BorderLayout.WEST);
        gradeValuePanel.add(new KPanel(new KLabel(Double.toString(this.getQualityPoint()), valueFont)), BorderLayout.CENTER);

        final KLabel statusLabel = new KLabel(this.status, valueFont, this.status.equals(CONFIRMED) ? Color.BLUE :
                this.status.equals(Globals.UNKNOWN) ? Color.RED : Color.GRAY);
        final KPanel statusPanel = new KPanel(new BorderLayout());
        statusPanel.add(new KPanel(new KLabel("Status:", hintFont)), BorderLayout.WEST);
        statusPanel.add(new KPanel(statusLabel), BorderLayout.CENTER);

        final KButton closeButton = new KButton("Close");
        closeButton.addActionListener(e -> exhibitor.dispose());

        final KPanel contentPanel = new KPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.addAll(codePanel, namePanel, lectPanel, yearPanel, semesterPanel, schedulePanel, venuePanel,
                typePanel, creditPanel, scorePanel, gradePanel, gradeValuePanel, statusPanel,
                MComponent.contentBottomGap(), new KPanel(closeButton));
        exhibitor.getRootPane().setDefaultButton(closeButton);
        exhibitor.setContentPane(contentPanel);
        exhibitor.pack();
        exhibitor.setLocationRelativeTo(base);
        SwingUtilities.invokeLater(()-> exhibitor.setVisible(true));
    }

}
