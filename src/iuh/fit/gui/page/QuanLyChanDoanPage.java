package iuh.fit.gui.page;

import iuh.fit.dto.BaseFormDialogConfig;
import iuh.fit.dto.ChanDoanUpdateFormData;
import iuh.fit.dto.ChanDoanView;
import iuh.fit.dto.DanhSachChanDoanCriteria;
import iuh.fit.dto.PaginatedResult;
import iuh.fit.enumeration.MenuGroup;
import iuh.fit.enumeration.TrangThaiVanDeKyThuat;
import iuh.fit.gui.component.BaseFormDialog;
import iuh.fit.gui.component.ManagementPanel;
import iuh.fit.gui.component.ThaoTac;
import iuh.fit.gui.component.ThongBao;
import iuh.fit.gui.form.ChanDoanSuaFormPanel;
import iuh.fit.gui.theme.Theme;
import iuh.fit.service.VanDeKyThuatService;
import iuh.fit.util.I18nReaderUtil;
import iuh.fit.util.NumberParseUtil;

import javax.swing.BorderFactory;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Component;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class QuanLyChanDoanPage extends AbstractPage {

    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = Logger.getLogger(QuanLyChanDoanPage.class.getName());

    private static final int SO_DONG_MOI_TRANG = 20;

    /** Dialog sửa chẩn đoán: 3 khối đọc + 4 khối nhập liệu + combo. */
    private static final int CHIEU_CAO_SUA = 650;

    private static final DateTimeFormatter DINH_DANG_NGAY_GIO =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final VanDeKyThuatService vanDeKyThuatService = new VanDeKyThuatService();

    private ManagementPanel managementPanel;
    private List<ChanDoanView> danhSachHienTai = List.of();

    private JTextField boLocPhieuTiepNhan;
    private ManagementPanel.BoLocKhoang boLocMucDo;
    private ManagementPanel.BoLocChon boLocKhachHangDongY;
    private ManagementPanel.BoLocNgay boLocNgayTao;

    private int trang = 1;

    public QuanLyChanDoanPage() {
        this.setLayout(new BorderLayout());
        this.setBackground(Theme.BG);
    }

    @Override
    public void render() {
        this.removeAll();
        this.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        this.setBackground(Theme.BG);

        this.managementPanel = new ManagementPanel();
        this.managementPanel.setListTitle("app.chanDoan.danhSachQuanLyTitle");
        this.managementPanel.enableMultiSelection(true);

        this.managementPanel.setHeaders(
                I18nReaderUtil.get("app.chanDoan.maVanDe"),
                I18nReaderUtil.get("app.chanDoan.phieuTiepNhan"),
                I18nReaderUtil.get("app.chanDoan.bienSo"),
                I18nReaderUtil.get("app.chanDoan.khachHang"),
                I18nReaderUtil.get("app.chanDoan.tieuDeCot"),
                I18nReaderUtil.get("app.chanDoan.mucDoUuTien"),
                I18nReaderUtil.get("app.chanDoan.khachHangDongY"),
                I18nReaderUtil.get("app.chanDoan.ngayTao")
        );

        this.managementPanel.addActionColumn("app.common.actions",
                new ThaoTac("pencil", "app.common.edit", ThaoTac.KieuThaoTac.THUONG,
                        row -> this.suaChanDoan(row)),
                new ThaoTac("trash-2", "app.common.delete", ThaoTac.KieuThaoTac.NGUY_HIEM,
                        row -> this.xoaChanDoan(row)));

        this.boLocPhieuTiepNhan = this.managementPanel.addTextFilter("app.chanDoan.phieuTiepNhan");
        this.boLocMucDo = this.managementPanel.addRangeFilter("app.chanDoan.mucDoUuTienTuDen");
        this.boLocKhachHangDongY = this.managementPanel.addComboBoxFilter(
                "app.chanDoan.khachHangDongY", TrangThaiVanDeKyThuat.values());
        this.boLocNgayTao = this.managementPanel.addDateRangeFilter("app.chanDoan.ngayTaoTuDen");

        this.managementPanel.setFilterChangeListener(() -> this.taiLai(1));

        this.managementPanel.setAddAction("app.common.add_new",
                () -> this.goTo(LapChanDoanPage.class));
        this.managementPanel.setBatchAction("app.common.batch_delete", "trash-2", ThaoTac.KieuThaoTac.NGUY_HIEM, this::xoaNhieu);
        this.managementPanel.setRowDoubleClickListener(this::suaChanDoan);

        this.managementPanel.getClearFilterButton().addActionListener(
                e -> this.managementPanel.clearFilters());
        this.managementPanel.getPrevButton().addActionListener(
                e -> this.taiLai(this.trang - 1));
        this.managementPanel.getNextButton().addActionListener(
                e -> this.taiLai(this.trang + 1));

        this.add(this.managementPanel, BorderLayout.CENTER);

        this.taiLai(1);

        this.revalidate();
        this.repaint();
    }

    private void taiLai(int page) {
        try {
            TrangThaiVanDeKyThuat khachHangDongY = this.boLocKhachHangDongY.giaTri()
                    instanceof TrangThaiVanDeKyThuat value ? value : null;

            DanhSachChanDoanCriteria tieuChi = new DanhSachChanDoanCriteria(
                    this.boLocPhieuTiepNhan.getText(),
                    NumberParseUtil.toIntegerOrNull(this.boLocMucDo.tuText()),
                    NumberParseUtil.toIntegerOrNull(this.boLocMucDo.denText()),
                    khachHangDongY,
                    this.boLocNgayTao.tuNgay(),
                    this.boLocNgayTao.denNgay(),
                    this.managementPanel.getQuickFilter(),
                    Math.max(page, 1),
                    SO_DONG_MOI_TRANG
            );

            PaginatedResult<ChanDoanView> ketQua = this.vanDeKyThuatService.search(tieuChi);

            List<Object[]> rows = new ArrayList<>();
            for (ChanDoanView view : ketQua.items()) {
                rows.add(new Object[]{
                        view.maVanDe(),
                        view.maPhieuTiepNhan(),
                        view.bienSo(),
                        view.tenKhachHang(),
                        view.tieuDe(),
                        String.valueOf(view.mucDoUuTien()),
                        view.khachHangDongY() == null ? "" : view.khachHangDongY().getI18NBusinessName(),
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

        } catch (IllegalArgumentException ex) {
            ThongBao.loi(this, ex);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Khong the tai danh sach chan doan", ex);
            ThongBao.loi(this, "app.chanDoan.loiTaiDanhSachQuanLy");
        }
    }

    private void suaChanDoan(int row) {
        if (row < 0 || row >= this.danhSachHienTai.size()) {
            return;
        }

        ChanDoanView view = this.danhSachHienTai.get(row);

        ChanDoanSuaFormPanel formSua = new ChanDoanSuaFormPanel();
        formSua.napDuLieu(view);

        BaseFormDialog dialog = new BaseFormDialog(new BaseFormDialogConfig(
                SwingUtilities.getWindowAncestor(this),
                "app.chanDoan.suaTitle",
                formSua,
                () -> this.capNhat(SwingUtilities.getWindowAncestor(formSua), formSua.layDuLieu()),
                CHIEU_CAO_SUA));

        if (dialog.showAndGet()) {
            this.taiLai(this.trang);
        }
    }

    private boolean capNhat(Component parent, ChanDoanUpdateFormData data) {
        try {
            this.vanDeKyThuatService.update(data);
            ThongBao.thongTin(parent, "app.common.update_succeed");
            return true;
        } catch (IllegalArgumentException ex) {
            ThongBao.loi(parent, ex);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Khong the cap nhat chan doan", ex);
            ThongBao.loi(parent, "app.login.errorSystem");
        }

        return false;
    }

    private void xoaChanDoan(int row) {
        try {
            if (row < 0 || row >= this.danhSachHienTai.size()) {
                ThongBao.canhBao(this, "app.chanDoan.chuaChonXoa");
                return;
            }

            String maVanDe = this.danhSachHienTai.get(row).maVanDe();
            if (maVanDe == null || maVanDe.isBlank()) {
                ThongBao.canhBao(this, "app.chanDoan.chuaChonXoa");
                return;
            }

            if (!ThongBao.xacNhan(this, "app.chanDoan.xacNhanXoa", "app.chanDoan.tieuDeXoa")) {
                return;
            }

            this.vanDeKyThuatService.delete(maVanDe);
            ThongBao.thongTin(this, "app.common.delete_succeed");

            this.taiLai(this.trang);

        } catch (IllegalArgumentException ex) {
            ThongBao.loi(this, ex);

        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Khong the xoa chan doan", ex);
            ThongBao.loi(this, "app.login.errorSystem");
        }
    }

    private void xoaNhieu(List<Integer> rows) {
        if (rows.isEmpty()) {
            return;
        }

        if (!ThongBao.xacNhanDinhDang(this, "app.common.batch_delete_confirm", "app.common.confirm", rows.size())) {
            return;
        }

        int thanhCong = 0;
        boolean loiHeThong = false;

        for (int row : rows) {
            if (row < 0 || row >= this.danhSachHienTai.size()) {
                continue;
            }

            String maVanDe = this.danhSachHienTai.get(row).maVanDe();

            if (maVanDe == null || maVanDe.isBlank()) {
                continue;
            }

            try {
                this.vanDeKyThuatService.delete(maVanDe);
                thanhCong++;
            } catch (IllegalArgumentException | IllegalStateException ignored) {
                // Bản ghi đang được tham chiếu: ghi nhận vào báo cáo tổng hợp.
            } catch (Exception ex) {
                LOGGER.log(Level.SEVERE, "Khong the xoa chan doan", ex);
                loiHeThong = true;
                break;
            }
        }

        if (loiHeThong) {
            ThongBao.loi(this, "app.login.errorSystem");
        } else if (thanhCong == rows.size()) {
            ThongBao.thongTin(this, "app.common.delete_succeed");
        } else {
            ThongBao.canhBaoDinhDang(this, "app.common.batch_delete_result", thanhCong, rows.size());
        }

        this.taiLai(this.trang);
    }

    private String dinhDangNgayGio(LocalDateTime thoiGian) {
        return thoiGian == null ? "" : thoiGian.format(DINH_DANG_NGAY_GIO);
    }

    @Override
    public String getTitleKey() {
        return "app.menu.diagnosisManagement";
    }

    @Override
    public MenuGroup getGroup() {
        return MenuGroup.TIEP_NHAN;
    }
}
