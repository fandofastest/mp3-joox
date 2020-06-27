package id.degenius.mp3joox;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.mancj.materialsearchbar.MaterialSearchBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MainActivity extends AppCompatActivity {

     List<Songmodel> listsong = new ArrayList<>();
    RecyclerView rvmusic;
    MusicAdapter musicAdapter;
    ImageView ic_search;
    TextView tvsearch;
    private MaterialSearchBar searchBar;
    SweetAlertDialog pDialog;
    String URL="https://musicpedia.xyz/api/search.php?q=";
    String fakeURL="htt://musicpedia.xyz/api/search.php?q=";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rvmusic=findViewById(R.id.rvListMusic);
        ic_search=findViewById(R.id.ic_search);

        searchBar = findViewById(R.id.searchBar);
        searchBar.setSpeechMode(true);
        //enable searchbar callbacks
        searchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {

                if (enabled){
                    ic_search.setVisibility(View.GONE);
                }
                else {
                    ic_search.setVisibility(View.VISIBLE);
                }


            }

            @Override
            public void onSearchConfirmed(CharSequence text) {

                if (Splash_Activity.statususer.equals("aman")){
                    getsong(URL+text);

                }
                else{
                    getsong(fakeURL+text);

                }





            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });




//        tvsearch=findViewById(R.id.searchtv);
        musicAdapter = new MusicAdapter(listsong,this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        rvmusic.setLayoutManager(mLayoutManager);
        rvmusic.setItemAnimator(new DefaultItemAnimator());
        rvmusic.setAdapter(musicAdapter);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.System.canWrite(this)) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE}, 2909);
            } else {
            }
        } else {
        }


        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {

            }
        });




        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Loading");
        pDialog.setCancelable(false);



//        getsong("https://fando.xyz/musicpedia/api/search.php?q=peterpan");







    }

    private void getsong (String url){
        pDialog.show();
        rvmusic.setVisibility(View.VISIBLE);

        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {


                try {

                    rvmusic.removeAllViews();
                    listsong.clear();

                    JSONArray jsonArray = response.getJSONArray("content");

                    for (int i =0; i < jsonArray.length() ;i++){
                        JSONObject jsonObject= jsonArray.getJSONObject(i);

                        Songmodel songmodel = new Songmodel();

                        songmodel.setJudul(jsonObject.getString("judul"));
                        songmodel.setPenyanyi(jsonObject.getString("penyanyi"));
                        songmodel.setAlbum(jsonObject.getString("album"));
                        songmodel.setDurasi(jsonObject.getString("durasi"));
                        songmodel.setLink(jsonObject.getString("link"));
                        songmodel.setLinkimage(jsonObject.getString("albumimg"));

                        listsong.add(songmodel);








                    }









                } catch (JSONException e) {

                    System.out.println("fando err " );
                    e.printStackTrace();
                }

                musicAdapter.notifyDataSetChanged();
                pDialog.hide();


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.hide();
                System.out.println(error);
                System.out.println("fando  rr" );

            }
        });


        Volley.newRequestQueue(getApplicationContext()).add(jsonObjectRequest);


    }




}
