package core.module;

import java.awt.*;

import core.Board;
import core.user.Student;
import core.utils.Globals;

/**
 * Module is meant to be the super type of all courses.
 */
public abstract class Module {
    String code;
    String name;
    String year;
    String semester;
    String lecturer;
    boolean isLecturerEditable;
    String campus;
    String room;
    String day;
    String time;
    String requirement;
    int creditHours;
    boolean isConfirmed;
    String status;
    // Status options
    public static final String CONFIRMED = "Confirmed";  // Verified
    public static final String VERIFYING = "Verifying...";  // Confirming...
    public static final String UNKNOWN = Globals.UNKNOWN;
    // Requirement options
    public static final String MAJOR_OBLIGATORY = "Major Obligatory";
    public static final String MAJOR_OPTIONAL = "Major Elective";
    public static final String MINOR_OBLIGATORY = "Minor Obligatory";
    public static final String MINOR_OPTIONAL = "Minor Elective";
    public static final String DIVISIONAL_REQUIREMENT = "Divisional Requirement";
    public static final String GENERAL_REQUIREMENT = "General Requirement";
    public static final String NONE = Globals.NONE;
    // Known divisional codes
    public static final String DER = "DER";
    public static final String GER = "GER";
    // 
    public static final String OTHER = Globals.OTHER;


