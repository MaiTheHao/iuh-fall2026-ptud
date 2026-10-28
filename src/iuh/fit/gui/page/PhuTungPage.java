package iuh.fit.gui.page;

import iuh.fit.dto.DanhSachPhuTungCriteria;
import iuh.fit.dto.PaginatedResult;
import iuh.fit.dto.PhuTungFormData;
import iuh.fit.entity.PhuTung;
import iuh.fit.enumeration.MenuGroup;
import iuh.fit.gui.component.AnhTableCellRenderer;
import iuh.fit.gui.component.ManagementFormPanel;
import iuh.fit.gui.component.ManagementPanel;
import iuh.fit.gui.component.ThaoTac;
import iuh.fit.gui.component.ThongBao;
import iuh.fit.gui.form.PhuTungFormPanel;
import iuh.fit.gui.theme.Theme;
import iuh.fit.service.PhuTungService;
import iuh.fit.util.I18nReaderUtil;
import iuh.fit.util.NumberParseUtil;

import javax.swing.BorderFactory;
import javax.swing.JSplitPane;
import javax.swing.JTextField;

import java.awt.BorderLayout;

import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

public class PhuTungPage extends AbstractPage {

    private static final long serialVersionUID = 1L;
    private static final int SO_DONG_MOI_TRANG = 20;

    private final PhuTungService phuTungService =
            new PhuTungService();

    private PhuTungFormPanel formPanel;

    private ManagementPanel panel;
    private ManagementFormPanel form;

    private JTextField boLocMa;
    private JTextField boLocTen;
    private ManagementPanel.BoLocKhoang boLocDonGia;
    private ManagementPanel.BoLocKhoang boLocSoLuong;

    private int trang = 1;

    public PhuTungPage() {
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
        this.panel.setListTitle("app.phuTung.danhSachTitle");

        this.panel.setHeaders(
                I18nReaderUtil.get("app.phuTung.hinhAnh"),
                I18nReaderUtil.get("app.phuTung.ma"),
                I18nReaderUtil.get("app.phuTung.ten"),
                I18nReaderUtil.get("app.phuTung.moTa"),
                I18nReaderUtil.get("app.phuTung.donGia"),
                I18nReaderUtil.get("app.phuTung.soLuongTon")
        );

        this.panel.addActionColumn("app.common.actions",
                new ThaoTac("trash-2", "app.common.delete", ThaoTac.KieuThaoTac.NGUY_HIEM,
                        row -> this.xoa(row)));

        this.form = new ManagementFormPanel("app.phuTung.thongTinTitle");

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
        this.formPanel = new PhuTungFormPanel();
        this.form.addBlock(this.formPanel);
    }

