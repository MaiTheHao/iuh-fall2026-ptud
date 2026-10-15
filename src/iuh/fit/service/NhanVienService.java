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

    /**
     * Lấy toàn bộ danh sách nhân viên.
     */
    public List<NhanVien> getAll() throws SQLException {
        return nhanVienDAO.findAll();
    }

    /**
     * Tìm kiếm nhân viên theo tiêu chí và phân trang.
     */
    public PaginatedResult<NhanVien> search(
            DanhSachNhanVienCriteria criteria) throws SQLException {

    	DanhSachNhanVienCriteria filter = criteria == null
    	        ? new DanhSachNhanVienCriteria(
    	                null, null, null, null, null, null,
    	                null, null,
    	                1, PaginatedResult.DEFAULT_PAGE_SIZE)
    	        : criteria;

        List<NhanVien> matched = nhanVienDAO.findAll()
                .stream()
                .filter(nv -> matches(
                        nv.getMaNhanVien(),
                        filter.maNhanVien()))
                .filter(nv -> matches(
                        nv.getHoTen(),
                        filter.hoTen()))
                .filter(nv -> matches(
                        nv.getSoDienThoai(),
                        filter.soDienThoai()))
                .filter(nv -> matches(
                        nv.getVaiTro() == null
                                ? null : nv.getVaiTro().name(),
                        filter.vaiTro()))
                .filter(nv -> matches(
                        nv.getTrangThai() == null
                                ? null : nv.getTrangThai().name(),
                        filter.trangThai()))
                .filter(nv -> matchesQuick(
                        nv, filter.timNhanh()))
                .filter(nv -> matchesDateFrom(
                        nv.getNgayTao(), filter.ngayTaoTu()))
                .filter(nv -> matchesDateTo(
                        nv.getNgayTao(), filter.ngayTaoDen()))
                .toList();

        return PaginatedResult.of(
                matched,
                filter.page(),
                filter.pageSize());
    }

    /**
     * So khớp bộ lọc riêng theo từng cột.
     * Không phân biệt chữ hoa, chữ thường và dấu tiếng Việt.
     */
    private boolean matches(String value, String condition) {
        if (isBlank(condition)) {
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
    /**
     * Tìm nhanh theo mã nhân viên, tên đăng nhập, họ tên,
     * số điện thoại, vai trò và trạng thái.
     */
    private boolean matchesQuick(NhanVien nv, String keyword) {
        if (isBlank(keyword)) {
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

    /**
     * Lấy nhân viên theo mã.
     */
    public NhanVien getById(String maNhanVien)
            throws SQLException {

        if (isBlank(maNhanVien)) {
            throw new IllegalArgumentException(ERROR_ID_REQUIRED);
        }

        Optional<NhanVien> result =
                nhanVienDAO.findById(maNhanVien.trim());

        return result.orElseThrow(
                () -> new IllegalArgumentException(ERROR_ID));
    }

    /**
     * Thêm nhân viên mới.
     */
    public void create(NhanVien nhanVien) throws SQLException {
        validateCommon(nhanVien);
        validateCreate(nhanVien);
        String ma = MaGeneratorUtil.genMaNhanVien(
                nhanVien.getSoDienThoai()
        );

        String username = nhanVien.getTenDangNhap().trim();

        if (nhanVienDAO.findById(ma).isPresent()) {
            throw new IllegalArgumentException(ERROR_DATA);
        }

        if (nhanVienDAO.existsByTenDangNhap(username)) {
            throw new IllegalArgumentException(ERROR_USERNAME_EXISTS);
        }

        nhanVien.setMaNhanVien(ma);
        nhanVien.setTenDangNhap(username);
        nhanVien.setHoTen(nhanVien.getHoTen().trim());
        nhanVien.setSoDienThoai(
                trimToNull(nhanVien.getSoDienThoai())
        );

        nhanVienDAO.insert(nhanVien);
    }

    /**
     * Cập nhật thông tin nhân viên.
     * Không cập nhật mật khẩu hiện tại.
     */
    public void update(NhanVien nhanVien) throws SQLException {
        validateCommon(nhanVien);

        if (isBlank(nhanVien.getMaNhanVien())) {
            throw new IllegalArgumentException(ERROR_ID_REQUIRED);
        }

        String ma = nhanVien.getMaNhanVien().trim();

        getById(ma);

        nhanVien.setMaNhanVien(ma);
        nhanVien.setHoTen(nhanVien.getHoTen().trim());
        nhanVien.setSoDienThoai(
                trimToNull(nhanVien.getSoDienThoai()));

        nhanVienDAO.updateInfo(nhanVien);
    }

    /**
     * Xóa nhân viên theo mã.
     */
    public void delete(String maNhanVien) throws SQLException {
        if (isBlank(maNhanVien)) {
            throw new IllegalArgumentException(ERROR_ID_REQUIRED);
        }

        String ma = maNhanVien.trim();

        getById(ma);

        if (!nhanVienDAO.delete(ma)) {
            throw new IllegalArgumentException(ERROR_ID);
        }
    }

    /**
     * Kiểm tra các thông tin chung khi thêm hoặc cập nhật.
     */
    private void validateCommon(NhanVien nv) {
        if (nv == null) {
            throw new IllegalArgumentException(ERROR_DATA);
        }

        if (isBlank(nv.getHoTen())
                || nv.getHoTen().trim().length() > 50) {
            throw new IllegalArgumentException(ERROR_NAME);
        }

        validatePhone(nv.getSoDienThoai());

        if (nv.getVaiTro() == null) {
            throw new IllegalArgumentException(ERROR_ROLE);
        }

        if (nv.getTrangThai() == null) {
            throw new IllegalArgumentException(ERROR_STATUS);
        }
    }

    /**
     * Kiểm tra các thông tin bắt buộc khi thêm mới.
     */
    private void validateCreate(NhanVien nv) {
        if (isBlank(nv.getMaNhanVien())
                || nv.getMaNhanVien().trim().length() > 20) {
            throw new IllegalArgumentException(ERROR_ID_REQUIRED);
        }

        if (isBlank(nv.getTenDangNhap())
                || nv.getTenDangNhap().trim().length() > 30) {
            throw new IllegalArgumentException(ERROR_USERNAME);
        }

        if (isBlank(nv.getMatKhau())
                || nv.getMatKhau().length() > 255) {
            throw new IllegalArgumentException(ERROR_PASSWORD);
        }
    }

    /**
     * Số điện thoại không bắt buộc.
     * Nếu có nhập, phải gồm 9 hoặc 10 chữ số.
     */
    private void validatePhone(String phone) {
        if (isBlank(phone)) {
            return;
        }

        if (!phone.trim().matches("\\d{9,10}")) {
            throw new IllegalArgumentException(ERROR_PHONE);
        }
    }

    /**
     * Chuỗi rỗng được chuyển thành null.
     */
    private String trimToNull(String value) {
        return isBlank(value) ? null : value.trim();
    }

    /**
     * Kiểm tra chuỗi null hoặc chỉ chứa khoảng trắng.
     */
    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}