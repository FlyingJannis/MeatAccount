package com.flyingjannis.meataccount.model;

import java.util.Calendar;

public class AccountV2 {
    /**
     * Dies ist eine geupdatete Klasse von Account, die anstatt WeekStamp WeekStampV2 verwendet. Einen Wochen Stempel mit 8 Tages-Eintrags Feldern
     * Das erste und das letzte sind hierbei jeweils für den gleichen Tag.
     */
    private String version;

    private int balance;
    private int weeklyAmount;
    private int payments;
    private WeekStampV2[] weeks = new WeekStampV2[1];


    private DateSaver creationDate;
    private long creationDateMillis;

    public AccountV2(int weeklyAmount) {
        this.version = "2";
        
        this.weeklyAmount = weeklyAmount;
        this.balance = weeklyAmount;
        Calendar calendar = Calendar.getInstance();
        long minutes = calendar.get(Calendar.MINUTE);        //Rundet die Millis auf die nächste volle Stunde ab!
        long seconds = calendar.get(Calendar.SECOND);
        long millisToFullHour = minutes * 60000 + seconds * 1000;

        payments = 0;
        weeks[payments] = new WeekStampV2(payments);
        creationDateMillis = calendar.getTimeInMillis() - millisToFullHour;
        creationDate = new DateSaver(calendar.get(Calendar.YEAR), calendar.get(Calendar.WEEK_OF_YEAR),
                calendar.get(Calendar.DAY_OF_WEEK), calendar.get(Calendar.HOUR_OF_DAY));
    }

    public void changeCreationHour(int i) {
        creationDate.setHour(creationDate.getHour() + i);
        if(creationDate.getHour() > 23) {                                            //Falls die Veränderung der Stunde, gleichzeitig den Tag ändert!
            creationDate.setHour(creationDate.getHour() - 24);                       //ACHTUNG: An Jahr und Woche wurde nicht gedacht!!!
            if(creationDate.getDayOfWeek() == 7) {
                creationDate.setDayOfWeek(1);
            } else {
                creationDate.setDayOfWeek(creationDate.getDayOfWeek() + 1);
            }
        } else if(creationDate.getHour() < 0) {
            creationDate.setHour(creationDate.getHour() + 24);
            if (creationDate.getDayOfWeek() == 1) {
                creationDate.setDayOfWeek(7);
            } else {
                creationDate.setDayOfWeek(creationDate.getDayOfWeek() - 1);
            }
        }
    }

    public void addMeatDay(int amount) {
        if(payments + 1 > weeks.length) {           //Neue WeekStamps müssen hinzugefügt werden!
            WeekStampV2[] tmp = weeks;
            weeks = new WeekStampV2[payments + 1];
            for(int i = 0; i < tmp.length; i++) {
                weeks[i] = tmp[i];
            }
            for(int n = tmp.length; n < weeks.length; n++) {
                weeks[n] = new WeekStampV2(n);
            }
        }

        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = ((((calendar.get(Calendar.DAY_OF_WEEK) - creationDate.getDayOfWeek()) % 7) + 7) % 7);
        if(dayOfWeek == 0) {
            if(calendar.get(Calendar.HOUR_OF_DAY) < creationDate.getHour()) {                       //Falls Zahltag, aber noch nicht Zahlstunde!
                dayOfWeek = 7;
            }
        }
        weeks[payments].setDay(dayOfWeek, amount);
    }

    public static AccountV2 transformAccount(Account oldAccount) {
        AccountV2 newAccount = new AccountV2(oldAccount.getWeeklyAmount());
        newAccount.setBalance(oldAccount.getBalance());
        newAccount.setPayments(oldAccount.getPayments());
        newAccount.setCreationDate(oldAccount.getCreationDate());
        newAccount.setCreationDateMillis(oldAccount.getCreationDateMillis());

        WeekStampV2[] newStamps = new WeekStampV2[oldAccount.getPayments() + 1];
        for(int i = 0; i <= oldAccount.getPayments(); i++) {
            newStamps[i] = WeekStampV2.transformWeekStamp(oldAccount.getWeeks()[i]);
        }
        newAccount.setWeeks(newStamps);
        return newAccount;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public int getWeeklyAmount() {
        return weeklyAmount;
    }

    public void setWeeklyAmount(int weeklyAmount) {
        this.weeklyAmount = weeklyAmount;
    }

    public int getPayments() {
        return payments;
    }

    public void setPayments(int payments) {
        this.payments = payments;
    }

    public WeekStampV2[] getWeeks() {
        return weeks;
    }

    public void setWeeks(WeekStampV2[] weeks) {
        this.weeks = weeks;
    }

    public DateSaver getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(DateSaver creationDate) {
        this.creationDate = creationDate;
    }

    public long getCreationDateMillis() {
        return creationDateMillis;
    }

    public void setCreationDateMillis(long creationDateMillis) {
        this.creationDateMillis = creationDateMillis;
    }
}
