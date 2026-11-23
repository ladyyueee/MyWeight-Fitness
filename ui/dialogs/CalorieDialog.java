package myweigh.ui.dialogs;

import myweigh.model.FitnessPlan;
import myweigh.service.InputValidator;
import myweigh.service.InvalidInputException;
import myweigh.service.Session;
import myweigh.ui.components.Formats;
import myweigh.ui.components.IconBadge;
import myweigh.ui.components.Icons;
import myweigh.ui.components.InputField;
import myweigh.ui.components.PillButton;
import myweigh.ui.components.ResponsiveGrid;
import myweigh.ui.components.RoundedPanel;
import myweigh.ui.components.RowItem;
import myweigh.ui.components.SlimProgressBar;
import myweigh.ui.components.Stack;
import myweigh.ui.components.UIFactory;
import myweigh.ui.theme.Theme;

import javax.swing.JLabel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Calorie monitor: log calories eaten today against the daily calorie target. */
public class CalorieDialog extends AppDialog {
    private final Session session;
    private final JLabel count;
    private final JLabel remaining;
    private final SlimProgressBar bar;
    private final InputField amount;
    private final Stack history = new Stack(0);

    public CalorieDialog(Component parent, Session session) {
        super(parent, "Daily Calories");
        this.session = session;
        Theme t = Theme.get();

        RoundedPanel top = UIFactory.tintedCard(new BorderLayout());
        Stack s = new Stack(8);
        s.add(UIFactory.wrapCenter(new IconBadge(Icons.Kind.FLAME, 52, 26, Theme.alpha(t.accent, 45), t.accent, true)));
        count = UIFactory.centered(UIFactory.label("", Font.BOLD, 28, t.text));
        s.add(count);
        remaining = UIFactory.centered(UIFactory.muted("", 12.5f));
        s.add(remaining);
        bar = new SlimProgressBar(0, t.accent, 8);
        s.add(bar);
        top.add(s, BorderLayout.CENTER);
        content.add(top);

        amount = new InputField(Icons.Kind.FLAME, "e.g. 450", false, "kcal");
        PillButton add = new PillButton("Add", PillButton.Style.PRIMARY, true).withIcon(Icons.Kind.PLUS);
        add.addActionListener(e -> addAmount());
        amount.addActionListener(e -> addAmount());
        content.add(UIFactory.fieldGroup("Calories eaten", UIFactory.row(amount, add)));

        ResponsiveGrid quick = new ResponsiveGrid(90, 4, 8);
        for (int v : new int[]{100, 250, 500}) {
            PillButton b = new PillButton("+" + v, PillButton.Style.SOFT, true);
            b.addActionListener(e -> {
                session.logCalories(v);
                update();
            });
            quick.add(b);
        }
        PillButton clear = new PillButton("Clear today", PillButton.Style.DANGER, true);
        clear.addActionListener(e -> {
            if (Dialogs.confirm(this, "Clear today's calories", "Reset today's logged calories to 0?", "Clear", true)) {
                session.clearCalories();
                update();
            }
        });
        quick.add(clear);
        content.add(quick);

        content.add(UIFactory.section("Last 7 days"));
        content.add(history);

        PillButton done = new PillButton("Done", PillButton.Style.PRIMARY);
        done.addActionListener(e -> dispose());
        addFooterButton(done);
        update();
    }

    private void addAmount() {
        try {
            int kcal = InputValidator.parseInt(amount.getText(), "Calories", 1, 5000);
            session.logCalories(kcal);
            amount.setText("");
            update();
        } catch (InvalidInputException ex) {
            Dialogs.error(this, ex.getMessage());
        }
    }

    private void update() {
        FitnessPlan plan = session.getCurrentUser().getFitnessPlan();
        LocalDate today = LocalDate.now();
        int eaten = plan.getCalories(today);
        int target = Math.max(1, plan.getDailyCalories());
        count.setText(Formats.thousands(eaten) + " / " + Formats.kcal(target));
        int left = target - eaten;
        remaining.setText(left >= 0 ? Formats.kcal(left) + " remaining today" : Formats.kcal(-left) + " over today's target");
        bar.setValue((double) eaten / target);

        history.removeAll();
        List<RowItem> rows = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            LocalDate d = today.minusDays(i);
            rows.add(new RowItem(null, i == 0 ? "Today" : Formats.DAY.format(d), null,
                    UIFactory.label(Formats.kcal(plan.getCalories(d)), Font.BOLD, 13, Theme.get().text), null));
        }
        history.add(UIFactory.groupCard(rows));
        history.revalidate();
        history.repaint();
    }
}
