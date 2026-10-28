package iuh.fit.gui.component;

import com.github.lgooddatepicker.components.DatePicker;
import iuh.fit.gui.theme.Theme;
import iuh.fit.util.I18nReaderUtil;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ManagementPanel extends JPanel {

    private static final String THE_BANG = "bang";
    private static final String THE_TRONG = "trong";

    /** Handle của một bộ lọc chọn một giá trị; đọc mã enum hoặc giá trị thô. */
    public record BoLocChon(JComboBox<Object> combo) {

        public String ma() {
            Object chon = this.combo.getSelectedItem();

            if (chon == null) {
                return null;
            }

            return chon instanceof Enum<?> e ? e.name() : chon.toString();
        }

        public Object giaTri() {
            return this.combo.getSelectedItem();
        }
    }

    /** Handle của bộ lọc khoảng ngày. */
    public record BoLocNgay(DatePicker tu, DatePicker den) {

        public LocalDate tuNgay() {
            return this.tu.getDate();
        }

        public LocalDate denNgay() {
            return this.den.getDate();
        }
    }

    /** Handle của bộ lọc khoảng nhập tay. */
    public record BoLocKhoang(JTextField tu, JTextField den) {

        public String tuText() {
            return this.tu.getText();
        }

        public String denText() {
            return this.den.getText();
        }
    }

    private final List<JComboBox<Object>> boLocCombo = new ArrayList<>();
    private final DefaultTableModel moHinhBang = new DefaultTableModel() {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable bang = new JTable(this.moHinhBang);
    private final CardLayout theDanhSach = new CardLayout();
    private final JPanel khungDanhSach = new JPanel(this.theDanhSach);

    private final JPanel khungBoLoc = new JPanel(new WrapLayout(FlowLayout.LEFT, 8, 2));
    private final List<JTextField> boLocText = new ArrayList<>();
    private final List<DatePicker[]> boLocNgay = new ArrayList<>();
    private final List<JLabel> nhanBoLoc = new ArrayList<>();
    private final JTextField oTimNhanh = new JTextField(20);
    private final JLabel nhanDem = new JLabel();
    private final JButton nutXoaLoc = new JButton(I18nReaderUtil.get("app.common.clearFilter"));
    private final JButton nutTruoc = new JButton(I18nReaderUtil.get("app.common.prev"));
    private final JButton nutSau = new JButton(I18nReaderUtil.get("app.common.next"));
    private final JLabel nhanTrang = new JLabel();

    private Runnable khiDoiBoLoc;
    private boolean dangXoaLoc;
    private final Timer henGioLoc = new Timer(300, e -> this.fireFilterChange());

    public ManagementPanel() {
        this.henGioLoc.setRepeats(false);

        this.setLayout(new BorderLayout(4, 4));
        this.setOpaque(false);

        this.bang.setFillsViewportHeight(true);
        this.bang.setRowHeight(36);

        Theme.styleTable(this.bang);
        Theme.applySecondary(this.nutXoaLoc);
        Theme.applySecondary(this.nutTruoc);
        Theme.applySecondary(this.nutSau);

        this.attachTextListener(this.oTimNhanh);

        JPanel listPanel = new JPanel(new BorderLayout(4, 4));
        listPanel.setOpaque(false);
        listPanel.add(this.buildFilterPanel(), BorderLayout.NORTH);
        listPanel.add(this.khungDanhSach, BorderLayout.CENTER);
        listPanel.add(this.buildPaginationPanel(), BorderLayout.SOUTH);

        this.khungDanhSach.setOpaque(false);
        this.khungDanhSach.add(new JScrollPane(this.bang), THE_BANG);
        this.khungDanhSach.add(this.buildEmptyLabel(), THE_TRONG);

        this.add(listPanel, BorderLayout.CENTER);
    }

    private JLabel buildEmptyLabel() {
        JLabel label = new JLabel(I18nReaderUtil.get("app.common.khongCoDuLieu"), SwingConstants.CENTER);
        label.setForeground(Theme.palette().INK_MUTED());
        return label;
    }

    private void hienTrangThaiDanhSach() {
        if (this.moHinhBang.getRowCount() == 0) {
            this.theDanhSach.show(this.khungDanhSach, THE_TRONG);
        } else {
            this.theDanhSach.show(this.khungDanhSach, THE_BANG);
        }
    }

    private JPanel buildFilterPanel() {
        this.khungBoLoc.setOpaque(false);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));
        actions.setOpaque(false);

        JLabel searchLabel = new JLabel(I18nReaderUtil.get("app.common.search"));
        searchLabel.setForeground(Theme.INK);
        actions.add(searchLabel);

        this.nhanDem.setForeground(Theme.INK);
        actions.add(this.oTimNhanh);
        actions.add(this.nhanDem);
        actions.add(this.nutXoaLoc);

        JPanel panel = new JPanel(new BorderLayout(4, 2));
        panel.setOpaque(false);
        panel.add(this.khungBoLoc, BorderLayout.CENTER);
        panel.add(actions, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildPaginationPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 2));
        panel.setOpaque(false);
        this.nhanTrang.setForeground(Theme.INK);
        panel.add(this.nutTruoc);
        panel.add(this.nhanTrang);
        panel.add(this.nutSau);
        return panel;
    }

    public JTextField addTextFilter(String labelKey) {
        JTextField field = new JTextField(14);
        this.boLocText.add(field);
        this.attachTextListener(field);
        this.khungBoLoc.add(this.buildFilterItem(labelKey, field));
        return field;
    }

    public BoLocChon addComboBoxFilter(String labelKey, Object[] values) {
        JComboBox<Object> combo = new JComboBox<>();
        combo.addItem(null); // Tất cả
        for (Object value : values) {
            combo.addItem(value);
        }
        combo.setRenderer(BoHienThiI18n.tao());
        this.boLocCombo.add(combo);
        combo.addActionListener(e -> this.fireFilterChange());
        this.khungBoLoc.add(this.buildFilterItem(labelKey, combo));
        return new BoLocChon(combo);
    }

    public BoLocNgay addDateRangeFilter(String labelKey) {
        DatePicker from = BoChonNgayGio.taoBoChonNgay();
        DatePicker to = BoChonNgayGio.taoBoChonNgay();
        this.boLocNgay.add(new DatePicker[] { from, to });
        this.attachDateListener(from);
        this.attachDateListener(to);

        this.khungBoLoc.add(this.buildRangeItem(labelKey, from, to));
        return new BoLocNgay(from, to);
    }

    public BoLocKhoang addRangeFilter(String labelKey) {
        JTextField fromField = new JTextField(14);
        JTextField toField = new JTextField(14);

        LocSoDocumentFilter locSo = new LocSoDocumentFilter();
        ((AbstractDocument) fromField.getDocument()).setDocumentFilter(locSo);
        ((AbstractDocument) toField.getDocument()).setDocumentFilter(locSo);

        this.boLocText.add(fromField);
        this.boLocText.add(toField);

        this.attachTextListener(fromField);
        this.attachTextListener(toField);

        this.khungBoLoc.add(this.buildRangeItem(labelKey, fromField, toField));
        return new BoLocKhoang(fromField, toField);
    }

    private JPanel buildRangeItem(String labelKey, JComponent from, JComponent to) {
        JPanel range = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        range.setOpaque(false);
        range.add(from);

        JLabel separator = new JLabel("-");
        separator.setForeground(Theme.palette().INK_MUTED());
        range.add(separator);

        range.add(to);
        return this.buildFilterItem(labelKey, range);
    }

    private JPanel buildFilterItem(String labelKey, JComponent field) {
        JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        item.setOpaque(false);
        item.add(this.buildFieldLabel(labelKey));
        item.add(field);
        return item;
    }

    private JLabel buildFieldLabel(String labelKey) {
        JLabel label = new JLabel(I18nReaderUtil.get(labelKey), SwingConstants.RIGHT);
        label.setForeground(Theme.INK);
        this.nhanBoLoc.add(label);
        this.updateFieldLabelWidth();
        return label;
    }

    private void updateFieldLabelWidth() {
        int width = 0;
        int height = 0;

        for (JLabel label : this.nhanBoLoc) {
            width = Math.max(width, label.getFontMetrics(label.getFont()).stringWidth(label.getText()));
            height = Math.max(height, label.getPreferredSize().height);
        }

        for (JLabel label : this.nhanBoLoc) {
            label.setPreferredSize(new Dimension(width, height));
        }
    }

    public String getQuickFilter() {
        return this.oTimNhanh.getText();
    }

    public JButton getClearFilterButton() {
        return this.nutXoaLoc;
    }

    public JButton getPrevButton() {
        return this.nutTruoc;
    }

    public JButton getNextButton() {
        return this.nutSau;
    }

    public void setFilterChangeListener(Runnable listener) {
        this.khiDoiBoLoc = listener;
    }

    public void clearFilters() {
        this.henGioLoc.stop();
        this.dangXoaLoc = true;

        try {
            this.boLocText.forEach(field -> field.setText(""));

            for (DatePicker[] pickers : this.boLocNgay) {
                pickers[0].clear();
                pickers[1].clear();
            }

            this.oTimNhanh.setText("");
            this.boLocCombo.forEach(combo -> combo.setSelectedIndex(0));
        } finally {
            this.dangXoaLoc = false;
        }

        this.fireFilterChange();
    }

    public void setCount(long from, long to, long total) {
        this.nhanDem.setText(I18nReaderUtil.get("app.common.count") + " " + from + "-" + to + " / " + total);
    }

    public void setPage(int page, int totalPages) {
        this.nhanTrang.setText(I18nReaderUtil.get("app.common.pageLabel") + " " + page + "/" + totalPages);
        this.nutTruoc.setEnabled(page > 1);
        this.nutSau.setEnabled(page < totalPages);
    }

    public JTable getTable() {
        return this.bang;
    }

    public DefaultTableModel getModel() {
        return this.moHinhBang;
    }

    public void setListTitle(String titleKey) {
        this.setBorder(Theme.titledBorder(titleKey));
    }

    public void setHeaders(String... headers) {
        this.moHinhBang.setColumnIdentifiers(headers);
        this.hienTrangThaiDanhSach();
    }

    public void addActionColumn(String headerKey, ThaoTac... dsThaoTac) {
        if (dsThaoTac == null || dsThaoTac.length == 0) {
            return;
        }

        this.moHinhBang.addColumn(I18nReaderUtil.get(headerKey));

        int cotModel = this.moHinhBang.getColumnCount() - 1;
        int cotView = this.bang.convertColumnIndexToView(cotModel);

        ThaoTacTableCellRenderer.ganVao(this.bang, cotView, List.of(dsThaoTac));
    }

    public void setRows(List<Object[]> rows) {
        this.moHinhBang.setRowCount(0);
        for (Object[] row : rows) {
            this.moHinhBang.addRow(row);
        }

        this.hienTrangThaiDanhSach();
    }

    public int getSelectedRow() {
        return this.bang.getSelectedRow();
    }

    private static final class LocSoDocumentFilter extends DocumentFilter {
        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
                throws BadLocationException {
            if (string == null) {
                return;
            }
            StringBuilder proposed = new StringBuilder(fb.getDocument().getText(0, fb.getDocument().getLength()));
            proposed.insert(offset, string);
            if (proposed.toString().matches("\\d*")) {
                super.insertString(fb, offset, string, attr);
            }
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                throws BadLocationException {
            StringBuilder proposed = new StringBuilder(fb.getDocument().getText(0, fb.getDocument().getLength()));
            proposed.replace(offset, offset + length, text == null ? "" : text);
            if (proposed.toString().matches("\\d*")) {
                super.replace(fb, offset, length, text, attrs);
            }
        }
    }

    private void attachTextListener(JTextField field) {
        field.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                ManagementPanel.this.henGioLoc.restart();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                ManagementPanel.this.henGioLoc.restart();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                ManagementPanel.this.henGioLoc.restart();
            }
        });
    }

    private void attachDateListener(DatePicker picker) {
        picker.addDateChangeListener(event -> this.henGioLoc.restart());
    }

    @Override
    public void removeNotify() {
        this.henGioLoc.stop();
        super.removeNotify();
    }

    private void fireFilterChange() {
        if (this.dangXoaLoc || this.khiDoiBoLoc == null) {
            return;
        }

        this.khiDoiBoLoc.run();
    }
}
