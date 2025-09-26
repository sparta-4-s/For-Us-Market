package com.sparta.forusmarket.common._dummydata.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public abstract class RandomUtil {

    private static final Random random = new Random();

    public static <T> T getRandomKey(Map<T, ?> map) {
        List<T> keys = new ArrayList<>(map.keySet());
        return keys.get(random.nextInt(keys.size()));
    }

    public static <T> T getRandomValue(List<T> list) {
        return list.get(random.nextInt(list.size()));
    }
}
