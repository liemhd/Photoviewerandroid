package duyliem.photoviewer.NavigationView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import duyliem.photoviewer.R;


public class Login extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth auth;
    private CallbackManager callbackManager;
    private static final String TAG = "Login";
    private ImageView imvBack;
    private FirebaseUser user;
    private ImageButton ibtnLogoutFb;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        setContentView(R.layout.activity_login_fb);
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        imvBack = findViewById(R.id.imvBack);
        LoginButton loginButton = findViewById(R.id.login_button);
        ibtnLogoutFb = findViewById(R.id.ibtnLogoutFb);

        progressDialog= new ProgressDialog(Login.this);
        progressDialog.setMessage("Login...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(true);
        progressDialog.setCanceledOnTouchOutside(false);

        ibtnLogoutFb.setOnClickListener(this);

        loginButton.setReadPermissions(Arrays.asList("public_profile", "email"));

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                progressDialog.show();
                Log.d(TAG, "facebook:onSuccess: " + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
//                Toast.makeText(Login.this, "Loggin Success!", Toast.LENGTH_SHORT).show();

//                GraphRequest request = GraphRequest.newMeRequest(
//                        loginResult.getAccessToken(),
//                        new GraphRequest.GraphJSONObjectCallback() {
//                            @Override
//                            public void onCompleted(JSONObject object, GraphResponse response) {
//                                try {
//                                    String birthday = object.getString("birthday"); // 01/31/1980 format
//
//                                    Intent intent = new Intent();
//
//                                    intent.putExtra("birthday", birthday);
//                                    setResult(Activity.RESULT_CANCELED, intent);
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                        });
//                Bundle parameters = new Bundle();
//                parameters.putString("fields", "id,name,email,gender,birthday");
//                request.setParameters(parameters);
//                request.executeAsync();

            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
                Toast.makeText(Login.this, "CANCELED", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
                Toast.makeText(Login.this, "error to Login Facebook" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        imvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }


    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = auth.getCurrentUser();

                            Log.d(TAG, "signInWithCredential:success");
                            boolean newuser = task.getResult().getAdditionalUserInfo().isNewUser();
                            if (newuser) {
                                updateUINewUsers(user);
                            } else {
                                updateUI(user);
                            }
                            finish();

                        } else {
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(Login.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                    }
                });
    }

    public void signOut() {
        auth.signOut();
        LoginManager.getInstance().logOut();
        updateUI(null);
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            String nameUser = user.getDisplayName();
            String idUser = String.valueOf(user.getPhotoUrl());
            String emailUser = user.getEmail();

            Intent intent = new Intent();

            intent.putExtra("emailFb", emailUser);
            intent.putExtra("nameFb", nameUser);
            intent.putExtra("avtFb", idUser);

            setResult(Activity.RESULT_OK, intent);


        } else {
            Intent intent = new Intent();
            intent.putExtra("emailFb", "");
            intent.putExtra("nameFb", "");
            intent.putExtra("avtFb", "https://firebasestorage.googleapis.com/v0/b/photoviewer-515c7.appspot.com/o/Photo%2Favt_users_null%2Favt_users.jpg?alt=media&token=d0a1d5c3-e01a-4161-b648-b05434f2da60");
            setResult(Activity.RESULT_OK, intent);
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.ibtnLogoutFb) {
            signOut();
            Toast.makeText(this, "LogOut Success", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateUINewUsers(FirebaseUser user) {
        if (user != null) {
            String nameUser = user.getDisplayName();
            String idUser = String.valueOf(user.getPhotoUrl());
            String emailUser = user.getEmail();

            Intent intent = new Intent();

            intent.putExtra("emailFb", emailUser);
            intent.putExtra("nameFb", nameUser);
            intent.putExtra("avtFb", idUser);

            setResult(Activity.RESULT_OK, intent);

            String userId = auth.getCurrentUser().getUid();
            DatabaseReference dataUsers = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
            Map map = new HashMap();
            map.put("name", nameUser);
            map.put("avatar", idUser);
            map.put("email", emailUser);
            dataUsers.setValue(map);


        } else {
            Intent intent = new Intent();
            intent.putExtra("emailFb", "");
            intent.putExtra("nameFb", "");
            intent.putExtra("avtFb", "https://firebasestorage.googleapis.com/v0/b/photoviewer-515c7.appspot.com/o/Photo%2Favt_users_null%2Favt_users.jpg?alt=media&token=d0a1d5c3-e01a-4161-b648-b05434f2da60");
            setResult(Activity.RESULT_OK, intent);
        }
    }

}
