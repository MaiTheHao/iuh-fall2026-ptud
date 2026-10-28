package iuh.fit.gui.form;

import iuh.fit.dto.DichVuFormData;
import iuh.fit.gui.component.AnhField;
import iuh.fit.gui.component.FormFieldPanel;
import iuh.fit.gui.theme.Theme;
import iuh.fit.util.NumberParseUtil;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.Dimension;

/**
 * Form nhập liệu dịch vụ dùng chung cho màn quản lý và dialog sửa.
 */
public class DichVuFormPanel extends FormFieldPanel {

    private static final long serialVersionUID = 1L;

    private final JTextField maField = new JTextField(14);
    private final JTextField tenField = new JTextField(14);
    private final JTextArea moTaArea = new JTextArea(4, 20);
    private final JTextField donGiaField = new JTextField(14);

    private final AnhField anhField = new AnhField(new AnhField.KhoaAnh(
            "app.dichVu.anhUrl",
            "app.dichVu.chonAnh",
            "app.dichVu.goAnh",
            "app.dichVu.chuaCoAnh",
            "app.dichVu.khongTaiDuocAnh",
            "app.dichVu.loaiTepAnh",
            "app.dichVu.duLieuKhongPhaiAnh"));

    public DichVuFormPanel() {
        this.donGiaField.setHorizontalAlignment(SwingConstants.RIGHT);
        Theme.applyReadOnly(this.maField);

        this.addField("app.dichVu.ma", this.maField);
        this.addField("app.dichVu.ten", this.tenField);

        this.moTaArea.setLineWrap(true);
        this.moTaArea.setWrapStyleWord(true);
        JScrollPane scrollMoTa = new JScrollPane(this.moTaArea);
        scrollMoTa.setPreferredSize(new Dimension(200, 100));
        this.addField("app.dichVu.moTa", scrollMoTa);

        this.addBlock(this.anhField);
        this.addField("app.dichVu.donGia", this.donGiaField);
    }

    public void napDuLieu(DichVuFormData data) {
        this.maField.setText(data.maDichVu() == null ? "" : data.maDichVu());
        this.tenField.setText(data.tenDichVu() == null ? "" : data.tenDichVu());
        this.moTaArea.setText(data.moTa() == null ? "" : data.moTa());
        this.anhField.setDuongDan(data.anhUrl());
        this.donGiaField.setText(data.donGia() == null ? "" : String.valueOf(data.donGia()));
    }

    public DichVuFormData layDuLieu() {
        String ma = this.maField.getText().trim();
        String moTa = this.moTaArea.getText().trim();
        String anhUrl = this.anhField.getDuongDan();

        return new DichVuFormData(
                ma.isBlank() ? null : ma,
                this.tenField.getText().trim(),
                moTa.isBlank() ? null : moTa,
                anhUrl.isBlank() ? null : anhUrl,
                NumberParseUtil.toDoubleOrNull(this.donGiaField.getText()));
    }

    public void lamMoi() {
        this.maField.setText("");
        this.tenField.setText("");
        this.moTaArea.setText("");
        this.donGiaField.setText("");
        this.anhField.clear();
        this.focus();
    }

    public void focus() {
        this.tenField.requestFocusInWindow();
    }
}
