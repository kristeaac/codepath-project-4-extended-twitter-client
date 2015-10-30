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

public class TwitterClient extends OAuthBaseClient {
	public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class;
	public static final String REST_URL = "https://api.twitter.com/1.1";
	public static final String REST_CONSUMER_KEY = "FgoIJdfeJnVm77u4ncq11mCYG";
	public static final String REST_CONSUMER_SECRET = "eu2MSE60EA6DLhL3UcfpVfYVTVWhluZTlfzsTiwWvTSkqDn4S7";
	public static final String REST_CALLBACK_URL = "oauth://cpsimpletweets"; // Change this (here and in manifest)

	public TwitterClient(Context context) {
		super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
	}

	public void getHomeTimeline(TimelineResponseHandler handler) {
		getNewerHomeTimeline(handler, 1L);
	}

	public void getNewerHomeTimeline(final TimelineResponseHandler handler, Long sinceId) {
		getTimeline(handler, sinceId, "since_id", "home_timeline");
	}

	public void getOlderHomeTimeline(final TimelineResponseHandler handler, Long maxId) {
		getTimeline(handler, maxId, "max_id", "home_timeline");
	}

	public void getMentionsTimeline(TimelineResponseHandler handler) {
		getNewerMentionsTimeline(handler, 1L);
	}

	public void getNewerMentionsTimeline(final TimelineResponseHandler handler, Long sinceId) {
		getTimeline(handler, sinceId, "since_id", "mentions_timeline");
	}

	public void getOlderMentionsTimeline(final TimelineResponseHandler handler, Long maxId) {
		getTimeline(handler, maxId, "max_id", "mentions_timeline");
	}

	public void getTimeline(final TimelineResponseHandler handler, Long id, String paramName, String timelinePath) {
		String apiUrl = getApiUrl(String.format("statuses/%s.json", timelinePath));
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

	public void getUserTimeline(Long userId, final TimelineResponseHandler handler) {
		getNewerUserTimeline(handler, userId, 1L);
	}

	public void getNewerUserTimeline(final TimelineResponseHandler handler, Long userId, Long sinceId) {
		getUserTimeline(handler, userId, sinceId, "since_id");
	}

	public void getOlderUserTimeline(final TimelineResponseHandler handler, Long userId, Long maxId) {
		getUserTimeline(handler, userId, maxId, "max_id");
	}

	private void getUserTimeline(final TimelineResponseHandler handler, Long userId, Long id, String paramName) {
		String apiUrl = getApiUrl("statuses/user_timeline.json");
		RequestParams params = new RequestParams();
		params.put("user_id", userId);
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

	public void getAuthenticatedUser(final TwitterUserResponseHandler handler) {
		String apiUrl = getApiUrl("account/verify_credentials.json");
		getUser(handler, apiUrl, null);
	}

	public void getUser(Long userId, final TwitterUserResponseHandler handler) {
		String apiUrl = getApiUrl("users/show.json");
		RequestParams params = new RequestParams();
		params.put("user_id", userId);
		getUser(handler, apiUrl, params);
	}

	private void getUser(final TwitterUserResponseHandler handler, String apiUrl, RequestParams params) {
		getClient().get(apiUrl, params, new AsyncHttpResponseHandler() {
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
		replyToStatus(status, null, handler);
	}

	public void replyToStatus(String status, Long statusId, final StatusUpdateResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/update.json");
		RequestParams params = new RequestParams();
		params.put("status", status);
		if (statusId != null) {
			params.put("in_reply_to_status_id", statusId);
		}
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

	public void getStatus(Long id, final TweetResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/show.json");
		RequestParams params = new RequestParams();
		params.put("id", id);
		getClient().get(apiUrl, params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				ObjectMapper mapper = new ObjectMapper();
				try {
					Tweet tweet = mapper.readValue(responseBody, Tweet.class);
					handler.onSuccess(tweet);
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

	public void retweet(Long id, final TweetResponseHandler handler) {
		String apiUrl = getApiUrl(String.format("statuses/retweet/%d.json", id));
		getClient().post(apiUrl, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				ObjectMapper mapper = new ObjectMapper();
				try {
					Tweet tweet = mapper.readValue(responseBody, Tweet.class);
					handler.onSuccess(tweet);
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

	public void favorite(Long id, final TweetResponseHandler handler) {
		String apiUrl = getApiUrl("favorites/create.json");
		RequestParams params = new RequestParams();
		params.put("id", id);
		getClient().post(apiUrl, params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				ObjectMapper mapper = new ObjectMapper();
				try {
					Tweet tweet = mapper.readValue(responseBody, Tweet.class);
					handler.onSuccess(tweet);
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

	public void unfavorite(Long id, final TweetResponseHandler handler) {
		String apiUrl = getApiUrl("favorites/destroy.json");
		RequestParams params = new RequestParams();
		params.put("id", id);
		getClient().post(apiUrl, params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				ObjectMapper mapper = new ObjectMapper();
				try {
					Tweet tweet = mapper.readValue(responseBody, Tweet.class);
					handler.onSuccess(tweet);
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


	public interface TimelineResponseHandler {

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

	public interface TweetResponseHandler {

		void onSuccess(Tweet tweet);

		void onFailure(Throwable error);

	}

}