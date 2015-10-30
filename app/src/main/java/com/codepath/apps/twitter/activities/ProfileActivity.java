package com.codepath.apps.twitter.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.codepath.apps.twitter.R;
import com.codepath.apps.twitter.TwitterApplication;
import com.codepath.apps.twitter.TwitterClient;
import com.codepath.apps.twitter.constants.Extras;
import com.codepath.apps.twitter.fragments.UserTimelineFragment;
import com.codepath.apps.twitter.models.TwitterUser;
import com.squareup.picasso.Picasso;


public class ProfileActivity extends AppCompatActivity {
    public static final String TAG = "PROFILE";
    private TwitterClient client;
    private TwitterUser twitterUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ActionBar supportActionBar = getSupportActionBar();
        supportActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
        supportActionBar.setIcon(R.drawable.twitter_logo_white_48);
        client = TwitterApplication.getRestClient();
        Long userId = getIntent().getLongExtra(Extras.USER_ID, -1);
        client.getUser(userId, new TwitterClient.TwitterUserResponseHandler() {
            @Override
            public void onSuccess(TwitterUser user) {
                ProfileActivity.this.twitterUser = user;
                getSupportActionBar().setTitle("@" + twitterUser.getScreenName());
                ImageView ivUserPhoto = (ImageView) findViewById(R.id.ivUserPhoto);
                ivUserPhoto.setImageResource(0);
                Picasso.with(getApplicationContext()).load(twitterUser.getProfileImageUrl()).into(ivUserPhoto);
                TextView tvUserName = (TextView) findViewById(R.id.tvUserName);
                tvUserName.setText(twitterUser.getName());
                TextView tvUserScreenName = (TextView) findViewById(R.id.tvUserScreenName);
                tvUserScreenName.setText("@" + twitterUser.getScreenName());
                final RelativeLayout rlUserHeader = (RelativeLayout) findViewById(R.id.rlUserHeader);
                final String backgroundImageUrl = twitterUser.getProfileBackgroundImageUrl();
                Log.d(TAG, "backgroundImageUrl=" + backgroundImageUrl);
                ImageView ivUserBackgroundImage = (ImageView) findViewById(R.id.ivUserBackgroundImage);
                ivUserBackgroundImage.setImageResource(0);
                if (backgroundImageUrl != null && backgroundImageUrl != "") {
                    Picasso.with(getApplicationContext()).load(backgroundImageUrl).into(ivUserBackgroundImage);
                } else {
                    setHeaderBackgroundColor(rlUserHeader);
                }
            }

            @Override
            public void onFailure(Throwable error) {
                Log.e(TAG, "Failed to retrieve user profile", error);
            }
        });
        if (savedInstanceState == null) {
            UserTimelineFragment fragmentUserTimeline = UserTimelineFragment.newInstance(userId);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.flContainer, fragmentUserTimeline);
            ft.commit();
        }
    }

    private void setHeaderBackgroundColor(RelativeLayout rlUserHeader) {
        rlUserHeader.setBackgroundColor(Color.parseColor("#" + twitterUser.getProfileBackgroundColor()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem miProfile = menu.findItem(R.id.action_profile);
        miProfile.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return false;
            }
        });
        return true;
    }

}
