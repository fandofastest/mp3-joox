package id.degenius.mp3joox;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.krossovochkin.bottomsheetmenu.BottomSheetMenu;

import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static android.content.ContentValues.TAG;
import static android.content.Context.DOWNLOAD_SERVICE;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MyViewHolder> {
    List<Songmodel> songmodelList;
    public Context ctx;



    public MusicAdapter(List<Songmodel> songmodelList, Context ctx) {
        this.songmodelList = songmodelList;
        this.ctx = ctx;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemview = LayoutInflater.from(parent.getContext()).inflate(R.layout.item,parent,false);

        return new MyViewHolder(itemview);
    }

    @Override
    public void onBindViewHolder(@NonNull MusicAdapter.MyViewHolder holder, final int position) {
        final Songmodel songmodel=songmodelList.get(position);
        holder.txttitle.setText(songmodel.getJudul());
        holder.txtartis.setText(songmodel.getPenyanyi());
        holder.txtduration.setText(songmodel.getDurasi());

        Glide.with(ctx)
                .load(songmodel.getLinkimage())
                .centerCrop()
                .placeholder(R.mipmap.ic_launcher)
                .into(holder.imageView);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new BottomSheetMenu.Builder(ctx, new BottomSheetMenu.BottomSheetMenuListener() {
                    @Override
                    public void onCreateBottomSheetMenu(MenuInflater inflater, Menu menu) {
                        inflater.inflate(R.menu.menu, menu);


                    }

                    @Override
                    public void onBottomSheetMenuItemSelected(MenuItem item) {
                        final int itemId = item.getItemId();
                        switch (itemId) {
                            case R.id.play:

                                Intent intent = new Intent(ctx, PlaymusicActivity.class);
                                intent.putExtra("judul",songmodel.getJudul());
                                intent.putExtra("album",songmodel.getAlbum());
                                intent.putExtra("duration",songmodel.getDurasi());
                                intent.putExtra("penyanyi",songmodel.getPenyanyi());
                                intent.putExtra("imgurl",songmodel.getLinkimage());
                                intent.putExtra("link",songmodel.getLink());
                                ctx.startActivities(new Intent[]{intent});


                                break;

                            case R.id.dl:

                                SweetAlertDialog pDialog = new SweetAlertDialog(ctx, SweetAlertDialog.PROGRESS_TYPE);
                                pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));

                                pDialog.setTitleText("Downloading File");
                                pDialog.setCancelable(false);
                                pDialog.show();

                                long downloadFileRef = downloadFile(Uri.parse(songmodel.getLink()), Environment.DIRECTORY_DOWNLOADS, songmodel.getPenyanyi()+"-"+songmodel.getJudul()+".mp3");


                                if (downloadFileRef != 0) {

                                    pDialog.setTitleText("Download Started");
                                    pDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);


                                }else {

                                    pDialog.setTitleText("Download Failed");
                                    pDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);


                                }


                                break;
                        }

                    }
                }).show();

            }
        });


    }

    @Override
    public int getItemCount() {
        return songmodelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txtartis,txttitle,txtduration;
        ImageView imageView;
        CardView cardView;


        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtartis=itemView.findViewById(R.id.tvBand);
            txttitle=itemView.findViewById(R.id.tvTitleMusic);
            txtduration=itemView.findViewById(R.id.tvDura);
            imageView=itemView.findViewById(R.id.imgCover);
            cardView=itemView.findViewById(R.id.cvListMusic);

        }
    }

    private long downloadFile(Uri uri, String fileStorageDestinationUri, String fileName) {

        long downloadReference = 0;

        DownloadManager downloadManager = (DownloadManager)ctx.getSystemService(DOWNLOAD_SERVICE);
        try {
            DownloadManager.Request request = new DownloadManager.Request(uri);

            //Setting title of request
            request.setTitle(fileName);

            //Setting description of request
            request.setDescription("Your file is downloading");

            //set notification when download completed
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

            //Set the local destination for the downloaded file to a path within the application's external files directory
            request.setDestinationInExternalPublicDir(fileStorageDestinationUri, fileName);

            request.allowScanningByMediaScanner();

            //Enqueue download and save the referenceId
            downloadReference = downloadManager.enqueue(request);
        } catch (IllegalArgumentException e) {
            Toast.makeText(ctx,"Download link is broken or not availale for download",Toast.LENGTH_LONG).show();

            Log.e(TAG, "Line no: 455,Method: downloadFile: Download link is broken");

        }
        return downloadReference;
    }
}
