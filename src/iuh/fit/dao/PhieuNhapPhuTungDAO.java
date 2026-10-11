package iuh.fit.dao;

import iuh.fit.core.config.DatabaseConfig;
import iuh.fit.entity.ChiTietPhieuNhapPhuTung;
import iuh.fit.entity.PhieuNhapPhuTung;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public final class PhieuNhapPhuTungDAO extends BaseDao<PhieuNhapPhuTung> {

    public PhieuNhapPhuTung findById(String maPhieuNhap) throws SQLException {
        String sqlPhieu = "SELECT maPhieuNhap, ngayTao, maNhanVien, moTa FROM PhieuNhapPhuTung WHERE maPhieuNhap = ?";
        try (Connection connection = DatabaseConfig.getConnection();
                PreparedStatement ps = connection.prepareStatement(sqlPhieu)) {
            ps.setString(1, maPhieuNhap);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                PhieuNhapPhuTung phieu = mapRow(rs);
                phieu.setDanhSachChiTietPhieuNhap(loadChiTiet(connection, maPhieuNhap));
                return phieu;
            }
        }
    }

    public List<PhieuNhapPhuTung> findAll() throws SQLException {
        String sql = "SELECT maPhieuNhap, ngayTao, maNhanVien, moTa FROM PhieuNhapPhuTung";
        try (Connection connection = DatabaseConfig.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            return mapRows(rs);
        }
    }

    @Override
    protected PhieuNhapPhuTung mapRow(ResultSet rs) throws SQLException {
        PhieuNhapPhuTung phieu = new PhieuNhapPhuTung();
        phieu.setMaPhieuNhap(rs.getString("maPhieuNhap"));
        phieu.setNgayTao(rs.getObject("ngayTao", LocalDateTime.class));
        phieu.setMaNhanVien(rs.getString("maNhanVien"));
        phieu.setMoTa(rs.getString("moTa"));
        return phieu;
    }

    @Override
    protected List<PhieuNhapPhuTung> mapRows(ResultSet rs) throws SQLException {
        List<PhieuNhapPhuTung> list = new ArrayList<>();
        while (rs.next()) {
            list.add(mapRow(rs));
        }
        return list;
    }

    private List<ChiTietPhieuNhapPhuTung> loadChiTiet(Connection connection, String maPhieuNhap) throws SQLException {
        String sqlChiTiet = "SELECT maPhieuNhap, maPhuTung, soLuong, donGiaNhap FROM ChiTietPhieuNhapPhuTung WHERE maPhieuNhap = ? ORDER BY maPhuTung";
        List<ChiTietPhieuNhapPhuTung> chiTietList = new ArrayList<>();
        try (PreparedStatement psChiTiet = connection.prepareStatement(sqlChiTiet)) {
            psChiTiet.setString(1, maPhieuNhap);
            try (ResultSet rsChiTiet = psChiTiet.executeQuery()) {
                while (rsChiTiet.next()) {
                    ChiTietPhieuNhapPhuTung chiTiet = new ChiTietPhieuNhapPhuTung();
                    chiTiet.setMaPhieuNhap(rsChiTiet.getString("maPhieuNhap"));
                    chiTiet.setMaPhuTung(rsChiTiet.getString("maPhuTung"));
                    chiTiet.setSoLuong(rsChiTiet.getInt("soLuong"));
                    chiTiet.setDonGiaNhap(rsChiTiet.getDouble("donGiaNhap"));
                    chiTietList.add(chiTiet);
                }
            }
        }
        return chiTietList;
    }
}
