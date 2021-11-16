package core.user;

import core.Board;
import core.first.Login;
import core.first.PrePortal;
import core.module.ModuleHandler;
import core.setting.SettingsActivity;
import core.utils.*;
import utg.Dashboard;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;

/**
 * Todo: reconsider the {@link #isGraduated()} call
 */
public class Student {
    private static String firstName;
    private static String lastName;
    private static String matNumber;
    private static String program;
    private static String major;
    private static String minor;
    private static String school;
    private static String division;
    /**
     * Determines the currently running semester.
     * Initially provided by PrePortal but always auto-renewed.
     */
    private static String semester;
    /**
     * The state / status. Also auto-renewed.
     * "RUNNING", "UNKNOWN",
     */
    private static String status;
    /**
     * The level, i.e "Undergraduate", "Post-graduate", etc.
     * Also auto-renewed.
     */
    private static String level;
    private static String address;
    /**
     * Telephones: at any point in time, the first partition is returned
     * as the primary telephone.
     */
    private static final ArrayList<String> telephones = new ArrayList<>();
    private static String placeOfBirth;
    private static String nationality;
    private static String dateOfBirth;
    private static String maritalStatue;
    private static String portalMail;
    private static String portalPassword;
    private static String studentMail;
    private static String studentPassword;
    private static String majorCode;
    private static String minorCode;
    private static String about;
    private static int yearOfAdmission; // the exact year admission takes place
    private static int monthOfAdmission;
    /**
     * Deals with the level in 'cents'
     * Dashboard records levels in 50s.
     * 50 = first semester; 100 = second semester; 150 = 2nd year, first semester;
     * so on and so forth. The same way 300 implies 3rd year, 2nd semester.
     */
    private static int levelNumber;
    private static String CGPA;
    private static ImageIcon userIcon;
    private static final LinkedHashMap<String, String> additionalData = new LinkedHashMap<>();
    private static boolean isGuest;
    private static String nameFormat = "First Name first";
    private static final int ICON_WIDTH = 275;
    private static final int ICON_HEIGHT = 200;
    private static final ImageIcon DEFAULT_ICON = MComponent.scaleIcon(App.getIconURL("default-icon.png"),
            ICON_WIDTH, ICON_HEIGHT);
    private static final String IMAGE_PATH = Serializer.inPath("user", "imageIcon");
    public static final String FIRST_SEMESTER = "First Semester";
    public static final String SECOND_SEMESTER = "Second Semester";
    public static final String SUMMER_SEMESTER = "Summer Semester";
    // Upper-class divisions
    public static final String UNCLASSIFIED = "None";
    public static final String THIRD_CLASS = "Cum Laude (With Praise)!";
    public static final String SECOND_CLASS = "Magna Cum Laude (With Great Honor)!";
    public static final String FIRST_CLASS = "Summa Cum Laude (With Greatest Honor)!";


    public static String getFirstName(){
        return firstName;
    }

    public static void setFirstName(String firstName) {
        Student.firstName = firstName;
    }

    public static String getLastName() {
        return lastName;
    }

    public static void setLastName(String lastName) {
        Student.lastName = lastName;
    }

    public static String getProgram(){
        return program;
    }

    public static void setProgram(String program){
        Student.program = program;
    }

    public static String getMajor() {
        return major;
    }

    public static void setMajor(String major) {
        Student.major = major;
    }

    public static String getMinor() {
        return minor;
    }

    /**
     * Sets the minor of the student to the given minor.
     * This has runtime component modification consequences.
     * Also, if the minor has no text as specified by {@link Globals#hasNoText(String)},
     * the minor-code will reset.
     * @see #setMinorCode(String)
     */
    public static void setMinor(String minor){
        Student.minor = minor;
        SettingsActivity.minorLabel.setText(minor);
        SettingsActivity.minorField.setText(minor);
        if (Globals.hasNoText(minor)) {
            setMinorCode("");
        }
    }

    public static String getSchool() {
        return school;
    }

    public static void setSchool(String school) {
        Student.school = school;
    }

    public static String getDivision(){
        return division;
    }

    public static void setDivision(String division) {
        Student.division = division;
    }

    public static String getAddress() {
        return address;
    }

    public static void setAddress(String address) {
        Student.address = address;
    }

    public static String getPlaceOfBirth() {
        return placeOfBirth;
    }

    public static void setPlaceOfBirth(String placeOfBirth) {
        Student.placeOfBirth = placeOfBirth;
    }

    public static String getNationality() {
        return nationality;
    }

