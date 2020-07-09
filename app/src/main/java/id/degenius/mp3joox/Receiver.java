package id.degenius.mp3joox;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class Receiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //Method ini digunakan untuk menerima pesan Broadcast yang masuk
        Toast.makeText(context, "Nama Action: "+intent.getAction(), Toast.LENGTH_LONG).show();
        Toast.makeText(context, "Pesan: "+intent.getStringExtra("STATUS"), Toast.LENGTH_LONG).show();
    }

}