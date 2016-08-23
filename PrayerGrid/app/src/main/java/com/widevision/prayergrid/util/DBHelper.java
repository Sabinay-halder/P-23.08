package com.widevision.prayergrid.util;


import android.database.Cursor;
import android.util.Log;

import com.activeandroid.ActiveAndroid;
import com.widevision.prayergrid.Bean.CountryBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by mercury-five on 22/02/16.
 */
public class DBHelper {
    private static volatile DBHelper instance = null;
    public static ArrayList<CountryBean> countryBeanArrayList = new ArrayList<>();

    // private constructor
    private DBHelper() {
    }

    public static DBHelper getInstance() {
        if (instance == null) {
            synchronized (DBHelper.class) {
                // Double check
                if (instance == null) {
                    instance = new DBHelper();
                }
            }
        }
        return instance;
    }

    public ArrayList<String> getCountryList() {
        Cursor c = ActiveAndroid.getDatabase().rawQuery("select * from Country", null);
        ArrayList<String> list = new ArrayList<>();
        if (countryBeanArrayList != null) {
            countryBeanArrayList.clear();
        }
        if (c != null && c.moveToFirst()) {
            do {
                String name = c.getString(c.getColumnIndex("CountryName"));
                String code = c.getString(c.getColumnIndex("CountryId"));
                CountryBean bean = new CountryBean();
                bean.setCountryName(name);
                bean.setCountryCode(code);
                countryBeanArrayList.add(bean);
                list.add(name);
            } while (c.moveToNext());
        }
        c.close();


       /* String[] a = Locale.getISOCountries();
        CountryBean[] listt = new CountryBean[a.length - 1];
        for (int i = 0; i < a.length; i++) {
            Locale l = new Locale("", a[i]);
*//*            countries.put(l.getDisplayCountry(), iso);*//*
            CountryBean bean = new CountryBean();
            bean.setCountryName(l.getDisplayCountry());
            bean.setCountryCode(a[i]);
            listt[i] = bean;
        }*/

        return list;


       /* Locale[] locale = Locale.getAvailableLocales();
        ArrayList<String> countries = new ArrayList<>();
        String country;
        for (Locale loc : locale) {
            country = loc.getDisplayCountry();
            if (country.length() > 0 && !countries.contains(country)) {
                countries.add(country);
            }
        }
        Collections.sort(countries, String.CASE_INSENSITIVE_ORDER);
        return countries;*/
    }

  /*  public static void main(String[] args) throws InterruptedException {
        *//*Map<String, String> countries = new HashMap<>();*//*
        ArrayList<CountryBean> list = new ArrayList<>();
        for (String iso : Locale.getISOCountries()) {
            Locale l = new Locale("", iso);
*//*            countries.put(l.getDisplayCountry(), iso);*//*
            CountryBean bean = new CountryBean();
            bean.setCountryName(l.getDisplayCountry());
            bean.setCountryCode(iso);
            list.add(bean);
        }


    }*/
}
