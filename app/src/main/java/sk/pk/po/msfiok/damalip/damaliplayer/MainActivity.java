package sk.pk.po.msfiok.damalip.damaliplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity {

    private LinearLayout mLayout;
    private static final int PERMISSION_REQUEST = 1;
    static MediaPlayer mediaPlayer = new MediaPlayer();

    ArrayList<String> arrayList;
    ArrayList<String> listAZ = new ArrayList<>();
    ArrayList<String> listDate = new ArrayList<>();
    ArrayList<String> listFav = new ArrayList<>();

    static ArrayList<Nahravka> nahravky = new ArrayList<>();
    static ArrayList<Nahravka> nahravkyAZ = new ArrayList<>();
    static ArrayList<Nahravka> nahravkyDate = new ArrayList<>();
    static ArrayList<Nahravka> nahravkyFav = new ArrayList<>();
    BufferedWriter bw;

    ListView listView;
    ArrayAdapter <String> adapter;
    int colorID = 0;

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {

        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//povolenie apk - prístup do zariadenia
        if(ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST);
            }else {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST);
            }
        }else {
            doStuff();
        }

        // save("data");


        Button sort = findViewById(R.id.sortButton);
        mLayout = findViewById(R.id.mLayout);

        load();
        adapter.notifyDataSetChanged();

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
                        arrayList.clear();
                        arrayList.addAll(listAZ);
                        nahravky.clear();
                        nahravky.addAll(nahravkyAZ);
                        //load();
                        adapter.notifyDataSetChanged();
                    }
                });
                btnDate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        arrayList.clear();
                        arrayList.addAll(listDate);
                        nahravky.clear();
                        nahravky.addAll(nahravkyDate);
                        //load();
                        adapter.notifyDataSetChanged();
                    }
                });
                btnFav.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        adapter.notifyDataSetChanged();
                        nahravkyFav.clear();
                        listFav.clear();
                        load();
                        for (Nahravka nahravka:nahravky) {

                            System.out.println(nahravka.isOblubene());
                                if (nahravka.isOblubene()){
                                listFav.add("Názov      "+nahravka.getNazov() + "\n" +
                                        "Interpret  " +nahravka.getInterpret()
                                );
                                nahravkyFav.add(new Nahravka(nahravka.getNazov(), nahravka.getInterpret(), nahravka.getZdroj(),true));
                            }
                        }
                        arrayList.clear();
                        nahravky.clear();

                        arrayList.addAll(listFav);
                        nahravky.addAll(nahravkyFav);
                        adapter.notifyDataSetChanged();
                    }
                });
                builder.setView(dialogView);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });



    }
    public void doStuff() {
        listView = (ListView) findViewById(R.id.songList);
        arrayList = new ArrayList<>();
        getMusic();
        adapter = new ArrayAdapter<>(this, R.layout.list_view_white_text, arrayList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
 //do druhej aktivity tu
                Intent intent = new Intent(getApplicationContext(), Prehravac.class);
                intent.putExtra("SONGNAME", listView.getItemAtPosition(position).toString());
                intent.putExtra("COLOR", colorID);
                intent.putExtra("NAHRAVKA_POZICIA", position);
                startActivity(intent);

            }
        });
    }
    public void getMusic(){
        ContentResolver contentResolver = getContentResolver();
        Uri songUrl = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor songCursor = contentResolver.query(songUrl,null,null,null,null);

        if (songCursor != null && songCursor.moveToFirst()) {
            int songTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int songArtist = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int songLocation = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA);

            do{
                String currentTitle = songCursor.getString(songTitle);
                String currentArtist = songCursor.getString(songArtist);
                String currentLocation = songCursor.getString(songLocation);
                arrayList.add("Názov      "+currentTitle + "\n" +
                        "Interpret  " +currentArtist
                );
                nahravky.add(new Nahravka(currentTitle, currentArtist, currentLocation, false));
            }while (songCursor.moveToNext());

            nahravkyAZ.addAll(nahravky);
            nahravkyDate.addAll(nahravky);
            listAZ.addAll(arrayList);
            listDate.addAll(arrayList);


        }
        Collections.sort(nahravkyAZ, new Comparator<Nahravka>() {
            @Override
            public int compare(Nahravka nahravka1, Nahravka nahravka2) {
                return nahravka1.getNazov().compareTo(nahravka2.getNazov());

            }
        });
        Collections.sort(listAZ, new Comparator<String>() {
            @Override
            public int compare(String nahravka1, String nahravka2) {
                return nahravka1.compareTo(nahravka2);

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST: {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(MainActivity.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE ) == PackageManager.PERMISSION_GRANTED){
                        Toast.makeText(this, "Prístup povolený!", Toast.LENGTH_SHORT).show();

                        doStuff();
                    }
                }else {
                    Toast.makeText(this, "Prístup zamietnutý!", Toast.LENGTH_SHORT).show();
                    finish();
                }
                return;
            }
        }
    }

    public void load() {
        FileInputStream fis = null;
        try {
            fis = openFileInput("favourites.txt");
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            String text;

            while ((text = br.readLine()) != null) {
                for (int i = 0; i < MainActivity.nahravky.size(); i++) {
                    if (nahravky.get(i).getZdroj().equals(text)){
                        nahravky.get(i).setOblubene(true);
                    }
                    else nahravky.get(i).setOblubene(false);
                }
                System.out.println("text "+text);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
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
    }


    public void redTheme(View view){
        mLayout.setBackgroundColor(getColor(R.color.cervena));
        colorID = 0;
    }
    public void blueTheme(View view){
        mLayout.setBackgroundColor(getColor(R.color.modra));
        colorID = 1;
    }
    public void greyTheme(View view){
        mLayout.setBackgroundColor(getColor(R.color.siva));
        colorID = 2;
    }
}
