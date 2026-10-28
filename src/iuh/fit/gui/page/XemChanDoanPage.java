package iuh.fit.gui.page;

import iuh.fit.core.context.AppContext;
import iuh.fit.dto.PhieuTiepNhanView;
import iuh.fit.entity.VanDeKyThuat;
import iuh.fit.enumeration.MenuGroup;
import iuh.fit.gui.component.ManagementPanel;
import iuh.fit.gui.component.ThongBao;
import iuh.fit.gui.theme.Theme;
import iuh.fit.service.VanDeKyThuatService;
import iuh.fit.util.I18nReaderUtil;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Màn hình chỉ đọc hiển thị chẩn đoán kỹ thuật của phiếu tiếp nhận đang chọn.
 */
public class XemChanDoanPage extends AbstractPage {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(XemChanDoanPage.class.getName());

    private static final DateTimeFormatter NGAY_GIO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final VanDeKyThuatService vanDeKyThuatService = new VanDeKyThuatService();

    private ManagementPanel managementPanel;
    private JLabel lblMaPhieu;
    private JLabel lblBienSo;

    public XemChanDoanPage() {
        this.setLayout(new BorderLayout(0, 8));
        this.setBackground(Theme.BG);
    }

    @Override
    public void render() {
        this.removeAll();
        this.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        this.setBackground(Theme.BG);

        this.managementPanel = new ManagementPanel();
        this.managementPanel.setListTitle("app.chanDoan.danhSachChanDoanTitle");
        this.managementPanel.setHeaders(
                I18nReaderUtil.get("app.chanDoan.maVanDe"),
                I18nReaderUtil.get("app.chanDoan.tieuDe"),
                I18nReaderUtil.get("app.chanDoan.moTa"),
                I18nReaderUtil.get("app.chanDoan.mucDoUuTien"),
                I18nReaderUtil.get("app.chanDoan.giaiPhap"),
                I18nReaderUtil.get("app.chanDoan.khachHangDongY"),
                I18nReaderUtil.get("app.chanDoan.ngayTao")
        );

        this.managementPanel.setFilterChangeListener(() -> this.taiLai());
        this.managementPanel.getClearFilterButton().addActionListener(
                e -> this.managementPanel.clearFilters());

        this.add(this.taoThanhThongTin(), BorderLayout.NORTH);
        this.add(this.managementPanel, BorderLayout.CENTER);

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
                this.managementPanel.setRows(List.of());
                this.managementPanel.setCount(0, 0, 0);
                this.managementPanel.setPage(1, 1);
                return;
            }

            this.lblMaPhieu.setText(phieu.maPhieuTiepNhan() == null ? "" : phieu.maPhieuTiepNhan());
            this.lblBienSo.setText(phieu.bienSo() == null ? "" : phieu.bienSo());

            List<VanDeKyThuat> ds = this.vanDeKyThuatService.layDanhSachTheoPhieu(
                    phieu.maPhieuTiepNhan(), this.managementPanel.getQuickFilter());

            List<Object[]> rows = new ArrayList<>();
            for (VanDeKyThuat vd : ds) {
                rows.add(new Object[]{
                        vd.getMaVanDe(),
                        vd.getTieuDe(),
                        vd.getMoTa(),
                        vd.getMucDoUuTien(),
                        vd.getGiaiPhap(),
                        vd.getKhachHangDongY() == null ? "" : vd.getKhachHangDongY().getI18NBusinessName(),
                        vd.getNgayTao() != null ? vd.getNgayTao().format(NGAY_GIO) : ""
                });
            }

            this.managementPanel.setRows(rows);
            this.managementPanel.setCount(rows.isEmpty() ? 0 : 1, rows.size(), rows.size());
            this.managementPanel.setPage(1, 1);
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Khong the tai danh sach chan doan", ex);
            ThongBao.loi(this, "app.chanDoan.loiTaiDanhSach");
        }
    }

    @Override
    public String getTitleKey() {
        return "app.chanDoan.xemChanDoanTitle";
    }

    @Override
    public MenuGroup getGroup() {
        return null;
    }
}
