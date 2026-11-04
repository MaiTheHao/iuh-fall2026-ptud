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
        int pageSize) implements BaseCriteria {

    public static DanhSachKhachHangCriteria macDinh() {
        return new DanhSachKhachHangCriteria(
                null, null, null, null, null, null,
                1, PaginatedResult.DEFAULT_PAGE_SIZE);
    }
}
