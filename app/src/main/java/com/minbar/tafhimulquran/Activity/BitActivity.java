package com.minbar.tafhimulquran.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.preference.PreferenceManager;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.view.MenuItem;
import android.view.View;

import com.minbar.tafhimulquran.R;
import com.minbar.tafhimulquran.Utils.SqlLiteDbHelper;
import com.minbar.tafhimulquran.databinding.ActivityBitBinding;
import com.skydoves.colorpickerview.ColorEnvelope;
import com.skydoves.colorpickerview.ColorPickerDialog;
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

import es.dmoral.toasty.Toasty;

public class BitActivity extends AppCompatActivity {




    SqlLiteDbHelper dbHelper;
    String fileName;

    ActivityBitBinding binding;

    private int EXTERNAL_STORAGE_PERMISSION_CODE = 23;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_bit);

        Intent is = getIntent();
        int surah_id = Integer.parseInt(is.getStringExtra("surah_id"));
        dbHelper = new SqlLiteDbHelper(this);

        Objects.requireNonNull(getSupportActionBar()).setTitle(dbHelper.getSurahName(surah_id)+" : "+is.getStringExtra("verse_id"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fileName = "xoventech_s"+is.getStringExtra("surah_id")+"_v"+is.getStringExtra("verse_en")+".png";

        binding.arabic.setText(is.getStringExtra("arabicTxt"));

        String getbangla = is.getStringExtra("banglaTxt").replace("০","").replace("১","").replace("২","").replace("৩","").replace("৪","").replace("৫","").replace("৬","").replace("৭","").replace("৮","").replace("৯","").replace(" - তাফহীমুল কুরআন","");

        binding.bangla.setText(getbangla);
        binding.ref.setText("-- "+dbHelper.getSurahName(surah_id)+", আয়াতঃ "+is.getStringExtra("verse_id")+" --");

        int arLength = is.getStringExtra("arabicTxt").length();
        int bnLength = is.getStringExtra("banglaTxt").length();

        if (arLength > 650) {
            binding.arabic.setTextSize(16);
        } else if (arLength > 500) {
            binding.arabic.setTextSize(17);
        } else if (arLength > 350) {
            binding.arabic.setTextSize(19);
        } else if (arLength > 220) {
            binding.arabic.setTextSize(20);
        } else if (arLength > 150) {
            binding.arabic.setTextSize(22);
        } else if (arLength > 100) {
            binding.arabic.setTextSize(26);
        } else if (arLength > 50) {
            binding.arabic.setTextSize(30);
        } else {
            binding.arabic.setTextSize(32);
        }




        if (bnLength > 680) {
            binding.bangla.setTextSize(13);
        } else if (bnLength > 500) {
            binding.bangla.setTextSize(14);
        } else if (bnLength > 350) {
            binding.bangla.setTextSize(15);
        } else if (bnLength > 220) {
            binding.bangla.setTextSize(16);
        } else if (bnLength > 150) {
            binding.bangla.setTextSize(18);
        } else if (bnLength > 100) {
            binding.bangla.setTextSize(22);
        } else if (bnLength > 50) {
            binding.bangla.setTextSize(24);
        } else {
            binding.bangla.setTextSize(26);
        }
        binding.btTxtColor.setColorFilter(ContextCompat.getColor(this,
                R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);
        binding.btTxtColor.setOnClickListener(v -> {


            new ColorPickerDialog.Builder(this)
                    .setTitle("টেক্স কালার সিলেক্ট করুন")
                    .setPreferenceName("MyColorPickerDialog")
                    .setPositiveButton("Select",
                            new ColorEnvelopeListener() {
                                @Override
                                public void onColorSelected(ColorEnvelope envelope, boolean fromUser) {
                                    //setLayoutColor(envelope);
                                    binding.arabic.setTextColor(envelope.getColor());
                                    binding.bangla.setTextColor(envelope.getColor());
                                    binding.ref.setTextColor(envelope.getColor());
                                }
                            })
                    .setNegativeButton(getString(R.string.cancel),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            })
                    .attachAlphaSlideBar(true) // the default value is true.
                    .attachBrightnessSlideBar(true)  // the default value is true.
                    .setBottomSpace(12) // set a bottom space between the last slidebar and buttons.
                    .show();

        });

        binding.btSave.setColorFilter(ContextCompat.getColor(this,
                R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);
        binding.btSave.setOnClickListener(v -> {
            if (checkPermission()){
                File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

                Bitmap bitmap = getBitmapFromView(binding.saveLayout);
                try {
                    File file = new File(path,fileName);
                    if(file.exists())
                        file.delete();
                    file.createNewFile();

                    FileOutputStream outputStream = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                    outputStream.flush();
                    outputStream.close();
                    file.setReadable(true, false);

                    Toasty.success(getApplicationContext(), "File saved...", Toasty.LENGTH_SHORT).show();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }requestPermission();
        });

        binding.btColor.setColorFilter(ContextCompat.getColor(this,
                R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);
        binding.btColor.setOnClickListener(v -> {


            new ColorPickerDialog.Builder(this)
                    .setTitle("ব্যাকগ্রাউন্ড কালার সিলেক্ট করুন")
                    .setPreferenceName("MyColorPickerDialog")
                    .setPositiveButton("Select",
                            new ColorEnvelopeListener() {
                                @Override
                                public void onColorSelected(ColorEnvelope envelope, boolean fromUser) {
                                    //setLayoutColor(envelope);
                                    binding.colorOut.setBackgroundColor(envelope.getColor());
                                }
                            })
                    .setNegativeButton(getString(R.string.cancel),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            })
                    .attachAlphaSlideBar(true) // the default value is true.
                    .attachBrightnessSlideBar(true)  // the default value is true.
                    .setBottomSpace(12) // set a bottom space between the last slidebar and buttons.
                    .show();

        });


        binding.btShare.setColorFilter(ContextCompat.getColor(this,
                R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);
        binding.btShare.setOnClickListener(v -> {
            Bitmap bitmap = getBitmapFromView(binding.saveLayout);
            try {

                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());
                builder.detectFileUriExposure();


                File file = new File(BitActivity.this.getExternalCacheDir(),fileName);
                FileOutputStream outputStream = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                outputStream.flush();
                outputStream.close();
                file.setReadable(true, false);

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);


                /*
                Uri apkURI = FileProvider.getUriForFile(
                        this,
                        this.getApplicationContext()
                                .getPackageName() + ".provider", file);
                intent.setDataAndType(apkURI, "image/png");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);


                 */


               intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                intent.setType("image/png");
                startActivity(Intent.createChooser(intent,"Share By"));

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });


    }

    public boolean checkPermission() {
        if (ContextCompat.checkSelfPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE") == 0) {
            return true;
        }
        return false;
    }

    public void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{"android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE"}, this.EXTERNAL_STORAGE_PERMISSION_CODE);
    }



    private Bitmap getBitmapFromView(View view) {
        //Define a bitmap with the same size as the view
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),Bitmap.Config.ARGB_8888);
        //Bind a canvas to it
        Canvas canvas = new Canvas(returnedBitmap);
        //Get the view's background
        Drawable bgDrawable =view.getBackground();
        if (bgDrawable!=null) {
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas);
        }   else{
            //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.WHITE);
        }
        // draw the view on the canvas
        view.draw(canvas);
        //return the bitmap
        return returnedBitmap;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}