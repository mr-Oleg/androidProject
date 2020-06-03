package com.example.androidproject;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Base64;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import java.nio.charset.StandardCharsets;

public class Main2Activity extends AppCompatActivity {

    private static String title;
    private static String text;
    private static String author;
    private static String date;
    private static Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        TextView tw = findViewById(R.id.textView);
        WebView vw = findViewById(R.id.web);
        ImageView iw = findViewById(R.id.imageView);
        TextView tw2 = findViewById(R.id.textView2);
        TextView tw3 = findViewById(R.id.textView3);
        tw.setText(getTitleHtml());
        WebSettings settings = vw.getSettings();
        settings.setDefaultTextEncodingName("utf-8");
        vw.loadData(Base64.encodeToString(
                getText().getBytes(StandardCharsets.UTF_8),
                Base64.DEFAULT), // encode in Base64 encoded
                "text/html; charset=utf-8", // utf-8 html content (personal recommendation)
                "base64");
        tw3.setText("Дата: "+getDate());
        iw.setImageBitmap(getBitmap());
        tw2.setText(getAuthor());
    }

    public static String getTitleHtml() {
        return title;
    }

    public static void setTitleHtml(String title) {
        Main2Activity.title = title;
    }

    public static String getText() {
        return text;
    }

    public static void setText(String text) {
        Main2Activity.text = text;
    }

    public static String getAuthor() {
        return author;
    }

    public static void setAuthor(String author) {
        Main2Activity.author = author;
    }

    public static String getDate() {
        return date;
    }

    public static void setDate(String date) {
        Main2Activity.date = date;
    }

    public static Bitmap getBitmap() {
        return bitmap;
    }

    public static void setBitmap(Bitmap bitmap) {
        Main2Activity.bitmap = bitmap;
    }
}
