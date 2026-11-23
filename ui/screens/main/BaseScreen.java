package myweigh.ui.screens.main;

import myweigh.model.User;
import myweigh.service.Session;
import myweigh.ui.AppShell;
import myweigh.ui.components.Stack;
import myweigh.ui.components.UIFactory;
import myweigh.ui.theme.Theme;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Insets;

/**
 * Abstract base of the five main tabs. Each subclass supplies its own header
 * and body (polymorphism); refresh() re-renders them from the current data.
 */
public abstract class BaseScreen extends JPanel {
    protected final AppShell shell;
    protected final Session session;
    protected boolean compact = true;
    private final Stack body;
    private final JScrollPane scroll;
    private Component header;

    protected BaseScreen(AppShell shell) {
        super(new BorderLayout());
        this.shell = shell;
        this.session = shell.getSession();
        setBackground(Theme.get().background);
        body = new Stack(14);
        body.setMaxContentWidth(UIFactory.MAX_CONTENT_WIDTH);
        scroll = UIFactory.scroll(body);
        add(scroll, BorderLayout.CENTER);
        applyPadding();
    }

    /** Builds the header shown above the scrolling body (may return null). */
    protected abstract Component createHeader();

    /** Adds this screen's content to the body. */
    protected abstract void buildBody(Stack body);

    public final void refresh() {
        if (session.getCurrentUser() == null) {
            return;
        }
        int scrollY = scroll.getVerticalScrollBar().getValue();
        if (header != null) {
            remove(header);
        }
        header = createHeader();
        if (header != null) {
            add(header, BorderLayout.NORTH);
        }
        body.removeAll();
        buildBody(body);
        revalidate();
        repaint();
        SwingUtilities.invokeLater(() -> scroll.getVerticalScrollBar().setValue(scrollY));
    }

    public void setCompact(boolean compact) {
        this.compact = compact;
        applyPadding();
    }

    private void applyPadding() {
        int p = pad();
        body.setPadding(new Insets(p, p, p + 8, p));
    }

    protected int pad() {
        return compact ? 16 : 28;
    }

    protected User user() {
        return session.getCurrentUser();
    }

    protected Theme theme() {
        return Theme.get();
    }
}
