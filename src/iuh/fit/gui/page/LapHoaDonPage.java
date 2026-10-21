package iuh.fit.gui.page;

import iuh.fit.dto.DanhSachDichVuCriteria;
import iuh.fit.dto.DanhSachPhieuTiepNhanCriteria;
import iuh.fit.dto.DanhSachVoucherCriteria;
import iuh.fit.entity.ChiTietHoaDon;
import iuh.fit.entity.DichVu;
import iuh.fit.entity.HoaDon;
import iuh.fit.entity.PhuTung;
import iuh.fit.entity.Voucher;
import iuh.fit.enumeration.LoaiSanPham;
import iuh.fit.enumeration.MenuGroup;
import iuh.fit.enumeration.TrangThaiHoaDon;
import iuh.fit.gui.component.ChonPhuTungDialog;
import iuh.fit.gui.component.ManagementFormPanel;
import iuh.fit.gui.component.ThaoTac;
import iuh.fit.gui.component.ThaoTacTableCellRenderer;
import iuh.fit.gui.component.ThongBao;
import iuh.fit.gui.theme.Theme;
import iuh.fit.service.DichVuService;
import iuh.fit.service.HoaDonService;
import iuh.fit.service.PhieuTiepNhanService;
import iuh.fit.service.PhuTungService;
import iuh.fit.service.VoucherService;
import iuh.fit.util.I18nReaderUtil;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LapHoaDonPage extends AbstractPage {

	private final HoaDonService hoaDonService = new HoaDonService();
	private final PhieuTiepNhanService phieuTiepNhanService = new PhieuTiepNhanService();
	private final VoucherService voucherService = new VoucherService();
	private final DichVuService dichVuService = new DichVuService();
	private final PhuTungService phuTungService = new PhuTungService();

	private final DecimalFormat currencyFormat = new DecimalFormat("#,##0");
	private final DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

	private final JComboBox<String> cbPhieu = new JComboBox<>();
	private final JComboBox<String> cbVoucher = new JComboBox<>();
	private final JTextField ghiChuField = new JTextField();
	private final JTextField trangThaiField = new JTextField();
	private final JTextField tongThanhToanField = new JTextField();
	private final JTextField thoiGianTTField = new JTextField();
	private final JTextField lyDoHuyField = new JTextField();

	private final DefaultTableModel lineModel = new DefaultTableModel(0, 0) {
		@Override
		public boolean isCellEditable(int row, int column) {
			return false;
		}
	};
	private final JTable lineTable = new JTable(this.lineModel);

	private final List<ChiTietHoaDon> chiTiets = new ArrayList<>();
	private final List<String> tenSanPham = new ArrayList<>();
	private final Map<String, Integer> soLuongGoc = new HashMap<>();
	private final Map<String, Voucher> voucherTheoMa = new HashMap<>();

	private JLabel lblTong;
	private JLabel lblGiamGia;
	private JLabel lblThanhToan;
	private JButton nutHuy;
	private JButton nutLuu;
	private JButton nutThanhToan;
	private JButton nutThemDV;
	private JButton nutThemPT;
	private String maHoaDonHienTai;
	private TrangThaiHoaDon trangThaiHienTai;
	private boolean dangNap;

	public LapHoaDonPage() {
		this.setLayout(new BorderLayout());
	}

	@Override
	public void render() {
		this.removeAll();
		this.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
		this.setBackground(Theme.BG);

		ManagementFormPanel form = new ManagementFormPanel("app.hoaDon.thongTinTitle");

		this.loadPhieuCombo();
		this.loadVoucherCombo();

		Theme.applyReadOnly(this.trangThaiField);
		Theme.applyReadOnly(this.tongThanhToanField);
		Theme.applyReadOnly(this.thoiGianTTField);
		Theme.applyReadOnly(this.lyDoHuyField);

		form.addField("app.hoaDon.maPhieuTiepNhan", this.cbPhieu);
		form.addField("app.hoaDon.voucher", this.cbVoucher);
		form.addField("app.hoaDon.ghiChu", this.ghiChuField);
		form.addField("app.hoaDon.trangThai", this.trangThaiField);
		form.addField("app.hoaDon.thoiGianThanhToan", this.thoiGianTTField);
		form.addField("app.hoaDon.lyDoHuy", this.lyDoHuyField);
		form.addField("app.hoaDon.tongThanhToan", this.tongThanhToanField);

		this.trangThaiHienTai = this.trangThaiHienTai == null ? TrangThaiHoaDon.CHO_THANH_TOAN : this.trangThaiHienTai;
		this.trangThaiField.setText(this.trangThaiHienTai.getI18NBusinessName());

		this.nutHuy = form.getNutXoa();
		this.nutLuu = form.getNutLuu();
		this.nutThanhToan = form.getNutXoaTrang();

		this.nutHuy.setText(I18nReaderUtil.get("app.hoaDon.huyHoaDon"));
		this.nutLuu.setText(I18nReaderUtil.get("app.hoaDon.luuHoaDon"));
		this.nutThanhToan.setText(I18nReaderUtil.get("app.hoaDon.xacNhanThanhToan"));

		Theme.applyDanger(this.nutHuy);
		Theme.applyPrimary(this.nutLuu);
		Theme.applyPrimary(this.nutThanhToan);

		this.nutHuy.addActionListener(e -> this.huyHoaDon());
		this.nutLuu.addActionListener(e -> this.luuHoaDon());
		this.nutThanhToan.addActionListener(e -> this.xacNhanThanhToan());

		form.thayNut(this.nutHuy, this.nutLuu, this.nutThanhToan);

		JPanel right = new JPanel(new BorderLayout(4, 4));
		right.setOpaque(false);

		JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
		btnRow.setOpaque(false);
		this.nutThemDV = new JButton(I18nReaderUtil.get("app.hoaDon.themDichVu"));
		this.nutThemPT = new JButton(I18nReaderUtil.get("app.hoaDon.themPhuTung"));
		Theme.applyPrimary(this.nutThemDV);
		Theme.applyPrimary(this.nutThemPT);
		this.nutThemDV.addActionListener(e -> this.themDichVu());
		this.nutThemPT.addActionListener(e -> this.themPhuTung());
		btnRow.add(this.nutThemDV);
		btnRow.add(this.nutThemPT);

		this.lineModel.setColumnIdentifiers(new Object[] { I18nReaderUtil.get("app.hoaDon.loai"),
				I18nReaderUtil.get("app.phuTung.ma"), I18nReaderUtil.get("app.phuTung.ten"),
				I18nReaderUtil.get("app.phieuNhap.soLuong"), I18nReaderUtil.get("app.phieuNhap.donGia"),
				I18nReaderUtil.get("app.phieuNhap.thanhTien"), I18nReaderUtil.get("app.common.actions") });
		Theme.styleTable(this.lineTable);
		ThaoTacTableCellRenderer.ganVao(this.lineTable, 6, List.of(
				new ThaoTac("trash-2", "app.common.delete", ThaoTac.KieuThaoTac.NGUY_HIEM, row -> this.xoaDong(row))));

		right.add(btnRow, BorderLayout.NORTH);
		right.add(new JScrollPane(this.lineTable), BorderLayout.CENTER);

		JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
		footer.setOpaque(false);
		this.lblTong = new JLabel("0");
		this.lblGiamGia = new JLabel("0");
		this.lblThanhToan = new JLabel("0");
		footer.add(new JLabel(I18nReaderUtil.get("app.hoaDon.tongTienGoc")));
		footer.add(this.lblTong);
		footer.add(new JLabel(I18nReaderUtil.get("app.hoaDon.giamGia")));
		footer.add(this.lblGiamGia);
		footer.add(new JLabel(I18nReaderUtil.get("app.hoaDon.tongThanhToan")));
		footer.add(this.lblThanhToan);
		right.add(footer, BorderLayout.SOUTH);

		JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, form, right);
		split.setDividerLocation(420);
		split.setOpaque(false);
		split.setBorder(null);
		this.add(split, BorderLayout.CENTER);

		this.clearActionListeners(this.cbPhieu, this.cbVoucher);
		this.cbPhieu.addActionListener(e -> this.chonPhieu());
		this.cbVoucher.addActionListener(e -> this.chonVoucherThayDoi());

		this.chonPhieu();
		this.refreshLineTable();
		this.capNhatQuyen();

		this.revalidate();
		this.repaint();
	}

	private void loadPhieuCombo() {
		this.dangNap = true;
		try {
			this.cbPhieu.removeAllItems();
			for (var v : this.phieuTiepNhanService.search(DanhSachPhieuTiepNhanCriteria.macDinh()).items()) {
				this.cbPhieu.addItem(v.maPhieuTiepNhan());
			}
		} catch (SQLException e) {
			ThongBao.loi(this, "app.login.errorSystem");
		} finally {
			this.dangNap = false;
		}
	}

	private void loadVoucherCombo() {
		this.dangNap = true;
		try {
			this.cbVoucher.removeAllItems();
			this.cbVoucher.addItem("");
			this.voucherTheoMa.clear();
			for (Voucher v : this.voucherService.search(DanhSachVoucherCriteria.macDinh()).items()) {
				this.cbVoucher.addItem(v.getMaVoucher());
				this.voucherTheoMa.put(v.getMaVoucher(), v);
			}
		} catch (SQLException e) {
			ThongBao.loi(this, "app.login.errorSystem");
		} finally {
			this.dangNap = false;
		}
	}

	private void chonPhieu() {
		if (this.dangNap) {
			return;
		}
		String maPhieu = (String) this.cbPhieu.getSelectedItem();
		if (maPhieu == null || maPhieu.isBlank()) {
			this.moLapMoi();
			return;
		}
		try {
			HoaDon hoaDon = this.hoaDonService.getByPhieuTiepNhan(maPhieu);
			if (hoaDon == null) {
				this.moLapMoi();
			} else {
				this.taiHoaDon(hoaDon);
			}
		} catch (SQLException e) {
			ThongBao.loi(this, "app.login.errorSystem");
		}
	}

	private void taiHoaDon(HoaDon hoaDon) throws SQLException {
		this.maHoaDonHienTai = hoaDon.getMaHoaDon();
		this.trangThaiHienTai = hoaDon.getTrangThaiHoaDon();
		this.trangThaiField.setText(this.trangThaiHienTai.getI18NBusinessName());
		this.ghiChuField.setText(hoaDon.getGhiChu() == null ? "" : hoaDon.getGhiChu());
		this.thoiGianTTField.setText(
				hoaDon.getThoiGianThanhToan() == null ? "" : hoaDon.getThoiGianThanhToan().format(this.dateTimeFormat));
		this.lyDoHuyField.setText(hoaDon.getLyDoHuy() == null ? "" : hoaDon.getLyDoHuy());
		this.chonVoucher(hoaDon.getMaVoucher());
		this.napChiTiet(hoaDon.getDanhSachChiTietHoaDon());
		this.refreshLineTable();
		this.tongThanhToanField.setText(this.dinhDangTien(this.hoaDonService.tinhTongTien(hoaDon)));
		this.capNhatQuyen();
	}

	private void moLapMoi() {
		this.chiTiets.clear();
		this.tenSanPham.clear();
		this.soLuongGoc.clear();
		this.maHoaDonHienTai = null;
		this.trangThaiHienTai = TrangThaiHoaDon.CHO_THANH_TOAN;
		this.trangThaiField.setText(this.trangThaiHienTai.getI18NBusinessName());
		this.ghiChuField.setText("");
		this.thoiGianTTField.setText("");
		this.lyDoHuyField.setText("");
		this.refreshLineTable();
		this.capNhatQuyen();
	}

	private void napChiTiet(List<ChiTietHoaDon> danhSach) throws SQLException {
		this.chiTiets.clear();
		this.tenSanPham.clear();
		this.soLuongGoc.clear();
		if (danhSach == null) {
			return;
		}

		Map<String, String> tenPhuTung = new HashMap<>();
		for (PhuTung phuTung : this.phuTungService.getAllForSelection()) {
			tenPhuTung.put(phuTung.getMaPhuTung(), phuTung.getTenPhuTung());
		}
		Map<String, String> tenDichVu = new HashMap<>();
		for (DichVu dichVu : this.dichVuService.search(DanhSachDichVuCriteria.macDinh()).items()) {
			tenDichVu.put(dichVu.getMaDichVu(), dichVu.getTenDichVu());
		}

		for (ChiTietHoaDon chiTiet : danhSach) {
			this.chiTiets.add(chiTiet);
			String ten = chiTiet.getLoaiSanPham() == LoaiSanPham.PHU_TUNG ? tenPhuTung.get(chiTiet.getMaSanPham())
					: tenDichVu.get(chiTiet.getMaSanPham());
			this.tenSanPham.add(ten == null ? chiTiet.getMaSanPham() : ten);
			if (chiTiet.getLoaiSanPham() == LoaiSanPham.PHU_TUNG) {
				this.soLuongGoc.merge(chiTiet.getMaSanPham(), chiTiet.getSoLuong(), Integer::sum);
			}
		}
	}

	private void chonVoucher(String maVoucher) {
		if (maVoucher == null || maVoucher.isBlank()) {
			this.cbVoucher.setSelectedItem("");
			return;
		}
		if (!this.voucherTheoMa.containsKey(maVoucher)) {
			this.cbVoucher.addItem(maVoucher);
		}
		this.cbVoucher.setSelectedItem(maVoucher);
	}

	private boolean choPhepSua() {
		return this.trangThaiHienTai == null || this.trangThaiHienTai == TrangThaiHoaDon.CHO_THANH_TOAN;
	}

	private void capNhatQuyen() {
		boolean choPhepSua = this.choPhepSua();
		boolean choThanhToan = this.trangThaiHienTai == TrangThaiHoaDon.CHO_THANH_TOAN;

		if (this.nutLuu != null) {
			this.nutLuu.setEnabled(choPhepSua);
		}
		if (this.nutThemDV != null) {
			this.nutThemDV.setEnabled(choPhepSua);
		}
		if (this.nutThemPT != null) {
			this.nutThemPT.setEnabled(choPhepSua);
		}
		if (this.cbVoucher != null) {
			this.cbVoucher.setEnabled(choPhepSua);
		}
		if (this.ghiChuField != null) {
			if (choPhepSua) {
				Theme.applyEditable(this.ghiChuField);
			} else {
				Theme.applyReadOnly(this.ghiChuField);
			}
			this.ghiChuField.setEditable(choPhepSua);
		}
		if (this.nutThanhToan != null) {
			this.nutThanhToan.setEnabled(choThanhToan);
		}
		if (this.nutHuy != null) {
			this.nutHuy.setEnabled(choThanhToan && this.maHoaDonHienTai != null);
		}
	}

	private void themDichVu() {
		if (!this.choPhepSua()) {
			return;
		}
		try {
			List<DichVu> ds = this.dichVuService.search(DanhSachDichVuCriteria.macDinh()).items();
			if (ds.isEmpty()) {
				ThongBao.canhBao(this, "app.dichVu.khongTimThay");
				return;
			}
			String[] options = ds.stream().map(d -> d.getMaDichVu() + " - " + d.getTenDichVu()).toArray(String[]::new);
			Object sel = JOptionPane.showInputDialog(this, I18nReaderUtil.get("app.hoaDon.themDichVu"),
					I18nReaderUtil.get("app.hoaDon.themDichVu"), JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
			if (sel == null) {
				return;
			}
			String ma = sel.toString().split(" - ")[0];
			DichVu dv = ds.stream().filter(d -> d.getMaDichVu().equals(ma)).findFirst().orElse(null);
			if (dv == null) {
				return;
			}

			JTextField soLuongField = new JTextField("1", 8);
			JPanel khungSoLuong = new JPanel(new BorderLayout(6, 4));
			khungSoLuong.setOpaque(false);
			String tenHienThi = dv.getTenDichVu() == null || dv.getTenDichVu().isBlank() ? dv.getMaDichVu()
					: dv.getTenDichVu();
			khungSoLuong.add(new JLabel(tenHienThi), BorderLayout.NORTH);
			khungSoLuong.add(soLuongField, BorderLayout.CENTER);
			int luaChon = JOptionPane.showConfirmDialog(this, khungSoLuong,
					I18nReaderUtil.get("app.hoaDon.nhapSoLuong"), JOptionPane.OK_CANCEL_OPTION,
					JOptionPane.PLAIN_MESSAGE);
			if (luaChon != JOptionPane.OK_OPTION) {
				return;
			}

			int sl = 1;
			try {
				sl = Integer.parseInt(soLuongField.getText().trim());
			} catch (Exception ignored) {
			}
			if (sl <= 0) {
				sl = 1;
			}

			int idx = this.indexOfChiTiet(LoaiSanPham.DICH_VU, dv.getMaDichVu());
			if (idx >= 0) {
				ChiTietHoaDon tonTai = this.chiTiets.get(idx);
				tonTai.setSoLuong(tonTai.getSoLuong() + sl);
			} else {
				ChiTietHoaDon ct = new ChiTietHoaDon();
				ct.setLoaiSanPham(LoaiSanPham.DICH_VU);
				ct.setMaSanPham(dv.getMaDichVu());
				ct.setSoLuong(sl);
				ct.setDonGia(dv.getDonGia());
				this.chiTiets.add(ct);
				this.tenSanPham.add(dv.getTenDichVu());
			}
			this.refreshLineTable();
		} catch (SQLException e) {
			ThongBao.loi(this, "app.login.errorSystem");
		}
	}

	private void themPhuTung() {
		if (!this.choPhepSua()) {
			return;
		}
		try {
			List<PhuTung> ds = this.phuTungService.getAllForSelection();
			Map<String, Integer> daChon = new HashMap<>();
			for (ChiTietHoaDon ct : this.chiTiets) {
				if (ct.getLoaiSanPham() == LoaiSanPham.PHU_TUNG) {
					daChon.merge(ct.getMaSanPham(), ct.getSoLuong(), Integer::sum);
				}
			}

			List<PhuTung> hienThi = ds.stream()
					.map(p -> new PhuTung(p.getMaPhuTung(), p.getTenPhuTung(), p.getMoTa(), p.getAnhUrl(),
							p.getDonGiaNhap(),
							Math.max(0, p.getSoLuongTon() + this.soLuongGoc.getOrDefault(p.getMaPhuTung(), 0)
									- daChon.getOrDefault(p.getMaPhuTung(), 0))))
					.toList();

			ChonPhuTungDialog dlg = new ChonPhuTungDialog(SwingUtilities.getWindowAncestor(this), hienThi);

			dlg.showDialog().ifPresent(kq -> {
				int conLai = kq.phuTung().getSoLuongTon();
				if (kq.soLuong() > conLai) {
					ThongBao.canhBao(this, "app.hoaDon.loiSoLuong");
					return;
				}

				int idx = this.indexOfChiTiet(LoaiSanPham.PHU_TUNG, kq.phuTung().getMaPhuTung());
				if (idx >= 0) {
					ChiTietHoaDon tonTai = this.chiTiets.get(idx);
					tonTai.setSoLuong(tonTai.getSoLuong() + kq.soLuong());
				} else {
					ChiTietHoaDon ct = new ChiTietHoaDon();
					ct.setLoaiSanPham(LoaiSanPham.PHU_TUNG);
					ct.setMaSanPham(kq.phuTung().getMaPhuTung());
					ct.setSoLuong(kq.soLuong());
					ct.setDonGia(kq.phuTung().getDonGiaNhap());
					this.chiTiets.add(ct);
					this.tenSanPham.add(kq.phuTung().getTenPhuTung());
				}
				this.refreshLineTable();
			});
		} catch (SQLException e) {
			ThongBao.loi(this, "app.login.errorSystem");
		}
	}

	private int indexOfChiTiet(LoaiSanPham loai, String maSanPham) {
		for (int i = 0; i < this.chiTiets.size(); i++) {
			ChiTietHoaDon ct = this.chiTiets.get(i);
			if (ct.getLoaiSanPham() == loai && ct.getMaSanPham() != null
					&& ct.getMaSanPham().equalsIgnoreCase(maSanPham)) {
				return i;
			}
		}
		return -1;
	}

	private void xoaDong(int row) {
		if (!this.choPhepSua()) {
			ThongBao.canhBao(this, "app.hoaDon.loiTrangThai");
			return;
		}
		if (row < 0 || row >= this.chiTiets.size()) {
			return;
		}
		this.chiTiets.remove(row);
		this.tenSanPham.remove(row);
		this.refreshLineTable();
	}

	private void chonVoucherThayDoi() {
		this.refreshLineTable();
		if (this.dangNap) {
			return;
		}
		String maVoucher = (String) this.cbVoucher.getSelectedItem();
		if (maVoucher == null || maVoucher.isBlank()) {
			return;
		}
		if (!this.voucherConHieuLuc(this.voucherTheoMa.get(maVoucher))) {
			ThongBao.canhBao(this, "app.hoaDon.loiVoucher");
		}
	}

	private boolean voucherConHieuLuc(Voucher voucher) {
		return voucher != null && voucher.isTrangThaiKichHoat()
				&& (voucher.getThoiHanApDung() == null
						|| !voucher.getThoiHanApDung().isBefore(LocalDateTime.now()));
	}

	private void refreshLineTable() {
		this.lineModel.setRowCount(0);
		double tongGoc = 0.0;
		for (int i = 0; i < this.chiTiets.size(); i++) {
			ChiTietHoaDon ct = this.chiTiets.get(i);
			double donGia = ct.getDonGia() == null ? 0.0 : ct.getDonGia();
			double thanhTien = ct.getSoLuong() * donGia;
			tongGoc += thanhTien;
			this.lineModel.addRow(new Object[] { ct.getLoaiSanPham().getI18NBusinessName(), ct.getMaSanPham(),
					this.tenSanPham.get(i), ct.getSoLuong(), this.dinhDangTien(donGia), this.dinhDangTien(thanhTien) });
		}

		Voucher voucher = this.voucherTheoMa.get((String) this.cbVoucher.getSelectedItem());
		double mucGiam = this.voucherConHieuLuc(voucher) && voucher.getMucGiam() != null
				? Math.min(Math.max(voucher.getMucGiam(), 0.0), 1.0)
				: 0.0;
		double giamGia = tongGoc * mucGiam;
		double tong = tongGoc - giamGia;
		this.tongThanhToanField.setText(this.dinhDangTien(tong));
		if (this.lblTong != null) {
			this.lblTong.setText(this.dinhDangTien(tongGoc));
			this.lblGiamGia.setText("-" + this.dinhDangTien(giamGia) + " (" + Math.round(mucGiam * 100) + "%)");
			this.lblThanhToan.setText(this.dinhDangTien(tong));
		}
	}

	private String dinhDangTien(Double gia) {
		return this.currencyFormat.format(gia == null ? 0.0 : gia);
	}

	private boolean luuHoaDon() {
		if (!this.choPhepSua()) {
			ThongBao.canhBao(this, "app.hoaDon.loiTrangThai");
			return false;
		}
		if (this.maHoaDonHienTai != null) {
			return this.capNhatHoaDon();
		}
		String maPhieu = (String) this.cbPhieu.getSelectedItem();
		if (maPhieu == null || maPhieu.isBlank()) {
			ThongBao.canhBao(this, "app.hoaDon.loiPhieuTiepNhan");
			return false;
		}
		if (this.chiTiets.isEmpty()) {
			ThongBao.canhBao(this, "app.hoaDon.loiChiTietTrong");
			return false;
		}
		String maVoucher = (String) this.cbVoucher.getSelectedItem();
		if (maVoucher != null && maVoucher.isBlank()) {
			maVoucher = null;
		}

		try {
			HoaDon hoaDon = this.hoaDonService.create(maPhieu, maVoucher, this.ghiChuField.getText(),
					new ArrayList<>(this.chiTiets));
			this.taiHoaDon(hoaDon);
			ThongBao.thongTin(this, "app.common.create_succeed");
			return true;
		} catch (IllegalArgumentException | IllegalStateException e) {
			ThongBao.loi(this, e);
			return false;
		} catch (SQLException e) {
			ThongBao.loi(this, "app.login.errorSystem");
			return false;
		}
	}

	private boolean capNhatHoaDon() {
		if (this.chiTiets.isEmpty()) {
			ThongBao.canhBao(this, "app.hoaDon.loiChiTietTrong");
			return false;
		}
		String maVoucher = (String) this.cbVoucher.getSelectedItem();
		if (maVoucher != null && maVoucher.isBlank()) {
			maVoucher = null;
		}

		try {
			HoaDon hoaDon = this.hoaDonService.capNhat(this.maHoaDonHienTai, maVoucher, this.ghiChuField.getText(),
					new ArrayList<>(this.chiTiets));
			this.taiHoaDon(hoaDon);
			ThongBao.thongTin(this, "app.common.update_succeed");
			return true;
		} catch (IllegalArgumentException | IllegalStateException e) {
			ThongBao.loi(this, e);
			return false;
		} catch (SQLException e) {
			ThongBao.loi(this, "app.login.errorSystem");
			return false;
		}
	}

	private void huyHoaDon() {
		if (this.maHoaDonHienTai == null) {
			ThongBao.canhBao(this, "app.hoaDon.chuaLuu");
			return;
		}
		if (!ThongBao.xacNhan(this, "app.hoaDon.xacNhanHuy", "app.hoaDon.huyHoaDon")) {
			return;
		}

		String lyDo = JOptionPane.showInputDialog(this, I18nReaderUtil.get("app.hoaDon.lyDoHuy"),
				I18nReaderUtil.get("app.hoaDon.huyHoaDon"), JOptionPane.QUESTION_MESSAGE);
		if (lyDo == null) {
			return;
		}

		try {
			HoaDon hoaDon = this.hoaDonService.huy(this.maHoaDonHienTai, lyDo);
			this.trangThaiHienTai = hoaDon.getTrangThaiHoaDon();
			this.trangThaiField.setText(this.trangThaiHienTai.getI18NBusinessName());
			this.lyDoHuyField.setText(hoaDon.getLyDoHuy() == null ? "" : hoaDon.getLyDoHuy());
			this.capNhatQuyen();
			ThongBao.thongTin(this, "app.common.update_succeed");
		} catch (IllegalArgumentException | IllegalStateException e) {
			ThongBao.loi(this, e);
		} catch (SQLException e) {
			ThongBao.loi(this, "app.login.errorSystem");
		}
	}

	private void xacNhanThanhToan() {
		if (this.maHoaDonHienTai == null) {
			if (!this.luuHoaDon()) {
				return;
			}
		}
		if (!ThongBao.xacNhan(this, "app.hoaDon.xacNhanThanhToan", "app.common.notification")) {
			return;
		}
		try {
			HoaDon hoaDon = this.hoaDonService.xacNhanThanhToan(this.maHoaDonHienTai);
			this.trangThaiHienTai = hoaDon.getTrangThaiHoaDon();
			this.trangThaiField.setText(this.trangThaiHienTai.getI18NBusinessName());
			this.thoiGianTTField.setText(hoaDon.getThoiGianThanhToan() == null ? ""
					: hoaDon.getThoiGianThanhToan().format(this.dateTimeFormat));
			this.tongThanhToanField.setText(this.dinhDangTien(this.hoaDonService.tinhTongTien(hoaDon)));
			this.capNhatQuyen();
			ThongBao.thongTin(this, "app.common.update_succeed");
		} catch (IllegalArgumentException | IllegalStateException e) {
			ThongBao.loi(this, e);
		} catch (SQLException e) {
			ThongBao.loi(this, "app.login.errorSystem");
		}
	}

	@Override
	public String getTitleKey() {
		return "app.menu.invoices";
	}

	@Override
	public MenuGroup getGroup() {
		return MenuGroup.THANH_TOAN;
	}
}
