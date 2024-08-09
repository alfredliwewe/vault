package com.rodz.vault.models;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.rodz.vault.FileManager;
import com.rodz.vault.R;

public class Album {
    public String name;
    Bitmap bitmap;

    public Album(Context ctx, String name, String imgResource){
        this.name = name;
        FileManager fileManager = new FileManager(ctx);
        if (imgResource.equals("default.png")){
            bitmap = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.xlogo);
        }
        else {
            bitmap = fileManager.getBitmap(imgResource);
        }
    }

    public String getName() {
        return name;
    }

    public Bitmap getCoverImageUrl() {
        return bitmap;
    }
}
