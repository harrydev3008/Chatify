package com.hisu.zola.util.converter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.util.Base64;

import androidx.core.content.ContextCompat;

import com.hisu.zola.R;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Random;

public class ImageConvertUtil {

    private static final int[] backgroundColors = {
            R.color.primary_color,
            R.color.secondaryColor,
            R.color.danger,
            R.color.teal_200,
            R.color.teal_700,
            R.color.purple_500
    };

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

    private static String getLetterFromName(String name) {
        String[] letters = name.split(" ");
        if (letters.length == 1)
            return String.valueOf(letters[0].charAt(0));
        return letters[0].charAt(0) + "" + letters[1].charAt(0);
    }

    public static Bitmap createImageFromText(Context context, int width, int height, String text) {

        Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        Paint paintCircle = new Paint();
        Paint paintText = new Paint();

        Rect rect = new Rect(0, 0, width, height);
        RectF rectF = new RectF(rect);
        float density = context.getResources().getDisplayMetrics().density;
        float roundPx = 100 * density;

        paintCircle.setColor(ContextCompat.getColor(context, R.color.primary_color));
        paintCircle.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);

        canvas.drawRoundRect(rectF, roundPx, roundPx, paintCircle);

        paintText.setTextAlign(Paint.Align.CENTER);
        paintText.setColor(Color.WHITE);
        paintText.setTextSize(64);

        int xPos = (canvas.getWidth() / 2);
        int yPos = (int) ((canvas.getHeight() / 2) - ((paintText.descent() + paintText.ascent()) / 2));

        canvas.drawText(getLetterFromName(text).toUpperCase(), xPos, yPos, paintText);

        return output;
    }

    private static int randomBackgroundColor(Context context) {
        int num = (new Random()).nextInt((backgroundColors.length - 1) + 1);
        return ContextCompat.getColor(context, backgroundColors[num]);
    }
}