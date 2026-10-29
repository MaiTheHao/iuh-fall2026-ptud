package iuh.fit.dto;

import iuh.fit.enumeration.TrangThaiVanDeKyThuat;

import java.time.LocalDate;

public record DanhSachChanDoanCriteria(
        String maPhieuTiepNhan,
        Integer mucDoTu,
        Integer mucDoDen,
        TrangThaiVanDeKyThuat khachHangDongY,
        LocalDate ngayTaoTu,
        LocalDate ngayTaoDen,
        String timNhanh,
        int page,
        int pageSize) implements DanhSachCriteria {

    public static DanhSachChanDoanCriteria macDinh() {
        return new DanhSachChanDoanCriteria(
                null, null, null, null, null, null, null,
                1, PaginatedResult.DEFAULT_PAGE_SIZE);
    }
}
