package iuh.fit.service;

import iuh.fit.dao.DichVuDAO;
import iuh.fit.dto.DanhSachDichVuCriteria;
import iuh.fit.dto.PaginatedResult;
import iuh.fit.entity.DichVu;
import iuh.fit.util.MaGeneratorUtil;
import iuh.fit.util.TextNormalizerUtil;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class DichVuService {

    private static final String ERROR_NAME = "app.dichVu.loiTen";
    private static final String ERROR_NAME_LENGTH = "app.dichVu.loiTenQuaDai";
    private static final String ERROR_DESCRIPTION = "app.dichVu.loiMoTa";
    private static final String ERROR_IMAGE_URL = "app.dichVu.loiAnhUrl";
    private static final String ERROR_PRICE = "app.dichVu.loiDonGia";
    private static final String ERROR_ID = "app.dichVu.khongTimThay";
    private static final String ERROR_DATA = "app.dichVu.loiDuLieu";

    private final DichVuDAO dichVuDAO;

    public DichVuService() {
        this.dichVuDAO = new DichVuDAO();
    }

    public DichVuService(DichVuDAO dichVuDAO) {
        this.dichVuDAO = Objects.requireNonNull(dichVuDAO);
    }

    /**
     * Tìm kiếm dịch vụ và phân trang.
     */
    public PaginatedResult<DichVu> search(
            DanhSachDichVuCriteria criteria) throws SQLException {

        if (criteria == null) {
            criteria = new DanhSachDichVuCriteria(
                    "", "", "", null, null,
                    1, PaginatedResult.DEFAULT_PAGE_SIZE);
        }

        List<DichVu> danhSach = dichVuDAO.findAll();

        final String ma = normalize(criteria.maDichVu());
        final String ten = normalize(criteria.tenDichVu());
        final String timNhanh = normalize(criteria.timNhanh());

        final Double giaTu = criteria.donGiaTu();
        final Double giaDen = criteria.donGiaDen();

        List<DichVu> ketQua = danhSach.stream()
                .filter(dv -> normalize(dv.getMaDichVu()).contains(ma))
                .filter(dv -> normalize(dv.getTenDichVu()).contains(ten))
                .filter(dv -> timNhanh.isEmpty()
                        || normalize(dv.getMaDichVu() + " "
                                + dv.getTenDichVu()).contains(timNhanh))
                .filter(dv -> giaTu == null || dv.getDonGia() >= giaTu)
                .filter(dv -> giaDen == null || dv.getDonGia() <= giaDen)
                .collect(Collectors.toList());

        int pageSize = criteria.pageSize() > 0
                ? criteria.pageSize()
                : PaginatedResult.DEFAULT_PAGE_SIZE;

        int totalPages = PaginatedResult.totalPages(
                ketQua.size(), pageSize);

        int page = PaginatedResult.clampPage(
                criteria.page(), totalPages);

        return PaginatedResult.of(ketQua, page, pageSize);
    }

    /**
     * Thêm dịch vụ mới và tự sinh mã DV.
     */
    public DichVu create(DichVu dichVu) throws SQLException {
        validate(dichVu);

        List<String> existingIds = dichVuDAO.findAll()
                .stream()
                .map(DichVu::getMaDichVu)
                .collect(Collectors.toList());

        String maDichVu = MaGeneratorUtil.sinhMa(
                "DV", 3, existingIds);

        DichVu dichVuMoi = new DichVu(
                maDichVu,
                dichVu.getTenDichVu().trim(),
                dichVu.getMoTa(),
                dichVu.getAnhUrl(),
                dichVu.getDonGia());

        dichVuDAO.insert(dichVuMoi);
        return dichVuMoi;
    }

    /**
     * Cập nhật dịch vụ.
     */
    public void update(DichVu dichVu) throws SQLException {
        validate(dichVu);

        if (dichVu.getMaDichVu() == null
                || dichVu.getMaDichVu().isBlank()
                || dichVuDAO.findById(
                        dichVu.getMaDichVu()).isEmpty()) {
            throw new IllegalArgumentException(ERROR_ID);
        }

        DichVu dichVuCapNhat = new DichVu(
                dichVu.getMaDichVu(),
                dichVu.getTenDichVu().trim(),
                dichVu.getMoTa(),
                dichVu.getAnhUrl(),
                dichVu.getDonGia());

        dichVuDAO.update(dichVuCapNhat);
    }

    /**
     * Xóa dịch vụ.
     */
    public void delete(String maDichVu) throws SQLException {
        if (maDichVu == null || maDichVu.isBlank()
                || dichVuDAO.findById(maDichVu).isEmpty()) {
            throw new IllegalArgumentException(ERROR_ID);
        }

        dichVuDAO.delete(maDichVu);
    }

    /**
     * Kiểm tra dữ liệu trước khi thêm hoặc sửa.
     * Các lỗi được trả về dưới dạng key i18n.
     */
    private void validate(DichVu dichVu) {
        if (dichVu == null) {
            throw new IllegalArgumentException(ERROR_DATA);
        }

        if (dichVu.getTenDichVu() == null
                || dichVu.getTenDichVu().isBlank()) {
            throw new IllegalArgumentException(ERROR_NAME);
        }

        if (dichVu.getTenDichVu().trim().length() > 100) {
            throw new IllegalArgumentException(ERROR_NAME_LENGTH);
        }

        if (dichVu.getMoTa() != null
                && dichVu.getMoTa().length() > 500) {
            throw new IllegalArgumentException(ERROR_DESCRIPTION);
        }

        if (dichVu.getAnhUrl() != null
                && dichVu.getAnhUrl().length() > 500) {
            throw new IllegalArgumentException(ERROR_IMAGE_URL);
        }

        if (dichVu.getDonGia() == null
                || !Double.isFinite(dichVu.getDonGia())
                || dichVu.getDonGia() < 0) {
            throw new IllegalArgumentException(ERROR_PRICE);
        }
    }

    private String normalize(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }

        return TextNormalizerUtil.normalize(value);
    }
}