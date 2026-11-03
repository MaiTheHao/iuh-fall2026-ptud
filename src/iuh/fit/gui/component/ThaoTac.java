package iuh.fit.gui.component;

import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Mô tả một nút thao tác dùng trong cột thao tác của bảng.
 * {@code hienThi} quyết định nút có xuất hiện trên một dòng hay không; {@code null} là luôn hiện.
 */
public record ThaoTac(
        String iconName,
        String tooltipKey,
        KieuThaoTac kieu,
        Consumer<Integer> khiChon,
        Predicate<Integer> hienThi) {

    public ThaoTac(String iconName, String tooltipKey, KieuThaoTac kieu, Consumer<Integer> khiChon) {
        this(iconName, tooltipKey, kieu, khiChon, null);
    }

    public enum KieuThaoTac {
        THUONG,
        NGUY_HIEM
    }
}
