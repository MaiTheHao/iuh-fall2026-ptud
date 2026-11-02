package iuh.fit.dao;

import iuh.fit.core.config.DatabaseConfig;
import iuh.fit.entity.HangXe;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public final class HangXeDAO extends BaseDao<HangXe> {

    private static final String SQL_SELECT = """
            SELECT maHangXe, ten FROM HangXe""";

    public Optional<HangXe> findById(String maHangXe) throws SQLException {
        return findOne(SQL_SELECT + " WHERE maHangXe = ?", maHangXe);
    }

    public List<HangXe> findAll() throws SQLException {
        return findAll(SQL_SELECT + " ORDER BY ten");
    }

    public void insert(HangXe hangXe) throws SQLException {
        String sql = """
                INSERT INTO HangXe (maHangXe, ten) VALUES (?, ?)""";
        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, hangXe.getMaHangXe());
            ps.setString(2, hangXe.getTen());
            ps.executeUpdate();
        }
    }

    public void update(HangXe hangXe) throws SQLException {
        String sql = """
                UPDATE HangXe SET ten = ? WHERE maHangXe = ?""";
        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, hangXe.getTen());
            ps.setString(2, hangXe.getMaHangXe());
            ps.executeUpdate();
        }
    }

    public void delete(String maHangXe) throws SQLException {
        String sql = """
                DELETE FROM HangXe WHERE maHangXe = ?""";
        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maHangXe);
            ps.executeUpdate();
        }
    }

    public boolean existsByTen(String ten) throws SQLException {
        String sql = """
                SELECT 1 FROM HangXe WHERE ten = ?""";
        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ten);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    @Override
    protected HangXe mapRow(ResultSet rs) throws SQLException {
        return new HangXe(
                rs.getString("maHangXe"),
                rs.getString("ten"));
    }
}
