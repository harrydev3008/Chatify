package com.hisu.zola.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class ImageConvertUtil {

    public static Bitmap uriToBitmap(Context context, Uri uri) throws FileNotFoundException {
        final InputStream imageStream = context.getContentResolver().openInputStream(uri);
        return BitmapFactory.decodeStream(imageStream);
    }

    public static String bitmapToBase64(Bitmap imageBitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 60, outputStream);

        byte[] byteArray = outputStream.toByteArray();

        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    public static Bitmap base64ToBitmap(String imgString) {
        imgString = imgString.replaceAll("data:image/(png|jpg|jpeg);base64,", "");
        byte[] decodedString = Base64.decode(imgString, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }
}