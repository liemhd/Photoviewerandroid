package duyliem.photoviewer.InfoImage;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import duyliem.photoviewer.R;

/**
 * Created by Duy Liem on 01/12/2018.
 */

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.MyViewHolder> {

    private ArrayList<Comment> comments;

    public CommentAdapter(ArrayList<Comment> comments) {
        this.comments = comments;
    }

    @NonNull
    @Override


    public CommentAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Glide.with(holder.imvAvt)
                .load(comments.get(position).getAvt()).apply(new RequestOptions().placeholder(R.mipmap.avt_users))
                .into(holder.imvAvt);
        holder.tvCmt.setText(comments.get(position).getCmt());
        holder.tvNameUsers.setText(comments.get(position).getName());

    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        CircleImageView imvAvt;
        TextView tvCmt;
        TextView tvNameUsers;

        public MyViewHolder(View v) {
            super(v);

            imvAvt = v.findViewById(R.id.imvUsers);
            tvCmt = v.findViewById(R.id.tvCmt);
            tvNameUsers = v.findViewById(R.id.tvNameUsers);

        }
    }
}
