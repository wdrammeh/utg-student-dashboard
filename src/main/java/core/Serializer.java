package core;

import org.apache.commons.io.FileUtils;
import utg.Dashboard;

import java.io.*;
import java.util.Formatter;

public class Serializer {
    public static final String ROOT_DIR = System.getProperty("user.home") + File.separator + "Dashboard";
    public static final String SERIALS_DIR = ROOT_DIR + File.separator + "serials";
    public static final String OUTPUT_DIR = ROOT_DIR + File.separator + "outputs";


    /**
     * Serializes the given object to the SERIALS_DIR with the given name.
     * Classes that perform serialization data eventually invoke this to do the ultimate writing.
     * This method is self-silent.
     */
    public static void toDisk(Object obj, String name){
        try {
            final File serialsPath = new File(SERIALS_DIR);
            if (serialsPath.exists() || serialsPath.mkdirs()) {
                final FileOutputStream fileOutputStream = new FileOutputStream(serialsPath + File.separator + name);
                final ObjectOutputStream out = new ObjectOutputStream(fileOutputStream);
                out.writeObject(obj);
                out.close();
            } else {
                App.silenceException("Error serialize file " + name + "; could not mount directory '" + serialsPath + "'");
            }
        } catch (Exception e) {
            App.silenceException("Error serializing file "+name);
        }
    }

    /**
     * reads the object, from the SERIALS_DIR, that was serialized
     * with the given name.
     * Classes that perform de-serialization eventually invoke this to do the ultimate reading.
     * This method is self-silent.
     * Therefore, callers must check nullity of the returned object
     */
    public static Object fromDisk(String name) {
        Object serObject = null;
        try {
            final FileInputStream fileInputStream = new FileInputStream(SERIALS_DIR + File.separator + name);
            final ObjectInputStream in = new ObjectInputStream(fileInputStream);
            serObject = in.readObject();
            in.close();
        } catch (Exception ignored) {
        }
        return serObject;
    }

    public static void placeReadMeFile(){
        final String readMeText = "This jar file, and its derivatives (Linux Executables, Windows Executables, etc.)\n" +
                "were compiled and distributed by Muhammed W. Drammeh <wakadrammeh@gmail.com>\n\n" +
                "Kindly report all issues, and feedback to "+Mailer.DEVELOPERS_MAIL+".\n\n" +
                "Do not modify or delete this file, or any other files in the \"serials\" directory.\n" +
                "Modifying files in the \"serials\" path can interrupt the 'Launch Sequence' which might cause Dashboard\n" +
                "to force a new instance, removing all your saved details and setting preferences.\n\n" +
                "This project is a FOSS [Free & Open Source Software]. Hence you are hereby permitted to make changes provided\n" +
                "you very well know and can make those changes.\n\n" +
                "Use this link - https://github.com/w-drammeh/utg-dashboard - to take part in the Dashboard open project on Github.\n\n" +
                "#Compilation Version = "+ Dashboard.VERSION+"\n" +
                "Get newer version from the Github Repository";
        try {
            final Formatter formatter = new Formatter(ROOT_DIR+File.separator+"README.txt");
            formatter.format(readMeText);
            formatter.close();
        } catch (FileNotFoundException e) {
            App.silenceException("Error: unable to place README file");
        }
    }

    public static void placeUserDetails(){
        final String data = "Month of Admission: "+ Student.getMonthOfAdmissionName()+"\n" +
                "Year of Admission: "+Student.getYearOfAdmission()+"\n" +
                "Current Semester: "+Student.getSemester()+"\n" +
                "First Name: "+Student.getFirstName()+"\n" +
                "Last Name: "+Student.getLastName()+"\n" +
                "Mat. Number: "+Student.getMatNumber()+"\n" +
                "Major: "+Student.getMajor()+"\n" +
                "Major Code: "+Student.getMajorCode()+"\n" +
                "Minor: "+Student.getMinor()+"\n" +
                "Minor Code: "+Student.getMinorCode()+"\n" +
                "Program: "+Student.getProgram()+"\n" +
                "School: "+Student.getSchool()+"\n" +
                "Department: "+Student.getDivision()+"\n" +
                "Address: "+Student.getAddress()+"\n" +
                "Telephone: "+Student.getTelephone()+"\n" +
                "Nationality: "+Student.getNationality()+"\n" +
                "Date of Birth: "+Student.getDateOfBirth()+"\n" +
                "Student Mail: "+Student.getStudentMail()+"\n" +
                "Marital Status: "+Student.getMaritalStatue()+"\n" +
                "Place of Birth: "+Student.getPlaceOfBirth()+"\n" +
                "Level: "+Student.getLevel()+"\n" +
                "Status: "+Student.getStatus()+"\n" +
                "#\n" +
                "Dashboard Version: "+ Dashboard.VERSION;
        try {
            final File outputsPath = new File(OUTPUT_DIR);
            if (outputsPath.exists() || outputsPath.mkdirs()) {
                final Formatter formatter = new Formatter(OUTPUT_DIR + File.separator + "user.txt");
                formatter.format(data);
                formatter.close();
            } else {
                App.silenceException("Error: unable to place output file: "+outputsPath);
            }
        } catch (FileNotFoundException e) {
            App.silenceException(e);
        }
    }

    public static void mountUserData(){
        toDisk(System.getProperty("user.name"), "user-name.ser");
        placeReadMeFile();
        Settings.serialize();
        if (!Student.isTrial()) {
            placeUserDetails();
            Portal.serialize();
            RunningCourseActivity.serialize();
            ModuleHandler.serialize();
        }
        Student.serialize();
        TaskSelf.serialize();
        Notification.serialize();
        News.serialize();
    }

    public static boolean unMountUserData(){
        try {
            FileUtils.deleteDirectory(new File(ROOT_DIR));
            return true;
        } catch (IOException ioe) {
            final File userData = new File(SERIALS_DIR + File.separator + "core.ser");
            return !userData.exists() || userData.delete();
        }
    }

}
