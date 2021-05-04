package com.example.meditrack;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;

/**
 * SQLiteHelper.java
 */
public class SQLiteHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 19; //increment each time a change is made to the database
    public static final String DATABASE_NAME = "FinalYearProject.db"; //database name

    public static final String TABLE_NAME = "MEDS"; //table name
    //Sets column titles
    public static final String COLUMN_ID = "ID";
    public static final String COL_MED_NAME = "MED_NAME";
    public static final String COL_MED_DOSAGE = "MED_DOSAGE";
    public static final String COL_PKT_QUANTITY = "PKT_QUANTITY";
    public static final String COL_QTY_TO_TAKE = "QTY_TO_TAKE";
    public static final String COLUMN_TIME_H = "TIME_HOUR"; //
    public static final String COLUMN_TIME_M = "TIME_MIN";

    private SQLiteDatabase database;

    public SQLiteHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL("create table " + TABLE_NAME + " ( " + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_MED_NAME + " VARCHAR, " + COL_MED_DOSAGE + " INTEGER, " + COL_PKT_QUANTITY + " INTEGER, " + COL_QTY_TO_TAKE
                + " INTEGER," + COLUMN_TIME_H + " INTEGER,"
                + COLUMN_TIME_M + " INTEGER);"); //creates meds table

    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db); //drops table and recreates onUpgrade
    }

    public void insertRecord(MedicationModel medication) //inserts record
    {
        database = this.getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_ID,medication.getID());
        contentValues.put(COL_MED_NAME, medication.getMedName());
        contentValues.put(COL_MED_DOSAGE, medication.getMedDosage());
        contentValues.put(COL_PKT_QUANTITY, medication.getPktQuantity());
        contentValues.put(COL_QTY_TO_TAKE, medication.getQtyToTake());
        contentValues.put(COLUMN_TIME_H, medication.getTime1());
        contentValues.put(COLUMN_TIME_M, medication.getTime2());
        database.insert(TABLE_NAME, null, contentValues);
        database.close();
    }

    public ArrayList<MedicationModel> getAllRecords() //gets all records from the database and reads into ArrayList<>
    {
        database = this.getReadableDatabase();
        Cursor cursor = database.query(TABLE_NAME, null, null, null, null, null, null);

        ArrayList<MedicationModel> medications = new ArrayList<MedicationModel>();
        MedicationModel medicationModel;
        if (cursor.getCount() > 0)
        {
            for (int i = 0; i < cursor.getCount(); i++)
            {
                cursor.moveToNext();

                medicationModel = new MedicationModel();
                medicationModel.setID(cursor.getString(0));
                medicationModel.setMedName(cursor.getString(1));
                medicationModel.setMedDosage(cursor.getString(2));
                medicationModel.setPktQuantity(cursor.getString(3));
                medicationModel.setQtyToTake(cursor.getString(4));
                medicationModel.setTime1(cursor.getString(5));// added in v3
                medicationModel.setTime2(cursor.getString(6));

                medications.add(medicationModel);
            }
        }
        cursor.close();
        database.close();

        return medications;
    }

    public void updateRecord(MedicationModel medication) //updates record
    {
        database = this.getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_MED_NAME, medication.getMedName());
        contentValues.put(COL_MED_DOSAGE, medication.getMedDosage());
        contentValues.put(COL_PKT_QUANTITY, medication.getPktQuantity());
        contentValues.put(COL_QTY_TO_TAKE, medication.getQtyToTake());
        contentValues.put(COLUMN_TIME_H, medication.getTime1());
        contentValues.put(COLUMN_TIME_M, medication.getTime2());
        database.update(TABLE_NAME, contentValues, COLUMN_ID + " = ?", new String[]{medication.getID()});
        database.close();
    }
    public void updateQuantity(MedicationModel medication,String val) //update COL_PKT_QUANTITY
    {
        database = this.getReadableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_PKT_QUANTITY,val);
        database.update(TABLE_NAME,cv,COL_PKT_QUANTITY + val, new String[]{medication.getID()});
        database.close();
    }
    public void deleteRecord(MedicationModel medication) //deletes record
    {
        database = this.getReadableDatabase();
        database.delete(TABLE_NAME, COLUMN_ID + " = ?", new String[]{medication.getID()});
        database.close();
    }

    public void deleteAllMedication() //deletes all records from the table
    {
        database = this.getReadableDatabase();
        database.delete(TABLE_NAME, null, null);
        database.close();
    }
}
