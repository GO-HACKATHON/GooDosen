package id.ranuwp.greetink.goodosen;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import devlight.io.library.ntb.NavigationTabBar;
import id.ranuwp.greetink.goodosen.model.Constant;
import id.ranuwp.greetink.goodosen.model.User;
import id.ranuwp.greetink.goodosen.model.Chat;
import id.ranuwp.greetink.goodosen.model.helper.FirebaseUserHelper;

public class MainActivity extends AppCompatActivity {

    //view
    private NavigationTabBar navigarion_bar;
    private ViewPager viewpager;

    //var
    private FirebaseUserHelper firebaseUserHelper;
    private ArrayList<NavigationTabBar.Model> models;
    private ArrayList<User> users;
    private ArrayList<Chat> chats;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseUserHelper = new FirebaseUserHelper();
        firebaseUserHelper.setAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if(firebaseUser == null){
                    firebaseUserHelper.signOut();
                    LoginActivity.toLoginActivity(MainActivity.this);
                    finish();
                }
            }
        });
        id = Constant.getSharedPreference(this).getString("id","");
        setContentView(R.layout.activity_main);
        viewSetup();
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
                        getResources().getDrawable(R.drawable.ic_chat),
                        selected_color)
                        .title("Chat")
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
                return 3;
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
            public Object instantiateItem(ViewGroup container, int position) {
                View view = new View(getApplicationContext());
                switch (position){
                    case 0 :
                        view = LayoutInflater.from(getBaseContext()).inflate(R.layout.tab_profile, null, false);
                        CircularImageView profile_image = (CircularImageView) view.findViewById(R.id.profile_image);
                        Picasso.with(getApplicationContext())
                                .load("https://graph.facebook.com/"+ id +"/picture?type=large")
                                .placeholder(R.drawable.loading_placeholder)
                                .error(R.drawable.error_placeholder)
                                .into(profile_image);
                        TextView logout_textview = (TextView) view.findViewById(R.id.logout_textview);
                        final TextView name_textview = (TextView) view.findViewById(R.id.name_textview);
                        final TextView from_textview = (TextView) view.findViewById(R.id.from_textview);
                        firebaseUserHelper.getDatabaseReference().child("user/"+ id).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String name = dataSnapshot.child("name").getValue().toString();
                                String from = dataSnapshot.child("from").getValue().toString();
                                name_textview.setText(name);
                                from_textview.setText(from);
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
                        break;
                    case 1 :
                        view = LayoutInflater.from(getBaseContext()).inflate(R.layout.tab_chat, null, false);
                        break;
                    case 2 :
                        view = LayoutInflater.from(getBaseContext()).inflate(R.layout.tab_search, null, false);
                        break;
                }
                container.addView(view);
                return view;
            }
        });
        navigarion_bar.setViewPager(viewpager);
    }

    public static void toMainActivity(Activity activity){
        activity.startActivity(new Intent(activity.getApplicationContext(),MainActivity.class));
    }
}
