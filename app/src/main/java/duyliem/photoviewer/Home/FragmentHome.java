package duyliem.photoviewer.Home;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import duyliem.photoviewer.InfoImage.InfoImage;
import duyliem.photoviewer.Object.Photo;
import duyliem.photoviewer.R;

/**
 * Created by Duy Liem on 19/10/2018.
 */

public class FragmentHome extends Fragment {

    public SharedPreferences sp;

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {


            String display = intent.getStringExtra("display");

            if (display.equals("List")) {
                LinearLayoutManager layoutManager1 = new LinearLayoutManager(getActivity());
                layoutManager1.setReverseLayout(true);
                layoutManager1.setStackFromEnd(true);
                recyclerHome.setLayoutManager(layoutManager1);
            } else if (display.equals("Grid")) {
                GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2, LinearLayoutManager.VERTICAL, true);
                gridLayoutManager.setReverseLayout(false);
//                gridLayoutManager.setStackFromEnd(true);
                recyclerHome.setLayoutManager(gridLayoutManager);
            } else if (display.equals("Straggered")) {
                StaggeredGridLayoutManager layoutManager2 = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
                layoutManager2.setReverseLayout(false);
                recyclerHome.setLayoutManager(layoutManager2);
            }
        }
    };

    public View HomeView;

    private RecyclerView recyclerHome;
    private DatabaseReference dataHome;
    private DatabaseReference dataUsers;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private AccessToken token;
    boolean liked = false;

    private static final int LAYOUT_ONE = 1;
    private static final int LAYOUT_TWO = 2;

    public FragmentHome() {

    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        HomeView = inflater.inflate(R.layout.fragment_home, container, false);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        dataHome = FirebaseDatabase.getInstance().getReference().child("Home");
        Query query = dataHome.orderByChild("created");
        dataHome.keepSynced(true);
        recyclerHome = HomeView.findViewById(R.id.RecyclerHome);

        sp = PreferenceManager.getDefaultSharedPreferences(getContext());

//        String display = sp.getString("display", "");
//
//        if (display.equals("List")) {
//            LinearLayoutManager layoutManager1 = new LinearLayoutManager(getActivity());
//            layoutManager1.setReverseLayout(true);
//            layoutManager1.setStackFromEnd(true);
//            recyclerHome.setLayoutManager(layoutManager1);
//        } else if (display.equals("Grid")) {
//            GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2, LinearLayoutManager.VERTICAL, true);
////                gridLayoutManager.setReverseLayout(true);
//            recyclerHome.setLayoutManager(gridLayoutManager);
//        } else if (display.equals("Straggered")) {
//            StaggeredGridLayoutManager layoutManager2 = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
////                layoutManager2.setReverseLayout(true);
//            recyclerHome.setLayoutManager(layoutManager2);
//        }

        IntentFilter intentFilter = new IntentFilter("detect_display");
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastReceiver, intentFilter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerHome.setLayoutManager(layoutManager);

        FirebaseRecyclerOptions<Photo> options = new FirebaseRecyclerOptions.Builder<Photo>().setQuery(query, Photo.class).build();

        FirebaseRecyclerAdapter<Photo, PhotoViewHoler> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Photo, PhotoViewHoler>(options) {

//            @Override
//            public int getItemViewType(int position) {
//                Photo photo = getItem(position);
//                if (photo.getType() == "Straggered") {
//                    return LAYOUT_ONE;
//                } else {
//                    return LAYOUT_TWO;
//                }
//            }

            @NonNull
            @Override
            public PhotoViewHoler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view;
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home, parent, false);
                return new PhotoViewHoler(view);

//                switch (viewType) {
//                    case 1:
//                        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home, parent, false);
//                        return new PhotoViewHoler(view);
//                    case 2:
//                        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_display_collection, parent, false);
//                        return new PhotoViewHoler(view);
//                }
//                return null;

            }

            @SuppressLint("ResourceType")
            @Override
            protected void onBindViewHolder(@NonNull final PhotoViewHoler holder, int position, @NonNull final Photo photo) {
                final String key = getRef(position).getKey();
                token = AccessToken.getCurrentAccessToken();

                holder.setLike(key);

                if (user == null) {
                    holder.imvFavorite.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                }

                holder.tvName.setText(photo.getName());

                Glide.with(getContext())
                        .load(photo.getLinks())
                        .into(holder.imvImage);

                DatabaseReference dataCountLike = FirebaseDatabase.getInstance().getReference("Home").child(key).child("like");
                dataCountLike.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
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
                        Intent intent = new Intent(getContext(), InfoImage.class);
                        intent.putExtra("name", photo.getName());
                        intent.putExtra("key", key);
                        intent.putExtra("url", photo.getLinks());
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        getContext().startActivity(intent);
                    }
                });
                holder.rlDown.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DownImage(photo.getName());
                    }

                    private void DownImage(String nameImage) {
                        File direct = new File(Environment.getExternalStorageDirectory() + "/PhotoViewer/" + nameImage + ".jpg");

                        if (direct.exists()) {
                            AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                                    .setTitle("Download Image...")
                                    .setMessage("You have already downloaded this photo. Do you want to download again?")
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            DownloadManager mgr = (DownloadManager) getContext().getSystemService(Context.DOWNLOAD_SERVICE);

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
                                            Toast.makeText(getContext(), "Download Image " + photo.getName(), Toast.LENGTH_LONG).show();
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

                            DownloadManager mgr = (DownloadManager) getContext().getSystemService(Context.DOWNLOAD_SERVICE);

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
                            Toast.makeText(getContext(), "Download Image " + photo.getName(), Toast.LENGTH_LONG).show();
                        }
                    }
                });

                holder.rlFavorite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        liked = true;
                        user = auth.getCurrentUser();
                        token = AccessToken.getCurrentAccessToken();
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
                                            dataUsers.child(key).child("name").setValue(photo.getName());
                                            dataUsers.child(key).child("links").setValue(photo.getLinks());
                                            dataUsers.child(key).child("created").setValue(photo.getCreated());
                                            dataUsers.child(key).child("tag").setValue(photo.getTag());
                                            dataUsers.child(key).child("type").setValue(photo.getType());
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
                            holder.imvFavorite.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                            Toast.makeText(getContext(), "You need to login!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                dataHome.child(key).child("like").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String count = String.valueOf(dataSnapshot.getChildrenCount());
                        dataHome.child(key).child("like").child("count").setValue(count);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        };

        recyclerHome.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();

        return HomeView;
    }

    public static class PhotoViewHoler extends RecyclerView.ViewHolder {

        View view;
        DatabaseReference dataUsers;
        FirebaseAuth auth;
        FirebaseUser user;
        ImageView imvFavorite;
        ImageView imvDown;
        TextView tvName;
        ImageView imvImage;
        TextView tvCountLike, tvCountDown;
        RelativeLayout rlDown, rlFavorite;


        public PhotoViewHoler(View itemView) {
            super(itemView);
            view = itemView;

            tvName = view.findViewById(R.id.tvName);
            imvImage = view.findViewById(R.id.imvImage);
            imvDown = view.findViewById(R.id.imvDown);
            imvFavorite = view.findViewById(R.id.imvUnFavorite);
            tvCountLike = view.findViewById(R.id.tvCountLike);
            tvCountDown = view.findViewById(R.id.tvCountDown);
            rlDown = view.findViewById(R.id.rlDown);
            rlFavorite = view.findViewById(R.id.rlFavorite);

            ShimmerFrameLayout shimmerFrameLayout = view.findViewById(R.id.Shimmer);
            shimmerFrameLayout.startShimmer();

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
                            imvFavorite.setImageResource(R.drawable.ic_favorite_white_24dp);
                        } else {
                            imvFavorite.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            } else {
                imvFavorite.setImageResource(R.drawable.ic_favorite_border_black_24dp);
            }

        }

    }

}
