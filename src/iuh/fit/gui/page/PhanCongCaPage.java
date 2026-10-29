package iuh.fit.gui.page;

import iuh.fit.dto.BaseFormDialogConfig;
import iuh.fit.entity.NhanVien;
import iuh.fit.entity.PhanCong;
import iuh.fit.enumeration.CaLamViec;
import iuh.fit.enumeration.MenuGroup;
import iuh.fit.gui.component.BaseFormDialog;
import iuh.fit.gui.component.ChonNhanVienDialog;
import iuh.fit.gui.component.ChonSanPhamDialog;
import iuh.fit.gui.component.LuoiLichTuan;
import iuh.fit.gui.component.ThanhDieuHuongTuan;
import iuh.fit.gui.component.ThongBao;
import iuh.fit.gui.form.PhanCongCaFormPanel;
import iuh.fit.gui.form.PhanCongCaSuaFormPanel;
import iuh.fit.gui.theme.Theme;
import iuh.fit.service.PhanCongService;

import javax.swing.SwingUtilities;

import java.awt.BorderLayout;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PhanCongCaPage extends AbstractPage {

	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = Logger.getLogger(PhanCongCaPage.class.getName());

	/** Dialog cập nhật: 5 trường nhập. */
	private static final int CHIEU_CAO_SUA = 500;

	private final ThanhDieuHuongTuan thanhDieuHuong = new ThanhDieuHuongTuan();

	private final LuoiLichTuan luoiLich = new LuoiLichTuan();

	private final PhanCongService phanCongService = new PhanCongService();

	private LocalDate ngayDauTuan;

	public PhanCongCaPage() {
		this.setLayout(new BorderLayout(0, 8));
		this.ngayDauTuan = this.layNgayDauTuanHienTai();
		this.thanhDieuHuong.datNgayDauTuan(this.ngayDauTuan);

		this.ganSuKien();
	}

	@Override
	public void render() {
		this.removeAll();
		this.thanhDieuHuong.capNhatNgonNgu();
		this.setBorder(Theme.leDeu(8));

		this.add(this.thanhDieuHuong, BorderLayout.NORTH);
		this.add(this.luoiLich, BorderLayout.CENTER);

		this.taiLai();

		this.revalidate();
		this.repaint();
	}

	private void ganSuKien() {
		this.thanhDieuHuong.datXuLyDoiTuan(ngay -> { this.ngayDauTuan = ngay; this.taiLai(); });

		this.luoiLich.datXuLyThem(this::moChonNhanVien);
		this.luoiLich.datXuLySua(this::moDialogSua);
		this.luoiLich.datXuLyXoa(this::xoaPhanCong);
	}

	private LocalDate layNgayDauTuanHienTai() {
		return LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
	}

	private void taiLai() {
		try {
			LocalDate ngayCuoiTuan = this.ngayDauTuan.plusDays(6);

			List<PhanCong> danhSach = this.phanCongService.search(null, null, this.ngayDauTuan, ngayCuoiTuan, null,
					null, null);
			Map<String, String> tenNhanVienTheoMa = this.phanCongService.getTenNhanVienTheoMa();

			Map<LocalDate, Map<CaLamViec, List<LuoiLichTuan.MucPhanCong>>> duLieu = new HashMap<>();

			for (PhanCong phanCong : danhSach) {
				LocalDate ngayLam = LocalDate.of(phanCong.getNam(), phanCong.getThang(), phanCong.getNgay());

				if (ngayLam.isBefore(this.ngayDauTuan) || ngayLam.isAfter(ngayCuoiTuan)
						|| phanCong.getCaLamViec() == null) {
					continue;
				}

				String maNhanVien = phanCong.getMaNhanVien();
				LuoiLichTuan.MucPhanCong muc = new LuoiLichTuan.MucPhanCong(phanCong.getMaPhanCong(), maNhanVien,
						tenNhanVienTheoMa.getOrDefault(maNhanVien, maNhanVien));

				duLieu.computeIfAbsent(ngayLam, ngay -> new HashMap<>())
						.computeIfAbsent(phanCong.getCaLamViec(), ca -> new ArrayList<>())
						.add(muc);
			}

			this.luoiLich.datTuan(this.ngayDauTuan);
			this.luoiLich.datDuLieu(duLieu);

		} catch (SQLException ex) {
			LOGGER.log(Level.SEVERE, "Khong the tai lich phan cong ca", ex);
			ThongBao.loi(this, "app.login.errorSystem");
		}
	}

	private void moChonNhanVien(LocalDate ngay, CaLamViec ca) {
		ChonNhanVienDialog dialog = new ChonNhanVienDialog(SwingUtilities.getWindowAncestor(this),
				this.phanCongService, ngay, ca);

		Optional<ChonSanPhamDialog.KetQuaChon<NhanVien>> ketQua = dialog.showDialog();

		if (ketQua.isEmpty() || ketQua.get().sanPham() == null) {
			return;
		}

		this.taoPhanCong(ketQua.get().sanPham().getMaNhanVien(), ngay, ca);
	}

	private boolean taoPhanCong(String maNhanVien, LocalDate ngay, CaLamViec ca) {
		try {
			this.phanCongService.create(maNhanVien, ngay, ca, null);
			ThongBao.thongTin(this, "app.schedule.assignmentCreated");
			this.taiLai();
			return true;
		} catch (IllegalArgumentException | IllegalStateException ex) {
			ThongBao.loi(this, ex);
		} catch (SQLException ex) {
			LOGGER.log(Level.SEVERE, "Khong the tao phan cong ca", ex);
			ThongBao.loi(this, "app.login.errorSystem");
		}

		return false;
	}

	private void moDialogSua(LuoiLichTuan.MucPhanCong muc) {
		try {
			Optional<PhanCong> ketQua = this.phanCongService.getById(muc.maPhanCong());

			if (ketQua.isEmpty()) {
				ThongBao.canhBao(this, "app.schedule.assignmentNotFound");
				return;
			}

			PhanCong phanCong = ketQua.get();
			List<NhanVien> danhSachNhanVien = new ArrayList<>(this.layDanhSachNhanVien());
			this.boSungNhanVienDangPhanCong(danhSachNhanVien, phanCong);

			PhanCongCaSuaFormPanel form = new PhanCongCaSuaFormPanel(danhSachNhanVien);
			form.napDuLieu(phanCong);

			BaseFormDialog dialog = new BaseFormDialog(new BaseFormDialogConfig(
					SwingUtilities.getWindowAncestor(this),
					"app.common.editTitle",
					form,
					() -> this.capNhatPhanCong(form),
					CHIEU_CAO_SUA));

			if (dialog.showAndGet()) {
				this.taiLai();
			}
		} catch (SQLException ex) {
			LOGGER.log(Level.SEVERE, "Khong the tai phan cong ca", ex);
			ThongBao.loi(this, "app.login.errorSystem");
		}
	}

	private boolean capNhatPhanCong(PhanCongCaFormPanel form) {
		if (!form.validateInput()) {
			return false;
		}

		PhanCongCaFormPanel.DuLieu duLieu = form.layDuLieu();

		try {
			this.phanCongService.update(duLieu.maPhanCong(), duLieu.maNhanVien(),
					duLieu.ngayLam(), duLieu.caLamViec(), duLieu.ghiChu());
			ThongBao.thongTin(this, "app.schedule.assignmentUpdated");
			return true;
		} catch (IllegalArgumentException | IllegalStateException ex) {
			ThongBao.loi(this, ex);
		} catch (SQLException ex) {
			LOGGER.log(Level.SEVERE, "Khong the cap nhat phan cong ca", ex);
			ThongBao.loi(this, "app.login.errorSystem");
		}

		return false;
	}

	private void xoaPhanCong(LuoiLichTuan.MucPhanCong muc) {
		if (muc.maPhanCong().isBlank()) {
			ThongBao.canhBao(this, "app.schedule.assignmentRequired");
			return;
		}

		if (!ThongBao.xacNhan(this, "app.schedule.confirmDelete", "app.common.confirm")) {
			return;
		}

		try {
			this.phanCongService.delete(muc.maPhanCong());
			ThongBao.thongTin(this, "app.schedule.assignmentDeleted");
			this.taiLai();
		} catch (IllegalArgumentException | IllegalStateException ex) {
			ThongBao.loi(this, ex);
		} catch (SQLException ex) {
			LOGGER.log(Level.SEVERE, "Khong the xoa phan cong ca", ex);
			ThongBao.loi(this, "app.login.errorSystem");
		}
	}

	private List<NhanVien> layDanhSachNhanVien() {
		try {
			return this.phanCongService.getNhanVienCoThePhanCong();
		} catch (SQLException ex) {
			LOGGER.log(Level.SEVERE, "Khong the tai danh sach nhan vien", ex);
			ThongBao.loi(this, "app.login.errorSystem");
			return List.of();
		}
	}

	private void boSungNhanVienDangPhanCong(List<NhanVien> danhSach, PhanCong phanCong) throws SQLException {
		boolean daCo = danhSach.stream()
				.anyMatch(nhanVien -> nhanVien.getMaNhanVien() != null
						&& nhanVien.getMaNhanVien().equals(phanCong.getMaNhanVien()));

		if (daCo) {
			return;
		}

		Optional<NhanVien> nhanVien = this.phanCongService.getNhanVienById(phanCong.getMaNhanVien());
		nhanVien.ifPresent(danhSach::add);
	}

	@Override
	public MenuGroup getGroup() {
		return MenuGroup.KE_HOACH;
	}

	@Override
	public String getTitleKey() {
		return "app.menu.shiftAssignment";
	}
}
