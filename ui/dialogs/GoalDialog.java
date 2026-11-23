package myweigh.ui.dialogs;

import myweigh.model.GoalType;
import myweigh.model.Timeframes;
import myweigh.model.User;
import myweigh.service.HealthCalculator;
import myweigh.service.InputValidator;
import myweigh.service.InvalidInputException;
import myweigh.service.Session;
import myweigh.ui.components.Dropdown;
import myweigh.ui.components.Formats;
import myweigh.ui.components.Icons;
import myweigh.ui.components.InputField;
import myweigh.ui.components.PillButton;
import myweigh.ui.components.SegmentedControl;
import myweigh.ui.components.Toast;
import myweigh.ui.components.UIFactory;
import myweigh.ui.components.WrapText;
import myweigh.ui.theme.Theme;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.Component;
import java.awt.Font;
import java.awt.Window;

/** Edits the weight goal: type, target weight and timeframe. */
public class GoalDialog extends AppDialog {
    private final Session session;
    private final SegmentedControl type;
    private final InputField target;
    private final Dropdown<Integer> timeframe;
    private final WrapText preview;

    public GoalDialog(Component parent, Session session) {
        super(parent, "Weight Goal");
        this.session = session;
        User u = session.getCurrentUser();

        content.add(UIFactory.muted("Current weight: " + Formats.kg(u.getCurrentWeight()), 13));
        type = new SegmentedControl("Lose", "Maintain", "Gain");
        type.setSelectedIndex(u.getGoalType().ordinal());
        content.add(UIFactory.fieldGroup("Goal Type", type));

        target = new InputField(Icons.Kind.TARGET, "e.g. 60", false, "kg");
        target.setText(Formats.number(u.getTargetWeight()));
        content.add(UIFactory.fieldGroup("Target Weight (kg)", target));

        timeframe = new Dropdown<>(Timeframes.options(), u.getTimeframeWeeks(), Timeframes::label);
        content.add(UIFactory.fieldGroup("Timeframe", timeframe));

        preview = new WrapText("", Theme.font(Font.PLAIN, 13), Theme.get().primary);
        content.add(preview);

        type.addChangeListener(i -> {
            if (i == GoalType.MAINTAIN.ordinal()) {
                target.setText(Formats.number(u.getCurrentWeight()));
            }
            target.setEditable(i != GoalType.MAINTAIN.ordinal());
            updatePreview();
        });
        target.setEditable(u.getGoalType() != GoalType.MAINTAIN);
        timeframe.addChangeListener(w -> updatePreview());
        target.getField().getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updatePreview();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updatePreview();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updatePreview();
            }
        });

        PillButton cancel = new PillButton("Cancel", PillButton.Style.OUTLINE);
        cancel.addActionListener(e -> dispose());
        PillButton save = new PillButton("Save Goal", PillButton.Style.PRIMARY);
        save.addActionListener(e -> save());
        addFooterButton(cancel);
        addFooterButton(save);
        updatePreview();
    }

    private void updatePreview() {
        User u = session.getCurrentUser();
        try {
            double t = Double.parseDouble(target.getText().trim());
            double weekly = (t - u.getCurrentWeight()) / timeframe.getSelected();
            double bmi = HealthCalculator.calculateBMI(u.getHeightCm(), t);
            preview.setText("Weekly change: " + Formats.signed(weekly, " kg/week") + "  •  Target BMI: "
                    + Formats.one(bmi) + " (" + HealthCalculator.bmiCategory(bmi) + ")");
        } catch (IllegalArgumentException e) {
            preview.setText("Enter a target weight to see your weekly change.");
        }
    }

    private void save() {
        User u = session.getCurrentUser();
        try {
            GoalType goal = GoalType.values()[type.getSelectedIndex()];
            double t = InputValidator.parseDouble(target.getText(), "Target weight", 25, 300);
            int weeks = timeframe.getSelected();
            double weekly = Math.abs(t - u.getCurrentWeight()) / weeks;
            if (weekly > 1.0 && !Dialogs.confirm(this, "Ambitious goal", "This goal means about " + Formats.one(weekly)
                    + " kg per week. A safe pace is 0.5–1 kg per week. Save anyway?", "Save", false)) {
                return;
            }
            u.setGoal(goal, t, weeks);
            session.regeneratePlan();
            Window owner = getOwner();
            dispose();
            Toast.show(owner, "Goal updated");
        } catch (InvalidInputException | IllegalArgumentException ex) {
            Dialogs.error(this, ex.getMessage());
        }
    }
}