package myweigh.ui.screens.main;

import myweigh.model.User;
import myweigh.ui.AppShell;
import myweigh.ui.components.Avatar;
import myweigh.ui.components.Badge;
import myweigh.ui.components.Formats;
import myweigh.ui.components.Icons;
import myweigh.ui.components.ResponsiveGrid;
import myweigh.ui.components.RowItem;
import myweigh.ui.components.Stack;
import myweigh.ui.components.UIFactory;
import myweigh.ui.dialogs.Dialogs;
import myweigh.ui.dialogs.EditProfileDialog;
import myweigh.ui.dialogs.GoalDialog;

import javax.swing.JComponent;
import java.awt.Component;
import java.awt.Font;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Panel 10 — My Profile (view; edit and delete through the Edit dialog). */
public class ProfileScreen extends BaseScreen {

    public ProfileScreen(AppShell shell) {
        super(shell);
    }

    @Override
    protected Component createHeader() {
        JComponent edit = UIFactory.headerButton(Icons.Kind.EDIT, "Edit", "Edit profile",
                () -> new EditProfileDialog(this, session).showDialog());
        return UIFactory.greenHeader("My Profile", "Personal metrics & fitness goals", pad(), edit);
    }

    @Override
    protected void buildBody(Stack body) {
        User u = user();
        Stack identity = new Stack(8, new Insets(8, 0, 8, 0));
        identity.add(UIFactory.wrapCenter(new Avatar(u.getInitials(), 96, theme().dark, theme().accent)));
        identity.add(UIFactory.centered(UIFactory.label(u.getFullName(), Font.BOLD, 19, theme().text)));
        identity.add(UIFactory.wrapCenter(Badge.success(u.getCategoryName() + " • " + u.getFitnessLevel().getLabel())));
        identity.add(UIFactory.centered(UIFactory.muted(u.getEmail(), 12.5f)));

        List<RowItem> rows = new ArrayList<>();
        rows.add(row(Icons.Kind.PROFILE, "Name", u.getFullName(), null));
        rows.add(row(Icons.Kind.INFO, "Age", String.valueOf(u.getAge()), null));
        rows.add(row(Icons.Kind.RULER, "Height", Formats.number(u.getHeightCm()) + " cm", null));
        rows.add(row(Icons.Kind.SCALE, "Weight", Formats.kg(u.getCurrentWeight()), null));
        rows.add(row(Icons.Kind.PROFILE, "Gender", u.getGender(), null));
        rows.add(row(Icons.Kind.PULSE, "Fitness Level", u.getFitnessLevel().getLabel(), null));
        rows.add(row(Icons.Kind.TARGET, u.getFocusLabel(), u.getFocusDisplay(), null));
        RowItem goal = new RowItem(UIFactory.iconLabel(Icons.Kind.TARGET, 18, theme().primary), "Primary Goal", null,
                UIFactory.label(u.getGoalType().getDisplay(), Font.BOLD, 14, theme().primary), null);
        rows.add(goal);
        rows.add(row(Icons.Kind.CALENDAR, "Weekly Goal", Formats.signed(u.getWeeklyGoalKg(), " kg/week"),
                () -> new GoalDialog(this, session).showDialog()));
        rows.add(row(Icons.Kind.CLOCK, "Workout Frequency", u.getWorkoutDaysPerWeek() + " days/week", this::changeFrequency));

        ResponsiveGrid grid = new ResponsiveGrid(320, 2, 16);
        grid.add(identity);
        grid.add(UIFactory.groupCard(rows));
        body.add(grid);

        body.add(UIFactory.dangerAction("Logout", shell::logout));
    }

    private RowItem row(Icons.Kind icon, String label, String value, Runnable onClick) {
        JComponent trailing;
        if (onClick != null) {
            trailing = UIFactory.hbox(UIFactory.label(value, Font.BOLD, 14, theme().text),
                    UIFactory.iconLabel(Icons.Kind.CHEVRON_RIGHT, 16, theme().muted));
        } else {
            trailing = UIFactory.label(value, Font.BOLD, 14, theme().text);
        }
        return new RowItem(UIFactory.iconLabel(icon, 18, theme().primary), label, null, trailing, onClick);
    }

    private void changeFrequency() {
        User u = user();
        Integer days = Dialogs.choose(this, "Workout Frequency", Arrays.asList(2, 3, 4, 5, 6, 7),
                u.getWorkoutDaysPerWeek(), d -> d + " days per week" + (d == u.getRecommendedDaysPerWeek() ? " (recommended)" : ""));
        if (days != null && days != u.getWorkoutDaysPerWeek()) {
            u.setWorkoutDaysPerWeek(days);
            session.regeneratePlan();
        }
    }
}
