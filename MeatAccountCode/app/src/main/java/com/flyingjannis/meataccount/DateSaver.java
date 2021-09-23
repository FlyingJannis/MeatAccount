package com.flyingjannis.meataccount;

public class DateSaver {

    protected int year;
    protected int weekOfYear;
    protected int dayOfWeek;
    protected int hour;

    public DateSaver(int year, int weekOfYear, int dayOfWeek, int hour) {
        this.year = year;
        this.weekOfYear = weekOfYear;
        this.dayOfWeek = dayOfWeek;
        this.hour = hour;
    }
}
