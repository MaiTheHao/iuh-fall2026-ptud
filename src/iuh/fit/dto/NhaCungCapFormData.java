package iuh.fit.dto;

import iuh.fit.enumeration.TrangThaiNhaCungCap;

public record NhaCungCapFormData(
        String maNhaCungCap,
        String tenNhaCungCap,
        String sdt,
        String email,
        String diaChi,
        TrangThaiNhaCungCap trangThai,
        String ghiChu) {}
