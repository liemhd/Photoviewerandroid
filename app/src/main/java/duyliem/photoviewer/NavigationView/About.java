package duyliem.photoviewer.NavigationView;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import de.hdodenhof.circleimageview.CircleImageView;
import duyliem.photoviewer.R;

public class About extends AppCompatActivity {

    ImageView imvCover;
    ImageView imvBack;
    CircleImageView circleImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        imvCover = findViewById(R.id.imvCover);
        imvBack = findViewById(R.id.imvBack);
        circleImageView = findViewById(R.id.imvAvt);

        Glide.with(this).load("https://firebasestorage.googleapis.com/v0/b/photoviewer-515c7.appspot.com/o/About%2F18034236_985067908297416_8617550958492127074_n.jpg?alt=media&token=f5132e22-db36-43e9-96a4-7a0528690aff")
                .into(imvCover);
        Glide.with(this).load("https://firebasestorage.googleapis.com/v0/b/photoviewer-515c7.appspot.com/o/About%2FSnapseed.jpg?alt=media&token=ebb3abb2-8f15-4add-baf6-81cbe86cc87d")
                .into(circleImageView);

        imvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
