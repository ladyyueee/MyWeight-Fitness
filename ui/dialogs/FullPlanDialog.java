package myweigh.ui.dialogs;

import myweigh.model.User;
import myweigh.model.Workout;
import myweigh.model.WorkoutHistory;
import myweigh.model.WorkoutStatus;
import myweigh.service.Session;
import myweigh.ui.components.Badge;
import myweigh.ui.components.Formats;
import myweigh.ui.components.IconBadge;
import myweigh.ui.components.Icons;
import myweigh.ui.components.ListCard;
import myweigh.ui.components.PillButton;
import myweigh.ui.components.RoundedPanel;
import myweigh.ui.components.UIFactory;
import myweigh.ui.components.WrapText;
import myweigh.ui.theme.Theme;

import javax.swing.JComponent;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** Plan summary (showFitnessPlan) and the generated monthly schedule. */
public class FullPlanDialog extends AppDialog {

    public FullPlanDialog(Component parent, Session session) {
        super(parent, "Full Fitness Plan");
        Theme t = Theme.get();
        User u = session.getCurrentUser();
        LocalDate today = LocalDate.now();

        RoundedPanel summary = UIFactory.tintedCard(new BorderLayout());
        summary.add(new WrapText(u.getFitnessPlan().showFitnessPlan(u), Theme.font(Font.PLAIN, 13.5f), t.text));
        content.add(summary);
        content.add(UIFactory.section("Monthly schedule"));

        WorkoutHistory history = u.getWorkoutHistory();
        for (Map.Entry<LocalDate, List<Workout>> e : u.getFitnessPlan().getSchedule().tailMap(today, true).entrySet()) {
            LocalDate date = e.getKey();
            List<Workout> workouts = e.getValue();
            if (workouts.isEmpty()) {
                content.add(new ListCard(new IconBadge(Icons.Kind.CLOCK, 40, 20, t.subtle, t.muted, true),
                        Formats.DAY.format(date) + " • Rest Day", "Recovery and light mobility",
                        date.equals(today) ? Badge.warning("Today") : null, false, null));
                continue;
            }
            List<String> names = new ArrayList<>();
            int done = 0;
            for (Workout w : workouts) {
                names.add(w.getWorkoutName());
                if (history.completionStatus(date, w.getWorkoutName()) == WorkoutStatus.DONE) {
                    done++;
                }
            }
            JComponent badge;
            if (done == workouts.size()) {
                badge = Badge.success("Done");
            } else if (done > 0) {
                badge = Badge.success(done + "/" + workouts.size());
            } else if (date.equals(today)) {
                badge = Badge.warning("Today");
            } else {
                badge = UIFactory.iconLabel(Icons.Kind.CHEVRON_RIGHT, 18, t.muted);
            }
            content.add(new ListCard(new IconBadge(Icons.Kind.PLANS, 40, 20, t.mint, t.primary, true),
                    Formats.DAY.format(date) + " • " + u.getDayLabel(date, session.getEngine()),
                    String.join(" • ", names), badge, date.equals(today),
                    () -> new DayPlanDialog(this, session, date).showDialog()));
        }

        PillButton close = new PillButton("Close", PillButton.Style.PRIMARY);
        close.addActionListener(e -> dispose());
        addFooterButton(close);
    }

    @Override
    protected int dialogWidth() {
        return 600;
    }
}
