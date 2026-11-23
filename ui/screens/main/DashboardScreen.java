package myweigh.ui.screens.main;

import myweigh.model.Exercise;
import myweigh.model.FitnessPlan;
import myweigh.model.PainEntry;
import myweigh.model.RehabPatient;
import myweigh.model.ScheduledActivity;
import myweigh.model.Timeframes;
import myweigh.model.User;
import myweigh.model.Workout;
import myweigh.ui.AppShell;
import myweigh.ui.components.Avatar;
import myweigh.ui.components.Badge;
import myweigh.ui.components.Formats;
import myweigh.ui.components.IconBadge;
import myweigh.ui.components.Icons;
import myweigh.ui.components.ResponsiveGrid;
import myweigh.ui.components.RoundedPanel;
import myweigh.ui.components.RowItem;
import myweigh.ui.components.SlimProgressBar;
import myweigh.ui.components.Stack;
import myweigh.ui.components.UIFactory;
import myweigh.ui.dialogs.CalorieDialog;
import myweigh.ui.dialogs.Dialogs;
import myweigh.ui.dialogs.ExerciseDialog;
import myweigh.ui.dialogs.LibraryDialog;
import myweigh.ui.dialogs.PainDialog;
import myweigh.ui.dialogs.ScheduleDialog;
import myweigh.ui.dialogs.WaterDialog;
import myweigh.ui.dialogs.WeightLogDialog;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Panel 07 — Dashboard / Home: BMI, calories, water, workout progress and today's activities. */
public class DashboardScreen extends BaseScreen {

    public DashboardScreen(AppShell shell) {
        super(shell);
    }

    @Override
    protected Component createHeader() {
        User u = user();
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(theme().card);
        bar.setBorder(new MatteBorder(0, 0, 1, 0, theme().border));
        Stack inner = new Stack(0, new Insets(12, pad(), 12, pad()));
        inner.setMaxContentWidth(UIFactory.MAX_CONTENT_WIDTH);
        JComponent left;
        if (compact) {
            left = UIFactory.brandRow();
        } else {
            JPanel text = UIFactory.transparent(new GridLayout(0, 1, 0, 2));
            text.add(UIFactory.title("Dashboard", 22));
            text.add(UIFactory.muted(Formats.LONG.format(LocalDate.now()), 12.5f));
            left = text;
        }
        inner.add(UIFactory.row(left, userChip(u)));
        bar.add(inner, BorderLayout.CENTER);
        return bar;
    }

    private JComponent userChip(User u) {
        RoundedPanel chip = new RoundedPanel(new BorderLayout(8, 0), theme().mint, theme().mintBorder, 30);
        chip.setBorder(new EmptyBorder(4, 5, 4, 10));
        chip.add(new Avatar(u.getFirstName().substring(0, 1).toUpperCase(), 26, theme().accent, null), BorderLayout.WEST);
        JPanel text = UIFactory.transparent(new GridLayout(0, 1));
        text.add(UIFactory.label(u.getFirstName(), Font.BOLD, 12, theme().text));
        text.add(UIFactory.label(u.getCategoryName(), Font.PLAIN, 10, theme().muted));
        chip.add(text, BorderLayout.CENTER);
        chip.add(UIFactory.iconLabel(Icons.Kind.CHEVRON_RIGHT, 14, theme().muted), BorderLayout.EAST);
        UIFactory.makeClickable(chip, () -> shell.select(AppShell.PROFILE));
        return chip;
    }

