package pojo;

/**
 * @author j.chen@91kge.com
 * create on 2017/8/23
 */
public class BookVO {

    private int chapter = 0;//当前章节

    private ContentsVO contents;

    public ChapterVO getCurrentChapter() {
        return getChapterByPage(chapter);
    }

    public ChapterVO getNextChapter() {
        return getChapterByPage(chapter + 1);

    }

    public ChapterVO getPreChapter() {
        return getChapterByPage(chapter - 1);
    }

    public ChapterVO getChapterByPage(int page) {
        if (page >= 0 && page < contents.getChapters().size()) {
            return contents.getChapters().get(page);
        }
        return null;
    }

    public int getChapter() {
        return chapter;
    }

    public void setChapter(int chapter) {
        this.chapter = chapter;
    }

    public ContentsVO getContents() {
        return contents;
    }

    public void setContents(ContentsVO contents) {
        this.contents = contents;
    }

}
