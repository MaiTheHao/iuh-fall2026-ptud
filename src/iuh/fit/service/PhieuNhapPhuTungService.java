package iuh.fit.service;

import iuh.fit.core.context.AppContext;
import iuh.fit.dao.PhieuNhapPhuTungDAO;
import iuh.fit.dao.TransactionScope;
import iuh.fit.dto.DanhSachPhieuNhapCriteria;
import iuh.fit.dto.PaginatedResult;
import iuh.fit.entity.ChiTietPhieuNhapPhuTung;
import iuh.fit.entity.NhanVien;
import iuh.fit.entity.PhieuNhapPhuTung;
import iuh.fit.enumeration.LoaiMa;
import iuh.fit.util.TextNormalizerUtil;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PhieuNhapPhuTungService {

	private final PhieuNhapPhuTungDAO phieuNhapDAO = new PhieuNhapPhuTungDAO();
	private final PhuTungService phuTungService = new PhuTungService();
	private final MaService maService = new MaService();

	private static final String ERROR_DATA = "app.phieuNhap.loiDongNhapLieu";
	private static final String ERROR_DESCRIPTION = "app.phieuNhap.loiMoTa";
	private static final String ERROR_DETAILS_EMPTY = "app.phieuNhap.loiTrongChiTiet";
	private static final String ERROR_PART_REQUIRED = "app.phieuNhap.loiPhuTung";
	private static final String ERROR_QUANTITY = "app.phieuNhap.loiSoLuong";
	private static final String ERROR_PRICE = "app.phieuNhap.loiDonGia";
	private static final String ERROR_ID_REQUIRED = "app.phieuNhap.chuaChonXoa";
	private static final String ERROR_NOT_LOGGED_IN = "app.common.notLoggedIn";

	public PaginatedResult<PhieuNhapPhuTung> search(DanhSachPhieuNhapCriteria criteria) throws SQLException {
		DanhSachPhieuNhapCriteria c = criteria != null ? criteria : DanhSachPhieuNhapCriteria.macDinh();
		List<PhieuNhapPhuTung> all = this.phieuNhapDAO.findAll();

		String keyword = (c.timNhanh() != null && !c.timNhanh().isBlank())
				? TextNormalizerUtil.normalize(c.timNhanh().trim())
				: null;
		String fMaPhieu = (c.maPhieuNhap() != null && !c.maPhieuNhap().isBlank())
				? TextNormalizerUtil.normalize(c.maPhieuNhap().trim())
				: null;
		String fMaNV = (c.maThuKho() != null && !c.maThuKho().isBlank())
				? TextNormalizerUtil.normalize(c.maThuKho().trim())
				: null;
		String fMoTa = (c.moTa() != null && !c.moTa().isBlank()) ? TextNormalizerUtil.normalize(c.moTa().trim())
				: null;

		List<PhieuNhapPhuTung> matched = new ArrayList<>(all.stream().filter(item -> {
			String maPhieu = TextNormalizerUtil.normalize(item.getMaPhieuNhap());
			String maNV = item.getNhanVien() != null ? TextNormalizerUtil.normalize(item.getNhanVien().getMaNhanVien())
					: "";
			String moTa = item.getMoTa() != null ? TextNormalizerUtil.normalize(item.getMoTa()) : "";
			if (fMaPhieu != null && !maPhieu.contains(fMaPhieu)) {
				return false;
			}
			if (fMaNV != null && !maNV.contains(fMaNV)) {
				return false;
			}
			if (fMoTa != null && !moTa.contains(fMoTa)) {
				return false;
			}
			if (keyword != null && !(maPhieu.contains(keyword) || moTa.contains(keyword) || maNV.contains(keyword))) {
				return false;
			}
			if (c.ngayTaoTu() != null && item.getNgayTao() != null
					&& item.getNgayTao().toLocalDate().isBefore(c.ngayTaoTu())) {
				return false;
			}
			if (c.ngayTaoDen() != null && item.getNgayTao() != null
					&& item.getNgayTao().toLocalDate().isAfter(c.ngayTaoDen())) {
				return false;
			}

			double tongChiPhi = this.tinhTongChiPhi(item);
			if (c.tongChiPhiTu() != null && tongChiPhi < c.tongChiPhiTu()) {
				return false;
			}
			if (c.tongChiPhiDen() != null && tongChiPhi > c.tongChiPhiDen()) {
				return false;
			}

			return true;
		}).toList());

		matched.sort(Comparator.comparing(PhieuNhapPhuTung::getNgayTao,
				Comparator.nullsLast(Comparator.reverseOrder()))
				.thenComparing(PhieuNhapPhuTung::getMaPhieuNhap, Comparator.reverseOrder()));

		return PaginatedResult.of(matched, c);
	}

	public double tinhTongChiPhi(PhieuNhapPhuTung item) {
		if (item == null || item.getDanhSachChiTietPhieuNhap() == null) {
			return 0.0;
		}
		double tong = 0.0;
		for (ChiTietPhieuNhapPhuTung ct : item.getDanhSachChiTietPhieuNhap()) {
			double gia = ct.getDonGiaNhap() != null ? ct.getDonGiaNhap() : 0.0;
			tong += ct.getSoLuong() * gia;
		}
		return tong;
	}

	public boolean create(PhieuNhapPhuTung phieu, List<ChiTietPhieuNhapPhuTung> chiTiets) throws SQLException {
		this.validate(phieu, chiTiets);
		phieu.setMoTa(phieu.getMoTa().trim());
		List<ChiTietPhieuNhapPhuTung> danhSachGop = this.gopChiTietTrung(chiTiets);
		NhanVien user = AppContext.getInstance().getCurrentUser();
		if (user == null || user.getMaNhanVien() == null || user.getMaNhanVien().isBlank()) {
			throw new IllegalArgumentException(ERROR_NOT_LOGGED_IN);
		}

		LocalDateTime now = LocalDateTime.now();
		try (TransactionScope scope = TransactionScope.begin()) {
			phieu.setMaPhieuNhap(this.maService.genMa(scope, LoaiMa.PN));
			phieu.setNgayTao(now);
			phieu.setNhanVien(user);
			this.phieuNhapDAO.insert(scope, phieu, danhSachGop);
			for (ChiTietPhieuNhapPhuTung ct : danhSachGop) {
				this.phuTungService.increaseStock(scope, ct.getMaPhuTung(), ct.getSoLuong());
			}
			scope.commit();
		}
		return true;
	}

	public boolean delete(String maPhieuNhap) throws SQLException {
		if (maPhieuNhap == null || maPhieuNhap.isBlank()) {
			throw new IllegalArgumentException(ERROR_ID_REQUIRED);
		}
		throw new IllegalStateException("app.phieuNhap.khongTheXoa");
	}

	private List<ChiTietPhieuNhapPhuTung> gopChiTietTrung(List<ChiTietPhieuNhapPhuTung> chiTiets) {
		Map<String, ChiTietPhieuNhapPhuTung> map = new LinkedHashMap<>();
		for (ChiTietPhieuNhapPhuTung ct : chiTiets) {
			String ma = ct.getMaPhuTung().trim();
			if (map.containsKey(ma)) {
				ChiTietPhieuNhapPhuTung tonTai = map.get(ma);
				tonTai.setSoLuong(tonTai.getSoLuong() + ct.getSoLuong());
				tonTai.setDonGiaNhap(ct.getDonGiaNhap());
			} else {
				ChiTietPhieuNhapPhuTung moi = new ChiTietPhieuNhapPhuTung();
				moi.setMaPhuTung(ma);
				moi.setSoLuong(ct.getSoLuong());
				moi.setDonGiaNhap(ct.getDonGiaNhap());
				map.put(ma, moi);
			}
		}
		return new ArrayList<>(map.values());
	}

	private void validate(PhieuNhapPhuTung phieu, List<ChiTietPhieuNhapPhuTung> chiTiets) {
		if (phieu == null) {
			throw new IllegalArgumentException(ERROR_DATA);
		}
		if (phieu.getMoTa() == null || phieu.getMoTa().isBlank()) {
			throw new IllegalArgumentException(ERROR_DESCRIPTION);
		}
		if (chiTiets == null || chiTiets.isEmpty()) {
			throw new IllegalArgumentException(ERROR_DETAILS_EMPTY);
		}

		for (ChiTietPhieuNhapPhuTung ct : chiTiets) {
			if (ct.getMaPhuTung() == null || ct.getMaPhuTung().isBlank()) {
				throw new IllegalArgumentException(ERROR_PART_REQUIRED);
			}
			if (ct.getSoLuong() <= 0) {
				throw new IllegalArgumentException(ERROR_QUANTITY);
			}
			if (ct.getDonGiaNhap() == null || !Double.isFinite(ct.getDonGiaNhap()) || ct.getDonGiaNhap() < 0) {
				throw new IllegalArgumentException(ERROR_PRICE);
			}
		}
	}
}
