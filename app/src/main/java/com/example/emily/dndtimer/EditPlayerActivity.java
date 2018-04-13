package com.example.emily.dndtimer;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.Locale;

public class EditPlayerActivity extends AppCompatActivity implements View.OnClickListener {

    EditText playerName, playerInitiative;
    Button btnSave;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        playerName = findViewById(R.id.edit_name);
        playerInitiative = findViewById(R.id.edit_init);
        btnSave = findViewById(R.id.btn_save);
        btnSave.setOnClickListener(this);

        String name = getIntent().getStringExtra(Constants.KEY_NAME);
        int initiative = getIntent().getIntExtra(Constants.KEY_INIT, 0);

        if (name != null) playerName.setText(name);
        if (initiative != 0) playerInitiative.setText(String.format(Locale.US, "%d", initiative));
    }

    @Override
    public void onClick(View v) {
        finish();
    }

    @Override
    public void finish() {
        Intent i = new Intent();
        i.putExtra(Constants.KEY_NAME, playerName.getText().toString());
        i.putExtra(Constants.KEY_INIT, Integer.parseInt(playerInitiative.getText().toString()));
        setResult(RESULT_OK, i);
        super.finish();
    }
}
