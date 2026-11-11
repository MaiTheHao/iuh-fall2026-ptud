package iuh.fit.gui.page;

import iuh.fit.dto.DanhSachDichVuCriteria;
import iuh.fit.dto.DanhSachPhieuTiepNhanCriteria;
import iuh.fit.dto.DanhSachPhuTungCriteria;
import iuh.fit.dto.DanhSachVoucherCriteria;
import iuh.fit.dto.PhieuTiepNhanView;
import iuh.fit.entity.ChiTietHoaDon;
import iuh.fit.entity.DichVu;
import iuh.fit.entity.HoaDon;
import iuh.fit.entity.PhuTung;
import iuh.fit.entity.SanPham;
import iuh.fit.entity.Voucher;
import iuh.fit.enumeration.MenuGroup;
import iuh.fit.enumeration.TrangThaiHoaDon;
import iuh.fit.gui.Theme;
import iuh.fit.gui.UiConstants;
import iuh.fit.gui.component.ChonDichVuDialog;
import iuh.fit.gui.component.ChonPhuTungDialog;
import iuh.fit.gui.component.ComboTimKiem;
import iuh.fit.gui.component.ManagementFormPanel;
import iuh.fit.gui.component.ThanhToanDialog;
import iuh.fit.gui.component.ThaoTac;
import iuh.fit.gui.component.ThaoTacTableCellRenderer;
import iuh.fit.gui.component.ThongBao;
import iuh.fit.service.DichVuService;
import iuh.fit.service.HoaDonService;
import iuh.fit.service.PhieuTiepNhanService;
import iuh.fit.service.PhuTungService;
import iuh.fit.service.ThanhToanService;
import iuh.fit.service.VoucherService;
import iuh.fit.util.I18nReaderUtil;

