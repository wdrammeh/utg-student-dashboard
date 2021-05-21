import proto.KFrame;

import java.awt.*;

public class Display {

    public static void main(String[] args) {

    }

    private static void display(Component c){
        final KFrame frame = new KFrame("Component Test");
//        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(c);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

}
