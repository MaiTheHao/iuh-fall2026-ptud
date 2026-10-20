package iuh.fit.gui.form;

import iuh.fit.dto.NhanVienFormData;
import iuh.fit.entity.NhanVien;
import iuh.fit.enumeration.TrangThaiNhanVien;
import iuh.fit.enumeration.VaiTro;
import iuh.fit.gui.component.AnhField;
import iuh.fit.gui.component.BoHienThiI18n;
import iuh.fit.gui.component.FormFieldPanel;
import iuh.fit.gui.theme.Theme;
import iuh.fit.util.I18nReaderUtil;

import javax.swing.JComboBox;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.time.format.DateTimeFormatter;

/**
 * Form nhập liệu nhân viên dùng chung cho màn quản lý và dialog sửa.
 */
public class NhanVienFormPanel extends FormFieldPanel {

    private static final long serialVersionUID = 1L;

    private final AnhField anhField = new AnhField(new AnhField.KhoaAnh(
            "app.nhanVien.anhUrl",
            "app.nhanVien.chonAnh",
            "app.nhanVien.goAnh",
            "app.nhanVien.chuaCoAnh",
            "app.nhanVien.khongTaiDuocAnh",
            "app.nhanVien.loaiTepAnh",
            "app.nhanVien.duLieuKhongPhaiAnh"));

    private final JTextField maField = new JTextField(14);
    private final JTextField tenDangNhapField = new JTextField(14);
    private final JPasswordField matKhauField = new JPasswordField(14);
    private final JTextField hoTenField = new JTextField(14);
    private final JTextField soDienThoaiField = new JTextField(14);
    private final JTextField ngayTaoField = new JTextField(14);
    private final JComboBox<VaiTro> vaiTroCombo = new JComboBox<>(VaiTro.values());
    private final JComboBox<TrangThaiNhanVien> trangThaiCombo = new JComboBox<>(TrangThaiNhanVien.values());

    public NhanVienFormPanel() {
        Theme.applyReadOnly(this.maField);
        this.maField.setFocusable(false);
        Theme.applyEditable(this.tenDangNhapField);
        Theme.applyEditable(this.matKhauField);
        Theme.applyReadOnly(this.ngayTaoField);

        this.vaiTroCombo.setRenderer(BoHienThiI18n.tao());
        this.trangThaiCombo.setRenderer(BoHienThiI18n.tao());

        this.addField("app.nhanVien.maNhanVien", this.maField);
        this.addField("app.nhanVien.tenDangNhap", this.tenDangNhapField);
        this.addField("app.nhanVien.matKhau", this.matKhauField);
        this.addField("app.nhanVien.hoTen", this.hoTenField);
        this.addField("app.nhanVien.soDienThoai", this.soDienThoaiField);
        this.addField("app.nhanVien.vaiTro", this.vaiTroCombo);
        this.addField("app.nhanVien.trangThai", this.trangThaiCombo);
        this.addField("app.nhanVien.ngayTao", this.ngayTaoField);
        this.addBlock(this.anhField);

        this.vaiTroCombo.setSelectedItem(VaiTro.NHAN_VIEN_TIEP_NHAN);
        this.trangThaiCombo.setSelectedItem(TrangThaiNhanVien.HOAT_DONG);
    }

    public void napDuLieu(NhanVien nhanVien) {
        this.maField.setText(nhanVien.getMaNhanVien() == null ? "" : nhanVien.getMaNhanVien());
        this.tenDangNhapField.setText(nhanVien.getTenDangNhap() == null ? "" : nhanVien.getTenDangNhap());
        this.matKhauField.setText("");
        this.hoTenField.setText(nhanVien.getHoTen() == null ? "" : nhanVien.getHoTen());
        this.soDienThoaiField.setText(nhanVien.getSoDienThoai() == null ? "" : nhanVien.getSoDienThoai());
        this.ngayTaoField.setText(nhanVien.getNgayTao() == null ? ""
                : nhanVien.getNgayTao().format(DateTimeFormatter.ofPattern(I18nReaderUtil.get("app.date.format"))));
        this.anhField.setDuongDan(nhanVien.getAnhDaiDien());
        this.vaiTroCombo.setSelectedItem(nhanVien.getVaiTro());
        this.trangThaiCombo.setSelectedItem(nhanVien.getTrangThai());
        Theme.applyReadOnly(this.tenDangNhapField);
    }

    public NhanVienFormData layDuLieu() {
        String soDienThoai = this.soDienThoaiField.getText().trim();

        return new NhanVienFormData(
                this.maField.getText().trim(),
                this.tenDangNhapField.getText().trim(),
                new String(this.matKhauField.getPassword()),
                this.hoTenField.getText().trim(),
                soDienThoai,
                this.anhField.getDuongDan(),
                (VaiTro) this.vaiTroCombo.getSelectedItem(),
                (TrangThaiNhanVien) this.trangThaiCombo.getSelectedItem(),
                this.ngayTaoField.getText().trim());
    }

    public void lamMoi() {
        this.maField.setText("");
        this.tenDangNhapField.setText("");
        this.matKhauField.setText("");
        this.hoTenField.setText("");
        this.soDienThoaiField.setText("");
        this.ngayTaoField.setText("");
        this.anhField.clear();
        this.vaiTroCombo.setSelectedItem(VaiTro.NHAN_VIEN_TIEP_NHAN);
        this.trangThaiCombo.setSelectedItem(TrangThaiNhanVien.HOAT_DONG);
        Theme.applyReadOnly(this.maField);
        this.maField.setFocusable(false);
        Theme.applyEditable(this.tenDangNhapField);
        Theme.applyEditable(this.matKhauField);
        this.focus();
    }

    public void focus() {
        if (this.tenDangNhapField.isEditable()) {
            this.tenDangNhapField.requestFocusInWindow();
        } else {
            this.hoTenField.requestFocusInWindow();
        }
    }
}