    public static void setNationality(String nationality) {
        Student.nationality = nationality;
    }

    public static String getDateOfBirth() {
        return dateOfBirth;
    }

    public static void setDateOfBirth(String dateOfBirth) {
        Student.dateOfBirth = dateOfBirth;
    }

    public static String getMaritalStatue() {
        return maritalStatue;
    }

    public static void setMaritalStatue(String maritalStatue) {
        Student.maritalStatue = maritalStatue;
    }

    public static String getPortalMail() {
        return portalMail;
    }

    public static String getVisiblePortalMail(){
        return Globals.hasText(portalMail) ? getVisibleMail(portalMail) : "";
    }

    public static String getVisibleMail(String mail){
        try {
            final String[] parts = mail.split("@");
            final int l = parts[0].length();
            final String mask = "*".repeat(l - 3);
            return mail.substring(0, 2) + mask + mail.charAt(l - 1) + "@" + parts[1];
        } catch (Exception e) { // indexOutOfBounds, ?
            App.silenceException(String.format("Bad mail format '%s'.", mail));
            return "";
        }
    }

    public static String getVisibleStudentMail(){
        return Globals.hasText(studentMail) ? getVisibleMail(studentMail) : "";
    }

    public static void setPortalMail(String portalMail) {
        Student.portalMail = portalMail;
    }

    public static String getStudentMail() {
        return studentMail;
    }

    public static void setStudentMail(String studentMail) {
        Student.studentMail = studentMail;
    }

    public static String getStudentPassword(){
        return studentPassword;
    }

    public static void setStudentPassword(String studentPassword) {
        Student.studentPassword = studentPassword;
    }

    public static String getPortalPassword(){
        return portalPassword;
    }

    public static void setPortalPassword(String portalPassword){
        Student.portalPassword = portalPassword;
    }

    public static String getMatNumber() {
        return matNumber;
    }

    public static void setMatNumber(String matNumber) {
        Student.matNumber = matNumber;
    }

    public static int getYearOfAdmission() {
        return yearOfAdmission;
    }

    public static void setYearOfAdmission(int yearOfAdmission) {
        Student.yearOfAdmission = yearOfAdmission;
    }

    public static int getMonthOfAdmission() {
        return monthOfAdmission;
    }

    public static void setMonthOfAdmission(int monthOfAdmission) {
        Student.monthOfAdmission = monthOfAdmission;
    }

    /**
     * Returns just the first contact in the current telephones list
     * This may give 'Unknown' (when given by PrePortal / Portal),
     * or an empty-string, null (when all are removed) by the user.
     */
    public static String getTelephone() {
        return telephones.get(0);
    }

    public static ArrayList<String> getTelephones() {
        return telephones;
    }

    public static void addTelephone(String tel) {
        telephones.add(tel);
    }

    public static void removeTelephone(String tel) {
        telephones.remove(tel);
    }

    /**
     * Returns the CGPA of the student.
     * Dashboard does not compute the student's GPA.
     */
    public static String getCGPA() {
        return CGPA;
    }

    public static void setCGPA(String CGPA) {
        Student.CGPA = CGPA;
    }

    public static void setMajorCode(String majorCode) {
        SettingsActivity.majorCodeField.setText(majorCode);
        ModuleHandler.effectMajorCodeChanges(Student.majorCode, majorCode);
        Student.majorCode = majorCode;
    }

    public static String getMajorCode(){
        return majorCode;
    }

    public static void setMinorCode(String minorCode){
        SettingsActivity.minorCodeField.setText(minorCode);
        ModuleHandler.effectMinorCodeChanges(Student.minorCode, minorCode);
        Student.minorCode = minorCode;
    }

    public static String getMinorCode(){
        return minorCode;
    }

    public static String getSemester() {
        return semester;
    }

    /**
     * Sets the semester to the given academic-semester.
     * The semester says a lot about the student,
     * including his/her level.
     * Never call this before setting the yearOfAdmission.
     *
     * At every login, level is set first, state, then semester.
     */
    public static void setSemester(String semester) {
        semester = semester.toUpperCase();
        final String academicYear = semester.split(" ")[0];
        if (semester.contains("FIRST")) {
            Student.semester = String.join(" ", academicYear, FIRST_SEMESTER);
        } else if (semester.contains("SECOND")) {
            Student.semester = String.join(" ", academicYear, SECOND_SEMESTER);
        } else if (semester.contains("SUMMER")) {
            Student.semester = String.join(" ", academicYear, SUMMER_SEMESTER);
        }
        Board.effectSemesterUpgrade();
        final int current = Integer.parseInt(semester.substring(0, 4));
        levelNumber = (current - yearOfAdmission)  * 100;
    }

