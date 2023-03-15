package com.GraphicsImageDecoderTest.DecoderTest;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.ImageDecoder;
import android.graphics.drawable.Drawable;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.myapplication.R;

import java.io.IOException;

public class graphicsImageDecoder extends AppCompatActivity
{
    // Initialize variable
    Button btnSelect,btnDecode;
    TextView textView;
    ImageView dView;
    ImageView bmView;
    ImageDecoder.Source source;
    Drawable iDrawable;
    Bitmap iBitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnSelect = findViewById(R.id.btn_select);
        btnDecode = findViewById(R.id.btn_decode);
        textView = findViewById(R.id.textView);
        dView = findViewById(R.id.dView);
        bmView = findViewById(R.id.bmView);

        // Code for Encode button
        btnSelect.setOnClickListener(view -> {

            // check condition
            if (ContextCompat.checkSelfPermission(graphicsImageDecoder.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
            {
                // when permission is nor granted
                // request permission
                ActivityCompat.requestPermissions(graphicsImageDecoder.this
                        , new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},100);

            }
            else
            {
                // when permission is granted create method
                selectImage();
            }
        });

        // Code for Decode button
        btnDecode.setOnClickListener(view -> {
            // display image from drawable object
            bmView.setImageBitmap(iBitmap);
            dView.setImageDrawable(iDrawable);
        });
    }

    public void selectImage()
    {
        // clear previous data
        textView.setText("");
        bmView.setImageBitmap(null);
        dView.setImageDrawable(null);
        // Initialize intent
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        Intent chooserIntent = Intent.createChooser(intent,"Select Image");
        // start activity result
        startActivityForResult(chooserIntent,100);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // check condition
        if (requestCode==100 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
        {
            // when permission is granted call method
            selectImage();
        }
        else
        {
            // when permission is denied
            Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        // check condition
        if (requestCode==100 && resultCode==RESULT_OK && data!=null)
        {
            // when result is ok
            // initialize uri
            Uri uri=data.getData();
            try
            {
                // set image's uri on textview
                textView.setText(uri.toString());
                long startTime = System.nanoTime();
                // create ImageDecoder source
                source = ImageDecoder.createSource(getContentResolver(), uri);
                // create bitmap object
                iBitmap = ImageDecoder.decodeBitmap(source);
                long stopTime = System.nanoTime();
                Log.i("PERF", "Execution time: " + ((stopTime - startTime)/1000000) + " ms");
                // create drawable object
                iDrawable = ImageDecoder.decodeDrawable(source);

            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
}
