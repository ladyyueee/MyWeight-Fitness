package myweigh.ui.screens.main;

import myweigh.model.FitnessPlan;
import myweigh.model.ScheduledActivity;
import myweigh.model.Timeframes;
import myweigh.model.User;
import myweigh.model.WeightEntry;
import myweigh.model.Workout;
import myweigh.model.WorkoutStatus;
import myweigh.service.HealthCalculator;
import myweigh.ui.AppShell;
import myweigh.ui.components.Badge;
import myweigh.ui.components.Formats;
import myweigh.ui.components.IconBadge;
import myweigh.ui.components.Icons;
import myweigh.ui.components.ListCard;
import myweigh.ui.components.PillButton;
import myweigh.ui.components.ResponsiveGrid;
import myweigh.ui.components.RoundedPanel;
import myweigh.ui.components.RowItem;
import myweigh.ui.components.SegmentedControl;
import myweigh.ui.components.SlimProgressBar;
import myweigh.ui.components.Stack;
import myweigh.ui.components.UIFactory;
import myweigh.ui.dialogs.Dialogs;
import myweigh.ui.dialogs.EquipmentDialog;
import myweigh.ui.dialogs.ExerciseDialog;
import myweigh.ui.dialogs.FullPlanDialog;
import myweigh.ui.dialogs.GoalDialog;
import myweigh.ui.dialogs.ScheduleDialog;
import myweigh.ui.dialogs.WeightLogDialog;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/** Panel 08 — My Fitness Plan with Recommended, Custom (scheduler) and Goals tabs. */
public class PlanScreen extends BaseScreen {
    private int tab;

    public PlanScreen(AppShell shell) {
        super(shell);
    }

    public void setTab(int tab) {
        this.tab = tab;
    }

