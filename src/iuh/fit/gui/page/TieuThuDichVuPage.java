
package iuh.fit.gui.page;

import iuh.fit.dto.BaoCaoTieuThuDichVuDTO;
import iuh.fit.dto.TieuThuDichVuBaoCaoDTO;
import iuh.fit.enumeration.MenuGroup;
import iuh.fit.gui.Theme;
import iuh.fit.gui.component.BaoCaoUi;
import iuh.fit.gui.component.ThongBao;
import iuh.fit.service.TieuThuDichVuService;
import iuh.fit.util.DateFormats;
import iuh.fit.util.I18nReaderUtil;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.YearMonth;
import java.util.List;
import java.util.Locale;

public class TieuThuDichVuPage extends AbstractPage {

	

	private final TieuThuDichVuService tieuThuDichVuService = new TieuThuDichVuService();

	private final JComboBox<String> cmbThang = new JComboBox<>();

	private final JLabel lblTongLuot = new JLabel("0");
	private final JLabel lblDoanhThu = new JLabel("0");
	private final JLabel lblDichVuPhoBien = new JLabel("-");
	private final JLabel lblSoDichVu = new JLabel("0");

	private final DefaultTableModel modelDichVu = new DefaultTableModel(new Object[0][6], new String[6]) {

		

		@Override
		public boolean isCellEditable(int row, int column) {
			return false;
		}
	};

	private final JTable tblDichVu = new JTable(this.modelDichVu);

	private final NumberFormat dinhDangSo = NumberFormat.getNumberInstance(new Locale("vi", "VN"));

	public TieuThuDichVuPage() {
		this.setLayout(new BorderLayout(8, 8));
		this.setBorder(Theme.leDeu(8));

		this.dinhDangSo.setMaximumFractionDigits(0);
		this.dinhDangSo.setMinimumFractionDigits(0);
	}

	@Override
	public void render() {
		this.removeAll();

		this.khoiTaoThang();
		this.capNhatTieuDeBang();
		this.cauHinhBang();

		this.add(this.taoBoLoc(), BorderLayout.NORTH);
		this.add(this.taoNoiDung(), BorderLayout.CENTER);
		this.add(BaoCaoUi.thanhCongCu("app.report.revenue.print", this::inBaoCao,
				"app.report.service.export", this::xuatBaoCao), BorderLayout.SOUTH);

		this.clearActionListeners(this.cmbThang);
		this.cmbThang.addActionListener(e -> this.taiBaoCao());

		this.revalidate();
		this.repaint();

		this.taiBaoCao();
	}

	private void khoiTaoThang() {
		if (this.cmbThang.getItemCount() > 0) {
			return;
		}

		YearMonth thangHienTai = YearMonth.now();

		for (int i = 0; i < 12; i++) {
			this.cmbThang.addItem(thangHienTai.minusMonths(i).format(DateFormats.THANG_NAM));
		}

		this.cmbThang.setSelectedIndex(0);
		this.cmbThang.setPreferredSize(new Dimension(150, 28));
	}

	private YearMonth layThangDuocChon() {
		String thang = (String) this.cmbThang.getSelectedItem();

		if (thang == null || thang.trim().isEmpty()) {
			throw new IllegalArgumentException("Chưa chọn tháng báo cáo.");
		}

		return YearMonth.parse(thang, DateFormats.THANG_NAM);
	}

	private void capNhatTieuDeBang() {
		this.modelDichVu.setColumnIdentifiers(new Object[] { I18nReaderUtil.get("app.report.service.code"),
				I18nReaderUtil.get("app.report.service.name"), I18nReaderUtil.get("app.report.service.count"),
				I18nReaderUtil.get("app.report.service.price"), I18nReaderUtil.get("app.report.service.revenue"),
				I18nReaderUtil.get("app.report.service.ratio") });
	}

	private void cauHinhBang() {
		this.tblDichVu.setRowHeight(25);
		this.tblDichVu.setAutoCreateRowSorter(true);
		this.tblDichVu.getTableHeader().setReorderingAllowed(false);
		this.tblDichVu.setShowGrid(true);
		this.tblDichVu.setFillsViewportHeight(true);

		DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
		renderer.setHorizontalAlignment(SwingConstants.RIGHT);

		this.tblDichVu.getColumnModel().getColumn(2).setCellRenderer(renderer);
		this.tblDichVu.getColumnModel().getColumn(3).setCellRenderer(renderer);
		this.tblDichVu.getColumnModel().getColumn(4).setCellRenderer(renderer);
		this.tblDichVu.getColumnModel().getColumn(5).setCellRenderer(renderer);

		this.tblDichVu.getColumnModel().getColumn(0).setPreferredWidth(150);
		this.tblDichVu.getColumnModel().getColumn(1).setPreferredWidth(360);
		this.tblDichVu.getColumnModel().getColumn(2).setPreferredWidth(130);
		this.tblDichVu.getColumnModel().getColumn(3).setPreferredWidth(150);
		this.tblDichVu.getColumnModel().getColumn(4).setPreferredWidth(220);
		this.tblDichVu.getColumnModel().getColumn(5).setPreferredWidth(130);
	}

