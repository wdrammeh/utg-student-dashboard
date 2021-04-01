package core.module;

import core.transcript.TranscriptActivity;
import core.transcript.TranscriptExporter;
import core.user.Analysis;
import core.user.Student;
import core.utils.Globals;

import java.util.ArrayList;

/**
 * This is the backbone of analysis, and transcript.
 * Among the things it does is to keep track of verified courses
 * to be supplied to such types.
 * @see Analysis
 * @see TranscriptActivity
 * @see TranscriptExporter
 */
public class Memory {
    /**
     * Keeps track of the verified-modules.
     * There should be no chance of adding unverified modules.
     * Accessors of this list - noticeably, the Transcript and Analysis -
     * should refresh calls prior to any activity-answer.
     * This list must remain updated by the monitor in ModuleHandler.
     * Never directly add or withdraw from this list - all such must be directed
     * by the monitor.
     */
    private static final ArrayList<Course> VERIFIED_LIST = new ArrayList<Course>() {
        @Override
        public boolean add(Course course) {
            return course.isVerified() && super.add(course);
        }
    };

    /**
     * Attempts to add the given course to the verified list.
     * If the course is not verified, nothing is done;
     * otherwise, if successful, it is sent to the transcript right away.
     */
    public static void add(Course c){
        if (VERIFIED_LIST.add(c)) {
            TranscriptActivity.TRANSCRIPT_MODEL.addRow(new String[] {c.getCode(), c.getName(),
                    Integer.toString(c.getCreditHours()), c.getGrade(), Double.toString(c.getQualityPoint())});
        }
    }

    /**
     * Attempts to remove the given course from the verified list.
     */
    public static void remove(Course course){
        if (VERIFIED_LIST.remove(course)) {
            TranscriptActivity.TRANSCRIPT_MODEL.removeRow(TranscriptActivity.TRANSCRIPT_MODEL.getRowOf(course.getCode()));
        }
    }

    /**
     * Attempts to replace the outgoing course with the incoming.
     * Nothing is done if the outgoing course cannot be located
     * in the list.
     */
    public static void replace(Course outgoing, Course incoming){
        final int i = indexOf(outgoing.getCode());
        if (i >= 0) {
            VERIFIED_LIST.set(i, incoming);
        }
    }

    /**
     * Returns the index, from the list, of the first course found with
     * the given code.
     */
    public static int indexOf(String code){
        for (int i = 0; i < VERIFIED_LIST.size(); i++) {
            if (VERIFIED_LIST.get(i).getCode().equalsIgnoreCase(code)) {
                return i;
            }
        }
        return -1;
    }

    public static ArrayList<Course> getList(){
        return VERIFIED_LIST;
    }

    /**
     * Returns a course purposely for experimentation by this class.
     * This is mostly used by the tracer functions, and the convention
     * is that they may not return it however.
     */
    private static Course newBlankModule(){
        return new Course("", "", "", "", "", "", "", "",
                0D, 0, "", true);
    }

    /**
     * Returns a filtered list of all the academic years of the student
     * as determined by the verified courses he/she did.
     * Returns an empty list if the {@link #VERIFIED_LIST} is empty.
     */
    public static ArrayList<String> getAcademicYears(){
        final ArrayList<String> list = new ArrayList<>();
        for (Course c : VERIFIED_LIST) {
            if (!list.contains(c.getYear())) {
                list.add(c.getYear());
            }
        }
        return list;
    }

    /**
     * Returns a filtered list of all the semesters the student
     * has undergone.
     * A single academic year may have up to three semesters -
     * first, second, and, or summer.
     * This method fetches the distinct fully-qualified semester names.
     * Returns an empty list if the {@link #VERIFIED_LIST} is empty.
     */
    public static ArrayList<String> getSemesters(){
        final ArrayList<String> list = new ArrayList<>();
        for (String yearName : getAcademicYears()) {
            for (Course course : VERIFIED_LIST) {
                if (course.isFirstSemester() && course.getYear().equals(yearName)) {
                    list.add(yearName+" "+ Student.FIRST_SEMESTER);
                    break;
                }
            }
            for (Course course : VERIFIED_LIST) {
                if (course.isSecondSemester() && course.getYear().equals(yearName)) {
                    list.add(yearName+" "+Student.SECOND_SEMESTER);
                    break;
                }
            }
            for (Course course : VERIFIED_LIST) {
                if (course.isSummerSemester() && course.getYear().equals(yearName)) {
                    list.add(yearName+" "+Student.SUMMER_SEMESTER);
                    break;
                }
            }
        }
        return list;
    }