import javax.swing.JButton;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class LapHoaDonPage extends AbstractPage {


	private final HoaDonService hoaDonService = new HoaDonService();
	private final PhieuTiepNhanService phieuTiepNhanService = new PhieuTiepNhanService();
	private final VoucherService voucherService = new VoucherService();
	private final DichVuService dichVuService = new DichVuService();
	private final PhuTungService phuTungService = new PhuTungService();
	private final ThanhToanService thanhToanService = new ThanhToanService();

	private final DecimalFormat currencyFormat = new DecimalFormat("#,##0");

	private final ComboTimKiem<PhieuTiepNhanView> cmbPhieu = new ComboTimKiem<>(
			tuKhoa -> this.phieuTiepNhanService.search(new DanhSachPhieuTiepNhanCriteria(
					null,
					null,
					null,
					null,
					null,
					null,
					null,
					null,
					tuKhoa,
					1,
					UiConstants.SO_GOI_Y)).items(),
			p -> p.maPhieuTiepNhan() + " - " + p.bienSo() + " - " + p.tenKhachHang() + " - " + p.sdt(),
			PhieuTiepNhanView::maPhieuTiepNhan);

	private final ComboTimKiem<Voucher> cmbVoucher = new ComboTimKiem<>(
			tuKhoa -> this.voucherService.search(new DanhSachVoucherCriteria(
					tuKhoa,
					null,
					null,
					null,
					null,
					null,
					true,
					1,
					UiConstants.SO_GOI_Y)).items().stream()
					.filter(this.hoaDonService::voucherConHieuLuc)
					.toList(),
			v -> v.getMaVoucher() + " - " + v.getTenChuongTrinh() + " - "
					+ Math.round((v.getMucGiam() == null ? 0.0 : v.getMucGiam()) * 100) + "%",
			Voucher::getMaVoucher);

	private final JTextField txtGhiChu = new JTextField();

	private final DefaultTableModel lineModel = new DefaultTableModel(0, 0) {
		@Override
		public boolean isCellEditable(int row, int column) {
			return column == 0;
		}

		@Override
		public Class<?> getColumnClass(int column) {
			return column == 0 ? Boolean.class : Object.class;
		}
	};
	private final JTable tblDong = new JTable(this.lineModel);

	private final List<ChiTietHoaDon> chiTiets = new ArrayList<>();
	private final Map<String, Integer> soLuongGoc = new HashMap<>();
	private Voucher voucherDangChon;

	private JLabel lblTong;
	private JLabel lblGiamGia;
	private JLabel lblThanhToan;
	private JButton btnXoaTrang;
	private JButton btnHuy;
	private JButton btnLuu;
	private JButton btnThanhToan;
	private JButton btnThemDV;
	private JButton btnThemPT;
	private JButton btnXoaDaChon;
	private String maHoaDonHienTai;
	private String maPhieuHienTai;
	private String maHoaDonCanMo;
	private TrangThaiHoaDon trangThaiHienTai;

	public LapHoaDonPage() {
		this.setLayout(new BorderLayout());
		this.lineModel.addTableModelListener(e -> this.capNhatNutXoaDaChon());
		this.cmbPhieu.datXuLyChon(this::chonPhieu);
		this.cmbVoucher.datXuLyChon(this::chonVoucherTuGoiY);
		this.cmbVoucher.datXuLyGo(this::khiGoVoucher);
	}

	@Override
	public void render() {
		this.removeAll();
		this.setBorder(Theme.leDeu(8));
		this.setBackground(Theme.BG);

		ManagementFormPanel form = new ManagementFormPanel("app.hoaDon.thongTinTitle");

		form.addField("app.hoaDon.maPhieuTiepNhan", this.cmbPhieu);
		form.addField("app.hoaDon.voucher", this.cmbVoucher);
		form.addField("app.hoaDon.ghiChu", this.txtGhiChu);

		this.trangThaiHienTai = this.trangThaiHienTai == null ? TrangThaiHoaDon.CHO_THANH_TOAN : this.trangThaiHienTai;

		this.btnXoaTrang = form.getNutXoaTrang();
		this.btnHuy = form.getNutXoa();
		this.btnLuu = form.getNutLuu();
		this.btnThanhToan = form.getNutThem();

		this.btnXoaTrang.setText(I18nReaderUtil.get("app.common.clear"));
		this.btnHuy.setText(I18nReaderUtil.get("app.hoaDon.huyHoaDon"));
		this.btnLuu.setText(I18nReaderUtil.get("app.hoaDon.luuHoaDon"));
		this.btnThanhToan.setText(I18nReaderUtil.get("app.hoaDon.xacNhanThanhToan"));

		Theme.applyDanger(this.btnHuy);
		Theme.applyPrimary(this.btnLuu);
		Theme.applyPrimary(this.btnThanhToan);

		this.btnXoaTrang.addActionListener(e -> this.xoaTrang());
		this.btnHuy.addActionListener(e -> this.huyHoaDon());
		this.btnLuu.addActionListener(e -> this.luuHoaDon());
		this.btnThanhToan.addActionListener(e -> this.thanhToan());

		form.thayNut(this.btnXoaTrang, this.btnHuy, this.btnLuu, this.btnThanhToan);

		JPanel right = new JPanel(new BorderLayout(4, 4));
		right.setOpaque(false);

		JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
		btnRow.setOpaque(false);
		this.btnThemDV = new JButton(I18nReaderUtil.get("app.hoaDon.themDichVu"));
		this.btnThemPT = new JButton(I18nReaderUtil.get("app.hoaDon.themPhuTung"));
		Theme.applyPrimary(this.btnThemDV);
		Theme.applyPrimary(this.btnThemPT);
		this.btnThemDV.addActionListener(e -> this.themDichVu());
		this.btnThemPT.addActionListener(e -> this.themPhuTung());
		btnRow.add(this.btnThemDV);
		btnRow.add(this.btnThemPT);
		this.btnXoaDaChon = new JButton(I18nReaderUtil.get("app.common.batch_delete"));
		Theme.applyDanger(this.btnXoaDaChon);
		this.btnXoaDaChon.setEnabled(false);
		this.btnXoaDaChon.addActionListener(e -> this.xoaDongDaChon());
		btnRow.add(this.btnXoaDaChon);

		this.lineModel.setColumnIdentifiers(new Object[] { "",
				I18nReaderUtil.get("app.hoaDon.loai"),
				I18nReaderUtil.get("app.phuTung.ma"), I18nReaderUtil.get("app.phuTung.ten"),
				I18nReaderUtil.get("app.phieuNhap.soLuong"), I18nReaderUtil.get("app.phieuNhap.donGia"),
				I18nReaderUtil.get("app.phieuNhap.thanhTien"), I18nReaderUtil.get("app.common.actions") });
		Theme.styleTable(this.tblDong);
		this.tblDong.getColumnModel().getColumn(0).setMinWidth(44);
		this.tblDong.getColumnModel().getColumn(0).setMaxWidth(44);
		this.tblDong.getColumnModel().getColumn(0).setPreferredWidth(44);
		ThaoTacTableCellRenderer.ganVao(this.tblDong, 7, List.of(
				new ThaoTac("trash-2", "app.common.delete", ThaoTac.KieuThaoTac.NGUY_HIEM, row -> this.xoaDong(row))));

		right.add(btnRow, BorderLayout.NORTH);
		right.add(new JScrollPane(this.tblDong), BorderLayout.CENTER);

		JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
		footer.setOpaque(false);
		JLabel nhanGoc = new JLabel(I18nReaderUtil.get("app.hoaDon.tongTienGoc"));
		JLabel nhanGiam = new JLabel(I18nReaderUtil.get("app.hoaDon.giamGia"));
		JLabel nhanThanhToan = new JLabel(I18nReaderUtil.get("app.hoaDon.tongThanhToan"));
		for (JLabel nhan : List.of(nhanGoc, nhanGiam, nhanThanhToan)) {
			nhan.setForeground(Theme.palette().INK_SECONDARY());
		}
		this.lblTong = new JLabel("0");
		this.lblGiamGia = new JLabel("0");
		this.lblThanhToan = new JLabel("0");
		for (JLabel giaTri : List.of(this.lblTong, this.lblGiamGia, this.lblThanhToan)) {
			giaTri.setForeground(Theme.INK);
		}
		footer.add(nhanGoc);
		footer.add(this.lblTong);
		footer.add(nhanGiam);
		footer.add(this.lblGiamGia);
		footer.add(nhanThanhToan);
		footer.add(this.lblThanhToan);
		right.add(footer, BorderLayout.SOUTH);

		JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, form, right);
		split.setDividerLocation(420);
		split.setOpaque(false);
		split.setBorder(null);
		this.add(split, BorderLayout.CENTER);

		this.moHoaDonDangCho();
		this.refreshLineTable();
		this.capNhatQuyen();
		this.revalidate();
		this.repaint();
	}

	public void moHoaDon(String maHoaDon) {
		this.maHoaDonCanMo = maHoaDon;
	}

	private void moHoaDonDangCho() {
		if (this.maHoaDonCanMo == null) {
			this.cmbPhieu.datTuKhoa("");
			this.cmbVoucher.xoa();
			this.voucherDangChon = null;
			this.moLapMoi();
			return;
		}
		String maHoaDon = this.maHoaDonCanMo;
		this.maHoaDonCanMo = null;
		try {
			HoaDon hoaDon = this.hoaDonService.getById(maHoaDon);
			this.cmbPhieu.datTuKhoa(hoaDon.getPhieuTiepNhan() == null ? ""
					: hoaDon.getPhieuTiepNhan().getMaPhieuTiepNhan());
			this.taiHoaDon(hoaDon);
		} catch (IllegalArgumentException | SQLException e) {
			ThongBao.loi(this, "app.hoaDon.khongTonTai");
			this.cmbPhieu.datTuKhoa("");
			this.cmbVoucher.xoa();
			this.voucherDangChon = null;
			this.moLapMoi();
		}
	}

	private void chonPhieu(PhieuTiepNhanView phieu) {
		try {
			HoaDon hoaDon = this.hoaDonService.getByPhieuTiepNhan(phieu.maPhieuTiepNhan());
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
		this.maPhieuHienTai = hoaDon.getPhieuTiepNhan() == null ? null
				: hoaDon.getPhieuTiepNhan().getMaPhieuTiepNhan();
		this.trangThaiHienTai = hoaDon.getTrangThaiHoaDon();
		this.txtGhiChu.setText(hoaDon.getGhiChu() == null ? "" : hoaDon.getGhiChu());

		this.chonVoucher(hoaDon.getVoucher() == null ? null : hoaDon.getVoucher().getMaVoucher());

		this.napChiTiet(hoaDon.getDanhSachChiTietHoaDon());
		this.refreshLineTable();
		this.capNhatQuyen();
	}

	private void moLapMoi() {
		this.chiTiets.clear();
		this.soLuongGoc.clear();
		this.maHoaDonHienTai = null;
		this.maPhieuHienTai = null;
		this.trangThaiHienTai = TrangThaiHoaDon.CHO_THANH_TOAN;
		this.txtGhiChu.setText("");
		this.refreshLineTable();
		this.capNhatQuyen();
	}

	private void napChiTiet(List<ChiTietHoaDon> danhSach) {
		this.chiTiets.clear();
		this.soLuongGoc.clear();
		if (danhSach == null) {
			return;
		}

		for (ChiTietHoaDon chiTiet : danhSach) {
			this.chiTiets.add(chiTiet);
			if (chiTiet.getSanPham() instanceof PhuTung phuTung) {
				this.soLuongGoc.merge(phuTung.getMaSanPham(), chiTiet.getSoLuong(), Integer::sum);
			}
		}
	}

	private void chonVoucher(String maVoucher) throws SQLException {
		if (maVoucher == null || maVoucher.isBlank()) {
			this.cmbVoucher.xoa();
			this.voucherDangChon = null;
			return;
		}

		Voucher voucher = this.voucherService.getById(maVoucher).orElse(null);

		if (voucher == null || !this.hoaDonService.voucherConHieuLuc(voucher)) {
			this.cmbVoucher.xoa();
			this.voucherDangChon = null;
			ThongBao.canhBao(this, "app.hoaDon.loiVoucher");
			return;
		}

		this.voucherDangChon = voucher;
		this.cmbVoucher.datTuKhoa(voucher.getMaVoucher());
	}

	private boolean choPhepSua() {
		return this.trangThaiHienTai == null || this.trangThaiHienTai == TrangThaiHoaDon.CHO_THANH_TOAN;
	}

	private void capNhatQuyen() {
		boolean choPhepSua = this.choPhepSua();
		boolean choThanhToan = this.trangThaiHienTai == TrangThaiHoaDon.CHO_THANH_TOAN;

		if (this.btnLuu != null) {
			this.btnLuu.setEnabled(choPhepSua);
		}
		if (this.btnXoaTrang != null) {
			this.btnXoaTrang.setEnabled(choPhepSua);
		}
		if (this.btnThemDV != null) {
			this.btnThemDV.setEnabled(choPhepSua);
		}
		if (this.btnThemPT != null) {
			this.btnThemPT.setEnabled(choPhepSua);
		}
		if (this.cmbVoucher != null) {
			this.cmbVoucher.datChoPhepNhap(choPhepSua);
		}
		if (this.txtGhiChu != null) {
			if (choPhepSua) {
				Theme.applyEditable(this.txtGhiChu);
			} else {
				Theme.applyReadOnly(this.txtGhiChu);
			}
			this.txtGhiChu.setEditable(choPhepSua);
		}
		if (this.btnThanhToan != null) {
			this.btnThanhToan.setEnabled(choThanhToan);
		}
		if (this.btnHuy != null) {
			this.btnHuy.setEnabled(choThanhToan && this.maHoaDonHienTai != null);
		}
		this.tblDong.setEnabled(this.choPhepSua());
	}

	private void themDichVu() {
		if (!this.choPhepSua()) {
			return;
		}

		ChonDichVuDialog dlg = new ChonDichVuDialog(
				SwingUtilities.getWindowAncestor(this),
				this.dichVuService,
				DanhSachDichVuCriteria.macDinh());

		dlg.showDialog().ifPresent(kq -> {
			DichVu dv = kq.sanPham();
			int sl = kq.soLuong();

			int idx = this.indexOfChiTiet(dv);
			if (idx >= 0) {
				ChiTietHoaDon tonTai = this.chiTiets.get(idx);
				tonTai.setSoLuong(tonTai.getSoLuong() + sl);
			} else {
				ChiTietHoaDon ct = new ChiTietHoaDon();
				ct.setSanPham(dv);
				ct.setSoLuong(sl);
				ct.setDonGia(dv.getDonGia());
				this.chiTiets.add(ct);
			}
			this.refreshLineTable();
		});
	}

	private PhuTung voiTonKhaDung(PhuTung p, Map<String, Integer> daChon, Map<String, Integer> soLuongGoc) {
		return new PhuTung(p.getMaSanPham(), p.getTenSanPham(),
				p.getMoTa(), p.getAnhUrl(), p.getDonGiaNhap(),
				Math.max(0, p.getSoLuongTon() + soLuongGoc.getOrDefault(p.getMaSanPham(), 0)
						- daChon.getOrDefault(p.getMaSanPham(), 0)),
				p.getMaNhaCungCap());
	}

	private void themPhuTung() {
		if (!this.choPhepSua()) {
			return;
		}

		Map<String, Integer> daChon = new HashMap<>();
		for (ChiTietHoaDon ct : this.chiTiets) {
			if (ct.getSanPham() instanceof PhuTung phuTung) {
				daChon.merge(phuTung.getMaSanPham(), ct.getSoLuong(), Integer::sum);
			}
		}

		Map<String, Integer> soLuongGocSnapshot = new HashMap<>(this.soLuongGoc);

		Function<PhuTung, PhuTung> hienThi = p -> this.voiTonKhaDung(p, daChon, soLuongGocSnapshot);

		ChonPhuTungDialog dlg = new ChonPhuTungDialog(
				SwingUtilities.getWindowAncestor(this),
				this.phuTungService,
				DanhSachPhuTungCriteria.macDinh(),
				hienThi,
				true);

		dlg.showDialog().ifPresent(kq -> {
			if (kq.soLuong() <= 0) {
				ThongBao.canhBao(this, "app.hoaDon.loiSoLuong");
				return;
			}
			int conLai = kq.sanPham().getSoLuongTon();
			if (kq.soLuong() > conLai) {
				ThongBao.canhBao(this, "app.hoaDon.loiTonKho");
				return;
			}

			int idx = this.indexOfChiTiet(kq.sanPham());
			if (idx >= 0) {
				ChiTietHoaDon tonTai = this.chiTiets.get(idx);
				tonTai.setSoLuong(tonTai.getSoLuong() + kq.soLuong());
			} else {
				ChiTietHoaDon ct = new ChiTietHoaDon();
				ct.setSanPham(kq.sanPham());
				ct.setSoLuong(kq.soLuong());
				ct.setDonGia(kq.sanPham().getDonGiaNhap());
				this.chiTiets.add(ct);
			}
			this.refreshLineTable();
		});
	}

	private int indexOfChiTiet(SanPham sanPham) {
		for (int i = 0; i < this.chiTiets.size(); i++) {
			SanPham hienTai = this.chiTiets.get(i).getSanPham();
			if (hienTai != null && hienTai.getLoaiSanPham() == sanPham.getLoaiSanPham()
					&& hienTai.getMaSanPham() != null
					&& hienTai.getMaSanPham().equalsIgnoreCase(sanPham.getMaSanPham())) {
				return i;
			}
		}
		return -1;
	}

	private void xoaTrang() {
	    if (!this.choPhepSua()) {
	        ThongBao.canhBao(this, "app.hoaDon.loiTrangThai");
	        return;
	    }
	    boolean coDuLieu = !this.txtGhiChu.getText().isBlank() || !this.cmbVoucher.getTuKhoa().isBlank();
	    if (coDuLieu && !ThongBao.xacNhan(this, "app.common.confirmClear", "app.common.confirm")) {
	        return;
	    }
	    this.txtGhiChu.setText("");
	    this.cmbVoucher.xoa();
	    this.voucherDangChon = null;
	    this.refreshLineTable();
	}

	private void xoaDong(int row) {
		if (!this.choPhepSua()) {
			if (this.trangThaiHienTai == TrangThaiHoaDon.DA_HUY) {
				ThongBao.canhBao(this, "app.hoaDon.loiDaHuy");
			} else {
				ThongBao.canhBao(this, "app.hoaDon.loiTrangThai");
			}
			return;
		}
		if (row < 0 || row >= this.chiTiets.size()) {
			return;
		}
		if (!ThongBao.xacNhan(this, "app.hoaDon.xacNhanXoaDong", "app.hoaDon.tieuDeXoaDong")) {
			return;
		}
		this.chiTiets.remove(row);
		this.refreshLineTable();
	}

	private void xoaDongDaChon() {
		if (!this.choPhepSua()) {
			if (this.trangThaiHienTai == TrangThaiHoaDon.DA_HUY) {
				ThongBao.canhBao(this, "app.hoaDon.loiDaHuy");
			} else {
				ThongBao.canhBao(this, "app.hoaDon.loiTrangThai");
			}
			return;
		}

		List<Integer> dongDaChon = new ArrayList<>();
		for (int i = this.lineModel.getRowCount() - 1; i >= 0; i--) {
			if (Boolean.TRUE.equals(this.lineModel.getValueAt(i, 0))) {
				dongDaChon.add(i);
			}
		}
		if (dongDaChon.isEmpty()) {
			ThongBao.canhBao(this, "app.hoaDon.loiChiTietTrong");
			return;
		}
		if (!ThongBao.xacNhanDinhDang(this, "app.common.batch_delete_confirm", "app.common.confirm",
				dongDaChon.size())) {
			return;
		}

		for (int row : dongDaChon) {
			if (row >= 0 && row < this.chiTiets.size()) {
				this.chiTiets.remove(row);
			}
		}
		this.refreshLineTable();
	}

	private void capNhatNutXoaDaChon() {
		if (this.btnXoaDaChon == null) {
			return;
		}

		int soDong = 0;
		for (int i = 0; i < this.lineModel.getRowCount(); i++) {
			if (Boolean.TRUE.equals(this.lineModel.getValueAt(i, 0))) {
				soDong++;
			}
		}
		this.btnXoaDaChon.setText(I18nReaderUtil.get("app.common.batch_delete") + " (" + soDong + ")");
		this.btnXoaDaChon.setEnabled(soDong > 0);
	}

	private void chonVoucherTuGoiY(Voucher voucher) {
		if (!this.hoaDonService.voucherConHieuLuc(voucher)) {
			this.voucherDangChon = null;
			this.cmbVoucher.xoa();
			ThongBao.canhBao(this, "app.hoaDon.loiVoucher");
			this.refreshLineTable();
			return;
		}
		this.voucherDangChon = voucher;
		this.refreshLineTable();
	}

	private void khiGoVoucher() {
		if (this.voucherDangChon == null) {
			return;
		}
		this.voucherDangChon = null;
		this.refreshLineTable();
	}

	private void refreshLineTable() {
		this.lineModel.setRowCount(0);
		for (int i = 0; i < this.chiTiets.size(); i++) {
			ChiTietHoaDon ct = this.chiTiets.get(i);
			double donGia = ct.getDonGia() == null ? 0.0 : ct.getDonGia();
			double thanhTien = ct.getSoLuong() * donGia;
			this.lineModel.addRow(new Object[] { Boolean.FALSE,
					ct.getSanPham().getLoaiSanPham().getI18NBusinessName(),
					ct.getSanPham().getMaSanPham(), this.tenHienThi(ct.getSanPham()), ct.getSoLuong(),
					this.dinhDangTien(donGia), this.dinhDangTien(thanhTien) });
		}

		Voucher voucher = this.voucherDangChon;
		double tongGoc = this.hoaDonService.tinhTongTienGoc(this.chiTiets);
		double mucGiam = this.hoaDonService.tinhMucGiam(voucher);
		double tong = this.hoaDonService.tinhTongTien(this.chiTiets, voucher);
		if (this.lblTong != null) {
			this.lblTong.setText(this.dinhDangTien(tongGoc));
			this.lblGiamGia.setText("-" + this.dinhDangTien(tongGoc - tong)
					+ " (" + Math.round(mucGiam * 100) + "%)");
			this.lblThanhToan.setText(this.dinhDangTien(tong));
		}
	}

	private String tenHienThi(SanPham sanPham) {
		String ten = sanPham.getTenSanPham();
		return ten == null || ten.isBlank() ? sanPham.getMaSanPham() : ten;
	}

	private String dinhDangTien(Double gia) {
		return this.currencyFormat.format(gia == null ? 0.0 : gia);
	}
	private boolean luuHoaDon() {
	    return this.luuHoaDon(true);
	}

	private boolean luuHoaDon(boolean dieuHuongSauKhiLuu) {
		if (!this.choPhepSua()) {
			ThongBao.canhBao(this, "app.hoaDon.loiTrangThai");
			return false;
		}
		if (this.maHoaDonHienTai != null) {
			return this.capNhatHoaDon();
		}
		String maPhieu = this.cmbPhieu.getTuKhoa().trim();
		if (maPhieu.isEmpty()) {
			ThongBao.canhBao(this, "app.hoaDon.loiPhieuTiepNhan");
			return false;
		}
		try {
			HoaDon daCo = this.hoaDonService.getByPhieuTiepNhan(maPhieu);
			if (daCo != null) {
				ThongBao.canhBao(this, "app.hoaDon.daTonTai");
				this.taiHoaDon(daCo);
				return false;
			}
		} catch (SQLException e) {
			ThongBao.loi(this, "app.login.errorSystem");
			return false;
		}
		if (this.chiTiets.isEmpty()) {
			ThongBao.canhBao(this, "app.hoaDon.loiChiTietTrong");
			return false;
		}
		String maVoucher = this.cmbVoucher.getTuKhoa().trim();
		if (maVoucher.isEmpty()) {
			maVoucher = null;
		}

		try {
			HoaDon hoaDon = this.hoaDonService.create(maPhieu, maVoucher, this.txtGhiChu.getText(),
					new ArrayList<>(this.chiTiets));
			this.taiHoaDon(hoaDon);
			ThongBao.thongTin(this, "app.common.create_succeed");

			if (dieuHuongSauKhiLuu) {
			    this.goTo(QuanLyHoaDonPage.class);
			}

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
		return this.capNhatHoaDon(true);
	}

	private boolean capNhatHoaDon(boolean hienThongBao) {
		String maPhieu = this.cmbPhieu.getTuKhoa().trim();
		if (this.maPhieuHienTai == null || !maPhieu.equalsIgnoreCase(this.maPhieuHienTai)) {
			ThongBao.canhBao(this, "app.hoaDon.phieuKhongKhop");
			return false;
		}
		if (this.chiTiets.isEmpty()) {
			ThongBao.canhBao(this, "app.hoaDon.loiChiTietTrong");
			return false;
		}
		String maVoucher = this.cmbVoucher.getTuKhoa().trim();
		if (maVoucher.isEmpty()) {
			maVoucher = null;
		}

		try {
			HoaDon hoaDon = this.hoaDonService.capNhat(this.maHoaDonHienTai, maVoucher, this.txtGhiChu.getText(),
					new ArrayList<>(this.chiTiets));
			this.taiHoaDon(hoaDon);
			if (hienThongBao) {
				ThongBao.thongTin(this, "app.common.update_succeed");
			}
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
			this.capNhatQuyen();
			ThongBao.thongTin(this, "app.common.update_succeed");
		} catch (IllegalArgumentException | IllegalStateException e) {
			ThongBao.loi(this, e);
		} catch (SQLException e) {
			ThongBao.loi(this, "app.login.errorSystem");
		}
	}

private void thanhToan() {
    if (this.maHoaDonHienTai == null) {
        if (!this.luuHoaDon(false)) {
            return;
        }
    } else if (!this.capNhatHoaDon(false)) {
        return;
    }

    double tongTien = this.hoaDonService.tinhTongTien(
            this.chiTiets,
            this.voucherDangChon
    );

    long soTienThanhToan = Math.round(tongTien);

    ThanhToanDialog dlgThanhToan = new ThanhToanDialog(
            SwingUtilities.getWindowAncestor(this),
            soTienThanhToan,
            this.maHoaDonHienTai
    );

    dlgThanhToan.setVisible(true);

    if (!dlgThanhToan.duocXacNhan()) {
        return;
    }

    try {
        this.thanhToanService.thanhToan(
                this.maHoaDonHienTai,
                dlgThanhToan.phuongThuc(),
                dlgThanhToan.ghiChu()
        );

        this.trangThaiHienTai = TrangThaiHoaDon.DA_THANH_TOAN;
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
