package iuh.fit.dto;

public record DanhSachXeCriteria(
        String bienSo,
        String maHangXe,
        String maDongXe,
        Integer namSXTu,
        Integer namSXDen,
        Integer odoTu,
        Integer odoDen,
        String timNhanh,
        int page,
        int pageSize) implements BaseCriteria {

    public static DanhSachXeCriteria macDinh() {
        return new DanhSachXeCriteria(
                null, null, null, null, null, null, null, null,
                1, PaginatedResult.DEFAULT_PAGE_SIZE);
    }
}
