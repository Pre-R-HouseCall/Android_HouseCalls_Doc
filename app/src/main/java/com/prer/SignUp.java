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

import android.support.v7.app.ActionBarActivity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SignUp extends ActionBarActivity {
    Button b;
    EditText et,pass;
    TextView tv;
    HttpPost httppost;
    StringBuffer buffer;
    HttpResponse response;
    HttpClient httpclient;
    List<NameValuePair> nameValuePairs;
    ProgressDialog dialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        b = (Button) findViewById(R.id.btnSignUp);
        et = (EditText) findViewById(R.id.txtEmail0);
        pass = (EditText) findViewById(R.id.txtPassword0);
        tv = (TextView)findViewById(R.id.tv0);

        TextView site = (TextView) findViewById(R.id.site);
        site.setText(Html.fromHtml("<a href=http://www.pre-r.com> Pre-R "));
        site.setMovementMethod(LinkMovementMethod.getInstance());

        b.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = ProgressDialog.show(SignUp.this, "",
                        "Adding user...", true);
                new Thread(new Runnable() {
                    public void run() {
                        signUp();
                    }
                }).start();
            }
        });

        Button back = (Button) findViewById(R.id.signup_back_button);
        back.setOnClickListener(new OnClickListener() {

            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), Login.class);
                startActivityForResult(myIntent, 0);
            }
        });
    }

    void signUp(){
        try{
            httpclient=new DefaultHttpClient();
            httppost= new HttpPost("http://54.191.98.90/api/test1/add_doctor.php"); // make sure the url is correct.
            //add your data
            nameValuePairs = new ArrayList<NameValuePair>(2);
            // Always use the same variable name for posting i.e the android side variable name and php side variable name should be similar,
            nameValuePairs.add(new BasicNameValuePair("username",et.getText().toString().trim()));  // $Edittext_value = $_POST['Edittext_value'];
            nameValuePairs.add(new BasicNameValuePair("password",pass.getText().toString().trim()));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            //Execute HTTP Post Request
            ResponseHandler<String> responseHandler = new BasicResponseHandler();

            final String response = httpclient.execute(httppost, responseHandler);

            System.out.println("Response : " + response);
            runOnUiThread(new Runnable() {
                public void run() {
                    tv.setText("Response from PHP : " + response);
                    dialog.dismiss();
                }
            });

            System.out.println("return: " + response.equalsIgnoreCase("Row = 1. User Found\n"));

            if(response.equalsIgnoreCase("Row = 1. User Found\n") || response.equalsIgnoreCase("Missing Required field(s)\n")){
                showAlert();
            }else{
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(SignUp.this,"SignUp Success", Toast.LENGTH_SHORT).show();
                    }
                });

                startActivity(new Intent(SignUp.this, Login.class));
            }

        }catch(Exception e){
            dialog.dismiss();
            System.out.println("Exception : " + e.getMessage());
        }
    }

    public void showAlert() {
        SignUp.this.runOnUiThread(new Runnable() {
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(SignUp.this);
                builder.setTitle("SignUp Error:");
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
