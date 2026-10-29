package iuh.fit.service;

import iuh.fit.dao.KhachHangDAO;
import iuh.fit.dao.PhieuTiepNhanDAO;
import iuh.fit.dao.TransactionScope;
import iuh.fit.dao.VanDeKyThuatDAO;
import iuh.fit.dto.ChanDoanUpdateFormData;
import iuh.fit.dto.ChanDoanView;
import iuh.fit.dto.DanhSachChanDoanCriteria;
import iuh.fit.dto.PaginatedResult;
import iuh.fit.entity.KhachHang;
import iuh.fit.entity.PhieuTiepNhan;
import iuh.fit.entity.VanDeKyThuat;
import iuh.fit.enumeration.TrangThaiPhieuTiepNhan;
import iuh.fit.enumeration.TrangThaiVanDeKyThuat;
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

public class VanDeKyThuatService {

    private static final int DO_DAI_TIEU_DE = 200;
    private static final int DO_DAI_MO_TA = 1000;
    private static final int DO_DAI_GIAI_PHAP = 1000;
    private static final int MUC_DO_UU_TIEN_MIN = 1;
    private static final int MUC_DO_UU_TIEN_MAX = 100;

    private static final String ERROR_DU_LIEU = "app.chanDoan.loiDuLieu";
    private static final String ERROR_MA_PHIEU = "app.chanDoan.loiMaPhieu";
    private static final String ERROR_TIEU_DE = "app.chanDoan.loiTieuDe";
    private static final String ERROR_TIEU_DE_DAI = "app.chanDoan.loiTieuDeDai";
    private static final String ERROR_MO_TA_DAI = "app.chanDoan.loiMoTaDai";
    private static final String ERROR_GIAI_PHAP_DAI = "app.chanDoan.loiGiaiPhapDai";
    private static final String ERROR_MUC_DO_UU_TIEN = "app.chanDoan.loiMucDoUuTien";
    private static final String ERROR_PHIEU_KHONG_TON_TAI = "app.chanDoan.loiPhieuKhongTonTai";
    private static final String ERROR_PHIEU_DA_HUY = "app.chanDoan.loiPhieuDaHuy";
    private static final String ERROR_GHI_DU_LIEU = "app.chanDoan.loiGhiDuLieu";
    private static final String ERROR_MA_VAN_DE = "app.chanDoan.loiMaVanDe";
    private static final String ERROR_KHONG_TON_TAI = "app.chanDoan.khongTonTai";
    private static final String ERROR_TRANG_THAI = "app.chanDoan.loiTrangThai";
    private static final String ERROR_KHOANG_MUC_DO = "app.chanDoan.loiKhoangMucDo";

    private final VanDeKyThuatDAO vanDeKyThuatDAO = new VanDeKyThuatDAO();
    private final PhieuTiepNhanDAO phieuTiepNhanDAO = new PhieuTiepNhanDAO();
    private final KhachHangDAO khachHangDAO = new KhachHangDAO();

    public void lapChanDoan(VanDeKyThuat vanDe) throws SQLException {
        if (vanDe == null) {
            throw new IllegalArgumentException(ERROR_DU_LIEU);
        }

        this.chuanHoaDuLieu(vanDe);

        String maPhieu = vanDe.getMaPhieuTiepNhan();

        vanDe.setKhachHangDongY(TrangThaiVanDeKyThuat.CHUA_XAC_NHAN);

        LocalDateTime now = LocalDateTime.now();

        MaRetryUtil.thucHien(
                lanThu -> {
                    LocalDateTime thoiDiem = now.plusSeconds(lanThu);
                    vanDe.setNgayTao(thoiDiem);
                    return MaGeneratorUtil.sinhMa("VD", thoiDiem);
                },
                ma -> {
                    vanDe.setMaVanDe(ma);
                    try (TransactionScope scope = TransactionScope.begin()) {
                        PhieuTiepNhan phieu = this.phieuTiepNhanDAO.findById(scope, maPhieu)
                                .orElseThrow(() -> new IllegalArgumentException(ERROR_PHIEU_KHONG_TON_TAI));
                        if (phieu.getTrangThai() == TrangThaiPhieuTiepNhan.DA_HUY) {
                            throw new IllegalArgumentException(ERROR_PHIEU_DA_HUY);
                        }
                        if (!this.vanDeKyThuatDAO.insert(scope, vanDe)) {
                            throw new SQLException(ERROR_GHI_DU_LIEU);
                        }
                        scope.commit();
                    }
                });
    }

