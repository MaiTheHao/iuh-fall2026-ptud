package iuh.fit.service;

import iuh.fit.core.context.AppContext;
import iuh.fit.dao.KhachHangDAO;
import iuh.fit.dao.PhieuTiepNhanDAO;
import iuh.fit.dao.TransactionScope;
import iuh.fit.dao.XeDAO;
import iuh.fit.dto.DanhSachPhieuTiepNhanCriteria;
import iuh.fit.dto.PaginatedResult;
import iuh.fit.dto.PhieuTiepNhanView;
import iuh.fit.entity.HoaDon;
import iuh.fit.entity.KhachHang;
import iuh.fit.entity.NhanVien;
import iuh.fit.entity.PhieuTiepNhan;
import iuh.fit.enumeration.TrangThaiHoaDon;
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
import java.util.Optional;

public class PhieuTiepNhanService {

    private static final String ERROR_DU_LIEU = "app.phieuTiepNhan.loiDuLieu";
    private static final String ERROR_NHAN_VIEN = "app.phieuTiepNhan.loiNhanVien";
    private static final String ERROR_KHACH_HANG = "app.phieuTiepNhan.loiKhachHang";
    private static final String ERROR_BIEN_SO = "app.phieuTiepNhan.loiBienSo";
    private static final String ERROR_KHACH_HANG_NOT_FOUND = "app.phieuTiepNhan.loiKhachHangKhongTonTai";
    private static final String ERROR_BIEN_SO_NOT_FOUND = "app.phieuTiepNhan.loiBienSoKhongTonTai";
    private static final String ERROR_TRANG_THAI_CUOI = "app.phieuTiepNhan.loiTrangThaiCuoi";
    private static final String ERROR_TRANG_THAI_KHONG_HOP_LE = "app.phieuTiepNhan.loiTrangThaiKhongHopLe";
    private static final String ERROR_MO_TA_TRONG = "app.phieuTiepNhan.loiMoTaTrong";
    private static final String ERROR_GHI_DU_LIEU = "app.phieuTiepNhan.loiGhiDuLieu";
    private static final String ERROR_PHIEU_NOT_FOUND = "app.phieuTiepNhan.khongTonTai";

    private final PhieuTiepNhanDAO phieuTiepNhanDAO = new PhieuTiepNhanDAO();
    private final KhachHangDAO khachHangDAO = new KhachHangDAO();
    private final XeDAO xeDAO = new XeDAO();
    private final HoaDonService hoaDonService = new HoaDonService();

    public PhieuTiepNhan create(PhieuTiepNhan phieu) throws SQLException {
        try (TransactionScope scope = TransactionScope.begin()) {
            PhieuTiepNhan ketQua = this.create(scope, phieu);
            scope.commit();
            return ketQua;
        }
    }

    public PhieuTiepNhan create(TransactionScope scope, PhieuTiepNhan phieu) throws SQLException {
        if (phieu != null) {
            NhanVien nguoiDung = AppContext.getInstance().getCurrentUser();
            phieu.setMaNhanVien(nguoiDung == null ? null : nguoiDung.getMaNhanVien());
        }

        this.validate(scope, phieu);

        String moTa = phieu.getMoTa() == null ? "" : phieu.getMoTa().trim();
        if (moTa.isBlank()) {
            throw new IllegalArgumentException(ERROR_MO_TA_TRONG);
        }
        phieu.setMoTa(moTa);

        LocalDateTime now = LocalDateTime.now();

        if (phieu.getNgayTao() == null) {
            phieu.setNgayTao(now);
        }

        if (phieu.getTrangThai() == null) {
            phieu.setTrangThai(TrangThaiPhieuTiepNhan.TIEP_NHAN);
        }

        MaRetryUtil.thucHien(
                lanThu -> MaGeneratorUtil.sinhMaPhieuTiepNhan(
                        phieu.getMaNhanVien(),
                        phieu.getBienSo(),
                        now.plusSeconds(lanThu)),
                ma -> {
                    phieu.setMaPhieuTiepNhan(ma);
                    if (!this.phieuTiepNhanDAO.insert(scope, phieu)) {
                        throw new SQLException(ERROR_GHI_DU_LIEU);
                    }
                });

        return phieu;
    }

