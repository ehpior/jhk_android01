package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.jaredrummler.android.colorpicker.ColorPickerDialogListener;

public class ColorPickerActivity extends AppCompatActivity implements ColorPickerDialogListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_picker);
    }

    @Override
    public void onColorSelected(int dialogId, final int color) {

        final int invertColor = ~color;
        final String hexColor = String.format("%X", color);
        String hexInvertColor = String.format("%X", invertColor);
        if (BuildConfig.DEBUG) {
            Toast.makeText(this, "id " + dialogId + " c: " + hexColor + " i:" + hexInvertColor, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * @brief : Color Picker dismiss 호출되는 리스너
     * @param dialogId : 종료된 대화상자 고유 아이디
     */
    @Override
    public void onDialogDismissed(int dialogId) {

    }
}