    /**
     * Returns a list of all the lecturers of the student as specified
     * by the courses he/she has done.
     * This method does not collect blank lecturer names,
     * as some courses might have.
     * Returns an empty list if the {@link #VERIFIED_LIST} is empty.
     * @see Globals#hasNoText(String)
     */
    public static ArrayList<String> getLecturers(){
        final ArrayList<String> list = new ArrayList<>();
        for (Course c : VERIFIED_LIST) {
            final String lecturer = c.getLecturer();
            if (!(list.contains(lecturer) || Globals.hasNoText(lecturer))) {
                list.add(lecturer);
            }
        }
        return list;
    }

//    Fractions - return a fraction of the list fitting a condition
//    Some are, of course, little bit more specific
//    They may return empty-lists, never null

    /**
     * Returns a list of modules each of which has its
     * grade exactly equal to the given grade.
     * Returns an empty list if the {@link #VERIFIED_LIST} is empty,
     * or has no modules satisfying that condition.
     */
    public static ArrayList<Course> getFractionByGrade(String grade){
        final ArrayList<Course> list = new ArrayList<>();
        for (Course c : VERIFIED_LIST) {
            if (c.getGrade().equals(grade)) {
                list.add(c);
            }
        }
        return list;
    }

    /**
     * Returns a list of all the major courses of the student.
     * Returns an empty list if the {@link #VERIFIED_LIST} is empty,
     * or has no modules satisfying that condition.
     * @see Course#isMajor()
     */
    public static ArrayList<Course> getMajors(){
        final ArrayList<Course> list = new ArrayList<>();
        for (Course c : VERIFIED_LIST) {
            if (c.isMajor()) {
                list.add(c);
            }
        }
        return list;
    }

    /**
     * Returns a list of the major courses the student has done in the
     * given semester.
     * Returns an empty list if the {@link #VERIFIED_LIST} is empty,
     * or has no modules satisfying that condition.
     * @see Course#isMajor()
     * @see #getFractionBySemester(String)
     */
    public static ArrayList<Course> getMajorsBySemester(String semester){
        final ArrayList<Course> list = new ArrayList<>();
        for (Course c : getFractionBySemester(semester)) {
            if (c.isMajor()) {
                list.add(c);
            }
        }
        return list;
    }

    /**
     * Returns a list of the major courses the student has done in the
     * given academic year.
     * Returns an empty list if the {@link #VERIFIED_LIST} is empty,
     * or has no modules satisfying that condition.
     * @see #getFractionByYear(String)
     * @see Course#isMajor()
     */
    public static ArrayList<Course> getMajorsByYear(String year){
        final ArrayList<Course> list = new ArrayList<>();
        for (Course c : getFractionByYear(year)) {
            if (c.isMajor()) {
                list.add(c);
            }
        }
        return list;
    }

    /**
     * Returns a list of all the minor courses (if any) the student has done.
     * Returns an empty list if the {@link #VERIFIED_LIST} is empty,
     * or has no modules satisfying that condition.
     * @see Course#isMinor()
     */
    public static ArrayList<Course> getMinors(){
        final ArrayList<Course> list = new ArrayList<>();
        for (Course c : VERIFIED_LIST) {
            if (c.isMinor()) {
                list.add(c);
            }
        }
        return list;
    }

    /**
     * Returns a list of the minor courses (if any) the student has done in the
     * given semester.
     * Returns an empty list if the {@link #VERIFIED_LIST} is empty,
     * or has no modules satisfying that condition.
     * @see Course#isMinor()
     * @see #getFractionBySemester(String)
     */
    public static ArrayList<Course> getMinorsBySemester(String semester){
        final ArrayList<Course> list = new ArrayList<>();
        for (Course c : getFractionBySemester(semester)) {
            if (c.isMinor()) {
                list.add(c);
            }
        }
        return list;
    }

