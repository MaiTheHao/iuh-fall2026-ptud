package iuh.fit.gui.page;

import iuh.fit.core.context.AppContext;
import iuh.fit.entity.PhieuTiepNhan;
import iuh.fit.entity.VanDeKyThuat;
import iuh.fit.enumeration.MenuGroup;
import iuh.fit.service.VanDeKyThuatService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class XemChanDoanPage extends AbstractPage {

    private final VanDeKyThuatService vanDeKyThuatService = new VanDeKyThuatService();
    private JLabel lblMaPhieu, lblBienSo;
    private JTable tblChanDoan;
    private DefaultTableModel tableModel;

    public XemChanDoanPage() {
    }

    @Override
    public void render() {
        removeAll();
        setLayout(new BorderLayout(10, 10));

        // Header Info
        JPanel pnlHeader = new JPanel(new GridLayout(2, 2, 10, 5));
        pnlHeader.setBorder(BorderFactory.createTitledBorder("Thông tin phiếu tiếp nhận"));

        lblMaPhieu = new JLabel("-");
        lblBienSo = new JLabel("-");

        pnlHeader.add(new JLabel("Mã phiếu:"));
        pnlHeader.add(lblMaPhieu);
        pnlHeader.add(new JLabel("Biển số xe:"));
        pnlHeader.add(lblBienSo);

        add(pnlHeader, BorderLayout.NORTH);

        // Table
        String[] columns = {"Mã vấn đề", "Tiêu đề", "Mô tả", "Mức ưu tiên", "Giải pháp", "Khách hàng đồng ý", "Ngày tạo"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Read-only
            }
        };
        tblChanDoan = new JTable(tableModel);
        add(new JScrollPane(tblChanDoan), BorderLayout.CENTER);

        // Back Button
        JButton btnBack = new JButton("Quay lại");
        btnBack.addActionListener(e -> goTo(QuanLyPhieuTiepNhanPage.class));
        JPanel pnlSouth = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlSouth.add(btnBack);
        add(pnlSouth, BorderLayout.SOUTH);

        revalidate();
        repaint();
    }

    public void onShow() {
        PhieuTiepNhan phieu = AppContext.getInstance().getSelectedPhieuTiepNhan();
        if (phieu != null) {
            lblMaPhieu.setText(phieu.getMaPhieuTiepNhan());
            lblBienSo.setText(phieu.getBienSo());
            loadData(phieu.getMaPhieuTiepNhan());
        } else {
            lblMaPhieu.setText("-");
            lblBienSo.setText("-");
            if (tableModel != null) {
                tableModel.setRowCount(0);
            }
        }
    }

    private void loadData(String maPhieu) {
        if (tableModel == null) return;
        tableModel.setRowCount(0);
        try {
            List<VanDeKyThuat> ds = vanDeKyThuatService.layDanhSachTheoPhieu(maPhieu);
            for (VanDeKyThuat vd : ds) {
                tableModel.addRow(new Object[]{
                        vd.getMaVanDe(),
                        vd.getTieuDe(),
                        vd.getMoTa(),
                        vd.getMucDoUuTien(),
                        vd.getGiaiPhap(),
                        vd.getKhachHangDongY(),
                        vd.getNgayTao()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu chẩn đoán: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public String getTitleKey() {
        return "app.menu.diagnoses";
    }

    @Override
    public MenuGroup getGroup() {
        return null;
    }
}