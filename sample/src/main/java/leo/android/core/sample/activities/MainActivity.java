package leo.android.core.sample.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
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

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private IArticleDao mArticleDao;
    private MethodAsyncTask mArticleListMAT;
    private ListView mListView;
    private ProgressBar mProgressBar;
    private ArticleListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAdapter = new ArticleListAdapter(this);
        mProgressBar = (ProgressBar) findViewById(R.id.progressbar);
        mListView = (ListView) findViewById(R.id.listview);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);

        mArticleDao = new ArticleDao().getProxy();
        mArticleListMAT = mArticleDao.getArticleList(callback);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mArticleListMAT != null && mArticleListMAT.isRunning()) {
            mArticleListMAT.cancel(true);
        }
        // or use method instead, this will cancel all AsyncTasks created by mArticleDao.
//        mArticleDao.cancelAll();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Article a = mAdapter.getItem(position);
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(Consts.KEY_ARTICLE, a);
        startActivity(intent);
    }

    private AsyncCallback callback = new AsyncCallback<Integer, List<Article>>() {
        @Override
        protected void onResult(int err, List<Article>... lists) {
            Logger.d("begin, err = " + err);
            if (err == Consts.ERRCODE_FAILED) {
                Toast.makeText(MainActivity.this, R.string.failed_to_load_data, Toast.LENGTH_LONG).show();
            } else if (err == Consts.ERRCODE_NET_BUGEILI) {
                Toast.makeText(MainActivity.this, R.string.network_bugeili, Toast.LENGTH_LONG).show();
            } else if (err == Consts.ERRCODE_SUCCESSED) {
                List<Article> list = lists[0];
                mAdapter.setArticleList(lists[0]);
                mAdapter.notifyDataSetChanged();
            }
            mProgressBar.setVisibility(View.GONE);
        }
    };

    class ArticleListAdapter extends BaseAdapter {

        private Context mContext;
        private List<Article> mList;
        private ViewHolder mHolder;

        public ArticleListAdapter(Context context) {
            this.mContext = context;
        }

        public void setArticleList(List<Article> list) {
            this.mList = list;
        }

        @Override
        public int getCount() {
            return mList == null ? 0 : mList.size();
        }

        @Override
        public Article getItem(int position) {
            return mList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Article a = mList.get(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.listview_item, null);
                mHolder = new ViewHolder();
                mHolder.tvIndex = (TextView) convertView.findViewById(R.id.tv_index);
                mHolder.tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
                convertView.setTag(mHolder);
            } else {
                mHolder = ((ViewHolder) convertView.getTag());
            }
            mHolder.tvIndex.setText((position + 1) + ".");
            mHolder.tvTitle.setText(a.getTitle());
            return convertView;
        }

        class ViewHolder {
            TextView tvIndex;
            TextView tvTitle;
        }
    }
}
