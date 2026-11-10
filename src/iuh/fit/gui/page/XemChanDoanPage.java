package iuh.fit.gui.page;

import iuh.fit.core.context.AppContext;
import iuh.fit.dto.PhieuTiepNhanView;
import iuh.fit.entity.VanDeKyThuat;
import iuh.fit.enumeration.MenuGroup;
import iuh.fit.enumeration.TrangThaiVanDeKyThuat;
import iuh.fit.gui.Theme;
import iuh.fit.gui.component.ManagementPanel;
import iuh.fit.gui.component.ThaoTac;
import iuh.fit.gui.component.ThongBao;
import iuh.fit.service.VanDeKyThuatService;
import iuh.fit.util.DateFormats;
import iuh.fit.util.I18nReaderUtil;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class XemChanDoanPage extends AbstractPage {

    
    private static final Logger LOGGER = Logger.getLogger(XemChanDoanPage.class.getName());

    private final VanDeKyThuatService vanDeKyThuatService = new VanDeKyThuatService();

    private ManagementPanel pnlDanhSach;
    private List<VanDeKyThuat> danhSachHienTai = List.of();
    private JLabel lblMaPhieu;
    private JLabel lblBienSo;

    public XemChanDoanPage() {
        this.setLayout(new BorderLayout(0, 8));
        this.setBackground(Theme.BG);
    }

    @Override
    public void render() {
        this.removeAll();
        this.setBorder(Theme.leDeu(8));
        this.setBackground(Theme.BG);

        this.pnlDanhSach = new ManagementPanel();
        this.pnlDanhSach.setListTitle("app.chanDoan.danhSachChanDoanTitle");
        this.pnlDanhSach.setHeaders(
                I18nReaderUtil.get("app.chanDoan.maVanDe"),
                I18nReaderUtil.get("app.chanDoan.tieuDe"),
                I18nReaderUtil.get("app.chanDoan.moTa"),
                I18nReaderUtil.get("app.chanDoan.mucDoUuTien"),
                I18nReaderUtil.get("app.chanDoan.giaiPhap"),
                I18nReaderUtil.get("app.chanDoan.khachHangDongY"),
                I18nReaderUtil.get("app.chanDoan.ngayTao")
        );

        this.pnlDanhSach.addActionColumn("app.common.actions",
                new ThaoTac("check", "app.chanDoan.datCo", ThaoTac.KieuThaoTac.THUONG,
                        row -> this.capNhatTrangThai(row, TrangThaiVanDeKyThuat.CO)),
                new ThaoTac("ban", "app.chanDoan.datKhong", ThaoTac.KieuThaoTac.THUONG,
                        row -> this.capNhatTrangThai(row, TrangThaiVanDeKyThuat.KHONG)));

        this.pnlDanhSach.setFilterChangeListener(() -> this.taiLai());
        this.pnlDanhSach.getClearFilterButton().addActionListener(
                e -> this.pnlDanhSach.clearFilters());

        this.add(this.taoThanhThongTin(), BorderLayout.NORTH);
        this.add(this.pnlDanhSach, BorderLayout.CENTER);

        this.taiLai();
        this.revalidate();
        this.repaint();
    }

    private JPanel taoThanhThongTin() {
        JPanel thanh = Theme.contentPanel(8);

        JPanel dongThongTin = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        dongThongTin.setOpaque(false);

        JLabel nhanMa = new JLabel(I18nReaderUtil.get("app.chanDoan.ma") + ":");
        nhanMa.setFont(Theme.fieldLabelFont());
        nhanMa.setForeground(Theme.INK);
        this.lblMaPhieu = new JLabel("");
        this.lblMaPhieu.setForeground(Theme.INK);

        JLabel nhanBienSo = new JLabel(I18nReaderUtil.get("app.chanDoan.bienSo") + ":");
        nhanBienSo.setFont(Theme.fieldLabelFont());
        nhanBienSo.setForeground(Theme.INK);
        this.lblBienSo = new JLabel("");
        this.lblBienSo.setForeground(Theme.INK);

        dongThongTin.add(nhanMa);
        dongThongTin.add(this.lblMaPhieu);
        dongThongTin.add(nhanBienSo);
        dongThongTin.add(this.lblBienSo);

        JButton nutQuayLai = new JButton(I18nReaderUtil.get("app.chanDoan.quayLai"));
        Theme.applySecondary(nutQuayLai);
        nutQuayLai.addActionListener(e -> this.goTo(QuanLyPhieuTiepNhanPage.class));

        thanh.add(dongThongTin, BorderLayout.CENTER);
        thanh.add(nutQuayLai, BorderLayout.EAST);
        return thanh;
    }

    private void taiLai() {
        try {
            PhieuTiepNhanView phieu = AppContext.getInstance().getSelectedPhieuTiepNhan();

            if (phieu == null) {
                this.lblMaPhieu.setText("");
                this.lblBienSo.setText("");
                this.danhSachHienTai = List.of();
                this.pnlDanhSach.setRows(List.of());
                this.pnlDanhSach.setCount(0, 0, 0);
                this.pnlDanhSach.setPage(1, 1);
                return;
            }

            this.lblMaPhieu.setText(phieu.maPhieuTiepNhan() == null ? "" : phieu.maPhieuTiepNhan());
            this.lblBienSo.setText(phieu.bienSo() == null ? "" : phieu.bienSo());

            List<VanDeKyThuat> ds = this.vanDeKyThuatService.layDanhSachTheoPhieu(
                    phieu.maPhieuTiepNhan(), this.pnlDanhSach.getQuickFilter());
            this.danhSachHienTai = ds;

            List<Object[]> rows = new ArrayList<>();
            for (VanDeKyThuat vd : ds) {
                rows.add(new Object[]{
                        vd.getMaVanDe(),
                        vd.getTieuDe(),
                        vd.getMoTa(),
                        vd.getMucDoUuTien(),
                        vd.getGiaiPhap(),
                        vd.getKhachHangDongY() == null ? "" : vd.getKhachHangDongY().getI18NBusinessName(),
                        vd.getNgayTao() != null ? vd.getNgayTao().format(DateFormats.NGAY_GIO) : ""
                });
            }

            this.pnlDanhSach.setRows(rows);
            this.pnlDanhSach.setCount(rows.isEmpty() ? 0 : 1, rows.size(), rows.size());
            this.pnlDanhSach.setPage(1, 1);
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Khong the tai danh sach chan doan", ex);
            ThongBao.loi(this, "app.chanDoan.loiTaiDanhSach");
        }
    }

    private void capNhatTrangThai(int row, TrangThaiVanDeKyThuat trangThai) {
        if (row < 0 || row >= this.danhSachHienTai.size()) {
            return;
        }

        VanDeKyThuat vanDe = this.danhSachHienTai.get(row);
        try {
            this.vanDeKyThuatService.capNhatTrangThaiDongY(vanDe.getMaVanDe(), trangThai);
            this.taiLai();
        } catch (IllegalArgumentException | IllegalStateException ex) {
            ThongBao.loi(this, ex);
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Khong the cap nhat trang thai chan doan", ex);
            ThongBao.loi(this, "app.login.errorSystem");
        }
    }

    @Override
    public String getTitleKey() {
        return "app.chanDoan.xemChanDoanTitle";
    }

    @Override
    public MenuGroup getGroup() {
        return MenuGroup.TIEP_NHAN;
    }
}
