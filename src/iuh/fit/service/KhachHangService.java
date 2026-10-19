package iuh.fit.service;

import iuh.fit.dao.KhachHangDAO;
import iuh.fit.dao.TransactionScope;
import iuh.fit.dto.DanhSachKhachHangCriteria;
import iuh.fit.dto.PaginatedResult;
import iuh.fit.entity.KhachHang;
import iuh.fit.util.MaGeneratorUtil;
import iuh.fit.util.TextNormalizerUtil;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class KhachHangService {

    private static final String ERROR_PHONE = "app.khachHang.loiSdt";
    private static final String ERROR_FULL_NAME = "app.khachHang.loiHoTen";
    private static final String ERROR_FULL_NAME_TOO_LONG = "app.khachHang.loiHoTenQuaDai";
    private static final String ERROR_DUPLICATE_PHONE = "app.khachHang.trungSdt";

    private final KhachHangDAO khachHangDAO = new KhachHangDAO();

    public PaginatedResult<KhachHang> search(DanhSachKhachHangCriteria criteria) throws SQLException {
        DanhSachKhachHangCriteria filter = criteria == null
                ? new DanhSachKhachHangCriteria(null, null, null, null, null, null, 1,
                        PaginatedResult.DEFAULT_PAGE_SIZE)
                : criteria;

        List<KhachHang> matched = this.khachHangDAO.findAll().stream()
                .filter(customer -> this.matches(customer, filter))
                .toList();

        return PaginatedResult.of(matched, filter.page(), filter.pageSize());
    }

    public void create(KhachHang customer) throws SQLException {
        this.chuanBiTao(customer);
        this.khachHangDAO.insert(customer);
    }

    public void create(TransactionScope scope, KhachHang customer) throws SQLException {
        this.chuanBiTao(customer);
        this.khachHangDAO.insert(scope, customer);
    }

    public Optional<KhachHang> getBySdt(String sdt) throws SQLException {
        String sdtChuanHoa = sdt == null ? "" : sdt.trim();
        if (sdtChuanHoa.isEmpty()) {
            return Optional.empty();
        }
        return this.khachHangDAO.findBySdt(sdtChuanHoa);
    }

    public Optional<KhachHang> getBySdt(TransactionScope scope, String sdt) throws SQLException {
        String sdtChuanHoa = sdt == null ? "" : sdt.trim();
        if (sdtChuanHoa.isEmpty()) {
            return Optional.empty();
        }
        return this.khachHangDAO.findBySdt(scope, sdtChuanHoa);
    }

    public void update(KhachHang customer) throws SQLException {
        this.validate(customer);
        this.khachHangDAO.update(customer);
    }

    public void delete(String maKhachHang) throws SQLException {
        this.khachHangDAO.delete(maKhachHang);
    }

    private void validate(KhachHang customer) {
        if (customer == null || customer.getHoTen() == null || customer.getHoTen().isBlank()) {
            throw new IllegalArgumentException(ERROR_FULL_NAME);
        }
        if (customer.getHoTen().trim().length() > 50) {
            throw new IllegalArgumentException(ERROR_FULL_NAME_TOO_LONG);
        }
        if (customer.getSdt() == null || !customer.getSdt().matches("\\d{10}")) {
            throw new IllegalArgumentException(ERROR_PHONE);
        }
    }

    private void chuanBiTao(KhachHang customer) throws SQLException {
        this.validate(customer);
        if (this.khachHangDAO.existsBySdt(customer.getSdt())) {
            throw new IllegalArgumentException(ERROR_DUPLICATE_PHONE);
        }
        LocalDateTime now = LocalDateTime.now();
        customer.setMaKhachHang(MaGeneratorUtil.sinhMa("KH", now));
        customer.setNgayTao(now);
    }

    private boolean matches(KhachHang customer, DanhSachKhachHangCriteria filter) {
        if (!this.containsText(filter.maKhachHang(), customer.getMaKhachHang())) {
            return false;
        }
        if (!this.containsText(filter.sdt(), customer.getSdt())) {
            return false;
        }
        if (!this.containsText(filter.hoTen(), customer.getHoTen())) {
            return false;
        }
        if (this.isEarlierThan(customer.getNgayTao(), filter.ngayTaoTu())) {
            return false;
        }
        if (this.isLaterThan(customer.getNgayTao(), filter.ngayTaoDen())) {
            return false;
        }
        if (filter.timNhanh() != null && !filter.timNhanh().isBlank()) {
            String combined = customer.getMaKhachHang() + " " + customer.getSdt() + " " + customer.getHoTen();
            if (!TextNormalizerUtil.normalize(combined)
                    .contains(TextNormalizerUtil.normalize(filter.timNhanh()))) {
                return false;
            }
        }
        return true;
    }

    private boolean containsText(String filterValue, String value) {
        if (filterValue == null || filterValue.isBlank()) {
            return true;
        }
        return TextNormalizerUtil.normalize(value)
                .contains(TextNormalizerUtil.normalize(filterValue));
    }

    private boolean isEarlierThan(LocalDateTime createdAt, LocalDate bound) {
        return bound != null && (createdAt == null || createdAt.toLocalDate().isBefore(bound));
    }

    private boolean isLaterThan(LocalDateTime createdAt, LocalDate bound) {
        return bound != null && (createdAt == null || createdAt.toLocalDate().isAfter(bound));
    }
}
