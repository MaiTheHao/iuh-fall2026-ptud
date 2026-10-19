package iuh.fit.service;

import iuh.fit.core.context.AppContext;
import iuh.fit.dao.KhachHangDAO;
import iuh.fit.dao.KhieuNaiDAO;
import iuh.fit.dao.PhieuTiepNhanDAO;
import iuh.fit.dto.DanhSachKhieuNaiCriteria;
import iuh.fit.dto.KhieuNaiFormData;
import iuh.fit.dto.KhieuNaiUpdateFormData;
import iuh.fit.dto.KhieuNaiView;
import iuh.fit.dto.PaginatedResult;
import iuh.fit.entity.KhachHang;
import iuh.fit.entity.KhieuNai;
import iuh.fit.entity.NhanVien;
import iuh.fit.entity.PhieuTiepNhan;
import iuh.fit.enumeration.TrangThaiKhieuNai;
import iuh.fit.enumeration.TrangThaiPhieuTiepNhan;
import iuh.fit.util.MaGeneratorUtil;
import iuh.fit.util.MaRetryUtil;
import iuh.fit.util.TextNormalizerUtil;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Nghiệp vụ tiếp nhận, tra cứu, cập nhật và xóa khiếu nại của khách hàng.
 */
public class KhieuNaiService {

    private static final String ERROR_DU_LIEU = "app.khieuNai.loiDuLieu";
    private static final String ERROR_MA = "app.khieuNai.loiMa";
    private static final String ERROR_KHONG_TON_TAI = "app.khieuNai.khongTonTai";
    private static final String ERROR_KHACH_HANG = "app.khieuNai.loiKhachHang";
    private static final String ERROR_KHACH_HANG_NOT_FOUND = "app.khieuNai.loiKhachHangKhongTonTai";
    private static final String ERROR_PHIEU = "app.khieuNai.loiPhieu";
    private static final String ERROR_PHIEU_NOT_FOUND = "app.khieuNai.loiPhieuKhongTonTai";
    private static final String ERROR_PHIEU_NOT_OF_CUSTOMER = "app.khieuNai.loiPhieuKhongThuocKhach";
    private static final String ERROR_PHIEU_DA_HUY = "app.khieuNai.loiPhieuDaHuy";
    private static final String ERROR_NOI_DUNG = "app.khieuNai.loiNoiDung";
    private static final String ERROR_NHAN_VIEN = "app.khieuNai.loiNhanVien";
    private static final String ERROR_TRANG_THAI = "app.khieuNai.loiTrangThai";
    private static final String ERROR_TRANG_THAI_LUI = "app.khieuNai.loiTrangThaiLui";
    private static final String ERROR_TRANG_THAI_CUOI = "app.khieuNai.loiTrangThaiCuoi";
    private static final String ERROR_GHI_DU_LIEU = "app.khieuNai.loiGhiDuLieu";

    private final KhieuNaiDAO khieuNaiDAO = new KhieuNaiDAO();
    private final KhachHangDAO khachHangDAO = new KhachHangDAO();
    private final PhieuTiepNhanDAO phieuTiepNhanDAO = new PhieuTiepNhanDAO();

    public KhieuNai create(KhieuNaiFormData data) throws SQLException {
        this.validate(data);

        NhanVien nguoiDung = AppContext.getInstance().getCurrentUser();
        String maNhanVien = nguoiDung == null ? null : nguoiDung.getMaNhanVien();
        if (this.isBlank(maNhanVien)) {
            throw new IllegalArgumentException(ERROR_NHAN_VIEN);
        }

        LocalDateTime now = LocalDateTime.now();

        KhieuNai khieuNai = new KhieuNai();
        khieuNai.setMaKhachHang(data.maKhachHang().trim());
        khieuNai.setMaPhieuTiepNhan(data.maPhieuTiepNhan().trim());
        khieuNai.setNoiDung(data.noiDung().trim());
        khieuNai.setTrangThai(TrangThaiKhieuNai.CHUA_XU_LY);

        MaRetryUtil.thucHien(
                lanThu -> {
                    LocalDateTime thoiDiem = now.plusSeconds(lanThu);
                    khieuNai.setNgayTao(thoiDiem);
                    return MaGeneratorUtil.sinhMaKhieuNai(maNhanVien, thoiDiem);
                },
                ma -> {
                    khieuNai.setMaKhieuNai(ma);
                    if (!this.khieuNaiDAO.insert(khieuNai)) {
                        throw new SQLException(ERROR_GHI_DU_LIEU);
                    }
                });

        return khieuNai;
    }

