package iuh.fit.gui.component;

import iuh.fit.gui.Theme;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
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

    this.btnThem = Theme.nut("app.common.add", "plus", CO_ICON, Theme.palette().ON_ACCENT());
    this.btnLuu = Theme.nut("app.common.save", "save", CO_ICON, Theme.palette().ON_ACCENT());
    this.btnXoa = Theme.nut("app.common.delete", "trash-2", CO_ICON, Theme.palette().ON_ACCENT());
    this.btnXoaTrang = Theme.nut("app.common.clear", "rotate-ccw", CO_ICON, Theme.INK);

    for (JButton nut : new JButton[] { this.btnThem, this.btnLuu, this.btnXoa, this.btnXoaTrang }) {
      nut.setMinimumSize(KICH_THUOC_NUT);
    }

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
