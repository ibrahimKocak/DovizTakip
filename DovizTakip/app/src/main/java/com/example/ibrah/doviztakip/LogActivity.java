package com.example.ibrah.doviztakip;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class LogActivity extends Activity {

    ListView listView;
    Spinner spinner;

    private ArrayList<String> list_dolar,list_euro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        spinner = findViewById(R.id.log_spinner);
        listView = findViewById(R.id.log_listView);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.foreign_currency_list, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        //mainden gelen veri tabanindan cekilen verilerin listeleri
        list_dolar = new ArrayList<>(getIntent().getStringArrayListExtra("list_dolar"));
        list_euro = new ArrayList<>(getIntent().getStringArrayListExtra("list_euro"));
        Collections.reverse(list_dolar);
        Collections.reverse(list_euro);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here

                if(position == 0)       //spinnerda dolar secili ise listeyi dolar gecmisi ile
                    updateList(list_dolar);
                else if(position == 1)      //euro secili ise euro gecmisi ile dolduruyoruz
                    updateList(list_euro);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

    }

    //ListView'deki listenin spinner secimine gore atanma islemi
    private void updateList(ArrayList<String> arrayList)
    {
        ArrayAdapter<String> arrayAdapter=new ArrayAdapter<>(
                this,R.layout.mytextview,arrayList);
        listView.setAdapter(arrayAdapter);
    }

}
