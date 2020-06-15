package sk.pk.po.msfiok.damalip.damaliplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class Prehravac extends AppCompatActivity {

    TextView songname;
    Nahravka nahravka;
    SeekBar positionBar;
    LinearLayout mPrehravac;
    TextView elapsedTimeLabel;
    TextView remainingTimeLabel;
    ImageButton btnFavSelected;
    int totalTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prehravac);

        songname = findViewById(R.id.songname);
        mPrehravac = findViewById(R.id.mPrehravac);
        positionBar = findViewById(R.id.positionBar);
        elapsedTimeLabel = findViewById(R.id.elapsedTimeLabel);
        remainingTimeLabel = findViewById(R.id.remainingTimeLabel);
        btnFavSelected = findViewById(R.id.btnFav);

        Intent intent = getIntent();

        songname.setText(intent.getStringExtra("SONGNAME"));
        final int nahravka_pozicia = intent.getIntExtra("NAHRAVKA_POZICIA", 0);
        nahravka = MainActivity.nahravky.get(nahravka_pozicia);
        switch (intent.getIntExtra("COLOR", 0)){
            case 0: redTheme(); break;
            case 1: blueTheme(); break;
            case 2: greyTheme(); break;
        }

        try {
           // MainActivity.mediaPlayer.stop();
            MainActivity.mediaPlayer.reset();
            MainActivity.mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            MainActivity.mediaPlayer.setDataSource(nahravka.getZdroj());
            MainActivity.mediaPlayer.prepare();


        } catch (IOException e) {

            e.printStackTrace();
        }
        catch (Exception e){

        }
        MainActivity.mediaPlayer.seekTo(0);
        totalTime = MainActivity.mediaPlayer.getDuration();
        nahravkaBar();

        final Context context = this;

        if (nahravka.isOblubene()){
            btnFavSelected.setImageResource(R.drawable.ic_star_on_24dp);
        }
        else btnFavSelected.setImageResource(R.drawable.ic_star_off_24dp);

        btnFavSelected.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if (!nahravka.isOblubene()) {
                    nahravka.setOblubene(true);
                    btnFavSelected.setImageResource(R.drawable.ic_star_on_24dp);
                    Toast.makeText(context, "Pridané do Obľúbené", Toast.LENGTH_SHORT).show();
                    MainActivity.nahravky.get(nahravka_pozicia).setOblubene(true);

                    String data = "";
                    for (Nahravka nahravka:MainActivity.nahravky) {
                        if (nahravka.isOblubene()){
                            data += nahravka.getZdroj() + "\n";
                        }

                    }
                    save(data);

                }
                else {
                    nahravka.setOblubene(false);
                    btnFavSelected.setImageResource(R.drawable.ic_star_off_24dp);
                    Toast.makeText(context, "Odobrané z Obľúbené", Toast.LENGTH_SHORT).show();

                    MainActivity.nahravky.get(nahravka_pozicia).setOblubene(false);
                    //MainActivity.arrayList.remove(nahravka_pozicia);
                    String data = "";
                    for (Nahravka nahravka:MainActivity.nahravky) {
                        if (nahravka.isOblubene()){
                            data += nahravka.getZdroj() + "\n";
                        }

                    }
                    save(data);
                }

            }
        });

    }
    public void playSong(View view){

        System.out.println("Prehravam songu");
        MainActivity.mediaPlayer.start();
    }
    public void pauseSong(View view){
        System.out.println("Pause");
        if (MainActivity.mediaPlayer.isPlaying()){
            MainActivity.mediaPlayer.pause();
        }
    }

    public void nahravkaBar(){
        positionBar.setMax(totalTime);
        positionBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if (fromUser) {
                            MainActivity.mediaPlayer.seekTo(progress);
                            positionBar.setProgress(progress);
                        }
                    }
                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }
                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }
                }
        );

        //thread na update positionBar
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("spustil sa fred");
                 while (MainActivity.mediaPlayer != null) {

                     try {
                         Message msg = new Message();
                         msg.what = MainActivity.mediaPlayer.getCurrentPosition();
                         handler.sendMessage(msg);
                         Thread.sleep(1000);
                     }catch (InterruptedException e) {e.printStackTrace(); }
                 }
            }
        }).start();
    }
    public void save(String data) {
        FileOutputStream fos = null;
        try {
            fos = openFileOutput("favourites.txt", MODE_PRIVATE);
            fos.write(data.getBytes());
            Toast.makeText(this, "Saved to " + getFilesDir() + "/",
                    Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @SuppressLint("HandlerLeak")
        @Override
        public void handleMessage(Message msg) {
            int currentPosition = msg.what;
            positionBar.setProgress(currentPosition);

            String elapsedTime = createTimeLabel(currentPosition);
            elapsedTimeLabel.setText(elapsedTime);

            String remainingTime = createTimeLabel(totalTime-currentPosition);
            remainingTimeLabel.setText("- "+remainingTime);
        }
    };

    public String createTimeLabel(int time){
        String timeLabel = "";
        int min = time / 1000 /60;
        int sec = time / 1000 %60;

        timeLabel = min + ":";
        if (sec < 10) timeLabel += "0";
        timeLabel += sec;
        return timeLabel;
    }


    public void redTheme(){
        mPrehravac.setBackgroundColor(getColor(R.color.cervena));

    }
    public void blueTheme(){
        mPrehravac.setBackgroundColor(getColor(R.color.modra));

    }
    public void greyTheme(){
        mPrehravac.setBackgroundColor(getColor(R.color.siva));

    }

}
