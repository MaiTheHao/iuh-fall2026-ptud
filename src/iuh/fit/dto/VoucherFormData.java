package iuh.fit.dto;

import java.time.LocalDateTime;

public record VoucherFormData(
        String maVoucher,
        String tenChuongTrinh,
        String moTa,
        Double mucGiam,
        LocalDateTime thoiHanApDung,
        Boolean trangThaiKichHoat) {
}
