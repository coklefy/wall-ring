package com.wallring.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQuery;
import android.database.sqlite.SQLiteQueryBuilder;
import android.widget.Toast;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;
import com.wallring.Model.SavedImage;

import java.util.ArrayList;
import java.util.List;

public class Database extends SQLiteAssetHelper {

    private static final String DB_NAME = "SavedImages.db";
    private static final int DB_VER = 1;
    public Database(Context context) {
        super(context, DB_NAME, null, DB_VER);
    }

    public List<SavedImage> getSavedImages(){

        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String[] sqlSelect = {"image_URL"};
        String sqlTable = "Images_Table";

        qb.setTables(sqlTable);
        Cursor c = (Cursor) qb.query(db, sqlSelect, null, null, null, null, null);

        final List<SavedImage> result = new ArrayList<>();

        if(c.moveToFirst()){

            do{
                result.add(new SavedImage(
                        c.getString(c.getColumnIndex("image_URL")))
                        );
            }while (c.moveToNext());
        }
        return result;
    }


    public void addToSavedImages(SavedImage savedImage){
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("INSERT INTO Images_Table(image_URL) VALUES('%s');",
                savedImage.getImage_URL()
                );

        db.execSQL(query);
        this.cleanSavedImages();
    }

    public void cleanSavedImages(){
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("DELETE FROM Images_Table");

        db.execSQL(query);
    }


}
