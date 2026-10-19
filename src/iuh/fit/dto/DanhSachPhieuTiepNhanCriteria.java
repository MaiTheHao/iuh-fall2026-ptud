package iuh.fit.dto;

import iuh.fit.enumeration.TrangThaiPhieuTiepNhan;

import java.time.LocalDate;

public record DanhSachPhieuTiepNhanCriteria(
        String maPhieuTiepNhan,
        String bienSo,
        String tenKhachHang,
        String sdt,
        String moTa,
        TrangThaiPhieuTiepNhan trangThai,
        LocalDate ngayTaoTu,
        LocalDate ngayTaoDen,
        String timNhanh,
        int page,
        int pageSize) implements DanhSachCriteria {

    public static DanhSachPhieuTiepNhanCriteria macDinh() {
        return new DanhSachPhieuTiepNhanCriteria(
                null, null, null, null, null, null, null, null, null,
                1, PaginatedResult.DEFAULT_PAGE_SIZE);
    }
}