    /**
     * Returns a list of the minor courses (if any) the student has done in the
     * given academic year.
     * Returns an empty list if the {@link #VERIFIED_LIST} is empty,
     * or has no modules satisfying that condition.
     * @see Course#isMinor()
     * @see #getFractionByYear(String)
     */
    public static ArrayList<Course> getMinorsByYear(String year){
        final ArrayList<Course> list = new ArrayList<>();
        for (Course c : getFractionByYear(year)) {
            if (c.isMinor()) {
                list.add(c);
            }
        }
        return list;
    }

    /**
     * Returns all the DERs (Divisional Educational Requirements)
     * the student has done.
     * Returns an empty list if the {@link #VERIFIED_LIST} is empty,
     * or has no modules satisfying that condition.
     * @see Course#isDivisional()
     */
    public static ArrayList<Course> getDERs(){
        final ArrayList<Course> list = new ArrayList<>();
        for (Course c : VERIFIED_LIST) {
            if (c.isDivisional()) {
                list.add(c);
            }
        }
        return list;
    }

    /**
     * Returns the DERs (Divisional Educational Requirements)
     * the student has done in the given semester.
     * Returns an empty list if the {@link #VERIFIED_LIST} is empty,
     * or has no modules satisfying that condition.
     * @see Course#isDivisional()
     * @see #getFractionBySemester(String)
     */
    public static ArrayList<Course> getDERsBySemester(String semester){
        final ArrayList<Course> list = new ArrayList<>();
        for (Course c : getFractionBySemester(semester)) {
            if (c.isDivisional()) {
                list.add(c);
            }
        }
        return list;
    }

    /**
     * Returns the DERs (Divisional Educational Requirements)
     * the student has done in the given academic year.
     * Returns an empty list if the {@link #VERIFIED_LIST} is empty,
     * or has no modules satisfying that condition.
     * @see Course#isDivisional()
     * @see #getFractionByYear(String)
     */
    public static ArrayList<Course> getDERsByYear(String year){
        final ArrayList<Course> list = new ArrayList<>();
        for (Course c : getFractionByYear(year)) {
            if (c.isDivisional()) {
                list.add(c);
            }
        }
        return list;
    }

    /**
     * Returns all the GERs (General Educational Requirements)
     * the student has done.
     * Returns an empty list if the {@link #VERIFIED_LIST} is empty,
     * or has no modules satisfying that condition.
     * @see Course#isGeneral()
     */
    public static ArrayList<Course> getGERs(){
        final ArrayList<Course> list = new ArrayList<>();
        for (Course c : VERIFIED_LIST) {
            if (c.isGeneral()) {
                list.add(c);
            }
        }
        return list;
    }

    /**
     * Returns the GERs (General Educational Requirements)
     * the student has done in the given semester.
     * Returns an empty list if the {@link #VERIFIED_LIST} is empty,
     * or has no modules satisfying that condition.
     * @see Course#isGeneral()
     * @see #getFractionBySemester(String)
     */
    public static ArrayList<Course> getGERsBySemester(String semester){
        final ArrayList<Course> list = new ArrayList<>();
        for (Course c : getFractionBySemester(semester)) {
            if (c.isGeneral()) {
                list.add(c);
            }
        }
        return list;
    }

    /**
     * Returns the GERs (General Educational Requirements)
     * the student has done in the given academic year.
     * Returns an empty list if the {@link #VERIFIED_LIST} is empty,
     * or has no modules satisfying that condition.
     * @see Course#isGeneral()
     * @see #getFractionByYear(String)
     */
    public static ArrayList<Course> getGERsByYear(String year){
        final ArrayList<Course> list = new ArrayList<>();
        for (Course c : getFractionByYear(year)) {
            if (c.isGeneral()) {
                list.add(c);
            }
        }
        return list;
    }

    /**
     * Returns all the modules that are not currently requirement-set.
     * Returns an empty list if the {@link #VERIFIED_LIST} is empty,
     * or has no modules satisfying that condition.
     * @see Course#isUnclassified()
     */
    public static ArrayList<Course> getUnknowns(){
        final ArrayList<Course> list = new ArrayList<>();
        for (Course c : VERIFIED_LIST) {
            if (c.isUnclassified()) {
                list.add(c);
            }
        }
        return list;
    }

