package com.example.acer.newsapp;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<articleinfo>> {


    private static final String USGS_REQUEST_URL =
            "https://content.guardianapis.com/search?from-date=2019-01-01&q=%20Egypt&show-fields=byline&api-key=87cde62e-ded1-4432-95ff-448442dfdeeb";
    private ArrayList<articleinfo> articles = new ArrayList<articleinfo>();
    private MyAdapter adapter;
    private TextView emptyView;
    private ProgressBar loading;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar);


        loading = findViewById(R.id.progress);

        emptyView = findViewById(R.id.nodata);

        ListView articleListView = (ListView) findViewById(R.id.mainlist);

        articleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent ur = new Intent();
                Uri u = AsyncTaskloader.getarticle(position);
                ur.setAction(Intent.ACTION_VIEW);
                ur.setData(u);
                if (ur.resolveActivity(getPackageManager()) != null) {
                    startActivity(ur);
                }

            }
        });
        adapter = new MyAdapter(this, articles);

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        articleListView.setAdapter(adapter);
        articleListView.setEmptyView(emptyView);

        NetworkChangeReceiver n = new NetworkChangeReceiver() {// for detecting network changes
            public void onReceive(Context context, Intent intent) {

                if (intent.getExtras() != null) {
                    NetworkInfo ni = (NetworkInfo) intent.getExtras().get(ConnectivityManager.EXTRA_NETWORK_INFO);
                    if (ni != null && ni.getState() == NetworkInfo.State.CONNECTED) {

                        switchview(true);
                        Toast.makeText(context, "Connected", Toast.LENGTH_SHORT).show();


                    }
                }
                if (intent.getExtras().getBoolean(ConnectivityManager.EXTRA_NO_CONNECTIVITY, Boolean.FALSE)) {
                    switchview(false);
                    Toast.makeText(context, "not Connected", Toast.LENGTH_SHORT).show();

                }
            }


        };
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(n, filter);
    }

    @Override
    public Loader<ArrayList<articleinfo>> onCreateLoader(int id, Bundle args) {

        final String FORECAST_BASE_URL ="https://content.guardianapis.com/search?";

        final String DATE_PARAM = "from-date";
        final String QUERY_PARAM = "q";
        final String FIELD_PARAM = "show-fields";
        final String KEY_PARAM = "api-key";

        Uri.Builder builder = Uri.parse(FORECAST_BASE_URL).buildUpon();

        builder.appendQueryParameter(DATE_PARAM, "2019-01-01")
                .appendQueryParameter(QUERY_PARAM, "Egypt")
                .appendQueryParameter(FIELD_PARAM, "byline")
                .appendQueryParameter(KEY_PARAM,"87cde62e-ded1-4432-95ff-448442dfdeeb");

        Uri built = builder.build();


        return new AsyncTaskloader(this, built);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<articleinfo>> loader, ArrayList<articleinfo> data) {
        if (data == null) {
            Toast.makeText(this, "Invalid request", Toast.LENGTH_SHORT).show();
            return;
        }
        loading.setVisibility(View.GONE);
        emptyView.setText("No Data Found");
        articles = data;
        adapter.clear();
        adapter.addAll(articles);
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<articleinfo>> loader) {
        adapter.clear();
    }

    public void switchview(boolean status) {
        if (status) {
            adapter.clear();
            emptyView.setText("Connection Established");
            loading.setVisibility(View.VISIBLE);
            getLoaderManager().initLoader(1, null, this);
        } else {
            emptyView.setText("No Internet Connection");
            emptyView.setTextColor(getResources().getColor(R.color.colorAccent));
            adapter.clear();
            loading.setVisibility(View.GONE);

        }
    }
}




