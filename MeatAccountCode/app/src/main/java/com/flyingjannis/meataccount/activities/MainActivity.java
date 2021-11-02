package com.flyingjannis.meataccount.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.flyingjannis.meataccount.R;
import com.flyingjannis.meataccount.broadcasts.NewMeatBroadcastDE;
import com.flyingjannis.meataccount.broadcasts.NewMeatBroadcastEN;
import com.flyingjannis.meataccount.broadcasts.ReminderBroadcastDE;
import com.flyingjannis.meataccount.broadcasts.ReminderBroadcastEN;
import com.flyingjannis.meataccount.model.Account;
import com.flyingjannis.meataccount.model.AccountV2;
import com.flyingjannis.meataccount.model.TutorialsReceived;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static long REMINDER_TIME = 172800000;          //3 Tage: 259200000, 2 Tage: 172800000
    public static boolean loadingCompleted = false;

    private AccountV2 myAccount;
    private TutorialsReceived tutorialsReceived;

    private int actualMinus = 0;
    private int areYouSure = 0;
    private Toast actualToast;

    private Handler handler = new Handler();
    private Runnable runnable =  new Runnable(){
        @Override
        public void run() {
            //loadData();
            if(myAccount != null) {
                int payments = howManyPayments();               //howManyPayments() updated das payments-Fled (Anzahl der bisherigen Zahlungen), kann also nur einmal sinnvoll verwendet werden.
                if(payments > 0) {
                    makeToast(getResources().getString(R.string.amount_recieved) + " " +
                        beautifulWeight(payments * myAccount.getWeeklyAmount()) + " " +
                        getResources().getString(R.string.amount_recieved_end), Toast.LENGTH_LONG);
                }
                pay(payments * myAccount.getWeeklyAmount());         //draw() ist in pay() enthalten!

                handler.postDelayed(runnable, 10000);
            }
        }
    };

    private ConstraintLayout clDeleteDialog;
    private Button buttonCommitDelete;
    private Button buttonCancelDelete;

    private ProgressBar progressBar;
    private ProgressBar progressBarRed;
    private TextView tvBalance;
    private TextView tvPayday;
    private TextView tvExplanation;

    private TextView tvMinus;
    private Button buttonAccept;
    private Button buttonUndo;

    private Button button250;
    private Button button100;
    private Button button20;
    private Button button10;
    private Button buttonSettings;
    private Button buttonDelete;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.progressBar);
        progressBarRed = findViewById(R.id.progressBarRed);
        tvBalance = findViewById(R.id.tvBalance);
        tvMinus = findViewById(R.id.tvMinus);
        button10 = findViewById(R.id.button10);
        button20 = findViewById(R.id.button20);
        button100 = findViewById(R.id.button100);
        button250 = findViewById(R.id.button250);
        buttonAccept = findViewById(R.id.buttonAcceptMinus);
        buttonUndo = findViewById(R.id.buttonUndo);
        tvPayday = findViewById(R.id.tvPayday);
        buttonSettings = findViewById(R.id.buttonSettings);
        tvExplanation = findViewById(R.id.tvExplanation);
        buttonDelete = findViewById(R.id.buttonDelete);
        clDeleteDialog = findViewById(R.id.clDeleteDialog);
        buttonCommitDelete = findViewById(R.id.buttonCommitDelete);
        buttonCancelDelete = findViewById(R.id.buttonCancelDelete);

        button10.setOnClickListener(this);
        button20.setOnClickListener(this);
        button100.setOnClickListener(this);
        button250.setOnClickListener(this);
        buttonAccept.setOnClickListener(this);
        buttonUndo.setOnClickListener(this);
        buttonSettings.setOnClickListener(this);
        buttonDelete.setOnClickListener(this);
        buttonCommitDelete.setOnClickListener(this);
        buttonCancelDelete.setOnClickListener(this);

        buttonUndo.setVisibility(View.GONE);
        buttonAccept.setVisibility(View.GONE);
        tvMinus.setVisibility(View.GONE);
        clDeleteDialog.setZ(10);
        clDeleteDialog.setVisibility(View.GONE);

        progressBarRed.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        tvBalance.setVisibility(View.GONE);
        button10.setVisibility(View.GONE);
        button100.setVisibility(View.GONE);
        button250.setVisibility(View.GONE);

        if(!loadingCompleted) {
            loadData();     //Läd Konto
        }

        if(AccountV2.getInstance() == null) {
            startActivity(new Intent(this, CreateAmountActivity.class));
            finish();
        } else {
            myAccount = AccountV2.getInstance();

            progressBarRed.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            tvBalance.setVisibility(View.VISIBLE);
            button10.setVisibility(View.VISIBLE);
            button100.setVisibility(View.VISIBLE);
            button250.setVisibility(View.VISIBLE);
            tvPayday.setVisibility(View.VISIBLE);

            runnable.run();

            createNotificationChannel();                //Created bei Bedarf einen Notification Channel
            startNotificationTimerNewMeat();
            startNotificationTimerReminder();
        }

    }


    @Override
    public void finish(){
        super.finish();
    }

    public void onStop() {
        saveData();
        super.onStop();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent keyEvent) {
        if(keyCode == KeyEvent.KEYCODE_BACK && clDeleteDialog.getVisibility() == View.VISIBLE) {
            clDeleteDialog.setVisibility(View.GONE);
            enableButtons(true);
            areYouSure = 0;
            return true;
        } else if(keyCode == KeyEvent.KEYCODE_BACK && actualMinus < 0) {
            actualMinus = 0;                            //ff Kopiert aus den Aktionen vom "Undo-Button"
            buttonUndo.setVisibility(View.GONE);
            buttonAccept.setVisibility(View.GONE);
            tvMinus.setVisibility(View.GONE);
            tvExplanation.setVisibility(View.VISIBLE);
            return true;
        }
        return super.onKeyDown(keyCode, keyEvent);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonAcceptMinus:
                myAccount.addMeatDay(0);                                                            //Wichtig, dass die Datenstruktur erweitert wird, bevor daysSinceLastMeat aufgerufen wird!
                if(SettingsActivity.daysSinceLastMeat() - 1 > myAccount.getDaysWithoutMeatRecord()) {                   //neuer Rekord wird festgehalten!
                    myAccount.setDaysWithoutMeatRecord(SettingsActivity.daysSinceLastMeat() - 1);
                }
                myAccount.setBalance(myAccount.getBalance() + actualMinus);
                myAccount.addMeatDay(- actualMinus);                        //Abbuchung wird in den Statistiken vermerkt!
                actualMinus = 0;
                buttonUndo.setVisibility(View.GONE);
                buttonAccept.setVisibility(View.GONE);
                tvMinus.setVisibility(View.GONE);
                tvExplanation.setVisibility(View.VISIBLE);
                draw();
                break;
            case R.id.buttonUndo:
                actualMinus = 0;
                buttonUndo.setVisibility(View.GONE);
                buttonAccept.setVisibility(View.GONE);
                tvMinus.setVisibility(View.GONE);
                tvExplanation.setVisibility(View.VISIBLE);
                break;
            case R.id.button10:
                actualMinus -= 10;
                tvMinus.setText(beautifulWeight(actualMinus));
                if(actualMinus == -10) {
                    buttonUndo.setVisibility(View.VISIBLE);
                    buttonAccept.setVisibility(View.VISIBLE);
                    tvMinus.setVisibility(View.VISIBLE);
                    tvExplanation.setVisibility(View.INVISIBLE);
                }
                break;
            case R.id.button20:
                actualMinus -= 20;
                tvMinus.setText(beautifulWeight(actualMinus));
                if(actualMinus == -20) {
                    buttonUndo.setVisibility(View.VISIBLE);
                    buttonAccept.setVisibility(View.VISIBLE);
                    tvMinus.setVisibility(View.VISIBLE);
                    tvExplanation.setVisibility(View.INVISIBLE);
                }
                break;
            case R.id.button100:
                actualMinus -= 100;
                tvMinus.setText(beautifulWeight(actualMinus));
                if(actualMinus == -100) {
                    buttonUndo.setVisibility(View.VISIBLE);
                    buttonAccept.setVisibility(View.VISIBLE);
                    tvMinus.setVisibility(View.VISIBLE);
                    tvExplanation.setVisibility(View.INVISIBLE);
                }
                break;
            case R.id.button250:
                actualMinus -= 250;
                tvMinus.setText(beautifulWeight(actualMinus));
                if(actualMinus == -250) {
                    buttonUndo.setVisibility(View.VISIBLE);
                    buttonAccept.setVisibility(View.VISIBLE);
                    tvMinus.setVisibility(View.VISIBLE);
                    tvExplanation.setVisibility(View.INVISIBLE);
                }
                break;
            case R.id.buttonSettings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case R.id.buttonDelete:
                clDeleteDialog.setVisibility(View.VISIBLE);
                enableButtons(false);
                areYouSure = 0;
                break;
            case R.id.buttonCommitDelete:
                deleteAction();
                break;
            case R.id.buttonCancelDelete:
                clDeleteDialog.setVisibility(View.GONE);
                enableButtons(true);
                areYouSure = 0;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + view.getId());
        }
    }

    private void makeToast(String text, int length) {
        if(actualToast != null) {
            actualToast.cancel();
        }
        actualToast = Toast.makeText(MainActivity.this, text,
                length);
        actualToast.show();
    }

    private void deleteAction() {
        switch(areYouSure) {
            case 0:
                makeToast(getResources().getString(R.string.sure), Toast.LENGTH_SHORT);
                areYouSure = 1;
                break;
            case 1:
                makeToast(getResources().getString(R.string.really_sure), Toast.LENGTH_SHORT);
                areYouSure = 2;
                break;
            case 2:
                AccountV2.loadAccount(null);
                startActivity(new Intent(this, CreateAmountActivity.class));
                finish();
                areYouSure = 0;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + areYouSure);
        }
    }

    private void enableButtons(boolean enabled) {
        button10.setEnabled(enabled);
        button20.setEnabled(enabled);
        button100.setEnabled(enabled);
        button250.setEnabled(enabled);
        buttonAccept.setEnabled(enabled);
        buttonUndo.setEnabled(enabled);
        buttonSettings.setEnabled(enabled);
        buttonDelete.setEnabled(enabled);
    }

    public void draw() {        //Die Else-Fälle, falls Versionscode zum Animieren zu niedrig!
        if(myAccount.getBalance() >= 0) {
            setShadowForMinus(false);
            progressBar.setVisibility(View.VISIBLE);
            progressBarRed.setVisibility(View.GONE);
            if(myAccount.getWeeklyAmount() < myAccount.getBalance()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    progressBar.setProgress(100, true);
                } else {
                    progressBar.setProgress(100);
                }
            } else {
                int balancePercent = (int) Math.round((double) myAccount.getBalance() / (double) myAccount.getWeeklyAmount() * 100);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    progressBar.setProgress(balancePercent, true);
                } else {
                    progressBar.setProgress(balancePercent);
                }
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                progressBar.setProgress(0, true);
            } else {
                progressBar.setProgress(0);
            }
            //progressBarRed.setProgress(0);
            progressBar.setVisibility(View.GONE);
            progressBarRed.setVisibility(View.VISIBLE);
            if(myAccount.getWeeklyAmount() < -myAccount.getBalance()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    progressBarRed.setProgress(100, true);
                } else {
                    progressBarRed.setProgress(100);
                }
                setShadowForMinus(true);
            } else {
                int balancePercent = (int) Math.round((double) -myAccount.getBalance() / (double) myAccount.getWeeklyAmount() * 100);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    progressBarRed.setProgress(balancePercent, true);
                } else {
                    progressBarRed.setProgress(balancePercent);
                }

                if(balancePercent > 33) {       //ab 33 Prozent bekommt die Abbuch Schrift einen Schatten!
                    setShadowForMinus(true);
                } else {
                    setShadowForMinus(false);
                }
            }

        }

        tvBalance.setText(beautifulWeight(myAccount.getBalance()));
        tvPayday.setText(paydayString());
    }

    private void setShadowForMinus(boolean shadow) {
        if(shadow) {
            tvMinus = findViewById(R.id.tvMinusNegative);
        } else {
            tvMinus = findViewById(R.id.tvMinusNegative);
        }
    }

    public int howManyPayments() {
        Calendar calendar = Calendar.getInstance();
        int currentPayments = myAccount.getPayments();
        long milliDif = calendar.getTimeInMillis() - myAccount.getCreationDateMillis();
        System.out.println("MILLIS:" + myAccount.getCreationDateMillis() + "DIESA:" + calendar.getTimeInMillis());

        if(correctTimeZone(milliDif) == -1 || correctTimeZone(milliDif) == 1) {         //Sehr umständlich um sich an Sommer und Winterzeit anzupassen!
            myAccount.changeCreationHour(correctTimeZone(milliDif));
        }
        myAccount.setPayments((int) (milliDif / 604800000));   //604800000
        return myAccount.getPayments() - currentPayments;
    }

    private int correctTimeZone(long millisPassed) {
        long millisThisWeek = millisPassed % 604800000;
        long millisThisDay = millisThisWeek % 86400000;
        int expectedHour = myAccount.getCreationDate().getHour() + ((int) millisThisDay / 3600000);
        if(expectedHour > 23) {
            expectedHour -= 24;
        }
        Calendar calendar = Calendar.getInstance();
        int actualHour = calendar.get(Calendar.HOUR_OF_DAY);
        return actualHour - expectedHour;
    }

    public void pay(int amount) {
        myAccount.setBalance(myAccount.getBalance() + amount);
        draw();
    }

    public String paydayString() {
        String result = getResources().getString(R.string.payday) + "\n";
        switch (myAccount.getCreationDate().getDayOfWeek()) {
            case 2:
                result = result + getResources().getString(R.string.monday) + ", ";
                break;
            case 3:
                result = result + getResources().getString(R.string.tuesday) + ", ";
                break;
            case 4:
                result = result + getResources().getString(R.string.wednesday) + ", ";
                break;
            case 5:
                result = result + getResources().getString(R.string.thursday) + ", ";
                break;
            case 6:
                result = result + getResources().getString(R.string.friday) + ", ";
                break;
            case 7:
                result = result + getResources().getString(R.string.saturday) + ", ";
                break;
            case 1:
                result = result + getResources().getString(R.string.sunday) + ", ";
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + myAccount.getCreationDate().getDayOfWeek());
        }
        return result + myAccount.getCreationDate().getHour() + ":00";
    }

    public void startNotificationTimerNewMeat() {
        Intent intent;
        PendingIntent pendingIntent;
        if(getResources().getString(R.string.monday).equals("Montag")) {                            //Ich bin lost und weiß nicht wie ich es sonst differenzieren soll...
            intent = new Intent(MainActivity.this, NewMeatBroadcastDE.class);
            pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, 0);
        } else {
            intent = new Intent(MainActivity.this, NewMeatBroadcastEN.class);
            pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, 0);
        }


        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        long currentMillis = System.currentTimeMillis();

        alarmManager.set(AlarmManager.RTC_WAKEUP,
                currentMillis + millisTilNextPay(),
                pendingIntent);

    }

    private long millisTilNextPay() {
        Calendar calendar = Calendar.getInstance();
        long milliDif = calendar.getTimeInMillis() - myAccount.getCreationDateMillis();

        return 604800000 - (milliDif % 604800000);
    }

    public void startNotificationTimerReminder() {
        Intent intent;
        PendingIntent pendingIntent;
        if(getResources().getString(R.string.monday).equals("Montag")) {                            //Ich bin lost und weiß nicht wie ich es sonst differenzieren soll...
            intent = new Intent(MainActivity.this, ReminderBroadcastDE.class);
            pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, 0);
        } else {
            intent = new Intent(MainActivity.this, ReminderBroadcastEN.class);
            pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, 0);
        }


        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        long currentMillis = System.currentTimeMillis();

        alarmManager.set(AlarmManager.RTC_WAKEUP,
                currentMillis + REMINDER_TIME,
                pendingIntent);
    }

    private void createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //Channel 1:
            CharSequence name = "NewMeatChannel";
            String description = getResources().getString(R.string.new_meat_channel_describtion);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("flyingJannis_newMeat", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

            //Channel 2 (notwendig?):
            CharSequence name2 = "ReminderChannel";
            String description2 = "Channel to be reminded of the stats";
            int importance2 = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel2 = new NotificationChannel("flyingJannis_reminder", name2, importance2);
            channel2.setDescription(description2);

            NotificationManager notificationManager2 = getSystemService(NotificationManager.class);
            notificationManager2.createNotificationChannel(channel2);
        }
    }


    public static String beautifulNumber(int number) {
        String result = "" + number;
        if(number >= 1000000 || number <= -1000000) {
            result = result.substring(0, result.length() - 6) + "." + result.substring(result.length() - 6, result.length() - 3) + "."
                    + result.substring(result.length() - 3);
        } else if(number >= 1000 || number <= -1000) {
            result = result.substring(0, result.length() - 3) + "." + result.substring(result.length() - 3);
        }
        return result;
    }

    public static String beautifulWeight(int number) {
        return beautifulNumber(number) + "g";
    }



    public void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(AccountV2.getInstance());
        editor.putString("account", json);
        editor.apply();

        //Speicher TutorialsReceived:
        String jsonTut = gson.toJson(TutorialsReceived.getInstance());
        editor.putString("tutorialsReceived", jsonTut);
        editor.apply();

    }

    public void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("account", null);
        Type type = new TypeToken<AccountV2>() {}.getType();
        AccountV2 loadedAccount = gson.fromJson(json, type);
        if(loadedAccount != null && loadedAccount.getWeeks()[0].getDays().length == 7) {                    //erkennt, dass noch ein alter Account verwendet wird! Behebt Problem!
            type = new TypeToken<Account>() {}.getType();
            Account oldAccount = gson.fromJson(json, type);
            loadedAccount = AccountV2.transformAccount(oldAccount);
        }
        AccountV2.loadAccount(loadedAccount);
        //myAccount kann null sein, wenn noch nichts gespeichert wurde!

        //Lade TutorialsReceived:
        String jsonTut = sharedPreferences.getString("tutorialsReceived", null);
        Type typeTut = new TypeToken<TutorialsReceived>() {}.getType();
        TutorialsReceived loadedTutorialsReceived = gson.fromJson(jsonTut, typeTut);
        if(loadedTutorialsReceived == null) {
            loadedTutorialsReceived = new TutorialsReceived();
            if(loadedAccount != null) {
                loadedTutorialsReceived.setAllDone();
            }
        }
        TutorialsReceived.loadTutorialsReceived(loadedTutorialsReceived);

        loadingCompleted = true;
    }

}
