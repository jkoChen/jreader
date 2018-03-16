package pojo;

import java.util.List;

/**
 * 小说目录，包含小说的所有章节
 *
 * @author j.chen@91kge.com
 * create on 2017/8/23
 */
public class ContentsVO {

    private List<ChapterVO> chapters;

    public List<ChapterVO> getChapters() {
        return chapters;
    }

    public void setChapters(List<ChapterVO> chapters) {
        this.chapters = chapters;
    }

}
