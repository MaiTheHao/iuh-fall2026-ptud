package iuh.fit.dto;

import java.time.LocalDateTime;

public record HoaDonDoanhThuDTO(String maHoaDon, LocalDateTime ngayTao, LocalDateTime thoiGianThanhToan,
		String tenKhachHang, String bienSo, String trangThaiHoaDon, double doanhThuDichVu, double doanhThuPhuTung,
		double tongDoanhThu) {
}