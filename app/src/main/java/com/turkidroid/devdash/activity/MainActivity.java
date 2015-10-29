package com.turkidroid.devdash.activity;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.turkidroid.devdash.R;
import com.turkidroid.devdash.receiver.WidgetProvider;

public class MainActivity extends AppCompatActivity {

    private EditText mEditText;
    private Button mUpdateButton;
    public static String PREF_KEY_PACKAGES = "com.turkidroid.devdash.pref.packages";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mEditText = (EditText) findViewById(R.id.et_packages);
        mEditText.setText(getPackages());

        mUpdateButton = (Button) findViewById(R.id.btn_update);
        mUpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateWidget();
            }
        });
    }

    private void updateWidget() {
        if(mEditText == null || mEditText.getText().toString() == null ||
                mEditText.getText().toString().isEmpty()) {
            Toast.makeText(this, "No packages were specified!", Toast.LENGTH_SHORT).show();
            setPackages("");
        }
        setPackages(mEditText.getText().toString());

        Intent intentq = new Intent(this, WidgetProvider.class);
        intentq.setAction("android.appwidget.action.APPWIDGET_UPDATE");
        int ids[] = AppWidgetManager.getInstance(getApplication()).getAppWidgetIds(new ComponentName(getApplication(), WidgetProvider.class));
        intentq.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        sendBroadcast(intentq);

    }

    private String getPackages() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        return pref.getString(PREF_KEY_PACKAGES, "");
    }

    private void setPackages(String str) {
        if(str == null)
            str = "";
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(PREF_KEY_PACKAGES, str);
        editor.commit();
    }
}
