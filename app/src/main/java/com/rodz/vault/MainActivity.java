package com.rodz.vault;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rodz.vault.adpaters.AlbumAdapter;
import com.rodz.vault.models.Album;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    SQLiteDatabase db;
    Values values;
    LinearLayout main;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        /*ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });*/

        DbHelper helper = new DbHelper(getApplicationContext());
        db = helper.getWritableDatabase();
        values = new Values(db);

        main = findViewById(R.id.main);

        printAlbums();
    }

    @SuppressLint("Range")
    private void printAlbums() {
        Cursor c = db.rawQuery("SELECT * FROM albums ", null);
        if (c.getCount() == 0){
            db.execSQL("INSERT INTO albums (id,name,picture,views) VALUES (NULL,?,?,?)", new Object[]{
                    "Private Album",
                    "default.png",
                    0
            });
            c.close();
            printAlbums();
        }
        else{
            //loop
            //RecyclerView recyclerView = new RecyclerView(this);
            //main.addView(recyclerView);

            List<Album> albumList = new ArrayList<>();

            while(c.moveToNext()){
                String id =  c.getString(c.getColumnIndex("id"));
                //
                albumList.add(new Album(this, c.getString(c.getColumnIndex("name")), c.getString(c.getColumnIndex("picture"))));
                View row = getLayoutInflater().inflate(R.layout.item_album, main);
                TextView name = row.findViewById(R.id.album_name);
                name.setText(c.getString(c.getColumnIndex("name")));
                row.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, GeneralPurpose.class);
                        intent.putExtra("album", id);
                        intent.putExtra("task","album");
                        startActivity(intent);
                    }
                });
            }

            /*recyclerView.setHasFixedSize(true);

            // Set the GridLayoutManager with 2 columns
            GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
            recyclerView.setLayoutManager(layoutManager);

            // Assuming albumList is fetched from your database
            //albumList = fetchAlbumsFromDatabase();

            AlbumAdapter albumAdapter = new AlbumAdapter(this, albumList);
            recyclerView.setAdapter(albumAdapter);

            albumAdapter.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                @Override
                public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                    Album album = albumList.get(childPosition);
                    Toast.makeText(MainActivity.this, album.name, Toast.LENGTH_SHORT).show();
                    return false;
                }
            });*/

        }
    }
}