    private void chuanHoaDuLieu(VanDeKyThuat vanDe) {
        String maPhieu = vanDe.getMaPhieuTiepNhan() == null ? "" : vanDe.getMaPhieuTiepNhan().trim();
        if (maPhieu.isBlank()) {
            throw new IllegalArgumentException(ERROR_MA_PHIEU);
        }
        vanDe.setMaPhieuTiepNhan(maPhieu);

        String tieuDe = vanDe.getTieuDe() == null ? "" : vanDe.getTieuDe().trim();
        if (tieuDe.isBlank()) {
            throw new IllegalArgumentException(ERROR_TIEU_DE);
        }
        if (tieuDe.length() > DO_DAI_TIEU_DE) {
            throw new IllegalArgumentException(ERROR_TIEU_DE_DAI);
        }
        vanDe.setTieuDe(tieuDe);

        String moTa = vanDe.getMoTa() == null ? "" : vanDe.getMoTa().trim();
        if (moTa.length() > DO_DAI_MO_TA) {
            throw new IllegalArgumentException(ERROR_MO_TA_DAI);
        }
        vanDe.setMoTa(moTa.isBlank() ? null : moTa);

        String giaiPhap = vanDe.getGiaiPhap() == null ? "" : vanDe.getGiaiPhap().trim();
        if (giaiPhap.length() > DO_DAI_GIAI_PHAP) {
            throw new IllegalArgumentException(ERROR_GIAI_PHAP_DAI);
        }
        vanDe.setGiaiPhap(giaiPhap.isBlank() ? null : giaiPhap);

        if (vanDe.getMucDoUuTien() < MUC_DO_UU_TIEN_MIN || vanDe.getMucDoUuTien() > MUC_DO_UU_TIEN_MAX) {
            throw new IllegalArgumentException(ERROR_MUC_DO_UU_TIEN);
        }
    }

    public List<VanDeKyThuat> layDanhSachTheoPhieu(String maPhieuTiepNhan) throws SQLException {
        if (maPhieuTiepNhan == null || maPhieuTiepNhan.isBlank()) {
            return List.of();
        }

        List<VanDeKyThuat> danhSach = this.vanDeKyThuatDAO.findByPhieuTiepNhan(maPhieuTiepNhan.trim());
        Comparator<VanDeKyThuat> thuTu = Comparator.comparingInt(VanDeKyThuat::getMucDoUuTien)
                .thenComparing(VanDeKyThuat::getNgayTao,
                        Comparator.nullsLast(Comparator.<LocalDateTime>reverseOrder()));

        return danhSach.stream().sorted(thuTu).toList();
    }

    public List<VanDeKyThuat> layDanhSachTheoPhieu(String maPhieuTiepNhan, String tuKhoa) throws SQLException {
        List<VanDeKyThuat> danhSach = this.layDanhSachTheoPhieu(maPhieuTiepNhan);

        String khoa = TextNormalizerUtil.normalize(tuKhoa);
        if (khoa.isBlank()) {
            return danhSach;
        }

        return danhSach.stream()
                .filter(vanDe -> this.chuaKhoa(vanDe, khoa))
                .toList();
    }

    private boolean chuaKhoa(VanDeKyThuat vanDe, String khoa) {
        return TextNormalizerUtil.normalize(vanDe.getTieuDe()).contains(khoa)
                || TextNormalizerUtil.normalize(vanDe.getMoTa()).contains(khoa)
                || TextNormalizerUtil.normalize(vanDe.getGiaiPhap()).contains(khoa);
    }

