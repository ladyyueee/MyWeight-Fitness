package myweigh.ui.dialogs;

import myweigh.model.ScheduledActivity;
import myweigh.service.InputValidator;
import myweigh.service.InvalidInputException;
import myweigh.service.Session;
import myweigh.ui.components.Formats;
import myweigh.ui.components.Icons;
import myweigh.ui.components.InputField;
import myweigh.ui.components.PillButton;
import myweigh.ui.components.ResponsiveGrid;
import myweigh.ui.components.Toast;
import myweigh.ui.components.UIFactory;

import java.awt.Component;
import java.awt.Window;
import java.time.LocalDate;
import java.time.LocalTime;

/** Activity Scheduler: add, edit, complete or delete a scheduled activity. */
public class ScheduleDialog extends AppDialog {
    private final Session session;
    private final ScheduledActivity existing;
    private final InputField title;
    private final InputField date;
    private final InputField time;
    private final InputField duration;
    private final InputField notes;

    public ScheduleDialog(Component parent, Session session, ScheduledActivity existing) {
        super(parent, existing == null ? "Schedule Activity" : "Edit Activity");
        this.session = session;
        this.existing = existing;

        title = new InputField(Icons.Kind.RUN, "e.g. Morning Run", false, null);
        date = new InputField(Icons.Kind.CALENDAR, "YYYY-MM-DD", false, null);
        time = new InputField(Icons.Kind.CLOCK, "HH:MM", false, null);
        duration = new InputField(null, "30", false, "mins");
        notes = new InputField(Icons.Kind.EDIT, "Optional, e.g. 3 km", false, null);
        if (existing != null) {
            title.setText(existing.getTitle());
            date.setText(existing.getDate().toString());
            time.setText(Formats.TIME.format(existing.getTime()));
            duration.setText(String.valueOf(existing.getDurationMinutes()));
            notes.setText(existing.getNotes());
        } else {
            date.setText(LocalDate.now().toString());
            time.setText("07:00");
            duration.setText("30");
        }
        content.add(UIFactory.fieldGroup("Activity", title));
        ResponsiveGrid grid = new ResponsiveGrid(150, 3, 10);
        grid.add(UIFactory.fieldGroup("Date", date));
        grid.add(UIFactory.fieldGroup("Time (24h)", time));
        grid.add(UIFactory.fieldGroup("Duration", duration));
        content.add(grid);
        content.add(UIFactory.fieldGroup("Notes", notes));

        if (existing != null) {
            PillButton toggle = new PillButton(existing.isCompleted() ? "Mark as Not Done" : "Mark as Done",
                    PillButton.Style.SOFT).withIcon(Icons.Kind.CHECK);
            toggle.addActionListener(e -> {
                session.setActivityCompleted(existing, !existing.isCompleted());
                Window owner = getOwner();
                dispose();
                Toast.show(owner, existing.isCompleted() ? "Activity logged" : "Activity marked as not done");
            });
            content.add(toggle);
            content.add(UIFactory.dangerAction("Delete Activity", this::delete));
        }

        PillButton cancel = new PillButton("Cancel", PillButton.Style.OUTLINE);
        cancel.addActionListener(e -> dispose());
        PillButton save = new PillButton("Save", PillButton.Style.PRIMARY);
        save.addActionListener(e -> save());
        addFooterButton(cancel);
        addFooterButton(save);
    }

    @Override
    protected int dialogWidth() {
        return 520;
    }

    private void save() {
        try {
            String name = InputValidator.requireText(title.getText(), "Activity name");
            LocalDate d = InputValidator.parseDate(date.getText());
            LocalTime tm = InputValidator.parseTime(time.getText());
            int minutes = InputValidator.parseInt(duration.getText(), "Duration", 1, 600);
            if (existing == null) {
                session.saveActivity(new ScheduledActivity(name, d, tm, minutes, notes.getText()));
            } else {
                existing.setTitle(name);
                existing.setDate(d);
                existing.setTime(tm);
                existing.setDurationMinutes(minutes);
                existing.setNotes(notes.getText());
                session.saveActivity(existing);
            }
            Window owner = getOwner();
            dispose();
            Toast.show(owner, "Activity saved for " + Formats.DAY.format(d));
        } catch (InvalidInputException | IllegalArgumentException ex) {
            Dialogs.error(this, ex.getMessage());
        }
    }

    private void delete() {
        if (Dialogs.confirm(this, "Delete activity", "Delete \"" + existing.getTitle() + "\"?", "Delete", true)) {
            session.deleteActivity(existing);
            Window owner = getOwner();
            dispose();
            Toast.show(owner, "Activity deleted");
        }
    }
}
