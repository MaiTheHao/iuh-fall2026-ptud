package iuh.fit.gui.form;

import com.github.lgooddatepicker.components.DateTimePicker;
import iuh.fit.dto.VoucherFormData;
import iuh.fit.gui.component.BoChonNgayGio;
import iuh.fit.gui.component.FormFieldPanel;
import iuh.fit.gui.theme.Theme;
import iuh.fit.util.I18nReaderUtil;
import iuh.fit.util.NumberParseUtil;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.Dimension;

/**
 * Form nhập liệu voucher dùng chung cho màn quản lý và dialog sửa.
 */
public class VoucherFormPanel extends FormFieldPanel {

    private static final long serialVersionUID = 1L;

    private final JTextField maField = new JTextField(14);
    private final JTextField tenChuongTrinhField = new JTextField(14);
    private final JTextArea moTaArea = new JTextArea(4, 20);
    private final JTextField mucGiamField = new JTextField(14);
    private final DateTimePicker thoiHanPicker = BoChonNgayGio.taoBoChonNgayGio();
    private final JCheckBox trangThaiField = new JCheckBox();

    public VoucherFormPanel() {
        this.mucGiamField.setHorizontalAlignment(SwingConstants.RIGHT);
        Theme.applyReadOnly(this.maField);

        this.addField("app.voucher.ma", this.maField);
        this.addField("app.voucher.tenChuongTrinh", this.tenChuongTrinhField);

        this.moTaArea.setLineWrap(true);
        this.moTaArea.setWrapStyleWord(true);
        JScrollPane scrollMoTa = new JScrollPane(this.moTaArea);
        scrollMoTa.setPreferredSize(new Dimension(200, 100));
        this.addField("app.voucher.moTa", scrollMoTa);

        this.addField("app.voucher.mucGiam", this.mucGiamField);

        JLabel huongDan = new JLabel(I18nReaderUtil.get("app.voucher.huongDanMucGiam"));
        huongDan.setForeground(Theme.INK);
        this.addBlock(huongDan);

        this.addField("app.voucher.thoiHanApDung", this.thoiHanPicker);
        BoChonNgayGio.apDungMau(this.thoiHanPicker);

        this.addField("app.voucher.trangThaiKichHoat", this.trangThaiField);
        this.trangThaiField.setOpaque(false);
        this.trangThaiField.setSelected(true);
    }

    public void napDuLieu(VoucherFormData data) {
        this.maField.setText(data.maVoucher() == null ? "" : data.maVoucher());
        this.tenChuongTrinhField.setText(data.tenChuongTrinh() == null ? "" : data.tenChuongTrinh());
        this.moTaArea.setText(data.moTa() == null ? "" : data.moTa());
        this.mucGiamField.setText(data.mucGiam() == null ? "" : String.valueOf(data.mucGiam()));

        if (data.thoiHanApDung() == null) {
            this.thoiHanPicker.clear();
        } else {
            this.thoiHanPicker.setDateTimeStrict(data.thoiHanApDung());
        }

        this.trangThaiField.setSelected(Boolean.TRUE.equals(data.trangThaiKichHoat()));
    }

    public VoucherFormData layDuLieu() {
        String ma = this.maField.getText().trim();
        String moTa = this.moTaArea.getText().trim();

        return new VoucherFormData(
                ma.isBlank() ? null : ma,
                this.tenChuongTrinhField.getText().trim(),
                moTa.isEmpty() ? null : moTa,
                NumberParseUtil.toDoubleOrNull(this.mucGiamField.getText()),
                this.thoiHanPicker.getDateTimeStrict(),
                this.trangThaiField.isSelected());
    }

    public void lamMoi() {
        this.maField.setText("");
        this.tenChuongTrinhField.setText("");
        this.moTaArea.setText("");
        this.mucGiamField.setText("");
        this.thoiHanPicker.clear();
        this.trangThaiField.setSelected(true);
        this.focus();
    }

    public void focus() {
        this.tenChuongTrinhField.requestFocusInWindow();
    }
}
