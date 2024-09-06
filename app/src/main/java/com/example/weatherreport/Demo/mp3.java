package com.example.weatherreport.Demo;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.weatherreport.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class mp3 extends AppCompatActivity {
    SeekBar seekBar;
    ImageButton buttonPlayOrPause;
    ImageButton buttonBack;
    ImageButton buttonForward;
    ImageButton imageButtonPre;
    ImageButton imageButtonNext;
    MediaPlayer mediaPlayer;
    TextView textView;
    SeekBar seekBarVolume;
    ListView musicList;
    int index=0;
    ImageView cdImage;
    boolean flag = true;
    String[] array = {"1.mp3", "2.mp3", "3.mp3", "4.mp3", "5.mp3", "6.mp3"};
    ArrayList<String> mp3List = new ArrayList<>();
    int currentPlay = 0;
    ArrayAdapter<String> arrayAdapter;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //  setContentView(R.layout.mp3player);
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            play(0);
        } else {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
        cdImage = findViewById(R.id.cdImage);
        musicList = findViewById(R.id.musicList);
        seekBar = findViewById(R.id.seekBar);
        seekBar.setMax(100);
        seekBarVolume = findViewById(R.id.seekBarVolume);
        seekBarVolume.setMax(100);
        seekBarVolume.setProgress(50);
        buttonBack = findViewById(R.id.imageButtonBack);
        buttonForward = findViewById(R.id.imageButtonForward);
        buttonPlayOrPause = findViewById(R.id.imageButtonPlayOrPause);
        textView = findViewById(R.id.textView);
        arrayAdapter = new ArrayAdapter<>(mp3.this,android.R.layout.simple_list_item_1,mp3List);
        imageButtonPre=findViewById(R.id.imageButtonPre);
        imageButtonNext = findViewById(R.id.imageButtonNext);
        musicList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                index = position;
                play(position);
            }
        });
        imageButtonPre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int idx = index -1 >=0?index-1:0;
                index = idx;
                play(index);
            }
        });
        imageButtonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int idx = index + 1 >= mp3List.size() ? mp3List.size():index+1;
                index = idx;
                play(index);
            }
        });
        musicList.setAdapter(arrayAdapter);
        if (mediaPlayer == null) return;
        mediaPlayer.setVolume(0.5f, 0.5f);
        if (mediaPlayer.isPlaying()) {
            buttonPlayOrPause.setImageResource(android.R.drawable.ic_media_pause);
        } else if (!mediaPlayer.isPlaying()) {
            buttonPlayOrPause.setImageResource(android.R.drawable.ic_media_play);
        }
        buttonForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    mediaPlayer.seekTo((mediaPlayer.getCurrentPosition() + 10000) > mediaPlayer.getDuration() ? mediaPlayer.getCurrentPosition() : (mediaPlayer.getCurrentPosition() + 10000));
                }
            }
        });
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    mediaPlayer.seekTo((mediaPlayer.getCurrentPosition() - 10000) < 0 ? 0 : (mediaPlayer.getCurrentPosition() - 10000));
                }
            }
        });
        seekBarVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mediaPlayer != null) {
                    mediaPlayer.setVolume(seekBarVolume.getProgress() / 100.0f, seekBarVolume.getProgress() / 100.0f);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        buttonPlayOrPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    buttonPlayOrPause.setImageResource(android.R.drawable.ic_media_play);
                } else if (mediaPlayer != null && mediaPlayer.isPlaying() == false) {
                    mediaPlayer.start();
                    buttonPlayOrPause.setImageResource(android.R.drawable.ic_media_pause);
                }
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mediaPlayer != null && mediaPlayer.isPlaying() && fromUser) {
                    mediaPlayer.seekTo((int) (mediaPlayer.getDuration() * progress / 100));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        new MyTask().execute();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            play(0);
        }
    }

    void play(int index) {
        String path = Environment.getExternalStorageDirectory() + File.separator + "Music" + File.separator + mp3List.get(currentPlay);
        try {
            if(mediaPlayer.isPlaying()){
                mediaPlayer.stop();
                mediaPlayer.reset();
                mediaPlayer.release();
                mediaPlayer = null;
            }
            mediaPlayer = new MediaPlayer();
            Log.i("PATH", path);
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class MyTask extends AsyncTask<Object, Integer, Object> {
        @Override
        protected Object doInBackground(Object... objects) {
            while (flag) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                publishProgress(1);
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            if (mediaPlayer == null || mediaPlayer.isPlaying() == false) return;
            int duration = mediaPlayer.getDuration();
            Log.i("DUR", duration + "");
            int current = mediaPlayer.getCurrentPosition();
            Log.i("CURR", current + "");
            seekBar.setProgress(Math.round((current + 0.0f) / duration * 100));
            Log.i("PRO", Math.round(current / duration * 100) + "");

            String str = "文件名：" + mp3List.get(currentPlay)+"\n";
            str += "  总时长：" + (duration / 1000 / 60) + "分" + (duration / 1000 % 60) + "秒"+"\n";
            str += "  当前进度：" + (current / 1000 / 60) + "分" + (current / 1000 % 60) + "秒"+"\n";
            textView.setText(str);
            float degree = cdImage.getRotation()+20;
            if(degree>= 360){
                degree = 0;
            }
            cdImage.setRotation(degree);
         }
    }

    public void setList(String musicName){
        mp3List.add(musicName+".mp3");
    }
}
