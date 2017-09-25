package pojo;

/**
 * @author j.chen@91kge.com
 * create on 2017/8/23
 */
public class BookVO {

    private int chapter = 0;//当前章节
    private String name;

    private ContentsVO contents;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

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
        if (chapter < 0) {
            chapter = 0;
        }
        if (chapter >= contents.getChapters().size()) {
            chapter = contents.getChapters().size() - 1;
        }
        this.chapter = chapter;
    }

    public ContentsVO getContents() {
        return contents;
    }

    public void setContents(ContentsVO contents) {
        this.contents = contents;
    }

}
