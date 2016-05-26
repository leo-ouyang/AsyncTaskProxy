package leo.android.core.sample.dao;

import java.util.List;

import leo.android.core.AsyncCallback;
import leo.android.core.MethodAsyncTask;
import leo.android.core.sample.beans.Article;

/**
 * Created by leo on 16-5-25.
 */
public interface IArticleDao {

    public MethodAsyncTask getArticleList(AsyncCallback<Integer, List<Article>> callback);
    public MethodAsyncTask getArticleText(String articleUrl, AsyncCallback<Integer, String> callback);

    // You could define cancelAll() method or not, it implemented by AsyncTaskProxy,
    // this method use to cancel all AsyncTasks which created by ArticleDao class.
    public void cancelAll();
}