    /**
     * Returns the modules that are not currently requirement-set
     * in the given semester.
     * Returns an empty list if the {@link #VERIFIED_LIST} is empty,
     * or has no modules satisfying that condition.
     * @see Course#isUnclassified()
     * @see #getFractionBySemester(String)
     */
    public static ArrayList<Course> getUnknownsBySemester(String semester){
        final ArrayList<Course> list = new ArrayList<>();
        for (Course c : getFractionBySemester(semester)) {
            if (c.isUnclassified()) {
                list.add(c);
            }
        }
        return list;
    }

    /**
     * Returns the modules that are not currently requirement-set
     * in the given academic year.
     * Returns an empty list if the {@link #VERIFIED_LIST} is empty,
     * or has no modules satisfying that condition.
     * @see Course#isUnclassified()
     * @see #getFractionByYear(String)
     */
    public static ArrayList<Course> getUnknownsByYear(String year){
        final ArrayList<Course> list = new ArrayList<>();
        for (Course c : getFractionByYear(year)) {
            if (c.isUnclassified()) {
                list.add(c);
            }
        }
        return list;
    }

    /**
     * Returns the lecturers the student has attended in the
     * given academic year. Ignores blank names.
     * Returns an empty list if the {@link #VERIFIED_LIST} is empty,
     * or has no modules satisfying that condition.
     * @see #getFractionByYear(String)
     * @see Globals#hasNoText(String)
     */
    public static ArrayList<String> getLecturersByYear(String year){
        final ArrayList<String> list = new ArrayList<>();
        for (Course c : getFractionByYear(year)) {
            final String lecturer = c.getLecturer();
            if (!(list.contains(lecturer) || Globals.hasNoText(lecturer))) {
                list.add(lecturer);
            }
        }
        return list;
    }

    /**
     * Returns all the modules the student has done with the
     * given lecturer. Ignores blank names.
     * Returns an empty list if the {@link #VERIFIED_LIST} is empty,
     * or has no modules satisfying that condition.
     * @see Globals#hasText(String)
     */
    public static ArrayList<Course> getFractionByLecturer(String lecturer){
        final ArrayList<Course> list = new ArrayList<>();
        for (Course c : VERIFIED_LIST) {
            final String cLecturer = c.getLecturer();
            if (Globals.hasText(cLecturer) && cLecturer.equals(lecturer)) {
                list.add(c);
            }
        }
        return list;
    }

    /**
     * Returns the modules the student has done with the given lecturer
     * for the specific, given academic year.
     * Returns an empty list if the {@link #VERIFIED_LIST} is empty,
     * or has no modules satisfying that condition.
     * @see #getFractionByYear(String)
     */
    public static ArrayList<Course> getFractionByLecturer(String lecturer, String year){
        final ArrayList<Course> list = new ArrayList<>();
        for (Course c : getFractionByYear(year)) {
            if (c.getLecturer().equals(lecturer)) {
                list.add(c);
            }
        }
        return list;
    }

    /**
     * Returns the courses the student has done in the given semester.
     * @see Course#getAbsoluteSemesterName()
     */
    public static ArrayList<Course> getFractionBySemester(String semester){
        final ArrayList<Course> list = new ArrayList<>();
        for (Course c : VERIFIED_LIST) {
            if (c.getAbsoluteSemesterName().equals(semester)) {
                list.add(c);
            }
        }
        return list;
    }

    /**
     * Returns the courses the student has done in the given academic year.
     */
    public static ArrayList<Course> getFractionByYear(String year){
        final ArrayList<Course> list = new ArrayList<>();
        for (Course c : VERIFIED_LIST) {
            if (c.getYear().equals(year)) {
                list.add(c);
            }
        }
        return list;
    }


//    Traces - trace for a given condition
//    they may give back null

    /**
     * Returns the course with the highest overall score;
     * or null - if the {@link #VERIFIED_LIST} is empty.
     */
    public static Course getHighestScore(){
        if (VERIFIED_LIST.isEmpty()) {
            return null;
        } else {
            Course requiredCourse = VERIFIED_LIST.get(0);
            for (Course course : VERIFIED_LIST) {
                if (course.getScore() > requiredCourse.getScore()) {
                    requiredCourse = course;
                }
            }
            return requiredCourse;
        }
    }

