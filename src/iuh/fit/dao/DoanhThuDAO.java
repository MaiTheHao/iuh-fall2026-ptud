package iuh.fit.dao;

import iuh.fit.dto.DoanhThuDTO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

public class DoanhThuDAO extends BaseDao<DoanhThuDTO> {

	private static final String SQL_SELECT = """
			SELECT
			    hd.maHoaDon,
			    hd.ngayTao,
			    hd.thoiGianThanhToan,
			    kh.hoTen AS tenKhachHang,
			    ptn.bienSo,
			    hd.trangThaiHoaDon,
			    cthd.loaiSanPham,
			    cthd.soLuong,
			    cthd.donGia
			FROM HoaDon hd
			LEFT JOIN PhieuTiepNhan ptn
			    ON hd.maPhieuTiepNhan = ptn.maPhieuTiepNhan
			LEFT JOIN KhachHang kh
			    ON ptn.maKhachHang = kh.maKhachHang
			LEFT JOIN ChiTietHoaDon cthd
			    ON hd.maHoaDon = cthd.maHoaDon
			ORDER BY
			    COALESCE(hd.thoiGianThanhToan, hd.ngayTao) DESC,
			    hd.maHoaDon
			""";

	@Override
	protected DoanhThuDTO mapRow(ResultSet rs) throws SQLException {
		Timestamp ngayTao = rs.getTimestamp("ngayTao");
		Timestamp thoiGianThanhToan = rs.getTimestamp("thoiGianThanhToan");

		return new DoanhThuDTO(rs.getString("maHoaDon"), ngayTao == null ? null : ngayTao.toLocalDateTime(),
				thoiGianThanhToan == null ? null : thoiGianThanhToan.toLocalDateTime(), rs.getString("tenKhachHang"),
				rs.getString("bienSo"), rs.getString("trangThaiHoaDon"), rs.getString("loaiSanPham"),
				rs.getInt("soLuong"), rs.getDouble("donGia"));
	}

	public List<DoanhThuDTO> findAllChiTietDoanhThu() throws SQLException {
		return this.findAll(SQL_SELECT);
	}
}