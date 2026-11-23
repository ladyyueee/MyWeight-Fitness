package myweigh.ui.dialogs;

import myweigh.model.Equipment;
import myweigh.model.User;
import myweigh.service.Session;
import myweigh.ui.components.ChipToggle;
import myweigh.ui.components.PillButton;
import myweigh.ui.components.ResponsiveGrid;
import myweigh.ui.components.Toast;
import myweigh.ui.components.WrapText;
import myweigh.ui.theme.Theme;

import java.awt.Component;
import java.awt.Font;
import java.awt.Window;
import java.util.ArrayList;
import java.util.List;

/** Equipment customization: plans are regenerated to match the selected equipment. */
public class EquipmentDialog extends AppDialog {
    private final List<ChipToggle> chips = new ArrayList<>();

    public EquipmentDialog(Component parent, Session session) {
        super(parent, "Workout Equipment");
        User u = session.getCurrentUser();
        content.add(new WrapText("Select the equipment you can use. Your plan only recommends exercises you can do "
                + "with it. Bodyweight exercises are always included.", Theme.font(Font.PLAIN, 13), Theme.get().muted));
        ResponsiveGrid grid = new ResponsiveGrid(150, 3, 8);
        for (String name : Equipment.ALL) {
            ChipToggle chip = new ChipToggle(name, u.getEquipment().isAvailable(name));
            chips.add(chip);
            grid.add(chip);
        }
        content.add(grid);

        PillButton cancel = new PillButton("Cancel", PillButton.Style.OUTLINE);
        cancel.addActionListener(e -> dispose());
        PillButton save = new PillButton("Save", PillButton.Style.PRIMARY);
        save.addActionListener(e -> {
            List<String> selected = new ArrayList<>();
            for (ChipToggle chip : chips) {
                if (chip.isSelected()) {
                    selected.add(chip.getText());
                }
            }
            session.updateEquipment(selected);
            Window owner = getOwner();
            dispose();
            Toast.show(owner, "Equipment saved • plan updated");
        });
        addFooterButton(cancel);
        addFooterButton(save);
    }

    @Override
    protected int dialogWidth() {
        return 520;
    }
}
