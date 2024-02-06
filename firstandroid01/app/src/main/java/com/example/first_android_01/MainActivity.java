package com.example.first_android_01;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.StrictMode;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.example.first_android_01.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        Button btn_add = findViewById(R.id.button);
        EditText input = findViewById(R.id.editText);
        EditText output = findViewById(R.id.editText2);
        btn_add.setOnClickListener(v -> {
            try {
                output.setText(getHtml(input.getText().toString()));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            input.clearFocus();
            HideKeyboard(input);
        });
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View currentFocus = getCurrentFocus();
            if (IsShouldHideKeyBoard(currentFocus,ev)) {
                HideKeyboard(currentFocus);
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    private boolean IsShouldHideKeyBoard(View view, MotionEvent event){
        if ((view instanceof EditText)) {
            int[] point = {0, 0};
            view.getLocationInWindow(point);
            int left = point[0], top = point[1], right = left + view.getWidth(),bottom = top +view.getHeight();
            int evX = (int)event.getRawX(), enY = (int)event.getRawY();
            return !((left < evX && evX<=right) && (top <= enY && enY <= bottom));
        }
        return false;
    }

    public  void HideKeyboard(View v) {
        InputMethodManager manager = ((InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE));
        if (manager != null)
            manager.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        v.clearFocus();
    }

    public String getHtml(String requestUrl) throws IOException {
        URL url = new URL(requestUrl);
        StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
        urlConn.setConnectTimeout(5 * 1000);
        urlConn.setReadTimeout(5 * 1000);
        urlConn.setUseCaches(true);
        urlConn.setRequestMethod("GET");
        urlConn.setRequestProperty("Content-Type", "application/json");
        urlConn.addRequestProperty("Connection", "Keep-Alive");
        urlConn.connect();
        String result = "" ;
        if (urlConn.getResponseCode() == 200) {
            result = streamToString(urlConn.getInputStream());
        }
        urlConn.disconnect();
        return result;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private static String streamToString(InputStream is) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = is.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
            baos.close();
            is.close();
            byte[] byteArray = baos.toByteArray();
            return new String(byteArray);
        } catch (Exception e) {
            Log.e("abc", e.toString());
            return null;
        }
    }
}