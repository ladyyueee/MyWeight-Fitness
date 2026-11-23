package myweigh.ui.screens.main;

import myweigh.model.User;
import myweigh.ui.AppShell;
import myweigh.ui.components.Badge;
import myweigh.ui.components.Icons;
import myweigh.ui.components.ResponsiveGrid;
import myweigh.ui.components.RowItem;
import myweigh.ui.components.Stack;
import myweigh.ui.components.UIFactory;
import myweigh.ui.components.Toast;
import myweigh.ui.dialogs.AboutDialog;
import myweigh.ui.dialogs.ChangePasswordDialog;
import myweigh.ui.dialogs.Dialogs;
import myweigh.ui.dialogs.EditProfileDialog;
import myweigh.ui.dialogs.EquipmentDialog;
import myweigh.ui.dialogs.LibraryDialog;
import myweigh.ui.dialogs.PrivacyDialog;
import myweigh.ui.dialogs.ThemeDialog;

import javax.swing.JComponent;
import java.awt.Component;
import java.util.Arrays;
import java.util.List;

/** Panel 11 — Settings & Preferences. */
public class SettingsScreen extends BaseScreen {
    private static final String HELP_TEXT =
            "Getting started\n"
                    + "• Home shows today's BMI, calories, water and workouts. Tap any card to update it.\n"
                    + "• Plans → Recommended lists today's exercises. Open one to see instructions, change its "
                    + "duration, or mark it complete with the weight and reps you did.\n"
                    + "• Plans → Custom lets you schedule your own activities.\n"
                    + "• Plans → Goals tracks your target weight. Log your weight regularly.\n"
                    + "• Progress shows completed and missed workouts, progressive load and your level.\n\n"
                    + "Level advancement\n"
                    + "Complete the required number of exercises with at least 80% completion to move up a level.\n\n"
                    + "Safety\n"
                    + "Stop any exercise that causes sharp pain. Rehabilitation plans do not replace advice from "
                    + "your doctor or physical therapist.";

    public SettingsScreen(AppShell shell) {
        super(shell);
    }

    @Override
    protected Component createHeader() {
        return UIFactory.greenHeader("Settings", "Account, theme customization & system preferences", pad());
    }

    @Override
    protected void buildBody(Stack body) {
        User u = user();
        ResponsiveGrid grid = new ResponsiveGrid(340, 2, 16);

        grid.add(section("Account", Arrays.asList(
                row(Icons.Kind.USER_EDIT, "Edit Profile", chevron(), () -> new EditProfileDialog(this, session).showDialog()),
                row(Icons.Kind.KEY, "Change Password", chevron(), () -> new ChangePasswordDialog(this, session).showDialog()),
                row(Icons.Kind.SHIELD, "Privacy & Security", chevron(), () -> new PrivacyDialog(this, session).showDialog()))));

        grid.add(section("App Preferences", Arrays.asList(
                row(Icons.Kind.PALETTE, "Theme", Badge.success("Switch Theme"), () -> new ThemeDialog(this, session).showDialog()),
                row(Icons.Kind.BELL, "Notifications", u.isNotificationsOn() ? Badge.success("ON") : Badge.neutral("OFF"),
                        this::toggleNotifications),
                row(Icons.Kind.GLOBE, "Language", UIFactory.muted("English", 13), null),
                row(Icons.Kind.RULER, "Units", UIFactory.muted("kg / cm", 13), null))));

        grid.add(section("Fitness", Arrays.asList(
                row(Icons.Kind.DUMBBELL, "Workout Equipment", chevron(), () -> new EquipmentDialog(this, session).showDialog()),
                row(Icons.Kind.BOOK, "Exercise Library", chevron(), () -> new LibraryDialog(this, session, 0).showDialog()),
                row(Icons.Kind.INFO, "Equipment Guide", chevron(), () -> new LibraryDialog(this, session, 1).showDialog()))));

        grid.add(section("About", Arrays.asList(
                row(Icons.Kind.HELP, "Help & Support", chevron(), () -> Dialogs.info(this, "Help & Support", HELP_TEXT)),
                row(Icons.Kind.INFO, "About MyWeigh Fitness", chevron(), () -> new AboutDialog(this).showDialog()))));

        body.add(grid);
        body.add(UIFactory.dangerAction("Log Out", shell::logout));
    }

    private JComponent section(String title, List<RowItem> rows) {
        Stack s = new Stack(8);
        s.add(UIFactory.section(title));
        s.add(UIFactory.groupCard(rows));
        return s;
    }

    private RowItem row(Icons.Kind icon, String title, JComponent trailing, Runnable action) {
        return new RowItem(UIFactory.iconLabel(icon, 20, theme().primary), title, null, trailing, action);
    }

    private JComponent chevron() {
        return UIFactory.iconLabel(Icons.Kind.CHEVRON_RIGHT, 18, theme().muted);
    }

    private void toggleNotifications() {
        User u = user();
        u.setNotificationsOn(!u.isNotificationsOn());
        session.save();
        refresh();
        Toast.show(this, u.isNotificationsOn() ? "Login reminders turned on" : "Login reminders turned off");
    }
}
