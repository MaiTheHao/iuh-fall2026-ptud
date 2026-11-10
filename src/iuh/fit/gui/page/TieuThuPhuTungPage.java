package iuh.fit.gui.page;

import iuh.fit.dto.BaoCaoTieuThuPhuTungDTO;
import iuh.fit.dto.TieuThuPhuTungBaoCaoDTO;
import iuh.fit.enumeration.MenuGroup;
import iuh.fit.gui.Theme;
import iuh.fit.gui.component.BaoCaoUi;
import iuh.fit.gui.component.ThongBao;
import iuh.fit.service.TieuThuPhuTungService;
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
import java.text.NumberFormat;
import java.time.YearMonth;
import java.util.Locale;

public class TieuThuPhuTungPage extends AbstractPage {

	

	private final JComboBox<String> cmbThang = new JComboBox<>();

	private final JLabel lblTongNhap = new JLabel("0");
	private final JLabel lblTongXuat = new JLabel("0");
	private final JLabel lblTonKho = new JLabel("0");
	private final JLabel lblTyLeTieuThu = new JLabel("0%");

	private final DefaultTableModel modelPhuTung = new DefaultTableModel(new Object[0][0], new String[8]) {

		

		@Override
		public boolean isCellEditable(int row, int column) {
			return false;
		}
	};

	private final TieuThuPhuTungService tieuThuPhuTungService = new TieuThuPhuTungService();

	private final JTable tblPhuTung = new JTable(this.modelPhuTung);

	private final NumberFormat dinhDangSo = NumberFormat.getNumberInstance(Locale.forLanguageTag("vi-VN"));

	private final NumberFormat dinhDangTien = NumberFormat.getNumberInstance(Locale.forLanguageTag("vi-VN"));

