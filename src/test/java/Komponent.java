import proto.KFrame;
import proto.KLabel;
import proto.KPanel;

import java.awt.*;

public class Komponent {

    public static void main(String[] args) {
        display(new KPanel(new KLabel("Muhammed W. Drammeh")));
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
