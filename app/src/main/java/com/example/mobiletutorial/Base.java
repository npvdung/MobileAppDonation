package com.example.mobiletutorial;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;

import com.example.main.DonationApp;

public class Base extends AppCompatActivity {
    public DonationApp donationApp;

    public Base() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        donationApp = (DonationApp) getApplication();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        donationApp.dbManager.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem report = menu.findItem(R.id.menuReport);
        MenuItem donate = menu.findItem(R.id.menuDonate);
        MenuItem reset = menu.findItem(R.id.menuReset);

        if (donationApp.donations.isEmpty()) {
            report.setEnabled(false);
            reset.setEnabled(false);
        } else {
            report.setEnabled(true);
            reset.setEnabled(true);
        }

        if (this instanceof MainActivity) {
            donate.setVisible(false);
            if (!donationApp.donations.isEmpty()) {
                report.setEnabled(true);
                reset.setEnabled(true);
            }
        } else {
            report.setVisible(false);
            donate.setVisible(true);
            reset.setVisible(false);
        }
        return true;
    }

    public void report(MenuItem menuItem) {
        startActivity(new Intent(this, Report.class));
    }

    public void donate(MenuItem menuItem) {
        startActivity(new Intent(this, MainActivity.class));
    }

    public void reset(MenuItem menuItem) {

    }
}
