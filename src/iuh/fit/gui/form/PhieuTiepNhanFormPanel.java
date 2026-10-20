package iuh.fit.gui.form;

import iuh.fit.dto.PhieuTiepNhanView;
import iuh.fit.entity.PhieuTiepNhan;
import iuh.fit.enumeration.TrangThaiPhieuTiepNhan;
import iuh.fit.gui.component.FormFieldPanel;
import iuh.fit.gui.component.BoHienThiI18n;
import iuh.fit.gui.theme.Theme;

import javax.swing.JComboBox;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.Dimension;

/**
 * Form nhập liệu phiếu tiếp nhận dùng chung cho màn quản lý và dialog sửa.
 */
public class PhieuTiepNhanFormPanel extends FormFieldPanel {

    private static final long serialVersionUID = 1L;

    private final JTextField maPhieuField = new JTextField();
    private final JTextField bienSoField = new JTextField();
    private final JTextField khachHangField = new JTextField();
    private final JTextField sdtField = new JTextField();
    private final JTextArea moTaArea = new JTextArea(4, 20);
    private final JComboBox<TrangThaiPhieuTiepNhan> trangThaiCombo =
            new JComboBox<>(TrangThaiPhieuTiepNhan.values());

    public PhieuTiepNhanFormPanel() {
        Theme.applyReadOnly(this.maPhieuField);
        Theme.applyReadOnly(this.bienSoField);
        Theme.applyReadOnly(this.khachHangField);
        Theme.applyReadOnly(this.sdtField);

        this.addField("app.phieuTiepNhan.maPhieu", this.maPhieuField);
        this.addField("app.phieuTiepNhan.bienSo", this.bienSoField);
        this.addField("app.phieuTiepNhan.khachHang", this.khachHangField);
        this.addField("app.phieuTiepNhan.dienThoai", this.sdtField);

        this.moTaArea.setLineWrap(true);
        this.moTaArea.setWrapStyleWord(true);
        JScrollPane scrollMoTa = new JScrollPane(this.moTaArea);
        scrollMoTa.setPreferredSize(new Dimension(200, 100));
        this.addField("app.phieuTiepNhan.noiDungYeuCau", scrollMoTa);

        this.trangThaiCombo.setRenderer(BoHienThiI18n.tao());
        this.addField("app.phieuTiepNhan.trangThaiTienDo", this.trangThaiCombo);
    }

    public void napDuLieu(PhieuTiepNhanView view) {
        this.maPhieuField.setText(view.maPhieuTiepNhan() == null ? "" : view.maPhieuTiepNhan());
        this.bienSoField.setText(view.bienSo() == null ? "" : view.bienSo());
        this.khachHangField.setText(view.tenKhachHang() == null ? "" : view.tenKhachHang());
        this.sdtField.setText(view.sdt() == null ? "" : view.sdt());
        this.moTaArea.setText(view.moTa() == null ? "" : view.moTa());
        this.trangThaiCombo.setSelectedItem(view.trangThai());
    }

    public PhieuTiepNhan layDuLieu() {
        PhieuTiepNhan phieu = new PhieuTiepNhan();
        phieu.setMaPhieuTiepNhan(this.maPhieuField.getText().trim());
        phieu.setMoTa(this.moTaArea.getText().trim());
        phieu.setTrangThai((TrangThaiPhieuTiepNhan) this.trangThaiCombo.getSelectedItem());
        return phieu;
    }

    public void setTrangThai(TrangThaiPhieuTiepNhan trangThai) {
        this.trangThaiCombo.setSelectedItem(trangThai);
    }

    public void lamMoi() {
        this.maPhieuField.setText("");
        this.bienSoField.setText("");
        this.khachHangField.setText("");
        this.sdtField.setText("");
        this.moTaArea.setText("");
        this.trangThaiCombo.setSelectedIndex(0);
        this.focus();
    }

    public void focus() {
        this.moTaArea.requestFocusInWindow();
    }
}
