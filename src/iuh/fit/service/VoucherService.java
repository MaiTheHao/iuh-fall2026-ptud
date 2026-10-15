package iuh.fit.service;

import iuh.fit.dao.VoucherDAO;
import iuh.fit.dto.DanhSachVoucherCriteria;
import iuh.fit.dto.PaginatedResult;
import iuh.fit.entity.Voucher;
import iuh.fit.util.MaGeneratorUtil;
import iuh.fit.util.TextNormalizerUtil;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class VoucherService {

	private static final String ERROR_ID = "app.voucher.loiMa";
	private static final String ERROR_DATA = "app.voucher.duLieuKhongHopLe";
	private static final String ERROR_PAGINATION = "app.voucher.thamSoPhanTrangKhongHopLe";
	private static final String ERROR_DISCOUNT = "app.voucher.loiMucGiam";
	private static final String ERROR_DISCOUNT_RANGE = "app.voucher.khoangMucGiamKhongHopLe";
	private static final String ERROR_DATE_RANGE = "app.voucher.khoangThoiHanKhongHopLe";
	private static final String ERROR_NOT_FOUND = "app.voucher.khongTonTai";
	private static final String ERROR_NAME = "app.voucher.loiTen";
	private static final String ERROR_NAME_LENGTH = "app.voucher.tenQuaDai";
	private static final String ERROR_DESCRIPTION = "app.voucher.moTaQuaDai";
	private static final String ERROR_VALIDITY = "app.voucher.loiThoiHan";

    private final VoucherDAO voucherDAO = new VoucherDAO();

    public List<Voucher> findAll() throws SQLException {
        return voucherDAO.findAll();
    }

    public Optional<Voucher> findById(String maVoucher)
            throws SQLException {

        if (maVoucher == null || maVoucher.isBlank()) {
            throw new IllegalArgumentException(ERROR_ID);
        }

        return voucherDAO.findById(maVoucher.trim());
    }

    public PaginatedResult<Voucher> search(
            DanhSachVoucherCriteria criteria) throws SQLException {

        if (criteria == null) {
            throw new IllegalArgumentException(ERROR_DATA);
        }

        if (criteria.page() < 1 || criteria.pageSize() < 1) {
            throw new IllegalArgumentException(ERROR_PAGINATION);
        }

        if ((criteria.mucGiamTu() != null
                    && criteria.mucGiamTu() < 0)
                || (criteria.mucGiamDen() != null
                    && criteria.mucGiamDen() < 0)) {
            throw new IllegalArgumentException(ERROR_DISCOUNT);
        }

        if (criteria.mucGiamTu() != null
                && criteria.mucGiamDen() != null
                && criteria.mucGiamTu() > criteria.mucGiamDen()) {
            throw new IllegalArgumentException(ERROR_DISCOUNT_RANGE);
        }

        if (criteria.thoiHanTu() != null
                && criteria.thoiHanDen() != null
                && criteria.thoiHanTu().isAfter(criteria.thoiHanDen())) {
            throw new IllegalArgumentException(ERROR_DATE_RANGE);
        }

        List<Voucher> matched = voucherDAO.findAll()
                .stream()
                .filter(voucher -> matches(voucher, criteria))
                .collect(Collectors.toList());

        return PaginatedResult.of(
                matched,
                criteria.page(),
                criteria.pageSize());
    }

    private boolean matches(
            Voucher voucher,
            DanhSachVoucherCriteria criteria) {

        String tuKhoa = criteria.tuKhoa();

        if (tuKhoa != null && !tuKhoa.isBlank()) {
            String keyword = TextNormalizerUtil.normalize(tuKhoa);

            boolean matchMa = TextNormalizerUtil
                    .normalize(voucher.getMaVoucher())
                    .contains(keyword);

            boolean matchTen = TextNormalizerUtil
                    .normalize(voucher.getTenChuongTrinh())
                    .contains(keyword);

            boolean matchMoTa = TextNormalizerUtil
                    .normalize(voucher.getMoTa())
                    .contains(keyword);

            if (!matchMa && !matchTen && !matchMoTa) {
                return false;
            }
        }

        String maVoucher = TextNormalizerUtil.normalize(
                criteria.maVoucher());

        if (!maVoucher.isEmpty()
                && !TextNormalizerUtil.normalize(
                        voucher.getMaVoucher()).contains(maVoucher)) {
            return false;
        }

        if (criteria.mucGiamTu() != null) {
            if (voucher.getMucGiam() == null
                    || voucher.getMucGiam() < criteria.mucGiamTu()) {
                return false;
            }
        }

        if (criteria.mucGiamDen() != null) {
            if (voucher.getMucGiam() == null
                    || voucher.getMucGiam() > criteria.mucGiamDen()) {
                return false;
            }
        }

        if (voucher.getThoiHanApDung() == null) {
            return false;
        }

        LocalDate ngayApDung =
                voucher.getThoiHanApDung().toLocalDate();

        if (criteria.thoiHanTu() != null
                && ngayApDung.isBefore(criteria.thoiHanTu())) {
            return false;
        }

        if (criteria.thoiHanDen() != null
                && ngayApDung.isAfter(criteria.thoiHanDen())) {
            return false;
        }

        if (criteria.trangThaiKichHoat() != null
                && voucher.isTrangThaiKichHoat()
                        != criteria.trangThaiKichHoat()) {
            return false;
        }

        return true;
    }

    public Voucher create(Voucher voucher) throws SQLException {
        validate(voucher);

        List<String> existingIds = voucherDAO.findAll()
                .stream()
                .map(Voucher::getMaVoucher)
                .collect(Collectors.toList());

        String maVoucher = MaGeneratorUtil.sinhMa(
                "VC", 3, existingIds);

        voucher.setMaVoucher(maVoucher);

        voucherDAO.insert(voucher);

        return voucher;
    }

    public Voucher update(Voucher voucher) throws SQLException {
        validate(voucher);

        if (voucher.getMaVoucher() == null
                || voucher.getMaVoucher().isBlank()) {
            throw new IllegalArgumentException(ERROR_ID);
        }

        String maVoucher = voucher.getMaVoucher().trim();

        if (voucherDAO.findById(maVoucher).isEmpty()) {
            throw new IllegalArgumentException(ERROR_NOT_FOUND);
        }

        voucher.setMaVoucher(maVoucher);

        voucherDAO.update(voucher);

        return voucher;
    }

    public void delete(String maVoucher) throws SQLException {
        if (maVoucher == null || maVoucher.isBlank()) {
            throw new IllegalArgumentException(ERROR_ID);
        }

        String ma = maVoucher.trim();

        if (voucherDAO.findById(ma).isEmpty()) {
            throw new IllegalArgumentException(ERROR_NOT_FOUND);
        }

        voucherDAO.delete(ma);
    }

    private void validate(Voucher voucher) {
        if (voucher == null) {
            throw new IllegalArgumentException(ERROR_DATA);
        }

        if (voucher.getTenChuongTrinh() == null
                || voucher.getTenChuongTrinh().isBlank()) {
            throw new IllegalArgumentException(ERROR_NAME);
        }

        if (voucher.getTenChuongTrinh().trim().length() > 100) {
            throw new IllegalArgumentException(ERROR_NAME_LENGTH);
        }

        if (voucher.getMoTa() != null
                && voucher.getMoTa().length() > 500) {
            throw new IllegalArgumentException(ERROR_DESCRIPTION);
        }

        if (voucher.getMucGiam() == null
                || !Double.isFinite(voucher.getMucGiam())
                || voucher.getMucGiam() < 0
                || voucher.getMucGiam() > 1) {
            throw new IllegalArgumentException(ERROR_DISCOUNT);
        }

        if (voucher.getThoiHanApDung() == null) {
            throw new IllegalArgumentException(ERROR_VALIDITY);
        }
    }
}