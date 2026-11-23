package myweigh.ui.dialogs;

import myweigh.model.Equipment;
import myweigh.model.Exercise;
import myweigh.model.User;
import myweigh.service.Session;
import myweigh.ui.components.Badge;
import myweigh.ui.components.Dropdown;
import myweigh.ui.components.IconBadge;
import myweigh.ui.components.Icons;
import myweigh.ui.components.InputField;
import myweigh.ui.components.ListCard;
import myweigh.ui.components.PillButton;
import myweigh.ui.components.RoundedPanel;
import myweigh.ui.components.SegmentedControl;
import myweigh.ui.components.Stack;
import myweigh.ui.components.UIFactory;
import myweigh.ui.components.WrapText;
import myweigh.ui.theme.Theme;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

/** Exercise library (search and filter) and the equipment guide. */
public class LibraryDialog extends AppDialog {
    private static final String ALL = "All categories";
    private final Session session;
    private final Stack body = new Stack(10);
    private final InputField search = new InputField(Icons.Kind.SEARCH, "Search exercise, muscle or equipment", false, null);
    private final Dropdown<String> filter;
    private int tab;

    public LibraryDialog(Component parent, Session session, int initialTab) {
        super(parent, "Exercise Library");
        this.session = session;
        this.tab = initialTab;
        List<String> categories = new ArrayList<>();
        categories.add(ALL);
        categories.addAll(session.getLibrary().getCategories());
        filter = new Dropdown<>(categories, ALL, s -> s);

        SegmentedControl tabs = new SegmentedControl("Exercises", "Equipment Guide");
        tabs.setSelectedIndex(tab);
        tabs.addChangeListener(i -> {
            tab = i;
            render();
        });
        content.add(tabs);
        content.add(body);

        search.getField().getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                renderResults();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                renderResults();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                renderResults();
            }
        });
        filter.addChangeListener(s -> renderResults());

        PillButton close = new PillButton("Close", PillButton.Style.OUTLINE);
        close.addActionListener(e -> dispose());
        addFooterButton(close);
        render();
    }

    @Override
    protected int dialogWidth() {
        return 580;
    }

    private final Stack results = new Stack(8);

    private void render() {
        body.removeAll();
        if (tab == 0) {
            body.add(search);
            body.add(filter);
            body.add(results);
            renderResults();
        } else {
            renderGuide();
        }
        refreshLayout();
    }

    private void renderResults() {
        results.removeAll();
        String category = filter.getSelected();
        List<Exercise> found = new ArrayList<>();
        for (Exercise e : session.getLibrary().searchExercise(search.getText())) {
            if (ALL.equals(category) || category.equals(e.getMuscleGroup())) {
                found.add(e);
            }
        }
        Theme t = Theme.get();
        results.add(UIFactory.muted(found.size() + " exercises", 12.5f));
        User u = session.getCurrentUser();
        for (Exercise e : found) {
            boolean available = u.getEquipment().hasAll(e.getEquipment());
            results.add(new ListCard(new IconBadge(Icons.forExercise(e), 40, 20, t.mint, t.primary, true),
                    e.getName(), e.getMuscleGroup() + " • " + e.getEquipmentText(),
                    available ? UIFactory.iconLabel(Icons.Kind.CHEVRON_RIGHT, 18, t.muted) : Badge.neutral("No equipment"),
                    false, () -> new ExerciseDialog(this, session, e, null).showDialog()));
        }
        refreshLayout();
    }

    private void renderGuide() {
        Theme t = Theme.get();
        User u = session.getCurrentUser();
        for (String name : Equipment.ALL) {
            RoundedPanel card = UIFactory.card(new BorderLayout(12, 0));
            card.add(new IconBadge(Icons.Kind.DUMBBELL, 40, 20, t.mint, t.primary, true), BorderLayout.WEST);
            Stack s = new Stack(4);
            s.add(UIFactory.row(UIFactory.label(name, Font.BOLD, 15, t.text),
                    u.getEquipment().isAvailable(name) ? Badge.success("AVAILABLE") : Badge.neutral("NOT SELECTED")));
            s.add(new WrapText(Equipment.getGuide(name), Theme.font(Font.PLAIN, 13), t.muted));
            card.add(s, BorderLayout.CENTER);
            body.add(card);
        }
    }

    private void refreshLayout() {
        content.revalidate();
        content.repaint();
    }
}
