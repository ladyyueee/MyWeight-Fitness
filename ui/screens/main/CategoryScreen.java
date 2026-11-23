package myweigh.ui.screens.onboarding;

import myweigh.model.Timeframes;
import myweigh.model.User;
import myweigh.model.UserCategory;
import myweigh.service.UserManager;
import myweigh.ui.MainFrame;
import myweigh.ui.components.Formats;
import myweigh.ui.components.Icons;
import myweigh.ui.components.OptionCard;
import myweigh.ui.components.PillButton;
import myweigh.ui.components.RoundedPanel;
import myweigh.ui.components.SlimProgressBar;
import myweigh.ui.components.Stack;
import myweigh.ui.components.UIFactory;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.MatteBorder;
import java.awt.BorderLayout;
import java.awt.Font;
import java.time.LocalDate;
import java.util.EnumMap;
import java.util.Map;

/** Panel 05 — I am a... (user category). Step 3 of 4. */
public class CategoryScreen extends OnboardingStepScreen {
    private final Map<UserCategory, OptionCard> cards = new EnumMap<>(UserCategory.class);
    private final JLabel calories;
    private UserCategory selected;

    public CategoryScreen(MainFrame frame, OnboardingDraft draft) {
        super(frame, draft, 3);
        selected = draft.getCategory();
        form.add(UIFactory.title("I am a...", 26));

        for (UserCategory c : UserCategory.values()) {
            OptionCard card = new OptionCard(iconFor(c), c.getLabel(), c.getDescription(), false, () -> select(c));
            cards.put(c, card);
            form.add(card);
        }

        RoundedPanel summary = UIFactory.tintedCard(new BorderLayout());
        Stack s = new Stack(8);
        s.add(UIFactory.row(UIFactory.muted("Target Weight Goal", 12.5f),
                UIFactory.label(Formats.kg(draft.getTargetWeight()) + " • " + Timeframes.shortLabel(draft.getTimeframeWeeks()),
                        Font.BOLD, 13, theme.primary)));
        s.add(new SlimProgressBar(1.0, theme.primary, 8));
        JPanel ticks = UIFactory.transparent(new BorderLayout());
        ticks.add(UIFactory.muted("1 wk", 11), BorderLayout.WEST);
        ticks.add(UIFactory.muted(draft.getTimeframeWeeks() + " wks", 11), BorderLayout.EAST);
        s.add(ticks);
        calories = UIFactory.label("", Font.BOLD, 22, theme.primaryDark);
        JPanel calRow = UIFactory.row(UIFactory.muted("Daily Target Calorie", 13), calories);
        calRow.setBorder(new MatteBorder(1, 0, 0, 0, theme.mintBorder));
        s.add(calRow);
        summary.add(s, BorderLayout.CENTER);
        form.add(summary);

        PillButton confirm = new PillButton("Confirm Category", PillButton.Style.PRIMARY);
        confirm.addActionListener(e -> {
            draft.setCategory(selected);
            draft.setUser(buildUser(selected));
            frame.showOnboardingStep(4, draft);
        });
        footer.add(confirm);
        select(selected);
    }

    private static Icons.Kind iconFor(UserCategory c) {
        switch (c) {
            case ATHLETE:
                return Icons.Kind.TROPHY;
            case REHAB_PATIENT:
                return Icons.Kind.HEART;
            default:
                return Icons.Kind.DUMBBELL;
        }
    }

    private void select(UserCategory category) {
        selected = category;
        for (Map.Entry<UserCategory, OptionCard> e : cards.entrySet()) {
            e.getValue().setSelected(e.getKey() == category);
        }
        // The calorie target depends on the category (polymorphic activity bonus).
        User preview = buildUser(category);
        calories.setText(Formats.kcal(frame.getSession().getEngine().calculateCalorieTarget(preview)));
    }

    /** Creates the user object of the chosen subclass from the onboarding answers. */
    private User buildUser(UserCategory category) {
        User u = UserManager.createUser(category, draft.getFullName(), draft.getEmail(), draft.getPasswordHash());
        u.setAge(draft.getAge());
        u.setGender(draft.getGender());
        u.setHeightCm(draft.getHeightCm());
        u.logWeight(draft.getWeight(), LocalDate.now());
        u.setGoal(draft.getGoalType(), draft.getTargetWeight(), draft.getTimeframeWeeks());
        return u;
    }

    @Override
    protected void goBack() {
        frame.showOnboardingStep(2, draft);
    }
}