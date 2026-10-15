
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
        int pageSize) {
}
