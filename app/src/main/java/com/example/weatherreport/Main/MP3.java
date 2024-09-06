package com.example.weatherreport.Main;

import android.annotation.SuppressLint;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.weatherreport.R;

import java.io.IOException;

public class MP3 extends AppCompatActivity {
    SeekBar seekBar;
    Button netMusic;
    Button cancelNet;
    Button sureNet;
    EditText netName;
    ImageButton buttonPlayOrPause;
    ImageButton buttonBack;
    ImageButton buttonForward;
    ImageButton imageButtonPre;
    ImageButton imageButtonNext;
    TextView textView;
    SeekBar seekBarVolume;
    ListView musicList;
    ImageView cdImage;
    int index = 0;
    boolean flag = true;
    String path;
    String City;
    String cityNameIng;
    private MediaPlayer mediaPlayer;
    String[] musicLists = {"first","second","third","fourth","fifth","sixth","seventh"};
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mp3player);
        netMusic = findViewById(R.id.net);
        cancelNet = findViewById(R.id.cancelNet);
        sureNet = findViewById(R.id.sureNet);
        netName = findViewById(R.id.netName);
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
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(MP3.this, android.R.layout.simple_list_item_1, musicLists);
        imageButtonPre=findViewById(R.id.imageButtonPre);
        imageButtonNext = findViewById(R.id.imageButtonNext);
        play(index);
        sureNet.setEnabled(false);
        cancelNet.setEnabled(false);
        netName.setEnabled(false);
        netMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sureNet.setEnabled(true);
                cancelNet.setEnabled(true);
                netName.setEnabled(true);
                sureNet.setVisibility(View.VISIBLE);
                cancelNet.setVisibility(View.VISIBLE);
                netName.setVisibility(View.VISIBLE);
            }
        });
        sureNet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                path = netName.getText().toString();
                sureNet.setEnabled(false);
                cancelNet.setEnabled(false);
                netName.setEnabled(false);
                sureNet.setVisibility(View.INVISIBLE);
                cancelNet.setVisibility(View.INVISIBLE);
                netName.setVisibility(View.INVISIBLE);
                netName.setText("");
                try {
                    play(path);
                } catch (IOException e) {
                    play(0);
                    Toast.makeText(MP3.this, "网址错误或者资源无法播放", Toast.LENGTH_SHORT).show();
                }
            }
        });
        cancelNet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sureNet.setEnabled(false);
                cancelNet.setEnabled(false);
                netName.setEnabled(false);
                sureNet.setVisibility(View.INVISIBLE);
                cancelNet.setVisibility(View.INVISIBLE);
                netName.setVisibility(View.INVISIBLE);
                netName.setText("");
            }
        });
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
                int idx = index + 1 >= musicLists.length ? musicLists.length:index+1;
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

            String str =musicLists[index]+"\n";
            str += "  总时长：" + (duration / 1000 / 60) + "分" + (duration / 1000 % 60) + "秒"+"\n";
            str += "  当前进度：" + (current / 1000 / 60) + "分" + (current / 1000 % 60) + "秒"+"\n";
            textView.setText(str);
            float degree = cdImage.getRotation()+20;
            if(degree>= 360){
                degree = 0;
            }
            cdImage.setRotation(degree);
            if(duration/1000 == current/1000){
                if(index!=6) {
                    play(index + 1);
                    index++;
                }else{
                    play(0);
                    index = 0;
                }
            }
        }
    }
    private int getRawResourceId(String resourceName) {
        return this.getResources().getIdentifier(resourceName, "raw", this.getPackageName());
    }
    private void play(int index) {
        // Initialize the MediaPlayer with the music file from the raw directory
        if (mediaPlayer!=null) {
            if(mediaPlayer.isPlaying()){
                mediaPlayer.stop();
            }
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        mediaPlayer = new MediaPlayer();
        mediaPlayer = MediaPlayer.create(this, getRawResourceId(musicLists[index]));
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.start();
    }

    private void play(String path) throws IOException {
        // Initialize the MediaPlayer with the music file from the raw directory
        if (mediaPlayer!=null) {
            if(mediaPlayer.isPlaying()){
                mediaPlayer.stop();
            }
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setDataSource(path);
        mediaPlayer.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
