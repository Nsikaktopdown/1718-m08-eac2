package ibanez.jacob.cat.xtec.ioc.lectorrss.Interface;

import java.util.List;

import ibanez.jacob.cat.xtec.ioc.lectorrss.model.RssItem;

/**
 * Created by Nsikak on 10/17/17.
 */

public interface MainAcivityView<T> extends BaseView<T> {

    void fetchDataFromServer();
    void feedRecyclerView(List<RssItem> rssItemList);
}
