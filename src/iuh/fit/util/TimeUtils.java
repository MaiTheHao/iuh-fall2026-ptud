package iuh.fit.util;

import java.time.LocalTime;
import java.util.Objects;

public final class TimeUtils {

    private TimeUtils() {
        throw new AssertionError("Utility class");
    }

    public static int toSecondsOfDay(LocalTime time) {
        Objects.requireNonNull(time, "time must not be null");
        return time.toSecondOfDay();
    }
}
