package id.ranuwp.greetink.goodosen.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dd.processbutton.iml.ActionProcessButton;
import com.squareup.picasso.Picasso;

import java.util.List;

import id.ranuwp.greetink.goodosen.R;
import id.ranuwp.greetink.goodosen.UserDetailActivity;
import id.ranuwp.greetink.goodosen.model.Constant;
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
                ActionProcessButton follow_button = (ActionProcessButton) view;
                User user = (User) follow_button.getTag();
                String user_id = user.getId();
                String owner = Constant.getSharedPreference(context).getString("id","");
                if(user.isFollow()){
                    firebaseUserHelper.getDatabaseReference().child("users/"+user_id).child("followers/"+owner).removeValue();
                    firebaseUserHelper.getDatabaseReference().child("users/"+owner).child("followings/"+user_id).removeValue();
                    user.setFollow(false);
                    follow_button.setText("Follow");
                }else{
                    firebaseUserHelper.getDatabaseReference().child("users/"+user_id).child("followers").child(owner).setValue(true);
                    firebaseUserHelper.getDatabaseReference().child("users/"+owner).child("followings").child(user_id).setValue(true);
                    user.setFollow(true);
                    follow_button.setText("Unfollow");
                }
                break;
            case R.id.parent_card_view:
                CardView cardView = (CardView) view;
                user = (User) cardView.getTag();
                UserDetailActivity.toUserDetailActivity(context,user);
                break;
        }
    }
}
