package proto;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * This, like the KTable type, may always be surrounded in a KScrollPane.
 * Like the {@link KTextField} types, it does not accept pasting in its control areas.
 */
public class KTextArea extends JTextArea implements Preference {

    public KTextArea(){
        super();
        setPreferences();
    }

    /**
     * Use a TextArea with limited character entry as specified by the limit.
     */
    public static KTextArea getLimitedEntryArea(int limit){
        final KTextArea controlArea = new KTextArea(){
            @Override
            public void paste() {
                UIManager.getLookAndFeel().provideErrorFeedback(this);
            }
        };
        controlArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (controlArea.getText().length() >= limit) {
                    e.consume();
                }
            }
        });
        return controlArea;
    }

    /**
     * Surround TextAreas with this.
     * If used, border modifications should be done on it, and not the textArea itself,
     * because, to avoid duplicity, the area is assigned an empty border.
     * The scrollPane will use the given dimension as its preferred-size,
     * and hides its vertical bar forever.
     */
    public KScrollPane outerScrollPane(Dimension dimension){
        setBorder(BorderFactory.createEmptyBorder());
        final KScrollPane scrollPane = new KScrollPane(this, dimension);
        scrollPane.setVerticalScrollBarPolicy(KScrollPane.VERTICAL_SCROLLBAR_NEVER);
        return scrollPane;
    }

    public void setPreferences() {
        setFont(KFontFactory.createPlainFont(15));
        setLineWrap(true);
        setWrapStyleWord(true);
        setAutoscrolls(true);
    }

}
