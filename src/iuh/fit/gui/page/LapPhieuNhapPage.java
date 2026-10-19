package iuh.fit.gui.page;

import iuh.fit.core.context.AppContext;
import iuh.fit.dto.DanhSachPhuTungCriteria;
import iuh.fit.entity.ChiTietPhieuNhapPhuTung;
import iuh.fit.entity.NhanVien;
import iuh.fit.entity.PhieuNhapPhuTung;
import iuh.fit.entity.PhuTung;
import iuh.fit.enumeration.MenuGroup;
import iuh.fit.gui.component.ThongBao;
import iuh.fit.service.PhieuNhapPhuTungService;
import iuh.fit.service.PhuTungService;
import iuh.fit.util.I18nReaderUtil;
import iuh.fit.util.NumberParseUtil;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
	private JButton btnConfirm;
	private JButton btnAddRow;
	private JButton btnRemoveRow;

	private List<PhuTung> partsCache = new ArrayList<>();
	private static String maPhieuTam = null;

	public static void truyenMaPhieu(String maPhieu) {
		maPhieuTam = maPhieu;
	}

	private static String layVaXoaMaPhieu() {
		String ma = maPhieuTam;
		maPhieuTam = null;
		return ma;
	}

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

		String maCanSua = LapPhieuNhapPage.layVaXoaMaPhieu();
		if (maCanSua != null && !maCanSua.isBlank()) {
			this.hienThiChiTiet(maCanSua);
		} else {
			this.lamMoi();
		}

		this.revalidate();
		this.repaint();
	}

	private void taiDanhSachPhuTung() {
		try {
			this.partsCache = this.phuTungService.search(DanhSachPhuTungCriteria.macDinh()).items();
		} catch (SQLException e) {
			this.partsCache = new ArrayList<>();
			ThongBao.loi(this, "app.login.errorSystem");
		}
	}

	private JPanel taoPanelThongTin() {
		JPanel panel = new JPanel(new BorderLayout(0, 8));
		panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
				I18nReaderUtil.get("app.phieuNhap.thongTinPhieuNhap"), TitledBorder.LEFT, TitledBorder.TOP));

		JPanel formPanel = new JPanel();
		formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
		formPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

		this.maPhieuField.setEditable(false);
		this.ngayTaoField.setEditable(false);
		this.maNhanVienField.setEditable(false);

		this.themDongBox(formPanel, "app.phieuNhap.maPhieuNhap", this.maPhieuField);
		this.themDongBox(formPanel, "app.phieuNhap.ngayTao", this.ngayTaoField);
		this.themDongBox(formPanel, "app.phieuNhap.thuKho", this.maNhanVienField);
		this.themDongBox(formPanel, "app.phieuNhap.moTa", this.moTaField);
		panel.add(formPanel, BorderLayout.NORTH);

		JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
		JButton btnReset = new JButton(I18nReaderUtil.get("app.common.clear"));
		this.btnSave = new JButton(I18nReaderUtil.get("app.phieuNhap.luuPhieuNhap"));
		this.btnSave.setBackground(Color.BLUE);
		this.btnSave.setForeground(Color.WHITE);

		btnReset.setPreferredSize(new Dimension(150, 32));
		this.btnSave.setPreferredSize(new Dimension(150, 32));
		btnReset.addActionListener(e -> this.xoaTrang());
		this.btnSave.addActionListener(e -> this.luu(false));

		actionPanel.add(btnReset);
		actionPanel.add(this.btnSave);
		panel.add(actionPanel, BorderLayout.SOUTH);
		return panel;
	}

	private void xoaTrang() {
		String maHienTai = this.maPhieuField.getText().trim();
		if (!maHienTai.isEmpty()) {
			this.hienThiChiTiet(maHienTai);
		} else {
			this.lamMoi();
		}
	}

	private void themDongBox(JPanel container, String i18nKey, JTextField field) {
		JLabel label = new JLabel(I18nReaderUtil.get(i18nKey));
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
		panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
				I18nReaderUtil.get("app.phieuNhap.danhSachChiTiet"), TitledBorder.LEFT, TitledBorder.TOP));

		JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));
		this.btnAddRow = new JButton(I18nReaderUtil.get("app.phieuNhap.themDong"));
		this.btnAddRow.setBackground(Color.BLUE);
		this.btnAddRow.setForeground(Color.WHITE);
		this.btnRemoveRow = new JButton(I18nReaderUtil.get("app.phieuNhap.xoaDong"));
		this.btnRemoveRow.setForeground(Color.RED);

		this.btnAddRow.addActionListener(e -> this.themDong());
		this.btnRemoveRow.addActionListener(e -> this.xoaDongDaChon());
		topBar.add(this.btnAddRow);
		topBar.add(this.btnRemoveRow);
		panel.add(topBar, BorderLayout.NORTH);

		String[] headers = { "#", I18nReaderUtil.get("app.phuTung.ma"), I18nReaderUtil.get("app.phuTung.ten"),
				I18nReaderUtil.get("app.phieuNhap.soLuong"), I18nReaderUtil.get("app.phieuNhap.donGia"),
				I18nReaderUtil.get("app.phieuNhap.thanhTien") };

		this.modelDetails = new DefaultTableModel(headers, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return LapPhieuNhapPage.this.btnAddRow.isEnabled() && (column == 1 || column == 3 || column == 4);
			}
		};

		this.tableDetails = new JTable(this.modelDetails);
		this.tableDetails.setRowHeight(28);

		JComboBox<String> cboParts = new JComboBox<>();
		for (PhuTung item : this.partsCache) {
			cboParts.addItem(item.getMaPhuTung() + " - " + item.getTenPhuTung());
		}
		this.tableDetails.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(cboParts));
		this.modelDetails.addTableModelListener(e -> this.tinhThanhTienDong(e.getFirstRow(), e.getColumn()));

		panel.add(new JScrollPane(this.tableDetails), BorderLayout.CENTER);

		JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
		JLabel lblTitle = new JLabel(I18nReaderUtil.get("app.phieuNhap.tongChiPhi") + ":");

		this.btnConfirm = new JButton(I18nReaderUtil.get("app.phieuNhap.xacNhanHoanTat"));
		this.btnConfirm.setForeground(Color.WHITE);
		this.btnConfirm.setBackground(Color.BLUE);
		this.btnConfirm.addActionListener(e -> this.luu(true));

		this.totalCostLabel = new JLabel(this.currencyFormat.format(0));
		this.totalCostLabel.setPreferredSize(new Dimension(640, 30));

		bottomPanel.add(lblTitle);
		bottomPanel.add(this.totalCostLabel);
		bottomPanel.add(this.btnConfirm);
		panel.add(bottomPanel, BorderLayout.SOUTH);

		return panel;
	}

	private void themDong() {
		if (this.partsCache.isEmpty()) {
			ThongBao.canhBao(this, "app.phieuNhap.loiPhuTungTrong");
			return;
		}

		PhuTung ptChuaChon = this.partsCache.stream().filter(pt -> {
			for (int i = 0; i < this.modelDetails.getRowCount(); i++) {
				if (pt.getMaPhuTung().equalsIgnoreCase(this.getMaFromRow(i))) {
					return false;
				}
			}
			return true;
		}).findFirst().orElse(null);

		if (ptChuaChon == null) {
			ThongBao.canhBao(this, "app.phieuNhap.daThemTatCaPhuTung");
			return;
		}

		double giaNhap = ptChuaChon.getDonGiaNhap() != null ? ptChuaChon.getDonGiaNhap() : 0.0;
		this.modelDetails.addRow(new Object[] { this.modelDetails.getRowCount() + 1,
				ptChuaChon.getMaPhuTung() + " - " + ptChuaChon.getTenPhuTung(), ptChuaChon.getTenPhuTung(), 1, giaNhap,
				this.currencyFormat.format(giaNhap) });
		this.tinhTongChiPhi();
	}

	private void xoaDongDaChon() {
		int selectedRow = this.tableDetails.getSelectedRow();
		if (selectedRow < 0) {
			ThongBao.canhBao(this, "app.phieuNhap.chuaChonDongXoa");
			return;
		}

		this.modelDetails.removeRow(selectedRow);
		for (int i = 0; i < this.modelDetails.getRowCount(); i++) {
			this.modelDetails.setValueAt(i + 1, i, 0);
		}
		this.tinhTongChiPhi();
	}

	private void tinhThanhTienDong(int row, int col) {
		if (row < 0 || row >= this.modelDetails.getRowCount()) {
			return;
		}

		if (col == 1) {
			String code = this.getMaFromRow(row);
			for (int i = 0; i < this.modelDetails.getRowCount(); i++) {
				if (i != row && code.equalsIgnoreCase(this.getMaFromRow(i))) {
					String msg = I18nReaderUtil.get("app.phieuNhap.loiTrungPhuTungTienTo") + (i + 1)
							+ I18nReaderUtil.get("app.phieuNhap.loiTrungPhuTungHauTo");
					ThongBao.canhBao(this, msg);

					String tenCu = NumberParseUtil.textOf(this.modelDetails.getValueAt(row, 2));
					this.partsCache.stream().filter(pt -> pt.getTenPhuTung().equalsIgnoreCase(tenCu)).findFirst()
							.ifPresent(pt -> this.modelDetails
									.setValueAt(pt.getMaPhuTung() + " - " + pt.getTenPhuTung(), row, 1));
					return;
				}
			}

			this.partsCache.stream().filter(pt -> pt.getMaPhuTung().equalsIgnoreCase(code)).findFirst()
					.ifPresent(pt -> {
						double giaNhap = pt.getDonGiaNhap() != null ? pt.getDonGiaNhap() : 0.0;
						this.modelDetails.setValueAt(pt.getTenPhuTung(), row, 2);
						this.modelDetails.setValueAt(giaNhap, row, 4);
					});
		}

		if (col == 1 || col == 3 || col == 4) {
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

	private void hienThiChiTiet(String maPhieu) {
		try {
			Optional<PhieuNhapPhuTung> opt = this.phieuNhapPhuTungService.getById(maPhieu);
			if (opt.isEmpty()) {
				ThongBao.loi(this, "app.common.notFound");
				this.lamMoi();
				return;
			}

			PhieuNhapPhuTung phieu = opt.get();
			this.maPhieuField.setText(phieu.getMaPhieuNhap());
			this.ngayTaoField
					.setText(phieu.getNgayTao() != null
							? phieu.getNgayTao().format(
									DateTimeFormatter.ofPattern(I18nReaderUtil.get("app.date.format") + " HH:mm"))
							: "");
			this.maNhanVienField.setText(phieu.getMaNhanVien());
			this.moTaField.setText(phieu.getMoTa());
			this.moTaField.setEditable(true);

			this.btnSave.setEnabled(true);
			this.btnConfirm.setEnabled(true);
			this.btnAddRow.setEnabled(true);
			this.btnRemoveRow.setEnabled(true);

			if (this.modelDetails != null) {
				this.modelDetails.setRowCount(0);
				List<ChiTietPhieuNhapPhuTung> list = phieu.getDanhSachChiTietPhieuNhap();
				if (list != null) {
					int stt = 1;
					for (ChiTietPhieuNhapPhuTung ct : list) {
						String tenPt = this.partsCache.stream()
								.filter(pt -> pt.getMaPhuTung().equalsIgnoreCase(ct.getMaPhuTung()))
								.map(PhuTung::getTenPhuTung).findFirst().orElse(ct.getMaPhuTung());

						double gia = ct.getDonGiaNhap() != null ? ct.getDonGiaNhap() : 0.0;
						double thanhTien = ct.getSoLuong() * gia;

						this.modelDetails.addRow(new Object[] { stt++, ct.getMaPhuTung() + " - " + tenPt, tenPt,
								ct.getSoLuong(), gia, this.currencyFormat.format(thanhTien) });
					}
				}
				this.tinhTongChiPhi();
			}
		} catch (SQLException e) {
			ThongBao.loi(this, "app.login.errorSystem");
		}
	}

	private void lamMoi() {
		this.maPhieuField.setText("");
		this.ngayTaoField.setText("");

		NhanVien user = AppContext.getInstance().getCurrentUser();
		this.maNhanVienField.setText(user != null ? user.getMaNhanVien() : "");

		this.moTaField.setText("");
		this.moTaField.setEditable(true);
		this.moTaField.requestFocusInWindow();

		if (this.btnSave != null)
			this.btnSave.setEnabled(true);
		if (this.btnConfirm != null)
			this.btnConfirm.setEnabled(true);
		if (this.btnAddRow != null)
			this.btnAddRow.setEnabled(true);
		if (this.btnRemoveRow != null)
			this.btnRemoveRow.setEnabled(true);

		if (this.modelDetails != null) {
			this.modelDetails.setRowCount(0);
		}
		if (this.totalCostLabel != null) {
			this.totalCostLabel.setText(this.currencyFormat.format(0));
		}
	}

	private void luu(boolean hoanTat) {
		if (this.tableDetails.isEditing()) {
			this.tableDetails.getCellEditor().stopCellEditing();
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

		if (hoanTat && !ThongBao.xacNhan(this, "app.phieuNhap.xacNhanHoanTatMsg", "app.phieuNhap.tieuDeXacNhan")) {
			return;
		}

		try {
			PhieuNhapPhuTung phieu = new PhieuNhapPhuTung();
			String maHienTai = this.maPhieuField.getText().trim();
			phieu.setMaPhieuNhap(maHienTai.isEmpty() ? null : maHienTai);
			phieu.setMaNhanVien(this.maNhanVienField.getText().trim());
			phieu.setMoTa(moTa);

			boolean thanhCong;
			if (phieu.getMaPhieuNhap() != null) {
				thanhCong = this.phieuNhapPhuTungService.update(phieu, listChiTiet, hoanTat);
			} else {
				thanhCong = this.phieuNhapPhuTungService.create(phieu, listChiTiet, hoanTat);
			}

			if (thanhCong) {
				ThongBao.thongTin(this, hoanTat ? "app.phieuNhap.hoanTatThanhCong" : "app.common.create_succeed");
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
		Integer val = NumberParseUtil.toIntegerOrNull(NumberParseUtil.textOf(this.modelDetails.getValueAt(row, 3)));
		return val != null ? val : 0;
	}

	private double getDonGiaRow(int row) {
		Double val = NumberParseUtil.toDoubleOrNull(NumberParseUtil.textOf(this.modelDetails.getValueAt(row, 4)));
		return val != null ? val : 0.0;
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