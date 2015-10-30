package com.codepath.apps.twitter.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.codepath.apps.twitter.R;
import com.codepath.apps.twitter.TwitterApplication;
import com.codepath.apps.twitter.TwitterClient;
import com.codepath.apps.twitter.constants.Extras;
import com.codepath.apps.twitter.fragments.FollowersListFragment;
import com.codepath.apps.twitter.fragments.FollowingListFragment;
import com.codepath.apps.twitter.fragments.UserTimelineFragment;
import com.codepath.apps.twitter.models.TwitterUser;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;


public class ProfileActivity extends BaseActivity {
    private static final String TAG = "PROFILE";
    private static final NumberFormat NUMBER_FORMATTTER = NumberFormat.getIntegerInstance();
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
                twitterUser = user;
                populateUserDetails(twitterUser);
            }

            @Override
            public void onFailure(Throwable error) {
                Log.e(TAG, "Failed to retrieve user profile", error);
            }
        });
        if (savedInstanceState == null) {
            populateUserTimeline(userId);
        }
    }

    private void populateUserHeader(TwitterUser user) {
        ProfileActivity.this.twitterUser = user;
        getSupportActionBar().setTitle("@" + twitterUser.getScreenName());
        TextView tvUserDescription = (TextView) findViewById(R.id.tvUserDescription);
        tvUserDescription.setText(user.getDescription());
        ImageView ivUserPhoto = (ImageView) findViewById(R.id.ivUserPhoto);
        ivUserPhoto.setImageResource(0);
        Picasso.with(getApplicationContext()).load(twitterUser.getProfileImageUrl()).into(ivUserPhoto);
        TextView tvUserName = (TextView) findViewById(R.id.tvUserName);
        tvUserName.setText(twitterUser.getName());
        TextView tvUserScreenName = (TextView) findViewById(R.id.tvUserScreenName);
        tvUserScreenName.setText("@" + twitterUser.getScreenName());
        final RelativeLayout rlUserHeader = (RelativeLayout) findViewById(R.id.rlUserHeader);
        final String backgroundImageUrl = twitterUser.getProfileBackgroundImageUrl();
        ImageView ivUserBackgroundImage = (ImageView) findViewById(R.id.ivUserBackgroundImage);
        ivUserBackgroundImage.setImageResource(0);
        if (backgroundImageUrl != null && backgroundImageUrl != "") {
            Picasso.with(getApplicationContext()).load(backgroundImageUrl).into(ivUserBackgroundImage);
        } else {
            setHeaderBackgroundColor(rlUserHeader);
        }
    }

    private void populateUserStats(final TwitterUser user) {
        TextView tvTweetsCount = (TextView) findViewById(R.id.tvTweetCount);
        tvTweetsCount.setText(NUMBER_FORMATTTER.format(user.getTweetCount()));
        tvTweetsCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                populateUserTimeline(user.getId());
            }
        });

        TextView tvFollowingCount = (TextView) findViewById(R.id.tvFollowingCount);
        tvFollowingCount.setText(NUMBER_FORMATTTER.format(user.getFriendsCount()));
        tvFollowingCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                populateFollowingList(user.getId());
            }
        });

        TextView tvFollowersCount = (TextView) findViewById(R.id.tvFollowersCount);
        tvFollowersCount.setText(NUMBER_FORMATTTER.format(user.getFollowersCount()));
        tvFollowersCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                populateFollowersList(user.getId());
            }
        });

        TextView tvFavoritesCount = (TextView) findViewById(R.id.tvFavoritesCount);
        tvFavoritesCount.setText(NUMBER_FORMATTTER.format(user.getFavoritesCount()));
    }

    private void setHeaderBackgroundColor(RelativeLayout rlUserHeader) {
        rlUserHeader.setBackgroundColor(Color.parseColor("#" + twitterUser.getProfileBackgroundColor()));
    }

    private void populateUserTimeline(Long userId) {
        showFragment(UserTimelineFragment.newInstance(userId));
    }

    private void populateFollowingList(Long userId) {
        showFragment(FollowingListFragment.newInstance(userId));
    }

    private void populateFollowersList(Long userId) {
        showFragment(FollowersListFragment.newInstance(userId));
    }

    private void showFragment(Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.flContainer, fragment);
        ft.commit();
    }

    @Override
    protected void showAuthenticatedUserProfile() {
        populateUserDetails(authenticatedUser);
    }

    private void populateUserDetails(TwitterUser user) {
        populateUserHeader(user);
        populateUserTimeline(user.getId());
        populateUserStats(user);
    }

    @Override
    protected void showSearchResults(String query) {
        Intent intent = new Intent(this, SearchActivity.class);
        intent.putExtra(Extras.QUERY, query);
        startActivity(intent);
    }

    @Override
    protected String getTag() {
        return TAG;
    }

}
