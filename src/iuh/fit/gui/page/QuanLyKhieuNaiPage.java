package iuh.fit.gui.page;

import iuh.fit.dto.DanhSachKhieuNaiCriteria;
import iuh.fit.dto.KhieuNaiUpdateFormData;
import iuh.fit.dto.KhieuNaiView;
import iuh.fit.dto.PaginatedResult;
import iuh.fit.enumeration.MenuGroup;
import iuh.fit.enumeration.TrangThaiKhieuNai;
import iuh.fit.gui.component.ManagementFormPanel;
import iuh.fit.gui.component.ManagementPanel;
import iuh.fit.gui.component.SuaDialog;
import iuh.fit.gui.component.ThaoTac;
import iuh.fit.gui.component.ThongBao;
import iuh.fit.gui.form.KhieuNaiFormPanel;
import iuh.fit.gui.theme.Theme;
import iuh.fit.service.KhieuNaiService;
import iuh.fit.util.I18nReaderUtil;

import javax.swing.BorderFactory;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Component;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class QuanLyKhieuNaiPage extends AbstractPage {

    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = Logger.getLogger(QuanLyKhieuNaiPage.class.getName());

    private static final int SO_DONG_MOI_TRANG = 20;

    private static final DateTimeFormatter DINH_DANG_NGAY_GIO =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final KhieuNaiService khieuNaiService = new KhieuNaiService();

    private ManagementFormPanel form;
    private ManagementPanel managementPanel;
    private KhieuNaiFormPanel formPanel;
    private List<KhieuNaiView> danhSachHienTai = List.of();

    private JTextField boLocMa;
    private JTextField boLocKhachHang;
    private JTextField boLocPhieuTiepNhan;
    private JTextField boLocNoiDung;
    private ManagementPanel.BoLocChon boLocTrangThai;
    private ManagementPanel.BoLocNgay boLocNgayTao;

    private int trang = 1;

    public QuanLyKhieuNaiPage() {
        this.setLayout(new BorderLayout());
        this.setBackground(Theme.BG);
    }

    @Override
    public void render() {
        this.removeAll();
        this.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        this.setBackground(Theme.BG);

        this.managementPanel = new ManagementPanel();
        this.managementPanel.setListTitle("app.khieuNai.danhSachTitle");

        this.managementPanel.setHeaders(
                I18nReaderUtil.get("app.khieuNai.ma"),
                I18nReaderUtil.get("app.khieuNai.khachHang"),
                I18nReaderUtil.get("app.khieuNai.phieuTiepNhan"),
                I18nReaderUtil.get("app.khieuNai.noiDung"),
                I18nReaderUtil.get("app.khieuNai.trangThai"),
                I18nReaderUtil.get("app.khieuNai.ngayTao")
        );

        this.managementPanel.addActionColumn("app.common.actions",
                new ThaoTac("pencil", "app.common.edit", ThaoTac.KieuThaoTac.THUONG,
                        row -> this.suaKhieuNai(row)),
                new ThaoTac("trash-2", "app.common.delete", ThaoTac.KieuThaoTac.NGUY_HIEM,
                        row -> this.xoaKhieuNai(row)));

        this.boLocMa = this.managementPanel.addTextFilter("app.khieuNai.ma");
        this.boLocKhachHang = this.managementPanel.addTextFilter("app.khieuNai.khachHang");
        this.boLocPhieuTiepNhan = this.managementPanel.addTextFilter("app.khieuNai.phieuTiepNhan");
        this.boLocNoiDung = this.managementPanel.addTextFilter("app.khieuNai.noiDung");
        this.boLocTrangThai = this.managementPanel.addComboBoxFilter(
                "app.khieuNai.trangThai", TrangThaiKhieuNai.values());
        this.boLocNgayTao = this.managementPanel.addDateRangeFilter("app.khieuNai.ngayTaoTuDen");

        this.managementPanel.setFilterChangeListener(() -> this.taiLai(1));

        this.form = new ManagementFormPanel("app.khieuNai.thongTinTitle");

        this.formPanel = new KhieuNaiFormPanel();
        this.form.addBlock(this.formPanel);

        this.form.getNutThem().addActionListener(e -> this.goTo(TiepNhanKhieuNaiPage.class));
        this.form.getNutLuu().addActionListener(e -> this.luuKhieuNai());
        this.form.getNutXoa().addActionListener(
                e -> this.xoaKhieuNai(this.managementPanel.getSelectedRow()));
        this.form.getNutXoaTrang().setText(I18nReaderUtil.get("app.khieuNai.xoaTrang"));
        this.form.getNutXoaTrang().addActionListener(e -> this.lamMoiForm());

        this.managementPanel.getClearFilterButton().addActionListener(
                e -> this.managementPanel.clearFilters());
        this.managementPanel.getPrevButton().addActionListener(
                e -> this.taiLai(this.trang - 1));
        this.managementPanel.getNextButton().addActionListener(
                e -> this.taiLai(this.trang + 1));

        this.managementPanel.getTable().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                this.chonDong();
            }
        });

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, this.form, this.managementPanel);
        split.setDividerLocation(480);
        split.setOpaque(false);
        split.setBorder(null);

        this.add(split, BorderLayout.CENTER);

        this.lamMoiForm();
        this.taiLai(1);

        this.revalidate();
        this.repaint();
    }

    private void taiLai(int page) {
        try {
            TrangThaiKhieuNai trangThai = this.boLocTrangThai.giaTri()
                    instanceof TrangThaiKhieuNai value ? value : null;

            DanhSachKhieuNaiCriteria tieuChi = new DanhSachKhieuNaiCriteria(
                    this.boLocMa.getText(),
                    this.boLocKhachHang.getText(),
                    this.boLocPhieuTiepNhan.getText(),
                    this.boLocNoiDung.getText(),
                    trangThai,
                    this.boLocNgayTao.tuNgay(),
                    this.boLocNgayTao.denNgay(),
                    this.managementPanel.getQuickFilter(),
                    Math.max(page, 1),
                    SO_DONG_MOI_TRANG
            );

            PaginatedResult<KhieuNaiView> ketQua = this.khieuNaiService.search(tieuChi);

            List<Object[]> rows = new ArrayList<>();
            for (KhieuNaiView view : ketQua.items()) {
                rows.add(new Object[]{
                        view.maKhieuNai(),
                        view.tenKhachHang(),
                        view.maPhieuTiepNhan(),
                        view.noiDung(),
                        view.trangThai() == null ? "" : view.trangThai().getI18NBusinessName(),
                        this.dinhDangNgayGio(view.ngayTao())
                });
            }

            this.danhSachHienTai = ketQua.items();

            this.managementPanel.setRows(rows);
            this.managementPanel.setCount(
                    ketQua.fromIndex(),
                    ketQua.toIndex(),
                    ketQua.totalItems());
            this.managementPanel.setPage(
                    ketQua.page(),
                    Math.max(ketQua.totalPages(), 1));

            this.trang = ketQua.page();

        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Khong the tai danh sach khieu nai", ex);
            ThongBao.loi(this, "app.khieuNai.loiTaiDanhSach");
        }
    }

    private void chonDong() {
        int row = this.managementPanel.getSelectedRow();
        if (row < 0 || row >= this.danhSachHienTai.size()) {
            return;
        }

        this.formPanel.napDuLieu(this.danhSachHienTai.get(row));
    }

    private void lamMoiForm() {
        this.formPanel.lamMoi();
        this.managementPanel.getTable().clearSelection();
    }

    private void luuKhieuNai() {
        int row = this.managementPanel.getSelectedRow();
        KhieuNaiUpdateFormData data = this.formPanel.layDuLieu();

        if (row < 0 || data.maKhieuNai().isBlank()) {
            ThongBao.canhBao(this, "app.khieuNai.loiMa");
            return;
        }

        if (!this.capNhat(this, data)) {
            return;
        }

        this.taiLai(this.trang);
        this.lamMoiForm();
    }

    private void suaKhieuNai(int row) {
        if (row < 0 || row >= this.danhSachHienTai.size()) {
            return;
        }

        KhieuNaiView view = this.danhSachHienTai.get(row);

        KhieuNaiFormPanel formSua = new KhieuNaiFormPanel();
        formSua.napDuLieu(view);

        SuaDialog dialog = new SuaDialog(
                SwingUtilities.getWindowAncestor(this), "app.common.editTitle", formSua);
        dialog.datXuLyLuu(() -> this.capNhat(dialog, formSua.layDuLieu()));

        if (dialog.hienThiVaChoKetQua()) {
            this.taiLai(this.trang);
            this.lamMoiForm();
        }
    }

    private boolean capNhat(Component parent, KhieuNaiUpdateFormData data) {
        try {
            this.khieuNaiService.update(data);
            ThongBao.thongTin(parent, "app.common.update_succeed");
            return true;
        } catch (IllegalArgumentException ex) {
            ThongBao.loi(parent, ex);
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Khong the cap nhat khieu nai", ex);
            ThongBao.loi(parent, "app.login.errorSystem");
        }

        return false;
    }

    private void xoaKhieuNai(int row) {
        try {
            if (row < 0 || row >= this.danhSachHienTai.size()) {
                ThongBao.canhBao(this, "app.khieuNai.chuaChonXoa");
                return;
            }

            String maKhieuNai = this.danhSachHienTai.get(row).maKhieuNai();
            if (maKhieuNai == null || maKhieuNai.isBlank()) {
                ThongBao.canhBao(this, "app.khieuNai.chuaChonXoa");
                return;
            }

            if (!ThongBao.xacNhan(this, "app.khieuNai.xacNhanXoa", "app.khieuNai.tieuDeXoa")) {
                return;
            }

            this.khieuNaiService.delete(maKhieuNai);
            ThongBao.thongTin(this, "app.common.delete_succeed");

            this.taiLai(this.trang);
            this.lamMoiForm();

        } catch (IllegalArgumentException ex) {
            ThongBao.loi(this, ex);

        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Khong the xoa khieu nai", ex);
            ThongBao.loi(this, "app.login.errorSystem");
        }
    }

    private String dinhDangNgayGio(LocalDateTime thoiGian) {
        return thoiGian == null ? "" : thoiGian.format(DINH_DANG_NGAY_GIO);
    }

    @Override
    public String getTitleKey() {
        return "app.menu.complaintManagement";
    }

    @Override
    public MenuGroup getGroup() {
        return MenuGroup.TIEP_NHAN;
    }
}
