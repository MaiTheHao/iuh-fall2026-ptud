package iuh.fit.gui.page;

import iuh.fit.dto.DanhSachPhieuTiepNhanCriteria;
import iuh.fit.dto.PaginatedResult;
import iuh.fit.dto.PhieuTiepNhanView;
import iuh.fit.entity.VanDeKyThuat;
import iuh.fit.enumeration.MenuGroup;
import iuh.fit.enumeration.TrangThaiPhieuTiepNhan;
import iuh.fit.gui.component.ManagementFormPanel;
import iuh.fit.gui.component.ManagementPanel;
import iuh.fit.gui.component.ThaoTac;
import iuh.fit.gui.component.ThongBao;
import iuh.fit.gui.theme.Theme;
import iuh.fit.service.PhieuTiepNhanService;
import iuh.fit.service.VanDeKyThuatService;
import iuh.fit.util.I18nReaderUtil;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Màn hình lập chẩn đoán kỹ thuật cho một phiếu tiếp nhận đã có.
 */
public class LapChanDoanPage extends AbstractPage {

    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = Logger.getLogger(LapChanDoanPage.class.getName());

    private static final int SO_DONG_MOI_TRANG = 20;

    private final PhieuTiepNhanService phieuTiepNhanService = new PhieuTiepNhanService();
    private final VanDeKyThuatService vanDeKyThuatService = new VanDeKyThuatService();

    private ManagementPanel managementPanel;
    private ManagementFormPanel form;
    private ManagementPanel.BoLocChon boLocTrangThai;
    private JTextField txtMaPhieu;
    private JTextField txtBienSo;
    private JTextField txtTieuDe;
    private JTextField txtMucDoUuTien;
    private JTextArea txtMoTa;
    private JTextArea txtGiaiPhap;
    private List<PhieuTiepNhanView> danhSachHienTai = new ArrayList<>();
    private PhieuTiepNhanView phieuDangChon;
    private int trang = 1;

    public LapChanDoanPage() {
        this.setLayout(new BorderLayout());
        this.setBackground(Theme.BG);
    }

