package iuh.fit.dto;

import iuh.fit.enumeration.TrangThaiVanDeKyThuat;

public record ChanDoanUpdateFormData(
        String maVanDe,
        String tieuDe,
        String moTa,
        int mucDoUuTien,
        String giaiPhap,
        TrangThaiVanDeKyThuat khachHangDongY) {
}
