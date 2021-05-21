/*
UTG Student Dashboard:
    "A student management system for the University of The Gambia"

Copyright (C) 2021  Muhammed W. Drammeh <md21712494@utg.edu.gm>

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

package core.serial;

import core.News;
import core.Portal;
import core.alert.Notification;
import core.driver.MDriver;
import core.module.ModuleHandler;
import core.module.SemesterActivity;
import core.setting.Settings;
import core.task.TaskActivity;
import core.user.Student;
import core.utils.App;
import org.apache.commons.io.FileUtils;
import utg.Dashboard;

import java.io.*;

import static core.utils.Globals.joinPaths;

public class Serializer {
    public static final String ROOT_DIR = joinPaths(System.getProperty("user.home"), ".dashboard");


    /**
     * Serializes the given object to the given path.
     * Classes that perform serialization eventually invoke this to do the ultimate writing.
     * This method is self-silent.
     */
    public static void toDisk(Object obj, String path){
        try {
            final File file = new File(path);
            final File parentFile = file.getParentFile();
            if (parentFile.exists() || parentFile.mkdirs()) {
                final FileOutputStream fileOutputStream = new FileOutputStream(path);
                final ObjectOutputStream objOutputStream = new ObjectOutputStream(fileOutputStream);
                objOutputStream.writeObject(obj);
                objOutputStream.close();
            } else {
                App.silenceException("Failed to mount parent directories to serialize file '" + path + "'.");
            }
        } catch (IOException e) {
            App.silenceException(e);
        }
    }

    /**
     * Reads the object that was serialized to the given path.
     * Classes that perform de-serialization eventually
     * invoke this to do the ultimate reading.
     * This method is self-silent.
     * Therefore, callers must check nullity of the returned object
     */
    public static Object fromDisk(String path) {
        Object serObject = null;
        try {
            final FileInputStream fileInputStream = new FileInputStream(path);
            final ObjectInputStream objInputStream = new ObjectInputStream(fileInputStream);
            serObject = objInputStream.readObject();
            objInputStream.close();
            fileInputStream.close();
        } catch (IOException | ClassNotFoundException e) {
            App.silenceException(e);
        }
        return serObject;
    }

    public static String inPath(String... paths){
        return String.join(File.separator, ROOT_DIR, joinPaths(paths));
    }

    public static void mountUserData(){
        Dashboard.storeConfigs();
        Settings.serialize();
        if (!Student.isGuest()) {
            Portal.serialize();
            SemesterActivity.serialize();
            ModuleHandler.serialize();
        }
        Student.serialize();
        TaskActivity.serializeAll();
        Notification.serialize();
        News.serialize();
        MDriver.stopAll();
    }

    public static boolean unMountUserData(){
        try {
            FileUtils.deleteDirectory(new File(ROOT_DIR));
            return true;
        } catch (IOException e) {
            App.silenceException(e);
            final File configFile = new File(inPath("configs.ser"));
            return configFile.delete();
        }
    }

}