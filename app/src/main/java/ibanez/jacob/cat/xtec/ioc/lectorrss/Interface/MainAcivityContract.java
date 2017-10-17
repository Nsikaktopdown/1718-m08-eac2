package ibanez.jacob.cat.xtec.ioc.lectorrss.Interface;

import android.app.Activity;

/**
 * Created by Nsikak on 10/17/17.
 */

public class MainAcivityContract {
    public interface View extends MainAcivityView<Presenter> {

    }

    public interface Presenter extends BasePresenter<View> {
        void performFeedFetch(String URL, Activity activity);
    }
}
