package iuh.fit.gui.theme;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.plaf.BorderUIResource;
import javax.swing.plaf.ColorUIResource;

import iuh.fit.core.context.AppContext;

/**
 * Bảng màu chủ đạo cho giao diện Swing, hỗ trợ chế độ sáng/tối và áp dụng toàn cục.
 */
public final class Theme {

    public record Palette(
            Color BG, Color PANEL, Color PANEL_ALT, Color BORDER, Color BORDER_STRONG,
            Color ACCENT, Color ACCENT_HOVER, Color ACCENT_PRESSED, Color ACCENT_SOFT,
            Color INK, Color INK_SECONDARY, Color INK_MUTED, Color ON_ACCENT,
            Color INPUT_BG, Color INPUT_BORDER, Color FOCUS,
            Color TABLE_HEADER_BG, Color TABLE_ROW_ALT, Color TABLE_SELECTION_BG, Color TABLE_SELECTION_FG,
            Color INFO, Color INFO_BG, Color INFO_BORDER, Color INFO_TEXT,
            Color SUCCESS, Color SUCCESS_BG, Color SUCCESS_BORDER, Color SUCCESS_TEXT,
            Color WARNING, Color WARNING_BG, Color WARNING_BORDER, Color WARNING_TEXT,
            Color DANGER, Color DANGER_BG, Color DANGER_BORDER, Color DANGER_TEXT) {
    }

    private static final Palette LIGHT = new Palette(
            // Surfaces: Clean White / Cool Gray
            hex("#F5F7FB"), hex("#FFFFFF"), hex("#F8FAFC"),
            hex("#E2E8F0"), hex("#CBD5E1"),

            // Accent: Electric Blue
            hex("#2563EB"), hex("#1D4ED8"), hex("#1E40AF"), hex("#DBEAFE"),

            // Typography
            hex("#111827"), hex("#475569"), hex("#64748B"), hex("#FFFFFF"),

            // Inputs
            hex("#FFFFFF"), hex("#CBD5E1"), hex("#60A5FA"),

            // Tables
            hex("#F1F5F9"), hex("#F8FAFC"),
            hex("#DBEAFE"), hex("#1E3A8A"),

            // Info: Blue
            hex("#2563EB"), hex("#EFF6FF"), hex("#BFDBFE"), hex("#1D4ED8"),

            // Success: Emerald
            hex("#059669"), hex("#ECFDF5"), hex("#A7F3D0"), hex("#065F46"),

            // Warning: Amber
            hex("#B45309"), hex("#FFFBEB"), hex("#FCD34D"), hex("#92400E"),

            // Danger: Red
            hex("#DC2626"), hex("#FEF2F2"), hex("#FECACA"), hex("#B91C1C")
    );

    private static final Palette DARK = new Palette(
            // Surfaces: Modern Graphite
            hex("#0F1117"), hex("#181B23"), hex("#202531"),
            hex("#303746"), hex("#414B5D"),

            // Accent: Electric Blue
            hex("#60A5FA"), hex("#93C5FD"), hex("#3B82F6"), hex("#1E3353"),

            // Typography
            hex("#F1F5F9"), hex("#C2CAD6"), hex("#8B95A7"), hex("#0F172A"),

            // Inputs
            hex("#12151D"), hex("#414B5D"), hex("#60A5FA"),

            // Tables
            hex("#202531"), hex("#1B1F29"),
            hex("#263D5D"), hex("#DBEAFE"),

            // Info: Blue
            hex("#60A5FA"), hex("#17263D"), hex("#294C78"), hex("#BFDBFE"),

            // Success: Emerald
            hex("#34D399"), hex("#102C27"), hex("#236B54"), hex("#A7F3D0"),

            // Warning: Amber
            hex("#FBBF24"), hex("#332715"), hex("#765522"), hex("#FDE68A"),

            // Danger: Soft Red
            hex("#F87171"), hex("#351C22"), hex("#79313B"), hex("#FECACA")
    );

    private static volatile Palette current = LIGHT;
    private static volatile boolean darkMode = false;

    public static Color BG;
    public static Color PANEL;
    public static Color BORDER;
    public static Color ACCENT;
    public static Color ACCENT_STRONG;
    public static Color ACCENT_SOFT;
    public static Color INK;

    public static Color INFO;
    public static Color INFO_BG;
    public static Color INFO_BORDER;
    public static Color INFO_TEXT;

