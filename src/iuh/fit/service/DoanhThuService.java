package iuh.fit.service;

import iuh.fit.dao.DoanhThuDAO;
import iuh.fit.dto.BaoCaoDoanhThuDTO;
import iuh.fit.dto.DoanhThuDTO;
import iuh.fit.dto.HoaDonDoanhThuDTO;
import iuh.fit.util.TinhTienUtil;

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

		if (tuNgay != null && denNgay != null && tuNgay.isAfter(denNgay)) {
			throw new IllegalArgumentException("app.report.revenue.invalidDateRange");
		}

		LocalDateTime batDau = tuNgay == null ? null : tuNgay.atStartOfDay();
		LocalDateTime ketThuc = denNgay == null ? null : denNgay.plusDays(1).atStartOfDay();

		List<DoanhThuDTO> duLieu = this.doanhThuDAO.findAllChiTietDoanhThu();

		Map<String, DoanhThuDTO> hoaDonTheoMa = new LinkedHashMap<>();

		Map<String, double[]> gocTheoHoaDon = new LinkedHashMap<>();

		for (DoanhThuDTO dong : duLieu) {
			boolean daThanhToan = "DA_THANH_TOAN".equals(dong.trangThaiHoaDon());

			if (!daThanhToan) {
				continue;
			}

			LocalDateTime ngayLoc = dong.thoiGianThanhToan();

			if (ngayLoc == null) {
				continue;
			}

			if (batDau != null && ngayLoc.isBefore(batDau)) {
				continue;
			}

			if (ketThuc != null && !ngayLoc.isBefore(ketThuc)) {
				continue;
			}

			hoaDonTheoMa.putIfAbsent(dong.maHoaDon(), dong);

			double[] gocTheoLoai = gocTheoHoaDon.computeIfAbsent(dong.maHoaDon(), ma -> new double[2]);

			if (dong.loaiSanPham() == null) {
				continue;
			}

			double thanhTien = dong.soLuong() * dong.donGia();

			switch (dong.loaiSanPham()) {
			case "DICH_VU" -> gocTheoLoai[0] += thanhTien;
			case "PHU_TUNG" -> gocTheoLoai[1] += thanhTien;
			default -> {
			}
			}
		}

		List<HoaDonDoanhThuDTO> danhSachHoaDon = new ArrayList<>();

		double tongGoc = 0;
		double tongGiam = 0;
		double thucThu = 0;
		double tongDoanhThuDichVu = 0;
		double tongDoanhThuPhuTung = 0;

		for (Map.Entry<String, DoanhThuDTO> entry : hoaDonTheoMa.entrySet()) {

			String maHoaDon = entry.getKey();
			DoanhThuDTO hoaDon = entry.getValue();
			double[] gocTheoLoai = gocTheoHoaDon.get(maHoaDon);

			double gocDichVu = gocTheoLoai[0];
			double gocPhuTung = gocTheoLoai[1];
			double goc = gocDichVu + gocPhuTung;
			double mucGiam = TinhTienUtil.mucGiam(hoaDon.mucGiamApDung());
			double giam = goc * mucGiam;
			double net = TinhTienUtil.thucThu(goc, mucGiam).doubleValue();
			double netDichVu = TinhTienUtil.thucThu(gocDichVu, mucGiam).doubleValue();
			double netPhuTung = net - netDichVu;

			danhSachHoaDon.add(new HoaDonDoanhThuDTO(hoaDon.maHoaDon(), hoaDon.ngayTao(), hoaDon.thoiGianThanhToan(),
					hoaDon.tenKhachHang(), hoaDon.bienSo(), hoaDon.trangThaiHoaDon(), netDichVu, netPhuTung, net,
					goc, giam, net));

			tongGoc += goc;
			tongGiam += giam;
			thucThu += net;
			tongDoanhThuDichVu += netDichVu;
			tongDoanhThuPhuTung += netPhuTung;
		}

		return new BaoCaoDoanhThuDTO(tongGoc, tongGiam, thucThu, tongDoanhThuDichVu, tongDoanhThuPhuTung,
				danhSachHoaDon.size(), List.copyOf(danhSachHoaDon));
	}
}
