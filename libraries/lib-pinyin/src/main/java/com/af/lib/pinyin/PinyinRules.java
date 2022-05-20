package com.af.lib.pinyin;

import java.util.HashMap;
import java.util.Map;

public final class PinyinRules {
    private final Map<String, String[]> mOverrides = new HashMap<>();

    public PinyinRules add(char c, String pinyin) {
        mOverrides.put(String.valueOf(c), new String[]{pinyin});
        return this;
    }

    public PinyinRules add(String str, String pinyin) {
        mOverrides.put(str, new String[]{pinyin});
        return this;
    }

    String toPinyin(char c) {
        String[] value = mOverrides.get(String.valueOf(c));
        return null == value || 0 == value.length ? null : value[0];
    }

    PinyinMapDict toPinyinMapDict() {
        return new PinyinMapDict() {
            @Override
            public Map<String, String[]> mapping() {
                return mOverrides;
            }
        };
    }
}
