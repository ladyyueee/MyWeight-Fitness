package myweigh.ui;

import myweigh.service.Session;
import myweigh.ui.dialogs.Dialogs;
import myweigh.ui.screens.main.BaseScreen;
import myweigh.ui.screens.main.DashboardScreen;
import myweigh.ui.screens.main.HistoryScreen;
import myweigh.ui.screens.main.PlanScreen;
import myweigh.ui.screens.main.ProfileScreen;
import myweigh.ui.screens.main.SettingsScreen;
import myweigh.ui.theme.Theme;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Main app container. Phone width: bottom navigation bar.
 * Laptop width: sidebar navigation. Switches live when the window is resized.
 */
public class AppShell extends JPanel {
    public static final String HOME = "Home";
    public static final String PLANS = "Plans";
    public static final String PROGRESS = "Progress";
    public static final String PROFILE = "Profile";
    public static final String SETTINGS = "Settings";
    public static final String[] TABS = {HOME, PLANS, PROGRESS, PROFILE, SETTINGS};

    private final MainFrame frame;
    private final Session session;
    private final Map<String, BaseScreen> screens = new LinkedHashMap<>();
    private final CardLayout cards = new CardLayout();
    private final JPanel content = new JPanel(cards);
    private final NavBar bottomNav;
    private final NavBar sideNav;
    private Boolean compact;
    private String current = HOME;

    public AppShell(MainFrame frame) {
        super(new BorderLayout());
        this.frame = frame;
        this.session = frame.getSession();
        setBackground(Theme.get().background);
        content.setBackground(Theme.get().background);

        screens.put(HOME, new DashboardScreen(this));
        screens.put(PLANS, new PlanScreen(this));
        screens.put(PROGRESS, new HistoryScreen(this));
        screens.put(PROFILE, new ProfileScreen(this));
        screens.put(SETTINGS, new SettingsScreen(this));
        for (Map.Entry<String, BaseScreen> e : screens.entrySet()) {
            content.add(e.getValue(), e.getKey());
        }
        add(content, BorderLayout.CENTER);

        bottomNav = new NavBar(this, false);
        sideNav = new NavBar(this, true);
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                updateLayoutMode(getWidth());
            }
        });
        session.setChangeListener(this::refreshCurrent);
        int width = frame.getContentPane().getWidth() > 0 ? frame.getContentPane().getWidth() : frame.getWidth();
        updateLayoutMode(width);
    }

    private void updateLayoutMode(int width) {
        if (width <= 0) {
            return;
        }
        boolean isCompact = width < MainFrame.COMPACT_WIDTH;
        if (compact != null && compact == isCompact) {
            return;
        }
        compact = isCompact;
        remove(bottomNav);
        remove(sideNav);
        if (isCompact) {
            add(bottomNav, BorderLayout.SOUTH);
        } else {
            add(sideNav, BorderLayout.WEST);
        }
        for (BaseScreen s : screens.values()) {
            s.setCompact(isCompact);
        }
        screens.get(current).refresh();
        revalidate();
        repaint();
    }

    public void select(String tab) {
        current = tab;
        cards.show(content, tab);
        bottomNav.setSelected(tab);
        sideNav.setSelected(tab);
        screens.get(tab).refresh();
    }

    /** Opens the Plans tab on Recommended (0), Custom (1) or Goals (2). */
    public void openPlans(int tab) {
        ((PlanScreen) screens.get(PLANS)).setTab(tab);
        select(PLANS);
    }

    public void refreshCurrent() {
        sideNav.refreshUser();
        screens.get(current).refresh();
    }

    public void logout() {
        if (Dialogs.confirm(this, "Log Out", "Are you sure you want to log out?", "Log Out", true)) {
            session.logout();
            frame.showLogin();
        }
    }

    public boolean isCompact() {
        return compact == null || compact;
    }

    public String getCurrentTab() {
        return current;
    }

    public MainFrame getFrame() {
        return frame;
    }

    public Session getSession() {
        return session;
    }
}
