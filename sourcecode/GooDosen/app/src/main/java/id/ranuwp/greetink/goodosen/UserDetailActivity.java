package id.ranuwp.greetink.goodosen;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dd.processbutton.iml.ActionProcessButton;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;

import id.ranuwp.greetink.goodosen.model.Constant;
import id.ranuwp.greetink.goodosen.model.User;
import id.ranuwp.greetink.goodosen.model.helper.FirebaseUserHelper;

public class UserDetailActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener {

    //view
    private CircularImageView profile_image;
    private TextView name_textview;
    private TextView from_textview;
    private ActionProcessButton follow_button;
    private ImageView nothing_imageview;

    //var
    private GoogleMap googleMap;
    private User user;
    private String id;
    private FirebaseUserHelper firebaseUserHelper;
    private SupportMapFragment supportMapFragment;
    private MarkerOptions markerOptions;
    private LatLng fromPosition;

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
        nothing_imageview = (ImageView) findViewById(R.id.nothing_imageview);
        supportMapFragment = SupportMapFragment.newInstance();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.container,supportMapFragment);
        fragmentTransaction.commit();
        userSetup();
    }

    private void userSetup() {
        user = new User();
        firebaseUserHelper.getDatabaseReference().child("users/"+id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user.setId(id);
                user.setName(dataSnapshot.child("name").getValue().toString());
                name_textview.setText(user.getName());
                user.setImage_url(dataSnapshot.child("image_url").getValue().toString());
                Picasso.with(getApplicationContext())
                        .load(user.getImage_url())
                        .placeholder(R.drawable.loading_placeholder)
                        .error(R.drawable.error_placeholder)
                        .into(profile_image);
                user.setFrom(dataSnapshot.child("from").getValue().toString());
                from_textview.setText(user.getFrom());
                user.setFollow(dataSnapshot.child("followers/"+ Constant.getSharedPreference(getApplicationContext()).getString("id","")).exists());
                if(user.isFollow()){
                    follow_button.setText("Unfollow");
                }else{
                    follow_button.setText("Follow");
                }
                double latitude = Double.parseDouble(dataSnapshot.child("last_location/lat").getValue().toString());
                double longitude = Double.parseDouble(dataSnapshot.child("last_location/long").getValue().toString());
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
    public void onMapReady(GoogleMap gmap) {
        this.googleMap = gmap;
        //Test
        if(Geocoder.isPresent()){
            try{
                String location = user.getFrom();
                Geocoder geocoder = new Geocoder(this);
                List<Address> addresses = geocoder.getFromLocationName(location,1);
                double latitude = addresses.get(0).getLatitude();
                double longatude = addresses.get(0).getLongitude();
                fromPosition = new LatLng(latitude,longatude);

            }catch (IOException e){

            }
        }
        //Test
        markerOptions = new MarkerOptions();
        markerOptions.position(user.getLast_location());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(user.getLast_location(),13.0f);
        googleMap.moveCamera(cameraUpdate);
        firebaseUserHelper.getDatabaseReference().child("users/"+id+"/last_location").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                double latitude = Double.parseDouble(dataSnapshot.child("lat").getValue().toString());
                double longitude = Double.parseDouble(dataSnapshot.child("long").getValue().toString());
                user.setLast_location(new LatLng(latitude,longitude));
                markerOptions.position(user.getLast_location());
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker));
                markerOptions.title(user.getName());
                googleMap.clear();
                int strokeColor = 0xffff0000;
                int shadeColor = 0x10ff0000;
                CircleOptions circleOptions = new CircleOptions().center(fromPosition).radius(1000.0).fillColor(shadeColor).strokeColor(strokeColor).strokeWidth(2);
                googleMap.addCircle(circleOptions);
                float[] distance = new float[2];
                Location.distanceBetween(user.getLast_location().latitude,user.getLast_location().longitude,
                        circleOptions.getCenter().latitude,circleOptions.getCenter().longitude,distance);
                if(distance[0] < circleOptions.getRadius()){
                    nothing_imageview.setVisibility(View.GONE);
                    googleMap.addMarker(markerOptions);
                }else{
                    nothing_imageview.setVisibility(View.VISIBLE);
                }
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
                    firebaseUserHelper.getDatabaseReference().child("users/"+id+"/followers/"+owner).removeValue();
                    firebaseUserHelper.getDatabaseReference().child("users/"+owner+"/followings/"+id).removeValue();
                    user.setFollow(false);
                    actionProcessButton.setText("Follow");
                }else{
                    firebaseUserHelper.getDatabaseReference().child("users/"+id).child("followers").child(owner).setValue(true);
                    firebaseUserHelper.getDatabaseReference().child("users/"+owner).child("followings").child(id).setValue(true);
                    user.setFollow(true);
                    actionProcessButton.setText("Unfollow");
                }
                break;
        }
    }
}
