package id.ranuwp.greetink.goodosen.adapter;

import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import com.squareup.picasso.Picasso;

import java.util.List;

import id.ranuwp.greetink.goodosen.R;
import id.ranuwp.greetink.goodosen.model.Chat;
import id.ranuwp.greetink.goodosen.model.helper.DateHelper;
import id.ranuwp.greetink.goodosen.viewholder.ChatViewHolder;

/**
 * Created by ranuwp on 3/25/2017.
 */

public class ChatAdapter extends RecyclerView.Adapter<ChatViewHolder> implements View.OnClickListener{

    private Context context;
    private List<Chat> listChat;

    public ChatAdapter(Context context, List<Chat> listChat) {
        this.context = context;
        this.listChat = listChat;
    }

    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat,parent,false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ChatViewHolder holder, int position) {
        Chat chat = listChat.get(position);
        Picasso.with(context).load(chat.getUser().getImage_url()).placeholder(R.drawable.loading_placeholder).error(R.drawable.error_placeholder).into(holder.getImage_imageview());
        holder.getParent_card_view().setOnClickListener(this);
        holder.getParent_card_view().setTag(chat);
        if(chat.getUser().getName().equals("") || chat.getUser().getName() == null){
            holder.getName_textview().setText("Anonymouse");
        }else{
            holder.getName_textview().setText(chat.getUser().getName());
        }
        holder.getDate_time_text().setText(DateHelper.DATETIME_FORMAT.format(chat.getDatetime()));
        holder.getPopup_imageview().setTag(chat);
        holder.getPopup_imageview().setOnClickListener(this);
        if (chat.isNotification()){
            holder.getNotification_imageview().setImageResource(R.drawable.ic_insert_drive_file);
        }else {
            holder.getNotification_imageview().setImageResource(R.drawable.ic_done);
        }
    }

    @Override
    public int getItemCount() {
        return listChat.size();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.popup_imageview:
                showPopup(view);
                break;
            case R.id.parent_card_view:
                //PDFActivity.toPDFActivity(context);
                //DetailedDataResultsController.toDetailedDataActivity(context,chat);
                break;
        }
    }

    private void showPopup(final View view){
        PopupMenu popupMenu = new PopupMenu(context,view);
        MenuInflater menuInflater = popupMenu.getMenuInflater();
        menuInflater.inflate(R.menu.popup_chat, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.delete_chat :
                        view.getTag();
                        //notifyItemRemoved();
                        break;
                }
                return true;
            }
        });
        popupMenu.show();
    }
}