	public TieuThuPhuTungPage() {
		this.dinhDangSo.setMaximumFractionDigits(0);
		this.dinhDangTien.setMaximumFractionDigits(0);

		this.setLayout(new BorderLayout(8, 8));
		this.setBorder(Theme.leDeu(8));
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
				"app.report.parts.export", this::xuatBaoCao), BorderLayout.SOUTH);

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
			YearMonth thang = thangHienTai.minusMonths(i);
			this.cmbThang.addItem(thang.format(DateFormats.THANG_NAM));
		}

		this.cmbThang.setSelectedIndex(0);
		this.cmbThang.setPreferredSize(new Dimension(150, 28));
	}

	private void capNhatTieuDeBang() {
		this.modelPhuTung.setColumnIdentifiers(new Object[] { I18nReaderUtil.get("app.report.parts.code"),
				I18nReaderUtil.get("app.report.parts.name"), I18nReaderUtil.get("app.report.parts.imported"),
				I18nReaderUtil.get("app.report.parts.exported"), I18nReaderUtil.get("app.report.parts.stock"),
				I18nReaderUtil.get("app.report.parts.unitPrice"), I18nReaderUtil.get("app.report.parts.revenue"),
				I18nReaderUtil.get("app.report.parts.consumptionRateColumn") });
	}

	private void cauHinhBang() {
		this.tblPhuTung.setRowHeight(25);
		this.tblPhuTung.setAutoCreateRowSorter(true);
		this.tblPhuTung.getTableHeader().setReorderingAllowed(false);
		this.tblPhuTung.setShowGrid(true);
		this.tblPhuTung.setFillsViewportHeight(true);

		DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
		renderer.setHorizontalAlignment(SwingConstants.RIGHT);

		for (int i = 2; i < 8; i++) {
			this.tblPhuTung.getColumnModel().getColumn(i).setCellRenderer(renderer);
		}

		int[] doRongCot = { 130, 340, 135, 135, 115, 180, 220, 180 };

		for (int i = 0; i < doRongCot.length; i++) {
			this.tblPhuTung.getColumnModel().getColumn(i).setPreferredWidth(doRongCot[i]);
		}
	}

	private JPanel taoBoLoc() {
		JPanel pnl = new JPanel(new BorderLayout(8, 0));

		pnl.setBorder(Theme.khungLoc());

		JPanel pnlThang = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));

		JLabel lblThang = new JLabel(I18nReaderUtil.get("app.report.parts.month"));

		pnlThang.add(lblThang);
		pnlThang.add(this.cmbThang);

		pnl.add(pnlThang, BorderLayout.WEST);

		return pnl;
	}

	private JPanel taoNoiDung() {
		JPanel pnl = new JPanel(new BorderLayout(0, 8));

		JPanel pnlTongQuan = new JPanel(new GridLayout(1, 4, 8, 8));

		pnlTongQuan.add(BaoCaoUi.theThongKe("app.report.parts.totalImported", this.lblTongNhap));

		pnlTongQuan.add(BaoCaoUi.theThongKe("app.report.parts.totalExported", this.lblTongXuat));

		pnlTongQuan.add(BaoCaoUi.theThongKe("app.report.parts.currentStock", this.lblTonKho));

		pnlTongQuan.add(BaoCaoUi.theThongKe("app.report.parts.consumptionRate", this.lblTyLeTieuThu));

		JPanel pnlBang = new JPanel(new BorderLayout());

		pnlBang.setBorder(Theme.titledBorder("app.report.parts.top20"));

		pnlBang.add(new JScrollPane(this.tblPhuTung), BorderLayout.CENTER);

		pnl.add(pnlTongQuan, BorderLayout.NORTH);
		pnl.add(pnlBang, BorderLayout.CENTER);

		return pnl;
	}

	private void xuatBaoCao() {
		BaoCaoUi.xuat(this, this.modelPhuTung, "BaoCaoTieuThuPhuTung.xlsx",
				"app.report.parts.title", "app.report.parts.noData");
	}

	private void inBaoCao() {
		BaoCaoUi.in(this.tblPhuTung, "app.report.parts.noData");
	}

	private void taiBaoCao() {
		String thangDuocChon = (String) this.cmbThang.getSelectedItem();

		if (thangDuocChon == null || thangDuocChon.isBlank()) {
			ThongBao.canhBao(this, "app.report.parts.invalidMonth");
			return;
		}

		try {
			YearMonth thang = YearMonth.parse(thangDuocChon, DateFormats.THANG_NAM);

			BaoCaoTieuThuPhuTungDTO baoCao = this.tieuThuPhuTungService.taoBaoCao(thang);

			this.capNhatThongKe(baoCao);
			this.capNhatBangPhuTung(baoCao);

		} catch (IllegalArgumentException e) {
			ThongBao.canhBao(this, e.getMessage());

		} catch (SQLException e) {
			ThongBao.loi(this, "app.report.revenue.loadError");
		}
	}

	private void capNhatThongKe(BaoCaoTieuThuPhuTungDTO baoCao) {
		this.lblTongNhap.setText(this.dinhDangSo.format(baoCao.tongNhap()));

		this.lblTongXuat.setText(this.dinhDangSo.format(baoCao.tongXuat()));

		this.lblTonKho.setText(this.dinhDangSo.format(baoCao.tongTonKho()));

		this.lblTyLeTieuThu.setText(String.format(Locale.forLanguageTag("vi-VN"), "%.1f%%", baoCao.tyLeTieuThu()));
	}

	private void capNhatBangPhuTung(BaoCaoTieuThuPhuTungDTO baoCao) {

		this.modelPhuTung.setRowCount(0);

		for (TieuThuPhuTungBaoCaoDTO phuTung : baoCao.danhSachPhuTung()) {

			String tyLeTieuThu = phuTung.soLuongNhap() == 0 ? "—"
					: String.format(Locale.forLanguageTag("vi-VN"), "%.1f%%", phuTung.tyLeTieuThu());

			this.modelPhuTung.addRow(new Object[] { phuTung.maPhuTung(), phuTung.tenPhuTung(),
					this.dinhDangSo.format(phuTung.soLuongNhap()), this.dinhDangSo.format(phuTung.soLuongXuat()),
					this.dinhDangSo.format(phuTung.soLuongTon()), this.dinhDangTien.format(phuTung.donGiaBan()),
					this.dinhDangTien.format(phuTung.doanhThu()), tyLeTieuThu });
		}
	}

	@Override
	public String getTitleKey() {
		return "app.menu.partsConsumptionReport";
	}

	@Override
	public MenuGroup getGroup() {
		return MenuGroup.TONG_KET;
	}
}
