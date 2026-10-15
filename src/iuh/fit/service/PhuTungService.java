
package iuh.fit.service;

import iuh.fit.dao.PhuTungDAO;
import iuh.fit.dto.DanhSachPhuTungCriteria;
import iuh.fit.dto.PaginatedResult;
import iuh.fit.entity.PhuTung;
import iuh.fit.util.MaGeneratorUtil;
import iuh.fit.util.TextNormalizerUtil;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class PhuTungService {

	private static final String ERROR_NAME = "app.phuTung.loiTen";
	private static final String ERROR_PRICE = "app.phuTung.loiDonGia";
	private static final String ERROR_QUANTITY = "app.phuTung.loiSoLuong";
	private static final String ERROR_ID = "app.phuTung.khongTimThay";

	private final PhuTungDAO phuTungDAO = new PhuTungDAO();

	public PaginatedResult<PhuTung> getAll(int page, int pageSize) throws SQLException {

		return PaginatedResult.of(this.phuTungDAO.findAll(), page, pageSize);
	}

	public PaginatedResult<PhuTung> search(DanhSachPhuTungCriteria criteria) throws SQLException {

		DanhSachPhuTungCriteria filter = criteria == null
				? new DanhSachPhuTungCriteria(null, null, null, null, null, null, null, 1,
						PaginatedResult.DEFAULT_PAGE_SIZE)
				: criteria;

		List<PhuTung> matched = this.phuTungDAO.findAll().stream().filter(item -> this.matches(item, filter)).toList();

		return PaginatedResult.of(matched, filter.page(), filter.pageSize());
	}

	public Optional<PhuTung> getById(String maPhuTung) throws SQLException {

		return this.phuTungDAO.findById(maPhuTung);
	}

	public void create(PhuTung phuTung) throws SQLException {
		this.validate(phuTung);

		List<String> existingIds = this.phuTungDAO.findAll().stream().map(PhuTung::getMaPhuTung).toList();

		phuTung.setMaPhuTung(MaGeneratorUtil.sinhMa("PT", 3, existingIds));

		this.phuTungDAO.insert(phuTung);
	}

	public void update(PhuTung phuTung) throws SQLException {
		this.validate(phuTung);

		if (phuTung.getMaPhuTung() == null || phuTung.getMaPhuTung().isBlank()) {
			throw new IllegalArgumentException(ERROR_ID);
		}

		Optional<PhuTung> existing = this.phuTungDAO.findById(phuTung.getMaPhuTung());

		if (existing.isEmpty()) {
			throw new IllegalArgumentException(ERROR_ID);
		}

		// Không cho phép thay đổi tồn kho khi sửa thông tin.
		phuTung.setSoLuongTon(existing.get().getSoLuongTon());

		this.phuTungDAO.update(phuTung);
	}

	public void delete(String maPhuTung) throws SQLException {
		if (maPhuTung == null || maPhuTung.isBlank() || this.phuTungDAO.findById(maPhuTung).isEmpty()) {
			throw new IllegalArgumentException(ERROR_ID);
		}

		this.phuTungDAO.delete(maPhuTung);
	}

	public void increaseStock(String maPhuTung, int soLuong) throws SQLException {

		if (maPhuTung == null || maPhuTung.isBlank() || soLuong <= 0) {
			throw new IllegalArgumentException(ERROR_QUANTITY);
		}

		this.phuTungDAO.increaseStock(maPhuTung, soLuong);
	}

	private void validate(PhuTung phuTung) {
		if (phuTung == null || phuTung.getTenPhuTung() == null || phuTung.getTenPhuTung().isBlank()) {
			throw new IllegalArgumentException(ERROR_NAME);
		}

		if (phuTung.getDonGiaNhap() == null || !Double.isFinite(phuTung.getDonGiaNhap())
				|| phuTung.getDonGiaNhap() < 0) {
			throw new IllegalArgumentException(ERROR_PRICE);
		}

		if (phuTung.getSoLuongTon() < 0) {
			throw new IllegalArgumentException(ERROR_QUANTITY);
		}
	}

	private boolean matches(PhuTung item, DanhSachPhuTungCriteria filter) {

		if (!this.containsText(filter.maPhuTung(), item.getMaPhuTung())) {
			return false;
		}

		if (!this.containsText(filter.tenPhuTung(), item.getTenPhuTung())) {
			return false;
		}

		if (filter.timNhanh() != null && !filter.timNhanh().isBlank()) {

			String combined = item.getMaPhuTung() + " " + item.getTenPhuTung();

			if (!TextNormalizerUtil.normalize(combined).contains(TextNormalizerUtil.normalize(filter.timNhanh()))) {
				return false;
			}
		}

		double donGia = item.getDonGiaNhap();
		int soLuong = item.getSoLuongTon();

		if (filter.donGiaTu() != null && donGia < filter.donGiaTu()) {
			return false;
		}

		if (filter.donGiaDen() != null && donGia > filter.donGiaDen()) {
			return false;
		}

		if (filter.soLuongTu() != null && soLuong < filter.soLuongTu()) {
			return false;
		}

		if (filter.soLuongDen() != null && soLuong > filter.soLuongDen()) {
			return false;
		}

		return true;
	}

	private boolean containsText(String filterValue, String value) {
		if (filterValue == null || filterValue.isBlank()) {
			return true;
		}

		return TextNormalizerUtil.normalize(value).contains(TextNormalizerUtil.normalize(filterValue));
	}
}
