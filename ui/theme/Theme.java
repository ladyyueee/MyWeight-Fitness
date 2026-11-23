package myweigh.ui.theme;

import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Color themes. "Emerald" matches the UI reference; the others are user-selectable. */
public final class Theme {
    public final String name;
    public final Color primary;
    public final Color primaryDark;
    public final Color header;
    public final Color mint;
    public final Color mintBorder;
    public final Color accent;
    public final Color background;
    public final Color card;
    public final Color subtle;
    public final Color text;
    public final Color muted;
    public final Color border;
    public final Color danger;
    public final Color dangerBg;
    public final Color water;
    public final Color dark;

    private static final List<Theme> THEMES = Arrays.asList(
            new Theme("Emerald", 0x0F7B5C, 0x0B5E46, 0x0D5C44, 0xEEFAF4, 0xBFE9D5, 0xF5A524, 0xF4F7F9,
                    0xFFFFFF, 0xF1F5F9, 0x0F172A, 0x64748B, 0xE2E8F0, 0xE11D48, 0xFDECEF, 0x1EA4E0, 0x0F172A),
            new Theme("Ocean", 0x0E6BA8, 0x0A5384, 0x0B4C78, 0xEBF5FC, 0xBCDDF3, 0xF5A524, 0xF4F7FA,
                    0xFFFFFF, 0xF1F5F9, 0x0F172A, 0x64748B, 0xE2E8F0, 0xE11D48, 0xFDECEF, 0x0EA5E9, 0x0F172A),
            new Theme("Violet", 0x6D3FC0, 0x552F99, 0x4B2788, 0xF4EFFC, 0xD9C9F4, 0xF5A524, 0xF6F5FA,
                    0xFFFFFF, 0xF1F0F7, 0x1A1530, 0x6B6880, 0xE4E1EE, 0xE11D48, 0xFDECEF, 0x1EA4E0, 0x1A1530),
            new Theme("Midnight", 0x22B07D, 0x1A8E64, 0x0F3B2D, 0x13261F, 0x1F4A3A, 0xF5A524, 0x0B1215,
                    0x141C21, 0x1B252B, 0xE6EDF3, 0x8B98A5, 0x26323A, 0xF43F5E, 0x3B1820, 0x38BDF8, 0x2A3A44)
    );

    private static Theme current = THEMES.get(0);
    private static String fontFamily;

    private Theme(String name, int primary, int primaryDark, int header, int mint, int mintBorder, int accent,
                  int background, int card, int subtle, int text, int muted, int border, int danger,
                  int dangerBg, int water, int dark) {
        this.name = name;
        this.primary = new Color(primary);
        this.primaryDark = new Color(primaryDark);
        this.header = new Color(header);
        this.mint = new Color(mint);
        this.mintBorder = new Color(mintBorder);
        this.accent = new Color(accent);
        this.background = new Color(background);
        this.card = new Color(card);
        this.subtle = new Color(subtle);
        this.text = new Color(text);
        this.muted = new Color(muted);
        this.border = new Color(border);
        this.danger = new Color(danger);
        this.dangerBg = new Color(dangerBg);
        this.water = new Color(water);
        this.dark = new Color(dark);
    }

    public static Theme get() {
        return current;
    }

    public static List<Theme> all() {
        return Collections.unmodifiableList(THEMES);
    }

    public static void apply(String name) {
        for (Theme t : THEMES) {
            if (t.name.equalsIgnoreCase(name)) {
                current = t;
                return;
            }
        }
        current = THEMES.get(0);
    }

    public boolean isDark() {
        return "Midnight".equals(name);
    }

    public static Font font(int style, float size) {
        if (fontFamily == null) {
            fontFamily = pickFamily();
        }
        return new Font(fontFamily, style, 1).deriveFont(style, size);
    }

    private static String pickFamily() {
        Set<String> names = new HashSet<>(Arrays.asList(
                GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()));
        for (String candidate : new String[]{"Inter", "Plus Jakarta Sans", "Segoe UI", "SF Pro Text",
                "Helvetica Neue", "Roboto", "Noto Sans", "DejaVu Sans", "Arial"}) {
            if (names.contains(candidate)) {
                return candidate;
            }
        }
        return Font.SANS_SERIF;
    }

    public static Color alpha(Color c, int alpha) {
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha);
    }

    public static Color lighten(Color c, int amount) {
        return new Color(Math.min(255, c.getRed() + amount), Math.min(255, c.getGreen() + amount),
                Math.min(255, c.getBlue() + amount), c.getAlpha());
    }
}
