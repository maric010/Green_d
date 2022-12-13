package com.example.map;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class Form7 extends AppCompatActivity {
    static Form7 th;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form7);
    }
    int request_code=0;

    ArrayList<Uri> urist = new ArrayList<Uri>();
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {

                    try {
                        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && data != null) {

                            CropImage.ActivityResult result = CropImage.getActivityResult(data);
                            FileInputStream in = new FileInputStream(result.getUri().getPath());
                            my.file_avatar = server.send_file("203.png", in);
                            in.close();
                            if(my.file_avatar)
                            Form7.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    CircleImageView imageView = findViewById(R.id.avatar);
                                    Glide.with(Form7.this)
                                            .load(result.getUri())
                                            .into(imageView);
                                }
                            });
                        } else {
                            final Uri imageUri = data.getData();
                            InputStream imageStream = getContentResolver().openInputStream(imageUri);
                            InputStream imageStream2= getContentResolver().openInputStream(imageUri);
                            boolean result = server.send_file(requestCode + ".png", imageStream);
                            imageStream.close();
                            Form7.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream2);
                                    if (requestCode == 1) {
                                        my.file_tex = result;
                                        if(result)
                                            ((ImageView) findViewById(R.id.photo_vu1)).setImageBitmap(selectedImage);
                                    } else if (requestCode == 2) {
                                        my.file_vu1 = result;
                                        if(result)
                                            ((ImageView) findViewById(R.id.photo_vu2)).setImageBitmap(selectedImage);
                                    } else if (requestCode == 3)
                                    {
                                        my.file_vu2 = result;
                                        if(result)
                                            ((ImageView) findViewById(R.id.photo_tex1)).setImageBitmap(selectedImage);
                                    }
                                    else if (requestCode == 4) {
                                        my.file_a1 = result;
                                        if(result)
                                            ((ImageView) findViewById(R.id.photo_avto1)).setImageBitmap(selectedImage);
                                    } else if (requestCode == 5) {
                                        my.file_a2 = result;
                                        if(result)
                                            ((ImageView) findViewById(R.id.photo_avto2)).setImageBitmap(selectedImage);
                                    } else if (requestCode == 6) {
                                        my.file_a3 = result;
                                        if(result)
                                            ((ImageView) findViewById(R.id.photo_avto3)).setImageBitmap(selectedImage);
                                    } else if (requestCode == 7) {
                                        my.file_a4 = result;
                                        if(result)
                                            ((ImageView) findViewById(R.id.photo_avto4)).setImageBitmap(selectedImage);
                                    }
                                }
                            });

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
        }

    }

    public void set_image_onClick(View view) {

        request_code=0;
        CropImage.activity().setAspectRatio(1,1).start(this);
    }

    public void send_onclick(View view) {
        if(my.file_vu1 && my.file_a2 && my.file_vu2 && my.file_a3 && my.file_a1 && my.file_a4 && my.file_avatar && my.file_tex){
            String nomer_vu = ((EditText)findViewById(R.id.nomer_vu)).getText().toString();
            String kogda_vidan = ((EditText)findViewById(R.id.kogda_vidan)).getText().toString();
            String model_marka_avto = ((EditText)findViewById(R.id.model_marka_avto)).getText().toString();
            String nomer_avto = ((EditText)findViewById(R.id.nomer_avto)).getText().toString();
            String color_avto = ((EditText)findViewById(R.id.color)).getText().toString();
            String year_avto = ((EditText)findViewById(R.id.year_avto)).getText().toString();
            Form7.th=this;
            server.send("accept_info|"+nomer_vu+"|"+kogda_vidan+"|"+model_marka_avto+"|"+nomer_avto+"|"+color_avto+"|"+year_avto,true);
        }


    }

    public void set_vu1(View view) {

        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent,1);
    }

    public void set_vu2(View view) {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent,2);
    }

    public void set_texpas(View view) {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent,3);
    }

    public void set_car1(View view) {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent,4);
    }

    public void set_car2(View view) {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent,5);
    }

    public void set_car3(View view) {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent,6);
    }

    public void set_car4(View view) {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent,7);
    }
}