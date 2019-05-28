package site;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import pojo.BookVO;
import pojo.ChapterVO;

import java.io.IOException;
import java.util.List;

/**
 * @author j.chen@91kge.com  create on 2017/8/29
 */
public class DingdianSite implements IBookSite {
    private String searchUrl = "http://zhannei.baidu.com/cse/search?s=1682272515249779940&entry=1&q=%s";

    @Override
    public List<BookVO> search(String name) throws IOException {
        return BDZNSearch(searchUrl, name);
    }

    @Override
    public Elements getContentElements(Document document) {
        Elements els = document.getElementsByClass("chapterlist");
        return els.first().getElementsByTag("a");
    }

    @Override
    public ChapterVO getChapter(String bookUrl, Element ele) {
        String name = ele.text();
        String ul = ele.attr("href");
        ChapterVO chapterVO = new ChapterVO();
        chapterVO.setChapterName(name);
        chapterVO.setChapterUrl(bookUrl + "/" + ul);
        return chapterVO;
    }

    @Override
    public Element getChapterContent(Document document) {
        return document.getElementById("content");
    }
    @Override
    public BookSiteEnum bookSite() {
        return BookSiteEnum.DING_DIAN;
    }

}
