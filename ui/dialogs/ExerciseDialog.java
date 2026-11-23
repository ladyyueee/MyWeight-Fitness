package myweigh.ui.dialogs;

import myweigh.model.Equipment;
import myweigh.model.Exercise;
import myweigh.model.ExerciseType;
import myweigh.model.PainEntry;
import myweigh.model.User;
import myweigh.model.Workout;
import myweigh.model.WorkoutStatus;
import myweigh.service.InputValidator;
import myweigh.service.InvalidInputException;
import myweigh.service.Session;
import myweigh.ui.components.Formats;
import myweigh.ui.components.Icons;
import myweigh.ui.components.InputField;
import myweigh.ui.components.PillButton;
import myweigh.ui.components.ResponsiveGrid;
import myweigh.ui.components.RoundedPanel;
import myweigh.ui.components.Stack;
import myweigh.ui.components.ThemedSliderUI;
import myweigh.ui.components.Toast;
import myweigh.ui.components.UIFactory;
import myweigh.ui.components.WrapText;
import myweigh.ui.theme.Theme;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.Window;
import java.time.LocalDate;

/**
 * Exercise details from the library (instructions, target muscles, equipment
 * guide). When opened for a planned workout it also shows the prescription,
 * the progressive-load tip, custom duration and "Mark as Complete".
 */
public class ExerciseDialog extends AppDialog {
    private final Session session;
    private final Exercise exercise;
    private final Workout workout;
    private InputField weightField;
    private InputField amountField;
    private InputField durationField;
    private JSlider painSlider;

    public ExerciseDialog(Component parent, Session session, Exercise exercise, Workout workout) {
        super(parent, exercise.getName());
        this.session = session;
        this.exercise = exercise;
        this.workout = workout;
        build();
    }

    @Override
    protected int dialogWidth() {
        return 540;
    }

    private void build() {
        Theme t = Theme.get();
        User u = session.getCurrentUser();
        LocalDate today = LocalDate.now();

        ResponsiveGrid info = new ResponsiveGrid(140, 3, 8);
        info.add(UIFactory.infoTile("Muscle group", exercise.getMuscleGroup()));
        info.add(UIFactory.infoTile("Target muscles", exercise.getTargetMuscles()));
        info.add(UIFactory.infoTile("Equipment", exercise.getEquipmentText()));
        content.add(info);

        WorkoutStatus status = null;
        if (workout != null) {
            status = u.getWorkoutHistory().completionStatus(workout.getDate(), workout.getWorkoutName());
            content.add(UIFactory.section("Your prescription • " + Formats.DAY.format(workout.getDate())));
            ResponsiveGrid rx = new ResponsiveGrid(100, 4, 8);
            rx.add(UIFactory.infoTile("Sets", String.valueOf(workout.getSets())));
            rx.add(UIFactory.infoTile(exercise.getType() == ExerciseType.REPS ? "Reps" : "Seconds",
                    exercise.getType().format(workout.getAmount())));
            rx.add(UIFactory.infoTile("Duration", workout.getDurationMinutes() + " mins" + (workout.isCustomDuration() ? " (custom)" : "")));
            if (exercise.isWeighted()) {
                rx.add(UIFactory.infoTile("Weight", Workout.formatWeight(workout.getSuggestedWeight()) + " kg"));
            }
            content.add(rx);
            content.add(tip(u.getLoadTracker().getRecommendationText(exercise, workout.getAmount(), workout.getSuggestedWeight())));
        }

        content.add(UIFactory.section("How to perform"));
        content.add(new WrapText(exercise.getInstructions(), Theme.font(Font.PLAIN, 14), t.text));

        content.add(UIFactory.section("Equipment guide"));
        if (exercise.isBodyweight()) {
            content.add(new WrapText("No equipment needed. Use a mat on a non-slip surface and keep the area clear.",
                    Theme.font(Font.PLAIN, 13.5f), t.muted));
        } else {
            for (String item : exercise.getEquipment()) {
                Stack s = new Stack(3);
                s.add(UIFactory.label(item, Font.BOLD, 13.5f, t.text));
                s.add(new WrapText(Equipment.getGuide(item), Theme.font(Font.PLAIN, 13.5f), t.muted));
                content.add(s);
            }
        }

        PillButton close = new PillButton("Close", PillButton.Style.OUTLINE);
        close.addActionListener(e -> dispose());
        addFooterButton(close);

        if (workout == null) {
            return;
        }
        if (status == WorkoutStatus.DONE) {
            content.add(banner(Icons.Kind.CHECK_CIRCLE, "Completed — great job!", false));
            return;
        }
        if (status == WorkoutStatus.MISSED) {
            content.add(banner(Icons.Kind.X_CIRCLE, "This workout was missed.", true));
            return;
        }

        content.add(UIFactory.section("Customize duration"));
        durationField = new InputField(Icons.Kind.CLOCK, "Minutes", false, "mins");
        durationField.setText(String.valueOf(workout.getDurationMinutes()));
        PillButton saveDuration = new PillButton("Save", PillButton.Style.SOFT, true);
        saveDuration.addActionListener(e -> saveDuration());
        content.add(UIFactory.row(durationField, saveDuration));
        if (workout.isCustomDuration()) {
            content.add(UIFactory.link("Reset to recommended duration", () -> {
                session.clearCustomDuration(exercise.getName());
                Window owner = getOwner();
                dispose();
                Toast.show(owner, "Duration reset for " + exercise.getName());
            }));
        }

        if (workout.getDate().equals(today)) {
            content.add(UIFactory.section("Log your performance"));
            ResponsiveGrid grid = new ResponsiveGrid(160, 2, 10);
            if (exercise.isWeighted()) {
                weightField = new InputField(Icons.Kind.DUMBBELL, "kg", false, "kg");
                weightField.setText(Workout.formatWeight(workout.getSuggestedWeight()));
                grid.add(UIFactory.fieldGroup("Weight used (kg)", weightField));
            }
            amountField = new InputField(exercise.getType() == ExerciseType.REPS ? Icons.Kind.UPDOWN : Icons.Kind.CLOCK,
                    "0", false, exercise.getType() == ExerciseType.REPS ? "reps" : "sec");
            amountField.setText(String.valueOf(workout.getAmount()));
            grid.add(UIFactory.fieldGroup(exercise.getType().getInputLabel(), amountField));
            content.add(grid);

            if (u.requiresPainCheck()) {
                JLabel painLabel = UIFactory.label("0 / 10 — No pain", Font.BOLD, 13, t.primary);
                painSlider = ThemedSliderUI.create(0, 10, 0);
                painSlider.addChangeListener(e -> {
                    int v = painSlider.getValue();
                    painLabel.setText(v + " / 10 — " + PainEntry.describe(v));
                    painLabel.setForeground(v > 6 ? t.danger : t.primary);
                });
                Stack pain = new Stack(4);
                pain.add(painLabel);
                pain.add(painSlider);
                content.add(UIFactory.fieldGroup("Pain level after this exercise", pain));
            }
            PillButton complete = new PillButton("Complete", PillButton.Style.PRIMARY).withIcon(Icons.Kind.CHECK);
            complete.addActionListener(e -> complete());
            addFooterButton(complete);
        } else if (workout.getDate().isAfter(today)) {
            content.add(banner(Icons.Kind.CALENDAR, "Scheduled for " + Formats.DAY.format(workout.getDate())
                    + ". You can mark it complete on that day.", false));
        }
    }

