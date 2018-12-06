package duyliem.photoviewer.Popular;


import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.util.ArrayList;

import duyliem.photoviewer.InfoImage.InfoImage;
import duyliem.photoviewer.Object.Photo;
import duyliem.photoviewer.R;

/**
 * Created by Duy Liem on 19/10/2018.
 */

public class FragmentPopular extends Fragment {

    ArrayList<Photo> photos;
    DatabaseReference dataPopular;
    private RecyclerView rvPopular;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_popular, container, false);

        rvPopular = view.findViewById(R.id.rvPopular);
        photos = new ArrayList<>();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);

        rvPopular.setLayoutManager(layoutManager);

        dataPopular = FirebaseDatabase.getInstance().getReference("Home");
        Query query = dataPopular.orderByChild("like/count");

        FirebaseRecyclerOptions<Photo> options = new FirebaseRecyclerOptions.Builder<Photo>().setQuery(query, Photo.class).build();

        FirebaseRecyclerAdapter<Photo, PhotoViewHoler> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Photo, PhotoViewHoler>(options) {
            @NonNull
            @Override
            public PhotoViewHoler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_popular, parent, false);
                return new PhotoViewHoler(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull final PhotoViewHoler holder, int position, @NonNull final Photo photo) {
                final String key = getRef(position).getKey();

                holder.tvName.setText(photo.getName());

                Glide.with(holder.imvImage.getContext())
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
                        intent.putExtra("url", photo.getLinks());
                        intent.putExtra("key", key);
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

            }
        };
        rvPopular.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();

        return view;
    }

    public static class PhotoViewHoler extends RecyclerView.ViewHolder {

        View view;
        ImageView imvDown;
        TextView tvName;
        ImageView imvImage;
        TextView tvCountLike, tvCountDown;
        RelativeLayout rlDown;

        public PhotoViewHoler(View itemView) {
            super(itemView);
            view = itemView;

            tvName = view.findViewById(R.id.tvName);
            imvImage = view.findViewById(R.id.imvImage);
            imvDown = view.findViewById(R.id.imvDown);
            tvCountLike = view.findViewById(R.id.tvCountLike);
            tvCountDown = view.findViewById(R.id.tvCountDown);
            rlDown = view.findViewById(R.id.rlDown);

            ShimmerFrameLayout shimmerFrameLayout = view.findViewById(R.id.Shimmer);
            shimmerFrameLayout.startShimmer();


        }

    }


}
