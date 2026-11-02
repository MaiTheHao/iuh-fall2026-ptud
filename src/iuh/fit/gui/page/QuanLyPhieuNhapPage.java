package iuh.fit.gui.page;

import iuh.fit.dto.BaseFormDialogConfig;
import iuh.fit.dto.DanhSachCriteria;
import iuh.fit.dto.PaginatedResult;
import iuh.fit.entity.PhieuNhapPhuTung;
import iuh.fit.enumeration.MenuGroup;
import iuh.fit.gui.component.BaseFormDialog;
import iuh.fit.gui.component.ManagementPanel;
import iuh.fit.gui.component.ThaoTac;
import iuh.fit.gui.component.ThongBao;
import iuh.fit.gui.form.PhieuNhapChiTietFormPanel;
import iuh.fit.gui.theme.Theme;
import iuh.fit.service.PhieuNhapPhuTungService;
import iuh.fit.util.I18nReaderUtil;

import javax.swing.BorderFactory;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class QuanLyPhieuNhapPage extends AbstractPage {

	/** Dialog xem chi tiết phiếu nhập: 5 trường đầu phiếu + bảng dòng chi tiết. */
	private static final int CHIEU_CAO_CHI_TIET = 650;

	private static final int SO_DONG_MOI_TRANG = 20;

	private final PhieuNhapPhuTungService phieuNhapService = new PhieuNhapPhuTungService();
	private final DecimalFormat currencyFormat = new DecimalFormat("#,##0");
	private final DateTimeFormatter dateFormatter = DateTimeFormatter
			.ofPattern(I18nReaderUtil.get("app.date.format") + " HH:mm");

	private ManagementPanel managementPanel;
	private List<PhieuNhapPhuTung> danhSachHienTai = new ArrayList<>();
	private int trang = 1;

	private JTextField boLocMaPhieuNhap;
	private JTextField boLocMoTa;
	private JTextField boLocThuKho;
	private ManagementPanel.BoLocNgay boLocNgayTao;
	private ManagementPanel.BoLocKhoang boLocTongChiPhi;

	public QuanLyPhieuNhapPage() {
		this.setLayout(new BorderLayout());
	}

	@Override
	public void render() {
		this.removeAll();
		this.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
		this.setBackground(Theme.BG);

		this.managementPanel = new ManagementPanel();
		this.managementPanel.setListTitle("app.phieuNhap.danhSachTitle");

		this.managementPanel.setHeaders(
				"#",
				I18nReaderUtil.get("app.phieuNhap.maPhieuNhap"),
				I18nReaderUtil.get("app.phieuNhap.moTa"),
				I18nReaderUtil.get("app.phieuNhap.thuKho"),
				I18nReaderUtil.get("app.phieuNhap.ngayTao"),
				I18nReaderUtil.get("app.phieuNhap.tongChiPhi")
		);

		// Phiếu nhập đã tạo là chứng từ kho: chỉ xem chi tiết, không sửa/xóa.
		this.managementPanel.addActionColumn("app.common.actions",
				new ThaoTac("eye", "app.common.view", ThaoTac.KieuThaoTac.THUONG,
						this::xemChiTiet));

		this.managementPanel.setAddAction("app.phieuNhap.taoPhieuMoi",
				() -> this.goTo(LapPhieuNhapPage.class));
		this.managementPanel.setRowDoubleClickListener(this::xemChiTiet);

		this.boLocMaPhieuNhap = this.managementPanel.addTextFilter("app.phieuNhap.maPhieuNhap");
		this.boLocMoTa = this.managementPanel.addTextFilter("app.phieuNhap.moTa");
		this.boLocThuKho = this.managementPanel.addTextFilter("app.phieuNhap.thuKho");
		this.boLocNgayTao = this.managementPanel.addDateRangeFilter("app.phieuNhap.ngayTao");
		this.boLocTongChiPhi = this.managementPanel.addRangeFilter("app.phieuNhap.tongChiPhi");

		this.managementPanel.setFilterChangeListener(() -> this.taiLai(1));
		this.managementPanel.getClearFilterButton().addActionListener(
				e -> this.managementPanel.clearFilters());
		this.managementPanel.getPrevButton().addActionListener(
				e -> this.taiLai(this.trang - 1));
		this.managementPanel.getNextButton().addActionListener(
				e -> this.taiLai(this.trang + 1));

		JTable table = this.managementPanel.getTable();
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		table.getColumnModel().getColumn(0).setPreferredWidth(50);
		table.getColumnModel().getColumn(1).setPreferredWidth(200);
		table.getColumnModel().getColumn(2).setPreferredWidth(320);
		table.getColumnModel().getColumn(3).setPreferredWidth(150);
		table.getColumnModel().getColumn(4).setPreferredWidth(150);
		table.getColumnModel().getColumn(5).setPreferredWidth(160);
		table.setRowHeight(44);

		this.add(this.managementPanel, BorderLayout.CENTER);

		this.taiLai(1);

		this.revalidate();
		this.repaint();
	}

	private void taiLai(int page) {
		try {
			DanhSachCriteria criteria = new DanhSachCriteria() {
				@Override
				public int page() {
					return page;
				}

				@Override
				public int pageSize() {
					return SO_DONG_MOI_TRANG;
				}
			};

			Double minCost = this.parseCost(this.boLocTongChiPhi.tuText());
			Double maxCost = this.parseCost(this.boLocTongChiPhi.denText());

			if ((minCost != null && minCost.isNaN()) || (maxCost != null && maxCost.isNaN())) {
				ThongBao.canhBao(this, "app.phieuNhap.loiKhoangChiPhi");
				return;
			}

			if (minCost != null && maxCost != null && minCost > maxCost) {
				return;
			}

			PaginatedResult<PhieuNhapPhuTung> res = this.phieuNhapService.search(
					criteria,
					this.managementPanel.getQuickFilter(),
					this.boLocMaPhieuNhap.getText(),
					this.boLocThuKho.getText(),
					this.boLocMoTa.getText(),
					this.boLocNgayTao.tuNgay(),
					this.boLocNgayTao.denNgay(),
					minCost,
					maxCost
			);

			List<Object[]> rows = new ArrayList<>(res.items().size());
			int stt = res.fromIndex();

			for (PhieuNhapPhuTung item : res.items()) {
				rows.add(new Object[]{
						stt++,
						item.getMaPhieuNhap(),
						item.getMoTa(),
						item.getMaNhanVien(),
						item.getNgayTao() != null ? item.getNgayTao().format(this.dateFormatter) : "",
						this.currencyFormat.format(this.phieuNhapService.tinhTongChiPhi(item))
				});
			}

			this.danhSachHienTai = res.items();

			this.managementPanel.setRows(rows);
			this.managementPanel.setCount(res.fromIndex(), res.toIndex(), res.totalItems());
			this.managementPanel.setPage(res.page(), Math.max(res.totalPages(), 1));
			this.trang = res.page();
		} catch (SQLException e) {
			ThongBao.loi(this, "app.login.errorSystem");
		}
	}

	private Double parseCost(String val) {
		if (val == null || val.isBlank()) {
			return null;
		}
		if (!val.trim().matches("\\d+")) {
			return Double.NaN;
		}
		return Double.valueOf(val.trim());
	}

	private void xemChiTiet(int row) {
		if (row < 0 || row >= this.danhSachHienTai.size()) {
			return;
		}

		PhieuNhapPhuTung phieu = this.danhSachHienTai.get(row);

		PhieuNhapChiTietFormPanel form = new PhieuNhapChiTietFormPanel();
		form.napDuLieu(
				phieu,
				phieu.getNgayTao() == null ? "" : phieu.getNgayTao().format(this.dateFormatter),
				this.currencyFormat.format(this.phieuNhapService.tinhTongChiPhi(phieu)));

		BaseFormDialog dialog = new BaseFormDialog(new BaseFormDialogConfig(
				SwingUtilities.getWindowAncestor(this),
				"app.phieuNhap.thongTinTitle",
				form,
				null,
				CHIEU_CAO_CHI_TIET,
				true));
		dialog.showAndGet();
	}

	@Override
	public String getTitleKey() {
		return "app.menu.partsReceiptManagement";
	}

	@Override
	public MenuGroup getGroup() {
		return MenuGroup.NHAP_PHU_TUNG;
	}
}
