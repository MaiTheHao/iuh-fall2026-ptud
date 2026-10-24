package iuh.fit.dao;

import iuh.fit.core.config.DatabaseConfig;
import iuh.fit.dto.TieuThuDichVuDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

public class TieuThuDichVuDAO {

	private static final String SQL_SELECT_TIEU_THU_DICH_VU = """
			SELECT
			    dv.maDichVu,
			    dv.tenDichVu,
			    ISNULL(ct.soLuotSuDung, 0) AS soLuotSuDung,
			    ISNULL(ct.doanhThu, 0) AS doanhThu
			FROM DichVu dv
			LEFT JOIN (
			    SELECT
			        cthd.maSanPham,
			        SUM(cthd.soLuong) AS soLuotSuDung,
			        SUM(cthd.soLuong * cthd.donGia) AS doanhThu
			    FROM ChiTietHoaDon cthd
			    INNER JOIN HoaDon hd
			        ON hd.maHoaDon = cthd.maHoaDon
			    WHERE cthd.loaiSanPham = 'DICH_VU'
			        AND hd.trangThaiHoaDon = 'DA_THANH_TOAN'
			        AND hd.thoiGianThanhToan >= ?
			        AND hd.thoiGianThanhToan < ?
			    GROUP BY cthd.maSanPham
			) ct ON ct.maSanPham = dv.maDichVu
			ORDER BY dv.maDichVu
			""";

	public List<TieuThuDichVuDTO> findAll(YearMonth thang) throws SQLException {

		List<TieuThuDichVuDTO> danhSach = new ArrayList<>();

		Timestamp tuThang = Timestamp.valueOf(thang.atDay(1).atStartOfDay());

		Timestamp denThang = Timestamp.valueOf(thang.plusMonths(1).atDay(1).atStartOfDay());

		try (Connection connection = DatabaseConfig.getConnection();
				PreparedStatement statement = connection.prepareStatement(SQL_SELECT_TIEU_THU_DICH_VU)) {

			statement.setTimestamp(1, tuThang);
			statement.setTimestamp(2, denThang);

			try (ResultSet resultSet = statement.executeQuery()) {
				while (resultSet.next()) {
					danhSach.add(new TieuThuDichVuDTO(resultSet.getString("maDichVu"), resultSet.getString("tenDichVu"),
							resultSet.getInt("soLuotSuDung"), 0, resultSet.getDouble("doanhThu")));
				}
			}
		}

		return danhSach;
	}
}