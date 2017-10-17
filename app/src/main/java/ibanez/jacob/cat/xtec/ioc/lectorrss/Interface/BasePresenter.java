package ibanez.jacob.cat.xtec.ioc.lectorrss.Interface;

/**
 * Created by Nsikak on 10/17/17.
 */

public interface BasePresenter<T> {
    T getView();
    void onStart();

}
