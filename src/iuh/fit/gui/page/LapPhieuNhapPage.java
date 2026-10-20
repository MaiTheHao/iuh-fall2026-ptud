package iuh.fit.gui.page;

import iuh.fit.core.context.AppContext;
import iuh.fit.entity.ChiTietPhieuNhapPhuTung;
import iuh.fit.entity.NhanVien;
import iuh.fit.entity.PhieuNhapPhuTung;
import iuh.fit.entity.PhuTung;
import iuh.fit.enumeration.MenuGroup;
import iuh.fit.gui.component.ChonPhuTungDialog;
import iuh.fit.gui.component.ThaoTac;
import iuh.fit.gui.component.ThaoTacTableCellRenderer;
import iuh.fit.gui.component.ThongBao;
import iuh.fit.gui.theme.Theme;
import iuh.fit.service.PhieuNhapPhuTungService;
import iuh.fit.service.PhuTungService;
import iuh.fit.util.I18nReaderUtil;
import iuh.fit.util.NumberParseUtil;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class LapPhieuNhapPage extends AbstractPage {

	private final PhuTungService phuTungService = new PhuTungService();
	private final PhieuNhapPhuTungService phieuNhapPhuTungService = new PhieuNhapPhuTungService();

	private final DecimalFormat currencyFormat = new DecimalFormat("#,##0");

	private final JTextField maPhieuField = new JTextField();
	private final JTextField ngayTaoField = new JTextField();
	private final JTextField maNhanVienField = new JTextField();
	private final JTextField moTaField = new JTextField();

	private DefaultTableModel modelDetails;
	private JTable tableDetails;
	private JLabel totalCostLabel;
	private JButton btnSave;
	private JButton btnAddRow;
	private JButton btnRemoveRow;

	private List<PhuTung> partsCache = new ArrayList<>();

	public LapPhieuNhapPage() {
		this.setLayout(new BorderLayout(8, 8));
	}

	@Override
	public void render() {
		this.removeAll();
		this.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

		this.taiDanhSachPhuTung();

		JPanel leftPanel = this.taoPanelThongTin();
		leftPanel.setPreferredSize(new Dimension(340, 0));

		this.add(leftPanel, BorderLayout.WEST);
		this.add(this.taoPanelChiTiet(), BorderLayout.CENTER);

		this.lamMoi();

		this.revalidate();
		this.repaint();
	}

	private void taiDanhSachPhuTung() {
		try {
			this.partsCache = this.phuTungService.getAllForSelection();
		} catch (SQLException e) {
			this.partsCache = new ArrayList<>();
			ThongBao.loi(this, "app.login.errorSystem");
		}
	}

	private JPanel taoPanelThongTin() {
		JPanel panel = new JPanel(new BorderLayout(0, 8));
		panel.setOpaque(false);
		TitledBorder tieuDe = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Theme.BORDER),
				I18nReaderUtil.get("app.phieuNhap.thongTinPhieuNhap"), TitledBorder.LEFT, TitledBorder.TOP);
		tieuDe.setTitleColor(Theme.chuTrenAnh());
		panel.setBorder(tieuDe);

		JPanel formPanel = new JPanel();
		formPanel.setOpaque(false);
		formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
		formPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

		Theme.applyReadOnly(this.maPhieuField);
		Theme.applyReadOnly(this.ngayTaoField);
		Theme.applyReadOnly(this.maNhanVienField);

		this.themDongBox(formPanel, "app.phieuNhap.maPhieuNhap", this.maPhieuField);
		this.themDongBox(formPanel, "app.phieuNhap.ngayTao", this.ngayTaoField);
		this.themDongBox(formPanel, "app.phieuNhap.thuKho", this.maNhanVienField);
		this.themDongBox(formPanel, "app.phieuNhap.moTa", this.moTaField);
		panel.add(formPanel, BorderLayout.NORTH);

		JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
		actionPanel.setOpaque(false);
		JButton btnReset = new JButton(I18nReaderUtil.get("app.common.clear"));
		this.btnSave = new JButton(I18nReaderUtil.get("app.phieuNhap.luuPhieuNhap"));
		Theme.applyPrimary(this.btnSave);

		btnReset.setPreferredSize(new Dimension(150, 32));
		this.btnSave.setPreferredSize(new Dimension(150, 32));
		btnReset.addActionListener(e -> this.xoaTrang());
		this.btnSave.addActionListener(e -> this.luu());

		actionPanel.add(btnReset);
		actionPanel.add(this.btnSave);
		panel.add(actionPanel, BorderLayout.SOUTH);
		return panel;
	}

	private void xoaTrang() {
		if (this.isDirty() && !ThongBao.xacNhan(this, "app.common.confirmClear", "app.common.confirm")) {
			return;
		}
		this.lamMoi();
	}

	private boolean isDirty() {
		return !this.moTaField.getText().isBlank() || this.modelDetails.getRowCount() > 0;
	}

	private void themDongBox(JPanel container, String i18nKey, JTextField field) {
		JLabel label = new JLabel(I18nReaderUtil.get(i18nKey));
		label.setForeground(Theme.chuTrenAnh());
		label.setAlignmentX(Component.LEFT_ALIGNMENT);
		field.setAlignmentX(Component.LEFT_ALIGNMENT);
		field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

		container.add(label);
		container.add(Box.createVerticalStrut(4));
		container.add(field);
		container.add(Box.createVerticalStrut(10));
	}

	private JPanel taoPanelChiTiet() {
		JPanel panel = new JPanel(new BorderLayout(0, 8));
		panel.setOpaque(false);
		TitledBorder tieuDe = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Theme.BORDER),
				I18nReaderUtil.get("app.phieuNhap.danhSachChiTiet"), TitledBorder.LEFT, TitledBorder.TOP);
		tieuDe.setTitleColor(Theme.chuTrenAnh());
		panel.setBorder(tieuDe);

		JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));
		topBar.setOpaque(false);
		this.btnAddRow = new JButton(I18nReaderUtil.get("app.phieuNhap.themDong"));
		Theme.applyPrimary(this.btnAddRow);
		this.btnRemoveRow = new JButton(I18nReaderUtil.get("app.phieuNhap.xoaDong"));
		this.btnRemoveRow.setForeground(Theme.DANGER_TEXT);

		this.btnAddRow.addActionListener(e -> this.themDong());
		this.btnRemoveRow.addActionListener(e -> this.xoaDongDaChon());
		topBar.add(this.btnAddRow);
		topBar.add(this.btnRemoveRow);
		panel.add(topBar, BorderLayout.NORTH);

		String[] headers = { "#", I18nReaderUtil.get("app.phuTung.ma"), I18nReaderUtil.get("app.phuTung.ten"),
				I18nReaderUtil.get("app.phieuNhap.soLuong"), I18nReaderUtil.get("app.phieuNhap.donGia"),
				I18nReaderUtil.get("app.phieuNhap.thanhTien"), I18nReaderUtil.get("app.common.actions") };

		this.modelDetails = new DefaultTableModel(headers, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return LapPhieuNhapPage.this.btnAddRow.isEnabled() && (column == 3 || column == 4);
			}
		};

		this.tableDetails = new JTable(this.modelDetails);
		this.tableDetails.setRowHeight(28);
		this.tableDetails.getColumnModel().getColumn(3)
				.setCellEditor(new SoNguyenCellEditor("app.phieuNhap.loiSoLuong", true));
		this.tableDetails.getColumnModel().getColumn(4)
				.setCellEditor(new SoNguyenCellEditor("app.phieuNhap.loiDonGia", false));
		ThaoTacTableCellRenderer.ganVao(this.tableDetails, 6, List.of(
				new ThaoTac("trash-2", "app.common.delete", ThaoTac.KieuThaoTac.NGUY_HIEM,
						row -> this.xoaDong(row))));
		this.modelDetails.addTableModelListener(e -> this.tinhThanhTienDong(e.getFirstRow(), e.getColumn()));

		panel.add(new JScrollPane(this.tableDetails), BorderLayout.CENTER);

		JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
		bottomPanel.setOpaque(false);
		JLabel lblTitle = new JLabel(I18nReaderUtil.get("app.phieuNhap.tongChiPhi") + ":");
		lblTitle.setForeground(Theme.chuTrenAnh());

		this.totalCostLabel = new JLabel(this.currencyFormat.format(0));
		this.totalCostLabel.setForeground(Theme.chuTrenAnh());
		this.totalCostLabel.setPreferredSize(new Dimension(640, 30));

		bottomPanel.add(lblTitle);
		bottomPanel.add(this.totalCostLabel);
		panel.add(bottomPanel, BorderLayout.SOUTH);

		return panel;
	}

	private void themDong() {
		if (this.partsCache.isEmpty()) {
			ThongBao.canhBao(this, "app.phieuNhap.loiPhuTungTrong");
			return;
		}

		ChonPhuTungDialog.KetQua kq = new ChonPhuTungDialog(
				SwingUtilities.getWindowAncestor(this), this.partsCache).showDialog().orElse(null);
		if (kq == null) {
			return;
		}
		for (int i = 0; i < this.modelDetails.getRowCount(); i++) {
			if (this.getMaFromRow(i).equalsIgnoreCase(kq.phuTung().getMaPhuTung())) {
				int soLuongMoi = this.getSoLuongRow(i) + kq.soLuong();
				this.modelDetails.setValueAt(soLuongMoi, i, 3);
				this.modelDetails.setValueAt(kq.phuTung().getDonGiaNhap(), i, 4);
				this.tinhTongChiPhi();
				return;
			}
		}

		double giaNhap = kq.phuTung().getDonGiaNhap() != null ? kq.phuTung().getDonGiaNhap() : 0.0;
		this.modelDetails.addRow(new Object[] { this.modelDetails.getRowCount() + 1, kq.phuTung().getMaPhuTung(),
				kq.phuTung().getTenPhuTung(), kq.soLuong(), giaNhap,
				this.currencyFormat.format(kq.soLuong() * giaNhap) });
		this.tinhTongChiPhi();
	}

	private void xoaDongDaChon() {
		int selectedRow = this.tableDetails.getSelectedRow();
		if (selectedRow < 0) {
			ThongBao.canhBao(this, "app.phieuNhap.chuaChonDongXoa");
			return;
		}

		this.xoaDong(selectedRow);
	}

	private void xoaDong(int row) {
		if (row < 0 || row >= this.modelDetails.getRowCount()) {
			return;
		}

		this.modelDetails.removeRow(row);
		for (int i = 0; i < this.modelDetails.getRowCount(); i++) {
			this.modelDetails.setValueAt(i + 1, i, 0);
		}
		this.tinhTongChiPhi();
	}

	private void tinhThanhTienDong(int row, int col) {
		if (row < 0 || row >= this.modelDetails.getRowCount()) {
			return;
		}

		if (col == 3 || col == 4) {
			double thanhTien = this.getSoLuongRow(row) * this.getDonGiaRow(row);
			this.modelDetails.setValueAt(this.currencyFormat.format(thanhTien), row, 5);
			this.tinhTongChiPhi();
		}
	}

	private void tinhTongChiPhi() {
		double total = 0.0;
		for (int i = 0; i < this.modelDetails.getRowCount(); i++) {
			total += (this.getSoLuongRow(i) * this.getDonGiaRow(i));
		}
		this.totalCostLabel.setText(this.currencyFormat.format(total));
	}

	private void lamMoi() {
		this.maPhieuField.setText("");
		this.ngayTaoField.setText("");

		NhanVien user = AppContext.getInstance().getCurrentUser();
		this.maNhanVienField.setText(user != null ? user.getMaNhanVien() : "");

		this.moTaField.setText("");
		this.moTaField.setEditable(true);

		if (this.modelDetails != null) {
			this.modelDetails.setRowCount(0);
		}
		if (this.totalCostLabel != null) {
			this.totalCostLabel.setText(this.currencyFormat.format(0));
		}
		this.moTaField.requestFocusInWindow();
	}

	private void luu() {
		if (this.tableDetails.isEditing() && !this.tableDetails.getCellEditor().stopCellEditing()) {
			return;
		}

		String moTa = this.moTaField.getText().trim();
		if (moTa.isBlank()) {
			ThongBao.canhBao(this, "app.phieuNhap.loiMoTa");
			this.moTaField.requestFocusInWindow();
			return;
		}

		if (this.modelDetails.getRowCount() == 0) {
			ThongBao.canhBao(this, "app.phieuNhap.loiTrongChiTiet");
			return;
		}

		List<ChiTietPhieuNhapPhuTung> listChiTiet = new ArrayList<>();
		for (int i = 0; i < this.modelDetails.getRowCount(); i++) {
			String maPhuTung = this.getMaFromRow(i);
			int qty = this.getSoLuongRow(i);
			double price = this.getDonGiaRow(i);

			if (maPhuTung.isBlank() || qty <= 0 || price < 0) {
				ThongBao.loi(this, "app.phieuNhap.loiDongNhapLieu");
				return;
			}

			ChiTietPhieuNhapPhuTung ct = new ChiTietPhieuNhapPhuTung();
			ct.setMaPhuTung(maPhuTung);
			ct.setSoLuong(qty);
			ct.setDonGiaNhap(price);
			listChiTiet.add(ct);
		}

		try {
			PhieuNhapPhuTung phieu = new PhieuNhapPhuTung();
			phieu.setMoTa(moTa);

			if (this.phieuNhapPhuTungService.create(phieu, listChiTiet)) {
				ThongBao.thongTin(this, "app.common.create_succeed");
				this.goTo(QuanLyPhieuNhapPage.class);
			}
		} catch (SQLException e) {
			ThongBao.loi(this, "app.login.errorSystem");
		} catch (IllegalArgumentException e) {
			ThongBao.loi(this, e.getMessage());
		}
	}

	private String getMaFromRow(int row) {
		String val = NumberParseUtil.textOf(this.modelDetails.getValueAt(row, 1));
		return val.contains(" - ") ? val.split(" - ")[0].trim() : val.trim();
	}

	private int getSoLuongRow(int row) {
		try {
			Integer val = NumberParseUtil.toIntegerOrNull(NumberParseUtil.textOf(this.modelDetails.getValueAt(row, 3)));
			return val != null ? val : 0;
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	private double getDonGiaRow(int row) {
		try {
			Double val = NumberParseUtil.toDoubleOrNull(NumberParseUtil.textOf(this.modelDetails.getValueAt(row, 4)));
			return val != null ? val : 0.0;
		} catch (NumberFormatException e) {
			return 0.0;
		}
	}

	private class SoNguyenCellEditor extends DefaultCellEditor {

		private final String errorKey;
		private final boolean laSoLuong;

		private SoNguyenCellEditor(String errorKey, boolean laSoLuong) {
			super(new JTextField());
			this.errorKey = errorKey;
			this.laSoLuong = laSoLuong;
		}

		@Override
		public boolean stopCellEditing() {
			String text = ((JTextField) this.getComponent()).getText().trim();
			if (!text.matches("\\d+")) {
				ThongBao.canhBao(LapPhieuNhapPage.this, this.errorKey);
				return false;
			}

			try {
				if (this.laSoLuong) {
					if (Integer.parseInt(text) == 0) {
						ThongBao.canhBao(LapPhieuNhapPage.this, this.errorKey);
						return false;
					}
				} else if (!Double.isFinite(Double.parseDouble(text))) {
					ThongBao.canhBao(LapPhieuNhapPage.this, this.errorKey);
					return false;
				}
			} catch (NumberFormatException e) {
				ThongBao.canhBao(LapPhieuNhapPage.this, this.errorKey);
				return false;
			}

			return super.stopCellEditing();
		}
	}

	@Override
	public String getTitleKey() {
		return "app.menu.partsReceipts";
	}

	@Override
	public MenuGroup getGroup() {
		return MenuGroup.NHAP_PHU_TUNG;
	}
}
