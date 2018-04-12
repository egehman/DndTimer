package com.example.emily.dndtimer;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

public class OptionsActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnNew, btnResume;
    ArrayList<Player> playerList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        btnNew = findViewById(R.id.btn_new_game);
        btnResume = findViewById(R.id.btn_resume_game);

        btnNew.setOnClickListener(this);
        btnResume.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_resume_game: {
                playerList = getPlayerList();
                break;
            }
            default: {
                playerList = new ArrayList<>();
                break;
            }
        }

        Intent i = new Intent(OptionsActivity.this, MainActivity.class);
        i.putExtra(Constants.KEY_PLAYERS, playerList);
        startActivity(i);
    }

    // todo get list of players from saved data
    private ArrayList<Player> getPlayerList() {
        ArrayList<Player> players = new ArrayList<>();
        players.add(new Player("Chuck Norris", 17));
        players.add(new Player("Bruce Wayne", 13));
        players.add(new Player("Tony Stark", 7));
        players.add(new Player("Charles Xavier", 5));
        return players;
    }
}
