package iuh.fit.service;

import iuh.fit.dao.HoaDonDAO;
import iuh.fit.dao.KhieuNaiDAO;
import iuh.fit.dao.PhanCongDAO;
import iuh.fit.dao.PhieuTiepNhanDAO;
import iuh.fit.dto.ThongKeDashboard;
import iuh.fit.entity.ChiTietHoaDon;
import iuh.fit.entity.HoaDon;
import iuh.fit.entity.KhieuNai;
import iuh.fit.entity.PhanCong;
import iuh.fit.entity.PhieuTiepNhan;
import iuh.fit.enumeration.TrangThaiHoaDon;
import iuh.fit.enumeration.TrangThaiKhieuNai;
import iuh.fit.enumeration.TrangThaiPhieuTiepNhan;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class DashboardService {

    private final PhieuTiepNhanDAO phieuTiepNhanDAO = new PhieuTiepNhanDAO();
    private final KhieuNaiDAO khieuNaiDAO = new KhieuNaiDAO();
    private final HoaDonDAO hoaDonDAO = new HoaDonDAO();
    private final PhanCongDAO phanCongDAO = new PhanCongDAO();

    public ThongKeDashboard thongKe(String maNhanVien) throws SQLException {
        LocalDate homNay = LocalDate.now();

        List<PhieuTiepNhan> phieuTiepNhans = this.phieuTiepNhanDAO.findAll();
        List<KhieuNai> khieuNais = this.khieuNaiDAO.findAll();
        List<HoaDon> hoaDons = this.hoaDonDAO.findAllWithDetails();
        List<PhanCong> phanCongs = this.phanCongDAO.findAll();

        double doanhThuHomNay = 0;
        int hoaDonChuaThanhToan = 0;
        double tongHoaDonChuaThanhToan = 0;

        for (HoaDon hoaDon : hoaDons) {
            if (hoaDon.getTrangThaiHoaDon() == TrangThaiHoaDon.DA_THANH_TOAN
                    && hoaDon.getThoiGianThanhToan() != null
                    && hoaDon.getThoiGianThanhToan().toLocalDate().equals(homNay)) {
                doanhThuHomNay += this.tongTien(hoaDon);
            }

            if (hoaDon.getTrangThaiHoaDon() == TrangThaiHoaDon.CHO_THANH_TOAN) {
                hoaDonChuaThanhToan++;
                tongHoaDonChuaThanhToan += this.tongTien(hoaDon);
            }
        }

        int phieuDangXuLy = 0;
        int phieuChoBanGiao = 0;

        for (PhieuTiepNhan phieu : phieuTiepNhans) {
            if (phieu.getTrangThai() == TrangThaiPhieuTiepNhan.DANG_XU_LY) {
                phieuDangXuLy++;
            }

            if (phieu.getTrangThai() == TrangThaiPhieuTiepNhan.HOAN_THANH) {
                phieuChoBanGiao++;
            }
        }

        int khieuNaiChuaXuLy = 0;

        for (KhieuNai khieuNai : khieuNais) {
            if (khieuNai.getTrangThai() == TrangThaiKhieuNai.CHUA_XU_LY) {
                khieuNaiChuaXuLy++;
            }
        }

        return new ThongKeDashboard(
                doanhThuHomNay,
                phieuDangXuLy,
                phieuChoBanGiao,
                khieuNaiChuaXuLy,
                hoaDonChuaThanhToan,
                tongHoaDonChuaThanhToan,
                this.timCaHomNay(phanCongs, maNhanVien, homNay));
    }

    private Optional<PhanCong> timCaHomNay(List<PhanCong> phanCongs, String maNhanVien, LocalDate homNay) {
        if (maNhanVien == null) {
            return Optional.empty();
        }

        for (PhanCong phanCong : phanCongs) {
            if (maNhanVien.equals(phanCong.getMaNhanVien())
                    && phanCong.getNgay() == homNay.getDayOfMonth()
                    && phanCong.getThang() == homNay.getMonthValue()
                    && phanCong.getNam() == homNay.getYear()) {
                return Optional.of(phanCong);
            }
        }

        return Optional.empty();
    }

    private double tongTien(HoaDon hoaDon) {
        double tong = 0;

        for (ChiTietHoaDon chiTiet : hoaDon.getDanhSachChiTietHoaDon()) {
            if (chiTiet.getDonGia() != null) {
                tong += chiTiet.getSoLuong() * chiTiet.getDonGia();
            }
        }

        return tong;
    }
}
