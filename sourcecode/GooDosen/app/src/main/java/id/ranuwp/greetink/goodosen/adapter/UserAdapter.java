package id.ranuwp.greetink.goodosen.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dd.processbutton.iml.ActionProcessButton;
import com.squareup.picasso.Picasso;

import java.util.List;

import id.ranuwp.greetink.goodosen.R;
import id.ranuwp.greetink.goodosen.model.User;
import id.ranuwp.greetink.goodosen.model.helper.FirebaseUserHelper;
import id.ranuwp.greetink.goodosen.viewholder.UserViewHolder;

/**
 * Created by ranuwp on 3/25/2017.
 */

public class UserAdapter extends RecyclerView.Adapter<UserViewHolder> implements View.OnClickListener{

    private Context context;
    private List<User> listUser;
    private FirebaseUserHelper firebaseUserHelper;

    public UserAdapter(Context context, List<User> listUser) {
        this.context = context;
        this.listUser = listUser;
        this.firebaseUserHelper = new FirebaseUserHelper();
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user,parent,false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(UserViewHolder holder, int position) {
        User user = listUser.get(position);
        Picasso.with(context).load(user.getImage_url()).placeholder(R.drawable.loading_placeholder).error(R.drawable.error_placeholder).into(holder.getImage_imageview());
        holder.getParent_card_view().setOnClickListener(this);
        holder.getParent_card_view().setTag(user);
        if(user.getName().equals("") || user.getName() == null){
            holder.getName_textview().setText("Anonymouse");
        }else{
            holder.getName_textview().setText(user.getName());
        }
        holder.getFollow_button().setTag(user);
        holder.getFollow_button().setOnClickListener(this);
        if (user.isFollow()){
            holder.getFollow_button().setText("Unfollow");
        }else {
            holder.getFollow_button().setText("Follow");
        }
    }

    @Override
    public int getItemCount() {
        return listUser.size();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.follow_button:
                /*
                ActionProcessButton follow_button = (ActionProcessButton) view;
                User user = (User) follow_button.getTag();
                String user_id = user.getUser_id();
                if(user.isFollowed()){
                    firebaseUserHelper.getDatabaseReference().child("user/"+user_id).child("follower/"+ Constant.getSharedPreference(context).getString(Constant.SP_USER_ID,"")).removeValue();
                    firebaseUserHelper.getDatabaseReference().child("user/"+Constant.getSharedPreference(context).getString(Constant.SP_USER_ID,"")).child("following/"+user_id).removeValue();
                    user.setFollowed(false);
                    follow_button.setText("Follow");
                }else{
                    firebaseUserHelper.getDatabaseReference().child("user/"+user_id).child("follower").child(Constant.getSharedPreference(context).getString(Constant.SP_USER_ID,"")).setValue(true);
                    firebaseUserHelper.getDatabaseReference().child("user/"+Constant.getSharedPreference(context).getString(Constant.SP_USER_ID,"")).child("following").child(user_id).setValue(true);
                    user.setFollowed(true);
                    follow_button.setText("Unfollow");
                }
                break;
            case R.id.parent_card_view:
                follow_button = (ActionProcessButton) view;
                user = (User) follow_button.getTag();
                UserDetailActivity.toUserDetailActivity(context,user);
                break;*/
        }
    }
}
