package myweigh.ui.dialogs;

import myweigh.model.FitnessLevel;
import myweigh.model.User;
import myweigh.model.UserCategory;
import myweigh.service.InputValidator;
import myweigh.service.InvalidInputException;
import myweigh.service.Session;
import myweigh.service.UserManager;
import myweigh.ui.MainFrame;
import myweigh.ui.components.Dropdown;
import myweigh.ui.components.Formats;
import myweigh.ui.components.Icons;
import myweigh.ui.components.InputField;
import myweigh.ui.components.PillButton;
import myweigh.ui.components.ResponsiveGrid;
import myweigh.ui.components.SegmentedControl;
import myweigh.ui.components.Stack;
import myweigh.ui.components.Toast;
import myweigh.ui.components.UIFactory;

import java.awt.Component;
import java.awt.Window;
import java.time.LocalDate;
import java.util.Arrays;

/** Profile management: edit metrics and category, or delete the account (CRUD). */
public class EditProfileDialog extends AppDialog {
    private final Session session;
    private final InputField name;
    private final InputField age;
    private final InputField height;
    private final InputField weight;
    private final SegmentedControl gender;
    private final Dropdown<UserCategory> category;
    private final Dropdown<FitnessLevel> level;
    private final Dropdown<Integer> days;
    private final Stack focusBox = new Stack(7);
    private Dropdown<String> focus;

    public EditProfileDialog(Component parent, Session session) {
        super(parent, "Edit Profile");
        this.session = session;
        User u = session.getCurrentUser();

        name = new InputField(Icons.Kind.PROFILE, "Full name", false, null);
        name.setText(u.getFullName());
        content.add(UIFactory.fieldGroup("Full Name", name));

        age = new InputField(null, "21", false, "yrs");
        age.setText(String.valueOf(u.getAge()));
        gender = new SegmentedControl("Male", "Female");
        gender.setSelectedIndex("Female".equals(u.getGender()) ? 1 : 0);
        height = new InputField(null, "170", false, "cm");
        height.setText(Formats.number(u.getHeightCm()));
        weight = new InputField(null, "65", false, "kg");
        weight.setText(Formats.number(u.getCurrentWeight()));
        ResponsiveGrid metrics = new ResponsiveGrid(170, 2, 12);
        metrics.add(UIFactory.fieldGroup("Age", age));
        metrics.add(UIFactory.fieldGroup("Gender", gender));
        metrics.add(UIFactory.fieldGroup("Height (cm)", height));
        metrics.add(UIFactory.fieldGroup("Weight (kg)", weight));
        content.add(metrics);

        category = new Dropdown<>(Arrays.asList(UserCategory.values()), u.getCategory(), UserCategory::getLabel);
        level = new Dropdown<>(Arrays.asList(FitnessLevel.values()), u.getFitnessLevel(), FitnessLevel::getLabel);
        ResponsiveGrid profile = new ResponsiveGrid(170, 2, 12);
        profile.add(UIFactory.fieldGroup("Category", category));
        profile.add(UIFactory.fieldGroup("Fitness Level", level));
        content.add(profile);
        content.add(focusBox);

        days = new Dropdown<>(Arrays.asList(2, 3, 4, 5, 6, 7), u.getWorkoutDaysPerWeek(), d -> d + " days per week");
        content.add(UIFactory.fieldGroup("Workout Frequency", days));

        category.addChangeListener(c -> rebuildFocus());
        level.addChangeListener(l -> rebuildFocus());
        rebuildFocus();

        content.add(UIFactory.gap(4));
        content.add(UIFactory.dangerAction("Delete Account", this::deleteAccount));

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

    /** Focus options depend on the selected category (asks a preview object polymorphically). */
    private void rebuildFocus() {
        User current = session.getCurrentUser();
        User preview = UserManager.createUser(category.getSelected(), "Preview", "preview@local", "");
        preview.setFitnessLevel(level.getSelected());
        focusBox.removeAll();
        if (preview.isFocusSelectable()) {
            String selected = Arrays.asList(preview.getFocusOptions()).contains(current.getFocusArea())
                    ? current.getFocusArea() : preview.getDefaultFocus();
            focus = new Dropdown<>(Arrays.asList(preview.getFocusOptions()), selected, s -> s);
            focusBox.add(UIFactory.fieldGroup(preview.getFocusLabel(), focus));
        } else {
            focus = null;
            Dropdown<String> locked = new Dropdown<>(Arrays.asList("Monthly muscle rotation (Beginner)"), null, s -> s);
            locked.setActive(false);
            focusBox.add(UIFactory.fieldGroup(preview.getFocusLabel(), locked));
        }
        content.revalidate();
        content.repaint();
    }

    private void save() {
        try {
            String fullName = InputValidator.requireText(name.getText(), "Full name");
            int a = InputValidator.parseInt(age.getText(), "Age", 10, 100);
            double h = InputValidator.parseDouble(height.getText(), "Height", 100, 250);
            double w = InputValidator.parseDouble(weight.getText(), "Weight", 25, 300);

            UserManager manager = session.getUserManager();
            User u = session.getCurrentUser();
            if (category.getSelected() != u.getCategory()) {
                u = manager.changeCategory(u, category.getSelected());
                session.setCurrentUser(u);
            }
            u.setFullName(fullName);
            u.setAge(a);
            u.setHeightCm(h);
            u.setGender(gender.getSelectedIndex() == 1 ? "Female" : "Male");
            if (Math.abs(w - u.getCurrentWeight()) > 0.001) {
                u.logWeight(w, LocalDate.now());
            }
            if (level.getSelected() != u.getFitnessLevel()) {
                u.setFitnessLevel(level.getSelected());
                u.setLevelStartDate(LocalDate.now());
            }
            if (focus != null) {
                u.setFocusArea(focus.getSelected());
            }
            u.setWorkoutDaysPerWeek(days.getSelected());
            manager.editUser(u);
            session.regeneratePlan();
            Window owner = getOwner();
            dispose();
            Toast.show(owner, "Profile updated");
        } catch (InvalidInputException | IllegalArgumentException ex) {
            Dialogs.error(this, ex.getMessage());
        }
    }

    private void deleteAccount() {
        if (Dialogs.confirm(this, "Delete account",
                "This permanently deletes your profile, plan and history. This cannot be undone.", "Delete", true)) {
            MainFrame frame = MainFrame.of(this);
            session.deleteCurrentUser();
            dispose();
            frame.showLogin();
            Toast.show(frame, "Your account has been deleted");
        }
    }
}