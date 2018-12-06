package duyliem.photoviewer.Collection;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.ArrayList;

import duyliem.photoviewer.R;

/**
 * Created by Duy Liem on 30/10/2018.
 */

public class ListCollectionAdapter extends BaseAdapter {

    ArrayList<Collection> collections;

    public ListCollectionAdapter(ArrayList<Collection> collections) {
        this.collections = collections;
    }

    @Override
    public int getCount() {
        return collections.size();
    }

    @Override
    public Object getItem(int position) {
        return collections.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_collection, parent, false);
        TextView tvNameAlbum = view.findViewById(R.id.tvNameAlbum);
        ImageView imvAlbum = view.findViewById(R.id.imvAlbum);
        ShimmerFrameLayout shimmerFrameLayout = view.findViewById(R.id.Shimmer);
        shimmerFrameLayout.startShimmer();

        tvNameAlbum.setText(collections.get(position).getNameAlbum());
        Glide.with(imvAlbum.getContext())
                .load(collections.get(position).getImageAlbum())
//                .apply(new RequestOptions().placeholder(R.mipmap.default_image))
                .into(imvAlbum);
        return view;
    }
}
