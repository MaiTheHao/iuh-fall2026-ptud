package iuh.fit.dto;

public record DanhSachPhuTungCriteria(
        String maPhuTung,
        String tenPhuTung,
        String timNhanh,
        Double donGiaTu,
        Double donGiaDen,
        Integer soLuongTu,
        Integer soLuongDen,
        String maNhaCungCap,
        int page,
        int pageSize) implements BaseCriteria {

  public static DanhSachPhuTungCriteria macDinh() {
    return new DanhSachPhuTungCriteria(null, null, null, null, null, null, null, null, 1, PaginatedResult.DEFAULT_PAGE_SIZE);
  }
}
