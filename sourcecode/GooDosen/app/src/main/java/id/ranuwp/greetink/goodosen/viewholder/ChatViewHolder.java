package id.ranuwp.greetink.goodosen.viewholder;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import id.ranuwp.greetink.goodosen.R;

/**
 * Created by ranuwp on 3/25/2017.
 */

public class ChatViewHolder extends RecyclerView.ViewHolder {

    private ImageView image_imageview;
    private TextView name_textview;
    private TextView date_time_text;
    private ImageView popup_imageview;
    private ImageView notification_imageview;
    private CardView parent_card_view;

    public ChatViewHolder(View itemView) {
        super(itemView);
        image_imageview = (ImageView) itemView.findViewById(R.id.image_imageview);
        name_textview = (TextView) itemView.findViewById(R.id.name_text);
        date_time_text = (TextView) itemView.findViewById(R.id.date_time_text);
        popup_imageview = (ImageView) itemView.findViewById(R.id.popup_imageview);
        notification_imageview = (ImageView) itemView.findViewById(R.id.notification_imageview);
        parent_card_view = (CardView) itemView.findViewById(R.id.parent_card_view);
    }

    public ImageView getImage_imageview() {
        return image_imageview;
    }

    public TextView getName_textview() {
        return name_textview;
    }

    public TextView getDate_time_text() {
        return date_time_text;
    }

    public ImageView getPopup_imageview() {
        return popup_imageview;
    }

    public ImageView getNotification_imageview() {
        return notification_imageview;
    }

    public View getParent_card_view() {
        return parent_card_view;
    }
}
