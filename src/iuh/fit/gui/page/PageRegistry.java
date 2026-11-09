package iuh.fit.gui.page;

import iuh.fit.enumeration.ManHinh;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class PageRegistry {

    private static final PageRegistry instance = new PageRegistry();

    private final Map<Class<? extends AbstractPage>, AbstractPage> pages = new LinkedHashMap<>();
    private final Map<Class<? extends AbstractPage>, ManHinh> manHinhTheoTrang = new LinkedHashMap<>();

    private PageRegistry() {
        this.register(new LoginPage(), ManHinh.DANG_NHAP);
        this.register(new DashboardPage(), ManHinh.DASHBOARD);
        this.register(new KhachHangPage(), ManHinh.KHACH_HANG);
        this.register(new XePage(), ManHinh.XE);
        this.register(new DanhMucXePage(), ManHinh.DANH_MUC_XE);
        this.register(new NhanVienPage(), ManHinh.NHAN_VIEN);
        this.register(new PhuTungPage(), ManHinh.PHU_TUNG);
        this.register(new NhaCungCapPage(), ManHinh.NHA_CUNG_CAP);
        this.register(new DichVuPage(), ManHinh.DICH_VU);
        this.register(new VoucherPage(), ManHinh.VOUCHER);
        this.register(new PhanCongCaPage(), ManHinh.PHAN_CONG_CA);
        this.register(new LichCuaToiPage(), ManHinh.LICH_CUA_TOI);
        this.register(new LichCuaShopPage(), ManHinh.LICH_CUA_SHOP);
        this.register(new TiepNhanKhachHangPage(), ManHinh.TIEP_NHAN_KHACH_HANG);
        this.register(new QuanLyPhieuTiepNhanPage(), ManHinh.QUAN_LY_PHIEU_TIEP_NHAN);
        this.register(new BangChanDoanPage(), ManHinh.BANG_CHAN_DOAN);
        this.register(new XemChanDoanPage(), ManHinh.XEM_CHAN_DOAN);
        this.register(new TiepNhanKhieuNaiPage(), ManHinh.TIEP_NHAN_KHIEU_NAI);
        this.register(new QuanLyKhieuNaiPage(), ManHinh.QUAN_LY_KHIEU_NAI);
        this.register(new QuanLyChanDoanPage(), ManHinh.QUAN_LY_CHAN_DOAN);
        this.register(new LapPhieuNhapPage(), ManHinh.LAP_PHIEU_NHAP);
        this.register(new QuanLyPhieuNhapPage(), ManHinh.QUAN_LY_PHIEU_NHAP);
        this.register(new LapHoaDonPage(), ManHinh.LAP_HOA_DON);
        this.register(new QuanLyHoaDonPage(), ManHinh.QUAN_LY_HOA_DON);
        this.register(new DoanhThuPage(), ManHinh.DOANH_THU);
        this.register(new TieuThuPhuTungPage(), ManHinh.TIEU_THU_PHU_TUNG);
        this.register(new TieuThuDichVuPage(), ManHinh.TIEU_THU_DICH_VU);
        this.register(new DoiMatKhauPage(), ManHinh.DOI_MAT_KHAU);
    }

    public static PageRegistry getInstance() {
        return instance;
    }

    private void register(AbstractPage page, ManHinh manHinh) {
        this.pages.put(page.getClass(), page);
        this.manHinhTheoTrang.put(page.getClass(), manHinh);
    }

    public AbstractPage get(Class<? extends AbstractPage> type) {
        return this.pages.get(type);
    }

    public ManHinh getManHinh(Class<? extends AbstractPage> type) {
        return this.manHinhTheoTrang.get(type);
    }

    public Collection<AbstractPage> all() {
        return Collections.unmodifiableCollection(this.pages.values());
    }
}
