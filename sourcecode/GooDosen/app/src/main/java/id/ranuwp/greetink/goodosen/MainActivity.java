package id.ranuwp.greetink.goodosen;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.SearchSuggestionsAdapter;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import devlight.io.library.ntb.NavigationTabBar;
import id.ranuwp.greetink.goodosen.adapter.UserAdapter;
import id.ranuwp.greetink.goodosen.model.Constant;
import id.ranuwp.greetink.goodosen.model.User;
import id.ranuwp.greetink.goodosen.model.helper.FirebaseUserHelper;
import id.ranuwp.greetink.goodosen.model.helper.UserSuggestion;

public class MainActivity extends AppCompatActivity {

    //view
    private NavigationTabBar navigarion_bar;
    private ViewPager viewpager;
    private FloatingSearchView floating_search_view;

    //var
    private FirebaseUserHelper firebaseUserHelper;
    private ArrayList<NavigationTabBar.Model> models;
    private ArrayList<User> followersUser;
    private ArrayList<User> followingUser;
    private UserAdapter followerUserAdapter;
    private UserAdapter followingUserAdapter;
    private String id;
    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            firebaseUserHelper.getDatabaseReference().child("users/" + id + "/last_location/lat").setValue(location.getLatitude());
            firebaseUserHelper.getDatabaseReference().child("users/" + id + "/last_location/long").setValue(location.getLongitude());
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseUserHelper = new FirebaseUserHelper();
        if (!firebaseUserHelper.isLogin()) {
            LoginActivity.toLoginActivity(this);
            finish();
            return;
        }
        id = Constant.getSharedPreference(MainActivity.this).getString("id", "");
        firebaseUserHelper.getDatabaseReference().child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(id+"/from").exists()){
                    locationTracker();
                    setContentView(R.layout.activity_main);
                    viewSetup();
                }else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    AlertDialog dialog = builder.create();
                    View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.from_dialog,null,false);
                    final EditText from_edittext = (EditText) view.findViewById(R.id.from_edit_text);
                    dialog.setButton(DialogInterface.BUTTON_POSITIVE,"Save", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String from = from_edittext.getText().toString();
                            firebaseUserHelper.getDatabaseReference().child("users/"+id+"/from").setValue(from);
                            firebaseUserHelper.getDatabaseReference().child("users/"+id+"/last_location/lat").setValue(0.0);
                            firebaseUserHelper.getDatabaseReference().child("users/"+id+"/last_location/long").setValue(0.0);
                            locationTracker();
                            setContentView(R.layout.activity_main);
                            viewSetup();
                        }
                    });
                    dialog.setView(view);
                    dialog.setCancelable(false);
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationTracker();
    }

    private void locationTracker() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 50, locationListener);
    }

    private void viewSetup() {
        navigarion_bar = (NavigationTabBar) findViewById(R.id.navigation_bar);
        viewpager = (ViewPager) findViewById(R.id.viewpager);
        setupNavigationBar();
    }

    private void setupNavigationBar(){
        models = new ArrayList<>();
        int selected_color = getResources().getColor(R.color.selected_color);;
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_face),
                        selected_color)
                        .title("Profile")
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_search),
                        selected_color)
                        .title("Search")
                        .build()
        );
        navigarion_bar.setModels(models);
        viewpager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return 2;
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view.equals(object);
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((View) object);
            }

            @Override
            public Object instantiateItem(ViewGroup container, final int position) {
                View view = new View(getApplicationContext());
                switch (position){
                    case 0 :
                        view = LayoutInflater.from(getBaseContext()).inflate(R.layout.tab_profile, null, false);
                        final CircularImageView profile_image = (CircularImageView) view.findViewById(R.id.profile_image);
                        TextView logout_textview = (TextView) view.findViewById(R.id.logout_textview);
                        final TextView name_textview = (TextView) view.findViewById(R.id.name_textview);
                        final TextView from_textview = (TextView) view.findViewById(R.id.from_textview);
                        firebaseUserHelper.getDatabaseReference().child("users/"+ id).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String name = dataSnapshot.child("name").getValue().toString();
                                if(dataSnapshot.child("from").exists()){
                                    String from = dataSnapshot.child("from").getValue().toString();
                                    from_textview.setText(from);
                                }else{
                                    from_textview.setText("Not Difined");
                                }
                                String image_url = dataSnapshot.child("image_url").getValue().toString();
                                name_textview.setText(name);
                                Picasso.with(getApplicationContext())
                                        .load(image_url)
                                        .placeholder(R.drawable.loading_placeholder)
                                        .error(R.drawable.error_placeholder)
                                        .into(profile_image);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        logout_textview.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                firebaseUserHelper.signOut();
                                LoginActivity.toLoginActivity(MainActivity.this);
                                finish();
                            }
                        });
                        //Followers RecycleView
                        RecyclerView followers_recyclerview = (RecyclerView) view.findViewById(R.id.followers_recyclerview);
                        LinearLayoutManager followersLinearLayoutManager = new LinearLayoutManager(getApplicationContext());
                        followers_recyclerview.setLayoutManager(followersLinearLayoutManager);
                        followersUser = new ArrayList<>();
                        followerUserAdapter = new UserAdapter(MainActivity.this, followersUser);
                        firebaseUserHelper.getDatabaseReference()
                                .child("users/"+id+"/followers")
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        followersUser.clear();
                                        for(final DataSnapshot data : dataSnapshot.getChildren()){
                                            final User user = new User();
                                            user.setId(data.getKey());
                                            firebaseUserHelper.getDatabaseReference().child("users/"+ user.getId())
                                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            user.setName(dataSnapshot.child("name").getValue().toString());
                                                            user.setFrom(dataSnapshot.child("from").getValue().toString());
                                                            user.setImage_url(dataSnapshot.child("image_url").getValue().toString());
                                                            user.setFollow(dataSnapshot.child("followings").child(id).exists());
                                                            followersUser.add(user);
                                                            models.get(position).setBadgeTitle(String.valueOf(followersUser.size()));
                                                            models.get(position).showBadge();
                                                            followerUserAdapter.notifyDataSetChanged();
                                                        }

                                                        @Override
                                                        public void onCancelled(DatabaseError databaseError) {

                                                        }
                                                    });
                                        }
                                        followerUserAdapter.notifyDataSetChanged();
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                        followers_recyclerview.setAdapter(followerUserAdapter);
                        break;
                    case 1 :
                        view = LayoutInflater.from(getBaseContext()).inflate(R.layout.tab_search, null, false);
                        //Following RecyclerView
                        RecyclerView following_recyclerview = (RecyclerView) view.findViewById(R.id.following_recyclerview);
                        LinearLayoutManager followingLinearLayoutManager = new LinearLayoutManager(getApplicationContext());
                        following_recyclerview.setLayoutManager(followingLinearLayoutManager);
                        followingUser = new ArrayList<>();
                        followingUserAdapter = new UserAdapter(MainActivity.this, followingUser);
                        firebaseUserHelper.getDatabaseReference()
                                .child("users/"+id+"/followings")
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        followingUser.clear();
                                        for(final DataSnapshot data : dataSnapshot.getChildren()){
                                            final User user = new User();
                                            user.setId(data.getKey());
                                            firebaseUserHelper.getDatabaseReference().child("users/"+ user.getId())
                                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            user.setName(dataSnapshot.child("name").getValue().toString());
                                                            user.setFrom(dataSnapshot.child("from").getValue().toString());
                                                            user.setFollow(dataSnapshot.child("followers").child(id).exists());
                                                            user.setImage_url(dataSnapshot.child("image_url").getValue().toString());
                                                            followingUser.add(user);
                                                            models.get(position).setBadgeTitle(String.valueOf(followingUser.size()));
                                                            models.get(position).showBadge();
                                                            followingUserAdapter.notifyDataSetChanged();
                                                        }

                                                        @Override
                                                        public void onCancelled(DatabaseError databaseError) {

                                                        }
                                                    });

                                        }
                                        followingUserAdapter.notifyDataSetChanged();
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                        following_recyclerview.setAdapter(followingUserAdapter);
                        //Floating SearchView
                        floating_search_view = (FloatingSearchView) view.findViewById(R.id.floating_search_view);
                        floating_search_view.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
                            @Override
                            public void onSuggestionClicked(SearchSuggestion searchSuggestion) {
                                UserSuggestion userSuggestion = (UserSuggestion) searchSuggestion;
                                Intent intent = new Intent(MainActivity.this, UserDetailActivity.class);
                                intent.putExtra("id",userSuggestion.getUser().getId());
                                floating_search_view.hideProgress();
                                startActivity(intent);
                            }

                            @Override
                            public void onSearchAction(String currentQuery) {
                                floating_search_view.hideProgress();
                            }
                        });
                        floating_search_view.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
                                                                          @Override
                                                                          public void onSearchTextChanged(String oldQuery, final String newQuery) {
                                                                              if (oldQuery.equals("") || newQuery.equals("")) {
                                                                                  floating_search_view.clearSuggestions();
                                                                              } else {
                                                                                  floating_search_view.showProgress();
                                                                                  Query query = firebaseUserHelper.getDatabaseReference().child("users").orderByChild("name");
                                                                                  query.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                      @Override
                                                                                      public void onDataChange(DataSnapshot dataSnapshot) {
                                                                                          ArrayList<SearchSuggestion> searchSuggestions = new ArrayList<>();
                                                                                          for(DataSnapshot user_data : dataSnapshot.getChildren()){
                                                                                              if(!user_data.getKey().equals(id) && user_data.child("name").getValue().toString().toLowerCase().startsWith(newQuery.toLowerCase())){
                                                                                                  User user = new User();
                                                                                                  user.setName(user_data.child("name").getValue().toString());
                                                                                                  user.setImage_url(user_data.child("image_url").getValue().toString());
                                                                                                  user.setId(user_data.getKey());
                                                                                                  user.setFrom(user_data.child("from").getValue().toString());
                                                                                                  user.setFollow(user_data.child("follower").child(id).exists());
                                                                                                  UserSuggestion userSuggestion = new UserSuggestion(user);
                                                                                                  searchSuggestions.add(userSuggestion);
                                                                                              }
                                                                                          }
                                                                                          floating_search_view.hideProgress();
                                                                                          floating_search_view.swapSuggestions(searchSuggestions);
                                                                                      }

                                                                                      @Override
                                                                                      public void onCancelled(DatabaseError databaseError) {
                                                                                          floating_search_view.hideProgress();
                                                                                      }
                                                                                  });
                                                                              }
                                                                          }
                                                                      }
                        );
                        floating_search_view.setOnBindSuggestionCallback(new SearchSuggestionsAdapter.OnBindSuggestionCallback() {
                            @Override
                            public void onBindSuggestion(View suggestionView, ImageView leftIcon, TextView textView, SearchSuggestion item, int itemPosition) {
                                UserSuggestion userSuggestion = (UserSuggestion) item;
                                User user = userSuggestion.getUser();
                                textView.setText(user.getName()+"\n"+user.getFrom());
                                Picasso.with(getApplicationContext())
                                        .load(user.getImage_url())
                                        .placeholder(R.drawable.loading_placeholder)
                                        .error(R.drawable.error_placeholder)
                                        .into(leftIcon);
                            }
                        });
                        break;
                }
                container.addView(view);
                return view;
            }
        });
        navigarion_bar.setViewPager(viewpager,0);
    }

    public static void toMainActivity(Activity activity){
        activity.startActivity(new Intent(activity.getApplicationContext(),MainActivity.class));
    }
}
