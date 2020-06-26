package id.degenius.mp3joox;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    getsong("https://fando.xyz/musicpedia/api/search.php?q=peterpan");





    }

    private void getsong (String url){

        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {


                try {

                    JSONArray jsonArray = response.getJSONArray("content");

                    for (int i =1; i>response.length() ;i++){
                        JSONObject jsonObject= jsonArray.getJSONObject(i);

                        String judul = jsonObject.getString("judul");

                        System.out.println("test " +jsonArray);






                    }







                } catch (JSONException e) {


                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error);

            }
        });

        Volley.newRequestQueue(getApplicationContext()).add(jsonObjectRequest);


    }
}
