package myweigh.ui.dialogs;

import myweigh.model.User;
import myweigh.service.HealthCalculator;
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

/** Logs today's weight; BMI, goal progress and calorie target update automatically. */
public class WeightLogDialog extends AppDialog {
    private final Session session;
    private final InputField weight;

    public WeightLogDialog(Component parent, Session session) {
        super(parent, "Log Weight");
        this.session = session;
        User u = session.getCurrentUser();

        ResponsiveGrid info = new ResponsiveGrid(120, 3, 8);
        info.add(UIFactory.infoTile("Current weight", Formats.kg(u.getCurrentWeight())));
        info.add(UIFactory.infoTile("BMI", Formats.one(u.calculateBMI()) + " • " + u.getBmiCategory()));
        info.add(UIFactory.infoTile("Target", Formats.kg(u.getTargetWeight())));
        content.add(info);

        weight = new InputField(Icons.Kind.SCALE, "e.g. 64.5", false, "kg");
        weight.setText(Formats.number(u.getCurrentWeight()));
        weight.addActionListener(e -> save());
        content.add(UIFactory.fieldGroup("Today's weight (kg)", weight));

        PillButton cancel = new PillButton("Cancel", PillButton.Style.OUTLINE);
        cancel.addActionListener(e -> dispose());
        PillButton save = new PillButton("Save", PillButton.Style.PRIMARY);
        save.addActionListener(e -> save());
        addFooterButton(cancel);
        addFooterButton(save);
    }

    private void save() {
        try {
            double w = InputValidator.parseDouble(weight.getText(), "Weight", 25, 300);
            session.logWeight(w);
            User u = session.getCurrentUser();
            Window owner = getOwner();
            dispose();
            Toast.show(owner, "Weight saved • BMI " + Formats.one(u.calculateBMI()) + " ("
                    + HealthCalculator.bmiCategory(u.calculateBMI()) + ")");
        } catch (InvalidInputException ex) {
            Dialogs.error(this, ex.getMessage());
        }
    }
}
