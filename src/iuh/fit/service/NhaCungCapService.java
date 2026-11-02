package iuh.fit.service;

import iuh.fit.dao.NhaCungCapDAO;
import iuh.fit.dao.TransactionScope;
import iuh.fit.dto.DanhSachNhaCungCapCriteria;
import iuh.fit.dto.PaginatedResult;
import iuh.fit.entity.NhaCungCap;
import iuh.fit.enumeration.LoaiMa;
import iuh.fit.util.TextNormalizerUtil;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class NhaCungCapService {

    private static final String ERROR_TEN = "app.nhaCungCap.loiTen";
    private static final String ERROR_TEN_DAI = "app.nhaCungCap.loiTenDai";
    private static final String ERROR_SDT = "app.nhaCungCap.loiSoDienThoai";
    private static final String ERROR_SDT_TRUNG = "app.nhaCungCap.loiSoDienThoaiTrung";
    private static final String ERROR_EMAIL = "app.nhaCungCap.loiEmail";
    private static final String ERROR_EMAIL_DAI = "app.nhaCungCap.loiEmailDai";
    private static final String ERROR_DIA_CHI_DAI = "app.nhaCungCap.loiDiaChiDai";
    private static final String ERROR_GHI_CHU_DAI = "app.nhaCungCap.loiGhiChuDai";
    private static final String ERROR_TRANG_THAI = "app.nhaCungCap.loiTrangThai";
    private static final String ERROR_DATA = "app.nhaCungCap.loiDuLieuKhongHopLe";
    private static final String ERROR_NOT_FOUND = "app.nhaCungCap.khongTimThay";
    private static final String ERROR_DANG_SU_DUNG = "app.nhaCungCap.loiDangSuDung";

    private final NhaCungCapDAO nhaCungCapDAO = new NhaCungCapDAO();
    private final MaService maService = new MaService();

    public List<NhaCungCap> getAllForSelection() throws SQLException {
        List<NhaCungCap> list = new ArrayList<>(this.nhaCungCapDAO.findAll());
        list.sort(Comparator.comparing(NhaCungCap::getMaNhaCungCap));
        return list;
    }

    public PaginatedResult<NhaCungCap> search(DanhSachNhaCungCapCriteria criteria) throws SQLException {
        DanhSachNhaCungCapCriteria filter = criteria == null
                ? DanhSachNhaCungCapCriteria.macDinh()
                : criteria;

        List<NhaCungCap> matched = this.nhaCungCapDAO.findAll().stream()
                .filter(item -> this.matches(item, filter))
                .sorted(Comparator.comparing(NhaCungCap::getMaNhaCungCap))
                .toList();

        return PaginatedResult.of(matched, filter);
    }

    public Optional<NhaCungCap> getById(String maNhaCungCap) throws SQLException {
        return this.nhaCungCapDAO.findById(maNhaCungCap);
    }

    public void create(NhaCungCap nhaCungCap) throws SQLException {
        this.validate(nhaCungCap);

        if (this.nhaCungCapDAO.existsSdt(nhaCungCap.getSdt(), null)) {
            throw new IllegalArgumentException(ERROR_SDT_TRUNG);
        }

        try (TransactionScope scope = TransactionScope.begin()) {
            nhaCungCap.setMaNhaCungCap(this.maService.genMa(scope, LoaiMa.NCC));
            this.nhaCungCapDAO.insert(scope, nhaCungCap);
            scope.commit();
        }
    }

    public void update(NhaCungCap nhaCungCap) throws SQLException {
        this.validate(nhaCungCap);

        if (nhaCungCap.getMaNhaCungCap() == null
                || this.nhaCungCapDAO.findById(nhaCungCap.getMaNhaCungCap()).isEmpty()) {
            throw new IllegalArgumentException(ERROR_NOT_FOUND);
        }

        if (this.nhaCungCapDAO.existsSdt(nhaCungCap.getSdt(), nhaCungCap.getMaNhaCungCap())) {
            throw new IllegalArgumentException(ERROR_SDT_TRUNG);
        }

        try (TransactionScope scope = TransactionScope.begin()) {
            this.nhaCungCapDAO.update(scope, nhaCungCap);
            scope.commit();
        }
    }

    public void delete(String maNhaCungCap) throws SQLException {
        if (this.getById(maNhaCungCap).isEmpty()) {
            throw new IllegalArgumentException(ERROR_NOT_FOUND);
        }

        if (this.nhaCungCapDAO.existsPhuTung(maNhaCungCap)) {
            throw new IllegalStateException(ERROR_DANG_SU_DUNG);
        }

        try (TransactionScope scope = TransactionScope.begin()) {
            this.nhaCungCapDAO.delete(scope, maNhaCungCap);
            scope.commit();
        }
    }

    private void validate(NhaCungCap nhaCungCap) {
        if (nhaCungCap == null) {
            throw new IllegalArgumentException(ERROR_DATA);
        }

        if (nhaCungCap.getTenNhaCungCap() == null || nhaCungCap.getTenNhaCungCap().trim().isEmpty()) {
            throw new IllegalArgumentException(ERROR_TEN);
        }

        if (nhaCungCap.getTenNhaCungCap().length() > 100) {
            throw new IllegalArgumentException(ERROR_TEN_DAI);
        }

        if (nhaCungCap.getSdt() == null || !nhaCungCap.getSdt().matches("^0[0-9]{8,10}$")) {
            throw new IllegalArgumentException(ERROR_SDT);
        }

        if (nhaCungCap.getEmail() != null && !nhaCungCap.getEmail().isBlank()
                && !nhaCungCap.getEmail().matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            throw new IllegalArgumentException(ERROR_EMAIL);
        }

        if (nhaCungCap.getEmail() != null && nhaCungCap.getEmail().length() > 100) {
            throw new IllegalArgumentException(ERROR_EMAIL_DAI);
        }

        if (nhaCungCap.getDiaChi() != null && nhaCungCap.getDiaChi().length() > 255) {
            throw new IllegalArgumentException(ERROR_DIA_CHI_DAI);
        }

        if (nhaCungCap.getGhiChu() != null && nhaCungCap.getGhiChu().length() > 500) {
            throw new IllegalArgumentException(ERROR_GHI_CHU_DAI);
        }

        if (nhaCungCap.getTrangThai() == null) {
            throw new IllegalArgumentException(ERROR_TRANG_THAI);
        }
    }

    private boolean matches(NhaCungCap item, DanhSachNhaCungCapCriteria filter) {
        if (!this.containsText(filter.maNhaCungCap(), item.getMaNhaCungCap())) {
            return false;
        }

        if (!this.containsText(filter.tenNhaCungCap(), item.getTenNhaCungCap())) {
            return false;
        }

        if (!this.containsText(filter.sdt(), item.getSdt())) {
            return false;
        }

        if (filter.timNhanh() != null && !filter.timNhanh().isBlank()) {
            String combined = item.getMaNhaCungCap() + " " + item.getTenNhaCungCap()
                    + " " + item.getSdt() + " " + (item.getEmail() == null ? "" : item.getEmail());

            if (!TextNormalizerUtil.normalize(combined).contains(TextNormalizerUtil.normalize(filter.timNhanh()))) {
                return false;
            }
        }

        if (filter.trangThai() != null && !filter.trangThai().isBlank()) {
            String trangThai = item.getTrangThai() == null ? null : item.getTrangThai().name();
            if (!filter.trangThai().equals(trangThai)) {
                return false;
            }
        }

        if (filter.ngayTaoTu() != null
                && (item.getNgayTao() == null || item.getNgayTao().toLocalDate().isBefore(filter.ngayTaoTu()))) {
            return false;
        }

        if (filter.ngayTaoDen() != null
                && (item.getNgayTao() == null || item.getNgayTao().toLocalDate().isAfter(filter.ngayTaoDen()))) {
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
