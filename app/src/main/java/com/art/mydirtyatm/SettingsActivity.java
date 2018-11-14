package com.art.mydirtyatm;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import static com.art.mydirtyatm.OptionsActivity.atm;

public class SettingsActivity extends AppCompatActivity {

    String settingsDir = Environment.getExternalStorageDirectory().toString() + "/settings";            //каталог с файлом настроек
    File setFileDir = new File(settingsDir);
    File settingsFile = new File(settingsDir, "settings.txt");
    FileInputStream fis;
    String resultSettings = "";

    EditText nameFIO;
    EditText email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        nameFIO = findViewById(R.id.fio);
        email = findViewById(R.id.email);

        //если нет каталога, создаем
        if (!setFileDir.exists()) {
            setFileDir.mkdirs();
        } else  {
            try {
                //читакм из буфера в кодировке UTF8
                fis = new FileInputStream(settingsFile);
                InputStreamReader isr = new InputStreamReader(fis, "utf8");
                BufferedReader br = new BufferedReader(isr);
                resultSettings = br.readLine();

                //режем строку по символу
                String[] result = resultSettings.split("\\|");

                nameFIO.setText(result[0]);
                email.setText(result[1]);

            } catch (UnsupportedEncodingException e) {
                atm.setFio("John Doe");
                atm.setEmail("sberbank.ru");
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                atm.setFio("John Doe");
                atm.setEmail("sberbank.ru");
                e.printStackTrace();
            } catch (IOException e) {
                atm.setFio("John Doe");
                atm.setEmail("sberbank.ru");
                e.printStackTrace();
            }
        }

    }

    //кнопка сохранить изменения
    public void onSaveSettingsButtonClick(View view) {

        StringBuilder outSet = new StringBuilder();

        //делаем строку из ФИО и емейла для записи
        outSet.append(nameFIO.getText() + "|" + email.getText());

        if (!settingsFile.exists()) {
            try {
                settingsFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            FileOutputStream fo = new FileOutputStream(settingsFile, false);
            OutputStreamWriter osw = new OutputStreamWriter(fo, "utf8");
            osw.append(outSet.toString());

            osw.close();
            fo.flush();
            fo.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //это если пользователь что то поменял, что бы корректно отобразить в файле без перезагрузки
        atm.setFio(nameFIO.getText().toString());
        atm.setEmail(email.getText().toString());

        this.finish();
    }


}