    public static Color SUCCESS;
    public static Color SUCCESS_BG;
    public static Color SUCCESS_BORDER;
    public static Color SUCCESS_TEXT;

    public static Color WARNING;
    public static Color WARNING_BG;
    public static Color WARNING_BORDER;
    public static Color WARNING_TEXT;

    public static Color DANGER;
    public static Color DANGER_BG;
    public static Color DANGER_BORDER;
    public static Color DANGER_TEXT;

    static {
        capNhatAlias(LIGHT);
    }

    private static Color hex(String value) {
        return Color.decode(value);
    }

    private static void capNhatAlias(Palette p) {
        BG = p.BG();
        PANEL = p.PANEL();
        BORDER = p.BORDER();
        ACCENT = p.ACCENT();
        ACCENT_STRONG = p.ACCENT_PRESSED();
        ACCENT_SOFT = p.ACCENT_SOFT();
        INK = p.INK();

        INFO = p.INFO();
        INFO_BG = p.INFO_BG();
        INFO_BORDER = p.INFO_BORDER();
        INFO_TEXT = p.INFO_TEXT();

        SUCCESS = p.SUCCESS();
        SUCCESS_BG = p.SUCCESS_BG();
        SUCCESS_BORDER = p.SUCCESS_BORDER();
        SUCCESS_TEXT = p.SUCCESS_TEXT();

        WARNING = p.WARNING();
        WARNING_BG = p.WARNING_BG();
        WARNING_BORDER = p.WARNING_BORDER();
        WARNING_TEXT = p.WARNING_TEXT();

        DANGER = p.DANGER();
        DANGER_BG = p.DANGER_BG();
        DANGER_BORDER = p.DANGER_BORDER();
        DANGER_TEXT = p.DANGER_TEXT();
    }

    public static Palette palette() {
        return current;
    }

    public static boolean isDarkMode() {
        return darkMode;
    }

    public static Color scrim() {
        if (darkMode) {
            return new Color(current.BG().getRed(), current.BG().getGreen(), current.BG().getBlue(), 170);
        }
        return new Color(15, 23, 42, 120);
    }

    public static Color chuTrenAnh() {
        return new Color(248, 250, 252);
    }

