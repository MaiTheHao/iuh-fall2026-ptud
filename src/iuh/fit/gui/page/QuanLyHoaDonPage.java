package iuh.fit.gui.page;

import iuh.fit.dto.BaseFormDialogConfig;
import iuh.fit.dto.DanhSachHoaDonCriteria;
import iuh.fit.dto.HoaDonView;
import iuh.fit.dto.PaginatedResult;
import iuh.fit.enumeration.MenuGroup;
import iuh.fit.enumeration.TrangThaiHoaDon;
import iuh.fit.gui.component.BaseFormDialog;
import iuh.fit.gui.component.ManagementPanel;
import iuh.fit.gui.component.ThaoTac;
import iuh.fit.gui.component.ThongBao;
import iuh.fit.gui.form.HoaDonChiTietFormPanel;
import iuh.fit.gui.theme.Theme;
import iuh.fit.service.HoaDonService;
import iuh.fit.util.I18nReaderUtil;

import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class QuanLyHoaDonPage extends AbstractPage {

	private static final int SO_DONG_MOI_TRANG = 20;

	/** Dialog xem chi tiết hóa đơn: 10 trường chỉ đọc. */
	private static final int CHIEU_CAO_CHI_TIET = 800;

	private final HoaDonService hoaDonService = new HoaDonService();
	private final DecimalFormat currencyFormat = new DecimalFormat("#,##0");
	private final DateTimeFormatter dateTimeFormat = DateTimeFormatter
			.ofPattern(I18nReaderUtil.get("app.date.format") + " HH:mm");

	private int trang = 1;
	private List<HoaDonView> danhSachHienTai = new ArrayList<>();

	private JTextField boLocMaHoaDon;
	private JTextField boLocMaPhieu;
	private JTextField boLocKhachHang;
	private ManagementPanel.BoLocNgay boLocNgayTao;
	private ManagementPanel.BoLocChon boLocTrangThai;
	private ManagementPanel.BoLocKhoang boLocTongTien;

	public QuanLyHoaDonPage() {
		this.setLayout(new BorderLayout());
		this.setBackground(Theme.BG);
	}

	@Override
	public void render() {
		this.removeAll();
		this.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
		this.setBackground(Theme.BG);

		ManagementPanel panel = new ManagementPanel();
		panel.setListTitle("app.hoaDon.danhSachTitle");
		panel.setHeaders(
				"#",
				I18nReaderUtil.get("app.hoaDon.maHoaDon"),
				I18nReaderUtil.get("app.hoaDon.maPhieuTiepNhan"),
				I18nReaderUtil.get("app.khachHang.hoTen"),
				I18nReaderUtil.get("app.xe.bienSo"),
				I18nReaderUtil.get("app.hoaDon.ngayTao"),
				I18nReaderUtil.get("app.hoaDon.trangThai"),
				I18nReaderUtil.get("app.hoaDon.tongTien"));

		panel.addActionColumn("app.common.actions",
				new ThaoTac("pencil", "app.common.edit", ThaoTac.KieuThaoTac.THUONG,
						row -> this.moHoaDon(row)),
				new ThaoTac("eye", "app.common.view", ThaoTac.KieuThaoTac.THUONG,
						row -> this.xemChiTiet(row)),
				new ThaoTac("trash-2", "app.hoaDon.huyHoaDon", ThaoTac.KieuThaoTac.NGUY_HIEM,
						row -> this.huyHoaDon(panel, row)));

		panel.setAddAction("app.hoaDon.taoMoi", () -> this.goTo(LapHoaDonPage.class));
		panel.setRowDoubleClickListener(this::xemChiTiet);

		this.boLocMaHoaDon = panel.addTextFilter("app.hoaDon.maHoaDon");
		this.boLocMaPhieu = panel.addTextFilter("app.hoaDon.maPhieuTiepNhan");
		this.boLocKhachHang = panel.addTextFilter("app.khachHang.hoTen");
		this.boLocNgayTao = panel.addDateRangeFilter("app.hoaDon.ngayTao");
		this.boLocTrangThai = panel.addComboBoxFilter("app.hoaDon.trangThai", TrangThaiHoaDon.values());
		this.boLocTongTien = panel.addRangeFilter("app.hoaDon.tongTien");

		panel.setFilterChangeListener(() -> this.taiLai(panel, 1));
		panel.getClearFilterButton().addActionListener(e -> panel.clearFilters());
		panel.getPrevButton().addActionListener(e -> this.taiLai(panel, this.trang - 1));
		panel.getNextButton().addActionListener(e -> this.taiLai(panel, this.trang + 1));

		JTable table = panel.getTable();
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		table.getColumnModel().getColumn(0).setPreferredWidth(50);
		table.getColumnModel().getColumn(1).setPreferredWidth(170);
		table.getColumnModel().getColumn(2).setPreferredWidth(190);
		table.getColumnModel().getColumn(3).setPreferredWidth(180);
		table.getColumnModel().getColumn(4).setPreferredWidth(110);
		table.getColumnModel().getColumn(5).setPreferredWidth(120);
		table.getColumnModel().getColumn(6).setPreferredWidth(140);
		table.getColumnModel().getColumn(7).setPreferredWidth(150);
		table.setRowHeight(42);

		this.taiLai(panel, 1);

		this.add(panel, BorderLayout.CENTER);

		this.revalidate();
		this.repaint();
	}

	private void taiLai(ManagementPanel panel, int page) {
		Double tienTu = this.parseTien(this.boLocTongTien.tuText());
		Double tienDen = this.parseTien(this.boLocTongTien.denText());
		if ((tienTu != null && tienTu.isNaN()) || (tienDen != null && tienDen.isNaN())) {
			ThongBao.canhBao(this, "app.hoaDon.loiKhoangTien");
			return;
		}

		try {
			DanhSachHoaDonCriteria tieuChi = new DanhSachHoaDonCriteria(
					this.boLocMaHoaDon.getText(),
					this.boLocMaPhieu.getText(),
					this.boLocKhachHang.getText(),
					this.boLocTrangThai.giaTri() instanceof TrangThaiHoaDon value ? value : null,
					this.boLocNgayTao.tuNgay(),
					this.boLocNgayTao.denNgay(),
					tienTu,
					tienDen,
					panel.getQuickFilter(),
					Math.max(page, 1),
					SO_DONG_MOI_TRANG);

			PaginatedResult<HoaDonView> ketQua = this.hoaDonService.search(tieuChi);

			List<Object[]> rows = new ArrayList<>(ketQua.items().size());
			int stt = ketQua.fromIndex();
			for (HoaDonView view : ketQua.items()) {
				rows.add(new Object[] {
						stt++,
						view.maHoaDon(),
						view.maPhieuTiepNhan(),
						view.tenKhachHang(),
						view.bienSo(),
						view.ngayTao() != null ? view.ngayTao().format(this.dateTimeFormat) : "",
						view.trangThai() == null ? "" : view.trangThai().getI18NBusinessName(),
						this.currencyFormat.format(view.tongTien())
				});
			}

			this.danhSachHienTai = ketQua.items();
			panel.setRows(rows);
			panel.setCount(ketQua.fromIndex(), ketQua.toIndex(), ketQua.totalItems());
			panel.setPage(ketQua.page(), Math.max(ketQua.totalPages(), 1));
			this.trang = ketQua.page();
		} catch (IllegalArgumentException e) {
			ThongBao.loi(this, e);
		} catch (SQLException e) {
			ThongBao.loi(this, "app.login.errorSystem");
		}
	}

	private Double parseTien(String value) {
		if (value == null || value.isBlank()) {
			return null;
		}
		if (!value.trim().matches("\\d+")) {
			return Double.NaN;
		}
		return Double.valueOf(value.trim());
	}

	private void xemChiTiet(int row) {
		if (row < 0 || row >= this.danhSachHienTai.size()) {
			return;
		}

		HoaDonChiTietFormPanel form = new HoaDonChiTietFormPanel();
		form.napDuLieu(this.danhSachHienTai.get(row));

		BaseFormDialog dialog = new BaseFormDialog(new BaseFormDialogConfig(
				SwingUtilities.getWindowAncestor(this),
				"app.hoaDon.thongTinTitle",
				form,
				null,
				CHIEU_CAO_CHI_TIET,
				true));
		dialog.showAndGet();
	}

	private void moHoaDon(int row) {
		if (row < 0 || row >= this.danhSachHienTai.size()) {
			return;
		}
		LapHoaDonPage page = (LapHoaDonPage) PageRegistry.getInstance().get(LapHoaDonPage.class);
		page.moHoaDon(this.danhSachHienTai.get(row).maHoaDon());
		this.goTo(LapHoaDonPage.class);
	}

	private void huyHoaDon(ManagementPanel panel, int row) {
		if (row < 0 || row >= this.danhSachHienTai.size()) {
			return;
		}
		HoaDonView view = this.danhSachHienTai.get(row);
		if (view.trangThai() == TrangThaiHoaDon.DA_HUY) {
			ThongBao.canhBao(this, "app.hoaDon.loiDaHuy");
			return;
		}

		if (!ThongBao.xacNhan(this, "app.hoaDon.xacNhanHuy", "app.hoaDon.huyHoaDon")) {
			return;
		}

		String lyDo = JOptionPane.showInputDialog(this, I18nReaderUtil.get("app.hoaDon.lyDoHuy"));
		if (lyDo == null || lyDo.isBlank()) {
			return;
		}

		try {
			this.hoaDonService.huy(view.maHoaDon(), lyDo);
			ThongBao.thongTin(this, "app.common.update_succeed");
			this.taiLai(panel, this.trang);
		} catch (IllegalArgumentException | IllegalStateException e) {
			ThongBao.loi(this, e);
		} catch (SQLException e) {
			ThongBao.loi(this, "app.login.errorSystem");
		}
	}

	@Override
	public String getTitleKey() {
		return "app.menu.invoiceManagement";
	}

	@Override
	public MenuGroup getGroup() {
		return MenuGroup.THANH_TOAN;
	}
}
