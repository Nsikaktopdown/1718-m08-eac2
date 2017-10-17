package ibanez.jacob.cat.xtec.ioc.lectorrss.Interface;

import java.util.List;

import ibanez.jacob.cat.xtec.ioc.lectorrss.model.RssItem;

/**
 * Created by Nsikak on 10/17/17.
 */

public interface MainAcivityView<T> extends BaseView<T> {


    void feedRecyclerView(List<RssItem> rssItemList);
    void fetchFromDB(List<RssItem> feedList);
}
