package com.turkidroid.devdash.activity;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.turkidroid.devdash.R;
import com.turkidroid.devdash.receiver.WidgetProvider;

public class MainActivity extends AppCompatActivity {

    private EditText mEditText;
    private Button mUpdateButton;
    private TextView mAboutAppTextView, mAboutDevTextView;
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

        mAboutAppTextView = (TextView) findViewById(R.id.tv_about_app);
        mAboutAppTextView.setText(getString(R.string.tv_about_app, getVersion(this)));

        mAboutDevTextView = (TextView) findViewById(R.id.tv_about_dev);
        mAboutDevTextView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void updateWidget() {
        if(mEditText == null || mEditText.getText().toString() == null ||
                mEditText.getText().toString().isEmpty()) {
            //Toast.makeText(this, "No packages were specified!", Toast.LENGTH_SHORT).show();
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

    public String getVersion(Context context) {
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            return pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return "0";
        }
    }
}