    @Override
    public void render() {
        this.removeAll();
        this.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        this.setBackground(Theme.BG);

        this.managementPanel = new ManagementPanel();
        this.managementPanel.setListTitle("app.chanDoan.danhSachTitle");
        this.managementPanel.setHeaders(
                I18nReaderUtil.get("app.chanDoan.ma"),
                I18nReaderUtil.get("app.chanDoan.bienSo"),
                I18nReaderUtil.get("app.chanDoan.khachHang"),
                I18nReaderUtil.get("app.chanDoan.trangThai")
        );

        this.managementPanel.addActionColumn("app.common.actions",
                new ThaoTac("check", "app.chanDoan.chon", ThaoTac.KieuThaoTac.THUONG,
                        row -> this.chonPhieu(row)));

        this.boLocTrangThai = this.managementPanel.addComboBoxFilter(
                "app.chanDoan.trangThai", TrangThaiPhieuTiepNhan.values());

        this.managementPanel.setFilterChangeListener(() -> this.taiLaiData(1));
        this.managementPanel.getClearFilterButton().addActionListener(
                e -> this.managementPanel.clearFilters());
        this.managementPanel.getPrevButton().addActionListener(
                e -> this.taiLaiData(this.trang - 1));
        this.managementPanel.getNextButton().addActionListener(
                e -> this.taiLaiData(this.trang + 1));

        this.managementPanel.getTable().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                this.capNhatPhieuDangChon();
            }
        });

        this.txtMaPhieu = new JTextField();
        Theme.applyReadOnly(this.txtMaPhieu);

        this.txtBienSo = new JTextField();
        Theme.applyReadOnly(this.txtBienSo);

        this.txtTieuDe = new JTextField();
        this.txtMucDoUuTien = new JTextField("50");

        this.txtMoTa = new JTextArea();
        this.txtMoTa.setLineWrap(true);
        this.txtMoTa.setWrapStyleWord(true);
        JScrollPane cuonMoTa = new JScrollPane(this.txtMoTa);
        cuonMoTa.setPreferredSize(new Dimension(200, 100));

        this.txtGiaiPhap = new JTextArea();
        this.txtGiaiPhap.setLineWrap(true);
        this.txtGiaiPhap.setWrapStyleWord(true);
        JScrollPane cuonGiaiPhap = new JScrollPane(this.txtGiaiPhap);
        cuonGiaiPhap.setPreferredSize(new Dimension(200, 100));

        this.form = new ManagementFormPanel("app.chanDoan.lapChanDoanTitle");
        this.form.addField("app.chanDoan.ma", this.txtMaPhieu);
        this.form.addField("app.chanDoan.bienSo", this.txtBienSo);
        this.form.addField("app.chanDoan.tieuDe", this.txtTieuDe);
        this.form.addField("app.chanDoan.mucDoUuTien", this.txtMucDoUuTien);
        this.form.addField("app.chanDoan.moTa", cuonMoTa);
        this.form.addField("app.chanDoan.giaiPhap", cuonGiaiPhap);

        JButton nutLuu = new JButton(I18nReaderUtil.get("app.chanDoan.luu"));
        Theme.applyPrimary(nutLuu);
        nutLuu.addActionListener(e -> this.luuChanDoan());
        this.form.thayNut(nutLuu);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, this.form, this.managementPanel);
        split.setDividerLocation(480);
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
            TrangThaiPhieuTiepNhan trangThai = this.boLocTrangThai.giaTri()
                    instanceof TrangThaiPhieuTiepNhan value ? value : null;

            DanhSachPhieuTiepNhanCriteria tieuChi = new DanhSachPhieuTiepNhanCriteria(
                    null,
                    null,
                    null,
                    null,
                    null,
                    trangThai,
                    null,
                    null,
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
                        view.trangThai() == null ? "" : view.trangThai().getI18NBusinessName()
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
            this.phieuDangChon = null;
            this.txtMaPhieu.setText("");
            this.txtBienSo.setText("");
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Khong the tai danh sach phieu tiep nhan", ex);
            ThongBao.loi(this, "app.chanDoan.loiTaiDanhSach");
        }
    }

    private void capNhatPhieuDangChon() {
        int row = this.managementPanel.getSelectedRow();
        if (row < 0 || row >= this.danhSachHienTai.size()) {
            this.phieuDangChon = null;
            this.txtMaPhieu.setText("");
            this.txtBienSo.setText("");
            return;
        }

        PhieuTiepNhanView view = this.danhSachHienTai.get(row);
        this.phieuDangChon = view;
        this.txtMaPhieu.setText(view.maPhieuTiepNhan() == null ? "" : view.maPhieuTiepNhan());
        this.txtBienSo.setText(view.bienSo() == null ? "" : view.bienSo());
    }

    private void chonPhieu(int row) {
        if (row < 0 || row >= this.danhSachHienTai.size()) {
            return;
        }

        PhieuTiepNhanView view = this.danhSachHienTai.get(row);
        this.phieuDangChon = view;
        this.txtMaPhieu.setText(view.maPhieuTiepNhan() == null ? "" : view.maPhieuTiepNhan());
        this.txtBienSo.setText(view.bienSo() == null ? "" : view.bienSo());
        this.managementPanel.getTable().setRowSelectionInterval(row, row);
    }

    private void lamMoiForm() {
        this.txtTieuDe.setText("");
        this.txtMoTa.setText("");
        this.txtGiaiPhap.setText("");
        this.txtMucDoUuTien.setText("50");
        this.txtMaPhieu.setText("");
        this.txtBienSo.setText("");
        this.phieuDangChon = null;
        this.managementPanel.getTable().clearSelection();
        this.txtTieuDe.requestFocus();
    }

    private void luuChanDoan() {
        if (this.phieuDangChon == null) {
            ThongBao.canhBao(this, "app.chanDoan.chuaChonPhieu");
            return;
        }

        int mucDo;
        try {
            mucDo = Integer.parseInt(this.txtMucDoUuTien.getText().trim());
        } catch (NumberFormatException ex) {
            ThongBao.canhBao(this, "app.chanDoan.loiMucDoUuTien");
            return;
        }

        VanDeKyThuat vanDe = new VanDeKyThuat();
        vanDe.setMaPhieuTiepNhan(this.phieuDangChon.maPhieuTiepNhan());
        vanDe.setTieuDe(this.txtTieuDe.getText().trim());
        vanDe.setMoTa(this.txtMoTa.getText().trim());
        vanDe.setGiaiPhap(this.txtGiaiPhap.getText().trim());
        vanDe.setMucDoUuTien(mucDo);

        try {
            this.vanDeKyThuatService.lapChanDoan(vanDe);
            ThongBao.thongTin(this, "app.chanDoan.thanhCong");
            this.lamMoiForm();
            this.taiLaiData(this.trang);
        } catch (IllegalArgumentException ex) {
            ThongBao.loi(this, ex);
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Khong the lap chan doan", ex);
            ThongBao.loi(this, "app.login.errorSystem");
        }
    }

    @Override
    public String getTitleKey() {
        return "app.menu.diagnoses";
    }

    @Override
    public MenuGroup getGroup() {
        return MenuGroup.TIEP_NHAN;
    }
}
