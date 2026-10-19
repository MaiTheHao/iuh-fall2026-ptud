package iuh.fit.dto;

import iuh.fit.enumeration.TrangThaiKhieuNai;

import java.time.LocalDate;

public record DanhSachKhieuNaiCriteria(
        String maKhieuNai,
        String tenKhachHang,
        String maPhieuTiepNhan,
        String noiDung,
        TrangThaiKhieuNai trangThai,
        LocalDate ngayTaoTu,
        LocalDate ngayTaoDen,
        String timNhanh,
        int page,
        int pageSize) {
}
