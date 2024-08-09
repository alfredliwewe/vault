package com.rodz.vault;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Movie;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ImageDecodeOptions;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

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

            SimpleDraweeView imageView = new SimpleDraweeView(this);
            //ImageView imageView = new ImageView(this);
            imageView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 800));
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

        }
    }

    @SuppressLint("Range")
    private void printPictures() {
        Button add = new Button(this);
        add.setText("Add pictures");
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GeneralPurpose.this, GeneralPurpose.class);
                intent.putExtra("task","choosefile");
                intent.putExtra("album",album);
                startActivity(intent);
            }
        });
        main.addView(add);

        Cursor cursor = db.rawQuery("SELECT * FROM pictures ", null);
        while(cursor.moveToNext()){
            String filename = cursor.getString(cursor.getColumnIndex("name"));
            View folder_view = getLayoutInflater().inflate(R.layout.folder_view, null);
            TextView folder_name = folder_view.findViewById(R.id.folder_name), date_created = folder_view.findViewById(R.id.date_created);
            folder_name.setText(cursor.getString(cursor.getColumnIndex("name")));


            //date_created.setText(sdf.format(creationDate));

            ImageView icon = folder_view.findViewById(R.id.icon);
            //icon.setImageResource(R.drawable.ic_cached);
            Bitmap bitmap = fileManager.getBitmap(cursor.getString(cursor.getColumnIndex("name")));
            icon.setImageBitmap(bitmap);
            //System.out.println(fileEntry.getAbsolutePath());
            main.addView(folder_view);
            folder_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(GeneralPurpose.this, GeneralPurpose.class);
                    intent.putExtra("task","preview");
                    intent.putExtra("album",album);
                    intent.putExtra("file",filename);
                    startActivity(intent);
                }
            });
        }
        cursor.close();
    }

    public void showFolder(File folder){
        if (checkPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
            LinearLayout vert = new LinearLayout(this);
            vert.setOrientation(LinearLayout.VERTICAL);

            Thread thread1 = new Thread(new Runnable() {
                @SuppressLint("ResourceType")
                @Override
                public void run() {
                    //get list of music and store to database

                    if (folder != null) {
                        //if (folder.exists()) {
                        File[] children = folder.listFiles();
                        ArrayList<File> images = new ArrayList<>();

                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                        for (File fileEntry : children) {
                            long creationTime = fileEntry.lastModified();
                            Date creationDate = new Date(creationTime);

                            if (fileEntry.isDirectory()) {
                                try {
                                    //listFilesForFolder(fileEntry);
                                    // print folder
                                    View folder_view = getLayoutInflater().inflate(R.layout.folder_view, null);
                                    TextView folder_name = folder_view.findViewById(R.id.folder_name), date_created = folder_view.findViewById(R.id.date_created);
                                    folder_name.setText(fileEntry.getName());
                                    date_created.setText(sdf.format(creationDate));
                                    folder_view.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            dir_progress.add(fileEntry);
                                            showFolder(fileEntry);
                                        }
                                    });
                                    vert.addView(folder_view);
                                } catch (Exception exception) {
                                    exception.printStackTrace();
                                }
                            } else {
                                images.add(fileEntry);
                            }
                        }
                        //}

                        for (File fileEntry:images){
                            long creationTime = fileEntry.lastModified();
                            Date creationDate = new Date(creationTime);

                            String filename = fileEntry.getName();
                            String path = fileEntry.getAbsolutePath();
                            String[] chars = filename.toLowerCase().split("\\.");
                            if (chars.length > 1) {
                                String extension = chars[chars.length - 1];
                                List<String> image_extensions = Arrays.asList(new String[]{"png","webp","jpg","gif"});
                                if (image_extensions.contains(extension)) {
                                    //files.add(fileEntry.getAbsolutePath());
                                    View folder_view = getLayoutInflater().inflate(R.layout.folder_view, null);
                                    TextView folder_name = folder_view.findViewById(R.id.folder_name), date_created = folder_view.findViewById(R.id.date_created);
                                    folder_name.setText(filename);


                                    date_created.setText(sdf.format(creationDate));

                                    ImageView icon = folder_view.findViewById(R.id.icon);
                                    //icon.setImageResource(R.drawable.ic_cached);
                                    Bitmap bitmap = BitmapFactory.decodeFile(fileEntry.getAbsolutePath());
                                    icon.setImageBitmap(bitmap);
                                    //System.out.println(fileEntry.getAbsolutePath());
                                    vert.addView(folder_view);
                                    folder_view.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            fileManager.saveFiles(fileEntry.getName(), fileEntry);
                                            db.execSQL("INSERT INTO pictures (`id`, `name`, `origin`, `destination`, `views`, `album`) VALUES (NULL, ?,?,?,?,?)", new Object[]{
                                                    fileEntry.getName(),
                                                    fileEntry.getAbsolutePath(),
                                                    fileEntry.getName(),
                                                    "0",
                                                    album
                                            });
                                            fileEntry.delete();
                                            showFolder(folder);
                                            Toast.makeText(ctx, "Saved file", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            } else {
                                //System.out.println(fileEntry.getName());
                            }
                        }
                    }
                    else{
                        TextView empty = new TextView(ctx);
                        empty.setText("Could not open this folder");
                        empty.setTextColor(Color.parseColor(getString(R.color.red)));
                        empty.setBackgroundResource(R.drawable.alert_danger);
                        vert.addView(empty);
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            main.removeAllViews();
                            main.addView(vert);
                        }
                    });
                }
            });

            thread1.start();
        }
        else{
            if (!hasRequested) {
                hasRequested = true;
                ActivityCompat.requestPermissions(GeneralPurpose.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (dir_progress.size() > 1) {
                        dir_progress.remove(dir_progress.size() - 1);
                        showFolder(dir_progress.get(dir_progress.size() - 1));
                    } else {
                        finish();
                    }
                    return true;
            }

        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //indexFiles();
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    //permission result

    private boolean checkPermission(String permission)
    {
        //String permission = android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
        int res = getApplicationContext().checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }
}