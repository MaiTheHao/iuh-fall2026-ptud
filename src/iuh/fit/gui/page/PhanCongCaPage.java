package iuh.fit.gui.page;

import iuh.fit.dto.BaseFormDialogConfig;
import iuh.fit.entity.NhanVien;
import iuh.fit.entity.PhanCong;
import iuh.fit.enumeration.CaLamViec;
import iuh.fit.enumeration.MenuGroup;
import iuh.fit.gui.component.BaseFormDialog;
import iuh.fit.gui.component.ManagementPanel;
import iuh.fit.gui.component.ThaoTac;
import iuh.fit.gui.component.ThongBao;
import iuh.fit.gui.form.PhanCongCaFormPanel;
import iuh.fit.gui.form.PhanCongCaSuaFormPanel;
import iuh.fit.gui.form.PhanCongCaTaoFormPanel;
import iuh.fit.service.PhanCongService;
import iuh.fit.util.I18nReaderUtil;

import javax.swing.BorderFactory;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;

import java.awt.BorderLayout;
import java.awt.Component;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PhanCongCaPage extends AbstractPage {

	private static final long serialVersionUID = 1L;
	private static final int PAGE_SIZE = 20;
	private static final Logger LOGGER = Logger.getLogger(PhanCongCaPage.class.getName());

	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

	/** Dialog tạo mới: 4 trường nhập. */
	private static final int CHIEU_CAO_TAO = 420;

	/** Dialog cập nhật: 5 trường nhập. */
	private static final int CHIEU_CAO_SUA = 500;

	private ManagementPanel managementPanel;

	private final PhanCongService phanCongService = new PhanCongService();

	private JTextField boLocMa;
	private JTextField boLocNhanVien;
	private ManagementPanel.BoLocNgay boLocNgay;
	private ManagementPanel.BoLocChon boLocCa;
	private JTextField boLocGhiChu;

	private int trangHienTai = 1;

	public PhanCongCaPage() {
		this.setLayout(new BorderLayout());
	}

	@Override
	public void render() {
		this.removeAll();
		this.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

		this.managementPanel = new ManagementPanel();
		this.trangHienTai = 1;

		this.khoiTaoDanhSach();
		this.ganSuKien();
		this.taiLaiDanhSach();

		this.add(this.managementPanel, BorderLayout.CENTER);

		this.revalidate();
		this.repaint();
	}

	private void khoiTaoDanhSach() {
		this.managementPanel.setListTitle("app.schedule.assignmentList");

		this.managementPanel.setHeaders(I18nReaderUtil.get("app.schedule.assignmentId"),
				I18nReaderUtil.get("app.schedule.employee"), I18nReaderUtil.get("app.schedule.date"),
				I18nReaderUtil.get("app.schedule.shift"), I18nReaderUtil.get("app.schedule.note"));

		this.managementPanel.addActionColumn("app.common.actions",
				new ThaoTac("pencil", "app.common.edit", ThaoTac.KieuThaoTac.THUONG,
						this::moDialogSua),
				new ThaoTac("trash-2", "app.common.delete", ThaoTac.KieuThaoTac.NGUY_HIEM,
						this::xoaPhanCong));

		this.managementPanel.setAddAction("app.schedule.addAssignment", this::moDialogTao);
		this.managementPanel.setRowDoubleClickListener(this::moDialogSua);

		this.boLocMa = this.managementPanel.addTextFilter("app.schedule.assignmentId");
		this.boLocNhanVien = this.managementPanel.addTextFilter("app.schedule.employee");
		this.boLocNgay = this.managementPanel.addDateRangeFilter("app.schedule.date");
		this.boLocCa = this.managementPanel.addComboBoxFilter("app.schedule.shift", CaLamViec.values());
		this.boLocGhiChu = this.managementPanel.addTextFilter("app.schedule.note");

		this.managementPanel.setFilterChangeListener(() -> {
			this.trangHienTai = 1;
			this.taiLaiDanhSach();
		});

		JTable bang = this.managementPanel.getTable();

		DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
			private static final long serialVersionUID = 1L;

			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {

				Object giaTriHienThi = value;

				if (value instanceof LocalDate ngay) {
					giaTriHienThi = ngay.format(DATE_FORMATTER);
				}

				return super.getTableCellRendererComponent(table, giaTriHienThi, isSelected, hasFocus, row, column);
			}
		};

		bang.getColumnModel().getColumn(2).setCellRenderer(renderer);
	}

	private void ganSuKien() {
		this.managementPanel.getClearFilterButton().addActionListener(e -> this.managementPanel.clearFilters());

		this.managementPanel.getPrevButton().addActionListener(e -> {
			if (this.trangHienTai > 1) {
				this.trangHienTai--;
				this.taiLaiDanhSach();
			}
		});

		this.managementPanel.getNextButton().addActionListener(e -> {
			this.trangHienTai++;
			this.taiLaiDanhSach();
		});
	}

	private void moDialogTao() {
		List<NhanVien> danhSachNhanVien = this.layDanhSachNhanVien();

		if (danhSachNhanVien.isEmpty()) {
			ThongBao.canhBao(this, "app.schedule.chuaCoNhanVienPhuHop");
			return;
		}

		PhanCongCaTaoFormPanel form = new PhanCongCaTaoFormPanel(danhSachNhanVien);

		BaseFormDialog dialog = new BaseFormDialog(new BaseFormDialogConfig(
				SwingUtilities.getWindowAncestor(this),
				"app.schedule.addAssignment",
				form,
				() -> this.taoPhanCong(form),
				CHIEU_CAO_TAO));

		if (dialog.showAndGet()) {
			this.trangHienTai = 1;
			this.taiLaiDanhSach();
		}
	}

	private void moDialogSua(int row) {
		String maPhanCong = this.layMaDong(row);

		if (maPhanCong.isBlank()) {
			return;
		}

		try {
			Optional<PhanCong> ketQua = this.phanCongService.getById(maPhanCong);

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
				this.taiLaiDanhSach();
			}
		} catch (SQLException ex) {
			LOGGER.log(Level.SEVERE, "Khong the tai phan cong ca", ex);
			ThongBao.loi(this, "app.login.errorSystem");
		}
	}

	private boolean taoPhanCong(PhanCongCaFormPanel form) {
		if (!form.validateInput()) {
			return false;
		}

		PhanCongCaFormPanel.DuLieu duLieu = form.layDuLieu();

		try {
			this.phanCongService.create(duLieu.maNhanVien(), duLieu.ngayLam(),
					duLieu.caLamViec(), duLieu.ghiChu());
			ThongBao.thongTin(this, "app.schedule.assignmentCreated");
			return true;
		} catch (IllegalArgumentException | IllegalStateException ex) {
			ThongBao.loi(this, ex);
		} catch (SQLException ex) {
			LOGGER.log(Level.SEVERE, "Khong the tao phan cong ca", ex);
			ThongBao.loi(this, "app.login.errorSystem");
		}

		return false;
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

	private void xoaPhanCong(int row) {
		String maPhanCong = this.layMaDong(row);

		if (maPhanCong.isBlank()) {
			ThongBao.canhBao(this, "app.schedule.assignmentRequired");
			return;
		}

		if (!ThongBao.xacNhan(this, "app.schedule.confirmDelete", "app.common.confirm")) {
			return;
		}

		try {
			this.phanCongService.delete(maPhanCong);
			ThongBao.thongTin(this, "app.schedule.assignmentDeleted");
			this.taiLaiDanhSach();
		} catch (IllegalArgumentException | IllegalStateException ex) {
			ThongBao.loi(this, ex);
		} catch (SQLException ex) {
			LOGGER.log(Level.SEVERE, "Khong the xoa phan cong ca", ex);
			ThongBao.loi(this, "app.login.errorSystem");
		}
	}

	private String layMaDong(int row) {
		if (row < 0 || row >= this.managementPanel.getModel().getRowCount()) {
			return "";
		}

		return String.valueOf(this.managementPanel.getModel().getValueAt(row, 0));
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

	private void taiLaiDanhSach() {
		try {
			Object giaTriCa = this.boLocCa.giaTri();
			CaLamViec caLamViec = giaTriCa instanceof CaLamViec ca ? ca : null;

			List<PhanCong> danhSach = this.phanCongService.search(this.boLocMa.getText(), this.boLocNhanVien.getText(),
					this.boLocNgay.tuNgay(), this.boLocNgay.denNgay(), caLamViec, this.boLocGhiChu.getText(),
					this.managementPanel.getQuickFilter());

			int tongSoDong = danhSach.size();
			int tongSoTrang = Math.max(1, (int) Math.ceil((double) tongSoDong / PAGE_SIZE));

			if (this.trangHienTai > tongSoTrang) {
				this.trangHienTai = tongSoTrang;
			}

			if (this.trangHienTai < 1) {
				this.trangHienTai = 1;
			}

			int viTriBatDau = (this.trangHienTai - 1) * PAGE_SIZE;
			int viTriKetThuc = Math.min(viTriBatDau + PAGE_SIZE, tongSoDong);

			List<PhanCong> trangDuLieu = tongSoDong == 0 ? List.of() : danhSach.subList(viTriBatDau, viTriKetThuc);

			Map<String, String> tenNhanVienTheoMa = new HashMap<>();

			for (NhanVien nhanVien : this.layDanhSachNhanVien()) {
				tenNhanVienTheoMa.put(nhanVien.getMaNhanVien(), nhanVien.getHoTen() == null ? "" : nhanVien.getHoTen());
			}

			List<Object[]> duLieu = new ArrayList<>();

			for (PhanCong phanCong : trangDuLieu) {
				LocalDate ngayLam = LocalDate.of(phanCong.getNam(), phanCong.getThang(), phanCong.getNgay());

				duLieu.add(new Object[] { phanCong.getMaPhanCong(),
						tenNhanVienTheoMa.getOrDefault(phanCong.getMaNhanVien(), phanCong.getMaNhanVien()), ngayLam,
						phanCong.getCaLamViec() == null ? "" : phanCong.getCaLamViec().getI18NBusinessName(),
						phanCong.getGhiChu() == null ? "" : phanCong.getGhiChu() });
			}

			this.managementPanel.setRows(duLieu);

			long tuDong = tongSoDong == 0 ? 0 : viTriBatDau + 1L;
			long denDong = viTriKetThuc;

			this.managementPanel.setCount(tuDong, denDong, tongSoDong);

			this.managementPanel.setPage(this.trangHienTai, tongSoTrang);

		} catch (SQLException ex) {
			LOGGER.log(Level.SEVERE, "Khong the tai danh sach phan cong ca", ex);
			ThongBao.loi(this, "app.login.errorSystem");
		}
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
