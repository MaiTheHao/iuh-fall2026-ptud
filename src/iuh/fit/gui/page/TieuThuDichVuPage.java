
package iuh.fit.gui.page;

import iuh.fit.dto.BaoCaoTieuThuDichVuDTO;
import iuh.fit.dto.TieuThuDichVuBaoCaoDTO;
import iuh.fit.enumeration.MenuGroup;
import iuh.fit.service.TieuThuDichVuService;
import iuh.fit.util.I18nReaderUtil;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class TieuThuDichVuPage extends AbstractPage {

	private static final long serialVersionUID = 1L;

	private final TieuThuDichVuService tieuThuDichVuService = new TieuThuDichVuService();

	private final JComboBox<String> cboThang = new JComboBox<>();

	private final JLabel lblTongLuot = new JLabel("0");
	private final JLabel lblDoanhThu = new JLabel("0");
	private final JLabel lblDichVuPhoBien = new JLabel("-");
	private final JLabel lblSoDichVu = new JLabel("0");

	private final DefaultTableModel modelDichVu = new DefaultTableModel(new Object[0][6], new String[6]) {

		private static final long serialVersionUID = 1L;

		@Override
		public boolean isCellEditable(int row, int column) {
			return false;
		}
	};

	private final JTable tblDichVu = new JTable(this.modelDichVu);

	private final DateTimeFormatter dinhDangThang = DateTimeFormatter.ofPattern("MM/yyyy");

	private final NumberFormat dinhDangSo = NumberFormat.getNumberInstance(new Locale("vi", "VN"));

	public TieuThuDichVuPage() {
		this.setLayout(new BorderLayout(8, 8));
		this.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

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
		this.add(this.taoThanhCongCu(), BorderLayout.SOUTH);

		this.revalidate();
		this.repaint();

		this.taiBaoCao();
	}

	private void khoiTaoThang() {
		if (this.cboThang.getItemCount() > 0) {
			return;
		}

		YearMonth thangHienTai = YearMonth.now();

		for (int i = 0; i < 12; i++) {
			this.cboThang.addItem(thangHienTai.minusMonths(i).format(this.dinhDangThang));
		}

		this.cboThang.setSelectedIndex(0);
		this.cboThang.setPreferredSize(new Dimension(150, 28));
	}

	private YearMonth layThangDuocChon() {
		String thang = (String) this.cboThang.getSelectedItem();

		if (thang == null || thang.trim().isEmpty()) {
			throw new IllegalArgumentException("Chưa chọn tháng báo cáo.");
		}

		return YearMonth.parse(thang, this.dinhDangThang);
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
		this.tblDichVu.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
		this.tblDichVu.getTableHeader().setBackground(new Color(232, 235, 238));

		this.tblDichVu.setGridColor(new Color(220, 224, 228));
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

		pnl.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(190, 195, 200)),
				BorderFactory.createEmptyBorder(8, 10, 8, 10)));

		JPanel pnlThang = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));

		pnlThang.add(new JLabel(I18nReaderUtil.get("app.report.service.month")));
		pnlThang.add(this.cboThang);

		JPanel pnlNut = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));

		JButton btnXemBaoCao = new JButton(I18nReaderUtil.get("app.report.service.view"));

		btnXemBaoCao.addActionListener(e -> this.taiBaoCao());

		pnlNut.add(btnXemBaoCao);

		pnl.add(pnlThang, BorderLayout.WEST);
		pnl.add(pnlNut, BorderLayout.EAST);

		return pnl;
	}

	private JPanel taoNoiDung() {
		JPanel pnl = new JPanel(new BorderLayout(0, 8));

		JPanel pnlTongQuan = new JPanel(new GridLayout(1, 4, 8, 8));

		pnlTongQuan.add(this.taoTheThongKe("app.report.service.totalUses", this.lblTongLuot));
		pnlTongQuan.add(this.taoTheThongKe("app.report.service.totalRevenue", this.lblDoanhThu));
		pnlTongQuan.add(this.taoTheThongKe("app.report.service.mostUsed", this.lblDichVuPhoBien));
		pnlTongQuan.add(this.taoTheThongKe("app.report.service.distinctCount", this.lblSoDichVu));

		JPanel pnlBang = new JPanel(new BorderLayout());
		pnlBang.setBorder(BorderFactory.createTitledBorder(I18nReaderUtil.get("app.report.service.top20")));
		pnlBang.add(new JScrollPane(this.tblDichVu), BorderLayout.CENTER);

		pnl.add(pnlTongQuan, BorderLayout.NORTH);
		pnl.add(pnlBang, BorderLayout.CENTER);

		return pnl;
	}

	private JPanel taoTheThongKe(String key, JLabel lblGiaTri) {
		JPanel pnl = new JPanel(new BorderLayout(4, 8));

		pnl.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(pnl.getForeground()),
				BorderFactory.createEmptyBorder(10, 10, 10, 10)));

		JLabel lblTieuDe = new JLabel(I18nReaderUtil.get(key));
		lblGiaTri.setFont(new Font("Arial", Font.BOLD, 18));

		pnl.add(lblTieuDe, BorderLayout.NORTH);
		pnl.add(lblGiaTri, BorderLayout.CENTER);

		return pnl;
	}

	private JPanel taoThanhCongCu() {
		JPanel pnl = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 6));

		JButton btnIn = new JButton(I18nReaderUtil.get("app.report.revenue.print"));
		JButton btnXuat = new JButton(I18nReaderUtil.get("app.report.revenue.exportExcel"));

		btnIn.addActionListener(e -> this.inBaoCao());
		btnXuat.addActionListener(e -> this.xuatBaoCao());

		pnl.add(btnIn);
		pnl.add(btnXuat);

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

			JOptionPane.showMessageDialog(this, ex.getMessage(), I18nReaderUtil.get("app.common.notification"),
					JOptionPane.WARNING_MESSAGE);

		} catch (SQLException ex) {
			this.xoaDuLieuBaoCao();

			JOptionPane.showMessageDialog(this, I18nReaderUtil.get("app.report.service.loadError"),
					I18nReaderUtil.get("app.common.notification"), JOptionPane.ERROR_MESSAGE);

			ex.printStackTrace();
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
		if (this.modelDichVu.getRowCount() == 0) {
			JOptionPane.showMessageDialog(this, I18nReaderUtil.get("app.report.revenue.noData"),
					I18nReaderUtil.get("app.common.notification"), JOptionPane.INFORMATION_MESSAGE);
			return;
		}

		try {
			this.tblDichVu.print(JTable.PrintMode.FIT_WIDTH, null, null);
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, I18nReaderUtil.get("app.report.revenue.printError"),
					I18nReaderUtil.get("app.common.notification"), JOptionPane.ERROR_MESSAGE);
			ex.printStackTrace();
		}
	}

	private void xuatBaoCao() {
		if (this.modelDichVu.getRowCount() == 0) {
			JOptionPane.showMessageDialog(this, I18nReaderUtil.get("app.report.revenue.noData"),
					I18nReaderUtil.get("app.common.notification"), JOptionPane.INFORMATION_MESSAGE);
			return;
		}

		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle(I18nReaderUtil.get("app.report.revenue.export"));
		fileChooser.setSelectedFile(new File("BaoCaoTieuThuDichVu.xlsx"));
		fileChooser.setFileFilter(new FileNameExtensionFilter("XLSX (*.xlsx)", "xlsx"));

		if (fileChooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
			return;
		}

		File file = fileChooser.getSelectedFile();

		if (!file.getName().toLowerCase(Locale.ROOT).endsWith(".csv")) {
			file = new File(file.getAbsolutePath() + ".csv");
		}

		try (BufferedWriter writer = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {

			writer.write('\uFEFF');

			for (int column = 0; column < this.modelDichVu.getColumnCount(); column++) {
				if (column > 0) {
					writer.write(';');
				}

				writer.write(this.escCsv(this.modelDichVu.getColumnName(column)));
			}

			writer.newLine();

			for (int row = 0; row < this.modelDichVu.getRowCount(); row++) {
				for (int column = 0; column < this.modelDichVu.getColumnCount(); column++) {
					if (column > 0) {
						writer.write(';');
					}

					Object value = this.modelDichVu.getValueAt(row, column);

					writer.write(this.escCsv(value == null ? "" : value.toString()));
				}

				writer.newLine();
			}

			JOptionPane.showMessageDialog(this, I18nReaderUtil.get("app.report.revenue.exportSuccess"),
					I18nReaderUtil.get("app.common.notification"), JOptionPane.INFORMATION_MESSAGE);

		} catch (IOException ex) {
			JOptionPane.showMessageDialog(this, I18nReaderUtil.get("app.report.revenue.exportError"),
					I18nReaderUtil.get("app.common.notification"), JOptionPane.ERROR_MESSAGE);
			ex.printStackTrace();
		}
	}

	private String escCsv(String value) {
		return "\"" + value.replace("\"", "\"\"") + "\"";
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