    /**
     * Constructs a Module - complete and initialized - with the given credentials.
     * 
     * @param code
     * @param name
     * @param year
     * @param semester
     * @param lecturer
     * @param campus  Default: {@code Other}
     * @param room
     * @param day  Default: {@code Unknown}
     * @param time  Default: {@code Unknown}
     * @param requirement  Default: {@code None}
     * @param creditHours
     * @param isConfirmed
     */
    public Module(String code, String name, String year, String semester, String lecturer,
                String campus, String room, String day, String time, String requirement,
                int creditHours, boolean isConfirmed) {
        this.code = code;
        this.name = name;
        this.year = year;
        this.semester = semester;
        this.lecturer = lecturer;
        this.isLecturerEditable = true;
        this.campus = Globals.hasText(campus) ? campus : OTHER;
        this.room = room;
        this.day = Globals.hasText(day) ? day : UNKNOWN;
        this.time = Globals.hasText(time) ? time : UNKNOWN;
        this.requirement = Globals.hasText(requirement) ? requirement : NONE;
        this.creditHours = creditHours;
        this.isConfirmed = isConfirmed;
        this.status = isConfirmed ? CONFIRMED : UNKNOWN;
        if (this.requirement.equals(NONE)) {
            try {
                final String programPart = code.substring(0, 3);
                if (programPart.equals(Student.getMajorCode())) {
                    this.requirement = MAJOR_OBLIGATORY;
                } else if (programPart.equals(Student.getMinorCode())) {
                    this.requirement = MINOR_OBLIGATORY;
                } else if (programPart.equals(DER)) {
                    this.requirement = DIVISIONAL_REQUIREMENT;
                } else if (programPart.equals(GER)) {
                    this.requirement = GENERAL_REQUIREMENT;
                }
            } catch (IndexOutOfBoundsException ignored){
            }
        }
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

    public String getLecturer() {
        return lecturer;
    }

    public void setLecturer(String lecturer) {
        this.lecturer = lecturer;
    }

    public String getCampus() {
        return campus;
    }

    public void setCampus(String campus) {
        this.campus = campus;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
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

    public String getRequirement() {
        return requirement;
    }

    public void setRequirement(String requirement) {
        this.requirement = requirement;
    }

    public int getCreditHours() {
        return creditHours;
    }

    public void setCreditHours(int creditHours) {
        this.creditHours = creditHours;
    }

    public boolean isConfirmed() {
        return isConfirmed;
    }

    public void setConfirmed(boolean isConfirmed) {
        this.isConfirmed = isConfirmed;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isLecturerEditable() {
        return isLecturerEditable;
    }

    public void setLecturerEditable(boolean isLecturerEditable) {
        this.isLecturerEditable = isLecturerEditable;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }            

        final Module other = (Module) obj;
        if (code == null) {
            if (other.code != null) {
                return false;
            }
        } else if (!code.equals(other.code)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((code == null) ? 0 : code.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return String.format("Module (Code=%s Name=%s Year=%s Semester=%s Lecturer=%s Requirement=%s "+
                "Confirmed=%s )", code, name, year, semester, lecturer, requirement, isConfirmed);
    }

    // Field assistants...

    public String getAbsoluteName() {
        return String.format("%s (%s)", name, code);
    }

    public String getAbsoluteSemesterName(){
        return String.join(" ", year, semester);
    }

    public String getSchedule(){
        return schedule(this);
    }

    public String getVenue(){
        return venue(this);
    }

    /**
     * Returns true if this course is generally a major course; false otherwise.
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
     * @see #isMajor()
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
     * Returns true if the course is Misc.; false otherwise.
     * A course is marked miscellaneous if its `year` falls out
     * of the student's four years bachelor's program specification.
     */
    public boolean isMisc() {
        return !(year.equals(Student.firstAcademicYear()) || year.equals(Student.secondAcademicYear()) ||
        year.equals(Student.thirdAcademicYear()) || year.equals(Student.fourthAcademicYear()));
    }

    // Static calls...

    /**
     * Generates and return the schedule of the given module.
     * Remember: schedule is the combination of the day and time.
     * @param module
     * @return
     */
    public static String schedule(Module module) {
        final String day = module.day;
        final String time = module.time;
        if (day.equals(UNKNOWN)) {
            if (time.equals(UNKNOWN)) {
                return "";
            } else {
                return time+" - Unknown Day";
            }
        } else {
            if (time.equals(UNKNOWN)) {
                return day+" - Unknown Time";
            } else {
                return day+" - "+time;
            }
        }
    }

    /**
     * Generates and return the venue, which is the combination of the
     * campus and room.
     * @param module
     * @return
     */
    public static String venue(Module module) {
        final String campus = module.campus;
        final String room = module.room;
        if (campus.equals(OTHER)) {
            if (Globals.hasText(room)) {
                return room;
            } else {
                return "";
            }
        } else {
            if (Globals.hasText(room)) {
                return campus+" - "+room;
            } else {
                return campus+" - Unknown Room";
            }
        }
    }

    /**
     * Returns the grade of a module based on its score - the given score.
     * <b> Method must stay updated and in-line with UTG Grading System. </b>
     * @param score
     */
    public static String grade(double score) {
        String grade = "F";  // 0-39, 0
        if (score > 39 && score < 50) {
            grade = "D";  // 40-49, 1
        } else if (score > 49 && score < 54) {
            grade = "C-";  // 50-53, 1.7
        } else if (score > 53 && score < 57) {
            grade = "C"; // 54-56, 2
        } else if (score > 56 && score < 60) {
            grade = "C+";  // 57-59, 2.3
        } else if (score > 59 && score < 64) {
            grade = "B-";  // 60-63, 2.7
        } else if (score > 63 && score < 67) {
            grade = "B";  // 64-66, 3
        } else if (score > 66 && score < 70) {
            grade = "B+";  // 67-69, 3.3
        } else if (score > 69 && score < 80) {
            grade = "A-";  // 70-79, 3.7
        } else if (score > 79 && score < 90) {
            grade = "A";  // 80-89, 4
        } else if (score > 89 && score < 101) {
            grade = "A+";  // 90-100, 4.3
        }
        return grade;
    }

    /**
     * Returns the appropriate points (grade-value) for a module
     * based on its grade - the given grade.
     * <b> Method must stay updated and in-line with UTG Grading System. </b>
     * @param grade
     * @see #grade(double)
     */
    public static double gradePoints(String grade){
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
     * Assigns a comment to the given score. E.g "Excellent", "Good", "Fail".
     * <b> Method must stay updated and in-line with UTG Grading System. </b>
     */
    public static String gradeComment(double score){
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
     * Returns an array of times most, if not all, lectures are conducted in UTG.
     * All time boxes must delegate to this as their list of time options.
     */
    public static String[] periods(){
        return new String[]{"08:00", "08:30", "09:00", "11:00", "11:30", "14:00", "14:30", "15:00",
                "17:00", "17:30", "20:00", UNKNOWN};
    }

    /**
     * Returns an array of the days of a week.
     * All day boxes must delegate to this as their list of day options.
     */
    public static String[] weekDays(){
        return new String[]{"Monday", "Tuesday", "Wednesday", "Thursday", "Friday",
                "Saturday", "Sunday", UNKNOWN};
    }

    /**
     * Returns an array of the available requirement options.
     * All requirement boxes must delegate to this as their list of requirement options.
     */
    public static String[] requirements(){
        return new String[]{MAJOR_OBLIGATORY, MAJOR_OPTIONAL, MINOR_OBLIGATORY, MINOR_OPTIONAL,
                DIVISIONAL_REQUIREMENT, GENERAL_REQUIREMENT, NONE};
    }

    public static String[] creditHours(){
        return new String[]{"3", "4"};
    }

    public static String[] campuses(){
        return new String[]{"Brikama", "Faraba", "Kanifing", "Banjul", "GTTI", "Online", Globals.OTHER};
    }

    // Abstractions...

    public abstract String export();

    public void merge(Module old){
        this.day = old.day;
        this.time = old.time;
        this.requirement = old.requirement;
        if (this.isLecturerEditable) {
            this.lecturer = old.lecturer;
        }
    }


    /**
     * Exhibits the contents of this Module on a dialog,
     * placed on the given base component.
     */
    public abstract void exhibit(Component base);

    public void exhibit(){
        exhibit(Board.getRoot());
    }

}