    private JComponent tip(String text) {
        Theme t = Theme.get();
        RoundedPanel card = new RoundedPanel(new BorderLayout(10, 0), t.mint, t.mintBorder, 14);
        card.setBorder(new EmptyBorder(10, 12, 10, 12));
        card.add(UIFactory.iconLabel(Icons.Kind.TREND, 18, t.primary), BorderLayout.WEST);
        card.add(new WrapText(text, Theme.font(Font.PLAIN, 13), t.text), BorderLayout.CENTER);
        return card;
    }

    private JComponent banner(Icons.Kind icon, String text, boolean danger) {
        Theme t = Theme.get();
        RoundedPanel card = new RoundedPanel(new BorderLayout(10, 0), danger ? t.dangerBg : t.mint,
                danger ? Theme.alpha(t.danger, 80) : t.mintBorder, 14);
        card.setBorder(new EmptyBorder(12, 12, 12, 12));
        card.add(UIFactory.iconLabel(icon, 20, danger ? t.danger : t.primary), BorderLayout.WEST);
        card.add(new WrapText(text, Theme.font(Font.BOLD, 13.5f), danger ? t.danger : t.primary), BorderLayout.CENTER);
        return card;
    }

    private void saveDuration() {
        try {
            int minutes = InputValidator.parseInt(durationField.getText(), "Duration", 1, 180);
            session.setCustomDuration(exercise.getName(), minutes);
            Window owner = getOwner();
            dispose();
            Toast.show(owner, exercise.getName() + " set to " + minutes + " mins");
        } catch (InvalidInputException ex) {
            Dialogs.error(this, ex.getMessage());
        }
    }

    private void complete() {
        try {
            double weight = exercise.isWeighted()
                    ? InputValidator.parseDouble(weightField.getText(), "Weight used", 0, 500) : 0;
            int amount = InputValidator.parseInt(amountField.getText(), exercise.getType().getInputLabel(), 0, 1000);
            Integer pain = painSlider != null ? painSlider.getValue() : null;
            Session.CompletionResult result = session.completeWorkout(workout, weight, amount, pain);
            Window owner = getOwner();
            dispose();
            if (result.isPromoted()) {
                Dialogs.info(owner, "Level Up!", "Congratulations! You met the requirements and have been promoted to "
                        + result.getNewLevel().getLabel() + ". Your upcoming workouts now use the new level.");
            }
            if (result.isPainWarning()) {
                Dialogs.info(owner, "High pain level recorded", "Your pain level is above the safe limit, so your next "
                        + "sessions have been made lighter. Stop any exercise that causes sharp pain and consult your "
                        + "doctor or therapist if it continues.");
            }
            Toast.show(owner, "Nice work! " + exercise.getName() + " marked as done.");
        } catch (InvalidInputException ex) {
            Dialogs.error(this, ex.getMessage());
        }
    }
}