	private JPanel taoBoLoc() {
		JPanel pnl = new JPanel(new BorderLayout(8, 0));

		pnl.setBorder(Theme.khungLoc());

		JPanel pnlThang = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));

		pnlThang.add(new JLabel(I18nReaderUtil.get("app.report.service.month")));
		pnlThang.add(this.cmbThang);

		pnl.add(pnlThang, BorderLayout.WEST);

		return pnl;
	}

	private JPanel taoNoiDung() {
		JPanel pnl = new JPanel(new BorderLayout(0, 8));

		JPanel pnlTongQuan = new JPanel(new GridLayout(1, 4, 8, 8));

		pnlTongQuan.add(BaoCaoUi.theThongKe("app.report.service.totalUses", this.lblTongLuot));
		pnlTongQuan.add(BaoCaoUi.theThongKe("app.report.service.totalRevenue", this.lblDoanhThu));
		pnlTongQuan.add(BaoCaoUi.theThongKe("app.report.service.mostUsed", this.lblDichVuPhoBien));
		pnlTongQuan.add(BaoCaoUi.theThongKe("app.report.service.distinctCount", this.lblSoDichVu));

		JPanel pnlBang = new JPanel(new BorderLayout());
		pnlBang.setBorder(Theme.titledBorder("app.report.service.top20"));
		pnlBang.add(new JScrollPane(this.tblDichVu), BorderLayout.CENTER);

		pnl.add(pnlTongQuan, BorderLayout.NORTH);
		pnl.add(pnlBang, BorderLayout.CENTER);

		return pnl;
	}

	private void taiBaoCao() {
		try {
			YearMonth thang = this.layThangDuocChon();

			BaoCaoTieuThuDichVuDTO baoCao = this.tieuThuDichVuService.taoBaoCao(thang);

			if (baoCao == null) {
				this.xoaDuLieuBaoCao();
				return;
			}

			this.lblTongLuot.setText(this.dinhDangSo.format(baoCao.tongLuotSuDung()));

			this.lblDoanhThu.setText(this.dinhDangSo.format(baoCao.tongDoanhThu()));

			String tenDichVu = baoCao.tenDichVuPhoBien();

			this.lblDichVuPhoBien.setText(tenDichVu == null || tenDichVu.trim().isEmpty() ? "-" : tenDichVu);

			this.lblSoDichVu.setText(this.dinhDangSo.format(baoCao.soDichVuDuocSuDung()));

			this.capNhatDuLieuBang(baoCao.danhSachDichVu());

		} catch (IllegalArgumentException ex) {
			this.xoaDuLieuBaoCao();

			ThongBao.canhBao(this, ex.getMessage());

		} catch (SQLException ex) {
			this.xoaDuLieuBaoCao();

			ThongBao.loi(this, "app.report.service.loadError");
		}
	}

	private void capNhatDuLieuBang(List<TieuThuDichVuBaoCaoDTO> danhSachDichVu) {

		this.modelDichVu.setRowCount(0);

		if (danhSachDichVu == null || danhSachDichVu.isEmpty()) {
			return;
		}

		double tongDoanhThuBang = 0;

		for (TieuThuDichVuBaoCaoDTO dichVu : danhSachDichVu) {
			tongDoanhThuBang += dichVu.doanhThu();
		}

		for (TieuThuDichVuBaoCaoDTO dichVu : danhSachDichVu) {
			double doanhThu = dichVu.doanhThu();

			double tyLe = tongDoanhThuBang > 0 ? doanhThu * 100.0 / tongDoanhThuBang : 0;

			this.modelDichVu.addRow(new Object[] { dichVu.maDichVu(), dichVu.tenDichVu(), dichVu.soLuotSuDung(), "-",
					this.dinhDangSo.format(doanhThu), new DecimalFormat("0.##").format(tyLe) + "%" });
		}
	}

	private void xoaDuLieuBaoCao() {
		this.lblTongLuot.setText("0");
		this.lblDoanhThu.setText("0");
		this.lblDichVuPhoBien.setText("-");
		this.lblSoDichVu.setText("0");
		this.modelDichVu.setRowCount(0);
	}

	private void inBaoCao() {
		BaoCaoUi.in(this.tblDichVu, "app.report.revenue.noData");
	}

	private void xuatBaoCao() {
		BaoCaoUi.xuat(this, this.modelDichVu, "BaoCaoTieuThuDichVu.xlsx",
				"app.report.service.title", "app.report.revenue.noData");
	}

	@Override
	public String getTitleKey() {
		return "app.menu.serviceConsumptionReport";
	}

	@Override
	public MenuGroup getGroup() {
		return MenuGroup.TONG_KET;
	}
}