    public void update(PhieuTiepNhan phieu) throws SQLException {
        String ma = phieu == null ? null : phieu.getMaPhieuTiepNhan();
        if (ma == null || ma.isBlank()) {
            throw new IllegalArgumentException(ERROR_PHIEU_NOT_FOUND);
        }

        PhieuTiepNhan hienTai = this.phieuTiepNhanDAO.findById(ma)
                .orElseThrow(() -> new IllegalArgumentException(ERROR_PHIEU_NOT_FOUND));

        this.kiemTraChuyenTrangThai(hienTai.getTrangThai(), phieu.getTrangThai());
        if (phieu.getTrangThai() == TrangThaiPhieuTiepNhan.DA_HUY) {
            throw new IllegalArgumentException("app.phieuTiepNhan.huyBangNut");
        }

        String moTa = phieu.getMoTa() == null ? "" : phieu.getMoTa().trim();
        if (moTa.isBlank()) {
            throw new IllegalArgumentException(ERROR_MO_TA_TRONG);
        }
        phieu.setMoTa(moTa);

        if (!this.phieuTiepNhanDAO.update(phieu, hienTai.getTrangThai())) {
            throw new IllegalStateException(ERROR_TRANG_THAI_KHONG_HOP_LE);
        }
    }

    public void huyPhieu(String maPhieuTiepNhan, String lyDoHuy) throws SQLException {
        PhieuTiepNhan phieu = this.getById(maPhieuTiepNhan);
        if (phieu.getTrangThai() == TrangThaiPhieuTiepNhan.DA_HUY) {
            throw new IllegalArgumentException(ERROR_TRANG_THAI_CUOI);
        }

        Optional<HoaDon> hoaDon = this.hoaDonService.timTheoPhieuTiepNhan(phieu.getMaPhieuTiepNhan());
        if (hoaDon.isPresent() && hoaDon.get().getTrangThaiHoaDon() == TrangThaiHoaDon.DA_THANH_TOAN) {
            throw new IllegalArgumentException("app.phieuTiepNhan.loiHoaDonDaThanhToan");
        }
        boolean huyHoaDon = hoaDon.isPresent()
                && hoaDon.get().getTrangThaiHoaDon() == TrangThaiHoaDon.CHO_THANH_TOAN;
        if (huyHoaDon && this.isBlank(lyDoHuy)) {
            throw new IllegalArgumentException("app.hoaDon.loiLyDoHuy");
        }

        TrangThaiPhieuTiepNhan trangThaiCu = phieu.getTrangThai();
        try (TransactionScope scope = TransactionScope.begin()) {
            if (huyHoaDon) {
                this.hoaDonService.huy(scope, hoaDon.get(), lyDoHuy);
            }
            phieu.setTrangThai(TrangThaiPhieuTiepNhan.DA_HUY);
            if (!this.phieuTiepNhanDAO.update(scope, phieu, trangThaiCu)) {
                throw new IllegalStateException(ERROR_TRANG_THAI_KHONG_HOP_LE);
            }
            scope.commit();
        }
    }

    public PhieuTiepNhan getById(String maPhieuTiepNhan) throws SQLException {
        String ma = maPhieuTiepNhan == null ? "" : maPhieuTiepNhan.trim();
        if (ma.isEmpty()) {
            throw new IllegalArgumentException(ERROR_PHIEU_NOT_FOUND);
        }
        return this.phieuTiepNhanDAO.findById(ma)
                .orElseThrow(() -> new IllegalArgumentException(ERROR_PHIEU_NOT_FOUND));
    }

    public PaginatedResult<PhieuTiepNhanView> search(DanhSachPhieuTiepNhanCriteria criteria) throws SQLException {
        DanhSachPhieuTiepNhanCriteria filter = criteria == null
                ? DanhSachPhieuTiepNhanCriteria.macDinh()
                : criteria;

        Map<String, String> tenKhachHangTheoMa = new HashMap<>();
        Map<String, String> sdtTheoMa = new HashMap<>();
        for (KhachHang khachHang : this.khachHangDAO.findAll()) {
            String maKhachHang = khachHang.getMaKhachHang();
            tenKhachHangTheoMa.putIfAbsent(maKhachHang, khachHang.getHoTen() == null ? "" : khachHang.getHoTen());
            sdtTheoMa.putIfAbsent(maKhachHang, khachHang.getSdt() == null ? "" : khachHang.getSdt());
        }

        List<PhieuTiepNhanView> views = new ArrayList<>();
        for (PhieuTiepNhan phieu : this.phieuTiepNhanDAO.findAll()) {
            String tenKhachHang = tenKhachHangTheoMa.get(phieu.getMaKhachHang());
            if (tenKhachHang == null) {
                tenKhachHang = "";
            }
            String sdt = sdtTheoMa.get(phieu.getMaKhachHang());
            if (sdt == null) {
                sdt = "";
            }
            if (this.matches(phieu, tenKhachHang, sdt, filter)) {
                views.add(new PhieuTiepNhanView(
                        phieu.getMaPhieuTiepNhan(),
                        phieu.getBienSo(),
                        tenKhachHang,
                        sdt,
                        phieu.getMoTa(),
                        phieu.getTrangThai(),
                        phieu.getNgayTao()));
            }
        }

        views.sort(Comparator.comparing(
                PhieuTiepNhanView::ngayTao,
                Comparator.nullsLast(Comparator.reverseOrder())));

        return PaginatedResult.of(views, filter.page(), filter.pageSize());
    }

