package iuh.fit.gui.component;

import iuh.fit.gui.theme.Theme;
import iuh.fit.util.I18nReaderUtil;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;

/**
 * Khung form nhập liệu dùng chung cho các màn hình quản lý.
 */
public class ManagementFormPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private static final int DO_RONG = 480;
    private static final int CO_ICON = 18;
    private static final Dimension KICH_THUOC_NUT = new Dimension(100, 36);

    private final FormFieldPanel noiDungForm = new FormFieldPanel();
    private final JPanel thanhNut = new JPanel(new GridLayout(2, 2, 10, 10));
    private final JButton nutThem;
    private final JButton nutLuu;
    private final JButton nutXoa;
    private final JButton nutXoaTrang;

    public ManagementFormPanel(String titleKey) {
        this.setLayout(new BorderLayout(0, 10));
        Theme.applyCardStyle(this);
        this.setPreferredSize(new Dimension(DO_RONG, 400));

        this.noiDungForm.setOpaque(false);
        this.noiDungForm.setBorder(Theme.titledBorder(titleKey));
        JPanel boc = new JPanel(new BorderLayout());
        boc.setOpaque(false);
        boc.add(this.noiDungForm, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(boc);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        this.add(scroll, BorderLayout.CENTER);

        this.nutThem = taoNut("app.common.add", "plus", Theme.palette().ON_ACCENT());
        this.nutLuu = taoNut("app.common.save", "save", Theme.palette().ON_ACCENT());
        this.nutXoa = taoNut("app.common.delete", "trash-2", Theme.palette().ON_ACCENT());
        this.nutXoaTrang = taoNut("app.common.clear", "rotate-ccw", Theme.INK);

        Theme.applyPrimary(this.nutThem);
        Theme.applyPrimary(this.nutLuu);
        Theme.applyDanger(this.nutXoa);
        Theme.applySecondary(this.nutXoaTrang);

        this.thanhNut.setOpaque(false);
        this.thanhNut.setPreferredSize(new Dimension(DO_RONG, chieuCaoThanhNut(2)));
        this.thanhNut.add(this.nutThem);
        this.thanhNut.add(this.nutLuu);
        this.thanhNut.add(this.nutXoa);
        this.thanhNut.add(this.nutXoaTrang);
        this.add(this.thanhNut, BorderLayout.SOUTH);
    }

    private static JButton taoNut(String labelKey, String iconName, Color mauIcon) {
        JButton nut = new JButton(I18nReaderUtil.get(labelKey));
        nut.setIcon(new LucideIcon(iconName, CO_ICON, mauIcon));
        nut.setMinimumSize(KICH_THUOC_NUT);
        nut.setIconTextGap(6);
        nut.setFocusPainted(false);
        return nut;
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
