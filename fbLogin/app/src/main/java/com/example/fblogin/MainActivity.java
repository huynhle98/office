package com.example.fblogin;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.login.widget.ProfilePictureView;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {


    CallbackManager callbackManager;

    ProfilePictureView profilePictureView;
    LoginButton loginButton;
    Button logOut,fbView;
    TextView txtName;
    boolean map;
    String name;
    CircleImageView circleImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
        // lấy keyHash
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.example.loginfb",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }*/

        mapping();
        map=false;
        onoffmapping();
        callbackManager = CallbackManager.Factory.create();

        loginButton.setReadPermissions("public_profile");

        setLoginFB();
        setLogOutFB();
    }

    private void setLogOutFB() {
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                map = false;
                onoffmapping();
                LoginManager.getInstance().logOut();

                /*
                if (AccessToken.getCurrentAccessToken() == null) {
                    return; // already logged out
                }

                else {
                    new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/", null, HttpMethod.DELETE, new GraphRequest
                            .Callback() {
                        @Override
                        public void onCompleted(GraphResponse graphResponse) {

                            map = false;
                            onoffmapping();
                            LoginManager.getInstance().logOut();

                        }
                    }).executeAsync();
                }*/
            }
        });
    }

    private void setLoginFB() {
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                map=true;
                onoffmapping();
                result();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
    }

    private void result()
    {
        GraphRequest graphRequest=GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                Log.d("JSON",response.getJSONObject().toString());

                try {
                    name=object.getString("name");
                    profilePictureView.setProfileId(Profile.getCurrentProfile().getId());
                    circleImageView.setImageDrawable(profilePictureView.getBackground());
                    txtName.setText(name);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "name");
        graphRequest.setParameters(parameters);
        graphRequest.executeAsync();
    }

    public void onoffmapping()
    {
        if(map==false)
        {
            fbView.setVisibility(View.VISIBLE);
            logOut.setVisibility(View.INVISIBLE);
            txtName.setVisibility(View.VISIBLE);
            profilePictureView.setVisibility(View.INVISIBLE);
            txtName.setText("");
            profilePictureView.setProfileId(null);
        }
        else
        {
            fbView.setVisibility(View.INVISIBLE);
            logOut.setVisibility(View.VISIBLE);
            txtName.setVisibility(View.VISIBLE);
            profilePictureView.setVisibility(View.VISIBLE);
        }
    }

    public void mapping()
    {
        fbView=findViewById(R.id.facebookView);
        profilePictureView=findViewById(R.id.ProfilePicture);
        loginButton=findViewById(R.id.login_button);
        logOut=findViewById(R.id.logout_button);
        txtName=findViewById(R.id.txtName);
        circleImageView=findViewById(R.id.avatar);

        fbView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.facebookView){
                    loginButton.performClick();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode,resultCode,data);
    }

    @Override
    protected void onStart() {
        LoginManager.getInstance().logOut();//log out mỗi lần start
        super.onStart();
    }
}
