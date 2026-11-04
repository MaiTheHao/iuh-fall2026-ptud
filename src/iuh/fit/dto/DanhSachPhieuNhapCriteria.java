package iuh.fit.dto;

import java.time.LocalDate;

public record DanhSachPhieuNhapCriteria(
        String maPhieuNhap,
        String maThuKho,
        String moTa,
        LocalDate ngayTaoTu,
        LocalDate ngayTaoDen,
        Double tongChiPhiTu,
        Double tongChiPhiDen,
        String timNhanh,
        int page,
        int pageSize) implements BaseCriteria {

    public static DanhSachPhieuNhapCriteria macDinh() {
        return new DanhSachPhieuNhapCriteria(
                null, null, null, null, null, null, null, null,
                1, PaginatedResult.DEFAULT_PAGE_SIZE);
    }
}
