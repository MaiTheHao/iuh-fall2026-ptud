package iuh.fit.dto;

public record DanhSachDichVuCriteria(
        String maDichVu,
        String tenDichVu,
        String timNhanh,
        Double donGiaTu,
        Double donGiaDen,
        int page,
        int pageSize) implements DanhSachCriteria {

    public static DanhSachDichVuCriteria macDinh() {
        return new DanhSachDichVuCriteria(null, null, null, null, null, 1, PaginatedResult.DEFAULT_PAGE_SIZE);
    }
}
