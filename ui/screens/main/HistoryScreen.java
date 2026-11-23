package myweigh.ui.screens.main;

import myweigh.model.FitnessLevel;
import myweigh.model.FitnessLevelManager;
import myweigh.model.LoadRecord;
import myweigh.model.PainEntry;
import myweigh.model.ProgressiveLoadTracker;
import myweigh.model.RehabPatient;
import myweigh.model.User;
import myweigh.model.Workout;
import myweigh.model.WorkoutHistory;
import myweigh.model.WorkoutRecord;
import myweigh.model.WorkoutStatus;
import myweigh.ui.AppShell;
import myweigh.ui.components.Badge;
import myweigh.ui.components.Formats;
import myweigh.ui.components.IconBadge;
import myweigh.ui.components.Icons;
import myweigh.ui.components.ListCard;
import myweigh.ui.components.PillButton;
import myweigh.ui.components.ResponsiveGrid;
import myweigh.ui.components.RoundedPanel;
import myweigh.ui.components.SegmentedControl;
import myweigh.ui.components.SlimProgressBar;
import myweigh.ui.components.Stack;
import myweigh.ui.components.UIFactory;
import myweigh.ui.dialogs.Dialogs;
import myweigh.ui.dialogs.PainDialog;

import javax.swing.JComponent;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/** Panel 09 — Workout History, plus progressive load and fitness level advancement. */
public class HistoryScreen extends BaseScreen {
    private static final List<String> PERIODS = Arrays.asList("All time", "Last 7 days", "This month");
    private int view;
    private String period = PERIODS.get(0);
    private String query = "";

    public HistoryScreen(AppShell shell) {
        super(shell);
    }

    @Override
    protected Component createHeader() {
        JComponent calendar = UIFactory.headerButton(Icons.Kind.CALENDAR, null, "Filter by period", () -> {
            String choice = Dialogs.choose(this, "Show records from", PERIODS, period, s -> s);
            if (choice != null) {
                period = choice;
                view = 0;
                refresh();
            }
        });
        JComponent search = UIFactory.headerButton(Icons.Kind.SEARCH, null, "Search workouts", () -> {
            String q = Dialogs.prompt(this, "Search workouts", "Workout name", query);
            if (q != null) {
                query = q;
                view = 0;
                refresh();
            }
        });
        return UIFactory.greenHeader("Workout History", "Completed & scheduled workout records", pad(), calendar, search);
    }

    @Override
    protected void buildBody(Stack body) {
        SegmentedControl tabs = new SegmentedControl("History", "Load & Level");
        tabs.setSelectedIndex(view);
        tabs.addChangeListener(i -> {
            view = i;
            refresh();
        });
        body.add(tabs);
        if (view == 0) {
            buildHistory(body);
        } else {
            buildLoadAndLevel(body);
        }
    }

    private void buildHistory(Stack body) {
        User u = user();
        WorkoutHistory history = u.getWorkoutHistory();
        LocalDate today = LocalDate.now();
        LocalDate since = "Last 7 days".equals(period) ? today.minusDays(6)
                : "This month".equals(period) ? today.withDayOfMonth(1) : null;

        ResponsiveGrid summary = new ResponsiveGrid(90, 3, 10);
        summary.add(summaryTile("Done", String.valueOf(history.countStatus(WorkoutStatus.DONE, since)), Icons.Kind.CHECK_CIRCLE));
        summary.add(summaryTile("Missed", String.valueOf(history.countStatus(WorkoutStatus.MISSED, since)), Icons.Kind.X_CIRCLE));
        summary.add(summaryTile("Completion", Formats.percent(history.getCompletionRate(since)), Icons.Kind.TREND));
        body.add(summary);

        boolean filtered = since != null || !query.isEmpty();
        if (filtered) {
            String text = "Showing: " + period + (query.isEmpty() ? "" : " • \"" + query + "\"");
            body.add(UIFactory.row(UIFactory.muted(text, 12.5f), UIFactory.link("Clear filter", () -> {
                period = PERIODS.get(0);
                query = "";
                refresh();
            })));
        }

        List<WorkoutRecord> records = new ArrayList<>();
        String q = query.toLowerCase(Locale.ROOT);
        for (WorkoutRecord r : history.viewHistory()) {
            if ((since == null || !r.getWorkoutDate().isBefore(since))
                    && (q.isEmpty() || r.getWorkoutName().toLowerCase(Locale.ROOT).contains(q))) {
                records.add(r);
            }
        }
        if (records.isEmpty()) {
            body.add(UIFactory.emptyState(Icons.Kind.PROGRESS, filtered ? "No matching records" : "No history yet",
                    filtered ? "Try a different search or period." : "Completed and missed workouts will appear here."));
            return;
        }
        for (Map.Entry<LocalDate, List<WorkoutRecord>> e : history.groupByDate(records).entrySet()) {
            int n = e.getValue().size();
            body.add(UIFactory.row(UIFactory.label(Formats.LONG.format(e.getKey()), Font.BOLD, 13, theme().muted),
                    UIFactory.label(n + (n == 1 ? " Session" : " Sessions"), Font.BOLD, 12, theme().primary)));
            ResponsiveGrid grid = new ResponsiveGrid(320, 2, 10);
            for (WorkoutRecord r : e.getValue()) {
                grid.add(recordCard(r));
            }
            body.add(grid);
        }
    }

