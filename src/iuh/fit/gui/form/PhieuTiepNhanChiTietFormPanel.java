package iuh.fit.gui.form;

import iuh.fit.dto.PhieuTiepNhanView;
import iuh.fit.gui.component.FormFieldPanel;
import iuh.fit.gui.theme.Theme;
import iuh.fit.util.DateFormats;

import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * Form chỉ đọc hiển thị toàn bộ chi tiết của một phiếu tiếp nhận.
 */
public final class PhieuTiepNhanChiTietFormPanel extends FormFieldPanel {

    

    private final JTextField txtMaPhieu = new JTextField(14);
    private final JTextField txtBienSo = new JTextField(14);
    private final JTextField txtKhachHang = new JTextField(14);
    private final JTextField txtSdt = new JTextField(14);
    private final JTextArea txaMoTa = new JTextArea(4, 20);
    private final JTextField txtTrangThai = new JTextField(14);
    private final JTextField txtNgayTao = new JTextField(14);

    public PhieuTiepNhanChiTietFormPanel() {
        for (JTextField field : new JTextField[]{
            this.txtMaPhieu, this.txtBienSo, this.txtKhachHang,
            this.txtSdt, this.txtTrangThai, this.txtNgayTao}) {
            Theme.applyReadOnly(field);
            field.setFocusable(false);
        }

        this.txaMoTa.setEditable(false);
        this.txaMoTa.setFocusable(false);
        this.txaMoTa.setBackground(Theme.palette().ACCENT_SOFT());
        this.txaMoTa.setForeground(Theme.INK);

        this.addField("app.phieuTiepNhan.maPhieu", this.txtMaPhieu);
        this.addField("app.phieuTiepNhan.bienSo", this.txtBienSo);
        this.addField("app.phieuTiepNhan.khachHang", this.txtKhachHang);
        this.addField("app.phieuTiepNhan.dienThoai", this.txtSdt);
        this.addField("app.phieuTiepNhan.noiDungYeuCau", taoKhungMoTa(this.txaMoTa));
        this.addField("app.phieuTiepNhan.trangThaiTienDo", this.txtTrangThai);
        this.addField("app.phieuTiepNhan.thoiGianVao", this.txtNgayTao);
    }

    public void napDuLieu(PhieuTiepNhanView view) {
        if (view == null) {
            return;
        }

        this.txtMaPhieu.setText(view.maPhieuTiepNhan() == null ? "" : view.maPhieuTiepNhan());
        this.txtBienSo.setText(view.bienSo() == null ? "" : view.bienSo());
        this.txtKhachHang.setText(view.tenKhachHang() == null ? "" : view.tenKhachHang());
        this.txtSdt.setText(view.sdt() == null ? "" : view.sdt());
        this.txaMoTa.setText(view.moTa() == null ? "" : view.moTa());
        this.txtTrangThai.setText(
                view.trangThai() == null ? "" : view.trangThai().getI18NBusinessName());
        this.txtNgayTao.setText(view.ngayTao() == null ? "" : view.ngayTao().format(DateFormats.NGAY_GIO));
    }
}
