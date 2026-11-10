package iuh.fit.gui.form;

import iuh.fit.dto.PhieuTiepNhanView;
import iuh.fit.entity.PhieuTiepNhan;
import iuh.fit.enumeration.TrangThaiPhieuTiepNhan;
import iuh.fit.gui.Theme;
import iuh.fit.gui.component.BoHienThiI18n;
import iuh.fit.gui.component.FormFieldPanel;

import javax.swing.JComboBox;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.Dimension;
import java.util.List;

/**
 * Form sửa nội dung và trạng thái của phiếu tiếp nhận.
 */
public class PhieuTiepNhanFormPanel extends FormFieldPanel {

    

    private final JTextField txtMaPhieu = new JTextField();
    private final JTextField txtBienSo = new JTextField();
    private final JTextField txtKhachHang = new JTextField();
    private final JTextField txtSdt = new JTextField();
    private final JTextArea txaMoTa = new JTextArea(4, 20);
    private final JComboBox<TrangThaiPhieuTiepNhan> cmbTrangThai = new JComboBox<>();

    public PhieuTiepNhanFormPanel(List<TrangThaiPhieuTiepNhan> trangThaiChoPhep) {
        // Thông tin phiếu và khách hàng chỉ đọc; nội dung và trạng thái được sửa.
        Theme.applyReadOnly(this.txtMaPhieu);
        Theme.applyReadOnly(this.txtBienSo);
        Theme.applyReadOnly(this.txtKhachHang);
        Theme.applyReadOnly(this.txtSdt);

        this.addField("app.phieuTiepNhan.maPhieu", this.txtMaPhieu);
        this.addField("app.phieuTiepNhan.bienSo", this.txtBienSo);
        this.addField("app.phieuTiepNhan.khachHang", this.txtKhachHang);
        this.addField("app.phieuTiepNhan.dienThoai", this.txtSdt);

        this.txaMoTa.setLineWrap(true);
        this.txaMoTa.setWrapStyleWord(true);
        JScrollPane scrollMoTa = new JScrollPane(this.txaMoTa);
        scrollMoTa.setPreferredSize(new Dimension(200, 100));
        this.addField("app.phieuTiepNhan.noiDungYeuCau", scrollMoTa);

        for (TrangThaiPhieuTiepNhan trangThai : trangThaiChoPhep) {
            this.cmbTrangThai.addItem(trangThai);
        }
        this.cmbTrangThai.setRenderer(BoHienThiI18n.tao());
        this.addField("app.phieuTiepNhan.trangThai", this.cmbTrangThai);
    }

    public void napDuLieu(PhieuTiepNhanView view) {
        this.txtMaPhieu.setText(view.maPhieuTiepNhan() == null ? "" : view.maPhieuTiepNhan());
        this.txtBienSo.setText(view.bienSo() == null ? "" : view.bienSo());
        this.txtKhachHang.setText(view.tenKhachHang() == null ? "" : view.tenKhachHang());
        this.txtSdt.setText(view.sdt() == null ? "" : view.sdt());
        this.txaMoTa.setText(view.moTa() == null ? "" : view.moTa());
        this.cmbTrangThai.setSelectedItem(view.trangThai());
    }

    public PhieuTiepNhan layDuLieu() {
        PhieuTiepNhan phieu = new PhieuTiepNhan();
        phieu.setMaPhieuTiepNhan(this.txtMaPhieu.getText().trim());
        phieu.setMoTa(this.txaMoTa.getText().trim());
        phieu.setTrangThai((TrangThaiPhieuTiepNhan) this.cmbTrangThai.getSelectedItem());
        return phieu;
    }

    public void lamMoi() {
        this.txtMaPhieu.setText("");
        this.txtBienSo.setText("");
        this.txtKhachHang.setText("");
        this.txtSdt.setText("");
        this.txaMoTa.setText("");
        this.cmbTrangThai.setSelectedIndex(0);
        this.focus();
    }

    public void focus() {
        this.txaMoTa.requestFocusInWindow();
    }
}
