package com.abc.klpt;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by abhishekrai on 3/13/15.
 */
public class DbHandler extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "ClipBoard.db";

    public DbHandler(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table clipboard " +
                        "(id integer primary key autoincrement not null, cliptext text,starred boolean default 0,timestamp default current_timestamp not null)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS clipboard");
        onCreate(db);
    }

    public void addClipboardText(String text) {
        try{
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("cliptext", text);
            db.insert("clipboard", null, contentValues);
            db.close();
        }catch (Exception ex)
        {
            Log.e("Error insert: ",ex.getMessage());
        }

    }

    public int deleteClipboardText(int id) {
        try{
            SQLiteDatabase db = this.getWritableDatabase();
            return db.delete("clipboard",
                    "id = ? ",
                    new String[]{Integer.toString(id)});
        }catch (Exception ex){
            Log.e("Error deleting:",ex.getMessage());
        }
        return 0;
    }

    public void markAsStarred(int id, int starred)
    {
        try{
            SQLiteDatabase db = this.getWritableDatabase();
            String updateQuery = "update clipboard set starred="+starred+" where id="+id;
            db.execSQL(updateQuery);
            db.close();
        }catch (Exception ex){
            Log.e("Error marking:",ex.getMessage());
        }
    }

    public void updateRecord(int id, String text)
    {
        try{
            SQLiteDatabase db = this.getWritableDatabase();
            String updateQuery = "update clipboard set cliptext='"+text+"' where id="+id;
            db.execSQL(updateQuery);
            db.close();
        }catch (Exception ex){
            Log.e("Error updating:",ex.getMessage());
        }
    }

    public List<Clipboard> getAllClipboard() {
        List<Clipboard> list = new ArrayList<>();
        try{
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor res = db.rawQuery("select id,cliptext,starred,datetime(timestamp, 'localtime') from clipboard order by timestamp desc", null);
            if (res.moveToFirst()) {
                do {
                    Clipboard obj = new Clipboard();
                    obj.setId(res.getInt(0));
                    obj.setClipboardText(res.getString(1));
                    obj.setStarred(res.getInt(2)>0);
                    obj.setTimestamp(res.getString(3));
                    list.add(obj);
                } while (res.moveToNext());
            }
            db.close();
        }catch (Exception ex)
        {
            Log.e("Error fetching:",ex.getMessage());
        }

        return list;
    }

    public List<Clipboard> getQuery(String text,int starred) {
        List<Clipboard> list = new ArrayList<>();
        try{
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor res;
            if(starred == 1)
                res = db.rawQuery("select id,cliptext,starred,datetime(timestamp, 'localtime') from clipboard where cliptext like '%"+text+"%' and starred="+starred+" order by timestamp desc", null);
            else
                res = db.rawQuery("select id,cliptext,starred,datetime(timestamp, 'localtime') from clipboard where cliptext like '%"+text+"%' order by timestamp desc", null);
            if (res.moveToFirst()) {
                do {
                    Clipboard obj = new Clipboard();
                    obj.setId(res.getInt(0));
                    obj.setClipboardText(res.getString(1));
                    obj.setStarred(res.getInt(2)>0);
                    obj.setTimestamp(res.getString(3));
                    list.add(obj);
                } while (res.moveToNext());
            }
            db.close();
        }catch (Exception ex){
            Log.e("Error querying:",ex.getMessage());
        }
        return list;
    }

    public List<Clipboard> getMarkedClips() {
        List<Clipboard> list = new ArrayList<>();
        try{
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor res = db.rawQuery("select id,cliptext,starred,datetime(timestamp, 'localtime') from clipboard where starred=1 order by timestamp desc", null);
            if (res.moveToFirst()) {
                do {
                    Clipboard obj = new Clipboard();
                    obj.setId(res.getInt(0));
                    obj.setClipboardText(res.getString(1));
                    obj.setStarred(res.getInt(2)>0);
                    obj.setTimestamp(res.getString(3));
                    list.add(obj);
                } while (res.moveToNext());
            }
            db.close();
        }catch (Exception ex){
            Log.e("Error marked list",ex.getMessage());
        }
        return list;
    }
}
