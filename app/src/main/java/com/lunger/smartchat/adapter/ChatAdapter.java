package com.lunger.smartchat.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lunger.smartchat.R;
import com.lunger.smartchat.model.ChatModel;

import java.util.List;

/**
 * Created by Lunger on 2016/11/30.
 */
public class ChatAdapter extends BaseAdapter {
    private Context mContext;
    private List<ChatModel> mMessages;

    public ChatAdapter(Context context, List<ChatModel> messages) {
        mContext = context;
        mMessages = messages;
    }

    @Override
    public int getCount() {
        if (mMessages != null) {
            return mMessages.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (mMessages != null) {
            return mMessages.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if(convertView == null){
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_chat_adapter, null);
            holder.tv_received = (TextView) convertView.findViewById(R.id.tv_received);
            holder.tv_send = (TextView) convertView.findViewById(R.id.tv_send);
            holder.iv_robot_head = (ImageView) convertView.findViewById(R.id.iv_robot_head);
            holder.iv_self_head = (ImageView) convertView.findViewById(R.id.iv_self_head);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        ChatModel chatModel = mMessages.get(position);
        if(chatModel.isRobotMsg()){
            holder.tv_received.setVisibility(View.VISIBLE);
            holder.tv_received.setText(chatModel.getMessageReceived());
            holder.tv_send.setVisibility(View.GONE);
            holder.iv_robot_head.setVisibility(View.VISIBLE);
            holder.iv_self_head.setVisibility(View.GONE);
        }else{
            holder.tv_received.setVisibility(View.GONE);
            holder.tv_send.setVisibility(View.VISIBLE);
            holder.tv_send.setText(chatModel.getMessageSend());
            holder.iv_robot_head.setVisibility(View.GONE);
            holder.iv_self_head.setVisibility(View.VISIBLE);
        }
        return convertView;
    }

    private class ViewHolder {
        ImageView iv_robot_head;
        ImageView iv_self_head;
        TextView tv_send;
        TextView tv_received;
    }
}
