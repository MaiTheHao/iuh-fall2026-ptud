package iuh.fit.gui.page;

import iuh.fit.dto.DanhSachKhachHangCriteria;
import iuh.fit.entity.PhieuTiepNhan;
import iuh.fit.enumeration.MenuGroup;
import iuh.fit.enumeration.TrangThaiPhieuTiepNhan;
import iuh.fit.gui.component.ManagementPanel;
import iuh.fit.gui.component.ThongBao;
import iuh.fit.gui.theme.Theme;
import iuh.fit.service.KhachHangService;
import iuh.fit.service.PhieuTiepNhanService;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

public class LapChanDoanPage extends AbstractPage {

    private static final long serialVersionUID = 1L;
    
    private final PhieuTiepNhanService phieuTiepNhanService = new PhieuTiepNhanService();
    private final KhachHangService khachHangService = new KhachHangService();
    private LapChanDoanFilterPanel managementPanel;

    public LapChanDoanPage() {
        this.setLayout(new BorderLayout());
        this.setBackground(Theme.BG);
    }

    @Override
    public void render() {
        this.removeAll();
        this.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        // Khởi tạo Panel tùy chỉnh cho Lập Chẩn Đoán (ẩn form trái, cấu hình filter)
        this.managementPanel = new LapChanDoanFilterPanel();

        TitledBorder rightBorder = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Theme.BORDER),
                "Chọn Phiếu Tiếp Nhận"
        );
        rightBorder.setTitleFont(rightBorder.getTitleFont().deriveFont(Font.BOLD));
        this.managementPanel.setBorder(rightBorder);

        this.managementPanel.setHeaders(
                "Mã Tiếp Nhận",
                "Biển Số",
                "Khách Hàng",
                "Trạng Thái",
                "Thao Tác"
        );

        // Lắng nghe sự thay đổi của ComboBox Trạng Thái để load lại dữ liệu
        this.managementPanel.filterTrangThaiCombo.addActionListener(e -> this.taiLaiData(1));
        
        // Tích hợp tìm kiếm nhanh & load trang
        this.managementPanel.getClearFilterButton().addActionListener(e -> {
            this.managementPanel.clearFilters();
            this.taiLaiData(1);
        });
        
        this.add(this.managementPanel, BorderLayout.CENTER);
        
        // Lấy dữ liệu từ Database lên bảng lần đầu tiên
        this.taiLaiData(1);
        
        this.revalidate();
        this.repaint();
    }
    
    private void taiLaiData(int page) {
        try {
            // Lấy toàn bộ phiếu tiếp nhận từ database
            List<PhieuTiepNhan> danhSach = this.phieuTiepNhanService.findAll();
            List<Object[]> rows = new ArrayList<>();
            
            // Lấy giá trị bộ lọc trạng thái hiện tại
            Object selectedTrangThai = this.managementPanel.filterTrangThaiCombo.getSelectedItem();
            
            for (PhieuTiepNhan p : danhSach) {
                // Xử lý Lọc theo ComboBox Trạng Thái
                if (selectedTrangThai instanceof TrangThaiPhieuTiepNhan) {
                    if (p.getTrangThai() != selectedTrangThai) {
                        continue; // Bỏ qua nếu không khớp trạng thái lọc
                    }
                }
                
                String tenKH = "";
                // Truy vấn tên khách hàng từ mã
                if (p.getMaKhachHang() != null) {
                    var khCriteria = new DanhSachKhachHangCriteria(p.getMaKhachHang(), null, null, null, null, null, 1, 1);
                    var khResult = this.khachHangService.search(khCriteria);
                    if (khResult != null && !khResult.items().isEmpty()) {
                        tenKH = khResult.items().get(0).getHoTen();
                    }
                }

                // Dòng dữ liệu thêm vào bảng
                rows.add(new Object[]{
                        p.getMaPhieuTiepNhan(),
                        p.getBienSo(),
                        tenKH,
                        p.getTrangThai() != null ? p.getTrangThai().name() : "",
                        "Chọn" // Cột thao tác
                });
            }

            // Đưa dữ liệu lên UI bảng
            this.managementPanel.setRows(rows);
            this.managementPanel.setCount(1, rows.size(), rows.size());
            this.managementPanel.setPage(page, 1);
            
        } catch (Exception e) {
            ThongBao.loi(this, "Lỗi tải dữ liệu phiếu tiếp nhận: " + e.getMessage());
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

/**
 * Lớp cấu hình ManagementPanel chuyên biệt cho màn hình Chọn Phiếu Tiếp Nhận
 */
class LapChanDoanFilterPanel extends ManagementPanel {

    public final JComboBox<Object> filterTrangThaiCombo = new JComboBox<>();

    public LapChanDoanFilterPanel() {
        super();
        this.buildCustomLayout();
    }

    private void buildCustomLayout() {
        // 1. Ẩn toàn bộ form nhập liệu bên trái (BorderLayout.WEST) để full chiều rộng bảng
        if (this.getLayout() instanceof BorderLayout bl) {
            Component west = bl.getLayoutComponent(BorderLayout.WEST);
            if (west != null) {
                west.setVisible(false);
            }
            
            // Lấy listPanel ở vị trí CENTER
            Component center = bl.getLayoutComponent(BorderLayout.CENTER);
            if (center instanceof JPanel listPanel && listPanel.getLayout() instanceof BorderLayout bl2) {
                // Lấy vùng chứa Filters (nằm ở NORTH của listPanel)
                Component north = bl2.getLayoutComponent(BorderLayout.NORTH);
                
                if (north instanceof JPanel filterPanel && filterPanel.getLayout() instanceof BorderLayout bl3) {
                    // Xóa khu vực filter cũ (CENTER của filterPanel), chừa lại vùng Tìm nhanh (SOUTH)
                    Component oldCenter = bl3.getLayoutComponent(BorderLayout.CENTER);
                    if (oldCenter != null) {
                        filterPanel.remove(oldCenter);
                    }
                    
                    // Tạo Lưới Custom cho nhãn Trạng Thái & ComboBox
                    JPanel grid = new JPanel(new GridBagLayout());
                    grid.setOpaque(false);
                    grid.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));

                    GridBagConstraints gbc = new GridBagConstraints();
                    gbc.insets = new Insets(2, 4, 2, 4);
                    gbc.fill = GridBagConstraints.HORIZONTAL;
                    gbc.weightx = 1.0;

                    // Thêm Nhãn "Trạng Thái"
                    gbc.gridx = 0; 
                    gbc.gridy = 0;
                    grid.add(new JLabel("Trạng Thái"), gbc);

                    // Thêm ComboBox
                    gbc.gridy = 1;
                    this.filterTrangThaiCombo.removeAllItems();
                    this.filterTrangThaiCombo.addItem("Tất cả"); // Mặc định hiển thị tất cả
                    for (TrangThaiPhieuTiepNhan tt : TrangThaiPhieuTiepNhan.values()) {
                        this.filterTrangThaiCombo.addItem(tt);
                    }
                    grid.add(this.filterTrangThaiCombo, gbc);

                    // Gắn lại vào phần CENTER của Vùng filter, nó sẽ nằm ngay TRÊN vùng Tìm Nhanh (SOUTH)
                    filterPanel.add(grid, BorderLayout.CENTER);
                }
            }
        }
    }

    @Override
    public void clearFilters() {
        super.clearFilters(); // Xóa text tìm nhanh
        if (this.filterTrangThaiCombo.getItemCount() > 0) {
            this.filterTrangThaiCombo.setSelectedIndex(0); // Reset combobox về "Tất cả"
        }
    }
}