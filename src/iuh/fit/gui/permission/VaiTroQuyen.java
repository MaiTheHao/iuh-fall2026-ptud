package iuh.fit.gui.permission;

import java.util.Set;

import iuh.fit.enumeration.VaiTro;
import iuh.fit.gui.page.AbstractPage;
import iuh.fit.gui.page.BangChanDoanPage;
import iuh.fit.gui.page.DanhMucXePage;
import iuh.fit.gui.page.DoiMatKhauPage;
import iuh.fit.gui.page.KhachHangPage;
import iuh.fit.gui.page.LapHoaDonPage;
import iuh.fit.gui.page.LichCuaShopPage;
import iuh.fit.gui.page.LichCuaToiPage;
import iuh.fit.gui.page.LoginPage;
import iuh.fit.gui.page.PageRegistry;
import iuh.fit.gui.page.QuanLyChanDoanPage;
import iuh.fit.gui.page.QuanLyHoaDonPage;
import iuh.fit.gui.page.QuanLyKhieuNaiPage;
import iuh.fit.gui.page.QuanLyPhieuTiepNhanPage;
import iuh.fit.gui.page.TiepNhanKhachHangPage;
import iuh.fit.gui.page.TiepNhanKhieuNaiPage;
import iuh.fit.gui.page.XePage;
import iuh.fit.gui.page.XemChanDoanPage;

public final class VaiTroQuyen {

    private static final Set<Class<? extends AbstractPage>> TRANG_TIEP_NHAN = Set.of(
            KhachHangPage.class,
            XePage.class,
            DanhMucXePage.class,
            TiepNhanKhachHangPage.class,
            QuanLyPhieuTiepNhanPage.class,
            XemChanDoanPage.class,
            TiepNhanKhieuNaiPage.class,
            QuanLyKhieuNaiPage.class,
            LichCuaToiPage.class,
            LichCuaShopPage.class,
            DoiMatKhauPage.class
    );

    private static final Set<Class<? extends AbstractPage>> TRANG_THU_NGAN = Set.of(
            LapHoaDonPage.class,
            QuanLyHoaDonPage.class,
            LichCuaToiPage.class,
            DoiMatKhauPage.class
    );

    private static final Set<Class<? extends AbstractPage>> TRANG_KY_THUAT = Set.of(
            QuanLyChanDoanPage.class,
            BangChanDoanPage.class,
            XemChanDoanPage.class,
            LichCuaToiPage.class,
            DoiMatKhauPage.class
    );

    private VaiTroQuyen() {
    }

    public static boolean duocHien(
            VaiTro vaiTro,
            Class<? extends AbstractPage> trang) {

        if (vaiTro == null || trang == null || trang == LoginPage.class) {
            return false;
        }

        return switch (vaiTro) {
            case NHAN_VIEN_QUAN_LY -> {
                AbstractPage page = PageRegistry.getInstance().get(trang);
                yield page != null && page.getGroup() != null;
            }
            case NHAN_VIEN_TIEP_NHAN -> TRANG_TIEP_NHAN.contains(trang);
            case NHAN_VIEN_THU_NGAN -> TRANG_THU_NGAN.contains(trang);
            case NHAN_VIEN_KY_THUAT -> TRANG_KY_THUAT.contains(trang);
        };
    }
}
