package iuh.fit.util;

import iuh.fit.entity.KhachHang;
import iuh.fit.entity.ThanhToan;

import org.dhatim.fastexcel.Workbook;
import org.dhatim.fastexcel.Worksheet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Xuất hoá đơn ra tệp XLSX bằng fastexcel.
 */
public final class HoaDonXlsxUtil {

  private static final String TEN_CUA_HANG = "GARA Ô TÔ LÚA";
  private static final String TEN_UNG_DUNG = "Gara O To Lua";
  private static final String PHIEN_BAN = "1.0";

  private static final String DINH_DANG_TIEN = "#,##0";

  private HoaDonXlsxUtil() {
    throw new AssertionError("Utility class");
  }

  public static void write(File file, HoaDonInData data)
          throws IOException {

    if (file == null || data == null
            || data.hoaDon() == null
            || data.phieuTiepNhan() == null) {
      throw new IllegalArgumentException(
              "app.hoaDon.in.duLieuKhongHopLe");
    }

    try (OutputStream outputStream = new FileOutputStream(file);
            Workbook workbook = new Workbook(
                    outputStream,
                    TEN_UNG_DUNG,
                    PHIEN_BAN)) {

      Worksheet worksheet = workbook.newWorksheet(
              taoTenSheet(data.hoaDon().getMaHoaDon()));

      caiDatDoRongCot(worksheet);

      // Mỗi hàm ghi một khối và trả về dòng kế tiếp để các khối nối tiếp đúng vị trí.
      int dong = 0;

      dong = ghiTieuDe(worksheet, dong);
      dong = ghiThongTinHoaDon(worksheet, data, dong);
      dong = ghiThongTinKhachHang(worksheet, data, dong);
      dong = ghiBangChiTiet(worksheet, data, dong);
      dong = ghiTongTien(worksheet, data, dong);
      ghiThanhToan(worksheet, data, dong);
    }
  }

  private static void caiDatDoRongCot(Worksheet worksheet) {
    worksheet.width(0, 18);
    worksheet.width(1, 35);
    worksheet.width(2, 15);
    worksheet.width(3, 20);
    worksheet.width(4, 20);
  }

  private static int ghiTieuDe(
          Worksheet worksheet,
          int dong) {
    worksheet.range(0, 0, 0, 4).merge();

    worksheet.value(dong, 0, TEN_CUA_HANG);
    worksheet.style(dong, 0).bold().set();

    dong++;
    worksheet.range(1, 0, 1, 4).merge();
    worksheet.value(
            dong,
            0,
            I18nReaderUtil.get("app.hoaDon.in.tieuDe"));

    worksheet.style(dong, 0).bold().set();

    return dong + 2;
  }

  private static int ghiThongTinHoaDon(
          Worksheet worksheet,
          HoaDonInData data,
          int dong) {

    ghiNhanVaGiaTri(
            worksheet,
            dong++,
            0,
            I18nReaderUtil.get("app.hoaDon.in.maHoaDon"),
            giaTri(data.hoaDon().getMaHoaDon()));

    ghiNhanVaGiaTri(
            worksheet,
            dong++,
            0,
            I18nReaderUtil.get("app.hoaDon.in.ngayTao"),
            dinhDangNgay(data.hoaDon().getNgayTao()));

    return dong + 1;
  }

  private static int ghiThongTinKhachHang(
          Worksheet worksheet,
          HoaDonInData data,
          int dong) {

    KhachHang khachHang = data.phieuTiepNhan().getKhachHang();

    worksheet.range(dong, 0, dong, 4).merge();

    worksheet.value(
            dong,
            0,
            I18nReaderUtil.get(
                    "app.hoaDon.in.thongTinKhachHang"));

    worksheet.style(dong, 0).bold().set();

    dong++;

    ghiNhanVaGiaTri(
            worksheet,
            dong++,
            0,
            I18nReaderUtil.get("app.hoaDon.in.khachHang"),
            khachHang == null
                    ? ""
                    : giaTri(khachHang.getHoTen()));

    ghiNhanVaGiaTri(
            worksheet,
            dong++,
            0,
            I18nReaderUtil.get("app.hoaDon.in.sdt"),
            khachHang == null
                    ? ""
                    : giaTri(khachHang.getSdt()));

    ghiNhanVaGiaTri(
            worksheet,
            dong++,
            0,
            I18nReaderUtil.get("app.hoaDon.in.bienSo"),
            giaTri(data.phieuTiepNhan().getBienSo()));

    var nhanVien = data.phieuTiepNhan().getNhanVien();

    ghiNhanVaGiaTri(
            worksheet,
            dong++,
            0,
            I18nReaderUtil.get(
                    "app.hoaDon.in.nhanVienPhuTrach"),
            giaTri(nhanVien == null
                    ? null
                    : nhanVien.getHoTen()));

    return dong + 1;
  }

