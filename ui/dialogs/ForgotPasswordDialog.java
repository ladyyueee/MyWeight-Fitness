package myweigh.ui.dialogs;

import myweigh.service.InvalidInputException;
import myweigh.service.Session;
import myweigh.ui.components.Icons;
import myweigh.ui.components.InputField;
import myweigh.ui.components.PillButton;
import myweigh.ui.components.Toast;
import myweigh.ui.components.UIFactory;
import myweigh.ui.components.WrapText;
import myweigh.ui.theme.Theme;

import java.awt.Component;
import java.awt.Font;
import java.awt.Window;

/** Resets a forgotten password after confirming the account's email and full name. */
public class ForgotPasswordDialog extends AppDialog {
    private final Session session;
    private final InputField email;
    private final InputField fullName;
    private final InputField password;
    private final InputField confirm;

    public ForgotPasswordDialog(Component parent, Session session) {
        super(parent, "Reset Password");
        this.session = session;
        content.add(new WrapText("Confirm your email and full name to set a new password.",
                Theme.font(Font.PLAIN, 13), Theme.get().muted));
        email = new InputField(Icons.Kind.MAIL, "you@example.com", false, null);
        fullName = new InputField(Icons.Kind.PROFILE, "Full name on the account", false, null);
        password = new InputField(Icons.Kind.LOCK, "At least 6 characters", true, null);
        confirm = new InputField(Icons.Kind.SHIELD, "Re-enter new password", true, null);
        content.add(UIFactory.fieldGroup("Email Address", email));
        content.add(UIFactory.fieldGroup("Full Name", fullName));
        content.add(UIFactory.fieldGroup("New Password", password));
        content.add(UIFactory.fieldGroup("Confirm New Password", confirm));

        PillButton cancel = new PillButton("Cancel", PillButton.Style.OUTLINE);
        cancel.addActionListener(e -> dispose());
        PillButton reset = new PillButton("Reset", PillButton.Style.PRIMARY);
        reset.addActionListener(e -> reset());
        addFooterButton(cancel);
        addFooterButton(reset);
    }

    private void reset() {
        try {
            session.getUserManager().resetPassword(email.getText(), fullName.getText(), password.getText(), confirm.getText());
            Window owner = getOwner();
            dispose();
            Toast.show(owner, "Password reset. You can log in now.");
        } catch (InvalidInputException ex) {
            Dialogs.error(this, ex.getMessage());
        }
    }
}