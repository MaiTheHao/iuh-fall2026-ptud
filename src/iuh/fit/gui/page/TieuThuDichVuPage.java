package iuh.fit.gui.page;

import iuh.fit.enumeration.MenuGroup;
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
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

public class TieuThuDichVuPage extends AbstractPage {

	private static final long serialVersionUID = 1L;

	private final JComboBox<String> cboThang = new JComboBox<>();

	private final JLabel lblTongLuot = new JLabel();
	private final JLabel lblDoanhThu = new JLabel();
	private final JLabel lblDichVuPhoBien = new JLabel();
	private final JLabel lblSoDichVu = new JLabel();

	private final DefaultTableModel modelDichVu = new DefaultTableModel(new Object[0][0], new String[6]) {

		private static final long serialVersionUID = 1L;

		@Override
		public boolean isCellEditable(int row, int column) {
			return false;
		}
	};

	private final JTable tblDichVu = new JTable(this.modelDichVu);

	public TieuThuDichVuPage() {
		this.setLayout(new BorderLayout(8, 8));
		this.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
	}

	@Override
	public void render() {
		this.removeAll();

		this.khoiTaoThang();
		this.capNhatTieuDeBang();
//        this.cauHinhBang();

		this.add(this.taoBoLoc(), BorderLayout.NORTH);
		this.add(this.taoNoiDung(), BorderLayout.CENTER);
		this.add(this.taoThanhCongCu(), BorderLayout.SOUTH);

		this.revalidate();
		this.repaint();
	}

	/**
	 * Khởi tạo các lựa chọn tháng cho bộ lọc. Đây chỉ là các lựa chọn thời gian,
	 * không phải dữ liệu báo cáo.
	 */
	private void khoiTaoThang() {
		if (this.cboThang.getItemCount() > 0) {
			return;
		}

		YearMonth thangHienTai = YearMonth.now();

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yyyy");

		for (int i = 0; i < 12; i++) {
			YearMonth thang = thangHienTai.minusMonths(i);
			this.cboThang.addItem(thang.format(formatter));
		}

		this.cboThang.setSelectedIndex(0);
		this.cboThang.setPreferredSize(new Dimension(150, 28));
	}

	/**
	 * Đồng bộ tiêu đề bảng theo ngôn ngữ hiện tại.
	 */
	private void capNhatTieuDeBang() {
		this.modelDichVu.setColumnIdentifiers(new Object[] { I18nReaderUtil.get("app.report.service.code"),
				I18nReaderUtil.get("app.report.service.name"), I18nReaderUtil.get("app.report.service.count"),
				I18nReaderUtil.get("app.report.service.price"), I18nReaderUtil.get("app.report.service.revenue"),
				I18nReaderUtil.get("app.report.service.ratio") });
	}

	private JPanel taoBoLoc() {
		JPanel pnl = new JPanel(new BorderLayout(8, 0));

		pnl.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(190, 195, 200)),
				BorderFactory.createEmptyBorder(8, 10, 8, 10)));

		JPanel pnlThang = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));

		JLabel lblThang = new JLabel(I18nReaderUtil.get("app.report.service.month"));

		pnlThang.add(lblThang);
		pnlThang.add(this.cboThang);

		JPanel pnlNut = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));

		JButton btnXemBaoCao = new JButton(I18nReaderUtil.get("app.report.service.view"));

		btnXemBaoCao.addActionListener(e -> this.thongBaoChuaKetNoi());

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
		this.tblDichVu.getColumnModel().getColumn(3).setPreferredWidth(180);
		this.tblDichVu.getColumnModel().getColumn(4).setPreferredWidth(300);
		this.tblDichVu.getColumnModel().getColumn(5).setPreferredWidth(160);

		pnlBang.add(new JScrollPane(this.tblDichVu), BorderLayout.CENTER);

		pnl.add(pnlTongQuan, BorderLayout.NORTH);
		pnl.add(pnlBang, BorderLayout.CENTER);

		return pnl;
	}

	private JPanel taoTheThongKe(String key, JLabel lblGiaTri) {
		JPanel pnl = new JPanel(new BorderLayout(4, 8));

		pnl.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(pnl.getForeground()),
				BorderFactory.createEmptyBorder(10, 10, 10, 10)));

		pnl.add(new JLabel(I18nReaderUtil.get(key)), BorderLayout.NORTH);

		pnl.add(lblGiaTri, BorderLayout.CENTER);

		return pnl;
	}

	private JPanel taoThanhCongCu() {
		JPanel pnl = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 6));

		JButton btnIn = new JButton(I18nReaderUtil.get("app.report.service.print"));

		JButton btnXuatExcel = new JButton(I18nReaderUtil.get("app.report.service.export"));

		btnIn.addActionListener(e -> this.thongBaoChuaKetNoi());

		btnXuatExcel.addActionListener(e -> this.thongBaoChuaKetNoi());

		pnl.add(btnIn);
		pnl.add(btnXuatExcel);

		return pnl;
	}

	private void thongBaoChuaKetNoi() {
		JOptionPane.showMessageDialog(this, I18nReaderUtil.get("app.report.service.actionNotConnected"),
				I18nReaderUtil.get("app.common.notification"), JOptionPane.INFORMATION_MESSAGE);
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
