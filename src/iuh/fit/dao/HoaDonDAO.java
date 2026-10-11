package iuh.fit.dao;

import iuh.fit.core.config.DatabaseConfig;
import iuh.fit.entity.ChiTietHoaDon;
import iuh.fit.entity.HoaDon;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public final class HoaDonDAO extends BaseDao<HoaDon> {

    public HoaDon findById(String maHoaDon) throws SQLException {
        String sqlHoaDon = "SELECT maHoaDon, maPhieuTiepNhan, maVoucher, trangThaiHoaDon, lyDoHuy, ghiChu, ngayTao, thoiGianThanhToan FROM HoaDon WHERE maHoaDon = ?";
        try (Connection connection = DatabaseConfig.getConnection();
                PreparedStatement ps = connection.prepareStatement(sqlHoaDon)) {
            ps.setString(1, maHoaDon);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                HoaDon hoaDon = mapRow(rs);
                hoaDon.setDanhSachChiTietHoaDon(loadDanhSachChiTiet(connection, maHoaDon));
                return hoaDon;
            }
        }
    }

    public List<HoaDon> findAll() throws SQLException {
        String sql = "SELECT maHoaDon, maPhieuTiepNhan, maVoucher, trangThaiHoaDon, lyDoHuy, ghiChu, ngayTao, thoiGianThanhToan FROM HoaDon";
        try (Connection connection = DatabaseConfig.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            return mapRows(rs);
        }
    }

    @Override
    protected HoaDon mapRow(ResultSet rs) throws SQLException {
        HoaDon hoaDon = new HoaDon();
        hoaDon.setMaHoaDon(rs.getString("maHoaDon"));
        hoaDon.setMaPhieuTiepNhan(rs.getString("maPhieuTiepNhan"));
        hoaDon.setMaVoucher(rs.getString("maVoucher"));
        hoaDon.setTrangThaiHoaDon(rs.getString("trangThaiHoaDon"));
        hoaDon.setLyDoHuy(rs.getString("lyDoHuy"));
        hoaDon.setGhiChu(rs.getString("ghiChu"));
        hoaDon.setNgayTao(rs.getObject("ngayTao", LocalDateTime.class));
        hoaDon.setThoiGianThanhToan(rs.getObject("thoiGianThanhToan", LocalDateTime.class));
        return hoaDon;
    }

    @Override
    protected List<HoaDon> mapRows(ResultSet rs) throws SQLException {
        List<HoaDon> list = new ArrayList<>();
        while (rs.next()) {
            list.add(mapRow(rs));
        }
        return list;
    }

    private List<ChiTietHoaDon> loadDanhSachChiTiet(Connection connection, String maHoaDon) throws SQLException {
        String sqlChiTiet = "SELECT maHoaDon, loaiSanPham, maSanPham, soLuong, donGia FROM ChiTietHoaDon WHERE maHoaDon = ? ORDER BY loaiSanPham, maSanPham";
        List<ChiTietHoaDon> chiTietList = new ArrayList<>();
        try (PreparedStatement psChiTiet = connection.prepareStatement(sqlChiTiet)) {
            psChiTiet.setString(1, maHoaDon);
            try (ResultSet rsChiTiet = psChiTiet.executeQuery()) {
                while (rsChiTiet.next()) {
                    ChiTietHoaDon chiTiet = new ChiTietHoaDon();
                    chiTiet.setMaHoaDon(rsChiTiet.getString("maHoaDon"));
                    chiTiet.setLoaiSanPham(rsChiTiet.getString("loaiSanPham"));
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
