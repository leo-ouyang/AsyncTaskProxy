package leo.android.core.sample.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import leo.android.core.AsyncCallback;
import leo.android.core.MethodAsyncTask;
import leo.android.core.sample.R;
import leo.android.core.sample.beans.Article;
import leo.android.core.sample.commons.Consts;
import leo.android.core.sample.commons.Logger;
import leo.android.core.sample.dao.IArticleDao;
import leo.android.core.sample.dao.impl.ArticleDao;

public class DetailActivity extends AppCompatActivity {

    private IArticleDao mArticleDao;
    private MethodAsyncTask mArticleTextMAT;
    private Article mArticle;
    private TextView mTextView;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        mArticle = (Article) intent.getSerializableExtra(Consts.KEY_ARTICLE);
        if (mArticle == null) {
            Logger.e("mArticle is null");
            finish();
            return ;
        }
        getSupportActionBar().setTitle(mArticle.getTitle());

        mTextView = (TextView) findViewById(R.id.text);
        mProgressBar = (ProgressBar) findViewById(R.id.progressbar);

        mArticleDao = new ArticleDao().getProxy();
        mArticleTextMAT = mArticleDao.getArticleText(Consts.ARTICLE_BASE_URL + mArticle.getUrl(), callback);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mArticleTextMAT != null && mArticleTextMAT.isRunning()) {
            mArticleTextMAT.cancel(true);
        }
        // or use method below instead, this will cancel all AsyncTasks created by mArticleDao.
        // mArticleDao.cancelAll();
    }

    private AsyncCallback callback = new AsyncCallback<Integer, String>() {
        @Override
        protected void onResult(int err, String... texts) {
            Logger.d("begin, err = " + err);
            if (err == Consts.ERRCODE_FAILED) {
                Toast.makeText(DetailActivity.this, R.string.failed_to_load_data, Toast.LENGTH_LONG).show();
            } else if (err == Consts.ERRCODE_NET_BUGEILI) {
                Toast.makeText(DetailActivity.this, R.string.network_bugeili, Toast.LENGTH_LONG).show();
            } else if (err == Consts.ERRCODE_SUCCESSED) {
                String text = texts[0];
                if (text != null) {
                    mTextView.setText(Html.fromHtml(text));
                }
            }
            mProgressBar.setVisibility(View.GONE);
        }

        @Override
        protected void onCancel() {
            Toast.makeText(DetailActivity.this, R.string.cancelled_load_article, Toast.LENGTH_LONG).show();
        }
    };
}
