package core;

import proto.*;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * The Course type models a course or a module as often referred to as.
 * It has a single, generalized constructor, passing all the fundamental attributes
 * at that very instance of creation.
 * A course may be verified in two ways:
 * 1) those that are provided to the {@link Memory} from {@link PrePortal} through
 * {@link ModuleHandler#STARTUP_COURSES} are automatically "verified" set;
 * 2) those that are put into the tables by the user can be verified
 * (checked-out) in the Portal {@link ModuleHandler}.
 * It should be noted that only verified-courses are analyzed,
 * and only them are printed by the transcript. This limits forgery.
 * The Course type has many collaborators.
 * @see ModuleHandler
 * @see ModuleHandler#launchVerification(Course)
 * @see SummerModule
 * @see MiscModule
 * @see Memory
 */
public class Course {
//    This order should remain religious for backward-compatibility and deserialization sake.
    private String year;
    private String semester;
    private String code;
    private String name;
    private String lecturer;
    private String venue;
    private String day;
    private String time;
    private double score;
    private int creditHours;
    private String requirement;
    private boolean isVerified;
    private boolean lecturerNameChangeability;
//    Requirement options
    public static final String MAJOR_OBLIGATORY = "Major Obligatory";
    public static final String MINOR_OBLIGATORY = "Minor Obligatory";
    public static final String MAJOR_OPTIONAL = "Major Elective";
    public static final String MINOR_OPTIONAL = "Minor Elective";
    public static final String DIVISIONAL_REQUIREMENT = "Divisional Requirement";
    public static final String GENERAL_REQUIREMENT = "General Requirement";
    public static final String NONE = Globals.NONE;
//    Known divisional codes
    public static final String DER = "DER";
    public static final String GER = "GER";
//    The unknown constant
    public static final String UNKNOWN = Globals.UNKNOWN;


