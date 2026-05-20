package com.rodz.vault;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.rodz.vault.adpaters.FullscreenImageAdapter;

import java.util.ArrayList;
import java.util.List;

public class ImageSwipe extends AppCompatActivity {

    private ViewPager2 viewPager;
    private FullscreenImageAdapter adapter;
    private List<String> imageFilenames = new ArrayList<>();
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_image_swipe);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        viewPager = findViewById(R.id.viewPager);

        DbHelper helper = new DbHelper(getApplicationContext());
        db = helper.getReadableDatabase();

        String startingId = getIntent().getStringExtra("starting_id");
        String album = getIntent().getStringExtra("album");

        loadImages(album, startingId);
    }

    @SuppressLint("Range")
    private void loadImages(String album, String startingId) {
        imageFilenames.clear();
        int startingPosition = 0;

        Cursor cursor = db.rawQuery("SELECT * FROM pictures WHERE album = ? ORDER BY id ASC", new String[]{album});
        while (cursor.moveToNext()) {
            String id = cursor.getString(cursor.getColumnIndex("id"));
            String filename = cursor.getString(cursor.getColumnIndex("name"));
            if (id.equals(startingId)) {
                startingPosition = imageFilenames.size();
            }
            imageFilenames.add(filename);
        }
        cursor.close();

        adapter = new FullscreenImageAdapter(this, imageFilenames);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(startingPosition, false);
    }
}
