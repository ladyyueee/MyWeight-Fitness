package myweigh.ui.dialogs;

import myweigh.model.User;
import myweigh.model.Workout;
import myweigh.model.WorkoutStatus;
import myweigh.service.Session;
import myweigh.ui.components.Badge;
import myweigh.ui.components.Formats;
import myweigh.ui.components.IconBadge;
import myweigh.ui.components.Icons;
import myweigh.ui.components.ListCard;
import myweigh.ui.components.PillButton;
import myweigh.ui.components.UIFactory;
import myweigh.ui.theme.Theme;

import javax.swing.JComponent;
import java.awt.Component;
import java.time.LocalDate;

/** The workouts planned for one day. */
public class DayPlanDialog extends AppDialog {

    public DayPlanDialog(Component parent, Session session, LocalDate date) {
        super(parent, Formats.DAY.format(date));
        Theme t = Theme.get();
        User u = session.getCurrentUser();
        content.add(UIFactory.label(u.getDayLabel(date, session.getEngine()), java.awt.Font.BOLD, 15, t.primary));
        int total = 0;
        for (Workout w : u.getFitnessPlan().getWorkoutsFor(date)) {
            total += w.getDurationMinutes();
            WorkoutStatus status = u.getWorkoutHistory().completionStatus(date, w.getWorkoutName());
            JComponent trailing = status == WorkoutStatus.DONE ? Badge.success("Done")
                    : status == WorkoutStatus.MISSED ? Badge.danger("Missed")
                    : UIFactory.iconLabel(Icons.Kind.CHEVRON_RIGHT, 18, t.muted);
            content.add(new ListCard(new IconBadge(Icons.forExercise(w.getExercise()), 40, 20, t.card, t.primary, true),
                    w.getWorkoutName(), w.getSummary(), trailing, true,
                    () -> new ExerciseDialog(this, session, w.getExercise(), w).showDialog()));
        }
        content.add(UIFactory.muted("Total duration: " + total + " mins", 12.5f));
        PillButton close = new PillButton("Close", PillButton.Style.OUTLINE);
        close.addActionListener(e -> dispose());
        addFooterButton(close);
    }
}