    @Override
    protected void buildBody(Stack body) {
        User u = user();
        LocalDate today = LocalDate.now();
        body.add(welcomeCard(u));

        List<JComponent> stats = new ArrayList<>();
        stats.add(bmiCard(u));
        stats.add(calorieCard(u, today));
        stats.add(waterCard(u, today));
        stats.add(progressCard(u, today));
        if (u.requiresPainCheck()) {
            stats.add(painCard((RehabPatient) u));
        }
        ResponsiveGrid grid = new ResponsiveGrid(150, stats.size(), 12);
        for (JComponent c : stats) {
            grid.add(c);
        }
        body.add(grid);

        body.add(activitiesCard(u, today));
        body.add(goalCard(u));
        body.add(UIFactory.sectionRow("Recommended Exercises",
                UIFactory.link("Exercise Library", () -> new LibraryDialog(this, session, 0).showDialog())));
        body.add(recommendedGrid(u, today));
    }

    private JComponent welcomeCard(User u) {
        RoundedPanel card = new RoundedPanel(new BorderLayout(12, 0), null, null, 20);
        card.setGradient(theme().primary, theme().primaryDark);
        card.setBorder(new EmptyBorder(18, 20, 18, 18));
        JPanel text = UIFactory.transparent(new GridLayout(0, 1, 0, 4));
        text.add(UIFactory.label("Welcome, " + u.getFirstName() + "!", Font.BOLD, 20, Color.WHITE));
        text.add(UIFactory.label("Small steps. Big results.", Font.PLAIN, 13, new Color(255, 255, 255, 220)));
        card.add(text, BorderLayout.CENTER);
        IconBadge flame = new IconBadge(Icons.Kind.FLAME, 44, 22, new Color(255, 255, 255, 45), theme().accent, false);
        flame.setToolTipText("Your streak");
        UIFactory.makeClickable(flame, this::showStreak);
        card.add(UIFactory.wrapCenter(flame), BorderLayout.EAST);
        return card;
    }

    private void showStreak() {
        int streak = user().getWorkoutHistory().getStreak(LocalDate.now());
        Dialogs.info(this, "Your Streak", streak > 0
                ? "You're on a " + streak + "-day activity streak. Keep the momentum going!"
                : "Complete a workout or activity today to start a new streak.");
    }

    private JComponent statCard(String title, Icons.Kind icon, Color iconColor, JComponent value,
                                JComponent footer, Runnable onClick) {
        RoundedPanel card = UIFactory.card(new BorderLayout(0, 8));
        card.setBorder(new EmptyBorder(14, 14, 14, 14));
        card.add(UIFactory.row(UIFactory.label(title, Font.PLAIN, 12.5f, theme().muted),
                UIFactory.iconLabel(icon, 18, iconColor)), BorderLayout.NORTH);
        card.add(value, BorderLayout.CENTER);
        if (footer != null) {
            card.add(footer, BorderLayout.SOUTH);
        }
        UIFactory.makeClickable(card, onClick);
        return card;
    }

    private JComponent valueOf(String main, String suffix) {
        JLabel big = UIFactory.label(main, Font.BOLD, 22, theme().text);
        if (suffix == null) {
            return UIFactory.hbox(big);
        }
        return UIFactory.hbox(big, UIFactory.muted(suffix, 13));
    }

    private JComponent bmiCard(User u) {
        JComponent value = UIFactory.hbox(UIFactory.label(Formats.one(u.calculateBMI()), Font.BOLD, 22, theme().text),
                Badge.forBmi(u.getBmiCategory()));
        return statCard("BMI Result", Icons.Kind.PULSE, theme().primary, value,
                UIFactory.muted(Formats.kg(u.getCurrentWeight()) + " • " + Formats.number(u.getHeightCm()) + " cm", 11.5f),
                () -> new WeightLogDialog(this, session).showDialog());
    }

    private JComponent calorieCard(User u, LocalDate today) {
        FitnessPlan plan = u.getFitnessPlan();
        return statCard("Daily Calories", Icons.Kind.FLAME, theme().accent,
                valueOf(Formats.thousands(plan.getCalories(today)), "kcal"),
                UIFactory.muted("Goal: " + Formats.kcal(plan.getDailyCalories()), 11.5f),
                () -> new CalorieDialog(this, session).showDialog());
    }

