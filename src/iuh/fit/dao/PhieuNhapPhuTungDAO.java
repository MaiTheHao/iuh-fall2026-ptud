package iuh.fit.dao;

import iuh.fit.core.config.DatabaseConfig;
import iuh.fit.entity.ChiTietPhieuNhapPhuTung;
import iuh.fit.entity.PhieuNhapPhuTung;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class PhieuNhapPhuTungDAO extends BaseDao<PhieuNhapPhuTung> {

    private static final String SQL_SELECT = """
            SELECT maPhieuNhap, ngayTao, maNhanVien, moTa
            FROM PhieuNhapPhuTung
            """;

    public Optional<PhieuNhapPhuTung> findById(String maPhieuNhap) throws SQLException {
        try (Connection connection = DatabaseConfig.getConnection()) {
            Optional<PhieuNhapPhuTung> found = findOne(connection, SQL_SELECT + " WHERE maPhieuNhap = ?", maPhieuNhap);
            if (found.isEmpty()) {
                return Optional.empty();
            }
            PhieuNhapPhuTung phieu = found.get();
            phieu.setDanhSachChiTietPhieuNhap(loadChiTiet(connection, maPhieuNhap));
            return Optional.of(phieu);
        }
    }

    public List<PhieuNhapPhuTung> findAll() throws SQLException {
        return findAll(SQL_SELECT);
    }

    public boolean existsById(String maPhieuNhap) throws SQLException {
        String sql = """
                SELECT 1 FROM PhieuNhapPhuTung
                WHERE maPhieuNhap = ?
                """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maPhieuNhap);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean insert(PhieuNhapPhuTung phieu, List<ChiTietPhieuNhapPhuTung> chiTiets) throws SQLException {
        String sqlPhieu = """
                INSERT INTO PhieuNhapPhuTung (maPhieuNhap, ngayTao, maNhanVien, moTa)
                VALUES (?, ?, ?, ?)
                """;
        String sqlChiTiet = """
                INSERT INTO ChiTietPhieuNhapPhuTung (maPhieuNhap, maPhuTung, soLuong, donGiaNhap)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection connection = DatabaseConfig.getConnection()) {
            boolean autoCommit = connection.getAutoCommit();
            try {
                connection.setAutoCommit(false);

                try (PreparedStatement psPhieu = connection.prepareStatement(sqlPhieu)) {
                    psPhieu.setString(1, phieu.getMaPhieuNhap());
                    psPhieu.setTimestamp(2, Timestamp.valueOf(phieu.getNgayTao() != null ? phieu.getNgayTao() : LocalDateTime.now()));
                    psPhieu.setString(3, phieu.getMaNhanVien());
                    psPhieu.setString(4, phieu.getMoTa());
                    psPhieu.executeUpdate();
                }

                if (chiTiets != null && !chiTiets.isEmpty()) {
                    try (PreparedStatement psChiTiet = connection.prepareStatement(sqlChiTiet)) {
                        for (ChiTietPhieuNhapPhuTung ct : chiTiets) {
                            psChiTiet.setString(1, phieu.getMaPhieuNhap());
                            psChiTiet.setString(2, ct.getMaPhuTung());
                            psChiTiet.setInt(3, ct.getSoLuong());
                            psChiTiet.setDouble(4, ct.getDonGiaNhap());
                            psChiTiet.addBatch();
                        }
                        psChiTiet.executeBatch();
                    }
                }

                connection.commit();
                return true;
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(autoCommit);
            }
        }
    }

    public boolean delete(String maPhieuNhap) throws SQLException {
        String sqlXoaChiTiet = """
                DELETE FROM ChiTietPhieuNhapPhuTung
                WHERE maPhieuNhap = ?
                """;
        String sqlXoaPhieu = """
                DELETE FROM PhieuNhapPhuTung
                WHERE maPhieuNhap = ?
                """;

        try (Connection connection = DatabaseConfig.getConnection()) {
            boolean autoCommit = connection.getAutoCommit();
            try {
                connection.setAutoCommit(false);

                try (PreparedStatement psChiTiet = connection.prepareStatement(sqlXoaChiTiet)) {
                    psChiTiet.setString(1, maPhieuNhap);
                    psChiTiet.executeUpdate();
                }

                int affected;
                try (PreparedStatement psPhieu = connection.prepareStatement(sqlXoaPhieu)) {
                    psPhieu.setString(1, maPhieuNhap);
                    affected = psPhieu.executeUpdate();
                }

                connection.commit();
                return affected > 0;
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(autoCommit);
            }
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

    private List<ChiTietPhieuNhapPhuTung> loadChiTiet(Connection connection, String maPhieuNhap) throws SQLException {
        String sqlChiTiet = """
                SELECT maPhieuNhap, maPhuTung, soLuong, donGiaNhap
                FROM ChiTietPhieuNhapPhuTung
                WHERE maPhieuNhap = ?
                """;
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