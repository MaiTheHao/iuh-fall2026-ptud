package iuh.fit.dao;

import iuh.fit.core.config.DatabaseConfig;
import iuh.fit.entity.ChiTietHoaDon;
import iuh.fit.entity.HoaDon;
import iuh.fit.enumeration.LoaiSanPham;
import iuh.fit.enumeration.TrangThaiHoaDon;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class HoaDonDAO extends BaseDao<HoaDon> {

    private static final String SQL_SELECT = """
            SELECT maHoaDon, maPhieuTiepNhan, maVoucher, trangThaiHoaDon,
                   lyDoHuy, ghiChu, ngayTao, thoiGianThanhToan
            FROM HoaDon
            """;

    public Optional<HoaDon> findById(String maHoaDon) throws SQLException {
        try (Connection connection = DatabaseConfig.getConnection()) {
            Optional<HoaDon> found = findOne(connection, SQL_SELECT + " WHERE maHoaDon = ?", maHoaDon);
            if (found.isEmpty()) {
                return Optional.empty();
            }
            HoaDon hoaDon = found.get();
            hoaDon.setDanhSachChiTietHoaDon(loadDanhSachChiTiet(connection, maHoaDon));
            return Optional.of(hoaDon);
        }
    }

    public List<HoaDon> findAll() throws SQLException {
        return findAll(SQL_SELECT);
    }

    @Override
    protected HoaDon mapRow(ResultSet rs) throws SQLException {
        HoaDon hoaDon = new HoaDon();
        hoaDon.setMaHoaDon(rs.getString("maHoaDon"));
        hoaDon.setMaPhieuTiepNhan(rs.getString("maPhieuTiepNhan"));
        hoaDon.setMaVoucher(rs.getString("maVoucher"));
        hoaDon.setTrangThaiHoaDon(TrangThaiHoaDon.valueOf(rs.getString("trangThaiHoaDon")));
        hoaDon.setLyDoHuy(rs.getString("lyDoHuy"));
        hoaDon.setGhiChu(rs.getString("ghiChu"));
        hoaDon.setNgayTao(rs.getObject("ngayTao", LocalDateTime.class));
        hoaDon.setThoiGianThanhToan(rs.getObject("thoiGianThanhToan", LocalDateTime.class));
        return hoaDon;
    }

    private List<ChiTietHoaDon> loadDanhSachChiTiet(Connection connection, String maHoaDon) throws SQLException {
        String sqlChiTiet = """
                SELECT maHoaDon, loaiSanPham, maSanPham, soLuong, donGia
                FROM ChiTietHoaDon
                WHERE maHoaDon = ?
                """;
        List<ChiTietHoaDon> chiTietList = new ArrayList<>();
        try (PreparedStatement psChiTiet = connection.prepareStatement(sqlChiTiet)) {
            psChiTiet.setString(1, maHoaDon);
            try (ResultSet rsChiTiet = psChiTiet.executeQuery()) {
                while (rsChiTiet.next()) {
                    ChiTietHoaDon chiTiet = new ChiTietHoaDon();
                    chiTiet.setMaHoaDon(rsChiTiet.getString("maHoaDon"));
                    chiTiet.setLoaiSanPham(LoaiSanPham.valueOf(rsChiTiet.getString("loaiSanPham")));
                    chiTiet.setMaSanPham(rsChiTiet.getString("maSanPham"));
                    chiTiet.setSoLuong(rsChiTiet.getInt("soLuong"));
                    chiTiet.setDonGia(rsChiTiet.getDouble("donGia"));
                    chiTietList.add(chiTiet);
                }
            }
        }
        return chiTietList;
    }
}
