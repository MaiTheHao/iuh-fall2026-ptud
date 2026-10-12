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

    private static final String SQL_SELECT = "SELECT maPhieuNhap, ngayTao, maNhanVien, moTa FROM PhieuNhapPhuTung";

    public PhieuNhapPhuTung findById(String maPhieuNhap) throws SQLException {
        try (Connection connection = DatabaseConfig.getConnection()) {
            PhieuNhapPhuTung phieu = findOne(connection, SQL_SELECT + " WHERE maPhieuNhap = ?", maPhieuNhap);
            if (phieu == null) {
                return null;
            }
            phieu.setDanhSachChiTietPhieuNhap(loadChiTiet(connection, maPhieuNhap));
            return phieu;
        }
    }

    public List<PhieuNhapPhuTung> findAll() throws SQLException {
        return findAll(SQL_SELECT);
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

    private List<ChiTietPhieuNhapPhuTung> loadChiTiet(Connection connection, String maPhieuNhap) throws SQLException {
        String sqlChiTiet = "SELECT maPhieuNhap, maPhuTung, soLuong, donGiaNhap FROM ChiTietPhieuNhapPhuTung WHERE maPhieuNhap = ?";
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
