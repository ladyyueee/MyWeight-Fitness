package myweigh.ui.dialogs;

import myweigh.AppInfo;
import myweigh.ui.components.PillButton;
import myweigh.ui.components.UIFactory;
import myweigh.ui.components.WrapText;
import myweigh.ui.theme.Theme;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import java.awt.Component;
import java.awt.Font;
import java.awt.Image;
import java.net.URL;

/** About the application, with the project logo. */
public class AboutDialog extends AppDialog {

    public AboutDialog(Component parent) {
        super(parent, "About " + AppInfo.NAME);
        Theme t = Theme.get();
        URL logoUrl = AboutDialog.class.getResource("/myweigh/resources/logo.jpg");
        if (logoUrl != null) {
            ImageIcon raw = new ImageIcon(logoUrl);
            int width = 300;
            int height = raw.getIconHeight() * width / Math.max(1, raw.getIconWidth());
            JLabel logo = new JLabel(new ImageIcon(raw.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH)));
            content.add(UIFactory.wrapCenter(logo));
        } else {
            content.add(UIFactory.wrapCenter(UIFactory.logoBadge(64)));
        }
        content.add(UIFactory.centered(UIFactory.label(AppInfo.NAME + " " + AppInfo.VERSION, Font.BOLD, 16, t.text)));
        content.add(new WrapText("A Java-based fitness recommendation and tracking system for Gym Enthusiasts, "
                + "Athletes and Rehabilitation Patients. It calculates BMI and daily calorie targets, generates "
                + "personalized workout plans, tracks hydration, pain levels, workout history and progressive load, "
                + "and promotes users to higher fitness levels as they complete their plans.\n\n"
                + "This application does not provide medical diagnosis or treatment.",
                Theme.font(Font.PLAIN, 13.5f), t.muted));

        PillButton close = new PillButton("Close", PillButton.Style.PRIMARY);
        close.addActionListener(e -> dispose());
        addFooterButton(close);
    }
}
