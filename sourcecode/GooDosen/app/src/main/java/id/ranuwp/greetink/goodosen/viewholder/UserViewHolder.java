package id.ranuwp.greetink.goodosen.viewholder;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dd.processbutton.iml.ActionProcessButton;

import id.ranuwp.greetink.goodosen.R;

/**
 * Created by ranuwp on 3/25/2017.
 */

public class UserViewHolder extends RecyclerView.ViewHolder {

    private ImageView image_imageview;
    private TextView name_textview;
    private ActionProcessButton follow_button;
    private CardView parent_card_view;

    public UserViewHolder(View itemView) {
        super(itemView);
        image_imageview = (ImageView) itemView.findViewById(R.id.image_imageview);
        name_textview = (TextView) itemView.findViewById(R.id.name_text);
        follow_button = (ActionProcessButton) itemView.findViewById(R.id.follow_button);
        parent_card_view = (CardView) itemView.findViewById(R.id.parent_card_view);
    }

    public ImageView getImage_imageview() {
        return image_imageview;
    }

    public TextView getName_textview() {
        return name_textview;
    }

    public ActionProcessButton getFollow_button() {
        return follow_button;
    }

    public View getParent_card_view() {
        return parent_card_view;
    }
}
