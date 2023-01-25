package com.usman.dms.ui;


import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class SaveToGallery
{
    String fileName, subFolderPath, fileDescription;
    Bitmap.CompressFormat format ;
    int quality, width, height;
    Context context;
    View view ;
    Drawable drawableBg;

    public SaveToGallery(String fileName, String subFolderPath, String fileDescription, Bitmap.CompressFormat format, int quality, int width, int height, Context context, View view, Drawable drawableBg) {
        this.fileName = fileName;
        this.subFolderPath = subFolderPath;
        this.fileDescription = fileDescription;
        this.format = format;
        this.quality = quality;
        this.width = width;
        this.height = height;
        this.context = context;
        this.view = view;
        this.drawableBg = drawableBg;
    }

    /**
     * Returns the bitmap that represents the chart.
     *
     * @return
     */
    public Bitmap getChartBitmap(View view , int width,int height,Drawable drawableBg) {
        // Define a bitmap with the same size as the view
        Bitmap returnedBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        // Bind a canvas to it
        Canvas canvas = new Canvas(returnedBitmap);
        // Get the view's background
        Drawable bgDrawable = drawableBg;
        if (bgDrawable != null)
            // has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas);
        else {
            // does not have background drawable, then draw white background on
            // the canvas
            canvas.drawColor( Color.WHITE );
        }
        // draw the view on the canvas
        view.draw(canvas);
        // return the bitmap
        return returnedBitmap;
    }

    /**
     * Saves the current chart state with the given name to the given path on
     * the sdcard leaving the path empty "" will put the saved file directly on
     * the SD card chart is saved as a PNG image, example:
     * saveToPath("myfilename", "foldername1/foldername2");
     *
     * @param title
     * @param pathOnSD e.g. "folder1/folder2/folder3"
     * @return returns true on success, false on error
     */
    public boolean saveToPath(String title, String pathOnSD,View v , int wt, int ht,Drawable bg) {



        Bitmap b = getChartBitmap(v,wt,ht,bg);

        OutputStream stream = null;
        try {
            stream = new FileOutputStream( Environment.getExternalStorageDirectory().getPath()
                    + pathOnSD + "/" + title
                    + ".png");

            /*
             * Write bitmap to file using JPEG or PNG and 40% quality hint for
             * JPEG.
             */
            b.compress( Bitmap.CompressFormat.PNG, 40, stream);

            stream.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * Saves the current state of the chart to the gallery as an image type. The
     * compression must be set for JPEG only. 0 == maximum compression, 100 = low
     * compression (high quality). NOTE: Needs permission WRITE_EXTERNAL_STORAGE
     *
//     * @param fileName        e.g. "my_image"
//     * @param subFolderPath   e.g. "ChartPics"
//     * @param fileDescription e.g. "Chart details"
//     * @param format          e.g. Bitmap.CompressFormat.PNG
//     * @param quality         e.g. 50, min = 0, max = 100
     * @return returns true if saving was successful, false if not
     */
    public boolean saveToGallery1() {
        // restrain quality
        if (quality < 0 || quality > 100)
            quality = 50;

        long currentTime = System.currentTimeMillis();

        File extBaseDir = Environment.getExternalStorageDirectory();
        File file = new File(extBaseDir.getAbsolutePath() + "/DCIM/" + subFolderPath);
        if (!file.exists()) {
            if (!file.mkdirs()) {
                return false;
            }
        }

        String mimeType = "";
        switch (format) {
            case PNG:
                mimeType = "image/png";
                if (!fileName.endsWith(".png"))
                    fileName += ".png";
                break;
            case WEBP:
                mimeType = "image/webp";
                if (!fileName.endsWith(".webp"))
                    fileName += ".webp";
                break;
            case JPEG:
            default:
                mimeType = "image/jpeg";
                if (!(fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")))
                    fileName += ".jpg";
                break;
        }

        String filePath = file.getAbsolutePath() + "/" + fileName;
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(filePath);

            Bitmap b = getChartBitmap(view,width,height,drawableBg);
            b.compress(format, quality, out);

            out.flush();
            out.close();

        } catch (IOException e) {
            e.printStackTrace();

            return false;
        }

        long size = new File(filePath).length();

        ContentValues values = new ContentValues(8);

        // store the details
        values.put( MediaStore.Images.Media.TITLE, fileName);
        values.put( MediaStore.Images.Media.DISPLAY_NAME, fileName);
        values.put( MediaStore.Images.Media.DATE_ADDED, currentTime);
        values.put( MediaStore.Images.Media.MIME_TYPE, mimeType);
        values.put( MediaStore.Images.Media.DESCRIPTION, fileDescription);
        values.put( MediaStore.Images.Media.ORIENTATION, 0);
        values.put( MediaStore.Images.Media.DATA, filePath);
        values.put( MediaStore.Images.Media.SIZE, size);

        return context.getContentResolver().insert( MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values) != null;
    }


}
