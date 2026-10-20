package iuh.fit.gui.page;

import iuh.fit.dto.DanhSachPhieuTiepNhanCriteria;
import iuh.fit.dto.PaginatedResult;
import iuh.fit.dto.PhieuTiepNhanView;
import iuh.fit.entity.PhieuTiepNhan;
import iuh.fit.enumeration.MenuGroup;
import iuh.fit.enumeration.TrangThaiPhieuTiepNhan;
import iuh.fit.gui.component.ManagementFormPanel;
import iuh.fit.gui.component.ManagementPanel;
import iuh.fit.gui.component.ThaoTac;
import iuh.fit.gui.component.ThongBao;
import iuh.fit.gui.theme.Theme;
import iuh.fit.service.PhieuTiepNhanService;
import iuh.fit.util.I18nReaderUtil;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class QuanLyPhieuTiepNhanPage extends AbstractPage {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(QuanLyPhieuTiepNhanPage.class.getName());

    private static final int SO_DONG_MOI_TRANG = 20;

    private static final DateTimeFormatter NGAY_GIO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final PhieuTiepNhanService phieuTiepNhanService = new PhieuTiepNhanService();

    // Form fields (Left Panel)
    private final JTextField maPhieuField = new JTextField();
    private final JTextField bienSoField = new JTextField();
    private final JTextField khachHangField = new JTextField();
    private final JTextField sdtField = new JTextField();
    private final JTextArea moTaArea = new JTextArea(4, 20);
    private final JComboBox<TrangThaiPhieuTiepNhan> trangThaiCombo = new JComboBox<>(TrangThaiPhieuTiepNhan.values());

    private ManagementFormPanel form;
    private ManagementPanel managementPanel;
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
                        row -> this.huyPhieu(row)));

        this.managementPanel.addTextFilter("app.phieuTiepNhan.ma", 0);
        this.managementPanel.addTextFilter("app.xe.bienSo", 1);
        this.managementPanel.addTextFilter("app.khachHang.hoTen", 2);
        this.managementPanel.addTextFilter("app.khachHang.sdt", 3);
        this.managementPanel.addDateRangeFilter("app.phieuTiepNhan.thoiGianVao", 4);
        this.managementPanel.addTextFilter("app.phieuTiepNhan.noiDung", 5);
        this.managementPanel.addComboBoxFilter("app.phieuTiepNhan.trangThai", 6, TrangThaiPhieuTiepNhan.values());

        this.managementPanel.setFilterChangeListener(() -> this.taiLaiData(1));

        this.form = new ManagementFormPanel("app.phieuTiepNhan.thongTinTitle");

        Theme.applyReadOnly(this.maPhieuField);
        Theme.applyReadOnly(this.bienSoField);
        Theme.applyReadOnly(this.khachHangField);
        Theme.applyReadOnly(this.sdtField);

        this.form.addField("app.phieuTiepNhan.maPhieu", this.maPhieuField);
        this.form.addField("app.phieuTiepNhan.bienSo", this.bienSoField);
        this.form.addField("app.phieuTiepNhan.khachHang", this.khachHangField);
        this.form.addField("app.phieuTiepNhan.dienThoai", this.sdtField);

        this.moTaArea.setLineWrap(true);
        this.moTaArea.setWrapStyleWord(true);
        JScrollPane scrollMoTa = new JScrollPane(this.moTaArea);
        scrollMoTa.setPreferredSize(new Dimension(200, 100));
        this.form.addField("app.phieuTiepNhan.noiDungYeuCau", scrollMoTa);

        this.form.addField("app.phieuTiepNhan.trangThaiTienDo", this.trangThaiCombo);
        this.trangThaiCombo.setRenderer(ManagementPanel.taoBoHienThiI18n());

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
        split.setDividerLocation(380);
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
            TrangThaiPhieuTiepNhan trangThai = this.managementPanel.getComboFilterValue(6)
                    instanceof TrangThaiPhieuTiepNhan value ? value : null;

            DanhSachPhieuTiepNhanCriteria tieuChi = new DanhSachPhieuTiepNhanCriteria(
                    this.managementPanel.getTextFilter(0),
                    this.managementPanel.getTextFilter(1),
                    this.managementPanel.getTextFilter(2),
                    this.managementPanel.getTextFilter(3),
                    this.managementPanel.getTextFilter(5),
                    trangThai,
                    this.managementPanel.getDateFilterFrom(4),
                    this.managementPanel.getDateFilterTo(4),
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

        this.maPhieuField.setText(view.maPhieuTiepNhan() == null ? "" : view.maPhieuTiepNhan());
        this.bienSoField.setText(view.bienSo() == null ? "" : view.bienSo());
        this.khachHangField.setText(view.tenKhachHang() == null ? "" : view.tenKhachHang());
        this.sdtField.setText(view.sdt() == null ? "" : view.sdt());
        this.moTaArea.setText(view.moTa() == null ? "" : view.moTa());
        this.trangThaiCombo.setSelectedItem(view.trangThai());

        this.form.getNutXoa().setEnabled(view.trangThai() != TrangThaiPhieuTiepNhan.DA_HUY);
    }

    private void lamMoiForm() {
        this.maPhieuField.setText("");
        this.bienSoField.setText("");
        this.khachHangField.setText("");
        this.sdtField.setText("");
        this.moTaArea.setText("");
        this.trangThaiCombo.setSelectedIndex(0);
        this.managementPanel.getTable().clearSelection();
        this.form.getNutXoa().setEnabled(true);
    }

    private void luuPhieuTiepNhan() {
        String maPhieu = this.maPhieuField.getText().trim();
        if (maPhieu.isBlank()) {
            ThongBao.canhBao(this, "app.phieuTiepNhan.loiMa");
            return;
        }

        try {
            PhieuTiepNhan phieu = new PhieuTiepNhan();
            phieu.setMaPhieuTiepNhan(maPhieu);
            phieu.setMoTa(this.moTaArea.getText().trim());
            phieu.setTrangThai((TrangThaiPhieuTiepNhan) this.trangThaiCombo.getSelectedItem());

            this.phieuTiepNhanService.update(phieu);

            ThongBao.thongTin(this, "app.common.update_succeed");
            this.taiLaiData(this.trang);
        } catch (IllegalArgumentException ex) {
            ThongBao.loi(this, ex);
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Khong the cap nhat phieu tiep nhan", ex);
            ThongBao.loi(this, "app.login.errorSystem");
        }
    }

    private void suaPhieuTiepNhan(int row) {
        this.chonDong();
        this.moTaArea.requestFocusInWindow();
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

        this.trangThaiCombo.setSelectedItem(TrangThaiPhieuTiepNhan.DA_HUY);
        this.luuPhieuTiepNhan();
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
