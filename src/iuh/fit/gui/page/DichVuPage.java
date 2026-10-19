package iuh.fit.gui.page;

import iuh.fit.dto.DanhSachDichVuCriteria;
import iuh.fit.dto.DichVuFormData;
import iuh.fit.dto.PaginatedResult;
import iuh.fit.entity.DichVu;
import iuh.fit.enumeration.MenuGroup;
import iuh.fit.gui.component.AnhField;
import iuh.fit.gui.component.AnhTableCellRenderer;
import iuh.fit.gui.component.ManagementFormPanel;
import iuh.fit.gui.component.ManagementPanel;
import iuh.fit.gui.component.ThongBao;
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

    private final JTextField maField = new JTextField(14);
    private final JTextField tenField = new JTextField(14);
    private final JTextField moTaField = new JTextField(14);
    private final JTextField donGiaField = new JTextField(14);

    private final AnhField anhField = new AnhField(
            "app.dichVu.anhUrl",
            "app.dichVu.chonAnh",
            "app.dichVu.goAnh",
            "app.dichVu.chuaCoAnh",
            "app.dichVu.khongTaiDuocAnh",
            "app.dichVu.loaiTepAnh",
            "app.dichVu.duLieuKhongPhaiAnh"
    );

    private ManagementPanel panel;
    private ManagementFormPanel form;
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
        split.setDividerLocation(380);
        split.setOpaque(false);
        split.setBorder(null);

        this.add(split, BorderLayout.CENTER);

        this.revalidate();
        this.repaint();
    }

    private void khoiTaoForm() {
        Theme.applyReadOnly(this.maField);

        this.form.addField(
                "app.dichVu.ma",
                this.maField
        );

        this.form.addField(
                "app.dichVu.ten",
                this.tenField
        );

        this.form.addField(
                "app.dichVu.moTa",
                this.moTaField
        );

        this.form.addBlock(this.anhField);

        this.form.addField(
                "app.dichVu.donGia",
                this.donGiaField
        );
    }

    private void khoiTaoBoLoc() {
        this.panel.addTextFilter("app.dichVu.ma", 0);
        this.panel.addTextFilter("app.dichVu.ten", 1);
        this.panel.addRangeFilter("app.dichVu.donGiaTuDen", 2);

        this.panel.setFilterChangeListener(
                () -> this.taiLai(this.panel, 1)
        );
    }

    private void ganSuKien() {
        this.form.getNutThem().addActionListener(
                e -> this.lamMoi()
        );

        this.form.getNutLuu().addActionListener(
                e -> this.luu()
        );

        this.form.getNutXoaTrang().addActionListener(
                e -> this.lamMoi()
        );

        this.form.getNutXoa().addActionListener(
                e -> this.xoa()
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
                            panel.getTextFilter(0),
                            panel.getTextFilter(1),
                            panel.getQuickFilter(),
                            NumberParseUtil.toDoubleOrNull(panel.getTextFilter(2)),
                            NumberParseUtil.toDoubleOrNull(panel.getTextFilter(3)),
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

        this.anhField.setDuongDan(
                NumberParseUtil.textOf(
                        this.panel.getModel().getValueAt(row, 0)
                )
        );

        this.maField.setText(
                NumberParseUtil.textOf(
                        this.panel.getModel().getValueAt(row, 1)
                )
        );

        this.tenField.setText(
                NumberParseUtil.textOf(
                        this.panel.getModel().getValueAt(row, 2)
                )
        );

        this.moTaField.setText(
                NumberParseUtil.textOf(
                        this.panel.getModel().getValueAt(row, 3)
                )
        );

        this.donGiaField.setText(
                NumberParseUtil.textOf(
                        this.panel.getModel().getValueAt(row, 4)
                )
        );

    }

    private void lamMoi() {
        this.maField.setText("");
        this.tenField.setText("");
        this.moTaField.setText("");
        this.donGiaField.setText("");

        this.panel.getTable().clearSelection();
        this.anhField.clear();

        this.tenField.requestFocusInWindow();
    }

    private DichVuFormData getFormData() {
        String ma = this.maField.getText().trim();
        String moTa = this.moTaField.getText().trim();
        String anhUrl = this.anhField.getDuongDan();
        return new DichVuFormData(
                ma.isBlank() ? null : ma,
                this.tenField.getText().trim(),
                moTa.isBlank() ? null : moTa,
                anhUrl.isBlank() ? null : anhUrl,
                NumberParseUtil.toDoubleOrNull(this.donGiaField.getText()));
    }

    private void validateFormData(DichVuFormData data) {
        if (data.tenDichVu().isBlank()) {
            throw new IllegalArgumentException("app.dichVu.loiTen");
        }

        if (data.donGia() == null || !Double.isFinite(data.donGia()) || data.donGia() < 0) {
            throw new IllegalArgumentException("app.dichVu.loiDonGia");
        }
    }

    private void luu() {
        DichVuFormData data = this.getFormData();
        this.validateFormData(data);

        DichVu dichVu = new DichVu(
                data.maDichVu(),
                data.tenDichVu(),
                data.moTa(),
                data.anhUrl(),
                data.donGia()
        );

        try {
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

    private void xoa() {
        int row = this.panel.getSelectedRow();

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
