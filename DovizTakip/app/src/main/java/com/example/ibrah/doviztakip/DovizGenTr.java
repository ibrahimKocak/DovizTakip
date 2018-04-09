package com.example.ibrah.doviztakip;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


//url de belirtilen servisten kur bilglilerini ceken sinif, main de olusturmak yerine ayri bir sinif olusturdum,
//boylelikle baska web servisleri icin baska siniflar olusturup mainde kolayca gecis yapilabilir.

class DovizGenTr {

    private static final String string_url = "http://www.doviz.gen.tr/doviz_json.asp?version=1.2";
    private boolean update_success = false;     //veri guncellemesini basari durumu

    private Kur dolar,euro;
    private String time;

    DovizGenTr(){

        dolar = new Kur();
        euro = new Kur();
    }

    void Update() {
        HttpURLConnection connection;
        BufferedReader bufferedReader;

        try{
            URL url=new URL(string_url);
            connection=(HttpURLConnection)url.openConnection();
            connection.connect();
            bufferedReader=new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String satir;
            StringBuilder dosya= new StringBuilder();
            while((satir=bufferedReader.readLine())!=null){
                dosya.append(satir);
            }

            //web servisi json formatinda veri verdigi icin json nesnesi kullanarak verileri cekiyoruz
            JSONObject jsonObject=new JSONObject(dosya.toString());
            dolar.setAlis(jsonObject.get("dolar2").toString());
            dolar.setSatis(jsonObject.get("dolar").toString());
            dolar.setTarih(jsonObject.get("sonkayit").toString());
            euro.setAlis(jsonObject.get("euro2").toString());
            euro.setSatis(jsonObject.get("euro").toString());
            euro.setTarih(jsonObject.get("sonkayit").toString());

            time = jsonObject.get("sonkayit").toString();
            time += "\nVeri Tabanı:\t\t\t "+jsonObject.get("guncelleme").toString();

            update_success = true;
        }
        catch (Exception e){
            update_success = false;
        }
    }

    //get metotlar
    boolean isUpdate_success() {
        return update_success;
    }

    Kur getDolar() {
        return dolar;
    }

    Kur getEuro() {
        return euro;
    }

    String getTime() {
        return time;
    }
}