    private void khoiTaoBoLoc() {
        // Mã phụ tùng, tên phụ tùng
        this.boLocMa = this.panel.addTextFilter("app.phuTung.ma");
        this.boLocTen = this.panel.addTextFilter("app.phuTung.ten");

        // Đơn giá từ - đến
        this.boLocDonGia = this.panel.addRangeFilter("app.phuTung.donGiaTuDen");

        // Tồn kho từ - đến
        this.boLocSoLuong = this.panel.addRangeFilter("app.phuTung.soLuongTuDen");

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
            DanhSachPhuTungCriteria tieuChi =
                    new DanhSachPhuTungCriteria(
                            this.boLocMa.getText(),
                            this.boLocTen.getText(),
                            panel.getQuickFilter(),
                            NumberParseUtil.toDoubleOrNull(
                                    this.boLocDonGia.tuText()
                            ),
                            NumberParseUtil.toDoubleOrNull(
                                    this.boLocDonGia.denText()
                            ),
                            NumberParseUtil.toIntegerOrNull(
                                    this.boLocSoLuong.tuText()
                            ),
                            NumberParseUtil.toIntegerOrNull(
                                    this.boLocSoLuong.denText()
                            ),
                            page,
                            SO_DONG_MOI_TRANG
                    );

            PaginatedResult<PhuTung> ketQua =
                    this.phuTungService.search(tieuChi);

            List<Object[]> rows = new ArrayList<>();

            for (PhuTung phuTung : ketQua.items()) {
                rows.add(new Object[]{
                        phuTung.getAnhUrl(),
                        phuTung.getMaPhuTung(),
                        phuTung.getTenPhuTung(),
                        phuTung.getMoTa(),
                        phuTung.getDonGiaNhap(),
                        phuTung.getSoLuongTon()
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
            ThongBao.loi(this, "app.phuTung.loiBoLocSo");

        } catch (IllegalArgumentException e) {
            ThongBao.loi(this, e);

        } catch (SQLException e) {
            ThongBao.loi(this, "app.login.errorSystem");
        }
    }

    private void cauHinhCotAnh() {
        AnhTableCellRenderer.ganVao(
                this.panel,
                0,
                "app.phuTung.chuaCoAnh",
                "app.phuTung.khongTaiDuocAnh"
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

    private PhuTungFormData getFormData() {
        return this.formPanel.layDuLieu();
    }

    private void validateFormData(PhuTungFormData data) {
        if (data.tenPhuTung().isBlank()) {
            throw new IllegalArgumentException("app.phuTung.loiTen");
        }

        if (data.donGiaNhap() == null || !Double.isFinite(data.donGiaNhap()) || data.donGiaNhap() < 0) {
            throw new IllegalArgumentException("app.phuTung.loiDonGia");
        }
    }

    private PhuTung taoPhuTung(PhuTungFormData data, int soLuongTon) {
        return new PhuTung(
                data.maPhuTung(),
                data.tenPhuTung(),
                data.moTa(),
                data.anhUrl(),
                data.donGiaNhap(),
                soLuongTon
        );
    }

    private void them() {
        PhuTungFormData data = this.getFormData();

        try {
            this.validateFormData(data);
            this.phuTungService.create(this.taoPhuTung(data, 0));
            ThongBao.thongTin(this, "app.common.create_succeed");

            this.lamMoi();
            this.taiLai(this.panel, 1);

        } catch (IllegalArgumentException e) {
            ThongBao.loi(this, e);

        } catch (SQLException e) {
            ThongBao.loi(this, "app.login.errorSystem");
        }
    }

    private void luu() {
        PhuTungFormData data = this.getFormData();

        boolean taoMoi = data.maPhuTung() == null;

        try {
            this.validateFormData(data);
            PhuTung phuTung = this.taoPhuTung(data, 0);
            if (taoMoi) {
                this.phuTungService.create(phuTung);
                ThongBao.thongTin(this, "app.common.create_succeed");
            } else {
                this.phuTungService.update(phuTung);
                ThongBao.thongTin(this, "app.common.update_succeed");
            }

            this.lamMoi();
            this.taiLai(this.panel, 1);

        } catch (IllegalArgumentException e) {
            ThongBao.loi(this, e);

        } catch (SQLException e) {
            ThongBao.loi(this, "app.login.errorSystem");
        }
    }

    private PhuTungFormData docDuLieuDong(int row) {
        return new PhuTungFormData(
                NumberParseUtil.textOf(this.panel.getModel().getValueAt(row, 1)),
                NumberParseUtil.textOf(this.panel.getModel().getValueAt(row, 2)),
                NumberParseUtil.textOf(this.panel.getModel().getValueAt(row, 3)),
                NumberParseUtil.textOf(this.panel.getModel().getValueAt(row, 0)),
                NumberParseUtil.toDoubleOrNull(
                        NumberParseUtil.textOf(this.panel.getModel().getValueAt(row, 4))),
                NumberParseUtil.toIntegerOrNull(
                        NumberParseUtil.textOf(this.panel.getModel().getValueAt(row, 5))));
    }

    private void xoa(int row) {
        try {
            if (row < 0) {
                throw new IllegalArgumentException(
                        "app.phuTung.chuaChonXoa"
                );
            }

            String maPhuTung = String.valueOf(
                    this.panel.getModel().getValueAt(row, 1)
            );

            if (!ThongBao.xacNhan(
                    this,
                    "app.phuTung.xacNhanXoa",
                    "app.phuTung.tieuDeXoa"
            )) {
                return;
            }

            this.phuTungService.delete(maPhuTung);
            ThongBao.thongTin(this, "app.common.delete_succeed");

            this.lamMoi();
            this.taiLai(this.panel, this.trang);

        } catch (IllegalArgumentException e) {
            ThongBao.loi(this, e);

        } catch (SQLException e) {
            ThongBao.loi(this, "app.login.errorSystem");
        }
    }

    @Override
    public String getTitleKey() {
        return "app.menu.inventory";
    }

    @Override
    public MenuGroup getGroup() {
        return MenuGroup.TO_CHUC;
    }
}
