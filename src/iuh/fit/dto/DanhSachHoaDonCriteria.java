package iuh.fit.dto;

import iuh.fit.enumeration.TrangThaiHoaDon;

import java.time.LocalDate;

public record DanhSachHoaDonCriteria(
        String maHoaDon,
        String maPhieuTiepNhan,
        String tenKhachHang,
        TrangThaiHoaDon trangThai,
        LocalDate ngayTaoTu,
        LocalDate ngayTaoDen,
        Double tongTienTu,
        Double tongTienDen,
        String timNhanh,
        int page,
        int pageSize) implements DanhSachCriteria {

    public static DanhSachHoaDonCriteria macDinh() {
        return new DanhSachHoaDonCriteria(
                null, null, null, null, null, null, null, null, null,
                1, PaginatedResult.DEFAULT_PAGE_SIZE);
    }
}
