package com.example.mercedes.newsapp;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Article>> {

    /**
     * basic URL for query The Guardian API
     */
    private static final String GUARDIAN_REQUEST_URL =
            "https://content.guardianapis.com/search";
    private static final String SEARCH = "q";
    private static final String SECTION = "section";
    private static final String CONTRIBUTOR = "show-tags";
    private static final String CONTRIBUTOR_VALUE = "contributor";
    private static final String ORDER_BY = "order-by";
    private static final String API_KEY = "api-key";
    private static final String API_KEY_VALUE = "test";

    // Loader Id
    private static final int LOADER_ID = 1;

    //loader
    LoaderManager loaderManager;
    //list view for displaying a news (a list of articles)
    ListView newsListView;
    //View for loading indicator
    View loadingIndicator;
    //TextView that is displayed when the list is empty
    private TextView mEmptyStateTextView;
    /**
     * Adapter for the list of articles
     */
    private NewsAdapter mAdapter;

    //search terms
    private String searchTerms = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get a reference to the LoaderManager, in order to interact with loaders.
        loaderManager = getLoaderManager();

        //References to views
        newsListView = (ListView) findViewById(R.id.list);
        mEmptyStateTextView = (TextView) findViewById(R.id.empty_list);
        loadingIndicator = findViewById(R.id.progress_bar);

        //assigns mEmptyStateTextView as empty view for the list newsListView
        newsListView.setEmptyView(mEmptyStateTextView);

        // Create an {@link NewsAdapter}, whose data source is a list of {@link Article}s. The
        // adapter knows how to create list items for each item in the list.
        mAdapter = new NewsAdapter(this, new ArrayList<Article>());

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        newsListView.setAdapter(mAdapter);

        newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Find the current article that was clicked on
                Article currentArticle = mAdapter.getItem(position);

                // Convert the String URL into a URI object (to pass into the Intent constructor)
                Uri articleUri = Uri.parse(currentArticle.getUrl());

                // Create a new intent to view the article URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, articleUri);

                // Send the intent to launch a new activity
                startActivity(websiteIntent);
            }
        });

        ConnectivityManager cm =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
            //Initialize loader
            loaderManager.initLoader(LOADER_ID, null, this);

        } else {
            loadingIndicator.setVisibility(View.GONE);
            //sets the message in the TextView
            mEmptyStateTextView.setText(getString(R.string.no_internet_connection));
        }
    }

    @Override
    public Loader<List<Article>> onCreateLoader(int id, Bundle args) {

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String section = sharedPrefs.getString(
                getString(R.string.settings_section_key),
                getString(R.string.settings_section_default));
        String orderBy = sharedPrefs.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default));
        //Form Url
        Uri baseUri = Uri.parse(GUARDIAN_REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        if (!searchTerms.isEmpty()) {
            uriBuilder.appendQueryParameter(SEARCH, searchTerms);
        }
        uriBuilder.appendQueryParameter(SECTION, section);
        uriBuilder.appendQueryParameter(CONTRIBUTOR, CONTRIBUTOR_VALUE);
        uriBuilder.appendQueryParameter(ORDER_BY, orderBy);
        uriBuilder.appendQueryParameter(API_KEY, API_KEY_VALUE);
        // Create a new loader for the given URL
        return new NewsLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<Article>> loader, List<Article> result) {

        //sets the message in the TextView
        mEmptyStateTextView.setText(getString(R.string.no_articles_found));
        loadingIndicator.setVisibility(View.GONE);

        // Clear the adapter of previous news data
        mAdapter.clear();

        // If there is a valid list of {@link Article}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (result != null && !result.isEmpty()) {
            mAdapter.addAll(result);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Article>> loader) {
        // Loader reset, so we can clear out our existing data.
        mAdapter.clear();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}