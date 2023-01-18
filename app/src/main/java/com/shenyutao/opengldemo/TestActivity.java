package com.shenyutao.opengldemo;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.shenyutao.opengldemo.databinding.ActivityTestBinding;

public class TestActivity extends AppCompatActivity {
    private ActivityTestBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTestBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initView();
    }

    private void initView(){
        String imagePath = getExternalFilesDir(null) + "/j20.png";
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        binding.image.setImageBitmap(bitmap);
    }


    @Override
    protected void onDestroy() {
        binding = null;
        super.onDestroy();
    }
}