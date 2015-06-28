package com.abc.klpt;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by abhishekrai on 3/13/15.
 */
public class DbHandler extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "ClipBoard.db";

    public DbHandler(Context context) {
        super(context, DATABASE_NAME, null, 2);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table clipboard " +
                        "(id integer primary key autoincrement not null, cliptext text,starred boolean default 0,timestamp default current_timestamp not null,type text)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(oldVersion < 2)
        {
            db.execSQL("alter table clipboard add column type text");
            List<Clipboard> temp = getAllClipboard(db);
            //Updating previous records to populate the type parameter
            for(Clipboard c : temp)
            {
                updateRecord(db,c.getId(),c.getClipboardText());
            }
        }
    }

    public void addClipboardText(String text) {
        try{
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("type",parseTextType(text));
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
            String updateQuery = "update clipboard set type='"+parseTextType(text)+"',cliptext='"+text+"' where id="+id;
            db.execSQL(updateQuery);
            db.close();
        }catch (Exception ex){
            Log.e("Error updating:",ex.getMessage());
        }
    }

    private void updateRecord(SQLiteDatabase db,int id, String text)
    {
        try{
            String updateQuery = "update clipboard set type='"+parseTextType(text)+"',cliptext='"+text+"' where id="+id;
            db.execSQL(updateQuery);
        }catch (Exception ex){
            Log.e("Error updating:",ex.getMessage());
        }
    }

    private String parseTextType(String text)
    {
        URL url = null;
        try {
            url = new URL(text);
        } catch (MalformedURLException e) {
            Log.v("myApp", "bad url entered");
        }
        if (url == null)
            return "note";
        return "link";
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

    public List<Clipboard> getAllClipboard(SQLiteDatabase db) {
        List<Clipboard> list = new ArrayList<>();
        try{
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
                res = db.rawQuery("select id,cliptext,starred,datetime(timestamp, 'localtime') from clipboard where cliptext like '%"+text+"%' and starred=1 order by timestamp desc", null);
            else if(starred == 0)
                res = db.rawQuery("select id,cliptext,starred,datetime(timestamp, 'localtime') from clipboard where cliptext like '%"+text+"%' order by timestamp desc", null);
            else if(starred == 2)
                res = db.rawQuery("select id,cliptext,starred,datetime(timestamp, 'localtime') from clipboard where cliptext like '%"+text+"%' and type='link' order by timestamp desc", null);
            else if(starred == 3)
                res = db.rawQuery("select id,cliptext,starred,datetime(timestamp, 'localtime') from clipboard where cliptext like '%"+text+"%' and type='note' order by timestamp desc", null);
            else
                res = db.rawQuery("select id,cliptext,starred,datetime(timestamp, 'localtime') from clipboard where cliptext like '%"+text+"%' and starred=0 order by timestamp desc", null);
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

    public List<Clipboard> getLinkClips() {
        List<Clipboard> list = new ArrayList<>();
        try{
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor res = db.rawQuery("select id,cliptext,starred,datetime(timestamp, 'localtime') from clipboard where type='link' order by timestamp desc", null);
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
            Log.e("Error links list",ex.getMessage());
        }
        return list;
    }

    public List<Clipboard> getNoteClips() {
        List<Clipboard> list = new ArrayList<>();
        try{
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor res = db.rawQuery("select id,cliptext,starred,datetime(timestamp, 'localtime') from clipboard where type='note' order by timestamp desc", null);
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
            Log.e("Error notes list",ex.getMessage());
        }
        return list;
    }
}
