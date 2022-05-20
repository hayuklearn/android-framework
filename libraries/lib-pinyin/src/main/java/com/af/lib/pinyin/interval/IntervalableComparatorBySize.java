package com.af.lib.pinyin.interval;

import java.util.Comparator;

public class IntervalableComparatorBySize implements Comparator<Intervalable> {

    @Override
    public int compare(Intervalable intervalable1, Intervalable intervalable2) {
        int comparison = intervalable2.size() - intervalable1.size();
        if (comparison == 0) {
            comparison = intervalable1.getStart() - intervalable2.getStart();
        }
        return comparison;
    }

}
