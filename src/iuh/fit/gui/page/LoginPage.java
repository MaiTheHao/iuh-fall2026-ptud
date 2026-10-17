package iuh.fit.gui.page;

import iuh.fit.dto.LoginDTO;
import iuh.fit.exception.CredentialsInvalidException;
import iuh.fit.service.AuthService;
import iuh.fit.util.I18nReaderUtil;

import iuh.fit.gui.theme.Theme;

import iuh.fit.gui.component.ThongBao;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.sql.SQLException;

/** Màn hình đăng nhập hệ thống. */
public class LoginPage extends AbstractPage {

    private final AuthService authService = new AuthService();
    private final JTextField tenDangNhapField = new JTextField(16);
    private final JPasswordField matKhauField = new JPasswordField(16);

    public LoginPage() {
        setLayout(new GridBagLayout());
    }

    @Override
    public void render() {
        removeAll();

        JPanel box = Theme.contentPanel(16);
        box.setLayout(new GridLayout(0, 1, 4, 8));
        box.add(new JLabel(I18nReaderUtil.get("app.login.title")));
        box.add(new JLabel(I18nReaderUtil.get("app.login.username")));
        box.add(tenDangNhapField);
        box.add(new JLabel(I18nReaderUtil.get("app.login.password")));
        box.add(matKhauField);

        JPanel actions = new JPanel(new GridLayout(1, 2, 8, 0));
        JButton dangNhapButton = new JButton(I18nReaderUtil.get("app.login.submit"));
        dangNhapButton.addActionListener(e -> dangNhap());
        Theme.applyPrimary(dangNhapButton);
        JButton thoatButton = new JButton(I18nReaderUtil.get("app.login.exit"));
        thoatButton.addActionListener(e -> System.exit(0));
        actions.add(dangNhapButton);
        actions.add(thoatButton);
        box.add(actions);

        this.bindEnter(this.tenDangNhapField);
        this.bindEnter(this.matKhauField);

        add(box, new GridBagConstraints());
        revalidate();
        repaint();
    }

    @Override
    public String getTitleKey() {
        return "app.login.title";
    }

    private void bindEnter(JTextField field) {
        field.getInputMap(JComponent.WHEN_FOCUSED)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "dangNhap");
        field.getActionMap().put("dangNhap", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LoginPage.this.dangNhap();
            }
        });
    }

    private void dangNhap() {
        String tenDangNhap = tenDangNhapField.getText();
        String matKhau = new String(matKhauField.getPassword());
        try {
            authService.login(new LoginDTO(tenDangNhap, matKhau));
            goTo(DashboardPage.class);
        } catch (CredentialsInvalidException e) {
            ThongBao.loi(this, "app.login.errorInvalid");
        } catch (SQLException e) {
            ThongBao.loi(this, "app.login.errorSystem");
        }
    }
}
