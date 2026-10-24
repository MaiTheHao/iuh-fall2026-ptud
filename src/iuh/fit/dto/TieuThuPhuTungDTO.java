package iuh.fit.dto;

import java.time.LocalDateTime;

public record TieuThuPhuTungDTO(String maPhuTung, String tenPhuTung, int soLuongTon, int soLuongNhap,
		LocalDateTime ngayNhap, int soLuongXuat, LocalDateTime ngayXuat, double donGiaBan, String trangThaiHoaDon) {
}