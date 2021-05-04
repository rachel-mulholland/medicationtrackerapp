package com.example.meditrack;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 *TableManipulationActivity.java
 */
public class TableManipulationActivity extends Activity {

    EditText etMedName;
    EditText etMedDosage;
    EditText etPktQuantity;
    EditText etQtyToTake;
    EditText etTimeH;
    EditText etTimeM;

    Button btnDML;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.table_manipulation);

        ActionBar actionBar = getActionBar(); //sets icon to ActionBar
        actionBar.setLogo(R.mipmap.ic_launcher);
        try {
            actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#4286f4"))); //sets ActionBar colour
        } catch (Exception e) {
            e.printStackTrace();
        }
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        //calls methods
        getAllWidgets();
        bindWidgetsWithEvent();
        checkForRequest();
    }

    private void checkForRequest() //checks for request code passed and starts relevant function depending on the code that is received
    {
        String request = getIntent().getExtras().get(Constants.DML_TYPE).toString(); //gets request code passed in the intent
        if (request.equals(com.example.meditrack.Constants.UPDATE))
        {
            btnDML.setText(Constants.UPDATE);
            etMedName.setText(getIntent().getExtras().get(Constants.MED_NAME).toString());
            etMedDosage.setText(getIntent().getExtras().get(Constants.MED_DOSAGE).toString());
            etPktQuantity.setText(getIntent().getExtras().get(Constants.PKT_QUANTITY).toString());
            etQtyToTake.setText(getIntent().getExtras().get(Constants.QTY_TO_TAKE).toString());
            etTimeH.setText(getIntent().getExtras().get(Constants.TIME).toString());
            etTimeM.setText(getIntent().getExtras().get(Constants.TIME2).toString());
        }
        else
        {
            btnDML.setText(Constants.INSERT);
        }
    }

    private void bindWidgetsWithEvent()
    {
        btnDML.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onButtonClick(); //calls method
            }
        });
    }

    private void getAllWidgets() //initialises all widgets
    {
        etMedName = (EditText) findViewById(R.id.etMedName);
        etMedDosage = (EditText) findViewById(R.id.etMedDosage);
        etPktQuantity = (EditText)findViewById(R.id.etPktQuantity);
        etQtyToTake = (EditText)findViewById(R.id.qtyToTake);
        etTimeH = (EditText)findViewById(R.id.etTimeH); //added 5.4.18
        etTimeM = (EditText)findViewById(R.id.ettimeM);

        btnDML = (Button) findViewById(R.id.btnDML);
    }

    private void onButtonClick()
    { // field validation and retrieves data from fields
        String emptyText = "This field is required!";

        if(etMedName.getText().toString().length()==0)
        {
            etMedName.setError(emptyText);
        }
        else if (etMedDosage.getText().toString().length()==0)
        {
            etMedDosage.setError(emptyText);
        }
        else if(etPktQuantity.getText().toString().length()==0 || Integer.parseInt(etPktQuantity.getText().toString())>100)
        {
            etPktQuantity.setError("This field is required/Can't enter more than 100");
        }
        else if(etQtyToTake.getText().toString().length()==0)
        {
            etQtyToTake.setError(emptyText);
        }
        else if(etTimeH.getText().toString().length()==0)
        {
            etTimeH.setError(emptyText);
        }
        else if(etTimeM.getText().toString().length()==0)
        {
            etTimeM.setError(emptyText);
        }
        else
        {
            Intent intent = new Intent();
            intent.putExtra(Constants.MED_NAME, etMedName.getText().toString());
            intent.putExtra(Constants.MED_DOSAGE, etMedDosage.getText().toString());
            intent.putExtra(Constants.PKT_QUANTITY,etPktQuantity.getText().toString());
            intent.putExtra(Constants.QTY_TO_TAKE,etQtyToTake.getText().toString());
            intent.putExtra(Constants.TIME,etTimeH.getText().toString());
            intent.putExtra(Constants.TIME2,etTimeM.getText().toString());
            setResult(RESULT_OK, intent); //passes result code
            finish();
        }
    }
}
