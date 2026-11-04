package iuh.fit.dto;

import iuh.fit.enumeration.CaLamViec;

import java.time.LocalDate;

public record DanhSachPhanCongCriteria(
        String maPhanCong,
        String tenNhanVien,
        LocalDate tuNgay,
        LocalDate denNgay,
        CaLamViec caLamViec,
        String ghiChu,
        String timNhanh,
        int page,
        int pageSize) implements BaseCriteria {

    public static DanhSachPhanCongCriteria macDinh() {
        return new DanhSachPhanCongCriteria(
                null, null, null, null, null, null, null,
                1, PaginatedResult.DEFAULT_PAGE_SIZE);
    }

    public static DanhSachPhanCongCriteria tatCa(LocalDate tuNgay, LocalDate denNgay) {
        return new DanhSachPhanCongCriteria(
                null, null, tuNgay, denNgay, null, null, null,
                1, PaginatedResult.PAGE_SIZE_TAI_TOAN_BO);
    }
}
