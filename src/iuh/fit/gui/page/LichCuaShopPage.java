package iuh.fit.gui.page;

import iuh.fit.enumeration.MenuGroup;
import iuh.fit.gui.component.ThanhDieuHuongTuan;
import iuh.fit.service.PhanCongService;
import iuh.fit.util.I18nReaderUtil;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import iuh.fit.entity.PhanCong;
import iuh.fit.enumeration.CaLamViec;

/**
 * Trang lịch làm việc của cửa hàng.
 */
public class LichCuaShopPage extends AbstractPage {

	private static final long serialVersionUID = 1L;

	private static final int SO_NGAY = 7;
	private static final int SO_CA = 3;

	private static final Color MAU_TIEU_DE = new Color(232, 235, 238);
	private static final Color MAU_O_LE = new Color(234, 242, 249);
	private static final Color MAU_VIEN = new Color(195, 202, 210);
	private static final Color MAU_CHU = new Color(35, 85, 135);

	private final ThanhDieuHuongTuan thanhDieuHuong = new ThanhDieuHuongTuan();

	private final JLabel lblTongPhanCong = new JLabel();
	private final JLabel lblTongNhanVien = new JLabel();

	private final JPanel pnlBangLich = new JPanel(new GridLayout(4, 8, 0, 0));

	private final String[] khoaCa = { "app.schedule.morning", "app.schedule.afternoon", "app.schedule.allday" };

	private final PhanCongService phanCongService = new PhanCongService();

	private LocalDate ngayDauTuan;

	public LichCuaShopPage() {
		this.setLayout(new BorderLayout(0, 8));

		this.ngayDauTuan = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
		this.thanhDieuHuong.datNgayDauTuan(this.ngayDauTuan);

		this.khoiTaoSuKien();
	}

