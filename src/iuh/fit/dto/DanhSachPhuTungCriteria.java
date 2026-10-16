package iuh.fit.dto;

public record DanhSachPhuTungCriteria(
        String maPhuTung,
        String tenPhuTung,
        String timNhanh,
        Double donGiaTu,
        Double donGiaDen,
        Integer soLuongTu,
        Integer soLuongDen,
        int page,
        int pageSize) implements DanhSachCriteria {

    public static DanhSachPhuTungCriteria macDinh() {
        return new DanhSachPhuTungCriteria(null, null, null, null, null, null, null, 1, PaginatedResult.DEFAULT_PAGE_SIZE);
    }
}
