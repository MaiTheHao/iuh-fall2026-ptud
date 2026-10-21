package iuh.fit.gui.page;

import iuh.fit.dto.DanhSachPhieuTiepNhanCriteria;
import iuh.fit.dto.PaginatedResult;
import iuh.fit.dto.PhieuTiepNhanView;
import iuh.fit.entity.PhieuTiepNhan;
import iuh.fit.entity.VanDeKyThuat;
import iuh.fit.enumeration.MenuGroup;
import iuh.fit.service.PhieuTiepNhanService;
import iuh.fit.service.VanDeKyThuatService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class LapChanDoanPage extends AbstractPage {

    private final PhieuTiepNhanService phieuTiepNhanService = new PhieuTiepNhanService();
    private final VanDeKyThuatService vanDeKyThuatService = new VanDeKyThuatService();

    private JTextField txtTimKiem, txtTieuDe, txtMucDoUuTien;
    private JTextArea txtMoTa, txtGiaiPhap;
    private JTable tblPhieu;
    private DefaultTableModel tableModel;
    private PhieuTiepNhan phieuDangChon;

    public LapChanDoanPage() {
    }

    @Override
    public void render() {
        removeAll();
        setLayout(new BorderLayout(10, 10));

        // West Panel: Search & Pick Intake Slip
        JPanel pnlWest = new JPanel(new BorderLayout(5, 5));
        pnlWest.setBorder(BorderFactory.createTitledBorder("Chọn phiếu tiếp nhận"));
        pnlWest.setPreferredSize(new Dimension(400, 0));

        JPanel pnlSearch = new JPanel(new BorderLayout(5, 5));
        txtTimKiem = new JTextField();
        JButton btnTim = new JButton("Tìm");
        btnTim.addActionListener(e -> timKiemPhieu());
        pnlSearch.add(txtTimKiem, BorderLayout.CENTER);
        pnlSearch.add(btnTim, BorderLayout.EAST);
        pnlWest.add(pnlSearch, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new String[]{"Mã phiếu", "Biển số", "Khách hàng"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tblPhieu = new JTable(tableModel);
        tblPhieu.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tblPhieu.getSelectedRow() != -1) {
                int row = tblPhieu.getSelectedRow();
                String maPhieu = (String) tableModel.getValueAt(row, 0);
                try {
                    // Sửa lại thành getById thay vì findById
                    phieuDangChon = phieuTiepNhanService.getById(maPhieu);
                } catch (SQLException ex) {
                    phieuDangChon = null;
                    JOptionPane.showMessageDialog(this, "Không thể lấy thông tin phiếu: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        pnlWest.add(new JScrollPane(tblPhieu), BorderLayout.CENTER);

        // Center Panel: Form Entry
        JPanel pnlCenter = new JPanel(new GridBagLayout());
        pnlCenter.setBorder(BorderFactory.createTitledBorder("Lập chẩn đoán kỹ thuật"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtTieuDe = new JTextField(20);
        txtMucDoUuTien = new JTextField("50", 20);
        txtMoTa = new JTextArea(4, 20);
        txtGiaiPhap = new JTextArea(4, 20);

        gbc.gridx = 0; gbc.gridy = 0; pnlCenter.add(new JLabel("Tiêu đề (*):"), gbc);
        gbc.gridx = 1; pnlCenter.add(txtTieuDe, gbc);

        gbc.gridx = 0; gbc.gridy = 1; pnlCenter.add(new JLabel("Mức ưu tiên (1-100):"), gbc);
        gbc.gridx = 1; pnlCenter.add(txtMucDoUuTien, gbc);

        gbc.gridx = 0; gbc.gridy = 2; pnlCenter.add(new JLabel("Mô tả:"), gbc);
        gbc.gridx = 1; pnlCenter.add(new JScrollPane(txtMoTa), gbc);

        gbc.gridx = 0; gbc.gridy = 3; pnlCenter.add(new JLabel("Giải pháp đề xuất:"), gbc);
        gbc.gridx = 1; pnlCenter.add(new JScrollPane(txtGiaiPhap), gbc);

        JButton btnLuu = new JButton("Lưu chẩn đoán");
        btnLuu.addActionListener(e -> xuLyLuu());
        gbc.gridx = 1; gbc.gridy = 4; gbc.anchor = GridBagConstraints.EAST;
        pnlCenter.add(btnLuu, gbc);

        add(pnlWest, BorderLayout.WEST);
        add(pnlCenter, BorderLayout.CENTER);

        revalidate();
        repaint();
    }

    private void timKiemPhieu() {
        try {
            String tuKhoa = txtTimKiem.getText().trim();
            DanhSachPhieuTiepNhanCriteria criteria = new DanhSachPhieuTiepNhanCriteria(
                    null, null, null, null, null, null, null, null,
                    tuKhoa.isEmpty() ? null : tuKhoa, 1, 50
            );
            
            // Sửa kiểu trả về thành PhieuTiepNhanView
            PaginatedResult<PhieuTiepNhanView> result = phieuTiepNhanService.search(criteria);
            if (tableModel != null) {
                tableModel.setRowCount(0);
                for (PhieuTiepNhanView p : result.items()) {
                    // Dùng getter theo kiểu record của Java
                    tableModel.addRow(new Object[]{p.maPhieuTiepNhan(), p.bienSo(), p.tenKhachHang()});
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi tìm kiếm: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xuLyLuu() {
        if (phieuDangChon == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn phiếu tiếp nhận từ danh sách bên trái.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int mucDo = Integer.parseInt(txtMucDoUuTien.getText().trim());
            VanDeKyThuat vd = new VanDeKyThuat();
            vd.setMaPhieuTiepNhan(phieuDangChon.getMaPhieuTiepNhan());
            vd.setTieuDe(txtTieuDe.getText().trim());
            vd.setMoTa(txtMoTa.getText().trim());
            vd.setMucDoUuTien(mucDo);
            vd.setGiaiPhap(txtGiaiPhap.getText().trim());

            vanDeKyThuatService.lapChanDoan(vd);
            JOptionPane.showMessageDialog(this, "Lập chẩn đoán thành công!");

            // Clear form
            txtTieuDe.setText("");
            txtMoTa.setText("");
            txtGiaiPhap.setText("");
            txtMucDoUuTien.setText("50");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Mức độ ưu tiên phải là con số hợp lệ (1-100).", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void onShow() {
        timKiemPhieu();
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