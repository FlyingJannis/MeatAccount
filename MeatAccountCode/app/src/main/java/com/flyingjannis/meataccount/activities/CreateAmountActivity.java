package com.flyingjannis.meataccount.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.flyingjannis.meataccount.R;
import com.flyingjannis.meataccount.model.Account;
import com.flyingjannis.meataccount.model.AccountV2;
import com.flyingjannis.meataccount.model.TutorialsReceived;
import com.flyingjannis.meataccount.testing.ExampleAccounts;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.Objects;

public class CreateAmountActivity extends AppCompatActivity implements View.OnClickListener {

    private TutorialsReceived tutorialsReceived;
    private Toast actualToast;

    private TextView tvAmount;
    private SeekBar sbAmount;
    private Button buttonAccept;
    private TextView tvWelcomeSpeech;
    private ImageView ivSteak;
    private ImageView iv100;
    private ImageView iv20;
    private ImageView ivAccount;
    private ImageView ivStats;
    private ImageView ivInfo;
    private ImageView ivSteak2;
    private ImageView ivBigFingerTap;
    private EditText etLoadCode;
    private Button buttonLoadCode;

    private ConstraintLayout clWelcome;
    private int welcomeText = 1;

    private int amount = 500;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_amount);

        setTitle(R.string.create_account);

        tvAmount = findViewById(R.id.tvAmount);
        sbAmount = findViewById(R.id.sbAmount);
        buttonAccept = findViewById(R.id.buttonAccept);
        clWelcome = findViewById(R.id.clWelcome);
        tvWelcomeSpeech = findViewById(R.id.tvWelcomeSpeech);
        ivSteak = findViewById(R.id.ivSteak);
        iv100 = findViewById(R.id.iv100);
        iv20 = findViewById(R.id.iv20);
        ivAccount = findViewById(R.id.ivAccount);
        ivStats = findViewById(R.id.ivStats);
        ivInfo = findViewById(R.id.ivInfo);
        ivSteak2 = findViewById(R.id.ivSteak2);
        ivBigFingerTap = findViewById(R.id.ivBigFingerTap);
        etLoadCode = findViewById(R.id.etLoadCode);
        buttonLoadCode = findViewById(R.id.buttonLoadCode);

        clWelcome.setZ(10);
        clWelcome.setVisibility(View.GONE);


        tutorialsReceived = TutorialsReceived.getInstance();

        if(tutorialsReceived.isCreateAmountTutorialReceived()) {
            enableButtons(true);
        } else {
            Objects.requireNonNull(getSupportActionBar()).hide();
            enableButtons(false);
            clWelcome.setVisibility(View.VISIBLE);
        }

        sbAmount.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(i <= 1) {
                    amount = 10;
                } else {
                    amount = i * 10;
                }
                tvAmount.setText(MainActivity.beautifulWeight(amount));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        clWelcome.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch(motionEvent.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        ivBigFingerTap.setBackgroundResource(R.drawable.finger_tap_big_pressed);
                        break;
                    case MotionEvent.ACTION_UP:
                        ivBigFingerTap.setBackgroundResource(R.drawable.finger_tap_big);
                        nextMessage();
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        buttonAccept.setOnClickListener(this);
        buttonLoadCode.setOnClickListener(this);
    }

    @Override
    public void onStop() {
        saveData();
        super.onStop();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonAccept:
                //saveData();
                AccountV2.loadAccount(new AccountV2(amount));
                startActivity(new Intent(this, MainActivity.class));
                finish();
                break;
            case R.id.buttonLoadCode:
                try {
                    AccountV2 account = AccountV2.encodeAccount(etLoadCode.getText().toString());
                    Calendar calendar = Calendar.getInstance();
                    if(account.getCreationDateMillis() > calendar.getTimeInMillis()) {
                        throw new RuntimeException("Error: Creation Date Millis too big!");
                    }
                    AccountV2.loadAccount(account);

                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                } catch (Exception e) {
                    makeToast(getResources().getString(R.string.invalid_code), Toast.LENGTH_LONG);
                }

                break;
            default:
                throw new IllegalStateException("Unexpected value: " + view.getId());
        }
    }

    private void enableButtons(boolean enabled) {
        buttonAccept.setEnabled(enabled);
        sbAmount.setEnabled(enabled);
    }

    private void makeToast(String text, int length) {
        if(actualToast != null) {
            actualToast.cancel();
        }
        actualToast = Toast.makeText(CreateAmountActivity.this, text,
                length);
        actualToast.show();
    }

    private void nextMessage() {
        switch (welcomeText) {
            case 1:
                welcomeText++;
                ivSteak.setVisibility(View.GONE);
                ivAccount.setVisibility(View.VISIBLE);
                tvWelcomeSpeech.setText(getResources().getString(R.string.welcome2));
                break;
            case 2:
                welcomeText++;
                ivAccount.setVisibility(View.GONE);
                iv100.setVisibility(View.VISIBLE);
                iv20.setVisibility(View.VISIBLE);
                tvWelcomeSpeech.setText(getResources().getString(R.string.welcome3));
                break;
            case 3:
                welcomeText++;
                iv100.setVisibility(View.GONE);
                iv20.setVisibility(View.GONE);
                ivInfo.setVisibility(View.VISIBLE);
                ivStats.setVisibility(View.VISIBLE);
                tvWelcomeSpeech.setText(getResources().getString(R.string.welcome4));
                break;
            case 4:
                welcomeText++;
                ivInfo.setVisibility(View.GONE);
                ivStats.setVisibility(View.GONE);
                ivSteak2.setVisibility(View.VISIBLE);
                tvWelcomeSpeech.setText(getResources().getString(R.string.welcome5));
                tvWelcomeSpeech.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 40);
                break;
            case 5:
                Objects.requireNonNull(getSupportActionBar()).show();
                clWelcome.setVisibility(View.GONE);
                enableButtons(true);
                tutorialsReceived.setCreateAmountTutorialReceived(true);
                break;
            default:
                throw new IllegalStateException("Just 5 cases!");
        }
    }



    /*
    public void loadData() {
        //Läd nur TutorialsReceived:
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        Gson gson = new Gson();
        String jsonTut = sharedPreferences.getString("tutorialsReceived", null);
        Type typeTut = new TypeToken<TutorialsReceived>() {}.getType();
        tutorialsReceived = gson.fromJson(jsonTut, typeTut);
        if(tutorialsReceived == null) {
            tutorialsReceived = new TutorialsReceived();
        }
    }

     */

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

        AccountV2.loadAccount(new AccountV2(amount));


        String jsonTut = gson.toJson(tutorialsReceived);
        editor.putString("tutorialsReceived", jsonTut);
        editor.apply();

        MainActivity.loadingCompleted = true;

 */
    }
}
