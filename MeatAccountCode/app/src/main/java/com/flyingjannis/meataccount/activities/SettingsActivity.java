package com.flyingjannis.meataccount.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.flyingjannis.meataccount.R;
import com.flyingjannis.meataccount.model.Account;
import com.flyingjannis.meataccount.model.AccountV2;
import com.flyingjannis.meataccount.model.RepeatListener;
import com.flyingjannis.meataccount.model.TutorialsReceived;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.Calendar;

import static android.view.View.GONE;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {
    public final static boolean PRO_VERSION = false;

    public final static int MEAT_WEEK_EU = 1245;
    public final static double CO2_PER_KILO = 5.49;
    public final static double CO2_PER_KM = 0.15;
    public final static double LITER_PER_KILO = 7633; //Ausgerechnet mit jeweiligen Anteilen und Wasserverbrauch der Tierarten
    public final static double LITER_PER_TRUCK = 30000; //DUMMY!!!

    private AccountV2 myAccount;

    private AdView mAdView;

    private ConstraintLayout clFunFact;
    private LinearLayout llMakeSureButtons;
    private GraphView graphView;
    private TextView tvLastMonthNumber;
    private TextView tvAverageDayNumber;
    private Button buttonAmountUp;
    private Button buttonAmountDown;
    private Button button3Month;
    private Button buttonYear;
    private Button buttonTotal;
    private Button button100Less;
    private Button buttonAcceptGiveUp;
    private Button buttonCancelGiveUp;
    private TextView tvWeeklyAmount;
    private TextView tvNoStats;
    private TextView tvFact;
    private TextView tvBalanceSettings;
    private TextView tvActualMeetWeek;
    private TextView tvLastMeat;
    //private ImageView ivFingerTap;
    private Button buttonGetCode;
    private ScrollView svStats;
    private ConstraintLayout clCodeDialog;
    private Button buttonCopyCode;
    private TextView tvGeneratedCode;
    private TextView tvRecordNoMeatNumber;
    private ConstraintLayout clMeatLastMonth;

    private boolean statsAvailable = false;
    private int graphState = 2; //0 = 3 Monate, 1 = Jahr, 2 = Total
    private int factSwitch = 0;
    private boolean acceptMode = false;
    private Toast actualToast;
    private String accountCode;

    //TODO: Record ggf. aktualisieren und anzeigen!
    //TODO: Monats Facts!

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        mAdView = findViewById(R.id.adView);
        if(PRO_VERSION) {
            mAdView.setVisibility(GONE);
        } else {
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
        }

        if(!MainActivity.loadingCompleted) {
            loadData();
        }
        setTitle(getResources().getString(R.string.statistics));

        myAccount = AccountV2.getInstance();
        myAccount.addMeatDay(0);            //Statistik wird geupdated! (0 wird hinzugefügt)

        svStats = findViewById(R.id.svStats);
        clFunFact = findViewById(R.id.clFunFact);
        llMakeSureButtons = findViewById(R.id.llMakeSureButtons);
        graphView = findViewById(R.id.graphView);
        tvLastMonthNumber = findViewById(R.id.tvLastMonthNumber);
        tvAverageDayNumber = findViewById(R.id.tvAverageDayNumber);
        buttonAmountDown = findViewById(R.id.buttonAmountDown);
        buttonAmountUp = findViewById(R.id.buttonAmountUp);
        button3Month = findViewById(R.id.button3Month);
        buttonYear = findViewById(R.id.buttonYear);
        buttonTotal = findViewById(R.id.buttonTotal);
        buttonAcceptGiveUp = findViewById(R.id.buttonAcceptGiveUp);
        buttonCancelGiveUp = findViewById(R.id.buttonCancelGiveUp);
        tvWeeklyAmount = findViewById(R.id.tvWeeklyAmount);
        tvNoStats = findViewById(R.id.tvNoStats);
        tvFact = findViewById(R.id.tvFact);
        tvBalanceSettings = findViewById(R.id.tvBalanceSettings);
        tvActualMeetWeek = findViewById(R.id.tvActualMeetWeek);
        button100Less = findViewById(R.id.button100Less);
        tvLastMeat = findViewById(R.id.tvLastMeat);
        //ivFingerTap = findViewById(R.id.ivFingerTap);
        buttonGetCode = findViewById(R.id.buttonGetCode);
        clCodeDialog = findViewById(R.id.clCodeDialog);
        buttonCopyCode = findViewById(R.id.buttonCopyCode);
        tvGeneratedCode = findViewById(R.id.tvGeneratedCode);
        tvRecordNoMeatNumber = findViewById(R.id.tvRecordNoMeatNumber);
        clMeatLastMonth = findViewById(R.id.clMeatLastMonth);

        clCodeDialog.setVisibility(GONE);

        buttonAmountDown.setOnTouchListener(new RepeatListener(400, 100, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(myAccount.getWeeklyAmount() >= 20) {
                    myAccount.setWeeklyAmount(myAccount.getWeeklyAmount() - 10);
                    tvWeeklyAmount.setText(MainActivity.beautifulWeight(myAccount.getWeeklyAmount()));
                    //saveData();
                } else {
                    makeToast(getResources().getString(R.string.vegetarian), Toast.LENGTH_SHORT);
                }
                if(statsAvailable) {
                    updateGraph();
                }
            }
        }));
        buttonAmountUp.setOnTouchListener(new RepeatListener(400, 100, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(myAccount.getWeeklyAmount() <= 990) {
                    myAccount.setWeeklyAmount(myAccount.getWeeklyAmount() + 10);
                    tvWeeklyAmount.setText(MainActivity.beautifulWeight(myAccount.getWeeklyAmount()));
                    //saveData();
                } else {
                    makeToast(getResources().getString(R.string.calm_down), Toast.LENGTH_SHORT);
                }
                if(statsAvailable) {
                    updateGraph();
                }
            }
        }));
        clFunFact.setOnClickListener(this);
        button3Month.setOnClickListener(this);
        buttonYear.setOnClickListener(this);
        buttonTotal.setOnClickListener(this);
        button100Less.setOnClickListener(this);
        buttonAcceptGiveUp.setOnClickListener(this);
        buttonCancelGiveUp.setOnClickListener(this);
        buttonGetCode.setOnClickListener(this);
        buttonCopyCode.setOnClickListener(this);

        updateBalance();                                    //Setzt den aktuellen Kontostand
        double averagePerDay = averagePerDay();
        tvActualMeetWeek.setText(MainActivity.beautifulWeight((int) (averagePerDay * 7)));

        button3Month.setVisibility(GONE);
        buttonYear.setVisibility(GONE);
        buttonTotal.setVisibility(GONE);
        llMakeSureButtons.setVisibility(GONE);
        unselectAll();


        if(myAccount.getPayments() > 52) {                   //über ein Jahr sind vergangen
            button3Month.setVisibility(View.VISIBLE);
            buttonYear.setVisibility(View.VISIBLE);
            buttonTotal.setVisibility(View.VISIBLE);

            graphState = 0;
            button3Month.setSelected(true);
        } else if(myAccount.getPayments() > 12) {            //über 3 Monate sind vergangen
            button3Month.setVisibility(View.VISIBLE);
            buttonTotal.setVisibility(View.VISIBLE);

            graphState = 0;
            button3Month.setSelected(true);
        }                                         //unter 3 Monaten sind vergangen


        totalDataAsGraph();                         //Graphview wird initialisiert und zeigt Daten der Wochen an.

        if(averagePerDay < 100) {
            DecimalFormat df = new DecimalFormat("0.0");
            tvAverageDayNumber.setText(df.format(averagePerDay) + "g");
        } else {
            tvAverageDayNumber.setText(MainActivity.beautifulWeight((int) averagePerDay));
        }

        int averageWeekLast28Days = averageWeekLast28Days();
        if(averageWeekLast28Days >= 0) {                //Fehlercode ist -1
            tvLastMonthNumber.setText(MainActivity.beautifulWeight(averageWeekLast28Days));
        } else {
            clMeatLastMonth.setVisibility(GONE);
        }


        int daysSinceLastMeat = daysSinceLastMeat();
        if(daysSinceLastMeat >= 0) {                  //Fehlercode ist -1
            tvLastMeat.setText("" + MainActivity.beautifulNumber(daysSinceLastMeat));
            if(daysSinceLastMeat - 1 > myAccount.getDaysWithoutMeatRecord()) {
                myAccount.setDaysWithoutMeatRecord(daysSinceLastMeat - 1);
            }
        } else {
            tvLastMeat.setText("?");
        }

        tvRecordNoMeatNumber.setText("" + MainActivity.beautifulNumber(myAccount.getDaysWithoutMeatRecord()));
        tvWeeklyAmount.setText(MainActivity.beautifulWeight(myAccount.getWeeklyAmount()));

        loadFact();                                 //hier wurde entfernt, dass Facts erst nach einer Woche geladen werden.

    }

    public void onStop() {
        saveData();
        super.onStop();
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button3Month:
                if(graphState != 0) {
                    unselectAll();
                    graphState = 0;
                    button3Month.setSelected(true);
                    updateGraph();
                }
                break;
            case R.id.buttonYear:
                if(graphState != 1) {
                    unselectAll();
                    graphState = 1;
                    buttonYear.setSelected(true);
                    updateGraph();
                }
                break;
            case R.id.buttonTotal:
                if(graphState != 2) {
                    unselectAll();
                    graphState = 2;
                    buttonTotal.setSelected(true);
                    updateGraph();
                }
                break;
            case R.id.button100Less:
                if(myAccount.getBalance() >= 100) {                  //Man soll durch das Verzichten nicht ins Minus geraten können!
                    acceptMode = true;
                    changeButtons();
                    tvBalanceSettings.setText(MainActivity.beautifulWeight(myAccount.getBalance() - 100));
                } else {
                    makeToast(getResources().getString(R.string.cant_giveup), Toast.LENGTH_SHORT);
                }
                break;
            case R.id.buttonAcceptGiveUp:
                acceptMode = false;
                changeButtons();
                myAccount.setBalance(myAccount.getBalance() - 100);
                updateBalance();
                //saveData();
                break;
            case R.id.buttonCancelGiveUp:
                acceptMode = false;
                changeButtons();
                updateBalance();
                break;
            case R.id.clFunFact:
                loadFact();
                break;
            case R.id.buttonGetCode:
                clCodeDialog.setVisibility(View.VISIBLE);
                enableUnits(false);
                accountCode = AccountV2.dataToString(myAccount);
                tvGeneratedCode.setText(accountCode);
                break;
            case R.id.buttonCopyCode:
                clCodeDialog.setVisibility(GONE);
                enableUnits(true);
                ClipboardManager clipboard = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("code", accountCode);
                clipboard.setPrimaryClip(clip);
                makeToast(getResources().getString(R.string.code_copied), Toast.LENGTH_LONG);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + view.getId());
        }
    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent keyEvent) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            if(clCodeDialog.getVisibility() == View.VISIBLE) {
                clCodeDialog.setVisibility(GONE);
                enableUnits(true);
                return true;
            } else if(acceptMode) {
                acceptMode = false;
                changeButtons();
                updateBalance();
                return true;
            }
            //saveData();
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        return super.onKeyDown(keyCode, keyEvent);
    }

    private void enableUnits(boolean enabled) {
        clFunFact.setEnabled(enabled);
        button3Month.setEnabled(enabled);
        buttonYear.setEnabled(enabled);
        buttonTotal.setEnabled(enabled);
        button100Less.setEnabled(enabled);
        buttonAcceptGiveUp.setEnabled(enabled);
        buttonCancelGiveUp.setEnabled(enabled);
        buttonGetCode.setEnabled(enabled);
        buttonAmountDown.setEnabled(enabled);
        buttonAmountUp.setEnabled(enabled);

        svStats.setEnabled(enabled);

    }

    private void makeToast(String text, int length) {
        if(actualToast != null) {
            actualToast.cancel();
        }
        actualToast = Toast.makeText(SettingsActivity.this, text,
                length);
        actualToast.show();
    }


    private void changeButtons() {
        if(button100Less.getVisibility() == View.VISIBLE) {
            button100Less.setVisibility(GONE);
            llMakeSureButtons.setVisibility(View.VISIBLE);

            tvBalanceSettings.setTextColor(getResources().getColor(R.color.green));
        } else {
            button100Less.setVisibility(View.VISIBLE);
            llMakeSureButtons.setVisibility(GONE);

            tvBalanceSettings.setTextColor(getResources().getColor(R.color.numbers_in_boxes));
        }
    }

    private void updateBalance() {
        tvBalanceSettings.setText(MainActivity.beautifulWeight(myAccount.getBalance()));
    }


    private void unselectAll() {
        button3Month.setSelected(false);
        buttonYear.setSelected(false);
        buttonTotal.setSelected(false);
    }

    private void updateGraph() {        //Neu: Alle Graphen werden gelöscht und neu gezeichnet!
        graphView.removeAllSeries();
        totalDataAsGraph();
    }

    public void totalDataAsGraph() {
        graphView.setTitle(getResources().getString(R.string.graph_title));
        if(myAccount.getPayments() > 0) {
            statsAvailable = true;

            DataPoint[] average;

            DataPoint[] totalData;
            if(graphState == 0) {                       //letzten 3 Monate
                graphView.getViewport().setMinX(myAccount.getPayments() - 12);

                DataPoint[] tmp = new DataPoint[13];
                DataPoint[] data = getTotalData();
                for(int i = 0; i < 13; i++) {
                    tmp[i] = data[data.length - 13 + i];    //letzten 13 Werte (3 Monate)
                }
                totalData = reduceData(tmp);

                //Jetzt noch Average anpassen!
                average = new DataPoint[13];
                for(int i = 0; i < 13; i++) {
                    average[i] = new DataPoint(myAccount.getPayments() - 12 + i, myAccount.getWeeklyAmount());
                }
            } else if(graphState == 1) {                //Letztes Jahr
                graphView.getViewport().setMinX(myAccount.getPayments() - 52);

                DataPoint[] tmp = new DataPoint[53];
                DataPoint[] data = getTotalData();
                for(int i = 0; i < 53; i++) {
                    tmp[i] = data[data.length - 53 + i];    //letzten 53 Werte (1 Jahr)
                }
                totalData = reduceData(tmp);

                average = new DataPoint[53];
                for(int i = 0; i < 53; i++) {
                    average[i] = new DataPoint(myAccount.getPayments() - 52 + i, myAccount.getWeeklyAmount());
                }
            } else if(graphState == 2) {                //Total
                graphView.getViewport().setMinX(0);

                totalData = reduceData(getTotalData());
                average = new DataPoint[myAccount.getPayments() + 1];
                for(int i = 0; i < myAccount.getPayments() + 1; i++) {
                    average[i] = new DataPoint(i, myAccount.getWeeklyAmount());
                }
            } else {
                throw new RuntimeException("graphState has to be one of 0, 1 or 2!");
            }

            LineGraphSeries<DataPoint> averageLine = new LineGraphSeries<>(average);
            averageLine.setColor(getResources().getColor(R.color.green));
            averageLine.setThickness(10);


            LineGraphSeries<DataPoint> series = new LineGraphSeries<>(totalData);
            series.setColor(getResources().getColor(R.color.secondary_color));
            series.setDrawDataPoints(true);
            series.setDataPointsRadius(1);

            DataPoint[] tmp = {new DataPoint(myAccount.getPayments(), 0)};   //Setzt ans Ende des Graphen einen Punkt, damit bis 0 skaliert wird auf der y-Achse
            graphView.addSeries(new LineGraphSeries<DataPoint>(tmp));
            graphView.addSeries(averageLine);
            graphView.addSeries(series);

            graphView.getGridLabelRenderer().setGridColor(getResources().getColor(R.color.decent_white));
            graphView.setTitleColor(getResources().getColor(R.color.white));
            graphView.getGridLabelRenderer().setHorizontalLabelsColor(getResources().getColor(R.color.white));
            graphView.getGridLabelRenderer().setVerticalLabelsColor(getResources().getColor(R.color.white));

            if(myAccount.getPayments() < 4) {
                graphView.getGridLabelRenderer().setNumHorizontalLabels(myAccount.getPayments() + 1);
            } else {
                //graphView.getGridLabelRenderer().setNumHorizontalLabels(3);
            }

            graphView.getViewport().setXAxisBoundsManual(true);
            graphView.getViewport().setMaxX(myAccount.getPayments());

            graphView.getViewport().setYAxisBoundsManual(true);         //Passt Y-Achse an
            graphView.getViewport().setMaxY(getBorder(totalData));
            //if((getBorder(totalData) / myAccount.weeklyAmount) <= 6) {
            //    graphView.getGridLabelRenderer().setNumVerticalLabels(getBorder(totalData) / myAccount.weeklyAmount);
            //}

            graphView.getGridLabelRenderer().setHorizontalAxisTitle(getResources().getString(R.string.weeks));
            graphView.getGridLabelRenderer().setHorizontalAxisTitleColor(getResources().getColor(R.color.white));

            tvNoStats.setVisibility(GONE);
        } else {
            graphView.getGridLabelRenderer().setNumHorizontalLabels(2);
            graphView.getViewport().setYAxisBoundsManual(true);
            graphView.getViewport().setMaxY(myAccount.getWeeklyAmount());
            graphView.getGridLabelRenderer().setHorizontalAxisTitle(getResources().getString(R.string.weeks));
            graphView.getGridLabelRenderer().setNumVerticalLabels(6);
        }
    }

    private int getBorder(DataPoint[] data) {
        int max = 0;            //Maximaler Wochenkonsum
        for(int i = 0; i < data.length; i++) {
            if(data[i].getY() > max) {
                max = (int) data[i].getY();
            }
        }
        int tmp = max / myAccount.getWeeklyAmount();
        return tmp * myAccount.getWeeklyAmount() + myAccount.getWeeklyAmount();
    }

    public DataPoint[] getTotalData() {
        DataPoint[] result = new DataPoint[myAccount.getPayments() + 1];     //Aktuelle Woche wird nicht miteinbezogen!
        result[0] = new DataPoint(0, myAccount.getWeeklyAmount());           //Graph startet beim festgelegtem Durchschnitt
        for(int i = 1; i < result.length; i++) {
            result[i] = new DataPoint(i, myAccount.getWeeks()[i - 1].getMeatAmount());
        }
        //saveData();
        return result;
    }

    public DataPoint[] reduceData(DataPoint[] data) {
        if(data.length > 53) {
            DataPoint[] reduced;
            if((data.length - 1) % 2 == 0) {          //gerade Länge! WENN MAN DAS ERSTE ARRAY FELD IGNORIERT!
                reduced = new DataPoint[((data.length - 1) / 2) + 1];
                reduced[0] = data[0];
                for(int i = 1; i < reduced.length; i++) {
                    int average = (int) (( data[2 * i - 1].getY() + data[2 * i].getY() ) / 2);
                    reduced[i] = new DataPoint(data[2 * i].getX(), average);
                }
            } else {                            //ungerade Länge!
                reduced = new DataPoint[((data.length - 1) / 2) + 1 + 1];
                reduced[0] = data[0];
                for(int i = 1; i < (reduced.length - 1); i++) {
                    int average = (int) (( data[2 * i - 1].getY() + data[2 * i].getY() ) / 2);
                    reduced[i] = new DataPoint(data[2 * i].getX(), average);
                }
                reduced[reduced.length - 1] = data[data.length - 1];
            }
            return reduceData(reduced);
        } else {
            return data;
        }
    }

    /**
     * NACHHER LÖSCHEN!
     * Methode müsste bei 8ter Arrays geändert werden!
     * Version 1 fertig
     */
    public static int daysSinceLastMeat() {
        AccountV2 account = AccountV2.getInstance();
        int counter = 0;
        boolean foundMeat = false;
        boolean foundMeatAt8thPlace = false;
        int i = account.getPayments();
        while(i >= 0 && !foundMeat) {
            int j = 7;
            while(j >= 0 && !foundMeat) {
                if(account.getWeeks()[i].getDays()[j] > 0) {
                    foundMeat = true;
                    foundMeatAt8thPlace = (j == 7);
                } else {
                    if(j != 7) {
                        counter++;
                    }
                }
                j--;
            }
            i--;
        }
        if(!foundMeat) {            //Falls noch gar kein Fleisch konsumiert wurde!
            return -1;
        }
        if(foundMeatAt8thPlace) {
            counter = counter - 1;
        }
        Calendar calendar = Calendar.getInstance();
        int dayMeatWeek = ((((calendar.get(Calendar.DAY_OF_WEEK) - account.getCreationDate().getDayOfWeek()) % 7) + 7) % 7);
        boolean afterPayHour = calendar.get(Calendar.HOUR_OF_DAY) >= account.getCreationDate().getHour();
        if(dayMeatWeek == 0 && !afterPayHour) {
            counter = counter + 1;
        } else {
            counter = counter - (6 - dayMeatWeek);
        }
        return counter;
    }

    /**
     * NACHHER LÖSCHEN!
     * Methode müsste für 8ter Arrays geändert werden!
     * Version 1 fertig
     */
    public int averageWeekLast28Days() {
        if(myAccount.getPayments() >= 4) {
            Calendar calendar = Calendar.getInstance();
            int dayMeatWeek = ((((calendar.get(Calendar.DAY_OF_WEEK) - myAccount.getCreationDate().getDayOfWeek()) % 7) + 7) % 7);
            boolean afterPayHour = calendar.get(Calendar.HOUR_OF_DAY) >= myAccount.getCreationDate().getHour();
            int totalMeat28 = myAccount.getWeeks()[myAccount.getPayments() - 1].getMeatAmount() +             //Die drei letzten abgeschlossenen Wochen.
                    myAccount.getWeeks()[myAccount.getPayments() - 2].getMeatAmount() +
                    myAccount.getWeeks()[myAccount.getPayments() - 3].getMeatAmount();

            if(dayMeatWeek == 0) {
                if(afterPayHour) {
                    totalMeat28 += myAccount.getWeeks()[myAccount.getPayments() - 4].getMeatAmount();
                    if(myAccount.getPayments() > 4) {
                        totalMeat28 += myAccount.getWeeks()[myAccount.getPayments() - 5].getDays()[7];          //Der Rest von der Vorgängerwoche, falls diese existiert
                    }
                } else {
                    int[] tmpDayStamps = myAccount.getWeeks()[myAccount.getPayments()].getDays();
                    for(int i = 0; i < 7; i++) {                                                                //Der aktuelle
                        totalMeat28 += tmpDayStamps[i];
                    }
                    totalMeat28 += myAccount.getWeeks()[myAccount.getPayments() - 4].getDays()[7];
                }
            } else {
                int[] tmpDayStamps = myAccount.getWeeks()[myAccount.getPayments() - 4].getDays();
                for(int i = 7; i >= dayMeatWeek; i--) {                                     //Holt sich alle nötigen Tage vor den 3 Wochen
                    totalMeat28 += tmpDayStamps[i];
                }

                tmpDayStamps = myAccount.getWeeks()[myAccount.getPayments()].getDays();               //Updated Variable!
                for(int i = 0; i < dayMeatWeek; i++) {                                      //Holt sich alle nötigen Tage nach den drei Wochen.
                    totalMeat28 += tmpDayStamps[i];
                }
            }

            return totalMeat28 / 4;
        } else {
            return -1;
        }
    }


    /**
     * Wird die überhaupt benutzt?
     */
    public int eatenLastMonth() {
        int amount = 0;
        for(int i = myAccount.getPayments() - 4; i < myAccount.getPayments(); i++) {
            amount += myAccount.getWeeks()[i].getMeatAmount();
        }
        return amount;
    }

    /**
     * NACHHER LÖSCHEN!
     * Methode müsste für 8ter Arrays geändert werden!
     */
    public double averagePerDay() {
        int totalDays = 0;
        int totalMeat = 0;
        for(int i = 0; i < myAccount.getPayments(); i++) {
            totalMeat += myAccount.getWeeks()[i].getMeatAmount();
            totalDays += 7;
        }
        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = ((((calendar.get(Calendar.DAY_OF_WEEK) - myAccount.getCreationDate().getDayOfWeek()) % 7) + 7) % 7);

        if(myAccount.getCreationDate().getDayOfWeek() == calendar.get(Calendar.DAY_OF_WEEK) &&        //Falls der Paymenttag schon angebrochen, aber es noch kein Payment gab!
                myAccount.getCreationDate().getHour() > calendar.get(Calendar.HOUR_OF_DAY)) {
            dayOfWeek = 7;
        }
        for(int n = 0; n <= dayOfWeek; n++) {                       //Hier wird nurnoch die angebrochene Woche betrachtet!
            totalMeat += myAccount.getWeeks()[myAccount.getPayments()].getDays()[n];
            totalDays += 1;
        }
        //saveData();
        return ((double) totalMeat) / totalDays;
    }

    public void loadFact() {
        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = ((((calendar.get(Calendar.DAY_OF_WEEK) - myAccount.getCreationDate().getDayOfWeek()) % 7) + 7) % 7);
        if(calendar.get(Calendar.HOUR_OF_DAY) < myAccount.getCreationDate().getHour()) {
            dayOfWeek = (dayOfWeek + 6) % 7;                        //Falls die Zahlstunde noch nicht erreicht wurde!
        }

        int totalMeat = 0;
        for(int i = 0; i < myAccount.getPayments() + 1; i++) {
            totalMeat += myAccount.getWeeks()[i].getMeatAmount();
        }
        int totalAverageMeat = myAccount.getPayments() * MEAT_WEEK_EU + dayOfWeek * (MEAT_WEEK_EU / 7);
        if(totalAverageMeat == 0) {                                 //Das TotalAverageMeat sollte sinnvollerweise niemals 0 sein.
            totalAverageMeat = (MEAT_WEEK_EU / 7);
        }

        int lessMeat = totalAverageMeat - totalMeat;
        if(lessMeat < 0) {
            lessMeat = 0;
        }

        switch (factSwitch) {
            case 0:
                factSwitch = 1;
                setFactOne(lessMeat);
                break;
            case 1:
                factSwitch = 2;
                setFactTwo(totalMeat, totalAverageMeat);
                break;
            case 2:
                factSwitch = 3;
                setFactThree(lessMeat);
                break;
            case 3:
                factSwitch = 0;
                setFactFour(lessMeat);
                break;
            default:
                throw new IllegalStateException("case existiert nicht! Es gibt nur 0, 1, 2, 3!");
        }
    }

    private void setFactOne(int lessMeat) {
        DecimalFormat df = new DecimalFormat("0.00");
        String lessKilo;
        if(lessMeat / 1000.0 < 100) {
            lessKilo = df.format(lessMeat / 1000.0);       //Gramm wird in Kilo (2 Nachkomma Stellen) umgewandelt
        } else {
            lessKilo = MainActivity.beautifulNumber(lessMeat / 1000);    //Ohne Nachkommastellen
        }
        String str1 = getResources().getString(R.string.since_first_week) + " ";
        String strKilo = " " + getResources().getString(R.string.kilos);
        String str2 =  strKilo + " " + getResources().getString(R.string.kilos_less);
        SpannableString spString = new SpannableString(
                str1 + lessKilo + str2);
        int endOfImportantString = str1.length() + lessKilo.length() + strKilo.length();
        spString.setSpan(new RelativeSizeSpan(2.0f), str1.length(),
                endOfImportantString, 0); // set size
        spString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.accent_in_green)), str1.length(),
                endOfImportantString, 0);
        tvFact.setText(spString);
    }

    private void setFactTwo(int totalMeat, int totalAverageMeat) {
        DecimalFormat df = new DecimalFormat("0.0");

        String lessPercent = df.format((100.0 * totalMeat) / totalAverageMeat);

        String str1 = getResources().getString(R.string.thats) + " ";
        String str2 = getResources().getString(R.string.percent_average);
        SpannableString spString = new SpannableString(
                str1 + lessPercent + str2);
        int endOfImportantString = str1.length() + lessPercent.length() + 1;
        spString.setSpan(new RelativeSizeSpan(2.0f), str1.length(),
                endOfImportantString, 0); // set size
        spString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.accent_in_green)), str1.length(),
                endOfImportantString, 0);
        tvFact.setText(spString);
    }

    private void setFactThree(int lessMeat) {
        DecimalFormat df = new DecimalFormat("0.00");
        String lessKm;
        double km = (((lessMeat / 1000.0) * CO2_PER_KILO) / CO2_PER_KM);
        if(km < 1000) {
            lessKm = df.format(km);
        } else {
            lessKm = MainActivity.beautifulNumber((int) km);
        }
        String str1 = getResources().getString(R.string.means_for_emissions) + " ";
        String strKm = " " + getResources().getString(R.string.kilometres);
        String str2 = strKm + " " + getResources().getString(R.string.less_cardriving);
        SpannableString spString = new SpannableString(
                str1 + lessKm + str2);
        int endOfImportantString = str1.length() + lessKm.length() + strKm.length();
        spString.setSpan(new RelativeSizeSpan(2.0f), str1.length(),
                endOfImportantString, 0);
        spString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.accent_in_green)), str1.length(),
                endOfImportantString, 0);
        tvFact.setText(spString);
    }

    public void setFactFour(int lessMeat) {
        double trucks = ((lessMeat / 1000.0) * LITER_PER_KILO) / LITER_PER_TRUCK;
        String strTrucks;
        if(trucks < 10) {
            DecimalFormat df = new DecimalFormat("0.00");
            strTrucks = df.format(trucks);
        } else {
            strTrucks = MainActivity.beautifulNumber((int) trucks);
        }
        String str1 = getResources().getString(R.string.water_saved) + " ";
        String str2 = " " + getResources().getString(R.string.tanker_trucks);
        SpannableString spString = new SpannableString(
                str1 + strTrucks + str2);
        int endOfImportantString = str1.length() + strTrucks.length();
        spString.setSpan(new RelativeSizeSpan(2.0f), str1.length(),
                endOfImportantString, 0);
        spString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.accent_in_green)), str1.length(),
                endOfImportantString, 0);
        tvFact.setText(spString);
    }

    public void loadData() {
        /*
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("account", null);
        Type type = new TypeToken<AccountV2>() {}.getType();
        myAccount = gson.fromJson(json, type);

         */
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

        MainActivity.loadingCompleted = true;
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
        /*
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(myAccount);
        editor.putString("account", json);
        editor.apply();

         */
    }

}
