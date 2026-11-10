package iuh.fit.gui.page;

import iuh.fit.core.context.AppContext;
import iuh.fit.dto.BaseFormDialogConfig;
import iuh.fit.dto.DanhSachPhieuTiepNhanCriteria;
import iuh.fit.dto.PaginatedResult;
import iuh.fit.dto.PhieuTiepNhanView;
import iuh.fit.entity.HoaDon;
import iuh.fit.entity.PhieuTiepNhan;
import iuh.fit.enumeration.MenuGroup;
import iuh.fit.enumeration.TrangThaiHoaDon;
import iuh.fit.enumeration.TrangThaiPhieuTiepNhan;
import iuh.fit.gui.Theme;
import iuh.fit.gui.UiConstants;
import iuh.fit.gui.component.BaseFormDialog;
import iuh.fit.gui.component.ManagementPanel;
import iuh.fit.gui.component.ThaoTac;
import iuh.fit.gui.component.ThongBao;
import iuh.fit.gui.form.PhieuTiepNhanChiTietFormPanel;
import iuh.fit.gui.form.PhieuTiepNhanFormPanel;
import iuh.fit.service.HoaDonService;
import iuh.fit.service.PhieuTiepNhanService;
import iuh.fit.util.DateFormats;
import iuh.fit.util.I18nReaderUtil;

