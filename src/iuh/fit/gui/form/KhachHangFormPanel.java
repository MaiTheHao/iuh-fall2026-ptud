package iuh.fit.gui.form;

import iuh.fit.dto.KhachHangFormData;
import iuh.fit.gui.component.FormFieldPanel;
import iuh.fit.gui.theme.Theme;

import javax.swing.JTextField;

/**
 * Form nhập liệu khách hàng dùng chung cho màn quản lý và dialog sửa.
 */
public class KhachHangFormPanel extends FormFieldPanel {

    private static final long serialVersionUID = 1L;

    private final JTextField maField = new JTextField(14);
    private final JTextField sdtField = new JTextField(14);
    private final JTextField hoTenField = new JTextField(14);
    private final JTextField ngayTaoField = new JTextField(14);

    public KhachHangFormPanel() {
        Theme.applyReadOnly(this.maField);
        Theme.applyReadOnly(this.ngayTaoField);

        this.addField("app.khachHang.ma", this.maField);
        this.addField("app.khachHang.sdt", this.sdtField);
        this.addField("app.khachHang.hoTen", this.hoTenField);
        this.addField("app.khachHang.ngayTao", this.ngayTaoField);
    }

    public void napDuLieu(KhachHangFormData data) {
        this.maField.setText(data.maKhachHang() == null ? "" : data.maKhachHang());
        this.sdtField.setText(data.sdt() == null ? "" : data.sdt());
        this.hoTenField.setText(data.hoTen() == null ? "" : data.hoTen());
        this.ngayTaoField.setText(data.ngayTao() == null ? "" : data.ngayTao());
    }

    public KhachHangFormData layDuLieu() {
        String ma = this.maField.getText().trim();
        return new KhachHangFormData(
                ma.isBlank() ? null : ma,
                this.sdtField.getText().trim(),
                this.hoTenField.getText().trim(),
                this.ngayTaoField.getText().trim());
    }

    public void lamMoi() {
        this.maField.setText("");
        this.sdtField.setText("");
        this.hoTenField.setText("");
        this.ngayTaoField.setText("");
        this.focus();
    }

    public void focus() {
        this.sdtField.requestFocusInWindow();
    }
}
