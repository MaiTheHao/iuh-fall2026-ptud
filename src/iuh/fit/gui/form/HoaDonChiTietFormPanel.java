package iuh.fit.gui.form;

import iuh.fit.dto.HoaDonView;
import iuh.fit.gui.Theme;
import iuh.fit.gui.component.FormFieldPanel;
import iuh.fit.util.I18nReaderUtil;

import javax.swing.JTextField;

import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;

/**
 * Form chỉ đọc hiển thị chi tiết hóa đơn trong dialog.
 */
public final class HoaDonChiTietFormPanel extends FormFieldPanel {

  

  private static final DecimalFormat TIEN = new DecimalFormat("#,##0");

  private final DateTimeFormatter dinhDangNgayGio =
          DateTimeFormatter.ofPattern(I18nReaderUtil.get("app.date.format") + " HH:mm");

  private final JTextField txtMaHoaDon = new JTextField(14);
  private final JTextField txtMaPhieu = new JTextField(14);
  private final JTextField txtKhachHang = new JTextField(14);
  private final JTextField txtBienSo = new JTextField(14);
  private final JTextField txtNgayTao = new JTextField(14);
  private final JTextField txtTrangThai = new JTextField(14);
  private final JTextField txtTongTien = new JTextField(14);
  private final JTextField txtThoiGianThanhToan = new JTextField(14);
  private final JTextField txtGhiChu = new JTextField(14);
  private final JTextField txtLyDoHuy = new JTextField(14);

  public HoaDonChiTietFormPanel() {
    for (JTextField field : new JTextField[]{
        this.txtMaHoaDon, this.txtMaPhieu, this.txtKhachHang, this.txtBienSo,
        this.txtNgayTao, this.txtTrangThai, this.txtTongTien,
        this.txtThoiGianThanhToan, this.txtGhiChu, this.txtLyDoHuy}) {
      Theme.applyReadOnly(field);
      field.setFocusable(false);
    }

    this.addField("app.hoaDon.maHoaDon", this.txtMaHoaDon);
    this.addField("app.hoaDon.maPhieuTiepNhan", this.txtMaPhieu);
    this.addField("app.khachHang.hoTen", this.txtKhachHang);
    this.addField("app.xe.bienSo", this.txtBienSo);
    this.addField("app.hoaDon.ngayTao", this.txtNgayTao);
    this.addField("app.hoaDon.trangThai", this.txtTrangThai);
    this.addField("app.hoaDon.tongTien", this.txtTongTien);
    this.addField("app.hoaDon.thoiGianThanhToan", this.txtThoiGianThanhToan);
    this.addField("app.hoaDon.ghiChu", this.txtGhiChu);
    this.addField("app.hoaDon.lyDoHuy", this.txtLyDoHuy);
  }

  public void napDuLieu(HoaDonView view) {
    this.txtMaHoaDon.setText(view.maHoaDon() == null ? "" : view.maHoaDon());
    this.txtMaPhieu.setText(view.maPhieuTiepNhan() == null ? "" : view.maPhieuTiepNhan());
    this.txtKhachHang.setText(view.tenKhachHang() == null ? "" : view.tenKhachHang());
    this.txtBienSo.setText(view.bienSo() == null ? "" : view.bienSo());
    this.txtNgayTao.setText(view.ngayTao() == null ? "" : view.ngayTao().format(this.dinhDangNgayGio));
    this.txtTrangThai.setText(view.trangThai() == null ? "" : view.trangThai().getI18NBusinessName());
    this.txtTongTien.setText(TIEN.format(view.tongTien()));
    this.txtThoiGianThanhToan.setText(view.thoiGianThanhToan() == null
            ? ""
            : view.thoiGianThanhToan().format(this.dinhDangNgayGio));
    this.txtGhiChu.setText(view.ghiChu() == null ? "" : view.ghiChu());
    this.txtLyDoHuy.setText(view.lyDoHuy() == null ? "" : view.lyDoHuy());
  }
}
