package com.example.appcomics.Model;
import android.app.ProgressDialog; // Thêm import này
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.pdf.PdfDocument;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class PdfCreator {

    public void createPdf(Context context, String comicName, List<String> imageUrls) {
        new CreatePdfTask(context, comicName, imageUrls).execute();
    }

    private static class CreatePdfTask extends AsyncTask<Void, Void, File> {
        private final Context context;
        private final String comicName;
        private final List<String> imageUrls;
        private ProgressDialog progressDialog;

        public CreatePdfTask(Context context, String comicName, List<String> imageUrls) {
            this.context = context;
            this.comicName = comicName;
            this.imageUrls = imageUrls;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Đang tải và tạo PDF...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected File doInBackground(Void... voids) {
            File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File comicDir = new File(downloadsDir, comicName);
            if (!comicDir.exists() && !comicDir.mkdirs()) {
                Log.e("PDF Error", "Không thể tạo thư mục: " + comicDir.getAbsolutePath());
                return null;
            }

            File pdfFile = new File(comicDir, comicName + ".pdf");
            PdfDocument pdfDocument = new PdfDocument();
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(842, 1191, 1).create();

            try {
                for (String imageUrl : imageUrls) {
                    Bitmap bitmap = downloadImage(imageUrl);
                    if (bitmap != null) {
                        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
                        Canvas canvas = page.getCanvas();

                        float pdfWidth = pageInfo.getPageWidth();
                        float pdfHeight = pageInfo.getPageHeight();
                        float bitmapWidth = bitmap.getWidth();
                        float bitmapHeight = bitmap.getHeight();

                        float scale = Math.min(pdfWidth / bitmapWidth, pdfHeight / bitmapHeight);
                        float scaledWidth = bitmapWidth * scale;
                        float scaledHeight = bitmapHeight * scale;

                        float x = (pdfWidth - scaledWidth) / 2;
                        float y = (pdfHeight - scaledHeight) / 2;

                        RectF destRect = new RectF(x, y, x + scaledWidth, y + scaledHeight);
                        canvas.drawBitmap(bitmap, null, destRect, null);

                        pdfDocument.finishPage(page);
                        bitmap.recycle(); // Giải phóng bộ nhớ
                    } else {
                        Log.e("PDF Error", "Không thể tải ảnh từ URL: " + imageUrl);
                    }
                }

                pdfDocument.writeTo(new FileOutputStream(pdfFile));
            } catch (IOException e) {
                Log.e("PDF Error", "Lỗi khi tạo PDF: " + e.getMessage());
                return null;
            } finally {
                pdfDocument.close();
            }

            return pdfFile;
        }

        @Override
        protected void onPostExecute(File pdfFile) {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }

            if (pdfFile != null && pdfFile.exists()) {
                Toast.makeText(context, "PDF đã được lưu tại: " + pdfFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(context, "Lỗi khi tạo PDF. Vui lòng thử lại.", Toast.LENGTH_LONG).show();
            }
        }

        private Bitmap downloadImage(String urlString) {
            try {
                URL url = new URL(urlString);
                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                return BitmapFactory.decodeStream(input);
            } catch (IOException e) {
                Log.e("Image Download", "Lỗi tải ảnh từ URL: " + urlString + " - " + e.getMessage());
                return null;
            }
        }
    }
}
