package iuh.fit.dto;

import iuh.fit.entity.PhanCong;

import java.util.Optional;

public record ThongKeDashboard(
        double doanhThuHomNay,
        int phieuDangXuLy,
        int phieuChoBanGiao,
        int khieuNaiChuaXuLy,
        int hoaDonChuaThanhToan,
        double tongHoaDonChuaThanhToan,
        Optional<PhanCong> caHomNay) {

    public static ThongKeDashboard rong() {
        return new ThongKeDashboard(0, 0, 0, 0, 0, 0, Optional.empty());
    }
}
