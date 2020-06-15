package sk.pk.po.msfiok.damalip.damaliplayer;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private LinearLayout mLayout;
    private static final int PERMISSION_REQUEST = 1;
    static MediaPlayer mediaPlayer = new MediaPlayer();
    private Nahravky nahravky;
    private ArrayAdapter<Nahravka> adapter;
    int colorID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        nahravky = Nahravky.getInstance();
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST);
            } else {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST);
            }
        } else {
            intitialize();
        }

        Button sort = findViewById(R.id.sortButton);
        mLayout = findViewById(R.id.mLayout);

        sort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_lists, null);
                Button btnAZ = dialogView.findViewById(R.id.sortAZ);
                Button btnDate = dialogView.findViewById(R.id.sortDate);
                Button btnFav = dialogView.findViewById(R.id.sortFav);

                btnAZ.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        nahravky.setOrder(Nahravky.AZ_SORT);
                        adapter.notifyDataSetChanged();
                    }
                });
                btnDate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        nahravky.setOrder(Nahravky.DATE_SORT);
                        adapter.notifyDataSetChanged();
                    }
                });
                btnFav.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        nahravky.setOrder(Nahravky.FAVOURITES_ONLY);
                        adapter.notifyDataSetChanged();
                    }
                });
                builder.setView(dialogView);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });


    }

    public void intitialize() {
        ListView listView = (ListView) findViewById(R.id.songList);
        nahravky.setNahravky(getMusic());
        nahravky.setOrder(Nahravky.AZ_SORT);
        adapter = new ArrayAdapter<>(this, R.layout.list_view_white_text, nahravky.getNahravky());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), Prehravac.class);
                intent.putExtra("COLOR", colorID);
                intent.putExtra("NAHRAVKA_POZICIA", position);
                startActivity(intent);
            }
        });
    }

    public List<Nahravka> getMusic() {
        List<Nahravka> loaded = new ArrayList<>();
        ContentResolver contentResolver = getContentResolver();
        Uri songUrl = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor songCursor = contentResolver.query(songUrl, null, null, null, null);

        if (songCursor != null && songCursor.moveToFirst()) {
            int songTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int songArtist = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int songLocation = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            int songDate = songCursor.getColumnIndex(MediaStore.Audio.Media.DATE_MODIFIED);
            do {
                String currentTitle = songCursor.getString(songTitle);
                String currentArtist = songCursor.getString(songArtist);
                String currentLocation = songCursor.getString(songLocation);
                String currentDate = songCursor.getString(songDate);
                loaded.add(new Nahravka(currentTitle, currentArtist, currentLocation, currentDate, false));
            } while (songCursor.moveToNext());
        }

        FileInputStream fis = null;
        try {
            fis = openFileInput("favourites.txt");
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            String text;

            while ((text = br.readLine()) != null) {
                for (int i = 0; i < loaded.size(); i++) {
                    if (loaded.get(i).getZdroj().equals(text))
                        loaded.get(i).setOblubene(true);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return loaded;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(MainActivity.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "Prístup povolený!", Toast.LENGTH_SHORT).show();
                        intitialize();
                    }
                } else {
                    Toast.makeText(this, "Prístup zamietnutý!", Toast.LENGTH_SHORT).show();
                    finish();
                }
                return;
            }
        }
    }

    public void redTheme(View view) {
        mLayout.setBackgroundColor(getColor(R.color.cervena));
        colorID = 0;
    }

    public void blueTheme(View view) {
        mLayout.setBackgroundColor(getColor(R.color.modra));
        colorID = 1;
    }

    public void greyTheme(View view) {
        mLayout.setBackgroundColor(getColor(R.color.siva));
        colorID = 2;
    }
}
