package main;

import core.first.FirstLaunch;
import core.utils.App;
import proto.KFrame;
import proto.KPanel;

import java.awt.*;

import core.utils.Globals;

public class Min {

    public static void main(String[] args) {
        App.reportInfo("Version Update", "Dashboard has been updated from v1.1.1 to v1.1.2.\n" +
                "Please visit the official Dashboard repository on Github to see what's new about this release.\n-\n" +
                "https://github.com/wdrammeh/utg-student-dashboard/blob/master/ChangeLog.md");
    }

    private static void display(Component c) {
        final KFrame frame = new KFrame(Globals.PROJECT_TITLE);
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(c);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

}
