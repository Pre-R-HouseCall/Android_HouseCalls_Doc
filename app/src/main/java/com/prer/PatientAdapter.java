package com.prer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static java.lang.Integer.parseInt;

/**
 * Created by Ryan on 2/11/2015.
 */
public class PatientAdapter extends BaseAdapter{
    Context context;
    JSONArray content;
    String username;
    SharedPreferences logPrefs;

    public PatientAdapter(Context context, JSONArray content, String username, SharedPreferences logPrefs) {
        this.context = context;
        this.content = content;
        this.username = username;
        this.logPrefs = logPrefs;
    }

    @Override
    public int getCount() {
        return content.length();
    }

    @Override
    public Object getItem(int position) {
        try {
            return content.get(position);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new Object();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int id = -1;
        String str_id = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.patient_item, parent, false);
        }
        JSONObject jsonObject = null;
        TextView docName = (TextView) convertView.findViewById(R.id.name);
        TextView docDistance = (TextView) convertView.findViewById(R.id.distance);
        ImageButton form = (ImageButton) convertView.findViewById(R.id.form);
        ImageButton bio = (ImageButton) convertView.findViewById(R.id.profile);

        try {
            jsonObject = (JSONObject) content.get(position);
            docName.setText(jsonObject.getString("FirstName") + " " + jsonObject.getString("LastName"));
            docDistance.setText(jsonObject.getString("Distance"));
            str_id = jsonObject.getString("DoctorId")  ;
            id = parseInt(str_id);
            convertView.setId(id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final String finalStr_id = str_id;
        form.setOnClickListener(new View.OnClickListener() {
            Intent myIntent;
            public void onClick(View view) {
//                if (username != null) {
//                    myIntent = new Intent(context, Form.class);
//                } else {
                myIntent = new Intent(context, Profile.class);
//                }
//                myIntent.putExtra("docId", finalStr_id);
                context.startActivity(myIntent);
            }
        });

        bio.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent myIntent = new Intent(context, Profile.class);
//                myIntent.putExtra("docId", finalStr_id);
                context.startActivity(myIntent);
            }
        });

        return convertView;
    }
}