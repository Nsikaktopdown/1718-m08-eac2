package ibanez.jacob.cat.xtec.ioc.lectorrss.Interface;

/**
 * Created by Nsikak on 10/17/17.
 */

public interface BaseView<T> {
    void showLoading(boolean show);
    void showErrorMessage(String message);
    void setPresenter(T presenter);
}
