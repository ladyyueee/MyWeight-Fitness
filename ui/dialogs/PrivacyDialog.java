package myweigh.ui.dialogs;

import myweigh.service.Session;
import myweigh.ui.MainFrame;
import myweigh.ui.components.PillButton;
import myweigh.ui.components.Toast;
import myweigh.ui.components.UIFactory;
import myweigh.ui.components.WrapText;
import myweigh.ui.theme.Theme;

import java.awt.Component;
import java.awt.Font;

/** Explains how data is stored and offers account deletion. */
public class PrivacyDialog extends AppDialog {
    private final Session session;

    public PrivacyDialog(Component parent, Session session) {
        super(parent, "Privacy & Security");
        this.session = session;
        Theme t = Theme.get();
        content.add(new WrapText(
                "• Your profile, plan and history are stored only on this computer, in the file myweigh_data.ser.\n"
                        + "• Passwords are never stored as plain text; only a SHA-256 hash is saved.\n"
                        + "• No data is sent online, and there are no social or sharing features.\n"
                        + "• You can edit your profile at any time, or delete your account and all its data below.",
                Theme.font(Font.PLAIN, 14), t.text));
        content.add(UIFactory.gap(4));
        content.add(UIFactory.dangerAction("Delete Account", this::deleteAccount));

        PillButton close = new PillButton("Close", PillButton.Style.OUTLINE);
        close.addActionListener(e -> dispose());
        addFooterButton(close);
    }

    private void deleteAccount() {
        if (Dialogs.confirm(this, "Delete account",
                "This permanently deletes your profile, plan and history. This cannot be undone.", "Delete", true)) {
            MainFrame frame = MainFrame.of(this);
            session.deleteCurrentUser();
            dispose();
            frame.showLogin();
            Toast.show(frame, "Your account has been deleted");
        }
    }
}