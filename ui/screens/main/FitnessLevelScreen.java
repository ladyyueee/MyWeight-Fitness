package myweigh.ui.screens.onboarding;

import myweigh.model.Equipment;
import myweigh.model.FitnessLevel;
import myweigh.model.User;
import myweigh.ui.MainFrame;
import myweigh.ui.components.ChipToggle;
import myweigh.ui.components.Icons;
import myweigh.ui.components.ListCard;
import myweigh.ui.components.IconBadge;
import myweigh.ui.components.OptionCard;
import myweigh.ui.components.PillButton;
import myweigh.ui.components.ResponsiveGrid;
import myweigh.ui.components.Stack;
import myweigh.ui.components.UIFactory;
import myweigh.ui.components.WrapText;
import myweigh.ui.theme.Theme;

import javax.swing.ButtonGroup;
import java.awt.Font;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Panel 06 — Select Your Fitness Level. Step 4 of 4.
 * Also asks for the category-specific focus (target muscle, sport or recovery
 * area) and the available equipment, as described in the program flow.
 */
public class FitnessLevelScreen extends OnboardingStepScreen {
    private final Map<FitnessLevel, OptionCard> cards = new EnumMap<>(FitnessLevel.class);
    private final Stack focusSection = new Stack(10);
    private final List<ChipToggle> focusChips = new ArrayList<>();
    private final List<ChipToggle> equipmentChips = new ArrayList<>();
    private final User user;

    public FitnessLevelScreen(MainFrame frame, OnboardingDraft draft) {
        super(frame, draft, 4);
        user = draft.getUser();
        form.add(UIFactory.title("Select Your Fitness Level", 24));
        form.add(new WrapText("Choose the level that matches your fitness condition.",
                Theme.font(Font.PLAIN, 13), theme.muted));

        for (FitnessLevel level : FitnessLevel.values()) {
            OptionCard card = new OptionCard(null, level.getLabel(), level.getDescription(), true, () -> selectLevel(level));
            cards.put(level, card);
            form.add(card);
        }

        form.add(UIFactory.gap(4));
        form.add(focusSection);

        form.add(UIFactory.label("Available Equipment", Font.BOLD, 15, theme.text));
        form.add(new WrapText("Plans only use equipment you have. Bodyweight exercises are always included.",
                Theme.font(Font.PLAIN, 12.5f), theme.muted));
        ResponsiveGrid equipmentGrid = new ResponsiveGrid(150, 3, 8);
        for (String name : Equipment.ALL) {
            ChipToggle chip = new ChipToggle(name, user.getEquipment().isAvailable(name));
            equipmentChips.add(chip);
            equipmentGrid.add(chip);
        }
        form.add(equipmentGrid);

        PillButton next = new PillButton("Next", PillButton.Style.PRIMARY);
        next.addActionListener(e -> finish());
        footer.add(next);
        selectLevel(user.getFitnessLevel());
    }

    private void selectLevel(FitnessLevel level) {
        user.setFitnessLevel(level);
        for (Map.Entry<FitnessLevel, OptionCard> e : cards.entrySet()) {
            e.getValue().setSelected(e.getKey() == level);
        }
        rebuildFocus();
    }

    /** Builds the focus section for this user's category (polymorphic options). */
    private void rebuildFocus() {
        focusSection.removeAll();
        focusChips.clear();
        focusSection.add(UIFactory.label("Your " + user.getFocusLabel(), Font.BOLD, 15, theme.text));
        if (user.isFocusSelectable()) {
            ButtonGroup group = new ButtonGroup();
            ResponsiveGrid grid = new ResponsiveGrid(130, 3, 8);
            for (String option : user.getFocusOptions()) {
                ChipToggle chip = new ChipToggle(option, option.equals(user.getFocusArea()));
                group.add(chip);
                focusChips.add(chip);
                grid.add(chip);
            }
            focusSection.add(grid);
        } else {
            focusSection.add(new ListCard(new IconBadge(Icons.Kind.CALENDAR, 40, 20, theme.primary, java.awt.Color.WHITE, false),
                    "Monthly muscle rotation", "Assigned automatically for beginners", null, true, null));
        }
        focusSection.revalidate();
        form.revalidate();
        form.repaint();
    }

    private void finish() {
        for (ChipToggle chip : focusChips) {
            if (chip.isSelected()) {
                user.setFocusArea(chip.getText());
            }
        }
        List<String> equipment = new ArrayList<>();
        for (ChipToggle chip : equipmentChips) {
            if (chip.isSelected()) {
                equipment.add(chip.getText());
            }
        }
        user.getEquipment().setAvailable(equipment);
        user.setWorkoutDaysPerWeek(user.getRecommendedDaysPerWeek());
        frame.finishOnboarding(user);
    }

    @Override
    protected void goBack() {
        frame.showOnboardingStep(3, draft);
    }
}