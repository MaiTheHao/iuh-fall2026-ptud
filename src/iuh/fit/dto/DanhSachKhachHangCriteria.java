package iuh.fit.dto;

import java.time.LocalDate;

public record DanhSachKhachHangCriteria(
        String maKhachHang,
        String sdt,
        String hoTen,
        LocalDate ngayTaoTu,
        LocalDate ngayTaoDen,
        String timNhanh,
        int page,
        int pageSize) {
}
