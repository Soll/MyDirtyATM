package com.art.mydirtyatm;

//Класс АТМ описывает активность пользователя по чеклисту

import android.app.Application;
import android.net.Uri;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ATM extends Application {

    private String model;
    private String serialNumber;
    private String systemNumber;
    private String condition;
    private String atmLocation;
    int imageCounter = 0;
    private String fio;
    private String email;


    public static final String[] optionNames = {
            //--------Внешний вид---------
            "Грязный корпус",
            "Грязный экран",
            "Сколы/вмятины на корпусе",
            "Потертости пластика",
            //-------- Стикеры--------------
            "Полоса МПС отсутствует/ненадлежащего вида",
            "Оплата услуг отсутствует/ненадлежащего вида",
            "Вставьте карту отсутствует/ненадлежащего вида",
            "Возьмите деньги отсутствует/ненадлежащего вида",
            "Возьмите чек отсутствует/ненадлежащего вида",
            "Сканер штрих-кода отсутствует/ненадлежащего вида",
            "Стикер ОХРАНА отсутствует/ненадлежащего вида",
            //-------Чистота и порядок----------
            "Чеки валяются вокруг"
    };

    public ArrayList<Uri> images = new ArrayList<>();

    //массив значений показателей состояния банкомата в формате "наименование-значение"
    public static Map<String, Boolean> options = new HashMap<>();

    //конструктор
    public ATM () {

        for (String option : optionNames) {
            options.put(option, false);
        }
    }

    //возвращеает хэшмэп опций
    public Map getOptions() {
        return options;
    }

    //изменяет значение опции
    public void updateOption(String key, Boolean val) {
        options.put(key, val);
    }

    public String getModel() {
        return this.model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getSerialNumber() {
        return this.serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getSystemNumber() {
        return this.systemNumber;
    }

    public void setSystemNumber(String systemNumber) {
        this.systemNumber = systemNumber;
    }

    public String getAtmLocation() {
        return this.atmLocation;
    }

    public void setAtmLocation(String atmLocation) {
        this.atmLocation = atmLocation;
    }

    public String getCondition() {
        return this.condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public int getImageCounter() {
        return this.imageCounter;
    }

    public void increaseImageCounter() {
        this.imageCounter++;
    }

    public void setFio(String fio) {
        this.fio = fio;
    }

    public String getFio() {
        return fio;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}
