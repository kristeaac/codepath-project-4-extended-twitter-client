package com.codepath.apps.twitter.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.twitter.R;
import com.codepath.apps.twitter.TwitterApplication;
import com.codepath.apps.twitter.TwitterClient;
import com.codepath.apps.twitter.constants.Extras;
import com.codepath.apps.twitter.models.Tweet;
import com.codepath.apps.twitter.models.TwitterUser;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class TweetDetailsActivity extends AppCompatActivity {
    private static final String TAG = "TWEET_DETAILS";
    private static final SimpleDateFormat FORMATTER = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy");
    private static final SimpleDateFormat STRING_FORMATTER = new SimpleDateFormat("hh:mm a - dd MMM yyyy");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Long id = getIntent().getLongExtra(Extras.TWEET_ID, -1L);
        TwitterApplication.getRestClient().getStatus(id, new TwitterClient.TweetResponseHandler() {
            @Override
            public void onSuccess(Tweet tweet) {
                populateTweetDetails(tweet);
            }

            @Override
            public void onFailure(Throwable error) {
                Log.e(TAG, "Failed to fetch tweet", error);
            }
        });
    }

    private void populateTweetDetails(Tweet tweet) {
        TextView tvTweetText = (TextView) findViewById(R.id.tvTweetText);
        tvTweetText.setText(Html.fromHtml(tweet.getText()), TextView.BufferType.SPANNABLE);
        ImageView ivUserPhoto = (ImageView) findViewById(R.id.ivUserPhoto);
        TwitterUser user = tweet.getUser();
        Picasso.with(this).load(user.getProfileImageUrl()).into(ivUserPhoto);
        TextView tvUserName = (TextView) findViewById(R.id.tvUserName);
        tvUserName.setText(user.getName());
        TextView tvUserScreenName = (TextView) findViewById(R.id.tvUserScreenName);
        tvUserScreenName.setText("@" + user.getScreenName());
        TextView tvCreatedAt = (TextView) findViewById(R.id.tvCreatedAt);
        TextView tvRetweetCount = (TextView) findViewById(R.id.tvReweetCount);
        tvRetweetCount.setText(String.valueOf(tweet.getRetweetCount()));
        TextView tvFavoritesCount = (TextView) findViewById(R.id.tvFavoritesCount);
        tvFavoritesCount.setText(String.valueOf(tweet.getFavoritesCount()));
        try {
            tvCreatedAt.setText(STRING_FORMATTER.format(FORMATTER.parse(tweet.getCreatedAt())));
        } catch (ParseException e) {
            Log.d("TWEET", "uh oh", e);
        }
    }

}
