package com.flyingjannis.meataccountPRO;

import com.flyingjannis.meataccountPRO.activities.SettingsActivity;
import com.flyingjannis.meataccountPRO.model.AccountV2;
import com.flyingjannis.meataccountPRO.model.DateSaver;
import com.flyingjannis.meataccountPRO.testing.ExampleAccounts;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    AccountV2 testAccount;
    SettingsActivity testActivity;

    @Before
    public void createObjects() {
        testAccount = ExampleAccounts.getRandomAccountV2(50, 300);
        testActivity = new SettingsActivity();
    }



    @Test
    public void AccountCoderTest() {
        System.out.println(AccountV2.dataToString(testAccount));
        System.out.println(AccountV2.dataToString(testAccount).length());
        int[] ints = {1, 5, 8, 10, 37, 29, 291};
        int[] ints2 = Arrays.copyOfRange(ints, 3, 6);
        System.out.println(Arrays.toString(ints2));
    }

    @Test
    public void DateSaverCoderTest() {
        DateSaver test = new DateSaver(2018, 43, 6, 20);
        String str = DateSaver.getStringCode(test);
        assertTrue(str.length() == 4);
        System.out.println(str);
        assertEquals(test, DateSaver.encodeStringCode(str));
    }

    @Test
    public void longCoderTest() {
        long test = 1633957835071L;
        String str = AccountV2.longToString(test);
        assertTrue(str.length() == 4);
        System.out.println(str);
        assertEquals(test, AccountV2.stringToLong(str));
    }

    @Test
    public void intToString() {
        int[] ints = {172, 291, 902, 743, 65, 65535};

        char[] chars = new char[ints.length];
        for(int i = 0; i < ints.length; i++) {
            chars[i] = (char) ints[i];
        }

        String str = "";
        for(int i = 0; i < chars.length; i++) {
            System.out.println(chars[i]);
            str += chars[i];
        }

        System.out.println(str);

        char[] chars1 = str.toCharArray();
        int[] newInts = new int[chars1.length];
        for(int i = 0; i < chars1.length; i++) {
            newInts[i] = chars1[i];
            System.out.println(newInts[i]);
        }


    }


}