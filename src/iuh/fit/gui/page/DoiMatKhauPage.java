package iuh.fit.gui.page;

import iuh.fit.enumeration.MenuGroup;
import iuh.fit.exception.AuthException;
import iuh.fit.gui.component.ManagementFormPanel;
import iuh.fit.gui.component.ThongBao;
import iuh.fit.gui.theme.Theme;
import iuh.fit.service.AuthService;
import iuh.fit.util.I18nReaderUtil;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.sql.SQLException;

public class DoiMatKhauPage extends AbstractPage {

	private final AuthService authService = new AuthService();

	private final JPasswordField matKhauHienTaiField = new JPasswordField();
	private final JPasswordField matKhauMoiField = new JPasswordField();
	private final JPasswordField xacNhanField = new JPasswordField();

	public DoiMatKhauPage() {
		this.setLayout(new BorderLayout());
		this.setBackground(Theme.BG);
	}

	@Override
	public void render() {
		this.removeAll();
		this.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
		this.setBackground(Theme.BG);
		this.xoaTrang();

		ManagementFormPanel form = new ManagementFormPanel("app.doiMatKhau.title");
		form.addField("app.doiMatKhau.matKhauHienTai", this.matKhauHienTaiField);
		form.addField("app.doiMatKhau.matKhauMoi", this.matKhauMoiField);
		form.addField("app.doiMatKhau.xacNhan", this.xacNhanField);

		JButton luu = form.getNutLuu();
		JButton xoaTrang = form.getNutXoaTrang();
		luu.setText(I18nReaderUtil.get("app.common.save"));
		xoaTrang.setText(I18nReaderUtil.get("app.common.clear"));
		Theme.applyPrimary(luu);
		luu.addActionListener(e -> this.doiMatKhau());
		xoaTrang.addActionListener(e -> this.xoaTrang());
		form.thayNut(luu, xoaTrang);

		JPanel boc = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
		boc.setOpaque(false);
		boc.add(form);
		this.add(boc, BorderLayout.CENTER);

		this.revalidate();
		this.repaint();
	}

	private void doiMatKhau() {
		try {
			this.authService.updatePassword(
					new String(this.matKhauHienTaiField.getPassword()),
					new String(this.matKhauMoiField.getPassword()),
					new String(this.xacNhanField.getPassword()));
			ThongBao.thongTin(this, "app.doiMatKhau.thanhCong");
			this.xoaTrang();
		} catch (AuthException e) {
			ThongBao.loi(this, e);
		} catch (SQLException e) {
			ThongBao.loi(this, "app.login.errorSystem");
		}
	}

	private void xoaTrang() {
		this.matKhauHienTaiField.setText("");
		this.matKhauMoiField.setText("");
		this.xacNhanField.setText("");
	}

	@Override
	public String getTitleKey() {
		return "app.menu.changePassword";
	}

	@Override
	public MenuGroup getGroup() {
		return MenuGroup.CAI_DAT;
	}
}
