package myweigh.ui.components;

import javax.swing.JTextArea;
import java.awt.Color;
import java.awt.Font;

/** Read-only text that wraps to the available width. */
public class WrapText extends JTextArea {

    public WrapText(String text, Font font, Color color) {
        super(text);
        setLineWrap(true);
        setWrapStyleWord(true);
        setEditable(false);
        setFocusable(false);
        setOpaque(false);
        setBorder(null);
        setHighlighter(null);
        setFont(font);
        setForeground(color);
    }
}
