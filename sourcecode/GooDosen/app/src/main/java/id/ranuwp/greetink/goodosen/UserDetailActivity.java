package id.ranuwp.greetink.goodosen;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.dd.processbutton.iml.ActionProcessButton;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;

import id.ranuwp.greetink.goodosen.model.Constant;
import id.ranuwp.greetink.goodosen.model.User;
import id.ranuwp.greetink.goodosen.model.helper.FirebaseUserHelper;

public class UserDetailActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener {

    //view
    private CircularImageView profile_image;
    private TextView name_textview;
    private TextView from_textview;
    private ActionProcessButton follow_button;

    //var
    private GoogleMap googleMap;
    private User user;
    private String id;
    private FirebaseUserHelper firebaseUserHelper;
    private SupportMapFragment supportMapFragment;
    private MarkerOptions markerOptions;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseUserHelper = new FirebaseUserHelper();
        supportMapFragment = SupportMapFragment.newInstance();
        id = getIntent().getExtras().getString("id");
        setContentView(R.layout.activity_user_detail);
        setupView();
    }

    private void setupView() {
        profile_image = (CircularImageView) findViewById(R.id.profile_image);
        name_textview = (TextView) findViewById(R.id.name_textview);
        from_textview = (TextView) findViewById(R.id.from_textview);
        follow_button = (ActionProcessButton) findViewById(R.id.follow_button);
        follow_button.setOnClickListener(this);
        supportMapFragment = SupportMapFragment.newInstance();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.container,supportMapFragment);
        userSetup();
    }

    private void userSetup() {
        user = new User();
        firebaseUserHelper.getDatabaseReference().child("users/"+id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user.setId(id);
                user.setName(dataSnapshot.child("name").getValue().toString());
                user.setImage_url(dataSnapshot.child("image_url").getValue().toString());
                user.setFrom(dataSnapshot.child("from").getValue().toString());
                user.setFollow(dataSnapshot.child("followers/"+ Constant.getSharedPreference(getApplicationContext()).getString("id","")).exists());
                if(user.isFollow()){
                    follow_button.setText("Unfollow");
                }else{
                    follow_button.setText("Follow");
                }
                double latitude = Double.parseDouble(dataSnapshot.child("lat").getValue().toString());
                double longitude = Double.parseDouble(dataSnapshot.child("long").getValue().toString());
                user.setLast_location(new LatLng(latitude,longitude));
                supportMapFragment.getMapAsync(UserDetailActivity.this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void  toUserDetailActivity(Context context, User user){
        Intent intent = new Intent(context,UserDetailActivity.class);
        intent.putExtra("id",user.getId());
        context.startActivity(intent);
    }

    @Override
    public void onMapReady(final GoogleMap gmap) {
        this.googleMap = gmap;
        markerOptions = new MarkerOptions();
        markerOptions.position(user.getLast_location());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(user.getLast_location(),13.0f);
        googleMap.moveCamera(cameraUpdate);
        firebaseUserHelper.getDatabaseReference().child("users/"+id+"last_location").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                double latitude = Double.parseDouble(dataSnapshot.child("lat").getValue().toString());
                double longitude = Double.parseDouble(dataSnapshot.child("long").getValue().toString());
                user.setLast_location(new LatLng(latitude,longitude));
                markerOptions.position(user.getLast_location());
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker));
                markerOptions.title(user.getName());
                googleMap.clear();
                googleMap.addMarker(markerOptions);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.follow_button :
                ActionProcessButton actionProcessButton = (ActionProcessButton) view;
                String owner = Constant.getSharedPreference(this).getString("id","");
                if(user.isFollow()){
                    firebaseUserHelper.getDatabaseReference().child("users/"+id+"followers"+owner).removeValue();
                    firebaseUserHelper.getDatabaseReference().child("users/"+owner+"followings"+id).removeValue();
                    user.setFollow(false);
                    actionProcessButton.setText("Follow");
                }else{
                    firebaseUserHelper.getDatabaseReference().child("users/"+id).child("followers").child(owner).setValue(true);
                    firebaseUserHelper.getDatabaseReference().child("users/"+owner).child("following").child(id).setValue(true);
                    user.setFollow(true);
                    actionProcessButton.setText("Unfollow");
                }
                break;
        }
    }
}
