package iuh.fit.dao;

import iuh.fit.core.config.DatabaseConfig;
import iuh.fit.entity.PhieuTiepNhan;
import iuh.fit.entity.VanDeKyThuat;
import iuh.fit.enumeration.TrangThaiVanDeKyThuat;
import iuh.fit.enumeration.TrangThaiPhieuTiepNhan;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class PhieuTiepNhanDAO extends BaseDao<PhieuTiepNhan> {

    private static final String SQL_SELECT = """
            SELECT maPhieuTiepNhan, ngayTao, moTa, trangThai,
                   maNhanVien, maKhachHang, bienSo
            FROM PhieuTiepNhan
            """;

    public Optional<PhieuTiepNhan> findById(String maPhieuTiepNhan) throws SQLException {
        try (Connection connection = DatabaseConfig.getConnection()) {
            Optional<PhieuTiepNhan> found = this.findOne(connection, SQL_SELECT + " WHERE maPhieuTiepNhan = ?",
                    maPhieuTiepNhan);
            if (found.isEmpty()) {
                return Optional.empty();
            }
            PhieuTiepNhan phieuTiepNhan = found.get();
            phieuTiepNhan.setDanhSachVanDeKyThuat(this.loadDanhSachVanDeKyThuat(connection, maPhieuTiepNhan));
            return Optional.of(phieuTiepNhan);
        }
    }

    public Optional<PhieuTiepNhan> findById(TransactionScope scope, String maPhieuTiepNhan) throws SQLException {
        Optional<PhieuTiepNhan> found = this.findOne(scope, SQL_SELECT + " WHERE maPhieuTiepNhan = ?",
                maPhieuTiepNhan);
        if (found.isEmpty()) {
            return Optional.empty();
        }
        PhieuTiepNhan phieuTiepNhan = found.get();
        phieuTiepNhan.setDanhSachVanDeKyThuat(
                this.loadDanhSachVanDeKyThuat(scope.getConnection(), maPhieuTiepNhan));
        return Optional.of(phieuTiepNhan);
    }

    public List<PhieuTiepNhan> findAll() throws SQLException {
        return this.findAll(SQL_SELECT);
    }

    @Override
    protected PhieuTiepNhan mapRow(ResultSet rs) throws SQLException {
        PhieuTiepNhan phieuTiepNhan = new PhieuTiepNhan();
        phieuTiepNhan.setMaPhieuTiepNhan(rs.getString("maPhieuTiepNhan"));
        phieuTiepNhan.setNgayTao(rs.getObject("ngayTao", LocalDateTime.class));
        phieuTiepNhan.setMoTa(rs.getString("moTa"));
        phieuTiepNhan.setTrangThai(TrangThaiPhieuTiepNhan.valueOf(rs.getString("trangThai")));
        phieuTiepNhan.setMaNhanVien(rs.getString("maNhanVien"));
        phieuTiepNhan.setMaKhachHang(rs.getString("maKhachHang"));
        phieuTiepNhan.setBienSo(rs.getString("bienSo"));
        return phieuTiepNhan;
    }

    private List<VanDeKyThuat> loadDanhSachVanDeKyThuat(Connection connection, String maPhieuTiepNhan)
            throws SQLException {
        String sqlCon = """
                SELECT maVanDe, maPhieuTiepNhan, tieuDe, moTa,
                       mucDoUuTien, giaiPhap, khachHangDongY, ngayTao
                FROM VanDeKyThuat
                WHERE maPhieuTiepNhan = ?
                """;
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
                    vanDe.setKhachHangDongY(TrangThaiVanDeKyThuat.valueOf(rs.getString("khachHangDongY")));
                    vanDe.setNgayTao(rs.getObject("ngayTao", LocalDateTime.class));
                    danhSach.add(vanDe);
                }
            }
        }
        return danhSach;
    }

    public boolean insert(PhieuTiepNhan phieu) throws SQLException {
        try (TransactionScope scope = TransactionScope.begin()) {
            boolean inserted = this.insert(scope, phieu);
            scope.commit();
            return inserted;
        }
    }

    public boolean insert(TransactionScope scope, PhieuTiepNhan phieu) throws SQLException {
        String sql = """
                INSERT INTO PhieuTiepNhan (maPhieuTiepNhan, ngayTao, moTa, trangThai, maNhanVien, maKhachHang, bienSo)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement ps = scope.getConnection().prepareStatement(sql)) {
            ps.setString(1, phieu.getMaPhieuTiepNhan());
            ps.setObject(2, phieu.getNgayTao() != null ? phieu.getNgayTao() : LocalDateTime.now());
            ps.setString(3, phieu.getMoTa());
            ps.setString(4, phieu.getTrangThai() != null
                    ? phieu.getTrangThai().name()
                    : TrangThaiPhieuTiepNhan.TIEP_NHAN.name());
            ps.setString(5, phieu.getMaNhanVien());
            ps.setString(6, phieu.getMaKhachHang());
            ps.setString(7, phieu.getBienSo());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean update(PhieuTiepNhan phieu, TrangThaiPhieuTiepNhan trangThaiCu) throws SQLException {
        try (TransactionScope scope = TransactionScope.begin()) {
            boolean updated = this.update(scope, phieu, trangThaiCu);
            scope.commit();
            return updated;
        }
    }

    public boolean update(TransactionScope scope, PhieuTiepNhan phieu, TrangThaiPhieuTiepNhan trangThaiCu)
            throws SQLException {
        String sql = """
                UPDATE PhieuTiepNhan
                SET moTa = ?, trangThai = ?
                WHERE maPhieuTiepNhan = ? AND trangThai = ?
                """;

        try (PreparedStatement ps = scope.getConnection().prepareStatement(sql)) {
            ps.setString(1, phieu.getMoTa());
            ps.setString(2, phieu.getTrangThai().name());
            ps.setString(3, phieu.getMaPhieuTiepNhan());
            ps.setString(4, trangThaiCu.name());
            return ps.executeUpdate() > 0;
        }
    }
}
