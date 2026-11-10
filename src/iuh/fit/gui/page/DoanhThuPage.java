package iuh.fit.gui.page;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;

import iuh.fit.dto.BaoCaoDoanhThuDTO;
import iuh.fit.dto.HoaDonDoanhThuDTO;
import iuh.fit.enumeration.MenuGroup;
import iuh.fit.gui.Theme;
import iuh.fit.gui.component.BaoCaoUi;
import iuh.fit.gui.component.ThongBao;
import iuh.fit.service.DoanhThuService;
import iuh.fit.util.DateFormats;
import iuh.fit.util.I18nReaderUtil;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.Locale;
import java.util.List;

public class DoanhThuPage extends AbstractPage {

	

	private final DatePicker dpTuNgay = this.taoDatePicker();
	private final DatePicker dpDenNgay = this.taoDatePicker();

	private final JLabel lblTongDoanhThu = new JLabel();
	private final JLabel lblTongGoc = new JLabel();
	private final JLabel lblTongGiam = new JLabel();
	private final JLabel lblDoanhThuDichVu = new JLabel();
	private final JLabel lblDoanhThuPhuTung = new JLabel();
	private final JLabel lblSoHoaDon = new JLabel();

	private final DefaultTableModel modelHoaDon = new DefaultTableModel(new Object[0][0], new String[] {
			I18nReaderUtil.get("app.report.revenue.invoiceCode"), I18nReaderUtil.get("app.report.revenue.createdDate"),
			I18nReaderUtil.get("app.report.revenue.paymentTime"), I18nReaderUtil.get("app.report.revenue.customer"),
			I18nReaderUtil.get("app.report.revenue.licensePlate"), I18nReaderUtil.get("app.report.revenue.status"),
			I18nReaderUtil.get("app.hoaDon.tongTienGoc"), I18nReaderUtil.get("app.hoaDon.giamGia"),
			I18nReaderUtil.get("app.hoaDon.tongThanhToan") }) {
		

		@Override
		public boolean isCellEditable(int row, int column) {
			return false;
		}
	};

	private final DoanhThuService doanhThuService = new DoanhThuService();
	private final NumberFormat dinhDangTien = NumberFormat.getNumberInstance(Locale.forLanguageTag("vi-VN"));

	private final JTable tblHoaDon = new JTable(this.modelHoaDon);

	private ChartPanel pnlBieuDo;

	private boolean dangDungGiaoDien;

	public DoanhThuPage() {
		this.setLayout(new BorderLayout(8, 8));
		this.setBorder(Theme.leDeu(8));

		this.datMacDinhNgay();

		this.dpTuNgay.addDateChangeListener(event -> this.taiBaoCaoKhiDoiLoc());
		this.dpDenNgay.addDateChangeListener(event -> this.taiBaoCaoKhiDoiLoc());
	}

	@Override
	public void render() {
		this.dangDungGiaoDien = true;

		this.removeAll();

		this.add(this.taoBoLoc(), BorderLayout.NORTH);
		this.add(this.taoNoiDung(), BorderLayout.CENTER);
		this.add(BaoCaoUi.thanhCongCu("app.report.revenue.print", this::inBaoCao,
				"app.report.revenue.export", this::xuatBaoCao), BorderLayout.SOUTH);

		this.dangDungGiaoDien = false;

		this.revalidate();
		this.repaint();

		this.taiBaoCao();
	}

	private void datMacDinhNgay() {
		this.dpTuNgay.setDate(LocalDate.now().minusDays(29));
		this.dpDenNgay.setDate(LocalDate.now());
	}

	private void taiBaoCaoKhiDoiLoc() {
		if (this.dangDungGiaoDien) {
			return;
		}

		if (this.dpTuNgay.getDate() == null || this.dpDenNgay.getDate() == null) {
			return;
		}

		this.taiBaoCao();
	}

