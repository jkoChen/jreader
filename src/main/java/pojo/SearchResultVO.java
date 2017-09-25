package pojo;

/**
 * @author j.chen@91kge.com
 * create on 2017/8/23
 */
public class SearchResultVO {
    private String bookName;
    private String bookUrl;

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getBookUrl() {
        return bookUrl;
    }

    public void setBookUrl(String bookUrl) {
        this.bookUrl = bookUrl;
    }

    @Override
    public String toString() {
        return this.bookName;
    }
}
