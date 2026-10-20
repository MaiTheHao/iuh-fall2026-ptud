package iuh.fit.gui.page;

import iuh.fit.dto.DanhSachKhachHangCriteria;
import iuh.fit.entity.PhieuTiepNhan;
import iuh.fit.enumeration.MenuGroup;
import iuh.fit.enumeration.TrangThaiPhieuTiepNhan;
import iuh.fit.gui.component.ManagementFormPanel;
import iuh.fit.gui.component.ManagementPanel;
import iuh.fit.gui.component.ThaoTac;
import iuh.fit.gui.component.ThongBao;
import iuh.fit.gui.theme.Theme;
import iuh.fit.service.KhachHangService;
import iuh.fit.service.PhieuTiepNhanService;
import iuh.fit.util.I18nReaderUtil;
import iuh.fit.util.NumberParseUtil;

import javax.swing.BorderFactory;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

public class LapChanDoanPage extends AbstractPage {

    private static final long serialVersionUID = 1L;
    
    private final PhieuTiepNhanService phieuTiepNhanService = new PhieuTiepNhanService();
    private final KhachHangService khachHangService = new KhachHangService();
    private ManagementPanel managementPanel;
    private ManagementFormPanel form;

    private final JTextField maField = new JTextField();
    private final JTextField bienSoField = new JTextField();
    private final JTextField khachHangField = new JTextField();
    private final JTextField trangThaiField = new JTextField();

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

        this.managementPanel.addComboBoxFilter("app.chanDoan.trangThai", 3, TrangThaiPhieuTiepNhan.values());
        this.managementPanel.setFilterChangeListener(() -> this.taiLaiData(1));

        this.managementPanel.getClearFilterButton().addActionListener(e -> this.managementPanel.clearFilters());

        this.managementPanel.getPrevButton().addActionListener(e -> this.taiLaiData(this.trang - 1));

        this.managementPanel.getNextButton().addActionListener(e -> this.taiLaiData(this.trang + 1));

        this.form = new ManagementFormPanel("app.chanDoan.thongTinTitle");

        Theme.applyReadOnly(this.maField);
        Theme.applyReadOnly(this.bienSoField);
        Theme.applyReadOnly(this.khachHangField);
        Theme.applyReadOnly(this.trangThaiField);

        this.form.addField("app.chanDoan.ma", this.maField);
        this.form.addField("app.chanDoan.bienSo", this.bienSoField);
        this.form.addField("app.chanDoan.khachHang", this.khachHangField);
        this.form.addField("app.chanDoan.trangThai", this.trangThaiField);

        this.form.thayNut();

        this.managementPanel.getTable().getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) {
                return;
            }

            int row = this.managementPanel.getSelectedRow();

            if (row < 0) {
                return;
            }

            this.hienThiDong(row);
        });

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, this.form, this.managementPanel);
        split.setDividerLocation(380);
        split.setOpaque(false);
        split.setBorder(null);
        this.add(split, BorderLayout.CENTER);

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
            Object selectedTrangThai = this.managementPanel.getComboFilterValue(3);
            
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
                        p.getTrangThai() != null ? p.getTrangThai().getI18NBusinessName() : ""
                });
            }

            // Đưa dữ liệu lên UI bảng
            this.managementPanel.setRows(rows);
            this.managementPanel.setCount(1, rows.size(), rows.size());
            this.managementPanel.setPage(page, 1);
            this.trang = page;

        } catch (Exception e) {
            ThongBao.loi(this, "app.chanDoan.loiTaiDanhSach");
        }
    }

    private void chonPhieu(int row) {
        this.managementPanel.getTable().setRowSelectionInterval(row, row);
        this.hienThiDong(row);
    }

    private void hienThiDong(int row) {
        this.maField.setText(NumberParseUtil.textOf(this.managementPanel.getModel().getValueAt(row, 0)));
        this.bienSoField.setText(NumberParseUtil.textOf(this.managementPanel.getModel().getValueAt(row, 1)));
        this.khachHangField.setText(NumberParseUtil.textOf(this.managementPanel.getModel().getValueAt(row, 2)));
        this.trangThaiField.setText(NumberParseUtil.textOf(this.managementPanel.getModel().getValueAt(row, 3)));
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
