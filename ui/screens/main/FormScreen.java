package myweigh.ui.screens;

import myweigh.ui.MainFrame;
import myweigh.ui.components.Stack;
import myweigh.ui.components.UIFactory;
import myweigh.ui.theme.Theme;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import java.awt.Insets;

/**
 * Abstract base for full-page forms (login, sign up, onboarding steps).
 * The form is a centered column: full width on a phone, 460px on a laptop.
 */
public abstract class FormScreen extends JPanel {
    protected static final int FORM_WIDTH = 460;

    protected final MainFrame frame;
    protected final Theme theme = Theme.get();
    protected final Stack form;
    protected final Stack footer;

    protected FormScreen(MainFrame frame) {
        super(new BorderLayout());
        this.frame = frame;
        setBackground(theme.card);
        form = new Stack(14, new Insets(24, 24, 16, 24));
        form.setMaxContentWidth(FORM_WIDTH);
        JScrollPane scroll = UIFactory.scroll(form);
        scroll.getViewport().setBackground(theme.card);
        footer = new Stack(10, new Insets(8, 24, 22, 24));
        footer.setMaxContentWidth(FORM_WIDTH);
        add(scroll, BorderLayout.CENTER);
        add(footer, BorderLayout.SOUTH);
    }
}