package com.flyingjannis.meataccount.model;

public class DateSaver {
    private int year;
    private int weekOfYear;
    private int dayOfWeek;
    private int hour;

    public DateSaver(int year, int weekOfYear, int dayOfWeek, int hour) {
        this.year = year;
        this.weekOfYear = weekOfYear;
        this.dayOfWeek = dayOfWeek;
        this.hour = hour;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getWeekOfYear() {
        return weekOfYear;
    }

    public void setWeekOfYear(int weekOfYear) {
        this.weekOfYear = weekOfYear;
    }

    public int getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(int dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }
}
