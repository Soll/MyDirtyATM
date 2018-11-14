package com.art.mydirtyatm;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import java.util.Map;
import java.util.Set;

import static com.art.mydirtyatm.OptionsActivity.atm;


public class Checklist extends AppCompatActivity {

    Map options; //переменная для списка параметров

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checklist);

        options = atm.getOptions(); //выгружаем коллекцию опций и значений в переменную
        Set<Map.Entry<String, Boolean>> optionsSet = options.entrySet(); //фигачим сет для перебора

        LinearLayout parentLayout = findViewById(R.id.checkboxLayout); //главный лэйаут

        //простые параметры
        LinearLayout.LayoutParams layoutParams= new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        //перебор сета опций для динамического создания списка чекбоксов
        for (Map.Entry<String, Boolean> opt : optionsSet) {
            final CheckBox checkBox = new CheckBox(this);
            checkBox.setLayoutParams(layoutParams);
            checkBox.setText(opt.getKey()); //ключ сета - название чекбокса

            //если юзер уже тыкал на чекбоксы, восстанавливаем картину
            if (opt.getValue()) {
                checkBox.setSelected(true);
            }

            //херачим листенер на изменение чекбокса
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    atm.updateOption(checkBox.getText().toString(), b);
                }
            });

            parentLayout.addView(checkBox); //выводим на экран
        }

        Button nextButton = new Button(this);
        nextButton.setText("ДАЛЕЕ");
        layoutParams= new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        nextButton.setLayoutParams(layoutParams);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), Photos.class);
                startActivity(intent);
            }
        });
        parentLayout.addView(nextButton);

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
}
