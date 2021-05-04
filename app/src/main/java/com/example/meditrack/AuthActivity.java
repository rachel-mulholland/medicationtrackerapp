package com.example.meditrack;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import java.io.IOException;
import java.io.InputStream;

/**
 * AuthActivity.java
 */

public class AuthActivity extends Activity {


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) //if result code is ok
        {
            Intent i = new Intent(this, SplashScreen.class);
            startActivity(i);           //start splash screen activity
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //creates menu
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main,menu);
        menu.removeItem(R.id.menu_delete_meds); //hides Medication option from menu
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.menu_meds:
                Intent i2 = new Intent(this,MedsActivity.class);
                startActivity(i2);                              //starts MedsActivity
                break;
            case R.id.menu_help:
                showHelpDialog();       //shows help section
                break;
            case R.id.menu_exit:
                AlertDialog.Builder builder = new AlertDialog.Builder(AuthActivity.this,AlertDialog.THEME_DEVICE_DEFAULT_LIGHT); //AlertDialog creation
                builder.setTitle("Exit");
                builder.setIcon(R.mipmap.ic_launcher);
                builder.setMessage("Are you sure you want to exit?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) { //exits application
                        finish();
                    } //closes program
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //do nothing
                        Toast.makeText(AuthActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.create();
                builder.show();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        ActionBar actionBar = getActionBar(); //sets icon to ActionBar
        actionBar.setLogo(R.mipmap.ic_launcher);
        try {
            actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#4286f4"))); //sets ActionBar colour
        } catch (Exception e) {
            e.printStackTrace();
        }
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        KeyguardManager km = (KeyguardManager) getSystemService(getApplicationContext().KEYGUARD_SERVICE);     //authentication using system password
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {  //checks that the required Android version is installed
            if (km.isKeyguardSecure()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Intent i = km.createConfirmDeviceCredentialIntent(null, null);
                    startActivityForResult(i, RESULT_OK);   //starts authentication and passes through request code
                }
            }
        }
    }
    public void showHelpDialog()
    { //help dialog to explain to a user how to work the app

        //read in helpfile - works (file is in assets folder) if any changes are made to the file it will be updated on run
        String text = "";
        try {
            InputStream is = getAssets().open("help_file.txt");
            int size = is.available();
            byte[] buffer = new byte[size]; //reads text into Array[]
            is.read(buffer);
            is.close();
            text = new String(buffer); //sets buffer[] into a String
        } catch (IOException e) {
            e.printStackTrace();
        }

        AlertDialog.Builder b = new AlertDialog.Builder(AuthActivity.this,AlertDialog.THEME_DEVICE_DEFAULT_LIGHT); //AlertDialog creation and display
        b.setTitle("Help");
        b.setIcon(R.mipmap.ic_launcher);
        b.setMessage(text); //reads in text file from text variable
        b.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                //do nothing
            }
        });
        b.create();
        b.show();
    }
}

