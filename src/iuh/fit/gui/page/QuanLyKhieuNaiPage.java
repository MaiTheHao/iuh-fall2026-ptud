package iuh.fit.gui.page;

import iuh.fit.dto.BaseFormDialogConfig;
import iuh.fit.dto.DanhSachKhieuNaiCriteria;
import iuh.fit.dto.KhieuNaiUpdateFormData;
import iuh.fit.dto.KhieuNaiView;
import iuh.fit.dto.PaginatedResult;
import iuh.fit.enumeration.MenuGroup;
import iuh.fit.enumeration.TrangThaiKhieuNai;
import iuh.fit.gui.Theme;
import iuh.fit.gui.UiConstants;
import iuh.fit.gui.component.BaseFormDialog;
import iuh.fit.gui.component.ManagementPanel;
import iuh.fit.gui.component.ThaoTac;
import iuh.fit.gui.component.ThongBao;
import iuh.fit.gui.form.KhieuNaiFormPanel;
import iuh.fit.service.KhieuNaiService;
import iuh.fit.util.DateFormats;
import iuh.fit.util.I18nReaderUtil;

import javax.swing.BorderFactory;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Component;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class QuanLyKhieuNaiPage extends AbstractPage {

    

    private static final Logger LOGGER = Logger.getLogger(QuanLyKhieuNaiPage.class.getName());


    private static final int CHIEU_CAO_SUA = 570;

    private final KhieuNaiService khieuNaiService = new KhieuNaiService();

    private ManagementPanel pnlDanhSach;
    private List<KhieuNaiView> danhSachHienTai = List.of();

    private JTextField txtBoLocMa;
    private JTextField txtBoLocKhachHang;
    private JTextField txtBoLocPhieuTiepNhan;
    private JTextField txtBoLocNoiDung;
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

        this.pnlDanhSach = new ManagementPanel();
        this.pnlDanhSach.setListTitle("app.khieuNai.danhSachTitle");

        this.pnlDanhSach.setHeaders(
                I18nReaderUtil.get("app.khieuNai.ma"),
                I18nReaderUtil.get("app.khieuNai.khachHang"),
                I18nReaderUtil.get("app.khieuNai.phieuTiepNhan"),
                I18nReaderUtil.get("app.khieuNai.noiDung"),
                I18nReaderUtil.get("app.khieuNai.trangThai"),
                I18nReaderUtil.get("app.khieuNai.ngayTao")
        );

        this.pnlDanhSach.addActionColumn("app.common.actions",
                new ThaoTac("pencil", "app.common.edit", ThaoTac.KieuThaoTac.THUONG,
                        row -> this.suaKhieuNai(row)),
                new ThaoTac("trash-2", "app.common.delete", ThaoTac.KieuThaoTac.NGUY_HIEM,
                        row -> this.xoaKhieuNai(row)));

        this.txtBoLocMa = this.pnlDanhSach.addTextFilter("app.khieuNai.ma");
        this.txtBoLocKhachHang = this.pnlDanhSach.addTextFilter("app.khieuNai.khachHang");
        this.txtBoLocPhieuTiepNhan = this.pnlDanhSach.addTextFilter("app.khieuNai.phieuTiepNhan");
        this.txtBoLocNoiDung = this.pnlDanhSach.addTextFilter("app.khieuNai.noiDung");
        this.boLocTrangThai = this.pnlDanhSach.addComboBoxFilter(
                "app.khieuNai.trangThai", TrangThaiKhieuNai.values());
        this.boLocNgayTao = this.pnlDanhSach.addDateRangeFilter("app.khieuNai.ngayTaoTuDen");

        this.pnlDanhSach.setFilterChangeListener(() -> this.taiLai(1));

        this.pnlDanhSach.setAddAction("app.common.add_new",
                () -> this.goTo(TiepNhanKhieuNaiPage.class));
        this.pnlDanhSach.setRowDoubleClickListener(this::suaKhieuNai);

        this.pnlDanhSach.getClearFilterButton().addActionListener(
                e -> this.pnlDanhSach.clearFilters());
        this.pnlDanhSach.getPrevButton().addActionListener(
                e -> this.taiLai(this.trang - 1));
        this.pnlDanhSach.getNextButton().addActionListener(
                e -> this.taiLai(this.trang + 1));

        this.add(this.pnlDanhSach, BorderLayout.CENTER);

        this.taiLai(1);

        this.revalidate();
        this.repaint();
    }

    private void taiLai(int page) {
        try {
            TrangThaiKhieuNai trangThai = this.boLocTrangThai.giaTri()
                    instanceof TrangThaiKhieuNai value ? value : null;

            DanhSachKhieuNaiCriteria tieuChi = new DanhSachKhieuNaiCriteria(
                    this.txtBoLocMa.getText(),
                    this.txtBoLocKhachHang.getText(),
                    this.txtBoLocPhieuTiepNhan.getText(),
                    this.txtBoLocNoiDung.getText(),
                    trangThai,
                    this.boLocNgayTao.tuNgay(),
                    this.boLocNgayTao.denNgay(),
                    this.pnlDanhSach.getQuickFilter(),
                    Math.max(page, 1),
                    UiConstants.SO_DONG_MOI_TRANG
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

            this.pnlDanhSach.setRows(rows);
            this.pnlDanhSach.setCount(
                    ketQua.fromIndex(),
                    ketQua.toIndex(),
                    ketQua.totalItems());
            this.pnlDanhSach.setPage(
                    ketQua.page(),
                    Math.max(ketQua.totalPages(), 1));

            this.trang = ketQua.page();

        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Khong the tai danh sach khieu nai", ex);
            ThongBao.loi(this, "app.khieuNai.loiTaiDanhSach");
        }
    }

    private void suaKhieuNai(int row) {
        if (row < 0 || row >= this.danhSachHienTai.size()) {
            return;
        }

        KhieuNaiView view = this.danhSachHienTai.get(row);

        KhieuNaiFormPanel formSua = new KhieuNaiFormPanel();
        formSua.napDuLieu(view);

        BaseFormDialog dialog = new BaseFormDialog(new BaseFormDialogConfig(
                SwingUtilities.getWindowAncestor(this),
                "app.common.editTitle",
                formSua,
                () -> this.capNhat(SwingUtilities.getWindowAncestor(formSua), formSua.layDuLieu()),
                CHIEU_CAO_SUA));

        if (dialog.showAndGet()) {
            this.taiLai(this.trang);
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

        } catch (IllegalArgumentException ex) {
            ThongBao.loi(this, ex);

        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Khong the xoa khieu nai", ex);
            ThongBao.loi(this, "app.login.errorSystem");
        }
    }

    private String dinhDangNgayGio(LocalDateTime thoiGian) {
        return thoiGian == null ? "" : thoiGian.format(DateFormats.NGAY_GIO);
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
