package myweigh.ui.dialogs;

import myweigh.service.InvalidInputException;
import myweigh.service.Session;
import myweigh.ui.components.Icons;
import myweigh.ui.components.InputField;
import myweigh.ui.components.PillButton;
import myweigh.ui.components.Toast;
import myweigh.ui.components.UIFactory;

import java.awt.Component;
import java.awt.Window;

/** Changes the signed-in user's password. */
public class ChangePasswordDialog extends AppDialog {
    private final Session session;
    private final InputField current;
    private final InputField password;
    private final InputField confirm;

    public ChangePasswordDialog(Component parent, Session session) {
        super(parent, "Change Password");
        this.session = session;
        current = new InputField(Icons.Kind.LOCK, "Current password", true, null);
        password = new InputField(Icons.Kind.KEY, "At least 6 characters", true, null);
        confirm = new InputField(Icons.Kind.SHIELD, "Re-enter new password", true, null);
        content.add(UIFactory.fieldGroup("Current Password", current));
        content.add(UIFactory.fieldGroup("New Password", password));
        content.add(UIFactory.fieldGroup("Confirm New Password", confirm));
        confirm.addActionListener(e -> save());

        PillButton cancel = new PillButton("Cancel", PillButton.Style.OUTLINE);
        cancel.addActionListener(e -> dispose());
        PillButton save = new PillButton("Update", PillButton.Style.PRIMARY);
        save.addActionListener(e -> save());
        addFooterButton(cancel);
        addFooterButton(save);
    }

    private void save() {
        try {
            session.getUserManager().changePassword(session.getCurrentUser(), current.getText(),
                    password.getText(), confirm.getText());
            Window owner = getOwner();
            dispose();
            Toast.show(owner, "Password updated");
        } catch (InvalidInputException ex) {
            Dialogs.error(this, ex.getMessage());
        }
    }
}