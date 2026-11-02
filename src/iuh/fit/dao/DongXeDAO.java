package iuh.fit.dao;

import iuh.fit.core.config.DatabaseConfig;
import iuh.fit.entity.DongXe;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class DongXeDAO extends BaseDao<DongXe> {

    private static final String SQL_SELECT = """
            SELECT d.maDongXe, d.maHangXe, d.ten, h.ten AS tenHangXe FROM DongXe d JOIN HangXe h ON h.maHangXe = d.maHangXe""";

    public Optional<DongXe> findById(String maDongXe) throws SQLException {
        return findOne(SQL_SELECT + " WHERE d.maDongXe = ?", maDongXe);
    }

    public List<DongXe> findAll() throws SQLException {
        return findAll(SQL_SELECT + " ORDER BY h.ten, d.ten");
    }

    public List<DongXe> findByHang(String maHangXe) throws SQLException {
        String sql = SQL_SELECT + " WHERE d.maHangXe = ? ORDER BY d.ten";
        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maHangXe);
            try (ResultSet rs = ps.executeQuery()) {
                List<DongXe> list = new ArrayList<>();
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
                return list;
            }
        }
    }

    public void insert(DongXe dongXe) throws SQLException {
        String sql = """
                INSERT INTO DongXe (maDongXe, maHangXe, ten) VALUES (?, ?, ?)""";
        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, dongXe.getMaDongXe());
            ps.setString(2, dongXe.getMaHangXe());
            ps.setString(3, dongXe.getTen());
            ps.executeUpdate();
        }
    }

    public void update(DongXe dongXe) throws SQLException {
        String sql = """
                UPDATE DongXe SET maHangXe = ?, ten = ? WHERE maDongXe = ?""";
        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, dongXe.getMaHangXe());
            ps.setString(2, dongXe.getTen());
            ps.setString(3, dongXe.getMaDongXe());
            ps.executeUpdate();
        }
    }

    public void delete(String maDongXe) throws SQLException {
        String sql = """
                DELETE FROM DongXe WHERE maDongXe = ?""";
        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maDongXe);
            ps.executeUpdate();
        }
    }

    public boolean existsByHang(String maHangXe) throws SQLException {
        String sql = """
                SELECT 1 FROM DongXe WHERE maHangXe = ?""";
        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maHangXe);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean existsByHangAndTen(String maHangXe, String ten) throws SQLException {
        String sql = """
                SELECT 1 FROM DongXe WHERE maHangXe = ? AND ten = ?""";
        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maHangXe);
            ps.setString(2, ten);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    @Override
    protected DongXe mapRow(ResultSet rs) throws SQLException {
        return new DongXe(
                rs.getString("maDongXe"),
                rs.getString("maHangXe"),
                rs.getString("ten"),
                rs.getString("tenHangXe"));
    }
}
