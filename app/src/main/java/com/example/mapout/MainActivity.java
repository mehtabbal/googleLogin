package com.example.mapout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,GoogleApiClient.OnConnectionFailedListener {

    private ConstraintLayout profSection;
    private Button signOut;
    private SignInButton signIn;
    private TextView Name,Email;
    private ImageView profilePic;
    private GoogleApiClient googleApiClient;
    private static final int REQ_CODE = 9001;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        profSection = findViewById(R.id.profileSection);
        signOut = findViewById(R.id.btn);
        signIn = (SignInButton) findViewById(R.id.signIn);
        Name = findViewById(R.id.tv1);
        Email = findViewById(R.id.tv2);
        profilePic = findViewById(R.id.imageView);

        signIn.setOnClickListener(this);
        signOut.setOnClickListener(this);

        profSection.setVisibility(View.GONE);
// "google sign in options" class used to configure google sign in api
        GoogleSignInOptions signInOptions = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,signInOptions)
                .build();




    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.signIn:
                setSignIn();
                break;

            case R.id.btn: {
                setSignOut();
                break;
            }

        }

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    private void setSignIn(){

        Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(intent, REQ_CODE);


    }
    private void setSignOut(){
        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                updateUI(false);
            }
        });

    }
    private void handleResult(GoogleSignInResult result){
        if (result.isSuccess()){
            GoogleSignInAccount account = result.getSignInAccount();
            String name = account.getDisplayName();
            String email = account.getEmail();
//            Uri image = account.getPhotoUrl();
            Glide.with(this).load(account.getPhotoUrl())
                    .apply(RequestOptions.circleCropTransform())
                    .into(profilePic);
//            String img_url= account.getPhotoUrl().toString();

            Name.setText(name);
            Email.setText(email);            //            Glide.with(this).load(img_url).into(profilePic);


//            try{
//                String img_url = account.get
//                Glide.with(this).load(img_url).into(profilePic);
////                if (img_url==null) {
////                    profilePic.setImageResource(R.drawable.common_google_signin_btn_icon_dark);
////                }
//            }catch (NullPointerException e){
//                Toast.makeText(getApplicationContext(),"image not found",Toast.LENGTH_LONG).show();
//            }

            updateUI(true);

        }

        else {
            updateUI(false);
        }

    }
    private void updateUI (boolean isLogin){

        if(isLogin){
            profSection.setVisibility(View.VISIBLE);
            signIn.setVisibility(View.GONE);
        }
        else {
            profSection.setVisibility(View.GONE);
            signIn.setVisibility(View.VISIBLE);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==REQ_CODE) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleResult(result);
        }
    }
}
