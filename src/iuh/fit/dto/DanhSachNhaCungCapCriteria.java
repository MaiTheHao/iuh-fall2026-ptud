package iuh.fit.dto;

import java.time.LocalDate;

public record DanhSachNhaCungCapCriteria(
        String maNhaCungCap,
        String tenNhaCungCap,
        String sdt,
        String timNhanh,
        String trangThai,
        LocalDate ngayTaoTu,
        LocalDate ngayTaoDen,
        int page,
        int pageSize) implements BaseCriteria {

  public static DanhSachNhaCungCapCriteria macDinh() {
    return new DanhSachNhaCungCapCriteria(null, null, null, null, null, null, null, 1, PaginatedResult.DEFAULT_PAGE_SIZE);
  }
}
