package com.flyingjannis.meataccount.model;

import com.flyingjannis.meataccount.activities.SettingsActivity;

import java.util.Arrays;
import java.util.Calendar;

public class AccountV2 {
    /**
     * Dies ist eine geupdatete Klasse von Account, die anstatt WeekStamp WeekStampV2 verwendet. Einen Wochen Stempel mit 8 Tages-Eintrags Feldern
     * Das erste und das letzte sind hierbei jeweils für den gleichen Tag.
     */
    private static AccountV2 instance;

    private int balance;
    private int weeklyAmount;
    private int payments;
    private WeekStampV2[] weeks = new WeekStampV2[1];


    private DateSaver creationDate;
    private long creationDateMillis;

    public AccountV2(int weeklyAmount) {
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



    public static AccountV2 getInstance() {
        return instance;
    }

    public static void loadAccount(AccountV2 account) {
        instance = account;
    }

    /**
     * Aufbau: Balance(0) WeeklyAmount(1) Payments(2) CreationDate(3-6) CreationDateMillis(7-10) WeekStamps(11-?)
     * @param account
     * @return
     */
    public static String dataToString(AccountV2 account) {
        String result = "";
        result += (char) account.getBalance();
        result += (char) account.getWeeklyAmount();
        result += (char) account.getPayments();
        result += DateSaver.getStringCode(account.getCreationDate())
                + longToString(account.getCreationDateMillis())
                + accountWeeksToString(account);
        return result;
    }

    public static AccountV2 encodeAccount(String str) {
        char[] chars = str.toCharArray();
        AccountV2 account = new AccountV2(chars[1]);
        account.setBalance(chars[0]);
        account.setPayments(chars[2]);
        account.setCreationDate(DateSaver.encodeStringCode(new String(Arrays.copyOfRange(chars, 3, 7))));
        account.setCreationDateMillis(stringToLong(new String(Arrays.copyOfRange(chars, 7, 11))));
        account.setWeeks(stringToWeekStamp(new String(Arrays.copyOfRange(chars, 11, chars.length))));
        return account;
    }

    public static String longToString(long number) {
        boolean[] numberBinary = new boolean[8 * 8];
        long tmp = number;
        for(int i = 63; i >= 0; i--) {
            if(tmp >= (long) Math.pow(2.0, i)) {
                numberBinary[i] = true;
                tmp -= (long) Math.pow(2.0, i);
            }
        }
        String result = "";
        for(int i = 0; i < 4; i++) {
            int num = 0;
            for(int j = 0; j < 16; j++) {
                if(numberBinary[i * 16 + j]) {
                    num += Math.pow(2.0, j);
                }
            }
            result += (char) num;
        }
        return result;
    }

    public static long stringToLong(String str) {
        boolean[] numberBinary = new boolean[8 * 8];
        char[] chars = str.toCharArray();

        for(int i = 0; i < 4; i++) {
            int num = chars[i];
            for(int j = 15; j >= 0; j--) {
                if(num >= Math.pow(2.0, j)) {
                    numberBinary[i * 16 + j] = true;
                    num -= Math.pow(2.0, j);
                }
            }
        }
        long result = 0;
        for(int i = 0; i < 64; i++) {
            if(numberBinary[i]) {
                result += (long) Math.pow(2.0, i);
            }
        }
        return result;
    }

    /**
     * Genaue Fleischverteilung an den einzelnen Tagen wird weitesgehend ignoriert!
     * Aufbau: Week1Week2...WeekXTageOhneFleisch
     * @return
     */
    public static String accountWeeksToString(AccountV2 account) {
        WeekStampV2[] weeks = account.getWeeks();
        int dayLastMeat = 0;
        for(int i = weeks.length - 1; i >= 0; i--) {
            WeekStampV2 week = weeks[i];
            if(week.getMeatAmount() != 0) {
                int[] days = week.getDays();
                for(int j = 7; j >= 0; j--) {
                    if(days[j] != 0) {
                        dayLastMeat = j;
                        break;
                    }
                }
                break;
            }
        }
        String result = "";
        result += (char) dayLastMeat;
        for(int i = 0; i < weeks.length; i++) {
            result += (char) weeks[i].getMeatAmount();
        }
        return result;
    }

    public static WeekStampV2[] stringToWeekStamp(String str) {
        char[] chars = str.toCharArray();
        int dayLastMeat = chars[0];
        WeekStampV2[] weeks = new WeekStampV2[chars.length - 1];
        for(int i = 1; i < chars.length; i++) {
            WeekStampV2 week = new WeekStampV2(i - 1);
            if(chars[i] > 0) {
                week.setDay(dayLastMeat, chars[i]);
            }
            weeks[i - 1] = week;
        }
        return weeks;
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
