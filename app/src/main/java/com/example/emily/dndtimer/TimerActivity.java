package com.example.emily.dndtimer;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class TimerActivity extends AppCompatActivity {

    //region Constants
    static final String TIME = "1:00";
    static final long INITIAL_TIME = 60000; // 1 Minute
    static final long INTERVAL = 1000; //1 Second
    //endregion

    //region Global Variables
    //preference settings
    private long initialTime;
    private int initialTimeSeconds;

    private TextView txtPlayerName;
    //private TextView txtNextPlayer;
    private ListView lstPlayerList;
    private ListView lstGraveyard;
    private TextView txtCountdownTimer;
    private Button btnStart;
    private Button btnPause;
    private Button btnResume;
    private Button btnSkip;
    private Button btnDied;
    private ProgressBar pgbCountdownBar;

    private ArrayList<String> playerList;
    private ArrayList<String> graveyardList;
    private String currentPlayer;
    private boolean playerDied = false;
    private ArrayAdapter<String> playerListAdapter;
    private ArrayAdapter<String> graveyardListAdapter;
    //Timer Variables
    private boolean isPaused = true;
    private boolean isCancelled = false;
    private long timeRemaining;
//endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        initializeViews();
        initializeLists();

        SetNextPlayer();
    }

    @Override
    protected void onResume() {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        int timerStyle = prefs.getInt("timer_style", 0);
        initialTime = prefs.getLong("timer_time", INITIAL_TIME);
        initialTimeSeconds = (int) (initialTime / INTERVAL);
        UpdateTimer(initialTime);

        int upcomingPlayer = prefs.getInt("upcoming_players", 0);
        int graveyardPlayer = prefs.getInt("graveyard_players", 0);

        setTimerStyle(timerStyle);
        setPlayerSettings(upcomingPlayer, graveyardPlayer);

        super.onResume();
    }

    @Override
    protected void onPause() {
        PauseTimer();
        super.onPause();
    }

    //region Listeners
    private View.OnClickListener btnPlayerDeathListener = new View.OnClickListener() {
        public void onClick(View view) {
            playerDied = true;
            SetNextPlayer();
        }
    };

    private View.OnClickListener btnStartTimerListener = new View.OnClickListener() {
        public void onClick(View view) {
            btnPause.setVisibility(View.VISIBLE);
            btnStart.setVisibility(View.INVISIBLE);

            StartTimer();
        }
    };

    private View.OnClickListener btnPauseTimerListener = new View.OnClickListener() {
        public void onClick(View view) {
            btnResume.setVisibility(View.VISIBLE);
            btnPause.setVisibility(View.INVISIBLE);

            PauseTimer();
        }
    };

    private View.OnClickListener btnResumeTimerListener = new View.OnClickListener() {
        public void onClick(View view) {
            btnPause.setVisibility(View.VISIBLE);
            btnResume.setVisibility(View.INVISIBLE);

            ResumeTimer();
        }
    };

    private View.OnClickListener btnNextPlayerListener = new View.OnClickListener() {
        public void onClick(View view) {
            btnPause.setVisibility(View.INVISIBLE);
            btnResume.setVisibility(View.INVISIBLE);
            btnStart.setVisibility(View.VISIBLE);

            ResetTimer();
        }
    };

//endregion

    //region timer methods
    private void StartTimer() {
        Log.d("TimerActivity", "Starting Timer");

        isPaused = false;
        isCancelled = false;

        new CountDownTimer(initialTime, INTERVAL) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (isPaused || isCancelled) {
                    cancel(); //User selected Pause or Skip
                } else {
                    UpdateTimer(millisUntilFinished);
                    timeRemaining = millisUntilFinished;
                }
            }

            @Override
            public void onFinish() {
                btnPause.setEnabled(false);
            }

        }.start();

    }

    private void PauseTimer() {
        Log.d("TimerActivity", "Pausing Timer");

        isPaused = true;
    }

    private void ResumeTimer() {
        Log.d("TimerActivity", "Resuming Timer");

        isPaused = false;
        isCancelled = false;

        long initialTime = timeRemaining;

        new CountDownTimer(initialTime, INTERVAL) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (isPaused || isCancelled) {
                    cancel(); //User selected Pause or Skip
                } else {
                    UpdateTimer(millisUntilFinished);
                    timeRemaining = millisUntilFinished;
                }
            }

            @Override
            public void onFinish() {
                btnPause.setEnabled(false);
            }

        }.start();

        isPaused = false;
    }

    private void ResetTimer() {
        Log.d("TimerActivity", "Resetting Timer");

        UpdateTimer(initialTime);
        SetNextPlayer();

        isCancelled = true;
    }

    private void UpdateTimer(long millisUntilFinished) {

        int secondsRemaining = (int) (millisUntilFinished / INTERVAL);
        String timeRemaining = secondsRemaining / 60 + ":" + checkDigit(secondsRemaining % 60);

        if (txtCountdownTimer.getVisibility() == View.VISIBLE) {
            txtCountdownTimer.setText(timeRemaining);

            if (secondsRemaining <= 10)
                txtCountdownTimer.setTextColor(Color.RED);
        }

        if (pgbCountdownBar.getVisibility() == View.VISIBLE) {
            pgbCountdownBar.setProgress((secondsRemaining * 100) / initialTimeSeconds);

            if (secondsRemaining <= 10)
                pgbCountdownBar.getProgressDrawable().setColorFilter(Color.RED, android.graphics.PorterDuff.Mode.MULTIPLY);
        }
    }

    private String checkDigit(long number) {
        return number < 10 ? "0" + number : String.valueOf(number);
    }
