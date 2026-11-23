package myweigh.ui.dialogs;

import myweigh.model.PainEntry;
import myweigh.model.RehabPatient;
import myweigh.service.Session;
import myweigh.ui.components.Icons;
import myweigh.ui.components.InputField;
import myweigh.ui.components.PillButton;
import myweigh.ui.components.Stack;
import myweigh.ui.components.ThemedSliderUI;
import myweigh.ui.components.Toast;
import myweigh.ui.components.UIFactory;
import myweigh.ui.components.WrapText;
import myweigh.ui.theme.Theme;

import javax.swing.JLabel;
import javax.swing.JSlider;
import java.awt.Component;
import java.awt.Font;
import java.awt.Window;

/** Safety feedback loop: rehabilitation patients confirm their current pain level. */
public class PainDialog extends AppDialog {

    public PainDialog(Component parent, Session session) {
        super(parent, "Pain Level Check");
        Theme t = Theme.get();
        content.add(new WrapText("How much pain do you feel right now? 0 means no pain and 10 means the worst pain "
                + "imaginable. Levels above " + RehabPatient.SAFE_PAIN_LIMIT + " reduce your upcoming sessions.",
                Theme.font(Font.PLAIN, 13), t.muted));
        JLabel value = UIFactory.label("0 / 10 — No pain", Font.BOLD, 20, t.primary);
        JSlider slider = ThemedSliderUI.create(0, 10, 0);
        slider.addChangeListener(e -> {
            int v = slider.getValue();
            value.setText(v + " / 10 — " + PainEntry.describe(v));
            value.setForeground(v > RehabPatient.SAFE_PAIN_LIMIT ? t.danger : t.primary);
        });
        Stack s = new Stack(6);
        s.add(UIFactory.centered(value));
        s.add(slider);
        content.add(s);
        InputField note = new InputField(Icons.Kind.EDIT, "Optional note, e.g. after stairs", false, null);
        content.add(UIFactory.fieldGroup("Note", note));

        PillButton cancel = new PillButton("Cancel", PillButton.Style.OUTLINE);
        cancel.addActionListener(e -> dispose());
        PillButton save = new PillButton("Confirm", PillButton.Style.PRIMARY);
        save.addActionListener(e -> {
            boolean warning = session.recordPain(slider.getValue(), note.getText());
            Window owner = getOwner();
            dispose();
            if (warning) {
                Dialogs.info(owner, "High pain level recorded", "Your next sessions have been made lighter. Rest, avoid "
                        + "movements that cause sharp pain, and contact your doctor or therapist if the pain continues.");
            } else {
                Toast.show(owner, "Pain level recorded");
            }
        });
        addFooterButton(cancel);
        addFooterButton(save);
    }
}
