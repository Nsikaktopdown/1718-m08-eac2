package ibanez.jacob.cat.xtec.ioc.lectorrss;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.List;

import ibanez.jacob.cat.xtec.ioc.lectorrss.Interface.MainAcivityContract;
import ibanez.jacob.cat.xtec.ioc.lectorrss.model.RssItem;
import ibanez.jacob.cat.xtec.ioc.lectorrss.presenter.FetchFeedsPresenter;

/**
 * Main Activity
 *
 * @author <a href="mailto:jacobibanez@jacobibanez.com">Jacob Ibáñez Sánchez</a>.
 */
public class MainActivity extends AppCompatActivity implements MainAcivityContract.View {

    //Tag for logging purposes
    private static final String TAG = MainActivity.class.getSimpleName();

    public static final String FEED_CHANNEL = "http://www.eldiario.es/rss/";

    private LinearLayout mSearchBar;
    private EditText mSearchQuery;
    private ProgressBar mProgressBar;
    private ItemAdapter mItemAdapter;
    private MainAcivityContract.Presenter mPresenter;
    private AppCompatActivity activity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Get references to member variables
        mSearchBar = (LinearLayout) findViewById(R.id.search_bar);
        mSearchQuery = (EditText) findViewById(R.id.et_search);
        mProgressBar = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        mItemAdapter = new ItemAdapter(this);

        activity = new AppCompatActivity();


        new FetchFeedsPresenter(this, getApplicationContext());

        //set the layout manager and the adapter of the recycler view
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mItemAdapter);
        //add a decorator to separate items
        DividerItemDecoration decoration = new DividerItemDecoration(this, layoutManager.getOrientation());
        recyclerView.addItemDecoration(decoration);

        //set onClickListener for the search button
        ImageButton searchButton = (ImageButton) findViewById(R.id.ib_search);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mItemAdapter.searchItems(mSearchQuery.getText().toString());
            }
        });


       //calling the presenter to fetch the data from the server or db
        mPresenter.performFeedFetch(FEED_CHANNEL, this);


    }



    @Override
    protected void onStart() {
        super.onStart();
        mPresenter.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * This method handles behavior when a menu item is selected
     *
     * @param item The selected item
     * @return Return false to allow normal menu processing to proceed, true to consume it here.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //we check which button has been pressed
        switch (id) {
            case R.id.action_refresh:   //refresh button has been pressed
                //refresh the recycler view content
                mPresenter.performFeedFetch(FEED_CHANNEL, this);
                return true;
            case R.id.action_search:    //search button has been pressed
                //we only have to toggle the search bar
                toggleSearchBar();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void toggleSearchBar() {
        //When not visible, the search bar's visibility has to be GONE, so the layout
        //doesn't occupy space in the parent layout
        if (mSearchBar.getVisibility() == View.GONE) {
            mSearchBar.setVisibility(View.VISIBLE);
        } else if (mSearchBar.getVisibility() == View.VISIBLE) {
            mSearchBar.setVisibility(View.GONE);
        }
    }


    @Override
    public void showLoading(boolean show) {
        if (show) {
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mProgressBar.setVisibility(View.GONE);
        }


    }

    @Override
    public void showErrorMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void setPresenter(MainAcivityContract.Presenter presenter) {
        this.mPresenter = presenter;

    }


    @Override
    public void feedRecyclerView(List<RssItem> rssItemList) {
        mItemAdapter.setItems(rssItemList);

    }

    @Override
    public void fetchFromDB(List<RssItem> feedList) {
        mItemAdapter.setItems(feedList);

    }
}
