package com.af.lib.pinyin.dict;

import android.content.Context;

/**
 * Created by guyacong on 2016/12/23.
 */
public final class CnCityDict extends AndroidAssetDict {

    static volatile CnCityDict singleton = null;

    public CnCityDict(Context context) {
        super(context);
    }

    @Override
    protected String assetFileName() {
        return "cn-cities.txt";
    }

    public static CnCityDict getInstance(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("context == null");
        }
        if (singleton == null) {
            synchronized (CnCityDict.class) {
                if (singleton == null) {
                    singleton = new CnCityDict(context);
                }
            }
        }
        return singleton;
    }
}
