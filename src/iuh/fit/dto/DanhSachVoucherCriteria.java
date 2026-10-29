package iuh.fit.dto;

import java.time.LocalDate;

public record DanhSachVoucherCriteria(
        String timNhanh,
        String maVoucher,
        Double mucGiamTu,
        Double mucGiamDen,
        LocalDate thoiHanTu,
        LocalDate thoiHanDen,
        Boolean trangThaiKichHoat,
        int page,
        int pageSize
) implements DanhSachCriteria {

    public static DanhSachVoucherCriteria macDinh() {
        return new DanhSachVoucherCriteria(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                1,
                PaginatedResult.DEFAULT_PAGE_SIZE
        );
    }

    public static DanhSachVoucherCriteria dangKichHoat(int page, int pageSize) {
        return new DanhSachVoucherCriteria(
                null, null, null, null, null, null, true, page, pageSize);
    }
}
