package com.example.meditrack;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;

/*
 *   MedsActivity.java
 */
public class MedsActivity extends Activity {

    Button btnAddNewRecord;
    SQLiteHelper sQLiteHelper;

    android.widget.LinearLayout parentLayout;
    LinearLayout layoutDisplayMeds;

    TextView tvNoRecordsFound;
    private String rowID = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meds);

        ActionBar actionBar = getActionBar(); //sets icon to ActionBar
        actionBar.setLogo(R.mipmap.ic_launcher);
        try {
            actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#4286f4"))); //sets ActionBar colour
        } catch (Exception e) {
            e.printStackTrace();
        }
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        //calls methods and initalises instance of SQLiteHelper
        getAllWidgets();
        sQLiteHelper = new SQLiteHelper(MedsActivity.this);
        bindWidgetsWithEvent();
        displayAllRecords();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    { //creates menu
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main,menu);
        menu.removeItem(R.id.menu_meds);        //creates menu and hides Medication option
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    { //set code for each menu option
        switch (item.getItemId())
        {
            case R.id.menu_help:    //help option
                showHelpDialog(); //calls method
                break;
            case R.id.menu_exit:    //exit option
                AlertDialog.Builder builder = new AlertDialog.Builder(MedsActivity.this,AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
                builder.setTitle("Exit");
                builder.setIcon(R.mipmap.ic_launcher);
                builder.setMessage("Are you sure you want to exit?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //exit app
                        finish();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(MedsActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.create();
                builder.show();
                break;
            case R.id.menu_delete_meds: //delete all medication option
                AlertDialog.Builder b1 = new AlertDialog.Builder(MedsActivity.this,AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
                b1.setTitle("Delete All Medication?");
                b1.setMessage("Are you sure you want to delete all medication?");
                b1.setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        sQLiteHelper.deleteAllMedication();
                        displayAllRecords();
                        Toast.makeText(getApplicationContext(),"All Medication Deleted",Toast.LENGTH_LONG).show();
                    }
                });
                b1.setNegativeButton("No", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        //do nothing
                        Toast.makeText(MedsActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
                    }
                });
                b1.create();
                b1.show();
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void showHelpDialog()
    { //help dialog to explain to a user how to work the app

        //read in helpfile - works (file is in assets folder) if any changes are made to the file it will be updated on run
        String text = "";
        try {
            InputStream is = getAssets().open("help_file.txt");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            text = new String(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }

        AlertDialog.Builder b = new AlertDialog.Builder(MedsActivity.this,AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
        b.setTitle("Help");
        b.setIcon(R.mipmap.ic_launcher);
        b.setMessage(text); //reads in text file from text variable
        b.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {

            }
        });
        b.create();
        b.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK)
        { //retrieves all data and sets them to strings
            String medName = data.getStringExtra(Constants.MED_NAME);
            String medDosage = data.getStringExtra(Constants.MED_DOSAGE);
            String pktQuantity = data.getStringExtra(Constants.PKT_QUANTITY);
            String qtyToTake = data.getStringExtra(Constants.QTY_TO_TAKE);
            String medTime1 = data.getStringExtra(Constants.TIME); //HOUR
            String medTime2 = data.getStringExtra(Constants.TIME2);//MINUTE

            MedicationModel medication = new MedicationModel(); //creates and sets data in medicationmodel object
            medication.setID(rowID); //sets ID
            medication.setMedName(medName);
            medication.setMedDosage(medDosage);
            medication.setPktQuantity(pktQuantity);
            medication.setQtyToTake(qtyToTake);
            medication.setTime1(medTime1);
            medication.setTime2(medTime2);

            if (requestCode == Constants.ADD_RECORD)
            {
                sQLiteHelper.insertRecord(medication); //calls sqlitehelper class method (insertRecord)
                setAlarm(medication); //calls method to set the alarm
            }
            else if (requestCode == Constants.UPDATE_RECORD)
            { //if request code = UPDATE
                medication.setID(rowID); //gets row ID
                sQLiteHelper.updateRecord(medication); //calls sqlitehelper class method (updateRecord)
                setAlarm(medication);
            }
            displayAllRecords(); //displays listview
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void setAlarm(MedicationModel medication)
    { //reads in time variable to set alarm using alarm class

        medication.setID(rowID); //sets rowID
        int time1 = Integer.parseInt(medication.getTime1());
        int time2 =Integer.parseInt(medication.getTime2());
        Toast.makeText(getApplicationContext(),"Time set: " + time1 + ":" + time2,Toast.LENGTH_SHORT).show(); //this code reads in variable
        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);

        Calendar cal = Calendar.getInstance(); //creates instance of calendar and sets hour and minute to read in variables
        cal.set(Calendar.HOUR_OF_DAY,time1);
        cal.set(Calendar.MINUTE,time2);
        cal.set(Calendar.SECOND,0);

        Intent intent = new Intent(MedsActivity.this,AlarmClass.class);
        intent.putExtra(Constants.MED_NAME,medication.getMedName());        //intent passed to alarm class
        intent.putExtra(Constants.MED_DOSAGE,medication.getMedDosage());
        intent.putExtra(Constants.PKT_QUANTITY,medication.getPktQuantity());
        intent.putExtra(Constants.QTY_TO_TAKE,medication.getQtyToTake());
        intent.putExtra(Constants.TIME,medication.getTime1());
        intent.putExtra(Constants.TIME2,medication.getTime2());
        setResult(RESULT_OK);
        PendingIntent broadcast = PendingIntent.getBroadcast(this,100,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,cal.getTimeInMillis(),AlarmManager.INTERVAL_DAY,broadcast); //set to repeat daily
        displayAllRecords();
    }

    private void getAllWidgets()
    { //initialises all widgets
        btnAddNewRecord = (Button) findViewById(R.id.btnAddNewRecord);

        parentLayout = (LinearLayout) findViewById(R.id.parentLayout);
        layoutDisplayMeds = (LinearLayout) findViewById(R.id.layoutDisplayMeds);

        tvNoRecordsFound = (TextView) findViewById(R.id.tvNoMedsFound);
    }

    private void bindWidgetsWithEvent()
    {
        btnAddNewRecord.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onAddRecord(); //calls addRecordMethod

            }
        });
    }

    private void onAddRecord()
    { //starts TableManiplationActivity and passes the code to ADD a record
        Intent intent = new Intent(MedsActivity.this, TableManipulationActivity.class);
        intent.putExtra(Constants.DML_TYPE, Constants.INSERT);
        startActivityForResult(intent, Constants.ADD_RECORD);
    }

    private void onUpdateRecord(String medName, String medDosage, String pktQuantity, String qtyToTake, String medTime1, String medTime2)
    { //update record method
        Intent intent = new Intent(MedsActivity.this, TableManipulationActivity.class); //new intent for table manipulation and passes through data
        intent.putExtra(Constants.MED_NAME, medName);
        intent.putExtra(Constants.MED_DOSAGE, medDosage);
        intent.putExtra(Constants.PKT_QUANTITY,pktQuantity);
        intent.putExtra(Constants.QTY_TO_TAKE,qtyToTake);
        intent.putExtra(Constants.TIME,medTime1);
        intent.putExtra(Constants.TIME2,medTime2);
        intent.putExtra(Constants.DML_TYPE, Constants.UPDATE); //adds in UPDATE request code
        startActivityForResult(intent, Constants.UPDATE_RECORD); //starts update activity
        displayAllRecords();
    }

    public void displayAllRecords()
    { //displays all records in the database

        LinearLayout inflateParentView;
        parentLayout.removeAllViews();

        ArrayList<MedicationModel> medications = sQLiteHelper.getAllRecords(); //puts all records into an ArrayList

        if (medications.size() > 0)
        { // if there are records found in the database
            tvNoRecordsFound.setVisibility(View.GONE); //hides no records found option
            MedicationModel medicationModel;
            for (int i = 0; i < medications.size(); i++)
            {

                medicationModel = medications.get(i); //retrieves all records

                final Holder holder = new Holder();
                final View view = LayoutInflater.from(this).inflate(R.layout.inflate_record, null); //sets row view for listview
                inflateParentView = (LinearLayout) view.findViewById(R.id.inflateParentView);
                holder.tvMedDetails = (TextView) view.findViewById(R.id.tvMedDetails);
                holder.tvTime = (TextView)view.findViewById(R.id.tvTime); //added v4 5.4.18

                //retrieves and sets variables
                view.setTag(medicationModel.getID());
                holder.medName = medicationModel.getMedName();
                holder.medDosage = medicationModel.getMedDosage();
                holder.pktQuantity = medicationModel.getPktQuantity();
                holder.qtyToTake = medicationModel.getQtyToTake();
                holder.medTimeH = medicationModel.getTime1();
                holder.medTimeM = medicationModel.getTime2();
                String medDetails = holder.medName + "\n" + holder.medDosage + "mg" + "\nR:"
                        + holder.pktQuantity + " Take: " + holder.qtyToTake; //creates string to display data in one textview
                holder.tvMedDetails.setText(medDetails);
                String fulltime = holder.medTimeH + ":" + holder.medTimeM;
                holder.tvTime.setText(fulltime);

                final CharSequence[] items = {Constants.UPDATE, Constants.DELETE};
                inflateParentView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v)
                    { //called when the user holds the record in the listview

                        AlertDialog.Builder builder = new AlertDialog.Builder(MedsActivity.this);
                        builder.setItems(items, new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0) //Update
                                {
                                    rowID = view.getTag().toString();
                                    onUpdateRecord(holder.medName, holder.medDosage,holder.pktQuantity,holder.qtyToTake,holder.medTimeH,holder.medTimeM);

                                }
                                else
                                { //Delete
                                    AlertDialog.Builder deleteDialogOk = new AlertDialog.Builder(MedsActivity.this);
                                    deleteDialogOk.setTitle("Delete Medication?");
                                    deleteDialogOk.setIcon(R.mipmap.ic_launcher);
                                    deleteDialogOk.setPositiveButton("Ok", new DialogInterface.OnClickListener()
                                            {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which)
                                                {
                                                    MedicationModel medication = new MedicationModel();
                                                    medication.setID(view.getTag().toString()); // retrieves the ID for the selected option
                                                    sQLiteHelper.deleteRecord(medication); //deletes selected medication
                                                    displayAllRecords(); //updates the listview

                                                }
                                            }
                                    );
                                    deleteDialogOk.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which)
                                        {
                                            Toast.makeText(getApplicationContext(),"Cancelled",Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    deleteDialogOk.show();
                                }
                            }
                        });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show(); //dislays alert dialog
                        return true;
                    }
                });
                parentLayout.addView(view);
            }
        }
        else
        {
            tvNoRecordsFound.setVisibility(View.VISIBLE); //if no records found the textview is made visible
        }
    }

    private class Holder //subclass to hold String variables and TextView
    {
        TextView tvMedDetails;
        String medName;
        String medDosage;
        String medTimeH;
        String medTimeM;
        TextView tvTime;
        String pktQuantity;
        String qtyToTake;
    }
}
