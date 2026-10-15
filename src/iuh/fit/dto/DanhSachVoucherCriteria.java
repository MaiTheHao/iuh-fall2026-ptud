
package iuh.fit.dto;

import java.time.LocalDate;

public record DanhSachVoucherCriteria(
        String tuKhoa,
        String maVoucher,
        Double mucGiamTu,
        Double mucGiamDen,
        LocalDate thoiHanTu,
        LocalDate thoiHanDen,
        Boolean trangThaiKichHoat,
        int page,
        int pageSize
) {
}
