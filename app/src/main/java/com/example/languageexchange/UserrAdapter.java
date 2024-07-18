package com.example.languageexchange;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

public class UserrAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Userr> userList;
    private LayoutInflater inflater;

    public UserrAdapter(Context context, ArrayList<Userr> userList) {
        this.context = context;
        this.userList = userList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return userList.size();
    }

    @Override
    public Object getItem(int position) {
        return userList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.each_user, parent, false);
            holder = new ViewHolder();
            holder.userTv = convertView.findViewById(R.id.userTv);
            holder.usernameTv = convertView.findViewById(R.id.usernameTv);
            holder.imageIv = convertView.findViewById(R.id.imageIv);
            holder.reportButton = convertView.findViewById(R.id.reportButton);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Userr user = userList.get(position);

        holder.userTv.setText(user.getName());
        holder.usernameTv.setText(user.getUsername());

        RequestOptions requestOptions = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.hack) // Placeholder image
                .error(R.drawable.hack); // Error image

        Glide.with(context)
                .load(user.getProfile_picture())
                .apply(requestOptions)
                .into(holder.imageIv);

        holder.reportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, ReportActivity.class);
                intent.putExtra("userId", user.getUserId());
                context.startActivity(intent);
            }
        });

        return convertView;
    }

    private static class ViewHolder {
        TextView userTv;
        TextView usernameTv;
        ImageView imageIv;
        Button reportButton;
    }
}
