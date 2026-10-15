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
        int pageSize) {
}