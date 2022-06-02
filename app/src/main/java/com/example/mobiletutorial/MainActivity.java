package com.example.mobiletutorial;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;

import com.example.api.DonationApi;
import com.example.mobiletutorial.databinding.ActivityMainBinding;
import com.example.models.Donation;
import com.google.android.material.snackbar.Snackbar;

import android.util.Log;
import android.view.View;

import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import java.nio.channels.AsynchronousChannelGroup;
import java.util.List;

public class MainActivity extends Base {

    private ActivityMainBinding binding;
    private Button donateButton;
    private RadioGroup paymentMethod;
    private ProgressBar progressBar;
    private NumberPicker amountPicker;
    private EditText amountText;
    private TextView amountTotal;

    private int amount; // get value of amountPicker
    private int totalAmount; // get value of amountTotal
    private String editAmount; // get value of amountText
    private int methodId; // get value of paymentMethod

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        donateButton = (Button) findViewById(R.id.donateButton);
        paymentMethod = (RadioGroup) findViewById(R.id.paymentMethod);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        amountPicker = (NumberPicker) findViewById(R.id.amountPicker);
        amountText = (EditText) findViewById(R.id.paymentAmount);
        amountTotal = (TextView) findViewById(R.id.total);

        amountPicker.setMinValue(0);
        amountPicker.setMaxValue(1000);
        progressBar.setMax(donationApp.getTarget());
        progressBar.setProgress(donationApp.getTotalDonated());
        amountTotal.setText("$" + donationApp.getTotalDonated());
    }

    @Override
    public void onResume() {
        super.onResume();
        new GetAllTask(this).execute("/donations");
    }

    public void donateButtonPressed(View view) {
        amount = amountPicker.getValue();
        String method = paymentMethod.getCheckedRadioButtonId() == R.id.PayPal ? "PayPal" : "Direct";
        editAmount = amountText.getText().toString();
        methodId = paymentMethod.getCheckedRadioButtonId() == R.id.PayPal ? R.id.PayPal : R.id.Direct;

        if (!editAmount.equals("")) {
            amount = Integer.parseInt(editAmount);
            amountPicker.setValue(0);
        }

        if (amount > 0) {
            if (!donationApp.newDonation()) {
                new InsertTask(this).execute(new Donation(method, amount, donationApp.donations.size()));
            }
        }
    }

    public void reset(MenuItem menuItem) {
        onAllDonationDelete();
    }

    private class GetAllTask extends AsyncTask<String, Void, List<Donation>> {
        protected ProgressDialog dialog;
        protected Context context;

        public GetAllTask(Context context) {
            this.context = context;
        }

        @Override
        protected List<Donation> doInBackground(String... params) {
            try {
                Log.v("Donate", "Donation App Getting all Donations");
                return (List<Donation>) DonationApi.getAll((String) params[0]);
            } catch (Exception e) {
                Log.v("Donate", "Error: " + e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.dialog = new ProgressDialog(context, 1);
            this.dialog.setMessage("Retrieving Donations List");
            this.dialog.show();
        }

        @Override
        protected void onPostExecute(List<Donation> result) {
            super.onPostExecute(result);

            donationApp.donations = result;
            int temp = 0;
            for (int i = 0;  i < result.size(); i++) {
                temp = temp + result.get(i).amount;
            }
            donationApp.setTotalDonated(temp);
            progressBar.setProgress(donationApp.getTotalDonated());
            String total = "$" + donationApp.getTotalDonated();
            amountTotal.setText(total);

            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    }

    private class InsertTask extends AsyncTask<Object, Void, String> {
        protected ProgressDialog dialog;
        protected Context context;

        public InsertTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.dialog = new ProgressDialog(context, 1);
            this.dialog.setMessage("Saving new Donation");
            this.dialog.show();
        }

        @Override
        protected String doInBackground(Object... objects) {
            try {
                Log.v("Donate", "Donation App Inserting");
                return (String) DonationApi.insert("/donations", (Donation) objects[0]);
            } catch (Exception e) {
                Log.v("Donate", "ERROR: " + e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            new GetAllTask(MainActivity.this).execute("/donations");
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    }

    private class ResetTask extends AsyncTask<Object, Void, String> {
        protected ProgressDialog dialog;
        protected Context context;

        public ResetTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.dialog = new ProgressDialog(context, 1);
            this.dialog.setMessage("Deleting Donations ...");
            this.dialog.show();
        }

        @Override
        protected String doInBackground(Object... objects) {
            String res = null;
            try {
                res = DonationApi.deleteAll((String) objects[0]);
            } catch (Exception e) {
                Log.v("Donate", "RESET ERROR: " + e);
                e.printStackTrace();
            }
            return res;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            donationApp.setTotalDonated(0);
            progressBar.setProgress(donationApp.getTotalDonated());
            amountTotal.setText("$" + donationApp.getTotalDonated());
            new GetAllTask(MainActivity.this).execute("/donations");
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    }

    public void onAllDonationDelete() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete ALL Donations?");
        builder.setIcon(android.R.drawable.ic_delete);
        builder.setMessage("Are you sure you want to Delete ALL the Donations?");
        builder.setCancelable(false);

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                new ResetTask(MainActivity.this).execute("/donations");
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
