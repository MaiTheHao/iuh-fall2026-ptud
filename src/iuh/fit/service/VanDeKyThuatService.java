package iuh.fit.service;

import iuh.fit.dao.PhieuTiepNhanDAO;
import iuh.fit.dao.TransactionScope;
import iuh.fit.dao.VanDeKyThuatDAO;
import iuh.fit.dto.ChanDoanDong;
import iuh.fit.entity.PhieuTiepNhan;
import iuh.fit.entity.VanDeKyThuat;
import iuh.fit.enumeration.LoaiMa;
import iuh.fit.enumeration.TrangThaiPhieuTiepNhan;
import iuh.fit.enumeration.TrangThaiVanDeKyThuat;
import iuh.fit.util.TextNormalizerUtil;

import java.sql.SQLException;
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
    private static final String ERROR_CHI_SUA_KHI_MOI = "app.chanDoan.chiSuaKhiMoi";
    private static final String ERROR_GHI_DU_LIEU = "app.chanDoan.loiGhiDuLieu";
    private static final String ERROR_MA_VAN_DE = "app.chanDoan.loiMaVanDe";
    private static final String ERROR_KHONG_TON_TAI = "app.chanDoan.khongTonTai";
    private static final String ERROR_TRANG_THAI = "app.chanDoan.loiTrangThai";
    private static final String ERROR_KHONG_THE_HUY_XAC_NHAN = "app.chanDoan.khongTheHuyXacNhan";

    private final VanDeKyThuatDAO vanDeKyThuatDAO = new VanDeKyThuatDAO();
    private final PhieuTiepNhanDAO phieuTiepNhanDAO = new PhieuTiepNhanDAO();
    private final MaService maService = new MaService();

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

    public void capNhatTrangThaiDongY(String maVanDe, TrangThaiVanDeKyThuat trangThai) throws SQLException {
        if (this.isBlank(maVanDe)) {
            throw new IllegalArgumentException(ERROR_MA_VAN_DE);
        }
        if (trangThai == null) {
            throw new IllegalArgumentException(ERROR_TRANG_THAI);
        }

        String ma = maVanDe.trim();
        VanDeKyThuat vanDe = this.vanDeKyThuatDAO.findById(ma)
                .orElseThrow(() -> new IllegalArgumentException(ERROR_KHONG_TON_TAI));

        if (vanDe.getKhachHangDongY() == trangThai) {
            return;
        }

        if (vanDe.getKhachHangDongY() != null
                && vanDe.getKhachHangDongY() != TrangThaiVanDeKyThuat.CHUA_XAC_NHAN
                && trangThai == TrangThaiVanDeKyThuat.CHUA_XAC_NHAN) {
            throw new IllegalStateException(ERROR_KHONG_THE_HUY_XAC_NHAN);
        }

        try (TransactionScope scope = TransactionScope.begin()) {
            if (!this.vanDeKyThuatDAO.updateTrangThaiDongY(scope, ma, trangThai)) {
                throw new IllegalArgumentException(ERROR_KHONG_TON_TAI);
            }
            scope.commit();
        }
    }

    public void luuChanDoan(String maPhieuTiepNhan, List<ChanDoanDong> danhSach) throws SQLException {
        if (this.isBlank(maPhieuTiepNhan) || danhSach == null) {
            throw new IllegalArgumentException(ERROR_DU_LIEU);
        }

        String maPhieu = maPhieuTiepNhan.trim();

        try (TransactionScope scope = TransactionScope.begin()) {
            PhieuTiepNhan phieu = this.phieuTiepNhanDAO.findByIdForUpdate(scope, maPhieu)
                    .orElseThrow(() -> new IllegalArgumentException(ERROR_PHIEU_KHONG_TON_TAI));
            if (phieu.getTrangThai() != TrangThaiPhieuTiepNhan.TIEP_NHAN) {
                throw new IllegalStateException(ERROR_CHI_SUA_KHI_MOI);
            }

            Map<String, TrangThaiVanDeKyThuat> dongYDaCo = new HashMap<>();
            for (VanDeKyThuat daCo : this.vanDeKyThuatDAO.findByPhieuTiepNhan(scope, maPhieu)) {
                if (!this.isBlank(daCo.getMaVanDe())) {
                    dongYDaCo.put(daCo.getMaVanDe().trim(), daCo.getKhachHangDongY());
                }
            }

            List<String> canXoa = new ArrayList<>();
            List<VanDeKyThuat> canLuu = new ArrayList<>();

            for (ChanDoanDong dong : danhSach) {
                if (dong == null) {
                    throw new IllegalArgumentException(ERROR_DU_LIEU);
                }
                if (dong.xoa()) {
                    if (this.isBlank(dong.maVanDe())) {
                        throw new IllegalArgumentException(ERROR_MA_VAN_DE);
                    }
                    String maXoa = dong.maVanDe().trim();
                    if (!dongYDaCo.containsKey(maXoa)) {
                        throw new IllegalArgumentException(ERROR_KHONG_TON_TAI);
                    }
                    canXoa.add(maXoa);
                    continue;
                }

                String maVanDeCu = this.isBlank(dong.maVanDe()) ? null : dong.maVanDe().trim();
                VanDeKyThuat vanDe = new VanDeKyThuat();
                vanDe.setMaVanDe(maVanDeCu);
                vanDe.setMaPhieuTiepNhan(maPhieu);
                vanDe.setTieuDe(dong.tieuDe());
                vanDe.setMoTa(dong.moTa());
                vanDe.setMucDoUuTien(dong.mucDoUuTien());
                vanDe.setGiaiPhap(dong.giaiPhap());
                vanDe.setKhachHangDongY(maVanDeCu == null
                        ? TrangThaiVanDeKyThuat.CHUA_XAC_NHAN
                        : this.layDongYDaCo(dongYDaCo, maVanDeCu));
                this.chuanHoaDuLieu(vanDe);
                canLuu.add(vanDe);
            }

            for (String maVanDe : canXoa) {
                if (!this.vanDeKyThuatDAO.delete(scope, maVanDe)) {
                    throw new IllegalArgumentException(ERROR_KHONG_TON_TAI);
                }
            }

            LocalDateTime now = LocalDateTime.now();
            for (VanDeKyThuat vanDe : canLuu) {
                if (vanDe.getMaVanDe() == null) {
                    vanDe.setMaVanDe(this.maService.genMa(scope, LoaiMa.VD));
                    vanDe.setNgayTao(now);
                    if (!this.vanDeKyThuatDAO.insert(scope, vanDe)) {
                        throw new SQLException(ERROR_GHI_DU_LIEU);
                    }
                } else if (!this.vanDeKyThuatDAO.update(scope, vanDe)) {
                    throw new IllegalArgumentException(ERROR_KHONG_TON_TAI);
                }
            }

            scope.commit();
        }
    }

    private static TrangThaiVanDeKyThuat layDongYDaCo(
            Map<String, TrangThaiVanDeKyThuat> dongYDaCo, String maVanDe) {
        if (!dongYDaCo.containsKey(maVanDe)) {
            throw new IllegalArgumentException(ERROR_KHONG_TON_TAI);
        }

        TrangThaiVanDeKyThuat dongY = dongYDaCo.get(maVanDe);
        return dongY == null ? TrangThaiVanDeKyThuat.CHUA_XAC_NHAN : dongY;
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

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
