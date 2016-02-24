package com.genora.prospectmanagement.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.genora.prospectmanagement.model.EventModel;
import com.genora.prospectmanagement.myUtils.MyMessage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * Created by comnet on 11-Feb-16.
 */
public class ProspectDatabase extends SQLiteOpenHelper {

    private Context context;
    private SQLiteDatabase db;
    private static final String TABLE_LOGIN = "LOGIN_DETAILS";
    private static final String TABLE_EVENT = "EVENT_DETAILS";

    // "/data/data/com.genora.vubit/";
    private String appPath = "";
    private static String databaseName = "ProspectDB.sqlite";

    public ProspectDatabase(Context context) {
        super(context, databaseName, null, 1);
        this.context = context;
        this.appPath = context.getFilesDir().getPath();
        createdatabase();


    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }


    private void createdatabase() {

        if (isDatabaseCopied()) {
            MyMessage.myToast(context, "Database Exist!!");
        } else {
            copyDatabase();

            MyMessage.myToast(context, "Database Copied!!");
        }

    }


    private boolean isDatabaseCopied() {

        File f = new File(appPath + databaseName);
        if (f.exists()) {
            return true;
        } else {
            return false;
        }

    }


    private void copyDatabase() {

        try {
            InputStream is = context.getAssets().open(databaseName);
            String out_file = appPath + databaseName;
            OutputStream stream = new FileOutputStream(out_file);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                stream.write(buffer, 0, length);
            }
            stream.flush();
            stream.close();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void opendatabase() {

        db = SQLiteDatabase.openDatabase(appPath + databaseName, null, SQLiteDatabase.OPEN_READWRITE);
    }

    public synchronized void closeDatabase() {
        if (db != null) {
            db.close();
        }
    }
// insert login details

    public boolean insertLoginDetails(int id, String name, String designation) {

        boolean flag = false;
        opendatabase();
        try {
            ContentValues cv = new ContentValues();
            cv.put("emp_id", id);
            cv.put("emp_name", name);
            cv.put("emp_designation", designation);

            long rowInserted = db.insert(TABLE_LOGIN, null, cv);

            if (rowInserted > -1) {
                flag = true;
            } else {
                flag = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            flag = false;
        } finally {
            closeDatabase();
        }
        return flag;
    }

    //    insert Event Details
    public void insertEventdetails(List<EventModel> eventModelList) {

        opendatabase();
        long counter = 0;
        try {

            ContentValues cv = new ContentValues();

            for (int i = 0; i < eventModelList.size(); i++) {

                cv.put("emp_id", eventModelList.get(i).getId());
                cv.put("emp_name", eventModelList.get(i).getName());
                counter = db.insert(TABLE_EVENT, null, cv);
                counter++;
            }

            if (counter > -1) {
//                MyMessage.myToast(context, "" + counter);
            } else {
//                MyMessage.myToast(context, "Event Not Inserted");
            }


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeDatabase();
        }

    }
}
