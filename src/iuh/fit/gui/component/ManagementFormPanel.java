package iuh.fit.gui.component;

import iuh.fit.gui.Theme;
import iuh.fit.util.I18nReaderUtil;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;

public class ManagementFormPanel extends JPanel {

    

    private static final int DO_RONG = 480;
    private static final int CO_ICON = 18;
    private static final Dimension KICH_THUOC_NUT = new Dimension(100, 36);

    private final FormFieldPanel noiDungForm = new FormFieldPanel();
    private final JPanel pnlThanhNut = new JPanel(new GridLayout(2, 2, 10, 10));
    private final JButton btnThem;
    private final JButton btnLuu;
    private final JButton btnXoa;
    private final JButton btnXoaTrang;

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

        this.btnThem = taoNut("app.common.add", "plus", Theme.palette().ON_ACCENT());
        this.btnLuu = taoNut("app.common.save", "save", Theme.palette().ON_ACCENT());
        this.btnXoa = taoNut("app.common.delete", "trash-2", Theme.palette().ON_ACCENT());
        this.btnXoaTrang = taoNut("app.common.clear", "rotate-ccw", Theme.INK);

        Theme.applyPrimary(this.btnThem);
        Theme.applyPrimary(this.btnLuu);
        Theme.applyDanger(this.btnXoa);
        Theme.applySecondary(this.btnXoaTrang);

        this.pnlThanhNut.setOpaque(false);
        this.pnlThanhNut.setPreferredSize(new Dimension(DO_RONG, chieuCaoThanhNut(2)));
        this.pnlThanhNut.add(this.btnThem);
        this.pnlThanhNut.add(this.btnLuu);
        this.pnlThanhNut.add(this.btnXoa);
        this.pnlThanhNut.add(this.btnXoaTrang);
        this.add(this.pnlThanhNut, BorderLayout.SOUTH);
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
        return this.btnThem;
    }

    public JButton getNutLuu() {
        return this.btnLuu;
    }

    public JButton getNutXoa() {
        return this.btnXoa;
    }

    public JButton getNutXoaTrang() {
        return this.btnXoaTrang;
    }

    public void thayNut(JButton... buttons) {
        this.pnlThanhNut.removeAll();

        int soHang = Math.max(1, (buttons.length + 1) / 2);
        this.pnlThanhNut.setLayout(new GridLayout(soHang, 2, 10, 10));
        this.pnlThanhNut.setPreferredSize(
                new Dimension(DO_RONG, chieuCaoThanhNut(soHang)));

        for (JButton button : buttons) {
            this.pnlThanhNut.add(button);
        }

        this.pnlThanhNut.setVisible(buttons.length > 0);

        this.pnlThanhNut.revalidate();
        this.pnlThanhNut.repaint();
    }

    private static int chieuCaoThanhNut(int soHang) {
        return soHang == 1 ? 40 : 84;
    }
}
