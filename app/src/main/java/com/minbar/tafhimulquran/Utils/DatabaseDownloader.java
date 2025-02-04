package com.minbar.tafhimulquran.Utils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DatabaseDownloader extends AsyncTask<String, Integer, Boolean> {

    private Context context;
    private ProgressBar progressBar;
    private TextView progressText;
    private DownloadListener listener;

    public interface DownloadListener {
        void onDownloadComplete(String filePath);
        void onDownloadFailed();
    }

    public DatabaseDownloader(Context context, ProgressBar progressBar, TextView progressText, DownloadListener listener) {
        this.context = context;
        this.progressBar = progressBar;
        this.progressText = progressText;
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressBar.setProgress(0);
        progressText.setText("0%");
    }

    @Override
    protected Boolean doInBackground(String... urls) {
        String fileURL = urls[0];
        File dbFile = new File(context.getFilesDir(), "fezilalilquran.db");

        try {
            URL url = new URL(fileURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return false;
            }

            int fileLength = connection.getContentLength();
            InputStream input = connection.getInputStream();
            FileOutputStream output = new FileOutputStream(dbFile);

            byte[] data = new byte[4096];
            long total = 0;
            int count;

            while ((count = input.read(data)) != -1) {
                total += count;
                if (fileLength > 0) { // To avoid division by zero
                    int progress = (int) ((total * 100) / fileLength);
                    publishProgress(progress);
                }
                output.write(data, 0, count);
            }

            output.flush();
            output.close();
            input.close();
            return true;
        } catch (Exception e) {
            Log.e("DownloadError", "Error downloading database", e);
            return false;
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        progressBar.setProgress(values[0]);
        progressText.setText(values[0] + "%");
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if (result) {
            listener.onDownloadComplete(context.getFilesDir() + "/fezilalilquran.db");
        } else {
            listener.onDownloadFailed();
        }
    }
}
