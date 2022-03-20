package me.roinujnosde.wolfbot.models.spiget;

import java.util.Locale;

public class Rating {

    private final int count;
    private final double average;

    public Rating(int count, double average) {
        this.count = count;
        this.average = average;
    }

    public int getCount() {
        return count;
    }

    public double getAverage() {
        return average;
    }

    @Override
    public String toString() {
        return String.format(Locale.ENGLISH, "%.1f (%d)", getAverage(), getCount());
    }
}
