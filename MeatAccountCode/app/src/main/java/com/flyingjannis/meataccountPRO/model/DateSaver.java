package com.flyingjannis.meataccountPRO.model;

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

    /**
     * Aufbau des Strings: XXXX - YearWeekDayHour
     * @param date
     * @return
     */
    public static String getStringCode(DateSaver date) {
        String result = "";
        result += (char) date.getYear();
        result += (char) date.getWeekOfYear();
        result += (char) date.getDayOfWeek();
        result += (char) date.getHour();
        return result;
    }

    public static DateSaver encodeStringCode(String code) {
        char[] chars = code.toCharArray();
        return new DateSaver(chars[0], chars[1], chars[2], chars[3]);
    }

    @Override
    public boolean equals(Object other) {
        DateSaver otherDate = (DateSaver) other;
        return year == otherDate.getYear()
                && weekOfYear == otherDate.getWeekOfYear()
                && dayOfWeek == otherDate.getDayOfWeek()
                && hour == otherDate.getHour();
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
