package core;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
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
        final HashMap<String, Object> coreMap = new HashMap<>();
        coreMap.put("verificationUnneeded", noVerifyNeeded);
        coreMap.put("directLeave", confirmExit);
        coreMap.put("tipInitialDelay", ToolTipManager.sharedInstance().getInitialDelay());
        coreMap.put("tipDismissDelay", ToolTipManager.sharedInstance().getDismissDelay());
        coreMap.put("lafName", lookName);
        coreMap.put("bgName", backgroundName);
        Serializer.toDisk(coreMap, "settings.ser");
    }

    public static void deserialize() {
        final HashMap<String, Object> coreMap = (HashMap) Serializer.fromDisk("settings.ser");
        if (coreMap == null) {
            App.silenceException("Error reading Settings.");
            return;
        }
        noVerifyNeeded = (boolean) coreMap.get("verificationUnneeded");
        confirmExit = (boolean) coreMap.get("directLeave");
        backgroundName = (String) coreMap.get("bgName");
        lookName = (String) coreMap.get("lafName");
        ToolTipManager.sharedInstance().setInitialDelay((int) coreMap.get("tipInitialDelay"));
        ToolTipManager.sharedInstance().setDismissDelay((int) coreMap.get("tipDismissDelay"));
    }

}
