package com.af.lib.pinyin.interval;


import java.util.Comparator;

public class IntervalableComparatorByPosition implements Comparator<Intervalable> {

    @Override
    public int compare(Intervalable intervalable1, Intervalable intervalable2) {
        return intervalable1.getStart() - intervalable2.getStart();
    }

}

