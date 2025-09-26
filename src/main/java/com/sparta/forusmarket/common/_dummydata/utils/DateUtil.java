package com.sparta.forusmarket.common._dummydata.utils;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.ThreadLocalRandom;

public abstract class DateUtil {

    public static LocalDateTime getRandomDateTime(LocalDateTime start, LocalDateTime end) {
        long startSeconds = start.toEpochSecond(ZoneOffset.UTC);
        long endSeconds = end.toEpochSecond(ZoneOffset.UTC);
        long randomSeconds = ThreadLocalRandom.current().nextLong(startSeconds, endSeconds + 1);
        return LocalDateTime.ofEpochSecond(randomSeconds, 0, ZoneOffset.UTC);
    }
}
