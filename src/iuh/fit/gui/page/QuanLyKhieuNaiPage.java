package iuh.fit.gui.page;

import iuh.fit.dto.DanhSachKhieuNaiCriteria;
import iuh.fit.dto.KhieuNaiUpdateFormData;
import iuh.fit.dto.KhieuNaiView;
import iuh.fit.dto.PaginatedResult;
import iuh.fit.enumeration.MenuGroup;
import iuh.fit.enumeration.TrangThaiKhieuNai;
import iuh.fit.gui.component.ManagementFormPanel;
import iuh.fit.gui.component.ManagementPanel;
import iuh.fit.gui.component.ThaoTac;
import iuh.fit.gui.component.ThongBao;
import iuh.fit.gui.theme.Theme;
import iuh.fit.service.KhieuNaiService;
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

    private final JTextField maKhieuNaiField = new JTextField();
    private final JTextField khachHangField = new JTextField();
    private final JTextField phieuTiepNhanField = new JTextField();
    private final JTextArea noiDungArea = new JTextArea(4, 20);
    private final JComboBox<TrangThaiKhieuNai> trangThaiCombo = new JComboBox<>(TrangThaiKhieuNai.values());

    private ManagementFormPanel form;
    private ManagementPanel managementPanel;
    private List<KhieuNaiView> danhSachHienTai = List.of();
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

        this.managementPanel.addTextFilter("app.khieuNai.ma", 0);
        this.managementPanel.addTextFilter("app.khieuNai.khachHang", 1);
        this.managementPanel.addTextFilter("app.khieuNai.phieuTiepNhan", 2);
        this.managementPanel.addTextFilter("app.khieuNai.noiDung", 3);
        this.managementPanel.addComboBoxFilter(
                "app.khieuNai.trangThai", 4, TrangThaiKhieuNai.values());
        this.managementPanel.addDateRangeFilter("app.khieuNai.ngayTaoTuDen", 5);

        this.managementPanel.setFilterChangeListener(() -> this.taiLai(1));

        this.form = new ManagementFormPanel("app.khieuNai.thongTinTitle");

        Theme.applyReadOnly(this.maKhieuNaiField);
        Theme.applyReadOnly(this.khachHangField);
        Theme.applyReadOnly(this.phieuTiepNhanField);

        this.form.addField("app.khieuNai.ma", this.maKhieuNaiField);
        this.form.addField("app.khieuNai.khachHang", this.khachHangField);
        this.form.addField("app.khieuNai.phieuTiepNhan", this.phieuTiepNhanField);

        this.noiDungArea.setLineWrap(true);
        this.noiDungArea.setWrapStyleWord(true);
        JScrollPane scrollNoiDung = new JScrollPane(this.noiDungArea);
        scrollNoiDung.setPreferredSize(new Dimension(200, 100));
        this.form.addField("app.khieuNai.noiDung", scrollNoiDung);

        this.form.addField("app.khieuNai.trangThai", this.trangThaiCombo);
        this.trangThaiCombo.setRenderer(ManagementPanel.taoBoHienThiI18n());

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
        split.setDividerLocation(380);
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
            TrangThaiKhieuNai trangThai = this.managementPanel.getComboFilterValue(4)
                    instanceof TrangThaiKhieuNai value ? value : null;

            DanhSachKhieuNaiCriteria tieuChi = new DanhSachKhieuNaiCriteria(
                    this.managementPanel.getTextFilter(0),
                    this.managementPanel.getTextFilter(1),
                    this.managementPanel.getTextFilter(2),
                    this.managementPanel.getTextFilter(3),
                    trangThai,
                    this.managementPanel.getDateFilterFrom(5),
                    this.managementPanel.getDateFilterTo(5),
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

        KhieuNaiView view = this.danhSachHienTai.get(row);

        this.maKhieuNaiField.setText(view.maKhieuNai() == null ? "" : view.maKhieuNai());
        this.khachHangField.setText(view.tenKhachHang() == null ? "" : view.tenKhachHang());
        this.phieuTiepNhanField.setText(view.maPhieuTiepNhan() == null ? "" : view.maPhieuTiepNhan());
        this.noiDungArea.setText(view.noiDung() == null ? "" : view.noiDung());
        this.trangThaiCombo.setSelectedItem(view.trangThai());
    }

    private void lamMoiForm() {
        this.maKhieuNaiField.setText("");
        this.khachHangField.setText("");
        this.phieuTiepNhanField.setText("");
        this.noiDungArea.setText("");
        this.trangThaiCombo.setSelectedIndex(0);
        this.managementPanel.getTable().clearSelection();
    }

    private void luuKhieuNai() {
        try {
            int row = this.managementPanel.getSelectedRow();
            String maKhieuNai = this.maKhieuNaiField.getText().trim();

            if (row < 0 || maKhieuNai.isBlank()) {
                ThongBao.canhBao(this, "app.khieuNai.loiMa");
                return;
            }

            TrangThaiKhieuNai trangThai = (TrangThaiKhieuNai) this.trangThaiCombo.getSelectedItem();
            KhieuNaiUpdateFormData data = new KhieuNaiUpdateFormData(
                    maKhieuNai,
                    this.noiDungArea.getText(),
                    trangThai
            );

            this.khieuNaiService.update(data);
            ThongBao.thongTin(this, "app.common.update_succeed");

            this.taiLai(this.trang);
            this.lamMoiForm();

        } catch (IllegalArgumentException ex) {
            ThongBao.loi(this, ex);

        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Khong the cap nhat khieu nai", ex);
            ThongBao.loi(this, "app.login.errorSystem");
        }
    }

    private void suaKhieuNai(int row) {
        this.chonDong();
        this.noiDungArea.requestFocusInWindow();
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
