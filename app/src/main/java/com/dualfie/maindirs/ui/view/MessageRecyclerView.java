package com.dualfie.maindirs.ui.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.recyclerview.widget.RecyclerView;

import android.text.format.DateFormat;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;

import com.dualfie.maindirs.dconstants.Constants;
import com.dualfie.maindirs.model.MessageFormat;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.dualfie.maindirs.R;


/**
 * Created by Guest on 12/21/16.
 */
public class MessageRecyclerView extends RecyclerView.Adapter<MessageRecyclerView.MessageHolder> {

    private static final String TAG = "Messgage Recycler View";
    private ArrayList<MessageFormat> mChat = new ArrayList<>();
    private Context mContext;

    public MessageRecyclerView(Context context, ArrayList<MessageFormat> chat) {
        mContext = context;
        mChat = chat;
    }

    @Override
    public MessageRecyclerView.MessageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message, parent, false);
        MessageHolder viewHolder = new MessageHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MessageRecyclerView.MessageHolder holder, int position) {
        holder.bindChat(mChat.get(position));
    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }


    public class MessageHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.message_text) TextView mText;
        @BindView(R.id.message_user) TextView mUser;
        @BindView(R.id.message_time) TextView mTime;
        @BindView(R.id.image) ImageView mImage;
        private Context mContext;

        private SharedPreferences mSharedPreferences;

        public MessageHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mContext = itemView.getContext();
            this.setIsRecyclable(false);
        }
        public void bindChat(MessageFormat mm) {
            try {
                this.mImage.setImageBitmap(null);
                String message;
                String key;
                message = mm.getMessageText();
                // Set their text
                mText.setText(message);
                if (mm.getImageBitmap() != null ) {
                    mImage.setVisibility(View.VISIBLE);
                    Bitmap imageBitmap = mm.getImageBitmap();
                    mImage.setImageBitmap(imageBitmap);
                }
                if (mm.getBy() == true) {
                    mTime.setText(mm.getMessageUser());

                    // Format the date before showing it
                    mUser.setText(DateFormat.format("HH:mm:ss",
                            mm.getMessageTime()));
                } else {
                    mUser.setText(mm.getMessageUser());

                    // Format the date before showing it
                    mTime.setText(DateFormat.format("HH:mm:ss",
                            mm.getMessageTime()));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
