package iuh.fit.dto;

import iuh.fit.enumeration.TrangThaiHoaDon;

import java.time.LocalDateTime;

public record HoaDonView(
        String maHoaDon,
        String maPhieuTiepNhan,
        String tenKhachHang,
        String bienSo,
        LocalDateTime ngayTao,
        TrangThaiHoaDon trangThai,
        double tongTien,
        LocalDateTime thoiGianThanhToan,
        String ghiChu,
        String maVoucher) {
}
