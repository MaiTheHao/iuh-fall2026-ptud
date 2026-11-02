package iuh.fit.gui.form;

import iuh.fit.dto.PhieuTiepNhanView;
import iuh.fit.gui.component.FormFieldPanel;
import iuh.fit.gui.theme.Theme;

import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.time.format.DateTimeFormatter;

/**
 * Form chỉ đọc hiển thị toàn bộ chi tiết của một phiếu tiếp nhận.
 */
public final class PhieuTiepNhanChiTietFormPanel extends FormFieldPanel {

    private static final long serialVersionUID = 1L;

    private static final DateTimeFormatter NGAY_GIO =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final JTextField maPhieuField = new JTextField(14);
    private final JTextField bienSoField = new JTextField(14);
    private final JTextField khachHangField = new JTextField(14);
    private final JTextField sdtField = new JTextField(14);
    private final JTextArea moTaArea = new JTextArea(4, 20);
    private final JTextField trangThaiField = new JTextField(14);
    private final JTextField ngayTaoField = new JTextField(14);

    public PhieuTiepNhanChiTietFormPanel() {
        for (JTextField field : new JTextField[]{
            this.maPhieuField, this.bienSoField, this.khachHangField,
            this.sdtField, this.trangThaiField, this.ngayTaoField}) {
            Theme.applyReadOnly(field);
            field.setFocusable(false);
        }

        this.moTaArea.setEditable(false);
        this.moTaArea.setFocusable(false);
        this.moTaArea.setBackground(Theme.palette().ACCENT_SOFT());
        this.moTaArea.setForeground(Theme.INK);

        this.addField("app.phieuTiepNhan.maPhieu", this.maPhieuField);
        this.addField("app.phieuTiepNhan.bienSo", this.bienSoField);
        this.addField("app.phieuTiepNhan.khachHang", this.khachHangField);
        this.addField("app.phieuTiepNhan.dienThoai", this.sdtField);
        this.addField("app.phieuTiepNhan.noiDungYeuCau", taoKhungMoTa(this.moTaArea));
        this.addField("app.phieuTiepNhan.trangThaiTienDo", this.trangThaiField);
        this.addField("app.phieuTiepNhan.thoiGianVao", this.ngayTaoField);
    }

    public void napDuLieu(PhieuTiepNhanView view) {
        if (view == null) {
            return;
        }

        this.maPhieuField.setText(view.maPhieuTiepNhan() == null ? "" : view.maPhieuTiepNhan());
        this.bienSoField.setText(view.bienSo() == null ? "" : view.bienSo());
        this.khachHangField.setText(view.tenKhachHang() == null ? "" : view.tenKhachHang());
        this.sdtField.setText(view.sdt() == null ? "" : view.sdt());
        this.moTaArea.setText(view.moTa() == null ? "" : view.moTa());
        this.trangThaiField.setText(
                view.trangThai() == null ? "" : view.trangThai().getI18NBusinessName());
        this.ngayTaoField.setText(view.ngayTao() == null ? "" : view.ngayTao().format(NGAY_GIO));
    }
}
