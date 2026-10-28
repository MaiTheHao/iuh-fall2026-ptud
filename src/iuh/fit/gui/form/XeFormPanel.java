package iuh.fit.gui.form;

import iuh.fit.dto.XeFormData;
import iuh.fit.gui.component.ComboGoiY;
import iuh.fit.gui.component.FormFieldPanel;
import iuh.fit.gui.theme.Theme;
import iuh.fit.util.NumberParseUtil;

import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 * Form nhập liệu xe dùng chung cho màn quản lý và dialog sửa.
 */
public class XeFormPanel extends FormFieldPanel {

    private static final long serialVersionUID = 1L;

    private final JTextField bienSoField = new JTextField(14);
    private final JComboBox<String> hangXeCombo = new JComboBox<>();
    private final JComboBox<String> dongXeCombo = new JComboBox<>();
    private final JTextField doiXeField = new JTextField(14);
    private final JTextField odoField = new JTextField(14);

    public XeFormPanel() {
        this.odoField.setHorizontalAlignment(SwingConstants.RIGHT);
        this.hangXeCombo.setEditable(true);
        this.dongXeCombo.setEditable(true);
        Theme.applyEditable(this.bienSoField);

        this.addField("app.xe.bienSo", this.bienSoField);
        this.addField("app.xe.hangXe", this.hangXeCombo);
        this.addField("app.xe.dongXe", this.dongXeCombo);
        this.addField("app.xe.doiXe", this.doiXeField);
        this.addField("app.xe.odo", this.odoField);
    }

    public void napDuLieu(XeFormData data) {
        this.bienSoField.setText(data.bienSo() == null ? "" : data.bienSo());
        this.hangXeCombo.setSelectedItem(data.hangXe() == null ? "" : data.hangXe());
        this.dongXeCombo.setSelectedItem(data.dongXe() == null ? "" : data.dongXe());
        this.doiXeField.setText(data.doiXe() == null ? "" : String.valueOf(data.doiXe()));
        this.odoField.setText(data.odo() == null ? "" : String.valueOf(data.odo()));
        Theme.applyReadOnly(this.bienSoField);
    }

    public XeFormData layDuLieu() {
        return new XeFormData(
                this.bienSoField.getText().trim(),
                ComboGoiY.giaTri(this.hangXeCombo),
                ComboGoiY.giaTri(this.dongXeCombo),
                NumberParseUtil.toIntegerOrNull(this.doiXeField.getText()),
                NumberParseUtil.toIntegerOrNull(this.odoField.getText()));
    }

    public void lamMoi() {
        this.bienSoField.setText("");
        Theme.applyEditable(this.bienSoField);
        ComboGoiY.xoa(this.hangXeCombo);
        ComboGoiY.xoa(this.dongXeCombo);
        this.doiXeField.setText("");
        this.odoField.setText("");
        this.focus();
    }

    public void focus() {
        this.hangXeCombo.requestFocusInWindow();
    }

    public JComboBox<String> getHangXeCombo() {
        return this.hangXeCombo;
    }

    public JComboBox<String> getDongXeCombo() {
        return this.dongXeCombo;
    }
}
