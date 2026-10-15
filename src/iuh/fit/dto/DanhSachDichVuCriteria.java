package iuh.fit.dto;

public record DanhSachDichVuCriteria(
        String maDichVu,
        String tenDichVu,
        String timNhanh,
        Double donGiaTu,
        Double donGiaDen,
        int page,
        int pageSize) {
}