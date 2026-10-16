package iuh.fit.dao;

import iuh.fit.core.config.DatabaseConfig;
import iuh.fit.entity.DichVu;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public final class DichVuDAO extends BaseDao<DichVu> {

    private static final String SQL_SELECT = """
            SELECT maDichVu, tenDichVu, moTa, anhUrl, donGia
            FROM DichVu
            """;

    public Optional<DichVu> findById(String maDichVu)
                    throws SQLException {
        return findOne(
                        SQL_SELECT + " WHERE maDichVu = ?",
                        maDichVu);
    }

    public List<DichVu> findAll() throws SQLException {
        return findAll(SQL_SELECT);
    }

    public void insert(DichVu dichVu) throws SQLException {
        String sql = """
                        INSERT INTO DichVu (maDichVu, tenDichVu, moTa, anhUrl, donGia)
                        VALUES (?, ?, ?, ?, ?)
                        """;

        try (Connection conn = DatabaseConfig.getConnection();
                        PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, dichVu.getMaDichVu());
            ps.setString(2, dichVu.getTenDichVu());
            ps.setString(3, dichVu.getMoTa());
            ps.setString(4, dichVu.getAnhUrl());
            ps.setDouble(5, dichVu.getDonGia());

            ps.executeUpdate();
        }
    }

    public void update(DichVu dichVu) throws SQLException {
        String sql = """
                        UPDATE DichVu
                        SET tenDichVu = ?,
                            moTa = ?,
                            anhUrl = ?,
                            donGia = ?
                        WHERE maDichVu = ?
                        """;

        try (Connection conn = DatabaseConfig.getConnection();
                        PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, dichVu.getTenDichVu());
            ps.setString(2, dichVu.getMoTa());
            ps.setString(3, dichVu.getAnhUrl());
            ps.setDouble(4, dichVu.getDonGia());
            ps.setString(5, dichVu.getMaDichVu());

            ps.executeUpdate();
        }
    }

    public void delete(String maDichVu) throws SQLException {
        String sql = """
                DELETE FROM DichVu
                WHERE maDichVu = ?
                """;

        try (Connection conn = DatabaseConfig.getConnection();
                        PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maDichVu);
            ps.executeUpdate();
        }
    }

    @Override
    protected DichVu mapRow(ResultSet rs) throws SQLException {
        return new DichVu(
                        rs.getString("maDichVu"),
                        rs.getString("tenDichVu"),
                        rs.getString("moTa"),
                        rs.getString("anhUrl"),
                        rs.getDouble("donGia"));
    }
}
