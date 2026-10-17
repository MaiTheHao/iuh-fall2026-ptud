package iuh.fit.gui.page;

import iuh.fit.dto.DanhSachNhanVienCriteria;
import iuh.fit.dto.NhanVienFormData;
import iuh.fit.dto.PaginatedResult;
import iuh.fit.entity.NhanVien;
import iuh.fit.enumeration.MenuGroup;
import iuh.fit.enumeration.TrangThaiNhanVien;
import iuh.fit.enumeration.VaiTro;
import iuh.fit.gui.component.AnhField;
import iuh.fit.gui.component.AnhTableCellRenderer;
import iuh.fit.gui.component.ManagementPanel;
import iuh.fit.gui.component.ThongBao;
import iuh.fit.service.NhanVienService;
import iuh.fit.util.I18nReaderUtil;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableModel;

import java.awt.BorderLayout;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class NhanVienPage extends AbstractPage {

    private static final long serialVersionUID = 1L;
    private static final int PAGE_SIZE = 20;

    private final NhanVienService nhanVienService = new NhanVienService();

    private final AnhField anhField = new AnhField(
            "app.nhanVien.anhUrl",
            "app.nhanVien.chonAnh",
            "app.nhanVien.goAnh",
            "app.nhanVien.chuaCoAnh",
            "app.nhanVien.khongTaiDuocAnh",
            "app.nhanVien.loaiTepAnh",
            "app.nhanVien.duLieuKhongPhaiAnh");

    private ManagementPanel managementPanel;

    private final JTextField txtMaNhanVien = new JTextField(14);
    private final JTextField txtTenDangNhap = new JTextField(14);
    private final JPasswordField txtMatKhau = new JPasswordField(14);
    private final JTextField txtHoTen = new JTextField(14);
    private final JTextField txtSoDienThoai = new JTextField(14);
    private final JTextField txtNgayTao = new JTextField(14);

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

        managementPanel.setHeaders(I18nReaderUtil.get("app.nhanVien.hinhAnh"),
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

    @Override
    public boolean hasSeparatorBefore() {
        return true;
    }

    // ==================== FORM ====================

    private void khoiTaoForm() {
        JPanel form = this.managementPanel.getFormPanel();

        form.removeAll();

        this.txtMaNhanVien.setEditable(false);
        this.txtMaNhanVien.setFocusable(false);
        this.txtTenDangNhap.setEnabled(true);
        this.txtMatKhau.setEnabled(true);

        this.managementPanel.addFormRow("app.nhanVien.maNhanVien", this.txtMaNhanVien);

        this.managementPanel.addFormRow("app.nhanVien.tenDangNhap", this.txtTenDangNhap);

        this.managementPanel.addFormRow("app.nhanVien.matKhau", this.txtMatKhau);

        this.managementPanel.addFormRow("app.nhanVien.hoTen", this.txtHoTen);

        this.managementPanel.addFormRow("app.nhanVien.soDienThoai", this.txtSoDienThoai);

        this.cboVaiTro = new JComboBox<>(VaiTro.values());

        this.managementPanel.addFormRow("app.nhanVien.vaiTro", this.cboVaiTro);

        this.cboTrangThai = new JComboBox<>(TrangThaiNhanVien.values());

        this.managementPanel.addFormRow("app.nhanVien.trangThai", this.cboTrangThai);

        this.txtNgayTao.setEnabled(false);

        this.managementPanel.addFormRow("app.nhanVien.ngayTao", this.txtNgayTao);

        form.add(this.anhField);

        form.revalidate();
        form.repaint();

        this.lamMoiForm();
    }

    // ==================== BỘ LỌC ====================

    private void khoiTaoBoLoc() {
        this.managementPanel.addTextFilter("app.nhanVien.maNhanVien", 0);

        this.managementPanel.addTextFilter("app.nhanVien.hoTen", 1);

        this.managementPanel.addTextFilter("app.nhanVien.soDienThoai", 2);

        this.managementPanel.addComboBoxFilter("app.nhanVien.vaiTro", 3, VaiTro.values());

        this.managementPanel.addComboBoxFilter("app.nhanVien.trangThai", 4, TrangThaiNhanVien.values());

        this.managementPanel.addDateRangeFilter("app.nhanVien.ngayTao", 5);

        this.managementPanel.setFilterChangeListener(() -> {
            this.trangHienTai = 1;
            this.taiLaiDanhSach();
        });
    }

    private String layGiaTriBoLoc(int index) {
        String value = this.managementPanel.getTextFilter(index);

        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }

    private String layTimNhanh() {
        String value = this.managementPanel.getQuickFilter();

        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }

    // ==================== SỰ KIỆN ====================

    private void ganSuKien() {
        this.managementPanel.getThemButton().addActionListener(e -> this.lamMoiForm());

        this.managementPanel.getLuuButton().addActionListener(e -> this.luu());

        this.managementPanel.getXoaTrangButton().addActionListener(e -> this.lamMoiForm());

        this.managementPanel.getXoaButton().addActionListener(e -> this.xoa());

        this.managementPanel.getClearFilterButton().addActionListener(e -> {
            this.trangHienTai = 1;
            this.managementPanel.clearFilters();
            this.taiLaiDanhSach();
        });

        this.managementPanel.getPrevButton().addActionListener(e -> {
            if (this.trangHienTai > 1) {
                this.trangHienTai--;
                this.taiLaiDanhSach();
            }
        });

        this.managementPanel.getNextButton().addActionListener(e -> {
            if (this.ketQua != null && this.trangHienTai < this.ketQua.totalPages()) {
                this.trangHienTai++;
                this.taiLaiDanhSach();
            }
        });

        this.managementPanel.getTable().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                this.chonDong();
            }
        });
    }

    // ==================== TẢI DANH SÁCH ====================

    private void taiLaiDanhSach() {
        try {
            DanhSachNhanVienCriteria criteria =
                    new DanhSachNhanVienCriteria(
                            this.layGiaTriBoLoc(0),
                            this.layGiaTriBoLoc(1),
                            this.layGiaTriBoLoc(2),
                            this.managementPanel.getComboFilter(3),
                            this.managementPanel.getComboFilter(4),
                            this.layTimNhanh(),
                            this.managementPanel.getDateFilterFrom(5),
                            this.managementPanel.getDateFilterTo(5),
                            this.trangHienTai,
                            PAGE_SIZE
                    );

            this.ketQua = this.nhanVienService.search(criteria);

            if (this.ketQua == null) {
                return;
            }

            if (this.ketQua.totalPages() > 0 && this.trangHienTai > this.ketQua.totalPages()) {

                this.trangHienTai = this.ketQua.totalPages();
                this.taiLaiDanhSach();
                return;
            }

            this.hienThiDanhSach();

        } catch (SQLException ex) {
            ThongBao.loi(this, "app.nhanVien.loiTaiDanhSach");
        }
    }

    private void hienThiDanhSach() {
        if (this.ketQua == null) {
            return;
        }

        List<Object[]> rows = new ArrayList<>();

        for (NhanVien nv : this.ketQua.items()) {
            rows.add(new Object[] { nv.getAnhDaiDien(), nv.getMaNhanVien(), nv.getHoTen(),
                    nv.getSoDienThoai() == null ? "" : nv.getSoDienThoai(),
                    nv.getVaiTro() == null ? "" : nv.getVaiTro().getI18NBusinessName(),
                    nv.getTrangThai() == null ? "" : nv.getTrangThai().getI18NBusinessName() });
        }

        this.managementPanel.setRows(rows);

        this.cauHinhCotAnh();

        int from = this.ketQua.fromIndex();

        this.managementPanel.setCount(from, this.ketQua.toIndex(), this.ketQua.totalItems());

        this.managementPanel.setPage(this.ketQua.page(), Math.max(1, this.ketQua.totalPages()));

        this.trangHienTai = this.ketQua.page();
    }

    // ==================== CỘT ẢNH ====================

    private void cauHinhCotAnh() {
        AnhTableCellRenderer.ganVao(this.managementPanel, 0,
                "app.nhanVien.chuaCoAnh", "app.nhanVien.khongTaiDuocAnh");
    }

    // ==================== CHỌN NHÂN VIÊN ====================

    private void chonDong() {
        JTable table = this.managementPanel.getTable();

        int selectedRow = table.getSelectedRow();

        if (selectedRow < 0) {
            return;
        }

        int modelRow = table.convertRowIndexToModel(selectedRow);

        TableModel model = table.getModel();

        // Cột 0 là ảnh, cột 1 là mã nhân viên.
        String ma = String.valueOf(model.getValueAt(modelRow, 1));

        try {
            NhanVien nv = this.nhanVienService.getById(ma);

            this.dangSua = true;
            this.maNhanVienDangSua = nv.getMaNhanVien();

            this.txtMaNhanVien.setText(nv.getMaNhanVien());
            this.txtTenDangNhap.setText(nv.getTenDangNhap());
            this.txtHoTen.setText(nv.getHoTen());

            this.txtSoDienThoai.setText(nv.getSoDienThoai() == null ? "" : nv.getSoDienThoai());

            this.txtNgayTao.setText(nv.getNgayTao() == null ? ""
                    : nv.getNgayTao().format(DateTimeFormatter.ofPattern(I18nReaderUtil.get("app.date.format"))));

            this.anhField.setDuongDan(nv.getAnhDaiDien());

            this.cboVaiTro.setSelectedItem(nv.getVaiTro());
            this.cboTrangThai.setSelectedItem(nv.getTrangThai());

            // Không hiển thị mật khẩu hiện tại.
            this.txtMatKhau.setText("");

            // Không cho thay đổi mã và tên đăng nhập.
            this.txtMaNhanVien.setEnabled(false);
            this.txtTenDangNhap.setEnabled(false);

            // Không đổi mật khẩu tại form sửa thông tin.
            this.txtMatKhau.setEnabled(false);

        } catch (SQLException | IllegalArgumentException ex) {
            ThongBao.loi(this, "app.nhanVien.loiLayThongTin");
        }
    }

    // ==================== THÊM / CẬP NHẬT ====================

    private NhanVienFormData getFormData() {
        String soDienThoai = this.txtSoDienThoai.getText().trim();

        String anhDaiDien = this.anhField.getDuongDan();

        return new NhanVienFormData(
                this.txtMaNhanVien.getText().trim(),
                this.txtTenDangNhap.getText().trim(),
                new String(this.txtMatKhau.getPassword()),
                this.txtHoTen.getText().trim(),
                soDienThoai,
                anhDaiDien,
                (VaiTro) this.cboVaiTro.getSelectedItem(),
                (TrangThaiNhanVien) this.cboTrangThai.getSelectedItem(),
                this.txtNgayTao.getText().trim());
    }

    private void validateFormData(NhanVienFormData data) {
    }

    private void luu() {
        NhanVienFormData data = this.getFormData();
        this.validateFormData(data);

        String dienThoai = data.soDienThoai().isEmpty() ? null : data.soDienThoai();

        String anh = data.anhDaiDien().isEmpty() ? null : data.anhDaiDien();

        try {
            if (this.dangSua) {
                NhanVien nv = this.nhanVienService.getById(this.maNhanVienDangSua);

                nv.setHoTen(data.hoTen());
                nv.setSoDienThoai(dienThoai);
                nv.setAnhDaiDien(anh);
                nv.setVaiTro(data.vaiTro());
                nv.setTrangThai(data.trangThai());

                this.nhanVienService.update(nv);

                ThongBao.thongTin(this, "app.nhanVien.capNhatThanhCong");

            } else {

                NhanVien nv = new NhanVien(data.maNhanVien(), data.tenDangNhap(), data.matKhau(), data.hoTen(),
                        dienThoai, anh, data.vaiTro(), data.trangThai(), LocalDateTime.now());

                this.nhanVienService.create(nv);

                ThongBao.thongTin(this, "app.nhanVien.themThanhCong");
            }

            this.lamMoiForm();

            this.trangHienTai = 1;
            this.taiLaiDanhSach();

        } catch (SQLException | IllegalArgumentException ex) {
            ThongBao.loi(this, ex);
        }
    }

    // ==================== XÓA ====================

    private void xoa() {
        String ma = this.maNhanVienDangSua;

        if (ma == null || ma.isBlank()) {
            ThongBao.loi(this, "app.nhanVien.chonNhanVienCanXoa");
            return;
        }

        if (!ThongBao.xacNhan(this, "app.nhanVien.xacNhanXoa", "app.common.confirm")) {
            return;
        }

        try {
            this.nhanVienService.delete(ma);

            ThongBao.thongTin(this, "app.nhanVien.xoaThanhCong");

            this.lamMoiForm();

            this.trangHienTai = 1;
            this.taiLaiDanhSach();

        } catch (SQLException | IllegalArgumentException ex) {
            ThongBao.loi(this, ex);
        }
    }

    // ==================== LÀM MỚI ====================

    private void lamMoiForm() {
        this.dangSua = false;
        this.maNhanVienDangSua = null;

        // Xóa dữ liệu cũ trong form.
        this.txtMaNhanVien.setText("");

        this.txtTenDangNhap.setText("");
        this.txtMatKhau.setText("");
        this.txtHoTen.setText("");
        this.txtSoDienThoai.setText("");
        this.txtNgayTao.setText("");

        this.anhField.clear();

        this.cboVaiTro.setSelectedItem(VaiTro.NHAN_VIEN_TIEP_NHAN);

        this.cboTrangThai.setSelectedItem(TrangThaiNhanVien.HOAT_DONG);

        // Mã nhân viên do hệ thống sinh, không cho nhập tay.
        this.txtMaNhanVien.setEditable(false);
        this.txtMaNhanVien.setFocusable(false);
        this.txtTenDangNhap.setEnabled(true);
        this.txtMatKhau.setEnabled(true);

        if (this.managementPanel != null) {
            this.managementPanel.getTable().clearSelection();
        }

        this.txtMaNhanVien.requestFocusInWindow();
    }
}
