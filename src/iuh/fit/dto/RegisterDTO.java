package iuh.fit.dto;

import iuh.fit.enumeration.VaiTro;

public record RegisterDTO(String tenDangNhap, String matKhau, String hoTen, String soDienThoai, VaiTro vaiTro) {
}
