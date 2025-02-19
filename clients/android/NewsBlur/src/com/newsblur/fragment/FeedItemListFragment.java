package com.newsblur.fragment;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.content.CursorLoader;
import android.content.Loader;
import android.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.newsblur.R;
import com.newsblur.activity.FeedReading;
import com.newsblur.activity.ItemsList;
import com.newsblur.activity.Reading;
import com.newsblur.database.DatabaseConstants;
import com.newsblur.database.FeedItemsAdapter;
import com.newsblur.database.FeedProvider;
import com.newsblur.domain.Feed;
import com.newsblur.util.DefaultFeedView;
import com.newsblur.util.StateFilter;
import com.newsblur.util.StoryOrder;
import com.newsblur.util.ReadFilter;
import com.newsblur.view.FeedItemViewBinder;

public class FeedItemListFragment extends ItemListFragment implements OnItemClickListener {

	private String feedId;

    public static FeedItemListFragment newInstance(String feedId, StateFilter currentState, DefaultFeedView defaultFeedView) {
		FeedItemListFragment feedItemFragment = new FeedItemListFragment();

		Bundle args = new Bundle();
		args.putSerializable("currentState", currentState);
		args.putString("feedId", feedId);
        args.putSerializable("defaultFeedView", defaultFeedView);
		feedItemFragment.setArguments(args);

		return feedItemFragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		feedId = getArguments().getString("feedId");
	}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_itemlist, null);

        ListView itemList = (ListView) v.findViewById(R.id.itemlistfragment_list);
        setupBezelSwipeDetector(itemList);
        itemList.setEmptyView(v.findViewById(R.id.empty_view));

        ContentResolver contentResolver = getActivity().getContentResolver();
        // TODO: defer creation of the adapter until the loader's first callback so we don't leak this first stories cursor
        Cursor storiesCursor = dbHelper.getStoriesCursor(getFeedSet(), currentState);
        Uri feedUri = FeedProvider.FEEDS_URI.buildUpon().appendPath(feedId).build();
        Cursor feedCursor = contentResolver.query(feedUri, null, null, null, null);

        if (feedCursor.getCount() < 1) {
            // This shouldn't happen, but crash reports indicate that it does (very rarely).
            // If we are told to create an item list for a feed, but then can't find that feed ID in the DB,
            // something is very wrong, and we won't be able to recover, so just force the user back to the
            // feed list until we have a better understanding of how to prevent this.
            Log.w(this.getClass().getName(), "Feed not found in DB, can't create item list.");
            getActivity().finish();
            return v;
        }

        feedCursor.moveToFirst();
        Feed feed = Feed.fromCursor(feedCursor);
        feedCursor.close();

        String[] groupFrom = new String[] { DatabaseConstants.STORY_TITLE, DatabaseConstants.STORY_SHORT_CONTENT, DatabaseConstants.STORY_AUTHORS, DatabaseConstants.STORY_TIMESTAMP, DatabaseConstants.STORY_INTELLIGENCE_AUTHORS };
        int[] groupTo = new int[] { R.id.row_item_title, R.id.row_item_content, R.id.row_item_author, R.id.row_item_date, R.id.row_item_sidebar };

        // create the adapter before starting the loader, since the callback updates the adapter
        adapter = new FeedItemsAdapter(getActivity(), feed, R.layout.row_item, storiesCursor, groupFrom, groupTo, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

        getLoaderManager().initLoader(ITEMLIST_LOADER , null, this);

        itemList.setOnScrollListener(this);

        adapter.setViewBinder(new FeedItemViewBinder(getActivity()));
        itemList.setAdapter(adapter);
        itemList.setOnItemClickListener(this);
        itemList.setOnCreateContextMenuListener(this);
        
        return v;
    }

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (getActivity().isFinishing()) return;
		Intent i = new Intent(getActivity(), FeedReading.class);
        i.putExtra(Reading.EXTRA_FEEDSET, getFeedSet());
		i.putExtra(Reading.EXTRA_FEED, feedId);
		i.putExtra(FeedReading.EXTRA_POSITION, position);
		i.putExtra(ItemsList.EXTRA_STATE, currentState);
        i.putExtra(Reading.EXTRA_DEFAULT_FEED_VIEW, defaultFeedView);
		startActivity(i);
	}

}
