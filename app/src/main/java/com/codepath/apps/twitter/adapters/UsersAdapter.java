package com.codepath.apps.twitter.adapters;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.codepath.apps.twitter.R;
import com.codepath.apps.twitter.models.TwitterUser;
import com.squareup.picasso.Picasso;


import java.util.List;

public class UsersAdapter extends ArrayAdapter<TwitterUser> {

    public UsersAdapter(Context context, List<TwitterUser> users) {
        super(context, android.R.layout.simple_list_item_1, users);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final TwitterUser user = getItem(position);
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_user, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.rlUserHeader = (RelativeLayout) convertView.findViewById(R.id.rlUserHeader);
            viewHolder.ivBackgroundImage = (ImageView) convertView.findViewById(R.id.ivUserBackgroundImage);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        TextView tvUserDescription = (TextView) convertView.findViewById(R.id.tvUserDescription);
        tvUserDescription.setText(user.getDescription());
        ImageView ivUserPhoto = (ImageView) convertView.findViewById(R.id.ivUserPhoto);
        ivUserPhoto.setImageResource(0);
        Picasso.with(getContext()).load(user.getProfileImageUrl()).into(ivUserPhoto);
        TextView tvUserName = (TextView) convertView.findViewById(R.id.tvUserName);
        tvUserName.setText(user.getName());
        TextView tvUserScreenName = (TextView) convertView.findViewById(R.id.tvUserScreenName);
        tvUserScreenName.setText("@" + user.getScreenName());
        final String backgroundImageUrl = user.getProfileBackgroundImageUrl();
        viewHolder.ivBackgroundImage.setImageResource(0);
        if (backgroundImageUrl != null && backgroundImageUrl != "") {
            Picasso.with(getContext()).load(backgroundImageUrl).into(viewHolder.ivBackgroundImage);
        } else {
            setHeaderBackgroundColor(viewHolder.rlUserHeader, user.getProfileBackgroundColor());
        }
        return convertView;
    }

    private void setHeaderBackgroundColor(RelativeLayout rlUserHeader, String backgroundColor) {
        if (rlUserHeader != null && backgroundColor != null) {
            rlUserHeader.setBackgroundColor(Color.parseColor("#" + backgroundColor));
        }
    }

    public static class ViewHolder {
        ImageView ivBackgroundImage;
        RelativeLayout rlUserHeader;
    }

}
