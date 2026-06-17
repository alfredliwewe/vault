package com.rodz.vault;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Utilities {
    public static Bitmap cropSquareBitmap(Bitmap sourceBitmap, Context context){
        // Determine the dimensions of the original bitmap
        if(sourceBitmap == null){
            //return bitmap of app icon
            try {
                Drawable iconDrawable = context.getPackageManager().getApplicationIcon(context.getPackageName());

                // 2. Convert Drawable to Bitmap
                if (iconDrawable instanceof BitmapDrawable) {
                    return ((BitmapDrawable) iconDrawable).getBitmap();
                }
            }
            catch (PackageManager.NameNotFoundException e){
                e.printStackTrace();
                return null;
            }
        }
        int originalWidth = sourceBitmap.getWidth();
        int originalHeight = sourceBitmap.getHeight();

        // Calculate the size of the square (smallest dimension of the original bitmap)
        int squareSize = Math.min(originalWidth, originalHeight);

        // Calculate the top-left corner of the square crop area
        int xOffset = (originalWidth - squareSize) / 2;
        int yOffset = (originalHeight - squareSize) / 2;

        // Create the square bitmap by cropping from the original bitmap
        Bitmap squareBitmap = Bitmap.createBitmap(sourceBitmap, xOffset, yOffset, squareSize, squareSize);

        return squareBitmap;
    }

    public static <T> List<List<T>> splitList(List<T> originalList, int chunkSize) {
        return IntStream.range(0, (originalList.size() + chunkSize - 1) / chunkSize)
                .mapToObj(i -> originalList.subList(i * chunkSize, Math.min((i + 1) * chunkSize, originalList.size())))
                .collect(Collectors.toList());
    }
}