	private DatePicker taoDatePicker() {
		DatePickerSettings settings = new DatePickerSettings();

		settings.setFormatForDatesCommonEra(I18nReaderUtil.get("app.date.format"));

		return new DatePicker(settings);
	}

	private JPanel taoBoLoc() {
		JPanel pnl = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));

		pnl.add(new JLabel(I18nReaderUtil.get("app.report.revenue.fromDate")));
		pnl.add(this.dpTuNgay);

		pnl.add(new JLabel(I18nReaderUtil.get("app.report.revenue.toDate")));
		pnl.add(this.dpDenNgay);

		return pnl;
	}

	private void taiBaoCao() {
		try {
			LocalDate tuNgay = this.dpTuNgay.getDate();
			LocalDate denNgay = this.dpDenNgay.getDate();

			BaoCaoDoanhThuDTO baoCao = this.doanhThuService.taoBaoCao(tuNgay, denNgay);

			this.capNhatThongKe(baoCao);
			this.capNhatBangHoaDon(baoCao.danhSachHoaDon());
			this.capNhatBieuDo(baoCao);

		} catch (IllegalArgumentException e) {
			ThongBao.canhBao(this, e.getMessage());
		} catch (SQLException e) {
			ThongBao.loi(this, "app.report.revenue.loadError");
		}
	}

	private void capNhatBangHoaDon(List<HoaDonDoanhThuDTO> danhSachHoaDon) {
		DefaultTableModel model = (DefaultTableModel) this.tblHoaDon.getModel();

		model.setRowCount(0);

		for (HoaDonDoanhThuDTO hoaDon : danhSachHoaDon) {
			String ngayTao = hoaDon.ngayTao() == null ? "" : hoaDon.ngayTao().format(DateFormats.NGAY_GIO);

			String thoiGianThanhToan = hoaDon.thoiGianThanhToan() == null ? ""
					: hoaDon.thoiGianThanhToan().format(DateFormats.NGAY_GIO);

			model.addRow(new Object[] { hoaDon.maHoaDon(), ngayTao, thoiGianThanhToan, hoaDon.tenKhachHang(),
					hoaDon.bienSo(), hoaDon.trangThaiHoaDon(), this.dinhDangTien.format(hoaDon.tongGoc()),
					this.dinhDangTien.format(hoaDon.giamGia()), this.dinhDangTien.format(hoaDon.thucThu()) });
		}
	}

	private void capNhatBieuDo(BaoCaoDoanhThuDTO baoCao) {
		DefaultPieDataset<String> dataset = new DefaultPieDataset<>();

		dataset.setValue(I18nReaderUtil.get("app.report.revenue.serviceRevenue"), baoCao.doanhThuDichVu());
		dataset.setValue(I18nReaderUtil.get("app.report.revenue.partsRevenue"), baoCao.doanhThuPhuTung());

		JFreeChart bieuDo = this.pnlBieuDo.getChart();

		@SuppressWarnings("unchecked")
		PiePlot<String> piePlot = (PiePlot<String>) bieuDo.getPlot();

		piePlot.setDataset(dataset);
	}

	private void capNhatThongKe(BaoCaoDoanhThuDTO baoCao) {
		this.lblTongDoanhThu.setText(this.dinhDangTien.format(baoCao.thucThu()));

		this.lblTongGoc.setText(this.dinhDangTien.format(baoCao.tongGoc()));

		this.lblTongGiam.setText(this.dinhDangTien.format(baoCao.tongGiam()));

		this.lblDoanhThuDichVu.setText(this.dinhDangTien.format(baoCao.doanhThuDichVu()));

		this.lblDoanhThuPhuTung.setText(this.dinhDangTien.format(baoCao.doanhThuPhuTung()));

		this.lblSoHoaDon.setText(String.valueOf(baoCao.soLuongHoaDon()));
	}

	private JPanel taoNoiDung() {
		JPanel pnl = new JPanel(new BorderLayout(8, 8));

		JPanel pnlTongQuan = new JPanel(new GridLayout(1, 6, 8, 8));

		pnlTongQuan.add(BaoCaoUi.theThongKe("app.report.revenue.totalRevenue", this.lblTongDoanhThu));

		pnlTongQuan.add(BaoCaoUi.theThongKe("app.hoaDon.tongTienGoc", this.lblTongGoc));

		pnlTongQuan.add(BaoCaoUi.theThongKe("app.hoaDon.giamGia", this.lblTongGiam));

		pnlTongQuan.add(BaoCaoUi.theThongKe("app.report.revenue.serviceRevenue", this.lblDoanhThuDichVu));

		pnlTongQuan.add(BaoCaoUi.theThongKe("app.report.revenue.partsRevenue", this.lblDoanhThuPhuTung));

		pnlTongQuan.add(BaoCaoUi.theThongKe("app.report.revenue.invoiceCount", this.lblSoHoaDon));

		JPanel pnlKhungBieuDo = new JPanel(new BorderLayout());

		pnlKhungBieuDo
				.setBorder(Theme.titledBorder("app.report.revenue.revenueStructure"));

		DefaultPieDataset<String> dataset = new DefaultPieDataset<>();

		this.pnlBieuDo = new ChartPanel(ChartFactory.createPieChart(null, dataset, true, true, false));

		JFreeChart bieuDo = this.pnlBieuDo.getChart();

		@SuppressWarnings("unchecked")
		PiePlot<String> piePlot = (PiePlot<String>) bieuDo.getPlot();

		piePlot.setLabelGenerator(
				new StandardPieSectionLabelGenerator("{2}", new DecimalFormat("#,##0"), new DecimalFormat("0.00%")));

		piePlot.setSimpleLabels(true);
		piePlot.setLabelBackgroundPaint(null);
		piePlot.setLabelOutlinePaint(null);
		piePlot.setLabelShadowPaint(null);
		piePlot.setLabelPaint(Color.WHITE);
		piePlot.setLabelFont(Theme.soLieuFont());

		piePlot.setInteriorGap(0.04);

		piePlot.setLegendLabelGenerator(new StandardPieSectionLabelGenerator("{0}"));
		pnlKhungBieuDo.add(this.pnlBieuDo, BorderLayout.CENTER);

		JPanel pnlBangHoaDon = new JPanel(new BorderLayout());
		pnlBangHoaDon.setBorder(Theme.titledBorder("app.report.revenue.invoiceList"));

		this.tblHoaDon.setRowHeight(28);
		this.tblHoaDon.setAutoCreateRowSorter(true);
		this.tblHoaDon.getTableHeader().setReorderingAllowed(false);

		pnlBangHoaDon.add(new JScrollPane(this.tblHoaDon), BorderLayout.CENTER);

		JPanel pnlBieuDoVaBang = new JPanel(new GridBagLayout());

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weighty = 1.0;

		gbc.gridx = 0;
		gbc.weightx = 1.0;
		gbc.insets = new Insets(0, 0, 0, 4);
		pnlBieuDoVaBang.add(pnlKhungBieuDo, gbc);

		gbc.gridx = 1;
		gbc.weightx = 2.0;
		gbc.insets = new Insets(0, 4, 0, 0);
		pnlBieuDoVaBang.add(pnlBangHoaDon, gbc);

		pnl.add(pnlTongQuan, BorderLayout.NORTH);
		pnl.add(pnlBieuDoVaBang, BorderLayout.CENTER);

		return pnl;
	}

	private void xuatBaoCao() {
		BaoCaoUi.xuat(this, this.modelHoaDon, "BaoCaoDoanhThu.xlsx",
				"app.menu.revenueReport", "app.report.revenue.noData");
	}

	private void inBaoCao() {
		BaoCaoUi.in(this.tblHoaDon, "app.report.revenue.noData");
	}

	@Override
	public String getTitleKey() {
		return "app.menu.revenueReport";
	}

	@Override
	public MenuGroup getGroup() {
		return MenuGroup.TONG_KET;
	}

}