    public PaginatedResult<KhieuNaiView> search(DanhSachKhieuNaiCriteria criteria) throws SQLException {
        DanhSachKhieuNaiCriteria filter = criteria == null
                ? new DanhSachKhieuNaiCriteria(null, null, null, null, null, null, null, null, 1,
                        PaginatedResult.DEFAULT_PAGE_SIZE)
                : criteria;

        List<KhieuNai> danhSachKhieuNai = this.khieuNaiDAO.findAll();

        Map<String, String> tenKhachHangTheoMa = new HashMap<>();
        for (KhachHang khachHang : this.khachHangDAO.findAll()) {
            tenKhachHangTheoMa.putIfAbsent(
                    khachHang.getMaKhachHang(),
                    khachHang.getHoTen() == null ? "" : khachHang.getHoTen());
        }

        List<KhieuNaiView> views = new ArrayList<>();
        for (KhieuNai khieuNai : danhSachKhieuNai) {
            String tenKhachHang = tenKhachHangTheoMa.get(khieuNai.getMaKhachHang());
            if (tenKhachHang == null) {
                tenKhachHang = "";
            }
            if (this.matches(khieuNai, tenKhachHang, filter)) {
                views.add(new KhieuNaiView(
                        khieuNai.getMaKhieuNai(),
                        tenKhachHang,
                        khieuNai.getMaPhieuTiepNhan(),
                        khieuNai.getNoiDung(),
                        khieuNai.getTrangThai(),
                        khieuNai.getNgayTao()));
            }
        }

        views.sort(Comparator.comparing(
                KhieuNaiView::ngayTao,
                Comparator.nullsLast(Comparator.reverseOrder())));

        return PaginatedResult.of(views, filter.page(), filter.pageSize());
    }

    public KhieuNai getById(String maKhieuNai) throws SQLException {
        if (this.isBlank(maKhieuNai)) {
            throw new IllegalArgumentException(ERROR_MA);
        }
        return this.khieuNaiDAO.findById(maKhieuNai.trim())
                .orElseThrow(() -> new IllegalArgumentException(ERROR_KHONG_TON_TAI));
    }

    public KhieuNai update(KhieuNaiUpdateFormData data) throws SQLException {
        if (data == null || this.isBlank(data.maKhieuNai())) {
            throw new IllegalArgumentException(ERROR_MA);
        }

        KhieuNai khieuNai = this.khieuNaiDAO.findById(data.maKhieuNai().trim())
                .orElseThrow(() -> new IllegalArgumentException(ERROR_KHONG_TON_TAI));

        if (this.isBlank(data.noiDung())) {
            throw new IllegalArgumentException(ERROR_NOI_DUNG);
        }

        if (data.trangThai() == null) {
            throw new IllegalArgumentException(ERROR_TRANG_THAI);
        }

        this.kiemTraChuyenTrangThai(khieuNai.getTrangThai(), data.trangThai());

        khieuNai.setNoiDung(data.noiDung().trim());
        khieuNai.setTrangThai(data.trangThai());

        if (!this.khieuNaiDAO.update(khieuNai)) {
            throw new IllegalArgumentException(ERROR_KHONG_TON_TAI);
        }

        return khieuNai;
    }

    public void delete(String maKhieuNai) throws SQLException {
        if (this.isBlank(maKhieuNai)) {
            throw new IllegalArgumentException(ERROR_MA);
        }

        String ma = maKhieuNai.trim();
        if (this.khieuNaiDAO.findById(ma).isEmpty()) {
            throw new IllegalArgumentException(ERROR_KHONG_TON_TAI);
        }

        if (!this.khieuNaiDAO.delete(ma)) {
            throw new IllegalArgumentException(ERROR_KHONG_TON_TAI);
        }
    }

