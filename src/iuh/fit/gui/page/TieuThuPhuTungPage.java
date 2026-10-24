package iuh.fit.gui.page;

import iuh.fit.dto.BaoCaoTieuThuPhuTungDTO;
import iuh.fit.dto.TieuThuPhuTungBaoCaoDTO;
import iuh.fit.enumeration.MenuGroup;
import iuh.fit.service.TieuThuPhuTungService;
import iuh.fit.util.I18nReaderUtil;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class TieuThuPhuTungPage extends AbstractPage {

	private static final long serialVersionUID = 1L;

	private static final DateTimeFormatter DINH_DANG_THANG = DateTimeFormatter.ofPattern("MM/yyyy");

	private final JComboBox<String> cboThang = new JComboBox<>();

	private final JLabel lblTongNhap = new JLabel("0");
	private final JLabel lblTongXuat = new JLabel("0");
	private final JLabel lblTonKho = new JLabel("0");
	private final JLabel lblTyLeTieuThu = new JLabel("0%");

	private final DefaultTableModel modelPhuTung = new DefaultTableModel(new Object[0][0], new String[8]) {

		private static final long serialVersionUID = 1L;

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
		this.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
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
			YearMonth thang = thangHienTai.minusMonths(i);
			this.cboThang.addItem(thang.format(DINH_DANG_THANG));
		}

		this.cboThang.setSelectedIndex(0);
		this.cboThang.setPreferredSize(new Dimension(150, 28));
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
		this.tblPhuTung.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
		this.tblPhuTung.getTableHeader().setBackground(new Color(232, 235, 238));

		this.tblPhuTung.setGridColor(new Color(220, 224, 228));
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

		pnl.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(190, 195, 200)),
				BorderFactory.createEmptyBorder(8, 10, 8, 10)));

		JPanel pnlThang = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));

		JLabel lblThang = new JLabel(I18nReaderUtil.get("app.report.parts.month"));

		pnlThang.add(lblThang);
		pnlThang.add(this.cboThang);

		JPanel pnlNut = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));

		JButton btnXemBaoCao = new JButton(I18nReaderUtil.get("app.report.parts.view"));

		btnXemBaoCao.addActionListener(e -> this.taiBaoCao());

		this.cboThang.addActionListener(e -> this.taiBaoCao());

		pnlNut.add(btnXemBaoCao);

		pnl.add(pnlThang, BorderLayout.WEST);
		pnl.add(pnlNut, BorderLayout.EAST);

		return pnl;
	}

	private JPanel taoNoiDung() {
		JPanel pnl = new JPanel(new BorderLayout(0, 8));

		JPanel pnlTongQuan = new JPanel(new GridLayout(1, 4, 8, 8));

		pnlTongQuan.add(this.taoTheThongKe("app.report.parts.totalImported", this.lblTongNhap));

		pnlTongQuan.add(this.taoTheThongKe("app.report.parts.totalExported", this.lblTongXuat));

		pnlTongQuan.add(this.taoTheThongKe("app.report.parts.currentStock", this.lblTonKho));

		pnlTongQuan.add(this.taoTheThongKe("app.report.parts.consumptionRate", this.lblTyLeTieuThu));

		JPanel pnlBang = new JPanel(new BorderLayout());

		pnlBang.setBorder(BorderFactory.createTitledBorder(I18nReaderUtil.get("app.report.parts.top20")));

		pnlBang.add(new JScrollPane(this.tblPhuTung), BorderLayout.CENTER);

		pnl.add(pnlTongQuan, BorderLayout.NORTH);
		pnl.add(pnlBang, BorderLayout.CENTER);

		return pnl;
	}

	private JPanel taoTheThongKe(String key, JLabel lblGiaTri) {
		JPanel pnl = new JPanel(new BorderLayout(4, 8));

		pnl.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(pnl.getForeground()),
				BorderFactory.createEmptyBorder(12, 12, 12, 12)));

		JLabel lblTieuDe = new JLabel(I18nReaderUtil.get(key));
		lblGiaTri.setFont(new Font("Arial", Font.BOLD, 20));

		pnl.add(lblTieuDe, BorderLayout.NORTH);
		pnl.add(lblGiaTri, BorderLayout.CENTER);

		return pnl;
	}

	private JPanel taoThanhCongCu() {
		JPanel pnl = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 6));

		JButton btnIn = new JButton(I18nReaderUtil.get("app.report.parts.print"));

		JButton btnXuatExcel = new JButton(I18nReaderUtil.get("app.report.parts.export"));

		btnIn.addActionListener(e -> this.thongBaoChuaKetNoi());
		btnXuatExcel.addActionListener(e -> this.thongBaoChuaKetNoi());

		pnl.add(btnIn);
		pnl.add(btnXuatExcel);

		return pnl;
	}

	private void taiBaoCao() {
		String thangDuocChon = (String) this.cboThang.getSelectedItem();

		if (thangDuocChon == null || thangDuocChon.isBlank()) {
			JOptionPane.showMessageDialog(this, I18nReaderUtil.get("app.report.parts.invalidMonth"),
					I18nReaderUtil.get("app.common.notification"), JOptionPane.WARNING_MESSAGE);
			return;
		}

		try {
			YearMonth thang = YearMonth.parse(thangDuocChon, DINH_DANG_THANG);

			BaoCaoTieuThuPhuTungDTO baoCao = this.tieuThuPhuTungService.taoBaoCao(thang);

			this.capNhatThongKe(baoCao);
			this.capNhatBangPhuTung(baoCao);

		} catch (IllegalArgumentException e) {
			JOptionPane.showMessageDialog(this, I18nReaderUtil.get(e.getMessage()),
					I18nReaderUtil.get("app.common.notification"), JOptionPane.WARNING_MESSAGE);

		} catch (SQLException e) {
			JOptionPane.showMessageDialog(this, I18nReaderUtil.get("app.report.parts.loadError"),
					I18nReaderUtil.get("app.common.notification"), JOptionPane.ERROR_MESSAGE);
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

	private void thongBaoChuaKetNoi() {
		JOptionPane.showMessageDialog(this, I18nReaderUtil.get("app.report.parts.actionNotConnected"),
				I18nReaderUtil.get("app.common.notification"), JOptionPane.INFORMATION_MESSAGE);
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
