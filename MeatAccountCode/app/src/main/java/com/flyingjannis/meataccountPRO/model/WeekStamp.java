package com.flyingjannis.meataccountPRO.model;

public class WeekStamp {
    private int[] days = new int[7];
    private int weekNumber;

    public WeekStamp(int weekNumber) {
        this.weekNumber = weekNumber;
    }

    public void setDay(int day, int amount) {
        days[day] = amount + days[day];
    }

    public int getMeatAmount() {
        int result = 0;
        for(int i = 0; i < days.length; i++) {
            result += days[i];
        }
        return result;
    }

    public int getWeekNumber() {
        return weekNumber;
    }

    public int[] getDays() {
        return days;
    }


}