    public static JPanel contentPanel(int padding) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(current.PANEL());
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(current.BORDER()),
                BorderFactory.createEmptyBorder(padding, padding, padding, padding)));
        return panel;
    }

    public static void applyPrimary(JButton button) {
        button.setBackground(current.ACCENT());
        button.setForeground(current.ON_ACCENT());
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setFocusPainted(false);
    }

    public static void applyDanger(JButton button) {
        button.setBackground(current.DANGER());
        button.setForeground(current.ON_ACCENT());
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setFocusPainted(false);
    }

    public static void applyReadOnly(JTextField field) {
        field.setEditable(false);
        field.setBackground(current.ACCENT_SOFT());
        field.setForeground(current.INK());
    }

    public static void applyEditable(JTextField field) {
        field.setEditable(true);
        field.setBackground(current.INPUT_BG());
        field.setForeground(current.INK());
    }

    public static void styleTable(JTable table) {
        table.getTableHeader().setBackground(current.TABLE_HEADER_BG());
        table.getTableHeader().setForeground(current.INK());
        table.setSelectionBackground(current.TABLE_SELECTION_BG());
        table.setSelectionForeground(current.TABLE_SELECTION_FG());
        table.setGridColor(current.BORDER());
    }

    public static void capNhatNutMacDinh(Component root, Color mauNenCu) {
        // Nút còn giữ màu nền mặc định của theme cũ không được updateComponentTreeUI cập nhật.
        if (root instanceof JButton button && mauNenCu.equals(button.getBackground())) {
            button.setBackground(current.BG());
        }

        if (root instanceof Container container) {
            for (Component child : container.getComponents()) {
                capNhatNutMacDinh(child, mauNenCu);
            }
        }
    }

    private Theme() {
        throw new AssertionError("Utility class");
    }

    public static void install() {
        install(AppContext.getInstance().isDarkMode());
    }

    public static void install(boolean dark) {
        Palette p = dark ? DARK : LIGHT;
        darkMode = dark;
        current = p;
        capNhatAlias(p);

        UIManager.put("control", new ColorUIResource(p.BG()));
        UIManager.put("Panel.background", new ColorUIResource(p.PANEL()));
        UIManager.put("MenuBar.background", new ColorUIResource(p.BG()));
        UIManager.put("MenuBar.foreground", new ColorUIResource(p.INK()));
        UIManager.put("Menu.background", new ColorUIResource(p.BG()));
        UIManager.put("Menu.foreground", new ColorUIResource(p.INK()));
        UIManager.put("Menu.selectionBackground", new ColorUIResource(p.ACCENT()));
        UIManager.put("Menu.selectionForeground", new ColorUIResource(p.ON_ACCENT()));
        UIManager.put("MenuItem.background", new ColorUIResource(p.PANEL()));
        UIManager.put("MenuItem.foreground", new ColorUIResource(p.INK()));
        UIManager.put("MenuItem.selectionBackground", new ColorUIResource(p.ACCENT()));
        UIManager.put("MenuItem.selectionForeground", new ColorUIResource(p.ON_ACCENT()));
        UIManager.put("PopupMenu.background", new ColorUIResource(p.PANEL()));
        UIManager.put("PopupMenu.foreground", new ColorUIResource(p.INK()));
        UIManager.put("PopupMenu.border", new BorderUIResource.LineBorderUIResource(p.BORDER()));
        UIManager.put("Label.foreground", new ColorUIResource(p.INK()));
        // MetalButtonUI chỉ vẽ nền nút khi background không phải ColorUIResource.
        UIManager.put("Button.background", p.BG());
        UIManager.put("Button.foreground", new ColorUIResource(p.INK()));
        UIManager.put("TextField.background", new ColorUIResource(p.INPUT_BG()));
        UIManager.put("TextField.foreground", new ColorUIResource(p.INK()));
        UIManager.put("TextArea.background", new ColorUIResource(p.INPUT_BG()));
        UIManager.put("TextArea.foreground", new ColorUIResource(p.INK()));
        UIManager.put("ComboBox.background", new ColorUIResource(p.INPUT_BG()));
        UIManager.put("ComboBox.foreground", new ColorUIResource(p.INK()));
        UIManager.put("ComboBox.selectionBackground", new ColorUIResource(p.ACCENT()));
        UIManager.put("ComboBox.selectionForeground", new ColorUIResource(p.ON_ACCENT()));
        UIManager.put("List.background", new ColorUIResource(p.PANEL()));
        UIManager.put("List.foreground", new ColorUIResource(p.INK()));
        UIManager.put("List.selectionBackground", new ColorUIResource(p.ACCENT_SOFT()));
        UIManager.put("List.selectionForeground", new ColorUIResource(p.INK()));
        UIManager.put("ScrollPane.background", new ColorUIResource(p.PANEL()));
        UIManager.put("Viewport.background", new ColorUIResource(p.PANEL()));
        UIManager.put("SplitPane.background", new ColorUIResource(p.PANEL()));
        UIManager.put("SplitPane.dividerColor", new ColorUIResource(p.BORDER()));
        UIManager.put("Table.background", new ColorUIResource(p.PANEL()));
        UIManager.put("Table.foreground", new ColorUIResource(p.INK()));
        UIManager.put("Table.gridColor", new ColorUIResource(p.BORDER()));
        UIManager.put("Table.selectionBackground", new ColorUIResource(p.TABLE_SELECTION_BG()));
        UIManager.put("Table.selectionForeground", new ColorUIResource(p.TABLE_SELECTION_FG()));
        UIManager.put("TableHeader.background", new ColorUIResource(p.TABLE_HEADER_BG()));
        UIManager.put("TableHeader.foreground", new ColorUIResource(p.INK()));
        UIManager.put("OptionPane.background", new ColorUIResource(p.PANEL()));
        UIManager.put("OptionPane.foreground", new ColorUIResource(p.INK()));
        UIManager.put("OptionPane.messageForeground", new ColorUIResource(p.INK()));
        UIManager.put("Dialog.background", new ColorUIResource(p.PANEL()));
        UIManager.put("CheckBox.foreground", new ColorUIResource(p.INK()));
        UIManager.put("RadioButton.foreground", new ColorUIResource(p.INK()));
        UIManager.put("ToolTip.background", new ColorUIResource(p.PANEL_ALT()));
        UIManager.put("ToolTip.foreground", new ColorUIResource(p.INK()));
        UIManager.put("Separator.background", new ColorUIResource(p.BORDER()));
    }

    public static void setDarkMode(boolean dark) {
        install(dark);
    }
}