  private static int ghiBangChiTiet(
          Worksheet worksheet,
          HoaDonInData data,
          int dong) {

    String[] tieuDe = {
        I18nReaderUtil.get("app.hoaDon.in.loai"),
        I18nReaderUtil.get("app.hoaDon.in.ten"),
        I18nReaderUtil.get("app.hoaDon.in.soLuong"),
        I18nReaderUtil.get("app.hoaDon.in.donGia"),
        I18nReaderUtil.get("app.hoaDon.in.thanhTien")
    };

    for (int cot = 0; cot < tieuDe.length; cot++) {
      worksheet.value(dong, cot, tieuDe[cot]);
      worksheet.style(dong, cot).bold().set();
    }

    dong++;

    for (HoaDonInData.DongIn chiTiet : data.danhSachDong()) {
      worksheet.value(dong, 0, giaTri(chiTiet.loai()));
      worksheet.value(dong, 1, giaTri(chiTiet.ten()));
      worksheet.value(dong, 2, chiTiet.soLuong());

      ghiTien(worksheet, dong, 3, chiTiet.donGia());
      ghiTien(worksheet, dong, 4, chiTiet.thanhTien());

      dong++;
    }

    return dong + 1;
  }

  private static int ghiTongTien(
          Worksheet worksheet,
          HoaDonInData data,
          int dong) {

    ghiNhanVaTien(
            worksheet,
            dong++,
            I18nReaderUtil.get("app.hoaDon.in.tongGoc"),
            data.tongGoc());

    ghiNhanVaTien(
            worksheet,
            dong++,
            I18nReaderUtil.get("app.hoaDon.in.giamGia"),
            data.soTienGiam().doubleValue());

    ghiNhanVaTien(
            worksheet,
            dong,
            I18nReaderUtil.get("app.hoaDon.in.thucThu"),
            data.thucThu().doubleValue());

    worksheet.style(dong, 0).bold().set();
    worksheet.style(dong, 4).bold().set();

    return dong + 2;
  }

  private static void ghiThanhToan(
          Worksheet worksheet,
          HoaDonInData data,
          int dong) {

    worksheet.range(dong, 0, dong, 4).merge();

    worksheet.value(
            dong,
            0,
            I18nReaderUtil.get(
                    "app.hoaDon.in.thongTinThanhToan"));

    worksheet.style(dong, 0).bold().set();

    dong++;

    if (data.thanhToan().isEmpty()) {
      worksheet.range(dong, 0, dong, 4).merge();

      worksheet.value(
              dong,
              0,
              I18nReaderUtil.get(
                      "app.hoaDon.in.chuaThanhToan"));

      return;
    }

    ThanhToan thanhToan = data.thanhToan().get();

    ghiNhanVaGiaTri(
            worksheet,
            dong++,
            0,
            I18nReaderUtil.get("app.hoaDon.in.phuongThuc"),
            thanhToan.getPhuongThuc() == null
                    ? ""
                    : I18nReaderUtil.get(
                            thanhToan.getPhuongThuc().getI18nKey()));

    ghiNhanVaGiaTri(
            worksheet,
            dong++,
            0,
            I18nReaderUtil.get("app.hoaDon.in.thoiGian"),
            dinhDangNgay(thanhToan.getThoiGian()));

    ghiNhanVaTien(
            worksheet,
            dong++,
            I18nReaderUtil.get(
                    "app.hoaDon.in.soTienThanhToan"),
            thanhToan.getSoTien() == null
                    ? 0
                    : thanhToan.getSoTien().doubleValue());

    ghiNhanVaGiaTri(
            worksheet,
            dong++,
            0,
            I18nReaderUtil.get("app.hoaDon.in.nhanVienThuTien"),
            thanhToan.getNhanVien() == null
                    ? ""
                    : thanhToan.getNhanVien().getTenHienThi());

    if (thanhToan.getGhiChu() != null
            && !thanhToan.getGhiChu().isBlank()) {

      ghiNhanVaGiaTri(
              worksheet,
              dong,
              0,
              I18nReaderUtil.get("app.hoaDon.in.ghiChu"),
              thanhToan.getGhiChu());
    }
  }

  private static void ghiNhanVaGiaTri(
          Worksheet worksheet,
          int dong,
          int cot,
          String nhan,
          String giaTri) {

    worksheet.value(dong, cot, nhan);
    worksheet.style(dong, cot).bold().set();

    worksheet.value(dong, cot + 1, giaTri);
  }

  private static void ghiNhanVaTien(
          Worksheet worksheet,
          int dong,
          String nhan,
          double giaTri) {

    worksheet.value(dong, 3, nhan);
    worksheet.style(dong, 3).bold().set();

    ghiTien(worksheet, dong, 4, giaTri);
  }

  private static void ghiTien(
          Worksheet worksheet,
          int dong,
          int cot,
          double giaTri) {

    worksheet.value(dong, cot, giaTri);

    // Ghi dạng số kèm định dạng hiển thị để Excel vẫn tính toán được.
    worksheet.style(dong, cot)
            .format(DINH_DANG_TIEN)
            .set();
  }

  private static String taoTenSheet(String maHoaDon) {
    String tenSheet = "HoaDon-" + giaTri(maHoaDon);

    // Excel giới hạn tên sheet 31 ký tự và không cho phép các ký tự đặc biệt này.
    tenSheet = tenSheet.replaceAll("[\\\\/:*?\\[\\]]", "-");

    if (tenSheet.length() > 31) {
      tenSheet = tenSheet.substring(0, 31);
    }

    return tenSheet.isBlank()
            ? "HoaDon"
            : tenSheet;
  }

  private static String dinhDangNgay(
          java.time.LocalDateTime ngay) {

    return ngay == null
            ? ""
            : ngay.format(DateFormats.NGAY_GIO);
  }

  private static String giaTri(Object value) {
    return value == null ? "" : value.toString();
  }
}
