package com.example.meditrack;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import java.util.Calendar;

/**
 * HomeActivity.java
 */
public class HomeActivity extends Activity {
    public SQLiteHelper sqlh2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        sqlh2 = new SQLiteHelper(HomeActivity.this); //initialises instance of SQLiteHelpere
        displayMedDialog();     //displays medication reminder dialog

    }
    private void displayMedDialog() {
        final String medName = getIntent().getStringExtra(Constants.MED_NAME);  //retrieves data from intent passed
        final String medDosage = getIntent().getStringExtra(Constants.MED_DOSAGE);
        final String pcktQty = getIntent().getStringExtra(Constants.PKT_QUANTITY);
        final String qtyToTake = getIntent().getStringExtra(Constants.QTY_TO_TAKE);
        final String time1 = getIntent().getStringExtra(Constants.TIME);
        final String time2 = getIntent().getStringExtra(Constants.TIME2);

        AlertDialog.Builder b = new AlertDialog.Builder(HomeActivity.this,AlertDialog.THEME_DEVICE_DEFAULT_LIGHT); //builds AlertDialog
        b.setIcon(R.mipmap.ic_launcher);
        b.setTitle("Reminder\n\n");
        b.setMessage("Have you taken your medication?\n\nMedication Name: " + medName +
                "\nDosage: " + medDosage + "mg" + "\nRemaining: " + pcktQty + "\n\nTake: " + qtyToTake); //message that reads in variables set above
        b.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(getApplicationContext(), "Meds Taken", Toast.LENGTH_SHORT).show();

                int val1 = Integer.parseInt(pcktQty) - Integer.parseInt(qtyToTake);
                String val = Integer.toString(val1); //new variable displays value of int val1
                Toast.makeText(HomeActivity.this, "New string val: " + val, Toast.LENGTH_SHORT).show(); //Toast to test that new variable is being read

                MedicationModel medication = new MedicationModel(); //new instance of MedicationModel
                medication.setMedName(medName);
                medication.setMedDosage(medDosage);
                medication.setPktQuantity(val); //sets pktQuantity to val
                medication.setQtyToTake(qtyToTake);
                medication.setTime1(time1);
                medication.setTime2(time2);

                try{
                    sqlh2.updateQuantity(medication,val); //calls updateQuantity(medication,val) from SQLiteHelper
                    //                     sqlh2.updateRecord(medication);
                }catch (Exception e)
                {
                    e.printStackTrace();
                }

                //starts MedsActivity and closes HomeActivity
                Intent in = new Intent(HomeActivity.this,MedsActivity.class);
                startActivity(in);
                finish();

            }
        });
        b.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(HomeActivity.this,"Meds Not Taken - Alarm will Repeat",Toast.LENGTH_SHORT).show();

                Calendar calendar = Calendar.getInstance(); //gets new instance of calendar class
                Intent in = new Intent(HomeActivity.this,AlarmClass.class);
                in.putExtra(Constants.MED_NAME,medName);
                in.putExtra(Constants.MED_DOSAGE,medDosage);
                in.putExtra(Constants.PKT_QUANTITY,pcktQty);
                in.putExtra(Constants.QTY_TO_TAKE,qtyToTake);
                //passes through variables in intent to alarm class to reset notification
                AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(HomeActivity.this,0,in,0);
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),1000*60*5,pendingIntent); //call to set alarm to repeat every 5 minutes
                finish(); //closes activity
            }
        });
        b.create();
        b.show();
    }
}

