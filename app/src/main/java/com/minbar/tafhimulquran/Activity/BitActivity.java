package com.minbar.tafhimulquran.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.minbar.tafhimulquran.R;
import com.minbar.tafhimulquran.Utils.SqlLiteDbHelper;
import com.minbar.tafhimulquran.Utils.ThemeManager;
import com.minbar.tafhimulquran.databinding.ActivityBitBinding;
import com.skydoves.colorpickerview.ColorPickerDialog;
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

import es.dmoral.toasty.Toasty;

public class BitActivity extends AppCompatActivity {

    private SqlLiteDbHelper dbHelper;
    private String fileName;
    private ActivityBitBinding binding;
    private static final int PERMISSION_REQUEST_CODE = 100;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.applyTheme(this);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_bit);

        dbHelper = SqlLiteDbHelper.getInstance(this);
        initData();
        setupToolbar();
        setupClickListeners();
    }

    private void initData() {
        Intent intent = getIntent();
        if (intent == null) return;

        String surahIdStr = intent.getStringExtra("surah_id");
        int surah_id = 1;
        if (surahIdStr != null) {
            try {
                surah_id = Integer.parseInt(surahIdStr);
            } catch (NumberFormatException ignored) {}
        }
        
        String verse_id = intent.getStringExtra("verse_id");
        String verse_en = intent.getStringExtra("verse_en");
        String arabicTxt = intent.getStringExtra("arabicTxt");
        String banglaTxt = intent.getStringExtra("banglaTxt");

        if (verse_id == null) verse_id = "";
        if (verse_en == null) verse_en = "";
        if (arabicTxt == null) arabicTxt = "";
        if (banglaTxt == null) banglaTxt = "";

        fileName = "Tafheem_Surah" + surah_id + "_Ayat" + verse_en + "_" + System.currentTimeMillis() + ".png";

        binding.arabic.setText(arabicTxt);

        String cleanedBangla = banglaTxt.replaceAll("[০-৯]", "")
                .replace(" - তাফহীমুল কুরআন", "")
                .trim();
        binding.bangla.setText(cleanedBangla);
        
        String surahName = dbHelper.getSurahName(surah_id);
        binding.ref.setText(surahName + ", আয়াতঃ " + verse_id);

        adjustTextSizes(arabicTxt.length(), cleanedBangla.length());
    }

    private void setupToolbar() {
        MaterialToolbar toolbar = binding.toolBar;
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("আয়াত পোস্টার");
        }
    }

    private void adjustTextSizes(int arLength, int bnLength) {
        // Dynamic Arabic Text Size
        if (arLength > 650) binding.arabic.setTextSize(18);
        else if (arLength > 400) binding.arabic.setTextSize(20);
        else if (arLength > 200) binding.arabic.setTextSize(24);
        else binding.arabic.setTextSize(28);

        // Dynamic Bangla Text Size
        if (bnLength > 600) binding.bangla.setTextSize(14);
        else if (bnLength > 400) binding.bangla.setTextSize(16);
        else if (bnLength > 200) binding.bangla.setTextSize(18);
        else binding.bangla.setTextSize(20);
    }

    private void setupClickListeners() {
        binding.btTxtColor.setOnClickListener(v -> showColorPicker("টেক্স কালার সিলেক্ট করুন", (envelope, fromUser) -> {
            int color = envelope.getColor();
            binding.arabic.setTextColor(color);
            binding.bangla.setTextColor(color);
            binding.ref.setTextColor(color);
        }));

        binding.btColor.setOnClickListener(v -> showColorPicker("ব্যাকগ্রাউন্ড কালার সিলেক্ট করুন", (envelope, fromUser) -> binding.colorOut.setBackgroundColor(envelope.getColor())));

        binding.btSave.setOnClickListener(v -> {
            if (checkStoragePermission()) {
                saveImageToGallery(getBitmapFromView(binding.saveLayout));
            } else {
                requestStoragePermission();
            }
        });

        binding.btShare.setOnClickListener(v -> sharePoster());
    }

    private void showColorPicker(String title, ColorEnvelopeListener listener) {
        new ColorPickerDialog.Builder(this)
                .setTitle(title)
                .setPreferenceName("ColorPicker_" + title.hashCode())
                .setPositiveButton(getString(R.string.ok), listener)
                .setNegativeButton(getString(R.string.cancel), (dialog, which) -> dialog.dismiss())
                .attachAlphaSlideBar(true)
                .attachBrightnessSlideBar(true)
                .show();
    }

    private void sharePoster() {
        Bitmap bitmap = getBitmapFromView(binding.saveLayout);
        if (bitmap == null) {
            Toasty.error(this, "ইমেজ তৈরি করতে সমস্যা হয়েছে", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            File cachePath = new File(getExternalCacheDir(), "images");
            if (!cachePath.exists()) {
                cachePath.mkdirs();
            }
            File file = new File(cachePath, "shared_poster.png");
            FileOutputStream stream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.close();

            Uri contentUri = FileProvider.getUriForFile(this, getPackageName() + ".provider", file);

            if (contentUri != null) {
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                shareIntent.setDataAndType(contentUri, "image/png");
                shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
                startActivity(Intent.createChooser(shareIntent, "শেয়ার করুন"));
            } else {
                Toasty.error(this, "শেয়ার করতে সমস্যা হয়েছে", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toasty.error(this, "শেয়ার করতে সমস্যা হয়েছে: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void saveImageToGallery(Bitmap bitmap) {
        OutputStream fos = null;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/png");
                contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/TafheemPoster");

                // Ensure the directory exists
                File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "TafheemPoster");
                if (!directory.exists()) {
                    directory.mkdirs();
                }

                Uri imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                if (imageUri != null) {
                    fos = getContentResolver().openOutputStream(imageUri);
                }
            } else {
                File imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                File tafheemDir = new File(imagesDir, "TafheemPoster");
                if (!tafheemDir.exists()) {
                    tafheemDir.mkdirs();
                }
                File image = new File(tafheemDir, fileName);
                fos = new FileOutputStream(image);
            }

            if (fos != null) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.close();
                Toasty.success(this, "গ্যালারিতে সেভ হয়েছে", Toast.LENGTH_SHORT).show();
            } else {
                Toasty.error(this, "সেভ করতে সমস্যা হয়েছে", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toasty.error(this, "সেভ করতে সমস্যা হয়েছে: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean checkStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED;
        } else {
            return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
    }

    private void requestStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_MEDIA_IMAGES}, PERMISSION_REQUEST_CODE);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                saveImageToGallery(getBitmapFromView(binding.saveLayout));
            } else {
                Toasty.warning(this, "পারমিশন প্রয়োজন", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private Bitmap getBitmapFromView(View view) {
        // Measure the view if it hasn't been measured yet
        if (view.getWidth() <= 0 || view.getHeight() <= 0) {
            view.measure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED));
            view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        }

        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        Drawable bgDrawable = view.getBackground();

        if (bgDrawable != null) {
            bgDrawable.draw(canvas);
        } else {
            // Use the background color from the root view if no background is set
            View rootView = view.getRootView();
            if (rootView != null) {
                int backgroundColor = Color.WHITE;
                try {
                    backgroundColor = ((ColorDrawable) rootView.getBackground()).getColor();
                } catch (ClassCastException e) {
                    // Not a ColorDrawable, use default white
                }
                canvas.drawColor(backgroundColor);
            } else {
                canvas.drawColor(Color.WHITE);
            }
        }

        view.draw(canvas);
        return returnedBitmap;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
