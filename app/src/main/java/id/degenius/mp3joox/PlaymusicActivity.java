package id.degenius.mp3joox;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.marcinmoskala.arcseekbar.ArcSeekBar;
import com.marcinmoskala.arcseekbar.ProgressListener;

import static android.widget.Toast.LENGTH_SHORT;

public class PlaymusicActivity extends AppCompatActivity  implements BackgroundSoundService.Callbacks {
    MediaPlayer mp;
    public static String judul,album,imgurl,duration,link,penyanyi;
    ImageView imgplay,imgpause,imagealbum;
    TextView tvdura,tvjudul,tvpenyanyi,tvalbum;

    ArcSeekBar arcSeekBar;
    BackgroundSoundService backgroundSoundService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playmusic);
        tvdura=findViewById(R.id.tvTime);
        tvjudul=findViewById(R.id.tvTitleMusic);
        tvpenyanyi=findViewById(R.id.tvBand);
        tvalbum=findViewById(R.id.tvalbum);
        imagealbum=findViewById(R.id.imgCover);

        arcSeekBar = findViewById(R.id.seekArc);
        arcSeekBar.setProgress(0);
        arcSeekBar.setMaxProgress(BackgroundSoundService.MusicUtils.MAX_PROGRESS);
        int[] intArray = getResources().getIntArray(R.array.progressGradientColors);
        arcSeekBar.setProgressGradient(intArray);
        imgplay=findViewById(R.id.imgPlay);
        imgpause=findViewById(R.id.imgPause);
            judul = getIntent().getStringExtra("judul");
            imgurl = getIntent().getStringExtra("imgurl");
            album=getIntent().getStringExtra("album");
            duration=getIntent().getStringExtra("duration");
            penyanyi=getIntent().getStringExtra("penyanyi");
            tvdura.setText(duration);
            tvjudul.setText(judul);
            tvpenyanyi.setText(penyanyi);
            tvalbum.setText(album);
            link=getIntent().getStringExtra("link");

            Glide.with(getApplicationContext()).load(imgurl).error(R.drawable.ic_music).into(imagealbum);
            playmusic(link);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow()
                .getDecorView()
                .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        imgplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backgroundSoundService.onResume();
                imgplay.setVisibility(View.GONE);
                imgpause.setVisibility(View.VISIBLE);

            }
        });


        imgpause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backgroundSoundService.onPause();
                imgpause.setVisibility(View.GONE);
                imgplay.setVisibility(View.VISIBLE);
            }
        });


    }


    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }





    public void playmusic(String link){

        if (isMyServiceRunning(BackgroundSoundService.class)) {

            stopService(new Intent(PlaymusicActivity.this, BackgroundSoundService.class));
        }


        Intent intent = new Intent(PlaymusicActivity.this, BackgroundSoundService.class);
        intent.putExtra("link",link);
        intent.putExtra("judul",judul);
        intent.setAction("START");
        startService(intent);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE); //Binding to the service!

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            BackgroundSoundService.LocalBinder binder = (BackgroundSoundService.LocalBinder) service;
            backgroundSoundService = binder.getServiceInstance(); //Get instance of your service!
            backgroundSoundService.registerClient(PlaymusicActivity.this); //Activity register in the service as client for callabcks!

        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            Toast.makeText(PlaymusicActivity.this, "onServiceDisconnected called", Toast.LENGTH_SHORT).show();

        }
    };

    @Override
    public void updateClient(String status) {
        if (status.equals("play")){
            imgplay.setVisibility(View.GONE);
            imgpause.setVisibility(View.VISIBLE);

        }

         }


    @Override
    public void updateseekbar(int current,String total) {
        tvdura.setText(total);
        arcSeekBar.setProgress(current);

    }





}
