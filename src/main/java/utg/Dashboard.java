/*
UTG Student Dashboard:
    "A student management system for the University of The Gambia"

Copyright (C) 2021  Muhammed W. Drammeh. All rights reserved.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

package utg;

import core.Board;
import core.Preview;
import core.alert.Notification;
import core.first.Welcome;
import core.serial.Serializer;
import core.user.Student;
import core.utils.*;

import javax.swing.*;
import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @author Muhammed W. Drammeh <md21712494@utg.edu.gm>
 *
 * This is the actual "runner" type of the program.
 * In a nutshell, it reads from a serializable state if existed,
 * or triggers a new instance if not - or otherwise found inconsistent.
 * This class defines the normal process-flow of the Dashboard.
 * Please read the "Logic" file.
 * @see Serializer
 * @see Transition
 * @see Board
 */
public class Dashboard {
    private static final Preview PREVIEW = new Preview(null);
    public static final Version VERSION = new Version("2021.6");
    private static boolean isAuthentic = true;
    private static boolean isFirst;


    public static void main(String[] args) {
        statusCheck();
        PREVIEW.setVisible(true);
        final File rootDir = new File(Serializer.ROOT_DIR);
        if (rootDir.exists()) {
            final File configFile = new File(Serializer.inPath("configs.ser"));
            if (configFile.exists()) {
                final HashMap<String, String> lastConfigs = getLastConfigs();
                if (lastConfigs.isEmpty()) {
                    App.silenceException("Bad configuration files. Launching a new instance...");
                    freshStart();
                } else {
                    final boolean isAuthentic = Boolean.parseBoolean(lastConfigs.get("isAuthentic"));
                    if (!isAuthentic) {
                        PREVIEW.dispose();
                        reportAuthenticationError();
                    }
                    final Version recentVersion = new Version(lastConfigs.get("version"));
                    final int comparison = VERSION.compare(recentVersion);
                    if (comparison == Version.LESS) {
                        PREVIEW.dispose();
                        App.reportError(null, "Version Error | Downgrade Detected",
                                "You're trying to launch Dashboard with an older version than your configuration files.\n" +
                                        "Please use Dashboard version '"+recentVersion+"', or later.");
                        System.exit(0);
                    } else if (comparison == Version.EQUAL) {
                        final String deprecateTime = lastConfigs.get("deprecateTime");
                        if (Globals.hasText(deprecateTime)) {
                            final Date deprecateDate = MDate.parse(deprecateTime);
                            VERSION.setDeprecateTime(deprecateDate);
                            if (new Date().after(deprecateDate)) {
                                App.reportError(null, "Dashboard Outdated",
                                        "This version of Dashboard is outdated. You must update...\n" +
                                                Internet.DOWNLOAD_URL);
                                System.exit(0);
                            } else {
                                App.silenceWarning(String.format("This version will be outdated by '%s'. " +
                                        "Kindly download the latest version: %s", deprecateTime, Internet.DOWNLOAD_URL));
                            }
                        }
                    } else if (comparison == Version.GREATER) {
                        App.silenceInfo("A version upgrade detected.");
                        final String logAddress = String.join("/", Internet.REPO_URL, "blob", "master", "ChangeLog.md");
                        Board.POST_PROCESSES.add(()-> Notification.create("New Update", "Your Dashboard has been updated.",
                                "<p>A version upgrade was detected: from <b>"+recentVersion+"</b> to <b>"+VERSION+"</b>.</p>" +
                                        "<p>Kindly visit the official Dashboard repository on Github to <i>check out</i> " +
                                        "<a href="+logAddress+">What's New</a> about this release.</p>"));
                        Transition.transit(recentVersion, VERSION);
                    }
                    if (Globals.userName().equals(lastConfigs.get("userName"))) {
                        rebuildNow(true);
                    } else {
                        verifyUser(true);
                    }
                }
            } else {
                App.silenceException("Missing configuration files. Launching a new instance...");
                freshStart();
            }
        } else {
            freshStart();
        }
    }

