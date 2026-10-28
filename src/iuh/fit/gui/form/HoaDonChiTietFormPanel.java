package iuh.fit.gui.form;

import iuh.fit.dto.HoaDonView;
import iuh.fit.gui.component.FormFieldPanel;
import iuh.fit.gui.theme.Theme;
import iuh.fit.util.I18nReaderUtil;

import javax.swing.JTextField;

import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;

/**
 * Form chỉ đọc hiển thị chi tiết hóa đơn trong dialog.
 */
public final class HoaDonChiTietFormPanel extends FormFieldPanel {

  private static final long serialVersionUID = 1L;

  private static final DecimalFormat TIEN = new DecimalFormat("#,##0");

  private final DateTimeFormatter dinhDangNgayGio =
          DateTimeFormatter.ofPattern(I18nReaderUtil.get("app.date.format") + " HH:mm");

  private final JTextField maHoaDonField = new JTextField(14);
  private final JTextField maPhieuField = new JTextField(14);
  private final JTextField khachHangField = new JTextField(14);
  private final JTextField bienSoField = new JTextField(14);
  private final JTextField ngayTaoField = new JTextField(14);
  private final JTextField trangThaiField = new JTextField(14);
  private final JTextField tongTienField = new JTextField(14);
  private final JTextField thoiGianThanhToanField = new JTextField(14);
  private final JTextField ghiChuField = new JTextField(14);
  private final JTextField lyDoHuyField = new JTextField(14);

  public HoaDonChiTietFormPanel() {
    for (JTextField field : new JTextField[]{
        this.maHoaDonField, this.maPhieuField, this.khachHangField, this.bienSoField,
        this.ngayTaoField, this.trangThaiField, this.tongTienField,
        this.thoiGianThanhToanField, this.ghiChuField, this.lyDoHuyField}) {
      Theme.applyReadOnly(field);
      field.setFocusable(false);
    }

    this.addField("app.hoaDon.maHoaDon", this.maHoaDonField);
    this.addField("app.hoaDon.maPhieuTiepNhan", this.maPhieuField);
    this.addField("app.khachHang.hoTen", this.khachHangField);
    this.addField("app.xe.bienSo", this.bienSoField);
    this.addField("app.hoaDon.ngayTao", this.ngayTaoField);
    this.addField("app.hoaDon.trangThai", this.trangThaiField);
    this.addField("app.hoaDon.tongTien", this.tongTienField);
    this.addField("app.hoaDon.thoiGianThanhToan", this.thoiGianThanhToanField);
    this.addField("app.hoaDon.ghiChu", this.ghiChuField);
    this.addField("app.hoaDon.lyDoHuy", this.lyDoHuyField);
  }

  public void napDuLieu(HoaDonView view) {
    this.maHoaDonField.setText(view.maHoaDon() == null ? "" : view.maHoaDon());
    this.maPhieuField.setText(view.maPhieuTiepNhan() == null ? "" : view.maPhieuTiepNhan());
    this.khachHangField.setText(view.tenKhachHang() == null ? "" : view.tenKhachHang());
    this.bienSoField.setText(view.bienSo() == null ? "" : view.bienSo());
    this.ngayTaoField.setText(view.ngayTao() == null ? "" : view.ngayTao().format(this.dinhDangNgayGio));
    this.trangThaiField.setText(view.trangThai() == null ? "" : view.trangThai().getI18NBusinessName());
    this.tongTienField.setText(TIEN.format(view.tongTien()));
    this.thoiGianThanhToanField.setText(view.thoiGianThanhToan() == null
            ? ""
            : view.thoiGianThanhToan().format(this.dinhDangNgayGio));
    this.ghiChuField.setText(view.ghiChu() == null ? "" : view.ghiChu());
    this.lyDoHuyField.setText(view.lyDoHuy() == null ? "" : view.lyDoHuy());
  }
}