    public PaginatedResult<ChanDoanView> search(DanhSachChanDoanCriteria criteria) throws SQLException {
        DanhSachChanDoanCriteria filter = criteria == null
                ? DanhSachChanDoanCriteria.macDinh()
                : criteria;

        this.kiemTraKhoangMucDo(filter);

        BanDoTraCuu banDo = this.taoBanDoTraCuu();
        List<ChanDoanView> views = new ArrayList<>();
        for (VanDeKyThuat vanDe : this.vanDeKyThuatDAO.findAll()) {
            if (this.matches(vanDe, filter)) {
                views.add(this.taoView(vanDe,
                        banDo.bienSoTheoMaPhieu(),
                        banDo.maKhachHangTheoMaPhieu(),
                        banDo.tenKhachHangTheoMa()));
            }
        }

        Comparator<ChanDoanView> thuTu = Comparator.comparing(
                ChanDoanView::ngayTao,
                Comparator.nullsLast(Comparator.<LocalDateTime>reverseOrder()))
                .thenComparingInt(ChanDoanView::mucDoUuTien)
                .thenComparing(ChanDoanView::maVanDe, Comparator.nullsLast(Comparator.naturalOrder()));

        views.sort(thuTu);

        return PaginatedResult.of(views, filter);
    }

    public ChanDoanView getById(String maVanDe) throws SQLException {
        if (this.isBlank(maVanDe)) {
            throw new IllegalArgumentException(ERROR_MA_VAN_DE);
        }

        VanDeKyThuat vanDe = this.vanDeKyThuatDAO.findById(maVanDe.trim())
                .orElseThrow(() -> new IllegalArgumentException(ERROR_KHONG_TON_TAI));
        BanDoTraCuu banDo = this.taoBanDoTraCuu();

        return this.taoView(vanDe,
                banDo.bienSoTheoMaPhieu(),
                banDo.maKhachHangTheoMaPhieu(),
                banDo.tenKhachHangTheoMa());
    }

    public void update(ChanDoanUpdateFormData data) throws SQLException {
        if (data == null || this.isBlank(data.maVanDe())) {
            throw new IllegalArgumentException(ERROR_MA_VAN_DE);
        }

        VanDeKyThuat vanDe = this.vanDeKyThuatDAO.findById(data.maVanDe().trim())
                .orElseThrow(() -> new IllegalArgumentException(ERROR_KHONG_TON_TAI));

        if (data.khachHangDongY() == null) {
            throw new IllegalArgumentException(ERROR_TRANG_THAI);
        }

        vanDe.setTieuDe(data.tieuDe());
        vanDe.setMoTa(data.moTa());
        vanDe.setMucDoUuTien(data.mucDoUuTien());
        vanDe.setGiaiPhap(data.giaiPhap());
        vanDe.setKhachHangDongY(data.khachHangDongY());

        this.chuanHoaDuLieu(vanDe);

        try (TransactionScope scope = TransactionScope.begin()) {
            if (!this.vanDeKyThuatDAO.update(scope, vanDe)) {
                throw new IllegalArgumentException(ERROR_KHONG_TON_TAI);
            }
            scope.commit();
        }
    }

    public void delete(String maVanDe) throws SQLException {
        if (this.isBlank(maVanDe)) {
            throw new IllegalArgumentException(ERROR_MA_VAN_DE);
        }

        String ma = maVanDe.trim();
        if (this.vanDeKyThuatDAO.findById(ma).isEmpty()) {
            throw new IllegalArgumentException(ERROR_KHONG_TON_TAI);
        }

        try (TransactionScope scope = TransactionScope.begin()) {
            if (!this.vanDeKyThuatDAO.delete(scope, ma)) {
                throw new IllegalArgumentException(ERROR_KHONG_TON_TAI);
            }
            scope.commit();
        }
    }

