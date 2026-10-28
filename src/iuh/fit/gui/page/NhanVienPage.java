package iuh.fit.gui.page;

import iuh.fit.dto.DanhSachNhanVienCriteria;
import iuh.fit.dto.NhanVienFormData;
import iuh.fit.dto.PaginatedResult;
import iuh.fit.entity.NhanVien;
import iuh.fit.enumeration.MenuGroup;
import iuh.fit.enumeration.TrangThaiNhanVien;
import iuh.fit.enumeration.VaiTro;
import iuh.fit.gui.component.AnhTableCellRenderer;
import iuh.fit.gui.component.ManagementFormPanel;
import iuh.fit.gui.component.ManagementPanel;
import iuh.fit.gui.component.ThaoTac;
import iuh.fit.gui.component.ThongBao;
import iuh.fit.gui.form.NhanVienFormPanel;
import iuh.fit.gui.theme.Theme;
import iuh.fit.service.NhanVienService;
import iuh.fit.util.I18nReaderUtil;

import javax.swing.BorderFactory;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableModel;

import java.awt.BorderLayout;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class NhanVienPage extends AbstractPage {

    private static final long serialVersionUID = 1L;
    private static final int PAGE_SIZE = 20;

    private final NhanVienService nhanVienService = new NhanVienService();

    private ManagementPanel managementPanel;
    private ManagementFormPanel form;
    private NhanVienFormPanel formPanel;

    private JTextField boLocMa;
    private JTextField boLocHoTen;
    private JTextField boLocSoDienThoai;
    private ManagementPanel.BoLocChon boLocVaiTro;
    private ManagementPanel.BoLocChon boLocTrangThai;
    private ManagementPanel.BoLocNgay boLocNgayTao;

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
        this.setBackground(Theme.BG);

        managementPanel = new ManagementPanel();
        this.managementPanel.setListTitle("app.nhanVien.danhSachTitle");

        managementPanel.setHeaders(I18nReaderUtil.get("app.nhanVien.hinhAnh"),
                I18nReaderUtil.get("app.nhanVien.maNhanVien"), I18nReaderUtil.get("app.nhanVien.hoTen"),
                I18nReaderUtil.get("app.nhanVien.soDienThoai"), I18nReaderUtil.get("app.nhanVien.vaiTro"),
                I18nReaderUtil.get("app.nhanVien.trangThai"));

        this.managementPanel.addActionColumn("app.common.actions",
                new ThaoTac("trash-2", "app.common.delete", ThaoTac.KieuThaoTac.NGUY_HIEM,
                        row -> this.xoa(row)));

        this.form = new ManagementFormPanel("app.nhanVien.thongTinTitle");

        khoiTaoForm();
        khoiTaoBoLoc();
        ganSuKien();

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, this.form, this.managementPanel);
        split.setDividerLocation(480);
        split.setOpaque(false);
        split.setBorder(null);
        this.add(split, BorderLayout.CENTER);

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
        this.formPanel = new NhanVienFormPanel();
        this.form.addBlock(this.formPanel);
        this.lamMoiForm();
    }

    // ==================== BỘ LỌC ====================

    private void khoiTaoBoLoc() {
        this.boLocMa = this.managementPanel.addTextFilter("app.nhanVien.maNhanVien");

        this.boLocHoTen = this.managementPanel.addTextFilter("app.nhanVien.hoTen");

        this.boLocSoDienThoai = this.managementPanel.addTextFilter("app.nhanVien.soDienThoai");

        this.boLocVaiTro = this.managementPanel.addComboBoxFilter("app.nhanVien.vaiTro", VaiTro.values());

        this.boLocTrangThai = this.managementPanel.addComboBoxFilter("app.nhanVien.trangThai",
                TrangThaiNhanVien.values());

        this.boLocNgayTao = this.managementPanel.addDateRangeFilter("app.nhanVien.ngayTao");

        this.managementPanel.setFilterChangeListener(() -> {
            this.trangHienTai = 1;
            this.taiLaiDanhSach();
        });
    }

    private String layGiaTriBoLoc(String value) {
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
        this.form.getNutThem().addActionListener(e -> this.them());

        this.form.getNutLuu().addActionListener(e -> this.luu());

        this.form.getNutXoaTrang().addActionListener(e -> this.lamMoiForm());

        this.form.getNutXoa().addActionListener(e -> this.xoa(this.managementPanel.getSelectedRow()));

        this.managementPanel.getClearFilterButton().addActionListener(e -> {
            this.trangHienTai = 1;
            this.managementPanel.clearFilters();
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
                            this.layGiaTriBoLoc(this.boLocMa.getText()),
                            this.layGiaTriBoLoc(this.boLocHoTen.getText()),
                            this.layGiaTriBoLoc(this.boLocSoDienThoai.getText()),
                            this.boLocVaiTro.ma(),
                            this.boLocTrangThai.ma(),
                            this.layTimNhanh(),
                            this.boLocNgayTao.tuNgay(),
                            this.boLocNgayTao.denNgay(),
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

            this.formPanel.napDuLieu(nv);

        } catch (SQLException | IllegalArgumentException ex) {
            ThongBao.loi(this, "app.nhanVien.loiLayThongTin");
        }
    }

    // ==================== THÊM / CẬP NHẬT ====================

    private NhanVienFormData getFormData() {
        return this.formPanel.layDuLieu();
    }

    private void validateFormData(NhanVienFormData data) {
        if (data.hoTen() == null || data.hoTen().isBlank()) {
            throw new IllegalArgumentException("app.nhanVien.loiHoTen");
        }
        String soDienThoai = data.soDienThoai();
        if (soDienThoai == null || soDienThoai.isBlank() || !soDienThoai.trim().matches("\\d{9,10}")) {
            throw new IllegalArgumentException("app.nhanVien.loiSoDienThoai");
        }
    }

    private void them() {
        NhanVienFormData data = this.getFormData();

        try {
            this.validateFormData(data);
            String dienThoai = data.soDienThoai().isEmpty() ? null : data.soDienThoai();
            String anh = data.anhDaiDien().isEmpty() ? null : data.anhDaiDien();
            NhanVien nv = new NhanVien(data.maNhanVien(), data.tenDangNhap(), data.matKhau(), data.hoTen(),
                    dienThoai, anh, data.vaiTro(), data.trangThai(), LocalDateTime.now());

            this.nhanVienService.create(nv);

            ThongBao.thongTin(this, "app.nhanVien.themThanhCong");

            this.lamMoiForm();

            this.trangHienTai = 1;
            this.taiLaiDanhSach();

        } catch (SQLException | IllegalArgumentException ex) {
            ThongBao.loi(this, ex);
        }
    }

    private void luu() {
        NhanVienFormData data = this.getFormData();

        try {
            this.validateFormData(data);
            String dienThoai = data.soDienThoai().isEmpty() ? null : data.soDienThoai();
            String anh = data.anhDaiDien().isEmpty() ? null : data.anhDaiDien();
            if (this.dangSua) {
                NhanVien nv = this.nhanVienService.getById(this.maNhanVienDangSua);

                this.capNhatThongTin(nv, data);

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

    private void capNhatThongTin(NhanVien nv, NhanVienFormData data) {
        this.validateFormData(data);
        nv.setHoTen(data.hoTen());
        nv.setSoDienThoai(data.soDienThoai().isEmpty() ? null : data.soDienThoai());
        nv.setAnhDaiDien(data.anhDaiDien().isEmpty() ? null : data.anhDaiDien());
        nv.setVaiTro(data.vaiTro());
        nv.setTrangThai(data.trangThai());
        nv.setMatKhau(data.matKhau().isBlank() ? null : data.matKhau());
    }

    private void xoa(int row) {
        if (row < 0 || row >= this.managementPanel.getModel().getRowCount()) {
            ThongBao.loi(this, "app.nhanVien.chonNhanVienCanXoa");
            return;
        }

        String ma = String.valueOf(
                this.managementPanel.getModel().getValueAt(row, 1));

        if (ma.isBlank()) {
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

        this.formPanel.lamMoi();

        if (this.managementPanel != null) {
            this.managementPanel.getTable().clearSelection();
        }
    }
}
