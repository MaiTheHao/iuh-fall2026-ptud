package iuh.fit.service;

import iuh.fit.dao.DoanhThuDAO;
import iuh.fit.dto.BaoCaoDoanhThuDTO;
import iuh.fit.dto.DoanhThuDTO;
import iuh.fit.dto.HoaDonDoanhThuDTO;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DoanhThuService {

	private final DoanhThuDAO doanhThuDAO;

	public DoanhThuService() {
		this.doanhThuDAO = new DoanhThuDAO();
	}

	public BaoCaoDoanhThuDTO taoBaoCao(LocalDate tuNgay, LocalDate denNgay) throws SQLException {

		if (tuNgay == null || denNgay == null) {
			throw new IllegalArgumentException("app.report.revenue.invalidDate");
		}

		if (tuNgay.isAfter(denNgay)) {
			throw new IllegalArgumentException("app.report.revenue.invalidDateRange");
		}

		LocalDateTime batDau = tuNgay.atStartOfDay();
		LocalDateTime ketThuc = denNgay.plusDays(1).atStartOfDay();

		List<DoanhThuDTO> duLieu = this.doanhThuDAO.findAllChiTietDoanhThu();

		Map<String, DoanhThuDTO> hoaDonTheoMa = new LinkedHashMap<>();

		Map<String, double[]> doanhThuTheoHoaDon = new LinkedHashMap<>();

		double tongDoanhThuDichVu = 0;
		double tongDoanhThuPhuTung = 0;

		for (DoanhThuDTO dong : duLieu) {
			boolean daThanhToan = "DA_THANH_TOAN".equals(dong.trangThaiHoaDon());

			LocalDateTime ngayLoc = daThanhToan ? dong.thoiGianThanhToan() : dong.ngayTao();

			if (ngayLoc == null || ngayLoc.isBefore(batDau) || !ngayLoc.isBefore(ketThuc)) {
				continue;
			}

			hoaDonTheoMa.putIfAbsent(dong.maHoaDon(), dong);

			double[] tongTheoLoai = doanhThuTheoHoaDon.computeIfAbsent(dong.maHoaDon(), ma -> new double[2]);

			if (dong.loaiSanPham() == null) {
				continue;
			}

			double thanhTien = dong.soLuong() * dong.donGia();

			switch (dong.loaiSanPham()) {
			case "DICH_VU" -> {
				tongTheoLoai[0] += thanhTien;

				if (daThanhToan) {
					tongDoanhThuDichVu += thanhTien;
				}
			}
			case "PHU_TUNG" -> {
				tongTheoLoai[1] += thanhTien;

				if (daThanhToan) {
					tongDoanhThuPhuTung += thanhTien;
				}
			}
			default -> {
			}
			}
		}

		List<HoaDonDoanhThuDTO> danhSachHoaDon = new ArrayList<>();

		double tongDoanhThu = 0;

		for (Map.Entry<String, DoanhThuDTO> entry : hoaDonTheoMa.entrySet()) {

			String maHoaDon = entry.getKey();
			DoanhThuDTO hoaDon = entry.getValue();
			double[] tongTheoLoai = doanhThuTheoHoaDon.get(maHoaDon);

			double doanhThuDichVu = tongTheoLoai[0];
			double doanhThuPhuTung = tongTheoLoai[1];
			double tongTien = doanhThuDichVu + doanhThuPhuTung;

			danhSachHoaDon.add(new HoaDonDoanhThuDTO(hoaDon.maHoaDon(), hoaDon.ngayTao(), hoaDon.thoiGianThanhToan(),
					hoaDon.tenKhachHang(), hoaDon.bienSo(), hoaDon.trangThaiHoaDon(), doanhThuDichVu, doanhThuPhuTung,
					tongTien));

			if ("DA_THANH_TOAN".equals(hoaDon.trangThaiHoaDon())) {
				tongDoanhThu += tongTien;
			}
		}

		return new BaoCaoDoanhThuDTO(tongDoanhThu, tongDoanhThuDichVu, tongDoanhThuPhuTung, danhSachHoaDon.size(),
				List.copyOf(danhSachHoaDon));
	}
}