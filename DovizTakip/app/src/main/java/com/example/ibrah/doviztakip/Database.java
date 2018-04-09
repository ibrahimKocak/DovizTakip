package com.example.ibrah.doviztakip;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

//Kur verilerinin kayit edilecegi veritabani sinifi
public class Database extends SQLiteOpenHelper{

    private static final int DATABASE_VERSION = 1;//Database Version
    private static final String DATABASE_NAME = "DOVIZKURLAR";//Database Name
    private static final String[] TABLE_NAMES = {"DOLAR","EURO"};

    private static String ID="id";
    private static String ALIS="alis";
    private static String SATIS="satis";
    private static String TARIH="tarih";

    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //Tablolarin olusturulmasi
    @Override
    public void onCreate(SQLiteDatabase db) {

        for(int i=0;i<TABLE_NAMES.length;i++)
        {
            String CREATE_TABLE="CREATE TABLE "+TABLE_NAMES[i]+"("
                    +ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "
                    +ALIS+" TEXT, "
                    +SATIS+" TEXT, "
                    +TARIH+" TEXT )";
            db.execSQL(CREATE_TABLE);
        }
    }

    //Upgrade de tablolari silip sifirliyoruz, simdilik kullanilmayacak
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        for(int i=0;i<TABLE_NAMES.length;i++)
            db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAMES[i]);

        onCreate(db);
    }

    //Downgrade de tablolari silip sifirliyoruz, simdilik kullanilmayacak
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //super.onDowngrade(db, oldVersion, newVersion);

        db.setVersion(DATABASE_VERSION);
        onUpgrade(db,oldVersion,newVersion);
    }

    //Veri ekleme islemi
    public void insert(String table_name, Kur kur){

        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put(ALIS,kur.getAlis());
        values.put(SATIS,kur.getSatis());
        values.put(TARIH,kur.getTarih());
        db.insert(table_name,null,values);
        db.close();
    }

    //Veri tabanindaki verilerin okunup liste seklinde dondurulmesi
    //Kur gecmisini listeleme de kullanilacak
    public ArrayList<Kur> toArray(String table_name){

        ArrayList<Kur> kurBilgileriList=new ArrayList<>();
        String selectQuery="SELECT alis,satis,tarih FROM "+table_name;
        SQLiteDatabase db=this.getReadableDatabase();

        Cursor cursor=db.rawQuery(selectQuery,null);
        if(cursor.moveToFirst()){
            do{
                Kur kur=new Kur();
                kur.setAlis(cursor.getString(0));
                kur.setSatis(cursor.getString(1));
                kur.setTarih(cursor.getString(2));
                kurBilgileriList.add(kur);
            }
            while (cursor.moveToNext());
        }
        return kurBilgileriList;
    }
}