    private JComponent summaryTile(String label, String value, Icons.Kind icon) {
        RoundedPanel card = UIFactory.card(new BorderLayout(10, 0));
        JPanel text = UIFactory.transparent(new GridLayout(0, 1));
        text.add(UIFactory.label(value, Font.BOLD, 18, theme().text));
        text.add(UIFactory.muted(label, 11.5f));
        card.add(text, BorderLayout.CENTER);
        return card;
    }

    private JComponent recordCard(WorkoutRecord r) {
        Badge badge;
        Icons.Kind icon;
        switch (r.getStatus()) {
            case DONE:
                badge = Badge.success("Done");
                icon = Icons.Kind.CHECK_CIRCLE;
                break;
            case MISSED:
                badge = Badge.danger("Missed");
                icon = Icons.Kind.X_CIRCLE;
                break;
            default:
                badge = Badge.warning("Logged");
                icon = Icons.Kind.RUN;
                break;
        }
        return new ListCard(new IconBadge(icon, 40, 20, theme().card, theme().primary, true),
                r.getWorkoutName(), r.getDetails(), badge, true, null);
    }

    private void buildLoadAndLevel(Stack body) {
        User u = user();
        FitnessLevelManager manager = new FitnessLevelManager(u.getFitnessLevel());
        manager.checkFitnessLevel(u.getWorkoutHistory(), u.getLevelStartDate());
        FitnessLevel level = u.getFitnessLevel();

        RoundedPanel levelCard = UIFactory.card(new BorderLayout());
        Stack s = new Stack(10);
        s.add(UIFactory.row(UIFactory.label("Fitness Level", Font.BOLD, 16, theme().text),
                level.isHighest() ? Badge.success("HIGHEST LEVEL") : Badge.neutral("Next: " + level.next().getLabel())));
        s.add(UIFactory.label(level.getLabel(), Font.BOLD, 26, theme().primary));
        if (level.isHighest()) {
            s.add(UIFactory.muted("You've reached the highest level. Keep progressing your loads!", 12.5f));
        } else {
            s.add(UIFactory.row(UIFactory.muted("Exercises completed", 12.5f),
                    UIFactory.label(manager.getSessionsCompleted() + " / " + manager.getSessionsRequired(), Font.BOLD, 12.5f, theme().text)));
            s.add(new SlimProgressBar(manager.getSessionProgress(), theme().primary, 8));
            s.add(UIFactory.row(UIFactory.muted("Completion rate (needs " + Formats.percent(FitnessLevelManager.REQUIRED_COMPLETION_RATE) + ")", 12.5f),
                    UIFactory.label(Formats.percent(manager.getCompletionRate()), Font.BOLD, 12.5f, theme().text)));
            s.add(new SlimProgressBar(manager.getCompletionRate(), theme().accent, 8));
            PillButton check = new PillButton("Check Advancement", PillButton.Style.OUTLINE, true).withIcon(Icons.Kind.TROPHY);
            check.addActionListener(e -> {
                FitnessLevel promoted = session.checkAdvancement();
                if (promoted != null) {
                    Dialogs.info(this, "Level Up!", "Congratulations! You've been promoted to " + promoted.getLabel()
                            + ". Your upcoming plan now uses the new level.");
                } else {
                    Dialogs.info(this, "Keep going", "Complete " + manager.getSessionsRequired() + " exercises with at least "
                            + Formats.percent(FitnessLevelManager.REQUIRED_COMPLETION_RATE)
                            + " completion to advance to " + level.next().getLabel() + ".");
                }
            });
            s.add(UIFactory.row(null, check));
        }
        levelCard.add(s, BorderLayout.CENTER);
        body.add(levelCard);

        body.add(UIFactory.section("Progressive load"));
        ProgressiveLoadTracker tracker = u.getLoadTracker();
        if (tracker.getTrackedExercises().isEmpty()) {
            body.add(UIFactory.emptyState(Icons.Kind.TREND, "No load data yet",
                    "Mark workouts as complete with the weight and reps you did to track your progression."));
        } else {
            ResponsiveGrid grid = new ResponsiveGrid(320, 2, 10);
            for (String name : tracker.getTrackedExercises()) {
                grid.add(loadCard(tracker, name));
            }
            body.add(grid);
        }

        if (u.requiresPainCheck()) {
            RehabPatient patient = (RehabPatient) u;
            PillButton record = new PillButton("Record Pain", PillButton.Style.SOFT, true).withIcon(Icons.Kind.PLUS);
            record.addActionListener(e -> new PainDialog(this, session).showDialog());
            body.add(UIFactory.row(UIFactory.section("Pain level log"), record));
            List<PainEntry> log = patient.getPainLog();
            if (log.isEmpty()) {
                body.add(UIFactory.emptyState(Icons.Kind.HEART, "No pain levels recorded",
                        "Confirm your pain level after each session so the plan can stay within safe limits."));
            }
            for (PainEntry p : log) {
                Badge badge = p.getLevel() > RehabPatient.SAFE_PAIN_LIMIT ? Badge.danger(p.getLevel() + "/10")
                        : Badge.success(p.getLevel() + "/10");
                body.add(new ListCard(new IconBadge(Icons.Kind.HEART, 40, 20, theme().dangerBg, theme().danger, true),
                        PainEntry.describe(p.getLevel()) + " • " + Formats.SHORT.format(p.getDate()),
                        p.getNote().isEmpty() ? "No note" : p.getNote(), badge, false, null));
            }
        }
    }

