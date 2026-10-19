package iuh.fit.gui.component;

import iuh.fit.entity.PhuTung;
import iuh.fit.util.I18nReaderUtil;
import iuh.fit.util.TextNormalizerUtil;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class ChonPhuTungDialog extends JDialog {

	private final List<PhuTung> phuTungs;
	private final List<PhuTung> filtered = new ArrayList<>();
	private final JTextField searchField = new JTextField();
	private final JLabel statusLabel = new JLabel();
	private final JSpinner spinner = new JSpinner(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));
	private final DefaultTableModel model = new DefaultTableModel(new Object[] {
			I18nReaderUtil.get("app.phuTung.ma"),
			I18nReaderUtil.get("app.phuTung.ten"),
			I18nReaderUtil.get("app.phuTung.soLuongTon"),
			I18nReaderUtil.get("app.phieuNhap.donGia") }, 0) {
		@Override
		public boolean isCellEditable(int row, int column) {
			return false;
		}
	};
	private final JTable table = new JTable(this.model);

	private KetQua result;

	public ChonPhuTungDialog(Window owner, List<PhuTung> phuTungs) {
		super(owner, I18nReaderUtil.get("app.phieuNhap.chonPhuTung"), ModalityType.APPLICATION_MODAL);
		this.phuTungs = phuTungs == null ? new ArrayList<>() : new ArrayList<>(phuTungs);

		this.setSize(640, 420);
		this.setLocationRelativeTo(owner);
		this.setLayout(new BorderLayout(8, 8));
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		this.table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.table.setRowHeight(26);
		this.table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					ChonPhuTungDialog.this.chon();
				}
			}
		});

		this.searchField.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				ChonPhuTungDialog.this.locDanhSach();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				ChonPhuTungDialog.this.locDanhSach();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				ChonPhuTungDialog.this.locDanhSach();
			}
		});

		this.add(this.taoPanelTimKiem(), BorderLayout.NORTH);
		this.add(new JScrollPane(this.table), BorderLayout.CENTER);
		this.add(this.taoPanelDuoi(), BorderLayout.SOUTH);

		this.locDanhSach();
	}

	public Optional<KetQua> showDialog() {
		this.setVisible(true);
		return Optional.ofNullable(this.result);
	}

	private JPanel taoPanelTimKiem() {
		JPanel panel = new JPanel(new BorderLayout(4, 4));
		panel.add(new JLabel(I18nReaderUtil.get("app.phieuNhap.timPhuTung")), BorderLayout.NORTH);
		panel.add(this.searchField, BorderLayout.CENTER);
		return panel;
	}

	private JPanel taoPanelDuoi() {
		JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
		JButton btnChon = new JButton(I18nReaderUtil.get("app.common.confirm"));
		JButton btnHuy = new JButton(I18nReaderUtil.get("app.common.cancel"));

		btnChon.addActionListener(e -> this.chon());
		btnHuy.addActionListener(e -> this.dispose());

		controls.add(this.spinner);
		controls.add(btnChon);
		controls.add(btnHuy);

		JPanel panel = new JPanel(new BorderLayout(8, 0));
		panel.add(this.statusLabel, BorderLayout.WEST);
		panel.add(controls, BorderLayout.EAST);
		return panel;
	}

	private void locDanhSach() {
		String keyword = TextNormalizerUtil.normalize(this.searchField.getText());
		this.filtered.clear();
		for (PhuTung phuTung : this.phuTungs) {
			boolean khopMa = TextNormalizerUtil.normalize(phuTung.getMaPhuTung()).contains(keyword);
			boolean khopTen = TextNormalizerUtil.normalize(phuTung.getTenPhuTung()).contains(keyword);
			if (keyword.isEmpty() || khopMa || khopTen) {
				this.filtered.add(phuTung);
			}
		}

		this.model.setRowCount(0);
		for (PhuTung phuTung : this.filtered) {
			double giaNhap = phuTung.getDonGiaNhap() != null ? phuTung.getDonGiaNhap() : 0.0;
			this.model.addRow(new Object[] { phuTung.getMaPhuTung(), phuTung.getTenPhuTung(),
					phuTung.getSoLuongTon(), giaNhap });
		}

		this.statusLabel.setText(this.filtered.isEmpty()
				? I18nReaderUtil.get("app.phieuNhap.khongCoPhuTungPhuHop")
				: "");
	}

	private void chon() {
		int selectedRow = this.table.getSelectedRow();
		if (selectedRow < 0) {
			ThongBao.canhBao(this, "app.phieuNhap.loiPhuTung");
			return;
		}

		PhuTung phuTung = this.filtered.get(this.table.convertRowIndexToModel(selectedRow));
		this.result = new KetQua(phuTung, (Integer) this.spinner.getValue());
		this.dispose();
	}

	public record KetQua(PhuTung phuTung, int soLuong) {
	}
}
