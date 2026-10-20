package iuh.fit.gui.page;

import iuh.fit.dto.DanhSachXeCriteria;
import iuh.fit.dto.PaginatedResult;
import iuh.fit.dto.XeFormData;
import iuh.fit.entity.Xe;
import iuh.fit.enumeration.MenuGroup;
import iuh.fit.gui.component.ManagementFormPanel;
import iuh.fit.gui.component.ManagementPanel;
import iuh.fit.gui.component.ThaoTac;
import iuh.fit.gui.component.ThongBao;
import iuh.fit.gui.theme.Theme;
import iuh.fit.service.XeService;
import iuh.fit.util.I18nReaderUtil;
import iuh.fit.util.NumberParseUtil;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JSplitPane;
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
    private final JComboBox<String> hangXeCombo = new JComboBox<>();
    private final JComboBox<String> dongXeCombo = new JComboBox<>();
    private final JTextField doiXeField = new JTextField(14);
    private final JTextField odoField = new JTextField(14);

    private int trang = 1;

    public XePage() {
        this.setLayout(new BorderLayout());
        this.hangXeCombo.setEditable(true);
        this.dongXeCombo.setEditable(true);
    }

    @Override
    public void render() {
        Theme.applyEditable(this.bienSoField);

        this.removeAll();
        this.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        this.setBackground(Theme.BG);

        ManagementPanel panel = new ManagementPanel();

        panel.setListTitle("app.xe.danhSachTitle");

        panel.setHeaders(I18nReaderUtil.get("app.xe.bienSo"), I18nReaderUtil.get("app.xe.hangXe"),
                I18nReaderUtil.get("app.xe.dongXe"), I18nReaderUtil.get("app.xe.doiXe"),
                I18nReaderUtil.get("app.xe.odo"));

        panel.addActionColumn("app.common.actions",
                new ThaoTac("pencil", "app.common.edit", ThaoTac.KieuThaoTac.THUONG,
                        row -> this.sua(panel, row)),
                new ThaoTac("trash-2", "app.common.delete", ThaoTac.KieuThaoTac.NGUY_HIEM,
                        row -> this.xoa(panel, row)));

        ManagementFormPanel form = new ManagementFormPanel("app.xe.thongTinTitle");

        form.addField("app.xe.bienSo", this.bienSoField);
        form.addField("app.xe.hangXe", this.hangXeCombo);
        form.addField("app.xe.dongXe", this.dongXeCombo);
        form.addField("app.xe.doiXe", this.doiXeField);
        form.addField("app.xe.odo", this.odoField);

        panel.addTextFilter("app.xe.bienSo", 0);
        panel.addTextFilter("app.xe.hangXe", 1);
        panel.addTextFilter("app.xe.dongXe", 2);
        panel.addRangeFilter("app.xe.doiXeTuDen", 3);
        panel.addRangeFilter("app.xe.odoTuDen", 5);

        panel.setFilterChangeListener(() -> this.taiLai(panel, 1));
        form.getNutThem().addActionListener(e -> this.them(panel));

        form.getNutLuu().addActionListener(e -> this.luu(panel));

        form.getNutXoaTrang().addActionListener(e -> this.lamMoi(panel));

        form.getNutXoa().addActionListener(e -> this.xoa(panel, panel.getSelectedRow()));

        panel.getClearFilterButton().addActionListener(e -> panel.clearFilters());

        this.clearActionListeners(this.hangXeCombo);
        this.hangXeCombo.addActionListener(e -> {
            this.dongXeCombo.setSelectedItem("");
            this.taiGoiYDongXe();
        });

        panel.getPrevButton().addActionListener(e -> this.taiLai(panel, this.trang - 1));

        panel.getNextButton().addActionListener(e -> this.taiLai(panel, this.trang + 1));

        panel.getTable().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                this.chonDong(panel);
            }
        });
        panel.getTable().setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        this.taiLai(panel, 1);
        this.taiGoiYHangXe();

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, form, panel);
        split.setDividerLocation(380);
        split.setOpaque(false);
        split.setBorder(null);

        this.add(split, BorderLayout.CENTER);

        this.revalidate();
        this.repaint();
    }

    private void sua(ManagementPanel panel, int row) {
        this.chonDong(panel);
        this.hangXeCombo.requestFocusInWindow();
    }

    private void xoa(ManagementPanel panel, int row) {
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
        Theme.applyReadOnly(this.bienSoField);

        this.hangXeCombo.setSelectedItem(String.valueOf(panel.getModel().getValueAt(row, 1)));

        this.dongXeCombo.setSelectedItem(String.valueOf(panel.getModel().getValueAt(row, 2)));

        this.doiXeField.setText(String.valueOf(panel.getModel().getValueAt(row, 3)));

        this.odoField.setText(String.valueOf(panel.getModel().getValueAt(row, 4)));
    }

    private void lamMoi(ManagementPanel panel) {
        this.bienSoField.setText("");
        Theme.applyEditable(this.bienSoField);
        this.hangXeCombo.setSelectedItem("");
        this.dongXeCombo.setSelectedItem("");
        this.doiXeField.setText("");
        this.odoField.setText("");

        panel.getTable().clearSelection();
        this.hangXeCombo.requestFocusInWindow();
    }

    private String giaTriCombo(JComboBox<String> combo) {
        Object v = combo.getEditor().getItem();
        return v == null ? "" : v.toString().trim();
    }

    private void taiGoiYHangXe() {
        try {
            String dangChon = this.giaTriCombo(this.hangXeCombo);

            this.hangXeCombo.removeAllItems();

            for (String h : this.xeService.getDanhSachHangXe()) {
                this.hangXeCombo.addItem(h);
            }

            this.hangXeCombo.setSelectedItem(dangChon);

        } catch (SQLException e) {
            ThongBao.loi(this, "app.login.errorSystem");
        }
    }

    private void taiGoiYDongXe() {
        try {
            String dangChon = this.giaTriCombo(this.dongXeCombo);

            this.dongXeCombo.removeAllItems();

            for (String d : this.xeService.getDanhSachDongXe(this.giaTriCombo(this.hangXeCombo))) {
                this.dongXeCombo.addItem(d);
            }

            this.dongXeCombo.setSelectedItem(dangChon);

        } catch (SQLException e) {
            ThongBao.loi(this, "app.login.errorSystem");
        }
    }

    private XeFormData getFormData() {
        return new XeFormData(
                this.bienSoField.getText().trim(),
                this.giaTriCombo(this.hangXeCombo),
                this.giaTriCombo(this.dongXeCombo),
                NumberParseUtil.toIntegerOrNull(this.doiXeField.getText()),
                NumberParseUtil.toIntegerOrNull(this.odoField.getText()));
    }

    private void validateFormData(XeFormData data) {
        if (data.doiXe() == null) {
            throw new IllegalArgumentException("app.xe.loiDoiXeSo");
        }
    }

    private Xe taoXe(XeFormData data) {
        Xe xe = new Xe();

        String bienSo = data.bienSo();

        xe.setBienSo(bienSo.isBlank() ? null : bienSo);
        xe.setHangXe(data.hangXe());
        xe.setDongXe(data.dongXe());
        xe.setDoiXe(data.doiXe());
        xe.setOdo(data.odo() == null ? 0 : data.odo());

        return xe;
    }

    private void them(ManagementPanel panel) {
        try {
            XeFormData data = this.getFormData();
            this.validateFormData(data);

            this.xeService.create(this.taoXe(data));

            ThongBao.thongTin(this, "app.common.create_succeed");

            this.lamMoi(panel);
            this.taiLai(panel, 1);

        } catch (IllegalArgumentException e) {
            ThongBao.loi(this, e);
        } catch (SQLException e) {
            ThongBao.loi(this, "app.login.errorSystem");
        }
    }

    private void luu(ManagementPanel panel) {
        try {
            XeFormData data = this.getFormData();
            this.validateFormData(data);

            Xe xe = this.taoXe(data);

            if (!this.xeService.existsByBienSo(data.bienSo())) {
                this.xeService.create(xe);

                ThongBao.thongTin(this, "app.common.create_succeed");
            } else {
                this.xeService.update(xe);

                ThongBao.thongTin(this, "app.common.update_succeed");
            }
            this.lamMoi(panel);
            this.taiLai(panel, 1);

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
