package com.flyingjannis.meataccount.testing;

import com.flyingjannis.meataccount.model.Account;
import com.flyingjannis.meataccount.model.AccountV2;
import com.flyingjannis.meataccount.model.WeekStamp;
import com.flyingjannis.meataccount.model.WeekStampV2;

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


    public static AccountV2 getRandomAccountV2(int weeks, int amount) {
        AccountV2 account = new AccountV2(amount);
        Calendar calender = Calendar.getInstance();
        account.setCreationDateMillis(calender.getTimeInMillis() - ((long) weeks * 604800000) -
                (calender.get(Calendar.MINUTE) * 60000) - (calender.get(Calendar.SECOND) * 1000));
        account.setWeeks(new WeekStampV2[weeks + 1]);
        for(int i = 0; i < account.getWeeks().length - 1; i++) {
            WeekStampV2 tmp = new WeekStampV2(i);
            Random random = new Random();
            for(int j = 0; j < 8; j++) {
                int x = random.nextInt(200);
                if(x < 160) x = 0;
                tmp.setDay(j, x);
            }
            account.getWeeks()[i] = tmp;
        }
        account.getWeeks()[weeks] = new WeekStampV2(weeks);
        account.setPayments(weeks);
        return account;
    }
}
