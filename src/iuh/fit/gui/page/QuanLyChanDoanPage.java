package iuh.fit.gui.page;

import iuh.fit.core.context.AppContext;
import iuh.fit.dto.DanhSachPhieuTiepNhanCriteria;
import iuh.fit.dto.PaginatedResult;
import iuh.fit.dto.PhieuTiepNhanView;
import iuh.fit.enumeration.MenuGroup;
import iuh.fit.enumeration.TrangThaiPhieuTiepNhan;
import iuh.fit.gui.component.ManagementPanel;
import iuh.fit.gui.component.ThaoTac;
import iuh.fit.gui.component.ThongBao;
import iuh.fit.gui.theme.Theme;
import iuh.fit.service.PhieuTiepNhanService;
import iuh.fit.util.I18nReaderUtil;

import javax.swing.BorderFactory;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class QuanLyChanDoanPage extends AbstractPage {

    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = Logger.getLogger(QuanLyChanDoanPage.class.getName());

    private static final int SO_DONG_MOI_TRANG = 20;

    private static final DateTimeFormatter DINH_DANG_NGAY_GIO =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final PhieuTiepNhanService phieuTiepNhanService = new PhieuTiepNhanService();

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

    public QuanLyChanDoanPage() {
        this.setLayout(new BorderLayout());
        this.setBackground(Theme.BG);
    }

    @Override
    public void render() {
        this.removeAll();
        this.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        this.setBackground(Theme.BG);

        this.pnlDanhSach = new ManagementPanel();
        this.pnlDanhSach.setListTitle("app.chanDoan.danhSachQuanLyTitle");

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
                new ThaoTac("clipboard-list", "app.chanDoan.moChanDoan", ThaoTac.KieuThaoTac.THUONG,
                        row -> this.moBangChanDoan(row)));

        this.txtBoLocMa = this.pnlDanhSach.addTextFilter("app.phieuTiepNhan.ma");
        this.txtBoLocBienSo = this.pnlDanhSach.addTextFilter("app.xe.bienSo");
        this.txtBoLocHoTen = this.pnlDanhSach.addTextFilter("app.khachHang.hoTen");
        this.txtBoLocSdt = this.pnlDanhSach.addTextFilter("app.khachHang.sdt");
        this.txtBoLocNoiDung = this.pnlDanhSach.addTextFilter("app.phieuTiepNhan.noiDung");
        this.boLocThoiGianVao = this.pnlDanhSach.addDateRangeFilter("app.phieuTiepNhan.thoiGianVao");
        this.boLocTrangThai = this.pnlDanhSach.addComboBoxFilter("app.phieuTiepNhan.trangThai",
                TrangThaiPhieuTiepNhan.values());

        this.pnlDanhSach.setFilterChangeListener(() -> this.taiLai(1));

        this.pnlDanhSach.setRowDoubleClickListener(this::moBangChanDoan);

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
                        view.ngayTao() != null ? view.ngayTao().format(DINH_DANG_NGAY_GIO) : "",
                        view.moTa(),
                        view.trangThai() == null ? "" : view.trangThai().getI18NBusinessName()
                });
            }

            this.danhSachHienTai = ketQua.items();

            this.pnlDanhSach.setRows(rows);
            this.pnlDanhSach.setCount(ketQua.fromIndex(), ketQua.toIndex(), ketQua.totalItems());
            this.pnlDanhSach.setPage(ketQua.page(), Math.max(ketQua.totalPages(), 1));

            this.trang = ketQua.page();

        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Khong the tai danh sach phieu tiep nhan", ex);
            ThongBao.loi(this, "app.phieuTiepNhan.loiTaiDanhSach");
        }
    }

    private void moBangChanDoan(int row) {
        if (row < 0 || row >= this.danhSachHienTai.size()) {
            return;
        }

        PhieuTiepNhanView view = this.danhSachHienTai.get(row);

        AppContext.getInstance().setSelectedPhieuTiepNhan(view);
        this.goTo(BangChanDoanPage.class);
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
