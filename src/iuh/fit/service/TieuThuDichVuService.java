package iuh.fit.service;

import iuh.fit.dao.TieuThuDichVuDAO;
import iuh.fit.dto.BaoCaoTieuThuDichVuDTO;
import iuh.fit.dto.TieuThuDichVuBaoCaoDTO;
import iuh.fit.dto.TieuThuDichVuDTO;

import java.sql.SQLException;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.List;

public class TieuThuDichVuService {

	private final TieuThuDichVuDAO tieuThuDichVuDAO;

	public TieuThuDichVuService() {
		this.tieuThuDichVuDAO = new TieuThuDichVuDAO();
	}

	public BaoCaoTieuThuDichVuDTO taoBaoCao(YearMonth thang) throws SQLException {

		if (thang == null) {
			throw new IllegalArgumentException("app.report.services.invalidMonth");
		}

		List<TieuThuDichVuDTO> duLieu = this.tieuThuDichVuDAO.findAll(thang);

		int tongLuotSuDung = 0;
		double tongDoanhThu = 0;

		for (TieuThuDichVuDTO dichVu : duLieu) {
			tongLuotSuDung += dichVu.soLuotSuDung();
			tongDoanhThu += dichVu.doanhThu();
		}

		List<TieuThuDichVuBaoCaoDTO> danhSach = duLieu.stream().filter(dichVu -> dichVu.soLuotSuDung() > 0)
				.map(dichVu -> new TieuThuDichVuBaoCaoDTO(dichVu.maDichVu(), dichVu.tenDichVu(), dichVu.soLuotSuDung(),
						dichVu.doanhThu()))
				.sorted(Comparator.comparingInt(TieuThuDichVuBaoCaoDTO::soLuotSuDung).reversed()
						.thenComparing(Comparator.comparingDouble(TieuThuDichVuBaoCaoDTO::doanhThu).reversed()))
				.limit(20).toList();

		String tenDichVuPhoBien = danhSach.isEmpty() ? "" : danhSach.get(0).tenDichVu();

		return new BaoCaoTieuThuDichVuDTO(tongLuotSuDung, tongDoanhThu, tenDichVuPhoBien,
				(int) duLieu.stream().filter(dichVu -> dichVu.soLuotSuDung() > 0).count(), danhSach);
	}
}