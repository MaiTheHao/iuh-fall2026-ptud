package iuh.fit.gui.theme;

import com.formdev.flatlaf.FlatLightLaf;

import iuh.fit.util.I18nReaderUtil;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.BorderUIResource;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;

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
            hex("#F8FAFC"), hex("#FFFFFF"), hex("#F8FAFC"),
            hex("#E2E8F0"), hex("#CBD5E1"),

            // Accent: Electric Blue
            hex("#2563EB"), hex("#1D4ED8"), hex("#1E40AF"), hex("#EFF6FF"),

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

    // Token hay dùng ngoài Theme, giữ dạng field ngắn để màn hình không phải gọi palette().
    public static Color BG = LIGHT.BG();
    public static Color PANEL = LIGHT.PANEL();
    public static Color BORDER = LIGHT.BORDER();
    public static Color ACCENT = LIGHT.ACCENT();
    public static Color INK = LIGHT.INK();
    public static Color WARNING_TEXT = LIGHT.WARNING_TEXT();
    public static Color DANGER_TEXT = LIGHT.DANGER_TEXT();

    private static Color hex(String value) {
        return Color.decode(value);
    }

    public static Palette palette() {
        return LIGHT;
    }

    public static Color scrim() {
        return new Color(15, 23, 42, 120);
    }

    public static Color phuSangAnh() {
        return new Color(255, 255, 255, 200);
    }

    public static Color chuTrenAnh() {
        return new Color(248, 250, 252);
    }

    public static JPanel contentPanel(int padding) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(LIGHT.PANEL());
        panel.setBorder(BorderFactory.createCompoundBorder(
                vienNhat(),
                BorderFactory.createEmptyBorder(padding, padding, padding, padding)));
        return panel;
    }

    public static javax.swing.border.Border vienNhat() {
        return BorderFactory.createLineBorder(LIGHT.BORDER());
    }

    public static javax.swing.border.Border leDeu(int padding) {
        return BorderFactory.createEmptyBorder(padding, padding, padding, padding);
    }

    public static javax.swing.border.Border vienTren() {
        return BorderFactory.createMatteBorder(1, 0, 0, 0, LIGHT.BORDER());
    }

    public static javax.swing.border.Border vienDuoi() {
        return BorderFactory.createMatteBorder(0, 0, 1, 0, LIGHT.BORDER());
    }

    public static void applyCardStyle(JPanel panel) {
        panel.setOpaque(true);
        panel.setBackground(LIGHT.PANEL());
        panel.setBorder(BorderFactory.createCompoundBorder(
                vienNhat(),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)));
    }

    public static void applyPillButton(JButton button, Color bg, Color fg) {
        styleNut(button, bg, fg);
        button.setMargin(new Insets(4, 12, 4, 12));
        button.setBorderPainted(false);
    }

    public static void applyPrimary(JButton nut) {
        styleNut(nut, LIGHT.ACCENT(), LIGHT.ON_ACCENT());
    }

    public static void applySecondary(JButton nut) {
        styleNut(nut, LIGHT.PANEL_ALT(), LIGHT.INK());
    }

    public static void applyDanger(JButton nut) {
        styleNut(nut, LIGHT.DANGER(), LIGHT.ON_ACCENT());
    }

    private static void styleNut(JButton nut, Color nen, Color chu) {
        nut.setBackground(nen);
        nut.setForeground(chu);
        nut.setOpaque(true);
        nut.setContentAreaFilled(true);
        nut.setFocusPainted(false);
    }

    public static TitledBorder titledBorder(String titleKey) {
        TitledBorder tieuDe = BorderFactory.createTitledBorder(
                new LineBorder(LIGHT.BORDER(), 1), I18nReaderUtil.get(titleKey));
        tieuDe.setTitleFont(fieldLabelFont()
                .deriveFont((float) UIManager.getFont("TitledBorder.font").getSize()));
        tieuDe.setTitleColor(LIGHT.INK());
        return tieuDe;
    }

    public static Font fieldLabelFont() {
        return UIManager.getFont("Label.font").deriveFont(Font.BOLD);
    }

    public static Insets fieldRowInsets() {
        return new Insets(8, 10, 8, 10);
    }

    public static void applyReadOnly(JTextField field) {
        field.setEditable(false);
        field.setBackground(LIGHT.ACCENT_SOFT());
        field.setForeground(LIGHT.INK());
    }

    public static void applyEditable(JTextField field) {
        field.setEditable(true);
        field.setBackground(LIGHT.INPUT_BG());
        field.setForeground(LIGHT.INK());
    }

    public static void styleTable(JTable table) {
        table.getTableHeader().setBackground(LIGHT.TABLE_HEADER_BG());
        table.getTableHeader().setForeground(LIGHT.INK());
        table.setSelectionBackground(LIGHT.TABLE_SELECTION_BG());
        table.setSelectionForeground(LIGHT.TABLE_SELECTION_FG());
        table.setGridColor(LIGHT.BORDER());
    }

    private Theme() {
        throw new AssertionError("Utility class");
    }

    public static void install() {
        FlatLightLaf.setup();
        caiDatFlatLaf();

        Palette p = LIGHT;

        datMau("control", p.BG());
        datMau("Panel.background", p.PANEL());
        datMau("MenuBar.background", p.BG());
        datMau("MenuBar.foreground", p.INK());
        datMau("Menu.background", p.BG());
        datMau("Menu.foreground", p.INK());
        datMau("Menu.selectionBackground", p.ACCENT());
        datMau("Menu.selectionForeground", p.ON_ACCENT());
        datMau("MenuItem.background", p.PANEL());
        datMau("MenuItem.foreground", p.INK());
        datMau("MenuItem.selectionBackground", p.ACCENT());
        datMau("MenuItem.selectionForeground", p.ON_ACCENT());
        datMau("PopupMenu.background", p.PANEL());
        datMau("PopupMenu.foreground", p.INK());
        UIManager.put("PopupMenu.border", new BorderUIResource.LineBorderUIResource(p.BORDER()));
        datMau("Label.foreground", p.INK());
        // MetalButtonUI chỉ vẽ nền nút khi background không phải ColorUIResource.
        UIManager.put("Button.background", p.BG());
        datMau("Button.foreground", p.INK());
        datMau("TextField.background", p.INPUT_BG());
        datMau("TextField.foreground", p.INK());
        datMau("TextArea.background", p.INPUT_BG());
        datMau("TextArea.foreground", p.INK());
        datMau("ComboBox.background", p.INPUT_BG());
        datMau("ComboBox.foreground", p.INK());
        datMau("ComboBox.selectionBackground", p.ACCENT());
        datMau("ComboBox.selectionForeground", p.ON_ACCENT());
        datMau("List.background", p.PANEL());
        datMau("List.foreground", p.INK());
        datMau("List.selectionBackground", p.ACCENT_SOFT());
        datMau("List.selectionForeground", p.INK());
        datMau("ScrollPane.background", p.PANEL());
        datMau("Viewport.background", p.PANEL());
        datMau("SplitPane.background", p.PANEL());
        datMau("SplitPane.dividerColor", p.BORDER());
        datMau("Table.background", p.PANEL());
        datMau("Table.foreground", p.INK());
        datMau("Table.gridColor", p.BORDER());
        datMau("Table.selectionBackground", p.TABLE_SELECTION_BG());
        datMau("Table.selectionForeground", p.TABLE_SELECTION_FG());
        datMau("TableHeader.background", p.TABLE_HEADER_BG());
        datMau("TableHeader.foreground", p.INK());
        datMau("OptionPane.background", p.PANEL());
        datMau("OptionPane.foreground", p.INK());
        datMau("OptionPane.messageForeground", p.INK());
        datMau("Dialog.background", p.PANEL());
        datMau("CheckBox.foreground", p.INK());
        datMau("RadioButton.foreground", p.INK());
        datMau("ToolTip.background", p.PANEL_ALT());
        datMau("ToolTip.foreground", p.INK());
        datMau("Separator.background", p.BORDER());

        FontUIResource chu16 = fontChu(Font.PLAIN, 16);
        UIManager.put("Label.font", chu16);
        UIManager.put("Button.font", chu16);
        UIManager.put("TextField.font", chu16);
        UIManager.put("PasswordField.font", chu16);
        UIManager.put("TextArea.font", chu16);
        UIManager.put("ComboBox.font", chu16);
        UIManager.put("CheckBox.font", chu16);
        UIManager.put("RadioButton.font", chu16);
        UIManager.put("OptionPane.font", chu16);
        UIManager.put("OptionPane.messageFont", chu16);
        UIManager.put("Spinner.font", chu16);

        UIManager.put("Table.font", fontChu(Font.PLAIN, 15));
        UIManager.put("TableHeader.font", fontChu(Font.BOLD, 15));
        UIManager.put("Menu.font", fontChu(Font.PLAIN, 15));
        UIManager.put("MenuItem.font", fontChu(Font.PLAIN, 15));
        UIManager.put("TitledBorder.font", fontChu(Font.BOLD, 20));
        UIManager.put("ToolTip.font", fontChu(Font.PLAIN, 14));
    }

    private static void datMau(String khoa, Color mau) {
        UIManager.put(khoa, new ColorUIResource(mau));
    }

    private static void caiDatFlatLaf() {
        Palette p = LIGHT;

        UIManager.put("Button.arc", 8);
        UIManager.put("Component.arc", 8);
        UIManager.put("TextComponent.arc", 8);
        UIManager.put("ScrollBar.thumbArc", 999);
        UIManager.put("ScrollBar.width", 12);
        UIManager.put("Table.rowHeight", 34);
        UIManager.put("Table.showHorizontalLines", true);
        UIManager.put("Table.showVerticalLines", false);
        UIManager.put("Table.intercellSpacing", new Dimension(0, 1));
        UIManager.put("Component.focusColor", p.FOCUS());
        UIManager.put("Component.borderColor", p.INPUT_BORDER());
        UIManager.put("TextComponent.placeholderForeground", p.INK_MUTED());
        UIManager.put("Component.focusWidth", 1);
        UIManager.put("Component.innerFocusWidth", 1);
    }

    private static FontUIResource fontChu(int kieu, int co) {
        return new FontUIResource("SansSerif", kieu, co);
    }
}
