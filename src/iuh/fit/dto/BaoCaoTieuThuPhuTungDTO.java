package iuh.fit.dto;

import java.util.List;

public record BaoCaoTieuThuPhuTungDTO(int tongNhap, int tongXuat, int tongTonKho, double tyLeTieuThu,
		double tongDoanhThu, List<TieuThuPhuTungBaoCaoDTO> danhSachPhuTung) {
}