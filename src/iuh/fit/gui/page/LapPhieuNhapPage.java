package iuh.fit.gui.page;

import iuh.fit.dto.DanhSachPhuTungCriteria;
import iuh.fit.entity.ChiTietPhieuNhapPhuTung;
import iuh.fit.entity.PhieuNhapPhuTung;
import iuh.fit.entity.PhuTung;
import iuh.fit.enumeration.MenuGroup;
import iuh.fit.gui.Theme;
import iuh.fit.gui.component.ChonPhuTungDialog;
import iuh.fit.gui.component.ChonSanPhamDialog;
import iuh.fit.gui.component.ManagementFormPanel;
import iuh.fit.gui.component.ThaoTac;
import iuh.fit.gui.component.ThaoTacTableCellRenderer;
import iuh.fit.gui.component.ThongBao;
import iuh.fit.service.PhieuNhapPhuTungService;
import iuh.fit.service.PhuTungService;
import iuh.fit.util.I18nReaderUtil;
import iuh.fit.util.NumberParseUtil;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class LapPhieuNhapPage extends AbstractPage {

	private final PhuTungService phuTungService = new PhuTungService();
	private final PhieuNhapPhuTungService phieuNhapPhuTungService = new PhieuNhapPhuTungService();

	private final DecimalFormat currencyFormat = new DecimalFormat("#,##0");

	private final JTextField txtMoTa = new JTextField();

	private DefaultTableModel modelDetails;
	private JTable tblChiTiet;
	private JLabel lblTotalCost;
	private JButton btnSave;
	private JButton btnAddRow;
	private JButton btnXoaDaChon;

	public LapPhieuNhapPage() {
		this.setLayout(new BorderLayout(8, 8));
	}

	@Override
	public void render() {
		this.removeAll();
		this.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
		this.setBackground(Theme.BG);

		ManagementFormPanel form = new ManagementFormPanel("app.phieuNhap.thongTinPhieuNhap");
		form.addField("app.phieuNhap.moTa", this.txtMoTa);

		JButton btnReset = form.getNutXoaTrang();
		this.btnSave = form.getNutLuu();
		btnReset.setText(I18nReaderUtil.get("app.common.clear"));
		this.btnSave.setText(I18nReaderUtil.get("app.phieuNhap.luuPhieuNhap"));
		this.clearActionListeners(btnReset, this.btnSave);
		btnReset.addActionListener(e -> this.xoaTrang());
		this.btnSave.addActionListener(e -> this.luu());
		form.thayNut(btnReset, this.btnSave);

		JPanel right = new JPanel(new BorderLayout(4, 4));
		right.setOpaque(false);

		JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));
		topBar.setOpaque(false);
		this.btnAddRow = new JButton(I18nReaderUtil.get("app.phieuNhap.themDong"));
		Theme.applyPrimary(this.btnAddRow);
		this.btnAddRow.addActionListener(e -> this.themDong());
		topBar.add(this.btnAddRow);
		this.btnXoaDaChon = new JButton(I18nReaderUtil.get("app.common.batch_delete"));
		Theme.applyDanger(this.btnXoaDaChon);
		this.btnXoaDaChon.setEnabled(false);
		this.btnXoaDaChon.addActionListener(e -> this.xoaDongDaChon());
		topBar.add(this.btnXoaDaChon);
		right.add(topBar, BorderLayout.NORTH);

		String[] headers = { "", "#", I18nReaderUtil.get("app.phuTung.ma"), I18nReaderUtil.get("app.phuTung.ten"),
				I18nReaderUtil.get("app.phieuNhap.soLuong"), I18nReaderUtil.get("app.phieuNhap.donGia"),
				I18nReaderUtil.get("app.phieuNhap.thanhTien"), I18nReaderUtil.get("app.common.actions") };

		this.modelDetails = new DefaultTableModel(headers, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return column == 0
						|| (LapPhieuNhapPage.this.btnAddRow.isEnabled() && (column == 4 || column == 5));
			}

			@Override
			public Class<?> getColumnClass(int column) {
				return column == 0 ? Boolean.class : Object.class;
			}
		};

		this.tblChiTiet = new JTable(this.modelDetails);
		Theme.styleTable(this.tblChiTiet);
		this.tblChiTiet.getColumnModel().getColumn(0).setMinWidth(44);
		this.tblChiTiet.getColumnModel().getColumn(0).setMaxWidth(44);
		this.tblChiTiet.getColumnModel().getColumn(0).setPreferredWidth(44);
		this.tblChiTiet.getColumnModel().getColumn(4)
				.setCellEditor(new SoNguyenCellEditor("app.phieuNhap.loiSoLuong", true));
		this.tblChiTiet.getColumnModel().getColumn(5)
				.setCellEditor(new SoNguyenCellEditor("app.phieuNhap.loiDonGia", false));
		ThaoTacTableCellRenderer.ganVao(this.tblChiTiet, 7, List.of(
				new ThaoTac("trash-2", "app.common.delete", ThaoTac.KieuThaoTac.NGUY_HIEM,
						row -> this.xoaDong(row))));
		this.modelDetails.addTableModelListener(e -> this.tinhThanhTienDong(e.getFirstRow(), e.getColumn()));
		this.modelDetails.addTableModelListener(e -> this.capNhatNutXoaDaChon());

		right.add(new JScrollPane(this.tblChiTiet), BorderLayout.CENTER);

		JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 6));
		bottomPanel.setOpaque(false);
		JLabel lblTitle = new JLabel(I18nReaderUtil.get("app.phieuNhap.tongChiPhi") + ":");
		lblTitle.setForeground(Theme.palette().INK_SECONDARY());
		this.lblTotalCost = new JLabel(this.currencyFormat.format(0));
		this.lblTotalCost.setForeground(Theme.INK);
		bottomPanel.add(lblTitle);
		bottomPanel.add(this.lblTotalCost);
		right.add(bottomPanel, BorderLayout.SOUTH);

		JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, form, right);
		split.setDividerLocation(420);
		split.setOpaque(false);
		split.setBorder(null);
		this.add(split, BorderLayout.CENTER);

		this.lamMoi();

		this.revalidate();
		this.repaint();
	}

	private void xoaTrang() {
		if (this.isDirty() && !ThongBao.xacNhan(this, "app.common.confirmClear", "app.common.confirm")) {
			return;
		}
		this.lamMoi();
	}

	private boolean isDirty() {
		return !this.txtMoTa.getText().isBlank() || this.modelDetails.getRowCount() > 0;
	}

	private void themDong() {
		ChonSanPhamDialog.KetQuaChon<PhuTung> kq = new ChonPhuTungDialog(
				SwingUtilities.getWindowAncestor(this),
				this.phuTungService,
				DanhSachPhuTungCriteria.macDinh()).showDialog().orElse(null);
		if (kq == null) {
			return;
		}
		for (int i = 0; i < this.modelDetails.getRowCount(); i++) {
			if (this.getMaFromRow(i).equalsIgnoreCase(kq.sanPham().getMaPhuTung())) {
				int soLuongMoi = this.getSoLuongRow(i) + kq.soLuong();
				this.modelDetails.setValueAt(soLuongMoi, i, 4);
				double giaMoi = kq.sanPham().getDonGiaNhap() != null ? kq.sanPham().getDonGiaNhap() : 0.0;
				this.modelDetails.setValueAt(giaMoi, i, 5);
				this.tinhTongChiPhi();
				return;
			}
		}

		double giaNhap = kq.sanPham().getDonGiaNhap() != null ? kq.sanPham().getDonGiaNhap() : 0.0;
		this.modelDetails.addRow(new Object[] { Boolean.FALSE, this.modelDetails.getRowCount() + 1,
				kq.sanPham().getMaPhuTung(),
				kq.sanPham().getTenPhuTung(), kq.soLuong(), giaNhap,
				this.currencyFormat.format(kq.soLuong() * giaNhap) });
		this.tinhTongChiPhi();
	}

	private void xoaDong(int row) {
		if (row < 0 || row >= this.modelDetails.getRowCount()) {
			return;
		}
		if (!ThongBao.xacNhan(this, "app.phieuNhap.xacNhanXoaDong", "app.phieuNhap.tieuDeXoaDong")) {
			return;
		}

		this.modelDetails.removeRow(row);
		for (int i = 0; i < this.modelDetails.getRowCount(); i++) {
			this.modelDetails.setValueAt(i + 1, i, 1);
		}
		this.tinhTongChiPhi();
	}

	private void xoaDongDaChon() {
		List<Integer> dongDaChon = new ArrayList<>();
		for (int i = this.modelDetails.getRowCount() - 1; i >= 0; i--) {
			if (Boolean.TRUE.equals(this.modelDetails.getValueAt(i, 0))) {
				dongDaChon.add(i);
			}
		}
		if (dongDaChon.isEmpty()) {
			ThongBao.canhBao(this, "app.phieuNhap.loiTrongChiTiet");
			return;
		}
		if (!ThongBao.xacNhanDinhDang(this, "app.common.batch_delete_confirm", "app.common.confirm",
				dongDaChon.size())) {
			return;
		}

		for (int i : dongDaChon) {
			if (i >= 0 && i < this.modelDetails.getRowCount()) {
				this.modelDetails.removeRow(i);
			}
		}
		for (int i = 0; i < this.modelDetails.getRowCount(); i++) {
			this.modelDetails.setValueAt(i + 1, i, 1);
		}
		this.tinhTongChiPhi();
	}

	private void capNhatNutXoaDaChon() {
		if (this.btnXoaDaChon == null) {
			return;
		}

		int soDong = 0;
		for (int i = 0; i < this.modelDetails.getRowCount(); i++) {
			if (Boolean.TRUE.equals(this.modelDetails.getValueAt(i, 0))) {
				soDong++;
			}
		}
		this.btnXoaDaChon.setText(I18nReaderUtil.get("app.common.batch_delete") + " (" + soDong + ")");
		this.btnXoaDaChon.setEnabled(soDong > 0);
	}

	private void tinhThanhTienDong(int row, int col) {
		if (row < 0 || row >= this.modelDetails.getRowCount()) {
			return;
		}

		if (col == 4 || col == 5) {
			double thanhTien = this.getSoLuongRow(row) * this.getDonGiaRow(row);
			this.modelDetails.setValueAt(this.currencyFormat.format(thanhTien), row, 6);
			this.tinhTongChiPhi();
		}
	}

	private void tinhTongChiPhi() {
		double total = 0.0;
		for (int i = 0; i < this.modelDetails.getRowCount(); i++) {
			total += (this.getSoLuongRow(i) * this.getDonGiaRow(i));
		}
		this.lblTotalCost.setText(this.currencyFormat.format(total));
	}

	private void lamMoi() {
		this.txtMoTa.setText("");
		this.txtMoTa.setEditable(true);

		if (this.modelDetails != null) {
			this.modelDetails.setRowCount(0);
		}
		if (this.lblTotalCost != null) {
			this.lblTotalCost.setText(this.currencyFormat.format(0));
		}
		this.txtMoTa.requestFocusInWindow();
	}

	private void luu() {
		if (this.tblChiTiet.isEditing() && !this.tblChiTiet.getCellEditor().stopCellEditing()) {
			return;
		}

		String moTa = this.txtMoTa.getText().trim();
		if (moTa.isBlank()) {
			ThongBao.canhBao(this, "app.phieuNhap.loiMoTa");
			this.txtMoTa.requestFocusInWindow();
			return;
		}

		if (this.modelDetails.getRowCount() == 0) {
			ThongBao.canhBao(this, "app.phieuNhap.loiTrongChiTiet");
			return;
		}

		try {
			List<ChiTietPhieuNhapPhuTung> listChiTiet = new ArrayList<>();
			for (int i = 0; i < this.modelDetails.getRowCount(); i++) {
				String maPhuTung = this.getMaFromRow(i);
				int qty = this.getSoLuongRow(i);
				double price = this.getDonGiaRow(i);

				if (maPhuTung.isBlank()) {
					throw new IllegalArgumentException("app.phieuNhap.loiPhuTung");
				}
				if (qty <= 0) {
					throw new IllegalArgumentException("app.phieuNhap.loiSoLuong");
				}
				if (price < 0 || !Double.isFinite(price)) {
					throw new IllegalArgumentException("app.phieuNhap.loiDonGia");
				}

				ChiTietPhieuNhapPhuTung ct = new ChiTietPhieuNhapPhuTung();
				ct.setMaPhuTung(maPhuTung);
				ct.setSoLuong(qty);
				ct.setDonGiaNhap(price);
				listChiTiet.add(ct);
			}

			PhieuNhapPhuTung phieu = new PhieuNhapPhuTung();
			phieu.setMoTa(moTa);

			if (this.phieuNhapPhuTungService.create(phieu, listChiTiet)) {
				ThongBao.thongTin(this, "app.common.create_succeed");
				this.goTo(QuanLyPhieuNhapPage.class);
			}
		} catch (SQLException e) {
			ThongBao.loi(this, "app.login.errorSystem");
		} catch (IllegalArgumentException e) {
			ThongBao.loi(this, e);
		}
	}

	private String getMaFromRow(int row) {
		String val = NumberParseUtil.textOf(this.modelDetails.getValueAt(row, 2));
		return val.contains(" - ") ? val.split(" - ")[0].trim() : val.trim();
	}

	private int getSoLuongRow(int row) {
		try {
			Integer val = NumberParseUtil.toIntegerOrNull(NumberParseUtil.textOf(this.modelDetails.getValueAt(row, 4)));
			return val != null ? val : 0;
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	private double getDonGiaRow(int row) {
		try {
			Double val = NumberParseUtil.toDoubleOrNull(NumberParseUtil.textOf(this.modelDetails.getValueAt(row, 5)));
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
			JTextField field = (JTextField) this.getComponent();
			((AbstractDocument) field.getDocument()).setDocumentFilter(new DocumentFilter() {
				@Override
				public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
						throws BadLocationException {
					if (string == null) {
						return;
					}
					StringBuilder proposed = new StringBuilder(fb.getDocument().getText(0, fb.getDocument().getLength()));
					proposed.insert(offset, string);
					if (proposed.toString().matches("\\d*")) {
						super.insertString(fb, offset, string, attr);
					}
				}

				@Override
				public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
						throws BadLocationException {
					StringBuilder proposed = new StringBuilder(fb.getDocument().getText(0, fb.getDocument().getLength()));
					proposed.replace(offset, offset + length, text == null ? "" : text);
					if (proposed.toString().matches("\\d*")) {
						super.replace(fb, offset, length, text, attrs);
					}
				}
			});
		}

		@Override
		public boolean stopCellEditing() {
			JTextField field = (JTextField) this.getComponent();
			String text = field.getText().trim();
			if (text.isEmpty() || !text.matches("\\d+")) {
				ThongBao.canhBao(LapPhieuNhapPage.this, "app.phieuNhap.loiDinhDangSo");
				field.setText(NumberParseUtil.textOf(LapPhieuNhapPage.this.modelDetails.getValueAt(
						LapPhieuNhapPage.this.tblChiTiet.getEditingRow(),
						LapPhieuNhapPage.this.tblChiTiet.getEditingColumn())));
				return super.stopCellEditing();
			}

			try {
				if (this.laSoLuong) {
					if (Integer.parseInt(text) == 0) {
						ThongBao.canhBao(LapPhieuNhapPage.this, this.errorKey);
						field.setText(NumberParseUtil.textOf(LapPhieuNhapPage.this.modelDetails.getValueAt(
								LapPhieuNhapPage.this.tblChiTiet.getEditingRow(),
								LapPhieuNhapPage.this.tblChiTiet.getEditingColumn())));
						return super.stopCellEditing();
					}
				} else if (!Double.isFinite(Double.parseDouble(text))) {
					ThongBao.canhBao(LapPhieuNhapPage.this, this.errorKey);
					field.setText(NumberParseUtil.textOf(LapPhieuNhapPage.this.modelDetails.getValueAt(
							LapPhieuNhapPage.this.tblChiTiet.getEditingRow(),
							LapPhieuNhapPage.this.tblChiTiet.getEditingColumn())));
					return super.stopCellEditing();
				}
			} catch (NumberFormatException e) {
				ThongBao.canhBao(LapPhieuNhapPage.this, this.errorKey);
				field.setText(NumberParseUtil.textOf(LapPhieuNhapPage.this.modelDetails.getValueAt(
						LapPhieuNhapPage.this.tblChiTiet.getEditingRow(),
						LapPhieuNhapPage.this.tblChiTiet.getEditingColumn())));
				return super.stopCellEditing();
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
