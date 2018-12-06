package duyliem.photoviewer;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import de.hdodenhof.circleimageview.CircleImageView;
import duyliem.photoviewer.Collection.FragmentCollection;
import duyliem.photoviewer.NavigationView.Favorite;
import duyliem.photoviewer.NavigationView.Settings;
import duyliem.photoviewer.Popular.FragmentPopular;
import duyliem.photoviewer.Home.FragmentHome;
import duyliem.photoviewer.NavigationView.About;
import duyliem.photoviewer.NavigationView.Login;
import duyliem.photoviewer.Search.SearchImage;

public class MainActivity extends AppCompatActivity {

    private ImageView imvMenu;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private TabLayout tabLayout;
    private ViewPager pager;
    private ImageView imvSearch;
    private TextView tvNameFb;
    private TextView tvEmailFb;
    private CircleImageView imvAvtFb;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private Button btnLogin;
    private FragmentManager manager;
    private PagerAdapter adapter;

    private int[] tabIcons = {
            R.drawable.ic_home_black_24dp,
            R.drawable.ic_photo_popular_black_24dp,
            R.drawable.ic_photo_library_black_24dp
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getKeyHash();
        Mapped();
        addControl();
        setupTabIcons();
        NavigationView_Click();

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        updateUI(user);

        imvMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        imvSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), SearchImage.class);
                startActivity(intent);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), Login.class);
                startActivityForResult(intent, 0);
            }
        });

        imvAvtFb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth = FirebaseAuth.getInstance();
                user = auth.getCurrentUser();
                if (user != null) {
                    Intent intent = new Intent(getBaseContext(), ProfileUsers.class);
                    startActivityForResult(intent, 2);
                    drawerLayout.closeDrawer(GravityCompat.START);

                } else {

                }

            }
        });

        if (user != null) {
            String nameUser = user.getDisplayName();
            String idUser = String.valueOf(user.getPhotoUrl());
            String emailUser = user.getEmail();

            tvNameFb.setText(nameUser);
            tvEmailFb.setText(emailUser);
            Glide.with(this).load(idUser).into(imvAvtFb);

            btnLogin.setVisibility(View.INVISIBLE);

        } else {
            tvEmailFb.setText("");
            tvNameFb.setText("");
            Glide.with(this).load("https://firebasestorage.googleapis.com/v0/b/photoviewer-515c7.appspot.com/o/Photo%2Favt_users_null%2Favt_users.jpg?alt=media&token=d0a1d5c3-e01a-4161-b648-b05434f2da60").into(imvAvtFb);
            btnLogin.setVisibility(View.VISIBLE);
        }

    }

    private void NavigationView_Click() {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.menuFavorite:
                        Intent favorite = new Intent(getBaseContext(), Favorite.class);
                        startActivityForResult(favorite, 1);
                        break;
                    case R.id.menuSettting:
                        Intent setting = new Intent(getBaseContext(), Settings.class);
                        startActivity(setting);
                        break;
                    case R.id.menuAbout:
                        Intent about = new Intent(getBaseContext(), About.class);
                        startActivity(about);
                        break;
                    case R.id.menuShare:
                        Toast.makeText(MainActivity.this, "Share", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.menuRate:
                        Toast.makeText(MainActivity.this, "Rate", Toast.LENGTH_SHORT).show();
                        break;
                }
                drawerLayout.closeDrawer(GravityCompat.START);

                return false;
            }
        });
    }

    private void getKeyHash() {
        try {
            PackageInfo info = null;
            try {
                info = getPackageManager().getPackageInfo("duyliem.photoviewer", PackageManager.GET_SIGNATURES);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            for (Signature signature : info.signatures) {
                MessageDigest md = null;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                switch (Log.d
                        ("KeyHash", Base64.encodeToString(md.digest(), Base64.DEFAULT))) {
                }
            }
        } catch (NoSuchAlgorithmException e) {
        }
    }

    class PagerAdapter extends FragmentStatePagerAdapter {


        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment frag = null;
            switch (position) {
                case 0:
                    frag = new FragmentHome();
                    break;
                case 1:
                    frag = new FragmentPopular();
                    break;
                case 2:
                    frag = new FragmentCollection();
                    break;
            }
            return frag;
        }

        @Override
        public int getCount() {
            return 3;
        }
    }

    private void addControl() {
        manager = getSupportFragmentManager();
        adapter = new PagerAdapter(manager);
        pager.setAdapter(adapter);
        tabLayout.setupWithViewPager(pager);
        pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setTabsFromPagerAdapter(adapter);
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(pager));
    }

    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);

        tabLayout.getTabAt(0).getIcon().setColorFilter(Color.parseColor("#725B91"), PorterDuff.Mode.SRC_IN);
        tabLayout.getTabAt(1).getIcon().setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_IN);
        tabLayout.getTabAt(2).getIcon().setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_IN);


        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tab.getIcon().setColorFilter(Color.parseColor("#725B91"), PorterDuff.Mode.SRC_IN);

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.getIcon().setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    public void Mapped() {
        imvSearch = findViewById(R.id.imvSearch);
        drawerLayout = findViewById(R.id.drawer_layout);
        imvMenu = findViewById(R.id.imvMenu);
        navigationView = findViewById(R.id.nav_view);
        pager = findViewById(R.id.view_paper);
        tabLayout = findViewById(R.id.tab_layout);
        tvNameFb = navigationView.getHeaderView(0).findViewById(R.id.tvNameUser);
        imvAvtFb = navigationView.getHeaderView(0).findViewById(R.id.imvAvatarUser);
        tvEmailFb = navigationView.getHeaderView(0).findViewById(R.id.tvMailUser);
        btnLogin = navigationView.getHeaderView(0).findViewById(R.id.btnLogin);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case RESULT_OK:
                if (requestCode == 0) {
                    String name = data.getStringExtra("nameFb");
                    String avt = data.getStringExtra("avtFb");
                    String email = data.getStringExtra("emailFb");

                    tvNameFb.setText(name);
                    tvEmailFb.setText(email);
                    Glide.with(this).load(avt).into(imvAvtFb);

                    btnLogin.setVisibility(View.GONE);
                } else if (requestCode == 2) {
                    String name = data.getStringExtra("nameFb");
                    String avt = data.getStringExtra("avtFb");
                    String email = data.getStringExtra("emailFb");

                    tvNameFb.setText(name);
                    tvEmailFb.setText(email);
                    Glide.with(this).load(avt).into(imvAvtFb);

                    btnLogin.setVisibility(View.VISIBLE);
                }

                break;
        }
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            String nameUser = user.getDisplayName();
            String idUser = String.valueOf(user.getPhotoUrl());
            String emailUser = user.getEmail();

            tvNameFb.setText(nameUser);
            tvEmailFb.setText(emailUser);
            Glide.with(this).load(idUser).into(imvAvtFb);

            btnLogin.setVisibility(View.INVISIBLE);

        } else {
            tvEmailFb.setText("");
            tvNameFb.setText("");
            Glide.with(this).load("https://firebasestorage.googleapis.com/v0/b/photoviewer-515c7.appspot.com/o/Photo%2Favt_users_null%2Favt_users.jpg?alt=media&token=d0a1d5c3-e01a-4161-b648-b05434f2da60").into(imvAvtFb);
            btnLogin.setVisibility(View.VISIBLE);
        }
    }


}
