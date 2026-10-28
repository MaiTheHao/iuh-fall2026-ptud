package iuh.fit.gui.page;

import iuh.fit.core.context.AppContext;
import iuh.fit.dto.DanhSachPhieuTiepNhanCriteria;
import iuh.fit.dto.PaginatedResult;
import iuh.fit.dto.PhieuTiepNhanView;
import iuh.fit.entity.HoaDon;
import iuh.fit.entity.PhieuTiepNhan;
import iuh.fit.enumeration.MenuGroup;
import iuh.fit.enumeration.TrangThaiHoaDon;
import iuh.fit.enumeration.TrangThaiPhieuTiepNhan;
import iuh.fit.gui.component.ManagementFormPanel;
import iuh.fit.gui.component.ManagementPanel;
import iuh.fit.gui.component.SuaDialog;
import iuh.fit.gui.component.ThaoTac;
import iuh.fit.gui.component.ThongBao;
import iuh.fit.gui.form.PhieuTiepNhanFormPanel;
import iuh.fit.gui.theme.Theme;
import iuh.fit.service.HoaDonService;
import iuh.fit.service.PhieuTiepNhanService;
import iuh.fit.util.I18nReaderUtil;

import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Component;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class QuanLyPhieuTiepNhanPage extends AbstractPage {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(QuanLyPhieuTiepNhanPage.class.getName());

    private static final int SO_DONG_MOI_TRANG = 20;

    private static final DateTimeFormatter NGAY_GIO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final PhieuTiepNhanService phieuTiepNhanService = new PhieuTiepNhanService();
    private final HoaDonService hoaDonService = new HoaDonService();

    private ManagementFormPanel form;
    private ManagementPanel managementPanel;

    private JTextField boLocMa;
    private JTextField boLocBienSo;
    private JTextField boLocHoTen;
    private JTextField boLocSdt;
    private JTextField boLocNoiDung;
    private ManagementPanel.BoLocNgay boLocThoiGianVao;
    private ManagementPanel.BoLocChon boLocTrangThai;
    private PhieuTiepNhanFormPanel formPanel;
    private List<PhieuTiepNhanView> danhSachHienTai = new ArrayList<>();
    private int trang = 1;

    public QuanLyPhieuTiepNhanPage() {
        this.setLayout(new BorderLayout());
        this.setBackground(Theme.BG);
    }

    @Override
    public void render() {
        this.removeAll();
        this.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        this.setBackground(Theme.BG);

        this.managementPanel = new ManagementPanel();
        this.managementPanel.setListTitle("app.phieuTiepNhan.danhSachTitle");

        this.managementPanel.setHeaders(
                I18nReaderUtil.get("app.phieuTiepNhan.ma"),
                I18nReaderUtil.get("app.xe.bienSo"),
                I18nReaderUtil.get("app.khachHang.hoTen"),
                I18nReaderUtil.get("app.khachHang.sdt"),
                I18nReaderUtil.get("app.phieuTiepNhan.thoiGianVao"),
                I18nReaderUtil.get("app.phieuTiepNhan.noiDung"),
                I18nReaderUtil.get("app.phieuTiepNhan.trangThai")
        );

        this.managementPanel.addActionColumn("app.common.actions",
                new ThaoTac("pencil", "app.common.edit", ThaoTac.KieuThaoTac.THUONG,
                        row -> this.suaPhieuTiepNhan(row)),
                new ThaoTac("ban", "app.phieuTiepNhan.huyPhieu", ThaoTac.KieuThaoTac.NGUY_HIEM,
                        row -> this.huyPhieu(row)),
                new ThaoTac("clipboard-list", "app.chanDoan.xemChanDoanTitle", ThaoTac.KieuThaoTac.THUONG,
                        row -> this.xemChanDoan(row)));

        this.boLocMa = this.managementPanel.addTextFilter("app.phieuTiepNhan.ma");
        this.boLocBienSo = this.managementPanel.addTextFilter("app.xe.bienSo");
        this.boLocHoTen = this.managementPanel.addTextFilter("app.khachHang.hoTen");
        this.boLocSdt = this.managementPanel.addTextFilter("app.khachHang.sdt");
        this.boLocThoiGianVao = this.managementPanel.addDateRangeFilter("app.phieuTiepNhan.thoiGianVao");
        this.boLocNoiDung = this.managementPanel.addTextFilter("app.phieuTiepNhan.noiDung");
        this.boLocTrangThai = this.managementPanel.addComboBoxFilter("app.phieuTiepNhan.trangThai",
                TrangThaiPhieuTiepNhan.values());

        this.managementPanel.setFilterChangeListener(() -> this.taiLaiData(1));

        this.form = new ManagementFormPanel("app.phieuTiepNhan.thongTinTitle");

        this.formPanel = new PhieuTiepNhanFormPanel();
        this.form.addBlock(this.formPanel);

        this.form.getNutLuu().setText(I18nReaderUtil.get("app.phieuTiepNhan.luu"));
        this.form.getNutLuu().addActionListener(e -> this.luuPhieuTiepNhan());
        this.form.getNutXoa().setText(I18nReaderUtil.get("app.phieuTiepNhan.huyPhieu"));
        this.form.getNutXoa().addActionListener(
                e -> this.huyPhieu(this.managementPanel.getSelectedRow()));
        this.form.getNutXoaTrang().setText(I18nReaderUtil.get("app.phieuTiepNhan.xoaTrang"));
        this.form.getNutXoaTrang().addActionListener(e -> this.lamMoiForm());
        this.form.getNutThem().addActionListener(e -> this.goTo(TiepNhanKhachHangPage.class));

        this.managementPanel.getClearFilterButton().addActionListener(e -> this.managementPanel.clearFilters());
        this.managementPanel.getPrevButton().addActionListener(e -> this.taiLaiData(this.trang - 1));
        this.managementPanel.getNextButton().addActionListener(e -> this.taiLaiData(this.trang + 1));

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
        this.taiLaiData(1);
        this.revalidate();
        this.repaint();
    }

    private void taiLaiData(int page) {
        try {
            TrangThaiPhieuTiepNhan trangThai = this.boLocTrangThai.giaTri()
                    instanceof TrangThaiPhieuTiepNhan value ? value : null;

            DanhSachPhieuTiepNhanCriteria tieuChi = new DanhSachPhieuTiepNhanCriteria(
                    this.boLocMa.getText(),
                    this.boLocBienSo.getText(),
                    this.boLocHoTen.getText(),
                    this.boLocSdt.getText(),
                    this.boLocNoiDung.getText(),
                    trangThai,
                    this.boLocThoiGianVao.tuNgay(),
                    this.boLocThoiGianVao.denNgay(),
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
                        view.sdt(),
                        view.ngayTao() != null ? view.ngayTao().format(NGAY_GIO) : "",
                        view.moTa(),
                        view.trangThai() == null ? "" : view.trangThai().getI18NBusinessName()
                });
            }

            this.danhSachHienTai = ketQua.items();

            this.managementPanel.setRows(rows);
            this.managementPanel.setCount(ketQua.fromIndex(), ketQua.toIndex(), ketQua.totalItems());
            this.managementPanel.setPage(ketQua.page(), Math.max(ketQua.totalPages(), 1));
            this.trang = ketQua.page();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Khong the tai danh sach phieu tiep nhan", e);
            ThongBao.loi(this, "app.phieuTiepNhan.loiTaiDanhSach");
        }
    }

    private void chonDong() {
        int row = this.managementPanel.getSelectedRow();
        if (row < 0 || row >= this.danhSachHienTai.size()) {
            return;
        }

        PhieuTiepNhanView view = this.danhSachHienTai.get(row);
        this.formPanel.napDuLieu(view);
        this.form.getNutXoa().setEnabled(view.trangThai() != TrangThaiPhieuTiepNhan.DA_HUY);
    }

    private void lamMoiForm() {
        this.formPanel.lamMoi();
        this.managementPanel.getTable().clearSelection();
        this.form.getNutXoa().setEnabled(true);
    }

    private void luuPhieuTiepNhan() {
        PhieuTiepNhan phieu = this.formPanel.layDuLieu();

        if (phieu.getMaPhieuTiepNhan().isBlank()) {
            ThongBao.canhBao(this, "app.phieuTiepNhan.loiMa");
            return;
        }

        if (!this.capNhat(this, phieu)) {
            return;
        }

        this.taiLaiData(this.trang);
    }

    private void suaPhieuTiepNhan(int row) {
        if (row < 0 || row >= this.danhSachHienTai.size()) {
            return;
        }

        PhieuTiepNhanView view = this.danhSachHienTai.get(row);

        PhieuTiepNhanFormPanel formSua = new PhieuTiepNhanFormPanel();
        formSua.napDuLieu(view);

        SuaDialog dialog = new SuaDialog(
                SwingUtilities.getWindowAncestor(this), "app.common.editTitle", formSua);
        dialog.datXuLyLuu(() -> this.capNhat(dialog, formSua.layDuLieu()));

        if (dialog.hienThiVaChoKetQua()) {
            this.taiLaiData(this.trang);
            this.lamMoiForm();
        }
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

        this.chonDong();

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
            this.lamMoiForm();
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