    /**
     * Returns the course with the lowest overall score;
     * or null - if the {@link #VERIFIED_LIST} is empty.
     */
    public static Course getLowestScore(){
        if (VERIFIED_LIST.isEmpty()) {
            return null;
        } else {
            Course requiredCourse = VERIFIED_LIST.get(0);
            for (Course course : VERIFIED_LIST) {
                if (course.getScore() < requiredCourse.getScore()) {
                    requiredCourse = course;
                }
            }
            return requiredCourse;
        }
    }

    /**
     * Returns the course with the highest major score;
     * or null - if the respective fraction is empty.
     * @see #getMajors()
     */
    public static Course getHighestMajorScore(){
        final ArrayList<Course> majors = getMajors();
        if (majors.isEmpty()) {
            return null;
        } else {
            Course requiredCourse = majors.get(0);
            for (Course course : majors) {
                if (course.getScore() > requiredCourse.getScore()) {
                    requiredCourse = course;
                }
            }
            return requiredCourse;
        }
    }

    /**
     * Returns the course with the lowest major score;
     * or null - if the respective fraction is empty.
     * @see #getMajors()
     */
    public static Course getLowestMajorScore(){
        final ArrayList<Course> majors = getMajors();
        if (majors.isEmpty()) {
            return null;
        } else {
            Course requiredCourse = majors.get(0);
            for (Course course : majors) {
                if (course.getScore() < requiredCourse.getScore()) {
                    requiredCourse = course;
                }
            }
            return requiredCourse;
        }
    }

    /**
     * Returns the course with the highest minor score;
     * or null - if the respective fraction is empty.
     * @see #getMinors()
     */
    public static Course getHighestMinorScore(){
        final ArrayList<Course> minors = getMinors();
        if (minors.isEmpty()) {
            return null;
        } else {
            Course requiredCourse = minors.get(0);
            for (Course course : minors) {
                if (course.getScore() > requiredCourse.getScore()) {
                    requiredCourse = course;
                }
            }
            return requiredCourse;
        }
    }

    /**
     * Returns the course with the lowest minor score;
     * or null - if the respective fraction is empty.
     * @see #getMinors()
     */
    public static Course getLowestMinorScore(){
        final ArrayList<Course> minors = getMinors();
        if (minors.isEmpty()) {
            return null;
        } else {
            Course requiredCourse = minors.get(0);
            for (Course course : minors) {
                if (course.getScore() < requiredCourse.getScore()) {
                    requiredCourse = course;
                }
            }
            return requiredCourse;
        }
    }

    /**
     * Returns the course with the highest DER score;
     * or null - if the respective fraction is empty.
     * @see #getDERs()
     */
    public static Course getHighestDERScore(){
        final ArrayList<Course> DERs = getDERs();
        if (DERs.isEmpty()) {
            return null;
        } else {
            Course requiredCourse = DERs.get(0);
            for (Course course : DERs) {
                if (course.getScore() > requiredCourse.getScore()) {
                    requiredCourse = course;
                }
            }
            return requiredCourse;
        }
    }

    /**
     * Returns the course with the lowest DER score;
     * or null - if the respective fraction is empty.
     * @see #getDERs()
     */
    public static Course getLowestDERScore(){
        final ArrayList<Course> DERs = getDERs();
        if (DERs.isEmpty()) {
            return null;
        } else {
            Course requiredCourse = DERs.get(0);
            for (Course course : DERs) {
                if (course.getScore() < requiredCourse.getScore()) {
                    requiredCourse = course;
                }
            }
            return requiredCourse;
        }
    }

    /**
     * Returns the course with the highest GER score;
     * or null - if the respective fraction is empty.
     * @see #getGERs()
     */
    public static Course getHighestGERScore(){
        final ArrayList<Course> GERs = getGERs();
        if (GERs.isEmpty()) {
            return null;
        } else {
            Course requiredCourse = GERs.get(0);
            for (Course course : GERs) {
                if (course.getScore() > requiredCourse.getScore()) {
                    requiredCourse = course;
                }
            }
            return requiredCourse;
        }
    }

