package com.abc.klpt;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("cliptext", text);
        db.insert("clipboard", null, contentValues);
        db.close();
    }

    public int deleteClipboardText(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("clipboard",
                "id = ? ",
                new String[]{Integer.toString(id)});
    }

    public void markAsStarred(int id, int starred)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String updateQuery = "update clipboard set starred="+starred+" where id="+id;
        db.execSQL(updateQuery);
        db.close();
    }

    public List<Clipboard> getAllClipboard() {
        List<Clipboard> list = new ArrayList<>();
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
        return list;
    }
}
