package iuh.fit.gui.page;

import com.github.lgooddatepicker.components.DatePicker;

import iuh.fit.entity.NhanVien;
import iuh.fit.entity.PhanCong;
import iuh.fit.enumeration.CaLamViec;
import iuh.fit.enumeration.MenuGroup;
import iuh.fit.gui.component.BoChonNgayGio;
import iuh.fit.gui.component.BoHienThiI18n;
import iuh.fit.gui.component.ManagementFormPanel;
import iuh.fit.gui.component.ManagementPanel;
import iuh.fit.service.PhanCongService;
import iuh.fit.util.I18nReaderUtil;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JSplitPane;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import java.awt.BorderLayout;
import java.awt.Component;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class PhanCongCaPage extends AbstractPage {

	private static final long serialVersionUID = 1L;
	private static final int PAGE_SIZE = 20;

	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

	private ManagementPanel managementPanel;
	private ManagementFormPanel managementFormPanel;

	private final JTextField txtMaPhanCong = new JTextField(14);
	private final JComboBox<NhanVien> cboNhanVien = new JComboBox<>();
	private final DatePicker dpNgayLam = BoChonNgayGio.taoBoChonNgay();
	private final JComboBox<CaLamViec> cboCaLam = new JComboBox<>(CaLamViec.values());
	private final JTextField txtGhiChu = new JTextField(14);

	private final PhanCongService phanCongService = new PhanCongService();

	private JTextField boLocMa;
	private JTextField boLocNhanVien;
	private ManagementPanel.BoLocNgay boLocNgay;
	private ManagementPanel.BoLocChon boLocCa;
	private JTextField boLocGhiChu;

	private int trangHienTai = 1;

	public PhanCongCaPage() {
		this.setLayout(new BorderLayout());
		this.dpNgayLam.getSettings().setFormatForDatesCommonEra("dd/MM/yyyy");
	}

	@Override
	public void render() {
		this.removeAll();
		this.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

		this.managementPanel = new ManagementPanel();
		this.managementFormPanel = new ManagementFormPanel("app.schedule.assignmentId");

		this.khoiTaoForm();
		this.taiDanhSachNhanVien();
		this.khoiTaoDanhSach();
		this.ganSuKien();

		JSplitPane noiDung = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, this.managementFormPanel,
				this.managementPanel);

		noiDung.setResizeWeight(0.35);
		noiDung.setDividerLocation(480);
		noiDung.setContinuousLayout(true);
		noiDung.setBorder(null);

		this.add(noiDung, BorderLayout.CENTER);

		this.trangHienTai = 1;
		this.taiLaiDanhSach();

		this.revalidate();
		this.repaint();
	}

	private void khoiTaoForm() {
		this.txtMaPhanCong.setEditable(false);
		this.txtMaPhanCong.setFocusable(false);

		this.cboCaLam.setRenderer(BoHienThiI18n.tao());

		this.managementFormPanel.addField("app.schedule.assignmentId", this.txtMaPhanCong);
		this.cboNhanVien.setRenderer(new DefaultListCellRenderer() {
			private static final long serialVersionUID = 1L;

			@Override
			public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
					boolean cellHasFocus) {

				String hienThi = "";

				if (value instanceof NhanVien nhanVien) {
					hienThi = nhanVien.getHoTen() + " (" + nhanVien.getMaNhanVien() + ")";
				}

				return super.getListCellRendererComponent(list, hienThi, index, isSelected, cellHasFocus);
			}
		});

		this.managementFormPanel.addField("app.schedule.employee", this.cboNhanVien);
		this.managementFormPanel.addField("app.schedule.date", this.dpNgayLam);
		this.managementFormPanel.addField("app.schedule.shift", this.cboCaLam);
		this.managementFormPanel.addField("app.schedule.note", this.txtGhiChu);
	}

	private void khoiTaoDanhSach() {
		this.managementPanel.setListTitle("app.schedule.assignmentList");

		this.managementPanel.setHeaders(I18nReaderUtil.get("app.schedule.assignmentId"),
				I18nReaderUtil.get("app.schedule.employee"), I18nReaderUtil.get("app.schedule.date"),
				I18nReaderUtil.get("app.schedule.shift"), I18nReaderUtil.get("app.schedule.note"));

		this.boLocMa = this.managementPanel.addTextFilter("app.schedule.assignmentId");

		this.boLocNhanVien = this.managementPanel.addTextFilter("app.schedule.employee");

		this.boLocNgay = this.managementPanel.addDateRangeFilter("app.schedule.date");

		this.boLocCa = this.managementPanel.addComboBoxFilter("app.schedule.shift", CaLamViec.values());

		this.boLocGhiChu = this.managementPanel.addTextFilter("app.schedule.note");

		this.managementPanel.setFilterChangeListener(() -> {
			this.trangHienTai = 1;
			this.taiLaiDanhSach();
		});

		JTable bang = this.managementPanel.getTable();

		DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
			private static final long serialVersionUID = 1L;

			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {

				Object giaTriHienThi = value;

				if (value instanceof LocalDate ngay) {
					giaTriHienThi = ngay.format(DATE_FORMATTER);
				}

				return super.getTableCellRendererComponent(table, giaTriHienThi, isSelected, hasFocus, row, column);
			}
		};

		bang.getColumnModel().getColumn(2).setCellRenderer(renderer);
	}

	private void ganSuKien() {
		this.managementFormPanel.getNutThem().addActionListener(e -> this.themVaoBang());

		this.managementFormPanel.getNutXoaTrang().addActionListener(e -> this.lamMoiForm());

		this.managementFormPanel.getNutLuu().addActionListener(e -> this.luu());

		this.managementFormPanel.getNutXoa().addActionListener(e -> this.xoa());

		this.managementPanel.getClearFilterButton().addActionListener(e -> this.managementPanel.clearFilters());

		this.managementPanel.getPrevButton().addActionListener(e -> {
			if (this.trangHienTai > 1) {
				this.trangHienTai--;
				this.taiLaiDanhSach();
			}
		});

		this.managementPanel.getNextButton().addActionListener(e -> {
			this.trangHienTai++;
			this.taiLaiDanhSach();
		});

		JTable bang = this.managementPanel.getTable();

		bang.getSelectionModel().addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting()) {
				this.chonDong();
			}
		});
	}

	private void themVaoBang() {
	    NhanVien nhanVien = (NhanVien) this.cboNhanVien.getSelectedItem();

	    if (nhanVien == null) {
	        JOptionPane.showMessageDialog(
	                this,
	                I18nReaderUtil.get("app.schedule.employeeRequired"),
	                I18nReaderUtil.get("app.title"),
	                JOptionPane.WARNING_MESSAGE);
	        return;
	    }

	    LocalDate ngayLam = this.dpNgayLam.getDate();
	    CaLamViec caLamViec = (CaLamViec) this.cboCaLam.getSelectedItem();
	    String ghiChu = this.txtGhiChu.getText();

	    try {
	        this.phanCongService.create(
	                nhanVien.getMaNhanVien(),
	                ngayLam,
	                caLamViec,
	                ghiChu);

	        JOptionPane.showMessageDialog(
	                this,
	                I18nReaderUtil.get("app.schedule.assignmentCreated"),
	                I18nReaderUtil.get("app.title"),
	                JOptionPane.INFORMATION_MESSAGE);

	        this.lamMoiForm();
	        this.trangHienTai = 1;
	        this.taiLaiDanhSach();

	    } catch (IllegalArgumentException | IllegalStateException ex) {
	        JOptionPane.showMessageDialog(
	                this,
	                I18nReaderUtil.get(ex.getMessage()),
	                I18nReaderUtil.get("app.title"),
	                JOptionPane.WARNING_MESSAGE);

	    } catch (SQLException ex) {
	        JOptionPane.showMessageDialog(
	                this,
	                I18nReaderUtil.get("app.common.loiKhongXacDinh"),
	                I18nReaderUtil.get("app.title"),
	                JOptionPane.ERROR_MESSAGE);
	    }
	}

	private void taiLaiDanhSach() {
	    try {
	        Object giaTriCa = this.boLocCa.giaTri();
	        CaLamViec caLamViec =
	                giaTriCa instanceof CaLamViec ca ? ca : null;

	        List<PhanCong> danhSach = this.phanCongService.search(
	                this.boLocMa.getText(),
	                this.boLocNhanVien.getText(),
	                this.boLocNgay.tuNgay(),
	                this.boLocNgay.denNgay(),
	                caLamViec,
	                this.boLocGhiChu.getText(),
	                this.managementPanel.getQuickFilter());

	        int tongSoDong = danhSach.size();
	        int tongSoTrang = Math.max(
	                1,
	                (int) Math.ceil((double) tongSoDong / PAGE_SIZE));

	        if (this.trangHienTai > tongSoTrang) {
	            this.trangHienTai = tongSoTrang;
	        }

	        if (this.trangHienTai < 1) {
	            this.trangHienTai = 1;
	        }

	        int viTriBatDau = (this.trangHienTai - 1) * PAGE_SIZE;
	        int viTriKetThuc = Math.min(
	                viTriBatDau + PAGE_SIZE,
	                tongSoDong);

	        List<PhanCong> trangDuLieu = tongSoDong == 0
	                ? List.of()
	                : danhSach.subList(viTriBatDau, viTriKetThuc);

	        java.util.Map<String, String> tenNhanVienTheoMa =
	                new java.util.HashMap<>();

	        for (int i = 0; i < this.cboNhanVien.getItemCount(); i++) {
	            NhanVien nhanVien = this.cboNhanVien.getItemAt(i);

	            tenNhanVienTheoMa.put(
	                    nhanVien.getMaNhanVien(),
	                    nhanVien.getHoTen() == null
	                            ? ""
	                            : nhanVien.getHoTen());
	        }

	        List<Object[]> duLieu = new ArrayList<>();

	        for (PhanCong phanCong : trangDuLieu) {
	            LocalDate ngayLam = LocalDate.of(
	                    phanCong.getNam(),
	                    phanCong.getThang(),
	                    phanCong.getNgay());

	            duLieu.add(new Object[] {
	                    phanCong.getMaPhanCong(),
	                    tenNhanVienTheoMa.getOrDefault(
	                            phanCong.getMaNhanVien(),
	                            phanCong.getMaNhanVien()),
	                    ngayLam,
	                    phanCong.getCaLamViec(),
	                    phanCong.getGhiChu() == null
	                            ? ""
	                            : phanCong.getGhiChu()
	            });
	        }

	        this.managementPanel.setRows(duLieu);

	        long tuDong = tongSoDong == 0
	                ? 0
	                : viTriBatDau + 1L;
	        long denDong = viTriKetThuc;

	        this.managementPanel.setCount(
	                tuDong,
	                denDong,
	                tongSoDong);

	        this.managementPanel.setPage(
	                this.trangHienTai,
	                tongSoTrang);

	    } catch (SQLException ex) {
	        JOptionPane.showMessageDialog(
	                this,
	                I18nReaderUtil.get("app.common.loiKhongXacDinh"),
	                I18nReaderUtil.get("app.title"),
	                JOptionPane.ERROR_MESSAGE);
	    }
	}

	private void chonDong() {
		JTable bang = this.managementPanel.getTable();
		int dong = bang.getSelectedRow();

		if (dong < 0) {
			return;
		}

		int dongModel = bang.convertRowIndexToModel(dong);
		DefaultTableModel model = this.managementPanel.getModel();

		Object maPhanCong = model.getValueAt(dongModel, 0);

		if (maPhanCong == null) {
			return;
		}

		try {
			Optional<PhanCong> ketQua = this.phanCongService.getById(maPhanCong.toString());

			if (ketQua.isEmpty()) {
				return;
			}

			PhanCong phanCong = ketQua.get();

			this.txtMaPhanCong.setText(phanCong.getMaPhanCong());

			this.cboNhanVien.setSelectedIndex(-1);

			for (int i = 0; i < this.cboNhanVien.getItemCount(); i++) {
			    NhanVien nhanVien = this.cboNhanVien.getItemAt(i);

			    if (nhanVien.getMaNhanVien().equals(
			            phanCong.getMaNhanVien())) {
			        this.cboNhanVien.setSelectedIndex(i);
			        break;
			    }
			}

			this.dpNgayLam.setDate(LocalDate.of(phanCong.getNam(), phanCong.getThang(), phanCong.getNgay()));

			this.cboCaLam.setSelectedItem(phanCong.getCaLamViec());

			this.txtGhiChu.setText(phanCong.getGhiChu() == null ? "" : phanCong.getGhiChu());

		} catch (SQLException ex) {
			JOptionPane.showMessageDialog(this, "Không thể tải thông tin phân công: " + ex.getMessage(),
					I18nReaderUtil.get("app.title"), JOptionPane.ERROR_MESSAGE);
		}
	}

	private void luu() {
	    String maPhanCong = this.txtMaPhanCong.getText().trim();

	    if (maPhanCong.isEmpty()) {
	        JOptionPane.showMessageDialog(
	                this,
	                I18nReaderUtil.get("app.schedule.employeeRequired"),
	                I18nReaderUtil.get("app.title"),
	                JOptionPane.WARNING_MESSAGE);
	        return;
	    }

	    NhanVien nhanVien =
	            (NhanVien) this.cboNhanVien.getSelectedItem();

	    if (nhanVien == null) {
	        JOptionPane.showMessageDialog(
	                this,
	                I18nReaderUtil.get("app.schedule.employeeRequired"),
	                I18nReaderUtil.get("app.title"),
	                JOptionPane.WARNING_MESSAGE);
	        return;
	    }

	    LocalDate ngayLam = this.dpNgayLam.getDate();
	    CaLamViec caLamViec =
	            (CaLamViec) this.cboCaLam.getSelectedItem();
	    String ghiChu = this.txtGhiChu.getText();

	    try {
	        this.phanCongService.update(
	                maPhanCong,
	                nhanVien.getMaNhanVien(),
	                ngayLam,
	                caLamViec,
	                ghiChu);

	        JOptionPane.showMessageDialog(
	                this,
	                I18nReaderUtil.get("app.schedule.assignmentUpdated"),
	                I18nReaderUtil.get("app.title"),
	                JOptionPane.INFORMATION_MESSAGE);

	        this.lamMoiForm();
	        this.trangHienTai = 1;
	        this.taiLaiDanhSach();

	    } catch (IllegalArgumentException | IllegalStateException ex) {
	        JOptionPane.showMessageDialog(
	                this,
	                I18nReaderUtil.get(ex.getMessage()),
	                I18nReaderUtil.get("app.title"),
	                JOptionPane.WARNING_MESSAGE);

	    } catch (SQLException ex) {
	        JOptionPane.showMessageDialog(
	                this,
	                I18nReaderUtil.get("app.common.loiKhongXacDinh"),
	                I18nReaderUtil.get("app.title"),
	                JOptionPane.ERROR_MESSAGE);
	    }
	}

	private void xoa() {
		String maPhanCong = this.txtMaPhanCong.getText().trim();

		if (maPhanCong.isEmpty()) {
			JOptionPane.showMessageDialog(this, I18nReaderUtil.get("app.schedule.assignmentRequired"),
					I18nReaderUtil.get("app.title"), JOptionPane.WARNING_MESSAGE);
			return;
		}

		int luaChon = JOptionPane.showConfirmDialog(this, I18nReaderUtil.get("app.schedule.confirmDelete"),
				I18nReaderUtil.get("app.title"), JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

		if (luaChon != JOptionPane.YES_OPTION) {
			return;
		}

		try {
			this.phanCongService.delete(maPhanCong);

			JOptionPane.showMessageDialog(this, I18nReaderUtil.get("app.schedule.assignmentDeleted"),
					I18nReaderUtil.get("app.title"), JOptionPane.INFORMATION_MESSAGE);

			this.lamMoiForm();
			this.taiLaiDanhSach();

		} catch (IllegalArgumentException | IllegalStateException ex) {
			JOptionPane.showMessageDialog(this, I18nReaderUtil.get(ex.getMessage()), I18nReaderUtil.get("app.title"),
					JOptionPane.WARNING_MESSAGE);

		} catch (SQLException ex) {
			ex.printStackTrace();

			JOptionPane.showMessageDialog(this, ex.getMessage(), I18nReaderUtil.get("app.title"),
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void taiDanhSachNhanVien() {
	    try {
	        List<NhanVien> danhSach =
	                this.phanCongService.getDanhSachNhanVien();

	        this.cboNhanVien.removeAllItems();

	        for (NhanVien nhanVien : danhSach) {
	            this.cboNhanVien.addItem(nhanVien);
	        }
	    } catch (SQLException ex) {
	        JOptionPane.showMessageDialog(
	                this,
	                I18nReaderUtil.get("app.common.loiKhongXacDinh"),
	                I18nReaderUtil.get("app.title"),
	                JOptionPane.ERROR_MESSAGE);
	    }
	}

	private void lamMoiForm() {
		this.txtMaPhanCong.setText("");
		if (this.cboNhanVien.getItemCount() > 0) {
			this.cboNhanVien.setSelectedIndex(0);
		}
		this.dpNgayLam.clear();
		this.cboCaLam.setSelectedIndex(0);
		this.txtGhiChu.setText("");

		this.managementPanel.getTable().clearSelection();
	}

	@Override
	public MenuGroup getGroup() {
		return MenuGroup.KE_HOACH;
	}

	@Override
	public String getTitleKey() {
		return "app.menu.shiftAssignment";
	}
}