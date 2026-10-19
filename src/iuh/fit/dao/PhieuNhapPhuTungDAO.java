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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class PhieuNhapPhuTungDAO extends BaseDao<PhieuNhapPhuTung> {

    private static final String SQL_SELECT = """
            SELECT p.maPhieuNhap, p.ngayTao, p.maNhanVien, p.moTa
            FROM PhieuNhapPhuTung p
            """;

    private static final String SQL_FIND_ALL = """
            SELECT p.maPhieuNhap, p.ngayTao, p.maNhanVien, p.moTa,
                   ct.maPhuTung, ct.soLuong, ct.donGiaNhap
            FROM PhieuNhapPhuTung p
            LEFT JOIN ChiTietPhieuNhapPhuTung ct ON ct.maPhieuNhap = p.maPhieuNhap
            """;

    public List<PhieuNhapPhuTung> findAll() throws SQLException {
        Map<String, PhieuNhapPhuTung> map = new LinkedHashMap<>();
        try (Connection connection = DatabaseConfig.getConnection();
                PreparedStatement ps = connection.prepareStatement(SQL_FIND_ALL);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String maPhieuNhap = rs.getString("maPhieuNhap");
                PhieuNhapPhuTung phieu = map.get(maPhieuNhap);
                if (phieu == null) {
                    phieu = this.mapRow(rs);
                    map.put(maPhieuNhap, phieu);
                }

                if (rs.getString("maPhuTung") != null) {
                    ChiTietPhieuNhapPhuTung chiTiet = new ChiTietPhieuNhapPhuTung();
                    chiTiet.setMaPhieuNhap(maPhieuNhap);
                    chiTiet.setMaPhuTung(rs.getString("maPhuTung"));
                    chiTiet.setSoLuong(rs.getInt("soLuong"));
                    chiTiet.setDonGiaNhap(rs.getDouble("donGiaNhap"));
                    phieu.getDanhSachChiTietPhieuNhap().add(chiTiet);
                }
            }
        }
        return new ArrayList<>(map.values());
    }

    public Optional<PhieuNhapPhuTung> findById(TransactionScope scope, String maPhieuNhap) throws SQLException {
        Optional<PhieuNhapPhuTung> found = this.findOne(scope, SQL_SELECT + " WHERE p.maPhieuNhap = ?", maPhieuNhap);
        if (found.isEmpty()) {
            return Optional.empty();
        }
        PhieuNhapPhuTung phieu = found.get();
        phieu.setDanhSachChiTietPhieuNhap(this.loadChiTiet(scope.getConnection(), maPhieuNhap));
        return Optional.of(phieu);
    }

    public boolean insert(TransactionScope scope, PhieuNhapPhuTung phieu, List<ChiTietPhieuNhapPhuTung> chiTiets)
            throws SQLException {
        String sqlPhieu = """
                INSERT INTO PhieuNhapPhuTung (maPhieuNhap, ngayTao, maNhanVien, moTa)
                VALUES (?, ?, ?, ?)
                """;
        String sqlChiTiet = """
                INSERT INTO ChiTietPhieuNhapPhuTung (maPhieuNhap, maPhuTung, soLuong, donGiaNhap)
                VALUES (?, ?, ?, ?)
                """;

        try (PreparedStatement psPhieu = scope.getConnection().prepareStatement(sqlPhieu)) {
            psPhieu.setString(1, phieu.getMaPhieuNhap());
            psPhieu.setTimestamp(2,
                    Timestamp.valueOf(phieu.getNgayTao() != null ? phieu.getNgayTao() : LocalDateTime.now()));
            psPhieu.setString(3, phieu.getMaNhanVien());
            psPhieu.setString(4, phieu.getMoTa());
            psPhieu.executeUpdate();
        }

        if (chiTiets != null && !chiTiets.isEmpty()) {
            try (PreparedStatement psChiTiet = scope.getConnection().prepareStatement(sqlChiTiet)) {
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

        return true;
    }

    public boolean delete(TransactionScope scope, String maPhieuNhap) throws SQLException {
        String sqlXoaChiTiet = """
                DELETE FROM ChiTietPhieuNhapPhuTung
                WHERE maPhieuNhap = ?
                """;
        String sqlXoaPhieu = """
                DELETE FROM PhieuNhapPhuTung
                WHERE maPhieuNhap = ?
                """;

        try (PreparedStatement psChiTiet = scope.getConnection().prepareStatement(sqlXoaChiTiet)) {
            psChiTiet.setString(1, maPhieuNhap);
            psChiTiet.executeUpdate();
        }

        try (PreparedStatement psPhieu = scope.getConnection().prepareStatement(sqlXoaPhieu)) {
            psPhieu.setString(1, maPhieuNhap);
            return psPhieu.executeUpdate() > 0;
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
