package com.example.threes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG="DatabaseHelper";
    private static final String TABLE_NAME="highscore";
    private static final String COL1="ID";
    private static final String COL2="score";

    public DatabaseHelper(Context context){
        super(context, TABLE_NAME,null,1);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable="CREATE TABLE "+TABLE_NAME+" (ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                COL2 +" TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP IF TABLE EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean addData(int item){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(COL2,item);
        long result=db.insert(TABLE_NAME, null,contentValues);

        if(result==-1){
            return false;
        }else{
            return true;
        }
    }

    public void update(int item){
        SQLiteDatabase db=this.getWritableDatabase();
        String query="UPDATE "+TABLE_NAME+" SET score = "+item+" WHERE ID=1";
        db.execSQL(query);
    }

    public Cursor getData(){
        SQLiteDatabase db=this.getWritableDatabase();
        String query="SELECT score FROM "+TABLE_NAME+" WHERE ID=1";
        Cursor data=db.rawQuery(query,null);
        return data;
    }
}
