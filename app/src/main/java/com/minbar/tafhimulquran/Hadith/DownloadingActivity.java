package com.minbar.tafhimulquran.Hadith;



import android.Manifest;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


import com.minbar.tafhimulquran.Activity.MainActivity;
import com.minbar.tafhimulquran.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadingActivity extends AppCompatActivity {

    private static final String TAG = "DownloadingActivity";
    private static final int REQUEST_PERMISSIONS = 1;
    private static final String DATABASE_URL = "https://download.onedrive.live.com/download.aspx?cid=065fb95c5353287b&resid=9907ff50-c505-4041-8e9b-d539a03062d9";
    private static final String DATABASE_NAME = "hadith.db";

    private ProgressBar progressBar;
    private TextView progressText;

    private int EXTERNAL_STORAGE_PERMISSION_CODE = 23;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downloading);

        Button downloadButton = findViewById(R.id.downloadButton);
        progressBar = findViewById(R.id.progressBar);
        progressText = findViewById(R.id.progressText);

        // Check permissions
        if (!checkPermissions()) {
            requestPermissions();
        }

        downloadButton.setOnClickListener(v -> {
            if (checkPermissions()) {
                new DownloadDatabaseTask().execute(DATABASE_URL);
            } else {
                Toast.makeText(this, "Please grant necessary permissions!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean checkPermissions() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                REQUEST_PERMISSIONS);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission denied. Unable to download the database.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class DownloadDatabaseTask extends AsyncTask<String, Integer, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            progressText.setVisibility(View.VISIBLE);
        }

        @Override
        protected Boolean doInBackground(String... urls) {
            String fileUrl = urls[0];
            String filePath = getApplicationContext().getFilesDir() + "/" + DATABASE_NAME;

            Log.d(TAG, "Downloading from: " + fileUrl);
            Log.d(TAG, "Saving to: " + filePath);

            try {
                URL url = new URL(fileUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    Log.e(TAG, "Server returned HTTP " + connection.getResponseCode() + " " + connection.getResponseMessage());
                    return false;
                }

                int fileLength = connection.getContentLength();

                InputStream input = connection.getInputStream();
                FileOutputStream output = new FileOutputStream(new File(filePath));

                byte[] buffer = new byte[4096];
                long total = 0;
                int bytesRead;

                while ((bytesRead = input.read(buffer)) != -1) {
                    total += bytesRead;
                    output.write(buffer, 0, bytesRead);

                    if (fileLength > 0) {
                        int progress = (int) (total * 100 / fileLength);
                        publishProgress(progress);
                    }
                }

                output.close();
                input.close();
                return true;

            } catch (Exception e) {
                Log.e(TAG, "Error downloading file", e);
                return false;
            }
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            progressBar.setProgress(progress[0]);
            progressText.setText(progress[0] + "%");
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            progressBar.setVisibility(View.GONE);
            progressText.setVisibility(View.GONE);

            if (result) {
                Toast.makeText(DownloadingActivity.this, "Database downloaded successfully!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(DownloadingActivity.this, "Failed to download database", Toast.LENGTH_SHORT).show();
            }
        }
    }
}