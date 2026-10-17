package iuh.fit.gui.page;

import iuh.fit.dto.DanhSachXeCriteria;
import iuh.fit.dto.PaginatedResult;
import iuh.fit.entity.Xe;
import iuh.fit.enumeration.MenuGroup;
import iuh.fit.gui.component.ManagementPanel;
import iuh.fit.gui.component.ThongBao;
import iuh.fit.service.XeService;
import iuh.fit.util.I18nReaderUtil;
import iuh.fit.util.NumberParseUtil;

import javax.swing.BorderFactory;
import javax.swing.JTable;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class XePage extends AbstractPage {

    private static final int SO_DONG_MOI_TRANG = 20;

    private final XeService xeService = new XeService();

    private final JTextField bienSoField = new JTextField(14);
    private final JTextField hangXeField = new JTextField(14);
    private final JTextField dongXeField = new JTextField(14);
    private final JTextField doiXeField = new JTextField(14);
    private final JTextField odoField = new JTextField(14);

    private int trang = 1;

    public XePage() {
        this.setLayout(new BorderLayout());
    }

    @Override
    public void render() {
        this.removeAll();
        this.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        ManagementPanel panel = new ManagementPanel();

        panel.setHeaders(I18nReaderUtil.get("app.xe.bienSo"), I18nReaderUtil.get("app.xe.hangXe"),
                I18nReaderUtil.get("app.xe.dongXe"), I18nReaderUtil.get("app.xe.doiXe"),
                I18nReaderUtil.get("app.xe.odo"));

        panel.addFormRow("app.xe.bienSo", this.bienSoField);
        panel.addFormRow("app.xe.hangXe", this.hangXeField);
        panel.addFormRow("app.xe.dongXe", this.dongXeField);
        panel.addFormRow("app.xe.doiXe", this.doiXeField);
        panel.addFormRow("app.xe.odo", this.odoField);

        panel.addTextFilter("app.xe.bienSo", 0);
        panel.addTextFilter("app.xe.hangXe", 1);
        panel.addTextFilter("app.xe.dongXe", 2);
        panel.addRangeFilter("app.xe.doiXeTuDen", 3);
        panel.addRangeFilter("app.xe.odoTuDen", 5);

        panel.setFilterChangeListener(() -> this.taiLai(panel, 1));
        panel.getThemButton().addActionListener(e -> this.lamMoi(panel));

        panel.getLuuButton().addActionListener(e -> this.luu(panel));

        panel.getXoaTrangButton().addActionListener(e -> this.lamMoi(panel));

        panel.getXoaButton().addActionListener(e -> this.xoa(panel));

        panel.getClearFilterButton().addActionListener(e -> panel.clearFilters());

        panel.getPrevButton().addActionListener(e -> this.taiLai(panel, this.trang - 1));

        panel.getNextButton().addActionListener(e -> this.taiLai(panel, this.trang + 1));

        panel.getTable().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                this.chonDong(panel);
            }
        });
        panel.getTable().setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        this.taiLai(panel, 1);

        this.add(panel, BorderLayout.CENTER);

        this.revalidate();
        this.repaint();
    }

    private void xoa(ManagementPanel panel) {
        int row = panel.getSelectedRow();

        if (row < 0) {
            ThongBao.canhBao(this, "app.xe.chuaChonXoa");
            return;
        }

        String bienSo = String.valueOf(panel.getModel().getValueAt(row, 0));

        if (!ThongBao.xacNhan(this, "app.xe.xacNhanXoa", "app.xe.tieuDeXoa")) {
            return;
        }

        try {
            this.xeService.delete(bienSo);

            ThongBao.thongTin(this, "app.common.delete_succeed");

            this.lamMoi(panel);
            this.taiLai(panel, this.trang);

        } catch (IllegalArgumentException | IllegalStateException e) {
            ThongBao.loi(this, e);
        } catch (SQLException e) {
            ThongBao.loi(this, "app.login.errorSystem");
        }
    }

    private void taiLai(ManagementPanel panel, int page) {
        try {
            DanhSachXeCriteria tieuChi = new DanhSachXeCriteria(panel.getTextFilter(0), panel.getTextFilter(1),
                    panel.getTextFilter(2), NumberParseUtil.toIntegerOrNull(panel.getTextFilter(3)),
                    NumberParseUtil.toIntegerOrNull(panel.getTextFilter(4)),
                    NumberParseUtil.toIntegerOrNull(panel.getTextFilter(5)),
                    NumberParseUtil.toIntegerOrNull(panel.getTextFilter(6)), panel.getQuickFilter(), page,
                    SO_DONG_MOI_TRANG);

            PaginatedResult<Xe> ketQua = this.xeService.search(tieuChi);

            List<Object[]> rows = new ArrayList<>();

            for (Xe xe : ketQua.items()) {
                rows.add(new Object[] { xe.getBienSo(), xe.getHangXe(), xe.getDongXe(), xe.getDoiXe(), xe.getOdo() });
            }

            panel.setRows(rows);

            panel.setCount(ketQua.fromIndex(), ketQua.toIndex(), ketQua.totalItems());

            panel.setPage(ketQua.page(), Math.max(ketQua.totalPages(), 1));

            this.trang = ketQua.page();

        } catch (NumberFormatException e) {
            ThongBao.loi(this, "app.xe.loiBoLocSo");
        } catch (SQLException e) {
            ThongBao.loi(this, "app.login.errorSystem");
        }
    }

    private void chonDong(ManagementPanel panel) {
        int row = panel.getSelectedRow();

        if (row < 0) {
            return;
        }

        this.bienSoField.setText(String.valueOf(panel.getModel().getValueAt(row, 0)));

        this.hangXeField.setText(String.valueOf(panel.getModel().getValueAt(row, 1)));

        this.dongXeField.setText(String.valueOf(panel.getModel().getValueAt(row, 2)));

        this.doiXeField.setText(String.valueOf(panel.getModel().getValueAt(row, 3)));

        this.odoField.setText(String.valueOf(panel.getModel().getValueAt(row, 4)));
    }

    private void lamMoi(ManagementPanel panel) {
        this.bienSoField.setText("");
        this.hangXeField.setText("");
        this.dongXeField.setText("");
        this.doiXeField.setText("");
        this.odoField.setText("");

        panel.getTable().clearSelection();
        this.hangXeField.requestFocusInWindow();
    }

    private void luu(ManagementPanel panel) {
        try {
            Xe xe = new Xe();

            String bienSo = this.bienSoField.getText().trim();

            xe.setBienSo(bienSo.isBlank() ? null : bienSo);
            xe.setHangXe(this.hangXeField.getText().trim());
            xe.setDongXe(this.dongXeField.getText().trim());

            String doiXe = this.doiXeField.getText().trim();
            String odo = this.odoField.getText().trim();

            if (doiXe.isBlank() || odo.isBlank()) {
                throw new IllegalArgumentException("app.xe.loiThongTinSo");
            }

            xe.setDoiXe(Integer.parseInt(doiXe));
            xe.setOdo(Integer.parseInt(odo));

            if (!this.xeService.existsByBienSo(bienSo)) {
                this.xeService.create(xe);

                ThongBao.thongTin(this, "app.common.create_succeed");
            } else {
                this.xeService.update(xe);

                ThongBao.thongTin(this, "app.common.update_succeed");
            }
            this.lamMoi(panel);
            this.taiLai(panel, 1);

        } catch (NumberFormatException e) {
            ThongBao.loi(this, "app.xe.loiThongTinSo");
        } catch (IllegalArgumentException e) {
            ThongBao.loi(this, e);
        } catch (SQLException e) {
            ThongBao.loi(this, "app.login.errorSystem");
        }
    }

    @Override
    public String getTitleKey() {
        return "app.menu.vehicles";
    }

    @Override
    public MenuGroup getGroup() {
        return MenuGroup.TO_CHUC;
    }
}
