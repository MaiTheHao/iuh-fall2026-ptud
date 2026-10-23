package iuh.fit.service;

import iuh.fit.dao.PhieuTiepNhanDAO;
import iuh.fit.dao.TransactionScope;
import iuh.fit.dao.VanDeKyThuatDAO;
import iuh.fit.entity.PhieuTiepNhan;
import iuh.fit.entity.VanDeKyThuat;
import iuh.fit.enumeration.TrangThaiPhieuTiepNhan;
import iuh.fit.enumeration.TrangThaiVanDeKyThuat;
import iuh.fit.util.MaGeneratorUtil;
import iuh.fit.util.MaRetryUtil;
import iuh.fit.util.TextNormalizerUtil;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

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

    private final VanDeKyThuatDAO vanDeKyThuatDAO = new VanDeKyThuatDAO();
    private final PhieuTiepNhanDAO phieuTiepNhanDAO = new PhieuTiepNhanDAO();

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
}
