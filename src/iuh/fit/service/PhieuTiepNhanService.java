package iuh.fit.service;

import iuh.fit.dao.PhieuTiepNhanDAO;
import iuh.fit.entity.PhieuTiepNhan;
import iuh.fit.enumeration.TrangThaiPhieuTiepNhan;
import iuh.fit.util.MaGeneratorUtil;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class PhieuTiepNhanService {

    private final PhieuTiepNhanDAO phieuTiepNhanDAO = new PhieuTiepNhanDAO();

    public PhieuTiepNhan create(PhieuTiepNhan phieu) throws SQLException {
        this.validate(phieu);

        // Sinh mã phiếu tiếp nhận dạng TN001, TN002,...
        List<String> existingIds = this.phieuTiepNhanDAO.findAll().stream()
                .map(PhieuTiepNhan::getMaPhieuTiepNhan)
                .toList();
        phieu.setMaPhieuTiepNhan(MaGeneratorUtil.sinhMa("TN", 3, existingIds));

        if (phieu.getNgayTao() == null) {
            phieu.setNgayTao(LocalDateTime.now());
        }

        if (phieu.getTrangThai() == null) {
            phieu.setTrangThai(TrangThaiPhieuTiepNhan.TIEP_NHAN);
        }

        this.phieuTiepNhanDAO.insert(phieu);
        return phieu;
    }

    private void validate(PhieuTiepNhan phieu) {
        if (phieu == null) {
            throw new IllegalArgumentException("Dữ liệu phiếu tiếp nhận không được để trống.");
        }
        if (phieu.getMaKhachHang() == null || phieu.getMaKhachHang().isBlank()) {
            throw new IllegalArgumentException("Khách hàng không được để trống.");
        }
        if (phieu.getBienSo() == null || phieu.getBienSo().isBlank()) {
            throw new IllegalArgumentException("Biển số xe không được để trống.");
        }
    }

	public List<PhieuTiepNhan> findAll() throws SQLException {
		return this.phieuTiepNhanDAO.findAll();
	}
}