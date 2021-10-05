package com.flyingjannis.meataccount;

import com.flyingjannis.meataccount.activities.SettingsActivity;
import com.flyingjannis.meataccount.model.AccountV2;

import org.junit.Before;
import org.junit.Test;

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
        TestAccount = new AccountV2(500);
        testActivity = new SettingsActivity();
    }



    @Test
    public void daysSinceLastMeatTest() {

    }


}