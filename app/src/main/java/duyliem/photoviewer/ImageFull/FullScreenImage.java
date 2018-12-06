package duyliem.photoviewer.ImageFull;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jsibbold.zoomage.ZoomageView;

import java.io.File;

import duyliem.photoviewer.R;

public class FullScreenImage extends FragmentActivity {

    ZoomageView myImage;
    ImageView imvDown;
    TextView tvName;
    ImageView imvBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);

        myImage = findViewById(R.id.myImage);
        tvName = findViewById(R.id.tvName);
        imvBack = findViewById(R.id.imvBack);
        imvDown = findViewById(R.id.imvDown);
        final ShimmerFrameLayout shimmerFrameLayout = findViewById(R.id.Shimmer);
        shimmerFrameLayout.startShimmer();

        final String name = getIntent().getStringExtra("name");
        final String url = getIntent().getStringExtra("url");


        tvName.setText(name);
        Glide.with(this).load(url)
                .listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                shimmerFrameLayout.setVisibility(View.GONE);
                return false;
            }
        })
                .into(myImage);

        imvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        imvDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DownImage(url, name);
            }
        });
    }

    private void DownImage(final String url, final String nameImage) {

        final String key = getIntent().getStringExtra("key");
        File direct = new File(Environment.getExternalStorageDirectory() + "/PhotoViewer/" + nameImage + ".jpg");

        if (direct.exists()) {
            AlertDialog alertDialog = new AlertDialog.Builder(FullScreenImage.this)
                    .setTitle("Download Image...")
                    .setMessage("You have already downloaded this photo. Do you want to download again?")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            DownloadManager mgr = (DownloadManager) getBaseContext().getSystemService(Context.DOWNLOAD_SERVICE);

                            Uri downloadUri = Uri.parse(url);
                            DownloadManager.Request request = new DownloadManager.Request(
                                    downloadUri);

                            request.setAllowedNetworkTypes(
                                    DownloadManager.Request.NETWORK_WIFI
                                            | DownloadManager.Request.NETWORK_MOBILE)
                                    .setAllowedOverRoaming(false).setTitle("Downloading...")
                                    .setDescription("Something useful. No, really.")
                                    .setDestinationInExternalPublicDir("/PhotoViewer", nameImage + ".jpg");

                            mgr.enqueue(request);
                            Toast.makeText(getBaseContext(), "Download Image " + nameImage, Toast.LENGTH_LONG).show();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .create();
            alertDialog.show();
        } else {

            DatabaseReference dataDownload = FirebaseDatabase.getInstance().getReference("Home").child(key);
            dataDownload.child("download").push().setValue("download");

            DownloadManager mgr = (DownloadManager) getBaseContext().getSystemService(Context.DOWNLOAD_SERVICE);

            Uri downloadUri = Uri.parse(url);
            DownloadManager.Request request = new DownloadManager.Request(
                    downloadUri);

            request.setAllowedNetworkTypes(
                    DownloadManager.Request.NETWORK_WIFI
                            | DownloadManager.Request.NETWORK_MOBILE)
                    .setAllowedOverRoaming(false).setTitle("Downloading...")
                    .setDescription("Something useful. No, really.")
                    .setDestinationInExternalPublicDir("/PhotoViewer", nameImage + ".jpg");

            mgr.enqueue(request);
            Toast.makeText(getBaseContext(), "Download Image " + nameImage, Toast.LENGTH_LONG).show();
        }
    }
}
