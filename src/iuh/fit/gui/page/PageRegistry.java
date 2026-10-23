package iuh.fit.gui.page;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class PageRegistry {

    private static final PageRegistry instance = new PageRegistry();

    private final Map<Class<? extends AbstractPage>, AbstractPage> pages = new LinkedHashMap<>();

    private PageRegistry() {
        this.register(new LoginPage());
        this.register(new DashboardPage());
        this.register(new KhachHangPage());
        this.register(new XePage());
        this.register(new NhanVienPage());
        this.register(new PhuTungPage());
        this.register(new DichVuPage());
        this.register(new VoucherPage());
        this.register(new PhanCongCaPage());
        this.register(new LichCuaToiPage());
        this.register(new LichCuaShopPage());
        this.register(new TiepNhanKhachHangPage());
        this.register(new QuanLyPhieuTiepNhanPage());
        this.register(new LapChanDoanPage());
        this.register(new XemChanDoanPage());
        this.register(new TiepNhanKhieuNaiPage());
        this.register(new QuanLyKhieuNaiPage());
        this.register(new LapPhieuNhapPage());
        this.register(new QuanLyPhieuNhapPage());
        this.register(new LapHoaDonPage());
        this.register(new QuanLyHoaDonPage());
        this.register(new DoanhThuPage());
        this.register(new TieuThuPhuTungPage());
        this.register(new TieuThuDichVuPage());
        this.register(new DoiMatKhauPage());
    }

    public static PageRegistry getInstance() {
        return instance;
    }

    private void register(AbstractPage page) {
        this.pages.put(page.getClass(), page);
    }

    public AbstractPage get(Class<? extends AbstractPage> type) {
        return this.pages.get(type);
    }

    public Collection<AbstractPage> all() {
        return Collections.unmodifiableCollection(this.pages.values());
    }
}
