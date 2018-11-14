package com.art.mydirtyatm;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class OptionsActivity extends AppCompatActivity {

    public static ATM atm; //переменная класса

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        atm = new ATM(); //создаем экземпляр

        String settingsDir = Environment.getExternalStorageDirectory().toString() + "/settings";
        File settingsFile = new File(settingsDir, "settings.txt");
        FileInputStream fis;
        String resultSettings = "";

        try {
            fis = new FileInputStream(settingsFile);
            char current;
            while (fis.available() > 0) {
                current = (char) fis.read();
                resultSettings = resultSettings + String.valueOf(current);
            }

            String[] result = resultSettings.split("\\|");

            atm.setFio(result[0]);
            atm.setEmail(result[1]);

        } catch (FileNotFoundException e) {
            atm.setFio("John Doe");
            atm.setEmail("sberbank.ru");
        } catch (IOException e) {
            atm.setFio("John Doe");
            atm.setEmail("sberbank.ru");
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void onFillChecklistButtonClick(View view) {

        Spinner modelText = findViewById(R.id.model);
        atm.setModel(modelText.getSelectedItem().toString());

        EditText serialNumber = findViewById(R.id.serial);
        atm.setSerialNumber(serialNumber.getText().toString());

        EditText systemNumber = findViewById(R.id.system);
        atm.setSystemNumber(systemNumber.getText().toString());

        Spinner condition = findViewById(R.id.condition);
        atm.setCondition(condition.getSelectedItem().toString());

        EditText atmLocation = findViewById(R.id.address);
        atm.setAtmLocation(atmLocation.getText().toString());

        if (systemNumber.getText().toString().equals("") || atmLocation.getText().toString().equals("")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(OptionsActivity.this);
            builder.setTitle("Незаполненные поля!!!")
                    .setMessage("Поля \"Системный номер\" и \"Адрес установки\" обязательны к заполнению!")
                    .setCancelable(false)
                    .setNegativeButton("Давай заполню!",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
            AlertDialog alert = builder.create();
            alert.show();
        }
        else {
            Intent intent = new Intent(this, Checklist.class);
            startActivity(intent);
        }

    }
}
