package iuh.fit.gui.component;

import iuh.fit.dto.BaseFormDialogConfig;
import iuh.fit.gui.theme.Theme;
import iuh.fit.util.I18nReaderUtil;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.UIManager;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.util.function.BooleanSupplier;

public class BaseFormDialog extends JDialog {

  private static final long serialVersionUID = 1L;

  private static final int CO_ICON = 16;
  private static final int CHIEU_RONG = 760;
  private static final int CHIEU_CAO_TOI_DA = 800;

  private final BooleanSupplier xuLyLuu;
  private boolean daLuu;

  public BaseFormDialog(BaseFormDialogConfig cauHinh) {
    super(cauHinh.owner(), I18nReaderUtil.get(cauHinh.titleKey()), ModalityType.APPLICATION_MODAL);

    this.xuLyLuu = cauHinh.xuLyLuu();

    this.setLayout(new BorderLayout());
    this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    this.getContentPane().setBackground(Theme.BG);

    JButton nutHuy = this.taoNut(cauHinh.chiDoc() ? "app.common.close" : "app.common.cancel",
            "ban", Theme.INK);
    Theme.applySecondary(nutHuy);

    JButton nutLuu = null;
    if (!cauHinh.chiDoc()) {
      nutLuu = this.taoNut("app.common.save", "save", Theme.palette().ON_ACCENT());
      Theme.applyPrimary(nutLuu);
    }

    JPanel thanhNut = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
    thanhNut.setOpaque(false);
    thanhNut.add(nutHuy);
    if (nutLuu != null) {
      thanhNut.add(nutLuu);
    }

    JPanel noiDungDialog = Theme.contentPanel(12);
    noiDungDialog.add(this.taoPhanDau(cauHinh.titleKey()), BorderLayout.NORTH);
    noiDungDialog.add(this.taoThanDialog(cauHinh.noiDungForm()), BorderLayout.CENTER);
    noiDungDialog.add(thanhNut, BorderLayout.SOUTH);
    this.add(noiDungDialog, BorderLayout.CENTER);

    nutHuy.addActionListener(e -> this.dispose());
    if (nutLuu != null) {
      nutLuu.addActionListener(e -> this.luu());
      this.getRootPane().setDefaultButton(nutLuu);
    }

    this.getRootPane().registerKeyboardAction(
            e -> this.dispose(),
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
            JComponent.WHEN_IN_FOCUSED_WINDOW);

    this.setSize(CHIEU_RONG, Math.min(cauHinh.chieuCao(), CHIEU_CAO_TOI_DA));
    this.setMinimumSize(new Dimension(CHIEU_RONG, 0));
    this.setResizable(true);
    this.setLocationRelativeTo(cauHinh.owner());
  }

  public boolean showAndGet() {
    this.setVisible(true);
    return this.daLuu;
  }

  private JLabel taoPhanDau(String titleKey) {
    JLabel tieuDe = new JLabel(I18nReaderUtil.get(titleKey));
    Font fontTieuDe = UIManager.getFont("TitledBorder.font");
    tieuDe.setFont(fontTieuDe == null ? Theme.fieldLabelFont() : fontTieuDe);
    tieuDe.setForeground(Theme.INK);
    tieuDe.setBorder(Theme.leDeu(4));
    return tieuDe;
  }

  private JScrollPane taoThanDialog(JComponent noiDungForm) {
    JScrollPane scroll = new JScrollPane(noiDungForm);
    scroll.setBorder(null);
    scroll.setOpaque(false);
    scroll.getViewport().setOpaque(false);
    return scroll;
  }

  private JButton taoNut(String labelKey, String iconName, Color mauIcon) {
    JButton nut = new JButton(I18nReaderUtil.get(labelKey));
    nut.setIcon(new LucideIcon(iconName, CO_ICON, mauIcon));
    nut.setIconTextGap(6);
    nut.setFocusPainted(false);
    return nut;
  }

  private void luu() {
    if (this.xuLyLuu == null || !this.xuLyLuu.getAsBoolean()) {
      return;
    }

    this.daLuu = true;
    this.dispose();
  }
}
