package com.flyingjannis.meataccount;


import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

public class Account {      //DateSaver speichert mittlerweile viel unnötigen Stuff, da die Payments mit Millisekunden geregelt werden!
    protected int balance;
    protected int weeklyAmount;
    protected int payments;
    protected WeekStamp[] weeks = new WeekStamp[1];


    protected DateSaver creationDate;
    protected long creationDateMillis;

    public Account(int weeklyAmount) {
        this.weeklyAmount = weeklyAmount;
        this.balance = weeklyAmount;
        Calendar calendar = Calendar.getInstance();
        long minutes = calendar.get(Calendar.MINUTE);        //Rundet die Millis auf die nächste volle Stunde ab!
        long seconds = calendar.get(Calendar.SECOND);
        long millisToFullHour = minutes * 60000 + seconds * 1000;

        payments = 0;
        weeks[payments] = new WeekStamp(payments);
        creationDateMillis = calendar.getTimeInMillis() - millisToFullHour;
        creationDate = new DateSaver(calendar.get(Calendar.YEAR), calendar.get(Calendar.WEEK_OF_YEAR),
                calendar.get(Calendar.DAY_OF_WEEK), calendar.get(Calendar.HOUR_OF_DAY));
    }

    public void changeCreationHour(int i) {
        creationDate.hour = creationDate.hour + i;
        if(creationDate.hour > 23) {                        //Falls die Veränderung der Stunde, gleichzeitig den Tag ändert!
            creationDate.hour -= 24;                        //ACHTUNG: An Jahr und Woche wurde nicht gedacht!!!
            if(creationDate.dayOfWeek == 7) {
                creationDate.dayOfWeek = 1;
            } else {
                creationDate.dayOfWeek += 1;
            }
        } else if(creationDate.hour < 0) {
            creationDate.hour += 24;
            if (creationDate.dayOfWeek == 1) {
                creationDate.dayOfWeek = 7;
            } else {
                creationDate.dayOfWeek -= 1;
            }
        }
    }

    public void addMeatDay(int amount) {
        if(payments + 1 > weeks.length) {           //Neue WeekStamps müssen hinzugefügt werden!
            WeekStamp[] tmp = weeks;
            weeks = new WeekStamp[payments + 1];
            for(int i = 0; i < tmp.length; i++) {
                weeks[i] = tmp[i];
            }
            for(int n = tmp.length; n < weeks.length; n++) {
                weeks[n] = new WeekStamp(n);
            }
        }
        //Der Creation Day ist immer das Feld 0 in jeder Woche!

        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = ((((calendar.get(Calendar.DAY_OF_WEEK) - creationDate.dayOfWeek) % 7) + 7) % 7);
        weeks[payments].setDay(dayOfWeek, amount);
    }


}
