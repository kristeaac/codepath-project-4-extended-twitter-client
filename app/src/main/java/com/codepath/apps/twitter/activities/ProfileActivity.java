package com.codepath.apps.twitter.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;
import com.codepath.apps.twitter.R;
import com.codepath.apps.twitter.TwitterApplication;
import com.codepath.apps.twitter.TwitterClient;
import com.codepath.apps.twitter.constants.Extras;
import com.codepath.apps.twitter.fragments.ComposeTweetFragment;
import com.codepath.apps.twitter.fragments.HomeTimelineFragment;
import com.codepath.apps.twitter.fragments.MentionsTimelineFragment;
import com.codepath.apps.twitter.fragments.TweetListFragment;
import com.codepath.apps.twitter.fragments.UserTimelineFragment;
import com.codepath.apps.twitter.models.TwitterUser;
import com.squareup.picasso.Picasso;


public class ProfileActivity extends AppCompatActivity {
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
        client.getProfile(new TwitterClient.TwitterUserResponseHandler() { // TODO accept user_id
            @Override
            public void onSuccess(TwitterUser user) {
                ProfileActivity.this.twitterUser = user;
                getSupportActionBar().setTitle("@" + twitterUser.getScreenName());
                ImageView ivUserPhoto = (ImageView) findViewById(R.id.ivUserPhoto);
                Picasso.with(getApplicationContext()).load(user.getProfileImageUrl()).into(ivUserPhoto);
                TextView tvUserName = (TextView) findViewById(R.id.tvUserName);
                tvUserName.setText(user.getName());
                TextView tvUserScreenName = (TextView) findViewById(R.id.tvUserScreenName);
                tvUserScreenName.setText("@" + user.getScreenName());
                RelativeLayout rlUserHeader = (RelativeLayout) findViewById(R.id.rlUserHeader);
                rlUserHeader.setBackgroundColor(Color.parseColor("#" + user.getProfileBackgroundColor()));
            }

            @Override
            public void onFailure(Throwable error) {

            }
        });
        Long userId = getIntent().getLongExtra(Extras.USER_ID, -1);
        if (savedInstanceState == null) {
            UserTimelineFragment fragmentUserTimeline = UserTimelineFragment.newInstance(userId);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.flContainer, fragmentUserTimeline);
            ft.commit();
        }
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