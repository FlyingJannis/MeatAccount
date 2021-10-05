package com.flyingjannis.meataccount.model;

public class WeekStampV2 {
    private int[] days = new int[8];                            //Das 8te Feld, falls schon Zahltag ist, aber noch nicht gezahlt wurde (neue WeekStamps werden erst bei Zahlung erstellt!)
    private int weekNumber;

    public WeekStampV2(int weekNumber) {
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

    public int[] getDays() {
        return days;
    }
}
