package iuh.fit.gui.page;

import iuh.fit.core.context.AppContext;
import iuh.fit.dto.PhieuTiepNhanView;
import iuh.fit.entity.VanDeKyThuat;
import iuh.fit.enumeration.MenuGroup;
import iuh.fit.gui.component.ManagementFormPanel;
import iuh.fit.gui.component.ManagementPanel;
import iuh.fit.gui.component.ThongBao;
import iuh.fit.gui.theme.Theme;
import iuh.fit.service.VanDeKyThuatService;
import iuh.fit.util.I18nReaderUtil;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import java.awt.BorderLayout;
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
    private ManagementFormPanel form;
    private JTextField txtMaPhieu;
    private JTextField txtBienSo;

    public XemChanDoanPage() {
        this.setLayout(new BorderLayout());
        this.setBackground(Theme.BG);
    }

    @Override
    public void render() {
        this.removeAll();
        this.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        this.setBackground(Theme.BG);

        this.form = new ManagementFormPanel("app.chanDoan.thongTinTitle");

        this.txtMaPhieu = new JTextField();
        Theme.applyReadOnly(this.txtMaPhieu);
        this.form.addField("app.chanDoan.ma", this.txtMaPhieu);

        this.txtBienSo = new JTextField();
        Theme.applyReadOnly(this.txtBienSo);
        this.form.addField("app.chanDoan.bienSo", this.txtBienSo);

        JButton nutQuayLai = new JButton(I18nReaderUtil.get("app.chanDoan.quayLai"));
        Theme.applySecondary(nutQuayLai);
        nutQuayLai.addActionListener(e -> this.goTo(QuanLyPhieuTiepNhanPage.class));
        this.form.thayNut(nutQuayLai);

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

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, this.form, this.managementPanel);
        split.setDividerLocation(480);
        split.setOpaque(false);
        split.setBorder(null);

        this.add(split, BorderLayout.CENTER);

        this.taiLai();
        this.revalidate();
        this.repaint();
    }

    private void taiLai() {
        try {
            PhieuTiepNhanView phieu = AppContext.getInstance().getSelectedPhieuTiepNhan();

            if (phieu == null) {
                this.txtMaPhieu.setText("");
                this.txtBienSo.setText("");
                this.managementPanel.setRows(List.of());
                this.managementPanel.setCount(0, 0, 0);
                this.managementPanel.setPage(1, 1);
                return;
            }

            this.txtMaPhieu.setText(phieu.maPhieuTiepNhan());
            this.txtBienSo.setText(phieu.bienSo());

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
