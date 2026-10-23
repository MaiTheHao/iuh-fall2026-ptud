
package iuh.fit.dao;

import iuh.fit.core.config.DatabaseConfig;
import iuh.fit.entity.PhanCong;
import iuh.fit.enumeration.CaLamViec;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class PhanCongDAO extends BaseDao<PhanCong> {

	private static final String SQL_SELECT = """
			SELECT maPhanCong, maNhanVien, ngay, thang, nam, caLamViec, ghiChu
			FROM PhanCong
			""";

	private static final String SQL_INSERT = """
			INSERT INTO PhanCong (
			    maPhanCong, maNhanVien, ngay, thang, nam, caLamViec, ghiChu
			)
			VALUES (?, ?, ?, ?, ?, ?, ?)
			""";

	private static final String SQL_DELETE = """
			DELETE FROM PhanCong
			WHERE maPhanCong = ?
			""";

	private static final String SQL_UPDATE = """
			UPDATE PhanCong
			SET maNhanVien = ?,
			    ngay = ?,
			    thang = ?,
			    nam = ?,
			    caLamViec = ?,
			    ghiChu = ?
			WHERE maPhanCong = ?
			""";

	public Optional<PhanCong> findById(String maPhanCong) throws SQLException {
		return this.findOne(SQL_SELECT + " WHERE maPhanCong = ?", maPhanCong);
	}

	public List<PhanCong> findAll() throws SQLException {
		return this.findAll(SQL_SELECT);
	}

	public boolean insert(PhanCong phanCong) throws SQLException {
		try (Connection connection = DatabaseConfig.getConnection();
				PreparedStatement ps = connection.prepareStatement(SQL_INSERT)) {

			ps.setString(1, phanCong.getMaPhanCong());
			ps.setString(2, phanCong.getMaNhanVien());
			ps.setInt(3, phanCong.getNgay());
			ps.setInt(4, phanCong.getThang());
			ps.setInt(5, phanCong.getNam());
			ps.setString(6, phanCong.getCaLamViec().name());
			ps.setString(7, phanCong.getGhiChu());

			return ps.executeUpdate() > 0;
		}
	}

	public boolean delete(String maPhanCong) throws SQLException {
		try (Connection connection = DatabaseConfig.getConnection();
				PreparedStatement ps = connection.prepareStatement(SQL_DELETE)) {

			ps.setString(1, maPhanCong);

			return ps.executeUpdate() > 0;
		}
	}

	public boolean update(PhanCong phanCong) throws SQLException {
		try (Connection connection = DatabaseConfig.getConnection();
				PreparedStatement ps = connection.prepareStatement(SQL_UPDATE)) {

			ps.setString(1, phanCong.getMaNhanVien());
			ps.setInt(2, phanCong.getNgay());
			ps.setInt(3, phanCong.getThang());
			ps.setInt(4, phanCong.getNam());
			ps.setString(5, phanCong.getCaLamViec().name());
			ps.setString(6, phanCong.getGhiChu());
			ps.setString(7, phanCong.getMaPhanCong());

			return ps.executeUpdate() > 0;
		}
	}

	public List<PhanCong> findByDateRange(LocalDate tuNgay, LocalDate denNgay) throws SQLException {

		String sql = SQL_SELECT + """
				WHERE DATEFROMPARTS(nam, thang, ngay) BETWEEN ? AND ?
				ORDER BY nam, thang, ngay, caLamViec, maNhanVien
				""";

		try (Connection connection = DatabaseConfig.getConnection();
				PreparedStatement ps = connection.prepareStatement(sql)) {

			ps.setObject(1, tuNgay);
			ps.setObject(2, denNgay);

			try (ResultSet rs = ps.executeQuery()) {
				List<PhanCong> danhSach = new ArrayList<>();

				while (rs.next()) {
					danhSach.add(this.mapRow(rs));
				}

				return danhSach;
			}
		}
	}

	@Override
	protected PhanCong mapRow(ResultSet rs) throws SQLException {
		PhanCong phanCong = new PhanCong();

		phanCong.setMaPhanCong(rs.getString("maPhanCong"));
		phanCong.setMaNhanVien(rs.getString("maNhanVien"));
		phanCong.setNgay(rs.getInt("ngay"));
		phanCong.setThang(rs.getInt("thang"));
		phanCong.setNam(rs.getInt("nam"));
		phanCong.setCaLamViec(CaLamViec.valueOf(rs.getString("caLamViec")));
		phanCong.setGhiChu(rs.getString("ghiChu"));

		return phanCong;
	}
}
