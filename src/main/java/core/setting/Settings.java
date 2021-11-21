package core.setting;

import java.awt.Color;
import java.util.Arrays;
import java.util.LinkedHashMap;

import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;

import core.Board;
import core.utils.App;
import core.utils.Globals;
import core.utils.Serializer;
import proto.KDialog;
import proto.KFrame;
import proto.KPanel;
import proto.KPopupMenu;
import proto.KTextPane;
import utg.Dashboard;

public class Settings {
    private static boolean isVerifyNeeded;
    private static boolean confirmExit;
    private static String backgroundName;
    private static UIManager.LookAndFeelInfo[] LooksInfo;
    private static String lookName;
    private static LinkedHashMap<String, Color> colorMap;


    public static void init() {
        if (Dashboard.isFirst()) {
            isVerifyNeeded = true;
            confirmExit = true;
            backgroundName = Globals.NONE;
            lookName = UIManager.getLookAndFeel() == null ? "Metal" : UIManager.getLookAndFeel().getName();
        } else {
            deserialize();
        }
        
        LooksInfo = UIManager.getInstalledLookAndFeels();

        colorMap = new LinkedHashMap<>();
        colorMap.put("Default", null);
        colorMap.put("White", Color.WHITE);
        colorMap.put("Cyan", Color.CYAN);
        colorMap.put("Pink", Color.PINK);
        colorMap.put("Green", Color.GREEN);
        colorMap.put("Yellow", Color.YELLOW);
        // colorMap.put("Black", Color.BLACK);  Todo: Implement black theme
    }

    public static boolean isVerifyNeeded() {
        return isVerifyNeeded;
    }

    public static void setVerifyNeeded(boolean isVerifyNeeded) {
        Settings.isVerifyNeeded = isVerifyNeeded;
    }

    public static boolean isConfirmExit() {
        return confirmExit;
    }

    public static void setConfirmExit(boolean confirmExit) {
        Settings.confirmExit = confirmExit;
    }

    public static String getBackgroundName() {
        return backgroundName;
    }

    public static void setBackgroundName(String backgroundName) {
        Settings.backgroundName = backgroundName;
        
        KPanel.effectBackgroundChanges();
        KTextPane.effectBackgroundChanges();
    }

    public static void setLookName(String lookName) {
        Settings.lookName = lookName;

        for (UIManager.LookAndFeelInfo lookAndFeelInfo : LooksInfo) {
            if (lookAndFeelInfo.getName().equals(lookName)) {
                try {
                    UIManager.setLookAndFeel(lookAndFeelInfo.getClassName());
                    final KFrame instance = Board.getInstance();
                    SwingUtilities.invokeLater(()-> {
                        SwingUtilities.updateComponentTreeUI(instance);
                        for (KDialog dialog : KDialog.ALL_DIALOGS) {
                            SwingUtilities.updateComponentTreeUI(dialog);
                            dialog.pack();
                        }
                        for (KPopupMenu popupMenu : KPopupMenu.POPUP_MENUS) {
                            SwingUtilities.updateComponentTreeUI(popupMenu);
                            popupMenu.pack();
                        }
                        instance.pack();
                    });
                } catch (Exception e1) {
                    App.reportError(e1);
                }
                break;
            }
        }
    }

    public static String getLookName(){
        return lookName;
    }

    public static UIManager.LookAndFeelInfo[] getLooksInfo() {
        return LooksInfo;
    }

    // Other calls...

    public static String[] getLookNames() {
        final String[] names = new String[LooksInfo.length];
        for (int i = 0; i < names.length; i++) {
            names[i] = LooksInfo[i].getName();
        }
        return names;
    }

    public static String[] getBackgroundNames(){
        final Object[] backgrounds = colorMap.keySet().toArray();
        final String[] names = new String[backgrounds.length];
        for (int i = 0; i < backgrounds.length; i++) {
            names[i] = (String) backgrounds[i];
        }
        return names;
    }

    public static Color getBackground() {
        return backgroundName == null ? null : colorMap.get(backgroundName);
    }


    public static void serialize() {
        final String settings = Globals.joinLines(new Object[]{isVerifyNeeded, confirmExit,
                ToolTipManager.sharedInstance().getInitialDelay(),
                ToolTipManager.sharedInstance().getDismissDelay(),
                lookName, backgroundName});
        Serializer.toDisk(settings, Serializer.inPath("settings.ser"));
    }

    public static void deserialize() {
        final Object obj = Serializer.fromDisk(Serializer.inPath("settings.ser"));
        if (obj != null) {
            try {
                final String[] settings = Globals.splitLines(((String) obj));
                isVerifyNeeded = Boolean.parseBoolean(settings[0]);
                confirmExit = Boolean.parseBoolean(settings[1]);
                ToolTipManager.sharedInstance().setInitialDelay(Integer.parseInt(settings[2]));
                ToolTipManager.sharedInstance().setDismissDelay(Integer.parseInt(settings[3]));
                lookName = settings[4];
                backgroundName = settings[5];
            } catch (Exception e) {
                App.silenceException(e);
            }
        }
    }

}
