package com.rodz.vault;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.app.RecoverableSecurityException;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Movie;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ImageDecodeOptions;
import com.google.android.material.button.MaterialButton;
import com.rodz.vault.adpaters.ImageAdapter;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class GeneralPurpose extends AppCompatActivity {
    SQLiteDatabase db;
    Values values;
    LinearLayout main;
    boolean hasRequested = false;
    Context ctx;
    ArrayList<File> dir_progress = new ArrayList<>();
    FileManager fileManager;
    String album;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder(StrictMode.getVmPolicy())
                .detectLeakedClosableObjects()
                .build());
        setContentView(R.layout.activity_general_purpose);
        /*ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });*/

        DbHelper helper = new DbHelper(getApplicationContext());
        db = helper.getWritableDatabase();
        values = new Values(db);
        Fresco.initialize(this);

        main = findViewById(R.id.main);
        ctx = this;
        fileManager = new FileManager(this);

        Intent intent = getIntent();
        String task = intent.getStringExtra("task");
        album = intent.getStringExtra("album");

        assert task != null;
        if (task.equals("album")){
            printPictures();
        }
        else if(task.equals("choosefile")){
            //get the root folder
            File mainFolder = Environment.getExternalStorageDirectory();
            dir_progress.add(mainFolder);
            showFolder(mainFolder);

            /*File[] children = mainFolder.listFiles();

            for (File child : children) {
                if (child.isDirectory()) {
                    // child is a directory
                    Log.d("Directory", child.getAbsolutePath());
                } else {
                    // child is a file
                    Log.d("File", child.getAbsolutePath());
                }
            }*/
        }
        else if(task.equals("preview")){
            String filename = intent.getStringExtra("file");
            String id = intent.getStringExtra("id");

            Intent intent1 = new Intent(this, ImageSwipe.class);
            intent1.putExtra("starting_id", id);
            intent1.putExtra("album", album);
            startActivity(intent1);
            finish(); // Finish this activity as we're moving to the full screen swipe view
        }
    }

    private void printAnimation(String filename, String id) {
        Bitmap bitmap = fileManager.getBitmap(filename);
        //calculate height
        double width = bitmap.getWidth(), height = bitmap.getHeight();
        double new_height = height/width * getScreenWidth();

        SimpleDraweeView imageView = new SimpleDraweeView(this);
        //ImageView imageView = new ImageView(this);
        imageView.setLayoutParams(new LinearLayout.LayoutParams(getScreenWidth(), (int)new_height));
        main.removeAllViews();
        main.addView(imageView);

        ContextWrapper cw = new ContextWrapper(this.ctx);
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        File newFile = new File(directory, filename);

            /*Glide.with(this)
                    .asDrawable()
                    .load(newFile)
                    .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                    .into(imageView);

             */
        Uri webpUri = Uri.fromFile(newFile);

        imageView.getHierarchy().setPlaceholderImage(R.drawable.xlogo);
        ImageDecodeOptions decodeOptions = ImageDecodeOptions.newBuilder()
                .setForceStaticImage(false)
                .setDecodeAllFrames(true) // Enable animation decoding
                .build();
        imageView.setImageURI(webpUri, decodeOptions);
        imageView.animate();
        imageView.getHierarchy().setActualImageScaleType(ScalingUtils.ScaleType.FIT_Y);

        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setUri(webpUri)
                .setAutoPlayAnimations(true) // Enable animation autoplay
                .build();
        imageView.setController(controller);

            /*Picasso.get()
                    .load(newFile)
                    .into(imageView);
            Picasso.get().setIndicatorsEnabled(true);
            Picasso.get().setLoggingEnabled(true); */

            /*Cursor cursor = db.rawQuery("SELECT * FROM pictures",null);
            while (cursor.moveToNext()){
                @SuppressLint("Range") String filename1 = cursor.getString(cursor.getColumnIndex("name"));

                SimpleDraweeView imageView1 = new SimpleDraweeView(this);
                //ImageView imageView = new ImageView(this);
                imageView1.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 800));
                main.addView(imageView1);

                File file = new File(directory, filename1);

                Uri webpUri1 = Uri.fromFile(file);

                imageView1.getHierarchy().setPlaceholderImage(R.drawable.xlogo);
                imageView1.setImageURI(webpUri1, decodeOptions);
                imageView1.getHierarchy().setActualImageScaleType(ScalingUtils.ScaleType.FIT_Y);

                DraweeController controller1 = Fresco.newDraweeControllerBuilder()
                        .setUri(webpUri1)
                        .setAutoPlayAnimations(true) // Enable animation autoplay
                        .build();
                imageView1.setController(controller1);
            }
            *
             */
        View buttons = getLayoutInflater().inflate(R.layout.buttons, null);
        main.addView(buttons);

        MaterialButton previous_btn = buttons.findViewById(R.id.previous_btn), next_btn = buttons.findViewById(R.id.next_btn);

        previous_btn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("Range")
            @Override
            public void onClick(View v) {
                Cursor c = db.rawQuery("SELECT * FROM pictures WHERE album = ? AND id < ? ORDER BY id DESC LIMIT 1", new String[]{album,id});
                if (c.getCount() > 0){
                    c.moveToFirst();
                    printAnimation(c.getString(c.getColumnIndex("name")), c.getString(c.getColumnIndex("id")));
                }
                else{
                    Toast.makeText(ctx, "You have reached margin", Toast.LENGTH_SHORT).show();
                }
                c.close();
            }
        });

        next_btn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("Range")
            @Override
            public void onClick(View v) {
                Cursor c = db.rawQuery("SELECT * FROM pictures WHERE album = ? AND id > ? LIMIT 1", new String[]{album,id});
                if (c.getCount() > 0){
                    c.moveToFirst();
                    printAnimation(c.getString(c.getColumnIndex("name")), c.getString(c.getColumnIndex("id")));
                }
                else{
                    Toast.makeText(ctx, "You have reached margin", Toast.LENGTH_SHORT).show();
                }
                c.close();
            }
        });
    }

    @SuppressLint("Range")
    private void printPictures() {
        MaterialButton add = new MaterialButton(this);
        add.setTextColor(Color.WHITE);
        add.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        add.setText("Add pictures");
        add.setOnClickListener(v -> {
            Intent intent = new Intent(GeneralPurpose.this, GeneralPurpose.class);
            intent.putExtra("task","choosefile");
            intent.putExtra("album",album);
            startActivity(intent);
        });
        main.addView(add);

        ProgressBar progressBar = new ProgressBar(this);
        main.addView(progressBar);

        Thread thread = new Thread(() -> {
            //
            LinearLayout vert = new LinearLayout(ctx);
            vert.setOrientation(LinearLayout.VERTICAL);
            //main.addView(vert);

            ArrayList<Bitmap> pictures = new ArrayList<>();
            ArrayList<String> filenames = new ArrayList<>(), ids = new ArrayList<>();
            Cursor cursor = db.rawQuery("SELECT * FROM pictures WHERE album = ?", new String[]{album});
            while(cursor.moveToNext()) {
                String thumbnail = cursor.getString(cursor.getColumnIndex("thumbnail")), id = cursor.getString(cursor.getColumnIndex("id"));
                Bitmap bitmap = null;
                if (thumbnail != null){
                    bitmap = fileManager.getBitmap(thumbnail);
                }
                else {
                    Bitmap sourceBitmap = fileManager.getBitmap(cursor.getString(cursor.getColumnIndex("name")));
                    if(sourceBitmap == null){
                        continue;
                    }

                    bitmap = Utilities.cropSquareBitmap(sourceBitmap, ctx);
                    String filename = UUID.randomUUID().toString()+".png";
                    fileManager.saveImage(filename, bitmap);
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("thumbnail", filename);
                    db.update("pictures", contentValues, "id = ?", new String[]{id});
                }
                pictures.add(bitmap);
                filenames.add(cursor.getString(cursor.getColumnIndex("name")));
                ids.add(cursor.getString(cursor.getColumnIndex("id")));
            }

            int h = (int)((double)getScreenWidth()/4);

            List<List<Bitmap>> chunks = Utilities.splitList(pictures, 4);
            int index = 0;
            for (List<Bitmap> chunk:chunks){
                LinearLayout row = new LinearLayout(ctx);
                row.setOrientation(LinearLayout.HORIZONTAL);
                vert.addView(row);

                for (Bitmap bitmap:chunk){
                    ImageView imageView = new ImageView(ctx);
                    imageView.setImageBitmap(bitmap);
                    imageView.setLayoutParams(new LinearLayout.LayoutParams(0, h,.1f));
                    row.addView(imageView);

                    int finalIndex = index;
                    imageView.setOnClickListener(v->{
                        String filename = filenames.get(finalIndex);
                        String id = ids.get(finalIndex);

                        Intent intent = new Intent(GeneralPurpose.this, GeneralPurpose.class);
                        intent.putExtra("task","preview");
                        intent.putExtra("album",album);
                        intent.putExtra("file",filename);
                        intent.putExtra("id",id);
                        startActivity(intent);
                    });

                    index += 1;
                }
            }


            runOnUiThread(() -> {
                main.removeView(progressBar);
                main.addView(vert);
            });
        });
        thread.start();

    }

    private int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    private void showFolder(File folder) {
        main.removeAllViews();
        TextView path = new TextView(this);
        path.setText(folder.getAbsolutePath());
        path.setTextSize(20);
        main.addView(path);

        File[] children = folder.listFiles();
        if (children != null) {
            for (File child : children) {
                Button button = new Button(this);
                button.setText(child.getName());
                button.setAllCaps(false);
                button.setOnClickListener(v -> {
                    if (child.isDirectory()) {
                        dir_progress.add(child);
                        showFolder(child);
                    } else {
                        //Toast.makeText(ctx, "File: " + child.getName(), Toast.LENGTH_SHORT).show();
                        //add file to database
                        //fileManager.saveImage(child);
                        //db.execSQL("INSERT INTO pictures (name, album) VALUES ('" + child.getName() + "', '" + album + "')");
                        ContentValues contentValues = new ContentValues();
                        contentValues.put("name", child.getName());
                        contentValues.put("album", album);
                        db.insert("pictures", null, contentValues);
                        Toast.makeText(ctx, "Saved", Toast.LENGTH_SHORT).show();
                    }
                });
                main.addView(button);
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (dir_progress.size() > 1) {
                dir_progress.remove(dir_progress.size() - 1);
                showFolder(dir_progress.get(dir_progress.size() - 1));
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
