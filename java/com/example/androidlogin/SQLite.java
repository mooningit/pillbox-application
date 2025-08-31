package com.example.androidlogin;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SQLite extends SQLiteOpenHelper {

    String tag = "SQLite";

    public SQLite(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String sql = "create table DHT11" +
                     "(ID text primary key," + "Temp_Humi text);";
        sqLiteDatabase.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        String sql = "drop table if exists DHT11;";
        sqLiteDatabase.execSQL(sql);
        onCreate(sqLiteDatabase);
    }

    int count = 0;
    public int insert(String ID, String Temp_Humi ){
        SQLiteDatabase db = getWritableDatabase();
        try{
            db.execSQL("INSERT INTO DHT11 VALUES('"+ID+"', '" + Temp_Humi + "');");
            db.close();
            Log.e(tag, "데이터생성.");
            return 0;
        }catch (SQLException e){
            Log.e(tag, "중복되었습니다.");
            try{
                if( Integer.getInteger(ID) < 5){
                    update_temp(ID, Temp_Humi);
                }else if( (Integer.getInteger(ID) >= 5) && (Integer.getInteger(ID) < 10) ){
                    update_humi(ID, Temp_Humi);
                }
            }catch (NullPointerException e1){}

            return 1;
        }
    }

    public void update_temp(String id, String temp){
        try{
            SQLiteDatabase db = getWritableDatabase();
            db.execSQL("UPDATE DHT11 SET Temp_Humi = '"+ temp +"' WHERE ID = '"+id+"';");
            db.close();
            Log.e(tag, "상태값수정");

        }catch (SQLException e){}
    }

    public void update_humi(String id, String humi){
        try{
            SQLiteDatabase db = getWritableDatabase();
            db.execSQL("UPDATE DHT11 SET Temp_Humi = '"+ humi +"' WHERE ID = '"+id+"';");
            db.close();
            Log.e(tag, "상태값수정");

        }catch (SQLException e){}
    }

    public String Result_Temp(String id){
        SQLiteDatabase db = getReadableDatabase();
        String result ="";
        Cursor cursor = db.rawQuery("SELECT * FROM DHT11 WHERE ID = '"+id+"' ", null);
        while(cursor.moveToNext()){                 // 0 아이디, 1 temp
            result = cursor.getString(1);
            //Log.e(tag, "Temp ::" + result);
        }
        return result;
    }

    public String Result_Humi(String id){
        SQLiteDatabase db = getReadableDatabase();
        String result ="";
        Cursor cursor = db.rawQuery("SELECT * FROM DHT11 WHERE ID = '"+id+"' ", null);
        while(cursor.moveToNext()){                 // 0 아이디, 1 humi
             result = cursor.getString(1);
            //Log.e(tag, "Humi :: " + result);
        }
        return result;
    }


    public void delete(String id){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM DHT11 WHERE ID ='"+ id +"';");
        db.close();
    }



}
