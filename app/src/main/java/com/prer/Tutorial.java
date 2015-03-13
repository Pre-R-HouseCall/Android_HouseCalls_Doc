package com.prer;

import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Tutorial extends ActionBarActivity {
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        SharedPreferences logPrefs = getSharedPreferences("loginDetails", 0);
        username = logPrefs.getString("username", null);

        Button skip = (Button) findViewById(R.id.skip_button);
        skip.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent myIntent;

                if (username != null) {
                    myIntent = new Intent(view.getContext(), Profile.class);
                } else {
                    myIntent = new Intent(view.getContext(), Login.class);
                }
                startActivityForResult(myIntent, 0);
            }
        });
    }
}
