package iuh.fit.service;

import iuh.fit.dao.DichVuDAO;
import iuh.fit.dto.DanhSachDichVuCriteria;
import iuh.fit.dto.PaginatedResult;
import iuh.fit.entity.DichVu;
import iuh.fit.enumeration.LoaiMa;
import iuh.fit.util.TextNormalizerUtil;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class DichVuService {

    private static final String ERROR_NAME = "app.dichVu.loiTen";
    private static final String ERROR_NAME_LENGTH = "app.dichVu.loiTenQuaDai";
    private static final String ERROR_DESCRIPTION = "app.dichVu.loiMoTa";
    private static final String ERROR_IMAGE_URL = "app.dichVu.loiAnhUrl";
    private static final String ERROR_PRICE = "app.dichVu.loiDonGia";
    private static final String ERROR_ID = "app.dichVu.khongTimThay";
    private static final String ERROR_DATA = "app.dichVu.loiDuLieu";
    private static final String ERROR_RANGE = "app.dichVu.loiKhoangLoc";

    private final DichVuDAO dichVuDAO;
    private final MaService maService = new MaService();

    public DichVuService() {
        this.dichVuDAO = new DichVuDAO();
    }

    public DichVuService(DichVuDAO dichVuDAO) {
        this.dichVuDAO = Objects.requireNonNull(dichVuDAO);
    }

    public PaginatedResult<DichVu> search(
            DanhSachDichVuCriteria criteria) throws SQLException {

        DanhSachDichVuCriteria filter = criteria == null
                ? DanhSachDichVuCriteria.macDinh()
                : criteria;

        if ((filter.donGiaTu() != null && filter.donGiaTu() < 0)
                || (filter.donGiaDen() != null && filter.donGiaDen() < 0)) {
            throw new IllegalArgumentException(ERROR_RANGE);
        }

        if (filter.donGiaTu() != null
                && filter.donGiaDen() != null
                && filter.donGiaTu() > filter.donGiaDen()) {
            throw new IllegalArgumentException(ERROR_RANGE);
        }

        final String ma = this.normalize(filter.maDichVu());
        final String ten = this.normalize(filter.tenDichVu());
        final String timNhanh = this.normalize(filter.timNhanh());

        final Double giaTu = filter.donGiaTu();
        final Double giaDen = filter.donGiaDen();

        List<DichVu> ketQua = this.dichVuDAO.findAll().stream()
                .filter(dv -> this.normalize(dv.getMaSanPham()).contains(ma))
                .filter(dv -> this.normalize(dv.getTenSanPham()).contains(ten))
                .filter(dv -> timNhanh.isEmpty()
                        || this.normalize(dv.getMaSanPham() + " "
                                + dv.getTenSanPham() + " "
                                + (dv.getMoTa() == null ? "" : dv.getMoTa())).contains(timNhanh))
                .filter(dv -> giaTu == null || dv.getDonGia() >= giaTu)
                .filter(dv -> giaDen == null || dv.getDonGia() <= giaDen)
                .toList();

        return PaginatedResult.of(ketQua, filter);
    }

    public DichVu create(DichVu dichVu) throws SQLException {
        this.validate(dichVu);

        String maDichVu = this.maService.genMa(LoaiMa.DV);

        DichVu dichVuMoi = new DichVu(
                maDichVu,
                dichVu.getTenSanPham().trim(),
                dichVu.getMoTa(),
                dichVu.getAnhUrl(),
                dichVu.getDonGia());

        this.dichVuDAO.insert(dichVuMoi);
        return dichVuMoi;
    }

    public void update(DichVu dichVu) throws SQLException {
        this.validate(dichVu);

        if (dichVu.getMaSanPham() == null
                || dichVu.getMaSanPham().isBlank()
                || this.dichVuDAO.findById(
                        dichVu.getMaSanPham()).isEmpty()) {
            throw new IllegalArgumentException(ERROR_ID);
        }

        DichVu dichVuCapNhat = new DichVu(
                dichVu.getMaSanPham(),
                dichVu.getTenSanPham().trim(),
                dichVu.getMoTa(),
                dichVu.getAnhUrl(),
                dichVu.getDonGia());

        this.dichVuDAO.update(dichVuCapNhat);
    }

    public void delete(String maDichVu) throws SQLException {
        if (maDichVu == null || maDichVu.isBlank()
                || this.dichVuDAO.findById(maDichVu).isEmpty()) {
            throw new IllegalArgumentException(ERROR_ID);
        }

        if (this.dichVuDAO.existsChiTietHoaDon(maDichVu)) {
            throw new IllegalArgumentException("app.dichVu.loiDangSuDung");
        }

        this.dichVuDAO.delete(maDichVu);
    }

    private void validate(DichVu dichVu) {
        if (dichVu == null) {
            throw new IllegalArgumentException(ERROR_DATA);
        }

        if (dichVu.getTenSanPham() == null
                || dichVu.getTenSanPham().isBlank()) {
            throw new IllegalArgumentException(ERROR_NAME);
        }

        if (dichVu.getTenSanPham().trim().length() > 100) {
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
