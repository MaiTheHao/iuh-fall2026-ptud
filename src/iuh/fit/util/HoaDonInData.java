
package iuh.fit.util;

import iuh.fit.dto.DanhSachDichVuCriteria;
import iuh.fit.entity.ChiTietHoaDon;
import iuh.fit.entity.DichVu;
import iuh.fit.entity.HoaDon;
import iuh.fit.entity.KhachHang;
import iuh.fit.entity.NhanVien;
import iuh.fit.entity.PhieuTiepNhan;
import iuh.fit.entity.PhuTung;
import iuh.fit.entity.ThanhToan;
import iuh.fit.enumeration.LoaiSanPham;
import iuh.fit.service.DichVuService;
import iuh.fit.service.HoaDonService;
import iuh.fit.service.KhachHangService;
import iuh.fit.service.NhanVienService;
import iuh.fit.service.PhieuTiepNhanService;
import iuh.fit.service.PhuTungService;
import iuh.fit.service.ThanhToanService;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public record HoaDonInData(
        HoaDon hoaDon,
        PhieuTiepNhan phieuTiepNhan,
        List<DongIn> danhSachDong,
        double tongGoc,
        double mucGiam,
        BigDecimal thucThu,
        Optional<ThanhToan> thanhToan) {

    private static final String ERROR_PHIEU_NOT_FOUND =
            "app.phieuTiepNhan.khongTonTai";

    private static final int TRANG_DICH_VU_MAC_DINH = 1;
    private static final int SO_DICH_VU_TOI_DA = 10000;

    private static HoaDonService hoaDonService =
            new HoaDonService();

    private static PhieuTiepNhanService phieuTiepNhanService =
            new PhieuTiepNhanService();

    private static KhachHangService khachHangService =
            new KhachHangService();

    private static NhanVienService nhanVienService =
            new NhanVienService();

    private static PhuTungService phuTungService =
            new PhuTungService();

    private static DichVuService dichVuService =
            new DichVuService();

    private static ThanhToanService thanhToanService =
            new ThanhToanService();

    public HoaDonInData {
        danhSachDong = danhSachDong == null
                ? List.of()
                : List.copyOf(danhSachDong);

        thanhToan = thanhToan == null
                ? Optional.empty()
                : thanhToan;
    }

    public record DongIn(
            String ten,
            String loai,
            int soLuong,
            double donGia,
            double thanhTien) {
    }

    public static HoaDonInData tao(String maHoaDon) throws SQLException {
        HoaDon hoaDon = hoaDonService.getById(maHoaDon);
        PhieuTiepNhan phieu = hoaDon.getPhieuTiepNhan();

        if (phieu == null || phieu.getMaPhieuTiepNhan() == null) {
            throw new IllegalArgumentException(ERROR_PHIEU_NOT_FOUND);
        }

        phieu = phieuTiepNhanService.getById(phieu.getMaPhieuTiepNhan());

        if (phieu == null) {
            throw new IllegalArgumentException(ERROR_PHIEU_NOT_FOUND);
        }
        if (phieu.getKhachHang() != null
                && phieu.getKhachHang().getMaKhachHang() != null) {

            KhachHang khachHang = khachHangService.getById(
                    phieu.getKhachHang().getMaKhachHang());

            phieu.setKhachHang(khachHang);
        }
        if (phieu.getNhanVien() != null
                && phieu.getNhanVien().getMaNhanVien() != null) {

            NhanVien nhanVien = nhanVienService.getById(
                    phieu.getNhanVien().getMaNhanVien());

            phieu.setNhanVien(nhanVien);
        }
        Map<String, String> tenPhuTungTheoMa = new HashMap<>();

        for (PhuTung phuTung : phuTungService.getAllForSelection()) {
            tenPhuTungTheoMa.put(
                    phuTung.getMaPhuTung(),
                    phuTung.getTenPhuTung());
        }

        Map<String, String> tenDichVuTheoMa = new HashMap<>();

        DanhSachDichVuCriteria tieuChiDichVu =
                new DanhSachDichVuCriteria(
                        null,
                        null,
                        null,
                        null,
                        null,
                        TRANG_DICH_VU_MAC_DINH,
                        SO_DICH_VU_TOI_DA);

        for (DichVu dichVu : dichVuService.search(tieuChiDichVu).items()) {
            tenDichVuTheoMa.put(
                    dichVu.getMaDichVu(),
                    dichVu.getTenDichVu());
        }

        List<DongIn> danhSachDong = hoaDon.getDanhSachChiTietHoaDon()
                .stream()
                .map(chiTiet -> taoDongIn(
                        chiTiet,
                        tenPhuTungTheoMa,
                        tenDichVuTheoMa))
                .toList();

        double tongGoc = TinhTienUtil.tongGoc(
                hoaDon.getDanhSachChiTietHoaDon());

        double mucGiam = hoaDonService.tinhMucGiam(hoaDon);

        BigDecimal thucThu = TinhTienUtil.thucThu(
                tongGoc,
                mucGiam);

        Optional<ThanhToan> thanhToan =
                thanhToanService.timTheoHoaDon(maHoaDon);

        return new HoaDonInData(
                hoaDon,
                phieu,
                danhSachDong,
                tongGoc,
                mucGiam,
                thucThu,
                thanhToan);
    }

    private static DongIn taoDongIn(
            ChiTietHoaDon chiTiet,
            Map<String, String> tenPhuTungTheoMa,
            Map<String, String> tenDichVuTheoMa) {

        String maSanPham = chiTiet.getMaSanPham();

        LoaiSanPham loaiSanPham = chiTiet.getLoaiSanPham();

        String tenSanPham;

        if (loaiSanPham == LoaiSanPham.PHU_TUNG) {
            tenSanPham = tenPhuTungTheoMa.get(maSanPham);
        } else {
            tenSanPham = tenDichVuTheoMa.get(maSanPham);
        }

        if (tenSanPham == null || tenSanPham.isBlank()) {
            tenSanPham = maSanPham;
        }

        double donGia = chiTiet.getDonGia() == null
                ? 0.0
                : chiTiet.getDonGia();

        double thanhTien = chiTiet.getSoLuong() * donGia;

        return new DongIn(
                tenSanPham,
                loaiSanPham.getI18NBusinessName(),
                chiTiet.getSoLuong(),
                donGia,
                thanhTien);
    }
}