    /**
     * Constructs a module, complete and initialized, with the given credentials.
     * Instead of the empty-string, the default value of the day and time is the
     * unknown-constant; the requirement is the none-constant.
     * This helps with the combo-boxes during editing of the modules.
     * Notice, if no explicit requirement is given, the constructor will
     * attempt to assign it a requirement.
     */
    public Course(String year, String semester, String code, String name, String tutor, String venue, String day,
                  String time, double score, int creditHours, String requirement, boolean verified) {
        this.year = year;
        this.semester = semester;
        this.code = code;
        this.name = name;
        this.lecturer = tutor;
        this.venue = venue;
        this.day = Globals.hasNoText(day) || day.equals(UNKNOWN) ? "" : day;
        this.time = Globals.hasNoText(time) || time.equals(UNKNOWN) ? "" : time;
        this.score = score;
        this.creditHours = creditHours;
        this.isVerified = verified;
        this.lecturerNameChangeability = !verified;
        this.requirement = Globals.hasText(requirement) ? requirement : NONE;
        if (this.requirement.equals(NONE)) {
            try {
                final String programPart = code.substring(0, 3);
                if (programPart.equals(Student.getMajorCode())) {
                    setRequirement(MAJOR_OBLIGATORY);
                } else if (programPart.equals(Student.getMinorCode())) {
                    setRequirement(MINOR_OBLIGATORY);
                } else if (programPart.equals(DER)) {
                    setRequirement(DIVISIONAL_REQUIREMENT);
                } else if (programPart.equals(GER)) {
                    setRequirement(GENERAL_REQUIREMENT);
                }
            } catch (StringIndexOutOfBoundsException ignored){
            }
        }
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLecturer() {
        return lecturer;
    }

    public void setLecturer(String lecturer, boolean changeable) {
        this.lecturer = lecturer;
        lecturerNameChangeability = changeable;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public int getCreditHours() {
        return creditHours;
    }

    public void setCreditHours(int creditHours) {
        this.creditHours = creditHours;
    }

    public String getRequirement() {
        return requirement;
    }

    /**
     * Sets the requirement of this course.
     * The given requirement must be a magic-constant, and any of those
     * defined herein this class.
     */
    public void setRequirement(String requirement) {
        this.requirement = requirement;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public void setVerified(boolean verified) {
        this.isVerified = verified;
    }

    /**
     * Returns a compound-string of the code and name of this course.
     */
    public String getAbsoluteName() {
        return String.join(" ", code, name);
    }

    /**
     * Returns a compound-string of the year and semester of this instance.
     * This is useful, especially, in comparing if courses were done in the same semester.
     */
    public String getAbsoluteSemesterName(){
        return String.join(" ", year, semester);
    }

    public boolean isFirstSemester(){
        return semester.equals(Student.FIRST_SEMESTER);
    }

    public boolean isSecondSemester(){
        return semester.equals(Student.SECOND_SEMESTER);
    }

    public boolean isSummerSemester(){
        return semester.equals(Student.SUMMER_SEMESTER);
    }

    public boolean isFirstYear(){
        return year.equals(Student.firstAcademicYear());
    }

    public boolean isSecondYear(){
        return year.equals(Student.secondAcademicYear());
    }

    public boolean isThirdYear(){
        return year.equals(Student.thirdAcademicYear());
    }

    public boolean isFourthYear(){
        return year.equals(Student.fourthAcademicYear());
    }

    /**
     * A course is marked miscellaneous if its year falls out of the student's
     * four years bachelor's program specification.
     */
    public boolean isMisc() {
        final String y = year;
        return !(y.equals(Student.firstAcademicYear()) || y.equals(Student.secondAcademicYear()) ||
                y.equals(Student.thirdAcademicYear()) || y.equals(Student.fourthAcademicYear()));
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
     * Returns true if this course is a major course; false otherwise.
     * A module is considered major if its requirement contains "major".
     * So it might be compulsory, or not.
     * @see #isMajorObligatory()
     * @see #isMajorElective()
     */
    public boolean isMajor() {
        return requirement.contains("Major");
    }

    /**
     * Returns true if this module has its requirement exactly
     * equals to {@link #MAJOR_OBLIGATORY}; false otherwise.
     */
    public boolean isMajorObligatory() {
        return requirement.equals(MAJOR_OBLIGATORY);
    }

    /**
     * Returns true if this module has its requirement exactly
     * equals to {@link #MAJOR_OPTIONAL}; false otherwise.
     */
    public boolean isMajorElective() {
        return requirement.equals(MAJOR_OPTIONAL);
    }

    /**
     * Returns true if this course is a minor course; false otherwise.
     * A module is considered minor if its requirement contains "minor".
     * So it might be compulsory, or not.
     * @see #isMinorObligatory()
     * @see #isMinorElective()
     */
    public boolean isMinor() {
        return requirement.contains("Minor");
    }

    /**
     * Returns true if this module has its requirement exactly
     * equals to {@link #MINOR_OBLIGATORY}; false otherwise.
     */
    public boolean isMinorObligatory() {
        return requirement.equals(MINOR_OBLIGATORY);
    }

    /**
     * Returns true if this module has its requirement exactly
     * equals to {@link #MINOR_OPTIONAL}; false otherwise.
     */
    public boolean isMinorElective() {
        return requirement.equals(MINOR_OPTIONAL);
    }

    public boolean isDivisional() {
        return requirement.equals(DIVISIONAL_REQUIREMENT);
    }

    public boolean isGeneral() {
        return requirement.equals(GENERAL_REQUIREMENT);
    }

    public boolean isUnclassified() {
        return requirement.equals(NONE);
    }

    public String getGrade() {
        return gradeOf(score);
    }

    public String getGradeComment() {
        return gradeCommentOf(score);
    }

    public double getQualityPoint() {
        return pointsOf(getGrade());
    }

    /**
     * A lecturer's name of a module is changeable
     * iff it was not actually found on the Portal.
     * Note that courses done before the implementation of the Portal
     * do not have their lecturer names uploaded afterwards.
     */
    public boolean isLecturerNameEditable() {
        return Globals.hasNoText(lecturer) || lecturerNameChangeability;
    }

    /**
     * Returns the list-index of this course.
     * This is useful for substitution, editing.
     * @see ModuleHandler
     */
    public int getListIndex() {
        final List<Course> monitor = ModuleHandler.getMonitor();
        for (int i = 0; i < monitor.size(); i++) {
            if (code.equals(monitor.get(i).code)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns a grade based on the given score.
     * This must stay updated and in-line with UTG grading system.
     */
    private static String gradeOf(double score) {
        String grade = "F";//0-39, 0
        if (score > 39 && score < 50) {
            grade = "D";//40-49, 1
        } else if (score > 49 && score < 54) {
            grade = "C-";//50-53, 1.7
        } else if (score > 53 && score < 57) {
            grade = "C";//54-56, 2
        } else if (score > 56 && score < 60) {
            grade = "C+";//57-59, 2.3
        } else if (score > 59 && score < 64) {
            grade = "B-";//60-63, 2.7
        } else if (score > 63 && score < 67) {
            grade = "B";//64-66, 3
        } else if (score > 66 && score < 70) {
            grade = "B+";//67-69, 3.3
        } else if (score > 69 && score < 80) {
            grade = "A-";//70-79, 3.7
        } else if (score > 79 && score < 90) {
            grade = "A";//80-89, 4
        } else if (score > 89 && score < 101) {
            grade = "A+";//90-100, 4.3
        }
        return grade;
    }

    /**
     * Returns the appropriate points (grade-value) for the given grade.
     * This must stay updated and in-line with UTG grading system.
     * @see #gradeOf(double)
     */
    private static double pointsOf(String grade){
        switch (grade) {
            case "D":
                return  1;
            case "C-":
                return 1.7;
            case "C":
                return 2;
            case "C+":
                return 2.3;
            case "B-":
                return 2.7;
            case "B":
                return 3;
            case "B+":
                return 3.3;
            case "A-":
                return 3.7;
            case "A":
                return 4;
            case "A+":
                return 4.3;
            default:
                return 0;
        }
    }

    /**
     * Assigns a comment to the grade / score. E.g "Excellent", "Fails", etc.
     * This must stay updated and in-line with UTG grading system.
     */
    private static String gradeCommentOf(double score){
        if (score >= 70) {
            return "Excellent";
        } else if (score >= 60) {
            return "Good";
        } else if (score >= 50) {
            return "Satisfactory";
        } else if (score >= 40) {
            return "Marginal Pass";
        } else {
            return "Fail";
        }
    }

    /**
     * Exports the contents of this course to a line-separated value text.
     * During build, a course will be reconstructed with these lines.
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
    public String exportContent(){
        return year + "\n" +
                semester + "\n" +
                code + "\n" +
                name + "\n" +
                lecturer + "\n" +
                venue + "\n" +
                day + "\n" +
                time + "\n" +
                score + "\n" +
                creditHours + "\n" +
                requirement + "\n" +
                isVerified + "\n" +
                lecturerNameChangeability;
    }

    /**
     * Creates a course whose exportContent() was this dataLines.
     * Exceptions throwable by this operation must be handled with great care across implementations.
     * @see #exportContent()
     */
    public static Course create(String dataLines) {
        final String[] data = dataLines.split("\n");
        double score = 0;
        try {
            score = Double.parseDouble(data[8]);
        } catch (Exception e) {
            App.silenceException("Error reading score of "+data[3]);
        }
        int creditsHours = 3;
        try {
            creditsHours = Integer.parseInt(data[9]);
        } catch (Exception e) {
            App.silenceException("Error reading credit hours of "+data[3]);
        }

        final Course serialCourse = new Course(data[0], data[1], data[2], data[3], data[4], data[5], data[6], data[7],
                score, creditsHours, data[10], Boolean.parseBoolean(data[11]));
        serialCourse.lecturerNameChangeability = Boolean.parseBoolean(data[12]);
        return serialCourse;
    }

    /**
     * Merges the incoming course with the outgoing course.
     * This ensures that the user's given details of the outgoing,
     * prior to verification, are not lost.
     * By the time this method returns, it's safe to substitute outgoing with incoming.
     */
    public static void merge(Course incoming, Course outgoing) {
        incoming.setDay(outgoing.day);
        incoming.setTime(outgoing.time);
        incoming.setVenue(outgoing.venue);
        incoming.setRequirement(outgoing.requirement);
        if (incoming.isLecturerNameEditable()) {
            incoming.setLecturer(outgoing.getLecturer(), true);
        }
    }

    /**
     * Returns an array  of times most, if not all, lectures are conducted in UTG.
     * All time boxes must delegate to this as their list of time options.
     */
    public static String[] getCoursePeriods(){
        return new String[]{UNKNOWN, "8:00", "8:30", "9:00", "11:00", "11:30", "14:00", "14:30", "15:00",
                "17:00", "17:30", "20:00"};
    }

    /**
     * Returns an array of the days of a week.
     * All day boxes must delegate to this as their list of day options.
     */
    public static String[] getWeekDays(){
        return new String[]{UNKNOWN, "Mondays", "Tuesdays", "Wednesdays", "Thursdays", "Fridays",
                "Saturdays", "Sundays"};
    }

    /**
     * Returns an array of the available requirement options.
     * All requirement boxes must delegate to this as their list of requirement options.
     */
    public static String[] getRequirements(){
        return new String[]{MAJOR_OBLIGATORY, MAJOR_OPTIONAL, MINOR_OBLIGATORY, MINOR_OPTIONAL,
                DIVISIONAL_REQUIREMENT, GENERAL_REQUIREMENT, NONE};
    }

    public static String[] creditHours(){
        return new String[]{"3", "4"};
    }

    /**
     * Exhibits the contents of the given course on a dialog,
     * placed on the given base component.
     * If the course is null, nothing is done; returns immediately.
     * Do not call this with {@link SwingUtilities#invokeLater(Runnable)},
     * {@link EventQueue#invokeLater(Runnable)}, etc.
     */
    public static void exhibit(Component base, Course course){
        if (course == null) {
            return;
        }

        final KDialog dialog = new KDialog(course.name);
        if (course.isMisc()) {
            dialog.setTitle(dialog.getTitle()+" - Miscellaneous");
        }
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

        final KPanel yearPanel = new KPanel(new BorderLayout());
        yearPanel.add(new KPanel(new KLabel("Academic Year:", hintFont)), BorderLayout.WEST);
        yearPanel.add(new KPanel(new KLabel(course.year, valueFont)), BorderLayout.CENTER);

        final KPanel semesterPanel = new KPanel(new BorderLayout());
        semesterPanel.add(new KPanel(new KLabel("Semester:", hintFont)), BorderLayout.WEST);
        semesterPanel.add(new KPanel(new KLabel(course.semester,valueFont)), BorderLayout.CENTER);

        final KPanel typePanel = new KPanel(new BorderLayout());
        typePanel.add(new KPanel(new KLabel("Requirement:", hintFont)), BorderLayout.WEST);
        typePanel.add(new KPanel(new KLabel(course.requirement, valueFont)), BorderLayout.CENTER);

        final KPanel schedulePanel = new KPanel(new BorderLayout());
        schedulePanel.add(new KPanel(new KLabel("Schedule:", hintFont)), BorderLayout.WEST);
        schedulePanel.add(new KPanel(new KLabel(course.getSchedule(), valueFont)), BorderLayout.CENTER);

        final KPanel venuePanel = new KPanel(new BorderLayout());
        venuePanel.add(new KPanel(new KLabel("Venue:", hintFont)), BorderLayout.WEST);
        venuePanel.add(new KPanel(new KLabel(course.venue, valueFont)), BorderLayout.CENTER);

        final KPanel creditPanel = new KPanel(new BorderLayout());
        creditPanel.add(new KPanel(new KLabel("Credit Hours:", hintFont)), BorderLayout.WEST);
        creditPanel.add(new KPanel(new KLabel(Integer.toString(course.creditHours), valueFont)), BorderLayout.CENTER);

        final KPanel scorePanel = new KPanel(new BorderLayout());
        scorePanel.add(new KPanel(new KLabel("Final Score:", hintFont)), BorderLayout.WEST);
        scorePanel.add(new KPanel(new KLabel(Double.toString(course.score), valueFont)), BorderLayout.CENTER);

        final KPanel gradePanel = new KPanel(new BorderLayout());
        gradePanel.add(new KPanel(new KLabel("Grade:", hintFont)), BorderLayout.WEST);
        gradePanel.add(new KPanel(new KLabel(course.getGrade()+"  ("+course.getGradeComment()+")", valueFont)), BorderLayout.CENTER);

        final KPanel gradeValuePanel = new KPanel(new BorderLayout());
        gradeValuePanel.add(new KPanel(new KLabel("Grade Value:", hintFont)), BorderLayout.WEST);
        gradeValuePanel.add(new KPanel(new KLabel(Double.toString(course.getQualityPoint()), valueFont)), BorderLayout.CENTER);

        final KPanel statusPanel = new KPanel(new BorderLayout());
        statusPanel.add(new KPanel(new KLabel("Status:", hintFont)), BorderLayout.WEST);
        final KLabel vLabel = course.isVerified ? new KLabel("Confirmed", valueFont, Color.BLUE) :
                new KLabel("Unknown", valueFont, Color.RED);
        statusPanel.add(new KPanel(vLabel), BorderLayout.CENTER);

        final KButton closeButton = new KButton("Close");
        closeButton.addActionListener(e -> dialog.dispose());

        final KPanel contentPanel = new KPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.addAll(codePanel, namePanel, lectPanel, yearPanel, semesterPanel, schedulePanel, venuePanel,
                typePanel, creditPanel, scorePanel, gradePanel, gradeValuePanel, statusPanel,
                MComponent.contentBottomGap(), new KPanel(new FlowLayout(FlowLayout.RIGHT), closeButton));
        dialog.getRootPane().setDefaultButton(closeButton);
        dialog.setContentPane(contentPanel);
        dialog.pack();
        dialog.setLocationRelativeTo(base == null ? Board.getRoot() : base);
        SwingUtilities.invokeLater(()-> dialog.setVisible(true));
    }

    /**
     * Exhibits the contents of the given course on the Dashboard's instance.
     * @see #exhibit(Component, Course)
     */
    public static void exhibit(Course c){
        exhibit(null, c);
    }

}