    public List<PhieuTiepNhan> getDanhSachTheoKhachHang(String maKhachHang) throws SQLException {
        return this.phieuTiepNhanDAO.findAll().stream()
                .filter(p -> maKhachHang != null && maKhachHang.equals(p.getMaKhachHang()))
                .filter(p -> p.getTrangThai() != TrangThaiPhieuTiepNhan.DA_HUY)
                .sorted(Comparator.comparing(
                        PhieuTiepNhan::getNgayTao,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .toList();
    }

    private void validate(TransactionScope scope, PhieuTiepNhan phieu) throws SQLException {
        if (phieu == null) {
            throw new IllegalArgumentException(ERROR_DU_LIEU);
        }
        if (phieu.getMaNhanVien() == null || phieu.getMaNhanVien().isBlank()) {
            throw new IllegalArgumentException(ERROR_NHAN_VIEN);
        }
        if (phieu.getMaKhachHang() == null || phieu.getMaKhachHang().isBlank()) {
            throw new IllegalArgumentException(ERROR_KHACH_HANG);
        }
        if (phieu.getBienSo() == null || phieu.getBienSo().isBlank()) {
            throw new IllegalArgumentException(ERROR_BIEN_SO);
        }
        if (this.khachHangDAO.findById(scope, phieu.getMaKhachHang()).isEmpty()) {
            throw new IllegalArgumentException(ERROR_KHACH_HANG_NOT_FOUND);
        }
        if (this.xeDAO.findById(scope, phieu.getBienSo()).isEmpty()) {
            throw new IllegalArgumentException(ERROR_BIEN_SO_NOT_FOUND);
        }
    }

    private void kiemTraChuyenTrangThai(TrangThaiPhieuTiepNhan hienTai, TrangThaiPhieuTiepNhan moi) {
        if (moi == null) {
            throw new IllegalArgumentException(ERROR_DU_LIEU);
        }
        if (moi == hienTai) {
            return;
        }
        if (hienTai == TrangThaiPhieuTiepNhan.DA_HUY) {
            throw new IllegalArgumentException(ERROR_TRANG_THAI_CUOI);
        }
        if (moi == TrangThaiPhieuTiepNhan.DA_HUY) {
            return;
        }
        if (hienTai == null || moi.ordinal() != hienTai.ordinal() + 1) {
            throw new IllegalArgumentException(ERROR_TRANG_THAI_KHONG_HOP_LE);
        }
    }

    private boolean matches(PhieuTiepNhan phieu, String tenKhachHang, String sdt, DanhSachPhieuTiepNhanCriteria filter) {
        if (!this.containsText(filter.maPhieuTiepNhan(), phieu.getMaPhieuTiepNhan())) {
            return false;
        }
        if (!this.containsText(filter.bienSo(), phieu.getBienSo())) {
            return false;
        }
        if (!this.containsText(filter.tenKhachHang(), tenKhachHang)) {
            return false;
        }
        if (!this.containsText(filter.sdt(), sdt)) {
            return false;
        }
        if (!this.containsText(filter.moTa(), phieu.getMoTa())) {
            return false;
        }
        if (filter.trangThai() != null && filter.trangThai() != phieu.getTrangThai()) {
            return false;
        }
        if (this.isEarlierThan(phieu.getNgayTao(), filter.ngayTaoTu())) {
            return false;
        }
        if (this.isLaterThan(phieu.getNgayTao(), filter.ngayTaoDen())) {
            return false;
        }
        if (!this.isBlank(filter.timNhanh())) {
            String combined = phieu.getMaPhieuTiepNhan() + " " + phieu.getBienSo() + " "
                    + tenKhachHang + " " + sdt + " " + phieu.getMoTa();
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

    public List<PhieuTiepNhan> findAll() throws SQLException {
        return this.phieuTiepNhanDAO.findAll();
    }
}
