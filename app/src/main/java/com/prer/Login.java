package com.prer;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Login extends ActionBarActivity {
    Button b;
    EditText et,pass;
    TextView tv;
    HttpPost httppost;
    StringBuffer buffer;
    HttpResponse response;
    HttpClient httpclient;
    List<NameValuePair> nameValuePairs;
    ProgressDialog dialog = null;
    String username;
    String password;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        b = (Button) findViewById(R.id.btnLogin);
        et = (EditText) findViewById(R.id.txtEmail);
        pass = (EditText) findViewById(R.id.txtPassword);
        tv = (TextView)findViewById(R.id.tv);

        b.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = ProgressDialog.show(Login.this, "",
                        "Validating user...", true);
                new Thread(new Runnable() {
                    public void run() {
                        login();
                    }
                }).start();
            }
        });

        Button back = (Button) findViewById(R.id.login_back_button);
        back.setOnClickListener(new OnClickListener() {

            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), Tutorial.class);
                startActivityForResult(myIntent, 0);
            }
        });

        Button signup = (Button) findViewById(R.id.btnSignUp);
        signup.setOnClickListener(new OnClickListener() {

            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), SignUp.class);
                startActivityForResult(myIntent, 0);
            }
        });
    }

    void login(){
        try{
            username = et.getText().toString().trim();
            password = pass.getText().toString().trim();

            httpclient=new DefaultHttpClient();
            httppost= new HttpPost("http://54.191.98.90/api/test1/check_doc.php"); // make sure the url is correct.
            //add your data
            nameValuePairs = new ArrayList<NameValuePair>(2);
            // Always use the same variable name for posting i.e the android side variable name and php side variable name should be similar,
            nameValuePairs.add(new BasicNameValuePair("username", username));  // $Edittext_value = $_POST['Edittext_value'];
            nameValuePairs.add(new BasicNameValuePair("password", password));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            // Execute HTTP Post Request
            response=httpclient.execute(httppost);

            // Execute rest of script
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            final String response = httpclient.execute(httppost, responseHandler);
            System.out.println("Response : " + response);

            runOnUiThread(new Runnable() {
                public void run() {
                    tv.setText("Response from PHP : " + response);
                    dialog.dismiss();
                }
            });

            if(!response.equalsIgnoreCase("No Such User Found")){
                JSONObject json = new JSONObject(response);

                SharedPreferences sp = getSharedPreferences("loginDetails", 0);
                SharedPreferences.Editor spEdit = sp.edit();
                spEdit.putString("username", username);
                spEdit.putString("password", password);
                spEdit.putInt("docID", json.getInt("DoctorId"));
                spEdit.commit();

                System.out.println(json);

                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(Login.this, "Login Success", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(Login.this, Profile.class));
                    }
                });
            }else{
                showAlert();
            }

        } catch(Exception e) {
            dialog.dismiss();
            System.out.println("Exception : " + e.getMessage());
        }
    }

    public void showAlert() {
        Login.this.runOnUiThread(new Runnable() {
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
                builder.setTitle("Login Error.");
                builder.setMessage("Invalid Username/Password. Try Again.")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }
}
