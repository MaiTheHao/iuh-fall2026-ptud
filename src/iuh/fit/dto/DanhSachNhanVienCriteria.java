package iuh.fit.dto;

import java.time.LocalDate;

public record DanhSachNhanVienCriteria(
        String maNhanVien,
        String hoTen,
        String soDienThoai,
        String vaiTro,
        String trangThai,
        String timNhanh,
        LocalDate ngayTaoTu,
        LocalDate ngayTaoDen,
        int page,
        int pageSize) implements BaseCriteria {

    public static DanhSachNhanVienCriteria macDinh() {
        return new DanhSachNhanVienCriteria(null, null, null, null, null, null, null, null, 1, PaginatedResult.DEFAULT_PAGE_SIZE);
    }
}
