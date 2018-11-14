package com.art.mydirtyatm;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Toast;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import static com.art.mydirtyatm.OptionsActivity.atm;


public class Photos extends AppCompatActivity {

    private static final int CAMERA_REQUEST = 1888;
    GridLayout imageGrid;
    int x = 25;
    int y = 50;
    Uri mImageCaptureUri1;
    Map options;
    File filePath;
    String currentDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photos);
        imageGrid = findViewById(R.id.imageGrid);
        showPhotos();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        MenuItem item=menu.add("Добавить фото");
        item.setIcon(R.drawable.plus_photo);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                mImageCaptureUri1 = (Uri.fromFile(new File(Environment.getExternalStorageDirectory(),String.valueOf(atm.getImageCounter()) + ".png")));
                atm.increaseImageCounter();
                atm.images.add(mImageCaptureUri1);
                cameraIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri1);
                cameraIntent.putExtra("return-data", true);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
                return false;
            }
        });

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

    //сохраняем все в документ
    public void onSaveButtonClick(View view) {

        //диалог да нет
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Осмотр закончен?");
        builder.setMessage("Сохранить в файл и отправить?");

        builder.setPositiveButton("Да", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                createPDFFile();
                sendFileToEmail();
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("Нет", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    //функция выводит массив картинок в грид на экране
    private void showPhotos() {

       //очищаем грид
        imageGrid.removeAllViews();

        //для определения ширины/высоты экрана устройства след код
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenHeight = displayMetrics.heightPixels - 40;
        int screenWidth = displayMetrics.widthPixels - 40;

        //перебираем массив картинок
       for(int i = atm.images.size()-1; i >= 0; i--) {
           ImageView ph = new ImageView(this);                                                      //создаем объект имейджвью
           GridLayout.LayoutParams lp = new GridLayout.LayoutParams(imageGrid.getLayoutParams());           //подтаскиваем ему параметры

           lp.width = screenWidth / 3;     //3 фотки в ряд
           lp.height = screenHeight / 5;
           lp.leftMargin = 10;
           lp.topMargin = 10;
           ph.setLayoutParams(lp);

           //связываем УРИ с фоткой и выводим ее в грид
           try {
               Bitmap tempBmp = MediaStore.Images.Media.getBitmap(this.getContentResolver(), atm.images.get(i));
               ph.setImageBitmap(cropPhoto(tempBmp));
           } catch (IOException e) {
               e.printStackTrace();
           }
           imageGrid.addView(ph);                                                                           //показываем картинку
       }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            showPhotos();
        }
    }

   //сохраняем всю эту галиматью в пдфник
    private void createPDFFile () {

        int pageWidth = 584;

        //форматируем дату и время
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        currentDate = sdf.format(new Date());
        sdf = new SimpleDateFormat("HH-mm");
        String currentTime = sdf.format(new Date());


        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, 862, 1).create();

        PdfDocument.Page page = document.startPage(pageInfo);
        Canvas canvas = page.getCanvas();
        Paint paint = new Paint();
        paint.setTextSize(16);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawText("Акт проверки качества работ по мытью и брендированию УС", x, y, paint);

        paint.setTextSize(14);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        canvas.drawText("Модель: " + atm.getModel(), x, increaseYBy20(), paint);
        canvas.drawText("Серийный номер: " + atm.getSerialNumber(), x, increaseYBy20(), paint);
        canvas.drawText("Системный номер: " + atm.getSystemNumber(), x, increaseYBy20(), paint);
        canvas.drawText("Адрес установки: " + atm.getAtmLocation(), x, increaseYBy20(), paint);
        canvas.drawText("Дата проверки: " + currentDate, x, increaseYBy20(), paint);
        canvas.drawText("Время проверки: " + currentTime, x, increaseYBy20(), paint);

        canvas.drawText("Настоящий АКТ составлен в том, что проведена проверка качества работ", x, increaseYBy30(), paint);
        canvas.drawText("по мытью и брендированию УС.", x, increaseYBy20(), paint);

        canvas.drawText("Состояние УС на дату проверки: " + atm.getCondition(), x, increaseYBy30(), paint);

        canvas.drawText("Замечания по мытью и брендированию УС: ", x, increaseYBy30(), paint);

        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        //фигачим сет для перебора хэшмэпа
        options = atm.getOptions();
        Set<Map.Entry<String, Boolean>> optionsSet = options.entrySet();

        //в цикле проверяем значения, если истина, то выводим ключ в ПДФник
        for (Map.Entry<String, Boolean> option :optionsSet) {
            if (option.getValue()) {
                canvas.drawText(option.getKey(), x, increaseYBy20(), paint);
            }
        }
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));

        canvas.drawText("Приложения: фотоматериалы на _" + atm.images.size() + "_ листах.", x, increaseYBy30(), paint);

        canvas.drawText("Подпись и ФИО проверяющего:____________________" + atm.getFio(), x, increaseYBy30(), paint);

        canvas.drawText("Выводы представителей Заказчика: УС прошло проверку успешно/неуспешно", x, increaseYBy30(), paint);

        canvas.drawText("Особое мнение представителя Исполнителя*:", x, increaseYBy30(), paint);

        canvas.drawText("____________________________________________________________________", x, increaseYBy30(), paint);
        canvas.drawText("____________________________________________________________________", x, increaseYBy20(), paint);

        canvas.drawText("От Заказчика                                          От Исполнителя", x, increaseYBy30(), paint);
        increaseYBy30();
        increaseYBy30();

        canvas.drawText("________________________                     _______________________", x, increaseYBy30(), paint);
        canvas.drawText("      ФИО                                                            ФИО         ", x, increaseYBy20(), paint);

        canvas.drawText("Экземпляр Акта получил представитель Исполнителя ________________ФИО", x, increaseYBy30(), paint);

        canvas.drawText("*указывается особое мнение или «особое мнение отсутствует»", x, increaseYBy30(), paint);

        document.finishPage(page);

        for (int i = 0; i < atm.images.size(); i++) {
            page = document.startPage(pageInfo);
            canvas = page.getCanvas();
            Bitmap newIm;
            try {
                newIm = MediaStore.Images.Media.getBitmap(this.getContentResolver(), atm.images.get(i));
                newIm = Bitmap.createScaledBitmap(newIm,pageWidth - 20, setOutHeight(newIm, pageWidth - 20), false);
                canvas.drawBitmap(newIm, 10, 10, null);
            } catch (IOException e) {
                e.printStackTrace();
            }

            document.finishPage(page);
        }
        
        //пишем
        //проверяем наличие каталога. Если нет, создаем
        String targetDir = this.getFilesDir().getPath() + "/shared";
        File tarDir = new File(targetDir);
        if (!tarDir.exists()) {
            tarDir.mkdirs();
        }

        //чистим каталог от прошлых файлов
        for(File tempFile : tarDir.listFiles()) {
            tempFile.delete();
        }

        //присобачиваем имя файла
        String targetPdf = targetDir + "/checklist_of_" + atm.getSystemNumber() + "_at_" + currentDate + ".pdf";
        filePath = new File(targetPdf);

        //пытаемся записать файл
        try {
            document.writeTo(new FileOutputStream(filePath));
            Toast.makeText(this, "Файл сохранен. Запускаю почтовое приложение.", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Хрень какая то: " + e.toString(),
                    Toast.LENGTH_LONG).show();
            Toast.makeText(this, "Файл:", Integer.parseInt(targetPdf)).show();
        }

        //закрываем
        document.close();
    }

    private int increaseYBy20 () {
        y = y + 20;
        return y;
    }

    private int increaseYBy30 () {
        y = y + 30;
        return y;
    }

    //подгоняет под размер высоту картинки в документе ПДФ
    private int setOutHeight(Bitmap pic, int pageWidth) {

        double realHeight = pic.getHeight();
        double realWidth = pic.getWidth();

        double percent = pageWidth / realWidth;

        return (int) (realHeight * percent);

    }

    //отсылаем по почте
    private Boolean sendFileToEmail() {

        if (!filePath.exists() || !filePath.canRead()) {
            return false;
        }

        //расшариваем файл для активности почтового приложения
        File sharedFile = filePath;
        Uri sharedFileUri = FileProvider.getUriForFile(this, "com.art.mydirtyatm.fileprovider", sharedFile);

        //собираем информацию для отрправки
        ShareCompat.IntentBuilder intentBuilder = ShareCompat.IntentBuilder
                .from(this)
                .addEmailTo(atm.getEmail())
                .setChooserTitle("Выберите программу для отправки")
                .setSubject("Акт осмотра УС №" + atm.getSystemNumber() + " по клинингу от " + currentDate)
                .setText("Акт осмотра УС №" + atm.getSystemNumber())
                .setType("text/plain")
                .addStream(sharedFileUri);

        //создаем интент и вывзываем программу отправки
        Intent chooserIntent = intentBuilder.createChooserIntent();
        startActivity(chooserIntent);

        return true;
    }

    //режет картинку под размер иконки, чтоб ровно и красиво
    private Bitmap cropPhoto(Bitmap srcBmp) {
        if (srcBmp.getWidth() >= srcBmp.getHeight()){

           return Bitmap.createBitmap(
                    srcBmp,
                    srcBmp.getWidth()/2 - srcBmp.getHeight()/2,
                    0,
                    srcBmp.getHeight(),
                    srcBmp.getHeight()
            );

        }else{

            return Bitmap.createBitmap(
                    srcBmp,
                    0,
                    srcBmp.getHeight()/2 - srcBmp.getWidth()/2,
                    srcBmp.getWidth(),
                    srcBmp.getWidth()
            );
        }
    }
}
