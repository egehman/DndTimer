package com.example.emily.dndtimer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListView lv;
    Button btnAdd, btnStart;
    EditText playerName, playerInitiative;

    ArrayList<Player> playerList = new ArrayList<>();
    PlayerListArrayAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lv = findViewById(R.id.player_list);
        btnAdd = findViewById(R.id.btn_add);
        btnStart = findViewById(R.id.btn_start);
        playerName = findViewById(R.id.edit_name);
        playerInitiative = findViewById(R.id.edit_init);

        btnAdd.setOnClickListener(btnAddListener);
        btnStart.setOnClickListener(btnStartListener);

        playerList = getIntent().getParcelableArrayListExtra(Constants.KEY_PLAYERS);
        myAdapter = new PlayerListArrayAdapter(this, R.layout.item_row_main, playerList);
        lv.setAdapter(myAdapter);
    }

    private Player createPlayer(String name, String initiative) {
        int init = Integer.parseInt(initiative);
        return new Player(name, init);
    }

    private boolean validatePlayerInfo() {
        return !playerName.getText().toString().equals("")
                && !playerInitiative.getText().toString().equals("");
    }

    private View.OnClickListener btnAddListener = new View.OnClickListener() {
        public void onClick(View view) {
            if (validatePlayerInfo()) {
                playerList.add(createPlayer(
                        playerName.getText().toString(),
                        playerInitiative.getText().toString()));
                myAdapter.notifyDataSetChanged();
                playerName.setText(null);
                playerInitiative.setText(null);
            } else {
                Toast.makeText(getBaseContext(), "missing player info", Toast.LENGTH_LONG).show();
            }
        }
    };

    private View.OnClickListener btnStartListener = new View.OnClickListener() {
        public void onClick(View view) {
            if (playerList.isEmpty()) {
                Toast.makeText(getBaseContext(), "no players!", Toast.LENGTH_LONG).show();
            } else {
                Intent i = new Intent(MainActivity.this, TimerActivity.class);
                i.putExtra(Constants.KEY_PLAYERS, playerList);
                startActivity(i);
            }
        }
    };
}
