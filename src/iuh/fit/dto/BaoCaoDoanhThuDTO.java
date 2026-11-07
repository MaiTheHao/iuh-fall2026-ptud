package iuh.fit.dto;

import java.util.List;

public record BaoCaoDoanhThuDTO(double tongGoc, double tongGiam, double thucThu, double doanhThuDichVu,
		double doanhThuPhuTung, int soLuongHoaDon, List<HoaDonDoanhThuDTO> danhSachHoaDon) {
}
