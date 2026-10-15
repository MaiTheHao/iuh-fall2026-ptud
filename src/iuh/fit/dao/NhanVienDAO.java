
package iuh.fit.dao;

import iuh.fit.core.config.DatabaseConfig;
import iuh.fit.entity.NhanVien;
import iuh.fit.enumeration.TrangThaiNhanVien;
import iuh.fit.enumeration.VaiTro;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public final class NhanVienDAO extends BaseDao<NhanVien> {

    private static final String SQL_SELECT =
            "SELECT maNhanVien, tenDangNhap, matKhau, hoTen, "
            + "soDienThoai, anhDaiDien, vaiTro, trangThai, ngayTao "
            + "FROM NhanVien";

    /**
     * Tìm nhân viên theo mã.
     */
    public Optional<NhanVien> findById(String maNhanVien)
            throws SQLException {
        return findOne(
                SQL_SELECT + " WHERE maNhanVien = ?",
                maNhanVien
        );
    }

    /**
     * Lấy toàn bộ nhân viên.
     */
    public List<NhanVien> findAll() throws SQLException {
        return findAll(SQL_SELECT + " ORDER BY maNhanVien");
    }

    /**
     * Tìm nhân viên theo tên đăng nhập.
     */
    public Optional<NhanVien> findByTenDangNhap(String tenDangNhap)
            throws SQLException {

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     SQL_SELECT + " WHERE tenDangNhap = ?")) {

            ps.setString(1, tenDangNhap);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next()
                        ? Optional.of(mapRow(rs))
                        : Optional.empty();
            }
        }
    }

    /**
     * Kiểm tra tên đăng nhập đã tồn tại hay chưa.
     */
    public boolean existsByTenDangNhap(String tenDangNhap)
            throws SQLException {

        String sql =
                "SELECT 1 FROM NhanVien WHERE tenDangNhap = ?";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, tenDangNhap);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    /**
     * Tìm kiếm theo mã, họ tên, số điện thoại,
     * vai trò hoặc trạng thái.
     */
    public List<NhanVien> search(String keyword)
            throws SQLException {

        if (keyword == null || keyword.trim().isEmpty()) {
            return findAll();
        }

        String sql = SQL_SELECT
                + " WHERE LOWER(maNhanVien) LIKE ?"
                + " OR LOWER(hoTen) LIKE ?"
                + " OR LOWER(soDienThoai) LIKE ?"
                + " OR LOWER(vaiTro) LIKE ?"
                + " OR LOWER(trangThai) LIKE ?"
                + " ORDER BY maNhanVien";

        String pattern = "%" + keyword.trim().toLowerCase() + "%";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            for (int i = 1; i <= 5; i++) {
                ps.setString(i, pattern);
            }

            try (ResultSet rs = ps.executeQuery()) {
                List<NhanVien> result = new java.util.ArrayList<>();

                while (rs.next()) {
                    result.add(mapRow(rs));
                }

                return result;
            }
        }
    }

    /**
     * Đăng nhập.
     */
    public Optional<NhanVien> login(String tenDangNhap, String matKhau)
            throws SQLException {

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     SQL_SELECT
                     + " WHERE tenDangNhap = ?"
                     + " AND matKhau = ?"
                     + " AND trangThai <> ?")) {

            ps.setString(1, tenDangNhap);
            ps.setString(2, matKhau);
            ps.setString(3, TrangThaiNhanVien.DA_KHOA.name());

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next()
                        ? Optional.of(mapRow(rs))
                        : Optional.empty();
            }
        }
    }

    /**
     * Thêm nhân viên.
     */
    public void insert(NhanVien nhanVien) throws SQLException {
        register(nhanVien);
    }

    /**
     * Đăng ký nhân viên.
     * Giữ nguyên để tương thích với code hiện tại.
     */
    public void register(NhanVien nhanVien) throws SQLException {

        String sql = "INSERT INTO NhanVien "
                + "(maNhanVien, tenDangNhap, matKhau, hoTen, "
                + "soDienThoai, anhDaiDien, vaiTro, trangThai, ngayTao) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, nhanVien.getMaNhanVien());
            ps.setString(2, nhanVien.getTenDangNhap());
            ps.setString(3, nhanVien.getMatKhau());
            ps.setString(4, nhanVien.getHoTen());
            ps.setString(5, nhanVien.getSoDienThoai());
            ps.setString(6, nhanVien.getAnhDaiDien());
            ps.setString(7, nhanVien.getVaiTro().name());
            ps.setString(8, nhanVien.getTrangThai().name());
            ps.setObject(9, nhanVien.getNgayTao());

            ps.executeUpdate();
        }
    }

    /**
     * Cập nhật thông tin quản lý nhân viên.
     * Không cập nhật mật khẩu hoặc tên đăng nhập.
     */
    public void updateInfo(NhanVien nhanVien) throws SQLException {

        String sql = "UPDATE NhanVien "
                + "SET hoTen = ?, soDienThoai = ?, anhDaiDien = ?, "
                + "vaiTro = ?, trangThai = ? "
                + "WHERE maNhanVien = ?";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, nhanVien.getHoTen());
            ps.setString(2, nhanVien.getSoDienThoai());
            ps.setString(3, nhanVien.getAnhDaiDien());
            ps.setString(4, nhanVien.getVaiTro().name());
            ps.setString(5, nhanVien.getTrangThai().name());
            ps.setString(6, nhanVien.getMaNhanVien());

            ps.executeUpdate();
        }
    }

    /**
     * Cập nhật nhân viên theo cách cũ.
     * Giữ lại để tránh phá vỡ các chức năng đang gọi hàm này.
     */
    public void update(NhanVien nhanVien) throws SQLException {

        String sql = "UPDATE NhanVien "
                + "SET hoTen = ?, soDienThoai = ?, anhDaiDien = ?, "
                + "vaiTro = ?, trangThai = ?, matKhau = ? "
                + "WHERE maNhanVien = ?";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, nhanVien.getHoTen());
            ps.setString(2, nhanVien.getSoDienThoai());
            ps.setString(3, nhanVien.getAnhDaiDien());
            ps.setString(4, nhanVien.getVaiTro().name());
            ps.setString(5, nhanVien.getTrangThai().name());
            ps.setString(6, nhanVien.getMatKhau());
            ps.setString(7, nhanVien.getMaNhanVien());

            ps.executeUpdate();
        }
    }

    /**
     * Cập nhật hồ sơ cá nhân.
     * Giữ nguyên để tương thích với code hiện tại.
     */
    public void updateProfile(NhanVien nhanVien) throws SQLException {

        String sql = "UPDATE NhanVien "
                + "SET hoTen = ?, soDienThoai = ?, anhDaiDien = ? "
                + "WHERE maNhanVien = ?";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, nhanVien.getHoTen());
            ps.setString(2, nhanVien.getSoDienThoai());
            ps.setString(3, nhanVien.getAnhDaiDien());
            ps.setString(4, nhanVien.getMaNhanVien());

            ps.executeUpdate();
        }
    }

    /**
     * Cập nhật mật khẩu.
     * Giữ nguyên để tương thích với code hiện tại.
     */
    public void updatePassword(NhanVien nhanVien) throws SQLException {

        String sql =
                "UPDATE NhanVien SET matKhau = ? WHERE maNhanVien = ?";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, nhanVien.getMatKhau());
            ps.setString(2, nhanVien.getMaNhanVien());

            ps.executeUpdate();
        }
    }

    /**
     * Xóa nhân viên theo mã.
     *
     * @return true nếu có bản ghi được xóa, false nếu không tìm thấy.
     */
    public boolean delete(String maNhanVien) throws SQLException {

        String sql = "DELETE FROM NhanVien WHERE maNhanVien = ?";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, maNhanVien);

            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Chuyển dữ liệu từ ResultSet thành entity NhanVien.
     */
    @Override
    protected NhanVien mapRow(ResultSet rs) throws SQLException {

        return new NhanVien(
                rs.getString("maNhanVien"),
                rs.getString("tenDangNhap"),
                rs.getString("matKhau"),
                rs.getString("hoTen"),
                rs.getString("soDienThoai"),
                rs.getString("anhDaiDien"),
                VaiTro.valueOf(rs.getString("vaiTro")),
                TrangThaiNhanVien.valueOf(rs.getString("trangThai")),
                rs.getObject("ngayTao", LocalDateTime.class)
        );
    }
}
