package iuh.fit.dto;

import iuh.fit.enumeration.TrangThaiKhieuNai;

import java.time.LocalDateTime;

public record KhieuNaiView(
        String maKhieuNai,
        String tenKhachHang,
        String maPhieuTiepNhan,
        String noiDung,
        TrangThaiKhieuNai trangThai,
        LocalDateTime ngayTao) {
}
