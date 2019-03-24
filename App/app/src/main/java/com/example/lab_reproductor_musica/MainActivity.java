package com.example.lab_reproductor_musica;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.lang.reflect.Field;

public class MainActivity extends AppCompatActivity
{
    private MediaPlayer mediaPlayer;
    private AudioManager audioManager;
    private Field[] trackList;
    private int trackId;
    private boolean isPlaying;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Inicializar las variables
        trackList = R.raw.class.getFields();
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        trackId = 0;
        isPlaying = false;
        getTrackById(trackId);
    }

    public void getTrackById(int pTrackId)
    {
        String track = trackList[pTrackId].getName();
        Uri trackUri = Uri.parse(R.raw.class.getResource(track).getPath());
        mediaPlayer = MediaPlayer.create(this, trackUri);
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
    }

    public void btnListenerPlay(View view)
    {
        if(isPlaying)
        {
            isPlaying = false;
            mediaPlayer.pause();
        }
        else
        {
            isPlaying = true;
            mediaPlayer.start();
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
    }
}
