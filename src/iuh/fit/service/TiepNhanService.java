package iuh.fit.service;

import iuh.fit.dao.KhachHangXeDAO;
import iuh.fit.dao.TransactionScope;
import iuh.fit.dto.TiepNhanFormData;
import iuh.fit.entity.KhachHang;
import iuh.fit.entity.KhachHangXe;
import iuh.fit.entity.PhieuTiepNhan;
import iuh.fit.entity.Xe;
import iuh.fit.enumeration.TrangThaiPhieuTiepNhan;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Optional;

public class TiepNhanService {

    private static final String ERROR_DU_LIEU = "app.tiepNhan.loiDuLieu";
    private static final String ERROR_HO_TEN_TRONG = "app.tiepNhan.loiHoTenTrong";
    private static final String ERROR_ODO_GIAM = "app.tiepNhan.loiOdoGiam";
    private static final String ERROR_LIEN_KET = "app.tiepNhan.loiLienKet";
    private static final String ERROR_DOI_XE = "app.xe.loiDoiXe";

    private final KhachHangService khachHangService = new KhachHangService();
    private final XeService xeService = new XeService();
    private final PhieuTiepNhanService phieuTiepNhanService = new PhieuTiepNhanService();
    private final KhachHangXeDAO khachHangXeDAO = new KhachHangXeDAO();

    public PhieuTiepNhan tiepNhanKhachHang(TiepNhanFormData data) throws SQLException {
        this.validate(data);

        try (TransactionScope scope = TransactionScope.begin()) {
            Optional<KhachHang> khachHangHienCo = this.khachHangService.getBySdt(scope, data.sdt());
            KhachHang khachHang;
            if (khachHangHienCo.isPresent()) {
                khachHang = khachHangHienCo.get();
            } else {
                if (isBlank(data.hoTen())) {
                    throw new IllegalArgumentException(ERROR_HO_TEN_TRONG);
                }
                khachHang = new KhachHang();
                khachHang.setSdt(data.sdt().trim());
                khachHang.setHoTen(data.hoTen().trim());
                this.khachHangService.create(scope, khachHang);
            }

            Optional<Xe> xeHienCo = this.xeService.getByBienSo(scope, data.bienSo());
            Xe xe;
            if (xeHienCo.isPresent()) {
                xe = xeHienCo.get();
                if (data.odo() != null) {
                    if (data.odo() < xe.getOdo()) {
                        throw new IllegalArgumentException(ERROR_ODO_GIAM);
                    }
                    xe.setOdo(data.odo());
                    this.xeService.update(scope, xe);
                }
            } else {
                if (data.doiXe() == null) {
                    throw new IllegalArgumentException(ERROR_DOI_XE);
                }
                xe = new Xe();
                xe.setBienSo(data.bienSo());
                xe.setHangXe(data.hangXe());
                xe.setDongXe(data.dongXe());
                xe.setDoiXe(data.doiXe());
                xe.setOdo(data.odo() == null ? 0 : data.odo());
                this.xeService.create(scope, xe);
            }

            String maKhachHang = khachHang.getMaKhachHang();
            String bienSo = xe.getBienSo();
            if (!this.khachHangXeDAO.exists(scope, maKhachHang, bienSo)
                    && !this.khachHangXeDAO.insert(scope,
                            new KhachHangXe(maKhachHang, bienSo, LocalDateTime.now()))) {
                throw new IllegalStateException(ERROR_LIEN_KET);
            }

            PhieuTiepNhan phieu = new PhieuTiepNhan();
            phieu.setBienSo(bienSo);
            phieu.setMaKhachHang(maKhachHang);
            phieu.setMoTa(data.moTa());
            phieu.setTrangThai(TrangThaiPhieuTiepNhan.TIEP_NHAN);
            this.phieuTiepNhanService.create(scope, phieu);

            scope.commit();
            return phieu;
        }
    }

    private void validate(TiepNhanFormData data) {
        if (data == null) {
            throw new IllegalArgumentException(ERROR_DU_LIEU);
        }

        if (isBlank(data.sdt())) {
            throw new IllegalArgumentException("app.khachHang.loiSdt");
        }

        if (isBlank(data.bienSo())) {
            throw new IllegalArgumentException("app.xe.loiBienSo");
        }
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
