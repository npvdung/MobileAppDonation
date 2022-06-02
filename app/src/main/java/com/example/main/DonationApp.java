package com.example.main;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import com.example.database.DBManager;
import com.example.models.Donation;

import java.util.ArrayList;
import java.util.List;

public class DonationApp extends Application {
    private final int target = 10000;
    private int totalDonated = 0;
    public List<Donation> donations = new ArrayList<Donation>();

    public int getTarget() {
        return target;
    }

    public int getTotalDonated() {
        return totalDonated;
    }

    public void setTotalDonated(int totalDonated) {
        this.totalDonated = totalDonated;
    }

    public boolean newDonation() {
        boolean targetAchieved = totalDonated > target;
        if (targetAchieved) {
            Toast.makeText(this, "Target Exceeded!", Toast.LENGTH_SHORT).show();
        }
        return targetAchieved;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        Log.v("Donate", "Donation App Started");
    }
}