	@Override
	public void render() {
		this.removeAll();
		this.thanhDieuHuong.capNhatNgonNgu();
		this.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

		JLabel lblTieuDe = new JLabel(I18nReaderUtil.get("app.schedule.shopSchedule"));
		lblTieuDe.setFont(lblTieuDe.getFont().deriveFont(Font.BOLD, 16f));

		JPanel pnlTren = new JPanel(new BorderLayout());
		pnlTren.add(lblTieuDe, BorderLayout.NORTH);
		pnlTren.add(this.thanhDieuHuong, BorderLayout.CENTER);

		this.pnlBangLich.setBorder(BorderFactory.createLineBorder(MAU_VIEN));

		JPanel pnlThongKe = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 4));
		pnlThongKe.add(this.lblTongPhanCong);
		pnlThongKe.add(this.lblTongNhanVien);

		this.add(pnlTren, BorderLayout.NORTH);
		this.add(this.pnlBangLich, BorderLayout.CENTER);
		this.add(pnlThongKe, BorderLayout.SOUTH);

		this.capNhatBangLich();

		this.revalidate();
		this.repaint();
	}

	private void khoiTaoSuKien() {
		this.thanhDieuHuong.datXuLyDoiTuan(ngay -> { this.ngayDauTuan = ngay; this.capNhatBangLich(); });
	}

	private void capNhatBangLich() {
		this.pnlBangLich.removeAll();

		int tongPhanCong = 0;
		Set<String> danhSachNhanVien = new LinkedHashSet<>();

		try {
			Map<LocalDate, Map<CaLamViec, List<String>>> lich = this.phanCongService.getLichCuaShop(this.ngayDauTuan);

			CaLamViec[] cacCa = { CaLamViec.CA_SANG, CaLamViec.CA_CHIEU, CaLamViec.CA_NGAY };

			DateTimeFormatter dinhDangNgay = DateTimeFormatter.ofPattern("dd/MM/yyyy");

			this.themO("app.schedule.shift", true, false);

			String[] khoaThu = { "app.schedule.day.monday", "app.schedule.day.tuesday", "app.schedule.day.wednesday",
					"app.schedule.day.thursday", "app.schedule.day.friday", "app.schedule.day.saturday",
					"app.schedule.day.sunday" };

			DateTimeFormatter dateformatter = DateTimeFormatter.ofPattern("dd/MM");

			for (int ngay = 0; ngay < SO_NGAY; ngay++) {
				LocalDate ngayHienTai = this.ngayDauTuan.plusDays(ngay);

				String thu = I18nReaderUtil.get(khoaThu[ngay]);
				String ngayThang = ngayHienTai.format(dinhDangNgay);

				JPanel oNgay = this.taoO(thu + "\n" + ngayThang, true, false);

				this.pnlBangLich.add(oNgay);
			}

			for (CaLamViec ca : cacCa) {
				this.themO(this.khoaCa[this.layViTriCa(ca)], true, false);

				for (int ngay = 0; ngay < SO_NGAY; ngay++) {
					LocalDate ngayHienTai = this.ngayDauTuan.plusDays(ngay);

					List<String> nhanViens = lich.getOrDefault(ngayHienTai, Map.of()).getOrDefault(ca, List.of());

					JPanel oLich = this.taoO("", false, ngay % 2 == 1);
					oLich.setLayout(new BorderLayout(0, 4));

					if (nhanViens.isEmpty()) {
						JLabel lblTrong = new JLabel("–", SwingConstants.CENTER);
						lblTrong.setForeground(Color.GRAY);
						oLich.add(lblTrong, BorderLayout.NORTH);
					} else {
						JPanel pnlNhanVien = new JPanel();
						pnlNhanVien.setOpaque(false);
						pnlNhanVien.setLayout(new BoxLayout(pnlNhanVien, BoxLayout.Y_AXIS));

						for (String tenNhanVien : nhanViens) {
							JLabel lblNhanVien = new JLabel(tenNhanVien);
							lblNhanVien.setForeground(MAU_CHU);
							lblNhanVien.setOpaque(true);
							lblNhanVien.setBackground(MAU_O_LE);
							lblNhanVien.setBorder(BorderFactory.createCompoundBorder(
									BorderFactory.createLineBorder(new Color(80, 145, 210)),
									BorderFactory.createEmptyBorder(4, 6, 4, 6)));

							lblNhanVien.setAlignmentX(JLabel.LEFT_ALIGNMENT);

							pnlNhanVien.add(lblNhanVien);
							pnlNhanVien.add(Box.createVerticalStrut(4));

							danhSachNhanVien.add(tenNhanVien);
							tongPhanCong++;
						}

						oLich.add(pnlNhanVien, BorderLayout.NORTH);
					}

					this.pnlBangLich.add(oLich);
				}
			}

			this.lblTongPhanCong.setText(I18nReaderUtil.get("app.schedule.totalAssignments") + ": " + tongPhanCong);

			this.lblTongNhanVien
					.setText(I18nReaderUtil.get("app.schedule.totalEmployees") + ": " + danhSachNhanVien.size());

		} catch (SQLException | IllegalArgumentException ex) {
			ex.printStackTrace();

			JOptionPane.showMessageDialog(this, I18nReaderUtil.get("app.common.loiKhongXacDinh"),
					I18nReaderUtil.get("app.title"), JOptionPane.ERROR_MESSAGE);
		}

		this.pnlBangLich.revalidate();
		this.pnlBangLich.repaint();
	}

	private int layViTriCa(CaLamViec ca) {
		return switch (ca) {
		case CA_SANG -> 0;
		case CA_CHIEU -> 1;
		case CA_NGAY -> 2;
		};
	}

	private void themO(String khoa, boolean tieuDe, boolean le) {
		this.pnlBangLich.add(this.taoO(I18nReaderUtil.get(khoa), tieuDe, le));
	}

	private JPanel taoO(String noiDung, boolean tieuDe, boolean le) {

		JPanel panel = new JPanel(new BorderLayout());
		panel.setOpaque(true);
		panel.setBackground(tieuDe ? MAU_TIEU_DE : (le ? MAU_O_LE : Color.WHITE));

		Border vien = BorderFactory.createLineBorder(MAU_VIEN);
		Border dem = BorderFactory.createEmptyBorder(6, 8, 6, 8);

		panel.setBorder(BorderFactory.createCompoundBorder(vien, dem));

		JLabel label = new JLabel("<html>" + noiDung.replace("\n", "<br>") + "</html>");
		label.setFont(label.getFont().deriveFont(tieuDe ? Font.BOLD : Font.PLAIN));

		panel.add(label, BorderLayout.CENTER);
		panel.setMinimumSize(new Dimension(80, 48));

		return panel;
	}

	@Override
	public String getTitleKey() {
		return "app.menu.shopSchedule";
	}

	@Override
	public MenuGroup getGroup() {
		return MenuGroup.KE_HOACH;
	}
}
