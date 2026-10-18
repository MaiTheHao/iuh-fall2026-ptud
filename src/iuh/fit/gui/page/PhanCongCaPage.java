
package iuh.fit.gui.page;

import com.github.lgooddatepicker.components.DatePicker;

import iuh.fit.enumeration.CaLamViec;
import iuh.fit.enumeration.MenuGroup;
import iuh.fit.gui.component.ManagementPanel;
import iuh.fit.gui.component.ThongBao;
import iuh.fit.util.I18nReaderUtil;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableModel;

import java.awt.BorderLayout;
import java.awt.Component;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class PhanCongCaPage extends AbstractPage {

	private static final long serialVersionUID = 1L;

	private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

	private ManagementPanel managementPanel;

	private final JTextField txtMaPhanCong = new JTextField(14);
	private final JTextField txtNhanVien = new JTextField(14);
	private final DatePicker dpNgayLam = new DatePicker();
	private final JComboBox<CaLamViec> cboCaLam = new JComboBox<>(CaLamViec.values());
	private final JTextField txtGhiChu = new JTextField(14);

	public PhanCongCaPage() {
		this.setLayout(new BorderLayout());
	}

	@Override
	public void render() {
		this.removeAll();
		this.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

		this.managementPanel = new ManagementPanel();

		this.managementPanel.setHeaders(I18nReaderUtil.get("app.schedule.assignmentId"),
				I18nReaderUtil.get("app.schedule.employee"), I18nReaderUtil.get("app.schedule.date"),
				I18nReaderUtil.get("app.schedule.shift"), I18nReaderUtil.get("app.schedule.note"));

		this.khoiTaoForm();
		this.khoiTaoBoLoc();
		this.ganSuKien();

		this.add(this.managementPanel, BorderLayout.CENTER);

		this.taiLaiDanhSach();

		this.revalidate();
		this.repaint();
	}

	private void khoiTaoForm() {
		JPanel form = this.managementPanel.getFormPanel();
		form.removeAll();

		this.txtMaPhanCong.setEditable(false);
		this.txtMaPhanCong.setFocusable(false);

		this.cauHinhComboCaLam();

		this.dpNgayLam.getSettings().setFormatForDatesCommonEra(dateFormatter);

		this.managementPanel.addFormRow("app.schedule.assignmentId", this.txtMaPhanCong);
		this.managementPanel.addFormRow("app.schedule.employee", this.txtNhanVien);
		this.managementPanel.addFormRow("app.schedule.date", this.dpNgayLam);
		this.managementPanel.addFormRow("app.schedule.shift", this.cboCaLam);
		this.managementPanel.addFormRow("app.schedule.note", this.txtGhiChu);

		form.revalidate();
		form.repaint();

		this.lamMoiForm();
	}

	private void cauHinhComboCaLam() {
		this.cboCaLam.setRenderer(new DefaultListCellRenderer() {
			private static final long serialVersionUID = 1L;

			@Override
			public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
					boolean cellHasFocus) {

				super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

				if (value instanceof CaLamViec caLamViec) {
					this.setText(caLamViec.getI18NBusinessName());
				}

				return this;
			}
		});
	}

	private void khoiTaoBoLoc() {
		this.managementPanel.addTextFilter("app.schedule.assignmentId", 0);

		this.managementPanel.addTextFilter("app.schedule.employee", 1);

		this.managementPanel.addDateRangeFilter("app.schedule.date", 2);

		this.managementPanel.addComboBoxFilter("app.schedule.shift", 3, CaLamViec.values());

		this.managementPanel.addTextFilter("app.schedule.note", 4);

		this.managementPanel.setFilterChangeListener(this::taiLaiDanhSach);
	}

	private void ganSuKien() {
		this.managementPanel.getThemButton().addActionListener(e -> this.lamMoiForm());

		this.managementPanel.getXoaTrangButton().addActionListener(e -> this.lamMoiForm());

		this.managementPanel.getLuuButton().addActionListener(e -> this.luu());

		this.managementPanel.getXoaButton().addActionListener(e -> this.xoa());

		this.managementPanel.getClearFilterButton().addActionListener(e -> {
			this.managementPanel.clearFilters();
			this.taiLaiDanhSach();
		});

		this.managementPanel.getTable().getSelectionModel().addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting()) {
				this.chonDong();
			}
		});
	}

	private void taiLaiDanhSach() {
		List<Object[]> rows = new ArrayList<>();

		this.managementPanel.setRows(rows);
		this.managementPanel.setCount(0, 0, 0);
		this.managementPanel.setPage(1, 1);
	}

	private void chonDong() {
		JTable table = this.managementPanel.getTable();
		int selectedRow = table.getSelectedRow();

		if (selectedRow < 0) {
			return;
		}

		int modelRow = table.convertRowIndexToModel(selectedRow);
		TableModel model = table.getModel();

		this.txtMaPhanCong.setText(String.valueOf(model.getValueAt(modelRow, 0)));

		this.txtNhanVien.setText(String.valueOf(model.getValueAt(modelRow, 1)));

		Object ngayLam = model.getValueAt(modelRow, 2);

		if (ngayLam == null || ngayLam.toString().isBlank()) {
			this.dpNgayLam.clear();
		} else {
			this.dpNgayLam.setDate(LocalDate.parse(ngayLam.toString(),
					DateTimeFormatter.ofPattern(I18nReaderUtil.get("app.date.format"))));
		}

		this.txtGhiChu
				.setText(model.getValueAt(modelRow, 4) == null ? "" : String.valueOf(model.getValueAt(modelRow, 4)));
	}

	private void luu() {
		if (this.txtNhanVien.getText().isBlank() || this.dpNgayLam.getDate() == null
				|| this.cboCaLam.getSelectedItem() == null) {
			ThongBao.loi(this, "app.schedule.requiredFields");
			return;
		}

		ThongBao.loi(this, "app.schedule.serviceNotConnected");
	}

	private void xoa() {
		if (this.txtMaPhanCong.getText().isBlank()) {
			this.lamMoiForm();
			return;
		}

		ThongBao.loi(this, "app.schedule.serviceNotConnected");
	}

	private void lamMoiForm() {
		this.txtMaPhanCong.setText("");
		this.txtNhanVien.setText("");
		this.dpNgayLam.clear();
		this.txtGhiChu.setText("");

		if (this.cboCaLam.getItemCount() > 0) {
			this.cboCaLam.setSelectedIndex(0);
		}

		if (this.managementPanel != null) {
			this.managementPanel.getTable().clearSelection();
		}
	}

	@Override
	public String getTitleKey() {
		return "app.menu.shiftAssignment";
	}

	@Override
	public MenuGroup getGroup() {
		return MenuGroup.KE_HOACH;
	}

}