    private JComponent waterCard(User u, LocalDate today) {
        FitnessPlan plan = u.getFitnessPlan();
        int water = plan.getWaterIntake(today);
        int target = Math.max(1, plan.getWaterTargetGlasses());
        return statCard("Water Intake", Icons.Kind.DROP, theme().water,
                valueOf(String.valueOf(water), "/ " + target + " glasses"),
                new SlimProgressBar((double) water / target, theme().water, 6),
                () -> new WaterDialog(this, session).showDialog());
    }

    private JComponent progressCard(User u, LocalDate today) {
        List<Workout> workouts = u.getFitnessPlan().getWorkoutsFor(today);
        int done = 0;
        for (Workout w : workouts) {
            if (u.getWorkoutHistory().isDone(today, w.getWorkoutName())) {
                done++;
            }
        }
        if (workouts.isEmpty()) {
            return statCard("Workout Progress", Icons.Kind.DUMBBELL, theme().primary, valueOf("Rest", null),
                    UIFactory.muted("Rest day", 11.5f), () -> shell.openPlans(0));
        }
        return statCard("Workout Progress", Icons.Kind.DUMBBELL, theme().primary,
                valueOf(String.valueOf(done), "/ " + workouts.size()),
                new SlimProgressBar((double) done / workouts.size(), theme().primary, 6), () -> shell.openPlans(0));
    }

    private JComponent painCard(RehabPatient patient) {
        PainEntry latest = patient.getLatestPain();
        JComponent value = latest == null ? valueOf("—", null) : valueOf(String.valueOf(latest.getLevel()), "/ 10");
        JComponent footer;
        if (latest == null) {
            footer = UIFactory.muted("Tap to record", 11.5f);
        } else if (latest.getLevel() > RehabPatient.SAFE_PAIN_LIMIT) {
            footer = UIFactory.hbox(Badge.danger(PainEntry.describe(latest.getLevel()).toUpperCase()));
        } else {
            footer = UIFactory.hbox(Badge.success(PainEntry.describe(latest.getLevel()).toUpperCase()));
        }
        return statCard("Pain Level", Icons.Kind.HEART, theme().danger, value, footer,
                () -> new PainDialog(this, session).showDialog());
    }

