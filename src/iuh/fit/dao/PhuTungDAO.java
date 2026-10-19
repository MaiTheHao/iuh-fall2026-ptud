package iuh.fit.dao;

import iuh.fit.core.config.DatabaseConfig;
import iuh.fit.entity.PhuTung;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public final class PhuTungDAO extends BaseDao<PhuTung> {

    private static final String SQL_SELECT = """
            SELECT maPhuTung, tenPhuTung, moTa, anhUrl,
                   donGiaNhap, soLuongTon
            FROM PhuTung
            """;

    public Optional<PhuTung> findById(String maPhuTung)
            throws SQLException {
        return findOne(
                SQL_SELECT + " WHERE maPhuTung = ?",
                maPhuTung);
    }

    public List<PhuTung> findAll() throws SQLException {
        return findAll(SQL_SELECT);
    }

    public void insert(PhuTung phuTung) throws SQLException {
        String sql = """
                INSERT INTO PhuTung (maPhuTung, tenPhuTung, moTa, anhUrl, donGiaNhap, soLuongTon)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, phuTung.getMaPhuTung());
            ps.setString(2, phuTung.getTenPhuTung());
            ps.setString(3, phuTung.getMoTa());
            ps.setString(4, phuTung.getAnhUrl());
            ps.setDouble(5, phuTung.getDonGiaNhap());
            ps.setInt(6, phuTung.getSoLuongTon());

            ps.executeUpdate();
        }
    }

    public void update(PhuTung phuTung) throws SQLException {
        String sql = """
                UPDATE PhuTung
                SET tenPhuTung = ?,
                    moTa = ?,
                    anhUrl = ?,
                    donGiaNhap = ?
                WHERE maPhuTung = ?
                """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, phuTung.getTenPhuTung());
            ps.setString(2, phuTung.getMoTa());
            ps.setString(3, phuTung.getAnhUrl());
            ps.setDouble(4, phuTung.getDonGiaNhap());
            ps.setString(5, phuTung.getMaPhuTung());

            ps.executeUpdate();
        }
    }

    public void delete(String maPhuTung) throws SQLException {
        String sql = """
                DELETE FROM PhuTung
                WHERE maPhuTung = ?
                """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maPhuTung);
            ps.executeUpdate();
        }
    }

    public boolean increaseStock(TransactionScope scope, String maPhuTung, int soLuong) throws SQLException {
        String sql = """
                UPDATE PhuTung
                SET soLuongTon = soLuongTon + ?
                WHERE maPhuTung = ?
                """;

        try (PreparedStatement ps = scope.getConnection().prepareStatement(sql)) {
            ps.setInt(1, soLuong);
            ps.setString(2, maPhuTung);

            return ps.executeUpdate() > 0;
        }
    }

    public boolean decreaseStock(TransactionScope scope, String maPhuTung, int soLuong) throws SQLException {
        String sql = """
                UPDATE PhuTung
                SET soLuongTon = soLuongTon - ?
                WHERE maPhuTung = ?
                """;

        try (PreparedStatement ps = scope.getConnection().prepareStatement(sql)) {
            ps.setInt(1, soLuong);
            ps.setString(2, maPhuTung);

            return ps.executeUpdate() > 0;
        }
    }

    @Override
    protected PhuTung mapRow(ResultSet rs) throws SQLException {
        return new PhuTung(
                rs.getString("maPhuTung"),
                rs.getString("tenPhuTung"),
                rs.getString("moTa"),
                rs.getString("anhUrl"),
                rs.getDouble("donGiaNhap"),
                rs.getInt("soLuongTon"));
    }
}
