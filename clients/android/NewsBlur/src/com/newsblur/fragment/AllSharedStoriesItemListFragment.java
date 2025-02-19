package com.newsblur.fragment;

import java.util.ArrayList;

import android.content.ContentResolver;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.newsblur.R;
import com.newsblur.activity.AllSharedStoriesReading;
import com.newsblur.activity.FeedReading;
import com.newsblur.activity.ItemsList;
import com.newsblur.activity.Reading;
import com.newsblur.database.DatabaseConstants;
import com.newsblur.database.MultipleFeedItemsAdapter;
import com.newsblur.util.DefaultFeedView;
import com.newsblur.util.StateFilter;
import com.newsblur.util.StoryOrder;
import com.newsblur.view.SocialItemViewBinder;

public class AllSharedStoriesItemListFragment extends ItemListFragment implements OnItemClickListener {

	private String[] feedIds;
	private ContentResolver contentResolver;

    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ArrayList<String> feedIdArrayList = getArguments().getStringArrayList("feedIds");
		feedIds = new String[feedIdArrayList.size()];
		feedIdArrayList.toArray(feedIds);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_itemlist, null);
		ListView itemList = (ListView) v.findViewById(R.id.itemlistfragment_list);
        setupBezelSwipeDetector(itemList);
		itemList.setEmptyView(v.findViewById(R.id.empty_view));

		contentResolver = getActivity().getContentResolver();

		String[] groupFrom = new String[] { DatabaseConstants.STORY_TITLE, DatabaseConstants.STORY_SHORT_CONTENT, DatabaseConstants.STORY_AUTHORS, DatabaseConstants.STORY_TIMESTAMP, DatabaseConstants.STORY_INTELLIGENCE_AUTHORS, DatabaseConstants.FEED_TITLE };
		int[] groupTo = new int[] { R.id.row_item_title, R.id.row_item_content, R.id.row_item_author, R.id.row_item_date, R.id.row_item_sidebar, R.id.row_item_feedtitle };
        // TODO: defer creation of the adapter until the loader's first callback so we don't leak this first ListView cursor
        Cursor cursor = dbHelper.getStoriesCursor(getFeedSet(), currentState);
        adapter = new MultipleFeedItemsAdapter(getActivity(), R.layout.row_socialitem, cursor, groupFrom, groupTo, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        adapter.setViewBinder(new SocialItemViewBinder(getActivity()));
        itemList.setAdapter(adapter);

		getLoaderManager().initLoader(ITEMLIST_LOADER , null, this);

		itemList.setOnScrollListener(this);
		itemList.setOnItemClickListener(this);

		return v;
	}

	public static ItemListFragment newInstance(ArrayList<String> feedIds, StateFilter currentState, DefaultFeedView defaultFeedView) {
		ItemListFragment everythingFragment = new AllSharedStoriesItemListFragment();
		Bundle arguments = new Bundle();
		arguments.putSerializable("currentState", currentState);
		arguments.putStringArrayList("feedIds", feedIds);
        arguments.putSerializable("defaultFeedView", defaultFeedView);
		everythingFragment.setArguments(arguments);

		return everythingFragment;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (getActivity().isFinishing()) return;
		Intent i = new Intent(getActivity(), AllSharedStoriesReading.class);
        i.putExtra(Reading.EXTRA_FEEDSET, getFeedSet());
		i.putExtra(FeedReading.EXTRA_FEED_IDS, feedIds);
		i.putExtra(FeedReading.EXTRA_POSITION, position);
		i.putExtra(ItemsList.EXTRA_STATE, currentState);
        i.putExtra(Reading.EXTRA_DEFAULT_FEED_VIEW, defaultFeedView);
		startActivity(i);
	}

}
