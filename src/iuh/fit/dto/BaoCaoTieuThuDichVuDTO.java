package iuh.fit.dto;

import java.util.List;

public record BaoCaoTieuThuDichVuDTO(int tongLuotSuDung, double tongDoanhThu, String tenDichVuPhoBien,
		int soDichVuDuocSuDung, List<TieuThuDichVuBaoCaoDTO> danhSachDichVu) {
}