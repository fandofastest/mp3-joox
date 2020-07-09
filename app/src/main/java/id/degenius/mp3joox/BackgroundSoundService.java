package id.degenius.mp3joox;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;

import static android.widget.Toast.LENGTH_SHORT;

public class BackgroundSoundService extends Service {
    MediaPlayer mp;
    Callbacks activity;
    public static String judul;
     Handler mHandler = new Handler();

    private final IBinder mBinder = new LocalBinder();

    private final String BROADCAST_ACCESS = "custom_broadcast_filter";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
    //Here Activity register to the service as Callbacks client
    public void registerClient(Activity activity){
        this.activity = (Callbacks)activity;
    }
    //callbacks interface for communication with service clients!
    public interface Callbacks{
        public void updateClient(String status);
        public void updateseekbar(int current,String total);
    }


    @Override
    public void onCreate() {
        super.onCreate();


    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent.getAction().equals("PAUSE")) {
            mp.pause();
          }
        if (intent.getAction().equals("RES")) {
//            mHandler.post(mUpdateTimeTask);
        }

       if (intent.getAction().equals("PLAY")) {
           mp.start();
        }

       else {
        String link = intent.getStringExtra("link");
        judul=intent.getStringExtra("judul");
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
                System.out.println("music play");
                activity.updateClient("play");

                if (mp.isPlaying()) {
                    mp.pause();
                    // Changing button image to play button

                } else {
                    // Resume song
                    mp.start();
                    mHandler.post(mUpdateTimeTask);
                }

            }
        });
        mp.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {
            }
        });
       }

        return START_NOT_STICKY;

    }
    //returns the instance of the service
    public class LocalBinder extends Binder {
        public BackgroundSoundService getServiceInstance(){
            return BackgroundSoundService.this;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mp.stop();
    }
    public  void onPause(){
        mp.pause();
    }
    public  void onResume(){
        mHandler.post(mUpdateTimeTask);
        mp.start();
    }





    public Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            updateTimerAndSeekbar();
            // Running this thread after 10 milliseconds
            if (mp.isPlaying()) {
                mHandler.postDelayed(this, 100);
            }
        }
    };

    public void updateTimerAndSeekbar() {

        long totalDuration = mp.getDuration();
        long currentDuration = mp.getCurrentPosition();

        // Displaying Total Duration time
        MusicUtils utils = new MusicUtils();
//        tv_song_total_duration.setText(utils.milliSecondsToTimer(totalDuration));
        // Displaying time completed playing
        String total=utils.milliSecondsToTimer(currentDuration);
        int current = (int) (utils.getProgressSeekBar(currentDuration, totalDuration));

        // Updating progress bar

        activity.updateseekbar(current,total);



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
}