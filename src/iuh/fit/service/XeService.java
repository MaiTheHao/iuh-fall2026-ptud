package iuh.fit.service;

import iuh.fit.dao.TransactionScope;
import iuh.fit.dao.XeDAO;
import iuh.fit.dto.DanhSachXeCriteria;
import iuh.fit.dto.PaginatedResult;
import iuh.fit.entity.Xe;
import iuh.fit.util.TextNormalizerUtil;

import java.sql.SQLException;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class XeService {

    private final XeDAO xeDAO = new XeDAO();

    private static final String ERROR_XE_NOT_FOUND = "app.xe.loiKhongTimThay";
    private static final String ERROR_BIEN_SO = "app.xe.loiBienSo";
    private static final String ERROR_DUPLICATE = "app.xe.loiTrungBienSo";
    private static final String ERROR_HANG_XE = "app.xe.loiHangXe";
    private static final String ERROR_DONG_XE = "app.xe.loiDongXe";
    private static final String ERROR_HANG_XE_TOO_LONG = "app.xe.loiHangXeQuaDai";
    private static final String ERROR_DONG_XE_TOO_LONG = "app.xe.loiDongXeQuaDai";
    private static final String ERROR_DOI_XE = "app.xe.loiDoiXe";
    private static final String ERROR_ODO = "app.xe.loiOdo";
    private static final String ERROR_XE_IN_USE = "app.xe.loiXeDangSuDung";

    public PaginatedResult<Xe> search(DanhSachXeCriteria criteria)
            throws SQLException {
        DanhSachXeCriteria filter = criteria == null
                ? new DanhSachXeCriteria(null, null, null, null, null, null, null, null, 1,
                        PaginatedResult.DEFAULT_PAGE_SIZE)
                : criteria;

        List<Xe> matched = this.xeDAO.findAll().stream()
                .filter(xe -> this.matches(xe, filter))
                .toList();

        return PaginatedResult.of(
                matched, filter.page(), filter.pageSize());
    }

    public Optional<Xe> getByBienSo(String bienSo)
            throws SQLException {
        String normalized = this.normalizeBienSo(bienSo);

        if (normalized.isBlank()) {
            return Optional.empty();
        }

        return this.xeDAO.findById(normalized);
    }

    public Optional<Xe> getByBienSo(TransactionScope scope, String bienSo)
            throws SQLException {
        String normalized = this.normalizeBienSo(bienSo);

        if (normalized.isBlank()) {
            return Optional.empty();
        }

        return this.xeDAO.findById(scope, normalized);
    }

    public boolean existsByBienSo(String bienSo) throws SQLException {
        bienSo = this.normalizeBienSo(bienSo);
        return this.xeDAO.existsByBienSo(bienSo);
    }

    public boolean existsByBienSo(TransactionScope scope, String bienSo) throws SQLException {
        bienSo = this.normalizeBienSo(bienSo);
        return this.xeDAO.existsByBienSo(scope, bienSo);
    }

    public void create(Xe xe) throws SQLException {
        String bienSo = this.chuanBi(xe);

        if (this.xeDAO.existsByBienSo(bienSo)) {
            throw new IllegalArgumentException(ERROR_DUPLICATE);
        }

        this.xeDAO.insert(xe);
    }

    public void create(TransactionScope scope, Xe xe) throws SQLException {
        String bienSo = this.chuanBi(xe);

        if (this.xeDAO.existsByBienSo(scope, bienSo)) {
            throw new IllegalArgumentException(ERROR_DUPLICATE);
        }

        this.xeDAO.insert(scope, xe);
    }

    public void update(Xe xe) throws SQLException {
        String bienSo = this.chuanBi(xe);

        if (!this.xeDAO.existsByBienSo(bienSo)) {
            throw new IllegalArgumentException(ERROR_XE_NOT_FOUND);
        }

        if (!this.xeDAO.update(xe)) {
            throw new IllegalArgumentException(ERROR_XE_NOT_FOUND);
        }
    }

    public void update(TransactionScope scope, Xe xe) throws SQLException {
        String bienSo = this.chuanBi(xe);

        if (!this.xeDAO.existsByBienSo(scope, bienSo)) {
            throw new IllegalArgumentException(ERROR_XE_NOT_FOUND);
        }

        if (!this.xeDAO.update(scope, xe)) {
            throw new IllegalArgumentException(ERROR_XE_NOT_FOUND);
        }
    }

    public void delete(String bienSo) throws SQLException {
        String normalized = this.normalizeBienSo(bienSo);

        if (normalized.isBlank()) {
            throw new IllegalArgumentException(ERROR_BIEN_SO);
        }

        if (!this.xeDAO.existsByBienSo(normalized)) {
            throw new IllegalArgumentException(ERROR_XE_NOT_FOUND);
        }

        if (this.xeDAO.existsInKhachHangXe(normalized)) {
            throw new IllegalStateException(ERROR_XE_IN_USE);
        }

        if (!this.xeDAO.delete(normalized)) {
            throw new IllegalArgumentException(ERROR_XE_NOT_FOUND);
        }
    }

    public List<String> getDanhSachHangXe() throws SQLException {
        return this.xeDAO.findAll().stream()
                .map(Xe::getHangXe)
                .map(this::normalizeTenXe)
                .filter(name -> !name.isEmpty())
                .distinct()
                .sorted()
                .toList();
    }

    public List<String> getDanhSachDongXe(String hangXe) throws SQLException {
        String chuanHoa = this.normalizeTenXe(hangXe);

        return this.xeDAO.findAll().stream()
                .filter(xe -> chuanHoa.isEmpty()
                        || chuanHoa.equals(this.normalizeTenXe(xe.getHangXe())))
                .map(Xe::getDongXe)
                .map(this::normalizeTenXe)
                .filter(name -> !name.isEmpty())
                .distinct()
                .sorted()
                .toList();
    }

    private void validate(Xe xe) {
        if (xe == null) {
            throw new IllegalArgumentException(
                    "Thông tin xe không được để trống.");
        }

        String bienSo = this.normalizeBienSo(xe.getBienSo());

        if (!bienSo.matches("^[0-9]{2}[A-Z]{1,2}[0-9]{4,6}$")) {
            throw new IllegalArgumentException(ERROR_BIEN_SO);
        }

        if (xe.getHangXe() == null || xe.getHangXe().isBlank()) {
            throw new IllegalArgumentException(ERROR_HANG_XE);
        }

        if (xe.getHangXe().trim().length() > 50) {
            throw new IllegalArgumentException(ERROR_HANG_XE_TOO_LONG);
        }

        if (xe.getDongXe() == null || xe.getDongXe().isBlank()) {
            throw new IllegalArgumentException(ERROR_DONG_XE);
        }

        if (xe.getDongXe().trim().length() > 50) {
            throw new IllegalArgumentException(ERROR_DONG_XE_TOO_LONG);
        }

        int currentYear = java.time.Year.now().getValue();

        if (xe.getDoiXe() < 1886 || xe.getDoiXe() > currentYear + 1) {
            throw new IllegalArgumentException(ERROR_DOI_XE);
        }

        if (xe.getOdo() < 0) {
            throw new IllegalArgumentException(ERROR_ODO);
        }
    }

    private String chuanBi(Xe xe) {
        this.validate(xe);

        xe.setHangXe(this.normalizeTenXe(xe.getHangXe()));
        xe.setDongXe(this.normalizeTenXe(xe.getDongXe()));

        String bienSo = this.normalizeBienSo(xe.getBienSo());
        xe.setBienSo(bienSo);
        return bienSo;
    }

    private boolean matches(Xe xe, DanhSachXeCriteria criteria) {
        if (!this.containsText(criteria.bienSo(), xe.getBienSo())) {
            return false;
        }

        if (!this.containsText(criteria.hangXe(), xe.getHangXe())) {
            return false;
        }

        if (!this.containsText(criteria.dongXe(), xe.getDongXe())) {
            return false;
        }

        if (criteria.doiXeTu() != null
                && xe.getDoiXe() < criteria.doiXeTu()) {
            return false;
        }

        if (criteria.doiXeDen() != null
                && xe.getDoiXe() > criteria.doiXeDen()) {
            return false;
        }

        if (criteria.odoTu() != null
                && xe.getOdo() < criteria.odoTu()) {
            return false;
        }

        if (criteria.odoDen() != null
                && xe.getOdo() > criteria.odoDen()) {
            return false;
        }

        if (criteria.timNhanh() != null
                && !criteria.timNhanh().isBlank()) {
            String combined = xe.getBienSo() + " "
                    + xe.getHangXe() + " " + xe.getDongXe();

            if (!TextNormalizerUtil.normalize(combined)
                    .contains(TextNormalizerUtil.normalize(criteria.timNhanh()))) {
                return false;
            }
        }

        return true;
    }

    private boolean containsText(String filter, String value) {
        if (filter == null || filter.isBlank()) {
            return true;
        }

        return TextNormalizerUtil.normalize(value)
                .contains(TextNormalizerUtil.normalize(filter));
    }

    private String normalizeBienSo(String bienSo) {
        if (bienSo == null) {
            return "";
        }

        return bienSo.replaceAll("[\\s.-]", "")
                .toUpperCase(Locale.ROOT);
    }

    private String normalizeTenXe(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }

        return value.trim().toUpperCase(Locale.ROOT);
    }
}
