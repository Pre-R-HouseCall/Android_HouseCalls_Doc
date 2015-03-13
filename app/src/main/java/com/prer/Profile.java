package com.prer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Winifred on 2/27/2015.
 */
public class Profile extends ActionBarActivity {
    String firstName;
    String lastName;
    String description;
    String availability;
    int docID;
    Switch status;

    EditText FirstName;
    EditText LastName;
    EditText Description;

    HttpPost httppost;
    HttpClient httpclient;
    List<NameValuePair> nameValuePairs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        FirstName = (EditText) findViewById(R.id.FirstName);
        LastName = (EditText) findViewById(R.id.LastName);
        Description = (EditText) findViewById(R.id.Description);
        status = (Switch) findViewById(R.id.status);

        SharedPreferences logPrefs = getSharedPreferences("loginDetails", 0);
        docID = logPrefs.getInt("docID", -1);

        SharedPreferences profileDetails = getSharedPreferences("profileDetails", 0);
        firstName = profileDetails.getString("firstName", null);

        if (firstName != null) {
            lastName = profileDetails.getString("lastName", null);
            description = profileDetails.getString("description", null);
            availability = profileDetails.getString("availability", null);

            FirstName.setText(firstName);
            LastName.setText(lastName);
            Description.setText(description);

            if (availability.equals("Available")) {
                status.setChecked(true);
            } else {
                status.setChecked(false);
            }
        }

        status.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    availability = "Available";
                } else {
                    availability = "Busy";
                }
            }
        });

        Button back = (Button) findViewById(R.id.profile_back_button);
        back.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), Tutorial.class);
                startActivityForResult(myIntent, 0);
            }
        });

        Button done = (Button) findViewById(R.id.done_button);
        done.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                new Thread(new Runnable() {
                    public void run() {
                        profile();
                    }
                }).start();
            }
        });
    }

    void profile() {
        try {

            firstName = FirstName.getText().toString().trim();
            lastName = LastName.getText().toString().trim();
            description = Description.getText().toString().trim();

            httpclient=new DefaultHttpClient();
            // WRITE A SCRIPT AND PASS IT "docId" TO SEND THE FORM ONLY TO THAT DOCTOR
            httppost= new HttpPost("http://54.191.98.90/api/test1/update_status.php"); // make sure the url is correct.
            //add your data
            nameValuePairs = new ArrayList<NameValuePair>(2);
            // Always use the same variable name for posting i.e the android side variable name and php side variable name should be similar,
            nameValuePairs.add(new BasicNameValuePair("firstName", firstName));  // $Edittext_value = $_POST['Edittext_value'];
            nameValuePairs.add(new BasicNameValuePair("lastName", lastName));
            nameValuePairs.add(new BasicNameValuePair("description", description));
            nameValuePairs.add(new BasicNameValuePair("docID", String.valueOf(docID)));
            nameValuePairs.add(new BasicNameValuePair("availability", availability));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            //Execute HTTP Post Request
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            final String response = httpclient.execute(httppost, responseHandler);
            System.out.println(response);

            SharedPreferences pref = getSharedPreferences("profileDetails", 0);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("firstName", firstName);
            editor.putString("lastName", lastName);
            editor.putString("description", description);
            editor.putString("availability", availability);
            editor.commit();

            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(Profile.this, "Profile Sent", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Profile.this, Patients.class));
                }
            });

        } catch(Exception e) {
            System.out.println("Exception : " + e.getMessage());
        }
    }
}
