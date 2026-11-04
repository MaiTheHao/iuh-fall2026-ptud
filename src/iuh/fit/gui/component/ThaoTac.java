package iuh.fit.gui.component;

import java.util.function.Consumer;
import java.util.function.Predicate;

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
