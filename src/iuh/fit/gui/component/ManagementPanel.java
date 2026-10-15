package iuh.fit.gui.component;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import iuh.fit.core.context.AppContext;
import iuh.fit.gui.theme.Theme;
import iuh.fit.util.I18nReaderUtil;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ManagementPanel extends JPanel {

	private final JPanel formPanel = new JPanel(new BorderLayout());
	private final JPanel formFields = new JPanel();
	private final Map<Integer, JComboBox<Object>> comboFilters = new LinkedHashMap<>();
	private final DefaultTableModel model = new DefaultTableModel() {
		@Override
		public boolean isCellEditable(int row, int column) {
			return false;
		}
	};
	private final JTable table = new JTable(this.model);
	private final JButton themButton = new JButton(I18nReaderUtil.get("app.common.add"));
	private final JButton luuButton = new JButton(I18nReaderUtil.get("app.common.save"));
	private final JButton xoaTrangButton = new JButton(I18nReaderUtil.get("app.common.clear"));
	private final JButton xoaButton = new JButton(I18nReaderUtil.get("app.common.delete"));

	private final JPanel filterFields = new JPanel(new WrapLayout(FlowLayout.LEFT, 8, 2));
	private final Map<Integer, JTextField> textFilters = new LinkedHashMap<>();
	private final Map<Integer, DatePicker[]> dateFilters = new LinkedHashMap<>();
	private final List<JTextField> textInputs = new ArrayList<>();
	private final List<DatePicker> datePickers = new ArrayList<>();
	private final JTextField quickFilterField = new JTextField(12);
	private final JLabel countLabel = new JLabel();
	private final JButton clearFilterButton = new JButton(I18nReaderUtil.get("app.common.clearFilter"));
	private final JButton prevButton = new JButton(I18nReaderUtil.get("app.common.prev"));
	private final JButton nextButton = new JButton(I18nReaderUtil.get("app.common.next"));
	private final JLabel pageLabel = new JLabel();

	private Runnable filterChangeListener;

	public ManagementPanel() {
		this.setLayout(new BorderLayout(4, 4));
		this.setBackground(Theme.BG);

		this.formFields.setLayout(new BoxLayout(this.formFields, BoxLayout.Y_AXIS));
		this.formFields.setBackground(Theme.PANEL);
		this.formFields.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));

		this.formPanel.setBackground(Theme.PANEL);
		this.formPanel.setBorder(BorderFactory.createLineBorder(Theme.BORDER));
		this.formPanel.setPreferredSize(new Dimension(380, 1));
		this.formPanel.add(this.formFields, BorderLayout.NORTH);
		this.formPanel.add(this.buildFooter(), BorderLayout.SOUTH);
		this.table.setFillsViewportHeight(true);
		this.table.setRowHeight(24);

		JPanel listPanel = new JPanel(new BorderLayout(4, 4));
		listPanel.setBackground(Theme.BG);
		listPanel.add(this.buildFilterPanel(), BorderLayout.NORTH);
		listPanel.add(new JScrollPane(this.table), BorderLayout.CENTER);
		listPanel.add(this.buildPaginationPanel(), BorderLayout.SOUTH);

		this.add(this.formPanel, BorderLayout.WEST);
		this.add(listPanel, BorderLayout.CENTER);
	}

	private JPanel buildFooter() {
		JPanel footer = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));
		footer.setBackground(Theme.PANEL);
		footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Theme.BORDER));
		footer.add(this.themButton);
		footer.add(this.luuButton);
		footer.add(this.xoaTrangButton);
		footer.add(this.xoaButton);
		return footer;
	}

	private JPanel buildFilterPanel() {
		this.filterFields.setBackground(Theme.BG);

		JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));
		actions.setBackground(Theme.BG);
		actions.add(new JLabel(I18nReaderUtil.get("app.common.search")));
		actions.add(this.quickFilterField);
		actions.add(this.countLabel);
		actions.add(this.clearFilterButton);

		JPanel panel = new JPanel(new BorderLayout(4, 2));
		panel.setBackground(Theme.BG);
		panel.add(this.filterFields, BorderLayout.CENTER);
		panel.add(actions, BorderLayout.SOUTH);
		return panel;
	}

	private JPanel buildPaginationPanel() {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 2));
		panel.setBackground(Theme.BG);
		panel.add(this.prevButton);
		panel.add(this.pageLabel);
		panel.add(this.nextButton);
		return panel;
	}

	public JTextField addTextFilter(String labelKey, int column) {
		JTextField field = new JTextField(10);
		this.textFilters.put(column, field);
		this.textInputs.add(field);
		this.filterFields.add(this.buildFilterItem(labelKey, field));
		return field;
	}

	public void addComboBoxFilter(String labelKey, int column, Object[] values) {

		JComboBox<Object> combo = new JComboBox<>();

		combo.addItem(null); // Tất cả

		for (Object value : values) {
			combo.addItem(value);
		}

		combo.setRenderer(new javax.swing.DefaultListCellRenderer() {
			private static final long serialVersionUID = 1L;

			@Override
			public Component getListCellRendererComponent(JList<?> list, Object value, int index,
					boolean isSelected, boolean cellHasFocus) {

				super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

				if (value == null) {
					setText("Tất cả");
				} else if (value instanceof Enum<?> e) {
					String key = "app.enum." + e.getClass().getSimpleName() + "." + e.name();

					String translated = I18nReaderUtil.get(key);
					setText(translated == null || translated.equals(key) ? e.name() : translated);
				}

				return this;
			}
		});

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

	public void addDateRangeFilter(String labelKey, int column) {
		DatePicker from = this.taoBoChonNgay();
		DatePicker to = this.taoBoChonNgay();
		this.dateFilters.put(column, new DatePicker[] { from, to });
		this.datePickers.add(from);
		this.datePickers.add(to);

		JPanel range = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
		range.setOpaque(false);
		range.add(from);
		range.add(new JLabel("-"));
		range.add(to);
		this.filterFields.add(this.buildFilterItem(labelKey, range));
	}

	public void addRangeFilter(String labelKey, int column) {
		JTextField fromField = new JTextField(10);
		JTextField toField = new JTextField(10);

		this.textFilters.put(column, fromField);
		this.textFilters.put(column + 1, toField);

		this.textInputs.add(fromField);
		this.textInputs.add(toField);

		JPanel rangePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
		rangePanel.setOpaque(false);

		rangePanel.add(fromField);
		rangePanel.add(new JLabel("-"));
		rangePanel.add(toField);

		this.filterFields.add(this.buildFilterItem(labelKey, rangePanel));
	}

	private DatePicker taoBoChonNgay() {
		Locale locale = AppContext.getInstance().getLocale().getLocale();
		DatePickerSettings settings = new DatePickerSettings(locale);
		settings.setFormatForDatesCommonEra(I18nReaderUtil.get("app.date.format"));
		settings.setTranslationToday(I18nReaderUtil.get("app.date.today"));
		settings.setTranslationClear(I18nReaderUtil.get("app.date.clear"));
		settings.setAllowKeyboardEditing(false);
		return new DatePicker(settings);
	}

	private JPanel buildFilterItem(String labelKey, JComponent field) {
		JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
		item.setOpaque(false);
		item.add(new JLabel(I18nReaderUtil.get(labelKey)));
		item.add(field);
		return item;
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
		for (JTextField field : this.textInputs) {
			this.attachTextListener(field);
		}
		for (DatePicker picker : this.datePickers) {
			this.attachDateListener(picker);
		}
		this.attachTextListener(this.quickFilterField);
	}

	public void clearFilters() {
		this.textFilters.values().forEach(field -> field.setText(""));
		this.datePickers.forEach(DatePicker::clear);
		this.quickFilterField.setText("");
		this.comboFilters.values().forEach(
		        combo -> combo.setSelectedIndex(0)
		);
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

	public JPanel getFormPanel() {
		return this.formFields;
	}

	public JButton getThemButton() {
		return this.themButton;
	}

	public JButton getLuuButton() {
		return this.luuButton;
	}

	public JButton getXoaTrangButton() {
		return this.xoaTrangButton;
	}

	public JButton getXoaButton() {
		return this.xoaButton;
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
		if (this.filterChangeListener != null) {
			this.filterChangeListener.run();
		}
	}
}
