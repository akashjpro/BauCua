package com.example.tmha.baucua.view.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tmha.baucua.R;
import com.example.tmha.baucua.model.User;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity  implements View.OnClickListener,
        GoogleApiClient.OnConnectionFailedListener{

    private Button mLoginButton, mGoogleButton, mFacebookButton;
    private LoginButton mFbButton;
    private EditText mEmailEditext, mPasswordEdittext;
    private TextView mRegisterText, mResetPasswordText;
    private FirebaseAuth mAuth;
    private TextInputLayout mContainEamil, mContainPassword;
    private final int RC_SIGN_IN = 123;
    private GoogleApiClient mGoogleApiClient;
    private CallbackManager mCallbackManager;
    private DatabaseReference mDatabase;
    public static ShareDialog shareDialog;
    private boolean isLoginGoogle = false;

    public ProgressDialog mDialog;


    private final  String TAG = this.getClass().getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);
        initView();

        mGoogleButton.setOnClickListener(this);
        mFacebookButton.setOnClickListener(this);
        mLoginButton.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();

        mCallbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);

        mFbButton.setReadPermissions("email", "public_profile", "user_birthday", "user_location", "user_hometown", "user_friends");
        mFbButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                Log.d(TAG, "Object: "+ object.toString());
//                                Toast.makeText(LoginActivity.this, "object: "+ object.toString(), Toast.LENGTH_SHORT).show();

                                /**
                                 *  get information of user from facebook
                                 */

                                String objectProfile = object.toString();
                                try {
                                    String gender = object.getString("gender");
                                    String birthday = object.getString("birthday");
                                    String location = object.getJSONObject("location").getString("name");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                // Cac truong truy xuat lay thong tin from facebook
                parameters.putString("fields", "id,name,email,gender,birthday,location");
                //get day du hon
                // parameters.putString("fields", "id,name,email,gender,birthday,hometown,locale,location");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });

    }

    private void handleFacebookAccessToken(AccessToken token) {

        // Toast.makeText(this, "Token: "+ token, Toast.LENGTH_SHORT).show();
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        // Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                        final FirebaseUser user = mAuth.getCurrentUser();
                        String id   = user.getUid();
                        String name = user.getDisplayName();
                        String url  = user.getPhotoUrl().toString();
                        Toast.makeText(LoginActivity.this,
                                "Đăng nhập thành công",
                                Toast.LENGTH_SHORT).show();




                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

    private void initView() {
        mDatabase           = FirebaseDatabase.getInstance().getReference();

        mFbButton           = (LoginButton) findViewById(R.id.faceLogin_button);
        mLoginButton        = (Button) findViewById(R.id.buttonLogin);
        mGoogleButton       = (Button) findViewById(R.id.buttonGoogle);
        mFacebookButton     = (Button) findViewById(R.id.buttonFacebook);

        mEmailEditext       = (EditText) findViewById(R.id.editTextEmail);
        mPasswordEdittext   = (EditText) findViewById(R.id.editTextPassword);

        mRegisterText       = (TextView) findViewById(R.id.textViewRegister);
        mResetPasswordText  = (TextView) findViewById(R.id.textViewResetPassword);

        mContainEamil       = (TextInputLayout) findViewById(R.id.containEmail);
        mContainPassword    = (TextInputLayout) findViewById(R.id.contaiPassword);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.buttonLogin:
                login();
                break;
            case  R.id.buttonGoogle:
                loginWithGoogle();
                break;
            case R.id.buttonFacebook:
                mFbButton.performClick();
                break;
        }
    }

    private void login(){
        String email    = mEmailEditext.getText().toString().trim();
        String password = mPasswordEdittext.getText().toString().trim();
        if(email.equals("")){
            mContainEamil.setErrorEnabled(true);
            mContainEamil.setError("Email không được để trống");
            return;
        }
        if(password.equals("")){
            mContainPassword.setErrorEnabled(true);
            mContainPassword.setError("Mật khẩu không được để trống");
            return;
        }
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(( LoginActivity.this), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(LoginActivity.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();

                        }else {
                            if(task.getException().toString().contains("formatted")){
                                mContainEamil.setError("Email không đúng định dạng, xin nhập lại!!!");
                            }
                            if(task.getException().toString().contains("no user")){
                                mContainEamil.setError("Email chưa đăng ký, xin nhập đúng emai đã đăng ký hoặc tạo tài khoản mới!!!");
                            }
                            if(task.getException().toString().contains("password is invalid")){
                                mContainPassword.setError("Mật khẩu không chính xác, xin nhập lại!!!");
                            }

                        }
                    }
                });
    }

    private void loginWithGoogle() {
        mDialog = new ProgressDialog(this);
        mDialog.setMessage("Uploading...");
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();

        isLoginGoogle = true;
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(LoginActivity.this)
//                        .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }



    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("TAG", "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("TAG", "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w("TAG", "signInWithCredential", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }

                        mDialog.dismiss();
                        FirebaseUser user = mAuth.getCurrentUser();

                        String id   = user.getUid();
                        String name = user.getDisplayName();
                        String urlPhoto  = user.getPhotoUrl().toString();

                        User us = new User(id, name, urlPhoto, "xxxx", "xxxx" );
                        mDatabase.child("User").child(id).setValue(us);
                        Toast.makeText(LoginActivity.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                        // ...
                    }
                });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(!isLoginGoogle){
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }else {
            // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
            if (requestCode == RC_SIGN_IN) {
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                if (result.isSuccess()) {
                    // Google Sign In was successful, authenticate with Firebase
                    GoogleSignInAccount account = result.getSignInAccount();
                    firebaseAuthWithGoogle(account);
                } else {
                    // Google Sign In failed, update UI appropriately
                    // ...
                    Toast.makeText(this, "Fail authenticate with Google", Toast.LENGTH_SHORT).show();
                }
            }
        }

    }

}
