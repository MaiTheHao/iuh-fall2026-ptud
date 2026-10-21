package iuh.fit.service;

import iuh.fit.dao.PhieuTiepNhanDAO;
import iuh.fit.dao.TransactionScope;
import iuh.fit.dao.VanDeKyThuatDAO;
import iuh.fit.entity.VanDeKyThuat;
import iuh.fit.enumeration.TrangThaiVanDeKyThuat;
import iuh.fit.util.MaGeneratorUtil;
import iuh.fit.util.MaRetryUtil;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class VanDeKyThuatService {

    private final VanDeKyThuatDAO vanDeKyThuatDAO = new VanDeKyThuatDAO();
    private final PhieuTiepNhanDAO phieuTiepNhanDAO = new PhieuTiepNhanDAO();

    public void lapChanDoan(VanDeKyThuat vanDe) throws SQLException {
        if (vanDe == null) {
            throw new IllegalArgumentException("Dữ liệu chẩn đoán không được rỗng.");
        }
        if (vanDe.getMaPhieuTiepNhan() == null || vanDe.getMaPhieuTiepNhan().isBlank()) {
            throw new IllegalArgumentException("Mã phiếu tiếp nhận không được để trống.");
        }
        if (vanDe.getTieuDe() == null || vanDe.getTieuDe().isBlank()) {
            throw new IllegalArgumentException("Tiêu đề chẩn đoán không được để trống.");
        }
        if (vanDe.getMucDoUuTien() < 1 || vanDe.getMucDoUuTien() > 100) {
            throw new IllegalArgumentException("Mức độ ưu tiên phải nằm trong khoảng từ 1 đến 100.");
        }

        if (!phieuTiepNhanDAO.findById(vanDe.getMaPhieuTiepNhan()).isPresent()) {
            throw new IllegalArgumentException("Phiếu tiếp nhận không tồn tại.");
        }

        LocalDateTime thoiDiem = LocalDateTime.now();
        vanDe.setNgayTao(thoiDiem);
        vanDe.setKhachHangDongY(TrangThaiVanDeKyThuat.CHUA_XAC_NHAN);

        try (TransactionScope scope = TransactionScope.begin()) {
            MaRetryUtil.thucHien(
                lanThu -> MaGeneratorUtil.sinhMa("VD", thoiDiem),
                maGenerated -> {
                    vanDe.setMaVanDe(maGenerated);
                    vanDeKyThuatDAO.insert(scope, vanDe);
                }
            );
            scope.commit();
        }
    }

    public List<VanDeKyThuat> layDanhSachTheoPhieu(String maPhieuTiepNhan) throws SQLException {
        if (maPhieuTiepNhan == null || maPhieuTiepNhan.isBlank()) {
            return List.of();
        }
        return vanDeKyThuatDAO.findByPhieuTiepNhan(maPhieuTiepNhan);
    }
}