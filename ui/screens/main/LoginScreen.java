package myweigh.ui.screens.auth;

import myweigh.AppInfo;
import myweigh.model.User;
import myweigh.service.InvalidInputException;
import myweigh.ui.MainFrame;
import myweigh.ui.components.Icons;
import myweigh.ui.components.InputField;
import myweigh.ui.components.PillButton;
import myweigh.ui.components.UIFactory;
import myweigh.ui.dialogs.Dialogs;
import myweigh.ui.dialogs.ForgotPasswordDialog;
import myweigh.ui.screens.FormScreen;

import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.FlowLayout;
import java.awt.Font;

/** Panel 02 — Welcome Back (login). */
public class LoginScreen extends FormScreen {
    private final InputField identifier;
    private final InputField password;

    public LoginScreen(MainFrame frame) {
        super(frame);
        form.add(UIFactory.gap(16));
        form.add(UIFactory.wrapCenter(UIFactory.appIcon(68)));
        form.add(UIFactory.centered(UIFactory.label(AppInfo.NAME.toUpperCase(), Font.BOLD, 18, theme.text)));
        JLabel tagline = UIFactory.centered(UIFactory.muted(AppInfo.TAGLINE, 12));
        form.add(tagline);
        form.add(UIFactory.gap(18));
        form.add(UIFactory.title("Welcome Back!", 28));
        form.add(UIFactory.muted("Log in to continue your fitness journey", 14));
        form.add(UIFactory.gap(4));

        identifier = new InputField(Icons.Kind.PROFILE, "you@example.com", false, null);
        password = new InputField(Icons.Kind.LOCK, "Enter your password", true, null);
        form.add(UIFactory.fieldGroup("Username or Email", identifier));
        form.add(UIFactory.fieldGroup("Password", password));

        JPanel forgotRow = UIFactory.transparent(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        forgotRow.add(UIFactory.link("Forgot Password?", () -> new ForgotPasswordDialog(this, frame.getSession()).showDialog()));
        form.add(forgotRow);

        PillButton login = new PillButton("Login", PillButton.Style.PRIMARY);
        login.addActionListener(e -> doLogin());
        form.add(login);
        identifier.addActionListener(e -> password.getField().requestFocusInWindow());
        password.addActionListener(e -> doLogin());

        JPanel signUp = UIFactory.transparent(new FlowLayout(FlowLayout.CENTER, 4, 0));
        signUp.add(UIFactory.muted("Don't have an account?", 13));
        signUp.add(UIFactory.link("Sign Up", () -> frame.showCreateAccount(null)));
        footer.add(signUp);
    }

    private void doLogin() {
        try {
            User user = frame.getSession().getUserManager().authenticate(identifier.getText(), password.getText());
            frame.getSession().login(user);
            frame.enterApp();
        } catch (InvalidInputException ex) {
            Dialogs.error(this, ex.getMessage());
        }
    }
}