package iuh.fit.service;

import iuh.fit.dao.NhanVienDAO;
import iuh.fit.dao.PhanCongDAO;
import iuh.fit.entity.NhanVien;
import iuh.fit.entity.PhanCong;
import iuh.fit.enumeration.CaLamViec;
import iuh.fit.enumeration.TrangThaiNhanVien;
import iuh.fit.enumeration.VaiTro;
import iuh.fit.util.MaGeneratorUtil;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public final class PhanCongService {

	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

	private final PhanCongDAO phanCongDAO = new PhanCongDAO();
	private final NhanVienDAO nhanVienDAO = new NhanVienDAO();

	public List<PhanCong> getAll() throws SQLException {
		return this.phanCongDAO.findAll();
	}

	public List<NhanVien> getDanhSachNhanVien() throws SQLException {
		return this.nhanVienDAO.findAll();
	}

	public Optional<NhanVien> getNhanVienById(String maNhanVien) throws SQLException {
		if (maNhanVien == null || maNhanVien.isBlank()) {
			return Optional.empty();
		}

		return this.nhanVienDAO.findById(maNhanVien.trim());
	}

	public List<PhanCong> search(String maPhanCong, String tenNhanVien, LocalDate tuNgay, LocalDate denNgay,
			CaLamViec caLamViec, String ghiChu, String timNhanh) throws SQLException {

		List<PhanCong> danhSach = this.phanCongDAO.findAll();
		List<NhanVien> nhanViens = this.nhanVienDAO.findAll();

		Map<String, String> tenNhanVienTheoMa = new HashMap<>();

		for (NhanVien nhanVien : nhanViens) {
			tenNhanVienTheoMa.put(nhanVien.getMaNhanVien(), nhanVien.getHoTen() == null ? "" : nhanVien.getHoTen());
		}

		String maCanTim = this.chuanHoa(maPhanCong);
		String tenCanTim = this.chuanHoa(tenNhanVien);
		String ghiChuCanTim = this.chuanHoa(ghiChu);
		String timNhanhCanTim = this.chuanHoa(timNhanh);

		return danhSach.stream().filter(phanCong -> {
			String ma = phanCong.getMaPhanCong();
			return this.chuaChuoi(ma, maCanTim);
		}).filter(phanCong -> {
			String ten = tenNhanVienTheoMa.getOrDefault(phanCong.getMaNhanVien(), "");

			return this.chuaChuoi(ten, tenCanTim) || this.chuaChuoi(phanCong.getMaNhanVien(), tenCanTim);
		}).filter(phanCong -> {
			LocalDate ngayLam = LocalDate.of(phanCong.getNam(), phanCong.getThang(), phanCong.getNgay());

			return (tuNgay == null || !ngayLam.isBefore(tuNgay)) && (denNgay == null || !ngayLam.isAfter(denNgay));
		}).filter(phanCong -> caLamViec == null || phanCong.getCaLamViec() == caLamViec)
				.filter(phanCong -> this.chuaChuoi(phanCong.getGhiChu(), ghiChuCanTim)).filter(phanCong -> {
					if (timNhanhCanTim == null) {
						return true;
					}

					LocalDate ngayLam = LocalDate.of(phanCong.getNam(), phanCong.getThang(), phanCong.getNgay());

					String ten = tenNhanVienTheoMa.getOrDefault(phanCong.getMaNhanVien(), "");

					return this.chuaChuoi(phanCong.getMaPhanCong(), timNhanhCanTim)
							|| this.chuaChuoi(phanCong.getMaNhanVien(), timNhanhCanTim)
							|| this.chuaChuoi(ten, timNhanhCanTim)
							|| this.chuaChuoi(phanCong.getGhiChu(), timNhanhCanTim)
							|| this.chuaChuoi(ngayLam.format(DATE_FORMATTER), timNhanhCanTim)
							|| this.chuaChuoi(phanCong.getCaLamViec() == null ? "" : phanCong.getCaLamViec().name(),
									timNhanhCanTim);
				}).toList();
	}

	private String chuanHoa(String value) {
		if (value == null || value.isBlank()) {
			return null;
		}

		return value.trim().toLowerCase(Locale.ROOT);
	}

	private boolean chuaChuoi(String giaTri, String tuKhoa) {
		if (tuKhoa == null) {
			return true;
		}

		return giaTri != null && giaTri.toLowerCase(Locale.ROOT).contains(tuKhoa);
	}

	public Optional<PhanCong> getById(String maPhanCong) throws SQLException {

		if (maPhanCong == null || maPhanCong.isBlank()) {
			throw new IllegalArgumentException("app.schedule.assignmentIdRequired");
		}

		return this.phanCongDAO.findById(maPhanCong.trim());
	}

	public void kiemTraDuLieuTao(String maNhanVien, LocalDate ngayLam, CaLamViec caLamViec, String ghiChu,
			List<PhanCong> danhSachChoLuu) throws SQLException {

		this.kiemTraDuLieu(maNhanVien, ngayLam, caLamViec, ghiChu, danhSachChoLuu, null);
	}

	private void kiemTraDuLieu(String maNhanVien, LocalDate ngayLam, CaLamViec caLamViec, String ghiChu,
			List<PhanCong> danhSachChoLuu, String maPhanCongBoQua) throws SQLException {

		String maNhanVienDaChuanHoa = maNhanVien == null ? "" : maNhanVien.trim();

		if (maNhanVienDaChuanHoa.isEmpty()) {
			throw new IllegalArgumentException("app.schedule.employeeRequired");
		}

		if (ngayLam == null) {
			throw new IllegalArgumentException("app.schedule.dateRequired");
		}

		if (caLamViec == null) {
			throw new IllegalArgumentException("app.schedule.shiftRequired");
		}

		String ghiChuDaChuanHoa = ghiChu == null ? "" : ghiChu.trim();

		if (ghiChuDaChuanHoa.length() > 255) {
			throw new IllegalArgumentException("app.schedule.noteTooLong");
		}

		NhanVien nhanVien = this.nhanVienDAO.findById(maNhanVienDaChuanHoa)
				.orElseThrow(() -> new IllegalArgumentException("app.schedule.employeeNotFound"));

		if (nhanVien.getTrangThai() != TrangThaiNhanVien.HOAT_DONG
				|| nhanVien.getVaiTro() == VaiTro.NHAN_VIEN_QUAN_LY) {
			throw new IllegalArgumentException("app.schedule.employeeNotEligible");
		}

		boolean biTrungTrongDatabase = this.phanCongDAO.findAll().stream()
				.filter(pc -> maPhanCongBoQua == null || !maPhanCongBoQua.equals(pc.getMaPhanCong()))
				.anyMatch(pc -> maNhanVienDaChuanHoa.equals(pc.getMaNhanVien())
						&& pc.getNgay() == ngayLam.getDayOfMonth() && pc.getThang() == ngayLam.getMonthValue()
						&& pc.getNam() == ngayLam.getYear() && pc.getCaLamViec() == caLamViec);

		if (biTrungTrongDatabase) {
			throw new IllegalArgumentException("app.schedule.duplicateAssignment");
		}

		if (danhSachChoLuu == null) {
			return;
		}

		boolean biTrungTrongDanhSachChoLuu = danhSachChoLuu.stream()
				.filter(pc -> maPhanCongBoQua == null || !maPhanCongBoQua.equals(pc.getMaPhanCong()))
				.anyMatch(pc -> maNhanVienDaChuanHoa.equals(pc.getMaNhanVien())
						&& pc.getNgay() == ngayLam.getDayOfMonth() && pc.getThang() == ngayLam.getMonthValue()
						&& pc.getNam() == ngayLam.getYear() && pc.getCaLamViec() == caLamViec);

		if (biTrungTrongDanhSachChoLuu) {
			throw new IllegalArgumentException("app.schedule.duplicateAssignment");
		}
	}

	public void create(String maNhanVien, LocalDate ngayLam, CaLamViec caLamViec, String ghiChu) throws SQLException {

		this.kiemTraDuLieu(maNhanVien, ngayLam, caLamViec, ghiChu, List.of(), null);

		String maNhanVienDaChuanHoa = maNhanVien.trim();
		String ghiChuDaChuanHoa = ghiChu == null ? "" : ghiChu.trim();

		LocalDateTime thoiDiemTao = LocalDateTime.now();

		PhanCong phanCong = new PhanCong(MaGeneratorUtil.sinhMa("PC", thoiDiemTao), maNhanVienDaChuanHoa,
				ngayLam.getDayOfMonth(), ngayLam.getMonthValue(), ngayLam.getYear(), caLamViec,
				ghiChuDaChuanHoa.isEmpty() ? null : ghiChuDaChuanHoa);

		boolean daThem = this.phanCongDAO.insert(phanCong);

		if (!daThem) {
			throw new IllegalStateException("app.schedule.assignmentCreateFailed");
		}
	}

	public void delete(String maPhanCong) throws SQLException {
		if (maPhanCong == null || maPhanCong.isBlank()) {
			throw new IllegalArgumentException("app.schedule.assignmentIdRequired");
		}

		String maDaChuanHoa = maPhanCong.trim();

		if (this.phanCongDAO.findById(maDaChuanHoa).isEmpty()) {
			throw new IllegalArgumentException("app.schedule.assignmentNotFound");
		}

		boolean daXoa = this.phanCongDAO.delete(maDaChuanHoa);

		if (!daXoa) {
			throw new IllegalStateException("app.schedule.assignmentDeleteFailed");
		}
	}

	public void update(String maPhanCong, String maNhanVien, LocalDate ngayLam, CaLamViec caLamViec, String ghiChu)
			throws SQLException {

		if (maPhanCong == null || maPhanCong.isBlank()) {
			throw new IllegalArgumentException("app.schedule.assignmentIdRequired");
		}

		String maDaChuanHoa = maPhanCong.trim();
		String maNhanVienDaChuanHoa = maNhanVien == null ? "" : maNhanVien.trim();

		if (maNhanVienDaChuanHoa.isEmpty()) {
			throw new IllegalArgumentException("app.schedule.employeeRequired");
		}

		if (ngayLam == null) {
			throw new IllegalArgumentException("app.schedule.dateRequired");
		}

		if (caLamViec == null) {
			throw new IllegalArgumentException("app.schedule.shiftRequired");
		}

		if (this.phanCongDAO.findById(maDaChuanHoa).isEmpty()) {
			throw new IllegalArgumentException("app.schedule.assignmentNotFound");
		}

		if (this.nhanVienDAO.findById(maNhanVienDaChuanHoa).isEmpty()) {
			throw new IllegalArgumentException("app.schedule.employeeNotFound");
		}

		String ghiChuDaChuanHoa = ghiChu == null ? "" : ghiChu.trim();

		if (ghiChuDaChuanHoa.length() > 255) {
			throw new IllegalArgumentException("app.schedule.noteTooLong");
		}

		boolean biTrungLich = this.phanCongDAO.findAll().stream().filter(pc -> !pc.getMaPhanCong().equals(maDaChuanHoa))
				.anyMatch(pc -> pc.getMaNhanVien().equals(maNhanVienDaChuanHoa)
						&& pc.getNgay() == ngayLam.getDayOfMonth() && pc.getThang() == ngayLam.getMonthValue()
						&& pc.getNam() == ngayLam.getYear() && pc.getCaLamViec() == caLamViec);

		if (biTrungLich) {
			throw new IllegalArgumentException("app.schedule.duplicateAssignment");
		}

		PhanCong phanCong = new PhanCong(maDaChuanHoa, maNhanVienDaChuanHoa, ngayLam.getDayOfMonth(),
				ngayLam.getMonthValue(), ngayLam.getYear(), caLamViec,
				ghiChuDaChuanHoa.isEmpty() ? null : ghiChuDaChuanHoa);

		if (!this.phanCongDAO.update(phanCong)) {
			throw new IllegalStateException("app.schedule.assignmentUpdateFailed");
		}
	}

	public List<PhanCong> getLichCuaNhanVien(String maNhanVien, LocalDate ngayDauTuan) throws SQLException {
		if (maNhanVien == null || maNhanVien.isBlank()) {
			throw new IllegalArgumentException("app.schedule.employeeRequired");
		}

		if (ngayDauTuan == null) {
			throw new IllegalArgumentException("app.schedule.dateRequired");
		}

		LocalDate tuNgay = ngayDauTuan;
		LocalDate denNgay = tuNgay.plusDays(6);
		String maNhanVienCanTim = maNhanVien.trim();

		List<PhanCong> danhSach = new ArrayList<>();

		for (PhanCong phanCong : this.phanCongDAO.findAll()) {
			if (!maNhanVienCanTim.equals(phanCong.getMaNhanVien())) {
				continue;
			}

			LocalDate ngayLam = LocalDate.of(phanCong.getNam(), phanCong.getThang(), phanCong.getNgay());

			if (!ngayLam.isBefore(tuNgay) && !ngayLam.isAfter(denNgay)) {
				danhSach.add(phanCong);
			}
		}

		danhSach.sort(Comparator
				.comparing(phanCong -> LocalDate.of(phanCong.getNam(), phanCong.getThang(), phanCong.getNgay())));

		return danhSach;
	}

	public Map<LocalDate, Map<CaLamViec, List<String>>> getLichCuaShop(LocalDate ngayDauTuan) throws SQLException {

		if (ngayDauTuan == null) {
			throw new IllegalArgumentException("app.schedule.dateRequired");
		}

		if (ngayDauTuan.getDayOfWeek() != java.time.DayOfWeek.MONDAY) {
			throw new IllegalArgumentException("app.schedule.dateRequired");
		}

		LocalDate ngayCuoiTuan = ngayDauTuan.plusDays(6);

		List<PhanCong> danhSachPhanCong = this.phanCongDAO.findByDateRange(ngayDauTuan, ngayCuoiTuan);

		Map<String, String> tenNhanVienTheoMa = new HashMap<>();

		for (NhanVien nhanVien : this.nhanVienDAO.findAll()) {
			tenNhanVienTheoMa.put(nhanVien.getMaNhanVien(),
					nhanVien.getHoTen() == null ? nhanVien.getMaNhanVien() : nhanVien.getHoTen());
		}

		Map<LocalDate, Map<CaLamViec, List<String>>> lich = new LinkedHashMap<>();

		for (int i = 0; i < 7; i++) {
			LocalDate ngay = ngayDauTuan.plusDays(i);
			Map<CaLamViec, List<String>> lichTheoCa = new LinkedHashMap<>();

			for (CaLamViec ca : CaLamViec.values()) {
				lichTheoCa.put(ca, new ArrayList<>());
			}

			lich.put(ngay, lichTheoCa);
		}

		for (PhanCong phanCong : danhSachPhanCong) {
			LocalDate ngayLam = LocalDate.of(phanCong.getNam(), phanCong.getThang(), phanCong.getNgay());

			Map<CaLamViec, List<String>> lichTheoCa = lich.get(ngayLam);

			if (lichTheoCa == null || phanCong.getCaLamViec() == null) {
				continue;
			}

			String tenNhanVien = tenNhanVienTheoMa.getOrDefault(phanCong.getMaNhanVien(), phanCong.getMaNhanVien());

			lichTheoCa.get(phanCong.getCaLamViec()).add(tenNhanVien);
		}

		return lich;
	}

	public List<NhanVien> getNhanVienCoThePhanCong() throws SQLException {
		return this.nhanVienDAO.findAll().stream()
				.filter(nhanVien -> nhanVien.getTrangThai() == TrangThaiNhanVien.HOAT_DONG)
				.filter(nhanVien -> nhanVien.getVaiTro() != VaiTro.NHAN_VIEN_QUAN_LY)
				.sorted(Comparator.comparing(NhanVien::getHoTen, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)))
				.toList();
	}
}