package myweigh.ui.components;

import myweigh.ui.theme.Theme;

import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/** Rounded text or password field with a leading icon, placeholder, suffix and show/hide toggle. */
public class InputField extends RoundedPanel {
    private static final char ECHO = '•';
    private final JTextField field;
    private boolean revealed;

    public InputField(Icons.Kind icon, String placeholder, boolean password, String suffix) {
        super(new BorderLayout(10, 0), Theme.get().mint, Theme.get().mintBorder, 14);
        Theme t = Theme.get();
        setBorder(new EmptyBorder(0, 14, 0, 14));
        field = password ? new PlaceholderPasswordField(placeholder) : new PlaceholderTextField(placeholder);
        field.setBorder(new EmptyBorder(12, 0, 12, 0));
        field.setOpaque(false);
        field.setFont(Theme.font(Font.PLAIN, 15));
        field.setForeground(t.text);
        field.setCaretColor(t.text);
        field.setSelectionColor(t.mintBorder);
        field.setSelectedTextColor(t.text);
        if (password) {
            ((JPasswordField) field).setEchoChar(ECHO);
        }
        if (icon != null) {
            add(new JLabel(Icons.of(icon, 18, t.primary)), BorderLayout.WEST);
        }
        add(field, BorderLayout.CENTER);

        if (password) {
            JLabel eye = new JLabel(Icons.of(Icons.Kind.EYE_OFF, 18, t.muted));
            eye.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            eye.setToolTipText("Show / hide password");
            eye.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    revealed = !revealed;
                    ((JPasswordField) field).setEchoChar(revealed ? (char) 0 : ECHO);
                    eye.setIcon(Icons.of(revealed ? Icons.Kind.EYE : Icons.Kind.EYE_OFF, 18, t.muted));
                }
            });
            add(eye, BorderLayout.EAST);
        } else if (suffix != null) {
            JLabel s = new JLabel(suffix);
            s.setFont(Theme.font(Font.PLAIN, 13));
            s.setForeground(t.muted);
            add(s, BorderLayout.EAST);
        }

        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                setBorderColor(t.primary);
            }

            @Override
            public void focusLost(FocusEvent e) {
                setBorderColor(t.mintBorder);
            }
        });
    }

    public String getText() {
        if (field instanceof JPasswordField) {
            return new String(((JPasswordField) field).getPassword());
        }
        return field.getText();
    }

    public void setText(String text) {
        field.setText(text);
    }

    public void setEditable(boolean editable) {
        field.setEditable(editable);
        setFill(editable ? Theme.get().mint : Theme.get().subtle);
    }

    public void addActionListener(ActionListener l) {
        field.addActionListener(l);
    }

    public JTextField getField() {
        return field;
    }

    private static void paintPlaceholder(JTextField f, Graphics g, String placeholder) {
        if (placeholder == null || !f.getText().isEmpty()) {
            return;
        }
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setColor(Theme.alpha(Theme.get().muted, 170));
        g2.setFont(f.getFont());
        Insets in = f.getInsets();
        FontMetrics fm = g2.getFontMetrics();
        int y = (f.getHeight() - fm.getHeight()) / 2 + fm.getAscent();
        g2.drawString(placeholder, in.left, y);
        g2.dispose();
    }

    private static final class PlaceholderTextField extends JTextField {
        private final String placeholder;

        PlaceholderTextField(String placeholder) {
            this.placeholder = placeholder;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            paintPlaceholder(this, g, placeholder);
        }
    }

    private static final class PlaceholderPasswordField extends JPasswordField {
        private final String placeholder;

        PlaceholderPasswordField(String placeholder) {
            this.placeholder = placeholder;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            paintPlaceholder(this, g, placeholder);
        }
    }
}
