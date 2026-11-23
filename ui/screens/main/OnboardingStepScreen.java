package myweigh.ui.screens.onboarding;

import myweigh.ui.MainFrame;
import myweigh.ui.components.IconBadge;
import myweigh.ui.components.Icons;
import myweigh.ui.components.UIFactory;
import myweigh.ui.screens.FormScreen;
import myweigh.ui.theme.Theme;

import javax.swing.JComponent;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/** Abstract onboarding step with back button, progress dots and "n/4". */
public abstract class OnboardingStepScreen extends FormScreen {
    public static final int TOTAL_STEPS = 4;
    protected final OnboardingDraft draft;

    protected OnboardingStepScreen(MainFrame frame, OnboardingDraft draft, int step) {
        super(frame);
        this.draft = draft;
        form.add(stepHeader(step));
    }

    /** Where the back button goes. */
    protected abstract void goBack();

    private JComponent stepHeader(int step) {
        Theme t = Theme.get();
        JPanel row = UIFactory.transparent(new BorderLayout(12, 0));
        IconBadge back = new IconBadge(Icons.Kind.CHEVRON_LEFT, 36, 18, t.subtle, t.text, true);
        back.setToolTipText("Back");
        UIFactory.makeClickable(back, this::goBack);
        row.add(back, BorderLayout.WEST);

        JComponent dots = new JComponent() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = 24;
                int gap = 6;
                int total = TOTAL_STEPS * w + (TOTAL_STEPS - 1) * gap;
                int x = (getWidth() - total) / 2;
                int y = getHeight() / 2 - 3;
                for (int i = 0; i < TOTAL_STEPS; i++) {
                    g2.setColor(i < step ? t.primary : t.border);
                    g2.fillRoundRect(x + i * (w + gap), y, w, 6, 6, 6);
                }
                g2.dispose();
            }
        };
        dots.setPreferredSize(new Dimension(120, 36));
        row.add(dots, BorderLayout.CENTER);
        row.add(UIFactory.label(step + "/" + TOTAL_STEPS, Font.BOLD, 13,
                step == TOTAL_STEPS ? t.primary : t.muted), BorderLayout.EAST);
        return row;
    }

    /** Small MW logo row shown on the first step. */
    protected JComponent miniBrand() {
        JPanel row = UIFactory.transparent(new BorderLayout(10, 0));
        row.add(UIFactory.logoBadge(30), BorderLayout.WEST);
        row.add(UIFactory.label("MYWEIGH FITNESS", Font.BOLD, 13, Theme.get().text), BorderLayout.CENTER);
        return row;
    }
}