    /**
     * Programmatically employed to prevent Dashboard from running in parallel,
     * this function may simply let this launch to proceed, or prevent it somehow.
     */
    private static void statusCheck(){
        final List<ProcessHandle> handles = ProcessHandle.allProcesses().toList();
        for (ProcessHandle handle : handles) {
            final String command = handle.info().command().map(String::toString).orElse("");
            if (command.contains("UTG Student Dashboard")) {
                App.silenceInfo("Dashboard is already running.");
                System.exit(0);
            }
        }
    }

    /**
     * Serializes the configurations at this point in time -
     * usually during collapse.
     */
    public static void storeConfigs(){
        final String configs = Globals.joinLines(isAuthentic, Globals.userName(), VERSION,
                MDate.format(VERSION.getDeprecateTime()));
        Serializer.toDisk(configs, Serializer.inPath("configs.ser"));
    }

    /**
     * Gets the configurations when Dashboard was last used.
     * On a normal run, this should be invoked, but only once.
     * @see #storeConfigs()
     */
    private static HashMap<String, String> getLastConfigs(){
        final HashMap<String, String> map = new HashMap<>();
        final Object configObj = Serializer.fromDisk(Serializer.inPath("configs.ser"));
        if (configObj != null) {
            final String[] lines = Globals.splitLines((String) configObj);
            map.put("isAuthentic", lines[0]);
            map.put("userName", lines[1]);
            map.put("version", lines[2]);
            map.put("deprecateTime", lines[3]);
        }
        return map;
    }

    /**
     * Triggers a whole new Dashboard instance.
     * This happens, of course, if no data are found to deserialize.
     * The user might have signed out, or has actually never launched Dashboard.
     */
    private static void freshStart(){
        isFirst = true;
        final Welcome welcome = new Welcome();
        PREVIEW.dispose();
        welcome.setVisible(true);
        SwingUtilities.invokeLater(()-> welcome.getScrollPane().toTop());
    }

    /**
     * This is a security measure invoked if the current userName
     * does not matches the serialized userName.
     * The user will be asked of the previous user's Matriculation Number.
     * Dashboard should not build until such Mat. Number is correct.
     */
    private static void verifyUser(boolean initialize){
        if (initialize) {
            try {
                Student.initialize();
                if (Student.isGuest()) {
                    rebuildNow(false);
                    return;
                }
            } catch (Exception e) {
                App.silenceException("Failed to read user data. Launching a new instance...");
                freshStart();
                return;
            }
        }

        PREVIEW.setVisible(false);
        final String matNumber = requestPassword();
        if (matNumber.equals(Student.getMatNumber())) {
            PREVIEW.setVisible(true);
            rebuildNow(false);
        } else {
            final String userName = Student.getFullNamePostOrder();
            App.reportError(PREVIEW, "Error",
                    "Incorrect Matriculation Number for '"+userName+"'. Try again.");
            verifyUser(false);
        }
    }

    private static String requestPassword(){
        final String studentName = Student.getFullNamePostOrder();
        final String input = App.requestInput(null, "Dashboard",
                "This Dashboard belongs to '"+studentName+"'.\n" +
                "Please enter your Matriculation Number to confirm:");
        if (input == null) {
            System.exit(0);
        }
        return Globals.hasText(input) ? input : requestPassword();
    }

    /**
     * Builds the Dashboard from a serializable state.
     * Where initialize determines whether the user's data are to be loaded,
     * if were not already.
     */
    private static void rebuildNow(boolean initialize){
        if (initialize) {
            try {
                Student.initialize();
            } catch (NullPointerException e) {
                App.silenceException("Failed to read user data. Launching a new instance...");
                freshStart();
                return;
            }
        }

        SwingUtilities.invokeLater(()-> {
            final Board lastBoard = new Board();
            PREVIEW.dispose();
            lastBoard.setVisible(true);
        });
    }

    public static void setFirst(boolean first){
        isFirst = first;
    }

    public static boolean isFirst() {
        return isFirst;
    }

    public static void setAuthentic(boolean authentic){
        isAuthentic = authentic;
    }

    public static boolean isAuthentic() {
        return isAuthentic;
    }

    public static void reportAuthenticationError() {
        App.reportWarning("Authentication Error",
                "This program is either not verified, or no longer supported.\n" +
                        "For more info contact the developers: '"+ Mailer.DEVELOPER_MAIL +"'.");
        System.exit(0);
    }

}
