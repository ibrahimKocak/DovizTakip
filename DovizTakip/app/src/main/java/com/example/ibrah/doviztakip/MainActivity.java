package com.example.ibrah.doviztakip;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends Activity {

    private TextView tv_update,tv_update_dolar,tv_update_euro;
    private EditText et_convert_to,et_convert_from;
    private Spinner spinner,spinner2;
    private Toast toast;

    private DovizGenTr dovizGenTr;
    Database database;

    private String update="",update_dolar="",update_euro="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toast = new Toast(this);

        //textViewler
        tv_update = findViewById(R.id.main_tv_update);
        tv_update_dolar = findViewById(R.id.main_tv_update_dolar);
        tv_update_euro = findViewById(R.id.main_tv_update_euro);
        et_convert_from = findViewById(R.id.main_et_convert_from);
        et_convert_to = findViewById(R.id.main_et_convert_to);

        //spinners
        spinner = findViewById(R.id.main_spinner);
        spinner2 = findViewById(R.id.main_spinner2);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.foreign_currency, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner2.setAdapter(adapter);

        //Nesneler
        dovizGenTr = new DovizGenTr();
        database = new Database(this);
        new DovizKuru().execute();                  //asenkron olarak web servisten veri cekme
    }

    public void main_update_onclick(View view) {

        new DovizKuru().execute();      //GUNCELLE butonu olayi, her basildiginda web servisten veri cekecek
    }


    //Ceviri islemi icin yabanci parayi TL cevirme fonk, sonra donusturulecek para birimine donusturme
    public void main_Convert_onclick(View view) {

        if(et_convert_from.getText().toString().equals(""))

            Toast.makeText(this,"Lütfen dönüşüm yapılacak miktarı giriniz...",Toast.LENGTH_SHORT).show();

        else if(dovizGenTr.getDolar().getAlis() == null)

            Toast.makeText(this,"Kur bilgileri alınamadı, Lütfen kur bilgilerini güncelleyiniz...",Toast.LENGTH_SHORT).show();
        else
        {

            float convert_from = Float.valueOf(et_convert_from.getText().toString());

            float tl = Convert_to_TL(spinner.getSelectedItemPosition(), convert_from);

            //TL ye cevirme
            float doviz = Convert_from_TL(spinner2.getSelectedItemPosition(), tl);

            et_convert_to.setText(String.valueOf(doviz));
        }
    }

    //Cevirilecek para birimini TL ye cevirme fonk.
    private float Convert_to_TL(int doviz_index, float money)
    {
        if(doviz_index == 0)
            return money * Float.valueOf(dovizGenTr.getDolar().getAlis());
        else if(doviz_index == 1)
            return money * Float.valueOf(dovizGenTr.getEuro().getAlis());
        else
            return money;
    }

    //TL ye cevrilen birimin asil cevrilmek istenen birime donusturulmesi
    private float Convert_from_TL(int doviz_index, float money)
    {
        if(doviz_index == 0)
            return money / Float.valueOf(dovizGenTr.getDolar().getAlis());
        else if(doviz_index == 1)
            return money / Float.valueOf(dovizGenTr.getEuro().getAlis());
        else
            return money;
    }

    //kur gecmisini goruntulemek icin diger activiteye gecis
    public void main_wiev_log_onclick(View view) {

        Intent intent = new Intent(this,LogActivity.class);

        ArrayList<String> list_dolar=new ArrayList<>();
        ArrayList<String> list_euro=new ArrayList<>();
        String gecici="";

        //2. aktivitide dolar kurlari goruntelemek icin verileri veritabanindan listeye aliyoruz
        for(Kur kur:database.toArray("DOLAR")){
            gecici = "\nAlış :\t\t\t"+kur.getAlis()+ "\t\t\t\t\tSatış :\t\t\t"+kur.getSatis();
            gecici += "\n\n"+kur.getTarih();
            list_dolar.add(gecici);
        }

        //2. aktivitide euro kurlari goruntelemek icin verileri veritabanindan listeye aliyoruz
        for(Kur kur:database.toArray("EURO")){
            gecici = "\nAlış :\t\t\t"+kur.getAlis()+ "\t\t\t\t\tSatış :\t\t\t"+kur.getSatis();
            gecici += "\n\n"+kur.getTarih();
            list_euro.add(gecici);
        }

        //listelerle birlikte 2. aktiviteye gecis yapiyoruz
        intent.putStringArrayListExtra("list_dolar",list_dolar);
        intent.putStringArrayListExtra("list_euro",list_euro);
        startActivity(intent);

    }

    //O an ki kur degerlerini veri tabanina ekleme islemi
    public void main_add_log_onclick(View view) {

        try {

            database.insert("DOLAR",dovizGenTr.getDolar());
            database.insert("EURO",dovizGenTr.getEuro());

            Toast.makeText(this,"Kayıt Başarılı",Toast.LENGTH_SHORT).show();
        }
        catch (SQLiteException ex)
        {
            Toast.makeText(this,ex.getMessage(),Toast.LENGTH_SHORT).show();
        }

    }

    //Web servisten kur cekme ve ekrana gosterme islemleri
    @SuppressLint("StaticFieldLeak")
    private class DovizKuru extends AsyncTask<String,String,String> {

        //Web servisten veri cekip degiskenlere atmaya calisiyoruz
        @Override
        protected String doInBackground(String... strings) {

            //Web servisten veri cekme islemi
            dovizGenTr.Update();

            //Servisten veri cekme basarili olursa bilgileri degiskenlere atiyoruz
            if(dovizGenTr.isUpdate_success())
            {
                update = dovizGenTr.getTime();
                update_dolar = "Dolar - \t\tAlış:\t\t"+dovizGenTr.getDolar().getAlis()+"\t\tSatış:\t\t"+dovizGenTr.getDolar().getSatis();
                update_euro = "Euro - \t\t\tAlış:\t\t"+dovizGenTr.getEuro().getAlis()+"\t\tSatış:\t\t"+dovizGenTr.getEuro().getSatis();
            }else
                update = "\n";

            return null;
        }

        //degiskenleri ekranda guncelliyoruz
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            tv_update.setText(update);
            tv_update_dolar.setText(update_dolar);
            tv_update_euro.setText(update_euro);

            update_message(dovizGenTr.isUpdate_success());
        }
    }

    private void update_message(boolean isSuccesfull)
    {
        if(isSuccesfull)
            Toast.makeText(this,"Güncelleme Başarılı", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this,"Güncelleme Başarısız. Lütfen internet bağlantınızı kontrol ediniz..", Toast.LENGTH_SHORT).show();
    }
}
