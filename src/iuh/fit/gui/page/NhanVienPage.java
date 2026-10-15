package iuh.fit.gui.page;

import iuh.fit.dto.DanhSachNhanVienCriteria;
import iuh.fit.dto.PaginatedResult;
import iuh.fit.entity.NhanVien;
import iuh.fit.enumeration.MenuGroup;
import iuh.fit.enumeration.TrangThaiNhanVien;
import iuh.fit.enumeration.VaiTro;
import iuh.fit.gui.component.ManagementPanel;
import iuh.fit.service.NhanVienService;
import iuh.fit.util.I18nReaderUtil;
import iuh.fit.util.MaGeneratorUtil;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;

import java.io.File;
import java.io.IOException;

import java.net.URI;
import java.net.URLConnection;

import java.sql.SQLException;
import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class NhanVienPage extends AbstractPage {

	private static final long serialVersionUID = 1L;
	private static final int PAGE_SIZE = 10;

	private final NhanVienService nhanVienService = new NhanVienService();

	private ManagementPanel managementPanel;

	private final JTextField txtMaNhanVien = new JTextField(14);
	private final JTextField txtTenDangNhap = new JTextField(14);
	private final JPasswordField txtMatKhau = new JPasswordField(14);
	private final JTextField txtHoTen = new JTextField(14);
	private final JTextField txtSoDienThoai = new JTextField(14);
	private final JTextField txtAnhDaiDien = new JTextField(14);

	private final JLabel lblAnhPreview = new JLabel(I18nReaderUtil.get("app.phuTung.chuaCoAnh"), SwingConstants.CENTER);

	private JComboBox<VaiTro> cboVaiTro;
	private JComboBox<TrangThaiNhanVien> cboTrangThai;

	private boolean dangSua = false;
	private String maNhanVienDangSua;

	private PaginatedResult<NhanVien> ketQua;
	private int trangHienTai = 1;

	public NhanVienPage() {
		setLayout(new BorderLayout());
	}

	@Override
	public void render() {
		removeAll();

		setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

		managementPanel = new ManagementPanel();

		managementPanel.setHeaders(I18nReaderUtil.get("app.phuTung.hinhAnh"),
				I18nReaderUtil.get("app.nhanVien.maNhanVien"), I18nReaderUtil.get("app.nhanVien.hoTen"),
				I18nReaderUtil.get("app.nhanVien.soDienThoai"), I18nReaderUtil.get("app.nhanVien.vaiTro"),
				I18nReaderUtil.get("app.nhanVien.trangThai"));

		khoiTaoForm();
		khoiTaoBoLoc();
		ganSuKien();

		add(managementPanel, BorderLayout.CENTER);

		trangHienTai = 1;
		taiLaiDanhSach();

		revalidate();
		repaint();
	}

	@Override
	public String getTitleKey() {
		return "app.menu.employees";
	}

	@Override
	public MenuGroup getGroup() {
		return MenuGroup.TO_CHUC;
	}

	// ==================== FORM ====================

	private void khoiTaoForm() {
		JPanel form = managementPanel.getFormPanel();

		form.removeAll();

		txtMaNhanVien.setEditable(false);
		txtMaNhanVien.setFocusable(false);
		txtTenDangNhap.setEnabled(true);
		txtMatKhau.setEnabled(true);

		addFormRow(managementPanel, "app.nhanVien.maNhanVien", txtMaNhanVien);

		addFormRow(managementPanel, "app.nhanVien.tenDangNhap", txtTenDangNhap);

		addFormRow(managementPanel, "app.nhanVien.matKhau", txtMatKhau);

		addFormRow(managementPanel, "app.nhanVien.hoTen", txtHoTen);

		addFormRow(managementPanel, "app.nhanVien.soDienThoai", txtSoDienThoai);

		cboVaiTro = new JComboBox<>(VaiTro.values());

		addFormRow(managementPanel, "app.nhanVien.vaiTro", cboVaiTro);

		cboTrangThai = new JComboBox<>(TrangThaiNhanVien.values());

		addFormRow(managementPanel, "app.nhanVien.trangThai", cboTrangThai);

		// Đường dẫn ảnh đại diện.
		addFormRow(managementPanel, "app.phuTung.anhUrl", txtAnhDaiDien);

		txtAnhDaiDien.addActionListener(e -> hienThiAnh(txtAnhDaiDien.getText().trim()));

		// Các nút chọn và gỡ ảnh.
		JPanel nutAnhPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));

		nutAnhPanel.setOpaque(false);

		JButton btnChonAnh = new JButton(I18nReaderUtil.get("app.phuTung.chonAnh"));

		JButton btnGoAnh = new JButton(I18nReaderUtil.get("app.phuTung.goAnh"));

		btnChonAnh.addActionListener(e -> chonAnh());
		btnGoAnh.addActionListener(e -> goAnh());
		nutAnhPanel.add(btnChonAnh);
		nutAnhPanel.add(btnGoAnh);

		form.add(nutAnhPanel);

		// Khu vực xem trước ảnh.
		lblAnhPreview.setPreferredSize(new Dimension(180, 120));

		lblAnhPreview.setBorder(BorderFactory.createEtchedBorder());

		JPanel previewPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));

		previewPanel.setOpaque(false);
		previewPanel.add(lblAnhPreview);

		form.add(previewPanel);

		form.revalidate();
		form.repaint();

		lamMoiForm();
	}

	/**
	 * Tạo một dòng form giống cách bố trí của PhuTungPage.
	 */
	private void addFormRow(ManagementPanel panel, String labelKey, Component component) {

		JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));

		row.setOpaque(false);

		row.add(new JLabel(I18nReaderUtil.get(labelKey)));
		row.add(component);

		panel.getFormPanel().add(row);
	}

	// ==================== XỬ LÝ ẢNH ====================

	private void chonAnh() {
		JFileChooser chooser = new JFileChooser();

		chooser.setDialogTitle(I18nReaderUtil.get("app.phuTung.chonAnh"));

		chooser.setFileFilter(
				new FileNameExtensionFilter(I18nReaderUtil.get("app.phuTung.loaiTepAnh"), "png", "jpg", "jpeg", "gif"));

		int result = chooser.showOpenDialog(this);

		if (result == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();

			txtAnhDaiDien.setText(file.getAbsolutePath());

			hienThiAnh(file.getAbsolutePath());
		}
	}

	private void goAnh() {
		txtAnhDaiDien.setText("");
		lblAnhPreview.setIcon(null);

		lblAnhPreview.setText(I18nReaderUtil.get("app.phuTung.chuaCoAnh"));
	}

	/**
	 * Hiển thị ảnh từ đường dẫn file hoặc URL.
	 */
	private void hienThiAnh(String duongDan) {
		if (duongDan == null || duongDan.isBlank()) {
			goAnh();
			return;
		}

		try {
			BufferedImage bufferedImage;

			if (duongDan.startsWith("https://") || duongDan.startsWith("http://")) {

				URLConnection connection = URI.create(duongDan.trim()).toURL().openConnection();

				connection.setConnectTimeout(5000);
				connection.setReadTimeout(10000);

				try (var input = connection.getInputStream()) {
					bufferedImage = ImageIO.read(input);
				}

			} else {
				bufferedImage = ImageIO.read(new File(duongDan.trim()));
			}

			if (bufferedImage == null) {
				throw new IOException(I18nReaderUtil.get("app.phuTung.duLieuKhongPhaiAnh"));
			}

			Image image = bufferedImage.getScaledInstance(180, 120, Image.SCALE_SMOOTH);

			lblAnhPreview.setText("");
			lblAnhPreview.setIcon(new ImageIcon(image));

		} catch (Exception ex) {
			lblAnhPreview.setIcon(null);

			lblAnhPreview.setText(I18nReaderUtil.get("app.phuTung.khongTaiDuocAnh"));

			System.err.println(I18nReaderUtil.get("app.phuTung.khongCoAnh") + ": " + duongDan);

			ex.printStackTrace();
		}
	}

	// ==================== BỘ LỌC ====================

	private void khoiTaoBoLoc() {
		managementPanel.addTextFilter("app.nhanVien.maNhanVien", 0);

		managementPanel.addTextFilter("app.nhanVien.hoTen", 1);

		managementPanel.addTextFilter("app.nhanVien.soDienThoai", 2);

		managementPanel.addComboBoxFilter("app.nhanVien.vaiTro", 3, VaiTro.values());

		managementPanel.addComboBoxFilter("app.nhanVien.trangThai", 4, TrangThaiNhanVien.values());

		managementPanel.addDateRangeFilter("app.nhanVien.ngayTao", 5);

		managementPanel.setFilterChangeListener(() -> {
			trangHienTai = 1;
			taiLaiDanhSach();
		});
	}

	private String layGiaTriBoLoc(int index) {
		String value = managementPanel.getTextFilter(index);

		if (value == null || value.isBlank()) {
			return null;
		}

		return value.trim();
	}

	private String layTimNhanh() {
		String value = managementPanel.getQuickFilter();

		if (value == null || value.isBlank()) {
			return null;
		}

		return value.trim();
	}

	// ==================== SỰ KIỆN ====================

	private void ganSuKien() {
		managementPanel.getThemButton().addActionListener(e -> lamMoiForm());

		managementPanel.getLuuButton().addActionListener(e -> luu());

		managementPanel.getXoaTrangButton().addActionListener(e -> lamMoiForm());

		managementPanel.getXoaButton().addActionListener(e -> xoa());

		managementPanel.getClearFilterButton().addActionListener(e -> {
			trangHienTai = 1;
			managementPanel.clearFilters();
			taiLaiDanhSach();
		});

		managementPanel.getPrevButton().addActionListener(e -> {
			if (trangHienTai > 1) {
				trangHienTai--;
				taiLaiDanhSach();
			}
		});

		managementPanel.getNextButton().addActionListener(e -> {
			if (ketQua != null && trangHienTai < ketQua.totalPages()) {
				trangHienTai++;
				taiLaiDanhSach();
			}
		});

		managementPanel.getTable().getSelectionModel().addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting()) {
				chonDong();
			}
		});
	}

	// ==================== TẢI DANH SÁCH ====================

	private void taiLaiDanhSach() {
		try {
			DanhSachNhanVienCriteria criteria =
			        new DanhSachNhanVienCriteria(
			                layGiaTriBoLoc(0),
			                layGiaTriBoLoc(1),
			                layGiaTriBoLoc(2),
			                managementPanel.getComboFilter(3),
			                managementPanel.getComboFilter(4),
			                layTimNhanh(),
			                managementPanel.getDateFilterFrom(5),
			                managementPanel.getDateFilterTo(5),
			                trangHienTai,
			                PAGE_SIZE
			        );

			ketQua = nhanVienService.search(criteria);

			if (ketQua == null) {
				return;
			}

			if (ketQua.totalPages() > 0 && trangHienTai > ketQua.totalPages()) {

				trangHienTai = ketQua.totalPages();
				taiLaiDanhSach();
				return;
			}

			hienThiDanhSach();

		} catch (SQLException ex) {
			hienThiLoi("app.nhanVien.loiTaiDanhSach", ex);
		}
	}

	private void hienThiDanhSach() {
		if (ketQua == null) {
			return;
		}

		List<Object[]> rows = new ArrayList<>();

		for (NhanVien nv : ketQua.items()) {
			rows.add(new Object[] { nv.getAnhDaiDien(), nv.getMaNhanVien(), nv.getHoTen(),
					nv.getSoDienThoai() == null ? "" : nv.getSoDienThoai(), nhanVaiTro(nv.getVaiTro()),
					nhanTrangThai(nv.getTrangThai()) });
		}

		managementPanel.setRows(rows);

		cauHinhCotAnh();

		int from = ketQua.totalItems() == 0 ? 0 : ketQua.fromIndex() + 1;

		managementPanel.setCount(from, ketQua.toIndex(), ketQua.totalItems());

		managementPanel.setPage(ketQua.page(), Math.max(1, ketQua.totalPages()));

		trangHienTai = ketQua.page();
	}

	// ==================== CỘT ẢNH ====================

	private void cauHinhCotAnh() {
		JTable table = managementPanel.getTable();

		if (table.getColumnCount() == 0) {
			return;
		}

		TableColumn cotAnh = table.getColumnModel().getColumn(0);

		cotAnh.setCellRenderer(new AnhRenderer());
		cotAnh.setPreferredWidth(65);
		cotAnh.setMinWidth(65);
		cotAnh.setMaxWidth(80);

		table.setRowHeight(55);
	}

	private static class AnhRenderer extends DefaultTableCellRenderer {

		private static final long serialVersionUID = 1L;

		private static final Map<String, ImageIcon> CACHE = new HashMap<>();

		private static final Set<String> LOI_TAI_ANH = new HashSet<>();

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {

			JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
					column);

			label.setHorizontalAlignment(SwingConstants.CENTER);

			label.setVerticalAlignment(SwingConstants.CENTER);

			label.setIcon(null);
			label.setText("");

			if (value == null || value.toString().isBlank()) {
				label.setText(I18nReaderUtil.get("app.phuTung.chuaCoAnh"));

				return label;
			}

			String duongDan = value.toString().trim();

			ImageIcon icon = CACHE.get(duongDan);

			if (icon == null && !LOI_TAI_ANH.contains(duongDan)) {

				try {
					BufferedImage bufferedImage;

					if (duongDan.startsWith("https://") || duongDan.startsWith("http://")) {

						URLConnection connection = URI.create(duongDan).toURL().openConnection();

						connection.setConnectTimeout(3000);
						connection.setReadTimeout(5000);

						try (var input = connection.getInputStream()) {
							bufferedImage = ImageIO.read(input);
						}

					} else {
						bufferedImage = ImageIO.read(new File(duongDan));
					}

					if (bufferedImage == null) {
						throw new IOException(I18nReaderUtil.get("app.phuTung.duLieuKhongPhaiAnh"));
					}

					Image image = bufferedImage.getScaledInstance(50, 45, Image.SCALE_SMOOTH);

					icon = new ImageIcon(image);
					CACHE.put(duongDan, icon);

				} catch (Exception ex) {
					LOI_TAI_ANH.add(duongDan);
				}
			}

			if (icon != null) {
				label.setIcon(icon);
			} else {
				label.setText(I18nReaderUtil.get("app.phuTung.khongTaiDuocAnh"));
			}

			return label;
		}
	}

	// ==================== CHỌN NHÂN VIÊN ====================

	private void chonDong() {
		JTable table = managementPanel.getTable();

		int selectedRow = table.getSelectedRow();

		if (selectedRow < 0) {
			return;
		}

		int modelRow = table.convertRowIndexToModel(selectedRow);

		TableModel model = table.getModel();

		// Cột 0 là ảnh, cột 1 là mã nhân viên.
		String ma = String.valueOf(model.getValueAt(modelRow, 1));

		try {
			NhanVien nv = nhanVienService.getById(ma);

			dangSua = true;
			maNhanVienDangSua = nv.getMaNhanVien();

			txtMaNhanVien.setText(nv.getMaNhanVien());
			txtTenDangNhap.setText(nv.getTenDangNhap());
			txtHoTen.setText(nv.getHoTen());

			txtSoDienThoai.setText(nv.getSoDienThoai() == null ? "" : nv.getSoDienThoai());

			txtAnhDaiDien.setText(nv.getAnhDaiDien() == null ? "" : nv.getAnhDaiDien());

			hienThiAnh(txtAnhDaiDien.getText().trim());

			cboVaiTro.setSelectedItem(nv.getVaiTro());
			cboTrangThai.setSelectedItem(nv.getTrangThai());

			// Không hiển thị mật khẩu hiện tại.
			txtMatKhau.setText("");

			// Không cho thay đổi mã và tên đăng nhập.
			txtMaNhanVien.setEnabled(false);
			txtTenDangNhap.setEnabled(false);

			// Không đổi mật khẩu tại form sửa thông tin.
			txtMatKhau.setEnabled(false);

		} catch (SQLException | IllegalArgumentException ex) {
			hienThiLoi("app.nhanVien.loiLayThongTin", ex);
		}
	}

	// ==================== THÊM / CẬP NHẬT ====================

	private void luu() {
		try {
			String ma = txtMaNhanVien.getText().trim();
			String tenDangNhap = txtTenDangNhap.getText().trim();
			String matKhau = new String(txtMatKhau.getPassword());
			String hoTen = txtHoTen.getText().trim();

			String soDienThoai = txtSoDienThoai.getText().trim();

			String anhDaiDien = txtAnhDaiDien.getText().trim();

			VaiTro vaiTro = (VaiTro) cboVaiTro.getSelectedItem();

			TrangThaiNhanVien trangThai = (TrangThaiNhanVien) cboTrangThai.getSelectedItem();

			String dienThoai = soDienThoai.isEmpty() ? null : soDienThoai;

			String anh = anhDaiDien.isEmpty() ? null : anhDaiDien;

			if (dangSua) {
				NhanVien nv = nhanVienService.getById(maNhanVienDangSua);

				nv.setHoTen(hoTen);
				nv.setSoDienThoai(dienThoai);
				nv.setAnhDaiDien(anh);
				nv.setVaiTro(vaiTro);
				nv.setTrangThai(trangThai);

				nhanVienService.update(nv);

				thongBao("app.nhanVien.capNhatThanhCong");

			} else {

				NhanVien nv = new NhanVien(ma, tenDangNhap, matKhau, hoTen, dienThoai, anh, vaiTro, trangThai,
						LocalDateTime.now());

				nhanVienService.create(nv);

				thongBao("app.nhanVien.themThanhCong");
			}

			lamMoiForm();

			trangHienTai = 1;
			taiLaiDanhSach();

		} catch (SQLException | IllegalArgumentException ex) {
			hienThiLoi(ex);
		}
	}

	// ==================== XÓA ====================

	private void xoa() {
		String ma = maNhanVienDangSua;

		if (ma == null || ma.isBlank()) {
			thongBao("app.nhanVien.chonNhanVienCanXoa");
			return;
		}

		int confirm = JOptionPane.showConfirmDialog(this,
				I18nReaderUtil.get("app.nhanVien.xacNhanXoa") + " " + ma + "?",
				I18nReaderUtil.get("app.common.confirm"), JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

		if (confirm != JOptionPane.YES_OPTION) {
			return;
		}

		try {
			nhanVienService.delete(ma);

			thongBao("app.nhanVien.xoaThanhCong");

			lamMoiForm();

			trangHienTai = 1;
			taiLaiDanhSach();

		} catch (SQLException | IllegalArgumentException ex) {
			hienThiLoi(ex);
		}
	}

	// ==================== LÀM MỚI ====================

	private void lamMoiForm() {
		dangSua = false;
		maNhanVienDangSua = null;

		// Xóa dữ liệu cũ trong form.
		txtMaNhanVien.setText("");

		txtTenDangNhap.setText("");
		txtMatKhau.setText("");
		txtHoTen.setText("");
		txtSoDienThoai.setText("");
		txtAnhDaiDien.setText("");

		goAnh();

		cboVaiTro.setSelectedItem(VaiTro.NHAN_VIEN_TIEP_NHAN);

		cboTrangThai.setSelectedItem(TrangThaiNhanVien.HOAT_DONG);

		// Mã nhân viên do hệ thống sinh, không cho nhập tay.
		txtMaNhanVien.setEditable(false);
		txtMaNhanVien.setFocusable(false);
		txtTenDangNhap.setEnabled(true);
		txtMatKhau.setEnabled(true);

		if (managementPanel != null) {
			managementPanel.getTable().clearSelection();
		}

		txtMaNhanVien.requestFocusInWindow();
	}

	// ==================== HIỂN THỊ TÊN ENUM ====================

	private String nhanVaiTro(VaiTro vaiTro) {
		if (vaiTro == null) {
			return "";
		}

		return I18nReaderUtil.get("app.enum.VaiTro." + vaiTro.name());
	}

	private String nhanTrangThai(TrangThaiNhanVien trangThai) {

		if (trangThai == null) {
			return "";
		}

		return I18nReaderUtil.get("app.enum.TrangThaiNhanVien." + trangThai.name());
	}

	// ==================== THÔNG BÁO ====================

	private void thongBao(String key) {
		JOptionPane.showMessageDialog(this, I18nReaderUtil.get(key), I18nReaderUtil.get("app.common.notification"),
				JOptionPane.INFORMATION_MESSAGE);
	}

	private void hienThiLoi(String key, Exception ex) {
		String message = I18nReaderUtil.get(key);

		if (message == null || message.isBlank() || message.equals(key)) {
			message = ex.getMessage();
		}

		JOptionPane.showMessageDialog(this, message == null ? "" : message, I18nReaderUtil.get("app.common.error"),
				JOptionPane.ERROR_MESSAGE);
	}

	private void hienThiLoi(Exception ex) {
		String message = ex.getMessage();

		if (message != null && message.startsWith("app.")) {
			String translated = I18nReaderUtil.get(message);

			if (translated != null && !translated.isBlank() && !translated.equals(message)) {
				message = translated;
			}
		}

		if (message == null || message.isBlank()) {
			message = I18nReaderUtil.get("app.common.error");
		}

		JOptionPane.showMessageDialog(this, message, I18nReaderUtil.get("app.common.error"), JOptionPane.ERROR_MESSAGE);
	}
}