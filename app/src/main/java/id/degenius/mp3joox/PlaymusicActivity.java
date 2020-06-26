package id.degenius.mp3joox;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.marcinmoskala.arcseekbar.ArcSeekBar;
import com.marcinmoskala.arcseekbar.ProgressListener;

import static android.widget.Toast.LENGTH_SHORT;

public class PlaymusicActivity extends AppCompatActivity  {
    MediaPlayer mp;
    String judul,album,imgurl,duration,link,penyanyi;
    ImageView imgplay,imgpause,imagealbum;
    TextView tvdura,tvjudul,tvpenyanyi,tvalbum;

    private Handler mHandler = new Handler();
    ArcSeekBar arcSeekBar;


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
        arcSeekBar.setMaxProgress(MusicUtils.MAX_PROGRESS);


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
                mp.start();
                imgplay.setVisibility(View.GONE);
                imgpause.setVisibility(View.VISIBLE);
                mHandler.post(mUpdateTimeTask);
            }
        });


        imgpause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp.pause();
                imgpause.setVisibility(View.GONE);
                imgplay.setVisibility(View.VISIBLE);
            }
        });


    }






    public void playmusic(String link){

        // Media PlayerActivity
        mp = new MediaPlayer();
        mp.stop();
        mp.reset();
        mp.release();
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                // Changing button image to play button
//                bt_play.setImageResource(R.drawable.ic_play_circle_outline_black_24dp);
            }
        });

        try {
            Uri myUri = Uri.parse(link);
            mp = new MediaPlayer();
            mp.setDataSource(this, myUri);
            mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mp.prepareAsync(); //don't use prepareAsync for mp3 playback
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Cannot load audio file", LENGTH_SHORT).show();

        }

        mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onPrepared(MediaPlayer mplayer) {
                if (mp.isPlaying()) {
                    mp.pause();
                    imgpause.setVisibility(View.GONE);
                    imgplay.setVisibility(View.VISIBLE);

                    // Changing button image to play button

                } else {
                    // Resume song

                    mp.start();
                    imgplay.setVisibility(View.GONE);
                    imgpause.setVisibility(View.VISIBLE);
                    mHandler.post(mUpdateTimeTask);
                    // Changing button image to pause button

                }

            }
        });

        mp.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {


            }
        });
    }

    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            updateTimerAndSeekbar();
            // Running this thread after 10 milliseconds
            if (mp.isPlaying()) {
                mHandler.postDelayed(this, 100);
            }
        }
    };

    private void updateTimerAndSeekbar() {

        long totalDuration = mp.getDuration();
        long currentDuration = mp.getCurrentPosition();

        // Displaying Total Duration time
        MusicUtils utils = new MusicUtils();
//        tv_song_total_duration.setText(utils.milliSecondsToTimer(totalDuration));
        // Displaying time completed playing
        tvdura.setText(utils.milliSecondsToTimer(currentDuration));

        // Updating progress bar
        int progress = (int) (utils.getProgressSeekBar(currentDuration, totalDuration));
        arcSeekBar.setProgress(progress);
    }

    public class MusicUtils {

        public static final int MAX_PROGRESS = 10000;

        /**
         * Function to convert milliseconds time to
         * Timer Format
         * Hours:Minutes:Seconds
         */
        public String milliSecondsToTimer(long milliseconds) {
            String finalTimerString = "";
            String secondsString = "";

            // Convert total duration into time
            int hours = (int) (milliseconds / (1000 * 60 * 60));
            int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
            int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);
            // Add hours if there
            if (hours > 0) {
                finalTimerString = hours + ":";
            }

            // Prepending 0 to seconds if it is one digit
            if (seconds < 10) {
                secondsString = "0" + seconds;
            } else {
                secondsString = "" + seconds;
            }

            finalTimerString = finalTimerString + minutes + ":" + secondsString;

            // return timer string
            return finalTimerString;
        }

        /**
         * Function to get Progress percentage
         *
         * @param currentDuration
         * @param totalDuration
         */
        public int getProgressSeekBar(long currentDuration, long totalDuration) {
            Double progress = (double) 0;
            // calculating percentage
            progress = (((double) currentDuration) / totalDuration) * MAX_PROGRESS;

            // return percentage
            return progress.intValue();
        }

        /**
         * Function to change progress to timer
         *
         * @param progress - totalDuration
         *                 returns current duration in milliseconds
         */
        public int progressToTimer(int progress, int totalDuration) {
            int currentDuration = 0;
            totalDuration = (int) (totalDuration / 1000);
            currentDuration = (int) ((((double) progress) / MAX_PROGRESS) * totalDuration);

            // return current duration in milliseconds
            return currentDuration * 1000;
        }

    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(mUpdateTimeTask);
        mp.release();
    }



}
