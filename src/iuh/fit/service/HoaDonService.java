package iuh.fit.service;

import iuh.fit.dao.HoaDonDAO;
import iuh.fit.dao.KhachHangDAO;
import iuh.fit.dao.TransactionScope;
import iuh.fit.dao.VoucherDAO;
import iuh.fit.dto.DanhSachHoaDonCriteria;
import iuh.fit.dto.HoaDonView;
import iuh.fit.dto.PaginatedResult;
import iuh.fit.entity.ChiTietHoaDon;
import iuh.fit.entity.HoaDon;
import iuh.fit.entity.KhachHang;
import iuh.fit.entity.PhieuTiepNhan;
import iuh.fit.entity.PhuTung;
import iuh.fit.entity.Voucher;
import iuh.fit.enumeration.LoaiSanPham;
import iuh.fit.enumeration.TrangThaiHoaDon;
import iuh.fit.util.MaGeneratorUtil;
import iuh.fit.util.MaRetryUtil;
import iuh.fit.util.TextNormalizerUtil;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HoaDonService {

    private static final String ERROR_CHI_TIET_TRONG = "app.hoaDon.loiChiTietTrong";
    private static final String ERROR_SO_LUONG = "app.hoaDon.loiSoLuong";
    private static final String ERROR_DON_GIA = "app.hoaDon.loiDonGia";
    private static final String ERROR_SAN_PHAM = "app.hoaDon.loiSanPham";
    private static final String ERROR_VOUCHER = "app.hoaDon.loiVoucher";
    private static final String ERROR_NOT_FOUND = "app.hoaDon.khongTonTai";
    private static final String ERROR_TRANG_THAI = "app.hoaDon.loiTrangThai";
    private static final String ERROR_THIEU_TON = "app.hoaDon.loiTonKho";
    private static final String ERROR_KHOANG_TIEN = "app.hoaDon.loiKhoangTien";
    private static final String ERROR_KHOANG_NGAY = "app.hoaDon.loiKhoangNgay";
    private static final String ERROR_PHAN_TRANG = "app.hoaDon.loiPhanTrang";
    private static final String ERROR_LY_DO_HUY = "app.hoaDon.loiLyDoHuy";
    private static final String ERROR_LY_DO_HUY_DAI = "app.hoaDon.loiLyDoHuyDai";

    private static final int MAX_LY_DO_HUY = 255;

    private final HoaDonDAO hoaDonDAO = new HoaDonDAO();
    private final KhachHangDAO khachHangDAO = new KhachHangDAO();
    private final PhieuTiepNhanService phieuTiepNhanService = new PhieuTiepNhanService();
    private final PhuTungService phuTungService = new PhuTungService();
    private final VoucherDAO voucherDAO = new VoucherDAO();

    public HoaDon create(String maPhieuTiepNhan, String maVoucher, List<ChiTietHoaDon> chiTiets)
            throws SQLException {
        return this.create(maPhieuTiepNhan, maVoucher, null, chiTiets);
    }

    public HoaDon create(String maPhieuTiepNhan, String maVoucher, String ghiChu,
            List<ChiTietHoaDon> chiTiets) throws SQLException {
        PhieuTiepNhan phieu = this.phieuTiepNhanService.getById(maPhieuTiepNhan);
        HoaDon existing = this.hoaDonDAO.findByPhieuTiepNhan(phieu.getMaPhieuTiepNhan()).orElse(null);
        if (existing != null) {
            return existing;
        }
        this.validateChiTiet(chiTiets);

        Voucher voucher = this.resolveVoucher(maVoucher);
        List<ChiTietHoaDon> danhSachGop = this.gopChiTietTrung(chiTiets);
        this.kiemTraTonKho(danhSachGop, List.of());

        HoaDon hoaDon = new HoaDon();
        hoaDon.setMaPhieuTiepNhan(phieu.getMaPhieuTiepNhan());
        hoaDon.setMaVoucher(voucher == null ? null : voucher.getMaVoucher());
        hoaDon.setGhiChu(this.chuanHoaGhiChu(ghiChu));
        hoaDon.setTrangThaiHoaDon(TrangThaiHoaDon.CHO_THANH_TOAN);
        hoaDon.setMucGiamApDung(voucher == null ? null : this.tinhMucGiam(voucher));

        LocalDateTime now = LocalDateTime.now();
        try {
            MaRetryUtil.thucHien(
                    lan -> MaGeneratorUtil.sinhMa("HD", now.plusSeconds(lan)),
                    ma -> {
                        hoaDon.setMaHoaDon(ma);
                        hoaDon.setNgayTao(now);
                        for (ChiTietHoaDon ct : danhSachGop) {
                            ct.setMaHoaDon(ma);
                        }

                        try (TransactionScope scope = TransactionScope.begin()) {
                            this.hoaDonDAO.insert(scope, hoaDon, danhSachGop);
                            this.capNhatTonKho(scope, danhSachGop, List.of());
                            scope.commit();
                        }
                    });
        } catch (SQLException ex) {
            // Hai may cung lap hoa don cho mot phieu: rang buoc UNIQUE thang truoc, tra ve hoa don da co.
            HoaDon daCo = this.laLoiTrungKhoa(ex)
                    ? this.hoaDonDAO.findByPhieuTiepNhan(phieu.getMaPhieuTiepNhan()).orElse(null)
                    : null;
            if (daCo != null) {
                return daCo;
            }
            throw ex;
        }

        return this.hoaDonDAO.findByPhieuTiepNhan(phieu.getMaPhieuTiepNhan()).orElse(hoaDon);
    }

    private boolean laLoiTrungKhoa(SQLException ex) {
        int errorCode = ex.getErrorCode();
        return errorCode == 2627 || errorCode == 2601;
    }

    public HoaDon capNhat(String maHoaDon, String maVoucher, String ghiChu, List<ChiTietHoaDon> chiTiets)
            throws SQLException {
        if (maHoaDon == null || maHoaDon.isBlank()) {
            throw new IllegalArgumentException(ERROR_NOT_FOUND);
        }
        this.validateChiTiet(chiTiets);

        HoaDon hienTai = this.getById(maHoaDon);
        if (hienTai.getTrangThaiHoaDon() != TrangThaiHoaDon.CHO_THANH_TOAN) {
            throw new IllegalStateException(ERROR_TRANG_THAI);
        }

        Voucher voucher = this.resolveVoucher(maVoucher);
        String maVoucherMoi = voucher == null ? null : voucher.getMaVoucher();
        Double mucGiamMoi = voucher == null ? null : this.tinhMucGiam(voucher);

        List<ChiTietHoaDon> danhSachMoi = this.gopChiTietTrung(chiTiets);
        this.kiemTraTonKho(danhSachMoi, hienTai.getDanhSachChiTietHoaDon());

        try (TransactionScope scope = TransactionScope.begin()) {
            if (!this.hoaDonDAO.updateThongTin(scope, maHoaDon, maVoucherMoi,
                    this.chuanHoaGhiChu(ghiChu), mucGiamMoi)) {
                throw new IllegalStateException(ERROR_TRANG_THAI);
            }
            this.capNhatTonKho(scope, danhSachMoi, hienTai.getDanhSachChiTietHoaDon());
            this.hoaDonDAO.deleteChiTiet(scope, maHoaDon);
            this.hoaDonDAO.insertChiTiet(scope, maHoaDon, danhSachMoi);
            scope.commit();
        }

        return this.getById(maHoaDon);
    }

    public HoaDon xacNhanThanhToan(String maHoaDon) throws SQLException {
        HoaDon hoaDon = this.getById(maHoaDon);
        if (hoaDon.getTrangThaiHoaDon() != TrangThaiHoaDon.CHO_THANH_TOAN) {
            throw new IllegalStateException(ERROR_TRANG_THAI);
        }

        LocalDateTime now = LocalDateTime.now();
        // Chot muc giam tai thoi diem thanh toan de tong tien khong doi sau khi voucher het han.
        Double mucGiamChot = hoaDon.getMucGiamApDung();
        if (mucGiamChot == null && hoaDon.getMaVoucher() != null && !hoaDon.getMaVoucher().isBlank()) {
            mucGiamChot = this.tinhMucGiam(hoaDon);
        }

        try (TransactionScope scope = TransactionScope.begin()) {
            if (!this.hoaDonDAO.capNhatThanhToan(scope, maHoaDon, now, mucGiamChot)) {
                throw new IllegalStateException(ERROR_TRANG_THAI);
            }
            scope.commit();
        }

        hoaDon.setTrangThaiHoaDon(TrangThaiHoaDon.DA_THANH_TOAN);
        hoaDon.setThoiGianThanhToan(now);
        hoaDon.setMucGiamApDung(mucGiamChot);
        return hoaDon;
    }

    public HoaDon huy(String maHoaDon, String lyDoHuy) throws SQLException {
        HoaDon hoaDon = this.getById(maHoaDon);
        if (hoaDon.getTrangThaiHoaDon() != TrangThaiHoaDon.CHO_THANH_TOAN) {
            throw new IllegalStateException(ERROR_TRANG_THAI);
        }

        String lyDo = lyDoHuy == null ? "" : lyDoHuy.trim();
        if (lyDo.isEmpty()) {
            throw new IllegalArgumentException(ERROR_LY_DO_HUY);
        }
        if (lyDo.length() > MAX_LY_DO_HUY) {
            throw new IllegalArgumentException(ERROR_LY_DO_HUY_DAI);
        }

        try (TransactionScope scope = TransactionScope.begin()) {
            if (!this.hoaDonDAO.huy(scope, maHoaDon, lyDo)) {
                throw new IllegalStateException(ERROR_TRANG_THAI);
            }
            this.hoanTon(scope, hoaDon.getDanhSachChiTietHoaDon());
            scope.commit();
        }

        hoaDon.setTrangThaiHoaDon(TrangThaiHoaDon.DA_HUY);
        hoaDon.setLyDoHuy(lyDo);
        return hoaDon;
    }

    public void xoa(String maHoaDon) throws SQLException {
        HoaDon hoaDon = this.getById(maHoaDon);
        boolean daHoanTon = hoaDon.getTrangThaiHoaDon() == TrangThaiHoaDon.DA_HUY;

        try (TransactionScope scope = TransactionScope.begin()) {
            if (!daHoanTon) {
                this.hoanTon(scope, hoaDon.getDanhSachChiTietHoaDon());
            }
            if (!this.hoaDonDAO.delete(scope, maHoaDon)) {
                throw new IllegalArgumentException(ERROR_NOT_FOUND);
            }
            scope.commit();
        }
    }

    public HoaDon getById(String maHoaDon) throws SQLException {
        return this.hoaDonDAO.findById(maHoaDon)
                .orElseThrow(() -> new IllegalArgumentException(ERROR_NOT_FOUND));
    }

    public HoaDon getByPhieuTiepNhan(String maPhieuTiepNhan) throws SQLException {
        return this.hoaDonDAO.findByPhieuTiepNhan(maPhieuTiepNhan).orElse(null);
    }

    public PaginatedResult<HoaDonView> search(DanhSachHoaDonCriteria criteria) throws SQLException {
        DanhSachHoaDonCriteria filter = criteria == null
                ? DanhSachHoaDonCriteria.macDinh()
                : criteria;

        this.validateFilter(filter);

        Map<String, KhachHang> khachHangTheoMa = this.khachHangTheoMa();
        List<PhieuTiepNhan> danhSachPhieu = this.phieuTiepNhanService.findAll();
        Map<String, String> tenKhachHangTheoPhieu = this.tenKhachHangTheoPhieu(khachHangTheoMa, danhSachPhieu);
        Map<String, String> bienSoTheoPhieu = this.bienSoTheoPhieu(danhSachPhieu);
        Map<String, Voucher> voucherTheoMa = this.voucherTheoMa();

        List<HoaDonView> views = this.taoViews(filter, tenKhachHangTheoPhieu, bienSoTheoPhieu, voucherTheoMa);

        views.sort(Comparator
                .comparing(HoaDonView::ngayTao, Comparator.nullsLast(Comparator.reverseOrder()))
                .thenComparing(HoaDonView::maHoaDon, Comparator.reverseOrder()));

        return PaginatedResult.of(views, filter);
    }

    private Map<String, KhachHang> khachHangTheoMa() throws SQLException {
        Map<String, KhachHang> khachHangTheoMa = new HashMap<>();
        for (KhachHang khachHang : this.khachHangDAO.findAll()) {
            khachHangTheoMa.put(khachHang.getMaKhachHang(), khachHang);
        }
        return khachHangTheoMa;
    }

    private Map<String, String> tenKhachHangTheoPhieu(Map<String, KhachHang> khachHangTheoMa,
            List<PhieuTiepNhan> danhSachPhieu) {
        Map<String, String> tenKhachHangTheoPhieu = new HashMap<>();
        for (PhieuTiepNhan phieu : danhSachPhieu) {
            KhachHang khachHang = khachHangTheoMa.get(phieu.getMaKhachHang());
            tenKhachHangTheoPhieu.put(phieu.getMaPhieuTiepNhan(),
                    khachHang == null || khachHang.getHoTen() == null ? "" : khachHang.getHoTen());
        }
        return tenKhachHangTheoPhieu;
    }

    private Map<String, String> bienSoTheoPhieu(List<PhieuTiepNhan> danhSachPhieu) {
        Map<String, String> bienSoTheoPhieu = new HashMap<>();
        for (PhieuTiepNhan phieu : danhSachPhieu) {
            bienSoTheoPhieu.put(phieu.getMaPhieuTiepNhan(),
                    phieu.getBienSo() == null ? "" : phieu.getBienSo());
        }
        return bienSoTheoPhieu;
    }

    private Map<String, Voucher> voucherTheoMa() throws SQLException {
        Map<String, Voucher> voucherTheoMa = new HashMap<>();
        for (Voucher voucher : this.voucherDAO.findAll()) {
            voucherTheoMa.put(voucher.getMaVoucher(), voucher);
        }
        return voucherTheoMa;
    }

    private List<HoaDonView> taoViews(DanhSachHoaDonCriteria filter,
            Map<String, String> tenKhachHangTheoPhieu, Map<String, String> bienSoTheoPhieu,
            Map<String, Voucher> voucherTheoMa) throws SQLException {
        List<HoaDonView> views = new ArrayList<>();
        for (HoaDon hoaDon : this.hoaDonDAO.findAllWithDetails()) {
            HoaDonView view = this.taoView(hoaDon, tenKhachHangTheoPhieu, bienSoTheoPhieu, voucherTheoMa);
            if (this.matches(view, filter)) {
                views.add(view);
            }
        }
        return views;
    }

    private HoaDonView taoView(HoaDon hoaDon, Map<String, String> tenKhachHangTheoPhieu,
            Map<String, String> bienSoTheoPhieu, Map<String, Voucher> voucherTheoMa) {
        return new HoaDonView(
                hoaDon.getMaHoaDon(),
                hoaDon.getMaPhieuTiepNhan(),
                tenKhachHangTheoPhieu.getOrDefault(hoaDon.getMaPhieuTiepNhan(), ""),
                bienSoTheoPhieu.getOrDefault(hoaDon.getMaPhieuTiepNhan(), ""),
                hoaDon.getNgayTao(),
                hoaDon.getTrangThaiHoaDon(),
                this.tinhTongTien(hoaDon, voucherTheoMa),
                hoaDon.getThoiGianThanhToan(),
                hoaDon.getGhiChu(),
                hoaDon.getMaVoucher(),
                hoaDon.getLyDoHuy());
    }

    public double tinhTongTien(HoaDon hoaDon) {
        return this.tinhTongTienGoc(hoaDon) * (1.0 - this.tinhMucGiam(hoaDon));
    }

    public double tinhTongTien(List<ChiTietHoaDon> chiTiets, Voucher voucher) {
        return this.tinhTongTienGoc(chiTiets) * (1.0 - this.tinhMucGiam(voucher));
    }

    private double tinhTongTien(HoaDon hoaDon, Map<String, Voucher> voucherTheoMa) {
        if (hoaDon == null) {
            return 0.0;
        }
        return this.tinhTongTienGoc(hoaDon) * (1.0 - this.tinhMucGiam(hoaDon, voucherTheoMa));
    }

    private double tinhMucGiam(HoaDon hoaDon, Map<String, Voucher> voucherTheoMa) {
        if (hoaDon == null) {
            return 0.0;
        }
        if (hoaDon.getMucGiamApDung() != null) {
            return Math.min(Math.max(hoaDon.getMucGiamApDung(), 0.0), 1.0);
        }
        if (hoaDon.getMaVoucher() == null || hoaDon.getMaVoucher().isBlank()) {
            return 0.0;
        }
        return this.tinhMucGiam(voucherTheoMa.get(hoaDon.getMaVoucher()));
    }

    public double tinhTongTienGoc(HoaDon hoaDon) {
        return hoaDon == null ? 0.0 : this.tinhTongTienGoc(hoaDon.getDanhSachChiTietHoaDon());
    }

    public double tinhTongTienGoc(List<ChiTietHoaDon> chiTiets) {
        if (chiTiets == null) {
            return 0.0;
        }
        double tong = 0.0;
        for (ChiTietHoaDon ct : chiTiets) {
            double donGia = ct.getDonGia() == null ? 0.0 : ct.getDonGia();
            tong += ct.getSoLuong() * donGia;
        }
        return tong;
    }

    public double tinhMucGiam(HoaDon hoaDon) {
        if (hoaDon == null) {
            return 0.0;
        }
        if (hoaDon.getMucGiamApDung() != null) {
            return Math.min(Math.max(hoaDon.getMucGiamApDung(), 0.0), 1.0);
        }
        if (hoaDon.getMaVoucher() == null || hoaDon.getMaVoucher().isBlank()) {
            return 0.0;
        }
        try {
            return this.tinhMucGiam(this.voucherDAO.findById(hoaDon.getMaVoucher()).orElse(null));
        } catch (SQLException ignored) {
            return 0.0;
        }
    }

    public double tinhMucGiam(Voucher voucher) {
        if (!this.voucherConHieuLuc(voucher)) {
            return 0.0;
        }
        double mucGiam = voucher.getMucGiam() == null ? 0.0 : voucher.getMucGiam();
        return Math.min(Math.max(mucGiam, 0.0), 1.0);
    }

    private Voucher resolveVoucher(String maVoucher) throws SQLException {
        if (maVoucher == null || maVoucher.isBlank()) {
            return null;
        }
        Voucher voucher = this.voucherDAO.findById(maVoucher.trim())
                .orElseThrow(() -> new IllegalArgumentException(ERROR_VOUCHER));
        if (!this.voucherConHieuLuc(voucher)) {
            throw new IllegalArgumentException(ERROR_VOUCHER);
        }
        return voucher;
    }

    public boolean voucherConHieuLuc(Voucher voucher) {
        return voucher != null && voucher.isTrangThaiKichHoat()
                && (voucher.getThoiHanApDung() == null
                || !voucher.getThoiHanApDung().isBefore(LocalDateTime.now()));
    }

    private void capNhatTonKho(TransactionScope scope, List<ChiTietHoaDon> danhSachMoi,
            List<ChiTietHoaDon> danhSachCu) throws SQLException {
        this.hoanTon(scope, danhSachCu);
        this.truTon(scope, danhSachMoi);
    }

    private void hoanTon(TransactionScope scope, List<ChiTietHoaDon> chiTiets) throws SQLException {
        for (ChiTietHoaDon ct : chiTiets) {
            if (ct.getLoaiSanPham() == LoaiSanPham.PHU_TUNG) {
                this.phuTungService.increaseStock(scope, ct.getMaSanPham(), ct.getSoLuong());
            }
        }
    }

    private void truTon(TransactionScope scope, List<ChiTietHoaDon> chiTiets) throws SQLException {
        for (ChiTietHoaDon ct : chiTiets) {
            if (ct.getLoaiSanPham() == LoaiSanPham.PHU_TUNG) {
                this.phuTungService.decreaseStock(scope, ct.getMaSanPham(), ct.getSoLuong());
            }
        }
    }

    private void kiemTraTonKho(List<ChiTietHoaDon> danhSachMoi, List<ChiTietHoaDon> danhSachCu)
            throws SQLException {
        Map<String, Integer> soLuongCu = this.gopSoLuong(danhSachCu);
        for (ChiTietHoaDon ct : danhSachMoi) {
            if (ct.getLoaiSanPham() != LoaiSanPham.PHU_TUNG) {
                continue;
            }
            int soLuongTang = ct.getSoLuong() - soLuongCu.getOrDefault(this.khoa(ct), 0);
            if (soLuongTang <= 0) {
                continue;
            }
            PhuTung phuTung = this.phuTungService.getById(ct.getMaSanPham())
                    .orElseThrow(() -> new IllegalArgumentException(ERROR_SAN_PHAM));
            if (phuTung.getSoLuongTon() < soLuongTang) {
                throw new IllegalArgumentException(ERROR_THIEU_TON);
            }
        }
    }

    private List<ChiTietHoaDon> gopChiTietTrung(List<ChiTietHoaDon> chiTiets) {
        Map<String, ChiTietHoaDon> map = new LinkedHashMap<>();
        for (ChiTietHoaDon ct : chiTiets) {
            String key = this.khoa(ct);
            ChiTietHoaDon tonTai = map.get(key);
            if (tonTai != null) {
                tonTai.setSoLuong(tonTai.getSoLuong() + ct.getSoLuong());
                if (ct.getDonGia() != null) {
                    tonTai.setDonGia(ct.getDonGia());
                }
                continue;
            }
            ChiTietHoaDon moi = new ChiTietHoaDon();
            moi.setLoaiSanPham(ct.getLoaiSanPham());
            moi.setMaSanPham(ct.getMaSanPham().trim());
            moi.setSoLuong(ct.getSoLuong());
            moi.setDonGia(ct.getDonGia());
            map.put(key, moi);
        }
        return new ArrayList<>(map.values());
    }

    private Map<String, Integer> gopSoLuong(List<ChiTietHoaDon> chiTiets) {
        Map<String, Integer> ketQua = new LinkedHashMap<>();
        if (chiTiets == null) {
            return ketQua;
        }
        for (ChiTietHoaDon ct : chiTiets) {
            ketQua.merge(this.khoa(ct), ct.getSoLuong(), Integer::sum);
        }
        return ketQua;
    }

    private String khoa(ChiTietHoaDon ct) {
        return ct.getLoaiSanPham().name() + "|" + ct.getMaSanPham().trim();
    }

    private String chuanHoaGhiChu(String ghiChu) {
        return ghiChu == null || ghiChu.isBlank() ? null : ghiChu.trim();
    }

    private void validateFilter(DanhSachHoaDonCriteria filter) {
        if (filter.page() < 1 || filter.pageSize() < 1) {
            throw new IllegalArgumentException(ERROR_PHAN_TRANG);
        }
        if ((filter.tongTienTu() != null && filter.tongTienTu() < 0)
                || (filter.tongTienDen() != null && filter.tongTienDen() < 0)) {
            throw new IllegalArgumentException(ERROR_KHOANG_TIEN);
        }
        if (filter.tongTienTu() != null && filter.tongTienDen() != null
                && filter.tongTienTu() > filter.tongTienDen()) {
            throw new IllegalArgumentException(ERROR_KHOANG_TIEN);
        }
        if (filter.ngayTaoTu() != null && filter.ngayTaoDen() != null
                && filter.ngayTaoTu().isAfter(filter.ngayTaoDen())) {
            throw new IllegalArgumentException(ERROR_KHOANG_NGAY);
        }
    }

    private boolean matches(HoaDonView view, DanhSachHoaDonCriteria filter) {
        if (!this.containsText(filter.maHoaDon(), view.maHoaDon())
                || !this.containsText(filter.maPhieuTiepNhan(), view.maPhieuTiepNhan())
                || !this.containsText(filter.tenKhachHang(), view.tenKhachHang())) {
            return false;
        }
        if (filter.trangThai() != null && view.trangThai() != filter.trangThai()) {
            return false;
        }
        if (filter.ngayTaoTu() != null && (view.ngayTao() == null
                || view.ngayTao().toLocalDate().isBefore(filter.ngayTaoTu()))) {
            return false;
        }
        if (filter.ngayTaoDen() != null && (view.ngayTao() == null
                || view.ngayTao().toLocalDate().isAfter(filter.ngayTaoDen()))) {
            return false;
        }
        if (filter.tongTienTu() != null && view.tongTien() < filter.tongTienTu()) {
            return false;
        }
        if (filter.tongTienDen() != null && view.tongTien() > filter.tongTienDen()) {
            return false;
        }
        if (filter.timNhanh() != null && !filter.timNhanh().isBlank()) {
            String combined = this.chuanHoa(view.maHoaDon() + " " + view.maPhieuTiepNhan()
                    + " " + view.tenKhachHang() + " " + view.bienSo());
            if (!combined.contains(this.chuanHoa(filter.timNhanh()))) {
                return false;
            }
        }
        return true;
    }

    private boolean containsText(String filterValue, String value) {
        if (filterValue == null || filterValue.isBlank()) {
            return true;
        }
        return this.chuanHoa(value).contains(this.chuanHoa(filterValue));
    }

    private String chuanHoa(String value) {
        return TextNormalizerUtil.normalize(value);
    }

    private void validateChiTiet(List<ChiTietHoaDon> chiTiets) {
        if (chiTiets == null || chiTiets.isEmpty()) {
            throw new IllegalArgumentException(ERROR_CHI_TIET_TRONG);
        }
        for (ChiTietHoaDon ct : chiTiets) {
            if (ct == null || ct.getMaSanPham() == null || ct.getMaSanPham().isBlank()
                    || ct.getLoaiSanPham() == null) {
                throw new IllegalArgumentException(ERROR_SAN_PHAM);
            }
            if (ct.getSoLuong() <= 0) {
                throw new IllegalArgumentException(ERROR_SO_LUONG);
            }
            if (ct.getDonGia() == null || !Double.isFinite(ct.getDonGia()) || ct.getDonGia() < 0) {
                throw new IllegalArgumentException(ERROR_DON_GIA);
            }
        }
    }
}
