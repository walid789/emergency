package com.example.emercegcyapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DBHandler  extends SQLiteOpenHelper {
    private static final String DB_NAME1 = "test";

    // below int is our database version
    private static final int DB_VERSION = 1;

    // below variable is for our table name.
    private static final String TABLE_NAME = "user";
    private static final String ID_COL = "id";
    private static final String UserName_COL = "user_name";
    private static final String Phone_Number_COL = "phone_number";
    private static final String Password_COL = "password";
    public DBHandler(Context context) {
        //super(context, DB_NAME, null, DB_VERSION);
        super(context, DB_NAME1, null, DB_VERSION);
    }

    public void addNewUser(String UserName, String  Password, String Phone_Number) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(UserName_COL, UserName);
        values.put(Password_COL, Password);
        values.put(Phone_Number_COL, Phone_Number);
        db.insert(TABLE_NAME, null, values);
        db.close();
    }



    public int Auth(String UserName, String  Password){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursorUser = db.rawQuery( "SELECT user_name,id FROM " + TABLE_NAME +
                " WHERE " + UserName_COL + " = ? AND " + Password_COL + " = ?", new String[]{UserName, Password});
        int isAuthenticated = 0;
        String username = "";
        int id=0;

        if (cursorUser != null) {
            if (cursorUser.moveToFirst() ) {
                username = cursorUser.getString(cursorUser.getColumnIndexOrThrow("user_name"));
                id = cursorUser.getInt(cursorUser.getColumnIndexOrThrow("id"));

                if(username.equals("admin")){
                    isAuthenticated = -1;
                }
                else
                {
                    isAuthenticated =id ;
                }
            }
            cursorUser.close(); // Close the cursor
        }

        db.close(); // Close the database
        return isAuthenticated;
    }
    public void saveEmergcy(Emergcy em) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id_user", em.getId_user());
        values.put("date", em.getDate());
        values.put("Latitude", em.getLatitude());
        values.put("Longitude", em.getLongitude());
        values.put("cas", em.getCas());
        database.insert("emergcy", null, values);
        database.close();
    }
    public ArrayList<User> readUserById(int id)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursorCourses = db.rawQuery("SELECT * FROM " + "user" + " WHERE id = ?", new String[]{String.valueOf(id)});
        ArrayList<User> userArrayList
                = new ArrayList<User>();
        while (cursorCourses.moveToNext()) {
            User userObj = new User(
                    cursorCourses.getInt(cursorCourses.getColumnIndexOrThrow("id")),
                    cursorCourses.getString(cursorCourses.getColumnIndexOrThrow("user_name")),
                    cursorCourses.getString(cursorCourses.getColumnIndexOrThrow("phone_number")),
                    cursorCourses.getString(cursorCourses.getColumnIndexOrThrow("password"))
            );
            userArrayList.add(userObj);
        }
        cursorCourses.close();
        db.close();
        return userArrayList;
    }
    public ArrayList<Emergcy> readEmergcy()
    {
        // on below line we are creating a
        // database for reading our database.
        SQLiteDatabase db = this.getReadableDatabase();

        // on below line we are creating a cursor with query to
        // read data from database.
        Cursor cursorCourses = db.rawQuery("SELECT * FROM emergcy " , null);


        // on below line we are creating a new array list.
        ArrayList<Emergcy> courseModalArrayList
                = new ArrayList<Emergcy>();


        while (cursorCourses.moveToNext()) {
            // on below line we are adding the data from
            // cursor to our array list.
            Emergcy emergcy = new Emergcy(
                    cursorCourses.getInt(cursorCourses.getColumnIndexOrThrow("id")),
                    cursorCourses.getInt(cursorCourses.getColumnIndexOrThrow("id_user")),
                    cursorCourses.getString(cursorCourses.getColumnIndexOrThrow("date")),
                    cursorCourses.getFloat(cursorCourses.getColumnIndexOrThrow("Latitude")),
                    cursorCourses.getFloat(cursorCourses.getColumnIndexOrThrow("Longitude")),
                    cursorCourses.getString(cursorCourses.getColumnIndexOrThrow("cas"))
            );
                    courseModalArrayList.add(emergcy);
        }
        cursorCourses.close();
        db.close();
        return courseModalArrayList;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
