package main;

import proto.KFrame;
import proto.KLabel;
import proto.KPanel;

import java.awt.*;

import core.utils.FontFactory;
import core.utils.Globals;

public class Frame {

    public static void main(String[] args) {
        display(new KPanel(new BorderLayout()));
    }

    private static void display(Component c){
        final KFrame frame = new KFrame(Globals.PROJECT_NAME);
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(c);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

}