    private JComponent activitiesCard(User u, LocalDate today) {
        RoundedPanel card = UIFactory.card(new BorderLayout());
        card.setBorder(new EmptyBorder(14, 16, 6, 16));
        Stack s = new Stack(0);
        s.add(UIFactory.row(UIFactory.label("Today's Activities", Font.BOLD, 15, theme().text),
                UIFactory.label(Formats.SHORT.format(today), Font.BOLD, 12, theme().primary)));

        List<RowItem> rows = new ArrayList<>();
        for (ScheduledActivity a : u.getActivitiesOn(today)) {
            JLabel toggle = (JLabel) UIFactory.statusIcon(a.isCompleted());
            toggle.putClientProperty(UIFactory.OWN_CLICK, Boolean.TRUE);
            toggle.setToolTipText(a.isCompleted() ? "Mark as not done" : "Mark as done");
            toggle.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
            toggle.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    session.setActivityCompleted(a, !a.isCompleted());
                }
            });
            rows.add(new RowItem(UIFactory.iconLabel(Icons.Kind.RUN, 20, theme().primary), a.getTitle(),
                    a.getDurationMinutes() + " mins • " + Formats.TIME.format(a.getTime()), toggle,
                    () -> new ScheduleDialog(this, session, a).showDialog()));
        }
        List<Workout> workouts = u.getFitnessPlan().getWorkoutsFor(today);
        for (Workout w : workouts) {
            boolean done = u.getWorkoutHistory().isDone(today, w.getWorkoutName());
            rows.add(new RowItem(UIFactory.iconLabel(Icons.Kind.BOLT, 20, theme().primary), w.getWorkoutName(),
                    w.getSummary(), UIFactory.statusIcon(done),
                    () -> new ExerciseDialog(this, session, w.getExercise(), w).showDialog()));
        }
        if (workouts.isEmpty()) {
            rows.add(new RowItem(UIFactory.iconLabel(Icons.Kind.CLOCK, 20, theme().primary), "Rest Day",
                    "Recovery is part of your plan", null, () -> shell.openPlans(0)));
        }
        FitnessPlan plan = u.getFitnessPlan();
        int water = plan.getWaterIntake(today);
        rows.add(new RowItem(UIFactory.iconLabel(Icons.Kind.GLASS, 20, theme().primary), "Hydration",
                water + " of " + plan.getWaterTargetGlasses() + " glasses",
                UIFactory.statusIcon(water >= plan.getWaterTargetGlasses()),
                () -> new WaterDialog(this, session).showDialog()));
        for (int i = 0; i < rows.size(); i++) {
            rows.get(i).setDivider(i < rows.size() - 1);
            s.add(rows.get(i));
        }
        card.add(s, BorderLayout.CENTER);
        return card;
    }

    private JComponent goalCard(User u) {
        RoundedPanel card = UIFactory.tintedCard(new BorderLayout(10, 0));
        card.setBorder(new EmptyBorder(14, 16, 14, 16));
        JPanel text = UIFactory.transparent(new GridLayout(0, 1, 0, 3));
        text.add(UIFactory.label("YOUR GOAL", Font.BOLD, 11, theme().primary));
        JLabel main = new JLabel("<html><b>" + u.getGoalType().getDisplay() + "</b> <span style='color:"
                + Formats.hex(theme().muted) + "'>• " + Formats.kg(u.getTargetWeight()) + " ("
                + Timeframes.abbreviated(u.getTimeframeWeeks()) + ")</span></html>");
        main.setFont(myweigh.ui.theme.Theme.font(Font.PLAIN, 15));
        main.setForeground(theme().text);
        text.add(main);
        card.add(text, BorderLayout.CENTER);
        card.add(UIFactory.iconLabel(Icons.Kind.CHEVRON_RIGHT, 18, theme().muted), BorderLayout.EAST);
        UIFactory.makeClickable(card, () -> shell.openPlans(2));
        return card;
    }

    private JComponent recommendedGrid(User u, LocalDate today) {
        FitnessPlan plan = u.getFitnessPlan();
        LocalDate day = today;
        List<Workout> workouts = plan.getWorkoutsFor(today);
        if (workouts.isEmpty()) {
            LocalDate next = plan.nextWorkoutDate(today);
            if (next != null) {
                day = next;
                workouts = plan.getWorkoutsFor(next);
            }
        }
        if (workouts.isEmpty()) {
            return UIFactory.emptyState(Icons.Kind.DUMBBELL, "No exercises yet",
                    "Your recommendations appear here once your plan is generated.");
        }
        ResponsiveGrid grid = new ResponsiveGrid(104, 6, 10);
        final LocalDate planDay = day;
        for (Workout w : workouts) {
            Exercise ex = w.getExercise();
            RoundedPanel tile = UIFactory.card(new BorderLayout());
            tile.setBorder(new EmptyBorder(12, 8, 12, 8));
            Stack s = new Stack(6);
            s.add(UIFactory.wrapCenter(new IconBadge(Icons.forExercise(ex), 40, 20, theme().mint, theme().primary, true)));
            s.add(UIFactory.centered(UIFactory.label(ex.getName(), Font.BOLD, 12, theme().text)));
            s.add(UIFactory.centered(UIFactory.muted(planDay.equals(today) ? ex.getMuscleGroup() : Formats.DAY.format(planDay), 11)));
            tile.add(s, BorderLayout.CENTER);
            tile.setToolTipText(ex.getName());
            UIFactory.makeClickable(tile, () -> new ExerciseDialog(this, session, ex, w).showDialog());
            grid.add(tile);
        }
        return grid;
    }
}
