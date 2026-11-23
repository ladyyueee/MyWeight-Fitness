package myweigh.ui.dialogs;

import myweigh.model.FitnessPlan;
import myweigh.service.HealthCalculator;
import myweigh.service.Session;
import myweigh.ui.components.Formats;
import myweigh.ui.components.IconBadge;
import myweigh.ui.components.Icons;
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

/** Hydration tracker: log glasses of water and see the last 7 days. */
public class WaterDialog extends AppDialog {
    private final Session session;
    private final JLabel count;
    private final SlimProgressBar bar;
    private final Stack history = new Stack(0);

    public WaterDialog(Component parent, Session session) {
        super(parent, "Water Intake");
        this.session = session;
        Theme t = Theme.get();

        RoundedPanel top = UIFactory.tintedCard(new BorderLayout());
        Stack s = new Stack(8);
        s.add(UIFactory.wrapCenter(new IconBadge(Icons.Kind.DROP, 52, 26, Theme.alpha(t.water, 40), t.water, true)));
        count = UIFactory.centered(UIFactory.label("", Font.BOLD, 30, t.text));
        s.add(count);
        s.add(UIFactory.centered(UIFactory.muted("glasses today • " + HealthCalculator.ML_PER_GLASS + " ml each", 12.5f)));
        bar = new SlimProgressBar(0, t.water, 8);
        s.add(bar);
        top.add(s, BorderLayout.CENTER);
        content.add(top);

        ResponsiveGrid buttons = new ResponsiveGrid(100, 3, 8);
        PillButton minus = new PillButton("1", PillButton.Style.OUTLINE, true).withIcon(Icons.Kind.MINUS);
        minus.addActionListener(e -> change(-1));
        PillButton plus = new PillButton("1 Glass", PillButton.Style.PRIMARY, true).withIcon(Icons.Kind.PLUS);
        plus.addActionListener(e -> change(1));
        PillButton plusTwo = new PillButton("2", PillButton.Style.SOFT, true).withIcon(Icons.Kind.PLUS);
        plusTwo.addActionListener(e -> change(2));
        buttons.add(minus);
        buttons.add(plus);
        buttons.add(plusTwo);
        content.add(buttons);

        content.add(UIFactory.section("Last 7 days"));
        content.add(history);

        PillButton done = new PillButton("Done", PillButton.Style.PRIMARY);
        done.addActionListener(e -> dispose());
        addFooterButton(done);
        update();
    }

    private void change(int delta) {
        session.logWater(delta);
        update();
    }

    private void update() {
        FitnessPlan plan = session.getCurrentUser().getFitnessPlan();
        LocalDate today = LocalDate.now();
        int water = plan.getWaterIntake(today);
        int target = Math.max(1, plan.getWaterTargetGlasses());
        count.setText(water + " / " + target);
        bar.setValue((double) water / target);

        history.removeAll();
        List<RowItem> rows = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            LocalDate d = today.minusDays(i);
            int value = plan.getWaterIntake(d);
            rows.add(new RowItem(null, i == 0 ? "Today" : Formats.DAY.format(d), null,
                    UIFactory.hbox(UIFactory.label(value + " / " + target, Font.BOLD, 13, Theme.get().text),
                            UIFactory.statusIcon(value >= target)), null));
        }
        history.add(UIFactory.groupCard(rows));
        history.revalidate();
        history.repaint();
    }
}
