package com.flyingjannis.meataccount;

import com.flyingjannis.meataccount.activities.SettingsActivity;
import com.flyingjannis.meataccount.model.AccountV2;
import com.flyingjannis.meataccount.testing.ExampleAccounts;

import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    AccountV2 TestAccount;
    SettingsActivity testActivity;

    @Before
    public void createObjects() {
        TestAccount = ExampleAccounts.getRandomAccountV2(37, 500);
        testActivity = new SettingsActivity();
    }



    @Test
    public void daysSinceLastMeatTest() {

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