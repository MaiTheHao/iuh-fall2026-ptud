package iuh.fit.dto;

import iuh.fit.enumeration.TrangThaiNhanVien;
import iuh.fit.enumeration.VaiTro;

public record NhanVienFormData(
        String maNhanVien,
        String tenDangNhap,
        String matKhau,
        String hoTen,
        String soDienThoai,
        String anhDaiDien,
        VaiTro vaiTro,
        TrangThaiNhanVien trangThai,
        String ngayTao) {
}
