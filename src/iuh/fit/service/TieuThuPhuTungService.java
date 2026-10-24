package iuh.fit.service;

import iuh.fit.dao.TieuThuPhuTungDAO;
import iuh.fit.dto.BaoCaoTieuThuPhuTungDTO;
import iuh.fit.dto.TieuThuPhuTungBaoCaoDTO;
import iuh.fit.dto.TieuThuPhuTungDTO;

import java.sql.SQLException;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.List;

public class TieuThuPhuTungService {

    private final TieuThuPhuTungDAO tieuThuPhuTungDAO;

    public TieuThuPhuTungService() {
        this.tieuThuPhuTungDAO = new TieuThuPhuTungDAO();
    }

    public BaoCaoTieuThuPhuTungDTO taoBaoCao(YearMonth thang) throws SQLException {
        if (thang == null) {
            throw new IllegalArgumentException("app.report.parts.invalidMonth");
        }

        List<TieuThuPhuTungDTO> duLieu = this.tieuThuPhuTungDAO.findAll(thang);

        int tongNhap = 0;
        int tongXuat = 0;
        int tongTonKho = 0;
        double tongDoanhThu = 0;

        for (TieuThuPhuTungDTO phuTung : duLieu) {
            tongNhap += phuTung.soLuongNhap();
            tongXuat += phuTung.soLuongXuat();
            tongTonKho += phuTung.soLuongTon();
            tongDoanhThu += phuTung.doanhThu();
        }

        List<TieuThuPhuTungBaoCaoDTO> danhSach = duLieu.stream()
                .map(phuTung -> new TieuThuPhuTungBaoCaoDTO(
                        phuTung.maPhuTung(),
                        phuTung.tenPhuTung(),
                        phuTung.soLuongNhap(),
                        phuTung.soLuongXuat(),
                        phuTung.soLuongTon(),
                        phuTung.donGiaBan(),
                        phuTung.doanhThu(),
                        phuTung.soLuongNhap() == 0
                                ? 0
                                : phuTung.soLuongXuat() * 100.0 / phuTung.soLuongNhap()))
                .sorted(Comparator.comparingInt(TieuThuPhuTungBaoCaoDTO::soLuongXuat)
                        .reversed()
                        .thenComparing(
                                Comparator.comparingDouble(TieuThuPhuTungBaoCaoDTO::doanhThu).reversed()))
                .limit(20)
                .toList();

        double tyLeTieuThu = tongNhap == 0 ? 0 : tongXuat * 100.0 / tongNhap;

        return new BaoCaoTieuThuPhuTungDTO(
                tongNhap,
                tongXuat,
                tongTonKho,
                tyLeTieuThu,
                tongDoanhThu,
                danhSach);
    }
}