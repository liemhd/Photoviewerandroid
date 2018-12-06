package duyliem.photoviewer.NavigationView;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.AccessToken;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.File;


import duyliem.photoviewer.InfoImage.InfoImage;
import duyliem.photoviewer.Object.Photo;
import duyliem.photoviewer.R;

public class Favorite extends AppCompatActivity {

    ImageView imvBack;
    RecyclerView rvFavorite;
    FirebaseUser user;
    FirebaseAuth auth;
    TextView tvNotification;
    AccessToken token;
    DatabaseReference dataUsers;
    DatabaseReference dataHome;
    boolean liked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        tvNotification = findViewById(R.id.tvNotification);
        imvBack = findViewById(R.id.imvBack);
        rvFavorite = findViewById(R.id.rvFavorite);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        token = AccessToken.getCurrentAccessToken();
        dataHome = FirebaseDatabase.getInstance().getReference().child("Home");

        LinearLayoutManager layoutManager = new LinearLayoutManager(getBaseContext());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        rvFavorite.setLayoutManager(layoutManager);

        if (user == null) {
            tvNotification.setVisibility(View.VISIBLE);
        } else {
            tvNotification.setVisibility(View.GONE);
            auth = FirebaseAuth.getInstance();
            dataUsers = FirebaseDatabase.getInstance().getReference("Users").child(auth.getCurrentUser().getUid()).child("like");
            Query query = dataUsers.orderByChild("time");
            FirebaseRecyclerOptions<Photo> options = new FirebaseRecyclerOptions.Builder<Photo>().setQuery(query, Photo.class).build();

            FirebaseRecyclerAdapter<Photo, PhotoViewHoler> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Photo, PhotoViewHoler>(options) {
                @NonNull
                @Override
                public PhotoViewHoler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.item_home, parent, false);
                    return new PhotoViewHoler(view);
                }

                @Override
                protected void onBindViewHolder(@NonNull final PhotoViewHoler holder, int position, @NonNull final Photo photo) {
                    final String key = getRef(position).getKey();
                    token = AccessToken.getCurrentAccessToken();

                    if (token != null) {
                        holder.setLike(key);
                    } else {
                        holder.imvFavorite.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                    }

                    holder.tvName.setText(photo.getName());
                    Glide.with(holder.imvImage.getContext())
                            .load(photo.getLinks())
                            .into(holder.imvImage);

                    DatabaseReference dataCountLike = FirebaseDatabase.getInstance().getReference("Home").child(key).child("like");
                    dataCountLike.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            Log.e(dataSnapshot.getKey(), dataSnapshot.getChildrenCount() + "");
                            int count = (int) dataSnapshot.getChildrenCount();
                            holder.tvCountLike.setText(count - 1 + "");

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    DatabaseReference dataCountDown = FirebaseDatabase.getInstance().getReference("Home").child(key).child("download");
                    dataCountDown.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            int count = (int) dataSnapshot.getChildrenCount();
                            holder.tvCountDown.setText(count + "");

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    holder.imvImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getBaseContext(), InfoImage.class);
                            intent.putExtra("name", photo.getName());
                            intent.putExtra("url", photo.getLinks());
                            intent.putExtra("key", key);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            getBaseContext().startActivity(intent);
                            Toast.makeText(getBaseContext(), photo.getName(), Toast.LENGTH_SHORT).show();
                        }
                    });
                    holder.imvDown.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            DownImage(photo.getName());
                        }

                        private void DownImage(String nameImage) {
                            File direct = new File(Environment.getExternalStorageDirectory() + "/PhotoViewer/" + nameImage + ".jpg");

                            if (direct.exists()) {
                                AlertDialog alertDialog = new AlertDialog.Builder(Favorite.this)
                                        .setTitle("Download Image...")
                                        .setMessage("You have already downloaded this photo. Do you want to download again?")
                                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                DownloadManager mgr = (DownloadManager) getBaseContext().getSystemService(Context.DOWNLOAD_SERVICE);

                                                Uri downloadUri = Uri.parse(photo.getLinks());
                                                DownloadManager.Request request = new DownloadManager.Request(
                                                        downloadUri);

                                                request.setAllowedNetworkTypes(
                                                        DownloadManager.Request.NETWORK_WIFI
                                                                | DownloadManager.Request.NETWORK_MOBILE)
                                                        .setAllowedOverRoaming(false).setTitle("Downloading...")
                                                        .setDescription("Something useful. No, really.")
                                                        .setDestinationInExternalPublicDir("/PhotoViewer", photo.getName() + ".jpg");

                                                mgr.enqueue(request);
                                                Toast.makeText(getBaseContext(), "Download Image " + photo.getName(), Toast.LENGTH_LONG).show();
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

                                Uri downloadUri = Uri.parse(photo.getLinks());
                                DownloadManager.Request request = new DownloadManager.Request(
                                        downloadUri);

                                request.setAllowedNetworkTypes(
                                        DownloadManager.Request.NETWORK_WIFI
                                                | DownloadManager.Request.NETWORK_MOBILE)
                                        .setAllowedOverRoaming(false).setTitle("Downloading...")
                                        .setDescription("Something useful. No, really.")
                                        .setDestinationInExternalPublicDir("/PhotoViewer", photo.getName() + ".jpg");

                                mgr.enqueue(request);
                                Toast.makeText(getBaseContext(), "Download Image " + photo.getName(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                    holder.imvFavorite.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            liked = true;
                            token = AccessToken.getCurrentAccessToken();
                            if (token != null) {
                                auth = FirebaseAuth.getInstance();
                                user = auth.getCurrentUser();
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
                                                dataHome.child(key).child("like").setValue(userId);
                                                dataUsers.child(key).child("name").setValue(photo.getName());
                                                dataUsers.child(key).child("links").setValue(photo.getLinks());
                                                dataUsers.child(key).child("created").setValue(photo.getCreated());
                                                dataUsers.child(key).child("tag").setValue(photo.getTag());
                                                dataUsers.child(key).child("type").setValue(photo.getType());
                                                liked = false;
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            } else {
                                Toast.makeText(getBaseContext(), "You need to login!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
            };


            rvFavorite.setAdapter(firebaseRecyclerAdapter);
            firebaseRecyclerAdapter.startListening();

        }


        imvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public static class PhotoViewHoler extends RecyclerView.ViewHolder {

        View view;
        DatabaseReference dataUsers;
        FirebaseAuth auth;
        ImageView imvFavorite;
        ImageView imvDown;
        TextView tvName, tvCountDown, tvCountLike;
        ImageView imvImage;


        public PhotoViewHoler(View itemView) {
            super(itemView);
            view = itemView;

            tvName = view.findViewById(R.id.tvName);
            imvImage = view.findViewById(R.id.imvImage);
            imvDown = view.findViewById(R.id.imvDown);
            imvFavorite = view.findViewById(R.id.imvUnFavorite);
            tvCountLike = view.findViewById(R.id.tvCountLike);
            tvCountDown = view.findViewById(R.id.tvCountDown);

            ShimmerFrameLayout shimmerFrameLayout = view.findViewById(R.id.Shimmer);
            shimmerFrameLayout.startShimmer();

        }

        public void setLike(final String key) {
            auth = FirebaseAuth.getInstance();
            dataUsers = FirebaseDatabase.getInstance().getReference("Users").child(auth.getCurrentUser().getUid()).child("like");
            dataUsers.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(key).hasChild("name")) {
                        imvFavorite.setImageResource(R.drawable.ic_favorite_white_24dp);
                    } else {
                        imvFavorite.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

    }

}