    public static String getStatus() {
        return status;
    }

    public static void setStatus(String status) {
        Student.status = status;
        Board.effectStatusUpgrade();
    }

    public static String getLevel() {
        return level;
    }

    public static void setLevel(String level) {
        Student.level = level;
        Board.effectLevelUpgrade();
    }

    public static int getLevelNumber() {
        return levelNumber;
    }

    public static ImageIcon getIcon(){
        return userIcon == null ? DEFAULT_ICON : userIcon;
    }

    public static void setUserIcon(ImageIcon userIcon) {
        Student.userIcon = userIcon;
    }

    public static String getAbout(){
        return about;
    }

    public static void setAbout(String about){
        Student.about = about;
    }

    public static LinkedHashMap<String, String> getAdditional(){
        return additionalData;
    }

    public static String currentNameFormat(){
        return nameFormat;
    }

    public static void setNameFormat(String format){
        Student.nameFormat = format;
        Board.effectNameFormatChanges();
    }

    public static String requiredNameForFormat(){
        return nameFormat.startsWith("First") ? getFullName() : getFullNamePostOrder();
    }

    public static void initialize() {
        if (Dashboard.isFirst()) {
            final Object[] initials = PrePortal.USER_DATA.toArray();
            firstName = (String) initials[0];
            lastName = (String) initials[1];
            program = (String) initials[2];
            try {
                matNumber = (String) initials[3];
            } catch (Exception e){
                reportCriticalInfoMissing(Login.getRoot(), "Mat Number");
            }
            major = (String) initials[4];
            school = (String) initials[5];
            division = (String) initials[6];
            nationality = (String) initials[7];
            try {
                monthOfAdmission = Integer.parseInt((String) initials[8]);
            } catch (Exception e){
                reportCriticalInfoMissing(Login.getRoot(), "Month of Admission");
            }
            try {
                yearOfAdmission = Integer.parseInt((String) initials[9]);
            } catch (Exception e){
                reportCriticalInfoMissing(Login.getRoot(),"Year of Admission");
            }
            address = (String) initials[10];
            maritalStatue = (String) initials[11];
            dateOfBirth = (String) initials[12];
            addTelephone((String) initials[13]);
            portalMail = (String) initials[14];
            portalPassword = (String) initials[15];
            CGPA = (String) initials[19];
            Board.effectNameFormatChanges();
            setLevel((String)(initials[17]));
            setStatus((String)(initials[18]));
            setSemester((String)(initials[16]));

            minor = majorCode = minorCode = "";
            isGuest = false;
        } else {
            deserializeData();
        }
    }

    public static void setupTrial(String[] data){
        firstName = data[0];
        lastName = data[1];
        nationality = data[2];
        maritalStatue = dateOfBirth = placeOfBirth = about = address = "";
        isGuest = true;
    }

    public static boolean isGuest(){
        return isGuest;
    }

    public static String getFullName() {
        return String.join(" ", firstName, lastName);
    }

    public static String getFullNamePostOrder() {
        return String.join(" ", lastName, firstName);
    }

    public static String getAcronym(){
        return (firstName.charAt(0)+""+lastName.charAt(0)).toLowerCase();
    }

    public static String predictedStudentMailAddress(){
        return getAcronym()+matNumber+"@utg.edu.gm";
    }

    public static String predictedStudentPassword(){
        return matNumber; // "student@utg"?
    }

    public static String upperClassDivision() {
        try {
            final Double cgpa = Double.parseDouble(CGPA);
            if (cgpa >= 4) {
                return FIRST_CLASS;
            } else if (cgpa >= 3.8) {
                return SECOND_CLASS;
            } else if (cgpa >= 3.5) {
                return THIRD_CLASS;
            } else {
                return UNCLASSIFIED;
            }
        } catch (Exception e) {
            return Globals.UNKNOWN;
        }
    }

    /**
     * Returns the current academic year in yyyy/yyyy format.
     */
    public static String getAcademicYear() {
        return semester.split(" ")[0];
    }

    /**
     * Returns true if the given year is a valid academic year;
     * false otherwise.
     * A Dashboard's valid academic year is in the format yyyy/zzzz
     */
    public static boolean isValidAcademicYear(String year) {
        if (year.contains("/")) {
            try {
                final String[] parts = year.split("/");
                return String.valueOf(Integer.parseInt(parts[0])).length() == 4 &&
                        String.valueOf(Integer.parseInt(parts[1])).length() == 4;
            } catch (Exception e){
                return false;
            }
        } else {
            return false;
        }
    }

