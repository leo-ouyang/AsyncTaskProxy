# AsyncTaskProxy
Define or call an AsyncTask like to define or call a method, a class can define multiple AsyncTask methods, the AsyncTaskProxy implemented by JDK dynamic proxy.
<br>
If you don't like to write interface, you could use [AsyncTaskProxy2](https://github.com/leo-ouyang/AsyncTaskProxy2), which implemented by dynamically generate sub class.

## Usage
1 Copy below three files into your project.
  * AsyncCallback.java
  * AsyncTaskProxy.java
  * MethodAsyncTask.java

<br>
2 Define a interface, and declare methods which run in background.
```Java
public interface IArticleDao {
    public MethodAsyncTask getArticleList(AsyncCallback<Integer, List<Article>> callback);
}
```
Any method which its last argument type is AsyncCallback will be proxied, and an AsyncTask will be created while it be called, the method's body will execute in the AsyncTask object.

<br>
3 Implement interface and its methods, the class need to extend AsyncTaskProxy and pass generic type which use by AsyncTaskProxy.getProxy().
```Java
public class ArticleDao extends AsyncTaskProxy<IArticleDao> implements IArticleDao {

    public MethodAsyncTask getArticleList(AsyncCallback<Integer, List<Article>> callback) {
        ...
        if (res == null || res.getCode() != 200 || (text = res.getText()) == null) {
            callback.sendResult(Consts.ERRCODE_FAILED);
            return null;
        }
        ...
        callback.sendResult(Consts.ERRCODE_SUCCESSED, list);
        return null;  // even return null, the caller still can get MethodAsyncTask object, because getArticleList has been proxied.
    }

}
```
If you want a method don't be proxied, you could not declare AsyncCallback argument in the last position of method argument list, like below:
```Java
public class ArticleDao extends AsyncTaskProxy<IArticleDao> implements IArticleDao {

    public MethodAsyncTask getArticleList(AsyncCallback<Integer, List<Article>> callback) {
        ...
        String ip = getServiceIP();
        ...
    }
    public String getServerIP() {
        ...
    }
}
```

<br>
4 Use ArticleDao to get data from server.
```Java
IArticleDao articleDao = new ArticleDao().getProxy();
MethodAsyncTask articleListMAT = mArticleDao.getArticleList(callback);

private AsyncCallback callback = new AsyncCallback<Integer, List<Article>>() {
    @Override
    protected void onResult(int err, List<Article>... lists) {
        ...
        if (err == Consts.ERRCODE_SUCCESSED) {
            List<Article> list = lists[0];
            mAdapter.setArticleList(lists[0]);
            mAdapter.notifyDataSetChanged();
        }
    }
};

@Override
protected void onDestroy() {
    super.onDestroy();
    if (articleListMAT != null && articleListMAT.isRunning()) {
        articleListMAT.cancel(true);
    }
}
```
