package com.prer;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.os.Bundle;
import android.content.Intent;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import java.io.IOException;

public class Patients extends ActionBarActivity  {
    String username;
    int docID;
    SharedPreferences logPrefs;
    JSONArray json;
    ListView listView;
    PatientAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        JSONAsyncTask task = new JSONAsyncTask();
        // Execute task that grabs all patients
        task.execute(new String[] { "http://54.191.98.90/api/ios_connect/getAllDoctors.php" });
//        task.execute(new String[] { "http://54.191.98.90/api/ios_connect/getAllPatients.php" });
    }

    private class JSONAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            try {
                for (String url: urls) {
                    HttpGet httpget = new HttpGet(url);
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpResponse response = httpclient.execute(httpget);
                    String result = EntityUtils.toString(response.getEntity());
                    return result;
                }
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String result) {
            logPrefs = getSharedPreferences("loginDetails", 0);
            username = logPrefs.getString("username", null);
            docID = logPrefs.getInt("docID", -1);

            try {
                json = new JSONArray(result);
                setContentView(R.layout.activity_patients);
                listView = (ListView) findViewById(R.id.listView);
                adapter = new PatientAdapter(Patients.this, json, username, logPrefs);

                for(int i =0; i < json.length(); i++) {
                    adapter.getView(i, null, null);
                }
                listView.setAdapter(adapter);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            Button logout = (Button) findViewById(R.id.logout);
            logout.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    SharedPreferences.Editor editor = logPrefs.edit();
                    editor.clear();
                    editor.commit();
                    username = null;
                    Toast.makeText(Patients.this, "Logged Out", Toast.LENGTH_SHORT).show();

                    Intent myIntent = new Intent(view.getContext(), Login.class);
                    startActivityForResult(myIntent, 0);
                }
            });

            Button back = (Button) findViewById(R.id.back_button);
            back.setOnClickListener(new View.OnClickListener() {

                public void onClick(View view) {
                    Intent myIntent = new Intent(view.getContext(), Profile.class);
                    startActivityForResult(myIntent, 0);
                }
            });
        }
    }
}