    @Override
    protected Component createHeader() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(theme().card);
        bar.setBorder(new MatteBorder(0, 0, 1, 0, theme().border));
        Stack inner = new Stack(12, new Insets(16, pad(), 14, pad()));
        inner.setMaxContentWidth(UIFactory.MAX_CONTENT_WIDTH);
        inner.add(UIFactory.title("My Fitness Plan", 22));
        SegmentedControl tabs = new SegmentedControl("Recommended", "Custom", "Goals");
        tabs.setSelectedIndex(tab);
        tabs.addChangeListener(i -> {
            tab = i;
            refresh();
        });
        inner.add(tabs);
        bar.add(inner, BorderLayout.CENTER);
        return bar;
    }

    @Override
    protected void buildBody(Stack body) {
        switch (tab) {
            case 1:
                buildCustom(body);
                break;
            case 2:
                buildGoals(body);
                break;
            default:
                buildRecommended(body);
                break;
        }
    }

    // ---------------- Recommended ----------------

    private void buildRecommended(Stack body) {
        User u = user();
        LocalDate today = LocalDate.now();
        FitnessPlan plan = u.getFitnessPlan();

        String subtitle = u.isFocusSelectable() ? "Tap to change"
                : "Today: " + session.getEngine().assignMuscleRotation(u, today);
        body.add(new ListCard(new IconBadge(Icons.Kind.TARGET, 40, 20, theme().primary, java.awt.Color.WHITE, false),
                u.getFocusDisplay(), u.getFocusLabel() + " • " + subtitle,
                UIFactory.iconLabel(Icons.Kind.CHEVRON_RIGHT, 18, theme().muted), true, this::changeFocus));

        List<Workout> workouts = plan.getWorkoutsFor(today);
        body.add(UIFactory.row(UIFactory.section("Today's Workout"),
                UIFactory.label(workouts.isEmpty() ? "Rest day" : workouts.size() + " Exercises", Font.BOLD, 12, theme().primary)));

        if (workouts.isEmpty()) {
            LocalDate next = plan.nextWorkoutDate(today);
            body.add(UIFactory.emptyState(Icons.Kind.CLOCK, "Rest Day",
                    "Recovery is part of the plan. " + (next == null ? "" : "Next workout: " + Formats.DAY.format(next) + ".")));
        } else {
            ResponsiveGrid grid = new ResponsiveGrid(320, 2, 12);
            for (Workout w : workouts) {
                grid.add(workoutCard(u, w));
            }
            body.add(grid);
        }

        List<String> equipment = u.getEquipment().viewEquipment();
        body.add(new ListCard(new IconBadge(Icons.Kind.DUMBBELL, 40, 20, theme().mint, theme().primary, false),
                "Available Equipment", equipment.isEmpty() ? "Bodyweight only" : String.join(", ", equipment),
                UIFactory.iconLabel(Icons.Kind.CHEVRON_RIGHT, 18, theme().muted), false,
                () -> new EquipmentDialog(this, session).showDialog()));

        PillButton full = new PillButton("View Full Plan", PillButton.Style.PRIMARY);
        full.addActionListener(e -> new FullPlanDialog(this, session).showDialog());
        body.add(full);
    }

    private JComponent workoutCard(User u, Workout w) {
        WorkoutStatus status = u.getWorkoutHistory().completionStatus(w.getDate(), w.getWorkoutName());
        String muted = Formats.hex(theme().muted);
        String text = Formats.hex(theme().text);
        String amount = w.getExercise().getType().getShortLabel();
        JLabel details = new JLabel("<html><span style='color:" + muted + "'>sets: </span><b style='color:" + text + "'>"
                + w.getSets() + "</b>&nbsp;&nbsp;<span style='color:" + muted + "'>" + amount + ": </span><b style='color:"
                + text + "'>" + w.getExercise().getType().format(w.getAmount()) + "</b>&nbsp;&nbsp;<span style='color:"
                + muted + "'>duration: </span><b style='color:" + text + "'>" + w.getDurationMinutes() + " mins</b></html>");
        details.setFont(myweigh.ui.theme.Theme.font(Font.PLAIN, 12.5f));

        RoundedPanel card = UIFactory.tintedCard(new BorderLayout(12, 0));
        card.setBorder(new EmptyBorder(14, 14, 14, 14));
        card.add(new IconBadge(Icons.forExercise(w.getExercise()), 44, 20, theme().card, theme().primary, true), BorderLayout.WEST);
        JPanel textPanel = UIFactory.transparent(new GridLayout(0, 1, 0, 3));
        textPanel.add(UIFactory.label(w.getWorkoutName(), Font.BOLD, 16, theme().text));
        textPanel.add(details);
        card.add(textPanel, BorderLayout.CENTER);
        JComponent trailing = status == WorkoutStatus.DONE ? Badge.success("Done")
                : UIFactory.iconLabel(Icons.Kind.CHEVRON_RIGHT, 18, theme().muted);
        card.add(UIFactory.wrapCenter(trailing), BorderLayout.EAST);
        UIFactory.makeClickable(card, () -> new ExerciseDialog(this, session, w.getExercise(), w).showDialog());
        return card;
    }

    private void changeFocus() {
        User u = user();
        if (!u.isFocusSelectable()) {
            Dialogs.info(this, "Monthly muscle rotation",
                    "As a beginner, your plan rotates through Legs, Chest, Back, Core, Shoulders and Arms for "
                            + "balanced development. Once you reach Intermediate you can choose a target muscle group.");
            return;
        }
        String choice = Dialogs.choose(this, "Choose " + u.getFocusLabel(), Arrays.asList(u.getFocusOptions()),
                u.getFocusArea(), s -> s);
        if (choice != null && !choice.equals(u.getFocusArea())) {
            u.setFocusArea(choice);
            session.regeneratePlan();
        }
    }

    // ---------------- Custom (Activity Scheduler + custom durations) ----------------

    private void buildCustom(Stack body) {
        User u = user();
        LocalDate today = LocalDate.now();
        PillButton add = new PillButton("Add Activity", PillButton.Style.SOFT, true).withIcon(Icons.Kind.PLUS);
        add.addActionListener(e -> new ScheduleDialog(this, session, null).showDialog());
        body.add(UIFactory.row(UIFactory.section("Activity Scheduler"), add));

        boolean anyUpcoming = false;
        boolean anyPast = false;
        for (ScheduledActivity a : u.getSchedule()) {
            if (a.getDate().isBefore(today)) {
                anyPast = true;
            } else {
                anyUpcoming = true;
                body.add(activityCard(a, today));
            }
        }
        if (!anyUpcoming) {
            body.add(UIFactory.emptyState(Icons.Kind.CALENDAR, "No upcoming activities",
                    "Schedule a run, a class or any routine block with Add Activity."));
        }
        if (anyPast) {
            body.add(UIFactory.section("Past activities"));
            List<ScheduledActivity> all = u.getSchedule();
            for (int i = all.size() - 1; i >= 0; i--) {
                if (all.get(i).getDate().isBefore(today)) {
                    body.add(activityCard(all.get(i), today));
                }
            }
        }

        body.add(UIFactory.section("Custom workout durations"));
        Map<String, Integer> custom = u.getFitnessPlan().getCustomDurations();
        if (custom.isEmpty()) {
            body.add(UIFactory.emptyState(Icons.Kind.CLOCK, "No custom durations",
                    "Open any exercise in your plan to change its duration. The recommended sets and reps stay the same."));
        } else {
            for (Map.Entry<String, Integer> e : custom.entrySet()) {
                PillButton reset = new PillButton("Reset", PillButton.Style.OUTLINE, true);
                reset.addActionListener(ev -> session.clearCustomDuration(e.getKey()));
                body.add(new ListCard(new IconBadge(Icons.Kind.CLOCK, 40, 20, theme().mint, theme().primary, true),
                        e.getKey(), e.getValue() + " mins (custom)", reset, false, null));
            }
        }
    }

    private JComponent activityCard(ScheduledActivity a, LocalDate today) {
        WorkoutStatus status = user().getWorkoutHistory().completionStatus(a.getDate(), a.getTitle());
        Badge badge;
        if (a.isCompleted()) {
            badge = Badge.success("Done");
        } else if (status == WorkoutStatus.MISSED) {
            badge = Badge.danger("Missed");
        } else if (a.getDate().equals(today)) {
            badge = Badge.warning("Today");
        } else {
            badge = Badge.neutral("Upcoming");
        }
        return new ListCard(new IconBadge(Icons.Kind.RUN, 40, 20, theme().mint, theme().primary, true), a.getTitle(),
                Formats.DAY.format(a.getDate()) + " • " + Formats.TIME.format(a.getTime()) + " • " + a.getDurationMinutes() + " mins",
                badge, false, () -> new ScheduleDialog(this, session, a).showDialog());
    }

    // ---------------- Goals (weight goal and progress) ----------------

    private void buildGoals(Stack body) {
        User u = user();
        FitnessPlan plan = u.getFitnessPlan();
        double progress = u.getGoalProgress();

        RoundedPanel goal = UIFactory.card(new BorderLayout());
        Stack s = new Stack(10);
        s.add(UIFactory.row(UIFactory.label("Weight Goal", Font.BOLD, 16, theme().text),
                Badge.success(u.getGoalType().getDisplay().toUpperCase())));
        s.add(UIFactory.hbox(UIFactory.label(Formats.kg(u.getCurrentWeight()), Font.BOLD, 26, theme().text),
                UIFactory.iconLabel(Icons.Kind.CHEVRON_RIGHT, 18, theme().muted),
                UIFactory.label(Formats.kg(u.getTargetWeight()), Font.BOLD, 26, theme().primary)));
        s.add(new SlimProgressBar(progress, theme().primary, 8));
        s.add(UIFactory.muted(Formats.percent(progress) + " complete • " + Timeframes.label(u.getTimeframeWeeks())
                + " • by " + Formats.SHORT.format(u.getGoalDeadline()), 12.5f));
        ResponsiveGrid tiles = new ResponsiveGrid(120, 3, 8);
        tiles.add(UIFactory.infoTile("Weekly target", Formats.signed(u.getWeeklyGoalKg(), " kg/week")));
        tiles.add(UIFactory.infoTile("Current BMI", Formats.one(u.calculateBMI()) + " • " + u.getBmiCategory()));
        double targetBmi = u.calculateTargetBMI();
        tiles.add(UIFactory.infoTile("Target BMI", Formats.one(targetBmi) + " • " + HealthCalculator.bmiCategory(targetBmi)));
        s.add(tiles);
        goal.add(s, BorderLayout.CENTER);
        body.add(goal);

        ResponsiveGrid buttons = new ResponsiveGrid(140, 2, 10);
        PillButton log = new PillButton("Log Weight", PillButton.Style.PRIMARY).withIcon(Icons.Kind.SCALE);
        log.addActionListener(e -> new WeightLogDialog(this, session).showDialog());
        PillButton edit = new PillButton("Edit Goal", PillButton.Style.OUTLINE).withIcon(Icons.Kind.EDIT);
        edit.addActionListener(e -> new GoalDialog(this, session).showDialog());
        buttons.add(log);
        buttons.add(edit);
        body.add(buttons);

        ResponsiveGrid targets = new ResponsiveGrid(240, 2, 12);
        targets.add(targetCard(Icons.Kind.FLAME, theme().accent, "Daily calorie target", Formats.kcal(plan.getDailyCalories())));
        targets.add(targetCard(Icons.Kind.DROP, theme().water, "Hydration target", plan.getWaterTargetGlasses() + " glasses / day"));
        body.add(targets);

        body.add(UIFactory.section("Weight log"));
        List<WeightEntry> log2 = u.getWeightLog();
        java.util.List<RowItem> rows = new java.util.ArrayList<>();
        for (int i = 0; i < log2.size(); i++) {
            WeightEntry entry = log2.get(i);
            String change = i + 1 < log2.size()
                    ? Formats.signed(entry.getWeight() - log2.get(i + 1).getWeight(), " kg") + " from previous" : "Starting entry";
            JLabel trash = UIFactory.iconLabel(Icons.Kind.TRASH, 18, theme().muted);
            trash.setToolTipText("Delete entry");
            trash.putClientProperty(UIFactory.OWN_CLICK, Boolean.TRUE);
            if (log2.size() > 1) {
                UIFactory.makeClickable(trash, () -> deleteWeight(entry));
                trash.putClientProperty(UIFactory.OWN_CLICK, Boolean.TRUE);
            }
            rows.add(new RowItem(UIFactory.iconLabel(Icons.Kind.SCALE, 20, theme().primary),
                    Formats.kg(entry.getWeight()) + " • " + Formats.SHORT.format(entry.getDate()), change,
                    log2.size() > 1 ? trash : null, null));
        }
        body.add(UIFactory.groupCard(rows));
    }

    private JComponent targetCard(Icons.Kind icon, java.awt.Color color, String label, String value) {
        RoundedPanel card = UIFactory.card(new BorderLayout(12, 0));
        card.add(new IconBadge(icon, 40, 20, theme().mint, color, true), BorderLayout.WEST);
        JPanel text = UIFactory.transparent(new GridLayout(0, 1, 0, 2));
        text.add(UIFactory.muted(label, 12));
        text.add(UIFactory.label(value, Font.BOLD, 16, theme().text));
        card.add(text, BorderLayout.CENTER);
        return card;
    }

    private void deleteWeight(WeightEntry entry) {
        if (Dialogs.confirm(this, "Delete weight entry",
                "Delete the " + Formats.kg(entry.getWeight()) + " entry from " + Formats.SHORT.format(entry.getDate()) + "?",
                "Delete", true)) {
            try {
                user().removeWeightEntry(entry);
                session.regeneratePlan();
            } catch (IllegalStateException ex) {
                Dialogs.error(this, ex.getMessage());
            }
        }
    }
}
