package iuh.fit.gui.page;

import iuh.fit.dto.BaseFormDialogConfig;
import iuh.fit.dto.DanhSachPhieuTiepNhanCriteria;
import iuh.fit.dto.PaginatedResult;
import iuh.fit.dto.PhieuTiepNhanView;
import iuh.fit.enumeration.MenuGroup;
import iuh.fit.enumeration.TrangThaiPhieuTiepNhan;
import iuh.fit.gui.component.BaseFormDialog;
import iuh.fit.gui.component.ManagementPanel;
import iuh.fit.gui.component.ThaoTac;
import iuh.fit.gui.component.ThongBao;
import iuh.fit.gui.form.ChanDoanTaoFormPanel;
import iuh.fit.gui.theme.Theme;
import iuh.fit.service.PhieuTiepNhanService;
import iuh.fit.service.VanDeKyThuatService;
import iuh.fit.util.I18nReaderUtil;

import javax.swing.BorderFactory;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Màn hình lập chẩn đoán kỹ thuật cho một phiếu tiếp nhận đã có.
 */
public class LapChanDoanPage extends AbstractPage {

    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = Logger.getLogger(LapChanDoanPage.class.getName());

    private static final int SO_DONG_MOI_TRANG = 20;

    /** Dialog lập chẩn đoán: 6 khối nhập liệu. */
    private static final int CHIEU_CAO_TAO = 710;

    private final PhieuTiepNhanService phieuTiepNhanService = new PhieuTiepNhanService();
    private final VanDeKyThuatService vanDeKyThuatService = new VanDeKyThuatService();

    private ManagementPanel managementPanel;
    private ManagementPanel.BoLocChon boLocTrangThai;
    private List<PhieuTiepNhanView> danhSachHienTai = new ArrayList<>();
    private int trang = 1;

    public LapChanDoanPage() {
        this.setLayout(new BorderLayout());
        this.setBackground(Theme.BG);
    }

    @Override
    public void render() {
        this.removeAll();
        this.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        this.setBackground(Theme.BG);

        this.managementPanel = new ManagementPanel();
        this.managementPanel.setListTitle("app.chanDoan.danhSachTitle");
        this.managementPanel.setHeaders(
                I18nReaderUtil.get("app.chanDoan.ma"),
                I18nReaderUtil.get("app.chanDoan.bienSo"),
                I18nReaderUtil.get("app.chanDoan.khachHang"),
                I18nReaderUtil.get("app.chanDoan.trangThai")
        );

        this.managementPanel.addActionColumn("app.common.actions",
                new ThaoTac("clipboard-list", "app.chanDoan.lapChanDoan", ThaoTac.KieuThaoTac.THUONG,
                        this::moDialogChanDoan));

        this.managementPanel.setRowDoubleClickListener(this::moDialogChanDoan);

        this.boLocTrangThai = this.managementPanel.addComboBoxFilter(
                "app.chanDoan.trangThai", TrangThaiPhieuTiepNhan.values());

        this.managementPanel.setFilterChangeListener(() -> this.taiLaiData(1));
        this.managementPanel.getClearFilterButton().addActionListener(
                e -> this.managementPanel.clearFilters());
        this.managementPanel.getPrevButton().addActionListener(
                e -> this.taiLaiData(this.trang - 1));
        this.managementPanel.getNextButton().addActionListener(
                e -> this.taiLaiData(this.trang + 1));

        this.add(this.managementPanel, BorderLayout.CENTER);

        this.taiLaiData(1);
        this.revalidate();
        this.repaint();
    }

    private void moDialogChanDoan(int row) {
        if (row < 0 || row >= this.danhSachHienTai.size()) {
            return;
        }

        PhieuTiepNhanView phieu = this.danhSachHienTai.get(row);

        ChanDoanTaoFormPanel form = new ChanDoanTaoFormPanel();
        form.napPhieu(phieu);

        BaseFormDialog dialog = new BaseFormDialog(new BaseFormDialogConfig(
                SwingUtilities.getWindowAncestor(this),
                "app.chanDoan.lapChanDoanTitle",
                form,
                () -> this.luuChanDoan(form),
                CHIEU_CAO_TAO));

        if (dialog.showAndGet()) {
            this.taiLaiData(this.trang);
        }
    }

    private boolean luuChanDoan(ChanDoanTaoFormPanel form) {
        if (!form.validateInput()) {
            return false;
        }

        try {
            this.vanDeKyThuatService.lapChanDoan(form.taoVanDe());
            ThongBao.thongTin(this, "app.chanDoan.thanhCong");
            return true;
        } catch (IllegalArgumentException ex) {
            ThongBao.loi(this, ex);
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Khong the lap chan doan", ex);
            ThongBao.loi(this, "app.login.errorSystem");
        }

        return false;
    }

    private void taiLaiData(int page) {
        try {
            TrangThaiPhieuTiepNhan trangThai = this.boLocTrangThai.giaTri()
                    instanceof TrangThaiPhieuTiepNhan value ? value : null;

            DanhSachPhieuTiepNhanCriteria tieuChi = new DanhSachPhieuTiepNhanCriteria(
                    null,
                    null,
                    null,
                    null,
                    null,
                    trangThai,
                    null,
                    null,
                    this.managementPanel.getQuickFilter(),
                    Math.max(page, 1),
                    SO_DONG_MOI_TRANG
            );

            PaginatedResult<PhieuTiepNhanView> ketQua = this.phieuTiepNhanService.search(tieuChi);

            List<Object[]> rows = new ArrayList<>();
            for (PhieuTiepNhanView view : ketQua.items()) {
                rows.add(new Object[]{
                        view.maPhieuTiepNhan(),
                        view.bienSo(),
                        view.tenKhachHang(),
                        view.trangThai() == null ? "" : view.trangThai().getI18NBusinessName()
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
            LOGGER.log(Level.SEVERE, "Khong the tai danh sach phieu tiep nhan", ex);
            ThongBao.loi(this, "app.chanDoan.loiTaiDanhSach");
        }
    }

    @Override
    public String getTitleKey() {
        return "app.menu.diagnoses";
    }

    @Override
    public MenuGroup getGroup() {
        return MenuGroup.TIEP_NHAN;
    }
}
