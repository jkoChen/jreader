package pojo;

import java.util.List;

/**
 * @author j.chen@91kge.com
 * create on 2017/8/23
 */
public class BookVO {

    private int page = 0;//当前章节

    private ContentsVO contents;

    public ChapterVO getCurrentChapter() {
        return getChapterByPage(page);
    }

    public ChapterVO getNextChapter() {
        return getChapterByPage(page + 1);

    }

    public ChapterVO getPreChapter() {
        return getChapterByPage(page - 1);
    }

    public ChapterVO getChapterByPage(int page) {
        if (page >= 0 && page < contents.getChapters().size()) {
            return contents.getChapters().get(page);
        }
        return null;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public ContentsVO getContents() {
        return contents;
    }

    public void setContents(ContentsVO contents) {
        this.contents = contents;
    }

}
