package myweigh.ui.screens.auth;

import myweigh.service.InputValidator;
import myweigh.service.InvalidInputException;
import myweigh.service.UserManager;
import myweigh.ui.MainFrame;
import myweigh.ui.components.Icons;
import myweigh.ui.components.InputField;
import myweigh.ui.components.PillButton;
import myweigh.ui.components.UIFactory;
import myweigh.ui.dialogs.Dialogs;
import myweigh.ui.screens.FormScreen;
import myweigh.ui.screens.onboarding.OnboardingDraft;

import javax.swing.JPanel;
import java.awt.FlowLayout;

/** Panel 01 — Create Account. */
public class CreateAccountScreen extends FormScreen {
    private final OnboardingDraft draft;
    private final InputField fullName;
    private final InputField email;
    private final InputField password;
    private final InputField confirm;

    public CreateAccountScreen(MainFrame frame, OnboardingDraft existing) {
        super(frame);
        this.draft = existing != null ? existing : new OnboardingDraft();

        form.add(UIFactory.brandRow());
        form.add(UIFactory.gap(6));
        form.add(UIFactory.title("Create Account", 28));
        form.add(UIFactory.muted("Start your journey today!", 14));

        fullName = new InputField(Icons.Kind.PROFILE, "Juan Dela Cruz", false, null);
        email = new InputField(Icons.Kind.MAIL, "you@example.com", false, null);
        password = new InputField(Icons.Kind.LOCK, "At least 6 characters", true, null);
        confirm = new InputField(Icons.Kind.SHIELD, "Re-enter your password", true, null);
        if (draft.getFullName() != null) {
            fullName.setText(draft.getFullName());
            email.setText(draft.getEmail());
        }

        form.add(UIFactory.fieldGroup("Full Name", fullName));
        form.add(UIFactory.fieldGroup("Email Address", email));
        form.add(UIFactory.fieldGroup("Password", password));
        form.add(UIFactory.fieldGroup("Confirm Password", confirm));

        PillButton signUp = new PillButton("Sign Up", PillButton.Style.PRIMARY);
        signUp.addActionListener(e -> submit());
        confirm.addActionListener(e -> submit());
        form.add(UIFactory.gap(2));
        form.add(signUp);

        JPanel login = UIFactory.transparent(new FlowLayout(FlowLayout.CENTER, 4, 0));
        login.add(UIFactory.muted("Already have an account?", 13));
        login.add(UIFactory.link("Log In", frame::showLogin));
        footer.add(login);
    }

    private void submit() {
        try {
            String name = InputValidator.requireText(fullName.getText(), "Full name");
            if (name.length() > 60) {
                throw new InvalidInputException("Full name must be 60 characters or fewer.");
            }
            String mail = InputValidator.requireEmail(email.getText());
            if (frame.getSession().getUserManager().emailExists(mail)) {
                throw new InvalidInputException("An account with this email already exists. Please log in instead.");
            }
            InputValidator.requirePassword(password.getText(), confirm.getText());
            draft.setAccount(name, mail, UserManager.hashPassword(password.getText()));
            frame.showOnboardingStep(1, draft);
        } catch (InvalidInputException ex) {
            Dialogs.error(this, ex.getMessage());
        }
    }
}