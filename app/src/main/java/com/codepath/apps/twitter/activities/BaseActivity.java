package com.codepath.apps.twitter.activities;

import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.codepath.apps.twitter.R;
import com.codepath.apps.twitter.TwitterApplication;
import com.codepath.apps.twitter.TwitterClient;
import com.codepath.apps.twitter.models.TwitterUser;

public abstract class BaseActivity extends AppCompatActivity {
    protected TwitterUser authenticatedUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar supportActionBar = getSupportActionBar();
        supportActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
        supportActionBar.setIcon(R.drawable.twitter_logo_white_48);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        setupProfileMenuItem(menu);
        setupSearchMenuItem(menu);
        return true;
    }

    private void setupProfileMenuItem(Menu menu) {
        MenuItem miProfile = menu.findItem(R.id.action_profile);
        miProfile.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (authenticatedUser == null) {
                    TwitterApplication.getRestClient().getAuthenticatedUser(new TwitterClient.TwitterUserResponseHandler() {
                        @Override
                        public void onSuccess(TwitterUser user) {
                            authenticatedUser = user;
                            showAuthenticatedUserProfile();
                        }

                        @Override
                        public void onFailure(Throwable error) {
                            Log.e(getTag(), "Failed to get user's profile", error);
                        }
                    });
                } else {
                    showAuthenticatedUserProfile();
                }
                return true;
            }
        });
    }

    private void setupSearchMenuItem(Menu menu) {
        MenuItem searchItem = menu.findItem(R.id.action_search_tweets);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String query) {
                showSearchResults(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    protected abstract void showAuthenticatedUserProfile();

    protected abstract void showSearchResults(String query);

    protected abstract String getTag();

}
