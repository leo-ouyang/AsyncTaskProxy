package leo.android.core.sample.beans;

import java.io.Serializable;

/**
 * Created by leo on 16-5-25.
 */
public class Article implements Serializable {

    private String title;
    private String url;
    private String text;

    public Article() {
    }

    public Article(String title, String url, String text) {
        this.title = title;
        this.url = url;
        this.text = text;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
