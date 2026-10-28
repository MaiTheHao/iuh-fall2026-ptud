package iuh.fit.dao;

import iuh.fit.core.config.DatabaseConfig;
import iuh.fit.entity.KhachHang;
import iuh.fit.entity.Xe;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class KhachHangDAO extends BaseDao<KhachHang> {

    private static final String SQL_SELECT = """
            SELECT maKhachHang, sdt, hoTen, ngayTao
            FROM KhachHang
            """;

    public Optional<KhachHang> findById(String maKhachHang) throws SQLException {
        try (Connection conn = DatabaseConfig.getConnection()) {
            Optional<KhachHang> found = this.findOne(conn, SQL_SELECT + " WHERE maKhachHang = ?", maKhachHang);
            if (found.isEmpty()) {
                return Optional.empty();
            }
            KhachHang khachHang = found.get();
            khachHang.setDanhSachXe(this.loadDanhSachXe(conn, maKhachHang));
            return Optional.of(khachHang);
        }
    }

    public Optional<KhachHang> findById(TransactionScope scope, String maKhachHang) throws SQLException {
        Optional<KhachHang> found = this.findOne(scope, SQL_SELECT + " WHERE maKhachHang = ?", maKhachHang);
        if (found.isEmpty()) {
            return Optional.empty();
        }
        KhachHang khachHang = found.get();
        khachHang.setDanhSachXe(this.loadDanhSachXe(scope.getConnection(), maKhachHang));
        return Optional.of(khachHang);
    }

    public Optional<KhachHang> findBySdt(String sdt) throws SQLException {
        String sql = SQL_SELECT + " WHERE sdt = ? ORDER BY maKhachHang";
        return this.findOne(sql, sdt);
    }

    public Optional<KhachHang> findBySdt(TransactionScope scope, String sdt) throws SQLException {
        String sql = SQL_SELECT + " WHERE sdt = ? ORDER BY maKhachHang";
        return this.findOne(scope, sql, sdt);
    }

    public boolean existsBySdt(String sdt) throws SQLException {
        String sql = """
                SELECT 1 FROM KhachHang
                WHERE sdt = ?
                """;
        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, sdt);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean existsPhieuTiepNhan(String maKhachHang) throws SQLException {
        String sql = """
                SELECT 1 FROM PhieuTiepNhan
                WHERE maKhachHang = ?
                """;
        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maKhachHang);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean existsKhieuNai(String maKhachHang) throws SQLException {
        String sql = """
                SELECT 1 FROM KhieuNai
                WHERE maKhachHang = ?
                """;
        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maKhachHang);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public List<KhachHang> findAll() throws SQLException {
        return this.findAll(SQL_SELECT);
    }

    public void insert(KhachHang khachHang) throws SQLException {
        try (TransactionScope scope = TransactionScope.begin()) {
            this.insert(scope, khachHang);
            scope.commit();
        }
    }

    public void insert(TransactionScope scope, KhachHang khachHang) throws SQLException {
        String sql = """
                INSERT INTO KhachHang (maKhachHang, sdt, hoTen, ngayTao)
                VALUES (?, ?, ?, ?)
                """;
        try (PreparedStatement ps = scope.getConnection().prepareStatement(sql)) {
            ps.setString(1, khachHang.getMaKhachHang());
            ps.setString(2, khachHang.getSdt());
            ps.setString(3, khachHang.getHoTen());
            ps.setObject(4, khachHang.getNgayTao());
            ps.executeUpdate();
        }
    }

    public void update(KhachHang khachHang) throws SQLException {
        try (TransactionScope scope = TransactionScope.begin()) {
            this.update(scope, khachHang);
            scope.commit();
        }
    }

    private void update(TransactionScope scope, KhachHang khachHang) throws SQLException {
        String sql = """
                UPDATE KhachHang
                SET sdt = ?, hoTen = ?
                WHERE maKhachHang = ?
                """;
        try (PreparedStatement ps = scope.getConnection().prepareStatement(sql)) {
            ps.setString(1, khachHang.getSdt());
            ps.setString(2, khachHang.getHoTen());
            ps.setString(3, khachHang.getMaKhachHang());
            ps.executeUpdate();
        }
    }

    public void delete(String maKhachHang) throws SQLException {
        try (TransactionScope scope = TransactionScope.begin()) {
            this.delete(scope, maKhachHang);
            scope.commit();
        }
    }

    private void delete(TransactionScope scope, String maKhachHang) throws SQLException {
        String sql = """
                DELETE FROM KhachHang
                WHERE maKhachHang = ?
                """;
        try (PreparedStatement ps = scope.getConnection().prepareStatement(sql)) {
            ps.setString(1, maKhachHang);
            ps.executeUpdate();
        }
    }

    @Override
    protected KhachHang mapRow(ResultSet rs) throws SQLException {
        return new KhachHang(
                rs.getString("maKhachHang"),
                rs.getString("sdt"),
                rs.getString("hoTen"),
                rs.getObject("ngayTao", LocalDateTime.class));
    }

    private List<Xe> loadDanhSachXe(Connection conn, String maKhachHang) throws SQLException {
        String sql = """
                SELECT x.bienSo, x.hangXe, x.dongXe, x.doiXe, x.odo
                FROM Xe x
                JOIN KhachHang_Xe khx ON khx.bienSo = x.bienSo
                WHERE khx.maKhachHang = ?
                """;
        List<Xe> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maKhachHang);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Xe(
                            rs.getString("bienSo"),
                            rs.getString("hangXe"),
                            rs.getString("dongXe"),
                            rs.getInt("doiXe"),
                            rs.getInt("odo")));
                }
            }
        }
        return list;
    }
}
