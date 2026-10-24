package iuh.fit.dao;

import iuh.fit.core.config.DatabaseConfig;
import iuh.fit.dto.TieuThuPhuTungDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

public class TieuThuPhuTungDAO {

	private static final String SQL_SELECT_PHU_TUNG = """
			SELECT
			    pt.maPhuTung,
			    pt.tenPhuTung,
			    pt.soLuongTon,
			    ISNULL(nhap.soLuongNhap, 0) AS soLuongNhap,
			    ISNULL(xuat.soLuongXuat, 0) AS soLuongXuat,
			    ISNULL(gia.donGiaBan, 0) AS donGiaBan,
			    ISNULL(xuat.doanhThu, 0) AS doanhThu
			FROM PhuTung pt
			LEFT JOIN (
			    SELECT
			        ctn.maPhuTung,
			        SUM(ctn.soLuong) AS soLuongNhap
			    FROM ChiTietPhieuNhapPhuTung ctn
			    INNER JOIN PhieuNhapPhuTung pn
			        ON pn.maPhieuNhap = ctn.maPhieuNhap
			    WHERE pn.ngayTao >= ?
			      AND pn.ngayTao < ?
			    GROUP BY ctn.maPhuTung
			) nhap ON nhap.maPhuTung = pt.maPhuTung
			LEFT JOIN (
			    SELECT
			        cthd.maSanPham,
			        SUM(cthd.soLuong) AS soLuongXuat,
			        SUM(cthd.soLuong * cthd.donGia) AS doanhThu
			    FROM ChiTietHoaDon cthd
			    INNER JOIN HoaDon hd
			        ON hd.maHoaDon = cthd.maHoaDon
			    WHERE cthd.loaiSanPham = 'PHU_TUNG'
			      AND hd.trangThaiHoaDon = 'DA_THANH_TOAN'
			      AND hd.thoiGianThanhToan >= ?
			      AND hd.thoiGianThanhToan < ?
			    GROUP BY cthd.maSanPham
			) xuat ON xuat.maSanPham = pt.maPhuTung
			OUTER APPLY (
			    SELECT TOP 1
			        cthd.donGia AS donGiaBan
			    FROM ChiTietHoaDon cthd
			    INNER JOIN HoaDon hd
			        ON hd.maHoaDon = cthd.maHoaDon
			    WHERE cthd.maSanPham = pt.maPhuTung
			      AND cthd.loaiSanPham = 'PHU_TUNG'
			      AND hd.trangThaiHoaDon = 'DA_THANH_TOAN'
			      AND hd.thoiGianThanhToan >= ?
			      AND hd.thoiGianThanhToan < ?
			    ORDER BY hd.thoiGianThanhToan DESC, hd.maHoaDon DESC
			) gia
			ORDER BY pt.maPhuTung
			""";

	public List<TieuThuPhuTungDTO> findAll(YearMonth thang) throws SQLException {
		List<TieuThuPhuTungDTO> danhSach = new ArrayList<>();

		Timestamp tuNgay = Timestamp.valueOf(thang.atDay(1).atStartOfDay());
		Timestamp denNgay = Timestamp.valueOf(thang.plusMonths(1).atDay(1).atStartOfDay());

		try (Connection connection = DatabaseConfig.getConnection();
				PreparedStatement statement = connection.prepareStatement(SQL_SELECT_PHU_TUNG)) {

			statement.setTimestamp(1, tuNgay);
			statement.setTimestamp(2, denNgay);
			statement.setTimestamp(3, tuNgay);
			statement.setTimestamp(4, denNgay);
			statement.setTimestamp(5, tuNgay);
			statement.setTimestamp(6, denNgay);

			try (ResultSet resultSet = statement.executeQuery()) {
				while (resultSet.next()) {
					danhSach.add(new TieuThuPhuTungDTO(resultSet.getString("maPhuTung"),
							resultSet.getString("tenPhuTung"), resultSet.getInt("soLuongTon"),
							resultSet.getInt("soLuongNhap"), resultSet.getInt("soLuongXuat"),
							resultSet.getDouble("donGiaBan"), resultSet.getDouble("doanhThu")));
				}
			}
		}

		return danhSach;
	}
}