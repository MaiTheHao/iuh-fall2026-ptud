package iuh.fit.service;

import iuh.fit.dao.NhanVienDAO;
import iuh.fit.dto.DanhSachNhanVienCriteria;
import iuh.fit.dto.PaginatedResult;
import iuh.fit.entity.NhanVien;
import iuh.fit.util.MaGeneratorUtil;
import iuh.fit.util.TextNormalizerUtil;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class NhanVienService {

    private final NhanVienDAO nhanVienDAO = new NhanVienDAO();

    private static final String ERROR_ID =
            "app.nhanVien.khongTimThay";
    private static final String ERROR_ID_REQUIRED =
            "app.nhanVien.loiMaNhanVien";
    private static final String ERROR_USERNAME =
            "app.nhanVien.loiTenDangNhap";
    private static final String ERROR_USERNAME_EXISTS =
            "app.nhanVien.trungTenDangNhap";
    private static final String ERROR_PASSWORD =
            "app.nhanVien.loiMatKhau";
    private static final String ERROR_NAME =
            "app.nhanVien.loiHoTen";
    private static final String ERROR_PHONE =
            "app.nhanVien.loiSoDienThoai";
    private static final String ERROR_ROLE =
            "app.nhanVien.loiVaiTro";
    private static final String ERROR_STATUS =
            "app.nhanVien.loiTrangThai";
    private static final String ERROR_DATA =
            "app.nhanVien.loiDuLieu";
    private static final String ERROR_IN_USE =
            "app.nhanVien.loiDangSuDung";

    public PaginatedResult<NhanVien> search(
            DanhSachNhanVienCriteria criteria) throws SQLException {

        DanhSachNhanVienCriteria filter = criteria == null
                ? DanhSachNhanVienCriteria.macDinh()
                : criteria;

        List<NhanVien> matched = this.nhanVienDAO.findAll()
                .stream()
                .filter(nv -> this.matches(
                        nv.getMaNhanVien(),
                        filter.maNhanVien()))
                .filter(nv -> this.matches(
                        nv.getHoTen(),
                        filter.hoTen()))
                .filter(nv -> this.matches(
                        nv.getSoDienThoai(),
                        filter.soDienThoai()))
                .filter(nv -> this.matches(
                        nv.getVaiTro() == null
                                ? null : nv.getVaiTro().name(),
                        filter.vaiTro()))
                .filter(nv -> this.matches(
                        nv.getTrangThai() == null
                                ? null : nv.getTrangThai().name(),
                        filter.trangThai()))
                .filter(nv -> this.matchesQuick(
                        nv, filter.timNhanh()))
                .filter(nv -> this.matchesDateFrom(
                        nv.getNgayTao(), filter.ngayTaoTu()))
                .filter(nv -> this.matchesDateTo(
                        nv.getNgayTao(), filter.ngayTaoDen()))
                .sorted(Comparator.comparing(NhanVien::getMaNhanVien))
                .toList();

        return PaginatedResult.of(matched, filter);
    }

    private boolean matches(String value, String condition) {
        if (this.isBlank(condition)) {
            return true;
        }

        return TextNormalizerUtil.normalize(value)
                .contains(TextNormalizerUtil.normalize(condition));
    }

    private boolean matchesDateFrom(
            LocalDateTime ngayTao,
            LocalDate tuNgay) {

        return tuNgay == null
                || (ngayTao != null
                    && !ngayTao.toLocalDate().isBefore(tuNgay));
    }

    private boolean matchesDateTo(
            LocalDateTime ngayTao,
            LocalDate denNgay) {

        return denNgay == null
                || (ngayTao != null
                    && !ngayTao.toLocalDate().isAfter(denNgay));
    }

    private boolean matchesQuick(NhanVien nv, String keyword) {
        if (this.isBlank(keyword)) {
            return true;
        }

        String key = TextNormalizerUtil.normalize(keyword);

        return TextNormalizerUtil.normalize(
                    nv.getMaNhanVien()).contains(key)

                || TextNormalizerUtil.normalize(
                    nv.getTenDangNhap()).contains(key)

                || TextNormalizerUtil.normalize(
                    nv.getHoTen()).contains(key)

                || TextNormalizerUtil.normalize(
                    nv.getSoDienThoai()).contains(key)

                || TextNormalizerUtil.normalize(
                    nv.getVaiTro() == null
                            ? null : nv.getVaiTro().name())
                    .contains(key)

                || TextNormalizerUtil.normalize(
                    nv.getTrangThai() == null
                            ? null : nv.getTrangThai().name())
                    .contains(key);
    }

    public NhanVien getById(String maNhanVien)
            throws SQLException {

        if (this.isBlank(maNhanVien)) {
            throw new IllegalArgumentException(ERROR_ID_REQUIRED);
        }

        Optional<NhanVien> result =
                this.nhanVienDAO.findById(maNhanVien.trim());

        return result.orElseThrow(
                () -> new IllegalArgumentException(ERROR_ID));
    }

    public void create(NhanVien nhanVien) throws SQLException {
        this.validateCommon(nhanVien);

        LocalDateTime now = LocalDateTime.now();

        String ma = MaGeneratorUtil.sinhMaNhanVien(
                nhanVien.getSoDienThoai(), now);

        nhanVien.setMaNhanVien(ma);
        nhanVien.setNgayTao(now);

        this.validateCreate(nhanVien);

        String username = nhanVien.getTenDangNhap().trim();

        if (this.nhanVienDAO.findById(ma).isPresent()) {
            throw new IllegalArgumentException(ERROR_DATA);
        }

        if (this.nhanVienDAO.existsByTenDangNhap(username)) {
            throw new IllegalArgumentException(ERROR_USERNAME_EXISTS);
        }

        nhanVien.setTenDangNhap(username);
        nhanVien.setHoTen(nhanVien.getHoTen().trim());
        nhanVien.setSoDienThoai(
                this.trimToNull(nhanVien.getSoDienThoai())
        );

        this.nhanVienDAO.insert(nhanVien);
    }

    public void update(NhanVien nhanVien) throws SQLException {
        this.validateCommon(nhanVien);

        if (this.isBlank(nhanVien.getMaNhanVien())) {
            throw new IllegalArgumentException(ERROR_ID_REQUIRED);
        }

        String ma = nhanVien.getMaNhanVien().trim();

        this.getById(ma);

        nhanVien.setMaNhanVien(ma);
        nhanVien.setHoTen(nhanVien.getHoTen().trim());
        nhanVien.setSoDienThoai(
                this.trimToNull(nhanVien.getSoDienThoai()));

        if (this.isBlank(nhanVien.getMatKhau())) {
            this.nhanVienDAO.updateInfo(nhanVien);
            return;
        }

        if (nhanVien.getMatKhau().length() > 255) {
            throw new IllegalArgumentException(ERROR_PASSWORD);
        }

        this.nhanVienDAO.update(nhanVien);
    }

    public void delete(String maNhanVien) throws SQLException {
        if (this.isBlank(maNhanVien)) {
            throw new IllegalArgumentException(ERROR_ID_REQUIRED);
        }

        String ma = maNhanVien.trim();

        this.getById(ma);

        if (this.nhanVienDAO.existsPhieuTiepNhan(ma)
                || this.nhanVienDAO.existsPhieuNhap(ma)
                || this.nhanVienDAO.existsPhanCong(ma)) {
            throw new IllegalArgumentException(ERROR_IN_USE);
        }

        if (!this.nhanVienDAO.delete(ma)) {
            throw new IllegalArgumentException(ERROR_ID);
        }
    }

    private void validateCommon(NhanVien nv) {
        if (nv == null) {
            throw new IllegalArgumentException(ERROR_DATA);
        }

        if (this.isBlank(nv.getHoTen())
                || nv.getHoTen().trim().length() > 50) {
            throw new IllegalArgumentException(ERROR_NAME);
        }

        this.validatePhone(nv.getSoDienThoai());

        if (nv.getVaiTro() == null) {
            throw new IllegalArgumentException(ERROR_ROLE);
        }

        if (nv.getTrangThai() == null) {
            throw new IllegalArgumentException(ERROR_STATUS);
        }
    }

    private void validateCreate(NhanVien nv) {
        if (nv.getMaNhanVien() == null
                || nv.getMaNhanVien().trim().length() > 64) {
            throw new IllegalArgumentException(ERROR_ID_REQUIRED);
        }

        if (this.isBlank(nv.getTenDangNhap())
                || nv.getTenDangNhap().trim().length() > 30) {
            throw new IllegalArgumentException(ERROR_USERNAME);
        }

        if (this.isBlank(nv.getMatKhau())
                || nv.getMatKhau().length() > 255) {
            throw new IllegalArgumentException(ERROR_PASSWORD);
        }
    }

    private void validatePhone(String phone) {
        if (phone == null || !phone.trim().matches("\\d{9,10}")) {
            throw new IllegalArgumentException(ERROR_PHONE);
        }
    }

    private String trimToNull(String value) {
        return this.isBlank(value) ? null : value.trim();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
