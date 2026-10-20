package iuh.fit.gui.form;

import iuh.fit.dto.PhuTungFormData;
import iuh.fit.gui.component.AnhField;
import iuh.fit.gui.component.FormFieldPanel;
import iuh.fit.gui.theme.Theme;
import iuh.fit.util.NumberParseUtil;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.Dimension;

/**
 * Form nhập liệu phụ tùng dùng chung cho màn quản lý và dialog sửa.
 */
public class PhuTungFormPanel extends FormFieldPanel {

    private static final long serialVersionUID = 1L;

    private final JTextField maField = new JTextField(14);
    private final JTextField tenField = new JTextField(14);
    private final JTextArea moTaArea = new JTextArea(4, 20);
    private final JTextField donGiaField = new JTextField(14);
    private final JTextField soLuongField = new JTextField(14);

    private final AnhField anhField = new AnhField(new AnhField.KhoaAnh(
            "app.phuTung.anhUrl",
            "app.phuTung.chonAnh",
            "app.phuTung.goAnh",
            "app.phuTung.chuaCoAnh",
            "app.phuTung.khongTaiDuocAnh",
            "app.phuTung.loaiTepAnh",
            "app.phuTung.duLieuKhongPhaiAnh"));

    public PhuTungFormPanel() {
        Theme.applyEditable(this.maField);
        Theme.applyReadOnly(this.soLuongField);

        this.addField("app.phuTung.ma", this.maField);
        this.addField("app.phuTung.ten", this.tenField);

        this.moTaArea.setLineWrap(true);
        this.moTaArea.setWrapStyleWord(true);
        JScrollPane scrollMoTa = new JScrollPane(this.moTaArea);
        scrollMoTa.setPreferredSize(new Dimension(200, 100));
        this.addField("app.phuTung.moTa", scrollMoTa);

        this.addBlock(this.anhField);
        this.addField("app.phuTung.donGia", this.donGiaField);
        this.addField("app.phuTung.soLuongTon", this.soLuongField);
    }

    public void napDuLieu(PhuTungFormData data) {
        this.maField.setText(data.maPhuTung() == null ? "" : data.maPhuTung());
        this.tenField.setText(data.tenPhuTung() == null ? "" : data.tenPhuTung());
        this.moTaArea.setText(data.moTa() == null ? "" : data.moTa());
        this.anhField.setDuongDan(data.anhUrl());
        this.donGiaField.setText(data.donGiaNhap() == null ? "" : String.valueOf(data.donGiaNhap()));
        this.soLuongField.setText(data.soLuongTon() == null ? "" : String.valueOf(data.soLuongTon()));
        Theme.applyReadOnly(this.maField);
    }

    public PhuTungFormData layDuLieu() {
        String ma = this.maField.getText().trim();
        String moTa = this.moTaArea.getText().trim();
        String anhUrl = this.anhField.getDuongDan();

        return new PhuTungFormData(
                ma.isBlank() ? null : ma,
                this.tenField.getText().trim(),
                moTa.isBlank() ? null : moTa,
                anhUrl.isBlank() ? null : anhUrl,
                NumberParseUtil.toDoubleOrNull(this.donGiaField.getText()),
                NumberParseUtil.toIntegerOrNull(this.soLuongField.getText()));
    }

    public void lamMoi() {
        this.maField.setText("");
        this.tenField.setText("");
        this.moTaArea.setText("");
        this.donGiaField.setText("");
        this.soLuongField.setText("");
        this.anhField.clear();
        Theme.applyEditable(this.maField);
        this.focus();
    }

    public void focus() {
        this.tenField.requestFocusInWindow();
    }
}
