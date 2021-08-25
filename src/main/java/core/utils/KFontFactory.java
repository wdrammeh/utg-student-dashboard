package core.utils;

import java.awt.*;

/**
 * Handles font-related operations.
 * Dashboard uses a single font style for all supporting-components
 * as defined by {@link #FONT_NAME} herein.
 */
public abstract class KFontFactory {
    /**
     * The universal Dashboard font style name.
     * All labels, and text-panes (of all kind) must use this for uniformity.
     * In a future release, Dashboard may support runtime modification
     * of this, allowing the user to select from a variety of types.
     */
    public static final String FONT_NAME = "Arial";
    public static final Font BODY_HEAD_FONT = createPlainFont(20);


    /**
     * Creates a plain font with the specified size.
     */
    public static Font createPlainFont(int size){
        return new Font(FONT_NAME, Font.PLAIN, size);
    }

    /**
     * Creates a bold font with the specified size.
     */
    public static Font createBoldFont(int size){
        return new Font(FONT_NAME, Font.BOLD, size);
    }

    /**
     * Creates an italic font with the specified size.
     */
    public static Font createItalicFont(int size){
        return new Font(FONT_NAME, Font.ITALIC, size);
    }

    /**
     * Creates a bold font, that is italic, with the specified size.
     */
    public static Font createBoldItalic(int size){
        return new Font(FONT_NAME, Font.BOLD + Font.ITALIC, size);
    }

}
