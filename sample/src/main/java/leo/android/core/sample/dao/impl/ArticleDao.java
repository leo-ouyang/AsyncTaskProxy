package leo.android.core.sample.dao.impl;

import android.os.Environment;

import java.util.ArrayList;
import java.util.List;

import leo.android.core.AsyncCallback;
import leo.android.core.AsyncTaskProxy;
import leo.android.core.MethodAsyncTask;
import leo.android.core.sample.commons.Consts;
import leo.android.core.sample.beans.Article;
import leo.android.core.sample.commons.Logger;
import leo.android.core.sample.dao.IArticleDao;
import leo.commons.http.HttpRequester;
import leo.commons.http.Request;
import leo.commons.http.RequestIOException;
import leo.commons.http.Response;
import leo.commons.lang.Matcher;
import leo.commons.util.FileUtil;
import leo.commons.util.StringUtil;

/**
 * Created by leo on 16-5-25.
 */
public class ArticleDao extends AsyncTaskProxy<IArticleDao> implements IArticleDao {

    public MethodAsyncTask getArticleList(AsyncCallback<Integer, List<Article>> callback) {
        Request req = new Request();
        req.setUrl(Consts.ARTICLE_LIST_URL);
        req.setMethod("GET");
        req.setUserAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36");

        HttpRequester hr = new HttpRequester();
        Response res = null;
        try {
            res = hr.doRequest(req);
        } catch (RequestIOException e) {
            Logger.e(e);
            callback.sendResult(Consts.ERRCODE_NET_BUGEILI);
            return null;
        }
        String text = null;
        if (res == null || res.getCode() != 200 || (text = res.getText()) == null) {
            callback.sendResult(Consts.ERRCODE_FAILED);
            return null;
        }
        Logger.d("text = " + text);
        Matcher m = Matcher.init(text).match("id=\"article_list\".+?<\\!--显示分页-->").matches("<span class=\"link_title\">.+?</span>");
        String[] titles = m.clone().substring(">", "</a>", 2, 1).values();
        String[] urls = m.substring("href=\"", "\">", 1, 1).values();

        if (titles == null || urls == null) {
            callback.sendResult(Consts.ERRCODE_FAILED);
            return null;
        }
        List<Article> list = new ArrayList<Article>();
        for (int i=0; i<titles.length; i++) {
            Logger.d("title = " + titles[i] + ", url = " + urls[i]);
            list.add(new Article(titles[i], urls[i], null));
        }
        callback.sendResult(Consts.ERRCODE_SUCCESSED, list);
        return null;
    }

    public MethodAsyncTask getArticleText(String articleUrl, AsyncCallback<Integer, String> callback) {
        Request req = new Request();
        req.setUrl(articleUrl);
        req.setMethod("GET");
        req.setUserAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36");

        HttpRequester hr = new HttpRequester();
        Response res = null;
        try {
            res = hr.doRequest(req);
        } catch (RequestIOException e) {
            Logger.e(e);
            callback.sendResult(Consts.ERRCODE_NET_BUGEILI);
            return null;
        }
        String text = null;
        if (res == null || res.getCode() != 200 || (text = res.getText()) == null) {
            callback.sendResult(Consts.ERRCODE_FAILED);
            return null;
        }

        Logger.d("text 1 = " + text);
        text = Matcher.init(text).match("<div id=\"article_content\" class=\"article_content\">.+?</div>").substring(">", "</div>", 1, -1).trim().value();
        Logger.d("text 2 = " + text);

        callback.sendResult(Consts.ERRCODE_SUCCESSED, text);
        return null;
    }
}
