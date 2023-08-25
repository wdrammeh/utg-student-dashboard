package main;

import core.first.FirstLaunch;
import proto.KFrame;
import proto.KPanel;

import java.awt.*;

import core.utils.Globals;

public class Min {

    public static void main(String[] args) {
        new FirstLaunch().setVisible(true);
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
