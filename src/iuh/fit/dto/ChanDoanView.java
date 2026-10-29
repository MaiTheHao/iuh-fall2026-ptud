package iuh.fit.dto;

import iuh.fit.enumeration.TrangThaiVanDeKyThuat;

import java.time.LocalDateTime;

public record ChanDoanView(
        String maVanDe,
        String maPhieuTiepNhan,
        String bienSo,
        String tenKhachHang,
        String tieuDe,
        int mucDoUuTien,
        String moTa,
        String giaiPhap,
        TrangThaiVanDeKyThuat khachHangDongY,
        LocalDateTime ngayTao) {
}