//endregion

    //region setting/style methods
    private void setTimerStyle(int timerStyle) {
        switch (timerStyle) {
            case 0: //We want both countdown timer and progress bar
                txtCountdownTimer.setVisibility(View.VISIBLE);
                pgbCountdownBar.setVisibility(View.VISIBLE);
                break;

            case 1: //We only want Countdown Timer
                txtCountdownTimer.setVisibility(View.VISIBLE);
                pgbCountdownBar.setVisibility(View.INVISIBLE);
                break;

            case 2: //we only want Progress bar
                txtCountdownTimer.setVisibility(View.INVISIBLE);
                pgbCountdownBar.setVisibility(View.VISIBLE);
                break;

            default:
                Log.d("TimerActivity", "Unknown timer style");
        }
    }

    private void setPlayerSettings(int upcomingPlayers, int graveyardPlayers) {
        switch (upcomingPlayers) {
            case 0: //We want to see all players

                break;

            case 1: //We only want next player

                break;

            case 2: //we only want next 2 players

                break;

            case 5: //we only want next 5 players

                break;

            default:
                Log.d("TimerActivity", "Unknown number of upcoming players");
        }

        switch (graveyardPlayers) {
            case 0: //We want to see all players

                break;

            case 1: //We only want 1 player

                break;

            case 2: //we only want 2 players

                break;

            case 5: //we only want 5 players

                break;

            default:
                Log.d("TimerActivity", "Unknown number of upcoming players");
        }
    }
    //endregion

    //region Initialization Methods
    private void initializeViews() {
        txtPlayerName = findViewById(R.id.txtPlayerName);
        lstPlayerList = findViewById(R.id.lstNextPlayer);
        txtCountdownTimer = findViewById(R.id.txtCountdownTimer);
        btnStart = findViewById(R.id.btnStart);
        btnPause = findViewById(R.id.btnPause);
        btnResume = findViewById(R.id.btnResume);
        btnSkip = findViewById(R.id.btnSkip);
        btnDied = findViewById(R.id.btnDead);
        pgbCountdownBar = findViewById(R.id.pgbCountdownBar);

        //set initial visibilities
        btnStart.setVisibility(View.VISIBLE);
        btnPause.setVisibility(View.INVISIBLE);
        btnResume.setVisibility(View.INVISIBLE);
        txtCountdownTimer.setText(TIME);

        //Set button Listeners
        btnStart.setOnClickListener(btnStartTimerListener);
        btnPause.setOnClickListener(btnPauseTimerListener);
        btnResume.setOnClickListener(btnResumeTimerListener);
        btnSkip.setOnClickListener(btnNextPlayerListener);
        btnDied.setOnClickListener(btnPlayerDeathListener);
    }

    private void initializeLists() {
        playerList = new ArrayList<>();
        graveyardList = new ArrayList<>();

        setTestingData();

        //playerList.addAll(getIntent().getStringArrayListExtra("PlayerList"));
        //txtNextPlayer.setText(playerList.get(1));

        //set adapters
        playerListAdapter = new ArrayAdapter<>(this, R.layout.timer_list_text, playerList);
        lstPlayerList.setAdapter(playerListAdapter);

        graveyardListAdapter = new ArrayAdapter<>(this, R.layout.timer_list_text, graveyardList);
        lstPlayerList.setAdapter(graveyardListAdapter);
    }
    //endregion

    private void SetNextPlayer() {

        if (currentPlayer != null && !currentPlayer.equals("") && !playerDied)
            playerList.add(currentPlayer);

        if (playerDied) {
            graveyardList.add(currentPlayer);
            playerDied = false;
        }

        currentPlayer = playerList.get(0);
        txtPlayerName.setText(currentPlayer);
        playerList.remove(currentPlayer);
        playerListAdapter.notifyDataSetChanged();
        graveyardListAdapter.notifyDataSetChanged();
    }

    private void setTestingData() {
        playerList.add("Chuck Norris");
        playerList.add("Bruce Wayne");
        playerList.add("Tony Stark");
        playerList.add("Charles Xavier");
    }

}