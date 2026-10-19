package iuh.fit.service;

import iuh.fit.core.context.AppContext;
import iuh.fit.dao.PhieuNhapPhuTungDAO;
import iuh.fit.dao.TransactionScope;
import iuh.fit.dto.DanhSachCriteria;
import iuh.fit.dto.PaginatedResult;
import iuh.fit.entity.ChiTietPhieuNhapPhuTung;
import iuh.fit.entity.NhanVien;
import iuh.fit.entity.PhieuNhapPhuTung;
import iuh.fit.util.MaGeneratorUtil;
import iuh.fit.util.MaRetryUtil;
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

	private static final String ERROR_DATA = "app.phieuNhap.loiDongNhapLieu";
	private static final String ERROR_DESCRIPTION = "app.phieuNhap.loiMoTa";
	private static final String ERROR_DETAILS_EMPTY = "app.phieuNhap.loiTrongChiTiet";
	private static final String ERROR_PART_REQUIRED = "app.phieuNhap.loiPhuTung";
	private static final String ERROR_QUANTITY = "app.phieuNhap.loiSoLuong";
	private static final String ERROR_PRICE = "app.phieuNhap.loiDonGia";
	private static final String ERROR_ID_REQUIRED = "app.phieuNhap.chuaChonXoa";
	private static final String ERROR_NOT_FOUND = "app.common.notFound";
	private static final String ERROR_NOT_LOGGED_IN = "app.common.notLoggedIn";

	public PaginatedResult<PhieuNhapPhuTung> search(DanhSachCriteria criteria, String tuKhoa, String maPhieuFilter,
			String maThuKhoFilter, String moTaFilter, LocalDate fromDate, LocalDate toDate, Double minCost,
			Double maxCost) throws SQLException {
		List<PhieuNhapPhuTung> all = this.phieuNhapDAO.findAll();

		String keyword = (tuKhoa != null && !tuKhoa.isBlank()) ? TextNormalizerUtil.normalize(tuKhoa.trim()) : null;
		String fMaPhieu = (maPhieuFilter != null && !maPhieuFilter.isBlank())
				? TextNormalizerUtil.normalize(maPhieuFilter.trim())
				: null;
		String fMaNV = (maThuKhoFilter != null && !maThuKhoFilter.isBlank())
				? TextNormalizerUtil.normalize(maThuKhoFilter.trim())
				: null;
		String fMoTa = (moTaFilter != null && !moTaFilter.isBlank()) ? TextNormalizerUtil.normalize(moTaFilter.trim())
				: null;

		List<PhieuNhapPhuTung> matched = new ArrayList<>(all.stream().filter(item -> {
			String maPhieu = TextNormalizerUtil.normalize(item.getMaPhieuNhap());
			String maNV = item.getMaNhanVien() != null ? TextNormalizerUtil.normalize(item.getMaNhanVien()) : "";
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
			if (fromDate != null && item.getNgayTao() != null && item.getNgayTao().toLocalDate().isBefore(fromDate)) {
				return false;
			}
			if (toDate != null && item.getNgayTao() != null && item.getNgayTao().toLocalDate().isAfter(toDate)) {
				return false;
			}

			double tongChiPhi = this.tinhTongChiPhi(item);
			if (minCost != null && tongChiPhi < minCost) {
				return false;
			}
			if (maxCost != null && tongChiPhi > maxCost) {
				return false;
			}

			return true;
		}).toList());

		matched.sort(Comparator.comparing(PhieuNhapPhuTung::getNgayTao,
				Comparator.nullsLast(Comparator.reverseOrder()))
				.thenComparing(PhieuNhapPhuTung::getMaPhieuNhap, Comparator.reverseOrder()));

		int page = criteria != null ? criteria.page() : 1;
		int pageSize = criteria != null ? criteria.pageSize() : 10;
		return PaginatedResult.of(matched, page, pageSize);
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
		MaRetryUtil.thucHien(lan -> {
			LocalDateTime thoiDiem = now.plusSeconds(lan);
			phieu.setNgayTao(thoiDiem);
			return MaGeneratorUtil.sinhMa("PN", thoiDiem);
		}, ma -> {
			phieu.setMaPhieuNhap(ma);
			phieu.setMaNhanVien(user.getMaNhanVien());
			try (TransactionScope scope = TransactionScope.begin()) {
				this.phieuNhapDAO.insert(scope, phieu, danhSachGop);
				for (ChiTietPhieuNhapPhuTung ct : danhSachGop) {
					this.phuTungService.increaseStock(scope, ct.getMaPhuTung(), ct.getSoLuong());
				}
				scope.commit();
			}
		});
		return true;
	}

	public boolean delete(String maPhieuNhap) throws SQLException {
		if (maPhieuNhap == null || maPhieuNhap.isBlank()) {
			throw new IllegalArgumentException(ERROR_ID_REQUIRED);
		}

		String ma = maPhieuNhap.trim();
		try (TransactionScope scope = TransactionScope.begin()) {
			PhieuNhapPhuTung existing = this.phieuNhapDAO.findById(scope, ma)
					.orElseThrow(() -> new IllegalArgumentException(ERROR_NOT_FOUND));
			for (ChiTietPhieuNhapPhuTung ct : existing.getDanhSachChiTietPhieuNhap()) {
				this.phuTungService.decreaseStock(scope, ct.getMaPhuTung(), ct.getSoLuong());
			}
			if (!this.phieuNhapDAO.delete(scope, ma)) {
				throw new IllegalArgumentException(ERROR_NOT_FOUND);
			}
			scope.commit();
		}
		return true;
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
