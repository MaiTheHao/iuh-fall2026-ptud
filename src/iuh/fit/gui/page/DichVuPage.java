package iuh.fit.gui.page;

import iuh.fit.dto.DanhSachDichVuCriteria;
import iuh.fit.dto.DichVuFormData;
import iuh.fit.dto.PaginatedResult;
import iuh.fit.entity.DichVu;
import iuh.fit.enumeration.MenuGroup;
import iuh.fit.gui.component.AnhTableCellRenderer;
import iuh.fit.gui.component.ManagementFormPanel;
import iuh.fit.gui.component.ManagementPanel;
import iuh.fit.gui.component.ThaoTac;
import iuh.fit.gui.component.ThongBao;
import iuh.fit.gui.form.DichVuFormPanel;
import iuh.fit.gui.theme.Theme;
import iuh.fit.service.DichVuService;
import iuh.fit.util.I18nReaderUtil;
import iuh.fit.util.NumberParseUtil;

import javax.swing.BorderFactory;
import javax.swing.JSplitPane;
import javax.swing.JTextField;

import java.awt.BorderLayout;

import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

public class DichVuPage extends AbstractPage {

    private static final long serialVersionUID = 1L;
    private static final int SO_DONG_MOI_TRANG = 20;

    private final DichVuService dichVuService = new DichVuService();

    private DichVuFormPanel formPanel;

    private ManagementPanel panel;
    private ManagementFormPanel form;

    private JTextField boLocMa;
    private JTextField boLocTen;
    private ManagementPanel.BoLocKhoang boLocDonGia;

    private int trang = 1;

    public DichVuPage() {
        this.setLayout(new BorderLayout());
    }

    @Override
    public void render() {
        this.removeAll();

        this.setBorder(
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
        );
        this.setBackground(Theme.BG);

        this.panel = new ManagementPanel();
        this.panel.setListTitle("app.dichVu.danhSachTitle");

        this.panel.setHeaders(
                I18nReaderUtil.get("app.dichVu.hinhAnh"),
                I18nReaderUtil.get("app.dichVu.ma"),
                I18nReaderUtil.get("app.dichVu.ten"),
                I18nReaderUtil.get("app.dichVu.moTa"),
                I18nReaderUtil.get("app.dichVu.donGia")
        );

        this.panel.addActionColumn("app.common.actions",
                new ThaoTac("trash-2", "app.common.delete", ThaoTac.KieuThaoTac.NGUY_HIEM,
                        row -> this.xoa(row)));

        this.form = new ManagementFormPanel("app.dichVu.thongTinTitle");

        this.khoiTaoForm();
        this.khoiTaoBoLoc();
        this.ganSuKien();

        this.taiLai(this.panel, 1);

        JSplitPane split = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                this.form,
                this.panel
        );
        split.setDividerLocation(480);
        split.setOpaque(false);
        split.setBorder(null);

        this.add(split, BorderLayout.CENTER);

