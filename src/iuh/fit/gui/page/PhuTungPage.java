package iuh.fit.gui.page;

import iuh.fit.dto.DanhSachPhuTungCriteria;
import iuh.fit.dto.PaginatedResult;
import iuh.fit.dto.PhuTungFormData;
import iuh.fit.entity.PhuTung;
import iuh.fit.enumeration.MenuGroup;
import iuh.fit.gui.component.AnhField;
import iuh.fit.gui.component.AnhTableCellRenderer;
import iuh.fit.gui.component.ManagementPanel;
import iuh.fit.gui.component.ThongBao;
import iuh.fit.service.PhuTungService;
import iuh.fit.util.I18nReaderUtil;
import iuh.fit.util.NumberParseUtil;

import javax.swing.BorderFactory;
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

    private final JTextField maField = new JTextField(14);
    private final JTextField tenField = new JTextField(14);
    private final JTextField moTaField = new JTextField(14);
    private final JTextField donGiaField = new JTextField(14);
    private final JTextField soLuongField = new JTextField(14);

    private final AnhField anhField = new AnhField(
            "app.phuTung.anhUrl",
            "app.phuTung.chonAnh",
            "app.phuTung.goAnh",
            "app.phuTung.chuaCoAnh",
            "app.phuTung.khongTaiDuocAnh",
            "app.phuTung.loaiTepAnh",
            "app.phuTung.duLieuKhongPhaiAnh"
    );

    private ManagementPanel panel;
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

        this.panel = new ManagementPanel();

        this.panel.setHeaders(
                I18nReaderUtil.get("app.phuTung.hinhAnh"),
                I18nReaderUtil.get("app.phuTung.ma"),
                I18nReaderUtil.get("app.phuTung.ten"),
                I18nReaderUtil.get("app.phuTung.moTa"),
                I18nReaderUtil.get("app.phuTung.donGia"),
                I18nReaderUtil.get("app.phuTung.soLuongTon")
        );

        this.khoiTaoForm();
        this.khoiTaoBoLoc();
        this.ganSuKien();

        this.taiLai(this.panel, 1);

        this.add(this.panel, BorderLayout.CENTER);

        this.revalidate();
        this.repaint();
    }

    private void khoiTaoForm() {
        this.maField.setEnabled(true);

        this.panel.addFormRow("app.phuTung.ma", this.maField);
        this.panel.addFormRow("app.phuTung.ten", this.tenField);
        this.panel.addFormRow("app.phuTung.moTa", this.moTaField);

        this.panel.getFormPanel().add(this.anhField);

        this.panel.addFormRow("app.phuTung.donGia", this.donGiaField);
        this.panel.addFormRow("app.phuTung.soLuongTon", this.soLuongField);

        this.soLuongField.setEditable(false);
    }

    private void khoiTaoBoLoc() {
        // Mã phụ tùng, tên phụ tùng
        this.panel.addTextFilter("app.phuTung.ma", 0);
        this.panel.addTextFilter("app.phuTung.ten", 1);

        // Đơn giá từ - đến
        this.panel.addRangeFilter("app.phuTung.donGiaTuDen", 2);

        // Tồn kho từ - đến
        this.panel.addRangeFilter("app.phuTung.soLuongTuDen", 4);

        this.panel.setFilterChangeListener(
                () -> this.taiLai(this.panel, 1)
        );
    }

    private void ganSuKien() {
        this.panel.getThemButton().addActionListener(
                e -> this.lamMoi()
        );

        this.panel.getLuuButton().addActionListener(
                e -> this.luu()
        );

        this.panel.getXoaTrangButton().addActionListener(
                e -> this.lamMoi()
        );

        this.panel.getXoaButton().addActionListener(
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
            DanhSachPhuTungCriteria tieuChi =
                    new DanhSachPhuTungCriteria(
                            panel.getTextFilter(0),
                            panel.getTextFilter(1),
                            panel.getQuickFilter(),
                            NumberParseUtil.toDoubleOrNull(
                                    panel.getTextFilter(2)
                            ),
                            NumberParseUtil.toDoubleOrNull(
                                    panel.getTextFilter(3)
                            ),
                            NumberParseUtil.toIntegerOrNull(
                                    panel.getTextFilter(4)
                            ),
                            NumberParseUtil.toIntegerOrNull(
                                    panel.getTextFilter(5)
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

        this.soLuongField.setText(
                NumberParseUtil.textOf(
                        this.panel.getModel().getValueAt(row, 5)
                )
        );

        this.maField.setEnabled(false);
    }

    private void lamMoi() {
        this.maField.setText("");
        this.tenField.setText("");
        this.moTaField.setText("");
        this.donGiaField.setText("");
        this.soLuongField.setText("");

        this.anhField.clear();

        this.maField.setEnabled(true);

        this.panel.getTable().clearSelection();

        this.maField.requestFocusInWindow();
    }

    private PhuTungFormData getFormData() {
        String ma = this.maField.getText().trim();
        String moTa = this.moTaField.getText().trim();
        String anhUrl = this.anhField.getDuongDan();
        return new PhuTungFormData(
                ma.isBlank() ? null : ma,
                this.tenField.getText().trim(),
                moTa.isBlank() ? null : moTa,
                anhUrl.isBlank() ? null : anhUrl,
                NumberParseUtil.toDoubleOrNull(this.donGiaField.getText()),
                NumberParseUtil.toIntegerOrNull(this.soLuongField.getText()));
    }

    private void validateFormData(PhuTungFormData data) {
        if (data.tenPhuTung().isBlank()) {
            throw new IllegalArgumentException("app.phuTung.loiTen");
        }

        if (data.donGiaNhap() == null || !Double.isFinite(data.donGiaNhap()) || data.donGiaNhap() < 0) {
            throw new IllegalArgumentException("app.phuTung.loiDonGia");
        }

        if (data.maPhuTung() != null && (data.soLuongTon() == null || data.soLuongTon() < 0)) {
            throw new IllegalArgumentException("app.phuTung.loiSoLuong");
        }
    }

    private void luu() {
        PhuTungFormData data = this.getFormData();
        this.validateFormData(data);

        boolean taoMoi = data.maPhuTung() == null;
        int soLuong = taoMoi ? 0 : data.soLuongTon();

        PhuTung phuTung = new PhuTung(
                data.maPhuTung(),
                data.tenPhuTung(),
                data.moTa(),
                data.anhUrl(),
                data.donGiaNhap(),
                soLuong
        );

        try {
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

    private void xoa() {
        int row = this.panel.getSelectedRow();

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