    private static int firstYear(){
        return yearOfAdmission;
    }

    private static int secondYear(){
        return firstYear() + 1;
    }

    private static int thirdYear(){
        return secondYear() + 1;
    }

    private static int fourthYear(){
        return thirdYear() + 1;
    }

//    if this is readable from the portal, then be it.
    public static boolean isGraduated(){
        return levelNumber > 400;
    }

    public static String firstAcademicYear(){
        return firstYear() + "/" + secondYear();
    }

    public static String secondAcademicYear(){
        return secondYear() + "/" + thirdYear();
    }

    public static String thirdAcademicYear(){
        return thirdYear() + "/" + fourthYear();
    }

    public static String fourthAcademicYear(){
        return fourthYear() + "/" + (fourthYear() + 1);
    }

    public static boolean isFirstYear(){
        return levelNumber == 100;
    }

    public static boolean isSecondYear(){
        return levelNumber == 200;
    }

    public static boolean isThirdYear(){
        return levelNumber == 300;
    }

    public static boolean isFourthYear(){
        return levelNumber == 400;
    }

    public static int getExpectedYearOfGraduation(){
        return yearOfAdmission + 4;
    }

    public static String getMonthOfAdmissionName(){
        return MDate.getMonthName(monthOfAdmission);
    }

    public static boolean isUndergraduate(){
        return level.equalsIgnoreCase("Undergraduate");
    }

    public static boolean isPostgraduate(){
        return level.equalsIgnoreCase("Postgraduate");
    }

    public static boolean isDoingMinor(){
        return Globals.hasText(Student.minor);
    }

    private static void reportCriticalInfoMissing(Component parent, String info) {
        App.reportWarning(parent, info+" Missing","It turns out that your \""+info+"\" was not found.\n" +
                "This can lead to inaccurate analysis and prediction.\n" +
                "Please refer your department for this problem.");
    }

    public static void startSettingImage(Component parent){
        final Component actualParent = parent == null ? Board.getRoot() : parent;
        final String homeDir = System.getProperty("user.home");
        final String picturesDir = Globals.joinPaths(homeDir, "Pictures");
        final JFileChooser fileChooser = new JFileChooser(new File(picturesDir).exists() ? picturesDir : homeDir){
            @Override
            public JToolTip createToolTip() {
                return MComponent.preferredTip();
            }
        };
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setDialogTitle("Select Image");
        fileChooser.setMultiSelectionEnabled(false);
        final int selection = fileChooser.showOpenDialog(actualParent);
        if (selection == JFileChooser.APPROVE_OPTION) {
            fireIconChange(fileChooser.getSelectedFile(), actualParent);
        }
    }

    public static void startSettingImage(){
        startSettingImage(null);
    }

    /**
     * It will also notify the containers harboring the icon. One of such known component:
     * imagePanel at main.Board
     * If the parsing-file is null, nothing is done.
     */
    private static void fireIconChange(File file, Component c){
        if (file != null) {
            try {
                final ImageIcon newIcon = MComponent.scaleIcon(file.toURI().toURL(), ICON_WIDTH, ICON_HEIGHT);
                if (newIcon == null) {
                    App.reportError(c, "Error", "Could not set the image icon to '" + file.getAbsolutePath() + "'.\n" +
                            "Is that an image file? If it's not, try again with a valid image file, otherwise it is of an unsupported type.");
                    return;
                }
                userIcon = newIcon;
                effectIconChanges();

                final File imageFile = new File(IMAGE_PATH);
                final File parent = imageFile.getParentFile();
                if (parent.exists() || parent.mkdirs()) {
                    Files.copy(file.toPath(), parent.toPath().resolve("imageIcon"),
                            StandardCopyOption.REPLACE_EXISTING);
                } else {
                    App.silenceException("Failed to copy image icon. Could not mount parent directory.");
                }
            } catch (MalformedURLException e) {
                App.reportError(c, "Error", "An error occurred while attempting to set the image icon.\n" +
                        "Please try again, preferably, with a different choice.");
            } catch (IOException e) { // error related to copying
                App.silenceException(e);
            }
        }
    }

    /**
     * Effects visual changes on the containers holding the icon, globally.
     */
    private static void effectIconChanges() {
        Board.effectIconChanges();
    }

