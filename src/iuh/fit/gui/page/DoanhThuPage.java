
package iuh.fit.gui.page;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;

import iuh.fit.enumeration.MenuGroup;
import iuh.fit.util.I18nReaderUtil;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.text.DecimalFormat;

public class DoanhThuPage extends AbstractPage {

	private static final long serialVersionUID = 1L;

	private final JComboBox<String> cboKieuThongKe = new JComboBox<>();

	private final DatePicker dpTuNgay = this.taoDatePicker();
	private final DatePicker dpDenNgay = this.taoDatePicker();

	private final JLabel lblTongDoanhThu = new JLabel();
	private final JLabel lblDoanhThuDichVu = new JLabel();
	private final JLabel lblDoanhThuPhuTung = new JLabel();
	private final JLabel lblSoHoaDon = new JLabel();

	private final DefaultTableModel modelHoaDon = new DefaultTableModel(new Object[0][0], new String[] {
			I18nReaderUtil.get("app.report.revenue.invoiceCode"), I18nReaderUtil.get("app.report.revenue.createdDate"),
			I18nReaderUtil.get("app.report.revenue.customer"), I18nReaderUtil.get("app.report.revenue.licensePlate"),
			I18nReaderUtil.get("app.report.revenue.status"), I18nReaderUtil.get("app.report.revenue.amount") }) {
		private static final long serialVersionUID = 1L;

		@Override
		public boolean isCellEditable(int row, int column) {
			return false;
		}
	};

	private final JTable tblHoaDon = new JTable(this.modelHoaDon);

	private ChartPanel pnlBieuDo;

	public DoanhThuPage() {
		this.setLayout(new BorderLayout(8, 8));
		this.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));


	}

	@Override
	public void render() {
		this.removeAll();

		this.add(this.taoBoLoc(), BorderLayout.NORTH);
		this.add(this.taoNoiDung(), BorderLayout.CENTER);
		this.add(this.taoThanhCongCu(), BorderLayout.SOUTH);

		this.revalidate();
		this.repaint();
	}

	private DatePicker taoDatePicker() {
		DatePickerSettings settings = new DatePickerSettings();

		settings.setFormatForDatesCommonEra(I18nReaderUtil.get("app.date.format"));

		return new DatePicker(settings);
	}

	private JPanel taoBoLoc() {
		JPanel pnl = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));

		this.cboKieuThongKe.removeAllItems();

		this.cboKieuThongKe.addItem(I18nReaderUtil.get("app.report.revenue.byDay"));
		this.cboKieuThongKe.addItem(I18nReaderUtil.get("app.report.revenue.byMonth"));
		this.cboKieuThongKe.addItem(I18nReaderUtil.get("app.report.revenue.byYear"));

		JButton btnXemBaoCao = new JButton(I18nReaderUtil.get("app.report.revenue.viewReport"));

		btnXemBaoCao.addActionListener(e -> this.thongBaoChuaKetNoi());

		pnl.add(new JLabel(I18nReaderUtil.get("app.report.revenue.statisticsType")));
		pnl.add(this.cboKieuThongKe);

		pnl.add(new JLabel(I18nReaderUtil.get("app.report.revenue.fromDate")));
		pnl.add(this.dpTuNgay);

		pnl.add(new JLabel(I18nReaderUtil.get("app.report.revenue.toDate")));
		pnl.add(this.dpDenNgay);

		pnl.add(btnXemBaoCao);

		return pnl;
	}

	private JPanel taoNoiDung() {
		JPanel pnl = new JPanel(new BorderLayout(8, 8));

		JPanel pnlTongQuan = new JPanel(new GridLayout(1, 4, 8, 8));

		pnlTongQuan.add(this.taoTheThongKe("app.report.revenue.totalRevenue", this.lblTongDoanhThu));

		pnlTongQuan.add(this.taoTheThongKe("app.report.revenue.serviceRevenue", this.lblDoanhThuDichVu));

		pnlTongQuan.add(this.taoTheThongKe("app.report.revenue.partsRevenue", this.lblDoanhThuPhuTung));

		pnlTongQuan.add(this.taoTheThongKe("app.report.revenue.invoiceCount", this.lblSoHoaDon));

		JPanel pnlKhungBieuDo = new JPanel(new BorderLayout());

		pnlKhungBieuDo
				.setBorder(BorderFactory.createTitledBorder(I18nReaderUtil.get("app.report.revenue.revenueStructure")));

		DefaultPieDataset<String> dataset = new DefaultPieDataset<>();

		// dữ liệu mẫu cho chart
		dataset.setValue(I18nReaderUtil.get("app.menu.servicePriceList"), 5);
		dataset.setValue(I18nReaderUtil.get("app.menu.inventory"), 5);

		this.pnlBieuDo = new ChartPanel(ChartFactory.createPieChart(null, dataset, true, true, false));

		JFreeChart bieuDo = this.pnlBieuDo.getChart();

		PiePlot<?> piePlot = (PiePlot<?>) bieuDo.getPlot();

		piePlot.setLabelGenerator(
				new StandardPieSectionLabelGenerator("{2}", new DecimalFormat("#,##0"), new DecimalFormat("0.00%")));

		piePlot.setSimpleLabels(true);
		piePlot.setLabelBackgroundPaint(null);
		piePlot.setLabelOutlinePaint(null);
		piePlot.setLabelShadowPaint(null);
		piePlot.setLabelPaint(Color.WHITE);
		piePlot.setLabelFont(new Font("Arial", Font.BOLD, 18));

		piePlot.setInteriorGap(0.04);

		piePlot.setLegendLabelGenerator(new StandardPieSectionLabelGenerator("{0}"));
		pnlKhungBieuDo.add(this.pnlBieuDo, BorderLayout.CENTER);

		JPanel pnlBangHoaDon = new JPanel(new BorderLayout());
		pnlBangHoaDon.setBorder(BorderFactory.createTitledBorder(I18nReaderUtil.get("app.report.revenue.invoiceList")));

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

	private JPanel taoTheThongKe(String key, JLabel lblGiaTri) {
		JPanel pnl = new JPanel(new BorderLayout(4, 8));

		pnl.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(pnl.getForeground()),
				BorderFactory.createEmptyBorder(12, 12, 12, 12)));

		pnl.add(new JLabel(I18nReaderUtil.get(key)), BorderLayout.NORTH);

		pnl.add(lblGiaTri, BorderLayout.CENTER);

		return pnl;
	}

	private JPanel taoThanhCongCu() {
		JPanel pnl = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 6));

		JButton btnIn = new JButton(I18nReaderUtil.get("app.report.revenue.print"));

		JButton btnXuatExcel = new JButton(I18nReaderUtil.get("app.report.revenue.exportExcel"));

		btnIn.addActionListener(e -> this.thongBaoChuaKetNoi());
		btnXuatExcel.addActionListener(e -> this.thongBaoChuaKetNoi());

		pnl.add(btnIn);
		pnl.add(btnXuatExcel);

		return pnl;
	}

	private void thongBaoChuaKetNoi() {
		JOptionPane.showMessageDialog(this, I18nReaderUtil.get("app.report.revenue.actionNotConnected"),
				I18nReaderUtil.get("app.common.notification"), JOptionPane.INFORMATION_MESSAGE);
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
