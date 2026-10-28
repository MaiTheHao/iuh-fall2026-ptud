package iuh.fit.gui.component;

import iuh.fit.gui.theme.Theme;
import iuh.fit.util.I18nReaderUtil;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

/**
 * Khung lưới dùng chung để bố trí các cặp nhãn và trường nhập liệu.
 */
public class FormFieldPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private static final int CHIEU_CAO_TRUONG = 36;

    private int dongTiepTheo;

    public FormFieldPanel() {
        super(new GridBagLayout());
        this.setOpaque(false);
    }

    public void addField(String labelKey, JComponent field) {
        GridBagConstraints gbc = taoRangBuoc();

        JLabel label = new JLabel(I18nReaderUtil.get(labelKey));
        label.setFont(Theme.fieldLabelFont());
        label.setForeground(Theme.INK);

        gbc.insets = Theme.fieldRowInsets();
        gbc.gridy = this.dongTiepTheo++;
        this.add(label, gbc);

        this.chuanHoaChieuCaoTruong(field);
        gbc.insets = new Insets(0, 12, 8, 12);
        gbc.gridy = this.dongTiepTheo++;
        this.add(field, gbc);
    }

    public void addBlock(JComponent component) {
        GridBagConstraints gbc = taoRangBuoc();
        gbc.gridy = this.dongTiepTheo++;
        this.add(component, gbc);
    }

    private static GridBagConstraints taoRangBuoc() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 12, 6, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridwidth = 2;
        return gbc;
    }

    private void chuanHoaChieuCaoTruong(JComponent field) {
        if (field instanceof JTextField
                || field instanceof JPasswordField
                || field instanceof JComboBox<?>) {
            Dimension kichThuoc = field.getPreferredSize();
            field.setPreferredSize(new Dimension(kichThuoc.width, CHIEU_CAO_TRUONG));
        }
    }

    /** Khung cuộn chuẩn cho vùng mô tả nhiều dòng. */
    protected static JScrollPane taoKhungMoTa(JTextArea area) {
        area.setLineWrap(true);
        area.setWrapStyleWord(true);

        JScrollPane scroll = new JScrollPane(area);
        scroll.setPreferredSize(new Dimension(200, 100));
        return scroll;
    }

    /** Nhãn gợi ý phụ dưới một trường nhập liệu. */
    protected static JLabel taoGoiY(String labelKey) {
        JLabel goiY = new JLabel(I18nReaderUtil.get(labelKey));
        goiY.setForeground(Theme.palette().INK_MUTED());
        return goiY;
    }
}
