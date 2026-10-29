package iuh.fit.gui.component;

import iuh.fit.gui.theme.Theme;
import iuh.fit.util.I18nReaderUtil;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import java.awt.FlowLayout;
import java.awt.event.ItemEvent;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.function.Consumer;

/**
 * Thanh điều hướng tuần dùng chung: nút tuần trước/sau/tuần này kèm combo Tháng/Năm.
 * Mọi thay đổi đều quy về Thứ 2 của tuần và phát một callback duy nhất.
 */
public final class ThanhDieuHuongTuan extends JPanel {

	private static final long serialVersionUID = 1L;

	private static final int BAN_KINH_NAM = 5;
	private static final int NAM_HIEN_TAI = Year.now().getValue();
	private DateTimeFormatter dinhDangNgay = DateTimeFormatter.ofPattern(I18nReaderUtil.get("app.date.format"));
	private static final WeekFields THU_TU_TUAN = WeekFields.of(DayOfWeek.MONDAY, 4);

	private final JButton btnTruoc = new JButton("‹");
	private final JButton btnSau = new JButton("›");
	private final JButton btnTuanNay = new JButton(I18nReaderUtil.get("app.schedule.thisWeek"));

	private final JLabel lblKhoangTuan = new JLabel();
	private final JLabel lblThang = this.taoNhan("app.schedule.month");
	private final JLabel lblNam = this.taoNhan("app.schedule.year");

	private final JComboBox<Integer> cbThang = new JComboBox<>();
	private final DefaultComboBoxModel<Integer> moHinhNam = new DefaultComboBoxModel<>();
	private final JComboBox<Integer> cbNam = new JComboBox<>(this.moHinhNam);

	private LocalDate ngayDauTuan;
	private Consumer<LocalDate> xuLyDoiTuan;
	private boolean dangDongBo;

	public ThanhDieuHuongTuan() {
		super(new FlowLayout(FlowLayout.LEFT, 8, 4));

		this.btnTruoc.setToolTipText(I18nReaderUtil.get("app.schedule.previousWeek"));
		this.btnSau.setToolTipText(I18nReaderUtil.get("app.schedule.nextWeek"));

		this.lblKhoangTuan.setFont(Theme.fieldLabelFont());
		this.lblKhoangTuan.setForeground(Theme.INK);

		for (int thang = 1; thang <= 12; thang++) {
			this.cbThang.addItem(thang);
		}

		for (int nam = NAM_HIEN_TAI - BAN_KINH_NAM; nam <= NAM_HIEN_TAI + BAN_KINH_NAM; nam++) {
			this.moHinhNam.addElement(nam);
		}

		this.add(this.btnTruoc);
		this.add(this.lblKhoangTuan);
		this.add(this.btnSau);
		this.add(this.btnTuanNay);
		this.add(this.lblThang);
		this.add(this.cbThang);
		this.add(this.lblNam);
		this.add(this.cbNam);

		this.ganSuKien();
		this.datNgayDauTuan(LocalDate.now());
	}

	private JLabel taoNhan(String khoa) {
		JLabel nhan = new JLabel(I18nReaderUtil.get(khoa));
		nhan.setFont(Theme.fieldLabelFont());
		nhan.setForeground(Theme.INK);
		return nhan;
	}

	private void ganSuKien() {
		this.btnTruoc.addActionListener(e -> {
			this.datNgayDauTuan(this.ngayDauTuan.minusWeeks(1));
			this.phat();
		});

		this.btnSau.addActionListener(e -> {
			this.datNgayDauTuan(this.ngayDauTuan.plusWeeks(1));
			this.phat();
		});

		this.btnTuanNay.addActionListener(e -> {
			this.datNgayDauTuan(LocalDate.now());
			this.phat();
		});

		this.cbThang.addItemListener(e -> {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				this.xuLyChonThangNam();
			}
		});

		this.cbNam.addItemListener(e -> {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				this.xuLyChonThangNam();
			}
		});
	}

	private void xuLyChonThangNam() {
		if (this.dangDongBo) {
			return;
		}

		Integer thang = (Integer) this.cbThang.getSelectedItem();
		Integer nam = (Integer) this.cbNam.getSelectedItem();

		if (thang == null || nam == null) {
			return;
		}

		this.datNgayDauTuan(LocalDate.of(nam, thang, 1));
		this.phat();
	}

	public void datXuLyDoiTuan(Consumer<LocalDate> xuLy) {
		this.xuLyDoiTuan = xuLy;
	}

	public LocalDate layNgayDauTuan() {
		return this.ngayDauTuan;
	}

	public void datNgayDauTuan(LocalDate ngay) {
		LocalDate moc = ngay == null ? LocalDate.now() : ngay;
		this.ngayDauTuan = moc.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
		this.dongBoDieuKhien();
	}

	/** Nạp lại toàn bộ chữ hiển thị; gọi từ render() của trang khi đổi ngôn ngữ. */
	public void capNhatNgonNgu() {
		this.btnTruoc.setToolTipText(I18nReaderUtil.get("app.schedule.previousWeek"));
		this.btnSau.setToolTipText(I18nReaderUtil.get("app.schedule.nextWeek"));
		this.btnTuanNay.setText(I18nReaderUtil.get("app.schedule.thisWeek"));
		this.lblThang.setText(I18nReaderUtil.get("app.schedule.month"));
		this.lblNam.setText(I18nReaderUtil.get("app.schedule.year"));
		this.dinhDangNgay = DateTimeFormatter.ofPattern(I18nReaderUtil.get("app.date.format"));
		this.dongBoDieuKhien();
	}

	private void dongBoDieuKhien() {
		this.dangDongBo = true;

		try {
			int soTuan = this.ngayDauTuan.get(THU_TU_TUAN.weekOfWeekBasedYear());

			this.lblKhoangTuan.setText(I18nReaderUtil.get("app.schedule.week") + " " + soTuan + " · "
					+ this.ngayDauTuan.format(this.dinhDangNgay) + " – "
					+ this.ngayDauTuan.plusDays(6).format(this.dinhDangNgay));

			this.boSungNam(this.ngayDauTuan.getYear());
			this.cbThang.setSelectedItem(this.ngayDauTuan.getMonthValue());
			this.cbNam.setSelectedItem(this.ngayDauTuan.getYear());
		} finally {
			this.dangDongBo = false;
		}
	}

	private void boSungNam(int nam) {
		if (this.moHinhNam.getIndexOf(nam) >= 0) {
			return;
		}

		int viTri = 0;

		while (viTri < this.moHinhNam.getSize() && this.moHinhNam.getElementAt(viTri) < nam) {
			viTri++;
		}

		this.moHinhNam.insertElementAt(nam, viTri);
	}

	private void phat() {
		if (this.xuLyDoiTuan != null) {
			this.xuLyDoiTuan.accept(this.ngayDauTuan);
		}
	}
}
