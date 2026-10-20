package iuh.fit.gui.component;

import iuh.fit.gui.theme.Theme;
import iuh.fit.util.I18nReaderUtil;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.util.function.BooleanSupplier;

/**
 * Dialog nhập liệu dùng chung cho thao tác sửa một bản ghi.
 */
public final class SuaDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    private BooleanSupplier xuLyLuu;
    private boolean daLuu;

    public SuaDialog(Window owner, String titleKey, FormFieldPanel noiDung) {
        super(owner, I18nReaderUtil.get(titleKey), ModalityType.APPLICATION_MODAL);

        this.setLayout(new BorderLayout(8, 8));
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.getContentPane().setBackground(Theme.BG);

        JScrollPane scroll = new JScrollPane(noiDung);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);

        JButton nutLuu = new JButton(I18nReaderUtil.get("app.common.save"));
        JButton nutHuy = new JButton(I18nReaderUtil.get("app.common.cancel"));
        Theme.applyPrimary(nutLuu);

        JPanel thanhNut = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        thanhNut.setOpaque(false);
        thanhNut.add(nutHuy);
        thanhNut.add(nutLuu);

        JPanel noiDungDialog = new JPanel(new BorderLayout(0, 8));
        noiDungDialog.setOpaque(false);
        noiDungDialog.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        noiDungDialog.add(scroll, BorderLayout.CENTER);
        noiDungDialog.add(thanhNut, BorderLayout.SOUTH);
        this.add(noiDungDialog, BorderLayout.CENTER);

        nutHuy.addActionListener(e -> this.dispose());
        nutLuu.addActionListener(e -> this.luu());
        this.getRootPane().setDefaultButton(nutLuu);

        this.getRootPane().registerKeyboardAction(
                e -> this.dispose(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);

        this.setSize(760, 820);
        this.setMinimumSize(new Dimension(480, 280));
        this.setResizable(true);
        this.setLocationRelativeTo(owner);
    }

    public void datXuLyLuu(BooleanSupplier xuLyLuu) {
        this.xuLyLuu = xuLyLuu;
    }

    public boolean hienThiVaChoKetQua() {
        this.setVisible(true);
        return this.daLuu;
    }

    private void luu() {
        if (this.xuLyLuu == null || !this.xuLyLuu.getAsBoolean()) {
            return;
        }

        this.daLuu = true;
        this.dispose();
    }
}
