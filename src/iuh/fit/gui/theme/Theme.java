package iuh.fit.gui.theme;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.UIManager;

/**
 * Bảng màu chủ đạo cho giao diện Swing, áp dụng toàn cục cho toàn bộ ứng dụng.
 */
public final class Theme {

    public static final Color BG = new Color(0xE9ECEF);
    public static final Color PANEL = new Color(0xFFFFFF);
    public static final Color BORDER = new Color(0xADB5BD);
    public static final Color ACCENT = new Color(0x4682B4);
    public static final Color ACCENT_STRONG = new Color(0x38678F);
    public static final Color ACCENT_SOFT = new Color(0xEAF2F8);
    public static final Color INK = new Color(0x111827);

    public static final Color INFO = new Color(0x2563EB);
    public static final Color INFO_BG = new Color(0xEFF6FF);
    public static final Color INFO_BORDER = new Color(0x93C5FD);
    public static final Color INFO_TEXT = new Color(0x1D4ED8);

    public static final Color SUCCESS = new Color(0x059669);
    public static final Color SUCCESS_BG = new Color(0xECFDF5);
    public static final Color SUCCESS_BORDER = new Color(0x6EE7B7);
    public static final Color SUCCESS_TEXT = new Color(0x047857);

    public static final Color WARNING = new Color(0xD97706);
    public static final Color WARNING_BG = new Color(0xFFFBEB);
    public static final Color WARNING_BORDER = new Color(0xFCD34D);
    public static final Color WARNING_TEXT = new Color(0xB45309);

    public static final Color DANGER = new Color(0xDC2626);
    public static final Color DANGER_BG = new Color(0xFEF2F2);
    public static final Color DANGER_BORDER = new Color(0xFCA5A5);
    public static final Color DANGER_TEXT = new Color(0xB91C1C);

    public static JPanel contentPanel(int padding) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PANEL);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(padding, padding, padding, padding)));
        return panel;
    }

    private Theme() {
        throw new AssertionError("Utility class");
    }

    public static void install() {
        UIManager.put("control", BG);
        UIManager.put("Panel.background", PANEL);
        UIManager.put("MenuBar.background", BG);
        UIManager.put("Menu.background", BG);
        UIManager.put("Menu.foreground", INK);
        UIManager.put("Menu.selectionBackground", ACCENT);
        UIManager.put("Menu.selectionForeground", PANEL);
        UIManager.put("MenuItem.background", PANEL);
        UIManager.put("MenuItem.foreground", INK);
        UIManager.put("MenuItem.selectionBackground", ACCENT);
        UIManager.put("MenuItem.selectionForeground", PANEL);
        UIManager.put("Label.foreground", INK);
        UIManager.put("Button.background", BG);
        UIManager.put("Button.foreground", INK);
        UIManager.put("TextField.background", PANEL);
        UIManager.put("TextArea.background", PANEL);
        UIManager.put("Table.background", PANEL);
        UIManager.put("Table.gridColor", BORDER);
        UIManager.put("Table.selectionBackground", ACCENT_SOFT);
        UIManager.put("Table.selectionForeground", INK);
        UIManager.put("TableHeader.background", BG);
        UIManager.put("OptionPane.background", PANEL);
    }
}
