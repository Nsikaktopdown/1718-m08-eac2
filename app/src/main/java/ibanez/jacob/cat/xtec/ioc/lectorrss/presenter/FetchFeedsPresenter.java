package ibanez.jacob.cat.xtec.ioc.lectorrss.presenter;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.xmlpull.v1.XmlPullParserException;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.List;

import ibanez.jacob.cat.xtec.ioc.lectorrss.DBInterface;
import ibanez.jacob.cat.xtec.ioc.lectorrss.Interface.MainAcivityContract;
import ibanez.jacob.cat.xtec.ioc.lectorrss.R;
import ibanez.jacob.cat.xtec.ioc.lectorrss.RssItemParser;
import ibanez.jacob.cat.xtec.ioc.lectorrss.model.RssItem;
import ibanez.jacob.cat.xtec.ioc.lectorrss.utils.ConnectionUtils;

/**
 * Created by Nsikak on 10/17/17.
 */

public class FetchFeedsPresenter implements MainAcivityContract.Presenter {

    MainAcivityContract.View view;
    private DBInterface mDataBase = null;
    private Context context;

    //Tag for logging purposes
    private static final String TAG = MainAcivityContract.class.getSimpleName();

    public FetchFeedsPresenter(MainAcivityContract.View view, Context context){
        this.view = view;
        view.setPresenter(this);
        this.context = context;
        this.mDataBase = new DBInterface(context);

    }

    @Override
    public MainAcivityContract.View getView() {
        return view;
    }

    @Override
    public void onStart() {
        view.setPresenter(this);

    }

    @Override
    public void performFeedFetch(String URL, Activity activity) {
        if(ConnectionUtils.hasConnection(activity)){

            //fetch from the server
            new DownloadRssTask().execute(URL);
        }else {
            if(mDataBase != null){
                view.showLoading(true);
                //fetch data from db
                mDataBase.open();
                view.feedRecyclerView(mDataBase.getAllItems());
                view.showLoading(false);
                mDataBase.close();
                view.showErrorMessage(context.getResources().getString(R.string.toast_offline_load));
            }
            else {

            }

            view.showLoading(false);
            view.showErrorMessage(context.getResources().getString(R.string.toast_there_is_no_connection));
        }




    }
    /**
     * This class is for downloading a XML file from the internet in a background thread
     */
    private class DownloadRssTask extends AsyncTask<String, Void, List<RssItem>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //set progress bar visible and hid recycler view, so we are connecting to the internet
            view.showLoading(true);
        }

        @Override
        protected List<RssItem> doInBackground(String... strings) {
            List<RssItem> result = null;

            try {
                //get the XML from the feed url and process it
                result = getRssItems(strings[0],context);
                //TODO save to the database all the info of the XML file
                storeResult(result);
                //download thumbnails to the cache directory
                cacheImages(result);
            } catch (IOException ex) {

            } catch (XmlPullParserException ex) {

            }

            return result;
        }

        @Override
        protected void onPostExecute(List<RssItem> items) {
            //set progress bar invisible and show recycler view, so the result from the internet has arrived
           view.showLoading(false);
            //feed the list of items of the recycler view's adapter
            view.feedRecyclerView(items);
        }
    }




    private void storeResult(List<RssItem> result) {
        mDataBase.open();
        for (RssItem item : result) {
            mDataBase.insertItem(item);
        }
        mDataBase.close();
    }

    /**
     *
     * @param result
     */
    private void cacheImages(List<RssItem> result) {
        for (RssItem item : result) {
            try {
                URL imageUrl = new URL(item.getThumbnail());
                InputStream inputStream = (InputStream) imageUrl.getContent();
                byte[] bufferImage = new byte[1024];

                OutputStream outputStream = new FileOutputStream(item.getImagePathInCache());

                int count;
                while ((count = inputStream.read(bufferImage)) != -1) {
                    outputStream.write(bufferImage, 0, count);
                }

                inputStream.close();
                outputStream.close();
            } catch (IOException ex) {
                Log.e(TAG, "Error downloading image from " + item.getThumbnail(), ex);
            }
        }
    }

    /**
     *
     * @param url
     * @return
     * @throws IOException
     * @throws XmlPullParserException
     */
    private List<RssItem> getRssItems(String url, Context context) throws IOException, XmlPullParserException {
        InputStream in = null;
        RssItemParser parser = new RssItemParser(context);
        List<RssItem> result = null;

        try {
            in = ConnectionUtils.openHttpConnection(url);
            result = parser.parse(in);
        } finally {
            if (in != null) {
                in.close();
            }
        }

        return result;
    }
}
