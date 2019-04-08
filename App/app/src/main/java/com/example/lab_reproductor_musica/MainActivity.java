package com.example.lab_reproductor_musica;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity
{
    private MediaPlayer mediaPlayer;
    private AudioManager audioManager;
    private Field[] trackList;
    private int trackId;
    private boolean isPlaying;
    private SeekBar trackProgress;
    private Timer timer = new Timer();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Initialize the variables
        try
        {
            trackList = R.raw.class.getFields();
            trackId = 0;
            isPlaying = false;
            trackProgress = findViewById(R.id.seekBarProgress);
            audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            mediaPlayer = MediaPlayer.create(this, getTrackId(trackId));
            setTrackList();
            //Volumen bar
            SeekBar volumenControl = findViewById(R.id.seekBarVolume);
            volumenControl.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
            volumenControl.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
            volumenControl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
            {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
                {
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar){}

                @Override
                public void onStopTrackingTouch(SeekBar seekBar){}
            });
            //Track progress
            trackProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
            {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
                {
                    if(fromUser)
                    {
                        mediaPlayer.seekTo(progress);
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar){}

                @Override
                public void onStopTrackingTouch(SeekBar seekBar){}
            });
            trackProgress.setMax(mediaPlayer.getDuration());
            setTimer();
            play();
            ListView listView = findViewById(R.id.listViewTracks);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    trackId = position;
                    getTrackById(position);
                }
            });
            TextView txt = findViewById(R.id.lbTrackName);
            txt.setText(trackList[trackId].getName());
        }
        catch(IllegalAccessException e)
        {
            Log.i("track", e.getMessage());
        }
    }

    private void setTimer()
    {
        timer.scheduleAtFixedRate(
            new TimerTask()
            {
                @Override
                public void run()
                {
                    if(trackProgress.getMax() != trackProgress.getProgress())
                    {
                        trackProgress.setProgress(mediaPlayer.getCurrentPosition());
                    }
                }
            }, 0, 1000
        );
    }

    public void setTrackList()
    {
        ListView listView = findViewById(R.id.listViewTracks);
        ArrayList<String> trackNameList = new ArrayList<>();
        for(Field field : trackList)
        {
            trackNameList.add(field.getName());
        }
        ArrayAdapter<String> adapter =  new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, trackNameList);
        listView.setAdapter(adapter);
    }

    public void getTrackById(int pTrackId)
    {
        try
        {
            TextView txt = findViewById(R.id.lbTrackName);
            int newTrackId = getTrackId(pTrackId);
            mediaPlayer.release();
            mediaPlayer = MediaPlayer.create(this, newTrackId);
            txt.setText(trackList[trackId].getName());
            trackProgress.setMax(mediaPlayer.getDuration() + 1000);
            play();
        }
        catch(Exception e)
        {
            Log.i("track", e.getMessage());
        }
    }

    private void stop(int newTrackId)
    {
        if(trackProgress.getMax() == trackProgress.getProgress())
        {

        }
        else
        {
            mediaPlayer.release();
            mediaPlayer = MediaPlayer.create(this, newTrackId);
            setTimer();
        }
    }

    private int getTrackId(int pTrackId) throws IllegalAccessException
    {
        return trackList[pTrackId].getInt(trackList[pTrackId]); //extracted from stackoverflow
    }

    private void pause()
    {
        mediaPlayer.pause();
        ((ImageView)findViewById(R.id.btnPlay)).setImageResource(R.drawable.icon_play);
        isPlaying = false;
    }

    private void play()
    {
        mediaPlayer.start();
        ((ImageView)findViewById(R.id.btnPlay)).setImageResource(R.drawable.icon_pause);
        isPlaying = true;
    }

    public void btnListenerPrevious(View view)
    {
        if(trackId == 0)
        {
            trackId = trackList.length - 1;
        }
        else
        {
            trackId--;
        }
        getTrackById(trackId);
    }

    public void btnListenerPlay(View view)
    {
        if(isPlaying)
        {
            isPlaying = false;
            pause();
        }
        else
        {
            isPlaying = true;
            play();
        }
    }

    public void btnListenerNext(View view)
    {
        if(trackId == trackList.length - 1)
        {
            trackId = 0;
        }
        else
        {
            trackId++;
        }
        getTrackById(trackId);
    }
}
