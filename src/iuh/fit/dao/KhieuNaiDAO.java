package iuh.fit.dao;

import iuh.fit.entity.KhieuNai;
import iuh.fit.enumeration.TrangThaiKhieuNai;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public final class KhieuNaiDAO extends BaseDao<KhieuNai> {

    private static final String SQL_SELECT = """
            SELECT maKhieuNai, maKhachHang, maPhieuTiepNhan, noiDung, trangThai, ngayTao
            FROM KhieuNai
            """;

    @Override
    protected KhieuNai mapRow(ResultSet rs) throws SQLException {
        KhieuNai khieuNai = new KhieuNai();
        khieuNai.setMaKhieuNai(rs.getString("maKhieuNai"));
        khieuNai.setMaKhachHang(rs.getString("maKhachHang"));
        khieuNai.setMaPhieuTiepNhan(rs.getString("maPhieuTiepNhan"));
        khieuNai.setNoiDung(rs.getString("noiDung"));
        khieuNai.setTrangThai(TrangThaiKhieuNai.valueOf(rs.getString("trangThai")));
        khieuNai.setNgayTao(rs.getObject("ngayTao", LocalDateTime.class));
        return khieuNai;
    }

    public List<KhieuNai> findAll() throws SQLException {
        return this.findAll(SQL_SELECT);
    }

    public Optional<KhieuNai> findById(String maKhieuNai) throws SQLException {
        return this.findOne(SQL_SELECT + " WHERE maKhieuNai = ?", maKhieuNai);
    }

    public boolean insert(KhieuNai khieuNai) throws SQLException {
        try (TransactionScope scope = TransactionScope.begin()) {
            boolean inserted = this.insert(scope, khieuNai);
            scope.commit();
            return inserted;
        }
    }

    private boolean insert(TransactionScope scope, KhieuNai khieuNai) throws SQLException {
        String sql = """
                INSERT INTO KhieuNai (maKhieuNai, maKhachHang, maPhieuTiepNhan, noiDung, trangThai, ngayTao)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement ps = scope.getConnection().prepareStatement(sql)) {
            ps.setString(1, khieuNai.getMaKhieuNai());
            ps.setString(2, khieuNai.getMaKhachHang());
            ps.setString(3, khieuNai.getMaPhieuTiepNhan());
            ps.setString(4, khieuNai.getNoiDung());
            ps.setString(5, khieuNai.getTrangThai().name());
            ps.setObject(6, khieuNai.getNgayTao() != null ? khieuNai.getNgayTao() : LocalDateTime.now());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean update(KhieuNai khieuNai) throws SQLException {
        try (TransactionScope scope = TransactionScope.begin()) {
            boolean updated = this.update(scope, khieuNai);
            scope.commit();
            return updated;
        }
    }

    private boolean update(TransactionScope scope, KhieuNai khieuNai) throws SQLException {
        String sql = """
                UPDATE KhieuNai
                SET noiDung = ?, trangThai = ?
                WHERE maKhieuNai = ?
                """;

        try (PreparedStatement ps = scope.getConnection().prepareStatement(sql)) {
            ps.setString(1, khieuNai.getNoiDung());
            ps.setString(2, khieuNai.getTrangThai().name());
            ps.setString(3, khieuNai.getMaKhieuNai());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(String maKhieuNai) throws SQLException {
        try (TransactionScope scope = TransactionScope.begin()) {
            boolean deleted = this.delete(scope, maKhieuNai);
            scope.commit();
            return deleted;
        }
    }

    private boolean delete(TransactionScope scope, String maKhieuNai) throws SQLException {
        String sql = """
                DELETE FROM KhieuNai
                WHERE maKhieuNai = ?
                """;

        try (PreparedStatement ps = scope.getConnection().prepareStatement(sql)) {
            ps.setString(1, maKhieuNai);
            return ps.executeUpdate() > 0;
        }
    }
}
