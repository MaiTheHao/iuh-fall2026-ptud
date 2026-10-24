package iuh.fit.dto;

import java.util.List;

public record BaoCaoDoanhThuDTO(double tongDoanhThu, double doanhThuDichVu, double doanhThuPhuTung, int soLuongHoaDon,
		List<HoaDonDoanhThuDTO> danhSachHoaDon) {
}