import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Component;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class QuanLyPhieuTiepNhanPage extends AbstractPage {

    
    private static final Logger LOGGER = Logger.getLogger(QuanLyPhieuTiepNhanPage.class.getName());


    private static final int CHIEU_CAO_SUA = 650;

    private static final int CHIEU_CAO_CHI_TIET = 560;

    private final PhieuTiepNhanService phieuTiepNhanService = new PhieuTiepNhanService();
    private final HoaDonService hoaDonService = new HoaDonService();

    private ManagementPanel pnlDanhSach;

    private JTextField txtBoLocMa;
    private JTextField txtBoLocBienSo;
    private JTextField txtBoLocHoTen;
    private JTextField txtBoLocSdt;
    private JTextField txtBoLocNoiDung;
    private ManagementPanel.BoLocNgay boLocThoiGianVao;
    private ManagementPanel.BoLocChon boLocTrangThai;
    private List<PhieuTiepNhanView> danhSachHienTai = new ArrayList<>();
    private int trang = 1;

    public QuanLyPhieuTiepNhanPage() {
        this.setLayout(new BorderLayout());
        this.setBackground(Theme.BG);
    }

    @Override
    public void render() {
        this.removeAll();
        this.setBorder(Theme.leDeu(8));
        this.setBackground(Theme.BG);

        this.pnlDanhSach = new ManagementPanel();
        this.pnlDanhSach.setListTitle("app.phieuTiepNhan.danhSachTitle");

        this.pnlDanhSach.setHeaders(
                I18nReaderUtil.get("app.phieuTiepNhan.ma"),
                I18nReaderUtil.get("app.xe.bienSo"),
                I18nReaderUtil.get("app.khachHang.hoTen"),
                I18nReaderUtil.get("app.khachHang.sdt"),
                I18nReaderUtil.get("app.phieuTiepNhan.thoiGianVao"),
                I18nReaderUtil.get("app.phieuTiepNhan.noiDung"),
                I18nReaderUtil.get("app.phieuTiepNhan.trangThai")
        );

        this.pnlDanhSach.addActionColumn("app.common.actions",
                new ThaoTac("pencil", "app.phieuTiepNhan.suaNoiDung", ThaoTac.KieuThaoTac.THUONG,
                        row -> this.suaPhieuTiepNhan(row),
                        row -> this.danhSachHienTai.size() > row && row >= 0
                                && this.coTheSua(this.danhSachHienTai.get(row).trangThai())),
                new ThaoTac("eye", "app.common.view", ThaoTac.KieuThaoTac.THUONG,
                        row -> this.xemChiTiet(row)),
                new ThaoTac("ban", "app.phieuTiepNhan.huyPhieu", ThaoTac.KieuThaoTac.NGUY_HIEM,
                        row -> this.huyPhieu(row)),
                new ThaoTac("clipboard-list", "app.chanDoan.xemChanDoanTitle", ThaoTac.KieuThaoTac.THUONG,
                        row -> this.xemChanDoan(row)));

        this.txtBoLocMa = this.pnlDanhSach.addTextFilter("app.phieuTiepNhan.ma");
        this.txtBoLocBienSo = this.pnlDanhSach.addTextFilter("app.xe.bienSo");
        this.txtBoLocHoTen = this.pnlDanhSach.addTextFilter("app.khachHang.hoTen");
        this.txtBoLocSdt = this.pnlDanhSach.addTextFilter("app.khachHang.sdt");
        this.boLocThoiGianVao = this.pnlDanhSach.addDateRangeFilter("app.phieuTiepNhan.thoiGianVao");
        this.txtBoLocNoiDung = this.pnlDanhSach.addTextFilter("app.phieuTiepNhan.noiDung");
        this.boLocTrangThai = this.pnlDanhSach.addComboBoxFilter("app.phieuTiepNhan.trangThai",
                TrangThaiPhieuTiepNhan.values());

        this.pnlDanhSach.setFilterChangeListener(() -> this.taiLaiData(1));

        this.pnlDanhSach.setAddAction("app.common.add_new",
                () -> this.goTo(TiepNhanKhachHangPage.class));
        this.pnlDanhSach.setRowDoubleClickListener(this::xemChiTiet);

        this.pnlDanhSach.getClearFilterButton().addActionListener(e -> this.pnlDanhSach.clearFilters());
        this.pnlDanhSach.getPrevButton().addActionListener(e -> this.taiLaiData(this.trang - 1));
        this.pnlDanhSach.getNextButton().addActionListener(e -> this.taiLaiData(this.trang + 1));

        this.add(this.pnlDanhSach, BorderLayout.CENTER);

        this.taiLaiData(1);
        this.revalidate();
        this.repaint();
    }

    private void taiLaiData(int page) {
        try {
            TrangThaiPhieuTiepNhan trangThai = this.boLocTrangThai.giaTri()
                    instanceof TrangThaiPhieuTiepNhan value ? value : null;

            DanhSachPhieuTiepNhanCriteria tieuChi = new DanhSachPhieuTiepNhanCriteria(
                    this.txtBoLocMa.getText(),
                    this.txtBoLocBienSo.getText(),
                    this.txtBoLocHoTen.getText(),
                    this.txtBoLocSdt.getText(),
                    this.txtBoLocNoiDung.getText(),
                    trangThai,
                    this.boLocThoiGianVao.tuNgay(),
                    this.boLocThoiGianVao.denNgay(),
                    this.pnlDanhSach.getQuickFilter(),
                    Math.max(page, 1),
                    UiConstants.SO_DONG_MOI_TRANG
            );

            PaginatedResult<PhieuTiepNhanView> ketQua = this.phieuTiepNhanService.search(tieuChi);

            List<Object[]> rows = new ArrayList<>();
            for (PhieuTiepNhanView view : ketQua.items()) {
                rows.add(new Object[]{
                        view.maPhieuTiepNhan(),
                        view.bienSo(),
                        view.tenKhachHang(),
                        view.sdt(),
                        view.ngayTao() != null ? view.ngayTao().format(DateFormats.NGAY_GIO) : "",
                        view.moTa(),
                        view.trangThai() == null ? "" : view.trangThai().getI18NBusinessName()
                });
            }

            this.danhSachHienTai = ketQua.items();

            this.pnlDanhSach.setRows(rows);
            this.pnlDanhSach.setCount(ketQua.fromIndex(), ketQua.toIndex(), ketQua.totalItems());
            this.pnlDanhSach.setPage(ketQua.page(), Math.max(ketQua.totalPages(), 1));
            this.trang = ketQua.page();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Khong the tai danh sach phieu tiep nhan", e);
            ThongBao.loi(this, "app.phieuTiepNhan.loiTaiDanhSach");
        }
    }

    private void suaPhieuTiepNhan(int row) {
        if (row < 0 || row >= this.danhSachHienTai.size()) {
            return;
        }

        PhieuTiepNhanView view = this.danhSachHienTai.get(row);

        // Chỉ cho chọn trạng thái hiện tại hoặc trạng thái kế tiếp hợp lệ.
        List<TrangThaiPhieuTiepNhan> trangThaiChoPhep = new ArrayList<>();
        trangThaiChoPhep.add(view.trangThai());
        trangThaiChoPhep.addAll(this.phieuTiepNhanService.trangThaiKeTiep(view.trangThai()));

        PhieuTiepNhanFormPanel formSua = new PhieuTiepNhanFormPanel(trangThaiChoPhep);
        formSua.napDuLieu(view);

        BaseFormDialog dialog = new BaseFormDialog(new BaseFormDialogConfig(
                SwingUtilities.getWindowAncestor(this),
                "app.phieuTiepNhan.suaNoiDung",
                formSua,
                () -> this.capNhat(SwingUtilities.getWindowAncestor(formSua), formSua.layDuLieu()),
                CHIEU_CAO_SUA));

        if (dialog.showAndGet()) {
            this.taiLaiData(this.trang);
        }
    }

    private boolean coTheSua(TrangThaiPhieuTiepNhan trangThai) {
        return trangThai == TrangThaiPhieuTiepNhan.TIEP_NHAN
                || trangThai == TrangThaiPhieuTiepNhan.DANG_XU_LY;
    }

    private void xemChiTiet(int row) {
        if (row < 0 || row >= this.danhSachHienTai.size()) {
            return;
        }

        PhieuTiepNhanChiTietFormPanel form = new PhieuTiepNhanChiTietFormPanel();
        form.napDuLieu(this.danhSachHienTai.get(row));

        BaseFormDialog dialog = new BaseFormDialog(new BaseFormDialogConfig(
                SwingUtilities.getWindowAncestor(this),
                "app.phieuTiepNhan.chiTietTitle",
                form,
                null,
                CHIEU_CAO_CHI_TIET,
                true));
        dialog.showAndGet();
    }

    private void xemChanDoan(int row) {
        if (row < 0 || row >= this.danhSachHienTai.size()) {
            return;
        }

        AppContext.getInstance().setSelectedPhieuTiepNhan(this.danhSachHienTai.get(row));
        this.goTo(XemChanDoanPage.class);
    }

    private boolean capNhat(Component parent, PhieuTiepNhan phieu) {
        try {
            this.phieuTiepNhanService.update(phieu);
            ThongBao.thongTin(parent, "app.common.update_succeed");
            return true;
        } catch (IllegalArgumentException | IllegalStateException ex) {
            ThongBao.loi(parent, ex);
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Khong the cap nhat phieu tiep nhan", ex);
            ThongBao.loi(parent, "app.login.errorSystem");
        }

        return false;
    }

    private void huyPhieu(int row) {
        if (row < 0 || row >= this.danhSachHienTai.size()) {
            ThongBao.canhBao(this, "app.phieuTiepNhan.loiMa");
            return;
        }

        PhieuTiepNhanView view = this.danhSachHienTai.get(row);

        if (view.trangThai() == TrangThaiPhieuTiepNhan.DA_HUY) {
            ThongBao.canhBao(this, "app.phieuTiepNhan.loiTrangThaiCuoi");
            return;
        }

        if (!ThongBao.xacNhan(this, "app.phieuTiepNhan.xacNhanHuy", "app.phieuTiepNhan.tieuDeHuy")) {
            return;
        }

        try {
            Optional<HoaDon> hoaDon = this.hoaDonService.timTheoPhieuTiepNhan(view.maPhieuTiepNhan());

            String lyDo = null;
            if (hoaDon.isPresent() && hoaDon.get().getTrangThaiHoaDon() == TrangThaiHoaDon.CHO_THANH_TOAN) {
                lyDo = JOptionPane.showInputDialog(this, I18nReaderUtil.get("app.phieuTiepNhan.lyDoHuy"));
                if (lyDo == null || lyDo.isBlank()) {
                    return;
                }
            }

            this.phieuTiepNhanService.huyPhieu(view.maPhieuTiepNhan(), lyDo);
            ThongBao.thongTin(this, "app.common.update_succeed");
            this.taiLaiData(this.trang);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            ThongBao.loi(this, ex);
        } catch (SQLException ex) {
            ThongBao.loi(this, "app.login.errorSystem");
        }
    }

    @Override
    public String getTitleKey() {
        return "app.menu.intakeSlips";
    }

    @Override
    public MenuGroup getGroup() {
        return MenuGroup.TIEP_NHAN;
    }
}