    private void validate(KhieuNaiFormData data) throws SQLException {
        if (data == null) {
            throw new IllegalArgumentException(ERROR_DU_LIEU);
        }

        if (this.isBlank(data.maKhachHang())) {
            throw new IllegalArgumentException(ERROR_KHACH_HANG);
        }

        if (this.isBlank(data.maPhieuTiepNhan())) {
            throw new IllegalArgumentException(ERROR_PHIEU);
        }

        if (this.isBlank(data.noiDung())) {
            throw new IllegalArgumentException(ERROR_NOI_DUNG);
        }

        String maKhachHang = data.maKhachHang().trim();
        if (this.khachHangDAO.findById(maKhachHang).isEmpty()) {
            throw new IllegalArgumentException(ERROR_KHACH_HANG_NOT_FOUND);
        }

        PhieuTiepNhan phieu = this.phieuTiepNhanDAO.findById(data.maPhieuTiepNhan().trim())
                .orElseThrow(() -> new IllegalArgumentException(ERROR_PHIEU_NOT_FOUND));

        String maKhachHangCuaPhieu = phieu.getMaKhachHang() == null ? "" : phieu.getMaKhachHang().trim();
        if (!maKhachHang.equals(maKhachHangCuaPhieu)) {
            throw new IllegalArgumentException(ERROR_PHIEU_NOT_OF_CUSTOMER);
        }

        if (phieu.getTrangThai() == TrangThaiPhieuTiepNhan.DA_HUY) {
            throw new IllegalArgumentException(ERROR_PHIEU_DA_HUY);
        }
    }

    private void kiemTraChuyenTrangThai(TrangThaiKhieuNai hienTai, TrangThaiKhieuNai moi) {
        if (hienTai == moi) {
            return;
        }

        if (hienTai == TrangThaiKhieuNai.DA_XU_LY) {
            throw new IllegalArgumentException(ERROR_TRANG_THAI_CUOI);
        }

        if (hienTai == null || moi.ordinal() != hienTai.ordinal() + 1) {
            throw new IllegalArgumentException(ERROR_TRANG_THAI_LUI);
        }
    }

    private boolean matches(KhieuNai khieuNai, String tenKhachHang, DanhSachKhieuNaiCriteria filter) {
        if (!this.containsText(filter.maKhieuNai(), khieuNai.getMaKhieuNai())) {
            return false;
        }
        if (!this.containsText(filter.tenKhachHang(), tenKhachHang)) {
            return false;
        }
        if (!this.containsText(filter.maPhieuTiepNhan(), khieuNai.getMaPhieuTiepNhan())) {
            return false;
        }
        if (!this.containsText(filter.noiDung(), khieuNai.getNoiDung())) {
            return false;
        }
        if (filter.trangThai() != null && filter.trangThai() != khieuNai.getTrangThai()) {
            return false;
        }
        if (this.isEarlierThan(khieuNai.getNgayTao(), filter.ngayTaoTu())) {
            return false;
        }
        if (this.isLaterThan(khieuNai.getNgayTao(), filter.ngayTaoDen())) {
            return false;
        }
        if (!this.isBlank(filter.timNhanh())) {
            String combined = khieuNai.getMaKhieuNai() + " " + tenKhachHang + " "
                    + khieuNai.getMaPhieuTiepNhan() + " " + khieuNai.getNoiDung();
            if (!TextNormalizerUtil.normalize(combined)
                    .contains(TextNormalizerUtil.normalize(filter.timNhanh()))) {
                return false;
            }
        }
        return true;
    }

    private boolean containsText(String filterValue, String value) {
        if (this.isBlank(filterValue)) {
            return true;
        }
        return TextNormalizerUtil.normalize(value)
                .contains(TextNormalizerUtil.normalize(filterValue));
    }

    private boolean isEarlierThan(LocalDateTime ngayTao, LocalDate bound) {
        return bound != null && (ngayTao == null || ngayTao.toLocalDate().isBefore(bound));
    }

    private boolean isLaterThan(LocalDateTime ngayTao, LocalDate bound) {
        return bound != null && (ngayTao == null || ngayTao.toLocalDate().isAfter(bound));
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
