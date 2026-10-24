package iuh.fit.dao;

import iuh.fit.core.config.DatabaseConfig;
import iuh.fit.dto.TieuThuPhuTungDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class TieuThuPhuTungDAO {

	private static final String SQL_SELECT_PHU_TUNG = """
			SELECT
			    pt.maPhuTung,
			    pt.tenPhuTung,
			    pt.soLuongTon,
			    pn.ngayTao AS ngayNhap,
			    ctn.soLuong AS soLuongNhap,
			    hd.thoiGianThanhToan AS ngayXuat,
			    cthd.soLuong AS soLuongXuat,
			    cthd.donGia AS donGiaBan,
			    hd.trangThaiHoaDon
			FROM PhuTung pt
			LEFT JOIN ChiTietPhieuNhapPhuTung ctn
			    ON ctn.maPhuTung = pt.maPhuTung
			LEFT JOIN PhieuNhapPhuTung pn
			    ON pn.maPhieuNhap = ctn.maPhieuNhap
			LEFT JOIN ChiTietHoaDon cthd
			    ON cthd.maSanPham = pt.maPhuTung
			    AND cthd.loaiSanPham = 'PHU_TUNG'
			LEFT JOIN HoaDon hd
			    ON hd.maHoaDon = cthd.maHoaDon
			""";

	public List<TieuThuPhuTungDTO> findAll() throws SQLException {
		List<TieuThuPhuTungDTO> danhSach = new ArrayList<>();

		try (Connection connection = DatabaseConfig.getConnection();
				PreparedStatement statement = connection.prepareStatement(SQL_SELECT_PHU_TUNG);
				ResultSet resultSet = statement.executeQuery()) {

			while (resultSet.next()) {
				Timestamp ngayNhap = resultSet.getTimestamp("ngayNhap");
				Timestamp ngayXuat = resultSet.getTimestamp("ngayXuat");

				danhSach.add(new TieuThuPhuTungDTO(resultSet.getString("maPhuTung"), resultSet.getString("tenPhuTung"),
						resultSet.getInt("soLuongTon"), resultSet.getInt("soLuongNhap"),
						ngayNhap == null ? null : ngayNhap.toLocalDateTime(), resultSet.getInt("soLuongXuat"),
						ngayXuat == null ? null : ngayXuat.toLocalDateTime(), resultSet.getDouble("donGiaBan"),
						resultSet.getString("trangThaiHoaDon")));
			}
		}

		return danhSach;
	}
}