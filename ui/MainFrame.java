package myweigh.ui;

import myweigh.AppInfo;
import myweigh.model.User;
import myweigh.model.Workout;
import myweigh.service.InvalidInputException;
import myweigh.service.Session;
import myweigh.ui.components.Toast;
import myweigh.ui.dialogs.Dialogs;
import myweigh.ui.screens.auth.CreateAccountScreen;
import myweigh.ui.screens.auth.LoginScreen;
import myweigh.ui.screens.onboarding.BmiScreen;
import myweigh.ui.screens.onboarding.CategoryScreen;
import myweigh.ui.screens.onboarding.FitnessLevelScreen;
import myweigh.ui.screens.onboarding.OnboardingDraft;
import myweigh.ui.screens.onboarding.WeightGoalScreen;
import myweigh.ui.theme.Theme;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.time.LocalDate;

/** The application window. Swaps between the auth/onboarding screens and the main app shell. */
public class MainFrame extends JFrame {
    /** Below this content width the app uses the phone layout (bottom navigation, one column). */
    public static final int COMPACT_WIDTH = 760;

    private final Session session;
    private final JPanel root = new JPanel(new BorderLayout());
    private AppShell shell;

    public MainFrame(Session session, boolean phonePreview) {
        super(AppInfo.NAME + " — " + AppInfo.TAGLINE);
        this.session = session;
        setContentPane(root);
        setIconImage(createIconImage());
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                session.save();
                dispose();
                System.exit(0);
            }
        });

        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        boolean phone = phonePreview || screen.width < 900;
        int width = phone ? Math.min(400, screen.width) : Math.min(1280, screen.width - 80);
        int height = phone ? Math.min(860, screen.height - 60) : Math.min(820, screen.height - 80);
        setSize(width, height);
        setMinimumSize(new Dimension(340, 560));
        setLocationRelativeTo(null);
        showLogin();
    }

    public Session getSession() {
        return session;
    }

    /** Finds the main frame that owns a component (also through nested dialogs). */
    public static MainFrame of(Component c) {
        Window w = c instanceof Window ? (Window) c : SwingUtilities.getWindowAncestor(c);
        while (w != null && !(w instanceof MainFrame)) {
            w = w.getOwner();
        }
        return (MainFrame) w;
    }

    private void show(JComponent screen) {
        root.removeAll();
        root.add(screen, BorderLayout.CENTER);
        root.revalidate();
        root.repaint();
    }

    public void showLogin() {
        shell = null;
        Theme.apply("Emerald");
        show(new LoginScreen(this));
    }

    public void showCreateAccount(OnboardingDraft draft) {
        show(new CreateAccountScreen(this, draft));
    }

    public void showOnboardingStep(int step, OnboardingDraft draft) {
        switch (step) {
            case 1:
                show(new BmiScreen(this, draft));
                break;
            case 2:
                show(new WeightGoalScreen(this, draft));
                break;
            case 3:
                show(new CategoryScreen(this, draft));
                break;
            default:
                show(new FitnessLevelScreen(this, draft));
                break;
        }
    }

    /** Saves the new account, generates the plan and opens the dashboard. */
    public void finishOnboarding(User user) {
        try {
            session.getUserManager().addUser(user);
        } catch (InvalidInputException e) {
            Dialogs.error(this, e.getMessage());
            showCreateAccount(null);
            return;
        }
        session.login(user);
        showApp(AppShell.HOME);
        Toast.show(this, "Your personalized fitness plan is ready!");
    }

    /** Called after a successful login. */
    public void enterApp() {
        showApp(AppShell.HOME);
        User u = session.getCurrentUser();
        if (u.isNotificationsOn()) {
            LocalDate today = LocalDate.now();
            int pending = 0;
            for (Workout w : u.getFitnessPlan().getWorkoutsFor(today)) {
                if (!u.getWorkoutHistory().isDone(today, w.getWorkoutName())) {
                    pending++;
                }
            }
            int activities = u.getActivitiesOn(today).size();
            String message = pending == 0 && activities == 0 ? "Welcome back! No workouts left for today."
                    : "Reminder: " + pending + " exercise(s) and " + activities + " activity(ies) today";
            Toast.show(this, message);
        }
    }

    public void showApp(String tab) {
        Theme.apply(session.getCurrentUser().getThemeName());
        shell = new AppShell(this);
        show(shell);
        shell.select(tab);
    }

    /** Rebuilds the app after a theme change, keeping the current tab. */
    public void rebuildApp() {
        showApp(shell != null ? shell.getCurrentTab() : AppShell.HOME);
    }

    private static BufferedImage createIconImage() {
        int size = 64;
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setPaint(new GradientPaint(0, 0, new Color(0x0F7B5C), size, size, new Color(0x0B5E46)));
        g.fillRoundRect(0, 0, size, size, 20, 20);
        g.setColor(Color.WHITE);
        g.setFont(Theme.font(Font.BOLD, 24));
        FontMetrics fm = g.getFontMetrics();
        g.drawString("MW", (size - fm.stringWidth("MW")) / 2, (size - fm.getHeight()) / 2 + fm.getAscent());
        g.dispose();
        return img;
    }
}
