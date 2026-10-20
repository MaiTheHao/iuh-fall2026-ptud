package iuh.fit.gui.page;

import iuh.fit.dto.DanhSachKhachHangCriteria;
import iuh.fit.dto.KhachHangFormData;
import iuh.fit.dto.PaginatedResult;
import iuh.fit.entity.KhachHang;
import iuh.fit.enumeration.MenuGroup;
import iuh.fit.gui.component.ManagementFormPanel;
import iuh.fit.gui.component.ManagementPanel;
import iuh.fit.gui.component.ThaoTac;
import iuh.fit.gui.component.ThongBao;
import iuh.fit.gui.form.KhachHangFormPanel;
import iuh.fit.gui.theme.Theme;
import iuh.fit.service.KhachHangService;
import iuh.fit.util.I18nReaderUtil;

import javax.swing.BorderFactory;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class KhachHangPage extends AbstractPage {

    private static final int SO_DONG_MOI_TRANG = 20;

    private final KhachHangService khachHangService = new KhachHangService();
    private KhachHangFormPanel formPanel;

    private JTextField boLocMa;
    private JTextField boLocSdt;
    private JTextField boLocHoTen;
    private ManagementPanel.BoLocNgay boLocNgayTao;

    private int trang = 1;

    public KhachHangPage() {
        this.setLayout(new BorderLayout());
    }

    @Override
    public void render() {
        this.removeAll();
        this.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        this.setBackground(Theme.BG);

        ManagementPanel panel = new ManagementPanel();
        panel.setListTitle("app.khachHang.danhSachTitle");
        panel.setHeaders(
                I18nReaderUtil.get("app.khachHang.ma"),
                I18nReaderUtil.get("app.khachHang.sdt"),
                I18nReaderUtil.get("app.khachHang.hoTen"),
                I18nReaderUtil.get("app.khachHang.ngayTao"));

        panel.addActionColumn("app.common.actions",
                new ThaoTac("trash-2", "app.common.delete", ThaoTac.KieuThaoTac.NGUY_HIEM,
                        row -> this.xoa(panel, row)));

        ManagementFormPanel form = new ManagementFormPanel("app.khachHang.thongTinTitle");

        this.formPanel = new KhachHangFormPanel();
        form.addBlock(this.formPanel);

        this.boLocMa = panel.addTextFilter("app.khachHang.ma");
        this.boLocSdt = panel.addTextFilter("app.khachHang.sdt");
        this.boLocHoTen = panel.addTextFilter("app.khachHang.hoTen");
        this.boLocNgayTao = panel.addDateRangeFilter("app.khachHang.ngayTao");
        panel.setFilterChangeListener(() -> this.taiLai(panel, 1));

        form.getNutThem().addActionListener(e -> this.them(panel));
        form.getNutLuu().addActionListener(e -> this.luu(panel));
        form.getNutXoaTrang().addActionListener(e -> this.lamMoi(panel));
        form.getNutXoa().addActionListener(e -> this.xoa(panel, panel.getSelectedRow()));
        panel.getClearFilterButton().addActionListener(e -> panel.clearFilters());
        panel.getPrevButton().addActionListener(e -> this.taiLai(panel, this.trang - 1));
        panel.getNextButton().addActionListener(e -> this.taiLai(panel, this.trang + 1));
        panel.getTable().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                this.chonDong(panel);
            }
        });

        this.taiLai(panel, 1);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, form, panel);
        split.setDividerLocation(480);
        split.setOpaque(false);
        split.setBorder(null);

        this.add(split, BorderLayout.CENTER);
        this.revalidate();
        this.repaint();
    }

    private KhachHangFormData docDuLieuDong(ManagementPanel panel, int row) {
        return new KhachHangFormData(
                String.valueOf(panel.getModel().getValueAt(row, 0)),
                String.valueOf(panel.getModel().getValueAt(row, 1)),
                String.valueOf(panel.getModel().getValueAt(row, 2)),
                String.valueOf(panel.getModel().getValueAt(row, 3)));
    }

    private void xoa(ManagementPanel panel, int row) {
        try {
            if (row < 0) {
                throw new IllegalArgumentException(
                        "app.khachHang.chuaChonXoa");
            }

            String maKhachHang = String.valueOf(
                    panel.getModel().getValueAt(row, 0));

            if (!ThongBao.xacNhan(this, "app.khachHang.xacNhanXoa", "app.khachHang.tieuDeXoa")) {
                return;
            }

            this.khachHangService.delete(maKhachHang);
            ThongBao.thongTin(this, "app.common.delete_succeed");
            this.lamMoi(panel);
            this.taiLai(panel, this.trang);

        } catch (IllegalArgumentException e) {
            ThongBao.loi(this, e);
        } catch (SQLException e) {
            ThongBao.loi(this, "app.login.errorSystem");
        }
    }

    private void taiLai(ManagementPanel panel, int page) {
        DanhSachKhachHangCriteria tieuChi = new DanhSachKhachHangCriteria(
                this.boLocMa.getText(),
                this.boLocSdt.getText(),
                this.boLocHoTen.getText(),
                this.boLocNgayTao.tuNgay(),
                this.boLocNgayTao.denNgay(),
                panel.getQuickFilter(),
                page,
                SO_DONG_MOI_TRANG);
        try {
            PaginatedResult<KhachHang> ketQua = this.khachHangService.search(tieuChi);
            List<Object[]> rows = new ArrayList<>();
            for (KhachHang khachHang : ketQua.items()) {
                rows.add(new Object[] {
                        khachHang.getMaKhachHang(),
                        khachHang.getSdt(),
                        khachHang.getHoTen(),
                        this.dinhDangNgay(khachHang.getNgayTao())
                });
            }
            panel.setRows(rows);
            panel.setCount(ketQua.fromIndex(), ketQua.toIndex(), ketQua.totalItems());
            panel.setPage(ketQua.page(), Math.max(ketQua.totalPages(), 1));
            this.trang = ketQua.page();
        } catch (SQLException e) {
            ThongBao.loi(this, "app.login.errorSystem");
        }
    }

    private String dinhDangNgay(LocalDateTime thoiGian) {
        if (thoiGian == null) {
            return "";
        }
        return thoiGian.format(DateTimeFormatter.ofPattern(I18nReaderUtil.get("app.date.format")));
    }

    private void chonDong(ManagementPanel panel) {
        int row = panel.getSelectedRow();
        if (row < 0) {
            return;
        }

        this.formPanel.napDuLieu(this.docDuLieuDong(panel, row));
    }

    private void lamMoi(ManagementPanel panel) {
        this.formPanel.lamMoi();
        panel.getTable().clearSelection();
    }

    private KhachHangFormData getFormData() {
        return this.formPanel.layDuLieu();
    }

    private void validateFormData(KhachHangFormData data) {
    }

    private KhachHang taoKhachHang(KhachHangFormData data) {
        KhachHang khachHang = new KhachHang();
        khachHang.setMaKhachHang(data.maKhachHang());
        khachHang.setSdt(data.sdt());
        khachHang.setHoTen(data.hoTen());

        return khachHang;
    }

    private void them(ManagementPanel panel) {
        KhachHangFormData data = this.getFormData();
        this.validateFormData(data);

        try {
            this.khachHangService.create(this.taoKhachHang(data));
            ThongBao.thongTin(this, "app.common.create_succeed");
            this.lamMoi(panel);
            this.taiLai(panel, 1);
        } catch (IllegalArgumentException e) {
            ThongBao.loi(this, e);
        } catch (SQLException e) {
            ThongBao.loi(this, "app.login.errorSystem");
        }
    }

    private void luu(ManagementPanel panel) {
        KhachHangFormData data = this.getFormData();
        this.validateFormData(data);

        KhachHang khachHang = this.taoKhachHang(data);

        try {
            if (data.maKhachHang() == null) {
                this.khachHangService.create(khachHang);
                ThongBao.thongTin(this, "app.common.create_succeed");
            } else {
                this.khachHangService.update(khachHang);
                ThongBao.thongTin(this, "app.common.update_succeed");
            }
            this.lamMoi(panel);
            this.taiLai(panel, 1);
        } catch (IllegalArgumentException e) {
            ThongBao.loi(this, e);
        } catch (SQLException e) {
            ThongBao.loi(this, "app.login.errorSystem");
        }
    }

    @Override
    public String getTitleKey() {
        return "app.menu.customers";
    }

    @Override
    public MenuGroup getGroup() {
        return MenuGroup.TO_CHUC;
    }
}
