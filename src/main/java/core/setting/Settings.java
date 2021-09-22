package core.setting;

import core.utils.Serializer;
import core.utils.App;
import core.utils.Globals;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;

public class Settings {
    public static boolean noVerifyNeeded = false;
    public static boolean confirmExit = true;
    public static String backgroundName = Globals.NONE;
    public static UIManager.LookAndFeelInfo[] allLooksInfo;
    public static String lookName = UIManager.getLookAndFeel() == null ? "Metal" : UIManager.getLookAndFeel().getName();


    public static String[] getLookNames() {
        final String[] names = new String[allLooksInfo.length];
        for (int i = 0; i < names.length; i++) {
            names[i] = allLooksInfo[i].getName();
        }
        return names;
    }

    public static Color currentBackground() {
        final LinkedHashMap<String, Color> hashMap = backgroundsMap();
        for (String key : hashMap.keySet()) {
            if (key.equals(backgroundName)) {
                return hashMap.get(key);
            }
        }
        return null;
    }

    public static String currentBackgroundName() {
        return backgroundName;
    }

    // Todo: implement the black background
    private static LinkedHashMap<String, Color> backgroundsMap() {
        final LinkedHashMap<String, Color> colorMap = new LinkedHashMap<>();
        colorMap.put("Default", null);
        colorMap.put("White", Color.WHITE);
        colorMap.put("Cyan", Color.CYAN);
        colorMap.put("Pink", Color.PINK);
        colorMap.put("Green", Color.GREEN);
        colorMap.put("Yellow", Color.YELLOW);
//        colorMap.put("Black", Color.BLACK);
        return colorMap;
    }

    public static String[] backgroundNames(){
        final Object[] backgrounds = backgroundsMap().keySet().toArray();
        final String[] names = new String[backgrounds.length];
        for (int i = 0; i < backgrounds.length; i++) {
            names[i] = (String) backgrounds[i];
        }
        return names;
    }

    public static String currentLookName(){
        return lookName;
    }


    public static void serialize(){
        final String settings = Globals.joinLines(new Object[]{noVerifyNeeded, confirmExit,
                ToolTipManager.sharedInstance().getInitialDelay(),
                ToolTipManager.sharedInstance().getDismissDelay(),
                lookName, backgroundName});
        Serializer.toDisk(settings, Serializer.inPath("settings.ser"));
    }

    public static void deserialize() {
        final Object obj = Serializer.fromDisk(Serializer.inPath("settings.ser"));
        if (obj == null) {
            App.silenceException("Failed to read Settings.");
        } else {
            final String[] settings = Globals.splitLines(((String) obj));
            noVerifyNeeded = Boolean.parseBoolean(settings[0]);
            confirmExit = Boolean.parseBoolean(settings[1]);
            ToolTipManager.sharedInstance().setInitialDelay(Integer.parseInt(settings[2]));
            ToolTipManager.sharedInstance().setDismissDelay(Integer.parseInt(settings[3]));
            lookName = settings[4];
            backgroundName = settings[5];
        }
    }

}