    private JComponent loadCard(ProgressiveLoadTracker tracker, String name) {
        LoadRecord last = tracker.getLatest(name);
        boolean weighted = last.getCurrentWeight() > 0;
        String subtitle = weighted
                ? "Last: " + Workout.formatWeight(last.getCurrentWeight()) + " kg × " + last.getCurrentReps()
                + " • Prev: " + Workout.formatWeight(last.getPreviousWeight()) + " kg × " + last.getPreviousReps()
                : "Last: " + last.getCurrentReps() + " • Prev: " + last.getPreviousReps();
        double weightGain = tracker.getWeightImprovement(name);
        int repGain = tracker.getRepImprovement(name);
        Badge badge;
        if (weighted && weightGain != 0) {
            badge = weightGain > 0 ? Badge.success(Formats.signed(weightGain, " kg")) : Badge.warning(Formats.signed(weightGain, " kg"));
        } else if (repGain != 0) {
            badge = repGain > 0 ? Badge.success("+" + repGain) : Badge.warning(String.valueOf(repGain));
        } else {
            badge = Badge.neutral(tracker.getRecords(name).size() + "x");
        }
        return new ListCard(new IconBadge(Icons.Kind.TREND, 40, 20, theme().card, theme().primary, true),
                name, subtitle, badge, true, () -> showLoadHistory(tracker, name));
    }

    private void showLoadHistory(ProgressiveLoadTracker tracker, String name) {
        StringBuilder sb = new StringBuilder();
        for (LoadRecord r : tracker.getRecords(name)) {
            sb.append(Formats.SHORT.format(r.getDate())).append(":  ");
            if (r.getCurrentWeight() > 0) {
                sb.append(Workout.formatWeight(r.getCurrentWeight())).append(" kg × ");
            }
            sb.append(r.getCurrentReps()).append('\n');
        }
        myweigh.model.Exercise ex = session.getLibrary().viewExercise(name);
        if (ex != null) {
            sb.append('\n').append(tracker.getRecommendationText(ex, ex.getBaseAmount(),
                    tracker.recommendNextLoad(name, ex.getStartWeightKg(), ex.getBaseAmount())));
        }
        Dialogs.info(this, name, sb.toString().trim());
    }
}