    private record BanDoTraCuu(
            Map<String, String> bienSoTheoMaPhieu,
            Map<String, String> maKhachHangTheoMaPhieu,
            Map<String, String> tenKhachHangTheoMa) {
    }

    private BanDoTraCuu taoBanDoTraCuu() throws SQLException {
        Map<String, String> bienSoTheoMaPhieu = new HashMap<>();
        Map<String, String> maKhachHangTheoMaPhieu = new HashMap<>();
        for (PhieuTiepNhan phieu : this.phieuTiepNhanDAO.findAll()) {
            String maPhieu = phieu.getMaPhieuTiepNhan();
            if (maPhieu == null) {
                continue;
            }
            bienSoTheoMaPhieu.putIfAbsent(maPhieu, phieu.getBienSo() == null ? "" : phieu.getBienSo());
            maKhachHangTheoMaPhieu.putIfAbsent(maPhieu,
                    phieu.getMaKhachHang() == null ? "" : phieu.getMaKhachHang());
        }

        Map<String, String> tenKhachHangTheoMa = new HashMap<>();
        for (KhachHang khachHang : this.khachHangDAO.findAll()) {
            tenKhachHangTheoMa.putIfAbsent(
                    khachHang.getMaKhachHang(),
                    khachHang.getHoTen() == null ? "" : khachHang.getHoTen());
        }

        return new BanDoTraCuu(bienSoTheoMaPhieu, maKhachHangTheoMaPhieu, tenKhachHangTheoMa);
    }

    private ChanDoanView taoView(VanDeKyThuat vanDe,
            Map<String, String> bienSoTheoMaPhieu,
            Map<String, String> maKhachHangTheoMaPhieu,
            Map<String, String> tenKhachHangTheoMa) {
        String maPhieu = vanDe.getMaPhieuTiepNhan();
        String bienSo = bienSoTheoMaPhieu.getOrDefault(maPhieu == null ? "" : maPhieu, "");
        String maKhachHang = maKhachHangTheoMaPhieu.getOrDefault(maPhieu == null ? "" : maPhieu, "");
        String tenKhachHang = tenKhachHangTheoMa.getOrDefault(maKhachHang, "");

        return new ChanDoanView(
                vanDe.getMaVanDe(),
                maPhieu,
                bienSo,
                tenKhachHang,
                vanDe.getTieuDe(),
                vanDe.getMucDoUuTien(),
                vanDe.getMoTa(),
                vanDe.getGiaiPhap(),
                vanDe.getKhachHangDongY(),
                vanDe.getNgayTao());
    }

    private void kiemTraKhoangMucDo(DanhSachChanDoanCriteria filter) {
        if (filter.mucDoTu() != null
                && filter.mucDoDen() != null
                && filter.mucDoTu() > filter.mucDoDen()) {
            throw new IllegalArgumentException(ERROR_KHOANG_MUC_DO);
        }
    }

    private boolean matches(VanDeKyThuat vanDe, DanhSachChanDoanCriteria filter) {
        if (!this.containsText(filter.maPhieuTiepNhan(), vanDe.getMaPhieuTiepNhan())) {
            return false;
        }
        if (filter.mucDoTu() != null && vanDe.getMucDoUuTien() < filter.mucDoTu()) {
            return false;
        }
        if (filter.mucDoDen() != null && vanDe.getMucDoUuTien() > filter.mucDoDen()) {
            return false;
        }
        if (filter.khachHangDongY() != null && filter.khachHangDongY() != vanDe.getKhachHangDongY()) {
            return false;
        }
        if (this.isEarlierThan(vanDe.getNgayTao(), filter.ngayTaoTu())) {
            return false;
        }
        if (this.isLaterThan(vanDe.getNgayTao(), filter.ngayTaoDen())) {
            return false;
        }
        if (!this.isBlank(filter.timNhanh())) {
            String combined = vanDe.getMaVanDe() + " " + vanDe.getTieuDe();
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