    public static void fireIconReset(){
        if (!isDefaultIconSet()) {
            if (App.showYesNoCancelDialog("Confirm Reset",
                    "This action will remove your image icon. Continue?")) {
                userIcon = DEFAULT_ICON;
                effectIconChanges();
                try {
                    Files.delete(new File(IMAGE_PATH).toPath());
                } catch (IOException e) {
                    App.silenceException(e);
                }
            }
        }
    }

    /**
     * Algorithm generates true even if no icon is manually set.
     * @see #getIcon
     */
    public static boolean isDefaultIconSet(){
        return getIcon() == DEFAULT_ICON;
    }

    public static void serialize() {
        String core = Globals.joinLines(new Object[]{firstName, lastName, nationality, address,
                maritalStatue, dateOfBirth, placeOfBirth, nameFormat, isGuest});
        if (!isGuest) {
            core = Globals.joinLines(new Object[]{core, monthOfAdmission, yearOfAdmission, semester, matNumber,
                    major, majorCode, minor, minorCode, program, school, division, portalMail,
                    portalPassword, studentMail, studentPassword, level, status, CGPA});
        }
        Serializer.toDisk(core, Serializer.inPath("user", "core.ser"));
        Serializer.toDisk(telephones.toArray(new String[0]), Serializer.inPath("user", "dials.ser"));
        Serializer.toDisk(about, Serializer.inPath("user", "about.ser"));

        final String[] extraKeys = new String[additionalData.size()];
        final String[] extraValues = new String[additionalData.size()];
        int i = 0;
        for (String key : additionalData.keySet()){
            extraKeys[i] = key;
            extraValues[i] = additionalData.get(key);
            i++;
        }
        Serializer.toDisk(extraKeys, Serializer.inPath("user", "extra.keys.ser"));
        Serializer.toDisk(extraValues, Serializer.inPath("user", "extra.values.ser"));
    }

    private static void deserializeData() throws NullPointerException { // classCast,
        final Object coreObj = Serializer.fromDisk(Serializer.inPath("user", "core.ser"));
        if (coreObj == null) {
            throw new NullPointerException("User's core file is \"bad\", or missing.");
        }

        final String[] core = Globals.splitLines((String) coreObj);
        firstName = core[0];
        lastName = core[1];
        nationality = core[2];
        address = core[3];
        maritalStatue = core[4];
        dateOfBirth = core[5];
        placeOfBirth = core[6];
        setNameFormat(core[7]);
        isGuest = Boolean.parseBoolean(core[8]);
        try {
            about = (String) Serializer.fromDisk(Serializer.inPath("user", "about.ser"));
            final String[] dials = (String[]) Serializer.fromDisk(Serializer.inPath("user", "dials.ser"));
            Collections.addAll(telephones, dials);
        } catch (Exception e) {
            App.silenceException(e);
            about = "";
        }

        if (!isGuest) {
            monthOfAdmission = Integer.parseInt(core[9]);
            yearOfAdmission = Integer.parseInt(core[10]);
            setSemester(core[11]);
            matNumber = core[12];
            major = core[13];
            majorCode = core[14];
            minor = core[15];
            minorCode = core[16];
            program = core[17];
            school = core[18];
            division = core[19];
            portalMail = core[20];
            portalPassword = core[21];
            studentMail = core[22];
            studentPassword = core[23];
            setLevel(core[24]);
            setStatus(core[25]);
            CGPA = core[26];
        }

        try {
            final String[] extraKeys = (String[]) Serializer.fromDisk(Serializer.inPath("user", "extra.keys.ser"));
            final String[] extraValues = (String[]) Serializer.fromDisk(Serializer.inPath("user", "extra.values.ser"));
            int i = 0;
            while (i < extraKeys.length) {
                additionalData.put(extraKeys[i], extraValues[i]);
                i++;
            }
        } catch (Exception e) {
            App.silenceException(e);
        }

        final File imageFile = new File(IMAGE_PATH);
        if (imageFile.exists()) {
            try {
                final ImageIcon icon = MComponent.scaleIcon(imageFile.toURI().toURL(), ICON_WIDTH, ICON_HEIGHT);
                if (icon == null) {
                    App.silenceException("Failed to read/load the image icon.'");
                    Board.POST_PROCESSES.add(Student::fireIconReset);
                } else {
                    userIcon = icon;
                }
            } catch (MalformedURLException e) {
                App.silenceException(e);
                Board.POST_PROCESSES.add(Student::fireIconReset);
            }
        } else {
            Board.POST_PROCESSES.add(Student::fireIconReset);
        }
    }

}
