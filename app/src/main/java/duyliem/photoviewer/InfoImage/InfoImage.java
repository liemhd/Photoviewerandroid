package duyliem.photoviewer.InfoImage;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.AccessToken;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import duyliem.photoviewer.ImageFull.FullScreenImage;
import duyliem.photoviewer.Object.Photo;
import duyliem.photoviewer.R;

public class InfoImage extends AppCompatActivity {

    ImageView imvImage, imvSetWallpaper, imvShare, imvDisLike, imvDown;
    RecyclerView rvCmt;
    CircleImageView imvAvt;
    EditText etCmt;
    ImageView imvSend;
    ArrayList<Comment> comments;
    CommentAdapter cmtAdapter;
    FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference dataCmt;
    Bitmap bitmap = null;
    CollapsingToolbarLayout toolbarLayout;
    boolean liked = false;
    private DatabaseReference dataHome;
    private DatabaseReference dataUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_image);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        final Photo photo = new Photo();

        ShimmerFrameLayout shimmerFrameLayout = findViewById(R.id.Shimmer);
        shimmerFrameLayout.startShimmer();

        Mapped();

        dataHome = FirebaseDatabase.getInstance().getReference().child("Home");
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        comments = new ArrayList<>();
        cmtAdapter = new CommentAdapter(comments);

        final String name = getIntent().getStringExtra("name");
        final String url = getIntent().getStringExtra("url");
        final String key = getIntent().getStringExtra("key");

        setLike(key);

        toolbarLayout.setTitle(name);

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();
        ImageLoader.getInstance().init(config);
        ImageLoader.getInstance().displayImage(url, imvImage, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {

            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap loadedImage) {
                bitmap = loadedImage;
            }

            @Override
            public void onLoadingCancelled(String s, View view) {

            }
        });

        dataCmt = FirebaseDatabase.getInstance().getReference("Home").child(key).child("Comment");

        Glide.with(this).load(url).into(imvImage);

        imvImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), FullScreenImage.class);
                intent.putExtra("name", name);
                intent.putExtra("url", url);
                startActivity(intent);
            }
        });

        imvShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                shareImage();
                Intent intent = new Intent(Intent.ACTION_SEND);

                intent.setType("text/plain");

                intent.putExtra(Intent.EXTRA_TEXT, url);

                startActivity(Intent.createChooser(intent, "Share Image"));
            }
        });

        imvSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cmt = etCmt.getText().toString();
                if (!cmt.equals("")) {
                    if (user != null) {
                        String avt = String.valueOf(user.getPhotoUrl());
                        String name = user.getDisplayName();
                        dataCmt = FirebaseDatabase.getInstance().getReference("Home").child(key).child("Comment").push();
                        Map map = new HashMap();
                        map.put("avatar", avt);
                        map.put("comment", cmt);
                        map.put("name", name);
                        dataCmt.setValue(map);

                    } else {
                        Toast.makeText(getBaseContext(), "You need to login!", Toast.LENGTH_SHORT).show();
                    }
                }
                etCmt.setText(null);


            }
        });

        imvDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DownImage(name, url, key);
            }
        });

        imvDisLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                liked = true;
                user = auth.getCurrentUser();
                if (user != null) {
                    auth = FirebaseAuth.getInstance();
                    final String nameUser = user.getDisplayName();
                    final String idUser = String.valueOf(user.getPhotoUrl());
                    final String emailUser = user.getEmail();
                    final String userId = user.getUid();
                    dataUsers = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("like");
                    dataUsers.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (liked) {
                                if (dataSnapshot.child(key).hasChild("links")) {
                                    dataHome.child(key).child("like").child(userId).removeValue();
                                    dataUsers.child(key).removeValue();
                                    liked = false;
                                } else {
                                    Calendar cal = Calendar.getInstance();
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    String strDate = sdf.format(cal.getTime());
                                    Map map = new HashMap();
                                    map.put("name", nameUser);
                                    map.put("avatar", idUser);
                                    map.put("email", emailUser);
                                    dataHome.child(key).child("like").child(userId).setValue(map);
                                    dataUsers.child(key).child("name").setValue(name);
                                    dataUsers.child(key).child("links").setValue(url);
                                    dataUsers.child(key).child("time").setValue(strDate);
                                    liked = false;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                } else {
                    imvDisLike.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                    Toast.makeText(getBaseContext(), "You need to login!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (user != null) {
            String avt = String.valueOf(user.getPhotoUrl());
            Glide.with(this).load(avt).into(imvAvt);
        }

        dataCmt.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Comment comment = dataSnapshot.getValue(Comment.class);
                comments.add(comment);

                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getBaseContext());
                linearLayoutManager.setStackFromEnd(true);

                rvCmt.setLayoutManager(linearLayoutManager);
                cmtAdapter = new CommentAdapter(comments);
                rvCmt.setAdapter(cmtAdapter);

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        imvSetWallpaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                WallpaperManager wallpaperManager = WallpaperManager.getInstance(getApplicationContext());
                DisplayMetrics displayMetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

                int with = displayMetrics.widthPixels;
                int height = displayMetrics.heightPixels;

                try {
                    if (bitmap != null) {
                        wallpaperManager.setBitmap(bitmap);
                        wallpaperManager.suggestDesiredDimensions(with, height);
                        Toast.makeText(getBaseContext(), "Wallpaper Set", Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        });


    }

    private void DownImage(final String name, final String url, String key) {
        File direct = new File(Environment.getExternalStorageDirectory() + "/PhotoViewer/" + name + ".jpg");

        if (direct.exists()) {
            AlertDialog alertDialog = new AlertDialog.Builder(InfoImage.this)
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
                                    .setDestinationInExternalPublicDir("/PhotoViewer", name + ".jpg");

                            mgr.enqueue(request);
                            Toast.makeText(getBaseContext(), "Download Image " + name, Toast.LENGTH_LONG).show();
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
                    .setDestinationInExternalPublicDir("/PhotoViewer", name + ".jpg");

            mgr.enqueue(request);
            Toast.makeText(getBaseContext(), "Download Image " + name, Toast.LENGTH_LONG).show();
        }
    }


    private void Mapped() {
        imvAvt = findViewById(R.id.imvUsers);
        imvImage = findViewById(R.id.imvImage);
        imvSetWallpaper = findViewById(R.id.imvSetWallpaper);
        imvShare = findViewById(R.id.imvShare);
        imvDisLike = findViewById(R.id.imvDisLike);
        imvDown = findViewById(R.id.imvDown);
        rvCmt = findViewById(R.id.rvCmt);
        etCmt = findViewById(R.id.etCmt);
        imvSend = findViewById(R.id.imvSend);
        toolbarLayout = findViewById(R.id.collapsing_toolbar);
    }

    private void shareImage() {

        Bitmap bitmap = getBitmapFromView(imvImage);
        try {
            File file = new File(this.getExternalCacheDir(), "logicchip.png");
            FileOutputStream fOut = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();
            file.setReadable(true, false);
            final Intent intent = new Intent(android.content.Intent.ACTION_SEND);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Intent.EXTRA_TEXT, "");
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
            intent.setType("image/png");
            startActivity(Intent.createChooser(intent, "Share Image"));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private Bitmap getBitmapFromView(View view) {
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null) {
            bgDrawable.draw(canvas);
        } else {
            canvas.drawColor(Color.WHITE);
        }
        view.draw(canvas);
        return returnedBitmap;
    }

    public void setLike(final String key) {
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        if (user != null) {
            dataUsers = FirebaseDatabase.getInstance().getReference("Users").child(auth.getCurrentUser().getUid()).child("like");
            dataUsers.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(key).hasChild("name")) {
                        imvDisLike.setImageResource(R.drawable.ic_favorite_white_24dp);
                    } else {
                        imvDisLike.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else {
            imvDisLike.setImageResource(R.drawable.ic_favorite_border_black_24dp);
        }

    }

}
