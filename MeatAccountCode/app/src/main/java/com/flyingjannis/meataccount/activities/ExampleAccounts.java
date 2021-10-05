package com.flyingjannis.meataccount.activities;

import com.flyingjannis.meataccount.model.Account;
import com.flyingjannis.meataccount.model.WeekStamp;

import java.util.Calendar;
import java.util.Random;

public class ExampleAccounts {

    public static Account getRandomAccount(int weeks, int amount) {
        Account account = new Account(amount);
        Calendar calender = Calendar.getInstance();
        account.setCreationDateMillis(calender.getTimeInMillis() - ((long) weeks * 604800000) -
                (calender.get(Calendar.MINUTE) * 60000) - (calender.get(Calendar.SECOND) * 1000));
        account.setWeeks(new WeekStamp[weeks + 1]);
        for(int i = 0; i < account.getWeeks().length - 1; i++) {
            WeekStamp tmp = new WeekStamp(i);
            Random random = new Random();
            for(int j = 0; j < 7; j++) {
                int x = random.nextInt(200);
                if(x < 160) x = 0;
                tmp.setDay(j, x);
            }
            account.getWeeks()[i] = tmp;
        }
        account.getWeeks()[weeks] = new WeekStamp(weeks);
        account.setPayments(weeks);
        return account;
    }
}
