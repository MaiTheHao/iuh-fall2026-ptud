package iuh.fit.gui.component;

import iuh.fit.gui.theme.Theme;
import iuh.fit.util.I18nReaderUtil;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

/**
 * Khung form nhập liệu dùng chung cho các màn hình quản lý.
 */
public class ManagementFormPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private static final int DO_RONG = 380;

    private final JPanel noiDungForm = new JPanel(new GridBagLayout());
    private final JPanel thanhNut = new JPanel(new GridLayout(2, 2, 6, 6));
    private int dongTiepTheo;
    private final JButton nutThem;
    private final JButton nutLuu;
    private final JButton nutXoa;
    private final JButton nutXoaTrang;

    public ManagementFormPanel(String titleKey) {
        this.setLayout(new BorderLayout(0, 10));
        this.setOpaque(false);
        this.setPreferredSize(new Dimension(DO_RONG, 0));

        this.noiDungForm.setOpaque(false);
        TitledBorder tieuDe = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Theme.BORDER), I18nReaderUtil.get(titleKey));
        tieuDe.setTitleFont(tieuDe.getTitleFont().deriveFont(Font.BOLD));
        tieuDe.setTitleColor(Theme.chuTrenAnh());
        this.noiDungForm.setBorder(tieuDe);

        JPanel boc = new JPanel(new BorderLayout());
        boc.setOpaque(false);
        boc.add(this.noiDungForm, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(boc);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        this.add(scroll, BorderLayout.CENTER);

        this.nutThem = new JButton(I18nReaderUtil.get("app.common.add"));
        this.nutLuu = new JButton(I18nReaderUtil.get("app.common.save"));
        this.nutXoa = new JButton(I18nReaderUtil.get("app.common.delete"));
        this.nutXoaTrang = new JButton(I18nReaderUtil.get("app.common.clear"));

        Theme.applyPrimary(this.nutThem);
        Theme.applyPrimary(this.nutLuu);
        Theme.applyDanger(this.nutXoa);

        this.thanhNut.setOpaque(false);
        this.thanhNut.add(this.nutThem);
        this.thanhNut.add(this.nutLuu);
        this.thanhNut.add(this.nutXoa);
        this.thanhNut.add(this.nutXoaTrang);
        this.add(this.thanhNut, BorderLayout.SOUTH);
    }

    public void addField(String labelKey, JComponent field) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 5, 4, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 0;
        gbc.gridwidth = 2;

        JLabel label = new JLabel(I18nReaderUtil.get(labelKey));
        label.setFont(label.getFont().deriveFont(Font.BOLD));
        label.setForeground(Theme.chuTrenAnh());

        gbc.gridy = this.dongTiepTheo++;
        this.noiDungForm.add(label, gbc);

        gbc.gridy = this.dongTiepTheo++;
        this.noiDungForm.add(field, gbc);
    }

    public void addBlock(JComponent component) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 5, 4, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridwidth = 2;
        gbc.gridy = this.dongTiepTheo++;
        this.noiDungForm.add(component, gbc);
    }

    public JButton getNutThem() {
        return this.nutThem;
    }

    public JButton getNutLuu() {
        return this.nutLuu;
    }

    public JButton getNutXoa() {
        return this.nutXoa;
    }

    public JButton getNutXoaTrang() {
        return this.nutXoaTrang;
    }

    public void thayNut(JButton... buttons) {
        this.thanhNut.removeAll();
        this.thanhNut.setLayout(new GridLayout(Math.max(1, (buttons.length + 1) / 2), 2, 6, 6));

        for (JButton button : buttons) {
            this.thanhNut.add(button);
        }

        this.thanhNut.setVisible(buttons.length > 0);

        this.thanhNut.revalidate();
        this.thanhNut.repaint();
    }
}
