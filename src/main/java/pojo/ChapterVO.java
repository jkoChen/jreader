package pojo;

/**
 * 章节类
 *
 * @author j.chen@91kge.com
 * create on 2017/8/23
 */
public class ChapterVO {
    //章节名
    private String chapterName;
    //章节url 用于下载内容
    private String chapterUrl;

    //章节内容，通过 chapterUrl 获取，当赋值成功后，将 isFull 赋值true
    private String content;
    private boolean isFull = false;

    public boolean isFull() {
        return isFull;
    }

    public void setFull(boolean full) {
        isFull = full;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getChapterName() {
        return chapterName;
    }

    public void setChapterName(String chapterName) {
        this.chapterName = chapterName;
    }

    public String getChapterUrl() {
        return chapterUrl;
    }

    public void setChapterUrl(String chapterUrl) {
        this.chapterUrl = chapterUrl;
    }

    @Override
    public String toString() {
        return this.chapterName;
    }
}
