package iuh.fit.gui.page;

import iuh.fit.dto.DanhSachHoaDonCriteria;
import iuh.fit.dto.HoaDonView;
import iuh.fit.dto.PaginatedResult;
import iuh.fit.enumeration.MenuGroup;
import iuh.fit.enumeration.TrangThaiHoaDon;
import iuh.fit.gui.component.ManagementFormPanel;
import iuh.fit.gui.component.ManagementPanel;
import iuh.fit.gui.component.ThaoTac;
import iuh.fit.gui.component.ThongBao;
import iuh.fit.gui.theme.Theme;
import iuh.fit.service.HoaDonService;
import iuh.fit.util.I18nReaderUtil;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class QuanLyHoaDonPage extends AbstractPage {

	private static final int SO_DONG_MOI_TRANG = 20;

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

	private final JTextField maHoaDonField = new JTextField();
	private final JTextField maPhieuField = new JTextField();
	private final JTextField khachHangField = new JTextField();
	private final JTextField bienSoField = new JTextField();
	private final JTextField ngayTaoField = new JTextField();
	private final JTextField trangThaiField = new JTextField();
	private final JTextField tongTienField = new JTextField();
	private final JTextField thoiGianTTField = new JTextField();
	private final JTextField ghiChuField = new JTextField();
	private final JTextField lyDoHuyField = new JTextField();

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
				new ThaoTac("ban", "app.hoaDon.huyHoaDon", ThaoTac.KieuThaoTac.NGUY_HIEM,
						row -> this.huyHoaDon(panel, row)));

		this.boLocMaHoaDon = panel.addTextFilter("app.hoaDon.maHoaDon");
		this.boLocMaPhieu = panel.addTextFilter("app.hoaDon.maPhieuTiepNhan");
		this.boLocKhachHang = panel.addTextFilter("app.khachHang.hoTen");
		this.boLocNgayTao = panel.addDateRangeFilter("app.hoaDon.ngayTao");
		this.boLocTrangThai = panel.addComboBoxFilter("app.hoaDon.trangThai", TrangThaiHoaDon.values());
		this.boLocTongTien = panel.addRangeFilter("app.hoaDon.tongTien");

		ManagementFormPanel form = new ManagementFormPanel("app.hoaDon.thongTinTitle");
		Theme.applyReadOnly(this.maHoaDonField);
		Theme.applyReadOnly(this.maPhieuField);
		Theme.applyReadOnly(this.khachHangField);
		Theme.applyReadOnly(this.bienSoField);
		Theme.applyReadOnly(this.ngayTaoField);
		Theme.applyReadOnly(this.trangThaiField);
		Theme.applyReadOnly(this.tongTienField);
		Theme.applyReadOnly(this.thoiGianTTField);
		Theme.applyReadOnly(this.ghiChuField);
		Theme.applyReadOnly(this.lyDoHuyField);

		form.addField("app.hoaDon.maHoaDon", this.maHoaDonField);
		form.addField("app.hoaDon.maPhieuTiepNhan", this.maPhieuField);
		form.addField("app.khachHang.hoTen", this.khachHangField);
		form.addField("app.xe.bienSo", this.bienSoField);
		form.addField("app.hoaDon.ngayTao", this.ngayTaoField);
		form.addField("app.hoaDon.trangThai", this.trangThaiField);
		form.addField("app.hoaDon.tongTien", this.tongTienField);
		form.addField("app.hoaDon.thoiGianThanhToan", this.thoiGianTTField);
		form.addField("app.hoaDon.ghiChu", this.ghiChuField);
		form.addField("app.hoaDon.lyDoHuy", this.lyDoHuyField);

		JButton taoMoi = new JButton(I18nReaderUtil.get("app.hoaDon.taoMoi"));
		Theme.applyPrimary(taoMoi);
		taoMoi.addActionListener(e -> this.goTo(LapHoaDonPage.class));
		form.thayNut(taoMoi);

		panel.getTable().getSelectionModel().addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting()) {
				this.chonDong(panel);
			}
		});

		panel.setFilterChangeListener(() -> this.taiLai(panel, 1));
		panel.getClearFilterButton().addActionListener(e -> panel.clearFilters());
		panel.getPrevButton().addActionListener(e -> this.taiLai(panel, this.trang - 1));
		panel.getNextButton().addActionListener(e -> this.taiLai(panel, this.trang + 1));

		JTable table = panel.getTable();
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		table.setRowHeight(42);

		this.taiLai(panel, 1);

		JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, form, panel);
		split.setDividerLocation(480);
		split.setOpaque(false);
		split.setBorder(null);
		this.add(split, BorderLayout.CENTER);

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
			this.xoaTrangForm();
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

	private void chonDong(ManagementPanel panel) {
		int row = panel.getSelectedRow();
		if (row < 0 || row >= this.danhSachHienTai.size()) {
			return;
		}
		HoaDonView view = this.danhSachHienTai.get(row);
		this.maHoaDonField.setText(view.maHoaDon());
		this.maPhieuField.setText(view.maPhieuTiepNhan());
		this.khachHangField.setText(view.tenKhachHang());
		this.bienSoField.setText(view.bienSo());
		this.ngayTaoField.setText(view.ngayTao() == null ? "" : view.ngayTao().format(this.dateTimeFormat));
		this.trangThaiField.setText(view.trangThai() == null ? "" : view.trangThai().getI18NBusinessName());
		this.tongTienField.setText(this.currencyFormat.format(view.tongTien()));
		this.thoiGianTTField.setText(view.thoiGianThanhToan() == null
				? ""
				: view.thoiGianThanhToan().format(this.dateTimeFormat));
		this.ghiChuField.setText(view.ghiChu() == null ? "" : view.ghiChu());
		this.lyDoHuyField.setText(view.lyDoHuy() == null ? "" : view.lyDoHuy());
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
		if (view.trangThai() != TrangThaiHoaDon.CHO_THANH_TOAN) {
			ThongBao.canhBao(this, "app.hoaDon.loiTrangThai");
			return;
		}
		if (!ThongBao.xacNhan(this, "app.hoaDon.xacNhanHuy", "app.hoaDon.huyHoaDon")) {
			return;
		}

		String lyDo = JOptionPane.showInputDialog(this,
				I18nReaderUtil.get("app.hoaDon.lyDoHuy"),
				I18nReaderUtil.get("app.hoaDon.huyHoaDon"),
				JOptionPane.QUESTION_MESSAGE);
		if (lyDo == null) {
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

	private void xoaTrangForm() {
		this.maHoaDonField.setText("");
		this.maPhieuField.setText("");
		this.khachHangField.setText("");
		this.bienSoField.setText("");
		this.ngayTaoField.setText("");
		this.trangThaiField.setText("");
		this.tongTienField.setText("");
		this.thoiGianTTField.setText("");
		this.ghiChuField.setText("");
		this.lyDoHuyField.setText("");
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
