package iuh.fit.dao;

import iuh.fit.core.config.DatabaseConfig;
import iuh.fit.entity.ChiTietHoaDon;
import iuh.fit.entity.HoaDon;
import iuh.fit.enumeration.LoaiSanPham;
import iuh.fit.enumeration.TrangThaiHoaDon;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class HoaDonDAO extends BaseDao<HoaDon> {

    private static final String SQL_SELECT = """
            SELECT maHoaDon, maPhieuTiepNhan, maVoucher, trangThaiHoaDon,
                   lyDoHuy, ghiChu, ngayTao, thoiGianThanhToan
            FROM HoaDon
            """;

    private static final String SQL_SELECT_CHI_TIET = """
            SELECT maHoaDon, loaiSanPham, maSanPham, soLuong, donGia
            FROM ChiTietHoaDon
            """;

    public Optional<HoaDon> findById(String maHoaDon) throws SQLException {
        try (Connection connection = DatabaseConfig.getConnection()) {
            Optional<HoaDon> found = findOne(connection, SQL_SELECT + " WHERE maHoaDon = ?", maHoaDon);
            if (found.isEmpty()) {
                return Optional.empty();
            }
            HoaDon hoaDon = found.get();
            hoaDon.setDanhSachChiTietHoaDon(loadDanhSachChiTiet(connection, maHoaDon));
            return Optional.of(hoaDon);
        }
    }

    public Optional<HoaDon> findByPhieuTiepNhan(String maPhieuTiepNhan) throws SQLException {
        if (maPhieuTiepNhan == null || maPhieuTiepNhan.isBlank()) {
            return Optional.empty();
        }
        try (Connection connection = DatabaseConfig.getConnection()) {
            Optional<HoaDon> found = findOne(connection,
                    SQL_SELECT + " WHERE maPhieuTiepNhan = ? ORDER BY ngayTao DESC", maPhieuTiepNhan);
            if (found.isEmpty()) {
                return Optional.empty();
            }
            HoaDon hoaDon = found.get();
            hoaDon.setDanhSachChiTietHoaDon(loadDanhSachChiTiet(connection, hoaDon.getMaHoaDon()));
            return Optional.of(hoaDon);
        }
    }

    public List<HoaDon> findAll() throws SQLException {
        return findAll(SQL_SELECT);
    }

    public List<HoaDon> findAllWithDetails() throws SQLException {
        try (Connection connection = DatabaseConfig.getConnection()) {
            List<HoaDon> hoaDons;
            try (PreparedStatement ps = connection.prepareStatement(SQL_SELECT);
                    ResultSet rs = ps.executeQuery()) {
                hoaDons = this.mapRows(rs);
            }

            Map<String, List<ChiTietHoaDon>> chiTietTheoHoaDon = this.loadChiTietTheoHoaDon(connection);
            for (HoaDon hoaDon : hoaDons) {
                hoaDon.setDanhSachChiTietHoaDon(
                        chiTietTheoHoaDon.getOrDefault(hoaDon.getMaHoaDon(), new ArrayList<>()));
            }
            return hoaDons;
        }
    }

    public boolean insert(TransactionScope scope, HoaDon hoaDon, List<ChiTietHoaDon> chiTiets)
            throws SQLException {
        String sqlHoaDon = """
                INSERT INTO HoaDon (maHoaDon, maPhieuTiepNhan, maVoucher, trangThaiHoaDon,
                                    lyDoHuy, ghiChu, ngayTao, thoiGianThanhToan)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (PreparedStatement ps = scope.getConnection().prepareStatement(sqlHoaDon)) {
            ps.setString(1, hoaDon.getMaHoaDon());
            ps.setString(2, hoaDon.getMaPhieuTiepNhan());
            ps.setString(3, hoaDon.getMaVoucher());
            ps.setString(4, hoaDon.getTrangThaiHoaDon().name());
            ps.setString(5, hoaDon.getLyDoHuy());
            ps.setString(6, hoaDon.getGhiChu());
            LocalDateTime ngayTao = hoaDon.getNgayTao() != null ? hoaDon.getNgayTao() : LocalDateTime.now();
            ps.setTimestamp(7, Timestamp.valueOf(ngayTao));
            if (hoaDon.getThoiGianThanhToan() != null) {
                ps.setTimestamp(8, Timestamp.valueOf(hoaDon.getThoiGianThanhToan()));
            } else {
                ps.setNull(8, Types.TIMESTAMP);
            }
            ps.executeUpdate();
        }

        this.insertChiTiet(scope, hoaDon.getMaHoaDon(), chiTiets);
        return true;
    }

    public void insertChiTiet(TransactionScope scope, String maHoaDon, List<ChiTietHoaDon> chiTiets)
            throws SQLException {
        if (chiTiets == null || chiTiets.isEmpty()) {
            return;
        }
        String sql = """
                INSERT INTO ChiTietHoaDon (maHoaDon, loaiSanPham, maSanPham, soLuong, donGia)
                VALUES (?, ?, ?, ?, ?)
                """;
        try (PreparedStatement ps = scope.getConnection().prepareStatement(sql)) {
            for (ChiTietHoaDon ct : chiTiets) {
                ps.setString(1, maHoaDon);
                ps.setString(2, ct.getLoaiSanPham().name());
                ps.setString(3, ct.getMaSanPham());
                ps.setInt(4, ct.getSoLuong());
                if (ct.getDonGia() != null) {
                    ps.setDouble(5, ct.getDonGia());
                } else {
                    ps.setNull(5, Types.DOUBLE);
                }
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    public void deleteChiTiet(TransactionScope scope, String maHoaDon) throws SQLException {
        String sql = "DELETE FROM ChiTietHoaDon WHERE maHoaDon = ?";
        try (PreparedStatement ps = scope.getConnection().prepareStatement(sql)) {
            ps.setString(1, maHoaDon);
            ps.executeUpdate();
        }
    }

    public boolean updateThongTin(TransactionScope scope, String maHoaDon, String maVoucher, String ghiChu)
            throws SQLException {
        String sql = """
                UPDATE HoaDon
                SET maVoucher = ?, ghiChu = ?
                WHERE maHoaDon = ? AND trangThaiHoaDon = ?
                """;
        try (PreparedStatement ps = scope.getConnection().prepareStatement(sql)) {
            if (maVoucher == null || maVoucher.isBlank()) {
                ps.setNull(1, Types.VARCHAR);
            } else {
                ps.setString(1, maVoucher);
            }
            if (ghiChu == null || ghiChu.isBlank()) {
                ps.setNull(2, Types.VARCHAR);
            } else {
                ps.setString(2, ghiChu);
            }
            ps.setString(3, maHoaDon);
            ps.setString(4, TrangThaiHoaDon.CHO_THANH_TOAN.name());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean huy(TransactionScope scope, String maHoaDon, String lyDoHuy) throws SQLException {
        String sql = """
                UPDATE HoaDon
                SET trangThaiHoaDon = ?, lyDoHuy = ?
                WHERE maHoaDon = ? AND trangThaiHoaDon = ?
                """;
        try (PreparedStatement ps = scope.getConnection().prepareStatement(sql)) {
            ps.setString(1, TrangThaiHoaDon.DA_HUY.name());
            ps.setString(2, lyDoHuy);
            ps.setString(3, maHoaDon);
            ps.setString(4, TrangThaiHoaDon.CHO_THANH_TOAN.name());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean capNhatThanhToan(TransactionScope scope, String maHoaDon, LocalDateTime thoiGian)
            throws SQLException {
        String sql = """
                UPDATE HoaDon
                SET trangThaiHoaDon = ?, thoiGianThanhToan = ?
                WHERE maHoaDon = ?
                """;
        try (PreparedStatement ps = scope.getConnection().prepareStatement(sql)) {
            ps.setString(1, TrangThaiHoaDon.DA_THANH_TOAN.name());
            ps.setTimestamp(2, Timestamp.valueOf(thoiGian != null ? thoiGian : LocalDateTime.now()));
            ps.setString(3, maHoaDon);
            return ps.executeUpdate() > 0;
        }
    }
    
    @Override
    protected HoaDon mapRow(ResultSet rs) throws SQLException {
        HoaDon hoaDon = new HoaDon();
        hoaDon.setMaHoaDon(rs.getString("maHoaDon"));
        hoaDon.setMaPhieuTiepNhan(rs.getString("maPhieuTiepNhan"));
        hoaDon.setMaVoucher(rs.getString("maVoucher"));
        hoaDon.setTrangThaiHoaDon(TrangThaiHoaDon.valueOf(rs.getString("trangThaiHoaDon")));
        hoaDon.setLyDoHuy(rs.getString("lyDoHuy"));
        hoaDon.setGhiChu(rs.getString("ghiChu"));
        hoaDon.setNgayTao(rs.getObject("ngayTao", LocalDateTime.class));
        hoaDon.setThoiGianThanhToan(rs.getObject("thoiGianThanhToan", LocalDateTime.class));
        return hoaDon;
    }

    private Map<String, List<ChiTietHoaDon>> loadChiTietTheoHoaDon(Connection connection) throws SQLException {
        Map<String, List<ChiTietHoaDon>> chiTietTheoHoaDon = new HashMap<>();
        try (PreparedStatement ps = connection.prepareStatement(SQL_SELECT_CHI_TIET);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ChiTietHoaDon chiTiet = this.mapChiTiet(rs);
                chiTietTheoHoaDon
                        .computeIfAbsent(chiTiet.getMaHoaDon(), ma -> new ArrayList<>())
                        .add(chiTiet);
            }
        }
        return chiTietTheoHoaDon;
    }

    private List<ChiTietHoaDon> loadDanhSachChiTiet(Connection connection, String maHoaDon) throws SQLException {
        List<ChiTietHoaDon> chiTietList = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(SQL_SELECT_CHI_TIET + " WHERE maHoaDon = ?")) {
            ps.setString(1, maHoaDon);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    chiTietList.add(this.mapChiTiet(rs));
                }
            }
        }
        return chiTietList;
    }

    private ChiTietHoaDon mapChiTiet(ResultSet rs) throws SQLException {
        ChiTietHoaDon chiTiet = new ChiTietHoaDon();
        chiTiet.setMaHoaDon(rs.getString("maHoaDon"));
        chiTiet.setLoaiSanPham(LoaiSanPham.valueOf(rs.getString("loaiSanPham")));
        chiTiet.setMaSanPham(rs.getString("maSanPham"));
        chiTiet.setSoLuong(rs.getInt("soLuong"));
        chiTiet.setDonGia(rs.getDouble("donGia"));
        return chiTiet;
    }
}