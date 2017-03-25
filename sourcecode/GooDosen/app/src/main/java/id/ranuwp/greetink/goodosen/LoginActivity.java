package id.ranuwp.greetink.goodosen;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import id.ranuwp.greetink.goodosen.model.Constant;
import id.ranuwp.greetink.goodosen.model.helper.FirebaseUserHelper;

public class LoginActivity extends AppCompatActivity {

    //View
    private LoginButton facebook_loginbutton;
    private ProgressBar progressbar;

    //Var
    private CallbackManager callbackManager;
    private FirebaseUserHelper firebaseUserHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        firebaseUserHelper = new FirebaseUserHelper();
        setContentView(R.layout.activity_login);
        viewSetup();
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public static void toLoginActivity(Activity activity){
        activity.startActivity(new Intent(activity.getApplicationContext(),LoginActivity.class));
    }

    private void viewSetup() {
        //Progress Bar
        progressbar = (ProgressBar) findViewById(R.id.progressbar);
        //Facebook Login Button
        facebook_loginbutton = (LoginButton) findViewById(R.id.facebook_loginbutton);
        callbackManager = CallbackManager.Factory.create();
        facebook_loginbutton.setReadPermissions("email","public_profile");
        facebook_loginbutton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                facebook_loginbutton.setVisibility(View.GONE);
                progressbar.setVisibility(View.VISIBLE);
                final AuthCredential credential = FacebookAuthProvider.getCredential(loginResult.getAccessToken().getToken());
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                    final JSONObject objects = object;
                                    firebaseUserHelper.getFirebaseAuth().signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if(task.isSuccessful()){
                                                try {
                                                    String id = objects.getString("id");
                                                    String name = objects.getString("name");
                                                    String image_url = "https://graph.facebook.com/"+id+"/picture?type=large";
                                                    Map<String,String> user = new HashMap<>();
                                                    user.put("name",name);
                                                    user.put("image_url",image_url);
                                                    user.put("from","Universitas Diponegoro");
                                                    firebaseUserHelper.getDatabaseReference().child("users").child(id).setValue(user);
                                                    firebaseUserHelper.getDatabaseReference().child("makan").setValue("Ayok");
                                                    Constant.getSharedPreference(getApplicationContext())
                                                            .edit()
                                                            .putString("id",id)
                                                            .apply();
                                                    MainActivity.toMainActivity(LoginActivity.this);
                                                    finish();
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }else{
                                                Log.d("RWP","RWP");
                                            }
                                        }
                                    });
                            }
                        });
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                Toast.makeText(getApplicationContext(),"Canceled",Toast.LENGTH_SHORT).show();
                progressbar.setVisibility(View.GONE);
                facebook_loginbutton.setVisibility(View.VISIBLE);

            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_SHORT).show();
                progressbar.setVisibility(View.GONE);
                facebook_loginbutton.setVisibility(View.VISIBLE);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode,resultCode,data);
    }
}
