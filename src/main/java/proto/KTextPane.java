package proto;

import core.setting.Settings;

import javax.swing.*;
import java.util.ArrayList;

public class KTextPane extends JTextPane implements Preference {
    public static final String PLAIN_TYPE = "text/plain";
    public static final String HTML_TYPE = "text/html";
    public static final ArrayList<KTextPane> TEXT_PANES = new ArrayList<>();


    public KTextPane(String type, String text){
        super();
        setContentType(type);
        setText(text);
        setPreferences();
        if (type.equals(PLAIN_TYPE)) {
            setFont(KFontFactory.createPlainFont(15));
        }
    }

    public static KTextPane htmlFormattedPane(String htmlText){
        final String formattedText = "<!DOCTYPE html> <html> <head> <style>" +
                "body {font-size: 12px; font-family: "+KFontFactory.FONT_NAME+";} </style> </head>" +
                "<body>" + htmlText + "</body>" +
                "</html>";
        return new KTextPane(HTML_TYPE, formattedText);
    }

    public void setPreferences(){
        setBackground(Settings.currentBackground());
        setEditable(false);
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        TEXT_PANES.add(this);
    }

}
