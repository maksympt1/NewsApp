package com.example.android.newsapp;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Story>> {

    public static final String LOG_TAG = MainActivity.class.getName();

    // Constant value for the news loader ID.
    private static int NEWS_LOADER_ID = 1;

    private static String NEWS_REQUEST_URL_START = "http://content.guardianapis.com/search?q=";
    private static String NEWS_REQUEST_URL_END = "&api-key=test";

    // Adapter for the list of books
    private NewsAdapter mAdapter;

    //TextView that is displayed when the list is empty
    private TextView mEmptyStateTextView;

    private String mSearchWord = "android";

    boolean internet_connection() {
        //Check if connected to internet, output accordingly
        ConnectivityManager cm =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button searchButton = (Button) findViewById(R.id.search_button);
        final EditText searchEditText = (EditText) findViewById(R.id.search_edit_text);
        final ProgressBar loadingIndicator = (ProgressBar) findViewById(R.id.loading_indicator);

        // Find a reference to the {@link ListView} in the layout
        final ListView newsListView = (ListView) findViewById(R.id.list);

        // Create a new adapter that takes an empty list of stories as input
        mAdapter = new NewsAdapter(this, new ArrayList<Story>());

        //Hooking up Adapter and List
        newsListView.setAdapter(mAdapter);

        // OnItemClickListener open the website for the current story
        newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Story currentStory = (Story) mAdapter.getItem(position);
                Uri bookUri = Uri.parse(currentStory.getUrl());
                Intent webIntent = new Intent(Intent.ACTION_VIEW, bookUri);
                if (webIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(webIntent);
                }
            }
        });

        //Hooking up EmptyView to TextView to ListView
        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        newsListView.setEmptyView(mEmptyStateTextView);

        // Hide the keyboard when the app starts
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // Set an OnClickListener on the search button
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                if (internet_connection()) {
                    // If there is internet connection, look for the input
                    mSearchWord = searchEditText.getText().toString().replaceAll("\\s+", "").toLowerCase();
                    if (mSearchWord.isEmpty()) {
                        // User didn't write anything in the search bar
                        // Remind the user about the input
                        Toast.makeText(MainActivity.this, getString(R.string.hint), Toast.LENGTH_SHORT).show();
                    } else {
                        // User wrote something
                        //Hide these
                        newsListView.setVisibility(View.INVISIBLE);
                        mEmptyStateTextView.setVisibility(View.GONE);
                        // Show loading indicator
                        loadingIndicator.setVisibility(View.VISIBLE);
                        LoaderManager loaderManager = getLoaderManager();
                        NEWS_LOADER_ID=NEWS_LOADER_ID+1;
                        loaderManager.initLoader(NEWS_LOADER_ID, null, MainActivity.this);
                    }

                } else {
                    //Let the user still have access to the previous list, and just show him a Toast
                    Toast.makeText(MainActivity.this, getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Initialize Loader
        if (internet_connection()) {
            // There is internet connection
            // Start the Loader
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(NEWS_LOADER_ID, null, this);

        } else {
            //There is no internet connection
            loadingIndicator.setVisibility(View.GONE);
            mEmptyStateTextView.setVisibility(View.VISIBLE);
            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }
    }

    @Override
    public Loader<List<Story>> onCreateLoader(int i, Bundle bundle) {
        // Create a new loader for the given URL
        return new NewsLoader(this, NEWS_REQUEST_URL_START + mSearchWord + NEWS_REQUEST_URL_END);
    }

    @Override
    public void onLoadFinished(Loader<List<Story>> loader, List<Story> news) {
        //Data loaded - hide loading indicator
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);

        // Set empty state text to display "No news."
        mEmptyStateTextView.setText(R.string.no_news);

        // Clear and repopulate the adapter
        mAdapter.clear();

        if (news != null && !news.isEmpty()) {
            mAdapter.addAll(news);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Story>> loader) {
        // Loader reset, so we can clear out our existing data.
        mAdapter.clear();
    }
}
