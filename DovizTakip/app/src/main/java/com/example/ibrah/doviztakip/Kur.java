package com.example.ibrah.doviztakip;


//Her hangi bir para birimi icin kullanilabilecek kur sinifi, ozellikleri ve metotlari

public class Kur {

    private String alis;
    private String satis;
    private String tarih;

    String getAlis() {
        return alis;
    }

    void setAlis(String alis) {
        this.alis = alis;
    }

    String getSatis() {
        return satis;
    }

    void setSatis(String satis) {
        this.satis = satis;
    }

    String getTarih() {
        return tarih;
    }

    void setTarih(String tarih) {
        this.tarih = tarih;
    }
}