        this.revalidate();
        this.repaint();
    }

    private void khoiTaoForm() {
        this.formPanel = new DichVuFormPanel();
        this.form.addBlock(this.formPanel);
    }

    private void khoiTaoBoLoc() {
        this.boLocMa = this.panel.addTextFilter("app.dichVu.ma");
        this.boLocTen = this.panel.addTextFilter("app.dichVu.ten");
        this.boLocDonGia = this.panel.addRangeFilter("app.dichVu.donGiaTuDen");

        this.panel.setFilterChangeListener(
                () -> this.taiLai(this.panel, 1)
        );
    }

    private void ganSuKien() {
        this.form.getNutThem().addActionListener(
                e -> this.them()
        );

        this.form.getNutLuu().addActionListener(
                e -> this.luu()
        );

        this.form.getNutXoaTrang().addActionListener(
                e -> this.lamMoi()
        );

        this.form.getNutXoa().addActionListener(
                e -> this.xoa(this.panel.getSelectedRow())
        );

        this.panel.getClearFilterButton().addActionListener(
                e -> this.panel.clearFilters()
        );

        this.panel.getPrevButton().addActionListener(
                e -> this.taiLai(this.panel, this.trang - 1)
        );

        this.panel.getNextButton().addActionListener(
                e -> this.taiLai(this.panel, this.trang + 1)
        );

        this.panel.getTable().getSelectionModel()
                .addListSelectionListener(e -> {
                    if (!e.getValueIsAdjusting()) {
                        this.chonDong();
                    }
                });
    }

    private void taiLai(ManagementPanel panel, int page) {
        if (page < 1) {
            page = 1;
        }

        try {
            DanhSachDichVuCriteria tieuChi =
                    new DanhSachDichVuCriteria(
                            this.boLocMa.getText(),
                            this.boLocTen.getText(),
                            panel.getQuickFilter(),
                            NumberParseUtil.toDoubleOrNull(this.boLocDonGia.tuText()),
                            NumberParseUtil.toDoubleOrNull(this.boLocDonGia.denText()),
                            page,
                            SO_DONG_MOI_TRANG
                    );

            PaginatedResult<DichVu> ketQua =
                    this.dichVuService.search(tieuChi);

            List<Object[]> rows = new ArrayList<>();

            for (DichVu dichVu : ketQua.items()) {
                rows.add(new Object[]{
                        dichVu.getAnhUrl(),
                        dichVu.getMaDichVu(),
                        dichVu.getTenDichVu(),
                        dichVu.getMoTa(),
                        dichVu.getDonGia()
                });
            }

            panel.setRows(rows);
            this.cauHinhCotAnh();

            panel.setCount(
                    ketQua.fromIndex(),
                    ketQua.toIndex(),
                    ketQua.totalItems()
            );

            panel.setPage(
                    ketQua.page(),
                    Math.max(ketQua.totalPages(), 1)
            );

            this.trang = ketQua.page();

        } catch (NumberFormatException e) {
            ThongBao.loi(
                    this,
                    "app.dichVu.loiBoLocSo"
            );

        } catch (IllegalArgumentException e) {
            ThongBao.loi(this, e);

        } catch (SQLException e) {
            ThongBao.loi(
                    this,
                    "app.login.errorSystem"
            );
        }
    }

    private void cauHinhCotAnh() {
        AnhTableCellRenderer.ganVao(
                this.panel,
                0,
                "app.dichVu.chuaCoAnh",
                "app.dichVu.khongTaiDuocAnh"
        );
    }

    private void chonDong() {
        int row = this.panel.getSelectedRow();

        if (row < 0) {
            return;
        }

        this.formPanel.napDuLieu(this.docDuLieuDong(row));
    }

    private void lamMoi() {
        this.formPanel.lamMoi();
        this.panel.getTable().clearSelection();
    }

    private DichVuFormData getFormData() {
        return this.formPanel.layDuLieu();
    }

    private void validateFormData(DichVuFormData data) {
        if (data.tenDichVu().isBlank()) {
            throw new IllegalArgumentException("app.dichVu.loiTen");
        }

        if (data.donGia() == null || !Double.isFinite(data.donGia()) || data.donGia() < 0) {
            throw new IllegalArgumentException("app.dichVu.loiDonGia");
        }
    }

    private DichVu taoDichVu(DichVuFormData data) {
        return new DichVu(
                data.maDichVu(),
                data.tenDichVu(),
                data.moTa(),
                data.anhUrl(),
                data.donGia()
        );
    }

    private void them() {
        DichVuFormData data = this.getFormData();

        try {
            this.validateFormData(data);
            this.dichVuService.create(this.taoDichVu(data));

            ThongBao.thongTin(
                    this,
                    "app.common.create_succeed"
            );

            this.lamMoi();
            this.taiLai(this.panel, 1);

        } catch (IllegalArgumentException e) {
            ThongBao.loi(this, e);

        } catch (SQLException e) {
            ThongBao.loi(
                    this,
                    "app.login.errorSystem"
            );
        }
    }

    private void luu() {
        DichVuFormData data = this.getFormData();

        DichVu dichVu = this.taoDichVu(data);

        try {
            this.validateFormData(data);
            if (data.maDichVu() == null) {
                this.dichVuService.create(dichVu);

                ThongBao.thongTin(
                        this,
                        "app.common.create_succeed"
                );
            } else {
                this.dichVuService.update(dichVu);

                ThongBao.thongTin(
                        this,
                        "app.common.update_succeed"
                );
            }

            this.lamMoi();
            this.taiLai(this.panel, 1);

        } catch (IllegalArgumentException e) {
            ThongBao.loi(this, e);

        } catch (SQLException e) {
            ThongBao.loi(
                    this,
                    "app.login.errorSystem"
            );
        }
    }

    private DichVuFormData docDuLieuDong(int row) {
        return new DichVuFormData(
                NumberParseUtil.textOf(this.panel.getModel().getValueAt(row, 1)),
                NumberParseUtil.textOf(this.panel.getModel().getValueAt(row, 2)),
                NumberParseUtil.textOf(this.panel.getModel().getValueAt(row, 3)),
                NumberParseUtil.textOf(this.panel.getModel().getValueAt(row, 0)),
                NumberParseUtil.toDoubleOrNull(
                        NumberParseUtil.textOf(this.panel.getModel().getValueAt(row, 4))));
    }

    private void xoa(int row) {
        if (row < 0) {
            ThongBao.loi(this, "app.dichVu.chuaChonXoa");
            return;
        }

        String maDichVu = String.valueOf(
                this.panel.getModel().getValueAt(row, 1)
        );

        if (!ThongBao.xacNhan(
                this,
                "app.dichVu.xacNhanXoa",
                "app.dichVu.tieuDeXoa")) {
            return;
        }

        try {
            this.dichVuService.delete(maDichVu);

            ThongBao.thongTin(
                    this,
                    "app.common.delete_succeed"
            );

            this.lamMoi();
            this.taiLai(this.panel, this.trang);

        } catch (IllegalArgumentException e) {
            ThongBao.loi(this, e);

        } catch (SQLException e) {
            ThongBao.loi(
                    this,
                    "app.login.errorSystem"
            );
        }
    }

    @Override
    public String getTitleKey() {
        return "app.menu.servicePriceList";
    }

    @Override
    public MenuGroup getGroup() {
        return MenuGroup.TO_CHUC;
    }
}
