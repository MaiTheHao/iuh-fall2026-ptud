package iuh.fit.gui.component;

import iuh.fit.gui.theme.Theme;
import iuh.fit.util.I18nReaderUtil;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;

/**
 * Khung form nhập liệu dùng chung cho các màn hình quản lý.
 */
public class ManagementFormPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private static final int DO_RONG = 480;

    private final FormFieldPanel noiDungForm = new FormFieldPanel();
    private final JPanel thanhNut = new JPanel(new GridLayout(2, 2, 10, 10));
    private final JButton nutThem;
    private final JButton nutLuu;
    private final JButton nutXoa;
    private final JButton nutXoaTrang;

    public ManagementFormPanel(String titleKey) {
        this.setLayout(new BorderLayout(0, 10));
        this.setOpaque(true);
        this.setBackground(Theme.palette().PANEL());
        this.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.palette().BORDER()),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)));
        this.setPreferredSize(new Dimension(DO_RONG, 400));

        this.noiDungForm.setOpaque(false);
        TitledBorder tieuDe = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Theme.palette().BORDER()), I18nReaderUtil.get(titleKey));
        tieuDe.setTitleFont(tieuDe.getTitleFont().deriveFont(Font.BOLD, 15f));
        tieuDe.setTitleColor(Theme.palette().INK());
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

        this.nutXoaTrang.setOpaque(true);
        this.nutXoaTrang.setContentAreaFilled(true);
        this.nutXoaTrang.setBackground(Theme.palette().PANEL());
        this.nutXoaTrang.setForeground(Theme.palette().INK());
        this.nutXoaTrang.setBorder(BorderFactory.createLineBorder(Theme.palette().BORDER()));
        this.nutXoaTrang.setFocusPainted(false);

        this.thanhNut.setOpaque(false);
        this.thanhNut.setPreferredSize(new Dimension(DO_RONG, chieuCaoThanhNut(2)));
        this.thanhNut.add(this.nutThem);
        this.thanhNut.add(this.nutLuu);
        this.thanhNut.add(this.nutXoa);
        this.thanhNut.add(this.nutXoaTrang);
        this.add(this.thanhNut, BorderLayout.SOUTH);
    }

    public void addField(String labelKey, JComponent field) {
        this.noiDungForm.addField(labelKey, field);
    }

    public void addBlock(JComponent component) {
        this.noiDungForm.addBlock(component);
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

        int soHang = Math.max(1, (buttons.length + 1) / 2);
        this.thanhNut.setLayout(new GridLayout(soHang, 2, 10, 10));
        this.thanhNut.setPreferredSize(
                new Dimension(DO_RONG, chieuCaoThanhNut(soHang)));

        for (JButton button : buttons) {
            this.thanhNut.add(button);
        }

        this.thanhNut.setVisible(buttons.length > 0);

        this.thanhNut.revalidate();
        this.thanhNut.repaint();
    }

    private static int chieuCaoThanhNut(int soHang) {
        return soHang == 1 ? 40 : 84;
    }
}
