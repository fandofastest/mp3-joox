package id.degenius.mp3joox;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.ixidev.gdpr.GDPRChecker;

import org.json.JSONException;
import org.json.JSONObject;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class Splash_Activity extends AppCompatActivity {
    public static String statususer,banner,inter,admobappid,statusapp,apkupdate;
    InterstitialAd mInterstitialAd;
    SweetAlertDialog pDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Loading");
        pDialog.setCancelable(false);
        getStatusapp("https://musicpedia.xyz/api/getstatus.php");
    }


    private void getStatusapp(String url){
        pDialog.show();
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
//                    JSONObject jsonObject=response.getJSONObject("status");
                    statususer = response.getString("status");
                    banner = response.getString("banner");
                    inter=response.getString("inter");
                    admobappid=response.getString("admobappid");
                    apkupdate=response.getString("apkupdate");
                    statusapp=response.getString("statusapp");
                    Button button= findViewById(R.id.buttonstart);
                    pDialog.hide();
                    button.setVisibility(View.VISIBLE);

                    if (statusapp.equals("0")){
                        update();
                        button.setText("UPDATE");
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                update();
                            }
                        });
                    }
                    else{
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                showinter();
                            }
                        });
                    }
                    new GDPRChecker()
                            .withContext(Splash_Activity.this)
                            .withPrivacyUrl(getString(R.string.privacy_url)) // your privacy url
                            .withPublisherIds(admobappid) // your admob account Publisher id
                            .withTestMode("9424DF76F06983D1392E609FC074596C") // remove this on real project
                            .check();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        Volley.newRequestQueue(getApplicationContext()).add(jsonObjectRequest);
    }

    private void update() {
        new SweetAlertDialog(Splash_Activity.this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Update App")
                .setContentText("App Need To Update")
                .setConfirmText("Update")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog
                                .setTitleText("Update From Playstore")
                                .setContentText("Please Wait, Open Playstore")
                                .setConfirmText("Go")
                                .changeAlertType(SweetAlertDialog.PROGRESS_TYPE);

                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setData(Uri.parse(
                                        "https://play.google.com/store/apps/details?id=" + apkupdate));
                                intent.setPackage("com.android.vending");
                                startActivity(intent);
//                                Do something after 100ms
                            }
                        }, 3000);
                    }
                })

                .show();
    }


    public  void showinter() {
        Button button= findViewById(R.id.buttonstart);
        pDialog.show();
        button.setVisibility(View.GONE);
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(inter);
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                mInterstitialAd.show();
                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                kehome();

                // Code to be executed when an ad request fails.
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when the ad is displayed.
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                kehome();
                // Code to be executed when the interstitial ad is closed.
            }
        });


    }

    public  void kehome(){
        Intent intent = new Intent(Splash_Activity.this,MainActivity.class);
        startActivity(intent);
    }

}
