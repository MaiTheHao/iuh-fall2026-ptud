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
        int pageSize) {
}
