package iuh.fit.service;

import iuh.fit.dao.PhuTungDAO;
import iuh.fit.dao.TransactionScope;
import iuh.fit.dto.DanhSachPhuTungCriteria;
import iuh.fit.dto.PaginatedResult;
import iuh.fit.entity.PhuTung;
import iuh.fit.util.MaGeneratorUtil;
import iuh.fit.util.TextNormalizerUtil;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class PhuTungService {

    private static final String ERROR_DATA = "app.phuTung.duLieuKhongHopLe";
    private static final String ERROR_NAME = "app.phuTung.loiTen";
    private static final String ERROR_PRICE = "app.phuTung.loiDonGia";
    private static final String ERROR_QUANTITY = "app.phuTung.loiSoLuong";
    private static final String ERROR_ID = "app.phuTung.khongTimThay";
    private static final String ERROR_STOCK = "app.phuTung.khongDuTon";
    private static final String ERROR_RANGE = "app.phuTung.loiKhoangLoc";

    private final PhuTungDAO phuTungDAO = new PhuTungDAO();

    public PaginatedResult<PhuTung> getAll(int page, int pageSize) throws SQLException {

        return PaginatedResult.of(this.phuTungDAO.findAll(), page, pageSize);
    }

    public PaginatedResult<PhuTung> search(DanhSachPhuTungCriteria criteria) throws SQLException {

        DanhSachPhuTungCriteria filter = criteria == null
                ? DanhSachPhuTungCriteria.macDinh()
                : criteria;

        this.validateFilterRange(filter);

        List<PhuTung> matched = this.phuTungDAO.findAll().stream().filter(item -> this.matches(item, filter)).toList();

        return PaginatedResult.of(matched, filter);
    }

    public Optional<PhuTung> getById(String maPhuTung) throws SQLException {

        return this.phuTungDAO.findById(maPhuTung);
    }

    public void create(PhuTung phuTung) throws SQLException {
        this.validate(phuTung);
        phuTung.setSoLuongTon(0);

        phuTung.setMaPhuTung(MaGeneratorUtil.sinhMa("PT", LocalDateTime.now()));

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

    public List<PhuTung> getAllForSelection() throws SQLException {
        List<PhuTung> list = new ArrayList<>(this.phuTungDAO.findAll());
        list.sort(Comparator.comparing(PhuTung::getMaPhuTung));
        return list;
    }

    public void increaseStock(TransactionScope scope, String maPhuTung, int soLuong) throws SQLException {

        if (maPhuTung == null || maPhuTung.isBlank() || soLuong <= 0) {
            throw new IllegalArgumentException(ERROR_QUANTITY);
        }

        if (!this.phuTungDAO.increaseStock(scope, maPhuTung, soLuong)) {
            throw new IllegalArgumentException(ERROR_ID);
        }
    }

    public void decreaseStock(TransactionScope scope, String maPhuTung, int soLuong) throws SQLException {

        if (maPhuTung == null || maPhuTung.isBlank() || soLuong <= 0) {
            throw new IllegalArgumentException(ERROR_QUANTITY);
        }

        if (!this.phuTungDAO.decreaseStock(scope, maPhuTung, soLuong)) {
            if (this.phuTungDAO.findById(maPhuTung).isEmpty()) {
                throw new IllegalArgumentException(ERROR_ID);
            }
            throw new IllegalArgumentException(ERROR_STOCK);
        }
    }

    private void validateFilterRange(DanhSachPhuTungCriteria filter) {

        if ((filter.donGiaTu() != null && filter.donGiaTu() < 0)
                || (filter.donGiaDen() != null && filter.donGiaDen() < 0)
                || (filter.soLuongTu() != null && filter.soLuongTu() < 0)
                || (filter.soLuongDen() != null && filter.soLuongDen() < 0)) {
            throw new IllegalArgumentException(ERROR_RANGE);
        }

        if (filter.donGiaTu() != null
                && filter.donGiaDen() != null
                && filter.donGiaTu() > filter.donGiaDen()) {
            throw new IllegalArgumentException(ERROR_RANGE);
        }

        if (filter.soLuongTu() != null
                && filter.soLuongDen() != null
                && filter.soLuongTu() > filter.soLuongDen()) {
            throw new IllegalArgumentException(ERROR_RANGE);
        }
    }

    private void validate(PhuTung phuTung) {
        if (phuTung == null) {
            throw new IllegalArgumentException(ERROR_DATA);
        }

        if (phuTung.getTenPhuTung() == null || phuTung.getTenPhuTung().isBlank()) {
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