    /**
     * Returns the course with the lowest GER score;
     * or null - if the respective fraction is empty.
     * @see #getGERs()
     */
    public static Course getLowestGERScore(){
        final ArrayList<Course> GERs = getGERs();
        if (GERs.isEmpty()) {
            return null;
        } else {
            Course requiredCourse = GERs.get(0);
            for (Course course : GERs) {
                if (course.getScore() < requiredCourse.getScore()) {
                    requiredCourse = course;
                }
            }
            return requiredCourse;
        }
    }

    /**
     * Returns the CGPA earned for the given academic year.
     * This is intended for analysis within Dashboard only,
     * and so may not necessarily comply with UTG standards.
     * @see #getFractionByYear(String)
     */
    public static double getCGPAByYear(String year){
        final ArrayList<Course> yearList = getFractionByYear(year);
        double totalPoints = 0;
        for (Course c : yearList) {
            totalPoints += c.getQualityPoint();
        }
        return totalPoints / yearList.size();
    }

    /**
     * Returns the CGPA earned for the given academic semester.
     * This is intended for analysis within Dashboard only,
     * and so may not necessarily comply with UTG standards.
     * @see #getFractionBySemester(String)
     */
    public static double getCGPABySemester(String semester){
        final ArrayList<Course> semesterList = getFractionBySemester(semester);
        double totalPoints = 0;
        for (Course c : semesterList) {
            totalPoints += c.getQualityPoint();
        }
        return totalPoints / semesterList.size();
    }

    /**
     * Returns the best semester based on the CGPA.
     * @see #getSemesters()
     * @see #getCGPABySemester(String)
     */
    public static String getBestSemester(){
        final ArrayList<String> semesters = getSemesters();
        if (semesters.isEmpty()) {
            return "...";
        } else {
            String bestSemester = semesters.get(0);
            double bestCGPA = getCGPABySemester(bestSemester);
            for (String semester : semesters) {
                final double cgpa = getCGPABySemester(semester);
                if (cgpa > bestCGPA) {
                    bestCGPA = cgpa;
                    bestSemester = semester;
                }
            }
            return bestSemester+"    [CGPA = "+ Analysis.toFourth(bestCGPA)+"]";
        }
    }

    /**
     * Returns the worst semester based on the CGPA.
     * @see #getSemesters()
     * @see #getCGPABySemester(String)
     */
    public static String getWorstSemester(){
        final ArrayList<String> semesters = getSemesters();
        if (semesters.isEmpty()) {
            return "...";
        } else {
            String worstSemester = semesters.get(0);
            double worstCGPA = getCGPABySemester(worstSemester);
            for (String semester : semesters) {
                final double cgpa = getCGPABySemester(semester);
                if (cgpa < worstCGPA) {
                    worstCGPA = cgpa;
                    worstSemester = semester;
                }
            }
            return worstSemester+"    [CGPA = "+ Analysis.toFourth(worstCGPA)+"]";
        }
    }

    /**
     * Returns the best academic year based on the CGPA.
     * @see #getAcademicYears()
     * @see #getCGPAByYear(String)
     */
    public static String getBestYear(){
        final ArrayList<String> years = getAcademicYears();
        if (years.isEmpty()) {
            return "...";
        } else {
            String bestYear = years.get(0);
            double bestCGPA = getCGPAByYear(bestYear);
            for (String year : years) {
                final double cgpa = getCGPAByYear(year);
                if (cgpa > bestCGPA) {
                    bestCGPA = cgpa;
                    bestYear = year;
                }
            }
            return bestYear+"    [CGPA = "+ Analysis.toFourth(bestCGPA)+"]";
        }
    }

    /**
     * Returns the worst academic year based on the CGPA.
     * @see #getAcademicYears()
     * @see #getCGPAByYear(String)
     */
    public static String getWorstYear(){
        final ArrayList<String> years = getAcademicYears();
        if (years.isEmpty()) {
            return "...";
        } else {
            String worstYear = years.get(0);
            double worstCGPA = getCGPAByYear(worstYear);
            for (String year : years) {
                final double cgpa = getCGPAByYear(year);
                if (cgpa < worstCGPA) {
                    worstCGPA = cgpa;
                    worstYear = year;
                }
            }
            return worstYear+"    [CGPA = "+ Analysis.toFourth(worstCGPA)+"]";
        }
    }

}
