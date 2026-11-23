package myweigh.ui.components;

import java.awt.Color;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/** Number and date formatting used across the UI. */
public final class Formats {
    public static final DateTimeFormatter LONG = DateTimeFormatter.ofPattern("MMMM dd, yyyy", Locale.ENGLISH);
    public static final DateTimeFormatter SHORT = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.ENGLISH);
    public static final DateTimeFormatter DAY = DateTimeFormatter.ofPattern("EEE, MMM d", Locale.ENGLISH);
    public static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm", Locale.ENGLISH);

    private Formats() {
    }

    public static String number(double v) {
        if (Math.abs(v - Math.round(v)) < 0.05) {
            return String.valueOf(Math.round(v));
        }
        return String.format(Locale.US, "%.1f", v);
    }

    public static String kg(double v) {
        return number(v) + " kg";
    }

    public static String one(double v) {
        return String.format(Locale.US, "%.1f", v);
    }

    public static String kcal(int v) {
        return String.format(Locale.US, "%,d kcal", v);
    }

    public static String thousands(int v) {
        return String.format(Locale.US, "%,d", v);
    }

    public static String percent(double ratio) {
        return Math.round(ratio * 100) + "%";
    }

    public static String signed(double v, String unit) {
        return (v > 0 ? "+" : "") + number(v) + unit;
    }

    public static String hex(Color c) {
        return String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());
    }

    /** Escapes text for use inside HTML labels. */
    public static String html(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
