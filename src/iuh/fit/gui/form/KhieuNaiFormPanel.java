package iuh.fit.gui.form;

import iuh.fit.dto.KhieuNaiUpdateFormData;
import iuh.fit.dto.KhieuNaiView;
import iuh.fit.enumeration.TrangThaiKhieuNai;
import iuh.fit.gui.component.FormFieldPanel;
import iuh.fit.gui.Theme;
import iuh.fit.gui.component.BoHienThiI18n;

import javax.swing.JComboBox;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.Dimension;

/**
 * Form nhập liệu khiếu nại dùng chung cho màn quản lý và dialog sửa.
 */
public class KhieuNaiFormPanel extends FormFieldPanel {

    

    private final JTextField txtMa = new JTextField();
    private final JTextField txtKhachHang = new JTextField();
    private final JTextField txtPhieuTiepNhan = new JTextField();
    private final JTextArea txaNoiDung = new JTextArea(4, 20);
    private final JComboBox<TrangThaiKhieuNai> cmbTrangThai = new JComboBox<>(TrangThaiKhieuNai.values());

    public KhieuNaiFormPanel() {
        Theme.applyReadOnly(this.txtMa);
        Theme.applyReadOnly(this.txtKhachHang);
        Theme.applyReadOnly(this.txtPhieuTiepNhan);

        this.addField("app.khieuNai.ma", this.txtMa);
        this.addField("app.khieuNai.khachHang", this.txtKhachHang);
        this.addField("app.khieuNai.phieuTiepNhan", this.txtPhieuTiepNhan);

        this.txaNoiDung.setLineWrap(true);
        this.txaNoiDung.setWrapStyleWord(true);
        JScrollPane scrollNoiDung = new JScrollPane(this.txaNoiDung);
        scrollNoiDung.setPreferredSize(new Dimension(200, 100));
        this.addField("app.khieuNai.noiDung", scrollNoiDung);

        this.cmbTrangThai.setRenderer(BoHienThiI18n.tao());
        this.addField("app.khieuNai.trangThai", this.cmbTrangThai);
    }

    public void napDuLieu(KhieuNaiView view) {
        this.txtMa.setText(view.maKhieuNai() == null ? "" : view.maKhieuNai());
        this.txtKhachHang.setText(view.tenKhachHang() == null ? "" : view.tenKhachHang());
        this.txtPhieuTiepNhan.setText(view.maPhieuTiepNhan() == null ? "" : view.maPhieuTiepNhan());
        this.txaNoiDung.setText(view.noiDung() == null ? "" : view.noiDung());
        this.cmbTrangThai.setSelectedItem(view.trangThai());
    }

    public KhieuNaiUpdateFormData layDuLieu() {
        return new KhieuNaiUpdateFormData(
                this.txtMa.getText().trim(),
                this.txaNoiDung.getText(),
                (TrangThaiKhieuNai) this.cmbTrangThai.getSelectedItem());
    }

    public void lamMoi() {
        this.txtMa.setText("");
        this.txtKhachHang.setText("");
        this.txtPhieuTiepNhan.setText("");
        this.txaNoiDung.setText("");
        this.cmbTrangThai.setSelectedIndex(0);
        this.focus();
    }

    public void focus() {
        this.txaNoiDung.requestFocusInWindow();
    }
}
