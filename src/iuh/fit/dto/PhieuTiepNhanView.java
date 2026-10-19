package iuh.fit.dto;

import iuh.fit.enumeration.TrangThaiPhieuTiepNhan;

import java.time.LocalDateTime;

public record PhieuTiepNhanView(
        String maPhieuTiepNhan,
        String bienSo,
        String tenKhachHang,
        String sdt,
        String moTa,
        TrangThaiPhieuTiepNhan trangThai,
        LocalDateTime ngayTao) {
}
