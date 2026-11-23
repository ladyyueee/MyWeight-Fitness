package myweigh.ui.screens.onboarding;

import myweigh.service.HealthCalculator;
import myweigh.service.InputValidator;
import myweigh.service.InvalidInputException;
import myweigh.ui.MainFrame;
import myweigh.ui.components.Badge;
import myweigh.ui.components.BmiScale;
import myweigh.ui.components.Formats;
import myweigh.ui.components.Icons;
import myweigh.ui.components.InputField;
import myweigh.ui.components.PillButton;
import myweigh.ui.components.ResponsiveGrid;
import myweigh.ui.components.RoundedPanel;
import myweigh.ui.components.SegmentedControl;
import myweigh.ui.components.Stack;
import myweigh.ui.components.UIFactory;
import myweigh.ui.components.WrapText;
import myweigh.ui.dialogs.Dialogs;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Font;

/** Panel 03 — Tell us about yourself (BMI). Step 1 of 4. */
public class BmiScreen extends OnboardingStepScreen {
    private final InputField height;
    private final InputField weight;
    private final InputField age;
    private final SegmentedControl gender;
    private final RoundedPanel resultCard;
    private final JLabel bmiValue;
    private final JPanel badgeSlot;
    private final BmiScale scale;

    public BmiScreen(MainFrame frame, OnboardingDraft draft) {
        super(frame, draft, 1);
        form.add(miniBrand());
        form.add(UIFactory.title("Tell us about yourself", 24));
        form.add(new WrapText("Share your details to calculate your BMI and get personalized recommendations",
                myweigh.ui.theme.Theme.font(Font.PLAIN, 13), theme.muted));

        height = new InputField(null, "170", false, "cm");
        weight = new InputField(null, "65", false, "kg");
        age = new InputField(null, "21", false, "yrs");
        gender = new SegmentedControl("Male", "Female");
        form.add(UIFactory.fieldGroup("Height (cm)", height));
        form.add(UIFactory.fieldGroup("Weight (kg)", weight));
        ResponsiveGrid row = new ResponsiveGrid(150, 2, 12);
        row.add(UIFactory.fieldGroup("Age", age));
        row.add(UIFactory.fieldGroup("Gender", gender));
        form.add(row);

        PillButton calculate = new PillButton("Calculate BMI", PillButton.Style.PRIMARY);
        calculate.addActionListener(e -> calculate());
        form.add(calculate);

        resultCard = UIFactory.tintedCard(new BorderLayout());
        Stack result = new Stack(10);
        bmiValue = UIFactory.label("0.0", Font.BOLD, 34, theme.primaryDark);
        badgeSlot = UIFactory.transparent(new BorderLayout());
        JPanel valueRow = UIFactory.hbox(bmiValue, badgeSlot);
        result.add(UIFactory.row(UIFactory.muted("Your BMI", 13),
                UIFactory.iconLabel(Icons.Kind.PULSE, 22, theme.primary)));
        result.add(valueRow);
        scale = new BmiScale(22);
        result.add(scale);
        resultCard.add(result, BorderLayout.CENTER);
        resultCard.setBorder(new EmptyBorder(16, 18, 16, 18));
        resultCard.setVisible(false);
        form.add(resultCard);

        PillButton next = new PillButton("Continue to Weight Goal", PillButton.Style.DARK);
        next.addActionListener(e -> {
            if (calculate()) {
                frame.showOnboardingStep(2, draft);
            }
        });
        footer.add(next);

        if (draft.isBmiCalculated()) {
            height.setText(Formats.number(draft.getHeightCm()));
            weight.setText(Formats.number(draft.getWeight()));
            age.setText(String.valueOf(draft.getAge()));
            gender.setSelectedIndex("Female".equals(draft.getGender()) ? 1 : 0);
            calculate();
        }
    }

    /** Validates the inputs, shows the BMI and stores the values. Returns false on invalid input. */
    private boolean calculate() {
        try {
            double h = InputValidator.parseDouble(height.getText(), "Height", 100, 250);
            double w = InputValidator.parseDouble(weight.getText(), "Weight", 25, 300);
            int a = InputValidator.parseInt(age.getText(), "Age", 10, 100);
            String g = gender.getSelectedIndex() == 1 ? "Female" : "Male";
            double bmi = HealthCalculator.calculateBMI(h, w);
            draft.setBody(h, w, a, g);
            bmiValue.setText(Formats.one(bmi));
            badgeSlot.removeAll();
            badgeSlot.add(Badge.forBmi(HealthCalculator.bmiCategory(bmi)), BorderLayout.CENTER);
            scale.setBmi(bmi);
            resultCard.setVisible(true);
            form.revalidate();
            form.repaint();
            return true;
        } catch (InvalidInputException ex) {
            Dialogs.error(this, ex.getMessage());
            return false;
        }
    }

    @Override
    protected void goBack() {
        frame.showCreateAccount(draft);
    }
}