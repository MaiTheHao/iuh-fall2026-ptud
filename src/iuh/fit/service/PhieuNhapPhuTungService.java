package iuh.fit.service;

import iuh.fit.dao.PhieuNhapPhuTungDAO;
import iuh.fit.dto.DanhSachCriteria;
import iuh.fit.dto.PaginatedResult;
import iuh.fit.entity.ChiTietPhieuNhapPhuTung;
import iuh.fit.entity.NhanVien;
import iuh.fit.entity.PhieuNhapPhuTung;
import iuh.fit.enumeration.VaiTro;
import iuh.fit.util.MaGeneratorUtil;
import iuh.fit.util.TextNormalizerUtil;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class PhieuNhapPhuTungService {

    private final PhieuNhapPhuTungDAO phieuNhapDAO = new PhieuNhapPhuTungDAO();
    private final PhuTungService phuTungService = new PhuTungService();
    private final NhanVienService nhanVienService = new NhanVienService();

    private static final String ERROR_DATA = "app.phieuNhap.loiDongNhapLieu";
    private static final String ERROR_EMPLOYEE_REQUIRED = "app.phieuNhap.thuKho";
    private static final String ERROR_EMPLOYEE_ROLE = "app.phieuNhap.loiVaiTroThuKho";
    private static final String ERROR_DESCRIPTION = "app.phieuNhap.loiMoTa";
    private static final String ERROR_DETAILS_EMPTY = "app.phieuNhap.loiTrongChiTiet";
    private static final String ERROR_PART_REQUIRED = "app.phieuNhap.loiPhuTung";
    private static final String ERROR_QUANTITY = "app.phieuNhap.loiSoLuong";
    private static final String ERROR_PRICE = "app.phieuNhap.loiDonGia";
    private static final String ERROR_ID_REQUIRED = "app.phieuNhap.chuaChonXoa";
    private static final String ERROR_NOT_FOUND = "app.phieuNhap.khongTimThay";

    public PaginatedResult<PhieuNhapPhuTung> getAll(int page, int pageSize) throws SQLException {
        return PaginatedResult.of(this.phieuNhapDAO.findAll(), page, pageSize);
    }

    public PaginatedResult<PhieuNhapPhuTung> search(DanhSachCriteria criteria, String tuKhoa) throws SQLException {
        List<PhieuNhapPhuTung> all = this.phieuNhapDAO.findAll();
        String keyword = tuKhoa != null ? TextNormalizerUtil.normalize(tuKhoa.trim()) : "";

        List<PhieuNhapPhuTung> matched = all.stream().filter(item -> {
            if (keyword.isBlank()) {
                return true;
            }
            String maPhieu = TextNormalizerUtil.normalize(item.getMaPhieuNhap());
            String moTa = item.getMoTa() != null ? TextNormalizerUtil.normalize(item.getMoTa()) : "";
            String maNV = item.getMaNhanVien() != null ? TextNormalizerUtil.normalize(item.getMaNhanVien()) : "";
            return maPhieu.contains(keyword) || moTa.contains(keyword) || maNV.contains(keyword);
        }).toList();

        int page = criteria != null ? criteria.page() : 1;
        int pageSize = criteria != null ? criteria.pageSize() : 10;
        return PaginatedResult.of(matched, page, pageSize);
    }

    public Optional<PhieuNhapPhuTung> getById(String maPhieuNhap) throws SQLException {
        return (maPhieuNhap == null || maPhieuNhap.isBlank()) 
                ? Optional.empty() 
                : this.phieuNhapDAO.findById(maPhieuNhap.trim());
    }

    public boolean create(PhieuNhapPhuTung phieu, List<ChiTietPhieuNhapPhuTung> chiTiets, boolean hoanTat) throws SQLException {
        this.validate(phieu, chiTiets);

        String datePrefix = "PN" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        List<String> existingIds = this.phieuNhapDAO.findAll().stream().map(PhieuNhapPhuTung::getMaPhieuNhap).toList();
        String maPhieuMoi = MaGeneratorUtil.sinhMa(datePrefix, 4, existingIds);

        phieu.setMaPhieuNhap(maPhieuMoi);
        phieu.setNgayTao(LocalDateTime.now());
        phieu.setMoTa(phieu.getMoTa().trim());

        chiTiets.forEach(ct -> ct.setMaPhieuNhap(maPhieuMoi));

        boolean success = this.phieuNhapDAO.insert(phieu, chiTiets);
        if (success && hoanTat) {
            for (ChiTietPhieuNhapPhuTung ct : chiTiets) {
                this.phuTungService.increaseStock(ct.getMaPhuTung(), ct.getSoLuong());
            }
        }
        return success;
    }

    public boolean delete(String maPhieuNhap) throws SQLException {
        if (maPhieuNhap == null || maPhieuNhap.isBlank()) {
            throw new IllegalArgumentException(ERROR_ID_REQUIRED);
        }

        String ma = maPhieuNhap.trim();
        if (!this.phieuNhapDAO.existsById(ma)) {
            throw new IllegalArgumentException(ERROR_NOT_FOUND);
        }
        return this.phieuNhapDAO.delete(ma);
    }

    private void validate(PhieuNhapPhuTung phieu, List<ChiTietPhieuNhapPhuTung> chiTiets) throws SQLException {
        if (phieu == null) {
            throw new IllegalArgumentException(ERROR_DATA);
        }
        if (phieu.getMaNhanVien() == null || phieu.getMaNhanVien().isBlank()) {
            throw new IllegalArgumentException(ERROR_EMPLOYEE_REQUIRED);
        }

        NhanVien nv = this.nhanVienService.getById(phieu.getMaNhanVien().trim());
        if (nv == null || nv.getVaiTro() != VaiTro.NHAN_VIEN_QUAN_LY) {
            throw new IllegalArgumentException(ERROR_EMPLOYEE_ROLE);
        }

        if (phieu.getMoTa() == null || phieu.getMoTa().isBlank()) {
            throw new IllegalArgumentException(ERROR_DESCRIPTION);
        }
        if (chiTiets == null || chiTiets.isEmpty()) {
            throw new IllegalArgumentException(ERROR_DETAILS_EMPTY);
        }

        for (ChiTietPhieuNhapPhuTung ct : chiTiets) {
            if (ct.getMaPhuTung() == null || ct.getMaPhuTung().isBlank()) {
                throw new IllegalArgumentException(ERROR_PART_REQUIRED);
            }
            if (ct.getSoLuong() <= 0) {
                throw new IllegalArgumentException(ERROR_QUANTITY);
            }
            if (ct.getDonGiaNhap() == null || ct.getDonGiaNhap() < 0) {
                throw new IllegalArgumentException(ERROR_PRICE);
            }
        }
    }
}