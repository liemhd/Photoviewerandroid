package duyliem.photoviewer;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileUsers extends AppCompatActivity {

    private ImageView imvBack;
    private TextView tvName, tvEmail;
    private Button btnLogout;
    private CircleImageView imvAvt;
    FirebaseAuth auth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_users);

        imvBack = findViewById(R.id.imvBack);
        tvName = findViewById(R.id.tvNameUsers);
        tvEmail = findViewById(R.id.tvEmail);
        btnLogout = findViewById(R.id.btnLogout);
        imvAvt = findViewById(R.id.imvAvtUsers);

        final ProgressDialog progressDialog = new ProgressDialog(ProfileUsers.this);
        progressDialog.setMessage("Log Out...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(true);
        progressDialog.setCanceledOnTouchOutside(false);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        if (user != null) {
            String nameUser = user.getDisplayName();
            String idUser = String.valueOf(user.getPhotoUrl());
            String emailUser = user.getEmail();

            tvName.setText(nameUser);
            tvEmail.setText(emailUser);
            Glide.with(this).load(idUser).into(imvAvt);

        }

        imvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth = FirebaseAuth.getInstance();
                user = auth.getCurrentUser();
                AlertDialog alertDialog = new AlertDialog.Builder(ProfileUsers.this)
                        .setTitle("Log Out...")
                        .setMessage("Do you want to log out?")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                progressDialog.show();
                                auth.signOut();
                                LoginManager.getInstance().logOut();
                                Intent intent = new Intent();
                                intent.putExtra("emailFb", "");
                                intent.putExtra("nameFb", "");
                                intent.putExtra("avtFb", "https://firebasestorage.googleapis.com/v0/b/photoviewer-515c7.appspot.com/o/Photo%2Favt_users_null%2Favt_users.jpg?alt=media&token=d0a1d5c3-e01a-4161-b648-b05434f2da60");
                                setResult(RESULT_OK, intent);
                                onBackPressed();

                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .create();
                alertDialog.show();

            }
        });

    }
}
