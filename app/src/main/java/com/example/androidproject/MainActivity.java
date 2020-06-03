package com.example.androidproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class MainActivity extends AppCompatActivity {


    private static final String TAG = "DeviceInfoActivity";
    ImageLoader imageLoader;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(this));

        preparations();
        prepareListFines();
        prepareListSigns();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try {
            LinearLayout wrap = (LinearLayout) findViewById(R.id.wrapLayout);
            JSONArray jArray = new JSONArray(doGet("http://f0414424.xsph.ru/api/getNews.php?page=1"));
            for (int i = 0; i < jArray.length(); i++) {
                try {
                    LinearLayout linearLayout = new LinearLayout(this);
                    linearLayout.setOrientation(LinearLayout.VERTICAL);
                    linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                    linearLayout.setWeightSum(1.0f);
                    TextView textView1 = new TextView(this);
                    ImageView image = new ImageView(this);
                    WebView webView = new WebView(this);
                    JSONObject oneObject = jArray.getJSONObject(i);
                    // Pulling items from the array
                    final String title = oneObject.getString("Title");
                    final String name = "Автор: " + oneObject.getString("FirstName") + " " + oneObject.getString("LastName");
                    final String text = oneObject.getString("Text");
                    final Bitmap imageSrc = getBitmapFromURL(oneObject.getString("ImgSource"));
                    final String date = oneObject.getString("Date");
                    if (imageSrc != null) {
                        Bitmap extImgSrc = getBitmapFromURL("https://boatparts.com.ua/design/boatparts/images/no_image.png");
                        image.setImageBitmap(extImgSrc);
                    }
                    imageLoader.displayImage(oneObject.getString("ImgSource"), image);
                    textView1.setText(title);
                    textView1.setPaintFlags(textView1.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                    textView1.setTextColor(Color.BLACK);
                    textView1.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    textView1.setTypeface(null, Typeface.BOLD);
                    textView1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                    LinearLayout linearLayout1 = new LinearLayout(this);
                    linearLayout1.setOrientation(LinearLayout.HORIZONTAL);
                    linearLayout1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                    TextView textView2 = new TextView(this);
                    TextView textView3 = new TextView(this);
                    textView2.setText(name);
                    textView3.setText("");
                    View.OnClickListener tes = new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getBaseContext(), Main2Activity.class);
                            Main2Activity.setAuthor(name);
                            Main2Activity.setBitmap(imageSrc);
                            Main2Activity.setDate(date);
                            Main2Activity.setText(text);
                            Main2Activity.setTitleHtml(title);
                            startActivity(intent);
                        }
                    };
                    textView1.setOnClickListener(tes);
                    linearLayout.addView(textView1);
                    linearLayout.addView(image);
                    linearLayout.addView(textView2);
                    linearLayout.addView(textView3);
                    linearLayout.addView(linearLayout1);
                    wrap.addView(linearLayout);
                } catch (JSONException e) {

                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        prepareListFines();
    }

    public static String doGet(String url)
            throws Exception {

        URL obj = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) obj.openConnection();

        //add request header
        connection.setRequestMethod("GET");
        connection.setRequestProperty("User-Agent", "Mozilla/5.0");
        connection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        connection.setRequestProperty("Content-Type", "application/json");

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = bufferedReader.readLine()) != null) {
            response.append(inputLine + "\n");
        }
        bufferedReader.close();

        Log.d(TAG, "Response string: " + response.toString());


        return response.toString();
    }

    public static Bitmap getBitmapFromURL(String src) {

        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void preparations() {

        TabHost tabHost = (TabHost) findViewById(R.id.tabHost);

        tabHost.setup();

        TabHost.TabSpec tabSpec = tabHost.newTabSpec("tag1");
        tabSpec.setContent(R.id.tab1);
        tabSpec.setIndicator("Новости");
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("tag2");
        tabSpec.setContent(R.id.tab2);
        tabSpec.setIndicator("Знаки");
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("tag3");
        tabSpec.setContent(R.id.tab3);
        tabSpec.setIndicator("Штрафы");
        tabHost.addTab(tabSpec);


        tabHost.setCurrentTab(0);

    }

    public void prepareListFines() {
        String[] data = {"Эксплуатация ТС", "Тех. состояние ТС", "Управление ТС",
                "Скоростной режим", "Движение ТС", "Перевозка", "Приченение вреда",
                "Невыполнение требований", "Прочие нарушения"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Spinner spinner = (Spinner) findViewById(R.id.spinner1);
        spinner.setAdapter(adapter);
        // заголовок
        spinner.setPrompt("Список");
        // выделяем элемент
        spinner.setSelection(0);
        // устанавливаем обработчик нажатия
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                try {
                    LinearLayout layout = findViewById(R.id.wrapLayout3);
                    layout.removeAllViews();
                    JSONArray jArray = new JSONArray(doGet("http://f0414424.xsph.ru/api/getFines.php?section=" + String.valueOf(position + 1)));
                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject oneObject = jArray.getJSONObject(i);
                        LinearLayout container = new LinearLayout(getBaseContext());
                        if (i % 2 == 0) {
                            container.setBackgroundColor(Color.RED);
                        } else {
                            container.setBackgroundColor(Color.BLUE);
                        }
                        container.setOrientation(LinearLayout.VERTICAL);
                        container.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));

                        LinearLayout columnSep1 = new LinearLayout(getBaseContext());
                        columnSep1.setWeightSum(1);
                        columnSep1.setOrientation(LinearLayout.HORIZONTAL);
                        columnSep1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                        TextView caoTitle = new TextView(getBaseContext());
                        caoTitle.setTextColor(Color.BLACK);
                        caoTitle.setTextSize(16);
                        caoTitle.setText("КоАП");
                        caoTitle.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        caoTitle.setTypeface(null, Typeface.BOLD);
                        caoTitle.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.75f));
                        TextView caoContent = new TextView(getBaseContext());
                        caoContent.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.25f));
                        caoContent.setTextColor(Color.BLACK);
                        caoContent.setText(oneObject.getString("CAO"));
                        columnSep1.addView(caoTitle);
                        columnSep1.addView(caoContent);

                        LinearLayout columnSep2 = new LinearLayout(getBaseContext());
                        columnSep2.setWeightSum(1);
                        columnSep2.setOrientation(LinearLayout.HORIZONTAL);
                        columnSep2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                        TextView offenseTitle = new TextView(getBaseContext());
                        offenseTitle.setTextColor(Color.BLACK);
                        offenseTitle.setTextSize(16);
                        offenseTitle.setText("Нарушение");
                        offenseTitle.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        offenseTitle.setTypeface(null, Typeface.BOLD);
                        offenseTitle.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.75f));
                        TextView offenseContent = new TextView(getBaseContext());
                        offenseContent.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.25f));
                        offenseContent.setTextColor(Color.BLACK);
                        offenseContent.setText(oneObject.getString("Offense"));
                        columnSep2.addView(offenseTitle);
                        columnSep2.addView(offenseContent);

                        LinearLayout columnSep3 = new LinearLayout(getBaseContext());
                        columnSep3.setWeightSum(1);
                        columnSep3.setOrientation(LinearLayout.HORIZONTAL);
                        columnSep3.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                        TextView sanctionTitle = new TextView(getBaseContext());
                        sanctionTitle.setTextColor(Color.BLACK);
                        sanctionTitle.setTextSize(16);
                        sanctionTitle.setText("Меры");
                        sanctionTitle.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        sanctionTitle.setTypeface(null, Typeface.BOLD);
                        sanctionTitle.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.75f));
                        TextView sanctionContent = new TextView(getBaseContext());
                        sanctionContent.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.25f));
                        sanctionContent.setTextColor(Color.BLACK);
                        sanctionContent.setText(oneObject.getString("Sanctions"));
                        columnSep3.addView(sanctionTitle);
                        columnSep3.addView(sanctionContent);

                        container.addView(columnSep1);
                        container.addView(columnSep2);
                        container.addView(columnSep3);

                        layout.addView(container);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
    }

    public void prepareListSigns() {
        String[] data = {"Предупреждающие знаки", "Знаки приоритета", "Запрещающие знаки",
                "Предписывающие знаки", "Знаки особых предписание", "Информационные знаки",
                "Знаки сервиса", "Знаки дополнительной информации"};
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(this));
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setAdapter(adapter);
        // заголовок
        spinner.setPrompt("Список");
        // выделяем элемент
        spinner.setSelection(0);
        // устанавливаем обработчик нажатия
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                try {
                    LinearLayout layout = findViewById(R.id.wrapLayout1);
                    layout.removeAllViews();
                    JSONArray jArray = new JSONArray(doGet("http://f0414424.xsph.ru/api/getSigns.php?section=" + String.valueOf(position + 1)));
                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject oneObject = jArray.getJSONObject(i);
                        LinearLayout container = new LinearLayout(getBaseContext());
                        if (i % 2 == 0) {
                            container.setBackgroundColor(Color.RED);
                        } else {
                            container.setBackgroundColor(Color.BLUE);
                        }
                        container.setOrientation(LinearLayout.VERTICAL);
                        container.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));

                        TextView title = new TextView(getBaseContext());
                        TextView number = new TextView(getBaseContext());
                        title.setTextColor(Color.BLACK);
                        title.setTextSize(18);
                        title.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        title.setTypeface(null, Typeface.BOLD);
                        number.setTextColor(Color.BLACK);
                        number.setTextSize(18);
                        System.out.println(oneObject.getString("ImgSource"));
                        number.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        number.setTypeface(null, Typeface.BOLD);
                        TextView descr = new TextView(getBaseContext());
                        ImageView imageView = new ImageView(getBaseContext());
                        title.setText(oneObject.getString("Title"));
                        number.setText(oneObject.getString("Number"));
                        descr.setText((oneObject.getString("Description") == null || oneObject.getString("Description").equals("")) ? "Описание отсутствует" : oneObject.getString("Description"));
                        Bitmap imageSrc = getBitmapFromURL(oneObject.getString("ImgSource"));
                        if (imageSrc != null) {
                            Bitmap extImgSrc = getBitmapFromURL("https://boatparts.com.ua/design/boatparts/images/no_image.png");
                            imageView.setImageBitmap(extImgSrc);
                        }
                        imageLoader.displayImage(oneObject.getString("ImgSource"), imageView);
                        System.out.println(oneObject.getString("ImgSource"));
                        container.addView(title);
                        container.addView(descr);
                        container.addView(number);
                        container.addView(imageView);
                        layout.addView(container);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
    }
}
