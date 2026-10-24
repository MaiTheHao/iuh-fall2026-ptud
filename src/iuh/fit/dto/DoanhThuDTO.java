package iuh.fit.dto;

import java.time.LocalDateTime;

public record DoanhThuDTO(String maHoaDon, LocalDateTime ngayTao, LocalDateTime thoiGianThanhToan, String tenKhachHang,
		String bienSo, String trangThaiHoaDon, String loaiSanPham, int soLuong, double donGia) {
}