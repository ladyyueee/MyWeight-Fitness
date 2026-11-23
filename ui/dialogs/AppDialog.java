package myweigh.ui.dialogs;

import myweigh.ui.components.Icons;
import myweigh.ui.components.Stack;
import myweigh.ui.components.UIFactory;
import myweigh.ui.theme.Theme;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.KeyEvent;

/**
 * Base class for every dialog: themed header with a close button, scrollable
 * content and a footer for buttons. The width adapts to the window, so dialogs
 * fit on phone-sized windows too.
 */
public abstract class AppDialog extends JDialog {
    protected final Stack content;
    private final JPanel header;
    private final JPanel footer;

    protected AppDialog(Component parent, String title) {
        super(parent instanceof Window ? (Window) parent : SwingUtilities.getWindowAncestor(parent),
                ModalityType.APPLICATION_MODAL);
        Theme t = Theme.get();
        setUndecorated(true);
        setTitle(title);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(t.card);
        root.setBorder(new LineBorder(t.border, 1));

        header = new JPanel(new BorderLayout(10, 0));
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(16, 20, 8, 14));
        header.add(UIFactory.label(title, Font.BOLD, 18, t.text), BorderLayout.CENTER);
        JComponent close = UIFactory.iconLabel(Icons.Kind.CLOSE, 20, t.muted);
        close.setToolTipText("Close");
        UIFactory.makeClickable(close, this::dispose);
        header.add(close, BorderLayout.EAST);

        content = new Stack(12, new Insets(8, 20, 16, 20));
        JScrollPane scroll = UIFactory.scroll(content);
        scroll.getViewport().setBackground(t.card);

        footer = new JPanel(new GridLayout(1, 0, 10, 0));
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(10, 20, 18, 20));

        root.add(header, BorderLayout.NORTH);
        root.add(scroll, BorderLayout.CENTER);
        root.add(footer, BorderLayout.SOUTH);
        setContentPane(root);

        getRootPane().registerKeyboardAction(e -> dispose(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    protected void addFooterButton(JButton button) {
        footer.add(button);
    }

    protected int dialogWidth() {
        return 460;
    }

    /** Sizes the dialog to its content (within the owner window) and shows it. */
    public void showDialog() {
        Window owner = getOwner();
        int ownerWidth = owner != null && owner.getWidth() > 0 ? owner.getWidth() : 800;
        int ownerHeight = owner != null && owner.getHeight() > 0 ? owner.getHeight() : 700;
        int width = Math.min(dialogWidth(), ownerWidth - 24);
        footer.setVisible(footer.getComponentCount() > 0);
        setSize(width, 300);
        validate();
        int contentHeight = Stack.measure(content, width - 2);
        int footerHeight = footer.isVisible() ? footer.getPreferredSize().height : 0;
        int height = header.getPreferredSize().height + contentHeight + footerHeight + 4;
        height = Math.max(160, Math.min(height, ownerHeight - 40));
        setSize(width, height);
        setLocationRelativeTo(owner);
        setVisible(true);
    }
}
