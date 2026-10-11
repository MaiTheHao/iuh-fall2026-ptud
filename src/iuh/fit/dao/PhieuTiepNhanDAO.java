package iuh.fit.dao;

import iuh.fit.core.config.DatabaseConfig;
import iuh.fit.entity.PhieuTiepNhan;
import iuh.fit.entity.VanDeKyThuat;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public final class PhieuTiepNhanDAO extends BaseDao<PhieuTiepNhan> {

    public PhieuTiepNhan findById(String maPhieuTiepNhan) throws SQLException {
        String sqlCha = "SELECT maPhieuTiepNhan, ngayTao, moTa, trangThai, maNhanVien, maKhachHang, bienSo FROM PhieuTiepNhan WHERE maPhieuTiepNhan = ?";
        try (Connection connection = DatabaseConfig.getConnection();
                PreparedStatement ps = connection.prepareStatement(sqlCha)) {
            ps.setString(1, maPhieuTiepNhan);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                PhieuTiepNhan phieuTiepNhan = mapRow(rs);
                phieuTiepNhan.setDanhSachVanDeKyThuat(loadDanhSachVanDeKyThuat(connection, maPhieuTiepNhan));
                return phieuTiepNhan;
            }
        }
    }

    public List<PhieuTiepNhan> findAll() throws SQLException {
        String sql = "SELECT maPhieuTiepNhan, ngayTao, moTa, trangThai, maNhanVien, maKhachHang, bienSo FROM PhieuTiepNhan";
        try (Connection connection = DatabaseConfig.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            return mapRows(rs);
        }
    }

    @Override
    protected PhieuTiepNhan mapRow(ResultSet rs) throws SQLException {
        PhieuTiepNhan phieuTiepNhan = new PhieuTiepNhan();
        phieuTiepNhan.setMaPhieuTiepNhan(rs.getString("maPhieuTiepNhan"));
        phieuTiepNhan.setNgayTao(rs.getObject("ngayTao", LocalDateTime.class));
        phieuTiepNhan.setMoTa(rs.getString("moTa"));
        phieuTiepNhan.setTrangThai(rs.getString("trangThai"));
        phieuTiepNhan.setMaNhanVien(rs.getString("maNhanVien"));
        phieuTiepNhan.setMaKhachHang(rs.getString("maKhachHang"));
        phieuTiepNhan.setBienSo(rs.getString("bienSo"));
        return phieuTiepNhan;
    }

    @Override
    protected List<PhieuTiepNhan> mapRows(ResultSet rs) throws SQLException {
        List<PhieuTiepNhan> list = new ArrayList<>();
        while (rs.next()) {
            list.add(mapRow(rs));
        }
        return list;
    }

    private List<VanDeKyThuat> loadDanhSachVanDeKyThuat(Connection connection, String maPhieuTiepNhan)
            throws SQLException {
        String sqlCon = "SELECT maVanDe, maPhieuTiepNhan, tieuDe, moTa, mucDoUuTien, giaiPhap, khachHangDongY, ngayTao FROM VanDeKyThuat WHERE maPhieuTiepNhan = ? ORDER BY maVanDe";
        List<VanDeKyThuat> danhSach = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sqlCon)) {
            ps.setString(1, maPhieuTiepNhan);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    VanDeKyThuat vanDe = new VanDeKyThuat();
                    vanDe.setMaVanDe(rs.getString("maVanDe"));
                    vanDe.setMaPhieuTiepNhan(rs.getString("maPhieuTiepNhan"));
                    vanDe.setTieuDe(rs.getString("tieuDe"));
                    vanDe.setMoTa(rs.getString("moTa"));
                    vanDe.setMucDoUuTien(rs.getInt("mucDoUuTien"));
                    vanDe.setGiaiPhap(rs.getString("giaiPhap"));
                    vanDe.setKhachHangDongY(rs.getString("khachHangDongY"));
                    vanDe.setNgayTao(rs.getObject("ngayTao", LocalDateTime.class));
                    danhSach.add(vanDe);
                }
            }
        }
        return danhSach;
    }
}
