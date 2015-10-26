package com.codepath.apps.twitter;

import org.apache.http.Header;
import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;

import android.content.Context;

import com.codepath.apps.twitter.models.Tweet;
import com.codepath.apps.twitter.models.TwitterUser;
import com.codepath.oauth.OAuthBaseClient;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.IOException;
import java.util.List;

/*
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes: 
 *   https://github.com/fernandezpablo85/scribe-java/tree/master/src/main/java/org/scribe/builder/api
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 * 
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 * 
 */
public class TwitterClient extends OAuthBaseClient {
	public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class;
	public static final String REST_URL = "https://api.twitter.com/1.1/";
	public static final String REST_CONSUMER_KEY = "iLFoYN2ROqxLueDCPPx4hwuhK";
	public static final String REST_CONSUMER_SECRET = "tt1RGzFwkRBNhwdIMsa5rIzKGs50VZaURIizMtX3fSCSKaaHUI";
	public static final String REST_CALLBACK_URL = "oauth://cpsimpletweets"; // Change this (here and in manifest)

	public TwitterClient(Context context) {
		super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
	}

	public void getHomeTimeline(TweetResponseHandler handler) {
		getNewerHomeTimeline(handler, 1L);
	}

	public void getNewerHomeTimeline(final TweetResponseHandler handler, Long sinceId) {
		getHomeTimeline(handler, sinceId, "since_id");
	}

	public void getOlderHomeTimeline(final TweetResponseHandler handler, Long maxId) {
		getHomeTimeline(handler, maxId, "max_id");
	}

	public void getHomeTimeline(final TweetResponseHandler handler, Long id, String paramName) {
		String apiUrl = getApiUrl("statuses/home_timeline.json");
		RequestParams params = new RequestParams();
		params.put("count", 25);
		params.put(paramName, id);
		getClient().get(apiUrl, params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				ObjectMapper mapper = new ObjectMapper();
				try {
					List<Tweet> tweets = mapper.readValue(responseBody, new TypeReference<List<Tweet>>() {
					});
					handler.onSuccess(tweets);
				} catch (IOException e) {
					handler.onFailure(e);
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				handler.onFailure(error);
			}
		});
	}

	public void getProfile(final TwitterUserResponseHandler handler) {
		String apiUrl = getApiUrl("account/verify_credentials.json");
		getClient().get(apiUrl, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				ObjectMapper mapper = new ObjectMapper();
				try {
					TwitterUser user = mapper.readValue(responseBody, TwitterUser.class);
					handler.onSuccess(user);
				} catch (IOException e) {
					handler.onFailure(e);
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				handler.onFailure(error);
			}
		});
	}

	public void updateStatus(String status, final StatusUpdateResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/update.json");
		RequestParams params = new RequestParams();
		params.put("status", status);
		getClient().post(apiUrl, params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				handler.onSuccess();
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				handler.onFailure(error);
			}
		});
	}

	public interface TweetResponseHandler {

		void onSuccess(List<Tweet> tweets);

		void onFailure(Throwable error);

	}

	public interface TwitterUserResponseHandler {

		void onSuccess(TwitterUser user);

		void onFailure(Throwable error);

	}

	public interface StatusUpdateResponseHandler {

		void onSuccess();

		void onFailure(Throwable error);

	}

}