package iuh.fit.gui.component;

import com.github.lgooddatepicker.components.DatePicker;
import iuh.fit.enumeration.I18nEnum;
import iuh.fit.gui.theme.Theme;
import iuh.fit.util.I18nReaderUtil;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ManagementPanel extends JPanel {

    private final Map<Integer, JComboBox<Object>> comboFilters = new LinkedHashMap<>();
    private final DefaultTableModel model = new DefaultTableModel() {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable table = new JTable(this.model);

    private final JPanel filterFields = new JPanel(new WrapLayout(FlowLayout.LEFT, 8, 2));
    private final Map<Integer, JTextField> textFilters = new LinkedHashMap<>();
    private final Map<Integer, DatePicker[]> dateFilters = new LinkedHashMap<>();
    private final List<JLabel> filterLabels = new ArrayList<>();
    private final JTextField quickFilterField = new JTextField(12);
    private final JLabel countLabel = new JLabel();
    private final JButton clearFilterButton = new JButton(I18nReaderUtil.get("app.common.clearFilter"));
    private final JButton prevButton = new JButton(I18nReaderUtil.get("app.common.prev"));
    private final JButton nextButton = new JButton(I18nReaderUtil.get("app.common.next"));
    private final JLabel pageLabel = new JLabel();

    private Runnable filterChangeListener;
    private boolean dangXoaLoc;

    public ManagementPanel() {
        this.setLayout(new BorderLayout(4, 4));
        this.setOpaque(false);

        this.table.setFillsViewportHeight(true);
        this.table.setRowHeight(24);

        Theme.styleTable(this.table);

        this.attachTextListener(this.quickFilterField);

        JPanel listPanel = new JPanel(new BorderLayout(4, 4));
        listPanel.setOpaque(false);
        listPanel.add(this.buildFilterPanel(), BorderLayout.NORTH);
        listPanel.add(new JScrollPane(this.table), BorderLayout.CENTER);
        listPanel.add(this.buildPaginationPanel(), BorderLayout.SOUTH);

        this.add(listPanel, BorderLayout.CENTER);
    }

    private JPanel buildFilterPanel() {
        this.filterFields.setOpaque(false);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));
        actions.setOpaque(false);

        JLabel searchLabel = new JLabel(I18nReaderUtil.get("app.common.search"));
        searchLabel.setForeground(Theme.chuTrenAnh());
        actions.add(searchLabel);

        this.countLabel.setForeground(Theme.chuTrenAnh());
        actions.add(this.quickFilterField);
        actions.add(this.countLabel);
        actions.add(this.clearFilterButton);

        JPanel panel = new JPanel(new BorderLayout(4, 2));
        panel.setOpaque(false);
        panel.add(this.filterFields, BorderLayout.CENTER);
        panel.add(actions, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildPaginationPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 2));
        panel.setOpaque(false);
        this.pageLabel.setForeground(Theme.chuTrenAnh());
        panel.add(this.prevButton);
        panel.add(this.pageLabel);
        panel.add(this.nextButton);
        return panel;
    }

    public JTextField addTextFilter(String labelKey, int column) {
        JTextField field = new JTextField(10);
        this.textFilters.put(column, field);
        this.attachTextListener(field);
        this.filterFields.add(this.buildFilterItem(labelKey, field));
        return field;
    }

    public void addComboBoxFilter(String labelKey, int column, Object[] values) {

        JComboBox<Object> combo = new JComboBox<>();

        combo.addItem(null); // Tất cả

        for (Object value : values) {
            combo.addItem(value);
        }

        combo.setRenderer(taoBoHienThiI18n());

        comboFilters.put(column, combo);

        combo.addActionListener(e -> fireFilterChange());

        filterFields.add(buildFilterItem(labelKey, combo));
    }

    public String getComboFilter(int column) {
        JComboBox<Object> combo = comboFilters.get(column);

        if (combo == null || combo.getSelectedItem() == null) {
            return null;
        }

        Object selected = combo.getSelectedItem();

        return selected instanceof Enum<?> e ? e.name() : selected.toString();
    }

    public Object getComboFilterValue(int column) {
        JComboBox<Object> combo = this.comboFilters.get(column);
        return combo == null ? null : combo.getSelectedItem();
    }

    public void addDateRangeFilter(String labelKey, int column) {
        DatePicker from = BoChonNgayGio.taoBoChonNgay();
        DatePicker to = BoChonNgayGio.taoBoChonNgay();
        this.dateFilters.put(column, new DatePicker[] { from, to });
        this.attachDateListener(from);
        this.attachDateListener(to);

        this.filterFields.add(this.buildRangeItem(labelKey, from, to));
    }

    public void addRangeFilter(String labelKey, int column) {
        JTextField fromField = new JTextField(10);
        JTextField toField = new JTextField(10);

        this.textFilters.put(column, fromField);
        this.textFilters.put(column + 1, toField);

        this.attachTextListener(fromField);
        this.attachTextListener(toField);

        this.filterFields.add(this.buildRangeItem(labelKey, fromField, toField));
    }

    private JPanel buildRangeItem(String labelKey, JComponent from, JComponent to) {
        JPanel range = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        range.setOpaque(false);
        range.add(from);

        JLabel separator = new JLabel("-");
        separator.setForeground(Theme.chuTrenAnh());
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
        label.setForeground(Theme.chuTrenAnh());
        this.filterLabels.add(label);
        this.updateFieldLabelWidth();
        return label;
    }

    private void updateFieldLabelWidth() {
        int width = 0;
        int height = 0;

        for (JLabel label : this.filterLabels) {
            width = Math.max(width, label.getFontMetrics(label.getFont()).stringWidth(label.getText()));
            height = Math.max(height, label.getPreferredSize().height);
        }

        for (JLabel label : this.filterLabels) {
            label.setPreferredSize(new Dimension(width, height));
        }
    }

    public String getTextFilter(int column) {
        JTextField field = this.textFilters.get(column);
        return field == null ? null : field.getText();
    }

    public LocalDate getDateFilterFrom(int column) {
        DatePicker[] pickers = this.dateFilters.get(column);
        return pickers == null ? null : pickers[0].getDate();
    }

    public LocalDate getDateFilterTo(int column) {
        DatePicker[] pickers = this.dateFilters.get(column);
        return pickers == null ? null : pickers[1].getDate();
    }

    public String getQuickFilter() {
        return this.quickFilterField.getText();
    }

    public JButton getClearFilterButton() {
        return this.clearFilterButton;
    }

    public JButton getPrevButton() {
        return this.prevButton;
    }

    public JButton getNextButton() {
        return this.nextButton;
    }

    public void setFilterChangeListener(Runnable listener) {
        this.filterChangeListener = listener;
    }

    public void clearFilters() {
        this.dangXoaLoc = true;

        try {
            this.textFilters.values().forEach(field -> field.setText(""));

            for (DatePicker[] pickers : this.dateFilters.values()) {
                pickers[0].clear();
                pickers[1].clear();
            }

            this.quickFilterField.setText("");
            this.comboFilters.values().forEach(combo -> combo.setSelectedIndex(0));
        } finally {
            this.dangXoaLoc = false;
        }

        this.fireFilterChange();
    }

    public void setCount(long from, long to, long total) {
        this.countLabel.setText(I18nReaderUtil.get("app.common.count") + " " + from + "-" + to + " / " + total);
    }

    public void setPage(int page, int totalPages) {
        this.pageLabel.setText(I18nReaderUtil.get("app.common.pageLabel") + " " + page + "/" + totalPages);
        this.prevButton.setEnabled(page > 1);
        this.nextButton.setEnabled(page < totalPages);
    }

    public JTable getTable() {
        return this.table;
    }

    public DefaultTableModel getModel() {
        return this.model;
    }

    public void setListTitle(String titleKey) {
        TitledBorder border = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Theme.BORDER), I18nReaderUtil.get(titleKey));
        border.setTitleFont(border.getTitleFont().deriveFont(Font.BOLD));
        border.setTitleColor(Theme.chuTrenAnh());
        this.setBorder(border);
    }

    public static DefaultListCellRenderer taoBoHienThiI18n() {
        return new DefaultListCellRenderer() {
            private static final long serialVersionUID = 1L;

            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {

                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                if (value == null) {
                    setText(I18nReaderUtil.get("app.common.all"));
                } else if (value instanceof I18nEnum i18nEnum) {
                    setText(i18nEnum.getI18NBusinessName());
                }

                return this;
            }
        };
    }

    public void setHeaders(String... headers) {
        this.model.setColumnIdentifiers(headers);
    }

    public void setRows(List<Object[]> rows) {
        this.model.setRowCount(0);
        for (Object[] row : rows) {
            this.model.addRow(row);
        }
    }

    public int getSelectedRow() {
        return this.table.getSelectedRow();
    }

    private void attachTextListener(JTextField field) {
        field.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                fireFilterChange();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                fireFilterChange();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                fireFilterChange();
            }
        });
    }

    private void attachDateListener(DatePicker picker) {
        picker.addDateChangeListener(event -> fireFilterChange());
    }

    private void fireFilterChange() {
        if (this.dangXoaLoc || this.filterChangeListener == null) {
            return;
        }

        this.filterChangeListener.run();
    }
}
