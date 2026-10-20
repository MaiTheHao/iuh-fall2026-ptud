package iuh.fit.gui.form;

import iuh.fit.dto.KhieuNaiUpdateFormData;
import iuh.fit.dto.KhieuNaiView;
import iuh.fit.enumeration.TrangThaiKhieuNai;
import iuh.fit.gui.component.FormFieldPanel;
import iuh.fit.gui.component.BoHienThiI18n;
import iuh.fit.gui.theme.Theme;

import javax.swing.JComboBox;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.Dimension;

/**
 * Form nhập liệu khiếu nại dùng chung cho màn quản lý và dialog sửa.
 */
public class KhieuNaiFormPanel extends FormFieldPanel {

    private static final long serialVersionUID = 1L;

    private final JTextField maField = new JTextField();
    private final JTextField khachHangField = new JTextField();
    private final JTextField phieuTiepNhanField = new JTextField();
    private final JTextArea noiDungArea = new JTextArea(4, 20);
    private final JComboBox<TrangThaiKhieuNai> trangThaiCombo = new JComboBox<>(TrangThaiKhieuNai.values());

    public KhieuNaiFormPanel() {
        Theme.applyReadOnly(this.maField);
        Theme.applyReadOnly(this.khachHangField);
        Theme.applyReadOnly(this.phieuTiepNhanField);

        this.addField("app.khieuNai.ma", this.maField);
        this.addField("app.khieuNai.khachHang", this.khachHangField);
        this.addField("app.khieuNai.phieuTiepNhan", this.phieuTiepNhanField);

        this.noiDungArea.setLineWrap(true);
        this.noiDungArea.setWrapStyleWord(true);
        JScrollPane scrollNoiDung = new JScrollPane(this.noiDungArea);
        scrollNoiDung.setPreferredSize(new Dimension(200, 100));
        this.addField("app.khieuNai.noiDung", scrollNoiDung);

        this.trangThaiCombo.setRenderer(BoHienThiI18n.tao());
        this.addField("app.khieuNai.trangThai", this.trangThaiCombo);
    }

    public void napDuLieu(KhieuNaiView view) {
        this.maField.setText(view.maKhieuNai() == null ? "" : view.maKhieuNai());
        this.khachHangField.setText(view.tenKhachHang() == null ? "" : view.tenKhachHang());
        this.phieuTiepNhanField.setText(view.maPhieuTiepNhan() == null ? "" : view.maPhieuTiepNhan());
        this.noiDungArea.setText(view.noiDung() == null ? "" : view.noiDung());
        this.trangThaiCombo.setSelectedItem(view.trangThai());
    }

    public KhieuNaiUpdateFormData layDuLieu() {
        return new KhieuNaiUpdateFormData(
                this.maField.getText().trim(),
                this.noiDungArea.getText(),
                (TrangThaiKhieuNai) this.trangThaiCombo.getSelectedItem());
    }

    public void lamMoi() {
        this.maField.setText("");
        this.khachHangField.setText("");
        this.phieuTiepNhanField.setText("");
        this.noiDungArea.setText("");
        this.trangThaiCombo.setSelectedIndex(0);
        this.focus();
    }

    public void focus() {
        this.noiDungArea.requestFocusInWindow();
    }
}
